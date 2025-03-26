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

import it.aspix.archiver.assistenti.BarraAvanzamentoWizard;
import it.aspix.archiver.assistenti.RenderTabellaImportazione;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DialogoCorrezioneAbbondanze extends JDialog{
	
	private static final long serialVersionUID = 1L;
	DefaultTableModel dati;
	JTable tabella;
	JLabel avviso;
	
	public DialogoCorrezioneAbbondanze(DefaultTableModel dati, int numeroDiErrori, BarraAvanzamentoWizard ba){
		this.setTitle(TabImport.NOME_ASSISTENTE+": correzione delle abbondanze");
		this.dati = dati;
		if(numeroDiErrori>1){
			avviso =  new JLabel("Le abbondanze errate sono evidenziate in rosso, ne restano "+numeroDiErrori+" da correggere.");
		}else{
			avviso =  new JLabel("Le abbondanze errate sono evidenziate in rosso, ne resta 1 da correggere.");
		}
		
		tabella = new JTable(dati){
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int rowIndex, int colIndex) {
				  return colIndex!=0 && rowIndex!=0; //Disallow the editing of first column & first row
			  }
		};
		JScrollPane scroll = new JScrollPane(tabella);
		JPanel principale = new JPanel(new BorderLayout());
		
		principale.add(avviso, BorderLayout.NORTH);
		principale.add(scroll, BorderLayout.CENTER);
		ba.setDialogo(this);
		principale.add(ba, BorderLayout.SOUTH);
		this.getContentPane().add(principale);
		
		tabella.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		avviso.setOpaque(false);
		avviso.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 5));
		tabella.getColumnModel().getColumn(1).setMinWidth(200);
		
		tabella.setDefaultRenderer(Object.class, new RenderTabellaImportazione());
		this.setSize(900, 700);
		this.validate();
		this.setModal(true);
	}
}
