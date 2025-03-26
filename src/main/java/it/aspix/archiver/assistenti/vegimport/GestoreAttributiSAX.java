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
package it.aspix.archiver.assistenti.vegimport;

import java.util.HashSet;

import org.xml.sax.Attributes;

/****************************************************************************
 * Recupera gli attributi da un elenco di attributi di XML 
 * tenendo conto di quelli presi
 * Su richiesta può elencare gli attributi non presi
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class GestoreAttributiSAX {
	private Attributes att;
	private HashSet<String> attributiPresi;

	/************************************************************************
	 * @param att  l'elenco da cui leggere gli attributi
	 * @param o gli attributi da ignorare
	 ***********************************************************************/
	public GestoreAttributiSAX(Attributes att, Object... o ) {
		super();
		this.att = att;
		attributiPresi = new HashSet<String>();
		if(o!=null){
			for(int i=0; i<o.length ; i++){
				attributiPresi.add((String) o[i]);
			}
		}
	}
	
	/************************************************************************
	 * @param name dell'attributo
	 * @return il valore dell'attributo
	 ***********************************************************************/
	public String get(String name){
		attributiPresi.add(name);
		return att.getValue(name);
	}
	
	/************************************************************************
	 * Gli attributi che non sono stati letti
	 * @return una stringa di nomi separati da spazio
	 ***********************************************************************/
	public String nonPresi(){
		StringBuilder sb = new StringBuilder();
		String nome;
		for(int i=0; i<att.getLength(); i++){
			nome = att.getQName(i);
			if(!attributiPresi.contains(nome)){
				sb.append(nome);
				sb.append(' ');
			}
		}
		return sb.toString();
	}
	
}
