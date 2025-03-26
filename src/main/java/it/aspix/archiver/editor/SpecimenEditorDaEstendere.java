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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.archiver.TopLevelEditor;
import it.aspix.archiver.componenti.AreaTestoUnicode;
import it.aspix.archiver.componenti.CampoData;
import it.aspix.archiver.componenti.CampoTestoUnicode;
import it.aspix.archiver.componenti.ComboBoxModelED;
import it.aspix.archiver.componenti.ComboBoxModelEDFactory;
import it.aspix.archiver.componenti.ComboSuggerimenti;
import it.aspix.archiver.componenti.CoppiaED;
import it.aspix.archiver.componenti.VisualizzatoreInformazioniSpecie;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.Specimen;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SpecimenEditorDaEstendere extends SpecimenEditor implements TopLevelEditor{

    private static final long serialVersionUID = 1L;

    private String idCartellino; // non viene visualizzato
    
    ComboBoxModelED modelloTypus = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.TYPUS);
    ComboBoxModelED modelloOrigine = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.ORIGINE);
    ComboBoxModelED modelloPossesso = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.POSSESSO);
    ComboBoxModelED modelloDeterminavitRevidit = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.DETERMINAVIT_REVIDIT);
    ComboBoxModelED modelloLegitDeterminavit = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.LEGIT_DETERMINAVIT);
    
    PlaceEditor editorPlace;  
    DirectoryInfoEditor editorDirectoryInfo;
       
    GridBagLayout lPannelloDatiBase = new GridBagLayout();
    JPanel pannelloDatiBase         = new JPanel(lPannelloDatiBase);
    JLabel eSpecie                  = new JLabel();
    SpecieRefEditor specie;         // creata nel costruttore
    JCheckBox bloccato              = new JCheckBox();
    JComboBox typus                 = new JComboBox();
    VisualizzatoreInformazioniSpecie ispecie     = new VisualizzatoreInformazioniSpecie();
    JComboBox<CoppiaED> legitDeterminavit     = new JComboBox<>();
    ComboSuggerimenti legit;        // creata nel costruttore
    JLabel eDataLegit               = new JLabel();
    CampoData dataLegit             = new CampoData("data legit");
    JLabel eNomeProgetto            = new JLabel();
    ComboSuggerimenti nomeProgetto; // creata nel costruttore
    JCheckBox nuova                 = new JCheckBox();
    JCheckBox prestabile            = new JCheckBox();
    JComboBox determinavitRevidit   = new JComboBox();
    ComboSuggerimenti determinavit; // creata in genericInit();
    JLabel eDataDeterminavit        = new JLabel();
    CampoData dataDeterminavit      = new CampoData("data determinavit");
    JLabel eStoria                  = new JLabel();
    AreaTestoUnicode storia         = new AreaTestoUnicode();
    JScrollPane scrollStoria        = new JScrollPane(storia);
    JLabel eConservazione           = new JLabel();
    ComboSuggerimenti conservazione; // creata in genericInit();
    JLabel eNumeroFogli             = new JLabel();
    CampoTestoUnicode numeroFogli   = new CampoTestoUnicode();
    JLabel eCaratteristiche         = new JLabel();
    CampoTestoUnicode caratteristiche = new CampoTestoUnicode();
    JLabel eAssociazione            = new JLabel();
    CampoTestoUnicode associazione  = new CampoTestoUnicode();
    JLabel eNoteOriginali           = new JLabel();
    CampoTestoUnicode noteOriginali = new CampoTestoUnicode();
    JLabel eNote                    = new JLabel();
    CampoTestoUnicode note          = new CampoTestoUnicode();
    JLabel eRifCampagna             = new JLabel();
    CampoTestoUnicode rifCampagna   = new CampoTestoUnicode();
    JLabel eNomeOriginale           = new JLabel();
    CampoTestoUnicode nomeOriginale = new CampoTestoUnicode();     

    GridBagLayout lPannelloStato    = new GridBagLayout();  
    JPanel pannelloStato            = new JPanel(lPannelloStato);            
    JLabel eOrigine                 = new JLabel();
    JComboBox origine               = new JComboBox();
    CampoTestoUnicode origineOrg    = new CampoTestoUnicode();
    JLabel ePossesso                = new JLabel();
    JComboBox possesso              = new JComboBox();
    CampoTestoUnicode possessoOrg   = new CampoTestoUnicode();
    GridBagLayout lPannelloCollocazione = new GridBagLayout();
    JPanel pannelloCollocazione     = new JPanel(lPannelloCollocazione);
    JLabel eCollocazione            = new JLabel();
    CampoTestoUnicode collocazione  = new CampoTestoUnicode();
    
    /************************************************************************
     * Viene chiamato dai costruttori dalle clessi che estendono questa,
     * imposta gli elementio dell'iinterfaccia grafica
     ***********************************************************************/
    protected void init(){
        specie = new SpecieRefEditor();
        editorPlace  = new PlaceEditor("herbaria.database");
        legit = new ComboSuggerimenti(Proprieta.recupera("herbaria.database"),"legit",3,true,false,null);
        editorDirectoryInfo = new DirectoryInfoEditor(this);
        determinavit  = new ComboSuggerimenti(Proprieta.recupera("herbaria.database"),"determinavit",3,true,true,null);
        conservazione = new ComboSuggerimenti(Proprieta.recupera("herbaria.database"),null,ComboSuggerimenti.SOLO_AVVIO,true,false,null);
        nomeProgetto  = new ComboSuggerimenti(Proprieta.recupera("herbaria.database"),"project",2,true,false,null);
        // ---------- le stringhe ----------
        eSpecie.setText("* specie:");
        bloccato.setText("nome di specie bloccato");
        eDataLegit.setText("data:");
        nuova.setText("nuova segnalazione");
        prestabile.setText("campione prestabile");
        eNomeProgetto.setText("progetto:");
        eDataDeterminavit.setText("data:");
        eStoria.setText("storia:");
        eConservazione.setText("conservazione:");
        eNumeroFogli.setText("numero fogli:");
        eCaratteristiche.setText("caratteristiche:");
        eAssociazione.setText("associazione:");
        eNoteOriginali.setText("note originali:");
        eNote.setText("note:");
        eNomeOriginale.setText("nome originale:");
        eRifCampagna.setText("id campagna:");
        eOrigine.setText("origine:");
        ePossesso.setText("possesso:");
        eCollocazione.setText("collocazione:");
        String [] suggerimentiConservazione={"ottimo","buono", "sufficiente", "pessimo"};
        conservazione.setSuggerimenti(suggerimentiConservazione);
        associazione.setName("specimen.inAssociationWith");
        collocazione.setName("specimen.collocation");
        caratteristiche.setName("specimen.characteristics");
        rifCampagna.setName("specimen.countrysideReference");
        // ---------- inserimento nei pannelli ----------
        
        pannelloDatiBase.add(typus,             new GridBagConstraints(1, 0, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,        CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDatiBase.add(bloccato,          new GridBagConstraints(6, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,        CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDatiBase.add(eSpecie,           new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDatiBase.add(specie,            new GridBagConstraints(1, 1, 7, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDatiBase.add(ispecie,           new GridBagConstraints(1, 2, 7, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsScrollTesto, 0, 0));
        pannelloDatiBase.add(legitDeterminavit, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDatiBase.add(legit,             new GridBagConstraints(1, 3, 5, 1, 0.6, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDatiBase.add(eDataLegit,        new GridBagConstraints(6, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDatiBase.add(dataLegit,         new GridBagConstraints(7, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        
        pannelloDatiBase.add(nuova,          new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDatiBase.add(prestabile,     new GridBagConstraints(5, 4, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDatiBase.add(eNomeProgetto,  new GridBagConstraints(6, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDatiBase.add(nomeProgetto,   new GridBagConstraints(7, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        
        pannelloDatiBase.add(determinavitRevidit, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDatiBase.add(determinavit,        new GridBagConstraints(1, 5, 5, 1, 0.6, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDatiBase.add(eDataDeterminavit,   new GridBagConstraints(6, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDatiBase.add(dataDeterminavit,    new GridBagConstraints(7, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDatiBase.add(eStoria,             new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,      CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDatiBase.add(scrollStoria,        new GridBagConstraints(1, 6, 7, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsScrollTesto, 0, 0));
        pannelloDatiBase.add(eConservazione,      new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDatiBase.add(conservazione,       new GridBagConstraints(1, 7, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDatiBase.add(eNumeroFogli,        new GridBagConstraints(6, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDatiBase.add(numeroFogli,         new GridBagConstraints(7, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDatiBase.add(eCaratteristiche,    new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));     
        pannelloDatiBase.add(caratteristiche,     new GridBagConstraints(1, 8, 7, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDatiBase.add(eAssociazione,       new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDatiBase.add(associazione,        new GridBagConstraints(1, 9, 8, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));        
        pannelloDatiBase.add(eNoteOriginali,      new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,  GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDatiBase.add(noteOriginali,       new GridBagConstraints(1, 10, 7, 1, 0.0, 0.0, GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDatiBase.add(eNote,               new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,  GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDatiBase.add(note,                new GridBagConstraints(1, 11, 7, 1, 0.0, 0.0, GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDatiBase.add(eNote,               new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,  GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDatiBase.add(note,                new GridBagConstraints(1, 11, 7, 1, 0.0, 0.0, GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDatiBase.add(eNomeOriginale,      new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,  GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDatiBase.add(nomeOriginale,       new GridBagConstraints(1, 12, 7, 1, 0.0, 0.0, GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));

        pannelloStato.add(eOrigine,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloStato.add(origine,      new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloStato.add(origineOrg,   new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloStato.add(ePossesso,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloStato.add(possesso,     new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloStato.add(possessoOrg,  new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));

        pannelloCollocazione.add(eCollocazione, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloCollocazione.add(collocazione,  new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloCollocazione.add(eRifCampagna,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloCollocazione.add(rifCampagna,   new GridBagConstraints(1, 1, 7, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,   CostantiGUI.insetsDatoTesto, 0, 0));
        
        // ---------- impostazioni ----------
        pannelloDatiBase.setBorder(UtilitaGui.creaBordoConTesto("Dati base",0,5));
        editorPlace.setBorder(UtilitaGui.creaBordoConTesto("Località",0,5));
        pannelloStato.setBorder(UtilitaGui.creaBordoConTesto("Origine e possesso",0,5));
        pannelloCollocazione.setBorder(UtilitaGui.creaBordoConTesto("Collocazione",0,5));
        specie.setVisualizzatoreInformazioni(ispecie);
        specie.setEditable(true);
        typus.setModel(modelloTypus);
        legitDeterminavit.setModel(modelloLegitDeterminavit);
        determinavitRevidit.setModel(modelloDeterminavitRevidit);
        ispecie.setMinimumSize(new Dimension(ispecie.getPreferredSize().width,legit.getPreferredSize().height*3));
        ispecie.setPreferredSize(new Dimension(ispecie.getPreferredSize().width,legit.getPreferredSize().height*3));
        scrollStoria.setPreferredSize(new Dimension(storia.getPreferredSize().width,(int)(storia.getPreferredSize().height*3.5)));
        origine.setModel(modelloOrigine);
        possesso.setModel(modelloPossesso);
        bloccato.setSelected(Proprieta.recupera("herbaria.bloccaNomeSpecie").equals("true"));
        scrollStoria.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        storia.setWrapStyleWord(true);
        storia.setLineWrap(true);
        
        try{
            Specimen spe = CostruttoreOggetti.createSpecimen(null);
            this.setSpecimen(spe);
        }catch(Exception ex){
            // FIXME: così soltanto non va bene ma al momento della creazione è possibile che l'oggetto non sia inserito in una gerarchia
            ex.printStackTrace();
        }
    }
    
    /************************************************************************
     * @return il cartellino rappresentato da questo editor
     ***********************************************************************/
    public Specimen getSpecimen(){
        Specimen cartellino = new Specimen();
        cartellino.setDirectoryInfo(editorDirectoryInfo.getDirectoryInfo());
        cartellino.setPlace(editorPlace.getPlace());
        cartellino.setSpecieRef(specie.getSpecieRef());
        cartellino.setNewSign((nuova.isSelected()?"true":"false"));
        cartellino.setAdmitLoan((prestabile.isSelected()?"true":"false"));
        cartellino.setLocked((bloccato.isSelected()?"true":"false"));
        cartellino.setTypus(modelloTypus.getSelectedEnum());
        cartellino.setId(idCartellino);
        cartellino.setOriginalName(nomeOriginale.getText());
        cartellino.setLegitType(modelloLegitDeterminavit.getSelectedEnum());
        cartellino.setLegitName(legit.getText());
        cartellino.setLegitDate(dataLegit.getText());
        cartellino.setSpecieAssignedBy(modelloDeterminavitRevidit.getSelectedEnum());
        cartellino.setDeterminavitName(determinavit.getText());
        cartellino.setDeterminavitDate(dataDeterminavit.getText());
        cartellino.setHistory(storia.getText());
        cartellino.setConservationStatus(conservazione.getText());
        cartellino.setNumberOfSheets(numeroFogli.getText());
        cartellino.setOriginMode(modelloOrigine.getSelectedEnum());
        cartellino.setOrigin(origineOrg.getText());
        cartellino.setPossessionMode(modelloPossesso.getSelectedEnum());
        cartellino.setPossession(possessoOrg.getText());
        cartellino.setCharacteristics(caratteristiche.getText());
        cartellino.setInAssociationWith(associazione.getText());
        cartellino.setCollocation(collocazione.getText());
        cartellino.setNote(note.getText());
        cartellino.setOriginalNote(noteOriginali.getText());
        cartellino.setCountrysideReference(rifCampagna.getText());
        cartellino.setProject(nomeProgetto.getText());
        return cartellino;
    }
    
    /************************************************************************
     * @param cartellino da visualizzare in questo editor
     ***********************************************************************/
    public void setSpecimen(Specimen cartellino){        
        if(cartellino == null){
            throw new IllegalArgumentException("Specimen non può essere null");
        }
        editorDirectoryInfo.setDirectoryInfo(cartellino.getDirectoryInfo());
        editorPlace.setPlace(cartellino.getPlace());
        nuova.setSelected(cartellino.getNewSign()!=null && cartellino.getNewSign().equals("true") );
        prestabile.setSelected(cartellino.getAdmitLoan()!=null && cartellino.getAdmitLoan().equals("true") );
        bloccato.setSelected(cartellino.getLocked()!=null && cartellino.getLocked().equals("true") );
        modelloTypus.setSelectedEnum(cartellino.getTypus());
        idCartellino = cartellino.getId();
        nomeOriginale.setText(cartellino.getOriginalName());
        specie.setSpecieRef(cartellino.getSpecieRef());
        modelloLegitDeterminavit.setSelectedEnum(cartellino.getLegitType());
        legit.setText(cartellino.getLegitName());
        dataLegit.setText(cartellino.getLegitDate());
        modelloDeterminavitRevidit.setSelectedEnum(cartellino.getSpecieAssignedBy());
        determinavit.setText(cartellino.getDeterminavitName());
        dataDeterminavit.setText(cartellino.getDeterminavitDate());
        storia.setText(cartellino.getHistory());
        conservazione.setText(cartellino.getConservationStatus());
        numeroFogli.setText(cartellino.getNumberOfSheets());
        modelloOrigine.setSelectedEnum(cartellino.getOriginMode());
        origineOrg.setText(cartellino.getOrigin());
        modelloPossesso.setSelectedEnum(cartellino.getPossessionMode());
        possessoOrg.setText(cartellino.getPossession());
        caratteristiche.setText(cartellino.getCharacteristics());
        associazione.setText(cartellino.getInAssociationWith());
        collocazione.setText(cartellino.getCollocation());
        note.setText(cartellino.getNote());
        noteOriginali.setText(cartellino.getOriginalNote());
        rifCampagna.setText(cartellino.getCountrysideReference());
        nomeProgetto.setText(cartellino.getProject());
        try {
            Dispatcher.aggiornaRaccoglitore(this);
        } catch (Exception e) {
            Dispatcher.consegna(this, e);
            e.printStackTrace();
        }
    }
    
    /************************************************************************
     * @return una stringa estremamente sintetica che rappresenta
     * il dato contenuto in questo editor
     ***********************************************************************/
    @Override
    public String toString(){
        DirectoryInfo di = editorDirectoryInfo.getDirectoryInfo();
        String nome = di.getContainerName()+"#"+di.getContainerSeqNo(); 
        return nome.length()>1 ? nome : "cartellino";
    }
    
    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
	public String getSuggerimenti() {
		return null;
	}
}