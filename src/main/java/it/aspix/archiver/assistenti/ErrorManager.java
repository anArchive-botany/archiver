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

import java.util.ArrayList;

/****************************************************************************
 * Gestisce situazioni in cui viene rilevato più volte lo stesso errore
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class ErrorManager {
	
	/************************************************************************
	 * Rappresenta un singolo errore e il numero di volte che si è presentato
	 * @author Edoardo Panfili, studio Aspix
	 ***********************************************************************/
	public static class Errore{
		public String nome;
		public int contatore;
		public Errore(String nome) {
			super();
			this.nome = nome;
		}
		public boolean equals(Errore arg) {
			return this.nome.equals(arg.nome);
		}
		public String toString(){
			if(contatore<2){
				return nome;
			}else{
				return nome+" ripetuto "+contatore+" volte";
			}
		}
	}
	
	private ArrayList<Errore> errori;
	public ErrorManager(){
		errori = new ArrayList<ErrorManager.Errore>();
	}
	
	/************************************************************************
	 * @return gli errori registrati da questo oggetto
	 ***********************************************************************/
	public ArrayList<Errore> getErrori(){
		return errori;
	}
	
	/************************************************************************
	 * L'errore viene aggiunto se non ce ne è un'altro uguale, altrimenti viene 
	 * incrementato il contatore 
	 * @param e il nuovo errore da aggiungere
	 ***********************************************************************/
	public void addErrore(Errore e){
		Errore trovato = null;
		for(Errore x: errori){
			if(x.equals(e)){
				trovato = x;
				break;
			}
		}
		if(trovato==null){
			trovato = new Errore(e.nome);
			trovato.contatore = 0;
			errori.add(trovato);
		}
		trovato.contatore++;
	}
}
