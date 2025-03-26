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
package it.aspix.archiver.nucleo;

import java.util.Enumeration;

/****************************************************************************
 * Tabella tipo cache, questa versione non fa rimpiazzo dei dati
 * TODO: se si può sostituire con quella di HttpClient è meglio
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class Cache {
	
	public enum ContenutoCache {
		SPECIE 						('s'),
		SPECIE_INFO 				('i'),
		COMUNE 						('c'),
		LOCALITA 					('l'),
		ACCESSO_CONTENITORE 		('o'),
		INFORMAZIONI_CONTENITORE 	('f'),
		LIBRERIA 					('b'),
		NOME 						('n'),
		LISTA_ATTRIBUTI 			('A');
		
		private final char prefisso;
	    ContenutoCache(char prefisso) {
	        this.prefisso = prefisso;
	    }
	    
	    public char prefisso(){
	    	return this.prefisso;
	    }
	}

    private static final String valoreNullo = "***NULL***";
    private java.util.Hashtable<String,Object> tabellaDati;

    public Cache() {
        tabellaDati = new java.util.Hashtable<String,Object>();
    }

    /************************************************************************
     * la proprietà per leggere la grandezza della cache
     * 
     * @return l'ampiezza della cache
     ***********************************************************************/
    public int getAmpiezzaCache() {
        return 0;
    }

    /************************************************************************
     * il metodo per inserire un elemento in cache
     * @param type un char che identifica il tipo del dato, fa la funzione dei namespace
     * @param name è il nome dell'oggetto da inserire
     * @param dato l'oggetto da inserire
     ***********************************************************************/
    public void putElement(ContenutoCache type, String name, Object dato) {
        if (dato == null)
            dato = valoreNullo;
        tabellaDati.put(type.prefisso() + name, dato);
    }

    /************************************************************************
     * il metodo per recuperare un elemento dalla cache
     * @param type un char che identifica il tipo del dato, fa la funzione dei namespace
     * @param name � il nome dell'oggetto da inserire
     * @return l'oggetto recuperato
     ***********************************************************************/
    public Object retrieveElement(ContenutoCache type, String name) {
        Object recuperato=tabellaDati.get(type.prefisso + name);
        if(recuperato instanceof String && recuperato.equals(valoreNullo))
            return null;
        Stato.debugLog.fine("recuperato elemento "+name);
        return recuperato;
    }
    
    /************************************************************************
     * il metodo per rimuovere un elemento dalla cache
     * @param type un char che identifica il tipo del dato, fa la funzione dei namespace
     * @param name è il nome dell'oggetto da rimuovere
     ***********************************************************************/
    public void removeElement(ContenutoCache type, String name) {
        Stato.debugLog.fine("rimuovo l'elemento "+type.prefisso + name);
        tabellaDati.remove(type.prefisso + name);
    }
    
    /************************************************************************
     * controlla la presenza di un oggetto in cache
     * @param type un char che identifica il tipo del dato, fa la funzione dei namespace
     * @param name è il nome dell'oggetto da inserire
     * @return true se l'oggetto è presente in cache
     ***********************************************************************/
    public boolean presenteInCache(ContenutoCache type, String name) {
        return ( tabellaDati.get(type.prefisso() + name) ) != null ;
    }
    
    /************************************************************************
     * Elimina dalla cache tutti gli elementi di un certo tipo
     * @param tipo degli elementi da cancellare
     ***********************************************************************/
    public void rimuoviTutti(ContenutoCache tipo){
    	char daCercare = tipo.prefisso();
    	Enumeration<String> chiavi = tabellaDati.keys();
    	String chiave;
    	
    	Stato.debugLog.fine("cerco gli elementi di tipo '"+tipo.prefisso()+"'");
    	while(chiavi.hasMoreElements()){
    		chiave = chiavi.nextElement();
    		if( chiave.charAt(0)==daCercare ){
    			tabellaDati.remove(chiave);
    			Stato.debugLog.fine("elimino "+chiave);
    		}
    	}
    }

}