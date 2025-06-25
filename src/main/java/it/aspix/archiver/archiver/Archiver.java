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
package it.aspix.archiver.archiver;

import java.awt.Color;
import java.awt.Image;
import java.awt.Taskbar;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import it.aspix.archiver.Icone;
import it.aspix.archiver.Utilita;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.dialoghi.AperturaApplicazione;
import it.aspix.archiver.dialoghi.ComunicazioneAggiornamenti;
import it.aspix.archiver.dialoghi.PreferenzeFondamentali;
import it.aspix.archiver.nucleo.Comunicatore;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;

/*****************************************************************************
 * La classe che va lanciata quando l'utente fa click sul jar.
 * si occupa dell'inizializzazione di tutta l'applicazione
 * @author Edoardo Panfili, studio Aspix
 ****************************************************************************/
public class Archiver {
    public static void main(String[] args) {
        UIManager.put("TabbedPane.contentOpaque",false);

		Stato.debugLog.finest("Avvio Archiver");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name","archiver");
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        try {
	        Taskbar tb = Taskbar.getTaskbar();
	        InputStream is = Archiver.class.getResourceAsStream("/it/aspix/archiver/icone/anArchive_doc.png");
	        Image i = ImageIO.read( is );
	        tb.setIconImage(i);
        }catch(IOException | IllegalArgumentException e) {
        	// do nothing
        	e.printStackTrace();
        }

        AperturaApplicazione attesaApertura = new AperturaApplicazione(
            Icone.splash,
            "Versione "+Stato.versione+" http://www.anarchive.it",
            Color.WHITE,
            5
        );
        attesaApertura.setVisible(true);
        // recupera le proprieta
        attesaApertura.setAvanzamento("Recupero le preferenze...",1);
        Proprieta.caricaProprieta();
        if(Proprieta.proprietaMinimePresenti()!=null){
            	PreferenzeFondamentali pf = new PreferenzeFondamentali();
            	UtilitaGui.centraDialogoAlloSchermo(pf,UtilitaGui.CENTRO);
        		pf.setVisible(true);
        }
        // richiesta della password
        attesaApertura.setAvanzamento("Richiesta password...",2);
        // FIXME: commentate due righe e aggiunte quelle sotto per semplificare i test iniziali
        attesaApertura.chiediPassword();
        attesaApertura.aggiornaConnessione();

        Proprieta.check();

        Comunicatore comunicatore=new Comunicatore("archiver", Stato.versione);
        Stato.comunicatore = comunicatore;
        // controllo i diritti di accesso

        attesaApertura.setAvanzamento("Chiedo verifica diritti di accesso...",3);
        boolean esito;
        String messaggioErrore = "Utente non riconosciuto";
        try{
            esito = comunicatore.login();
        }catch(Exception ex){
            // costruisco un messaggio fittizio da accodare, serve se ad esempio non c'è risposta
            messaggioErrore = "Eccezione nella comunicazione: "+Utilita.getSpiegazione(ex);
            esito = false;
        }
        if(! esito){
            UtilitaGui.mostraMessaggioAndandoACapo(messaggioErrore, "errore", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        //apre la finestra degli aggiornamenti (che si apre solo se serve)
        ComunicazioneAggiornamenti da = new ComunicazioneAggiornamenti(Stato.versione);
        da.setVisible(true);
        //apre la finestra principale
        attesaApertura.setAvanzamento("Creo la finestra principale...",4);

        // imposto l'aspetto dell'interfaccia
        String nomeLaF = Proprieta.recupera("generale.aspettoInterfaccia");
        if(!nomeLaF.equals("nativo")){
        	try {
        	    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        	        if (nomeLaF.equals("metal") && "Metal".equals(info.getName())) {
        	            UIManager.setLookAndFeel(info.getClassName());
        	            break;
        	        }
        	        if (nomeLaF.equals("nimbus") && "Nimbus".equals(info.getName())) {
        	            UIManager.setLookAndFeel(info.getClassName());
        	            break;
        	        }
        	    }
        	} catch (Exception e) {
        	    e.printStackTrace();
        	}
        }

        JFrame fv = new FinestraArchiver();

        attesaApertura.setVisible(false);
        attesaApertura.dispose();
        attesaApertura.setAvanzamento("Avvio completato",5);
        fv.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                try {
                    Proprieta.salvaProprieta();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                System.exit(0);
            }
        });
        UtilitaGui.centraDialogoAlloSchermo(fv, UtilitaGui.CENTRO);
        fv.setVisible(true);
    }
}
