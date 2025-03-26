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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import it.aspix.archiver.CostantiGUI;

/****************************************************************************
 * Dialogo usato per i dati (contenitore, progessivo) di un nuovo link
 * le informazioni essenziali
 ***************************************************************************/
public class DatiMinimiLink extends JDialog {

    private static final long serialVersionUID = 1L;
    private boolean accettato;

    private JPanel pannelloPrincipale = new JPanel(new BorderLayout());
    
    private JPanel pannelloDati = new JPanel(new GridBagLayout());    
    private JLabel eContenitore = new JLabel();
    private JTextField contenitore = new JTextField();
    private JLabel eProgressivo = new JLabel();
    private JTextField progressivo = new JTextField();
    private JLabel eUrl = new JLabel();
    private JTextField url = new JTextField();

    private JPanel pannelloPulsanti = new JPanel(new GridBagLayout());
    private JButton ok = new JButton();
    private JButton annulla = new JButton();
    
    public DatiMinimiLink() {
        this.setTitle("Dimensioni nuovo plot");
        ok.setText("ok");
        annulla.setText("annulla");
        eContenitore.setText("contenitore:");
        eProgressivo.setText("progressivo:");
        eUrl.setText("URL:");
            
        // ---------- inserimento nei pannelli ----------
        this.getContentPane().add(pannelloPrincipale);
        pannelloPrincipale.add(pannelloDati, BorderLayout.CENTER);
        pannelloPrincipale.add(pannelloPulsanti, BorderLayout.SOUTH);
        pannelloDati.add(eContenitore,   new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDati.add(contenitore,    new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDati.add(eProgressivo,   new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDati.add(progressivo,    new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDati.add(eUrl,           new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDati.add(url,            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 150, 0));
        
        pannelloPulsanti.add(annulla,   new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,GridBagConstraints.EAST,    GridBagConstraints.NONE,    CostantiGUI.insetsAzioneInBarraUltimaDiGruppo, 0, 0));
        pannelloPulsanti.add(ok,        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,GridBagConstraints.CENTER,  GridBagConstraints.NONE,    CostantiGUI.insetsAzioneInBarra, 0, 0));
            
        // ---------- ascoltatori ----------
        ok.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) { ok_actionPerformed(); }
        });
        annulla.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) { annulla_actionPerformed(); }
        });
        this.setModal(true);
        this.pack();
    }

    /************************************************************************
     * Se si preme annulla semplicemente la finestra viene chiusa
     ***********************************************************************/
    void annulla_actionPerformed() {
        accettato=false;
        this.setVisible(false);
    }

    /************************************************************************
     * Alla pressione di OK si controlla le proprietà cambiate 
     * e si reimpostano solo quelle
     ***********************************************************************/
    void ok_actionPerformed() {
        accettato=true;
        this.setVisible(false);
    }
    
    /************************************************************************
     * @return true se l'operazione deve essere eseguita
     ***********************************************************************/
    public boolean isOk(){
        return accettato;
    }
    
    /************************************************************************ 
     * @return il nome del contenitore
     ***********************************************************************/
    public String getContenitore(){
        return contenitore.getText();
    }
    
    /************************************************************************ 
     * @return il progressivo del contenitore
     ***********************************************************************/
    public String getProgressivo(){
        return progressivo.getText();
    }
    
    /************************************************************************ 
     * @return la url
     ***********************************************************************/
    public String getUrl(){
        return url.getText();
    }

}