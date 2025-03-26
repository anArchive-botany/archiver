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
import it.aspix.archiver.componenti.CampoTestoUnicode;
import it.aspix.archiver.componenti.ComboBoxModelED;
import it.aspix.archiver.componenti.ComboBoxModelEDFactory;
import it.aspix.archiver.componenti.ComboSuggerimenti;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.SpecieRef;
import it.aspix.sbd.obj.SpecieSpecification;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/*****************************************************************************
 * Strutture di base per permettere l'editazione di un nuovo nome di specie,
 * questo editor va esteso semplicemente per organizzarne i pannelli principali
 * @param Edoardo Panfili, studio Aspix
 ****************************************************************************/
public class SpecieSpecificationEditorDaEstendere extends SpecieSpecificationEditor{
    private static final long serialVersionUID = 1L;
    
    private String idSpecie="";
    
    ComboBoxModelED modelloStatoSpecie = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.STATO_SPECIE);
    ComboBoxModelED modelloApplicabilita = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.USO_NOME_SPECIE);
    
    DirectoryInfoEditor editorDirectoryInfo;
    PublicationRefEditor editorPublicationRef = new PublicationRefEditor();
    
    JPanel              pannelloGeneraliCodici = new JPanel(new BorderLayout());
    
    GridBagLayout       lPannelloGenerali = new GridBagLayout();
    JPanel              pannelloGenerali = new JPanel(lPannelloGenerali);
    JLabel              eFamiglia = new JLabel();
    ComboSuggerimenti   famiglia; // inizializzata nel costruttore
    JLabel              eFormabiologica = new JLabel();
    ComboSuggerimenti   formaBiologica; // inizializzata nel costruttore    
    JLabel              eGruppoTrofico = new JLabel();
    ComboSuggerimenti   gruppoTrofico; // inizializzata nel costruttore
    JLabel              eTipoCorologico = new JLabel();
    ComboSuggerimenti   tipoCorologico; // inizializzata nel costruttore
    
    GridBagLayout       lPannelloCodici = new GridBagLayout();
    JPanel              pannelloCodici = new JPanel(lPannelloCodici);
    JLabel              eCodiceGenere = new JLabel();
    CampoTestoUnicode   codiceGenere = new CampoTestoUnicode();
    JLabel              eCodiceSpecie = new JLabel();
    CampoTestoUnicode   codiceSpecie = new CampoTestoUnicode();
    JLabel              eCodiceSottospecie = new JLabel();
    CampoTestoUnicode   codiceSottospecie = new CampoTestoUnicode();
    
    GridBagLayout   lPannelloStato = new GridBagLayout();
    JPanel          pannelloStato = new JPanel(lPannelloStato);
    JLabel 			eApplicabilita = new JLabel();
    JComboBox		applicabilita = new JComboBox(modelloApplicabilita);
    JCheckBox		basionimo = new JCheckBox();
    JComboBox       statoSpecie = new JComboBox();
    JLabel          eNomeCanonico = new JLabel();
    SpecieRefEditor nomeCanonico; // inizializzata nel costruttore

    GridBagLayout       lPannelloNome = new GridBagLayout();
    JPanel              pannelloNome = new JPanel(lPannelloNome);
    JLabel              eColonnaIbrido = new JLabel();
    JLabel              eColonnaNome = new JLabel();
    JLabel              eColonnaAutore = new JLabel();
    JLabel              eRigaGenere = new JLabel();
    JCheckBox           xGenere = new JCheckBox();
    CampoTestoUnicode   genere = new CampoTestoUnicode();
    CampoTestoUnicode   fittizio1 = new CampoTestoUnicode();
    JLabel              eRigaSpecie = new JLabel();
    JCheckBox           xSpecie = new JCheckBox();
    CampoTestoUnicode   specieNome = new CampoTestoUnicode();
    CampoTestoUnicode   specieAutore = new CampoTestoUnicode();
    JLabel              eRigaSottospecie = new JLabel();
    JCheckBox           xSottospecie = new JCheckBox();
    CampoTestoUnicode   sottospecieNome = new CampoTestoUnicode();
    CampoTestoUnicode   sottospecieAutore = new CampoTestoUnicode();
    JLabel              eRigaVarieta = new JLabel();
    JCheckBox           xVarieta = new JCheckBox();
    CampoTestoUnicode   varietaNome = new CampoTestoUnicode();
    CampoTestoUnicode   varietaAutore = new CampoTestoUnicode();
    CampoTestoUnicode   sottovarietaNome = new CampoTestoUnicode();
    CampoTestoUnicode   sottovarietaAutore = new CampoTestoUnicode();
    JLabel              eRigaSottovarieta = new JLabel();
    JCheckBox           xSottovarieta = new JCheckBox();
    JLabel              eRigaForma = new JLabel();
    JCheckBox           xForma = new JCheckBox();
    CampoTestoUnicode   formaNome = new CampoTestoUnicode();
    CampoTestoUnicode   formaAutore = new CampoTestoUnicode();
    JLabel              eRigaRace = new JLabel();
    JCheckBox           xRace = new JCheckBox();
    CampoTestoUnicode   raceNome = new CampoTestoUnicode();
    CampoTestoUnicode   raceAutore = new CampoTestoUnicode();
    JLabel              eRigaSublusus = new JLabel();
    JCheckBox           xSublusus = new JCheckBox();
    CampoTestoUnicode   sublususNome = new CampoTestoUnicode();
    CampoTestoUnicode   sublususAutore = new CampoTestoUnicode();
    JLabel              eRigaCultivar = new JLabel();
    JCheckBox           xCultivar = new JCheckBox();
    CampoTestoUnicode   cultivar = new CampoTestoUnicode();
    CampoTestoUnicode   fittizio2 = new CampoTestoUnicode();
    JLabel              eSpecieIbridante = new JLabel();
    SpecieRefEditor     specieIbridante1; // inizializzata nel costruttore
    SpecieRefEditor     specieIbridante2; // inizializzata nel costruttore
    JLabel              nomeCostruito = new JLabel();

    GridBagLayout       lPannelloNote = new GridBagLayout();
    JPanel              pannelloNote = new JPanel(lPannelloNote);
    JLabel              eNote = new JLabel();
    CampoTestoUnicode   note = new CampoTestoUnicode();
    
    GridBagLayout       lPannelloIndicatori = new GridBagLayout();
    JPanel              pannelloIndicatori = new JPanel(lPannelloIndicatori);
    JLabel              eIe1 = new JLabel();
    JLabel              eIe2 = new JLabel();
    JLabel              eIe3 = new JLabel();
    JLabel              eIe4 = new JLabel();
    JLabel              eIe5 = new JLabel();
    JLabel              eIe6 = new JLabel();
    JLabel              eIe7 = new JLabel();
    JLabel              eIe8 = new JLabel();
    CampoTestoUnicode   ie1 = new CampoTestoUnicode();
    CampoTestoUnicode   ie2 = new CampoTestoUnicode();
    CampoTestoUnicode   ie3 = new CampoTestoUnicode();
    CampoTestoUnicode   ie4 = new CampoTestoUnicode();
    CampoTestoUnicode   ie5 = new CampoTestoUnicode();
    CampoTestoUnicode   ie6 = new CampoTestoUnicode();
    CampoTestoUnicode   ie7 = new CampoTestoUnicode();
    CampoTestoUnicode   ie8 = new CampoTestoUnicode();
  
    /************************************************************************
     * Viene chiamato dai costruttori dalle clessi che estendono questa,
     * imposta gli elementio dell'iinterfaccia grafica
     ***********************************************************************/
    protected void init() {        
        // inizializzazione degli oggetti che hanno bisogno di parametri a run-time
        famiglia = new ComboSuggerimenti(Proprieta.recupera("check-list.database"), "family", ComboSuggerimenti.SOLO_AVVIO, false, false, Proprieta.recupera("check-list.categorieGeneriche"));
        formaBiologica = new ComboSuggerimenti(Proprieta.recupera("check-list.database"), "lifeForm", ComboSuggerimenti.SOLO_AVVIO, false, false, null);
        gruppoTrofico = new ComboSuggerimenti(Proprieta.recupera("check-list.database"), "trophicGroup", ComboSuggerimenti.SOLO_AVVIO, false, false, null);
        tipoCorologico = new ComboSuggerimenti(Proprieta.recupera("check-list.database"), "corologicalType", ComboSuggerimenti.SOLO_AVVIO, false, false, null);
        nomeCanonico = new SpecieRefEditor();
        specieIbridante1 = new SpecieRefEditor();
        specieIbridante2 = new SpecieRefEditor();
        editorDirectoryInfo = new DirectoryInfoEditor(this);
        
        // ---------- le stringhe ----------
        eApplicabilita.setText("applicabilità:");
        basionimo.setText("Basionimo");
        eFamiglia.setText("famiglia:"); 
        eFormabiologica.setText("f. bio.:");
        eGruppoTrofico.setText("gr. trof.:");
        eTipoCorologico.setText("t. corol.:");
        eIe1.setText("IE 1 (descr) :");
        eIe2.setText("IE 2 (descr) :");
        eIe3.setText("IE 3 (descr) :");
        eIe4.setText("IE 4 (descr) :");
        eIe5.setText("IE 5 (descr) :");
        eIe6.setText("IE 6 (descr) :");
        eIe7.setText("IE 7 (descr) :");
        eIe8.setText("IE 8 (descr) :");
        eNomeCanonico.setText("canonico:");
        eNote.setText("note:");
        eCodiceGenere.setText("codice genere:");
        eCodiceSpecie.setText("specie:");
        eCodiceSottospecie.setText("sottospecie:");
        eSpecieIbridante.setText("genitori:");
        eRigaGenere.setText("genere:");
        eRigaSpecie.setText("specie:");
        eRigaSottospecie.setText("sottospecie:");
        eRigaVarieta.setText("variet\u00e0:");
        eRigaSottovarieta.setText("sottovariet\u00e0:");
        eRigaForma.setText("forma:");
        eRigaRace.setText("race:");
        eRigaSublusus.setText("sublusus:");
        eRigaCultivar.setText("cultivar:");
        eColonnaAutore.setText("autore");
        eColonnaIbrido.setText("X");
        eColonnaNome.setText("nome");
        
        // ---------- inserimento nei pannelli ----------
        pannelloGeneraliCodici.add(pannelloGenerali, BorderLayout.CENTER);
        pannelloGeneraliCodici.add(pannelloCodici, BorderLayout.SOUTH);
        
        pannelloGenerali.add(eFamiglia,         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloGenerali.add(famiglia,          new GridBagConstraints(1, 0, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloGenerali.add(eFormabiologica,   new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloGenerali.add(formaBiologica,    new GridBagConstraints(3, 0, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloGenerali.add(eGruppoTrofico,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloGenerali.add(gruppoTrofico,     new GridBagConstraints(1, 1, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloGenerali.add(eTipoCorologico,   new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloGenerali.add(tipoCorologico,    new GridBagConstraints(3, 1, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        
        pannelloCodici.add(eCodiceGenere,       new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloCodici.add(codiceGenere,        new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloCodici.add(eCodiceSpecie,       new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloCodici.add(codiceSpecie,        new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloCodici.add(eCodiceSottospecie,  new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloCodici.add(codiceSottospecie,   new GridBagConstraints(5, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        
        pannelloStato.add(statoSpecie,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloStato.add(eNomeCanonico,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        pannelloStato.add(nomeCanonico,   new GridBagConstraints(2, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        
        pannelloStato.add(eApplicabilita, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        pannelloStato.add(applicabilita,  new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloStato.add(basionimo,      new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        
        pannelloNome.add(eColonnaIbrido,        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNome.add(eColonnaNome,          new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNome.add(eColonnaAutore,        new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNome.add(eRigaGenere,           new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNome.add(xGenere,               new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,        CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(genere,                new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        // il genere non ha autore
        pannelloNome.add(eRigaSpecie,           new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNome.add(xSpecie,               new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,        CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(specieNome,            new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(specieAutore,          new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(eRigaSottospecie,      new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNome.add(xSottospecie,          new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,        CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(sottospecieNome,       new GridBagConstraints(2, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(sottospecieAutore,     new GridBagConstraints(3, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(eRigaVarieta,          new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNome.add(xVarieta,              new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,        CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(varietaNome,           new GridBagConstraints(2, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(varietaAutore,         new GridBagConstraints(3, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(eRigaSottovarieta,     new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNome.add(xSottovarieta,         new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,        CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(sottovarietaNome,      new GridBagConstraints(2, 5, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(sottovarietaAutore,    new GridBagConstraints(3, 5, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(eRigaForma,            new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNome.add(xForma,                new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,        CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(formaNome,             new GridBagConstraints(2, 6, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(formaAutore,           new GridBagConstraints(3, 6, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(eRigaRace,             new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNome.add(xRace,                 new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,        CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(raceNome,              new GridBagConstraints(2, 7, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(raceAutore,            new GridBagConstraints(3, 7, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(eRigaSublusus,         new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNome.add(xSublusus,             new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,        CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(sublususNome,          new GridBagConstraints(2, 8, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(sublususAutore,        new GridBagConstraints(3, 8, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(eRigaCultivar,         new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNome.add(xCultivar,             new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,        CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(cultivar,              new GridBagConstraints(2, 9, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        // cultivar non ha autore
        pannelloNome.add(eSpecieIbridante,     new GridBagConstraints(0,10, 2, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNome.add(specieIbridante1,      new GridBagConstraints(2,10, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(specieIbridante2,      new GridBagConstraints(3,10, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloNome.add(nomeCostruito,         new GridBagConstraints(0,12, 4, 1, 0.0, 0.0, GridBagConstraints.NORTH,  GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        
        pannelloNote.add(eNote,         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloNote.add(note,          new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        
        pannelloIndicatori.add(eIe1,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloIndicatori.add(ie1,     new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloIndicatori.add(eIe2,    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloIndicatori.add(ie2,     new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloIndicatori.add(eIe3,    new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloIndicatori.add(ie3,     new GridBagConstraints(5, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloIndicatori.add(eIe4,    new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloIndicatori.add(ie4,     new GridBagConstraints(7, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloIndicatori.add(eIe5,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloIndicatori.add(ie5,     new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloIndicatori.add(eIe6,    new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloIndicatori.add(ie6,     new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloIndicatori.add(eIe7,    new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloIndicatori.add(ie7,     new GridBagConstraints(5, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloIndicatori.add(eIe8,    new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloIndicatori.add(ie8,     new GridBagConstraints(7, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));

        // ---------- ascoltatori ----------
        statoSpecie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { statoSpecie_actionPerformed(); }
        });
        genere.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) { ricalcolaNome(); }
        });
        specieNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) { ricalcolaNome();  }
        });
        specieAutore.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) { ricalcolaNome(); }
        });
        sottospecieNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) { ricalcolaNome(); }
        });
        sottospecieAutore.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) { ricalcolaNome(); }
        });
        varietaNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) { ricalcolaNome(); }
        });
        varietaAutore.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) { ricalcolaNome(); }
        });
        sottovarietaNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) { ricalcolaNome(); }
        });
        sottovarietaAutore.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) { ricalcolaNome(); }
        });
        formaNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) { ricalcolaNome(); }
        });
        formaAutore.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) { ricalcolaNome(); }
        });
        cultivar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) { ricalcolaNome(); }
        });
        xGenere.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { x_actionPerformed(xGenere); }
        });
        xSpecie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { x_actionPerformed(xSpecie); }
        });
        xSottospecie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { x_actionPerformed(xSottospecie); }
        });
        xVarieta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { x_actionPerformed(xVarieta); }
        });
        xSottovarieta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { x_actionPerformed(xSottovarieta); }
        });
        xForma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { x_actionPerformed(xForma); }
        });
        xRace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { x_actionPerformed(xRace); }
        });
        xSublusus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { x_actionPerformed(xSublusus); }
        });
        xCultivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { x_actionPerformed(xCultivar); }
        });
        // ---------- i bordi ----------
        Border bordoNomeCostruito = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(255, 255, 162),new Color(255, 255, 114),new Color(124, 112, 39),new Color(178, 161, 56)),BorderFactory.createEmptyBorder(1,3,1,3));
        
        // ---------- impostazioni ----------
        statoSpecie.setModel(modelloStatoSpecie);
        editorPublicationRef.setBorder(UtilitaGui.creaBordoConTesto("riferimento bibliografico", 0, 0));
        //pannelloGenerali.setBorder(UtilitaGui.creaBordoConTesto("dati generali", 0, 0));
        //pannelloCodici.setBorder(UtilitaGui.creaBordoConTesto("codici", 0, 0));
        pannelloGeneraliCodici.setBorder(UtilitaGui.creaBordoConTesto("dati generali", 0, 0));
        pannelloStato.setBorder(UtilitaGui.creaBordoConTesto("stato", 0, 0));
        pannelloNome.setBorder(UtilitaGui.creaBordoConTesto("nome", 0, 0));
        pannelloNote.setBorder(UtilitaGui.creaBordoConTesto("note", 0, 0));
        pannelloIndicatori.setBorder(UtilitaGui.creaBordoConTesto("indicatori ecologici", 0, 0));
        nomeCostruito.setBorder(bordoNomeCostruito);
        nomeCostruito.setFont(new java.awt.Font("Dialog", 1, 10));
        nomeCostruito.setBorder(bordoNomeCostruito);
        nomeCostruito.setOpaque(true);
        UtilitaGui.setOpaqueRicorsivo(this, false);
        nomeCostruito.setText(" ");

        // imposta le dimensioni minime delle caselle
        int altezza=specieNome.getPreferredSize().height;
        nomeCostruito.setPreferredSize(new Dimension(130,altezza));
        // stato iniziale della parte riguardante i sinonimi
        specieIbridante1.setEnabled(false);
        specieIbridante2.setEnabled(false);
        
        Font usuale=eFamiglia.getFont(); 
        Font piccolo=new Font(usuale.getName(),usuale.getStyle(), (int)(usuale.getSize()*0.75) );
        eFamiglia.setFont(piccolo);
        eFormabiologica.setFont(piccolo);
        eGruppoTrofico.setFont(piccolo);
        eTipoCorologico.setFont(piccolo);
        statoSpecie_actionPerformed();
        
        gruppoTrofico.setPreferredSize(new Dimension(100,gruppoTrofico.getPreferredSize().height));
        gruppoTrofico.setMaximumSize(new Dimension(100,gruppoTrofico.getPreferredSize().height));
        gruppoTrofico.setMinimumSize(new Dimension(100,gruppoTrofico.getPreferredSize().height));

        try{
            SpecieSpecification spe = CostruttoreOggetti.createSpecieSpecification(null);
            this.setSpecieSpecification(spe);
        }catch(Exception ex){
            // FIXME: così soltanto non va bene ma al momento della creazione è possibile che l'oggetto non sia inserito in una gerarchia
            ex.printStackTrace();
        }
    }


    /***************************************************************************
     * @param sp la specie da visualizzare 
     **************************************************************************/
    public void setSpecieSpecification(SpecieSpecification sp){
        editorDirectoryInfo.setDirectoryInfo(sp.getDirectoryInfo());
        editorPublicationRef.setPublicationRef(sp.getPublicationRef());
        idSpecie = sp.getId();
        modelloApplicabilita.setSelectedEnum(sp.getUse());
        basionimo.setSelected(sp.getBasionym()!=null && sp.getBasionym().equals("true") );
        if(sp.getHibridAt()!=null && !sp.getHibridAt().equals("none")){
            JCheckBox daSelezionare=null;
            if(sp.getHibridAt().equals("genus"))        daSelezionare=xGenere;
            if(sp.getHibridAt().equals("specie"))       daSelezionare=xSpecie;
            if(sp.getHibridAt().equals("subspecie"))    daSelezionare=xSottospecie;
            if(sp.getHibridAt().equals("variety"))      daSelezionare=xVarieta;
            if(sp.getHibridAt().equals("subvariety"))   daSelezionare=xSottovarieta;
            if(sp.getHibridAt().equals("form"))         daSelezionare=xForma;
            if(sp.getHibridAt().equals("race"))         daSelezionare=xRace;
            if(sp.getHibridAt().equals("sublusus"))     daSelezionare=xSublusus;
            if(sp.getHibridAt().equals("cultivar"))     daSelezionare=xCultivar;
            if(daSelezionare!=null){
                daSelezionare.setSelected(true);
                x_actionPerformed(daSelezionare);
                specieIbridante1.setSpecieRef(new SpecieRef(sp.getHibridationDataName1(),""));
                specieIbridante2.setSpecieRef(new SpecieRef(sp.getHibridationDataName2(),""));
            }
        }else{
            //seleziona una casella (per deselezionare le altre)
            xGenere.setSelected(true);x_actionPerformed(xGenere);
            //deseleziona la casella
            xGenere.setSelected(false);x_actionPerformed(xGenere);
            specieIbridante1.setSpecieRef(new SpecieRef("",""));
            specieIbridante2.setSpecieRef(new SpecieRef("",""));
        }
        nomeCanonico.setSpecieRef(new SpecieRef(sp.getAliasOf(),""));
        if(sp.getAliasOf()!=null && sp.getAliasOf().length()>0){
            if(sp.getProParte()!=null && sp.getProParte().equals("true")){
                modelloStatoSpecie.setSelectedEnum("proparte");
            }else{
                modelloStatoSpecie.setSelectedEnum("sinonimo");    
            }
        }else{
            modelloStatoSpecie.setSelectedEnum("canonico");
        }
        
        codiceGenere.setText(sp.getGenusCode());
        codiceSpecie.setText(sp.getSpecieCode());
        codiceSottospecie.setText(sp.getSubspecieCode());
        famiglia.setText(sp.getFamily());
        tipoCorologico.setText(sp.getCorologicalType());
        formaBiologica.setText(sp.getLifeForm());
        gruppoTrofico.setText(sp.getTrophicGroup());
        
        genere.setText(sp.getGenusName());
        specieNome.setText(sp.getSpecieName());
        specieAutore.setText(sp.getSpecieAuthor());
        sottospecieNome.setText(sp.getSubspecieName());
        sottospecieAutore.setText(sp.getSubspecieAuthor());
        varietaNome.setText(sp.getVarietyName());
        varietaAutore.setText(sp.getVarietyAuthor());
        sottovarietaNome.setText(sp.getSubvarietyName());
        sottovarietaAutore.setText(sp.getSubvarietyAuthor());
        formaNome.setText(sp.getFormName());
        formaAutore.setText(sp.getFormAuthor());
        raceNome.setText(sp.getRaceName());
        raceAutore.setText(sp.getRaceAuthor());
        sublususNome.setText(sp.getSublususName());
        sublususAutore.setText(sp.getSublususAuthor());
        cultivar.setText(sp.getCultivarName());
        
        ie1.setText(sp.getIe1());
        ie2.setText(sp.getIe2());
        ie3.setText(sp.getIe3());
        ie4.setText(sp.getIe4());
        ie5.setText(sp.getIe5());
        ie6.setText(sp.getIe6());
        ie7.setText(sp.getIe7());
        ie8.setText(sp.getIe8());
        note.setText(sp.getNote());
        ricalcolaNome();
    }

    /***************************************************************************
     * @return la specie rappresentata in questo editor
     **************************************************************************/
    public SpecieSpecification getSpecieSpecification(){
        SpecieSpecification sp=new SpecieSpecification();
        
        sp.setDirectoryInfo(editorDirectoryInfo.getDirectoryInfo());
        sp.setPublicationRef(editorPublicationRef.getPublicationRef());
        sp.setId(idSpecie);
        sp.setHibridAt("");
        sp.setUse(modelloApplicabilita.getSelectedEnum());
        sp.setBasionym((basionimo.isSelected()?"true":"false"));
        if(xGenere.isSelected()) sp.setHibridAt("genus");
        if(xSpecie.isSelected()) sp.setHibridAt("specie");
        if(xSottospecie.isSelected()) sp.setHibridAt("subspecie");
        if(xVarieta.isSelected()) sp.setHibridAt("variety");
        if(xSottovarieta.isSelected()) sp.setHibridAt("subvariety");
        if(xForma.isSelected()) sp.setHibridAt("form");
        if(xRace.isSelected()) sp.setHibridAt("race");
        if(xSublusus.isSelected()) sp.setHibridAt("sublusus");
        if(xCultivar.isSelected()) sp.setHibridAt("cultivar");
        if(modelloStatoSpecie.getSelectedEnum().equals("proparte")){
            sp.setProParte("true");
        }else{
            sp.setProParte("false");
        }
        sp.setHibridationDataName1(specieIbridante1.getSpecieRef().getName());
        sp.setHibridationDataName2(specieIbridante2.getSpecieRef().getName());
        sp.setAliasOf(nomeCanonico.getSpecieRef().getName());
        sp.setGenusCode(codiceGenere.getText());
        sp.setSpecieCode(codiceSpecie.getText());
        sp.setSubspecieCode(codiceSottospecie.getText());
        sp.setFamily(famiglia.getText());
        sp.setCorologicalType(tipoCorologico.getText());
        sp.setLifeForm(formaBiologica.getText());
        sp.setTrophicGroup(gruppoTrofico.getText());
        
        sp.setGenusName(genere.getText());
        sp.setSpecieName(specieNome.getText());
        sp.setSpecieAuthor(specieAutore.getText());
        sp.setSubspecieName(sottospecieNome.getText());
        sp.setSubspecieAuthor(sottospecieAutore.getText());
        sp.setVarietyName(varietaNome.getText());
        sp.setVarietyAuthor(varietaAutore.getText());
        sp.setSubvarietyName(sottovarietaNome.getText());
        sp.setSubvarietyAuthor(sottovarietaAutore.getText());
        sp.setFormName(formaNome.getText());
        sp.setFormAuthor(formaAutore.getText());
        sp.setRaceName(raceNome.getText());
        sp.setRaceAuthor(raceAutore.getText());
        sp.setSublususName(sublususNome.getText());
        sp.setSublususAuthor(sublususAutore.getText());
        sp.setCultivarName(cultivar.getText());
        
        sp.setIe1(ie1.getText());
        sp.setIe2(ie2.getText());
        sp.setIe3(ie3.getText());
        sp.setIe4(ie4.getText());
        sp.setIe5(ie5.getText());
        sp.setIe6(ie6.getText());
        sp.setIe7(ie7.getText());
        sp.setIe8(ie8.getText());
        sp.setNote(note.getText());
        
        return sp;
    }
    /**************************************************************************
     * attiva o disattiva ala casella per la selezione del nome canonico
     *************************************************************************/
    void statoSpecie_actionPerformed() {
        String stato = modelloStatoSpecie.getSelectedEnum();
        if(stato.equals("canonico")){
            eNomeCanonico.setEnabled(false);
            nomeCanonico.setEnabled(false);
            nomeCanonico.setSpecieRef(new SpecieRef("",""));
        }else{
            eNomeCanonico.setEnabled(true);
            nomeCanonico.setEnabled(true);
        }
    }
    

    /**************************************************************************
     * deseleziona tutte le spunte, tranne quella premuta
     *************************************************************************/
    void x_actionPerformed(JCheckBox casella) {
        if(casella.isSelected()){
            //deseleziona tutte le caselle, se lo faccio usando un ButtonGroup 
            // non mi permette di deselezionare tutto
            xGenere.setSelected(false);
            xSpecie.setSelected(false);
            xSottospecie.setSelected(false);
            xVarieta.setSelected(false);
            xSottovarieta.setSelected(false);
            xForma.setSelected(false);
            xRace.setSelected(false);
            xSublusus.setSelected(false);
            xCultivar.setSelected(false);
            casella.setSelected(true);
            specieIbridante1.setEnabled(true);
            specieIbridante2.setEnabled(true);
            ricalcolaNome();
        }else{
            specieIbridante1.setEnabled(false);
            specieIbridante2.setEnabled(false);
            ricalcolaNome();
        }
    }

    /***************************************************************************
     *  imposta il nome di specie "calcolato"
     **************************************************************************/
    private void ricalcolaNome(){
        Stato.debugLog.fine("Ricalcolo il nome completo");
        String s = this.getSpecieSpecification().getNome();
        if(s!=null && s.length()>0)
            nomeCostruito.setText(s.toString());
        else
            nomeCostruito.setText(" ");
    }


    /************************************************************************
     * @return una stringa estremamente sintetica che rappresenta
     * il dato contenuto in questo editor
     ***********************************************************************/
    @Override
    public String toString(){
        DirectoryInfo di = editorDirectoryInfo.getDirectoryInfo();
        String nome = di.getContainerName()+"#"+di.getContainerSeqNo(); 
        return nome.length()>1 ? nome : "specie";
    }
    
    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
	public String getSuggerimenti() {
		return null;
	}
    
}