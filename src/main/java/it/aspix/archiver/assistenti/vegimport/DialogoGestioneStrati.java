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

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.assistenti.BarraAvanzamentoWizard;
import it.aspix.archiver.componenti.CoppiaCSTesto;
import it.aspix.sbd.introspection.InformazioniTipiEnumerati;
import it.aspix.sbd.introspection.ValoreEnumeratoDescritto;
import it.aspix.sbd.obj.Cell;
import it.aspix.sbd.obj.Level;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/****************************************************************************
 * Permette di mostrare gli strati letti da turboveg e di associarli 
 * a quelli di anArchive
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DialogoGestioneStrati extends JDialog{

	private static final long serialVersionUID = 1L;
	DefaultListModel<AssociazioneAttributo> contenutoListaStratiTurboveg = new DefaultListModel<>();
	JList<AssociazioneAttributo> listaStratiTurboveg = new JList<>(contenutoListaStratiTurboveg);
	DefaultComboBoxModel<CoppiaCSTesto> modelloStratificazione = new DefaultComboBoxModel<>();
	JComboBox<CoppiaCSTesto> stratificazione = new JComboBox<>(modelloStratificazione);
	DefaultListModel<Level> contenutoListaStratiAnArchive = new DefaultListModel<>();
	JList<Level> listaStratiAnArchive = new JList<>(contenutoListaStratiAnArchive);
	
	private String modelloStratificazioneApplicato = null;

	HashMap<String, String> combinazione;
	
	/************************************************************************
	 * @param strati secondo le convenzioni di turboveg, quelli che contengono specie
	 * @param stratiDiAnArchive stratii aggiuntivi che servirannno poi per collegare delle proprietà
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws ClassNotFoundException 
	 ***********************************************************************/
	public DialogoGestioneStrati(HashSet<String> strati, HashSet<String> stratiDiAnArchive, BarraAvanzamentoWizard ba){
		JPanel pAssociazione = new JPanel(new GridBagLayout());
		JTextArea avviso;
		JPanel principale = new JPanel(new BorderLayout());
		JScrollPane scrollTurboveg = new JScrollPane(listaStratiTurboveg);
		JScrollPane scrollEsempio = new JScrollPane(listaStratiAnArchive);
		
		if(stratiDiAnArchive!=null && stratiDiAnArchive.size()>0){
			StringBuilder sb = new StringBuilder();
			for(String x: stratiDiAnArchive.toArray(new String[0])){
				if(sb.length()>0){
					sb.append(", ");
				}
				sb.append(x);
			}
			avviso = new JTextArea("ATTENZIONE: sono necessari gli strati per contenere le seguenti proprietà:\n "+sb.toString());
		}else{
			avviso = new JTextArea("ATTENZIONE: il livello unico DEVE essere presente!");
		}
		pAssociazione.add(avviso, 			new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		avviso.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.RED),
				BorderFactory.createEmptyBorder(3, 3, 3, 3) 
		));
		avviso.setOpaque(false);
		avviso.setEditable(false);
		avviso.setWrapStyleWord(true);
		avviso.setLineWrap(true);
		pAssociazione.add(stratificazione, 		new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzione, 0, 0));
		if(strati!=null){
			pAssociazione.add(scrollTurboveg, 		new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsGruppo, 0, 0));
		}
		pAssociazione.add(scrollEsempio, 		new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsGruppo, 0, 0));
		
		principale.add(pAssociazione, BorderLayout.CENTER);
		ba.setDialogo(this);
		principale.add(ba, BorderLayout.SOUTH);
		this.getContentPane().add(principale);
		
		this.setTitle("associazione degli strati");
		this.setSize(900,400);
		this.validate();
		this.setModal(true);
		
		// inserisco gli strati di turboveg se sono presenti come parametro
		// potrebbero non esserlo quando è tabImport a usare questo oggetto
		if(strati!=null){
			Iterator<String> iteratore = strati.iterator();
			ArrayList<String> es = new ArrayList<String>();
			while(iteratore.hasNext()){
				es.add(iteratore.next());
			}
			Collections.sort(es);
			for(String x: es){
				contenutoListaStratiTurboveg.addElement(new AssociazioneAttributo(x));
			}
		}
		
		// elenco dei pattern possibili
		ArrayList<ValoreEnumeratoDescritto> elencoModelloLivelli;
		elencoModelloLivelli = InformazioniTipiEnumerati.getValoriDescritti("Sample.Cell.ModelOfTheLevels","it");
		for(int i=0; i<elencoModelloLivelli.size(); i++){
			modelloStratificazione.addElement(new CoppiaCSTesto(elencoModelloLivelli.get(i)));
		}

		this.pack();
		this.validate();		
		
		stratificazione.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selezioneStratificazione();
			}
		});
		// se ci sono associazioni da salvare ha senso agganciare gli ascoltatori
		if(strati!=null){
			listaStratiAnArchive.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent arg0) {
					selezioneStratoAnArchive();
				}
			});
			stratificazione.setSelectedIndex(2);
		}else{
			// se non c'è niente di selezionato suppongo che lo strato unico basti
			stratificazione.setSelectedIndex(0);
		}
		
	}
	
	void selezioneStratificazione(){
		String nomePattern = ((CoppiaCSTesto)stratificazione.getSelectedItem()).getEsterno();
		AssociazioneAttributo aa;
		modelloStratificazioneApplicato = nomePattern;
		// ripulisco le associazioni
		for(int i=0 ; i<contenutoListaStratiTurboveg.getSize() ; i++){
			((AssociazioneAttributo) contenutoListaStratiTurboveg.get(i)).anArchive = "";
		}
		// inserisco le possibili alternative nella lista degli strati possibili
		Cell cella = new Cell();
		CostruttoreOggetti.addLevelsByPattern(cella, nomePattern);
		contenutoListaStratiAnArchive.removeAllElements();
		for(int i=0 ; i<cella.getLevelCount() ; i++){
			contenutoListaStratiAnArchive.addElement(cella.getLevel(i));
		}
		// si fanno alcune ipotesi ragionevoli in base agli id degli strati
		for(int i=0 ; i<contenutoListaStratiTurboveg.getSize() ; i++){
			aa = (AssociazioneAttributo) contenutoListaStratiTurboveg.get(i);
			if("0".equals(aa.turboveg)){
				if(contieneStrato("0")){
					aa.anArchive = "0";
				}
			}
			// Tree layer (high) (t1)
			if("1".equals(aa.turboveg)){
				if(contieneStrato("1.1")){
					aa.anArchive = "1.1";
				}else if(contieneStrato("1")){
					aa.anArchive = "1";
				}
			}
			// Tree layer (middle) (t2)
			if("2".equals(aa.turboveg)){
				if(contieneStrato("1.2")){
					aa.anArchive = "1.2";
				}else if(contieneStrato("1")){
					aa.anArchive = "1";
				}
			}
			// 	Tree layer (low) (t3)
			if("3".equals(aa.turboveg)){
				if(contieneStrato("1.3")){
					aa.anArchive = "1.3";
				}else if(contieneStrato("1")){
					aa.anArchive = "1";
				}
			}
			// Shrub layer (hight) (s1)
			if("4".equals(aa.turboveg)){
				if(contieneStrato("2.1")){
					aa.anArchive = "2.1";
				}else if(contieneStrato("2")){
					aa.anArchive = "2";
				}
			}
			// Shrub layer low (s2)
			if("5".equals(aa.turboveg)){
				if(contieneStrato("2.3")){
					aa.anArchive = "2.3";
				}else if(contieneStrato("2")){
					aa.anArchive = "2";
				}
			}
			// Herb layer (hl)
			if("6".equals(aa.turboveg)){
				if(contieneStrato("3")){
					aa.anArchive = "3";
				}else if(contieneStrato("3.3")){
					aa.anArchive = "3.3";
				}
			}
			if("9".equals(aa.turboveg)){
				if(contieneStrato("4")){
					// FIXME: da verificare
					aa.anArchive = "4";
				}
			}
		}
		listaStratiTurboveg.updateUI();
	}
	
	public void selezioneStratoAnArchive(){
		Level l = (Level) listaStratiAnArchive.getSelectedValue();
		if(l!=null){
			AssociazioneAttributo aa = (AssociazioneAttributo) listaStratiTurboveg.getSelectedValue();
			if(aa!=null){
				aa.anArchive = l.getId();
				listaStratiTurboveg.updateUI();
			}
		}
	}
	
	/************************************************************************
	 * @param id dello strato da cercare
	 * @return true se lo strato esiste nella stratificazione in uso
	 ***********************************************************************/
	private boolean contieneStrato(String id){
		Level l;
		for(int i=0 ; i<contenutoListaStratiAnArchive.size() ; i++){
			l = (Level) contenutoListaStratiAnArchive.get(i);
			if(l.getId().equals(id)){
				return true;
			}
		}
		return false;
	}
	
	/************************************************************************
	 * @return il nome del modello applicato
	 ***********************************************************************/
	public String getModelloStratificazioneApplicato() {
		return modelloStratificazioneApplicato;
	}
	
	/************************************************************************
	 * @return le corrispondenze id strato turboveg verso id strato anArchive
	 ***********************************************************************/
	public Hashtable<String, String> getAssociazioni(){
		AssociazioneAttributo aa ;
		Hashtable<String, String> risposta = new Hashtable<String, String>();
		
		for(int i=0 ; i<contenutoListaStratiTurboveg.size() ; i++){
			aa = (AssociazioneAttributo) contenutoListaStratiTurboveg.get(i);
			risposta.put(aa.turboveg, aa.anArchive);
		}
		return risposta;
	}
}
