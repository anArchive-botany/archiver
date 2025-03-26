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
package it.aspix.archiver.componenti.tabelle;

import java.util.ArrayList;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import it.aspix.archiver.nucleo.Stato;

public class ModelloTabella<T extends OggettoPerTabella > implements TableModel {
    ArrayList<T> dati;

    //usate per l'ordinamento
    private int colonnaOrdinata=-1;
    private int verso=-1;
    private T modello;

    /** il costruttore alloca semplicemente gli oggetti */
    public ModelloTabella(T modello){
        this.modello = modello;
        dati = new ArrayList<T>();
    }

    /** accoda un elemento alla tabella */
    public void aggiungi(T dato){
        dati.add(dato);
    }

    /** rimuove tutti gli elementi dalla tabella */
    public void rimuoviTutto(){
        dati.clear();
    }

    public void ordina(int colonna){
        Stato.debugLog.fine("Ordinamento nel modello");
        //test sugli ordinamenti
        if(colonna==colonnaOrdinata){
            verso = (-1)*verso;
        }
        colonnaOrdinata = colonna;
        if(dati!=null && dati.size()>0){
            // chiamo il primo e imposto l'ordinamento per tutti
            dati.get(0).setOrdinamento(colonna,verso);
        }
        java.util.Collections.sort(dati);
    }

    /** Adds a listener to the list that is notified each time a change to the data model occurs. */
    public void addTableModelListener(TableModelListener l){
        ; //System.out.println("Richiesta 'addTableModelListener'... IGNORATA");
    }

    /** Returns the most specific superclass for all the cell values in the column. **/
    public Class<?> getColumnClass(int columnIndex){
        T oggr=dati.get(0);
        Object o = oggr.getColumn(columnIndex);
        if(o==null)
            return Object.class;
        return o.getClass();
    }

    /** Returns the number of columns in the model. */
    public int getColumnCount(){
        return SpecimenTabella.NUMERO_COLONNE;
    }

    /** Returns the name of the column at columnIndex. */
    public String getColumnName(int columnIndex){
        return modello.getColumnHeader(columnIndex);
    }

    /** Returns the number of rows in the model. */
    public T getOggettoAt(int n){
        return dati.get(n);
    }

    /** Returns the number of rows in the model. */
    public int getRowCount(){
        return dati.size();
    }

    /** Returns the value for the cell at columnIndex and rowIndex. */
    public Object getValueAt(int rowIndex, int columnIndex){
        T oggr = dati.get(rowIndex);
        return oggr.getColumn(columnIndex);
    }

    /** Returns true if the cell at rowIndex and columnIndex is editable. */
    public boolean isCellEditable(int rowIndex, int columnIndex){
        return editable && columnIndex>1;
    }
    
    private boolean editable=false;
    public boolean isEditable() {
        return editable;
    }
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /** Removes a listener from the list that is notified each time a change to the data model occurs. */
    public void removeTableModelListener(TableModelListener l){
        ; //System.out.println("Richiesta 'removeTableModelListener'... IGNORATA");
    }

    /** Sets the value in the cell at columnIndex and rowIndex to aValue. */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
        T oggr = dati.get(rowIndex);
        oggr.setColumn(columnIndex,aValue);
        oggr.setModificato(true);
    }
}
