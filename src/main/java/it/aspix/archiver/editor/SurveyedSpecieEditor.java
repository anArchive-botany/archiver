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
import it.aspix.archiver.Icone;
import it.aspix.archiver.componenti.CampoAbbondanza;
import it.aspix.archiver.componenti.ComboBoxModelED;
import it.aspix.archiver.componenti.ComboBoxModelEDFactory;
import it.aspix.archiver.componenti.VisualizzatoreInformazioniSpecie;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.sbd.obj.SpecieRef;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SurveyedSpecieEditor extends JPanel{
    
    private static final long serialVersionUID = 1L;
    
    String idSpecie;
    
    ComboBoxModelED modelloDeterminazione = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.DETERMINAZIONE);
    ComboBoxModelED modelloIncidenza = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.INCIDENZA);
    
    GridBagLayout lThis = new GridBagLayout();
    JPanel riga1 = new JPanel(new GridBagLayout());
    JPanel riga2 = new JPanel(new GridBagLayout());
    JPanel riga3 = new JPanel(new GridBagLayout());
    JLabel eSpecie = new JLabel();
    SpecieRefEditor specie; // creato nel costruttore
    VisualizzatoreInformazioniSpecie infoSpecie = new VisualizzatoreInformazioniSpecie();
    JCheckBox bloccato = new JCheckBox();
    JLabel eAbbondanza = new JLabel();
    CampoAbbondanza abbondanza = new CampoAbbondanza();
    JComboBox determinazione = new JComboBox();
    JLabel eIdCampione = new JLabel();
    JTextField idCampione = new JTextField();
    JCheckBox juvenile = new JCheckBox();
    JLabel eIncidenza = new JLabel();
    JComboBox incidenza = new JComboBox();
    JLabel eNote = new JLabel();
    JTextField note = new JTextField();
    
    // XXX: il layout è stato modificato, adesso è lo stesso per tutti, queste costanti non servono più
    public enum Layout{ORIZZONTALE, VERTICALE, VERTICALE_COMPRESSO};
    
    /************************************************************************
     * @param layout 0 per il layout orizzontale, 1 per quello verticale
     ***********************************************************************/
    public SurveyedSpecieEditor(Layout layout) {
        specie = new SpecieRefEditor();
        // ----------------------------- testi ------------------------------
        // bloccato.setText("bloccato");
        eSpecie.setText("specie:");
        bloccato.setSelectedIcon(Icone.LucchettoChiuso);
        bloccato.setIcon(Icone.LucchettoAperto);
        eAbbondanza.setText("abbond.:");
        eIdCampione.setText("id campione:");
        juvenile.setText("juvenile");
        eIncidenza.setText("incidenza:");
        eNote.setText("note:");
        // --------------------- inserimento nei pannelli -------------------
        this.setLayout(lThis);
        this.add(eSpecie,        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,       GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(riga1,          new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,     GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        riga1.add(specie,        new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,     GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 200, 0));       
        riga1.add(determinazione,new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,     GridBagConstraints.NONE,       CostantiGUI.insetsDatoTesto, 0, 0));
        riga1.add(bloccato,      new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,     GridBagConstraints.NONE,       CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eAbbondanza,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,       GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(riga2,          new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,     GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        riga2.add(abbondanza,    new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,     GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        riga2.add(eIdCampione,   new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,       GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        riga2.add(idCampione,    new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,     GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        riga2.add(juvenile,      new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,     GridBagConstraints.NONE,       CostantiGUI.insetsDatoTesto, 0, 0));    
        riga2.add(eIncidenza,    new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,       GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        riga2.add(incidenza,     new GridBagConstraints(5, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,     GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(infoSpecie,     new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,     GridBagConstraints.HORIZONTAL, CostantiGUI.insetsTestoDescrittivo, 0, 0));
        this.add(eNote,          new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,       GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(note,           new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,     GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        
        // -------------------------- impostazioni --------------------------
        determinazione.setModel(modelloDeterminazione);
        incidenza.setModel(modelloIncidenza);
        specie.setVisualizzatoreInformazioni(infoSpecie);
        specie.setEditable(true);
        abbondanza.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { chiamaActionPerformed(); }
        });
        idCampione.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { chiamaActionPerformed(); }
        });
        
        eSpecie.setFont(CostantiGUI.fontPiccolo);
        eAbbondanza.setFont(CostantiGUI.fontPiccolo);
        eIdCampione.setFont(CostantiGUI.fontPiccolo);
        eIncidenza.setFont(CostantiGUI.fontPiccolo);
        eNote.setFont(CostantiGUI.fontPiccolo);
        infoSpecie.setFont(CostantiGUI.fontPiccolo);
        
        infoSpecie.setMinimumSize(new Dimension(infoSpecie.getPreferredSize().width, idCampione.getPreferredSize().height*3));
        infoSpecie.setPreferredSize(new Dimension(infoSpecie.getPreferredSize().width, (int) (idCampione.getPreferredSize().height*3*0.75)));
        bloccato.setSelected(Proprieta.recupera("vegetazione.bloccaNomeSpecie").equals("true"));
    }
        
    /************************************************************************
     * @param s la specie rilevata da visualizzare
     ***********************************************************************/
    public void setSurveyedSpecie(SurveyedSpecie s){
        if(s == null){
        	throw new IllegalArgumentException("SurveyedSpecie non può essere null");
        }
        idSpecie = s.getSpecieRefId();
        specie.setSpecieRef(new SpecieRef(s.getSpecieRefName(), s.getSpecieRefAliasOf()));
        bloccato.setSelected(s.getLocked()!=null && s.getLocked().equals("true"));
        abbondanza.setText(s.getAbundance());
        modelloDeterminazione.setSelectedEnum(s.getDetermination());
        idCampione.setText(s.getSampleId());
        juvenile.setSelected(s.getJuvenile()!=null && s.getJuvenile().equals("true"));
        modelloIncidenza.setSelectedEnum(s.getIncidence());
        note.setText(s.getNote());
    }
    
    /************************************************************************
     * @return la specie visualizzata
     ***********************************************************************/
    public SurveyedSpecie getSurveyedSpecie(){
        SurveyedSpecie s = new SurveyedSpecie();
        s.setSpecieRefId(idSpecie);
        s.setSpecieRefName(specie.getSpecieRef().getName());
        s.setSpecieRefAliasOf(specie.getSpecieRef().getAliasOf());
        s.setLocked((bloccato.isSelected() ? "true" : "false"));
        s.setAbundance(abbondanza.getText());
        s.setDetermination(modelloDeterminazione.getSelectedEnum());
        s.setSampleId(idCampione.getText());
        s.setJuvenile(juvenile.isSelected() ? "true" : "false");
        s.setIncidence(modelloIncidenza.getSelectedEnum());
        s.setNote(note.getText());
        return s;
    }
    
    @Override
    public void grabFocus(){
        super.grabFocus();
        specie.grabFocus();
    }
    
    // Gestione degli eventi
    ArrayList<ActionListener> ascoltatori = new ArrayList<ActionListener>();
    /************************************************************************
     * @param al l'ascoltatore da aggiungere
     ***********************************************************************/
    public void addActionListener(ActionListener al){
        ascoltatori.add(al);
    }
    
    private void chiamaActionPerformed(){
        for(ActionListener al: ascoltatori){
            al.actionPerformed(null);
        }
    }
}
