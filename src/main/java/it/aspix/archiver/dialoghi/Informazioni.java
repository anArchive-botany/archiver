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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.DesktopApiWrapper;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.nucleo.Comunicatore;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;

/****************************************************************************
 * Finestra "info"
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class Informazioni extends JDialog {

    private static final long serialVersionUID = 1L;
    
    GridBagLayout layoutPannelloPrincipale = new GridBagLayout();
    JPanel pannelloPrincipale= new JPanel(layoutPannelloPrincipale);
    JLabel immagine         = new JLabel();
    JLabel eDescrizione     = new JLabel();
    JLabel eAmbiente		= new JLabel();
    JTextArea ambiente 		= new JTextArea();
    JLabel eRete        = new JLabel();
    JTextField eTmp     = new JTextField();
    JComponent invioBug;
    JButton chiudi      = new JButton();

    public Informazioni(String titolo, Icon splash, String descrizione, String versione) {

        // ==================== stringhe
        this.setTitle(titolo);
        eDescrizione.setText(descrizione);
        eAmbiente.setText("ambiente di esecuzione");
        //eJava.setText("java virtual machine: "+System.getProperty("java.version")+" ("+System.getProperty("java.vendor")+")");
        ambiente.setText(
        		"archiver: "+ versione + " (" + Stato.buildTimeStamp + ")\n"+
        		"java virtual machine: "+System.getProperty("java.version")+" ("+System.getProperty("java.vendor")+")\n"+
        	    "sistema operativo: "+System.getProperty("os.name")+" "+System.getProperty("os.version")+" ("+System.getProperty("os.arch")+")"
        );
        eRete.setText("server: " + Proprieta.recupera("connessione.server"));
        eTmp.setText("cartella file temporanei: "+Comunicatore.getTempFile(""));
        if(DesktopApiWrapper.isDesktopApiBrowseAvailable()){  
            invioBug = new JButton("Segna un errore/una richiesta");
            ((JButton)invioBug).addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                    	DesktopApiWrapper.openLink(Informazioni.this, "http://bugs.anarchive.it");
                    } catch (Exception ex) {
                        Dispatcher.consegna(Informazioni.this, ex);
                    }
                }
            });
        }else{
            invioBug = new JLabel("Per inviare richieste http://bugs.anarchive.it");
        }
        chiudi.setText("chiudi");
        
        // ==================== inserimento nei contenitori
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(pannelloPrincipale, BorderLayout.CENTER);
        pannelloPrincipale.add(immagine,      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsTestoDescrittivo, 0, 0));
        pannelloPrincipale.add(eDescrizione,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,       CostantiGUI.insetsTestoDescrittivo, 0, 0));
        pannelloPrincipale.add(eAmbiente,     new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,       CostantiGUI.insetsTestoDescrittivo, 0, 0));
        pannelloPrincipale.add(ambiente,      new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,       CostantiGUI.insetsTestoDescrittivo, 0, 0));
        pannelloPrincipale.add(eRete,         new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,       CostantiGUI.insetsTestoDescrittivo, 0, 0));
        pannelloPrincipale.add(eTmp,          new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,       CostantiGUI.insetsTestoDescrittivo, 0, 0));
        pannelloPrincipale.add(invioBug,      new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsTestoDescrittivoIsolato, 0, 0));
        pannelloPrincipale.add(chiudi,        new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,       CostantiGUI.insetsAzioneInBarra, 0, 0));
        
        // ==================== ascoltatori
        chiudi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chiudi_actionPerformed();
            }
        });

        // ==================== altro
        Font fontPiccolo=new java.awt.Font("Dialog", 1, 10);
        Font fontGrande=new java.awt.Font("Dialog", 1, 12);
      
        immagine.setIcon(splash);
        eDescrizione.setFont(fontGrande);
        eAmbiente.setFont(fontPiccolo);
        eRete.setFont(fontPiccolo);
        ambiente.setFont(fontPiccolo);
        eTmp.setFont(fontPiccolo);

        if (Stato.isMacOSX) {
            chiudi.setOpaque(false);
        }
        this.pack();
        this.setModal(true);
        this.setResizable(false);
        UtilitaGui.centraDialogoAlloSchermo(this, UtilitaGui.LATO_ALTO, new Point(0, -250));
    }

    void chiudi_actionPerformed() {
        this.setVisible(false);
    }
}