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
package it.aspix.archiver.assistenti.vegimport.attributi;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.assistenti.SampleWrapper;
import it.aspix.archiver.assistenti.vegimport.VegImport;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.xml.sax.SAXException;

/****************************************************************************
 * Permette di splittare un campo in due
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class PannelloSeparazioneAttributi extends JPanel{
	
	private static final long serialVersionUID = 1L;
	public String NOME = "separazione";

	DefaultListModel contenuto = new DefaultListModel();
	JList lista = new JList(contenuto);
	JTextArea esempio = new JTextArea();
	DefaultComboBoxModel contenutoPattern = new DefaultComboBoxModel();
	JComboBox regexp = new JComboBox(contenutoPattern);
	JTextField nome1 = new JTextField();
	JTextField nome2 = new JTextField();
	
	ArrayList<SampleWrapper> elencoRilievi;
	DialogoElaborazioneAttributi master;
	
	public PannelloSeparazioneAttributi(ArrayList<SampleWrapper> elencoRilievi, DialogoElaborazioneAttributi master) throws SAXException, IOException{
		this.elencoRilievi = elencoRilievi;
		this.master = master;
		JLabel e1 = new JLabel("nome campo 1:");
		JLabel e2 = new JLabel("nome campo 2:");
		JScrollPane sLista = new JScrollPane(lista);
		JScrollPane sEsempio = new JScrollPane(esempio);
		JLabel eRegexp = new JLabel("espressione regolare (deve contenere due gruppi):");
		JButton esegui = new JButton("esegui");
		JPanel pPrincipale = new JPanel(new GridBagLayout());
		
		pPrincipale.add(sLista, 	new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsEtichetta, 0, 0));
		pPrincipale.add(sEsempio, 	new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsEtichetta, 0, 0));
		pPrincipale.add(eRegexp, 	new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, CostantiGUI.insetsEtichetta, 0, 0));
		pPrincipale.add(regexp, 	new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		pPrincipale.add(e1, 		new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, CostantiGUI.insetsEtichetta, 0, 0));
		pPrincipale.add(nome1, 		new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		pPrincipale.add(e2, 		new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, CostantiGUI.insetsEtichetta, 0, 0));
		pPrincipale.add(nome2, 		new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		pPrincipale.add(esegui, 	new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzione, 0, 0));
		
		this.setLayout(new BorderLayout());
		this.add(pPrincipale,BorderLayout.CENTER);
		generaElenchiAttributi();
		aggiornaEsempio((String) lista.getSelectedValue(), esempio);
		
		regexp.setEditable(true);
		
		lista.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				selezioneAssociazioneCambiata(lista,esempio);
			}
		});
		esegui.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				azioneEsegui();
			}
		});
		contenutoPattern.addElement("");
		contenutoPattern.addElement("(.*) \\((.*)\\)");
	}

	protected void generaElenchiAttributi(){
		ArrayList<String> temp = new ArrayList<String>();
		HashSet<String> attributi = VegImport.elencaAttributi(elencoRilievi);
		for(String y: attributi){
			temp.add(y);
		}
		Collections.sort(temp);
		// impostazioni iniziali per la lista
		contenuto.removeAllElements();
		for(String y: temp){
			contenuto.addElement(y);
		}
		lista.setSelectedIndex(0);
	}
	
	private void selezioneAssociazioneCambiata(JList lista, JTextArea esempio){
		String voceSelezionata = (String) lista.getSelectedValue();
		aggiornaEsempio(voceSelezionata,esempio);
	}

	private void azioneEsegui(){
		String pattern = (String) regexp.getSelectedItem();
		String campoOriginale = (String) lista.getSelectedValue();
		String campo1 = nome1.getText();
		String campo2 = nome2.getText();
		AzioneSuAttributi asa = new AzioneSuAttributi(NOME, pattern, campoOriginale, campo1, campo2);
		eseguiAzione(asa);
		master.aggiungiAzione(asa);
	}
	
	/************************************************************************
	 * @param asa l'azione da eseguire
	 ***********************************************************************/
	public void eseguiAzione(AzioneSuAttributi asa){
		if(!asa.esecutore.equals(NOME)){
			return;
		}
		
		String pattern = asa.param1;
		String campoOriginale = asa.param2;
		String campo1 = asa.param3;
		String campo2 = asa.param4;
		String testoOriginale;
		String v1,v2;
		
		Pattern patternCompilato = Pattern.compile(pattern);
		Matcher combaciatore;
		for(SampleWrapper sw: elencoRilievi){
			testoOriginale = sw.proprieta.get(campoOriginale);
			combaciatore = patternCompilato.matcher(testoOriginale);
			if(combaciatore.matches()){
				v1 = combaciatore.group(1).trim();
				v2 = combaciatore.group(2).trim();
				sw.proprieta.put(campo1,v1);
				sw.proprieta.put(campo2,v2);
				sw.proprieta.remove(campoOriginale);
			}else{
				System.out.println("   NO MATCH: "+testoOriginale);
			}
		}
		// generaElenchiAttributi(); non serve più lo chiama il master
		master.elencoCambiato();
	}

	/************************************************************************
	 * Mostra nella casella degli esempi i valori assunti dall'attributo
	 * selezionato
	 * @param voce da prendere in considerazione 
	 ***********************************************************************/
	private void aggiornaEsempio(String nomeProprieta, JTextArea esempio){
		StringBuffer sb = new StringBuffer();
		for(SampleWrapper sw: elencoRilievi){
			if(sw.proprieta.containsKey(nomeProprieta)){
				sb.append(sw.proprieta.get(nomeProprieta));
				sb.append('\n');
			}
		}
		esempio.setText(sb.toString());
	}
	
}
