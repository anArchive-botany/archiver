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
package it.aspix.archiver.editor;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.archiver.ManagerSuggerimenti;
import it.aspix.archiver.componenti.CampoData;
import it.aspix.archiver.componenti.CampoTestoUnicode;
import it.aspix.archiver.componenti.ComboBoxModelED;
import it.aspix.archiver.componenti.ComboBoxModelEDFactory;
import it.aspix.archiver.componenti.liste.RenderClassification;
import it.aspix.archiver.dialoghi.TestoMultiproprieta;
import it.aspix.archiver.eventi.SistemaException;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.sbd.obj.Cell;
import it.aspix.sbd.obj.Classification;
import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.Link;
import it.aspix.sbd.obj.Sample;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/****************************************************************************
 * @author edoardo
 ***************************************************************************/
public class SampleEditorDaEstendere extends SampleEditor{

    private static final long serialVersionUID = 1L;
    
    // altri editor utilizzati
    DirectoryInfoEditor editorDirectoryInfo;
    PublicationRefEditor editorPublicationRef;
    PlaceEditor editorPlace;
    CellEditorColori editorCella = new CellEditorColori();
    
    // gli ID
    String id;
    String pattern; 
    
    // la classificazione
    GridBagLayout lPannelloRilevatore       = new GridBagLayout();
    JPanel pannelloRilevatore               = new JPanel(lPannelloRilevatore);
    JLabel eComunita                        = new JLabel();
    CampoTestoUnicode comunita              = new CampoTestoUnicode();
    JLabel eRilevatore                      = new JLabel();
    CampoTestoUnicode rilevatore            = new CampoTestoUnicode();
    JLabel eData                            = new JLabel();
    CampoData data                          = new CampoData("data");
        
    // gli orari
    GridBagLayout lPannelloOrari= new GridBagLayout();
    JPanel pannelloOrari        = new JPanel(lPannelloOrari);
    JLabel eOraArrivo           = new JLabel();
    JTextField oraArrivo        = new CampoTestoUnicode();  
    JLabel eOraInizio           = new JLabel();
    JTextField oraInizio        = new CampoTestoUnicode();
    JLabel eOraFine             = new JLabel();
    JTextField oraFine          = new CampoTestoUnicode();
    
    // le note
    GridBagLayout lPannelloNote     = new GridBagLayout();
    JPanel pannelloNote             = new JPanel(lPannelloNote);
    JLabel eParoleChiave            = new JLabel();
    CampoTestoUnicode paroleChiave  = new CampoTestoUnicode();
    JLabel eNoteOriginali           = new JLabel();
    CampoTestoUnicode noteOriginali = new CampoTestoUnicode();
    JLabel eNote                    = new JLabel();
    CampoTestoUnicode note          = new CampoTestoUnicode();
    
