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

import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.introspection.ReflectUtil;
import it.aspix.sbd.obj.Attribute;
import it.aspix.sbd.obj.AttributeInfo;
import it.aspix.sbd.obj.Cell;
import it.aspix.sbd.obj.Classification;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.SimpleBotanicalData;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.xml.sax.SAXException;

public class GestoreAttributoAnArchive extends JPanel{

	private static final long serialVersionUID = 1L;
	
	public static String CONTENUTO_LATITUDINE = "latitudine";
	public static String CONTENUTO_LONGITUDINE = "longitudine";
	public static String CONTENUTO_ESPOSIZIONE = "esposizione";
	public static String CONTENUTO_INCLINAZIONE = "inclinazione";
	public static String CONTENUTO_DATA = "data";

	public static final String NON_UTILIZZARE = "non utilizzare";  
	
	private static final String SPECIALE = "SPE#";
	private static final String SPECIALE_LAYER = SPECIALE+"LAYER#";
	private static final String SPECIALE_ATTRIBUTO = SPECIALE+"ATTRIBUTO#";
	
	private static final String SPECIALE_X = SPECIALE+"X";
	private static final String SPECIALE_Y = SPECIALE+"Y";
	private static final String SPECIALE_EPSG = SPECIALE+"EPSG";
	private static final String SPECIALE_ASSOCIAZIONE = SPECIALE+"ASSOCIAZIONE";
	private static final String SPECIALE_TYPUS = SPECIALE+"TYPUS";
	
	public static class Tripletta{
		public String gruppo;
		public String attributo;
		public String descrizione;
		public boolean ignora = false;
		public Tripletta(String gruppo, String attributo, String descrizione) {
			super();
			this.gruppo = gruppo;
			this.attributo = attributo;
			this.descrizione = descrizione;
		}
	}
	
	/**
	 * Alcune triplette vanno inserire chiedendo informazioni al server,
	 * Questo può essere fatto soltanto dinamicamente a runtime
	 */
	private static boolean costruzioneTripletteCompletata = false;
	private static Tripletta[] triplette;
	public static HashMap<String, String> corrispondenzeTurbovegAnArchiveDefault;
	
	DefaultComboBoxModel<String> modelloGruppo;
	JComboBox<String> comboGruppo;
	DefaultComboBoxModel<String> modelloAttributo;
	JComboBox<String> comboAttributo;
	
