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
package it.aspix.archiver;

import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.Specimen;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/************************************************************************
 * Metodi di utilita per lavorare con XML
 * @author Edoardo Panfili
 ***********************************************************************/
public class Utilita {

    /***************************************************************************
     * Converte dai gradi rappresentati in decimale alla loro rappresentazione
     * in gradi, primi e secondi
     * @param decimali l'angolo nella sua rappresentazione decimale
     * @return l'angolo come "gg pp ss.ds"
     **************************************************************************/
    public static String angoloDecimaleToSessagesimale(String decimali){
        if(decimali==null || decimali.length()==0)
            return decimali;
        StringBuffer grPrSe=new StringBuffer();
        double valore=Double.parseDouble(decimali);
        int parteIntera;
        if(valore<0){
            valore*=-1;
            grPrSe.append("-");
        }
        // prendo i gradi
        parteIntera=(int)valore;
        valore=(valore-parteIntera);
        grPrSe.append(parteIntera);
        grPrSe.append('\u00b0');
        // prendo i primi
        valore*=60;
        parteIntera=(int)valore;
        valore=valore-parteIntera;
        grPrSe.append(" "+parteIntera);
        grPrSe.append('\'');
        // quel che resta sono i secondi
        valore*=60;
        // ma vanno arrotondati
        valore=(double)((int)(valore*1000+0.5))/1000;
        grPrSe.append(" "+valore);
        grPrSe.append('"');
        return grPrSe.toString();
    }

    /***************************************************************************
     * Converte dai gradi rappresentati come (gradi, primi e secondi)
     * alla rappresentazione decimale
     * @param grPrSe l'angolo come "gg pp ss.ds"
     * @return l'angolo nella sua rappresentazione decimale
     **************************************************************************/
    public static String angoloSessagesimaleToDecimale(String grPrSe){
        int segno=1;
        if(grPrSe.length()==0)
            return grPrSe;
        if(grPrSe.charAt(0)=='-'){
            segno=-1;
            grPrSe=grPrSe.substring(1);
        }
        int primoSpazio=grPrSe.indexOf(" ");
        if(primoSpazio==grPrSe.length()-1)
            primoSpazio=-1;
        int secondoSpazio=grPrSe.indexOf(" ",primoSpazio+1);
        if(secondoSpazio==grPrSe.length()-1)
            secondoSpazio=-1;
        String gradiS;
        String primiS;
        String secondiS;
        if(primoSpazio==-1){
            gradiS=grPrSe;
            primiS="0";
            secondiS="0";
        }else{
            if(secondoSpazio==-1){
                gradiS=grPrSe.substring(0,primoSpazio);
                primiS=grPrSe.substring(primoSpazio+1,grPrSe.length());
                secondiS="0";
            }else{
                gradiS=grPrSe.substring(0,primoSpazio);
                primiS=grPrSe.substring(primoSpazio+1,secondoSpazio);
                secondiS=grPrSe.substring(secondoSpazio+1,grPrSe.length());
            }
        }
        Stato.debugLog.fine("stringhe dei dati=("+gradiS+"#"+primiS+"#"+secondiS+")");
        double decimale=Double.parseDouble(gradiS)+Double.parseDouble(primiS)/60+Double.parseDouble(secondiS)/3600;
        return Double.toString(segno*decimale);
    }

    /***************************************************************************
     * Separa una stringa in base alla posizione di ";" o ","
     * @param intera la stringa intera
     * @return un vettore di stringhe ottenuto separando la stringa intera
     **************************************************************************/
    public static String[] spezza(String intera){
        String[] risultato;
        int posizioneSeparatore;
        //tenta di trovare un separatore
        posizioneSeparatore=intera.indexOf(";");
        if(posizioneSeparatore==-1)
            posizioneSeparatore=intera.indexOf(",");
        if(posizioneSeparatore!=-1){
            posizioneSeparatore++; // la prima stringa deve comprendere il segno di punteggiatura
            risultato=new String[2];
            risultato[0]=intera.substring(0,posizioneSeparatore);
            risultato[1]=intera.substring(posizioneSeparatore);
        }else{
            risultato=new String[1];
            risultato[0]=intera;
        }
        return risultato;
    }