    // la classificazione
    GridBagLayout lPannelloClassificazione = new GridBagLayout();
    JPanel pannelloClassificazione         = new JPanel(lPannelloClassificazione);
    DefaultListModel contenutoClassificazione = new DefaultListModel();
    JList  listaClassificazione            = new JList(contenutoClassificazione);
    JScrollPane scrollClassificazione      = new JScrollPane(listaClassificazione); 
    JLabel eTypus                          = new JLabel();
    ComboBoxModelED modelloTypus           = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.TYPUS_VEGETAZIONE);
    JComboBox typus                        = new JComboBox(modelloTypus);
    JLabel eCampoClassificazione           = new JLabel();
    JTextField campoClassificazione        = new JTextField();
    ComboBoxModelED modelloTipoClassificazione = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.CLASSIFICAZIONE); 
    JComboBox tipoClassificazione          = new JComboBox(modelloTipoClassificazione);
    JButton aggiungiClassificazione        = new JButton();
    
    /************************************************************************
     * Viene chiamato dai costruttori dalle clessi che estendono questa,
     * imposta gli elementio dell'iinterfaccia grafica
     ***********************************************************************/
    public void init() {
        // creo gli editor secondari
        editorDirectoryInfo = new DirectoryInfoEditor(this);
        editorPublicationRef = new PublicationRefEditor();
        editorPlace = new PlaceEditor("vegetazione.database");
        
        rilevatore.setName("sample.surveyer");
        oraArrivo.setName("sample.oraArrivo");
        oraInizio.setName("sample.oraInizio");
        oraFine.setName("sample.oraFine");
        campoClassificazione.setName("sample.classificazione");
        // imposto le stringhe
        eComunita.setText("nome provvisorio:");
        eTypus.setText("typus:");
        eRilevatore.setText("rilevatore:");
        eData.setText("data:");
        eCampoClassificazione.setText("classificazione:");
        aggiungiClassificazione.setText("aggiungi");
        eOraArrivo.setText("ora arrivo:");  
        eOraInizio.setText("ora inizio:");
        eOraFine.setText("ora fine:");
        eParoleChiave.setText("parole chiave:");
        eNoteOriginali.setText("note originali:");
        eNote.setText("note del rilevatore:");
        
        // -------------------- inserisco gli oggetti nei pannelli --------------------
        pannelloRilevatore.add(eComunita,       new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloRilevatore.add(comunita,        new GridBagConstraints(1, 0, 5, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloRilevatore.add(eRilevatore,     new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloRilevatore.add(rilevatore,      new GridBagConstraints(1, 3, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloRilevatore.add(eData,           new GridBagConstraints(4, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloRilevatore.add(data,            new GridBagConstraints(5, 3, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        
        pannelloOrari.add(eOraArrivo,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloOrari.add(oraArrivo,    new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloOrari.add(eOraInizio,   new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloOrari.add(oraInizio,    new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloOrari.add(eOraFine,     new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloOrari.add(oraFine,      new GridBagConstraints(5, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));

        pannelloNote.add(eParoleChiave,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNote.add(paroleChiave,   new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNote.add(eNoteOriginali, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNote.add(noteOriginali,  new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNote.add(eNote,          new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNote.add(note,           new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));

        // pannelloClassificazione.add(eCampoClassificazione,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta,  0, 0));
        pannelloClassificazione.add(campoClassificazione,    new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloClassificazione.add(tipoClassificazione,     new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,        CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloClassificazione.add(eTypus,                  new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta,  0, 0));
        pannelloClassificazione.add(typus,                   new GridBagConstraints(3, 0, 1, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloClassificazione.add(aggiungiClassificazione, new GridBagConstraints(4, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloClassificazione.add(scrollClassificazione,   new GridBagConstraints(0, 1, 6, 2, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsEtichetta,  0, 0));
         
        // impostazioni
        pannelloRilevatore.setBorder(UtilitaGui.creaBordoConTesto("Rilevatore",0,5));
        editorPublicationRef.setBorder(UtilitaGui.creaBordoConTesto("Pubblicazione",0,5));
        pannelloOrari.setBorder(UtilitaGui.creaBordoConTesto("Orari",0,5));
        pannelloNote.setBorder(UtilitaGui.creaBordoConTesto("Note",0,5));
        pannelloClassificazione.setBorder(UtilitaGui.creaBordoConTesto("Classificazione",0,5));
        editorPlace.setBorder(UtilitaGui.creaBordoConTesto("Geografici",0,5));
        scrollClassificazione.setMinimumSize(new Dimension(0,70));
        listaClassificazione.setCellRenderer(new RenderClassification());
        
        // ascoltatori
        aggiungiClassificazione.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                azioneAggiungiClassificazione();
            }
        });
        campoClassificazione.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { classificazione_keyPressed(e); }
        });
        listaClassificazione.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent e) { }
            public void mousePressed(MouseEvent e) { }
            public void mouseExited(MouseEvent e) { }
            public void mouseEntered(MouseEvent e) { }
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()==2){
                    azioneModificaClassificazione();
                }
            }
        });
        
        UtilitaGui.setOpaqueRicorsivo(this, false);
        try{
        	if(Proprieta.recupera("vegetazione.modelloPredefinito").equals("plot")){
        		CostruttoreOggetti.Suddivisione suddivisione = CostruttoreOggetti.suddivisioniDaPreferenze();
        		this.setSample(CostruttoreOggetti.createSample(null, 2, 2, suddivisione.nomi, suddivisione.divisioniX, suddivisione.divisioniY));
        	}else{
        		this.setSample(CostruttoreOggetti.createSample(null, 0, 0, null, null, null));
        	}
        }catch(Exception ex){
        	Dispatcher.consegna(this, ex);
        }
        ManagerSuggerimenti.check(pannelloRilevatore, pannelloOrari, pannelloClassificazione, editorPublicationRef, 
        		pannelloNote, editorPlace);
    }
    
    /************************************************************************
     * serve ad aggiungere una classificazione alla lista
     ***********************************************************************/
    private void azioneAggiungiClassificazione(){
        Classification c = new Classification();
        
        c.setType(modelloTipoClassificazione.getSelectedEnum());
        c.setTypus(modelloTypus.getSelectedEnum());
        c.setName(campoClassificazione.getText());
        contenutoClassificazione.addElement(c);
    }
    
    /************************************************************************
     * modifica di una voce della lista delle classificazioni
     ***********************************************************************/
    private void azioneModificaClassificazione(){
        int selezionato = listaClassificazione.getSelectedIndex();
        Classification c = (Classification) contenutoClassificazione.get(selezionato);
        contenutoClassificazione.remove(selezionato);
        modelloTypus.setSelectedEnum(c.getTypus());
        modelloTipoClassificazione.setSelectedEnum(c.getType());
        campoClassificazione.setText(c.getName());
    }

    /************************************************************************
     * mostra la finestra di edit per la sintassonomia
     ***********************************************************************/
    void classificazione_keyPressed(KeyEvent e) {
        if(KeyEvent.getKeyText(e.getKeyCode()).equals("E") && e.getModifiers()==InputEvent.CTRL_MASK){
        	String x = campoClassificazione.getText();
        	TestoMultiproprieta tm = new TestoMultiproprieta();
        	try {
				tm.setText(x);
				tm.setLocationRelativeTo(this);
	        	tm.setVisible(true);
	        	campoClassificazione.setText(tm.getText());
			} catch (Exception ex) {
				Dispatcher.consegna(this, ex);
			}
        }
    }

    /************************************************************************
     * @see it.aspix.archiver.editor.SampleEditor#getPlot()
     ***********************************************************************/
    public Sample getSample() throws ValoreException{
    	Sample risposta = new Sample();
    	
    	risposta.setPlace(editorPlace.getPlace());
    	risposta.setDirectoryInfo(editorDirectoryInfo.getDirectoryInfo());
    	risposta.setPublicationRef(editorPublicationRef.getPublicationRef());
        risposta.setCell(editorCella.getCell());
        risposta.setId(id);
        risposta.setPattern(pattern);
        
        risposta.setKeywords(paroleChiave.getText());
        risposta.setDate(data.getText());
        risposta.setSurveyer(rilevatore.getText());
        risposta.setCommunity(comunita.getText());
        for(int i=0 ; i<contenutoClassificazione.size() ; i++){
            risposta.addClassification((Classification) contenutoClassificazione.get(i));
        }
        
        risposta.setArrivalTime(oraArrivo.getText());
        risposta.setStartTime(oraInizio.getText());
        risposta.setStopTime(oraFine.getText());
        risposta.setOriginalNote(noteOriginali.getText());
        risposta.setNote(note.getText());
        
        // elimino gli strati vuoti dalla cella principale
        rimuoviStratiInutili(risposta);
        
        return risposta;
    }

    /************************************************************************
     * Elimina gli strati che non contengono informazioni utili
     * cioè senza specie e senza attributi
     * @param s
     ***********************************************************************/
    public static void rimuoviStratiInutili(Sample s){
        Cell principale = s.getCell();
        Level l[] = principale.getLevel();
        for(int i=0 ; i<l.length ; i++){
            if(l[i].getSurveyedSpecieCount()==0 && 
            		(l[i].getCoverage()==null || l[i].getCoverage().length()==0) &&
            		(l[i].getHeight()==null || l[i].getHeight().length()==0) &&
            		(l[i].getNote()==null || l[i].getNote().length()==0)
            ){
                principale.removeLevel(l[i]);
            }
        }
    }

    /************************************************************************
     * @throws ValoreException 
     * @throws SistemaException 
     * @see it.aspix.archiver.editor.SampleEditor#setPlot(it.aspix.sbd.obj.Sample)
     ***********************************************************************/
    public void setSample(Sample plot) throws SistemaException, ValoreException {
        if(plot == null){
        	throw new IllegalArgumentException("Plot non può essere null");
        }
        id = plot.getId(); // va fatto come prima cosa perché LevelEditorAlbero la legge 
        editorPlace.setPlace(plot.getPlace());
    	editorDirectoryInfo.setDirectoryInfo(plot.getDirectoryInfo());
        editorPublicationRef.setPublicationRef(plot.getPublicationRef());
        editorCella.setCell(plot.getCell());
        
        pattern = plot.getPattern();
        // il tipo di pattern determina anche l'interfaccia?
        paroleChiave.setText(plot.getKeywords());
        data.setText(plot.getDate());
        rilevatore.setText(plot.getSurveyer());
        comunita.setText(plot.getCommunity());
        // resetto il pannello della classificazione
        tipoClassificazione.setSelectedIndex(0);
        typus.setSelectedIndex(0);
        campoClassificazione.setText("");
        contenutoClassificazione.removeAllElements();
        for(int i=0 ; i<plot.getClassificationCount() ; i++){
           contenutoClassificazione.addElement(plot.getClassification(i));
        }
        
        oraArrivo.setText(plot.getArrivalTime());
        oraInizio.setText(plot.getStartTime());
        oraFine.setText(plot.getStopTime());
        noteOriginali.setText(plot.getOriginalNote());
        note.setText(plot.getNote());
        this.updateUI();
        try {
            Dispatcher.aggiornaRaccoglitore(this);
        } catch (Exception e) {
            Dispatcher.consegna(this, e);
            e.printStackTrace();
        }
    }

    @Override
    public String getLevelsSchema() {
        return editorCella.getLevelsSchema();
    }

    /************************************************************************
     * @return una stringa estremamente sintetica che rappresenta
     * il dato contenuto in questo editor
     ***********************************************************************/
    @Override
    public String toString(){
        DirectoryInfo di = editorDirectoryInfo.getDirectoryInfo();
        String nome = di.getContainerName()+"#"+di.getContainerSeqNo(); 
        return nome.length()>1 ? nome : "vegetazione";
    }
    
    /************************************************************************
     * Propaga la richiesta al DirectoryInfoEditor
     * @param l il link da aggiungere
     ***********************************************************************/
    public void addLink(Link l){
        editorDirectoryInfo.addLink(l);
    }

    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
	public String getSuggerimenti() {
		return null;
	}
}
