import json
import os
import shutil
import tempfile
import unittest

import ci_report

FEATURE_URI = "classpath:features/pending/club_visitor_approval.feature"


def step(status):
    return {"name": "a step", "result": {"status": status}}


def scenario(line, name, *statuses):
    return {"type": "scenario", "line": line, "name": name, "steps": [step(s) for s in statuses]}


class ScenarioCoverageTests(unittest.TestCase):
    def setUp(self):
        self.target = tempfile.mkdtemp()

    def write_run(self, *elements):
        folder = os.path.join(self.target, "cucumber-all")
        os.makedirs(folder)
        with open(os.path.join(folder, "cucumber.json"), "w") as report:
            json.dump([{"uri": FEATURE_URI, "name": "Approve a club visitor", "elements": list(elements)}], report)

    def test_a_scenario_with_an_undefined_step_is_pending(self):
        self.write_run(scenario(11, "Approve a prospect as a visitor", "passed", "undefined", "skipped"))

        coverage = ci_report.read_scenario_coverage(self.target)

        self.assertEqual(["Approve a prospect as a visitor"], [s["name"] for s in coverage["pending"]])

    def test_a_scenario_with_a_pending_step_is_pending(self):
        self.write_run(scenario(11, "Approve a prospect as a visitor", "pending", "skipped"))

        coverage = ci_report.read_scenario_coverage(self.target)

        self.assertEqual(1, len(coverage["pending"]))

    def test_a_pending_scenario_is_located_in_the_test_sources(self):
        self.write_run(scenario(11, "Approve a prospect as a visitor", "undefined"))

        pending = ci_report.read_scenario_coverage(self.target)["pending"][0]

        self.assertEqual("Approve a club visitor", pending["feature"])
        self.assertEqual("src/test/resources/features/pending/club_visitor_approval.feature", pending["path"])
        self.assertEqual(11, pending["line"])

    def test_passing_and_failing_scenarios_are_counted_but_not_pending(self):
        self.write_run(
            scenario(11, "Approve a prospect as a visitor", "passed", "passed"),
            scenario(17, "Approve a partner affiliated with another club", "passed", "failed", "skipped"),
            scenario(23, "Reject an unknown person", "undefined"))

        coverage = ci_report.read_scenario_coverage(self.target)

        self.assertEqual(1, coverage["passed"])
        self.assertEqual(["Reject an unknown person"], [s["name"] for s in coverage["pending"]])
        self.assertEqual(["Approve a partner affiliated with another club"],
                         [s["name"] for s in coverage["failing"]])

    def test_an_undefined_background_step_makes_the_following_scenario_pending(self):
        self.write_run(
            {"type": "background", "line": 10, "name": "", "steps": [step("undefined")]},
            scenario(13, "Affiliate a player for a season", "skipped", "skipped"))

        coverage = ci_report.read_scenario_coverage(self.target)

        self.assertEqual(0, coverage["passed"])
        self.assertEqual(["Affiliate a player for a season"], [s["name"] for s in coverage["pending"]])

    def test_a_scenario_whose_steps_were_all_skipped_is_not_passing(self):
        self.write_run(scenario(13, "Affiliate a player for a season", "skipped", "skipped"))

        coverage = ci_report.read_scenario_coverage(self.target)

        self.assertEqual(0, coverage["passed"])
        self.assertEqual(1, len(coverage["pending"]))

    def test_the_examples_of_one_outline_are_listed_once(self):
        self.write_run(
            scenario(19, "Record either supported license category", "undefined"),
            scenario(20, "Record either supported license category", "undefined"))

        pending = ci_report.read_scenario_coverage(self.target)["pending"]

        self.assertEqual([("Record either supported license category", 19, 2)],
                         [(s["name"], s["line"], s["examples"]) for s in pending])

    def test_coverage_is_unavailable_without_a_full_run(self):
        self.assertIsNone(ci_report.read_scenario_coverage(self.target))


