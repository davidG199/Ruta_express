#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
mkdir -p target/classes
find src/main/java -name '*.java' > target/sources.txt
javac -encoding UTF-8 -d target/classes @target/sources.txt
java -cp target/classes com.mycompany.rutaexpress.main.RutaExpress
