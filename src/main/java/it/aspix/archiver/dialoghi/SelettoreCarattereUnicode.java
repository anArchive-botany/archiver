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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

/*****************************************************************************
 * Selettere di caratteri Unicode, mancano le descrizioni ma aumenterebbero
 * enormemente la dimensione della cosa
 * @author Edoardo Panfili, studio Aspix
 ****************************************************************************/
public class SelettoreCarattereUnicode extends JDialog {

    private static final long serialVersionUID = 1L;

    public static final char NIENTE=0;

    public char lettera=NIENTE;      //la lettera selezionata

    String nomeGruppo[]={"Basic latin","Latin-1 supplement", "Latin Extended-A","Greek and Coptic","Combining Diacritical Marks"};
    char primoCarattere[] ={   0x0021,              0x00a1,             0x0100,            0x0386,                      0x0300};
    char ultimoCarattere[]={   0x007e,              0x00ff,             0x017f,            0x03f6,                      0x036f};
    String descrizione[]={
        null,
        null,
        null,
        "Alcuni sistemi operativi hanno problemi con l'allineamento verticale dei simboli.",
        "Scarso supporto praticamente in tutti i sistemi operativi (una lieve eccezioni per Mac OS X), per inserire il simbolo da solo basta inserire uno spazio seguito dal simbolo"
    };

    public SelettoreCarattereUnicode() {
        JTabbedPane pannelloLinguette = new JTabbedPane();
        JScrollPane pannelloScorrimento;
        JPanel contenitoreGruppo;
        JTextArea campoDescrizione;
        JPanel contenitoreTasti;
        JButton tasto;
    
        Border bordoDescrizione=BorderFactory.createEmptyBorder(5,5,5,5);
        this.setModal(true);
        this.setTitle("Selettore carattere Unicode");

        java.awt.event.ActionListener premuto=new java.awt.event.ActionListener(){
            public void actionPerformed(ActionEvent e){
                premuto_pulsante(e);
            }
        };

        for(int i=0;i<nomeGruppo.length;i++){
            pannelloScorrimento = new JScrollPane();
            pannelloScorrimento.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            contenitoreGruppo = new JPanel(new BorderLayout()); 
            contenitoreTasti=new JPanel(new GridLayout(0,10));
            for(char k=primoCarattere[i];k<ultimoCarattere[i];k++){
                tasto=new JButton(""+(k));
                tasto.setToolTipText("codice: "+Integer.toHexString(k));
                tasto.addActionListener(premuto);
                contenitoreTasti.add(tasto);
            }
            if(descrizione[i]!=null){
                campoDescrizione = new JTextArea(descrizione[i]); 
                campoDescrizione.setLineWrap(true);
                campoDescrizione.setWrapStyleWord(true);
                campoDescrizione.setBorder(bordoDescrizione);
                campoDescrizione.setEditable(false);
                campoDescrizione.setOpaque(false);
                campoDescrizione.setPreferredSize(new Dimension(contenitoreTasti.getPreferredSize().width,50));
                contenitoreGruppo.add(campoDescrizione,BorderLayout.NORTH); 
            }
            contenitoreGruppo.add(contenitoreTasti,BorderLayout.CENTER);
            pannelloScorrimento.getViewport().add(contenitoreGruppo, null);
            pannelloLinguette.add(pannelloScorrimento,nomeGruppo[i]);
        }
        this.getContentPane().add(pannelloLinguette, BorderLayout.CENTER);
        this.pack();
    }

    /** richiamato quando l'utente preme un pulsante */
    public void premuto_pulsante(ActionEvent e){
        lettera=((JButton)(e.getSource())).getText().charAt(0);
        this.setVisible(false);
    }
}