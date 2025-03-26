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
package it.aspix.archiver.componenti.liste;

import it.aspix.archiver.Utilita;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class ModelloListaSurveyedSpecie implements ListModel{
	
	
	private ArrayList<SurveyedSpecie> elementi;
	
	public ModelloListaSurveyedSpecie(){
		elementi = new ArrayList<SurveyedSpecie>();
	}
	
	public void removeSurveyedSpecie(SurveyedSpecie s){
		for(SurveyedSpecie ss: elementi){
			if(Utilita.ugualeIgnoraVuote(ss.getSpecieRefName(), s.getSpecieRefName()) &&
					Utilita.ugualeIgnoraVuote(ss.getSpecieRefAliasOf(), s.getSpecieRefAliasOf()) &&
					Utilita.ugualeIgnoraVuote(ss.getDetermination(), s.getDetermination()) ){
				elementi.remove(ss);
			}
		}
	}

	public void addListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
	}

	public SurveyedSpecie getElementAt(int index) {
		return elementi.get(index);
	}
	
	public void addSurveyedSpecie(SurveyedSpecie ss){
		elementi.add(ss);
	}
	
	public void removeElement(int n){
		elementi.remove(n);
	}
	
	public void removeAllElements(){
		while(elementi.size()>0)
			elementi.remove(0);
	}

	public int getSize() {
		return elementi.size();
	}
	
	public void sort(){
		Collections.sort(elementi);
	}

	public void removeListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
		
	}

}
