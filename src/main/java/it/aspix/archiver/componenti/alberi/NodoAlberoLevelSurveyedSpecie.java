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
package it.aspix.archiver.componenti.alberi;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.SurveyedSpecie;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class NodoAlberoLevelSurveyedSpecie implements TreeNode,Comparable<NodoAlberoLevelSurveyedSpecie>{
    
    public Level strato;
    public SurveyedSpecie specie;
    public Vector<NodoAlberoLevelSurveyedSpecie> figli;
    private NodoAlberoLevelSurveyedSpecie padre;
    
    /************************************************************************
     * @param l il livello rappresentato in questo nodo
     ***********************************************************************/
    public NodoAlberoLevelSurveyedSpecie(Level l){
        strato = l;
        specie = null;
        figli = new Vector<NodoAlberoLevelSurveyedSpecie>();
        padre = null;
    }
    
    /************************************************************************
     * @param s la specie rilevata rappresentata da questo nodo
     ***********************************************************************/
    public NodoAlberoLevelSurveyedSpecie(SurveyedSpecie s){
        strato = null;
        specie = s;
        figli = null;
        padre = null;
    }
    
    /************************************************************************
     * Costruttore per clonazione
     * @param source il nodo da clonare
     ***********************************************************************/
    public NodoAlberoLevelSurveyedSpecie(NodoAlberoLevelSurveyedSpecie source){
        if(source.strato!=null){
            this.strato = source.strato.clone();
        }
        if(source.specie!=null){
            this.specie = new SurveyedSpecie(source.specie);
        }
        if(source.figli!=null){
            this.figli = new Vector<NodoAlberoLevelSurveyedSpecie>();
            for(int i=0;i<source.getChildCount();i++){
                this.figli.add(new NodoAlberoLevelSurveyedSpecie(source.getFiglio(i)));
            }
        }
        padre = source.padre;
    }
    
    /************************************************************************
     * @return true se il nodo rappresenta uno strato
     ***********************************************************************/
    public boolean isStrato(){
        return strato!=null;
    }
    
    /************************************************************************
     * return: "id: nome" per gli strati o "nome [quantita]" per le specie
     ************************************************************************/
    public String toString(){
        if(isStrato())
            return "Lev:"+strato.getId();
        return "Spe:"+specie.getSpecieRefName();
    }
    
    /************************************************************************
     * @param n il numero di sequenza del figlio da recuperare
     * @return n'ennesimo figlio 
     ***********************************************************************/
    public NodoAlberoLevelSurveyedSpecie getFiglio(int n){
        return figli.get(n);
    }
    
    /************************************************************************
     * Contestualmente all'inserzione imposta il padre del nodo figlio
     * @param nodo il figlio da aggiungere
     ***********************************************************************/
    public void addFiglio(NodoAlberoLevelSurveyedSpecie nodo){
        nodo.padre = this;
        figli.add(nodo);
        Stato.debugLog.fine(this.toString()+" aggiunto figlio '"+nodo+"'");
    }
    
    /************************************************************************
     * @param nodo il figlio da aggiungere
     ***********************************************************************/
    public void removeFiglio(NodoAlberoLevelSurveyedSpecie nodo){
        figli.remove(nodo);
        Stato.debugLog.fine(this.toString()+" rimosso figlio '"+nodo+"'");
    }
    
    /************************************************************************
     * @return il numero di figli di questo nodo
     ***********************************************************************/
    public int getChildCount(){
        if(figli==null)
            return 0;
        return figli.size();
    }
    
    /************************************************************************
     * @see javax.swing.tree.TreeNode#getChildAt(int)
     ***********************************************************************/
    public TreeNode getChildAt(int i) {
        return figli.get(i);
    }
    
    /************************************************************************
     * @see javax.swing.tree.TreeNode#children()
     ***********************************************************************/
    public Enumeration<NodoAlberoLevelSurveyedSpecie> children() {
        Enumeration<NodoAlberoLevelSurveyedSpecie> risposta;
        if(figli==null){
            risposta = null;
        }else{
            risposta = figli.elements();
        }
        return risposta;
    }
    
    /************************************************************************
     * @return true se il nodo contiene delle specie, false in tutti gli
     *              altri casi
     ***********************************************************************/
    public boolean contieneSpecie(){
        if(figli==null || figli.size()==0)
            return false;
        for(int i=0;i<figli.size();i++){
            if( ! figli.get(i).isStrato() )
                return true;
        }
        return false;
    }
    
    /************************************************************************
     * @return true se il nodo contiene degli strati, false in tutti gli
     *              altri casi
     ***********************************************************************/
    public boolean contieneStrati(){
        if(figli==null || figli.size()==0)
            return false;
        for(int i=0;i<figli.size();i++){
            if( figli.get(i).isStrato() )
                return true;
        }
        return false;
    }
    
    /************************************************************************
     * @return true se il nodo e' una foglia
     ***********************************************************************/
    public boolean isLeaf(){
        return specie!=null || (figli.size()==0);
    }
    
    /************************************************************************
     * il confronto segue due strade: 
     *  - se l'oggetto e'un NodoAlberoSpecie ed entambe i nodi sono interni
     *    allora sono uguali se hanno lo stesso id
     *  - negli altri casi devono essere lo stesso oggetto 
     * @param o il nodo da confrontare
     * @return true se i noodi sono uguali
     ***********************************************************************/
    public boolean isEqual(Object o){
        if(o instanceof NodoAlberoLevelSurveyedSpecie && isStrato() && ((NodoAlberoLevelSurveyedSpecie)o).isStrato()){
            return strato.getId().equals( ((NodoAlberoLevelSurveyedSpecie)o).strato.getId() );
        }
        return this==o;
            
    }

    /************************************************************************
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     ***********************************************************************/
    public int compareTo(NodoAlberoLevelSurveyedSpecie altro) {
        if(this.isStrato() && !altro.isStrato()){
            // gli strati vengono prima delle specie
            return -1;
        }
        if(!this.isStrato() && altro.isStrato()){
            // le specie vengono dopo gli strati
            return 1;
        }
        // se sono entrambi strati si ordinano in base all'ID, altrimenti si ordinano le specie
        if(this.isStrato())
            return strato.getId().compareTo(altro.strato.getId());
        return specie.compareTo(altro.specie);
    }
    
    /************************************************************************
     * ordina ricorsivamente il sottoalbero di questo nodo
     ***********************************************************************/
    public void sort(){
        if(figli==null)
            return;
        for(int i=0;i<figli.size();i++)
            getFiglio(i).sort();
        Collections.sort(figli);
    }

    /************************************************************************
     * @see javax.swing.tree.TreeNode#getAllowsChildren()
     ***********************************************************************/
    public boolean getAllowsChildren() {
        return strato!=null;
    }

    /************************************************************************
     * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
     ***********************************************************************/
    public int getIndex(TreeNode arg) {
        if(isStrato())
            return -1;
        for(int i=0 ; i<figli.size() ; i++)
            if(arg==figli.elementAt(i))
                return i;
        return -1;
    }

    /************************************************************************
     * @see javax.swing.tree.TreeNode#getParent()
     ***********************************************************************/
    public TreeNode getParent() {
        return padre;
    }
    
}