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
package it.aspix.archiver.componenti.insiemi;

import it.aspix.sbd.obj.SurveyedSpecie;

import java.util.HashMap;

/****************************************************************************
 * Un insieme di specie rilevate
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SurveyedSpecieSet{

	private HashMap<String,SurveyedSpecie> insieme;

	/************************************************************************
	 * Costruisce un nuovo insieme vuoto
	 ***********************************************************************/
	public SurveyedSpecieSet(){
		insieme = new HashMap<String,SurveyedSpecie>();
	}

	/************************************************************************
	 * Costruisce un insieme con gli stessi elementi di orig
	 * @param orig insieme da cui prendere gli elementi
	 ***********************************************************************/
	public SurveyedSpecieSet(SurveyedSpecieSet orig){
		insieme = new HashMap<String,SurveyedSpecie>();
		for( SurveyedSpecie ss : orig.insieme.values()){
			add(ss);
		}
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		for( SurveyedSpecie ss : insieme.values()){
			sb.append(ss.toString());
			sb.append(' ');
		}
		return sb.toString();
	}

	private String chiave(SurveyedSpecie ss){
		return ss.getSpecieRefName()+ss.getSpecieRefAliasOf()+ss.getDetermination();
	}

	/************************************************************************
	 * @param ss la specie da aggiungere all'insieme
	 ***********************************************************************/
	public void add(SurveyedSpecie ss){
		String chiave = chiave(ss);
		if(!insieme.containsKey(chiave)){
			insieme.put(chiave, ss);
		}
	}

	/************************************************************************
	 * @param sss l'insieme di specie da aggiungere all'insieme
	 ***********************************************************************/
	public void add(SurveyedSpecieSet sss){
		for( SurveyedSpecie ss : sss.insieme.values()){
			add(ss);
		}
	}

	/************************************************************************
	 * @param ss l'elemento da rimuovere da questo insieme
	 ***********************************************************************/
	public void remove(SurveyedSpecie ss){
		String chiave = chiave(ss);
		insieme.remove(chiave);
	}

	/************************************************************************
	 * @return l'eleneco degli elementi di questo insieme
	 ***********************************************************************/
	public SurveyedSpecie[] getSurveyedSpecies(){
		SurveyedSpecie[] risultato = new SurveyedSpecie[insieme.size()];
		int indice = 0;
		for( SurveyedSpecie ss : insieme.values()){
			risultato[indice++] = ss;
		}
		return risultato;
	}

	/************************************************************************
	 * @return il numero di elementi di questo insieme
	 ***********************************************************************/
	public int size(){
		return insieme.size();
	}

	/************************************************************************
	 * @param s la specie da controllare
	 * @return true se la specie è presente
	 ***********************************************************************/
	public boolean contains(SurveyedSpecie s){
		String chiave = chiave(s);
		return insieme.containsKey(chiave);
	}

	/************************************************************************
	 * @param altro insieme da utilizzare nel test
	 * @return true se questo insieme contiene l'altro
	 ***********************************************************************/
	public boolean contiene(SurveyedSpecieSet altro){
		// elimino tutte le specie presenti in "altro" dall'array
		for( SurveyedSpecie ss : altro.insieme.values()){
			if(!contains(ss)){
				return false;
			}
		}
		return true;
	}

	/************************************************************************
	 * @param altro insieme da utilizzare nel test
	 * @return true se questo insieme contiene l'altro
	 ***********************************************************************/
	public SurveyedSpecieSet sottrai(SurveyedSpecieSet altro){
		// containsAll() non sembra funzionare serve un metodo custom
		SurveyedSpecieSet sottrazione = new SurveyedSpecieSet(this);
		// elimino tutte le specie presenti in "altro" dall'array
		for( SurveyedSpecie ss : altro.insieme.values()){
			sottrazione.remove(ss);
		}
		return sottrazione;
	}

}
