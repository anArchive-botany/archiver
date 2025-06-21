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
package it.aspix.archiver.testmanuali;

import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.dialoghi.ConfermaTemporizzata;
import it.aspix.archiver.dialoghi.DatiNuovoPlot;
import it.aspix.sbd.obj.Message;
import it.aspix.sbd.obj.MessageType;

/****************************************************************************
 * @author Edoarrdo Panfili, studio Aspix
 ***************************************************************************/
public class ProvaDialoghi {
    
    public static void main(String args[]){
    	if(args[0].equals("ConfermaSimulazione")){
            ConfermaTemporizzata cs = new ConfermaTemporizzata(0);
            cs.addMessaggio(CostruttoreOggetti.createMessage("Errore senza attesa!!", "it", MessageType.ERROR));
            UtilitaGui.centraDialogoAlloSchermo(cs, UtilitaGui.LATO_ALTO);
            cs.setVisible(true);
            System.out.println("Selezionato ok:"+cs.isChiusoOk());
            cs.dispose();
            Message[] mv = new Message[3];
            mv[0] = CostruttoreOggetti.createMessage("Errore con attesa!!", "it", MessageType.ERROR);
            mv[1] = CostruttoreOggetti.createMessage("Meglio che ci stai attento se no qualcuno se la potrebbe prendere a male", "it", MessageType.WARNING);
            mv[2] = CostruttoreOggetti.createMessage("Tutto bene, stravolgo il database ma OK! solo che questo messaggio è davvero chilometrico per simulare " +
            		"una risposta del server in cui vengono indicate due specie con tutte le indicazioni del caso, " +
            		"es: Ophrys holoserica (Burm. fil.) Greuter subsp. annae (Devillers-Tersch. et Devillers) H.Baumann, Giotta, Künkele, R. Lorenz et Piccitto", 
            		"it", MessageType.INFO);
            ConfermaTemporizzata cs2 = new ConfermaTemporizzata(4);
            cs2.addMessaggio(mv);
            UtilitaGui.centraDialogoAlloSchermo(cs2, UtilitaGui.CENTRO);
            cs2.setVisible(true);
            System.out.println("Selezionato ok:"+cs2.isChiusoOk());
        }else if(args[0].equals("DatiNuovoPlot")){
            DatiNuovoPlot np = new DatiNuovoPlot();
            np.setVisible(true);
        }else{
            System.err.println("Non conosco "+args[0]);
        }
        System.exit(0);
    }
}
