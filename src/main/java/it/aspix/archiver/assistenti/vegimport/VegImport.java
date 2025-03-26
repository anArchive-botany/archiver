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

import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.Icone;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.assistenti.AccettabilitaFile;
import it.aspix.archiver.assistenti.AttesaIndefinita;
import it.aspix.archiver.assistenti.BarraAvanzamentoWizard;
import it.aspix.archiver.assistenti.CommonTasks;
import it.aspix.archiver.assistenti.DialogoDatiStatici;
import it.aspix.archiver.assistenti.DialogoNomeFile;
import it.aspix.archiver.assistenti.DialogoRapportoInserimento;
import it.aspix.archiver.assistenti.DialogoSpecie;
import it.aspix.archiver.assistenti.ErrorManager;
import it.aspix.archiver.assistenti.GestoreAttributoAnArchive;
import it.aspix.archiver.assistenti.OggettoConLivello;
import it.aspix.archiver.assistenti.SampleWrapper;
import it.aspix.archiver.assistenti.tabimport.TabImport;
import it.aspix.archiver.assistenti.vegimport.attributi.AzioneSuAttributi;
import it.aspix.archiver.assistenti.vegimport.attributi.DialogoElaborazioneAttributi;
import it.aspix.archiver.dialoghi.AperturaApplicazione;
import it.aspix.archiver.dialoghi.ComunicazioneEccezione;
import it.aspix.archiver.editor.SampleEditorDaEstendere;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.archiver.nucleo.Comunicatore;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.Util;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.QualifiedName;
import it.aspix.sbd.obj.Sample;
import it.aspix.sbd.obj.SimpleBotanicalData;
import it.aspix.sbd.obj.SurveyedSpecie;
import it.aspix.sbd.saxHandlers.SHSample;
import it.aspix.sbd.saxHandlers.SHSurveyedSpecie;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/****************************************************************************
 * Effettua una importazione guidata dei dati da un file 
 * "XML standard file" di Turboveg.
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class VegImport {

	public static final String NOME_ASSISTENTE = "VegImport";
	public static final String FILE_LICENZA = "/it/aspix/entwash/assistenti/vegimport/licenza.html";
	public static final String URL_MANUALE = "http://www.vegitaly.it/manuale-VegImport.html";
	private static DefaultTableModel tabellaCorrispondenzaNomi; // (id,nome turboveg,nome anArchive)
	
	private static class InfoStratificazione{
		public String patternStrati;
		public Hashtable<String, String> corrispondenzaStrati;
	}
	
	static BarraAvanzamentoWizard barraAvanzamento;

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, URISyntaxException, InterruptedException {
        
        Stato.debugLog.finest("Avvio VegImport");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name","VegImport");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        UtilitaGui.impostaIcona(Icone.LogoVegImportDoc);
       
        // come numero di versione uso il timestamp
        AperturaApplicazione attesaApertura = new AperturaApplicazione(
            Icone.splashVegImport,
            "Versione ["+Stato.buildTimeStamp+"] http://www.anarchive.it", 
            Color.WHITE,
            5
        );
        attesaApertura.setVisible(true);
        // recupera le proprieta
        attesaApertura.setAvanzamento("Recupero le preferenze...",1);
        Proprieta.caricaProprieta();
        // richiesta della password
        attesaApertura.setAvanzamento("Richiesta password...",2);
        attesaApertura.chiediPassword();
        attesaApertura.aggiornaConnessione();
        Proprieta.check();
        
        Comunicatore comunicatore=new Comunicatore("VegImport", Stato.versioneTools);
        Stato.comunicatore = comunicatore;
        // controllo i diritti di accesso
        
        attesaApertura.setAvanzamento("Chiedo verifica diritti di accesso...",3);

        if(! comunicatore.login() ){
            UtilitaGui.mostraMessaggioAndandoACapo("In server non ha accettato il login", "Errore nella verifia dei diritti", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
          
        // apre la finestra degli aggiornamenti (che si apre solo se serve)
        // XXX: in VegImport la finestra non la apro
        /* ComunicazioneAggiornamenti da = new ComunicazioneAggiornamenti(Stato.versione);
        da.setVisible(true); */
        //apre la finestra principale
        attesaApertura.setAvanzamento("Creo la finestra principale...",4);
        
        attesaApertura.setVisible(false);
        attesaApertura.dispose();
        attesaApertura.setAvanzamento("Avvio completato",5);
        
        barraAvanzamento = new BarraAvanzamentoWizard(VegImport.NOME_ASSISTENTE, VegImport.FILE_LICENZA, VegImport.URL_MANUALE);
        
        String nomeFile = null;
        SampleWrapper modelloRilievi = null;
        SHTveg handler = null;
		HashMap<String, String> turbovegToAnArchive = null; // corrispondenze degli attributi
		InfoStratificazione infoStratificazione = null;
        Sample[] elencoRilieviDaInviare = null;
        int passo = 0;
		try{
			while(passo<8){
				if(passo==0){
					nomeFile = fase_nomeFile();
					// non c'è una barra di avanzamento della finestra del file
					passo++;
				}
				if(passo==1){
					modelloRilievi = fase_datiStatici(nomeFile);
					if(barraAvanzamento.isAvanti()){
						// analisi SAX
						handler = fase_analisiSax(nomeFile, modelloRilievi);
						// attenzione che questo sotto modifica il parametro passato
						// rimpiazza i valori presenti nei dizionari
						fase_elaborazioneDizionari(handler.getDizionari(), handler.getRilievi());
						passo += ( barraAvanzamento.isAvanti() ? 1 : 0 );
					}else{
						passo--;
					}
				}
				if(passo==2){
					fase_elaborazioneAttributi(nomeFile, handler);
					passo += ( barraAvanzamento.isAvanti() ? 1 : -1 );
				}
				if(passo==3){
					turbovegToAnArchive = fase_associazioneAttributi(nomeFile, handler);
					passo += ( barraAvanzamento.isAvanti() ? 1 : -1 );
				}
				if(passo==4){
					infoStratificazione = fase_associazioneStrati(turbovegToAnArchive, handler.getRilievi());
					passo += ( barraAvanzamento.isAvanti() ? 1 : -1 );
				}	        
		        if(passo==5){
		        	fase_nomiSpecie(nomeFile, handler.getSpecie());
		        	if(barraAvanzamento.isAvanti()){
		        		AttesaIndefinita ai = new AttesaIndefinita("Elaborazione dei rilievi...");
				        UtilitaGui.centraDialogoAlloSchermo(ai, UtilitaGui.CENTRO);
				        ai.setVisible(true);
		        		fase_inserimentoAttributiNeiRilievi(handler.getRilievi(), turbovegToAnArchive, infoStratificazione.patternStrati);
		        		fase_inserimentoSpecieNegliStrati(handler.getRilievi(), infoStratificazione.corrispondenzaStrati);
		    	        elencoRilieviDaInviare = eliminaStratiInutili(handler.getRilievi());
		    	        ai.setVisible(false);
		    	        passo++;
		        	}else{
		        		passo--;
		        	}
		        }
		        if(passo==6){
		        	CommonTasks.fase_revisionePrimaDellInvio(elencoRilieviDaInviare, modelloRilievi.getCell().getAbundanceScale(), barraAvanzamento);
		        	if(barraAvanzamento.isAvanti()){
		        		passo++;
		        	}else{
		        		passo--;
		        	}
		        }
		        if(passo==7){
		        	CommonTasks.fase_invioInSimulazione(elencoRilieviDaInviare, modelloRilievi.getCell().getAbundanceScale(), barraAvanzamento);
		        	if(barraAvanzamento.isAvanti()){
		        		passo++;
		        	}else{
		        		passo--;
		        	}
		        }
			}
	        CommonTasks.fase_invioDati(elencoRilieviDaInviare);	        
			DialogoRapportoInserimento dri = new DialogoRapportoInserimento(CommonTasks.esitoInvioRilievo);
			UtilitaGui.centraDialogoAlloSchermo(dri, UtilitaGui.CENTRO);
			dri.setVisible(true);
		}catch(Throwable ex){
			ex.printStackTrace();
			ComunicazioneEccezione ce = new ComunicazioneEccezione(ex);
			ce.setVisible(true);
		}
		System.exit(0);
		
	}
	
	/************************************************************************
	 * 
	 ***********************************************************************/
	public static String fase_nomeFile(){
		AccettabilitaFile af = new AccettabilitaFile() {
			public boolean isAccettabile(File f) {
				char buffer[] = new char[2000];
		    	String letta;
		    	boolean accettabile;
		    	FileReader fr;
				try {
					fr = new FileReader(f);
			    	fr.read(buffer);
			    	fr.close();
			    	letta = new String(buffer);
			    	accettabile = letta.indexOf("Plot_package")!=-1 && letta.indexOf("Plot")!=-1; 
				} catch (Exception e) {
					accettabile = false; 
				}
		    	return accettabile;
			}
		};
		DialogoNomeFile y = new DialogoNomeFile("Trascina un file xml", af);
		sistemaDialogo(y);
		return y.getNomeFile();
	}
	
	/************************************************************************
	 * Richiede i dati validi per tutti i rilievi
	 ***********************************************************************/
	public static SampleWrapper fase_datiStatici(String nomeFile){
		DialogoDatiStatici finestraDatiStatici = new DialogoDatiStatici(VegImport.NOME_ASSISTENTE+": dati comuni a tutti i rilievi",null,barraAvanzamento);

		File fileDatiStatici = CommonTasks.getFileProprieta(nomeFile, "comuni");
		if(fileDatiStatici.exists()){
			try{
				Properties staticiSalvati = new Properties();
				staticiSalvati.load(new FileInputStream(fileDatiStatici));
				
				SAXParser parserSAX;
				SHSample handler = new SHSample(Util.SBD_VERSION);
				InputSource is;
				
	            SAXParserFactory fpf = SAXParserFactory.newInstance();
	            fpf.setValidating(false);
	            parserSAX = fpf.newSAXParser();
	            if(staticiSalvati.getProperty("modello")!=null){
					is = new InputSource(new StringReader(staticiSalvati.getProperty("modello")));
					parserSAX.parse(is, handler);
					finestraDatiStatici.setModelloRilievo(handler.getSample());
					finestraDatiStatici.setEpsg(staticiSalvati.getProperty("epsg"));
				}
			}catch(Exception x){
				; // do nothing, la prima volta il file non esiste
			}
		}
		// mostro la finestra
		sistemaDialogo(finestraDatiStatici);
		// salvo i dati immessi
		if(fileDatiStatici!=null){
			Properties daSalvare = new Properties();
			daSalvare.put("modello", finestraDatiStatici.getModelloRilievo().toXMLString(false));
			daSalvare.put("epsg", finestraDatiStatici.getEpsg());
			try {
				daSalvare.store(new FileOutputStream(fileDatiStatici),"dati statici per VegImport");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return finestraDatiStatici.getModelloRilievo();
	}
	
	/************************************************************************
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 ***********************************************************************/
	public static SHTveg fase_analisiSax(String nome, SampleWrapper modelloRilievo) throws ParserConfigurationException, SAXException, IOException{
		SAXParserFactory parserFactory;
		SAXParser parserSAX;
		ErrorManager eManagerPerParser;
		FileInputStream fis;
		InputSource is;
		DialogoComunicazioneStatoImportazione finestraPasso2;	// visualizza statistiche e dati non importati
		
		parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(false);
        parserSAX = parserFactory.newSAXParser();
        eManagerPerParser = new ErrorManager();
        fis = new FileInputStream(nome);
		is = new InputSource(fis);
		SHTveg handlerSAXPerTurboveg = new SHTveg(eManagerPerParser, modelloRilievo);
		parserSAX.parse(is, handlerSAXPerTurboveg);
		fis.close();
		// mostro errori e statistiche all'utente
		finestraPasso2 = new DialogoComunicazioneStatoImportazione(handlerSAXPerTurboveg, barraAvanzamento);
		sistemaDialogo(finestraPasso2);
		return handlerSAXPerTurboveg;
	}
	
	/************************************************************************
	 * @param dizionari
	 * @param rilieviLetti ATTENZIONE: questi vengono modificati
	 ***********************************************************************/
	public static void fase_elaborazioneDizionari(HashMap<String, HashMap<String, HashMap<String, String>>> dizionari, ArrayList<SampleWrapper> rilieviLetti){
		Set<String> nomiDizionari = dizionari.keySet();
		HashMap<String, HashMap<String, String>> dizionarioDaUtilizzare;
		HashMap<String, String> sostituzioni;
		String valoreAttuale;
		String nomeNuovaProprieta;
		
		for(String nomeDizionario : nomiDizionari){
			dizionarioDaUtilizzare = dizionari.get(nomeDizionario);
			for(String k: dizionarioDaUtilizzare.keySet()){
				sostituzioni = dizionarioDaUtilizzare.get(k);
			}
		}
		
		// controllo tutti i rilievi
		for(SampleWrapper rilievo: rilieviLetti){
			// controllo tutti i dizionari
			for(String nomeDizionario : nomiDizionari){
				// il rilievo usa il dizionario in questione?
				if(rilievo.proprieta.containsKey(nomeDizionario)){
					valoreAttuale = rilievo.proprieta.get(nomeDizionario);
					dizionarioDaUtilizzare = dizionari.get(nomeDizionario);
					sostituzioni = dizionarioDaUtilizzare.get(valoreAttuale);
					// sostituisco ogni attributo
					for(String nomeAttributo: sostituzioni.keySet()){
						nomeNuovaProprieta = nomeDizionario+"_"+nomeAttributo;
						rilievo.proprieta.put(nomeNuovaProprieta, sostituzioni.get(nomeAttributo));
					}
					rilievo.proprieta.remove(nomeDizionario);
				}
			}
		}			
	}
	
	/************************************************************************
	 * Associa gli strati da turboveg ad anArchive
	 ***********************************************************************/
	static InfoStratificazione fase_associazioneStrati(HashMap<String, String> turbovegToAnArchive, ArrayList<SampleWrapper> rilieviLetti){
		InfoStratificazione is = new InfoStratificazione();
		DialogoGestioneStrati finestraPasso5;					// associazione strati turboveg -> anarchive
		HashSet<String> stratiDaTurboveg = new HashSet<String>();
		// aggiungo ad un insieme i nomi degli strati
		for(int i=0; i<rilieviLetti.size();i++){
			rilieviLetti.get(i).aggiungiStrati(stratiDaTurboveg);
		}
		// vanno aggiunti anche tutti gli strati che hanno delle proprietà
		// impostate (ad esempio l'altezza) sebbene non contengano specie
		HashSet<String> stratiDiAnArchive = new HashSet<String>();
		for(String prop:turbovegToAnArchive.values()){
			if(GestoreAttributoAnArchive.isStrato(prop)){
				stratiDiAnArchive.add(prop);
			}
		}
		
		finestraPasso5 = new DialogoGestioneStrati(stratiDaTurboveg, stratiDiAnArchive, barraAvanzamento);
		sistemaDialogo(finestraPasso5);
		is.patternStrati = finestraPasso5.getModelloStratificazioneApplicato();
		is.corrispondenzaStrati = finestraPasso5.getAssociazioni();
		return is;
	}
	
	/************************************************************************
	 * @param handlerSAXPerTurboveg
	 * @throws SAXException
	 * @throws IOException
	 ***********************************************************************/
	static void fase_elaborazioneAttributi(String nomeFile, SHTveg handlerSAXPerTurboveg) throws SAXException, IOException{
		DialogoElaborazioneAttributi dea = new DialogoElaborazioneAttributi(handlerSAXPerTurboveg, barraAvanzamento, nomeFile);
		File fileAzioniAttributi = CommonTasks.getFileProprieta(nomeFile, "azioni");
		if(fileAzioniAttributi.exists()){
			ArrayList<AzioneSuAttributi> elenco = new ArrayList<AzioneSuAttributi>();
			FileReader fr = new FileReader(fileAzioniAttributi);
			BufferedReader br = new BufferedReader(fr);
			String linea;
			while((linea=br.readLine())!=null){
				if(!linea.startsWith("#")){
					elenco.add(new AzioneSuAttributi(linea));
				}
			}
			br.close();
			fr.close();
			dea.setElenco(elenco);
		}
		sistemaDialogo(dea);
		if(fileAzioniAttributi!=null){
			ArrayList<AzioneSuAttributi> elenco = dea.getElenco();
			FileWriter fw = new FileWriter(fileAzioniAttributi);
			for(int i=0; i<elenco.size(); i++){
				fw.write(elenco.get(i).toString());
				fw.write('\n');
			}
			fw.close();
		}
	}
	
	/************************************************************************
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws URISyntaxException 
	 * @throws InterruptedException 
	 * @throws FileNotFoundException 
	 ***********************************************************************/
	static HashMap<String, String> fase_associazioneAttributi(String nomeFile, SHTveg parser) throws IOException, SAXException, InterruptedException, URISyntaxException{
		DialogoAssociazioneAttributi finestraPasso3;			// associazione attributi turboveg -> anarchive  
		
		File fileAssociazioni = CommonTasks.getFileProprieta(nomeFile, "corrispondenze");
		Properties associazioniSalvate = new Properties();
		if(fileAssociazioni.exists()){
			try{
				associazioniSalvate.load(new FileInputStream(fileAssociazioni));
			}catch(FileNotFoundException x){
				; // do nothing, la prima volta il file non esiste
			}
		}
		VegImport.associazioniSalvateSuFile = associazioniSalvate;
		finestraPasso3 = new DialogoAssociazioneAttributi(parser, barraAvanzamento);
		sistemaDialogo(finestraPasso3);
		HashMap<String, String> associazioniTotali = finestraPasso3.getAssociazioni();
		if(fileAssociazioni!=null){
			// devo inserire nelle associazioni soltanto quelle che hanno un valore
			// comprese quelle presenti nei default perché potrebbero essere state sovrascritte
			// e quelle vuote perché potrebbe essere che alcune vengono cancellate
			for(String k: associazioniTotali.keySet()){
				associazioniSalvate.put(k, associazioniTotali.get(k));
			}
			associazioniSalvate.store(new FileOutputStream(fileAssociazioni),"associazioni delle proprietà per VegImport");
		}
		return associazioniTotali;
	}
	
	/************************************************************************
	 * @param turbovegToAnArchive
	 * @throws Exception 
	 ***********************************************************************/
	static void fase_inserimentoAttributiNeiRilievi(ArrayList<SampleWrapper> rilieviLetti, HashMap<String, String> turbovegToAnArchive, String patternStrati) throws Exception{	
		for(SampleWrapper rilievo: rilieviLetti){
			// creo gli strati
			// FIXME: il metodo sotto ritorna una stringa con il messaggio di errore
			GestoreAttributoAnArchive.impostaAttributiRilievo(rilievo, turbovegToAnArchive, patternStrati);
		}
	}
	
	/************************************************************************
	 * Chiede al server di validare o approssimare i singoli nomi
	 * @throws IOException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws ClassCastException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 ***********************************************************************/
	static void fase_nomiSpecie(String nomeFile, Hashtable<String, SpecieDaConvertire> specieRilevate) throws IOException, ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException, SAXException, ParserConfigurationException{
		DialogoSpecie finestraPasso4;							// correzione dei nomi di specie
		ArrayList<SpecieDaConvertire> elecoSpecieTurboveg;  	// utilizzato per ordinare i nomi
		Enumeration<SpecieDaConvertire> enuSpecieTurboveg;		// da inserire in elencoSpecieTurboveg
		QualifiedName nomeConLivelloDiApprossimazione;
		String nomeSpecieTurboveg;
		AttesaIndefinita ai = new AttesaIndefinita("Verifica dei nomi di specie...");
		SimpleBotanicalData risposta = null;
		
		UtilitaGui.centraDialogoAlloSchermo(ai, UtilitaGui.CENTRO);
		ai.setVisible(true);
		enuSpecieTurboveg = specieRilevate.elements();
		elecoSpecieTurboveg = new ArrayList<SpecieDaConvertire>();
		while( enuSpecieTurboveg.hasMoreElements() ){
			elecoSpecieTurboveg.add(enuSpecieTurboveg.nextElement());
		}
		tabellaCorrispondenzaNomi = new DefaultTableModel(elecoSpecieTurboveg.size(), 3);
		Collections.sort(elecoSpecieTurboveg);
		for(int i=0; i<elecoSpecieTurboveg.size(); i++){
			tabellaCorrispondenzaNomi.setValueAt(elecoSpecieTurboveg.get(i).numero, i, 0);
			tabellaCorrispondenzaNomi.setValueAt(elecoSpecieTurboveg.get(i).nomeTurboveg, i, 1);
		}
		File fileNomiSpecificati = CommonTasks.getFileProprieta(nomeFile, "specie");
		File filePerDump = CommonTasks.getFileSuffisso(nomeFile, "_dump_specie.html");
		Properties associazioniSalvate = new Properties();
		HashMap<String, SurveyedSpecie> nomiGiaControllati = new HashMap<String, SurveyedSpecie>();
		if(fileNomiSpecificati.exists()){
			SAXParser parserSAX;
			SHSurveyedSpecie handler = new SHSurveyedSpecie(Util.SBD_VERSION);
			InputSource is;
			
            SAXParserFactory fpf = SAXParserFactory.newInstance();
            fpf.setValidating(false);
            parserSAX = fpf.newSAXParser();
			try{
				associazioniSalvate.load(new FileInputStream(fileNomiSpecificati));
				for(Object nome: associazioniSalvate.keySet()){
					handler = new SHSurveyedSpecie(Util.SBD_VERSION);
					is = new InputSource(new StringReader(associazioniSalvate.getProperty((String) nome)));
			        parserSAX.parse(is, handler);
			        nomiGiaControllati.put((String)nome, handler.getSurveyedSpecie());
				}
			}catch(FileNotFoundException x){
				; // do nothing, la prima volta il file non esiste
			}
		}
		Object ooo;
		OggettoConLivello ol;
		for(int i=0; i<tabellaCorrispondenzaNomi.getRowCount(); i++){
			nomeSpecieTurboveg = (String) tabellaCorrispondenzaNomi.getValueAt(i, 1);
			Stato.debugLog.finest("nome: "+nomeSpecieTurboveg);
			try {
				risposta = Stato.comunicatore.approssimaNome(nomeSpecieTurboveg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(risposta!=null && risposta.getNameListSize()>0 && risposta.getNameList(0).size()>0){
				nomeConLivelloDiApprossimazione = risposta.getNameList(0).getQualifiedName(0);
				tabellaCorrispondenzaNomi.setValueAt( 
						new OggettoConLivello(
								CostruttoreOggetti.createSurveyedSpecie(nomeConLivelloDiApprossimazione.getName(), true, "sure"),
								TabImport.mappaturaLivelli.get(nomeConLivelloDiApprossimazione.getQualification())
						), 
						i, 
						2
				);
			}else{
				tabellaCorrispondenzaNomi.setValueAt(new OggettoConLivello("???", java.util.logging.Level.SEVERE), i, 2);
			}
			ooo = nomiGiaControllati.get(tabellaCorrispondenzaNomi.getValueAt(i, 1));
			if(ooo!=null){
				ol = new OggettoConLivello(ooo, java.util.logging.Level.SEVERE);
				ol.setEditatoManualmente(true);
				tabellaCorrispondenzaNomi.setValueAt(ol, i, 2);
			}
		}
		ai.setVisible(false);
		finestraPasso4 = new DialogoSpecie(tabellaCorrispondenzaNomi, barraAvanzamento, filePerDump);
		sistemaDialogo(finestraPasso4);
		// vedo quali nomi l'utente ha modificato
		if(fileNomiSpecificati!=null){
			OggettoConLivello corrispondenza;
			SurveyedSpecie latoAnArchive;
			String latoTurboveg;
			
			Properties associazioniDaSalvare = new Properties();
			for(int i=0; i<tabellaCorrispondenzaNomi.getRowCount(); i++){
				corrispondenza = (OggettoConLivello) tabellaCorrispondenzaNomi.getValueAt(i, 2);
				if(corrispondenza.isEditatoManualmente()){
					latoTurboveg = (String) tabellaCorrispondenzaNomi.getValueAt(i, 1);
					latoAnArchive = (SurveyedSpecie) corrispondenza.getOggetto();
					associazioniDaSalvare.put(latoTurboveg, latoAnArchive.toXMLString(false));
				}
			}
			associazioniDaSalvare.store(new FileOutputStream(fileNomiSpecificati),"associazioni delle proprietà per VegImport");
		}
	}
	
	/************************************************************************
	 * @throws ValoreException
	 ***********************************************************************/
	static void fase_inserimentoSpecieNegliStrati(ArrayList<SampleWrapper> rilieviLetti, Hashtable<String, String> corrispondenzaStrati) throws ValoreException{
		SampleWrapper rilievoInElaborazione;
		String stratoAnArchive;
		SurveyedSpecie specieRilevata;
		
		for(int i=0; i<rilieviLetti.size();i++){
			rilievoInElaborazione = rilieviLetti.get(i);
			// inserisco le specie negli strati
			for(int j=0 ; j<rilievoInElaborazione.specieData.size(); j++){
				stratoAnArchive = corrispondenzaStrati.get(rilievoInElaborazione.specieData.get(j).strato);
				specieRilevata = CostruttoreOggetti.createSurveyedSpecie(
						getNomeSpecie(rilievoInElaborazione.specieData.get(j).id), true, "sure");
				specieRilevata.setAbundance(rilievoInElaborazione.specieData.get(j).abbondanza);
				getLevel(rilievoInElaborazione,stratoAnArchive).addSurveyedSpecie(specieRilevata);
			}
		}
	}
	
	/************************************************************************
	 * @param s il rilievo in cui cercare
	 * @param id l'id del rilievo per turboveg
	 * @return lo strato associato
	 * @throws ValoreException se non trova lo strato
	 ***********************************************************************/
	private static Level getLevel(Sample s, String id) throws ValoreException{
		for(int i=0; i<s.getCell().getLevelCount() ; i++){
			if(s.getCell().getLevel(i).getId().equals(id)){
				return s.getCell().getLevel(i);
			}
		}
		throw new ValoreException("Strato non trovato: \""+id+"\"");
	}

	/************************************************************************
	 * @param numeroTurboveg il numero della specie usato da turboveg
	 * @return il nome della specie
	 * @throws ValoreException
	 ***********************************************************************/
	private static String getNomeSpecie(String numeroTurboveg) throws ValoreException{
		OggettoConLivello oggetto;
		for(int i=0 ; i<tabellaCorrispondenzaNomi.getRowCount(); i++){
			if(tabellaCorrispondenzaNomi.getValueAt(i, 0).equals(numeroTurboveg)){
				oggetto = (OggettoConLivello) tabellaCorrispondenzaNomi.getValueAt(i, 2);
				if(oggetto.getOggetto()!= null && oggetto.getOggetto() instanceof SurveyedSpecie){
					return ((SurveyedSpecie) oggetto.getOggetto()).getSpecieRefName();
				}else{
					return oggetto.toString();
				}
			}
		}
		throw new ValoreException("Id specie non trovato: "+numeroTurboveg);
	}
	
	/************************************************************************
	 * Scrive il rilievo in un editor e poi lo rilegge, così facendo elimina 
	 * gli strati che non contengono informazioni utili
	 * @throws Exception 
	 ***********************************************************************/
	private static Sample[] eliminaStratiInutili(ArrayList<SampleWrapper> rilieviLetti) throws Exception{
		Sample s;
		Sample elencoRilieviDaInviare[] = new Sample[rilieviLetti.size()];
		for(int i=0; i<rilieviLetti.size();i++){
			s = rilieviLetti.get(i).getSample();
			SampleEditorDaEstendere.rimuoviStratiInutili(s);
			elencoRilieviDaInviare[i] = s;
		}
		return elencoRilieviDaInviare;
	}
	
	private static void sistemaDialogo(JDialog x){
		x.setModal(true);
		x.setSize(700,400);
		x.validate();
		UtilitaGui.centraDialogoAlloSchermo(x, UtilitaGui.CENTRO);
		x.setVisible(true);
	}
	
	public static final HashSet<String> elencaAttributi(ArrayList<SampleWrapper> rilievi){
		HashSet<String> insieme = new HashSet<String>();
		for(SampleWrapper s: rilievi){
			for(String x: s.proprieta.keySet()){
				insieme.add(x);
			}
		}
		return insieme;
	}

	public static Properties associazioniSalvateSuFile;
	
}
