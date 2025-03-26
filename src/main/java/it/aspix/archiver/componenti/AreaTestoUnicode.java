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
package it.aspix.archiver.componenti;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.dialoghi.SelettoreCarattereUnicode;
import it.aspix.archiver.nucleo.Stato;

/****************************************************************************
 * Un semplice JTextArea che alla pressione di CTRL+u apre un dialog box
 * @author Edoardo Panfili, studio Aspix
 ****************************************************************************/
public class AreaTestoUnicode extends JTextArea implements InterfacciaTestoUnicode{

    private static final long serialVersionUID = 1L;

    public AreaTestoUnicode() {
        super();
        this.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(FocusEvent e) {
                ctu_focusGained();
            }
        });
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(KeyEvent.getKeyText(e.getKeyCode()).equals("U") && e.getModifiers()==InputEvent.CTRL_MASK){
                    insersciCarattere();
                    e.consume();
                }
            }
        });
    }
    
    public void ctu_focusGained(){
        Stato.debugLog.fine("acquisito focus");
        Stato.ultimoUtilizzato=this;
    }
    
    /***************************************************************************
     * visualizza il dialogo per la scelta di un carattere
     * e lo inserisce nel testo
     **************************************************************************/
    public void insersciCarattere(){
        JTextArea sorgente=this;
        Color sfondo=this.getBackground();
        this.setBackground(CostantiGUI.coloreAttenzione);
        //SelettoreSimboli ss=new SelettoreSimboli();
        SelettoreCarattereUnicode ss = new SelettoreCarattereUnicode();
        ss.setLocationRelativeTo(sorgente);
        ss.setVisible(true);
        if(ss.lettera!=SelettoreCarattereUnicode.NIENTE){
            int posizioneCursore=sorgente.getCaretPosition();
            StringBuffer testo=new StringBuffer(sorgente.getText());
            testo.insert(posizioneCursore,ss.lettera);
            sorgente.setText(testo.toString());
            sorgente.setCaretPosition(posizioneCursore+1);
        }
        this.setBackground(sfondo);
        this.grabFocus();
    }

}