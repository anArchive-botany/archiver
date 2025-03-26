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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import it.aspix.archiver.Utilita;
import it.aspix.archiver.componenti.CoppiaCSTesto;
import it.aspix.archiver.eventi.ProprietaCambiataEvent;
import it.aspix.archiver.eventi.ProprietaCambiataListener;
import it.aspix.archiver.eventi.ProprietaException;

/****************************************************************************
 * Gestisce tutte le proprieta' del programma
 * Fa affidamento sul fatto che tutte le proprietà diano a due livelli:
 * gruppo.nome
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class Proprieta {
    
    /************************************************************************
     * serve per memorizzare oltre al valore della proprietà anche alcune
     * sue caratteristiche
     ***********************************************************************/
    static class Dettagli{
        public String valore;
        public boolean persistente;
        public Dettagli(String valore, boolean persistente) {
            this.valore = valore;
            this.persistente = persistente;
        }
        public Dettagli(String valore) {
            this.valore = valore;
            this.persistente = true;
        }
    }
    
    /************************************************************************
     * Descrive una proprietà con tutti i suoi attributi
     ***********************************************************************/
    public static class DescrizioneProprieta{
        public String nome;
        public String descrizione;
        public String tipo;
        public String valoreDefault;
        public String pattern;
        public boolean nascondi;
        public ArrayList<CoppiaCSTesto> valoreEnumerato;
        
        public DescrizioneProprieta(String nome){
            this.nome = nome;
            nascondi = false;
            valoreEnumerato = new ArrayList<CoppiaCSTesto>();
        }
        
        public String toString(){
            return nome;
        }
    }
    
    /************************************************************************
     * Un gruppo di oggetti DescrizioneProprieta
     ***********************************************************************/
    public static class GruppoDescrizioneProprieta{
        public String nome;
        public ArrayList<DescrizioneProprieta> descrizioniProprieta;
        
        public GruppoDescrizioneProprieta(String nome){
            this.nome = nome;
            descrizioniProprieta = new ArrayList<DescrizioneProprieta>();
        }
        
        public String toString(){
            return nome;
        }
    }

    static Properties tabella; // per mantenere le proprietà
    public static ArrayList<GruppoDescrizioneProprieta> gruppi = new ArrayList<GruppoDescrizioneProprieta>(); 
    static boolean salvareProprieta = false; // per evitare di scrivere su disco se non serve
    static Vector<ProprietaCambiataListener> ascoltatori;
    static {
        tabella = new Properties();
        ascoltatori = new Vector<ProprietaCambiataListener>();
    }

    /************************************************************************
     * Aggiunge un ascoltatore
     * @param l l'ascoltatore da aggiungere
     ***********************************************************************/
    public static void addProprietaCambiataListener(ProprietaCambiataListener l) {
        ascoltatori.add(l);
    }
    
    /************************************************************************
     * Toglie un ascoltatore
     * @param l l'ascoltatore da togliere
     ***********************************************************************/
    public static void removeProprietaCambiataListener(ProprietaCambiataListener l) {
        ascoltatori.remove(l);
    }    
    
    /************************************************************************
     * @param nome della proprietà da recuperari
     * @return i dettagli della proprietà
     ***********************************************************************/
    private static Dettagli recuperaProprieta(String nome){
        Dettagli dettagli = (Dettagli) (tabella.get(nome));
        if (dettagli==null || dettagli.valore == null) {
        	throw new ProprietaException(nome);
        }
        return dettagli;
    }

    /************************************************************************
     * aggiorna il valore di una proprieta'
     * @param nome il nome della proprietà
     * @param valore il valore della proprietà
     ***********************************************************************/
    public static void aggiorna(String nome, String valore) {
        Dettagli vecchio = recuperaProprieta(nome);
        if (!valore.equals(vecchio.valore)) {
            Stato.debugLog.fine("Variazione proprieta': (" + nome + "," + valore + ")");
            tabella.put(nome, new Dettagli(valore,vecchio.persistente));
            fireProprietaCambiata(nome, valore, vecchio.valore);
            if(vecchio.persistente)
                salvareProprieta = true;
        }
    }

    /************************************************************************
     * aggiorna il valore di una proprieta'
     * @param nome il nome della proprietà
     * @param valore il valore della proprietà
     ***********************************************************************/
    public static void aggiorna(String nome, boolean valore) {
        if (valore)
            aggiorna(nome, "true");
        else
            aggiorna(nome, "false");
    }

    /************************************************************************
     * recupera il valore di una proprieta
     * @param nome il nome della proprietà
     * @return il valore o la stringa vuota
     ***********************************************************************/
    public static String recupera(String nome) {
        return recuperaProprieta(nome).valore;
    }

    public static final String PREFISSO_STRATI = "%";
    public static final String SEPARATORE_STRATI_ID_NOME = "~";
    
    /************************************************************************
     * Carica le proprieta da disco
     * @return true se il file già esisteva, false altrimenti
     ***********************************************************************/
    public static boolean caricaProprieta() {
        try {
            // carica le descrizioni delle proprietà          
            InputStream is = Utilita.class.getResourceAsStream("descrizioneProprieta.txt");
            InputStreamReader isr = new InputStreamReader(is,"UTF-8");
            BufferedReader input = new BufferedReader(isr);
            String linea;
            int posizionePunto;
            int posizioneChiocciola;
            int posizioneUguale;
            String gruppo;
            String proprieta;
            String attributo;
            String valore;
            DescrizioneProprieta dp;
            
            while( (linea=input.readLine())!=null ){
                linea=linea.trim();
                if(linea.equals("#STOP")){
                    break;
                }
                if(linea.length()==0 || linea.startsWith("#")){
                    continue;
                }
                posizionePunto = linea.indexOf('.');
                posizioneChiocciola = linea.indexOf('@');
                posizioneUguale = linea.indexOf('=');
                gruppo = linea.substring(0,posizionePunto);
                proprieta = linea.substring(posizionePunto+1, posizioneChiocciola);
                attributo = linea.substring(posizioneChiocciola+1,posizioneUguale);
                valore = linea.substring(posizioneUguale+1);
                dp = getDescrizioneProprieta(gruppo, proprieta);
                if(attributo.equals("DESCRIZIONE")){
                    dp.descrizione = valore;
                }else if(attributo.equals("TIPO")){
                    dp.tipo = valore;
                }else if(attributo.equals("NASCONDI")){
                    dp.nascondi = valore.equals("true");
                }else if(attributo.equals("DEFAULT")){
                    dp.valoreDefault = valore;
                    // inizializzaProprietaDefault();
                    tabella.put(gruppo+"."+proprieta, new Dettagli(valore));
                }else if(attributo.equals("PATTERN")){
                    dp.pattern = valore;
                }else{
                    // resta soltanto un valore enumerato
                    dp.valoreEnumerato.add(new CoppiaCSTesto(attributo,valore));
                }
            }
            input.close();
            isr.close();
            is.close();
            
            // caricamento delle proprietà
            Properties temporanea = new Properties();
            temporanea.load(new FileInputStream(nomeFile()));
            // copia da disco solo le proprietà che servono,
            // cautela necessaria nel caso di upgrade delle proprietà
            Enumeration<Object> chiavi = temporanea.keys();
            String elemento;
            while (chiavi.hasMoreElements()) {
                elemento = (String) (chiavi.nextElement());
                if (tabella.get(elemento) != null) {
                    // inserisce una proprietà solo se è già presente
                    // cioè se è elencata tra quelle con un default (anche se vuoto)
                    tabella.put(elemento, new Dettagli((String) temporanea.get(elemento)));
                    // se qualcuno si è registrato come ascoltatore va notificato
                    fireProprietaCambiata(elemento, recuperaProprieta(elemento).valore, "");
                }
            }
            chiavi = null;
            Stato.debugLog.warning("Proprieta' caricate da disco");
            // trattamento speciale per alcune proprietà
            if(Stato.versione.equals(Stato.VERSISONE_NON_TROVATA)){
            	Proprieta.aggiorna("devel.sbdLogEnabled", "true");
            	Stato.debugLog.severe("sbdLog abilitato per assenza dei numeri di versione");
            	Stato.debugLog.severe("generale.sbdLogEnabled:"+Proprieta.isTrue("generale.sbdLogEnabled"));
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        	Stato.debugLog.throwing("it.aspix.entwash.nucleo.Proprieta", "caricaProprieta", ex);
            salvareProprieta = true;
            return false;
        }
    }
    
    /************************************************************************
     * Nella ricerca se la proprieta o il gruppo non esiste li crea
     * @param gruppo a cui appartiene a proprietà
     * @param proprieta di cui si vuole la descrizione
     * @return l'oggetto cercato
     ***********************************************************************/
    private static DescrizioneProprieta getDescrizioneProprieta(String gruppo, String proprieta){
        GruppoDescrizioneProprieta g = null;
        DescrizioneProprieta dp = null; 
        int i;
        
        for(i=0 ; i<gruppi.size() ; i++){
            if(gruppi.get(i).nome.equals(gruppo)){
                g = gruppi.get(i);
                break;
            }
        }
        if(g==null){
            g = new GruppoDescrizioneProprieta(gruppo);
            gruppi.add(g);
        }
        // cerco la proprieta specifica
        for(i=0 ; i<g.descrizioniProprieta.size() ; i++){
            if(g.descrizioniProprieta.get(i).nome.equals(proprieta)){
                dp = g.descrizioniProprieta.get(i);
                break;
            }
        }
        if(dp==null){
            dp = new DescrizioneProprieta(proprieta);
            g.descrizioniProprieta.add(dp);
        }
        return dp;
    }
    
    /************************************************************************ 
     * Controlla che le proprietà che hanno un pattern lo rispettino
     * @return una string con le descrizioni dei problemi o null se non 
     *         ce ne sono
     ***********************************************************************/
    public static String check(){
    	int iGruppo,iProprieta;
    	String nome;
    	String valore;
    	StringBuilder descrizione = new StringBuilder();
    	
    	GruppoDescrizioneProprieta g;
    	DescrizioneProprieta dp;
    	for(iGruppo=0 ; iGruppo<gruppi.size() ; iGruppo++){
    		g = gruppi.get(iGruppo);
    		for(iProprieta=0 ; iProprieta<g.descrizioniProprieta.size(); iProprieta++){
    			dp = g.descrizioniProprieta.get(iProprieta);
    			if(dp.pattern!=null){
    				nome = g.nome+"."+dp.nome;
    				valore = recupera(nome);
    				if(!valore.matches(dp.pattern)){
    					if(descrizione.length()>0){
    						descrizione.append(", ");
    					}
    					descrizione.append("la proprietà "+nome+" non è stata correttamente impostata");
    				}
    			}
    		}
    	}
    	if(descrizione.length()>0){
    		return descrizione.toString();
    	}else{
    		return null;
    	}
    }
    
    /************************************************************************
     * Controlla che i dati minimi per far funzionare archiver siano presenti:
     * un erbario o ppure un progetto oppure una check list, in caso ci sia 
     * una check-list devono esserci anche le categorie generiche 
     * @return null se i dati minimi ci sono
     ***********************************************************************/
    public static String proprietaMinimePresenti(){
    	
    	if(recupera("check-list.database").length()==0 && recupera("herbaria.database").length()==0 && recupera("vegetazione.database").length()==0){
    		return "Non hai ancora impostato un erabario o un progetto della vegetazione o una check-list su cui lavorare.";
    	}
    	if(recupera("check-list.database").length()!=0 && recupera("check-list.categorieGeneriche").length()==0){
    		return "Hai una check-list selezionata ma nessuna categoria generica, questo creerà problemi nel funzionamento del programma.";
    	}
    	return null;
    }

    /************************************************************************
     * Salva le proprieta su disco
     ***********************************************************************/
    public static void salvaProprieta() throws Exception {
        if (salvareProprieta) {
            tabella.remove("connessione.password");
            // le proprietà vanno traferite in una tabella che contiene solo stringhe
            Properties temp = new Properties();
            Enumeration<Object> chiavi = tabella.keys();
            String elemento;
            Dettagli dett;
            while (chiavi.hasMoreElements()) {
                elemento = (String) (chiavi.nextElement());
                dett = (Dettagli) tabella.get(elemento);
                if (dett != null && dett.persistente) {
                    temp.put(elemento, ( (Dettagli)tabella.get(elemento)).valore );
                }
            }
            temp.store(new FileOutputStream(nomeFile()), "Generato da programma, la modifica manuale puo' causare malfunzionamenti.");
            Stato.debugLog.warning("Proprieta' salvate su disco");
        }
    }
    
    /************************************************************************
     * @return il nome del file in cui salvare le proprietà
     ***********************************************************************/
    private static String nomeFile(){
        return System.getProperty("user.home") + System.getProperty("file.separator") + ".archiver5";    
    }

    /************************************************************************
     * chiama tutti gli ascolatatori registrati
     * @param nomeProprieta della proprietà che cambia
     * @param valore il nuovo valore
     * @param vecchioValore il valore precedente
     ***********************************************************************/
    protected static void fireProprietaCambiata(String nomeProprieta, String valore, String vecchioValore) {
        Stato.debugLog.fine("Chiamo gli ascoltatori registrati");
        ProprietaCambiataEvent evento = new ProprietaCambiataEvent(nomeProprieta, valore, vecchioValore);
        for (int i = 0; i < ascoltatori.size(); i++) {
            Stato.debugLog.fine("Chiamo l'ascoltatore " + ascoltatori.elementAt(i).getClass().getName());
            try{
                ascoltatori.elementAt(i).proprietaCambiata(evento);
            }catch(Exception ex){
                // un errore in questo punto non dovrebbe compromettere l'applicazione
                ex.printStackTrace();
            }
        }
    }

    /************************************************************************
     * @param nome il nome della proprieta da testare
     * @return true se la proprieta' e' impostata a "true"
     ***********************************************************************/
    public static boolean isTrue(String nome) {
        Dettagli prop = (Dettagli) (tabella.get(nome));
        if (prop == null) {
            return false;
        }
		return prop.valore.equals("true");
    }

}