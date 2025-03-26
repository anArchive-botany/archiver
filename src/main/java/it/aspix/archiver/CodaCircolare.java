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
package it.aspix.archiver;

import it.aspix.archiver.nucleo.Stato;

/****************************************************************************
 * Una classe generica per implementare una coda circolare.
 * @param <T>
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class CodaCircolare<T> {

    private Object buffer[];
    private int elementoBase;
    private int elementiPresenti;
    
    /************************************************************************
     * @param n numero di elementi della coda
     ***********************************************************************/
    public CodaCircolare(int n){
        buffer = new Object[n];
        elementoBase = 0;
        elementiPresenti = 0;
    }

    /************************************************************************
     * @param elemento da aggiungere in fondo alla lista
     ***********************************************************************/
    public void add(T elemento){
        int posizione;
        
        if(elementiPresenti == buffer.length){
            elementoBase = (elementoBase+1) % buffer.length;
        }else{
            elementiPresenti++;
        }
        posizione = (elementoBase+elementiPresenti-1) % buffer.length;
        Stato.debugLog.fine("aggiungo alla posizione:"+(posizione));
        buffer[posizione] = elemento;
    }
    
    /************************************************************************
     * @param n posizione a partire dalla base
     * @return l'elemento contenuto o null
     ***********************************************************************/
    @SuppressWarnings("unchecked")
    public T getFromBase(int n){
        if(n>=elementiPresenti)
            return null;
        return (T) (buffer[(elementoBase + n) % 10]);
    }
    
    /************************************************************************
     * @param n posizione a partire dalla base
     * @return l'elemento contenuto o null
     ***********************************************************************/
    @SuppressWarnings("unchecked")
    public T getFromHead(int n){
        if(n>=elementiPresenti)
            return null;
        return (T) (buffer[(elementoBase + elementiPresenti - 1 - n) % 10]);
    }
    
    /************************************************************************
     * @return tutti gli elementi contenuti nella lista
     ***********************************************************************/
    @SuppressWarnings("unchecked")
    public T[] getElements(){
        Object risposta[] = new Object[elementiPresenti];
        for(int i=0 ; i<elementiPresenti ; i++){
            risposta[i] = buffer[(elementoBase + i) % 10];
        }
        return (T[]) risposta;
    }
    
    /************************************************************************
     * @return il numero di elementi presenti nella coda
     ***********************************************************************/
    public int size(){
        return elementiPresenti;
    }
}
