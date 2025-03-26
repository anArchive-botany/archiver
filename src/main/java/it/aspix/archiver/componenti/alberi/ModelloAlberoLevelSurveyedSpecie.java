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

import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.UtilitaVegetazione;
import it.aspix.archiver.eventi.SistemaException;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.util.ArrayList;
import java.util.Stack;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/****************************************************************************
 * Il modello per l'albero dei rilievi
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class ModelloAlberoLevelSurveyedSpecie implements TreeModel{

    NodoAlberoLevelSurveyedSpecie radice;
    
    private static final String idLivelloRadice=".";
    
    /************************************************************************
     * Costruisce un n uovo albero vuoto
     ***********************************************************************/
    public ModelloAlberoLevelSurveyedSpecie() {
        radice = new NodoAlberoLevelSurveyedSpecie(CostruttoreOggetti.createLevel(idLivelloRadice, "STRATI"));
    }
    
    /************************************************************************
     * Prende in considerazione solamente i livelli principali e i sottolivelli
     * una stringa la stringa dei livelli di esempio è: %1~alberi%2~arbusti%3~erbe
     * @return una stringa che rappresenta i livelli presenti
     ***********************************************************************/
    public String getLevelsSchema(){
        int i; // scorre gli strati principali
        int j; // scorre i sottolivelli
        Level l,subL;
        
        StringBuffer sb=new StringBuffer();
        for(i=0;i<radice.getChildCount();i++){
            // i filgi del nodo radice sono tutti livelli
            l = radice.getFiglio(i).strato;
            sb.append("%"+l.getId()+"~"+l.getName());
            for(j=0;j<radice.getFiglio(i).getChildCount();j++){
                if(radice.getFiglio(i).getFiglio(j).isStrato()){
                    subL=radice.getFiglio(i).getFiglio(j).strato;
                    sb.append("%"+subL.getId()+"~"+subL.getName());
                }
            }
        }
        return sb.toString();   
    }
    
    /************************************************************************
     * reimposta lo schema dei livelli: suppone lo strato singolo
     * @param schema la riga che rappresenta uno schema di livelli
     ***********************************************************************/
    public void setLevelsSchema(String schema){
        String parti[] = schema.substring(1).split("%");
        
        int posizioneTilde;
        String id;
        String nome;
        int posizionePunto;
        
        for(int i=0;i<parti.length;i++){
            posizioneTilde = parti[i].indexOf("~");
            id=parti[i].substring(0,posizioneTilde);
            nome=parti[i].substring(posizioneTilde+1);
            posizionePunto = id.lastIndexOf('.');
            if(posizionePunto==-1)
                this.addChild(radice, new NodoAlberoLevelSurveyedSpecie(CostruttoreOggetti.createLevel(id, nome)));
            else
                this.addChild(
                        _cercaStratoPerId(radice,id.substring(0,posizionePunto)),
                        new NodoAlberoLevelSurveyedSpecie(CostruttoreOggetti.createLevel(id, nome))
                );
        }
    }
    
    /************************************************************************
     * Prende in considerazione solamente i livelli principali e i sottolivelli
     * che contengono qualche specie.
     * Restituisce direttamente i dati presenti nell'albero, non crea copie
     * @return una array di Level
     ***********************************************************************/
    public Level[] getLevels(){
        int i; // scorre gli strati principali
        int j; // scorre i sottolivelli
        Level risultato[];
        int contatore=0;

        // primo giro per contare
        for(i=0;i<radice.getChildCount();i++){
            // quelli di primo livello sono per forza strati
            contatore++;
            for(j=0;j<radice.getFiglio(i).getChildCount();j++){
                if(radice.getFiglio(i).getFiglio(j).isStrato()){
                    contatore++;
                }
            }
        }
        risultato=new Level[contatore];
        contatore=0;
        // riempio il vettore
        for(i=0;i<radice.getChildCount();i++){
            risultato[contatore++] = sistemLivelloDaNodo(radice.getFiglio(i).strato.clone(), radice.getFiglio(i));
            for(j=0;j<radice.getFiglio(i).getChildCount();j++){
                if(radice.getFiglio(i).getFiglio(j).isStrato()){
                    // risultato[contatore++] = radice.getFiglio(i).getFiglio(j).strato.clone();
                    risultato[contatore++] = sistemLivelloDaNodo(radice.getFiglio(i).getFiglio(j).strato.clone(),radice.getFiglio(i).getFiglio(j));
                }
            }
        }
        return risultato;   
    }
    
    /************************************************************************
     * Recupera figlio i_esimo, per evitare problemi pulisce lo strato e 
     * recupera le specie dai nodi foglia
     ***********************************************************************/
    private Level sistemLivelloDaNodo(Level livello, NodoAlberoLevelSurveyedSpecie nodo){
        livello.removeAllSurveyedSpecie();
        for(NodoAlberoLevelSurveyedSpecie x: nodo.figli){
            if(!x.isStrato()){
                livello.addSurveyedSpecie(x.specie);
            }
        }
        return livello;
    }
    
    /************************************************************************
     * ordina l'albero
     ***********************************************************************/
    public void sort(){
        Stato.debugLog.fine("Orindamento albero specie");
        radice.sort();
    }
    
    /************************************************************************
     * @param id dello strato da cercare
     * @return il nodo che rappresenta lo strato o null
     ***********************************************************************/
    public NodoAlberoLevelSurveyedSpecie cercaStratoPerId(String id){
        return _cercaStratoPerId(radice, id);         
    }
    private NodoAlberoLevelSurveyedSpecie _cercaStratoPerId(NodoAlberoLevelSurveyedSpecie padre, String id){
        NodoAlberoLevelSurveyedSpecie nodo;
        NodoAlberoLevelSurveyedSpecie trovato = null;
        
        for(int i=0 ; i<padre.getChildCount() && trovato==null ; i++){
            nodo = padre.getFiglio(i);
            if(!nodo.isStrato())
                continue;
            if(nodo.strato.getId().equals(id))
                trovato = nodo;
            if(trovato == null){
                trovato = _cercaStratoPerId(nodo,id);
            }
        }
        return trovato;         
    }
    
    /************************************************************************
     * @param padre in cui cercare la specie
     * @param specie la specie da cercare in base a (nome,sinonimodi,determinazione)
     * @param ricorsivo true se deve cercare nei sottolivelli
     * @return il nodo che contine la specie o null
     ***********************************************************************/
    public NodoAlberoLevelSurveyedSpecie cercaSurveyedSpecie(NodoAlberoLevelSurveyedSpecie padre, SurveyedSpecie specie, boolean ricorsivo){
        NodoAlberoLevelSurveyedSpecie nodo;
        NodoAlberoLevelSurveyedSpecie trovato = null;
        
        for(int i=0 ; i<padre.getChildCount() && trovato==null ; i++){
            nodo = padre.getFiglio(i);
            if(!nodo.isStrato()){
                // è una specie, si fanno i controlli
                if( UtilitaVegetazione.stessoNomeSurveyedSpecie(specie, nodo.specie) ){
                	trovato = nodo;
                }
            }
            if(nodo.isStrato() && ricorsivo){
                trovato = cercaSurveyedSpecie(nodo, specie, ricorsivo);
            }
        }
        return trovato;    
    }
    
    /************************************************************************
     * @param padre il nodo a cui va aggiunto un figlio, null se va aggiunto
     *              alla radice dell'albero
     * @param figlio il figlio da aggiungere
     ***********************************************************************/
    public void addChild(NodoAlberoLevelSurveyedSpecie padre, NodoAlberoLevelSurveyedSpecie figlio){
        Stato.debugLog.fine("A:"+padre+" aggiungo "+figlio);
        if(padre==null)
            padre=radice;
        padre.addFiglio(figlio);
    }

    /************************************************************************
     * @param livello il livello in cui cercare la specie
     * @param specie la specie da cercare (interessano il nome e il comesinonimodi)
     * @return la specie presente nell'albero
     ***********************************************************************/
    public SurveyedSpecie getSpecie(Level livello, SurveyedSpecie specie) {
        Stato.debugLog.fine("recupero la specie:"+specie+" dallo strato "+livello);
        NodoAlberoLevelSurveyedSpecie nodoLivello = _cercaStratoPerId(radice,livello.getId());
        NodoAlberoLevelSurveyedSpecie nodoSpecie=null;
        
        if(nodoLivello!=null){
            nodoSpecie = cercaSurveyedSpecie(nodoLivello, specie, false);
        }
        if(nodoSpecie!=null){
            return nodoSpecie.specie;
        }
        return null;
    }

    /************************************************************************
     * @param livello in cui cercare la specie
     * @param specie a eliminare, ricerca in base a (nome,comesinonimodi,determinazione)
     * @throws SistemaException 
     ***********************************************************************/
    public void removeSpecie(Level livello, SurveyedSpecie specie) throws SistemaException{
        NodoAlberoLevelSurveyedSpecie nodoSpecie=null;
        
        Stato.debugLog.fine("rimuovo la specie:"+specie+" dallo strato "+livello);
        if(radice!=null && radice.getChildCount()>0){
            // rimuovo soltanto se ci sono strati
            if(livello!=null){
                NodoAlberoLevelSurveyedSpecie nodoLivello = _cercaStratoPerId(radice,livello.getId());
                
                if(nodoLivello!=null){
                    nodoSpecie = cercaSurveyedSpecie(nodoLivello, specie, false);
                    if(nodoSpecie!=null){
                        nodoLivello.removeFiglio(nodoSpecie);
                    }else{
                        throw new SistemaException("ERRORE SISTEMA IA4: non trovo la specie "+specie);
                    }
                }else{
                    throw new SistemaException("ERRORE SISTEMA IA3: non trovo il livello "+livello);
                }
            }else{
                // devo rimuovere la specie da tutto l'albero
                Stack<NodoAlberoLevelSurveyedSpecie> pila = new Stack<NodoAlberoLevelSurveyedSpecie>();
                NodoAlberoLevelSurveyedSpecie attuale;
                pila.push(radice);
                while(!pila.isEmpty()){
                    attuale = pila.pop();
                    // scansione per trovare gli eventuali sotto-strati
                    for(NodoAlberoLevelSurveyedSpecie nodo: attuale.figli){
                        if(nodo.isStrato()){
                            pila.push(nodo);
                        }
                    }
                    // elimino la specie se presente in questo strato
                    nodoSpecie = cercaSurveyedSpecie(attuale, specie, false);
                    if(nodoSpecie!=null){
                        attuale.removeFiglio(nodoSpecie);
                    }
                }
            }
        }
    }
    
    /************************************************************************
     * cancella tutte le specie ricorsivamente ma non i livelli
     * @param nodoPadre da cui iniziare a rimuovere le specie
     ***********************************************************************/
    public void removeAllSpecie(NodoAlberoLevelSurveyedSpecie nodoPadre){
        NodoAlberoLevelSurveyedSpecie nodo;
        
        for(int i=0 ; i<nodoPadre.getChildCount() ; i++){
            nodo = nodoPadre.getFiglio(i);
            if(nodo.isStrato()){
                removeAllSpecie(nodo);
            }else{
                nodoPadre.removeFiglio(nodo);
            }
        }
    }
    
    /************************************************************************
     * @param bersaglio il nodo a cui il cammino deve arrivare
     * @return il cammino per arrivare a bersaglio dalla radice (escuso bersaglio)
     ***********************************************************************/
    public TreePath buildPathfor(NodoAlberoLevelSurveyedSpecie bersaglio){
        Stato.debugLog.fine("cerco il cammino per:"+bersaglio);
        ArrayList<NodoAlberoLevelSurveyedSpecie> v = new ArrayList<NodoAlberoLevelSurveyedSpecie>();
        do{
            v.add(0,bersaglio);
            bersaglio = (NodoAlberoLevelSurveyedSpecie) bersaglio.getParent();
            Stato.debugLog.fine("aggiungo al cammino:"+bersaglio);
        }while(bersaglio!=null);
        Object[] cammino = new Object[v.size()];
        for(int i=0; i<v.size();i++){
            cammino[i]=v.get(i);
        }
        return new TreePath(cammino);
    }

    /************************************************************************
     * rimuove tutti i nodi dall'albero
     ***********************************************************************/   
    public void removeAll(){
        radice = new NodoAlberoLevelSurveyedSpecie(CostruttoreOggetti.createLevel(idLivelloRadice,"STRATI"));
    }
    
    /************************************************************************
     * @param nodo il nodo da rimuovere;
     ***********************************************************************/
    public void removeNode(NodoAlberoLevelSurveyedSpecie nodo){
        rimuoviNodo(nodo);
    }
    
    /************************************************************************
     * @param nodo      il nodo da rimuovere
     ***********************************************************************/
    private void rimuoviNodo(NodoAlberoLevelSurveyedSpecie nodo){
        NodoAlberoLevelSurveyedSpecie padre = (NodoAlberoLevelSurveyedSpecie) nodo.getParent();
        if(padre!=null){
            padre.removeFiglio(nodo);
        }
    }
    
    /************************************************************************
     * return la radice dell'albero
     ***********************************************************************/
    public Object getRoot() {
        return radice;
    }

    /************************************************************************
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     ***********************************************************************/
    public Object getChild(Object padre, int indice) {
        return ((NodoAlberoLevelSurveyedSpecie)padre).figli.get(indice);
    }

    /************************************************************************
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     ***********************************************************************/
    public int getChildCount(Object arg0) {
        return (((NodoAlberoLevelSurveyedSpecie)arg0).getChildCount());
    }

    /************************************************************************
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     ***********************************************************************/
    public boolean isLeaf(Object arg0) {
        return (((NodoAlberoLevelSurveyedSpecie)arg0).isLeaf());
    }

    /************************************************************************
     * Questo tipo di albero non e' editabile quindi non fa nulla
     ***********************************************************************/
    public void valueForPathChanged(TreePath arg0, Object arg1) { }

    /************************************************************************
     * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
     ***********************************************************************/
    public int getIndexOfChild(Object p, Object f) {
        NodoAlberoLevelSurveyedSpecie padre = (NodoAlberoLevelSurveyedSpecie)p;
        NodoAlberoLevelSurveyedSpecie figlio = (NodoAlberoLevelSurveyedSpecie)f; 
        
        return padre.figli.indexOf(figlio);
    }
    
    /************************************************************************
     * @return la stringa che rappresenta i dati di questo modello
     ***********************************************************************/
    public String toString(){
        return _toString(radice);
    }
    private String _toString(NodoAlberoLevelSurveyedSpecie nodo){
        StringBuffer sb = new StringBuffer();
        sb.append("\n"+nodo.strato);
        sb.append("(");
        for(int spe=0 ; spe<nodo.getChildCount() ; spe++){
            if(spe!=0)
                sb.append(", ");
            if(!nodo.getFiglio(spe).isStrato()){
                sb.append(_toString(nodo.getFiglio(spe)));
            }else{
                sb.append(nodo.getFiglio(spe).specie.getSpecieRefName().substring(0,3));
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    /************************************************************************
     * Questo tipo di albero non e' editabile quindi non fa nulla
     ***********************************************************************/
    public void addTreeModelListener(TreeModelListener arg0) { }

    /************************************************************************
     * Questo tipo di albero non e' editabile quindi non fa nulla
     ***********************************************************************/
    public void removeTreeModelListener(TreeModelListener arg0) { }

}