#!/bin/bash
# ATTENZIONE: questo script fa affidamento su una serie di cose più o meno standard
# - "mvn package" mette tutti i jar in target/lib
# - le icone sono nella cartella immagini
# - su Windows deve esserci 7zip ionstallato in "/c/Program\ Files/7-Zip/7z"

# comando per jpackage
JPACKAGE=$JAVA_HOME/bin/jpackage
# dove stanno i jar (cartella impostata nel file pom.xml)
CARTELLA_JARS=target/lib
# dove mettere il file compilato
DESTINAZIONE=target
# versione (recuperata dal file pom)
VERSIONE=$(grep -m 1 version pom.xml | sed 's/[^0-9\\.]//g')
# nome del jar principale (contiene anche il numero di versione)
JAR_PRINCIPALE="archiver-$VERSIONE.jar"

echo "JAVA_HOME     : $JAVA_HOME"
echo "JPACKAGE      : $JPACKAGE"
echo "MAVEN         : $(which mvn)"
echo "OSTYPE        : $OSTYPE"
echo "CARTELLA_JARS : $CARTELLA_JARS"
echo "JAR_PRINCIPALE: $JAR_PRINCIPALE"
echo "VERSIONE      : $VERSIONE"
echo "DESTINAZIONE  : $DESTINAZIONE"

mvn -q clean
mvn -q package -DskipTests

MODULI=java.desktop,java.logging
MAINCLASS=it.aspix.archiver.archiver.Archiver

if [[ "$OSTYPE" == "darwin"*  ]]; then
    # ============================= file firmato per macOS ======================================
    jpackage --name archiver --app-version $VERSIONE --icon immagini/archiver.icns --type dmg \
    --input target/lib --dest target \
    --add-modules $MODULI \
    --main-class $MAINCLASS --main-jar archiver-$VERSIONE.jar \
    --mac-package-name Archiver \
    --mac-sign --mac-signing-key-user-name "Developer ID Application: IIS Cassata Gattapone (NMMRHK5G25)" \
    --mac-package-identifier it.aspix.archiver
elif [[ "$OSTYPE" == "linux"* ]]; then
    jpackage --name archiver --app-version $VERSIONE --icon immagini/archiver.png --type app-image \
    --input target/lib --dest target \
    --add-modules $MODULI \
    --main-class $MAINCLASS --main-jar archiver-$VERSIONE.jar 
else
    # ============================= zip per Windows =============================================
    jpackage --name archiver --app-version $VERSIONE --icon immagini/archiver.ico --type app-image \
    --input target/lib --dest target/compilato \
    --add-modules $MODULI \
    --main-class $MAINCLASS --main-jar archiver-$VERSIONE.jar
    cd target/compilato
    /c/Program\ Files/7-Zip/7z a -tzip ../archiver-$VERSIONE.zip *
fi