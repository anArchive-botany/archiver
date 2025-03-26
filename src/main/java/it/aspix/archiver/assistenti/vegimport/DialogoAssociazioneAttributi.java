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
import it.aspix.archiver.assistenti.GestoreAttributoAnArchive;
import it.aspix.archiver.assistenti.SampleWrapper;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.xml.sax.SAXException;

/****************************************************************************
 * Mostra e permette di modificare le associazioni degli attributi da
 * turboveg a anArchive
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DialogoAssociazioneAttributi extends JDialog{
	
	private static final long serialVersionUID = 1L;
	SHTveg handler;
	DefaultListModel<AssociazioneAttributo> contenutoListaAssociazioni = new DefaultListModel<>();
	JList<AssociazioneAttributo> listaAssociazioni = new JList<>(contenutoListaAssociazioni);
	GestoreAttributoAnArchive attributoAnArchive;
	JTextArea esempio = new JTextArea();
	
	public DialogoAssociazioneAttributi(SHTveg handler, BarraAvanzamentoWizard ba) throws SAXException, IOException, InterruptedException, URISyntaxException{
		JPanel pAssociazione = new JPanel(new GridLayout(1,2));
		JPanel principale = new JPanel(new BorderLayout());
		JScrollPane pSinistra = new JScrollPane(listaAssociazioni);
		JPanel pDestra = new JPanel(new BorderLayout());
		JScrollPane scrollEsempio = new JScrollPane(esempio);
		JPanel pannelloSpecifiche = new JPanel(new GridLayout(2,1));
		
		attributoAnArchive = new GestoreAttributoAnArchive(null);
		
		this.handler = handler;
		pannelloSpecifiche.add(attributoAnArchive);
		pDestra.add(pannelloSpecifiche, BorderLayout.NORTH);
		pDestra.add(scrollEsempio, BorderLayout.CENTER);
		pAssociazione.add(pSinistra);
		pAssociazione.add(pDestra);
		principale.add(pAssociazione, BorderLayout.CENTER);
		ba.setDialogo(this);
		principale.add(ba, BorderLayout.SOUTH);
		this.getContentPane().add(principale);
		
		this.setTitle("associazione degli attributi");
		this.setSize(900,400);
		this.validate();
		this.setModal(true);
		
		// costruisco un array con tutti i nomi delle proprietà recuperate
		ArrayList<AssociazioneAttributo> collegamentiProprieta = new ArrayList<AssociazioneAttributo>();
		HashSet<String> attributi = VegImport.elencaAttributi(handler.getRilievi());
		for(String y: attributi){
			collegamentiProprieta.add(new AssociazioneAttributo(y));
		}
		Collections.sort(collegamentiProprieta);
		// impostazioni iniziali per la lista
		for(AssociazioneAttributo y: collegamentiProprieta){
			contenutoListaAssociazioni.addElement(y);
		}
		listaAssociazioni.setSelectedIndex(0);
		selezioneAssociazioneCambiata(new ListSelectionEvent(this, 0, 0, false));
		listaAssociazioni.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				selezioneAssociazioneCambiata(arg0);
			}
		});
		
		attributoAnArchive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sceltoBersaglio();
			}
		});
	}
	
	private void selezioneAssociazioneCambiata(ListSelectionEvent arg0){
		if(arg0.getValueIsAdjusting()){
			return;
		}
		AssociazioneAttributo voceSelezionata = (AssociazioneAttributo) listaAssociazioni.getSelectedValue();
		aggiornaEsempio(voceSelezionata);
		attributoAnArchive.setText(voceSelezionata.anArchive);
	}
	
	private void sceltoBersaglio(){
		AssociazioneAttributo voceSelezionata = (AssociazioneAttributo) listaAssociazioni.getSelectedValue();
		String attributoSelezionato = attributoAnArchive.getText();
		voceSelezionata.anArchive = attributoSelezionato;
		listaAssociazioni.updateUI();
	}
	

	
	/************************************************************************
	 * Il metodo utilizzato per recuperare il lavoro fatto da e per mezzo 
	 * di questo dialogo
	 * @return le associazioni costruite da questo dialogo
	 ***********************************************************************/
	public HashMap<String, String> getAssociazioni(){
		HashMap<String, String> risposta = new HashMap<String, String>();
		AssociazioneAttributo x;
		
		for(int i=0 ; i<contenutoListaAssociazioni.size() ; i++){
			x = (AssociazioneAttributo) contenutoListaAssociazioni.get(i);
			risposta.put(x.turboveg, x.anArchive);
		}
		return risposta;
	}
	
	/************************************************************************
	 * Mostra nella casella degli esempi i valori assunti dall'attributo
	 * selezionato
	 * @param voce da prendere in considerazione 
	 ***********************************************************************/
	private void aggiornaEsempio(AssociazioneAttributo voce){
		StringBuffer sb = new StringBuffer();
		for(SampleWrapper sw: handler.getRilievi()){
			if(sw.proprieta.containsKey(voce.turboveg)){
				sb.append(sw.proprieta.get(voce.turboveg));
				sb.append('\n');
			}
		}
		esempio.setText(sb.toString());
	}
	
}
