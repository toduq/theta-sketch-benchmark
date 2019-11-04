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

Entries : 1000
Avg time of native set : 1ms
Avg time of theta sketch : 1ms

Entries : 10000
Avg time of native set : 3ms
Avg time of theta sketch : 1ms

Entries : 100000
Avg time of native set : 19ms
Avg time of theta sketch : 12ms

Entries : 1000000
Avg time of native set : 205ms
Avg time of theta sketch : 9ms

Entries : 10000000
Avg time of native set : 4544ms
Avg time of theta sketch : 8ms

$ ls -lh *.bin
-rw-r--r--  1 ___  ___    76M 11  1 18:03 nativeSet1.bin
-rw-r--r--  1 ___  ___    76M 11  1 18:03 nativeSet2.bin
-rw-r--r--  1 ___  ___   515K 11  1 18:04 sketch1.bin
-rw-r--r--  1 ___  ___   515K 11  1 18:04 sketch2.bin
```
