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
package it.aspix.archiver.assistenti;

import it.aspix.sbd.obj.Message;

import java.util.ArrayList;
import java.util.logging.Level;

/****************************************************************************
 * Oggetto con livello di errore, utilizzato per visualizzare informazioni
 * nelle JTable
 * Contiene anche un elenco di messaggi di errore
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class OggettoConLivello {
	String stringa;
	Level livello;
	Object oggetto;
	boolean editatoManualmente;
	ArrayList<Message> messaggi = new ArrayList<Message>(); 

	public OggettoConLivello(){
		editatoManualmente = false;
	}
	
	public OggettoConLivello(String s, Level l){
		stringa = s;
		livello = l;
		editatoManualmente = false;
	}

	public OggettoConLivello(Object o, Level l){
		stringa = o.toString();
		livello = l;
		oggetto = o;
		editatoManualmente = false;
	}
	
	public String getStringa(){
		return stringa;
	}

	public Level getLivello() {
		return livello;
	}
	
	public void setLivello(Level l) {
		livello = l;
	}

	public Object getOggetto() {
		return oggetto;
	}
	
	public void setOggetto(Object o) {
		oggetto = o;
	}
	
	public boolean isEditatoManualmente() {
		return editatoManualmente;
	}

	public void setEditatoManualmente(boolean editatoManualmente) {
		this.editatoManualmente = editatoManualmente;
	}
	
	public ArrayList<Message> getMessaggi() {
		return messaggi;
	}

	public void setMessaggi(ArrayList<Message> messaggi) {
		this.messaggi = messaggi;
	}

	public String toString(){
		if(stringa!=null){
			return stringa;
		}else{
			return oggetto.toString();
		}
	}
	
}
