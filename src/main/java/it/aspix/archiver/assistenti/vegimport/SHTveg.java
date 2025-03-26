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

import it.aspix.archiver.assistenti.ErrorManager;
import it.aspix.archiver.assistenti.SampleWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/****************************************************************************
 * La classe callback per recuperare i dati di un cartellino
 * per la definizione del dialetto XML vedi sbd.xsd
 * @author edoardo
 ***************************************************************************/
public class SHTveg extends DefaultHandler{
	
	private StringBuffer stringa;			// ci inserisce i caratteri characters
	private String campoInEsame = null;
	private boolean TAG_Species_list = false;
	private boolean TAG_species = false;
	private boolean TAG_header_data = false;
	private String TAG_list_iniziato = null;
	private String TAG_record = null;
	private Hashtable<String, SpecieDaConvertire> specie;
	private boolean ignora = false;
	private ArrayList<SampleWrapper> rilievi;
	private SampleWrapper rilievoAttuale;
	private ErrorManager errorManager;
	private ArrayList<String> tagAttraversati;
	// i dizionari presenti nel file di turboveg
	// nomeLista => ( code => ( attributo => valore) )
	private HashMap<String, HashMap<String, HashMap<String, String>>> dizionari;
	private SampleWrapper modelloNuoviRilievi;
	
	/************************************************************************
	 * @param errorManager il gestore degli errori da utilizzare
	 * @param modello per creare nuovi rilievi
	 ***********************************************************************/
	public SHTveg(ErrorManager errorManager, SampleWrapper modello){
		rilievi = new ArrayList<SampleWrapper>();
		specie = new Hashtable<String, SpecieDaConvertire>();
		tagAttraversati = new ArrayList<String>();
		this.errorManager = errorManager;
		dizionari = new HashMap<String, HashMap<String,HashMap<String, String>>>();
		modelloNuoviRilievi = modello;
	}
	
	// SAX SAX SAX SAX SAX SAX SAX SAX SAX SAX SAX SAX SAX SAX SAX SAX SAX SAX
	
	public void startDocument() throws SAXException{
	}
	
