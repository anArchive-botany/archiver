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
package it.aspix.archiver.dialoghi;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.componenti.FornitoreGestoreMessaggi;
import it.aspix.archiver.componenti.GestoreMessaggi;
import it.aspix.archiver.componenti.StatusBar;
import it.aspix.archiver.editor.SurveyedSpecieEditor;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

/************************************************************************
 * gestisce un JDialog che contiene i dati di una singola specie
 ***********************************************************************/
public class DatiSurveyedSpecie extends JDialog implements FornitoreGestoreMessaggi{
   
    private static final long serialVersionUID = 1L;
    
    GridBagLayout layoutPrincipale = new GridBagLayout();
    JPanel pannelloPrincipale = new JPanel(layoutPrincipale);
    SurveyedSpecieEditor pannelloDatiSpecie;  
    JButton annulla         = new JButton();
    JButton ok              = new JButton();
    JComponent padre;
    StatusBar barraDiStato	= new StatusBar();
    
    
    boolean annullaModifiche=true;
    
    public DatiSurveyedSpecie(JComponent padre, SurveyedSpecie s){
        this.padre = padre;
        this.setTitle("Dati specie rilevata");
        pannelloDatiSpecie = new SurveyedSpecieEditor(SurveyedSpecieEditor.Layout.ORIZZONTALE);
        annulla.setText("annnulla");
        ok.setText("ok");
    
        pannelloPrincipale.add(pannelloDatiSpecie,  new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.WEST,   GridBagConstraints.BOTH,       CostantiGUI.insetsGruppo, 0, 0));
        pannelloPrincipale.add(annulla,             new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzioneInBarraUltimaDiGruppo,0,0));
        pannelloPrincipale.add(ok,                  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzioneInBarra,0,0));
        pannelloPrincipale.add(barraDiStato,		new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0),0,0));
       
        annulla.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { azione_Annulla(); }
        });
        ok.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { azione_Ok(); }
        });

        pannelloPrincipale.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        pannelloDatiSpecie.setSurveyedSpecie(s);
        this.getContentPane().add(pannelloPrincipale);
        this.pack();
        this.setLocationRelativeTo(padre);
        this.setModal(true);
    }
    
    public SurveyedSpecie getSurveyedSpecie(){
        if(annullaModifiche)
            return null;
        return pannelloDatiSpecie.getSurveyedSpecie();
    }
    
    protected void azione_Annulla(){
        annullaModifiche=true;
        this.setVisible(false);
    }
    
    protected void azione_Ok(){
        SurveyedSpecie ss = pannelloDatiSpecie.getSurveyedSpecie();
        if( !(ss.getDetermination().equals("sure")) && ss.getSampleId().length()<1 ){
            Dispatcher.consegna(padre, CostruttoreOggetti.createMessage("Hai inserito una specie incerta senza riferimento al campione.","it",MessageType.WARNING));
        }
        annullaModifiche=false;
        this.setVisible(false);
    }

	public GestoreMessaggi getGestoreMessaggi() {
		return barraDiStato;
	}
}
