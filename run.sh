#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
if command -v mvn >/dev/null 2>&1; then
    mvn -q -DskipTests package
    exec java -jar target/RutaExpress-1.0-SNAPSHOT.jar
fi

MAIL_JAR="$HOME/.m2/repository/org/eclipse/angus/jakarta.mail/2.0.5/jakarta.mail-2.0.5.jar"
ACTIVATION_API="$(find "$HOME/.m2/repository/jakarta/activation/jakarta.activation-api" -name 'jakarta.activation-api-*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar' 2>/dev/null | sort -V | tail -n 1)"
ANGUS_ACTIVATION="$(find "$HOME/.m2/repository/org/eclipse/angus/angus-activation" -name 'angus-activation-*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar' 2>/dev/null | sort -V | tail -n 1)"
if [ ! -f "$MAIL_JAR" ] || [ -z "$ACTIVATION_API" ] || [ -z "$ANGUS_ACTIVATION" ]; then
    echo "No se encontró Maven ni una dependencia de Jakarta Mail en ~/.m2. Instala Maven y ejecuta mvn package." >&2
    exit 1
fi
MAIL_CP="$MAIL_JAR:$ACTIVATION_API:$ANGUS_ACTIVATION"
mkdir -p target/classes
find src/main/java -name '*.java' > target/sources.txt
javac --release 21 -encoding UTF-8 -cp "$MAIL_CP" -d target/classes @target/sources.txt
exec java -cp "target/classes:$MAIL_CP" com.mycompany.rutaexpress.main.RutaExpress
