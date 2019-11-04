# Theta Sketch Benchmark

This project shows how the Theta Sketch Framework speed up the intersection operation.

https://datasketches.github.io/docs/Theta/ThetaSketchFramework.html

## Result

Run on Macbook Pro 15-inch, 2017.

- entries : 4 millions
- intersection result : 2 millions
- theta sketch nominal entries : 2^16

```bash
$ ./gradlew run

> Task :run
Intersection is 2000000
Intersection is 2000000
Intersection is 2000000
Intersection is 2000000
Intersection is 2000000
Intersection is 2000000
Intersection is 2000000
Intersection is 2000000
Intersection is 2000000
Intersection is 2000000
Avg time of native set : 2044ms
Intersection is 2013511.162474927
Intersection is 2013511.162474927
Intersection is 2013511.162474927
Intersection is 2013511.162474927
Intersection is 2013511.162474927
Intersection is 2013511.162474927
Intersection is 2013511.162474927
Intersection is 2013511.162474927
Intersection is 2013511.162474927
Intersection is 2013511.162474927
Avg time of theta sketch : 15ms

BUILD SUCCESSFUL in 22s
2 actionable tasks: 2 executed

$ ls -Alh *.bin
-rw-r--r--  1 ___  ___    31M 11  1 18:03 nativeSet1.bin
-rw-r--r--  1 ___  ___    31M 11  1 18:03 nativeSet2.bin
-rw-r--r--  1 ___  ___   727K 11  1 18:04 sketch1.bin
-rw-r--r--  1 ___  ___   720K 11  1 18:04 sketch2.bin
```
