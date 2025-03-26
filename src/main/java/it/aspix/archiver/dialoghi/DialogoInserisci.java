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

import it.aspix.archiver.archiver.TopLevelEditor;
import it.aspix.archiver.editor.BlobEditor;
import it.aspix.archiver.editor.SpecimenEditorLinguette;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Blob;
import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.OggettoSBD;
import it.aspix.sbd.obj.SimpleBotanicalData;
import it.aspix.sbd.obj.Specimen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/****************************************************************************
 * Visualizza un editor per un oggetto con la possibilità di annullare
 * o di inviare il dato al server.
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DialogoInserisci extends JDialog {
    
    private static final long serialVersionUID = 1L;
    
    TopLevelEditor editorContenuto;
    
    DirectoryInfo informazioniOggettoInserito = null;

    public DialogoInserisci(OggettoSBD oggetto){
        JPanel principale = new JPanel(new BorderLayout());
        JButton ok = new JButton("ok (invia al server)");
        JButton annulla = new JButton("annulla");
        JPanel pulsanti = new JPanel(new GridLayout(1,2));
        
        // gestisce soltano cartellini e blob, se si modifica qui 
        // va modificato anche in azione_ok()
        if(oggetto instanceof Blob){
            editorContenuto = new BlobEditor();
        }else if(oggetto instanceof Specimen){
            editorContenuto = new SpecimenEditorLinguette();
        }else{
            throw new IllegalArgumentException("Questo oggetto non può visualizzare un "+oggetto.getClass().getCanonicalName());
        }
        try {
            editorContenuto.setOggettoSBD(oggetto);

            this.getContentPane().add(principale);
            principale.add((Component) editorContenuto, BorderLayout.CENTER);
            principale.add(pulsanti, BorderLayout.SOUTH);
            pulsanti.add(annulla);
            pulsanti.add(ok);
            this.setModal(true);
            
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    azione_ok();
                }
            });
            annulla.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DialogoInserisci.this.setVisible(false);
                }
            });
            this.pack();
        } catch (Exception e1) {
            Dispatcher.consegna(this, e1);
        }
    }
    
    void azione_ok(){
        OggettoSBD daInviare;
        SimpleBotanicalData risposta = null;
        DirectoryInfo di = null;
        try {
            daInviare = editorContenuto.getOggettoSBD();
            risposta = Stato.comunicatore.inserisci(daInviare, null, false);
            if(risposta.getMessage()[0].getType().equals(MessageType.ERROR)){
                Dispatcher.consegnaMessaggi(this,risposta);
            }else{
                if(editorContenuto instanceof BlobEditor){
                    di = risposta.getBlob()[0].getDirectoryInfo();
                }else{
                    di = risposta.getSpecimen()[0].getDirectoryInfo();
                }
            }
        } catch (Exception e) {
            Dispatcher.consegna(this, e);
        }
        informazioniOggettoInserito = di;
        this.setVisible(false);
    }
    
    public DirectoryInfo getdirectoryInfo(){
        return informazioniOggettoInserito;
    }
}
