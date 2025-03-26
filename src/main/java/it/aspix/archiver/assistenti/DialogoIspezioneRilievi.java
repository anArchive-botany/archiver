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

import it.aspix.archiver.editor.SampleEditor;
import it.aspix.archiver.editor.SampleEditorLinguette;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.sbd.obj.Cell;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.Message;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.Sample;
import it.aspix.sbd.scale.sample.GestoreScale;
import it.aspix.sbd.scale.sample.ScalaSample;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;

/****************************************************************************
 * Consente di visualizzare e di modificare i rilievi
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DialogoIspezioneRilievi extends JDialog{

	private static final long serialVersionUID = 1L;
	int rilievoVisualizzato = 0;
	Sample[] elenco;
	Message[] esito;
	SampleEditor se = new SampleEditorLinguette();
	DefaultListModel<String> elencoProblemi = new DefaultListModel<>();
	ScalaSample scala;
	JSlider slider;
	JList<String> lista;
	
	public DialogoIspezioneRilievi(Sample elenco[], String nomeScalaAbbondanza, Message[] esito, BarraAvanzamentoWizard ba){
		JPanel pPrincipale = new JPanel(new BorderLayout());
		JSplitPane pDati = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JPanel pRilievi = new JPanel(new BorderLayout());
		JPanel pPrecSucc = new JPanel(new BorderLayout());
		JButton precedente = new JButton("<< & salva");
		JButton successivo = new JButton("salva & >>");
		slider = new JSlider();
		lista = new JList<String>(elencoProblemi);
		JScrollPane scroll = new JScrollPane(lista);
		
		this.elenco = elenco;
		this.esito = esito;
		scala = GestoreScale.buildForName(nomeScalaAbbondanza);
		
		pPrincipale.add(pDati, BorderLayout.CENTER);
		ba.setDialogo(this);
		pPrincipale.add(ba, BorderLayout.SOUTH);
		pDati.add(scroll);
		pDati.add(pRilievi);
		pRilievi.add(se, BorderLayout.CENTER);
		pRilievi.add(pPrecSucc, BorderLayout.SOUTH);
		pPrecSucc.add(precedente, BorderLayout.WEST);
		pPrecSucc.add(slider, BorderLayout.CENTER);
		pPrecSucc.add(successivo, BorderLayout.EAST);

		// va fatto manualmente perché aggiornaVisualizzazione() leggerebbe
		// il vecchio dato presente (nessuno) cancellando il primo 
		this.getContentPane().add(pPrincipale);
		rilievoVisualizzato = 0;
		try {
			se.setSample(elenco[rilievoVisualizzato]);
		} catch (Exception ex) {
			Dispatcher.consegna(this, ex);
		}
		check();
		scroll.setPreferredSize(new Dimension(250,0));
		this.pack();
		this.setModal(true);
		slider.setMinimum(0);
		slider.setMaximum(elenco.length-1);
		aggiornaVisualizzazione(0, 0); // così si imposta titolo e slide
		
		precedente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				aggiornaVisualizzazione(0, -1);
			}
		});
		successivo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				aggiornaVisualizzazione(0, +1);
			}
		});
		slider.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e) {
				aggiornaVisualizzazione(slider.getValue(), 0);
			}
		});
		lista.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent ev) {
				if(ev.getClickCount()==2){
					String val = (String) lista.getSelectedValue();
					int posDuePunti = val.indexOf(':');
					int indice = Integer.parseInt(val.substring(0, posDuePunti));
					aggiornaVisualizzazione(indice-1, 0);
				}
			}
		});
	}

	void aggiornaVisualizzazione(int valore, int incremento){
		// aggiorno il rilievo visualizzato
		try {
			elenco[rilievoVisualizzato] = se.getSample();
		} catch (ValoreException e) {
			Dispatcher.consegna(this, e);
		}
		if(incremento==0){
			rilievoVisualizzato = valore;
		}else{
			rilievoVisualizzato+=incremento;
		}
		
		if(rilievoVisualizzato<0){
			rilievoVisualizzato = 0;
		}
		if(rilievoVisualizzato>=elenco.length){
			rilievoVisualizzato = elenco.length-1; 
		}
		this.setTitle("Correzione rilievo "+(rilievoVisualizzato+1)+" di "+elenco.length);
		try {
			se.setSample(elenco[rilievoVisualizzato]);
			slider.setValue(rilievoVisualizzato);
			check();
		} catch (Exception e) {
			Dispatcher.consegna(this, e);
			e.printStackTrace();
		}
	}
	
	/************************************************************************
	 * Ricalcola e mostra gli avvisi
	 ***********************************************************************/
	private void check(){
		int iRilievo,iStrato,iSpecie;
		Cell cElaborazione;
		Level sElaborazione;
		StringBuilder sb;
		elencoProblemi.removeAllElements();
		for(iRilievo=0 ; iRilievo<elenco.length ; iRilievo++){
			cElaborazione = elenco[iRilievo].getCell();
			sb = new StringBuilder();
			for(iStrato=0; iStrato<cElaborazione.getLevelCount() ; iStrato++){
				sElaborazione = cElaborazione.getLevel(iStrato);
				for(iSpecie=0; iSpecie<sElaborazione.getSurveyedSpecieCount() ; iSpecie++){
					if(!scala.isValid(sElaborazione.getSurveyedSpecie(iSpecie).getAbundance())){
						if(sb.length()>0){
							sb.append(", ");
						}
						sb.append("abbondanza "+sElaborazione.getSurveyedSpecie(iSpecie).getAbundance());
					}
				}
			}
			if(esito!=null){
				if(esito[iRilievo].getType()!= MessageType.INFO ){
					if(sb.length()>0){
						sb.append(", ");
					}
					sb.append("server: \""+esito[iRilievo].getText(0).getText()+"\"");
				}
			}
			if(sb.length()>0){
				// devo visualizzare il messaggio
				elencoProblemi.addElement( (iRilievo+1)+": "+ sb.toString());
			}
		}
	}
}
