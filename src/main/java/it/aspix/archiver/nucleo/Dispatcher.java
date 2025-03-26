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

import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.archiver.ElencoInterattivo;
import it.aspix.archiver.archiver.Raccoglitore;
import it.aspix.archiver.componenti.FornitoreGestoreMessaggi;
import it.aspix.archiver.componenti.VisualizzatoreOggetti;
import it.aspix.archiver.dialoghi.DialogoInserisci;
import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.Message;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.OggettoSBD;
import it.aspix.sbd.obj.SimpleBotanicalData;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;

/****************************************************************************
 * Questa classe si occupa di consegnare i dati da visualizzare ad oggetti
 * in grado di visualizzarli, oltre a questo ha dei metodi di utilità per
 * muoversi nelle gerarchie di oggetti.
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class Dispatcher {

    /************************************************************************
     * Consegna il primo messaggio di un elenco (se presente)
     * @param c il punto da cui partire nella ricerca di un visualizzatore
     * @param m il messaggio da visualizzare
     ***********************************************************************/
    public static void consegnaMessaggi(Component componente, SimpleBotanicalData sbd) {
        if(sbd.getMessageSize()>0) {
            consegna(componente, sbd.getMessage(0));
        }
    }
    
    /************************************************************************
     * Consegna un messaggio
     * @param c il punto da cui partire nella ricerca di un visualizzatore
     * @param m il messaggio da visualizzare
     ***********************************************************************/
    public static void consegna(Component c, Message m){
        while(c!=null && !(c instanceof FornitoreGestoreMessaggi) ){
            c = c.getParent();
        }
        if(c==null){
            UtilitaGui.mostraMessaggioAndandoACapo(m.getText(0).getText(), "Comunicazione", 
                    m.getType()==MessageType.ERROR 
                        ? JOptionPane.ERROR_MESSAGE 
                        : ( m.getType()==MessageType.WARNING 
                                ? JOptionPane.WARNING_MESSAGE 
                                : JOptionPane.INFORMATION_MESSAGE                                   
                          )
                    );
        }else{
            ((FornitoreGestoreMessaggi)c).getGestoreMessaggi().addMessaggio(m);
        }
    }
    
    /************************************************************************
     * Consegna un messaggio formato da una eccezione
     * @param c il punto da cui partire nella ricerca di un visualizzatore
     * @param e l'eccezione da visualizzare
     ***********************************************************************/
    public static void consegna(Component c, Exception e){
        e.printStackTrace();
        // consegna(c, CostruttoreOggetti.createMessage("Situazione eccezionale: "+e.getMessage(), "en", MessageType.ERROR));
        while(c!=null && !(c instanceof FornitoreGestoreMessaggi) ){
            c = c.getParent();
        }
        if(c==null){
        	Message m = CostruttoreOggetti.createMessage("Situazione eccezionale: "+e.getMessage(), "en", MessageType.ERROR);
            UtilitaGui.mostraMessaggioAndandoACapo(m.getText(0).getText(), "Comunicazione", 
                    m.getType()==MessageType.ERROR 
                        ? JOptionPane.ERROR_MESSAGE 
                        : ( m.getType()==MessageType.WARNING 
                                ? JOptionPane.WARNING_MESSAGE 
                                : JOptionPane.INFORMATION_MESSAGE                                   
                          )
                    );
        }else{
            ((FornitoreGestoreMessaggi)c).getGestoreMessaggi().addMessaggio(e);
        }
    }
    
    /************************************************************************
     * Consegna un array di oggetti OggettoSBD
     * @param c il punto da cui partire nella ricerca di un visualizzatore
     * @param elenco da visualizzare
     * @param pattern che rappresenta elenco
     * @throws Exception
     ***********************************************************************/
    public static void consegna(Component c, OggettoSBD elenco[], OggettoSBD pattern) throws Exception{
        while(c!=null && !(c instanceof VisualizzatoreOggetti) ){
            c = c.getParent();
        }
        if(c==null){
            throw new Exception("Nessuno in grado di visualizzare");
        }else{
            ((VisualizzatoreOggetti)c).visualizza(elenco, pattern);
        }
    }
    
    /************************************************************************
     * Consegna un ogetto OggettoSBD
     * @param c il punto da cui partire nella ricerca di un visualizzatore
     * @param elenco a cui poter chiedere successivo/precedente
     * @param oggetto da visualizzare
     * @throws Exception
     ***********************************************************************/
    public static void consegna(Component c, OggettoSBD oggetto, ElencoInterattivo elenco) throws Exception{
        while(c!=null && !(c instanceof VisualizzatoreOggetti) ){
            c = c.getParent();
        }
        if(c==null){
            throw new Exception("Nessuno in grado di visualizzare");
        }else{
            ((VisualizzatoreOggetti)c).visualizza(oggetto, elenco);
        }
    }
    
    /************************************************************************
     * Aggiorna il raccoglitore che contiene questo elemento
     * @param c il punto da cui iniziare a cercare
     * @throws Exception
     ***********************************************************************/
    public static void aggiornaRaccoglitore(Component c) throws Exception {
        while(c!=null && !(c instanceof Raccoglitore) ){
            c = c.getParent();
        }
        if(c==null){
            // XXX: se non ci sono linguette da aggiornare, non si aggiornano,
            // questo è il caso della fase di avvio in cui alcuni editor impostano
            // in contenuo di default
        }else{
            ((Raccoglitore)c).stateChanged(new ChangeEvent(c));
        }
    }
    
    /************************************************************************
     * Permette di inserire un oggetto bloccando tutte
     * le altre attività (finestra modale)
     * @param oggetto
     * @return
     ***********************************************************************/
    public static DirectoryInfo insert(OggettoSBD oggetto){
        DialogoInserisci di = new DialogoInserisci(oggetto);
        di.setVisible(true);
        return di.getdirectoryInfo();
    }
}
