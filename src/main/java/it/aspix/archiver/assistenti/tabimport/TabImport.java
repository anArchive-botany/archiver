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
package it.aspix.archiver.assistenti.tabimport;

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
import it.aspix.archiver.assistenti.GestoreAttributoAnArchive;
import it.aspix.archiver.assistenti.GestoreAttributoAnArchive.Tripletta;
import it.aspix.archiver.assistenti.OggettoConLivello;
import it.aspix.archiver.assistenti.SampleWrapper;
import it.aspix.archiver.assistenti.TablesUtils;
import it.aspix.archiver.assistenti.vegimport.DialogoGestioneStrati;
import it.aspix.archiver.componenti.CampoCoordinata;
import it.aspix.archiver.componenti.CampoData;
import it.aspix.archiver.componenti.CampoEsposizione;
import it.aspix.archiver.componenti.CampoInclinazione;
import it.aspix.archiver.dialoghi.AperturaApplicazione;
import it.aspix.archiver.dialoghi.ComunicazioneEccezione;
import it.aspix.archiver.nucleo.Comunicatore;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Message;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.QualifiedName;
import it.aspix.sbd.obj.Sample;
import it.aspix.sbd.obj.SimpleBotanicalData;
import it.aspix.sbd.obj.SpecieSpecification;
import it.aspix.sbd.obj.SurveyedSpecie;
import it.aspix.sbd.scale.sample.GestoreScale;
import it.aspix.sbd.scale.sample.ScalaSample;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

