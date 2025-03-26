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

public abstract class OggettoPerTabella implements Comparable<OggettoPerTabella> {
    
    /************************************************************************
     * @param n la colonna che interessa
     * @return il suo valore
     ***********************************************************************/
    public abstract Object getColumn(int n);
    /************************************************************************
     * @param n la colonna che interessa
     * @param o il suo valore
     ***********************************************************************/
    public abstract void setColumn(int n, Object o);
    /************************************************************************
     * @param n la colonna che interessa
     * @return il nome della colonna
     ***********************************************************************/
    public abstract String getColumnHeader(int n);
    
    private boolean modificato;
    /************************************************************************
     * @param b true se l'oggetto è stato modificato
     ***********************************************************************/
    public void setModificato(boolean modificato){
        this.modificato = modificato;
    }
    /************************************************************************
     * @return true se l'oggetto è stato modificato
     ***********************************************************************/
    public boolean isModificato(){
        return modificato;
    }
    /************************************************************************
     * @param colonna la colnna in baase alla quale ordinare
     * @param verso 1 (normale comparazione) -1 (inversa)
     ***********************************************************************/
    public abstract void setOrdinamento(int colonna, int verso);
        
}
