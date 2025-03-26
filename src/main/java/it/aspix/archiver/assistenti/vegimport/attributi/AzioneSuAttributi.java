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
package it.aspix.archiver.assistenti.vegimport.attributi;

public class AzioneSuAttributi {

	protected String esecutore;
	protected String param1;
	protected String param2;
	protected String param3;
	protected String param4;
	
	private static String SEPARATORE = "###";

	public AzioneSuAttributi(String esecutore, String param1, String param2, String param3, String param4) {
		super();
		this.esecutore = esecutore;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
		this.param4 = param4;
	}
	
	public AzioneSuAttributi(String x){
		parseString(x);
	}

	public String toString(){
		StringBuilder sb = new StringBuilder(esecutore);
		sb.append(SEPARATORE);
		sb.append(param1);
		sb.append(SEPARATORE);
		sb.append(param2==null?"":param2);
		sb.append(SEPARATORE);
		sb.append(param3==null?"":param3);
		sb.append(SEPARATORE);
		sb.append(param4==null?"":param4);
		return sb.toString();
	}
	
	public void parseString(String x){
		String[] parti = x.split(SEPARATORE);
		esecutore = parti[0];
		param1 = parti[1];
		param2 = parti[2];
		if(parti.length>=4){
			param3 = parti[3];
			if(parti.length>=5){
				param4 = parti[4];
			}
		}
	}
}
