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
import java.util.EventObject;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.dialoghi.SelettoreCarattereUnicode;
import it.aspix.archiver.nucleo.Stato;

/****************************************************************************
 * Un semplice JTextField che alla pressione di CTRL+u apre un dialog box
 * @author Edoardo Panfili, studio Aspix
 ****************************************************************************/
public class CampoTestoUnicode extends JTextField implements InterfacciaTestoUnicode, javax.swing.table.TableCellEditor{

    private static final long serialVersionUID = 1L;

    public CampoTestoUnicode() {
        super();
        this.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(FocusEvent e) {
                ctu_focusGained();
            }
        });
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(KeyEvent.getKeyText(e.getKeyCode()).equals("U") && e.getModifiersEx()==InputEvent.CTRL_DOWN_MASK){
                    insersciCarattere();
                    e.consume();
                }
            }
        });
        ascoltatori = new Vector<CellEditorListener>();
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
        JTextField sorgente=this;
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

    int row;
    int column;
    // =========================================================================
    // interfaccia javax.swing.table.TableCellEditor
    public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int riga, int colonna){
        this.setText((String)table.getModel().getValueAt(riga,colonna));
        this.row=riga;
        this.column=colonna;
        return this;
    }

    // =========================================================================
    // interfaccia javax.swing.table.TableCellEditor

    private Vector<CellEditorListener> ascoltatori;

    // Adds a listener to the list that's notified when the editor stops, or cancels editing.
    public void addCellEditorListener(CellEditorListener l){
        ascoltatori.add(l);
        Stato.debugLog.finest("");
    }

    // Removes a listener from the list that's notified
    public void removeCellEditorListener(CellEditorListener l){
        CellEditorListener c;
        for(int j=0;j<ascoltatori.size();j++){
            c = ascoltatori.elementAt(j);
            if(c==l)
                ascoltatori.remove(j);
        }
        Stato.debugLog.finest("");
    }

    // Tells the editor to cancel editing and not accept any partially edited value.
    public void cancelCellEditing(){
        Stato.debugLog.finest("");
    }

    // Returns the value contained in the editor.
    public Object getCellEditorValue(){
        Stato.debugLog.finest("");
        return this.getText();
    }

    // Asks the editor if it can start editing using anEvent.
    public boolean isCellEditable(EventObject anEvent){
        if(anEvent!=null)
            Stato.debugLog.finer("evento:"+anEvent.getClass().getName());
        else
            Stato.debugLog.finer("evento NULLO");
        this.requestFocus();
        return true;
    }

    // Returns true if the editing cell should be selected, false otherwise.
    public boolean shouldSelectCell(EventObject anEvent){
        Stato.debugLog.finer("evento:"+anEvent.getClass().getName());
        return true;
    }

    // Tells the editor to stop editing and accept any partially edited value as the value of the editor.
    public boolean stopCellEditing(){
        Stato.debugLog.finest("");
        CellEditorListener c;
        for(int j=0;j<ascoltatori.size();j++){
            c = ascoltatori.elementAt(j);
            c.editingStopped(new javax.swing.event.ChangeEvent(this));
        }
        return true;
    }
}