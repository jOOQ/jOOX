Content extraction benchmark for PR #221

Build and run from the repository root (JDK 21 and Maven):

  mvn -f jOOX/pom.xml -DskipTests package
  mvn -f benchmarks/content/pom.xml -DskipTests package
  java -cp benchmarks/content/target/benchmarks.jar:jOOX/target/joox-2.0.2.jar org.openjdk.jmh.Main 'org.joox.ContentBenchmark.*' -prof gc -rf json -rff results.json

On Windows use a semicolon instead of a colon between classpath entries.
The standalone POM explicitly uses the locally built PR jar; it does not
change the normal library build or download a released jOOX implementation.

regexFull and scanFull include the actual Util.toString(element) call.
regexOnly and scanOnly isolate extraction from a pre-serialized string;
they must not be interpreted as end-to-end content() speedups. Both
helpers reproduce the old/new mixed-content branches. Setup checks their
output against the current public content() method. Empty and text-only
branches are unchanged and are not benchmarked here. Normal caller-side
Match construction and the initial mixed-content detection are excluded
from both full methods.

JMH 1.37, OpenJDK 21.0.12.1 (Ubuntu), Linux x86_64, Intel i5-13600KF.
3 forks, 5 x 1-second warmups and 5 x 1-second measurements per fork,
512 MiB fixed heap, average microseconds/op, GC profiler enabled.

SMALL: one child, 72 serialized characters.
ATTRIBUTES: one child plus 32 attributes, 2110 serialized characters.
LARGE: 1000 children, 51804 serialized characters.

Initial run, including serialization (mean +/- JMH 99.9% confidence interval):
                regex us/op        scan us/op        regex B/op  scan B/op
SMALL            4.649 +/- 0.076     4.588 +/- 0.575       37869      36208
ATTRIBUTES      11.601 +/- 0.218    11.230 +/- 0.346       50445      48789
LARGE          205.118 +/- 8.092   131.244 +/- 4.653      323609     270233

The small and attribute-heavy timing intervals overlap; these measurements
do not establish an end-to-end improvement for those inputs. LARGE was
about 36% faster with approximately 16.5% less allocation. The host was
shared with other development tasks, so these are indicative measurements,
not a universal performance claim.

A separate repeat of both LARGE full methods used the same settings:
  java -cp benchmarks/content/target/benchmarks.jar:jOOX/target/joox-2.0.2.jar org.openjdk.jmh.Main 'org.joox.ContentBenchmark.(regexFull|scanFull)' -p shape=LARGE -prof gc -rf json -rff large-confirmation.json

Repeat: regex 214.452 +/- 0.908 us/op, scan 128.043 +/- 1.088 us/op;
allocation 323609 versus 270177 B/op. Both raw runs, including all fork
samples, are in results/. The large-input difference persisted in this
repeat; results can still vary across machines, JDKs and XML shapes.
