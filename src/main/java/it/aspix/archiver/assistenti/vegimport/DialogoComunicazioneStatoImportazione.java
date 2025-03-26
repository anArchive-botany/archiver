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

import it.aspix.archiver.assistenti.BarraAvanzamentoWizard;
import it.aspix.archiver.assistenti.ErrorManager.Errore;

import java.awt.BorderLayout;
import java.util.HashSet;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/****************************************************************************
 * Componente estremamente specializzato per la visualizzazione di 
 * statistiche ed errori di SHTveg
 *  
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DialogoComunicazioneStatoImportazione extends JDialog{

	private static final long serialVersionUID = 1L;

	public DialogoComunicazioneStatoImportazione(SHTveg handler, BarraAvanzamentoWizard ba){
		DefaultListModel<String> contenutoLista = new DefaultListModel<>();
		JList<String> lista = new JList<>(contenutoLista);
		JScrollPane scroll = new JScrollPane(lista);
		JPanel principale = new JPanel(new BorderLayout());
		principale.add(scroll, BorderLayout.CENTER);
		ba.setDialogo(this);
		principale.add(ba, BorderLayout.SOUTH);
		this.getContentPane().add(principale);
		HashSet<String> attributi = VegImport.elencaAttributi(handler.getRilievi());
		
		contenutoLista.addElement("Rilievi: "+handler.getRilievi().size());
		contenutoLista.addElement("Specie: "+handler.getSpecie().size());
		contenutoLista.addElement("Attributi: "+attributi.size());
		contenutoLista.addElement("Dizionari letti: "+handler.getDizionari().size());
		for(String x: handler.getDizionari().keySet()){
			contenutoLista.addElement("    "+x+" "+handler.getDizionari().get(x).size()+" elementi");
		}
		for(Errore e: handler.getErrorManager().getErrori()){
			contenutoLista.addElement(e.toString());
		}
		
		this.setTitle("Stato dell'importazione");
		this.setSize(900,400);
		this.validate();
		this.setModal(true);
	}
}
