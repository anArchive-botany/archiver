+-----------------------------------------------------------------------------------------------+
|           Gestione delle libreria SimpleBotanicalData                                         |
+-----------------------------------------------------------------------------------------------+


Usare SDKman o analogo, Java 21


===== compilazione ==============================================================================

serve avere disponibile la libreria simpleBotanicalData

----
mvn clean
mvn package
----


===== aggiungere la libreria al repository locale di Maven =====================================

mvn install

questo serve a tabParser che usa diversi pezzi di questo progetto


===== compilazione =============================================================================

C'è uno script: `creazionePacchetto.sh`