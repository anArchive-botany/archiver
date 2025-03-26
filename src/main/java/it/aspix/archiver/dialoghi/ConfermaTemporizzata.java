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
import it.aspix.archiver.Icone;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.componenti.GestoreMessaggi;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Message;
import it.aspix.sbd.obj.MessageType;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class ConfermaTemporizzata extends JDialog implements GestoreMessaggi{

    private static final long serialVersionUID = 1L;
    
    private static final int LARGHEZZA_TESTO_PREFERITA = 500;
    
    Aspetta attesa = null;
    private int tempoAttesa;
    
    private boolean chiusoOk = false;
    /************************************************************************
     * @return true se è stato premuto ok
     ***********************************************************************/
    public boolean isChiusoOk() {
        return chiusoOk;
    }

    JPanel pannelloPrincipale = new JPanel(new BorderLayout());
    JPanel pannelloMessaggi = new JPanel(new GridLayout(0,1));
    JPanel pannelloAzioni = new JPanel(new GridBagLayout());
    JButton ok = new JButton();
    JButton annulla = new JButton();
    
    /************************************************************************
     * Il dialogo non avrà ritardi per la visulizzazione del pulsante OK
     ***********************************************************************/
    public ConfermaTemporizzata(){
        tempoAttesa = 0;
        init(true);
    }

    /************************************************************************
     * Il dialogo avrà un ritardo prima della visulizzazione del pulsante OK
     * @param time il tempo da aspettare (in millisecondi)
     ***********************************************************************/
    public ConfermaTemporizzata(int time){
        tempoAttesa = time;
        init(true);
    }

    /************************************************************************
     * Il dialogo avrà un ritardo prima della visulizzazione del pulsante OK
     * @param time il tempo da aspettare (in millisecondi)
     * @param mostraAnnulla se true mostra anche il tasto annulla
     ***********************************************************************/
    public ConfermaTemporizzata(int time, boolean mostraAnnulla){
        tempoAttesa = time;
        init(mostraAnnulla);
    }
    
    /************************************************************************
     * @param mostraAnnulla se true mostra anche la casella annulla
     ***********************************************************************/
    private final void init(boolean mostraAnnulla){
        ok.setText("ok");
        annulla.setText("annulla");
        this.setTitle("Conferma simulazione");
        pannelloAzioni.add(ok,      new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE, CostantiGUI.insetsAzioneInBarraUltimaDiGruppo, 0, 0));
        if(mostraAnnulla){
        	pannelloAzioni.add(annulla, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE, CostantiGUI.insetsAzioneInBarra, 0, 0));
        }
        pannelloPrincipale.add(pannelloMessaggi, BorderLayout.CENTER);
        pannelloPrincipale.add(pannelloAzioni, BorderLayout.SOUTH);
        this.getContentPane().add(pannelloPrincipale);
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { azione(true); }
        });
        annulla.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { azione(false); }
        });
        this.setModal(true);
        Dimension dime = this.getSize();
        UtilitaGui.centraDialogoAlloSchermo(this, UtilitaGui.CENTRO, new Point(dime.width/2,0));
    }
    
    /************************************************************************
     * Questo metodo (se necessario) inizializza il timer per il pulsante OK
     ***********************************************************************/
    public void setVisible(boolean stato){
        if(stato){
            if(tempoAttesa != 0){
                Stato.debugLog.fine("Attesa temporizza per intervalli "+tempoAttesa);
                ok.setEnabled(false);
                attesa = new Aspetta(tempoAttesa, ok);
                attesa.start();
            }
            super.setVisible(true);
        }else{
            super.setVisible(false);
        }
    }
    
    /************************************************************************
     * Processa la pressione di un pulsante
     ***********************************************************************/
    public void azione(boolean esito){
        chiusoOk = esito;
        this.dispose();
    }
    
    /************************************************************************
     * @see it.aspix.archiver.componenti.GestoreMessaggi#addMessaggio(it.aspix.sbd.obj.Message)
     ***********************************************************************/
    public void addMessaggio(Message messaggio) {
        JPanel temp = new JPanel(new BorderLayout());
        JTextArea testo = new JTextArea();
        JLabel icona = new JLabel();
        
        testo.setWrapStyleWord(true);
        testo.setLineWrap(true);
        testo.setOpaque(false);
        testo.setEditable(false);
        testo.setText(messaggio.getText(0).getText()); //XXX: prende solo il primo, qui si parla della serie di messaggi internazionalizzati
        if(messaggio.getType().equals(MessageType.ERROR))
            icona.setIcon(Icone.MessageError);
        else if(messaggio.getType().equals(MessageType.WARNING))
            icona.setIcon(Icone.MessageWarning);
        else
            icona.setIcon(Icone.MessageInfo);
        icona.setBorder(BorderFactory.createEmptyBorder(20,20,0,20));
        testo.setBorder(BorderFactory.createEmptyBorder(20,00,0,20));
        temp.add(icona, BorderLayout.WEST);
        temp.add(testo, BorderLayout.CENTER);
        testo.setPreferredSize(new Dimension(LARGHEZZA_TESTO_PREFERITA, 64));
        pannelloMessaggi.add(temp);
        this.pack();
    }

    /************************************************************************
     * @see it.aspix.archiver.componenti.GestoreMessaggi#addMessaggio(java.lang.Exception)
     ***********************************************************************/
    public void addMessaggio(Exception ex) {
        addMessaggio(CostruttoreOggetti.createMessage(ex.getMessage(), "en", MessageType.ERROR));
    }

    /************************************************************************
     * @see it.aspix.archiver.componenti.GestoreMessaggi#addMessaggio(it.aspix.sbd.obj.Message[])
     ***********************************************************************/
    public void addMessaggio(Message[] messaggio) {
        for(int i=0;i<messaggio.length;i++)
            addMessaggio(messaggio[i]);
    }
    
    /************************************************************************
     * Ciclo di attesa durante il quale si aggirna il testo del pulsante OK
     ***********************************************************************/
    private class Aspetta extends Thread{
        private int unita;
        JButton pulsante;
        public Aspetta(int unita, JButton pulsante){
            this.unita = unita;
            this.pulsante = pulsante;
        }
        public void run(){
            try {
                for(int i=unita ; i>0 ; i--){
                    pulsante.setText("ok ("+i+")");
                    sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pulsante.setText("ok");
            pulsante.setEnabled(true);
        }
        
    }
}
