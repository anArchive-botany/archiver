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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.xml.sax.SAXException;

import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Message;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.Sample;
import it.aspix.sbd.obj.SimpleBotanicalData;

public class CommonTasks {
	
	public static Message esitoInvioRilievo[] = null;
	
	/************************************************************************
	 * Le modifiche vengono effettuate direttamente nei rilievi
	 * @param elencoRilieviDaInviare
	 * @return gli stessi rilievi passati
	 ***********************************************************************/
	public static Sample[] fase_revisionePrimaDellInvio(Sample elencoRilieviDaInviare[], String scalaAbbondanza, BarraAvanzamentoWizard ba){
		DialogoIspezioneRilievi finestraPasso6;
		finestraPasso6 = new DialogoIspezioneRilievi(elencoRilieviDaInviare, scalaAbbondanza, null, ba);
		UtilitaGui.centraDialogoAlloSchermo(finestraPasso6, UtilitaGui.CENTRO);
		finestraPasso6.setVisible(true);
		return elencoRilieviDaInviare;
	}	

	/************************************************************************
	 * @param elencoRilieviDaInviare
	 * @throws Exception 
	 ***********************************************************************/
	public static void fase_invioInSimulazione(Sample[] elencoRilieviDaInviare, String scalaAbbondanza, BarraAvanzamentoWizard ba) throws Exception{
		DialogoIspezioneRilievi finestraPasso6;
		while(!invioInSimulazione(elencoRilieviDaInviare)){
			finestraPasso6 = new DialogoIspezioneRilievi(elencoRilieviDaInviare, scalaAbbondanza, esitoInvioRilievo, ba);
			UtilitaGui.centraDialogoAlloSchermo(finestraPasso6, UtilitaGui.CENTRO);
			finestraPasso6.setVisible(true);
		}
	}
	
	/************************************************************************
	 * @param daInviare elenco di rilievi da inviare
	 * @return true se tutte le simulazioni sono andate abuon fine
	 * @throws Exception
	 ***********************************************************************/
	private static boolean invioInSimulazione(Sample[] daInviare) throws Exception{
		esitoInvioRilievo = new Message[daInviare.length];
		SimpleBotanicalData risposta;
		boolean tuttoOk = true;
		
		AttesaIndefinita ai = new AttesaIndefinita("Chiedo al server di validare i singoli rilievi...");
		UtilitaGui.centraDialogoAlloSchermo(ai, UtilitaGui.CENTRO);
		ai.setVisible(true);
		for(int n=0; n<daInviare.length ; n++){
			Sample x = daInviare[n]; 
			risposta = Stato.comunicatore.inserisci(x, null, true);
			esitoInvioRilievo[n] = risposta.getMessage(0);			
			tuttoOk &= esitoInvioRilievo[n].getType()!=MessageType.ERROR;
		}
		ai.setVisible(false);
		ai.dispose();
		return tuttoOk;
	}
	
	/************************************************************************
	 * @param elencoRilieviDaInviare
	 * @throws SAXException
	 * @throws IOException
	 * @throws URISyntaxException 
	 * @throws InterruptedException 
	 * @throws IllegalArgumentException 
	 ***********************************************************************/
	public static void fase_invioDati(Sample[] elencoRilieviDaInviare) throws SAXException, IOException, IllegalArgumentException, InterruptedException, URISyntaxException{
		SimpleBotanicalData risposta = null;
		esitoInvioRilievo = new Message[elencoRilieviDaInviare.length];
		AttesaIndefinita ai = new AttesaIndefinita("Invio i dati al server...");
		UtilitaGui.centraDialogoAlloSchermo(ai, UtilitaGui.CENTRO);
		ai.setVisible(true);
		for(int n=0; n<elencoRilieviDaInviare.length ; n++){
			risposta = Stato.comunicatore.inserisci(elencoRilieviDaInviare[n], null, false);
			esitoInvioRilievo[n] = risposta.getMessage(0);
		}
		ai.setVisible(false);
	}
	
	/************************************************************************
	 * @param base il file da usare come partenza
	 * @param nomeParte da appendere al nome
	 * @return un file in cui registrrare preferenze in formato testo
	 ***********************************************************************/
	public static File getFileProprieta(String base, String nomeParte){
		int indicePunto = base.lastIndexOf('.');
		Stato.debugHint = "base=\""+base+"\"";
		return new File(base.substring(0,indicePunto)+".vi_"+nomeParte+".txt");
	}

	/************************************************************************
	 * @param base il file da usare come partenza
	 * @param nome da appendere al nome
	 * @return un file in cui registrrare preferenze in formato testo
	 ***********************************************************************/
	public static File getFileSuffisso(String base, String nome){
		int indicePunto = base.lastIndexOf('.');
		
		return new File(base.substring(0,indicePunto)+nome);
	}
}
