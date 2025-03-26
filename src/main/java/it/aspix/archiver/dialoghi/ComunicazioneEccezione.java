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
package it.aspix.archiver.dialoghi;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import it.aspix.archiver.Icone;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.nucleo.Stato;

/****************************************************************************
 * Lo scopo di questa classe è quello di comunicare una situazione non
 * recuperabile all'utente.
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class ComunicazioneEccezione extends JDialog{

	private static final long serialVersionUID = 1L;

	public ComunicazioneEccezione(Throwable t){
		this.setTitle("Errore");
		JLabel attenzione = new JLabel("Situazione non recuperabile, invia il testo sottostante ad uno sviluppatore");
		JTextArea stackTrace = new JTextArea();
		JScrollPane scroll = new JScrollPane(stackTrace);
		JButton ok = new JButton("ok");
		JPanel unico = new JPanel(new BorderLayout());
		
		unico.add(attenzione, BorderLayout.NORTH);
		unico.add(scroll, BorderLayout.CENTER);
		unico.add(ok, BorderLayout.SOUTH);
		this.getContentPane().add(unico);
		unico.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		attenzione.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		ok.setOpaque(false);
		attenzione.setIcon(Icone.MessageError);
		this.setSize(650,400);
		this.validate();
		this.setModal(true);
		
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ComunicazioneEccezione.this.setVisible(false);
			}
		});
		
		StringBuilder sb = new StringBuilder();
		String proprieta[]= {"java.vendor","java.vendor.url","java.version","os.name","os.version","os.arch"};
		for(int i=0; i<proprieta.length ; i++){
			sb.append(proprieta[i]+"="+System.getProperty(proprieta[i])+"\n");
		}
	
		// stampo lo stacktrace
		t.printStackTrace(); // anche su console, può tornare utile
		sb.append('\n');
		while(t!=null){
			sb.append(t.getClass().getCanonicalName()+": "+t.getLocalizedMessage()+'\n');
			StackTraceElement[] ste = t.getStackTrace();
			for(int i=0 ; i<ste.length ; i++){
				sb.append(ste[i]);
				sb.append('\n');
			}
			t = t.getCause();
			if(t!=null){
				sb.append("\nCausato da:\n");
			}
		}
		
		sb.append("\ndebug hint: "+Stato.debugHint);
		stackTrace.setText(sb.toString());
		stackTrace.setFont(new Font("Monospaced", stackTrace.getFont().getStyle(), stackTrace.getFont().getSize() ));
		stackTrace.setCaretPosition(0);
		UtilitaGui.centraDialogoAlloSchermo(this, UtilitaGui.CENTRO);
	}
}
