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

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.UtilitaVegetazione;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/****************************************************************************
 * Questo dialogo serve a costruire una stratificazione basandosi
 * su nomi di specie presenti in una lista (stratificazione ad 1 livello)
 * e su una stratificazione preesistente
 ***************************************************************************/
public class FiltraSpecieListaAAlbero extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private BorderLayout layoutPannelloPrincipale = new BorderLayout();
	private JPanel pannelloPrincipale = new JPanel(layoutPannelloPrincipale);
	private JLabel etichettaSpecie = new JLabel();
	private GridBagLayout layoutPannelloStrati = new GridBagLayout();
	private JPanel pannelloStrati = new JPanel(layoutPannelloStrati);
	private JLabel eAbbondanza = new JLabel();
	private JLabel eIdCampione = new JLabel();
	private JLabel [] livelloNome;
	private JTextField [] livelloAbbondanza;
	private JTextField [] idCampione;
	private GridBagLayout layoutPannelloPulsanti = new GridBagLayout();
	private JPanel pannelloPulsanti = new JPanel(layoutPannelloPulsanti);
    private JButton precedente = new JButton();
	private JButton prossimo = new JButton();
	
	private int indiceSpecieInEsame;	// riguarda il livello "src"
	private Level src;
    private Level vecchiLivelli[];
    private Level nuoviLiveli[];
	
    /************************************************************************
     * @param src il livello (unico) da cui prendere i nomi delle specie
     * @param old vecchia stratificazione
     * @param soloNuovi true se deve chiedere soltanto le specie non presenti in old
     ***********************************************************************/
	public FiltraSpecieListaAAlbero(Level[] src, Level[] old, boolean soloNuovi){
		this.src = src[0]; // della lista interessa solo un livello
		this.vecchiLivelli = old;
		// creo la nuova sratificazione
		nuoviLiveli = new Level[old.length];
		for(int i=0 ; i<nuoviLiveli.length ; i++){
			nuoviLiveli[i] = vecchiLivelli[i].clone();
			nuoviLiveli[i].removeAllSurveyedSpecie();
		}
		// creazione degli elementi a runtime
		livelloNome = new JLabel[nuoviLiveli.length];
		livelloAbbondanza = new JTextField[nuoviLiveli.length];
		idCampione = new JTextField[nuoviLiveli.length];
		for(int i=0;i<nuoviLiveli.length;i++){
			livelloNome[i]=new JLabel();
			livelloAbbondanza[i]=new JTextField();
			idCampione[i]=new JTextField();
			livelloNome[i].setText(nuoviLiveli[i].toString());
		}
		// ---------- le stringhe ----------
		etichettaSpecie.setText("specie rilevata");
        precedente.setText("\u00ab precedente \u00ab");
		prossimo.setText("\u00bb successivo \u00bb");
		eAbbondanza.setText("abbondanza");
		eIdCampione.setText("id campione");
		this.setTitle("Filtro specie");
		// ---------- inserimento nei pannelli ----------
		this.getContentPane().add(pannelloPrincipale);
		pannelloPrincipale.add(etichettaSpecie, BorderLayout.NORTH);
		pannelloPrincipale.add(pannelloStrati, BorderLayout.CENTER);
		pannelloPrincipale.add(pannelloPulsanti, BorderLayout.SOUTH);
		
		pannelloStrati.add(eAbbondanza, 	new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 	CostantiGUI.insetsEtichettaColonna, 0, 0));
		pannelloStrati.add(eIdCampione, 	new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 	CostantiGUI.insetsEtichettaColonna, 0, 0));
		for(int i=0;i<nuoviLiveli.length;i++){
			pannelloStrati.add(livelloNome[i],       new GridBagConstraints(0, i+1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE, 		CostantiGUI.insetsEtichetta, 0, 0));
			pannelloStrati.add(livelloAbbondanza[i], new GridBagConstraints(1, i+1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 	CostantiGUI.insetsDatoTesto, 0, 0));
			pannelloStrati.add(idCampione[i],        new GridBagConstraints(2, i+1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 	CostantiGUI.insetsDatoTesto, 0, 0));
		}
        pannelloPulsanti.add(precedente, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, CostantiGUI.insetsAzioneInBarraUltimaDiGruppo  , 0, 0));
        pannelloPulsanti.add(prossimo,   new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, CostantiGUI.insetsAzioneInBarra, 0, 0));
		// ---------- gli ascoltatori ----------
		precedente.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				precedente_actionPerformed(e);
			}
		});
        prossimo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                prossimo_actionPerformed(e);
            }
        });
		etichettaSpecie.setPreferredSize(new Dimension(400,etichettaSpecie.getPreferredSize().height));
		this.setModal(true);
		this.pack();
	}
	
	/************************************************************************
	 * E' necessario l'overload di setVisible perché in alcuni casi la finestra
	 * non va aperta
	 ***********************************************************************/
	public void setVisible(boolean v){
		indiceSpecieInEsame = -1;
		prossimo_actionPerformed(null);
		if(indiceSpecieInEsame < src.getSurveyedSpecieCount()){
			super.setVisible(v);
		}
	}

    /************************************************************************
     * Copia i dati della specie presenti sullo schermo e prepara la precedente
     * @param e
     ***********************************************************************/
	protected void precedente_actionPerformed(ActionEvent e){
	    if(indiceSpecieInEsame>-1){
	        recuperaDatiSpecie();
	    }
	    if(indiceSpecieInEsame>0){
	        indiceSpecieInEsame--;
	        visualizzaDatiSpecie();
	    }
	}
    
    /************************************************************************
     * Copia i dati della specie presenti sullo schermo e prepara la prossima
     * @param e
     ***********************************************************************/
    protected void prossimo_actionPerformed(ActionEvent e){
        if(indiceSpecieInEsame>-1){
            recuperaDatiSpecie();
        }
        indiceSpecieInEsame++;
        if(indiceSpecieInEsame >= src.getSurveyedSpecieCount()){
        	Stato.debugLog.fine("Filtrate tutte le specie");
            this.dispose();
        }else{
        	Stato.debugLog.fine("Visualizzo una specie");
            boolean giaFatto = visualizzaDatiSpecie();
            if(giaFatto && Proprieta.isTrue("vegetazione.sincronizzaSolamenteNuove")){
            	Stato.debugLog.fine("Passo oltre in automatico");
            	// se la specie era già presente in precedenza e le preferenze indicano questo
            	// comportamento si passa direttamente oltre
            	prossimo_actionPerformed(null);
            }
        }
    }
    
    /************************************************************************
     * Recupera i dati dalla finestra presente sullo schermo
     * usa la variabile indiceSpecieInEsame
     ***********************************************************************/
	private void recuperaDatiSpecie(){
	    SurveyedSpecie ss;
	    
	    Stato.debugLog.fine("indice specie in esame: "+indiceSpecieInEsame+" su un totale di "+src.getSurveyedSpecieCount());
	    for(int i=0 ; i<nuoviLiveli.length ; i++){
	        if(livelloAbbondanza[i].getText().length()!=0){
	            Stato.debugLog.fine("imposto i due valori "+livelloAbbondanza[i].getText()+" e "+idCampione[i].getText());
	            ss = new SurveyedSpecie( src.getSurveyedSpecie(indiceSpecieInEsame));
	            ss.setAbundance(livelloAbbondanza[i].getText());
	            ss.setSampleId(idCampione[i].getText());
	            nuoviLiveli[i].addSurveyedSpecie(ss);
	        }
	    }
	}
    
    /************************************************************************
     * Visualizza i dati nella finestra presente sullo schermo
     * @return true se la specie era già presente nella vecchia stratificazione
     ***********************************************************************/
    private boolean visualizzaDatiSpecie(){
        SurveyedSpecie ss;
        SurveyedSpecie temp;
        boolean giaPresente = false;

        ss = src.getSurveyedSpecie(indiceSpecieInEsame);
        etichettaSpecie.setText( UtilitaVegetazione.calcolaNomeSpecie(ss.getSpecieRefName(), ss.getDetermination()) );
        for(int i=0;i<nuoviLiveli.length;i++){
        	temp = UtilitaVegetazione.cercaSurveyedSpecie(vecchiLivelli, nuoviLiveli[i], ss);
            if(temp==null){
            	livelloAbbondanza[i].setText("");
            	idCampione[i].setText("");  
            }else{
                livelloAbbondanza[i].setText(temp.getAbundance());
                idCampione[i].setText(temp.getSampleId());
                giaPresente = true;
            }
        }
        Stato.debugLog.fine("Specie "+ss.getSpecieRefName()+" già presente: "+giaPresente);
        return giaPresente;
    }
 
    /************************************************************************
     * @return i livelli immessi
     ***********************************************************************/
    public Level[] getLevels(){
    	return nuoviLiveli;
    }

}