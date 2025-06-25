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
package it.aspix.archiver.dialoghi;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.componenti.CoppiaCSTesto;

/****************************************************************************
 * Questo dialogo serve a gestire un unico testo che rappresenta più proprietà.
 * Le singole proprietà devono essere precedute da un prefisso.
 * I prefissi devono avere un pattern comune.
 * Attualmente questo componente è specializzato per la sintassonomia.
 *
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class TestoMultiproprieta extends JDialog{

	private static final long serialVersionUID = 1L;

	// XXX: questo potrebbe essere passato come parametro ma siccome finora
	// CampoMultiproprieta viene usato in un solo contesto lo lascio qui
	// XXX: ATTENZIONE: questa lista è duplicata in it.aspix.argentaroggia.web.vegetazione.TabellaDescrizioniRilieviTesto
	// sarebbe il caso di razionalizzare la cosa o di gestire proprio separamente le parti della classificazione
	private CoppiaCSTesto[] prefissi={
			new CoppiaCSTesto("AS:", "associazione"),
			  new CoppiaCSTesto("SA:", "subassociazione"),
			  new CoppiaCSTesto("AG:", "aggruppamento"),
			  new CoppiaCSTesto("VA:", "variante"),
			  new CoppiaCSTesto("FA:", "facies"),
			new CoppiaCSTesto("AL:", "alleanza"),
			  new CoppiaCSTesto("SL:", "suballeanza"),
			new CoppiaCSTesto("OR:", "ordine"),
			  new CoppiaCSTesto("SO:", "subordine"),
			new CoppiaCSTesto("CL:", "classe"),
			  new CoppiaCSTesto("SC:", "subclasse"),
			new CoppiaCSTesto("SS:", "superclasse")
	};

	// tutte le stringhe che seguono il pattern sotto devono essere un prefisso
	private Pattern patternPrefisso = Pattern.compile("([A-Z][A-Z]?[A-Z]?:)");

	JLabel nome[];
	JTextField valore[];

	public TestoMultiproprieta(){
		JPanel principale = new JPanel(new BorderLayout());
		JPanel dati = new JPanel(new GridBagLayout());
		JPanel pulsanti = new JPanel(new GridLayout(1,2));
		JButton ok = new JButton("ok");
		nome = new JLabel[prefissi.length];
		valore = new JTextField[prefissi.length];

		for(int i=0 ; i<prefissi.length ; i++){
			nome[i] = new JLabel(prefissi[i].getDescrizione());
			valore[i] = new JTextField();
			dati.add(nome[i], 	new GridBagConstraints(0, i, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
			dati.add(valore[i], new GridBagConstraints(1, i, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 200, 0));
		}
		pulsanti.add(ok);
		principale.add(dati, BorderLayout.CENTER);
		principale.add(pulsanti, BorderLayout.SOUTH);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TestoMultiproprieta.this.setVisible(false);
			}
		});
		this.getContentPane().add(principale);
		this.pack();
		this.setModal(true);
	}

	/************************************************************************
	 * @param testo da visualizzare (contiene più proprietà)
	 * @throws Exception se trova un pattern sconosciuto
	 ***********************************************************************/
	public void setText(String testo) throws Exception{
		Matcher m = patternPrefisso.matcher(testo);
		String prefisso;
		int partenza;
		int arrivo;
		String parte;
		int posizionePrefisso;

		// controllo la presenza di prefissi errati
		while(m.find()){
			prefisso = m.group(1);
			// cerco il prefisso
			posizionePrefisso = -1;
			for(int i=0; i<prefissi.length; i++){
				if(prefissi[i].getEsterno().equals(prefisso)){
					posizionePrefisso = i;
					break;
				}
			}
			// vedo se è stato trovato
			if(posizionePrefisso==-1){
				throw new Exception("Prefisso \""+prefisso+"\" non gestibile");
			}
		}

		// trovo la posizione di tutti i prefissi,
		// l'ultimo è fittizio ed è la fine della stringa
		int posizione[] = new int[prefissi.length+1];
		posizione[prefissi.length] = testo.length();
		for(int i=0; i<prefissi.length; i++){
			posizione[i] = testo.indexOf(prefissi[i].getEsterno());
		}

		// imposto i contenuti delle singole caselle
		for(int i=0; i<prefissi.length; i++){
			if(posizione[i]!=-1){
				partenza = posizione[i];
				arrivo = Integer.MAX_VALUE;
				for(int j=0; j<posizione.length; j++){
					if(posizione[j]>posizione[i] && posizione[j]<arrivo){
						arrivo=posizione[j];
					}
				}
				parte = testo.substring(partenza+prefissi[i].getEsterno().length(), arrivo).trim();
				valore[i].setText(parte);
			}
		}
	}

	/************************************************************************
	 * @return una stringa che rappresenta il contenuto di questo dialogo
	 ***********************************************************************/
	public String getText(){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<prefissi.length; i++){
			if(valore[i].getText().trim().length()>0){
				if(sb.length()>0){
					sb.append(' ');
				}
				sb.append(prefissi[i].getEsterno());
				sb.append(valore[i].getText().trim());
			}
		}
		return sb.toString();
	}

}
