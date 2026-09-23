"""Summarize test, coverage and mutation reports produced by the Maven build.

Usage: ci_report.py TARGET_DIR SITE_DIR

Prints a Markdown summary (for $GITHUB_STEP_SUMMARY) and assembles SITE_DIR,
a static site linking the HTML reports, for GitHub Pages.
Missing reports are reported as unavailable instead of failing the job.
"""
import csv
import glob
import html
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


def ratio(covered, missed):
    return percent(covered, covered + missed)


def percent(part, whole):
    return "n/a" if whole == 0 else "%.1f %%" % (100.0 * part / whole)


def summary_rows(tests, coverage, mutations):
    rows = []
    for kind in ("JUnit", "Cucumber", "ArchUnit"):
        counts = tests.get(kind)
        if counts is None:
            rows.append((kind, "unavailable"))
            continue
        broken = counts["failures"] + counts["errors"]
        status = "passed" if broken == 0 else "%d failed" % broken
        rows.append((kind, "%d tests, %s, %d skipped" % (counts["tests"], status, counts["skipped"])))
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


def markdown(rows, failed):
    lines = ["## Quality reports", "", "| Report | Result |", "| --- | --- |"]
    lines += ["| %s | %s |" % row for row in rows]
    if failed:
        lines += ["", "### Failed tests", ""] + ["- `%s`" % name for name in failed]
    return "\n".join(lines) + "\n"


def build_site(target, site, rows, failed):
    if os.path.exists(site):
        shutil.rmtree(site)
    os.makedirs(site)
    links = []
    for source, destination, label in (
            (os.path.join(target, "site", "jacoco"), "jacoco", "JaCoCo coverage"),
            (os.path.join(target, "pit-reports"), "pit", "PIT mutation testing"),
            (os.path.join(target, "cucumber"), "cucumber", "Cucumber acceptance scenarios")):
        if os.path.isdir(source):
            shutil.copytree(source, os.path.join(site, destination))
            entry = "cucumber.html" if destination == "cucumber" else "index.html"
            links.append('<li><a href="%s/%s">%s</a></li>' % (destination, entry, label))
    table = "".join("<tr><th>%s</th><td>%s</td></tr>" % (html.escape(k), html.escape(v)) for k, v in rows)
    failures = "".join("<li><code>%s</code></li>" % html.escape(name) for name in failed)
    commit = html.escape(os.environ.get("GITHUB_SHA", "local")[:7])
    with open(os.path.join(site, "index.html"), "w") as page:
        page.write("""<!doctype html>
<html lang="en"><head><meta charset="utf-8"><title>Chess Manager reports</title>
<style>body{font-family:system-ui,sans-serif;max-width:48rem;margin:2rem auto;padding:0 1rem}
table{border-collapse:collapse}th,td{text-align:left;padding:.3rem .8rem;border-bottom:1px solid #ccc}</style>
</head><body><h1>Chess Manager quality reports</h1><p>Commit %s</p>
<table>%s</table>%s<h2>Detailed reports</h2><ul>%s</ul>
<p>ArchUnit rules run as JUnit tests; their result appears in the table above.</p>
</body></html>
""" % (commit, table, "<h2>Failed tests</h2><ul>%s</ul>" % failures if failed else "", "".join(links)))


def main(target, site):
    tests, failed = read_tests(target)
    rows = summary_rows(tests, read_coverage(target), read_mutations(target))
    sys.stdout.write(markdown(rows, failed))
    build_site(target, site, rows, failed)


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
