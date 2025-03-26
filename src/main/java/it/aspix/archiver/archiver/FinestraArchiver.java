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
package it.aspix.archiver.archiver;

import it.aspix.archiver.Icone;
import it.aspix.archiver.componenti.ElencoBlob;
import it.aspix.archiver.componenti.ElencoSample;
import it.aspix.archiver.componenti.ElencoSpecieSpecification;
import it.aspix.archiver.componenti.ElencoSpecimen;
import it.aspix.archiver.componenti.FornitoreGestoreMessaggi;
import it.aspix.archiver.componenti.GestoreMessaggi;
import it.aspix.archiver.componenti.StatusBar;
import it.aspix.archiver.componenti.VisualizzatoreOggetti;
import it.aspix.archiver.dialoghi.ImpostazioneProprieta;
import it.aspix.archiver.dialoghi.Informazioni;
import it.aspix.archiver.editor.BlobEditor;
import it.aspix.archiver.editor.BlobEditorSintetico;
import it.aspix.archiver.editor.SampleEditorLinguette;
import it.aspix.archiver.editor.SampleEditorScroll;
import it.aspix.archiver.editor.SampleEditorSintetico;
import it.aspix.archiver.editor.SpecieSpecificationEditorLinguette;
import it.aspix.archiver.editor.SpecieSpecificationEditorScroll;
import it.aspix.archiver.editor.SpecieSpecificationEditorSintetico;
import it.aspix.archiver.editor.SpecimenEditorLinguette;
import it.aspix.archiver.editor.SpecimenEditorScroll;
import it.aspix.archiver.editor.SpecimenEditorSintetico;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.OggettoSBD;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/****************************************************************************
 * La finestra principale, contiene tutti gli altri elementi dell'interfaccia:
 * - un gruppo di editors
 * - un gruppo di finestre per la ricerca
 * - un elenco di oggetti trovati
 * Fa in pratica da instradatore per i messaggi da un elemento all'altro,
 * mantenerlo semplice è un obiettivo! Il suo lavoro è aggregare altri elementi 
 * di alto livello. 
 * I parametri di ricerca e i trovati sono in due gruppi distinti perché potrebbe 
 * succedere che, ad esempio, trovo più liste di rilievi.
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class FinestraArchiver extends JFrame implements FornitoreGestoreMessaggi, VisualizzatoreOggetti{

    private static final long serialVersionUID = 1L;
    
    JPanel pannelloPrincipale = new JPanel(new BorderLayout());
    JSplitPane pannelloDiviso = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    Raccoglitore pannelloOggetti = new Raccoglitore();
    Raccoglitore pannelloParametri = new Raccoglitore(); 
    Raccoglitore pannelloTrovati = new Raccoglitore();
    JPanel pannelloRicerche = new JPanel(new BorderLayout());
    StatusBar barraDiStato = new StatusBar();
    
    ArrayList<ElencoInterattivo> gestoriTrovati = new ArrayList<ElencoInterattivo>();
    ArrayList<GestoreEditor> gestoriEditor = new ArrayList<GestoreEditor>();
    
    public FinestraArchiver(){
    	this.setTitle("archiver");
    	this.setIconImage(Icone.LogoAnArchive.getImage());
        // ---------- pannelli ----------
        this.getContentPane().add(pannelloPrincipale);
        pannelloPrincipale.add(pannelloDiviso, BorderLayout.CENTER);
        pannelloPrincipale.add(barraDiStato, BorderLayout.SOUTH);
        pannelloDiviso.add(pannelloOggetti);
        pannelloDiviso.add(pannelloRicerche);
        pannelloRicerche.add(pannelloParametri, BorderLayout.NORTH);
        pannelloRicerche.add(pannelloTrovati, BorderLayout.CENTER);
        pannelloDiviso.setOneTouchExpandable(true);
        
        // il gruppo dei parametri di ricerca
        pannelloParametri.addTab(new GestoreRicerca(new SpecimenEditorSintetico()));
        pannelloParametri.addTab(new GestoreRicerca(new SampleEditorSintetico()));
        pannelloParametri.addTab(new GestoreRicerca(new SpecieSpecificationEditorSintetico()));
        pannelloParametri.addTab(new GestoreRicerca(new BlobEditorSintetico()));
        pannelloParametri.setMinimale(true);

        // il gruppo degli editor
        GestoreEditor ge;
        // l'editor per i cartellini
        if(Proprieta.recupera("herbaria.interfaccia").equals("singolo")){
        	ge = new GestoreEditor(new SpecimenEditorScroll());
        }else{
        	ge = new GestoreEditor(new SpecimenEditorLinguette());
        }
        gestoriEditor.add(ge);
        pannelloOggetti.addTab(ge);
        // l'editor per i saggi
        if(Proprieta.recupera("vegetazione.interfaccia").equals("singolo")){
        	ge = new GestoreEditor(new SampleEditorScroll());
        }else{
        	ge = new GestoreEditor(new SampleEditorLinguette());
        } 
        gestoriEditor.add(ge);
        pannelloOggetti.addTab(ge);
        // l'editor per le specie
        if(Proprieta.recupera("check-list.interfaccia").equals("singolo")){
        	ge = new GestoreEditor(new SpecieSpecificationEditorScroll());
        }else{
        	ge = new GestoreEditor(new SpecieSpecificationEditorLinguette());
        }
        gestoriEditor.add(ge);
        pannelloOggetti.addTab(ge);
        // l'editor per i blob
        ge = new GestoreEditor(new BlobEditor());
        gestoriEditor.add(ge);
        pannelloOggetti.addTab(ge);
        
        // il gruppo degli elenchi degli oggetti trovati
        ElencoSpecimen eSpecimen = new ElencoSpecimen();
        gestoriTrovati.add(eSpecimen);
        pannelloTrovati.addTab(eSpecimen);
        ElencoSample eSurvey = new ElencoSample();
        gestoriTrovati.add(eSurvey);
        pannelloTrovati.addTab(eSurvey);
        ElencoSpecieSpecification eSpecieSpecification = new ElencoSpecieSpecification();
        gestoriTrovati.add(eSpecieSpecification);
        pannelloTrovati.addTab(eSpecieSpecification);
        ElencoBlob eBlob = new ElencoBlob();
        gestoriTrovati.add(eBlob);
        pannelloTrovati.addTab(eBlob);
        pannelloTrovati.setMinimale(true); 

        aggiungiMenuStandard(this);
        this.pack();
    }

    public GestoreMessaggi getGestoreMessaggi() {
        return barraDiStato;
    }

    /************************************************************************
     * Visualizza un oggetto in un editor
     * Inizia a cercare un visualizzatore partendo dalla linguetta in 
     * primo piano
     ***********************************************************************/
    public boolean visualizza(OggettoSBD oggetto, ElencoInterattivo elenco) {
        int inEsame;
        for(int i=0 ; i<gestoriEditor.size() ; i++){
            inEsame = (pannelloOggetti.getSelezionata() + i) % gestoriEditor.size();
            if(gestoriEditor.get(inEsame).isVisualizzabile(oggetto)){
                try {
                    gestoriEditor.get(inEsame).setOggettoSBD(oggetto, elenco);
                } catch (Exception e) {
                    Dispatcher.consegna(this, e);
                    e.printStackTrace();
                }
                if(inEsame!=pannelloOggetti.getSelezionata()){
                    pannelloOggetti.setSelezionata(inEsame);
                }
                return true;
            }
        }
        return false;
    }

    /************************************************************************
     * Visualizza un elenco di oggetti
     * Inizia a cercare un visualizzatore partendo dalla linguetta in 
     * primo piano
     ***********************************************************************/
    public boolean visualizza(OggettoSBD[] oggetto, OggettoSBD pattern) {
        int inEsame;
        for(int i=0 ; i<gestoriTrovati.size() ; i++){
            inEsame = (pannelloTrovati.getSelezionata() + i) % gestoriTrovati.size();
            if(gestoriTrovati.get(inEsame).isVisualizzabile(oggetto)){
                try {
                    gestoriTrovati.get(inEsame).setOggettoSBD(oggetto,pattern);
                } catch (Exception e) {
                    Dispatcher.consegna(this, e);
                    e.printStackTrace();
                }
                if(inEsame!=pannelloTrovati.getSelezionata()){
                    pannelloTrovati.setSelezionata(inEsame);
                }
                return true;
            }
        }
        return false;
    }
    
    /************************************************************************
     * Crea un menu da poter usare sia in queste classi che nei fallback 
     * (linnaeus e herbArchiver) 
     * @param proprietario la finestra che conterrà questo menu
     * @return
     ***********************************************************************/
    public static void aggiungiMenuStandard(final JFrame proprietario){
        JMenuBar menu = new JMenuBar();
        JMenu menuFile = new JMenu("file");
        JMenuItem itemEsci = new JMenuItem("esci");
        JMenu menuStrumenti = new JMenu("strumenti");
        JMenuItem itemPreferenze = new JMenuItem("preferenze");
        JMenuItem itemCaratteriSpeciali = new JMenuItem("caratteri speciali");
        JMenu menuAiuto = new JMenu("aiuto");
        JMenuItem itemInfo = new JMenuItem("info");
        // ---------- menu ----------
        menu.add(menuFile);
        menu.add(menuStrumenti);
        menu.add(menuAiuto);
        menuFile.add(itemEsci);
        menuStrumenti.add(itemPreferenze);
        menuStrumenti.addSeparator();
        menuStrumenti.add(itemCaratteriSpeciali);
        menuAiuto.add(itemInfo);
        // i menu 
        itemEsci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	WindowEvent wev = new WindowEvent(proprietario, WindowEvent.WINDOW_CLOSING);
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
            	// proprietario.processWindowEvent(new WindowEvent(proprietario,WindowEvent.WINDOW_CLOSING));
            }
        });
        itemPreferenze.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                ImpostazioneProprieta pn = new ImpostazioneProprieta();
                pn.setLocationRelativeTo(proprietario);
                pn.setVisible(true);
            }
        });
        itemCaratteriSpeciali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                if(Stato.ultimoUtilizzato != null){
                    Stato.debugLog.fine("Richiedo l'immissione di un carattere nella casella di testo"+Stato.ultimoUtilizzato);
                    Stato.ultimoUtilizzato.insersciCarattere();
                }else{
                    Stato.debugLog.fine("Nessuna casella disponibile");
                }
            }
        });
        itemInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {  
                Informazioni info=new Informazioni(
                        "Informazioni archiver", 
                        Icone.splash, 
                        "Client per la gestione dei dati del sistema anArchive",
                        Stato.versione 
                );
                info.setVisible(true);
                info.dispose();
            }
        });  
        proprietario.setJMenuBar(menu);
    }

}
