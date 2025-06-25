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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.nucleo.Proprieta;

public class PreferenzeFondamentali extends JDialog{

	private static final long serialVersionUID = 1L;

	JTextField erbario = new JTextField();
	JTextField vegetazione = new JTextField();
	JTextField checkList = new JTextField();
	JTextField categorieGeneriche = new JTextField();
	JCheckBox nonMostrarePiu = new JCheckBox("Non mostarre più questo pannello");

	public PreferenzeFondamentali(){
		this.setTitle("Preferenze iniziali");
		JLabel eErbario = new JLabel("nome erbario:");
		JLabel eVegetazione = new JLabel("progetto vegetazione:");
		JLabel eCheckList = new JLabel("check-list:");
		JLabel eCategorieGeneriche = new JLabel("categorie generiche:");

		JTextArea dErbario = new JTextArea("Se inserisci dati di erbario scrivi qui sotto il nome dell'erbario che usi normalmente (i nomi di erbario sono di solito scritti in tutto maiuscolo");
		JTextArea dVegetazione = new JTextArea("Se inserisci dati di vegetazione (rilievi o plot) scrivi qui sotto il nome del progetto che usi normalmente (i nomi dei progetti sono di solito scritti in tutto maiuscolo");
		JTextArea dCheckList = new JTextArea("Se gestisci una check-list scrivi qui sotto il nome della check list (di solito in tutto minuscolo) e le categorie generiche che usi (oppure cancella quelle che non usi)");

		JSeparator sErbario = new JSeparator(SwingConstants.HORIZONTAL);
		JSeparator sVegetazione = new JSeparator(SwingConstants.HORIZONTAL);
		JSeparator sCheckList = new JSeparator(SwingConstants.HORIZONTAL);

		// imposto i valori delle proprieta
		erbario.setText(Proprieta.recupera("herbaria.database"));
		vegetazione.setText(Proprieta.recupera("vegetazione.database"));
		checkList.setText(Proprieta.recupera("check-list.database"));
		if(Proprieta.recupera("check-list.categorieGeneriche").length()>0){
			categorieGeneriche.setText(Proprieta.recupera("check-list.categorieGeneriche"));
		}else{
			categorieGeneriche.setText("Batteri, Alghe azzurre, Alghe, Funghi, Licheni, Muschi-epatiche, Felci, Gymnosperme, Angiosperme");
		}
		nonMostrarePiu.setSelected(!Proprieta.isTrue("generale.mostraPreferenzeFondamentali"));

		JButton ok = new JButton("ok");

		JPanel principale = new JPanel(new GridBagLayout());

		principale.add(dErbario,      		new GridBagConstraints(0,  1, 2, 1, 1.0, 1.0, GridBagConstraints.WEST,   GridBagConstraints.BOTH,     	CostantiGUI.insetsTestoDescrittivo, 0, 0));
		principale.add(eErbario,      		new GridBagConstraints(0,  2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,      	CostantiGUI.insetsEtichetta, 0, 0));
		principale.add(erbario,       		new GridBagConstraints(1,  2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		principale.add(sErbario,      		new GridBagConstraints(0,  3, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
		principale.add(dVegetazione,  		new GridBagConstraints(0,  4, 2, 1, 1.0, 1.0, GridBagConstraints.WEST,   GridBagConstraints.BOTH,    	CostantiGUI.insetsTestoDescrittivo, 0, 0));
		principale.add(eVegetazione,  		new GridBagConstraints(0,  5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,      	CostantiGUI.insetsEtichetta, 0, 0));
		principale.add(vegetazione,   		new GridBagConstraints(1,  5, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		principale.add(sVegetazione,  		new GridBagConstraints(0,  6, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
		principale.add(dCheckList,    		new GridBagConstraints(0,  7, 2, 1, 1.0, 1.0, GridBagConstraints.WEST,   GridBagConstraints.BOTH,    	CostantiGUI.insetsTestoDescrittivo, 0, 0));
		principale.add(eCheckList,    		new GridBagConstraints(0,  8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,      	CostantiGUI.insetsEtichetta, 0, 0));
		principale.add(checkList,     		new GridBagConstraints(1,  8, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		principale.add(eCategorieGeneriche, new GridBagConstraints(0,  9, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,      	CostantiGUI.insetsEtichetta, 0, 0));
		principale.add(categorieGeneriche,  new GridBagConstraints(1,  9, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		principale.add(sCheckList,          new GridBagConstraints(0, 10, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
		principale.add(nonMostrarePiu,      new GridBagConstraints(0, 11, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppoIsolatoVerticalmente, 0, 0));
		principale.add(ok,      			new GridBagConstraints(0, 12, 2, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,    	CostantiGUI.insetsDatoTesto, 0, 0));

		principale.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Font f = eErbario.getFont();
        f = f.deriveFont( f.getSize2D()*0.9f );

		dErbario.setOpaque(false);
		dErbario.setEditable(false);
		dErbario.setWrapStyleWord(true);
		dErbario.setLineWrap(true);
		dErbario.setFont(f);
		dVegetazione.setOpaque(false);
		dVegetazione.setEditable(false);
		dVegetazione.setWrapStyleWord(true);
		dVegetazione.setLineWrap(true);
		dVegetazione.setFont(f);
		dCheckList.setOpaque(false);
		dCheckList.setEditable(false);
		dCheckList.setWrapStyleWord(true);
		dCheckList.setLineWrap(true);
		dCheckList.setFont(f);

		this.getContentPane().add(principale);
		this.setSize(new Dimension(400,400));
		this.validate();
		this.setModal(true);

		ok.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				premutoOk();
			}
		});
	}

	private void premutoOk(){
		Proprieta.aggiorna("herbaria.database", erbario.getText().trim());
		Proprieta.aggiorna("vegetazione.database", vegetazione.getText().trim());
		Proprieta.aggiorna("check-list.database", checkList.getText().trim());
		Proprieta.aggiorna("check-list.categorieGeneriche", categorieGeneriche.getText().trim());
		Proprieta.aggiorna("generale.mostraPreferenzeFondamentali", !nonMostrarePiu.isSelected());
		this.setVisible(false);
		// this.dispose();
	}
}
