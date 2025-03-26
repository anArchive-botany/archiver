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

import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Sample;
import it.aspix.sbd.obj.SimpleBotanicalData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/****************************************************************************
 * Estende l'oggetto Sample fornendo la possibilità di memorizzare informazioni
 * sulle specie rilevate prima di avere gli strati veri e propri 
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SampleWrapper extends Sample implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public static class SpecieData{
		public String id;
		public String abbondanza;
		public String strato;
		public SpecieData(String id, String abbondanza, String strato) {
			super();
			this.id = id;
			this.abbondanza = abbondanza;
			this.strato = strato;
		}
	}
	
	public ArrayList<SpecieData> specieData;
	// nome attributo turboveg -> valore
	public HashMap<String, String> proprieta;
	
	// questi 3 campi servono per la conversione delle coordinate
	public String epsg;
	public String coordinataX;
	public String coordinataY;
	
	public SampleWrapper(){
		super();
		specieData = new ArrayList<SampleWrapper.SpecieData>();
		proprieta = new HashMap<String, String>();
	}
	
	@SuppressWarnings("unchecked")
	public SampleWrapper(SampleWrapper modello){
		super();
		specieData = (ArrayList<SpecieData>) modello.specieData.clone();
		proprieta = (HashMap<String, String>) modello.proprieta.clone();
		this.epsg = modello.epsg;
		this.coordinataX = modello.coordinataX;
		this.coordinataY = modello.coordinataY;
	}
	
	/************************************************************************
	 * Aggiunge gli strati di questo rilievo ad un hash
	 * @param strati l'insieme a cui aggiungere gli strati presenti in questo rilievo
	 ***********************************************************************/
	public void aggiungiStrati(HashSet<String> strati){
		for(SpecieData x: specieData){
			if(x.strato!=null){
				strati.add(x.strato);
			}
		}
	}
	
	/************************************************************************
	 * @return l'oggetto Sample inglobato da questo Wrapper
	 * @throws Exception 
	 ***********************************************************************/
	public Sample getSample() throws Exception{
		Sample s = ((Sample)this).clone();
		if(coordinataX!=null && coordinataY!=null){
			SimpleBotanicalData risposta = Stato.comunicatore.conversioneCoordinate(epsg, coordinataX, coordinataY);
			if(risposta.getPlaceSize()==1){
				s.getPlace().setLatitude(risposta.getPlace(0).getLatitude());
				s.getPlace().setLongitude(risposta.getPlace(0).getLongitude());
			}else{
				throw new Exception("coordinate non convertibili: x="+coordinataX+" y="+coordinataY+" epsg="+epsg);
			}
		}
		return s;
	}
	
	/************************************************************************
	 * @return una copia di questo oggetto
	 * @throws IOException
	 * @throws ClassNotFoundException
	 ***********************************************************************/
	public SampleWrapper getCopia() throws IOException, ClassNotFoundException{
		SampleWrapper copia;
		// oggetto -> rappresentazione binaria
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(this);
		oos.close();
		bos.close();
		byte[] b = bos.toByteArray();
		// rappresentazione binaria -> oggetto
		ByteArrayInputStream bis = new ByteArrayInputStream(b);
		ObjectInputStream ois = new ObjectInputStream(bis);
		copia = (SampleWrapper) ois.readObject();
		ois.close();
		bis.close();
		return copia;
	}
	
}
