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
package it.aspix.archiver.assistenti;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.componenti.ComboBoxModelED;
import it.aspix.archiver.componenti.ComboBoxModelEDFactory;
import it.aspix.archiver.componenti.CoppiaED;
import it.aspix.archiver.componenti.FornitoreGestoreMessaggi;
import it.aspix.archiver.componenti.GestoreMessaggi;
import it.aspix.archiver.componenti.StatusBar;
import it.aspix.archiver.componenti.VisualizzatoreInformazioniSpecie;
import it.aspix.archiver.editor.SpecieRefEditor;
import it.aspix.sbd.obj.SpecieRef;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/************************************************************************
 * Gestisce un JDialog che contiene i dati di uin singolo nome di specie
 ***********************************************************************/
public class DialogoNomeSpecieRilevata extends JDialog implements FornitoreGestoreMessaggi{
   
    private static final long serialVersionUID = 1L;
    
    SpecieRefEditor specie = new SpecieRefEditor();
    VisualizzatoreInformazioniSpecie infoSpecie = new VisualizzatoreInformazioniSpecie();
    ComboBoxModelED modelloDeterminazione = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.DETERMINAZIONE);
    JComboBox<CoppiaED> determinazione = new JComboBox<>(modelloDeterminazione);
    StatusBar sb = new StatusBar();
    
    JButton annulla         = new JButton("annulla");
    JButton ok              = new JButton("ok");
    JComponent padre;
    
    private SurveyedSpecie specieInserita;
    
    public DialogoNomeSpecieRilevata(JComponent padre, SurveyedSpecie s){
        this.padre = padre;
        this.setTitle("Dati specie rilevata");

        JPanel principale = new JPanel(new GridBagLayout());
        JPanel pulsanti = new JPanel(new GridLayout(1,2));
        
        pulsanti.add(annulla);
        pulsanti.add(ok);
        principale.add(new JLabel("specie:"), 	new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        principale.add(specie,        			new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       CostantiGUI.insetsEtichetta, 0, 0));
        principale.add(determinazione,        	new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        principale.add(infoSpecie, 	            new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       CostantiGUI.insetsEtichetta, 0, 0));
        principale.add(pulsanti, 	            new GridBagConstraints(0, 2, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        principale.add(sb, 			            new GridBagConstraints(0, 3, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsEtichetta, 0, 0));
    
        annulla.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { azione_Annulla(); }
        });
        ok.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { azione_Ok(); }
        });

        this.getContentPane().add(principale);
        this.setSize(600,200);
        this.validate();
        this.setLocationRelativeTo(padre);
        this.setModal(true);
        specie.setVisualizzatoreInformazioni(infoSpecie);
    }
    
    public SurveyedSpecie getSurveyedSpecie(){
        return specieInserita;
    }
    
    protected void azione_Annulla(){
    	specieInserita = null;
        this.setVisible(false);
    }
    
    protected void azione_Ok(){
        specieInserita = new SurveyedSpecie();
        SpecieRef sr = specie.getSpecieRef();
        specieInserita.setSpecieRefName(sr.getName());
        specieInserita.setSpecieRefAliasOf(sr.getAliasOf());
        specieInserita.setSpecieRefId(sr.getId());
        specieInserita.setDetermination(modelloDeterminazione.getSelectedEnum());

        this.setVisible(false);
    }

	public GestoreMessaggi getGestoreMessaggi() {
		return sb;
	}
}
