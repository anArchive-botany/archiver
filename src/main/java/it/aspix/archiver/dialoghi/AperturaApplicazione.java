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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.componenti.PannelloConSfondo;
import it.aspix.archiver.editor.IdentitaConnessioneEditor;

/******************************************************************************
 * Il pannello per il progresso nella creazione dell'applicazione
 * @author Edoardo Panfili, studio Aspix
 *****************************************************************************/
public class AperturaApplicazione extends JDialog {

    private static final long serialVersionUID = 1L;
    
    PannelloConSfondo pannelloPrincipale;
    GridBagLayout lPannelloPrincipale = new GridBagLayout();
    JLabel informazioniVersione = new JLabel();
    JPanel filler1 = new JPanel();
    JPanel filler2 = new JPanel();
    JLabel descrizioneProgresso = new JLabel("Avanzamento");
    JProgressBar progresso = new JProgressBar();
    
    JPanel pannelloPerIdentita = new JPanel(new BorderLayout());
    
    ImageIcon splash;
    IdentitaConnessioneEditor pi; 
    Color coloreEtichette;

    /************************************************************************
     * @param splash immagine di sfondo
     * @param testata da visualizzare in alto
     * @param colore delle etichette
     * @param passi totali per dimensionare la barra di progresso
     ***********************************************************************/
    public AperturaApplicazione(ImageIcon splash, String testata, Color colore, int passi) {
        this.splash = splash;
        this.coloreEtichette = colore;
        pannelloPrincipale = new PannelloConSfondo(splash);
        // ---------- i testi ----------
        informazioniVersione.setText(testata);
        // ---------- inserimento nei pannelli ----------
        this.getContentPane().add(pannelloPrincipale);
        pannelloPrincipale.setLayout(lPannelloPrincipale);
        pannelloPrincipale.setPreferredSize(new Dimension(600,360));
        
        pannelloPrincipale.add(informazioniVersione, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsSpreco, 0, 0));
        pannelloPrincipale.add(filler1,              new GridBagConstraints(0, 1, 1, 1, 1.0, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       CostantiGUI.insetsSpreco, 0, 0));
        pannelloPrincipale.add(pannelloPerIdentita,  new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,       CostantiGUI.insetsSpreco, 0, 0));
        pannelloPrincipale.add(filler2,              new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       CostantiGUI.insetsSpreco, 0, 0));
        pannelloPrincipale.add(descrizioneProgresso, new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsSpreco, 0, 0));
        pannelloPrincipale.add(progresso,            new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsSpreco, 0, 0));
        
        pannelloPrincipale.setOpaque(false);
        pannelloPerIdentita.setOpaque(false);
        this.setUndecorated(true);
        this.setResizable(false);
        filler1.setOpaque(false);
        filler2.setOpaque(false);
        descrizioneProgresso.setForeground(colore);
        informazioniVersione.setForeground(colore);
        
        this.pack();
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        UtilitaGui.centraDialogoAlloSchermo(this,UtilitaGui.LATO_ALTO, new Point(0,-250));
        progresso.setMaximum(passi);
    }

    public void setAvanzamento(String descrizione, int indice){
        descrizioneProgresso.setText(descrizione);
        progresso.setValue(indice);
        // pannelloPrincipale.updateUI();
    }
    
    /************************************************************************
     * @param chiediContenitore
     ***********************************************************************/
    public void chiediPassword(){
        pi = new IdentitaConnessioneEditor();
        pi.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));
        pi.setColoreEtichette(coloreEtichette);
        pi.setBackground(new Color(0,0,0,200));
        pi.setPadre(this);
        pannelloPerIdentita.add(pi,BorderLayout.CENTER);
        this.pack();
        pi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pi_actionPerformed(e);              
            }
        });
        pi.grabFocus();
        this.setModal(true);
        this.setVisible(false); // per renderla modale è necessario questi chiudi/riapri
        this.setVisible(true); 
    }
    
    public void pi_actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals("esci"))
            System.exit(0);
        pannelloPerIdentita.remove(pi);
        this.pack();    
        this.setVisible(false);// serve per cambiare il flag modal
        this.setModal(false);
        this.setVisible(true);
    }
    
    public void aggiornaConnessione(){
        pi.aggiornaConnessione();
    }
    
}