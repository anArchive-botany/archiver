# client per la gestione dei dati di anArchive

# compilazione

serve avere disponibile la libreria simpleBotanicalData

```bash
mvn clean
mvn package
```

su alcune combinazioni monitor/linux l'interfaccia viene piccolissima, in caso può essere
utile lanciare java con il parametro "-Dsun.java2d.uiScale=2.0",
per aggiugerlo nella creazione del pacchetto serve aggiungere un parametro a jpackage
```shell
    --java-options "-Dsun.java2d.uiScale=2.0" \
```


# aggiungere questa applicazione come artefatto al repository locale di Maven

serve a tabParser che usa diversi pezzi di questo progetto

```bash
mvn install
```


## creazione eseguibile stand alone

Serve bash, scritto usando git bash

```bash
creazionePacchetto.sh
```


