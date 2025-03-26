/****************************************************************************
 * Copyright 2011 studio Aspix 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 ***************************************************************************/
package it.aspix.archiver.nucleo;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.aspix.archiver.FiltroLog;
import it.aspix.archiver.LogFormatter;
import it.aspix.archiver.Utilita;
import it.aspix.archiver.componenti.InterfacciaTestoUnicode;

/*****************************************************************************
 * Mantiene uno stato (orrore!!!) del sistema, 
 *  - evita di passare a tutti il comunicatore
 *  - tiene registrati un insieme di visualizzatori
 * @author Edoardo Panfili, studio Aspix
 ****************************************************************************/
public class Stato {
    
    // ----------------------------- variabili globali ----------------------
	
    /** il logger per i messaggi di debug */  
    public static Logger debugLog;
    /** attualmente non usata ma potrebbe servire per poter personalizzare l'interfaccia
     * a seconda del nome dato al file jar */
    public static String nomeProgramma;
    /** dato da utilizzare per creare un link */
    public static String riferimentoPerLinkNome;
    /** dato da utilizzare per creare un link */
    public static String riferimentoPerLinkProgressivo;
    /** ultima operazione eseguita */
    public static String debugHint;
    
    static{
        Logger logger = Logger.getLogger("it.aspix.entwash");
        logger.setLevel(Level.ALL);
        debugLog = logger;
        // inizializzazione del sistema di traccia dei bug
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new LogFormatter());
        ch.setFilter(new FiltroLog());
        debugLog.addHandler(ch);
        debugLog.setUseParentHandlers(false);
        
        nomeProgramma = it.aspix.archiver.nucleo.Stato.class.getProtectionDomain().getCodeSource().getLocation().toString();
        if(nomeProgramma.startsWith("file") && nomeProgramma.endsWith(".jar")){
        	nomeProgramma = nomeProgramma.substring(nomeProgramma.lastIndexOf(File.separator)+1, nomeProgramma.indexOf(".jar")).toLowerCase();
        }
    }
    
    public static String versione = "undef";
    public static String versioneTools = "undef";
    public static String VERSISONE_NON_TROVATA = "errore";
    static {
        versione = Utilita.leggiStringa("versione.txt", VERSISONE_NON_TROVATA);
        versioneTools = Utilita.leggiStringa("versionetools.txt", VERSISONE_NON_TROVATA);
    }

    /** serve per alcune parti dell'interfaccia grafica */
    public static boolean isMacOSX = false;
    static{
        if(System.getProperty("os.name").equals("Mac OS X")){
            isMacOSX=true;
        }
    }

    /** e' usato se l'immissione di un carattere unicode viene richiesta da menu */
    public static InterfacciaTestoUnicode ultimoUtilizzato;
    
    /** il timestamp di costruzione del pacchetto */
    public static String buildTimeStamp;
    static{
        buildTimeStamp = Utilita.leggiStringa("timestamp.txt","Time Stamp non disponibile");
    }
    
    /** il gestore delle comunicazione (globale per l'applicazione) **/
    public static Comunicatore comunicatore;
    
}