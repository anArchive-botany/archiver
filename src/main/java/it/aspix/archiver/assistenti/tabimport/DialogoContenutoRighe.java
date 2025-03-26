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

import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.assistenti.BarraAvanzamentoWizard;
import it.aspix.archiver.assistenti.GestoreAttributoAnArchive;
import it.aspix.archiver.nucleo.Dispatcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import org.xml.sax.SAXException;

public class DialogoContenutoRighe extends JDialog{
	
	private static final long serialVersionUID = 1L;
	DefaultTableModel dati;
	JTable tabella;
	JPopupMenu popup;
	int rigaDaImpostare;
	
	public DialogoContenutoRighe(DefaultTableModel dati, BarraAvanzamentoWizard ba){
		this.setTitle(TabImport.NOME_ASSISTENTE+": contenuto delle righe");
		this.dati = dati;
		
		tabella = new JTable(dati){
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int rowIndex, int colIndex) {
				  return colIndex!=0; //Disallow the editing of first column
			  }
		};
		JScrollPane scroll = new JScrollPane(tabella);
		JPanel principale = new JPanel(new BorderLayout());
		JTextArea avviso = new JTextArea("Fai click con il tasto destro su una riga per impostare il suo contenuto");
		JButton nomiRighe = new JButton("nomi delle righe");
		JPanel pannelloAvviso = new JPanel(new BorderLayout());
		
		pannelloAvviso.add(avviso, BorderLayout.CENTER);
		pannelloAvviso.add(nomiRighe, BorderLayout.EAST);
		principale.add(pannelloAvviso, BorderLayout.NORTH);
		principale.add(scroll, BorderLayout.CENTER);
		ba.setDialogo(this);
		principale.add(ba, BorderLayout.SOUTH);
		this.getContentPane().add(principale);
		
		// costruisciECollegaMenu(tabella, vociMenu);
		
		tabella.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		avviso.setOpaque(false);
		avviso.setEditable(false);
		avviso.setWrapStyleWord(true);
		avviso.setLineWrap(true);
		avviso.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 5));
		tabella.getColumnModel().getColumn(1).setMinWidth(200);
		
		this.setSize(900, 700);
		this.validate();
		this.setModal(true);
		tabella.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int riga = tabella.rowAtPoint(e.getPoint());
				try {
					ImpostazioneAttributo ia = new ImpostazioneAttributo();
					UtilitaGui.posizionaDialogo(tabella, e.getPoint(), ia);
					ia.setVisible(true);
					if(ia.getText()!=null){
						DialogoContenutoRighe.this.dati.setValueAt(ia.getText(),  riga, 0);
					}
					
				} catch (Exception ex) {
					Dispatcher.consegna(DialogoContenutoRighe.this, ex);
				}
			}
			public void mousePressed(MouseEvent arg0) {
				gestisciClick(arg0);
			}
			private void gestisciClick(MouseEvent e){
				
			}
		});
		nomiRighe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ElencoNomiRighe enr = new ElencoNomiRighe();
				enr.setVisible(true);
			}
		});
	}
	
	class ImpostazioneAttributo extends JDialog{
		private static final long serialVersionUID = 1L;
		private GestoreAttributoAnArchive ga;
		private boolean ok = false;
		public ImpostazioneAttributo() throws SAXException, IOException, InterruptedException, URISyntaxException{
			JPanel principale = new JPanel(new BorderLayout());
			JPanel pAzioni = new JPanel(new GridLayout(1,2));
			JButton annulla = new JButton("annulla");
			JButton  ok = new JButton("ok");
			ga = new GestoreAttributoAnArchive(TabImport.tripletteGestiteDaTabImport);
			principale.add(ga, BorderLayout.CENTER);
			principale.add(pAzioni, BorderLayout.SOUTH);
			pAzioni.add(annulla);
			pAzioni.add(ok);
			principale.setBorder(BorderFactory.createLineBorder(Color.RED));
			this.getContentPane().add(principale);
			this.pack();
			this.setModal(true);
			this.setAlwaysOnTop(true);
			annulla.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					ImpostazioneAttributo.this.ok = false;
					ImpostazioneAttributo.this.setVisible(false);
				}
			});
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					ImpostazioneAttributo.this.ok = true;
					ImpostazioneAttributo.this.setVisible(false);
				}
			});
		}
		
		public String getText(){
			if(ok){
				return ga.getText();
			}else{
				return null;
			}
		}
	}
	
	/************************************************************************
	 * Mostra un elenco dei nomi delle righe ammissibili per l'importazione
	 ***********************************************************************/
	private class ElencoNomiRighe extends JDialog{

		private static final long serialVersionUID = 1L;
		
		public ElencoNomiRighe(){
			JPanel principale = new JPanel(new BorderLayout());
			JTextArea elenco = new JTextArea();
			JScrollPane scrollElenco = new JScrollPane(elenco);
			JButton chiudi = new JButton("chiudi");
			this.setTitle("Nomi delle righe");
			this.setModal(true);
			this.setSize(400, 600);
			this.getContentPane().add(principale);
			principale.add(scrollElenco, BorderLayout.CENTER);
			principale.add(chiudi, BorderLayout.SOUTH);
			UtilitaGui.centraDialogoAlloSchermo(this, UtilitaGui.CENTRO);
			
			ArrayList<String> nomi = GestoreAttributoAnArchive.elencoNomiRighe();
			StringBuilder testo = new StringBuilder();
			for(String x: nomi){
				testo.append(x);
				testo.append("\n");
			}
			elenco.setText(testo.toString());
			
			chiudi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					ElencoNomiRighe.this.dispose();
				}
			});
		}
	}
	
}
