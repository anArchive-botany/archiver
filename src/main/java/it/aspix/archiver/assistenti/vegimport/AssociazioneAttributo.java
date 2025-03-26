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

import it.aspix.archiver.assistenti.GestoreAttributoAnArchive;

/****************************************************************************
 * Una coppia (nome attributo turboveg, nome attributo anArchive) 
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class AssociazioneAttributo implements Comparable<AssociazioneAttributo>{
	public String turboveg;
	public String anArchive;
	
	public AssociazioneAttributo(String turboveg) {
		super();
		this.turboveg = turboveg;
		// cerco nelle corrispondenze di default
		if(GestoreAttributoAnArchive.corrispondenzeTurbovegAnArchiveDefault.containsKey(turboveg)){
			this.anArchive = GestoreAttributoAnArchive.corrispondenzeTurbovegAnArchiveDefault.get(turboveg);
		}
		if(VegImport.associazioniSalvateSuFile.containsKey(turboveg)){
			this.anArchive = (String) VegImport.associazioniSalvateSuFile.get(turboveg);
		}else{
			this.anArchive = "";
		}
	}

	public int compareTo(AssociazioneAttributo arg2) {
		if(this.turboveg.length()>0 && arg2.turboveg.length()<=0)
			return -1;
		if(arg2.turboveg.length()>0 && this.turboveg.length()<=0)
			return 1;
		return this.turboveg.compareTo(arg2.turboveg);	
	}
	
	public String toString(){
		return  turboveg + "⇒" + (anArchive.length()>0 ? anArchive : GestoreAttributoAnArchive.NON_UTILIZZARE);
	}
}
