"""Summarize test, coverage and mutation reports produced by the Maven build.

Usage: ci_report.py TARGET_DIR SITE_DIR

Prints a Markdown summary (for $GITHUB_STEP_SUMMARY) and assembles SITE_DIR,
a static site linking the HTML reports, for GitHub Pages.
Missing reports are reported as unavailable instead of failing the job.
"""
import csv
import glob
import html
import json
import os
import shutil
import sys
import xml.etree.ElementTree as ET

SUITE_KINDS = (("architecture.", "ArchUnit"), ("acceptance.", "Cucumber"))


def suite_kind(name):
    for prefix, kind in SUITE_KINDS:
        if name.startswith(prefix):
            return kind
    return "JUnit"


def read_tests(target):
    totals = {}
    failed = []
    for path in sorted(glob.glob(os.path.join(target, "surefire-reports", "TEST-*.xml"))):
        suite = ET.parse(path).getroot()
        kind = suite_kind(suite.get("name", ""))
        counts = totals.setdefault(kind, {"tests": 0, "failures": 0, "errors": 0, "skipped": 0})
        for key in counts:
            counts[key] += int(suite.get(key, "0"))
        for case in suite.iter("testcase"):
            if case.find("failure") is not None or case.find("error") is not None:
                failed.append("%s.%s" % (case.get("classname"), case.get("name")))
    return totals, failed


def read_coverage(target):
    path = os.path.join(target, "site", "jacoco", "jacoco.csv")
    if not os.path.exists(path):
        return None
    sums = {"LINE_MISSED": 0, "LINE_COVERED": 0, "BRANCH_MISSED": 0, "BRANCH_COVERED": 0}
    with open(path, newline="") as report:
        for row in csv.DictReader(report):
            for key in sums:
                sums[key] += int(row[key])
    return {
        "lines": ratio(sums["LINE_COVERED"], sums["LINE_MISSED"]),
        "branches": ratio(sums["BRANCH_COVERED"], sums["BRANCH_MISSED"]),
    }


def read_mutations(target):
    path = os.path.join(target, "pit-reports", "mutations.xml")
    if not os.path.exists(path):
        return None
    statuses = {}
    for mutation in ET.parse(path).getroot().iter("mutation"):
        status = mutation.get("status")
        statuses[status] = statuses.get(status, 0) + 1
    total = sum(statuses.values())
    killed = statuses.get("KILLED", 0) + statuses.get("TIMED_OUT", 0) + statuses.get("MEMORY_ERROR", 0)
    return {
        "total": total,
        "killed": killed,
        "survived": statuses.get("SURVIVED", 0),
        "no_coverage": statuses.get("NO_COVERAGE", 0),
        "score": percent(killed, total),
    }


def read_scenario_coverage(target):
    path = os.path.join(target, "cucumber-all", "cucumber.json")
    if not os.path.exists(path):
        return None
    with open(path) as report:
        features = json.load(report)
    coverage = {"passed": 0, "pending": [], "failing": []}
    listed = {}
    for feature in features:
        # Cucumber reports Background steps as a separate element preceding each scenario.
        background = []
        for element in feature.get("elements", []):
            if element.get("type") == "background":
                background = element.get("steps", [])
                continue
            status = scenario_status(background + scenario_steps(element))
            background = []
            if status == "passed":
                coverage["passed"] += 1
                continue
            path = source_path(feature.get("uri", ""))
            key = (status, path, element.get("name", ""))
            if key in listed:
                # Each example row of a Scenario Outline is reported as its own scenario.
                listed[key]["examples"] += 1
                continue
            listed[key] = {"feature": feature.get("name", ""), "name": element.get("name", ""),
                           "path": path, "line": element.get("line"), "examples": 1}
            coverage["failing" if status == "failed" else "pending"].append(listed[key])
    return coverage


def scenario_steps(element):
    return [step for key in ("before", "steps", "after") for step in element.get(key, [])]


def scenario_status(steps):
    statuses = {step["result"]["status"] for step in steps}
    if statuses & {"failed", "ambiguous"}:
        return "failed"
    return "passed" if statuses == {"passed"} else "pending"


def source_path(uri):
    return "src/test/resources/" + uri[len("classpath:"):] if uri.startswith("classpath:") else uri


def source_url(path, line, environment):
    keys = ("GITHUB_SERVER_URL", "GITHUB_REPOSITORY", "GITHUB_SHA")
    if not all(environment.get(key) for key in keys):
        return None
    server, repository, sha = (environment[key] for key in keys)
    return "%s/%s/blob/%s/%s#L%d" % (server, repository, sha, path, line)


def ratio(covered, missed):
    return percent(covered, covered + missed)


def percent(part, whole):
    return "n/a" if whole == 0 else "%.1f %%" % (100.0 * part / whole)


def summary_rows(tests, coverage, mutations, scenarios):
    rows = []
    for kind, label in (("JUnit", "JUnit"), ("Cucumber", "Cucumber, build gate"), ("ArchUnit", "ArchUnit")):
        counts = tests.get(kind)
        if counts is None:
            rows.append((label, "unavailable"))
        else:
            broken = counts["failures"] + counts["errors"]
            status = "passed" if broken == 0 else "%d failed" % broken
            tests_run = "1 test" if counts["tests"] == 1 else "%d tests" % counts["tests"]
            rows.append((label, "%s, %s, %d skipped" % (tests_run, status, counts["skipped"])))
        if kind == "Cucumber":
            rows.append(("Cucumber, all scenarios", "unavailable" if scenarios is None else
                         "%d passing, %d pending, %d failing" % (
                             scenarios["passed"], scenario_count(scenarios["pending"]),
                             scenario_count(scenarios["failing"]))))
    if coverage is None:
        rows.append(("JaCoCo coverage", "unavailable"))
    else:
        rows.append(("JaCoCo coverage", "%s lines, %s branches" % (coverage["lines"], coverage["branches"])))
    if mutations is None:
        rows.append(("PIT mutation score", "unavailable"))
    else:
        rows.append(("PIT mutation score", "%s (%d/%d killed, %d survived, %d without coverage)" % (
            mutations["score"], mutations["killed"], mutations["total"],
            mutations["survived"], mutations["no_coverage"])))
    return rows