	// Plot.@database -> sottoprogeto
	// Plot.@releve_nr -> progressivo sottoprogetto
	
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException{
		tagAttraversati.add(qName);
		if(ignora)
			return;
		if(qName.equals("vattelappesca")){
			// FAI QUALCOSA
			return;
		}else if(qName.equals("Plot")){
			try {
				rilievoAttuale = modelloNuoviRilievi.getCopia();
			} catch (Exception e) {
				e.printStackTrace();
				rilievoAttuale = new SampleWrapper();
			}
			// <Plot database="Cerrete taffetani" releve_nr="1" guid="{7BF47299-E059-407C-A9C6-7CC7BA2697FB}" create_user="laura" create_date="26/04/2012" modify_user="" modify_date="" ndff_qual="0">
			GestoreAttributiSAX gas = new GestoreAttributiSAX(atts, "guid","create_date", "modify_date", "create_user", "modify_user","ndff_qual");
			//rilievoAttuale.getDirectoryInfo().setSubContainerName(gas.get("database"));
			//rilievoAttuale.getDirectoryInfo().setSubContainerSeqNo(gas.get("releve_nr"));
			
			rilievoAttuale.proprieta.put("DATABASE", gas.get("database"));
			rilievoAttuale.proprieta.put("RELEVE_NR", gas.get("releve_nr"));
			
			if(gas.nonPresi().length()>0){
				errorManager.addErrore(new ErrorManager.Errore(
						"ATTENZIONE: nel tag \""+calcolaPercorso()+"\" non sono stati presi gli attibiuti \""+gas.nonPresi()+"\""));
			}
		}else if(qName.equals("header_data")){
			TAG_header_data = true;
		}else if(TAG_header_data && qName.equals("standard_record")){
			// <standard_record releve_nr="1" table_nr="1" nr_in_tab="1" coverscale="01" altitude="-" exposition="-" inclinatio="-" remarks="ril.1 di Tab.1 in Frattaroli et al. 2007"/>
			String nome;
			for(int i=0; i<atts.getLength(); i++){
				nome = atts.getQName(i);
				if(nome.equals("guid") || nome.equals("create_date") || nome.equals("create_user")
						|| nome.equals("modify_date") || nome.equals("modify_user")){
					continue;
				}
				rilievoAttuale.proprieta.put(nome, atts.getValue(nome).trim());
			}
		}else if(TAG_header_data && qName.equals("udf_record")){
			// <standard_record releve_nr="1" table_nr="1" nr_in_tab="1" coverscale="01" altitude="-" exposition="-" inclinatio="-" remarks="ril.1 di Tab.1 in Frattaroli et al. 2007"/>
			GestoreAttributiSAX gas = new GestoreAttributiSAX(atts, "guid","ispredefined","type","len","dec");
			String name = gas.get("name");
			String value = gas.get("value");
			rilievoAttuale.proprieta.put(name.trim(), value.trim());
			if(gas.nonPresi().length()>0){
				errorManager.addErrore(new ErrorManager.Errore(
						"ATTENZIONE: nel tag \""+calcolaPercorso()+"\" non sono stati presi gli attibiuti \""+gas.nonPresi()+"\""));
			}
		}else if(qName.equals("species_data")){
			; // do nothing: di questo interessano gli elementi figli
		}else if(qName.equals("species")){
			TAG_species = true;
		}else if(TAG_species && qName.equals("standard_record")){
			// <standard_record nr="270" cover="4" layer="0"/>
			GestoreAttributiSAX gas = new GestoreAttributiSAX(atts);
			String nr = gas.get("nr"); 
			String cover = gas.get("cover");
			String layer = gas.get("layer");
			SampleWrapper.SpecieData sd = new SampleWrapper.SpecieData(nr,cover,layer);
			rilievoAttuale.specieData.add(sd);
		}else if(qName.equals("Species_list")){
			TAG_Species_list = true;
		}else if(TAG_Species_list && qName.equals("species_record")){
			// <species_record nr="3047" code="ACERMON" name="Acer monspessulanum monspessulanum" author="Acer monspessulanum monspessulanum" valid_nr="" valid_name="" valid_author="" synonym="no" nativename=""/>
			GestoreAttributiSAX gas = new GestoreAttributiSAX(atts, "code","author","valid_nr","valid_name","valid_author","synonym","nativename");
			String nr = gas.get("nr");
			String name = gas.get("name");
			specie.put(nr, new SpecieDaConvertire(nr,name));
			if(gas.nonPresi().length()>0){
				errorManager.addErrore(new ErrorManager.Errore(
						"ATTENZIONE: nel tag \""+calcolaPercorso()+"\" non sono stati presi gli attibiuti \""+gas.nonPresi()+"\""));
			}
		}else if(qName.equals("Plot_package")){
			; // do nothing: di questo interessano gli elementi figli
		}else if(qName.equals("Plots")){
			; // do nothing: di questo interessano gli elementi figli
		}else if(qName.equals("Lookup_tables")){
			; // do nothing: di questo interessano gli elementi figli
		}else if(qName.equals("Coverscale_list")){
			ignora = true;
		}else if(qName.equals("Template")){
			ignora = true;
		}else if(qName.endsWith("_list")){
			TAG_list_iniziato = qName.substring(0,qName.indexOf("_list"));
			TAG_record = TAG_list_iniziato.toLowerCase(); 
			dizionari.put( TAG_record, new HashMap<String,HashMap<String, String>>() );
		}else if(TAG_record!=null && qName.equals(TAG_record+"_record")){
			dizionari.get(TAG_record).put(atts.getValue("code"), clonaAttributiUtili(atts));
		}else{
			errorManager.addErrore(new ErrorManager.Errore("ATTENZIONE: non conosco il tag \""+calcolaPercorso()+"\""));
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException{
		tagAttraversati.remove(tagAttraversati.size()-1);
		if(ignora){
			if(qName.equals("Template")){
				ignora = false;
			}
			if(qName.equals("Coverscale_list")){
				ignora = false;
			}
		}else if(qName.equals("Plot")){
			rilievi.add(rilievoAttuale);
			rilievoAttuale = null;
		}else if(qName.equals("header_data")){
			TAG_header_data = false;
		}else if(qName.equals("Species_list")){
			TAG_Species_list = false;
		}else if(qName.equals("species")){
			TAG_species = false;
		}else if(TAG_list_iniziato!=null && qName.startsWith(TAG_list_iniziato)){
			TAG_list_iniziato = null;
			TAG_record = null;
		}else if(campoInEsame != null){
			// impostaProprieta(cartellino, campoInEsame, stringa.toString());
			stringa = null;
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException{
		if(ignora)
			return;
		if(stringa!=null)
			stringa.append(ch,start,length);
	}
	
	private String calcolaPercorso(){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<tagAttraversati.size() ; i++){
			if(i>0){
				sb.append('→');
			}
			sb.append(tagAttraversati.get(i));
		}
		return sb.toString();
	}
	
	public Hashtable<String, SpecieDaConvertire> getSpecie() {
		return specie;
	}
	public ArrayList<SampleWrapper> getRilievi() {
		return rilievi;
	}
	public ErrorManager getErrorManager() {
		return errorManager;
	}
	public HashMap<String, HashMap<String, HashMap<String, String>>> getDizionari() {
		return dizionari;
	}
	
	private final HashMap<String, String> clonaAttributiUtili(Attributes att){
		HashMap<String, String> utili = new HashMap<String, String>();
		for(int i=0;i<att.getLength(); i++){
			if(!att.getQName(i).equals("code")){
				utili.put(att.getQName(i), att.getValue(i));
			}
		}
		return utili;
	}
	
}