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

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.DesktopApiWrapper;
import it.aspix.archiver.UtilitaGui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/****************************************************************************
 * un pannello con i pulsanti avanti (che chiude il JDialog)
 * e abbandona (che esce dall'apllicazione)
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class BarraAvanzamentoWizard extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JDialog dialogo;
	
	boolean premutoAvanti = true;

	public BarraAvanzamentoWizard(final String nomeAssistente, final String fileLicenza, final String url){
		JButton indietro = new JButton("indietro");
		JButton avanti = new JButton("avanti");
		JButton abbandona = new JButton("abbandona");
		JButton info = new JButton("info");
		JButton manuale = new JButton("manuale");
		
		this.setLayout(new GridBagLayout());
		this.add(abbandona,			new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzioneInBarraIsolata, 0, 0));
		this.add(info,				new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzioneInBarraPrimaDiGruppo, 0, 0));
		this.add(manuale,			new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzioneInBarraUltimaDiGruppo, 0, 0));
		this.add(indietro,			new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzioneInBarraPrimaDiGruppo, 0, 0));
		this.add(avanti,			new GridBagConstraints(4, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzioneInBarraUltimaDiGruppo, 0, 0));
		
		UtilitaGui.setOpaqueRicorsivo(this, false);
		this.setOpaque(true);
		this.setBackground(this.getBackground().darker());
		this.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		
		avanti.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				azioneAvanti();
			}
		});
		indietro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				azioneIndietro();
			}
		});
		abbandona.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InformazioniWizard iw = new InformazioniWizard(nomeAssistente, fileLicenza, url);
				iw.setVisible(true);
			}
		});
		manuale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(DesktopApiWrapper.isDesktopApiBrowseAvailable()){
					DesktopApiWrapper.openLink(BarraAvanzamentoWizard.this, url);
				}
			}
		});
	}
	
	public void setDialogo(JDialog d){
		dialogo = d;
	}
	
	private void azioneIndietro(){
		dialogo.setVisible(false);
		premutoAvanti = false;
	}
	
	private void azioneAvanti(){
		dialogo.setVisible(false);
		premutoAvanti = true;
	}
	
	/************************************************************************
	 * @return true se è stato premuto "avanti", false per "indietro"
	 ***********************************************************************/
	public boolean isAvanti(){
		return premutoAvanti;
	}
}