	public GestoreAttributoAnArchive(Tripletta esterni[]) throws SAXException, IOException, InterruptedException, URISyntaxException{
		if(!costruzioneTripletteCompletata){
			costruisciTriplette(esterni);
		}
		this.setLayout(new BorderLayout());
		modelloGruppo = new DefaultComboBoxModel<>();
		comboGruppo = new JComboBox<>(modelloGruppo);
		modelloAttributo = new DefaultComboBoxModel<>();
		comboAttributo = new JComboBox<>(modelloAttributo);
		this.add(comboGruppo, BorderLayout.WEST);
		this.add(comboAttributo, BorderLayout.CENTER);
		
		HashSet<String> giaInseriti = new HashSet<String>();
		for(Tripletta t:triplette){
			if(!giaInseriti.contains(t.gruppo)){
				modelloGruppo.addElement(t.gruppo);
				giaInseriti.add(t.gruppo);
			}
		}
		
		comboGruppo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				azioneGruppo();
			}
		});
		comboGruppo.setSelectedIndex(0);
		comboAttributo.setMaximumRowCount(18);
	}
	
	private void azioneGruppo(){
		String selezione = (String) modelloGruppo.getSelectedItem();
		modelloAttributo.removeAllElements();
		for(Tripletta t:triplette){
			if(t.gruppo.equals("-") || selezione.equals("-") || t.gruppo.equals(selezione)){
				modelloAttributo.addElement(t.descrizione);
			}
		}
	}

	public String getText(){
		return (String) comboAttributo.getSelectedItem();
	}
	
	public void setText(String testo){
		Tripletta t = getTriplettaPerDescrizione(testo);
		comboAttributo.setSelectedIndex(0);
		modelloAttributo.setSelectedItem(t.descrizione);
		if(comboAttributo.getSelectedIndex()==0){
			modelloGruppo.setSelectedItem(t.gruppo);
			azioneGruppo();
			modelloAttributo.setSelectedItem(t.descrizione);
		}
	}
	
	private static final Tripletta getTriplettaPerDescrizione(String descrizione){
		for(int i=0; i<triplette.length; i++){
			if(triplette[i].descrizione.equals(descrizione)){
				return triplette[i];
			}
		}
		return triplette[0];
	}
	
	private static final Tripletta getTriplettaPerAttributo(String attributo){
		for(int i=0; i<triplette.length; i++){
			if(triplette[i].attributo.equals(attributo)){
				return triplette[i];
			}
		}
		return triplette[0];
	}
	
	public void addActionListener(ActionListener a){
		comboAttributo.addActionListener(a);
	}
	
	public static boolean isStrato(String descrizione){
		Tripletta trovata = getTriplettaPerDescrizione(descrizione);
		return trovata!=null && trovata.attributo.startsWith(SPECIALE_LAYER);
	}
	
	public static String impostaAttributiRilievo(SampleWrapper rilievo, HashMap<String, String>mappaturaAttributi, String patternStrati) throws Exception{
		// pulizia: server per fare in modo che se viene chiamata due volte quetsa funzione sullo
		// stesso oggetto il tutto funzioni
		rilievo.getCell().removeAllLevels();
		rilievo.removeAllClassification();
		// creo gli strati
		CostruttoreOggetti.addLevelsByPattern(rilievo.getCell(), patternStrati);
		rilievo.getCell().setModelOfTheLevels(patternStrati);
		String proprietaValore;
		String proprietaAnArchive;
		String errore = null;
		for(String proprietaTurboveg: rilievo.proprieta.keySet()){
			// al difuori di questa classe si usano le descrizioni delgi attributi
			proprietaAnArchive = getTriplettaPerDescrizione(mappaturaAttributi.get(proprietaTurboveg)).attributo;
			proprietaValore = rilievo.proprieta.get(proprietaTurboveg);
			if(getTriplettaPerDescrizione(mappaturaAttributi.get(proprietaTurboveg)).ignora){
				// le proprieta marcate "ignora" sono quelle che non devono essere gestite da questa classe
				continue;
			}	
			if(proprietaAnArchive!=null && proprietaAnArchive.length()>0){
				impostaAttributoAnArchive(rilievo, proprietaAnArchive, proprietaValore);
			}
		}
		return errore;
	}
	
	/************************************************************************
	 * @param cella in cui cercare il livello (solo direttamente figli)
	 * @param id del livello da cercare
	 * @return il livello che corrisponde all'id
	 ***********************************************************************/
	private static Level cercaLevel(Cell cella, String id){
		Level trovato = null;
		for(int i=0; i<cella.getLevelCount() && trovato==null; i++){
			if(cella.getLevel(i)!=null && id.equals(cella.getLevel(i).getId())){
				trovato = cella.getLevel(i);
			}
		}
		return trovato;
	}
	
	/************************************************************************
	 * @param rilievo da utilizzare
	 * @param proprieta la descrizione della proprietà da impostare
	 * @param valore della proprietà
	 * @throws Exception
	 ***********************************************************************/
	public static void impostaAttributo(SampleWrapper rilievo, String proprieta, String valore) throws Exception{
		String proprietaAnArchive = getTriplettaPerDescrizione(proprieta).attributo;
		impostaAttributoAnArchive(rilievo, proprietaAnArchive, valore);
	}
	
	/************************************************************************
	 * Usata internamente per impostare le proprietà
	 * @param rilievo su cui lavorare
	 * @param proprietaAnArchive
	 * @param valore
	 * @throws Exception
	 ***********************************************************************/
	private static void impostaAttributoAnArchive(SampleWrapper rilievo, String proprietaAnArchive, String valore) throws Exception{
		if(proprietaAnArchive.startsWith(SPECIALE)){
			if(proprietaAnArchive.equals(SPECIALE_X)){
				rilievo.coordinataX = valore;
			}else if(proprietaAnArchive.equals(SPECIALE_Y)){
				rilievo.coordinataY = valore;
			}else if(proprietaAnArchive.equals(SPECIALE_EPSG)){
				rilievo.epsg = valore;
			}else if(proprietaAnArchive.startsWith(SPECIALE_ASSOCIAZIONE)){
				Classification c=getOrCreateClassification(rilievo);
				c.setName(valore.trim() );
			}else if(proprietaAnArchive.startsWith(SPECIALE_TYPUS)){
				Classification c=getOrCreateClassification(rilievo);
				c.setTypus(valore.trim() );
			}else if(proprietaAnArchive.startsWith(SPECIALE_LAYER)){
				// la struttura delle proprietà è nella classe Costanti
				String utile = proprietaAnArchive.substring(SPECIALE_LAYER.length());
				String parti[] = utile.split("\\-");
				Level l;
				l = cercaLevel(rilievo.getCell(), parti[0]);
				if(l!=null){
					ReflectUtil.setViaReflection(l, parti[1], valore);
				}else{
					throw new Exception("Non trovo lo strato \""+parti[0]+"\" nel rilievo.");
				}
			}else if(proprietaAnArchive.startsWith(SPECIALE_ATTRIBUTO)){
				// la struttura delle proprietà è nella classe Costanti
				String utile = proprietaAnArchive.substring(SPECIALE_ATTRIBUTO.length());
				Attribute attributo = new Attribute(utile, valore);
				rilievo.getDirectoryInfo().addAttribute(attributo);
			}else{
				throw new UnsupportedOperationException("La proprietà "+proprietaAnArchive+" non ha un metodo di gestione.");
			}
		}else{
			if(proprietaAnArchive.equals("date") && valore.matches("[0-9]+") && valore.length()==8){
				// la proprieta è nel formato aaaammgg
				valore = valore.substring(0, 4)+"-"+
				valore.substring(4, 6)+"-"+
				valore.substring(6, 8);
			}
			Stato.debugHint = "prop: "+proprietaAnArchive+" val:"+valore;
			ReflectUtil.setViaReflection(rilievo, proprietaAnArchive, valore);
		}
	}
	
	/************************************************************************
	 * @param x la descrizione da cercare
	 * @return quella trovata
	 ***********************************************************************/
	public static String cercaAttributo(String x){
		if(x!=null){
			for(Tripletta t: triplette){
				if(t.descrizione.toLowerCase().equals(x.toLowerCase())){
					return t.descrizione;
				}
			}
		}
		return null;
	}
	
	/************************************************************************
	 * @return un elenco di tutte le intestazioni possibili per le righe
	 ***********************************************************************/
	public static ArrayList<String> elencoNomiRighe(){
		ArrayList<String> elenco = new ArrayList<String>();
		for(Tripletta t: triplette){
			elenco.add(t.descrizione);
		}
		return elenco;
	}
	
	private static void costruisciTriplette(Tripletta esterni[]) throws SAXException, IOException, InterruptedException, URISyntaxException{
		ArrayList<Tripletta> predefinite = new ArrayList<Tripletta>();
		predefinite.add(new Tripletta("-","", NON_UTILIZZARE));
		
		predefinite.add(new Tripletta("Generale","keywords","parole chiave"));
		predefinite.add(new Tripletta("Generale","surveyer","rilevatore"));
		predefinite.add(new Tripletta("Generale","date", CONTENUTO_DATA));
		predefinite.add(new Tripletta("Generale","community","nome provvisorio"));
		// predefinite.add(new Tripletta("Generale","giacitura","giacitura"));
		// predefinite.add(new Tripletta("Generale","microHabitat","micro habitat"));
		// predefinite.add(new Tripletta("Generale","margini","margini"));
		// predefinite.add(new Tripletta("Generale","proprieta","proprietà"));
		// predefinite.add(new Tripletta("Generale","specieEsotiche","specie esotiche"));
		// predefinite.add(new Tripletta("Generale","corpiIdrici","corpi idrici"));
		// predefinite.add(new Tripletta("Generale","coperturaBoschi","copertura boschi"));
		// predefinite.add(new Tripletta("Generale","accesso","accesso"));
		predefinite.add(new Tripletta("Generale","arrivalTime","ora di arrivo"));
		predefinite.add(new Tripletta("Generale","startTime","ora di inizio"));
		predefinite.add(new Tripletta("Generale","stopTime","ora di fine"));
		predefinite.add(new Tripletta("Generale","originalNote","note originali"));
		predefinite.add(new Tripletta("Generale","note","note"));
		
		predefinite.add(new Tripletta("Pubblicazione","publicationRef.reference","riferimento LISY"));
		predefinite.add(new Tripletta("Pubblicazione","publicationRef.citation","citazione"));
		predefinite.add(new Tripletta("Pubblicazione","publicationRef.table","numero tabella"));
		predefinite.add(new Tripletta("Pubblicazione","publicationRef.number","numero del rilievo nella tabella"));
		predefinite.add(new Tripletta("Pubblicazione","publicationRef.number","numero rilievo nella tabella"));
		
		predefinite.add(new Tripletta("Geografici","place.name","località"));
		predefinite.add(new Tripletta("Geografici","place.town","comune"));
		predefinite.add(new Tripletta("Geografici","place.province","provincia"));
		predefinite.add(new Tripletta("Geografici","place.region","regione"));
		predefinite.add(new Tripletta("Geografici","place.country","stato"));
		predefinite.add(new Tripletta("Geografici","place.macroPlace","macro località"));
		predefinite.add(new Tripletta("Geografici","place.protectedAreaType","tipo dell'area protetta"));
		predefinite.add(new Tripletta("Geografici","place.protectedAreaType","tipo di area protetta"));
		predefinite.add(new Tripletta("Geografici","place.protectedAreaName","nome dell'area protetta"));
		predefinite.add(new Tripletta("Geografici","place.protectedAreaName","nome area protetta"));
		predefinite.add(new Tripletta("Geografici","place.mainGrid","reticolo cartografico regionale"));
		predefinite.add(new Tripletta("Geografici","place.secondaryGrid","reticolo UTM"));
		predefinite.add(new Tripletta("Geografici","place.idInfc","id infc"));
		predefinite.add(new Tripletta("Geografici","place.pointSource","sorgente del punto"));
		predefinite.add(new Tripletta("Geografici","place.latitude","latitudine"));
		predefinite.add(new Tripletta("Geografici","place.longitude","longitudine"));
		predefinite.add(new Tripletta("Geografici",SPECIALE_X,"coordinata X"));
		predefinite.add(new Tripletta("Geografici",SPECIALE_Y,"coordinata Y"));
		predefinite.add(new Tripletta("Geografici",SPECIALE_EPSG,"codice epsg"));
		predefinite.add(new Tripletta("Geografici","place.pointPrecision","precisione del punto"));
		// localita.pointEpsg;
		predefinite.add(new Tripletta("Geografici","place.elevation","altitudine"));
		predefinite.add(new Tripletta("Geografici","place.exposition", CONTENUTO_ESPOSIZIONE));
		predefinite.add(new Tripletta("Geografici","place.inclination", CONTENUTO_INCLINAZIONE));
		predefinite.add(new Tripletta("Geografici","place.substratum","substrato"));
		predefinite.add(new Tripletta("Geografici","place.habitat","habitat"));
		
		predefinite.add(new Tripletta("Area","cell.totalCovering","copertura totale"));
		predefinite.add(new Tripletta("Area","cell.shapeName","forma del rilievo"));
		predefinite.add(new Tripletta("Area","cell.shapeArea","area"));
		predefinite.add(new Tripletta("Area","cell.shapeDimension1","dimensione 1"));
		predefinite.add(new Tripletta("Area","cell.shapeDimension2","dimensione 2"));
		predefinite.add(new Tripletta("Area","cell.shapeOrientation","orientamento"));
		predefinite.add(new Tripletta("Area","cell.type","tipo"));
		predefinite.add(new Tripletta("Area","cell.abundanceScale","scala di abbondanza"));
		predefinite.add(new Tripletta("Area","cell.workTime","tempo di lavorazione"));
		
		// predefinite.add(new Tripletta("directoryInfo.containerName","nome del progetto"));
		predefinite.add(new Tripletta("Generale","directoryInfo.containerSeqNo","numero progessivo del progetto"));
		predefinite.add(new Tripletta("Generale","directoryInfo.containerExternalId","id nel progetto"));
		predefinite.add(new Tripletta("Generale","directoryInfo.subContainerName","sottoprogetto"));
		predefinite.add(new Tripletta("Generale","directoryInfo.subContainerSeqNo","progressivo nel sottoprogetto"));
		predefinite.add(new Tripletta("Generale","directoryInfo.subContainerExternalId","id nel sottoprogetto"));
		predefinite.add(new Tripletta("Generale","directoryInfo.note","note per il sistema"));
		// insertionName;
		// insertionTimeStamp;
		// lastUpdateName;
		// lastUpdateTimeStamp;
		// note;
		// ownerReadRights;
		// ownerWriteRights;
		// containerReadRights;
		// containerWriteRights;
		// othersReadRights;
		predefinite.add(new Tripletta("Generale","directoryInfo.othersReadRights","accessibilità via web"));
		// String othersWriteRights;
	    // private ArrayList<Classification> classificazione;
		predefinite.add(new Tripletta("Generale",SPECIALE_ASSOCIAZIONE,"classificazione"));
		predefinite.add(new Tripletta("Generale",SPECIALE_TYPUS,"typus classificazione"));
		
		addSerieAttributiStrato(predefinite, "1", "alberi");
		addSerieAttributiStrato(predefinite, "1.1", "alberi alti");
		addSerieAttributiStrato(predefinite, "1.2", "alberi medi");
		addSerieAttributiStrato(predefinite, "1.3", "alberi bassi");
		addSerieAttributiStrato(predefinite, "2", "arbusti");
		addSerieAttributiStrato(predefinite, "2.1", "arbusti alti");
		addSerieAttributiStrato(predefinite, "2.2", "arbusti medi");
		addSerieAttributiStrato(predefinite, "2.3", "arbusti bassi");
		addSerieAttributiStrato(predefinite, "3", "erbe");
		addSerieAttributiStrato(predefinite, "3.1", "erbe alte");
		addSerieAttributiStrato(predefinite, "3.2", "erbe medie");
		addSerieAttributiStrato(predefinite, "3.3", "erbe basse");
		addSerieAttributiStrato(predefinite, "4", "tallofite");
		addSerieAttributiStrato(predefinite, "4.1", "funghi");
		addSerieAttributiStrato(predefinite, "4.2", "briofite");
		addSerieAttributiStrato(predefinite, "4.3", "licheni");
		addSerieAttributiStrato(predefinite, "5", "liane");
		addSerieAttributiStrato(predefinite, "6", "idrofite");
		addSerieAttributiStrato(predefinite, "6.1", "idrofite galleggianti");
		addSerieAttributiStrato(predefinite, "6.2", "idrofite sommerse");
		addSerieAttributiStrato(predefinite, "6.3", "idrofite radicanti");
		addSerieAttributiStrato(predefinite, "7", "acqua");
		addSerieAttributiStrato(predefinite, "8", "lettiera");
		addSerieAttributiStrato(predefinite, "9", "rocce");
		addSerieAttributiStrato(predefinite, "10", "pietre");
		
		SimpleBotanicalData risposta;
		risposta = Stato.comunicatore.recuperaInformazioniAttributi();
        ArrayList<Tripletta> remoti = new ArrayList<GestoreAttributoAnArchive.Tripletta>();
        for(AttributeInfo ai: risposta.getAttributeInfo()){
        	if(ai.getValidIn().contains("vegetation")){
        		remoti.add(new Tripletta("attributi", SPECIALE_ATTRIBUTO+ai.getName(), ai.getName()));
        	}
        }
        Tripletta nuove[] = new Tripletta[predefinite.size()+remoti.size()+(esterni!=null?esterni.length:0)];
        int i=0;
        for(Tripletta x: predefinite){
        	nuove[i++] = x;
        }
        if(esterni!=null){
	        for(Tripletta x: esterni){
	        	x.ignora = true;
	        	nuove[i++] = x;
	        }
        }
        for(Tripletta x: remoti){
        	nuove[i++] = x;
        }
        triplette = nuove;
        
        // creo le corrispondenze di default
        corrispondenzeTurbovegAnArchiveDefault = new HashMap<String, String>();
		corrispondenzeTurbovegAnArchiveDefault.put("toponym", getTriplettaPerAttributo("place.name").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("comune", getTriplettaPerAttributo("place.town").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("provincia", getTriplettaPerAttributo("place.province").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("regione", getTriplettaPerAttributo("place.region").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("country", getTriplettaPerAttributo("place.country").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("georef", getTriplettaPerAttributo("place.pointSource").descrizione); // TODO: conversione
		corrispondenzeTurbovegAnArchiveDefault.put("longitude", getTriplettaPerAttributo("place.longitude").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("latitude", getTriplettaPerAttributo("place.latitude").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("x_coord", getTriplettaPerAttributo(SPECIALE_X).descrizione); // TODO: conversione
		corrispondenzeTurbovegAnArchiveDefault.put("y_coord", getTriplettaPerAttributo(SPECIALE_Y).descrizione); // TODO: conversione
		corrispondenzeTurbovegAnArchiveDefault.put("utm", getTriplettaPerAttributo("place.secondaryGrid").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("altitude", getTriplettaPerAttributo("place.elevation").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("exposition", getTriplettaPerAttributo("place.exposition").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("inclination", getTriplettaPerAttributo("place.inclination").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("cov_total", getTriplettaPerAttributo("cell.totalCovering").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("date", getTriplettaPerAttributo("date").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("surf_area", getTriplettaPerAttributo("cell.shapeArea").descrizione);
		// corrispondenzeTurbovegAnArchiveDefault.put("coverscale", getTriplettaPerAttributo("cell.abundanceScale").descrizione); meglio toglierlo perché la scala viene separata in altra sede
		corrispondenzeTurbovegAnArchiveDefault.put("remarks", getTriplettaPerAttributo("originalNote").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("table_nr", getTriplettaPerAttributo("publicationRef.table").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("nr_in_tab", getTriplettaPerAttributo("publicationRef.number").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("sic_zps", getTriplettaPerAttributo("place.protectedAreaType").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("fonte", getTriplettaPerAttributo("publicationRef.citation").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("transetto", getTriplettaPerAttributo("directoryInfo.subContainerSeqNo").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("releve_nr", getTriplettaPerAttributo("directoryInfo.subContainerSeqNo").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("project", getTriplettaPerAttributo("directoryInfo.subContainerName").descrizione); // TODO: verificare meglio
		corrispondenzeTurbovegAnArchiveDefault.put("author", getTriplettaPerAttributo("surveyer").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("authors", getTriplettaPerAttributo("surveyer").descrizione);
		// i dati sugli strati
		corrispondenzeTurbovegAnArchiveDefault.put("cov_shrubs", getTriplettaPerAttributo(SPECIALE_LAYER+"2-coverage").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("shrub_high", getTriplettaPerAttributo(SPECIALE_LAYER+"2-height").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("cov_herbs", getTriplettaPerAttributo(SPECIALE_LAYER+"3-coverage").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("herbs_high", getTriplettaPerAttributo(SPECIALE_LAYER+"3-height").descrizione);
		corrispondenzeTurbovegAnArchiveDefault.put("cov_rock", getTriplettaPerAttributo(SPECIALE_LAYER+"9-coverage").descrizione);	
	
		costruzioneTripletteCompletata = true;
	}
	
	private static void addSerieAttributiStrato(ArrayList<Tripletta>x, String numero, String descrizione){
		x.add(new Tripletta("Strati",SPECIALE_LAYER+numero+"-coverage", descrizione+": copertura"));
		x.add(new Tripletta("Strati",SPECIALE_LAYER+numero+"-height", descrizione+": altezza media"));
		x.add(new Tripletta("Strati",SPECIALE_LAYER+numero+"-heightMin", descrizione+": altezza minima"));
		x.add(new Tripletta("Strati",SPECIALE_LAYER+numero+"-heightMax", descrizione+": altezza massima"));
	}
	
	/************************************************************************
	 * @param rilievo di cui interessa la classificazione
	 * @return la prima possibile, se non c'è viene creata
	 ***********************************************************************/
	private static Classification getOrCreateClassification(SampleWrapper rilievo){
		Classification c;
		if(rilievo.getClassification()==null || rilievo.getClassification().length==0){
			c = new Classification();
			c.setType("actual");
			rilievo.addClassification(c);
		}else{
			c = rilievo.getClassification(0);
		}
		return c;
	}

}