/****************************************************************************
 * La classe principale per l'importatore delle tabelle di vegetazione
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class TabImport {
	
	public static final String NOME_ASSISTENTE = "TabImport";
	public static final String FILE_LICENZA = "/it/aspix/entwash/assistenti/tabimport/licenza.html";
	public static final String URL_MANUALE = "http://www.vegitaly.it/manuale-TabImport.html";
	public static final String SPECIE_ASSENTE = ".";
	
	private static final String CATEGORIA = "CATEGORIA";
	private static final String SPECIE = "SPECIE";	
	
	private static final String CATEGORIA_DESCRIZIONE = "categoria sintassonomica";
	private static final String SPECIE_DESCRIZIONE = "specie";	
	
	static Tripletta tripletteGestiteDaTabImport[] = new Tripletta[]{
			new Tripletta("specie", CATEGORIA, CATEGORIA_DESCRIZIONE),
			new Tripletta("specie", SPECIE, SPECIE_DESCRIZIONE)
	};
	
	/** mappa una risposta del server nell'approssimazione di un nome in un livello di warning */
	// FIXME: serve a vegImport quindi o se ne va da qui o meglio ancora si usa java.util.Leve
	public static HashMap<String, Level> mappaturaLivelli = new HashMap<String, Level>();
	static {
		mappaturaLivelli.put(QualifiedName.QUALIFICATION_EXACT, Level.OFF);
		mappaturaLivelli.put(QualifiedName.QUALIFICATION_IGNORING_AUTHORS, Level.FINE);
		mappaturaLivelli.put(QualifiedName.QUALIFICATION_SMALL_APPROXIMATION, Level.INFO);
		mappaturaLivelli.put(QualifiedName.QUALIFICATION_BIG_APPROXIMATION, Level.WARNING);
	}
	
	private static Level calcolaLivelloDaMessaggi(ArrayList<Message> al){
		// per default tutto bene
		Level risposta;
		MessageType massimo = MessageType.INFO;
		for(Message m: al){
			if(m.getType()!=null){
				massimo = MessageType.max(massimo, m.getType());
			}
		}
		switch(massimo){
		case INFO:
			risposta = Level.OFF; 
			break;
		case WARNING:
			risposta = Level.WARNING;
			break;
		case ERROR:
			risposta = Level.SEVERE;
			break;
		default:
			risposta = Level.SEVERE;
		}
		return risposta;
	}
	
	private static SampleWrapper modelloRilievo; 
	static BarraAvanzamentoWizard barraAvanzamento;
			
	public static void main(String[] args) throws Exception {       
        UIManager.put("TabbedPane.contentOpaque",false);
        UtilitaGui.impostaIcona(Icone.LogoTabImportDoc);
        
        Stato.debugLog.finest("Avvio TabImport");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name","TabImport");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
       
        // come numero di versione uso il timestamp
        AperturaApplicazione attesaApertura = new AperturaApplicazione(
            Icone.splashTabImport,
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
        
        Comunicatore comunicatore=new Comunicatore("TabImport", Stato.versioneTools);
        Stato.comunicatore = comunicatore;
        // controllo i diritti di accesso
        
        attesaApertura.setAvanzamento("Chiedo verifica diritti di accesso...",3);
        if(! comunicatore.login() ){
            UtilitaGui.mostraMessaggioAndandoACapo("In server non ha accettato il login", "Errore nella verifia dei diritti", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
          
        // apre la finestra degli aggiornamenti (che si apre solo se serve)
        // XXX: in TabImport la finestra non la apro
        /* ComunicazioneAggiornamenti da = new ComunicazioneAggiornamenti(Stato.versione);
        da.setVisible(true); */
        //apre la finestra principale
        attesaApertura.setAvanzamento("Creo la finestra principale...",4);
        
        attesaApertura.setVisible(false);
        attesaApertura.dispose();
        attesaApertura.setAvanzamento("Avvio completato",5);
        
        barraAvanzamento = new BarraAvanzamentoWizard(TabImport.NOME_ASSISTENTE, TabImport.FILE_LICENZA, TabImport.URL_MANUALE);
        String nomeFile = null;
        // la riga sotto serve soltanto per inizializzare il gestore
        @SuppressWarnings("unused")
		GestoreAttributoAnArchive gaa = new GestoreAttributoAnArchive(TabImport.tripletteGestiteDaTabImport);
		Sample rilieviCostruiti[] = null;
        int passo = 0;
        DefaultTableModel datiLettiDalFile = null;
        DefaultTableModel datiConRigheMarcate = null;
        DefaultTableModel datiConCampiControllati = null;
        DefaultTableModel datiConSpecieCorrette = null;
        String modelloStratificazione = null;
        try{
        	while(passo<8){
        		if(passo==0){
        			nomeFile = fase_nomeFile();
					passo++;
        		}
        		if(passo==1){
        			datiLettiDalFile = fase_datiStatici(nomeFile);
        			if(barraAvanzamento.isAvanti()){
        				fase_marcaturaAutomatica(datiLettiDalFile);
        				passo++;
        			}else{
        				passo--;
        			}
        		}
        		if(passo==2){
        			datiConRigheMarcate = fase_marcaturaRighe(datiLettiDalFile);
        			if(barraAvanzamento.isAvanti()){
		        		passo++;
        			}else{
        				passo--;
        			}
        		}
        		if(passo==3){
        			datiConCampiControllati= fase_controlloCampi(datiConRigheMarcate);
        			fase_controlloAbbondanze(datiConCampiControllati);
        			passo += ( barraAvanzamento.isAvanti() ? 1 : -1 );
        		}
	        	if(passo==4){
	        		datiConSpecieCorrette = fase_gestioneNomiSpecie(datiConCampiControllati, nomeFile);
	        		fase_puliziaTabella(datiConSpecieCorrette);
	        		passo += ( barraAvanzamento.isAvanti() ? 1 : -1 );
	        	}
	        	if(passo==5){
	        		modelloStratificazione = fase_associazioneStrati(datiConSpecieCorrette);
		        	if(barraAvanzamento.isAvanti()){
			        	rilieviCostruiti = fase_costruzioneRilievi(datiConSpecieCorrette, modelloStratificazione);
			        	passo++;
		        	}else{
		        		passo--;
		        	}
	        	}
	        	if(passo==6){
	        		CommonTasks.fase_revisionePrimaDellInvio(rilieviCostruiti, modelloRilievo.getCell().getAbundanceScale(), barraAvanzamento);
	        		passo += ( barraAvanzamento.isAvanti() ? 1 : -1 );
	        	}
	        	if(passo==7){
		        	CommonTasks.fase_invioInSimulazione(rilieviCostruiti, modelloRilievo.getCell().getAbundanceScale(), barraAvanzamento);
		        	passo += ( barraAvanzamento.isAvanti() ? 1 : -1 );
	        	}
        	}
        	CommonTasks.fase_invioDati(rilieviCostruiti);
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
	
	private static int contaEMarcaErroriAbbondanze(ScalaSample scala, DefaultTableModel dati){
		int contatoreErrori=0;
		String nome;
		String abbondanza;
		Object oggetto;
		OggettoConLivello erroreAbbondanza;
		for(int colonna=2; colonna<dati.getColumnCount(); colonna++){
			for(int riga=0; riga<dati.getRowCount(); riga++){
				nome = (String) dati.getValueAt(riga, 0);
				if(nome!=null && nome.equals(SPECIE_DESCRIZIONE)){
					oggetto = dati.getValueAt(riga, colonna);
					if(oggetto instanceof OggettoConLivello){
						abbondanza = ""+((OggettoConLivello) oggetto);
					}else{
						abbondanza = ""+oggetto; // oggetto potrebbe essere uno String o un numero 
					}
					if(abbondanza.length()>0){
						Stato.debugHint = "scala:"+scala;
						if(!scala.isValid(abbondanza)){
							erroreAbbondanza = new OggettoConLivello(abbondanza, Level.SEVERE);
							dati.setValueAt(erroreAbbondanza, riga, colonna);
							contatoreErrori++;
						}
					}
				}
			}
		}
		return contatoreErrori;
	}
	
	/************************************************************************
	 * 
	 ***********************************************************************/
	public static String fase_nomeFile(){
		AccettabilitaFile af = new AccettabilitaFile() {
			public boolean isAccettabile(File f) {
				String nome = f.toString();
				return nome.endsWith("ods") || nome.endsWith("xls") || nome.endsWith("xlsx");
			}
		};
		DialogoNomeFile y = new DialogoNomeFile("Trascina un file odt, xls o xlsx", af);
		sistemaDialogo(y, false);
		return y.getNomeFile();
	}
	
	/************************************************************************
	 * 
	 ***********************************************************************/
	private static DefaultTableModel fase_datiStatici(String nomeFile){
		File sorgenteDati;
		DialogoDatiStatici passo1;
		boolean esito;
		DefaultTableModel dati = null;
		
		do{
			passo1 = new DialogoDatiStatici(TabImport.NOME_ASSISTENTE+": dati comuni a tutti i rilievi",
					null, barraAvanzamento);
			UtilitaGui.centraDialogoAlloSchermo(passo1, UtilitaGui.CENTRO);
			sistemaDialogo(passo1, false);
			esito = true;
			sorgenteDati = new File(nomeFile);
			if(!sorgenteDati.exists()){
				JOptionPane.showMessageDialog(null, "File inesistete");
				esito = false;
			}
			try{
				dati = TablesUtils.loadTable(sorgenteDati);
				TablesUtils.inserisciColonna(dati, 0, "", "contenuto");
			}catch(Exception ex){
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Il file non è in un formato leggibile.");
				esito = false;
			}
		}while(!esito);
		if(barraAvanzamento.isAvanti()){
			modelloRilievo = passo1.getModelloRilievo();
		}
		return dati;
	}
	
	/************************************************************************
	 * @param dati questo oggetto viene modificato dal metodo
	 ***********************************************************************/
	private static void fase_marcaturaAutomatica(DefaultTableModel dati){
		// cerco la prima riga vuota, al disotto di quella marco tutte le righe non vuote come specie
		int primaVuota;
		String descrizione;
		String trovata;
		for(primaVuota=0; primaVuota<dati.getRowCount(); primaVuota++){
			if(TablesUtils.isVuota(dati, primaVuota)){
				break;
			}else{
				// provo a supporre il contenuto di una riga
				descrizione = (String) dati.getValueAt(primaVuota, 1);
				trovata = GestoreAttributoAnArchive.cercaAttributo(descrizione);
				if(trovata!=null && (dati.getValueAt(primaVuota, 0)==null || ((String)(dati.getValueAt(primaVuota, 0))).length()==0 )){
					dati.setValueAt(trovata, primaVuota, 0);
				}
			}
		}
		for(int i=primaVuota+1; i<dati.getRowCount(); i++){
			if(!TablesUtils.isVuota(dati, i)){
				dati.setValueAt(SPECIE_DESCRIZIONE, i, 0);
			}
		}
	}
	
	/************************************************************************
	 * 
	 ***********************************************************************/
	private static DefaultTableModel fase_marcaturaRighe(final DefaultTableModel d){
		DefaultTableModel dati = clonaDefaultTableModel(d);
		DialogoContenutoRighe passo2 = new DialogoContenutoRighe(dati, barraAvanzamento);
		sistemaDialogo(passo2, true);
		return dati;
	}
	
	/************************************************************************
	 * 
	 ***********************************************************************/
	private static DefaultTableModel fase_controlloCampi(DefaultTableModel d){
		String valore;
		String convertito;
		OggettoConLivello conversioneConLivello;
		String nome;
		CampoCoordinata convertitoreLatitudine = new CampoCoordinata(CampoCoordinata.Asse.LATITUDINE);
		CampoCoordinata convertitoreLongitudine = new CampoCoordinata(CampoCoordinata.Asse.LONGITUDINE);
		CampoData convertitoreData = new CampoData("data");	
		DefaultTableModel dati = clonaDefaultTableModel(d);
		
		for(int riga=0; riga<dati.getRowCount(); riga++){
			nome = (String) dati.getValueAt(riga, 0);
			if(nome!=null){
				// la riga contiene qualcosa di sensato
				if(nome.equals(GestoreAttributoAnArchive.CONTENUTO_ESPOSIZIONE)){
					for(int colonna=2; colonna<dati.getColumnCount(); colonna++){
						valore = ""+ dati.getValueAt(riga, colonna);
						convertito = CampoEsposizione.daCardinaleANumero(valore);
						if(convertito!=null){
							conversioneConLivello = new OggettoConLivello(convertito, Level.WARNING);
							dati.setValueAt(conversioneConLivello, riga, colonna);
						}
						if(valore.endsWith("°")){
							conversioneConLivello = new OggettoConLivello(valore.substring(0, valore.length()-1), Level.WARNING);
							dati.setValueAt(conversioneConLivello, riga, colonna); 
						}
					}
				}
				if(nome.equals(GestoreAttributoAnArchive.CONTENUTO_INCLINAZIONE)){
					for(int colonna=2; colonna<dati.getColumnCount(); colonna++){
						valore = ""+ dati.getValueAt(riga, colonna);
						convertito = CampoInclinazione.daTestoANumero(valore);
						if(convertito!=null){
							conversioneConLivello = new OggettoConLivello(convertito, Level.WARNING);
							dati.setValueAt(conversioneConLivello, riga, colonna);
						}
					}
				}
				if(nome.equals(GestoreAttributoAnArchive.CONTENUTO_LATITUDINE)){
					for(int colonna=2; colonna<dati.getColumnCount(); colonna++){
						valore = ""+ dati.getValueAt(riga, colonna);
						convertitoreLatitudine.setTextNoFormat(valore);
						convertito = convertitoreLatitudine.getText();
						if(convertito!=null){
							conversioneConLivello = new OggettoConLivello(convertito, Level.WARNING);
							dati.setValueAt(conversioneConLivello, riga, colonna);
						}
					}
				}
				if(nome.equals(GestoreAttributoAnArchive.CONTENUTO_LONGITUDINE)){
					for(int colonna=2; colonna<dati.getColumnCount(); colonna++){
						valore = ""+ dati.getValueAt(riga, colonna);
						convertitoreLongitudine.setTextNoFormat(valore);
						convertito = convertitoreLongitudine.getText();
						if(convertito!=null){
							conversioneConLivello = new OggettoConLivello(convertito, Level.WARNING);
							dati.setValueAt(conversioneConLivello, riga, colonna);
						}
					}
				}
				if(nome.equals(GestoreAttributoAnArchive.CONTENUTO_DATA)){
					for(int colonna=2; colonna<dati.getColumnCount(); colonna++){
						valore = ""+ dati.getValueAt(riga, colonna);
						convertitoreData.setTextNoFormat(valore);
						convertito = convertitoreData.getText();
						if(convertito!=null){
							conversioneConLivello = new OggettoConLivello(convertito, Level.WARNING);
							dati.setValueAt(conversioneConLivello, riga, colonna);
						}
					}
				}
			}
		}
		return dati;
	}
	
	/*************************************************************************
	 * @param dati Questa funzione modifica l'oggetto passato
	 ************************************************************************/
	private static void fase_controlloAbbondanze(DefaultTableModel dati){
		ScalaSample scala = GestoreScale.buildForName(modelloRilievo.getCell().getAbundanceScale());
		int contatoreErrori = contaEMarcaErroriAbbondanze(scala, dati);
		while(contatoreErrori!=0){
			DialogoCorrezioneAbbondanze passo2b = new DialogoCorrezioneAbbondanze(dati, contatoreErrori, barraAvanzamento);
			UtilitaGui.centraDialogoAlloSchermo(passo2b, UtilitaGui.CENTRO);
			sistemaDialogo(passo2b, true);
			contatoreErrori = contaEMarcaErroriAbbondanze(scala, dati);
		}
	}

	/************************************************************************
	 * @param d il modello in cui sono contenuti i dati
	 * @param nomeFile (prefiso del) in cui salvare il dump 
	 * @return
	 ***********************************************************************/
	public static DefaultTableModel fase_gestioneNomiSpecie(DefaultTableModel d, String nomeFile){
		SimpleBotanicalData rispostaServer = null;
		DefaultTableModel dati = clonaDefaultTableModel(d);
		
		// --------- inserisco una colonna aggiuntiva con i nomi di specie che verranno utilizzati -----
		TablesUtils.inserisciColonna(dati, 1, "", "nomi originali");
		for(int i=0; i<dati.getRowCount(); i++){
			if(dati.getValueAt(i, 0).equals(SPECIE_DESCRIZIONE)){
				dati.setValueAt(dati.getValueAt(i, 2), i, 1);
			}
		}
		
		// --------------- approssimo i nomi --------------------------------
		AttesaIndefinita ai = new AttesaIndefinita("Verifica dei nomi di specie in corso...");
		UtilitaGui.centraDialogoAlloSchermo(ai, UtilitaGui.CENTRO);
		ai.setVisible(true);
		String nomeSpecieOriginale;
		
		// Inserisco tutti i nomi in un unico vettore
		String[] nomi = new String[dati.getRowCount()];
		for(int i=0; i<dati.getRowCount(); i++){
			if(dati.getValueAt(i, 0).equals(SPECIE_DESCRIZIONE)){
				nomi[i] = (String) dati.getValueAt(i, 1);
			}else{
				nomi[i] = "";
			}
		}
		try {
			rispostaServer = Stato.comunicatore.controllaListaNomiSpecie(nomi);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		for(int i=0; i<dati.getRowCount(); i++){
			if(dati.getValueAt(i, 0).equals(SPECIE_DESCRIZIONE)){
				nomeSpecieOriginale = (String) dati.getValueAt(i, 1);
				Stato.debugLog.finest("nome: > "+nomeSpecieOriginale);
				if(rispostaServer!=null && rispostaServer.getSpecieSpecification(i)!=null && rispostaServer.getSpecieSpecification(i).getGenusName()!=null){
					SpecieSpecification specie = rispostaServer.getSpecieSpecification(i);
					OggettoConLivello ol = new OggettoConLivello();
					ol.setOggetto(CostruttoreOggetti.createSurveyedSpecie(specie.getNome(), true, "sure"));
					for(Message m: specie.getMessage()){
						ol.getMessaggi().add(m);
					}
					ol.setLivello(calcolaLivelloDaMessaggi(ol.getMessaggi()));
					dati.setValueAt(ol, i, 2);
					
				}else{
					dati.setValueAt(new OggettoConLivello("???", Level.SEVERE), i, 2);
				}
			}
		}
		ai.setVisible(false);
		ai.dispose();

		// ------------ chiedo di validare i nomi di specie -----------------
		File filePerDump = CommonTasks.getFileSuffisso(nomeFile, "_dump_specie.html");
		DialogoSpecie passo3 = new DialogoSpecie(dati, barraAvanzamento, filePerDump);
		sistemaDialogo(passo3, true);
		
		return dati;
	}
	
	/************************************************************************
	 * Associa gli strati da turboveg ad anArchive
	 ***********************************************************************/
	static String fase_associazioneStrati(DefaultTableModel dati){
		DialogoGestioneStrati finestraPasso5;					// associazione strati turboveg -> anarchive
		HashSet<String> stratiDiAnArchive = new HashSet<String>();
		// FIXME: un elenco di strati necessari è possibile averlo
		finestraPasso5 = new DialogoGestioneStrati(null, stratiDiAnArchive, barraAvanzamento);
		sistemaDialogo(finestraPasso5, false);
		return finestraPasso5.getModelloStratificazioneApplicato();
	}
	
	/**
	 * @param dati questa funzione modifica l'oggetto passato
	 */
	public static void fase_puliziaTabella(DefaultTableModel dati){
		TablesUtils.eliminaColonna(dati,1); 
	}

	private static Sample[] fase_costruzioneRilievi(DefaultTableModel dati, String modello) throws Exception{
		// costruirò tanti rilievi quante sono le colonne meno 2
		Sample[] daInviare = new Sample[dati.getColumnCount()-2];
		String nome;
		Object valore;
		String note = null;
		SampleWrapper rilievo;
		SurveyedSpecie ss;
		OggettoConLivello ol;
		for(int iColonna=2 ; iColonna<dati.getColumnCount(); iColonna++){
			rilievo = modelloRilievo.getCopia();
			// rilievo.getCell().addLevel(CostruttoreOggetti.createLevel("0", "unico"));
			CostruttoreOggetti.addLevelsByPattern(rilievo.getCell(), modello);
			rilievo.getCell().setModelOfTheLevels("0"); // XXX: modello stratificazione unico, per adesso TabImport non offre altre possibilità
			for(int riga=0 ; riga<dati.getRowCount(); riga++){
				nome = (String) dati.getValueAt(riga, 0);
				valore = dati.getValueAt(riga, iColonna);
				if(nome!=null && !nome.equals("") && !nome.equals(GestoreAttributoAnArchive.NON_UTILIZZARE)){
					Stato.debugLog.finest(nome+":= "+valore);
					if(nome.equals(CATEGORIA_DESCRIZIONE)){
						note = "caratteristico_di: "+dati.getValueAt(riga, 1);
					}else if(nome.equals(SPECIE_DESCRIZIONE)){
						// il nome della specie è nella colonna 1
						ol = (OggettoConLivello) dati.getValueAt(riga, 1);
						if(ol.getOggetto()!=null && (""+valore).length()>0 && !valore.equals(SPECIE_ASSENTE)){
							ss = new SurveyedSpecie( (SurveyedSpecie) (ol).getOggetto());
							ss.setAbundance(""+valore); // valore potrebbe essere un Integer o String
							if(note!=null){
								ss.setNote(note);
							}
							rilievo.getCell().getLevel(0).addSurveyedSpecie(ss);
						}
					}else{
						GestoreAttributoAnArchive.impostaAttributo(rilievo, nome, ""+valore);
					}
				}else{
					note = null;
				}
			}
			daInviare[iColonna-2] = rilievo.getSample();
		}
		return daInviare;
	}
	
	private static ArrayList<Image> iconeFinestra = null;
	
	private static void sistemaDialogo(JDialog x, boolean grande){
		if(iconeFinestra==null){
			iconeFinestra = new ArrayList<Image>();
			for(int dim=16 ; dim<=64 ; dim*=2){
				BufferedImage resizedImage = new BufferedImage(dim, dim, BufferedImage.TYPE_3BYTE_BGR);
				Graphics2D g = resizedImage.createGraphics();
				g.drawImage(Icone.LogoTabImportDoc.getImage(), 0, 0, dim, dim, null);
				g.dispose();
				iconeFinestra.add(Icone.LogoTabImportDoc.getImage());
			}
		}
		int dimX = 700;
		int dimY = 400;
		if(grande){
			// vedo se lo schermo mi permette qualcosa in più
		    Toolkit toolkit =  Toolkit.getDefaultToolkit ();
			Dimension dim = toolkit.getScreenSize();
			if(dim.width-100>dimX){
				dimX = dim.width-100; 
			}
			if(dimX>1400){
				dimX = 1400;
			}
			if(dim.height-100>dimY){
				dimY = dim.height-100; 
			}
			if(dimY>1000){
				dimY = 1000; 
			}
		}
		x.setModal(true);
		x.setSize(dimX, dimY);
		x.validate();
		UtilitaGui.centraDialogoAlloSchermo(x, UtilitaGui.CENTRO);
		x.setVisible(true);
		x.setIconImages(iconeFinestra);
		x.setIconImage(iconeFinestra.get(0));
	}
	
	private static DefaultTableModel clonaDefaultTableModel(DefaultTableModel x){
		DefaultTableModel nuovo = new DefaultTableModel(x.getRowCount(), x.getColumnCount());
		for(int riga=0; riga<x.getRowCount(); riga++){
			for(int colonna=0; colonna<x.getColumnCount(); colonna++){
				nuovo.setValueAt(x.getValueAt(riga, colonna), riga, colonna);
			}
		}
		return nuovo;
	}
	
}
