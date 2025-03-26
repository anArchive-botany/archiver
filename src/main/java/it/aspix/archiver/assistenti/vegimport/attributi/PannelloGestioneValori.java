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
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.assistenti.DialogoDizionario;
import it.aspix.archiver.assistenti.SampleWrapper;
import it.aspix.archiver.assistenti.vegimport.VegImport;
import it.aspix.archiver.nucleo.Dispatcher;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.xml.sax.SAXException;

/****************************************************************************
 * Permette di cambiare i valori delle proprietà nei singoli rilievi.
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class PannelloGestioneValori extends JPanel{
	
	private static final long serialVersionUID = 1L;
	DefaultListModel contenuto = new DefaultListModel();
	JList lista = new JList(contenuto);
	JTextArea esempio = new JTextArea();
	
	public static String NOME="valori";
	private static String AZIONE_CERCA_E_SOSTITUISCI = "cerca & sostituisci";
	private static String AZIONE_SOSTITUISCI_DIZIONARIO = "usa dizionario";
	private static String AZIONE_MINUSCOLO = "minuscolo";
	private static String AZIONE_INIZIALI_MAIUSCOLE = "Iniziali Maiuscole";
	private static String AZIONE_CALCOLA = "calcola";
	private static String AZIONE_RIBALTA_DATA = "gg-mm-aa";
	private static String AZIONE_SEPARA_DATA = "aaaammgg";
	private static String AZIONE_INSERISCI_TESTO = "inserisci testo";
	
	ArrayList<SampleWrapper> elencoRilievi;
	DialogoElaborazioneAttributi master;
	String fileDati;
	
	public PannelloGestioneValori(ArrayList<SampleWrapper> elencoRilievi, DialogoElaborazioneAttributi master, String fileDati) throws SAXException, IOException{
		this.elencoRilievi = elencoRilievi;
		this.master = master;
		this.fileDati = fileDati;
		
		JPanel pannelloAzioni = new JPanel(new GridLayout(0,4));
		JButton cercaESostituisci = new JButton(AZIONE_CERCA_E_SOSTITUISCI);
		JButton sostituisciDizionario = new JButton(AZIONE_SOSTITUISCI_DIZIONARIO);
		JButton minuscolo = new JButton(AZIONE_MINUSCOLO);
		JButton inizialiMaiuscole = new JButton(AZIONE_INIZIALI_MAIUSCOLE);
		JButton calcola = new JButton(AZIONE_CALCOLA);
		JButton ribaltaData = new JButton(AZIONE_RIBALTA_DATA);
		JButton separaData = new JButton(AZIONE_SEPARA_DATA);
		JButton inserisciTesto = new JButton(AZIONE_INSERISCI_TESTO);
		
		JScrollPane sLista = new JScrollPane(lista);
		JScrollPane sEsempio = new JScrollPane(esempio);
		JPanel pPrincipale = new JPanel(new GridBagLayout());
		
		pPrincipale.add(sLista, 		new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsEtichetta, 0, 0));
		pPrincipale.add(sEsempio, 		new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsEtichetta, 0, 0));
		pPrincipale.add(pannelloAzioni, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsEtichetta, 0, 0));
		
		this.setLayout(new BorderLayout());
		this.add(pPrincipale, BorderLayout.CENTER);
		generaElenchiAttributi();
		aggiornaEsempio((String) lista.getSelectedValue(), esempio);
		
		pannelloAzioni.add(cercaESostituisci);
		pannelloAzioni.add(sostituisciDizionario);
		pannelloAzioni.add(minuscolo);
		pannelloAzioni.add(inizialiMaiuscole);
		pannelloAzioni.add(calcola);
		pannelloAzioni.add(ribaltaData);
		pannelloAzioni.add(separaData);
		pannelloAzioni.add(inserisciTesto);

		this.setLayout(new BorderLayout());
		this.add(pPrincipale, BorderLayout.CENTER);
		
		lista.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				String voceSelezionata = (String) lista.getSelectedValue();
				aggiornaEsempio(voceSelezionata, esempio);
			}
		});
		
		cercaESostituisci.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				azioneCercaESostituisci();
			}
		});
		sostituisciDizionario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				azioneSostituisciDizionario();
			}
		});
		minuscolo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				azioneMinuscolo();
			}
		});
		inizialiMaiuscole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				azioneInizialiMaiuscole();
			}
		});
		calcola.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				azioneCalcola();
			}
		});
		ribaltaData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				azioneRibaltaData();
			}
		});
		separaData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				azioneSeparaData();
			}
		});
		inserisciTesto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				azioneInserisciTesto();
			}
		});
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

	private void azioneCercaESostituisci(){
		String voceSelezionata = (String) lista.getSelectedValue();
		ParametriSostituzione ps = new ParametriSostituzione();
		UtilitaGui.centraDialogoAlloSchermo(ps, UtilitaGui.CENTRO);
		ps.setVisible(true);
		if(ps.eseguiSostituzione){
			AzioneSuAttributi asa = new AzioneSuAttributi(NOME, AZIONE_CERCA_E_SOSTITUISCI, voceSelezionata, ps.getCerca(), ps.getSostituisci());
			eseguiAzione(asa);
			master.aggiungiAzione(asa);
		}
	}
	
	private void azioneSostituisciDizionario(){
		DialogoDizionario dd = new DialogoDizionario();
		UtilitaGui.centraDialogoAlloSchermo(dd, UtilitaGui.CENTRO);
		dd.setVisible(true);
		try{
			String nomeDizioString = dd.getDizionario();
			if(nomeDizioString!=null && nomeDizioString.length()>0){
				String iPrima = dd.getIgnoraPrima();
				String voceSelezionata = (String) lista.getSelectedValue();
				
				AzioneSuAttributi asa = new AzioneSuAttributi(NOME, AZIONE_SOSTITUISCI_DIZIONARIO, voceSelezionata, nomeDizioString, iPrima);
				eseguiAzione(asa);
				master.aggiungiAzione(asa);	
			}
		}catch(Exception e){
			e.printStackTrace();
			Dispatcher.consegna(this, e);
		}
	}
	
	private void azioneMinuscolo(){
		String voceSelezionata = (String) lista.getSelectedValue();
		AzioneSuAttributi asa = new AzioneSuAttributi(NOME, AZIONE_MINUSCOLO, voceSelezionata, null, null);
		eseguiAzione(asa);
		master.aggiungiAzione(asa);
	}
	
	private void azioneInizialiMaiuscole(){
		String voceSelezionata = (String) lista.getSelectedValue();
		AzioneSuAttributi asa = new AzioneSuAttributi(NOME, AZIONE_INIZIALI_MAIUSCOLE, voceSelezionata, null, null);
		eseguiAzione(asa);
		master.aggiungiAzione(asa);
	}
	
	private void azioneCalcola(){
		String voceSelezionata = (String) lista.getSelectedValue();
		RichiestaEspressione finestraEspressione = new RichiestaEspressione();
		UtilitaGui.centraDialogoAlloSchermo(finestraEspressione, UtilitaGui.CENTRO);
		finestraEspressione.setVisible(true);
		String espressione = finestraEspressione.getEspressione();
		if(espressione!=null){
			AzioneSuAttributi asa = new AzioneSuAttributi(NOME, AZIONE_CALCOLA, voceSelezionata, espressione, null);
			eseguiAzione(asa);
			master.aggiungiAzione(asa);
		}
	}
	
	private void azioneRibaltaData(){
		String voceSelezionata = (String) lista.getSelectedValue();
		AzioneSuAttributi asa = new AzioneSuAttributi(NOME, AZIONE_RIBALTA_DATA, voceSelezionata, null, null);
		eseguiAzione(asa);
		master.aggiungiAzione(asa);
	}
	
	private void azioneSeparaData(){
		String voceSelezionata = (String) lista.getSelectedValue();
		AzioneSuAttributi asa = new AzioneSuAttributi(NOME, AZIONE_SEPARA_DATA, voceSelezionata, null, null);
		eseguiAzione(asa);
		master.aggiungiAzione(asa);
	}
	
	public void azioneInserisciTesto(){
		String voceSelezionata = (String) lista.getSelectedValue();
		ParametriInserimento finestraPattern = new ParametriInserimento();
		UtilitaGui.centraDialogoAlloSchermo(finestraPattern, UtilitaGui.CENTRO);
		finestraPattern.setVisible(true);
		String pattern = finestraPattern.getPattern();
		if(pattern!=null){
			AzioneSuAttributi asa = new AzioneSuAttributi(NOME, AZIONE_INSERISCI_TESTO, voceSelezionata, pattern, null);
			eseguiAzione(asa);
			master.aggiungiAzione(asa);
		}
	}
	
	/************************************************************************
	 * @param asa l'azione da eseguire,
	 * 		il primo parametro "param1" è il nome della funzione
	 ***********************************************************************/
	public void eseguiAzione(AzioneSuAttributi asa){
		if(!asa.esecutore.equals(NOME)){
			return;
		}
		
		if(AZIONE_CERCA_E_SOSTITUISCI.equals(asa.param1)){
			String cerca = asa.param3;
			String sostituisciCon = asa.param4; 
			for(SampleWrapper sw: elencoRilievi){
				if(sw.proprieta.containsKey(asa.param2)){
					if( ((String) sw.proprieta.get(asa.param2)).equalsIgnoreCase(cerca) ){ 
						sw.proprieta.put(asa.param2, sostituisciCon==null ? "" : sostituisciCon);
					}
				}
			}
			aggiornaEsempio(asa.param2, esempio);
		}else if(AZIONE_SOSTITUISCI_DIZIONARIO.equals(asa.param1)){
			HashMap<String, String> dizionario;
			try {
				dizionario = DialogoDizionario.buildDizionario(asa.param3, asa.param4, fileDati);
				String sostituisciCon;
				for(String cerca: dizionario.keySet()){
					sostituisciCon = dizionario.get(cerca); 
					for(SampleWrapper sw: elencoRilievi){
						if(sw.proprieta.containsKey(asa.param2)){
							if( ((String) sw.proprieta.get(asa.param2)).equalsIgnoreCase(cerca) ){ 
								sw.proprieta.put(asa.param2, sostituisciCon);
							}
						}
					}
				}
				aggiornaEsempio(asa.param2, esempio);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(AZIONE_MINUSCOLO.equals(asa.param1)){
			for(SampleWrapper sw: elencoRilievi){
				if(sw.proprieta.containsKey(asa.param2)){
					sw.proprieta.put(asa.param2, ((String) sw.proprieta.get(asa.param2)).toLowerCase());
				}
			}
			aggiornaEsempio(asa.param2, esempio);
		}else if(AZIONE_INIZIALI_MAIUSCOLE.equals(asa.param1)){
			char c[];
			
			for(SampleWrapper sw: elencoRilievi){
				if(sw.proprieta.containsKey(asa.param2)){
					c = ((String) sw.proprieta.get(asa.param2)).toLowerCase().toCharArray();
					c[0] = Character.toUpperCase(c[0]);
					for(int i=1; i<c.length ; i++){
						if(c[i-1]==' '){
							c[i] = Character.toUpperCase(c[i]);
						}
					}
					sw.proprieta.put(asa.param2, new String(c));
				}
			}
			aggiornaEsempio(asa.param2, esempio);
		}else if(AZIONE_CALCOLA.equals(asa.param1)){
			// Create or retrieve a JexlEngine
	        JexlEngine jexl = new JexlEngine();
	        // Create an expression object
	        String jexlExp = asa.param3;
	        Expression e = jexl.createExpression( jexlExp );
	        // Create a context and add data
	        JexlContext jc = new MapContext();
	        jc.set("Math", Math.class);
	        Object valutato;
			for(SampleWrapper sw: elencoRilievi){
				if(sw.proprieta.containsKey(asa.param2) && sw.proprieta.get(asa.param2)!=null){
					try{
						jc.set("x", Double.parseDouble( (String) sw.proprieta.get(asa.param2) ) );
						valutato = e.evaluate(jc);
						sw.proprieta.put(asa.param2, valutato.toString());
					}catch(Exception ex){
						// alcuni campi potrebbero contenere dei dati non numeri, nel qual caso non si fa nulla
						ex.printStackTrace();
						
						// Dispatcher.consegna(this, new Exception(ex.getMessage()+" in "+sw));
					}
				}
			}
			aggiornaEsempio(asa.param2, esempio);
		}else if(AZIONE_RIBALTA_DATA.equals(asa.param1)){
			String dataOriginale;
			String pezzi[];
			
			for(SampleWrapper sw: elencoRilievi){
				if(sw.proprieta.containsKey(asa.param2)){
					dataOriginale = ((String) sw.proprieta.get(asa.param2));
					pezzi=dataOriginale.split("[-/]");
					if(pezzi.length==3){
						if(pezzi[0].length()==1){
							pezzi[0] = "0"+pezzi[0];
						}
						if(pezzi[1].length()==1){
							pezzi[1] = "0"+pezzi[1];
						}
						if(pezzi[2].length()!=4){
							if(Integer.parseInt(pezzi[2])>15){
								pezzi[2]="19"+pezzi[2];
							}else{
								pezzi[2]="20"+pezzi[2];
							}
						}
						sw.proprieta.put(asa.param2, pezzi[2]+"-"+pezzi[1]+"-"+pezzi[0]);
					}
				}
			}
			aggiornaEsempio(asa.param2, esempio);
		}else if(AZIONE_SEPARA_DATA.equals(asa.param1)){
			char c[];
			String dataOriginale;
			for(SampleWrapper sw: elencoRilievi){
				if(sw.proprieta.containsKey(asa.param2)){
					dataOriginale = ((String) sw.proprieta.get(asa.param2));
					c = dataOriginale.toCharArray();
					if(c.length==8){
						if(dataOriginale.endsWith("0000")){
							sw.proprieta.put(asa.param2, ""+c[0]+c[1]+c[2]+c[3] );
						}else{
							sw.proprieta.put( asa.param2, ""+c[0]+c[1]+c[2]+c[3]+"-"+c[4]+c[5]+"-"+c[6]+c[7]);
						}
					}
					if(c.length==6){
						sw.proprieta.put(asa.param2, ""+c[0]+c[1]+c[2]+c[3]+"-"+c[4]+c[5]);
					}
				}
			}
			aggiornaEsempio(asa.param2, esempio);
		}else if(AZIONE_INSERISCI_TESTO.equals(asa.param1)){
			String orig;
			String nuova;
			for(SampleWrapper sw: elencoRilievi){
				if(sw.proprieta.containsKey(asa.param2)){
					orig = sw.proprieta.get(asa.param2);
					nuova = asa.param3.replace("$", orig);
					sw.proprieta.put(asa.param2, nuova);
				}
			}
			aggiornaEsempio(asa.param2, esempio);
		}else{
			Dispatcher.consegna(this, new Exception("Non ho eseguito l'azione "+asa.param1));
		}
	}
	
	/************************************************************************
	 * Il dialogo utilizzaper impostare i parametri di cerca e sostituisci
	 * @author Edoardo Panfili, studio Aspix
	 ***********************************************************************/
	private class ParametriSostituzione extends JDialog{
		private static final long serialVersionUID = 1L;
		JTextField cerca = new JTextField();
		JTextField sostituisci = new JTextField();
		boolean eseguiSostituzione = false;
		ParametriSostituzione(){
			JButton annulla = new JButton("annulla");
			JButton ok = new JButton("sostituisci");
			JPanel principale = new JPanel(new GridLayout(3,2));
			principale.add(new JLabel("cerca:"));
			principale.add(cerca);
			principale.add(new JLabel("sostituisci con:"));
			principale.add(sostituisci);
			principale.add(annulla);
			principale.add(ok);
			
			principale.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			this.getContentPane().add(principale);
			this.pack();
			this.setModal(true);
			annulla.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					eseguiSostituzione = false;
					ParametriSostituzione.this.setVisible(false);					
				}
			});
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					eseguiSostituzione = true;
					ParametriSostituzione.this.setVisible(false);					
				}
			});
		}
		public String getCerca(){
			if(eseguiSostituzione)
				return cerca.getText();
			else
				return null;
		}
		public String getSostituisci(){
			if(eseguiSostituzione)
				return sostituisci.getText();
			else
				return null;
		}
	}

	
	/************************************************************************
	 * Il dialogo utilizzaper impostare l'espressione
	 * @author Edoardo Panfili, studio Aspix
	 ***********************************************************************/
	private class RichiestaEspressione extends JDialog{
		private static final long serialVersionUID = 1L;
		DefaultComboBoxModel modelloEspressione = new DefaultComboBoxModel();
		JComboBox espressione = new JComboBox(modelloEspressione);
		boolean eseguiEspressione = false;
		RichiestaEspressione(){
			this.setTitle("imposta una espressione");
			JButton annulla = new JButton("annulla");
			JButton ok = new JButton("esegui calcoli");
			JPanel principale = new JPanel(new GridBagLayout());
			JLabel eEspressione = new JLabel("espressione:");
			JLabel info1 = new JLabel("x sta per il valore del dato da elaborare");
			JLabel info2 = new JLabel("i simboli per le operazioni sono +-*/");
			JLabel info3 = new JLabel("usa Math.round() per arrotondare");
			
			principale.add(eEspressione,	new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
			principale.add(espressione,     new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
			principale.add(info1,         	new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsEtichetta, 0, 0));
			principale.add(info2,         	new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsEtichetta, 0, 0));
			principale.add(info3,         	new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsEtichetta, 0, 0));
			principale.add(annulla,         new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzione, 0, 0));
			principale.add(ok,         		new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzione, 0, 0));
			
			espressione.setEditable(true);
			modelloEspressione.addElement("");
			modelloEspressione.addElement("Math.round(x)");
			modelloEspressione.addElement("x/100.00");
			modelloEspressione.addElement("Math.round(Math.atan(x/100)*180/Math.PI)");
			principale.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			this.getContentPane().add(principale);
			this.pack();
			this.setModal(true);
			annulla.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					eseguiEspressione = false;
					RichiestaEspressione.this.setVisible(false);					
				}
			});
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					eseguiEspressione = true;
					RichiestaEspressione.this.setVisible(false);					
				}
			});
		}
		public String getEspressione(){
			if(eseguiEspressione)
				return (String) espressione.getSelectedItem();
			else
				return null;
		}
	}
	
	/************************************************************************
	 * Il dialogo utilizza per impostare i parametri di cerca e sostituisci
	 * @author Edoardo Panfili, studio Aspix
	 ***********************************************************************/
	private class ParametriInserimento extends JDialog{
		private static final long serialVersionUID = 1L;
		JTextField pattern = new JTextField();
		boolean eseguiInserimento = false;
		ParametriInserimento(){
			this.setTitle("imposta una testo da inserire");
			JButton annulla = new JButton("annulla");
			JButton ok = new JButton("esegui inserimento");
			JPanel principale = new JPanel(new GridBagLayout());
			JLabel eEspressione = new JLabel("espressione:");
			JLabel info1 = new JLabel("$ sta per il valore presente nel campo");
			
			principale.add(eEspressione,	new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
			principale.add(pattern,     	new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
			principale.add(info1,         	new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsEtichetta, 0, 0));
			principale.add(annulla,         new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzione, 0, 0));
			principale.add(ok,         		new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzione, 0, 0));
			
			principale.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			this.getContentPane().add(principale);
			this.pack();
			this.setModal(true);
			annulla.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					eseguiInserimento = false;
					ParametriInserimento.this.setVisible(false);					
				}
			});
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					eseguiInserimento = true;
					ParametriInserimento.this.setVisible(false);					
				}
			});
		}
		public String getPattern(){
			if(eseguiInserimento)
				return pattern.getText();
			else
				return null;
		}
	}
}
