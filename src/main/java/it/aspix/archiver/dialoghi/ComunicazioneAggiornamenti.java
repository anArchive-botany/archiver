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
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

import it.aspix.archiver.Utilita;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.eventi.ProprietaException;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;


/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class ComunicazioneAggiornamenti extends JDialog {

    private static final long serialVersionUID = 1L;

    private boolean deviMostrareLaFinestra = true;
    
    JPanel pannelloPrincipale = new JPanel(new BorderLayout());
    JEditorPane testo = new JEditorPane();
    JButton ok = new JButton();
    
    String versioneAttuale;
    String nomeProprieta;
    
    public ComunicazioneAggiornamenti(String versione){       
        String ultimaVersione;
        nomeProprieta="generale.ultimaVersioneUsata";
        versioneAttuale=versione;
        try{
            ultimaVersione=Proprieta.recupera(nomeProprieta);
        }catch(Exception e){
            Stato.debugLog.throwing("x","x",e);
            ultimaVersione=null;
        }catch(ProprietaException e){
            Stato.debugLog.throwing("x","x",e);
            ultimaVersione=null;
        }
        deviMostrareLaFinestra = ultimaVersione==null || !ultimaVersione.equals(versione);
        Stato.debugLog.fine("ultima="+ultimaVersione+"  attuale="+versione+"  mostro="+deviMostrareLaFinestra);
        if(deviMostrareLaFinestra){
            this.setTitle("Variazioni in questa versione");
            ok.setText("OK, ho letto!");
            this.getContentPane().add(pannelloPrincipale);
            pannelloPrincipale.add(testo,BorderLayout.CENTER);
            pannelloPrincipale.add(ok,BorderLayout.SOUTH);
            // ---------- ascoltatori ----------
            ok.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(ActionEvent e) { ok_actionPerformed(); }
            });
            // ---------- impostazioni ----------
            String messaggio=Utilita.leggiStringa("changes.html","<h1>errore</h1><h2>non riesco a recuperare le informazioni</h2>");
            testo.setEditable(false);
            testo.setContentType("text/html");
            testo.setText(messaggio);
            testo.setOpaque(false);
            pannelloPrincipale.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            this.setModal(true);
            
            this.pack();
            UtilitaGui.centraDialogoAlloSchermo(this,UtilitaGui.CENTRO);
        }
    }

    public void setVisible(boolean stato){
        if(stato){
            if(deviMostrareLaFinestra)
                super.setVisible(true);
            else
                Stato.debugLog.fine("Non visualizzo la finestra");
        }else{
            super.setVisible(false);
        }
    }
    
    private void ok_actionPerformed() {
        Proprieta.aggiorna(nomeProprieta,versioneAttuale);
        this.dispose();
    }
}