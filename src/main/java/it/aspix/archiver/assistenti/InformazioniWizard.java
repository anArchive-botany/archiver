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

import it.aspix.archiver.DesktopApiWrapper;
import it.aspix.archiver.Utilita;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.assistenti.tabimport.TabImport;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/****************************************************************************
 * Mostra le informazioni di un assistente, 
 * permette anche di aprire un browser se si fa click su un link
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class InformazioniWizard extends JDialog{

	private static final long serialVersionUID = 1L;
	
	public InformazioniWizard(String nomeAssistente, String fileLicenza, String urlManuale){
		JPanel principale = new JPanel(new BorderLayout());
		JEditorPane avvisi = new JEditorPane();
		JScrollPane scrollAvvisi = new JScrollPane(avvisi);
		JButton ok = new JButton("ok");
		
		this.setTitle(TabImport.NOME_ASSISTENTE+": informazioni");

		principale.add(scrollAvvisi, BorderLayout.CENTER);
		principale.add(ok, BorderLayout.SOUTH);
		
		this.getContentPane().add(principale);
		
		avvisi.setContentType("text/html");
		avvisi.setOpaque(false);
		avvisi.setEditable(false);
		String manuale;
		Runtime runtime = Runtime.getRuntime();
		manuale = Utilita.leggiStringa(fileLicenza, "licenza non trovato");
		manuale = manuale.replace("VERSIONE", Stato.versioneTools);
		manuale = manuale.replace("</body>","<p>manuale: <a href=\""+urlManuale+"\">"+urlManuale+"</a></p></body>");
		manuale = manuale.replace("</body>","<p>java virtual machine: "+System.getProperty("java.version")+" ("+System.getProperty("java.vendor")+")</p></body>");
		manuale = manuale.replace("</body>","<p>memoria disponibile: "+runtime.totalMemory()/1024/1024+"MB</p></body>");
		manuale = manuale.replace("</body>","<p>server: " + Proprieta.recupera("connessione.server")+"</p></body>");
		avvisi.setText(manuale);
		
		this.setSize(600, 350);
		this.setModal(true);
		UtilitaGui.centraDialogoAlloSchermo(this, UtilitaGui.CENTRO);
		
		avvisi.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent evento) {
				javax.swing.event.HyperlinkEvent.EventType tipo = evento.getEventType();
				if(tipo.equals(javax.swing.event.HyperlinkEvent.EventType.ACTIVATED)){
					DesktopApiWrapper.openLink(InformazioniWizard.this, evento.getURL().toString());
				}
			}
		});
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InformazioniWizard.this.setVisible(false);
			}
		});
	}

}
