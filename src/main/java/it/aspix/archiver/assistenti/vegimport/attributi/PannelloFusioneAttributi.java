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
import it.aspix.archiver.nucleo.Dispatcher;

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
 * Permettere di fondere due attributi
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class PannelloFusioneAttributi extends JPanel{
	
	private static final long serialVersionUID = 1L;
	public String NOME = "fusione";
	
	DefaultListModel contenuto = new DefaultListModel();
	JList lista = new JList(contenuto);
	JTextArea esempio = new JTextArea();
	JTextField nuovoNome = new JTextField();
	DefaultComboBoxModel contenutoPattern = new DefaultComboBoxModel();
	JComboBox comboPattern = new JComboBox(contenutoPattern);
	
	ArrayList<SampleWrapper> elencoRilievi;
	DialogoElaborazioneAttributi master;
	
	public PannelloFusioneAttributi(ArrayList<SampleWrapper> elencoRilievi, DialogoElaborazioneAttributi master) throws SAXException, IOException{
		this.elencoRilievi = elencoRilievi;
		this.master = master;
		
		JScrollPane sLista = new JScrollPane(lista);
		JScrollPane sEsempio = new JScrollPane(esempio);
		JLabel eNuovoNome = new JLabel("nuovo nome:");
		JLabel ePattern = new JLabel("patern:");
		JButton esegui = new JButton("esegui");
		JPanel pPrincipale = new JPanel(new GridBagLayout());
		
		pPrincipale.add(sLista, 		new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsEtichetta, 0, 0));
		pPrincipale.add(sEsempio, 		new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsEtichetta, 0, 0));
		pPrincipale.add(eNuovoNome, 	new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, CostantiGUI.insetsEtichetta, 0, 0));
		pPrincipale.add(nuovoNome, 		new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsEtichetta, 0, 0));
		pPrincipale.add(ePattern,       new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, CostantiGUI.insetsDatoTesto, 0, 0));
		pPrincipale.add(comboPattern, 	new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		pPrincipale.add(esegui, 		new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzione, 0, 0));
		
		this.setLayout(new BorderLayout());
		this.add(pPrincipale);
		generaElenchiAttributi();
		aggiornaEsempio((String) lista.getSelectedValue(), esempio);
		
		comboPattern.setEditable(true);
		
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
		contenutoPattern.addElement("AS:{campo_1} CL:{campo_2}");
	}

	protected void  generaElenchiAttributi(){
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
		String nn = nuovoNome.getText();
		String pattern = (String) comboPattern.getSelectedItem();
		AzioneSuAttributi asa = new AzioneSuAttributi(NOME, nn, pattern, null, null);
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
		
		Pattern p = Pattern.compile("^(.*)\\{(.*)\\}(.*)\\{(.*)\\}(.*)$");
		Matcher m = p.matcher(asa.param2);
		if(contenuto.contains(asa.param1)){
			Dispatcher.consegna(this, new Exception("Il campo "+asa.param1+" esiste già."));
		}else{
			if(m.matches()){
				String partePrima = m.group(1);
				String campo1 = m.group(2);
				String parteCentro = m.group(3);
				String campo2 = m.group(4);
				String parteDopo = m.group(5);
				String v1,v2;
				// TODO: metti nel manuale che i campi vengono comunque cancellati
				for(SampleWrapper sw: elencoRilievi){
					v1 = sw.proprieta.get(campo1)!=null ? sw.proprieta.get(campo1) : "";
					v2 = sw.proprieta.get(campo2)!=null ? sw.proprieta.get(campo2) : "";
					sw.proprieta.put(asa.param1, partePrima+v1+parteCentro+v2+parteDopo);
					sw.proprieta.remove(campo1);
					sw.proprieta.remove(campo2);
				}
			}else{
				Dispatcher.consegna(this, new Exception("Pattern scorretto: "+asa.param2));
			}
		}
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