def markdown(rows, failed, scenarios, environment):
    lines = ["## Quality reports", "", "| Report | Result |", "| --- | --- |"]
    lines += ["| %s | %s |" % row for row in rows]
    if failed:
        lines += ["", "### Failed tests", ""] + ["- `%s`" % name for name in failed]
    if scenarios is not None:
        lines += ["", "### Pending scenarios (%d)" % scenario_count(scenarios["pending"]), ""]
        lines += [markdown_scenario(s, environment) for s in scenarios["pending"]] or ["No pending scenario."]
    return "\n".join(lines) + "\n"


def markdown_scenario(scenario, environment):
    location = "%s:%d" % (os.path.basename(scenario["path"]), scenario["line"])
    url = source_url(scenario["path"], scenario["line"], environment)
    return "- %s › %s — %s" % (scenario["feature"], scenario_label(scenario),
                               "[%s](%s)" % (location, url) if url else "`%s`" % location)


def scenario_count(scenarios):
    return sum(scenario.get("examples", 1) for scenario in scenarios)


def scenario_label(scenario):
    examples = scenario.get("examples", 1)
    return scenario["name"] if examples == 1 else "%s (%d examples)" % (scenario["name"], examples)


def build_site(target, site, rows, failed, scenarios, environment):
    if os.path.exists(site):
        shutil.rmtree(site)
    os.makedirs(site)
    links = []
    for source, destination, entry, label in (
            (os.path.join(target, "site", "jacoco"), "jacoco", "index.html", "JaCoCo coverage"),
            (os.path.join(target, "pit-reports"), "pit", "index.html", "PIT mutation testing"),
            (os.path.join(target, "cucumber"), "cucumber", "cucumber.html",
             "Cucumber acceptance scenarios (build gate)"),
            (os.path.join(target, "cucumber-all"), "cucumber-all", "cucumber.html",
             "Cucumber, all scenarios including pending ones")):
        if os.path.isdir(source):
            shutil.copytree(source, os.path.join(site, destination))
            links.append('<li><a href="%s/%s">%s</a></li>' % (destination, entry, label))
    table = "".join("<tr><th>%s</th><td>%s</td></tr>" % (html.escape(k), html.escape(v)) for k, v in rows)
    failures = "".join("<li><code>%s</code></li>" % html.escape(name) for name in failed)
    commit = html.escape(environment.get("GITHUB_SHA", "local")[:7])
    with open(os.path.join(site, "index.html"), "w") as page:
        page.write("""<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Chess Manager reports</title>
<style>body{font-family:system-ui,sans-serif;max-width:56rem;margin:2rem auto;padding:0 1rem;line-height:1.4}
table{border-collapse:collapse;width:100%%}th,td{text-align:left;padding:.35rem .8rem;border-bottom:1px solid #ccc;vertical-align:top;overflow-wrap:anywhere}
thead th{border-bottom:2px solid #888}.muted{color:#666}</style>
</head><body><h1>Chess Manager quality reports</h1><p class="muted">Commit %s</p>
<table>%s</table>%s%s<h2>Detailed reports</h2><ul>%s</ul>
<p class="muted">ArchUnit rules run as JUnit tests; their result appears in the table above.</p>
</body></html>
""" % (commit, table, "<h2>Failed tests</h2><ul>%s</ul>" % failures if failed else "",
            html_scenarios(scenarios, environment), "".join(links)))


def html_scenarios(scenarios, environment):
    if scenarios is None:
        return ""
    sections = [html_scenario_table("Pending scenarios", scenarios["pending"], environment,
                                    "Steps are undefined or pending: these scenarios are not covered yet.",
                                    "No pending scenario.")]
    if scenarios["failing"]:
        sections.append(html_scenario_table("Failing scenarios", scenarios["failing"], environment,
                                            "Every step is defined, but the expected outcome is not met.", ""))
    return "".join(sections)


def html_scenario_table(title, scenarios, environment, explanation, empty):
    if not scenarios:
        return "<h2>%s (0)</h2><p>%s</p>" % (title, empty)
    rows = []
    for scenario in scenarios:
        location = "%s:%d" % (os.path.basename(scenario["path"]), scenario["line"])
        url = source_url(scenario["path"], scenario["line"], environment)
        cell = '<a href="%s">%s</a>' % (html.escape(url), html.escape(location)) if url else html.escape(location)
        rows.append("<tr><td>%s</td><td>%s</td><td><code>%s</code></td></tr>" % (
            html.escape(scenario["feature"]), html.escape(scenario_label(scenario)), cell))
    return ("<h2>%s (%d)</h2><p class=\"muted\">%s</p><table><thead><tr><th>Feature</th><th>Scenario</th>"
            "<th>Source</th></tr></thead><tbody>%s</tbody></table>") % (
        title, scenario_count(scenarios), explanation, "".join(rows))


def main(target, site):
    environment = dict(os.environ)
    tests, failed = read_tests(target)
    scenarios = read_scenario_coverage(target)
    rows = summary_rows(tests, read_coverage(target), read_mutations(target), scenarios)
    sys.stdout.write(markdown(rows, failed, scenarios, environment))
    build_site(target, site, rows, failed, scenarios, environment)


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
