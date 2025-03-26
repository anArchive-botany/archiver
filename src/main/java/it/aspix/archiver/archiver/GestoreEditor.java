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

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.dialoghi.ConfermaNome;
import it.aspix.archiver.dialoghi.ConfermaTemporizzata;
import it.aspix.archiver.dialoghi.DatiNuovoPlot;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Blob;
import it.aspix.sbd.obj.Message;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.OggettoSBD;
import it.aspix.sbd.obj.Sample;
import it.aspix.sbd.obj.SimpleBotanicalData;
import it.aspix.sbd.obj.SpecieSpecification;
import it.aspix.sbd.obj.Specimen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/****************************************************************************
 * Incapsula un editor consentendo di inviare i dati al server
 * o di sfogliare il contenuto di una lista
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class GestoreEditor extends PannelloDescrivibile{
    
    private static final long serialVersionUID = 1L;
    private static final String INSERISCI = "inserisci";
    private static final String MODIFICA = "modifica";

    TopLevelEditor editorContenuto;
    ElencoInterattivo elencoInterattivo;
    JButton invia = new JButton(INSERISCI);         // globale perché li suo nome cambia inserisci<->modifica
    JButton precedente = new JButton("precedente"); // globale perché può essere attivato e disattivato
    JButton successivo = new JButton("successivo"); // globale perché può essere attivato e disattivato
    
    OggettoSBD oggettoOriginale = null; // quello che è stto visualizzato, prima delle modifiche dell'utente
    OggettoSBD ultimoInviato = null; // ultimo oggetto inviato al server
    
    public GestoreEditor(TopLevelEditor tle){
        GridBagLayout lPannelloAzioni = new GridBagLayout();
        JPanel pannelloAzioni = new JPanel(lPannelloAzioni);
        JButton nuovo = new JButton("nuovo");
        JButton pulisci = new JButton("pulisci");
        JButton rimuovi = new JButton("rimuovi");
        
        editorContenuto = tle;
        PannelloDescrivibile pd = (PannelloDescrivibile)tle;
        this.setTipoContenuto(pd.getTipoContenuto());
        this.setLayout(new BorderLayout());
        
        this.add(pannelloAzioni, BorderLayout.SOUTH);
        pannelloAzioni.add(precedente, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzioneInBarra, 0, 0));
        pannelloAzioni.add(successivo, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzioneInBarraUltimaDiGruppo, 0, 0));
        pannelloAzioni.add(nuovo,      new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzioneInBarra, 0, 0));
        pannelloAzioni.add(pulisci,    new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzioneInBarra, 0, 0));
        pannelloAzioni.add(rimuovi,    new GridBagConstraints(4, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzioneInBarra, 0, 0));
        pannelloAzioni.add(invia,      new GridBagConstraints(5, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzioneInBarra, 0, 0));
        this.add((Component) editorContenuto, BorderLayout.CENTER);
        pannelloAzioni.setOpaque(false);
        
        ((JPanel) editorContenuto).setBorder(CostantiGUI.bordoSpaziatoreTopLevelEditor);
        
        precedente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(elencoInterattivo!=null){
                    try {
                        OggettoSBD prec = elencoInterattivo.getPrecedente();
                        GestoreEditor.this.setOggettoSBD(prec, elencoInterattivo);
                    } catch (Exception ex) {
                        Dispatcher.consegna(GestoreEditor.this, ex);
                    }
                }
            }
        });
        successivo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(elencoInterattivo!=null){
                    try {
                        OggettoSBD prec = elencoInterattivo.getSuccessivo();
                        GestoreEditor.this.setOggettoSBD(prec, elencoInterattivo);
                    } catch (Exception ex) {
                        Dispatcher.consegna(GestoreEditor.this, ex);
                    }
                }
            }
        });
        nuovo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                azione_nuovo();
            }
        });
        pulisci.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                azione_pulisci();
            }
        });
        rimuovi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                azione_rimuovi();
            }
        });
        invia.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(invia.getText().equals(INSERISCI)){
                    azione_inserisci();
                }else{
                    azione_modifica();
                }
            }
        });
    }
    
    /************************************************************************
     * @retun una descrizione dell'oggetto contenuto
     ***********************************************************************/
    public String toString(){
        return editorContenuto.toString();
    }
    
    /************************************************************************
     * @param oggetto da visualizzare
     * @param elenco da cui proviene l'oggetto
     * @throws Exception
     ***********************************************************************/
    public void setOggettoSBD(OggettoSBD oggetto, ElencoInterattivo elenco) throws Exception{
        editorContenuto.setOggettoSBD(oggetto);
        elencoInterattivo = elenco;
        oggettoOriginale = oggetto.clone();
        precedente.setEnabled(elenco!=null);
        successivo.setEnabled(elenco!=null);
        invia.setText(MODIFICA);
    }
    
    /************************************************************************
     * @param oggetto da visualizzare
     * @return true se l'oggetto è visualizzabile
     ***********************************************************************/
    public boolean isVisualizzabile(Object oggetto){
        return editorContenuto.isVisualizzabile(oggetto);
    }
    
    /************************************************************************
     * crea un nuovo oggetto seguendo le preferenze e usando come
     * pattern l'oggetto visualizzato e lo visualizza
     ***********************************************************************/
    public void azione_nuovo(){
        OggettoSBD creato = null;
        try{
            if(editorContenuto.getOggettoSBD() instanceof Specimen){
                creato = CostruttoreOggetti.createSpecimen((Specimen) editorContenuto.getOggettoSBD());
                editorContenuto.setOggettoSBD(creato);
            }else if(editorContenuto.getOggettoSBD() instanceof Sample){
                if(Proprieta.recupera("vegetazione.modelloPredefinito").equals("plot")){
                    // va creato un nuovo plot, tramite dialogo
                    DatiNuovoPlot dnp = new DatiNuovoPlot();
                    UtilitaGui.centraDialogoAlloSchermo(dnp, UtilitaGui.CENTRO);
                    dnp.setVisible(true);
                    if(dnp.isOk()){
                        creato = dnp.getSample((Sample) editorContenuto.getOggettoSBD());
                    }
                }else{
                    creato = CostruttoreOggetti.createSample((Sample) editorContenuto.getOggettoSBD(), 0, 0, null, null, null);
                }
                if(creato!=null){
                	setOggettoSBD(creato,null);
                }
            }else if(editorContenuto.getOggettoSBD() instanceof SpecieSpecification){
            	creato = CostruttoreOggetti.createSpecieSpecification((SpecieSpecification) editorContenuto.getOggettoSBD());
                editorContenuto.setOggettoSBD(creato);
            }
            invia.setText(INSERISCI);
        }catch (Exception ex) {
            Dispatcher.consegna(GestoreEditor.this, ex);
            ex.printStackTrace();
        }
    }
    
    /************************************************************************
     * crea un nuovo oggetto vuoto e lo visualizza
     ***********************************************************************/
    public void azione_pulisci(){
        OggettoSBD creato = null;
        try{
            if(editorContenuto.getOggettoSBD() instanceof Specimen){
                creato = CostruttoreOggetti.createSpecimen(null);
            }else if(editorContenuto.getOggettoSBD() instanceof Sample){
                creato = CostruttoreOggetti.createSample((Sample) editorContenuto.getOggettoSBD(), 0, 0, null, null, null);
            } else if(editorContenuto.getOggettoSBD() instanceof SpecieSpecification){
                creato = CostruttoreOggetti.createSpecieSpecification(null);
            } else if(editorContenuto.getOggettoSBD() instanceof Blob){
                creato = CostruttoreOggetti.createBlob(null);
            }
            setOggettoSBD(creato,null);
            invia.setText(INSERISCI);
        }catch (Exception ex) {
            Dispatcher.consegna(GestoreEditor.this, ex);
            ex.printStackTrace();
        }
    }
    
    /************************************************************************
     * Invia un nuovo dato al server
     ***********************************************************************/
    public void azione_inserisci(){
        OggettoSBD daInviare;
        SimpleBotanicalData risposta;
        boolean inserimentoApprovato = true;
        
        try{
            daInviare = editorContenuto.getOggettoSBD();
            if(daInviare.equals(ultimoInviato)){
            	ConfermaTemporizzata ct = new ConfermaTemporizzata(1, true);
            	ct.addMessaggio(CostruttoreOggetti.createMessage("Hai appena inserito un oggetto identico a questo, vuoi comunque inserirlo di nuovo?", "it", MessageType.ERROR));
            	ct.setVisible(true);
            	inserimentoApprovato = ct.isChiusoOk();
            }
            if(inserimentoApprovato){
	            risposta = Stato.comunicatore.inserisci(daInviare, null, false);
	            oggettoOriginale = daInviare; // lo aggiorno perché ormai sta sul server
	            Dispatcher.consegnaMessaggi(GestoreEditor.this, risposta);
	            ultimoInviato = daInviare.clone();
            }
        }catch (Exception ex) {
            Dispatcher.consegna(GestoreEditor.this, ex);
            ex.printStackTrace();
        }
    }
    
    /************************************************************************
     * Invia un nuovo dato da modificare al server
     ***********************************************************************/
    public void azione_modifica(){
        OggettoSBD daInviare;
        SimpleBotanicalData risposta = null;
        boolean inserimentoApprovato = true;
        
        try{
            daInviare = editorContenuto.getOggettoSBD();
            if(daInviare.equals(ultimoInviato)){
            	ConfermaTemporizzata ct = new ConfermaTemporizzata(1, true);
            	ct.addMessaggio(CostruttoreOggetti.createMessage("Hai appena richiesto una modifica identica a questa, vuoi comunque chiederla di nuovo?", "it", MessageType.ERROR));
            	ct.setVisible(true);
            	inserimentoApprovato = ct.isChiusoOk();
            }
            if(inserimentoApprovato){
	            if(editorContenuto.getOggettoSBD() instanceof Specimen){
	                risposta = Stato.comunicatore.modifica(daInviare, null, false);
	                oggettoOriginale = daInviare; // pervenuto al server
	            }else if(editorContenuto.getOggettoSBD() instanceof Sample){
	                risposta = Stato.comunicatore.modifica(daInviare, null, false);
	                oggettoOriginale = daInviare; // pervenuto al server
	            } else if(editorContenuto.getOggettoSBD() instanceof SpecieSpecification){
	                SpecieSpecification specieDaInserire = (SpecieSpecification) daInviare;
	                SpecieSpecification speciePrimaDellaModifica = (SpecieSpecification) oggettoOriginale;
	                String suggerimento=null;
	                if(specieDaInserire.getAliasOf().length()==0 && speciePrimaDellaModifica.getAliasOf().length()!=0){
	                    ConfermaNome cn = new ConfermaNome("Da Sinonimo a Canonico","Il nome di specie era un sinonimo","e diventa un nome canonico");
	                    cn.addAlternativa("Nome canonico della stessa specie", "canonicalForSameSpecie", null, -1);
	                    cn.addAlternativa("Nome canonico di una specie a se stante", "canonicalForNewSpecie", null, -1);
	                    cn.setLocationRelativeTo(this);
	                    cn.setVisible(true);
	                    suggerimento = (String) cn.getSelezionata();
	                }
	                if( Proprieta.isTrue("check-list.simulazioneModificaSpecie") ){
	                    risposta = Stato.comunicatore.modifica(daInviare, suggerimento, true);
	                    ConfermaTemporizzata cs = new ConfermaTemporizzata();
	                    cs.addMessaggio(risposta.getMessage());
	                    UtilitaGui.centraDialogoAlloSchermo(cs, UtilitaGui.CENTRO);
	                    cs.setModal(true);
	                    cs.setVisible(true);
	                    if(cs.isChiusoOk()){
	                        risposta = Stato.comunicatore.modifica(daInviare, suggerimento, false);
	                        oggettoOriginale = daInviare;
	                    }else{
	                        risposta = new SimpleBotanicalData();
	                        risposta.addMessage(CostruttoreOggetti.createMessage("Modifica della specie annullata", "it", MessageType.INFO));
	                    }
	                }else{
	                    risposta = Stato.comunicatore.modifica(daInviare, suggerimento, false);
	                    oggettoOriginale = daInviare;
	                }
	                // FIXME: questa parte va rifatta:
	                /*if(Proprieta.isTrue("linnaeus.refreshRicercaAutomatico") && speciePerRefresh!=null){
	                    Stato.debugLog.fine("Richiedo il refresh della ricerca.");
	                    _eseguiRicerca(speciePerRefresh);
	                }*/
	            } else if(editorContenuto.getOggettoSBD() instanceof Blob){
	                risposta = Stato.comunicatore.modifica(daInviare, null, false);
	                oggettoOriginale = daInviare;
	            }            
	            Dispatcher.consegnaMessaggi(GestoreEditor.this, risposta);
	            ultimoInviato = daInviare.clone();
            }
        }catch (Exception ex) {
            Dispatcher.consegna(GestoreEditor.this, ex);
            ex.printStackTrace();
        }        
    }
    
    /************************************************************************
     * Invia un nuovo dato al server
     ***********************************************************************/
    public void azione_rimuovi(){
        OggettoSBD daInviare;
        SimpleBotanicalData risposta;
        Message messaggio; 
        ConfermaTemporizzata dialogo = new ConfermaTemporizzata(4);
        
        try{
            daInviare = editorContenuto.getOggettoSBD();
            String nomeOggetto;
            if(daInviare instanceof Sample){
            	nomeOggetto = "Il plot/rilievo verrà cancellato";
            }else if(daInviare instanceof Specimen){
            	nomeOggetto = "Il cartellino verrò cancellato";
            }else if(daInviare instanceof SpecieSpecification){
            	nomeOggetto = "La specie verrà cancellata";
            }else {
            	nomeOggetto = "L'oggetto binario verrà cancellato";
            }
            messaggio = CostruttoreOggetti.createMessage(nomeOggetto+" dal database, sei sicuro di volerlo fare?", "it", MessageType.WARNING);
            dialogo.addMessaggio(messaggio);
            UtilitaGui.centraDialogoAlloSchermo(dialogo, UtilitaGui.LATO_ALTO);
            dialogo.setVisible(true);
            if(dialogo.isChiusoOk()){
                risposta = Stato.comunicatore.rimuovi(daInviare, null, false);
                oggettoOriginale = daInviare; // lo aggiorno perché ormai sta sul server
                Dispatcher.consegnaMessaggi(GestoreEditor.this, risposta);
            }
        }catch (Exception ex) {
            Dispatcher.consegna(GestoreEditor.this, ex);
            ex.printStackTrace();
        }
    }
}
