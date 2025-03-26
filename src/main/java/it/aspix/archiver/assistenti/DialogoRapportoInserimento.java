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

import it.aspix.archiver.assistenti.tabimport.TabImport;
import it.aspix.sbd.obj.Message;
import it.aspix.sbd.obj.MessageType;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/****************************************************************************
 * Mostra il risultato dell'invio di una serie di rilievi
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DialogoRapportoInserimento extends JDialog{
	
	private static final long serialVersionUID = 1L;
	
	public DialogoRapportoInserimento(Message esitoInvioRilievo[]){
		this.setTitle(TabImport.NOME_ASSISTENTE+": rapporto inserimento");
		JPanel elenco = new JPanel(new GridLayout(0,1));
		JScrollPane scroll = new JScrollPane(elenco);
		JPanel principale = new JPanel(new BorderLayout());
		JTextArea avviso = new JTextArea("Leggi con attenzione il report.");
		JPanel azioni = new JPanel(new BorderLayout());
		JButton fatto = new JButton("fatto");
		
		azioni.add(fatto, BorderLayout.EAST);
		principale.add(avviso, BorderLayout.NORTH);
		principale.add(scroll, BorderLayout.CENTER);
		principale.add(azioni, BorderLayout.SOUTH);
		this.getContentPane().add(principale);
		
		avviso.setOpaque(false);
		avviso.setEditable(false);
		avviso.setWrapStyleWord(true);
		avviso.setLineWrap(true);
		avviso.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 5));
		
		JLabel messaggio;
		for(int i=0 ; i<esitoInvioRilievo.length ; i++){
			messaggio = new JLabel();
			messaggio.setText(esitoInvioRilievo[i].getText(0).getText());
			if(esitoInvioRilievo[i].getType()!=MessageType.INFO){
				messaggio.setOpaque(false);
				messaggio.setBackground(Color.RED);
			}
			elenco.add(messaggio);
		}
		
		fatto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DialogoRapportoInserimento.this.setVisible(false);
			}
		});
		
		this.setSize(500, 700);
		this.validate();
		this.setModal(true);
	}
}
