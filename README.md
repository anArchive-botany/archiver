# client per la gestione dei dati di anArchive

# compilazione

serve avere disponibile la libreria simpleBotanicalData

```bash
mvn clean
mvn package
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