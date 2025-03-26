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

import it.aspix.archiver.assistenti.BarraAvanzamentoWizard;
import it.aspix.archiver.assistenti.vegimport.SHTveg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.xml.sax.SAXException;

/****************************************************************************
 * Questo permette di gestire gli attibuti e di cambiarne i valori
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DialogoElaborazioneAttributi extends JDialog{
	
	private static final long serialVersionUID = 1L;
	SHTveg handler;
	PannelloSeparazioneAttributi psa;
	PannelloFusioneAttributi pfa;
	PannelloDuplicazioneAttributi pda;
	PannelloGestioneValori pgv;
	JList<AzioneSuAttributi> listaAzioni;
	DefaultListModel<AzioneSuAttributi> elencoAzioni = new DefaultListModel<>();
	JButton play;
	
	/************************************************************************
	 * @param handler che ha fatto il parsing
	 * @param ba comune all'applicazione
	 * @throws SAXException
	 * @throws IOException
	 ***********************************************************************/
	public DialogoElaborazioneAttributi(SHTveg handler, BarraAvanzamentoWizard ba, String fileDati) throws SAXException, IOException{
		this.handler = handler;
		JTabbedPane pannelloLinguette = new JTabbedPane();
		listaAzioni = new JList<>(elencoAzioni);
		JScrollPane scrollAzioni = new JScrollPane(listaAzioni);
		JPanel pannelloAzioni = new JPanel(new BorderLayout());
		play = new JButton("▶");
		
		psa = new PannelloSeparazioneAttributi(handler.getRilievi(), this);
		pfa = new PannelloFusioneAttributi(handler.getRilievi(), this);
		pda = new PannelloDuplicazioneAttributi(handler.getRilievi(), this);
		pgv = new PannelloGestioneValori(handler.getRilievi(), this, fileDati);
		JPanel pPrincipale = new JPanel(new BorderLayout());
		ba.setDialogo(this);
		play.setForeground(Color.RED);
		Font originale = play.getFont();
		
		play.setFont(new Font(originale.getName(), originale.getStyle(), 30));
		
		pPrincipale.add(pannelloLinguette, BorderLayout.CENTER);
		pPrincipale.add(pannelloAzioni, BorderLayout.WEST);
		pPrincipale.add(ba, BorderLayout.SOUTH);
		
		pannelloAzioni.add(scrollAzioni, BorderLayout.CENTER);
		pannelloAzioni.add(play, BorderLayout.SOUTH);

		pannelloLinguette.add(pgv, "valori");
		pannelloLinguette.add(psa, "separazione");
		pannelloLinguette.add(pfa, "fusione");
		pannelloLinguette.add(pda, "duplicazione");
		
		this.getContentPane().add(pPrincipale);
		
		scrollAzioni.setPreferredSize(new Dimension(200,100));
		this.setTitle("elaborazione degli attributi");
		this.pack();
		
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eseguiElencoAzioni();
			}
		});
		listaAzioni.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				eseguiEliminazione(arg0);
			}
		});
	}
	
	/************************************************************************
	 * Chiede a tutti i pannellidi rigenerare gli attributi 
	 ***********************************************************************/
	protected void elencoCambiato(){
		psa.generaElenchiAttributi();
		pfa.generaElenchiAttributi();
		pda.generaElenchiAttributi();
		pgv.generaElenchiAttributi();
	}
	
	/************************************************************************
	 * @param asa l'azione da aggiungere all'elenco
	 ***********************************************************************/
	protected void aggiungiAzione(AzioneSuAttributi asa){
		elencoAzioni.addElement(asa);
	}
	
	/************************************************************************
	 * @param elenco da visualizzare nella lista
	 ***********************************************************************/
	public void setElenco(ArrayList<AzioneSuAttributi> elenco){
		elencoAzioni.removeAllElements();
		for(int i=0; i<elenco.size(); i++){
			elencoAzioni.addElement(elenco.get(i));
		}
	}
	
	/************************************************************************
	 * @return le azioni in elenco
	 ***********************************************************************/
	public ArrayList<AzioneSuAttributi> getElenco(){
		ArrayList<AzioneSuAttributi> risposta = new ArrayList<AzioneSuAttributi>();
		for(int i=0 ; i<elencoAzioni.getSize() ; i++){
			risposta.add((AzioneSuAttributi) elencoAzioni.getElementAt(i));
		}
		return risposta;
	}
	
	/************************************************************************
	 * Esegue tutte le azioni presenti in elenco
	 ***********************************************************************/
	private void eseguiElencoAzioni(){
		AzioneSuAttributi azione;
		for(int i=0 ; i<elencoAzioni.getSize() ; i++){
			azione = (AzioneSuAttributi) elencoAzioni.getElementAt(i);
			psa.eseguiAzione(azione);
			pfa.eseguiAzione(azione);
			pda.eseguiAzione(azione);
			pgv.eseguiAzione(azione);
		}
		play.setForeground(Color.GREEN);
		play.setEnabled(false);
	}
	
	/************************************************************************
	 * elimina una azione dalla lista
	 * @param arg l'evento da elaborare
	 ***********************************************************************/
	private void eseguiEliminazione(MouseEvent arg){
		if(arg.getClickCount()==2){
			AzioneSuAttributi asu = (AzioneSuAttributi) listaAzioni.getSelectedValue();
			int risposta = JOptionPane.showConfirmDialog(this, 
					"Vuoi eliminare "+asu, "elimina azione", JOptionPane.YES_NO_OPTION);
			if(risposta==JOptionPane.YES_OPTION){
				elencoAzioni.removeElement(asu);
			}
		}
	}
}
