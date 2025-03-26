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

/****************************************************************************
 * Mappa le corrispondenze dei nomi di specie
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SpecieDaConvertire implements Comparable<SpecieDaConvertire>{
	String numero;
	String nomeTurboveg;
	String nomeAnArchive;
	public SpecieDaConvertire(String numero, String nomeTurboveg) {
		super();
		this.numero = numero;
		this.nomeTurboveg = nomeTurboveg;
		try{
			char v[] = this.nomeTurboveg.toCharArray();
			if(v[0]==Character.toUpperCase(0) && v[1]==Character.toUpperCase(1)){
				
			}
			for(int i=1; i<v.length ; i++){
				v[i] = Character.toLowerCase(v[i]);
			}
			// in ogni caso la prima lettera è maiuscola
			v[0] = Character.toUpperCase(v[0]);
			this.nomeTurboveg = new String(v);
		}catch(Exception e){
			; //do nothing
		}
	}
	public int compareTo(SpecieDaConvertire o) {
		return nomeTurboveg.compareTo(o.nomeTurboveg);
	}
}