    /***************************************************************************
     * Appende una stringa ad un vettore di stringhe
     * @param vec il vettore di stringhe
     * @param s la stringa da appendere
     * @return un vettore nuovo formato dalle strimghe di vec pi� s
     **************************************************************************/
    public static String[] appendi(String[] vec, String s){
        String risult[]=new String[vec.length+1];
        for(int i=0;i<vec.length;i++)
            risult[i]=vec[i];
        risult[vec.length]=s;
        return risult;
    }

    public static final int LATITUDINE=0;
    public static final int LONGITUDINE=1;
    /***************************************************************************
     * Converte un angolo decimale secondo le preferenze e accoda
     * NORD/SUD EST/OVEST anzich� il segno
     * @param decimali la stringa da convertire
     * @param tipo deve essere una delle costanti LATITUDINE o LONGITUDINE
     **************************************************************************/
    public static String formattaAngolo(String decimali,int tipo){
        StringBuffer angolo;

        if(decimali==null || decimali.length()==0)
            return "";

        if(Proprieta.recupera("gradi").equals("gps")){
            angolo=new StringBuffer(angoloDecimaleToSessagesimale(decimali));
        }else{
            angolo=new StringBuffer(decimali);
        }
        if(angolo.charAt(0)=='-'){
            angolo=angolo.delete(0,1);
            angolo.append(tipo==LATITUDINE?" OVEST":" SUD");
        }else{
            angolo.append(tipo==LATITUDINE?" EST":" NORD");
        }
        return angolo.toString();
    }

    /***************************************************************************
    * Codifica i dati del cartellino per la rappresentazione in barre
    * @param cartellino da codificare
    * @param parte se questo cartellino è diviso in più parti questo è il progressivo della parte
    * @param totale il numero totale delle parti
    * @param fixedSizeDim numero di caratteri, -1 se la dimensione fissa non interessa,
    *        altrimenti vengono aggiunti zeri in testa
    **************************************************************************/
    public static String codificaCodice(Specimen cartellino, int parte, int totale, int fixedSizeDim){
        String codice;
        if(fixedSizeDim==-1){
            codice = cartellino.getDirectoryInfo().getContainerName()+"-"+
            cartellino.getDirectoryInfo().getContainerSeqNo()+""+parte+""+totale;
        }else{
            codice = cartellino.getDirectoryInfo().getContainerSeqNo();
            codice = "00000000".substring(0,fixedSizeDim-codice.length())+codice;
            codice = cartellino.getDirectoryInfo().getContainerName()+"-"+codice+""+parte+""+totale;
        }
        return codice;
    }
    
    /************************************************************************
     * @param s il dato da formattare
     * @param modo "gps" "gradiPrimi" o altro
     * @param negativo la stringa suffisso per i valori negativi
     * @param positivo la stringa suffisso per i valori positivi
     * @param separatoreDecimale 
     ***********************************************************************/
    public static final String formattaCoordinata(String s, String modo, String negativo, String positivo, DecimalFormatSymbols separatoreDecimale){
        if(modo.equals("gps") && s!=null){
            String emisfero=positivo;
            double convertito=Double.parseDouble(s);
            if(convertito<0){
                convertito=-convertito;
                emisfero=negativo;
            }
            long gradi=(long)(convertito);
            convertito=(convertito-gradi)*60;
            long primi=(long)(convertito);
            convertito=(convertito-primi)*60;
            DecimalFormat df = new DecimalFormat("##.####");
            df.setDecimalFormatSymbols(separatoreDecimale);
            return gradi+"\u00b0 "+primi+"' "+df.format(convertito)+"\" "+emisfero;
        }else if(modo.equals("gradiPrimi")){
            String emisfero=positivo;
            double convertito=Double.parseDouble(s);
            if(convertito<0){
                convertito=-convertito;
                emisfero=negativo;
            }
            int gradi=(int)convertito;
            convertito=(convertito-gradi)*60;
            if(convertito<0)
                convertito=-convertito; // non pu� essere minore di zero
            DecimalFormat df = new DecimalFormat("##.####");
            df.setDecimalFormatSymbols(separatoreDecimale);
            return gradi+"\u00b0 "+df.format(convertito)+"' "+emisfero; 
        }else{
            // l'unica altra modalita' possibile e' quella numerica
            return s;
        }
    }