class PendingScenarioSummaryTests(unittest.TestCase):
    PENDING = {"feature": "Approve a club visitor", "name": "Approve a prospect as a visitor",
               "path": "src/test/resources/features/pending/club_visitor_approval.feature", "line": 11,
               "examples": 1}

    def test_lists_each_pending_scenario_with_a_link_to_its_source(self):
        coverage = {"passed": 3, "pending": [self.PENDING], "failing": []}
        environment = {"GITHUB_SERVER_URL": "https://github.com",
                       "GITHUB_REPOSITORY": "SebMace/chess-manager", "GITHUB_SHA": "ddc4c00"}

        summary = ci_report.markdown([], [], coverage, environment)

        self.assertIn("### Pending scenarios (1)", summary)
        self.assertIn("- Approve a club visitor › Approve a prospect as a visitor — "
                      "[club_visitor_approval.feature:11](https://github.com/SebMace/chess-manager/blob/ddc4c00/"
                      "src/test/resources/features/pending/club_visitor_approval.feature#L11)", summary)

    def test_shows_how_many_examples_of_an_outline_are_pending(self):
        outline = dict(self.PENDING, examples=3)
        coverage = {"passed": 0, "pending": [outline], "failing": []}

        self.assertIn("Approve a prospect as a visitor (3 examples) — ", ci_report.markdown([], [], coverage, {}))

    def test_counts_pending_scenarios_like_cucumber_one_per_example(self):
        coverage = {"passed": 0, "pending": [dict(self.PENDING, examples=3), self.PENDING], "failing": []}

        self.assertIn("### Pending scenarios (4)", ci_report.markdown([], [], coverage, {}))
        self.assertIn(("Cucumber, all scenarios", "0 passing, 4 pending, 0 failing"),
                      ci_report.summary_rows({}, None, None, coverage))

    def test_says_so_when_every_scenario_is_covered(self):
        coverage = {"passed": 3, "pending": [], "failing": []}

        self.assertIn("No pending scenario.", ci_report.markdown([], [], coverage, {}))


class SummaryRowTests(unittest.TestCase):
    def test_uses_the_singular_for_a_single_test(self):
        tests = {"Cucumber": {"tests": 1, "failures": 0, "errors": 0, "skipped": 0}}

        self.assertIn(("Cucumber, build gate", "1 test, passed, 0 skipped"),
                      ci_report.summary_rows(tests, None, None, None))


class ReportSiteTests(unittest.TestCase):
    def setUp(self):
        self.target = tempfile.mkdtemp()
        self.site = os.path.join(tempfile.mkdtemp(), "site")
        for folder, content in (("cucumber", "build gate run"), ("cucumber-all", "every scenario run")):
            os.makedirs(os.path.join(self.target, folder))
            with open(os.path.join(self.target, folder, "cucumber.html"), "w") as report:
                report.write(content)

    def build(self):
        ci_report.build_site(self.target, self.site, [], [], None, {})
        with open(os.path.join(self.site, "index.html")) as page:
            return page.read()

    def test_publishes_the_run_of_every_scenario_as_the_only_cucumber_report(self):
        index = self.build()

        with open(os.path.join(self.site, "cucumber", "cucumber.html")) as report:
            self.assertEqual("every scenario run", report.read())
        self.assertFalse(os.path.exists(os.path.join(self.site, "cucumber-all")))
        self.assertEqual(1, index.count("cucumber.html"))

    def test_falls_back_to_the_build_gate_report_without_a_full_run(self):
        shutil.rmtree(os.path.join(self.target, "cucumber-all"))

        self.build()

        with open(os.path.join(self.site, "cucumber", "cucumber.html")) as report:
            self.assertEqual("build gate run", report.read())


class SourceLinkTests(unittest.TestCase):
    PATH = "src/test/resources/features/pending/club_visitor_approval.feature"

    def test_links_to_the_scenario_line_of_the_built_commit(self):
        environment = {"GITHUB_SERVER_URL": "https://github.com",
                       "GITHUB_REPOSITORY": "SebMace/chess-manager", "GITHUB_SHA": "ddc4c00"}

        self.assertEqual(
            "https://github.com/SebMace/chess-manager/blob/ddc4c00/" + self.PATH + "#L11",
            ci_report.source_url(self.PATH, 11, environment))

    def test_has_no_link_outside_github_actions(self):
        self.assertIsNone(ci_report.source_url(self.PATH, 11, {}))


if __name__ == "__main__":
    unittest.main()
