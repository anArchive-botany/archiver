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

import java.awt.Component;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import it.aspix.archiver.componenti.ComboBoxModelEDFactory.TipoCombo;
import it.aspix.archiver.componenti.tabelle.PseudoTipo;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.archiver.nucleo.Stato;

/****************************************************************************
 * Un JComboBox con un contenuto prestabilito
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class ComboGenerico extends JComboBox implements TableCellEditor{

    private static final long serialVersionUID = 1L;
    
    private DefaultComboBoxModel modello;
    private String descrizione;
    
    /************************************************************************
     * La descrizione della casella viene desrunuta dal tipo
     * @param contenuto
     * @param includiNonSo
     ***********************************************************************/
    public ComboGenerico(TipoCombo contenuto, boolean includiNonSo){
        ascoltatori = new Vector<CellEditorListener>();
        modello = ComboBoxModelEDFactory.createComboBoxModelED(contenuto);
        modello = new DefaultComboBoxModel();
        descrizione = contenuto.descrizione();
        if(includiNonSo)
            modello.addElement("non importa");
        this.setModel(modello);
    }
    
    /************************************************************************
     * @param contenuto
     * @param descrizione esplicita del contenuto di questa casella
     * @param includiNonSo
     ***********************************************************************/
    public ComboGenerico(TipoCombo contenuto, String descrizione, boolean includiNonSo){
        ascoltatori = new Vector<CellEditorListener>();
        modello = ComboBoxModelEDFactory.createComboBoxModelED(contenuto);
        modello = new DefaultComboBoxModel();
        this.descrizione = descrizione;
        if(includiNonSo)
            modello.addElement("non importa");
        this.setModel(modello);
    }

    public void setText(String s) throws ValoreException {
        if(s!=null){
            boolean trovato=false;
            for(int i=0;i<modello.getSize();i++){
                if( ((String) modello.getElementAt(i) ).equals(s) ){
                    this.setSelectedIndex(i);
                    trovato=true;
                }
            }
            if(!trovato){
                throw new ValoreException("Valore non ammissibile per "+descrizione);
            }
        }
    }

    public String getText() {
        return ((String) this.getSelectedItem() );
    }

    public Component getTableCellEditorComponent(JTable table, Object arg1, boolean arg2, int row, int column) {
        try {
            this.setText( ((PseudoTipo) table.getModel().getValueAt(row,column)).valore );
        } catch (ValoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return this;
    }

    public Object getCellEditorValue() {
        return getText();
    }

    public boolean isCellEditable(EventObject arg0) {
        this.requestFocus();
        return true;
    }

    public boolean shouldSelectCell(EventObject arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    // Tells the editor to stop editing and accept any partially edited value as the value of the editor.
    public boolean stopCellEditing(){
        CellEditorListener c;
        for(int j=0;j<ascoltatori.size();j++){
            c = ascoltatori.elementAt(j);
            c.editingStopped(new javax.swing.event.ChangeEvent(this));
        }
        return true;
    }

    // Tells the editor to cancel editing and not accept any partially edited value.
    public void cancelCellEditing(){
        ;
    }

    private Vector<CellEditorListener> ascoltatori;

    // Adds a listener to the list that's notified when the editor stops, or cancels editing.
    public void addCellEditorListener(CellEditorListener l){
        ascoltatori.add(l);
        Stato.debugLog.finer("aggiunto ascoltatore");
    }

    // Removes a listener from the list that's notified
    public void removeCellEditorListener(CellEditorListener l){
        CellEditorListener c;
        for(int j=0;j<ascoltatori.size();j++){
            c = ascoltatori.elementAt(j);
            if(c==l)
                ascoltatori.remove(j);
        }
        Stato.debugLog.finer("rimosso ascoltatore");
    }
}