    /************************************************************************
     * Fa un confronto tenendo conto degli eventuali null
     * @param a primo termine del confronto
     * @param b secondo termine del confronto
     * @return true se le stringhe sono uguali
     ***********************************************************************/
    public static boolean uguale(String a, String b){
        if(a==null && b==null)
            return true;
            
        if(a==null && b!=null)
            return false;
        
        if(a!=null && b==null)
            return false;
            
        return a.equals(b);
    }

    /************************************************************************
     * Fa un confronto considerando null uguale a ""
     * @param a primo termine del confronto
     * @param b secondo termine del confronto
     * @return true se le stringhe sono uguali
     ***********************************************************************/
    public static boolean ugualeIgnoraVuote(String a, String b){
        String cfr1=(a==null? "" : a);
        String cfr2=(b==null? "" : b);
                    
        return cfr1.equals(cfr2);
    }
    
    /************************************************************************
     * Utile per il debug
     * @param livello un array anche con elementi null
     * @return la stringa che rappresenta l'array di livelli
     ***********************************************************************/
    public static String prettyPrint(Level livello[]){
    	StringBuffer sb = new StringBuffer();
    	for(int i=0 ; i<livello.length ; i++){
    		if(livello[i]!=null && livello[i].getSurveyedSpecieCount()>0){
    			sb.append(livello[i].getId()+":"+livello[i].getName()+"\n");
    			for(int j=0; j<livello[i].getSurveyedSpecieCount() ; j++){
    				sb.append("    <"+livello[i].getSurveyedSpecie(j).getAbundance()+">"+
    						livello[i].getSurveyedSpecie(j).getSpecieRefName()+"\n");
    			}
    		}
    	}
    	return sb.toString();
    }

    /************************************************************************
     * Legge l'intero contenuto di un file in una unica stringa,
     * soltnato file fino a 4096Bytes
     * @param nomeFile il nome di un file contenuto nel pacchetto
     * @param predefinito testo da ritornare in caso di errore
     * @return il terso trovato o default
     ***********************************************************************/
    public static String leggiStringa(String nomeFile, String predefinito){
        String risultato;
        try{
            Stato.debugLog.fine("leggo il file \""+nomeFile+"\"");
            char[] buffer = new char[1024*8];
            int letti;
            InputStream is = Stato.class.getResourceAsStream(""+nomeFile);
            InputStreamReader isr = new InputStreamReader(is,"utf8");
            letti = isr.read(buffer);
            risultato = new String(buffer,0,letti);
            isr.close();
            is.close();
            Stato.debugLog.fine("stringa letta:"+risultato);
        }catch(Exception ex){
            risultato = predefinito;
            Stato.debugLog.throwing(Stato.class.getCanonicalName(), "leggiStringa", ex);
        }
        return risultato;
    }
    
    /************************************************************************
     * @param s1
     * @param s2
     * @return s1 se è diverso da null altrimenti s2
     ***********************************************************************/
    public static final String coalesce(String s1, String s2){
        return s1!=null ? s1 : s2;
    }
 
    /************************************************************************
     * fornisce una spiegazione discorsiva di una eccezione
     * @param ex l'eccezione da descrivere
     * @return la descrizione
     ***********************************************************************/
    public static String getSpiegazione(Exception ex){
        if(ex instanceof java.net.SocketTimeoutException){
            return "impossibile connettersi al server (non risponde)";
        }
        if(ex instanceof java.net.UnknownHostException){
            return "impossibile connettersi al server (indirizzo Internet di "+ex.getMessage()+" ignoto)";
        }
        if(ex instanceof java.net.ConnectException){
            return "impossibile connettersi al server (connessione non disponibile)";
        }
        return ex.getMessage();
    }
}