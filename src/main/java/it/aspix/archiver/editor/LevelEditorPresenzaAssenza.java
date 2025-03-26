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
package it.aspix.archiver.editor;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.Icone;
import it.aspix.archiver.UtilitaVegetazione;
import it.aspix.archiver.componenti.insiemi.SurveyedSpecieSet;
import it.aspix.archiver.eventi.SistemaException;
import it.aspix.archiver.eventi.SurveyedSpecieListener;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JRadioButton;
import javax.swing.event.ChangeListener;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class LevelEditorPresenzaAssenza extends LevelEditor{

	private static enum TipoIncidenza{ASSENTE, COPRENTE, RADICATA };
	
	private static final long serialVersionUID = 1L;
	
	ArrayList<ChangeListener> ascoltatoriCambiamenti = new ArrayList<ChangeListener>();
	private Level[] livelli;
	private int indiceLivelloInEdit;
	private SurveyedSpecie specieInEditPresente;    // diversa da null se la specie è presente nello strato in edit
	private SurveyedSpecie specieInEditNonPresente; // semplice copia della specie in edit (non presente nello strato in edit)
	
	private JRadioButton specieCoprente = new JRadioButton(Icone.PlotSpecieCoprenteNo);
	private JRadioButton specieRadicata = new JRadioButton(Icone.PlotSpecieRadicataNo);
		
	public LevelEditorPresenzaAssenza(){
		this.setLayout(new GridBagLayout());
		this.setOpaque(false);
		
		this.add(specieCoprente, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, 	CostantiGUI.insetsGruppo, 0, 0));
		this.add(specieRadicata, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, 	CostantiGUI.insetsGruppo, 0, 0));
		
		specieCoprente.setSelectedIcon(Icone.PlotSpecieCoprente);
		specieRadicata.setSelectedIcon(Icone.PlotSpecieRadicata);
		specieCoprente.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				azione_cambiaStato(e);
			}
		});
		specieRadicata.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				azione_cambiaStato(e);
			}
		});
		specieCoprente.setOpaque(false);
		specieRadicata.setOpaque(false);
	}
	
	/************************************************************************
	 * Questo componente modifica direttamente i livelli passati come argomento
	 * Se è presente un solo livello, quello sarà messo in fase di edit
	 * @param l l'array di elementi da editare
	 ***********************************************************************/
	public void setLevels(Level[] l) {
		livelli = l;
		if(l.length==1)
			indiceLivelloInEdit = 0;
		else
			indiceLivelloInEdit = -1;
		specieInEditPresente = null;
		specieInEditNonPresente = null;
	}
	
	/************************************************************************
	 * @return i livelli presenti in questo componente
	 ***********************************************************************/
	public Level[] getLevels() {
		return livelli;
	}
	
	/************************************************************************
	 * Elabora il cambiamento di stato di una specie
	 * @param evento
	 ***********************************************************************/
	void azione_cambiaStato(ActionEvent evento){
		if(evento.getSource() == specieCoprente){
			if(specieCoprente.isSelected()){
				specieRadicata.setSelected(false);
				impostaCopertura(TipoIncidenza.COPRENTE);
			}else{
				impostaCopertura(TipoIncidenza.ASSENTE);
			}
		}
		if(evento.getSource() == specieRadicata){
			if(specieRadicata.isSelected()){
				specieCoprente.setSelected(false);
				impostaCopertura(TipoIncidenza.RADICATA);
			}else{
				impostaCopertura(TipoIncidenza.ASSENTE);
			}
		}
	}
	
	/************************************************************************
	 * @param nuovoStato una delle costanti INCIDENZA_*
	 ***********************************************************************/
	public void impostaCopertura(TipoIncidenza nuovoStato){    	        
        Stato.debugLog.fine("cambio l'incidenza per "+specieInEditPresente+" ("+specieInEditNonPresente+")");
        if(nuovoStato == TipoIncidenza.ASSENTE){
        	if(specieInEditPresente!=null){
    			livelli[indiceLivelloInEdit].removeSurveyedSpecie(specieInEditPresente);
    		}
        }else{
        	if(specieInEditPresente==null){
    			// TODO: dovrebbe copiare solo alcuni campi dalla specie originale
    			specieInEditPresente = new SurveyedSpecie(specieInEditNonPresente);
    			livelli[indiceLivelloInEdit].addSurveyedSpecie(specieInEditPresente);
    			Stato.debugLog.fine("Ho inserito la specie "+specieInEditPresente);
    		}
			if(nuovoStato == TipoIncidenza.RADICATA){
				specieInEditPresente.setIncidence("rooted");
			}else{
				specieInEditPresente.setIncidence("covering");
			}
        }
        for (ChangeListener ascoltatore : ascoltatoriCambiamenti) {
			ascoltatore.stateChanged(null);
		}
	}

	@Override
	/************************************************************************
	 * @param l in cui cercare la specie
	 ***********************************************************************/
	public void setLivelloInEdit(Level l) {
		indiceLivelloInEdit = -1;
        for(int i=0 ; i<livelli.length ; i++)
            if( livelli[i].getId().equals(l.getId()) ){
            	indiceLivelloInEdit = i;
                break;
            }
        if(indiceLivelloInEdit == -1){
            Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Impossibile trovare lo strato \""+l.getId()+"\" contattare l'assistenza.","it", MessageType.ERROR));
        }
        specieInEditPresente = null;
        specieInEditNonPresente = null;
	}

	@Override
	/************************************************************************
	 * @param ss la spcie da editare
	 ***********************************************************************/
	public void setSpecieInEdit(SurveyedSpecie ss) {
		specieInEditPresente = null;
		Stato.debugLog.fine("Cerco la specie "+ss+" nello strato "+livelli[indiceLivelloInEdit]);
    	for(int i=0 ; i<livelli[indiceLivelloInEdit].getSurveyedSpecieCount() ; i++){
            if( UtilitaVegetazione.stessoNomeSurveyedSpecie(livelli[indiceLivelloInEdit].getSurveyedSpecie(i), ss) ){
            	specieInEditPresente = livelli[indiceLivelloInEdit].getSurveyedSpecie(i);
            	Stato.debugLog.fine("trovata");
                break;
            }
    	}
    	// visualizzo lo stato della specie
    	if(specieInEditPresente == null){
    		// non ho trovato la specie => assente
    		specieCoprente.setSelected(false);
    		specieRadicata.setSelected(false);
    		specieInEditNonPresente = ss;
    	}else{
    		if(specieInEditPresente.getIncidence().equals("covering")){
    			specieCoprente.setSelected(true);
    			specieRadicata.setSelected(false);
    		}else{
    			specieCoprente.setSelected(false);
    			specieRadicata.setSelected(true);
    		}
    	}
	}
	
	@Override
	public void rimuoviSpecie(Level l, SurveyedSpecie s) throws SistemaException{
		boolean qualcosaModificato=false;
        for(int i=0 ; i<livelli.length ; i++){
            if( l==null || livelli[i].getId().equals(l.getId()) ){
                livelli[i].removeSurveyedSpecie(s);
                for (ChangeListener ascoltatore : ascoltatoriCambiamenti) {
                    ascoltatore.stateChanged(null);
                }
                qualcosaModificato = true;
            }
        }
        if(!qualcosaModificato){
            Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Impossibile trovare lo strato \""+l.getId()+"\" contattare l'assistenza.", "it", MessageType.ERROR));
        }else{
            this.updateUI();
        }
	}

	@Override
	public SurveyedSpecieSet getSpecieSet() {
		int i,j;
		SurveyedSpecieSet sss = new SurveyedSpecieSet();
		
		for(i=0 ; i<livelli.length ; i++){
			for(j=0 ; j<livelli[i].getSurveyedSpecieCount() ; j++){
				sss.add(livelli[i].getSurveyedSpecie(j));
			}
		}
		return sss;
	}

	@Override
	public void addSurveyedSpecieListener(SurveyedSpecieListener ssl) {
		// TODO Auto-generated method stub
	}
	
    @Override
    public String getLevelsSchema() {
        if(livelli==null || livelli.length==0){
            return null;
        }else{
            StringBuffer sb = new StringBuffer();
            for(int i=0; i<livelli.length ; i++){
                sb.append(Proprieta.PREFISSO_STRATI);
                sb.append(livelli[i].getId());
                sb.append(Proprieta.SEPARATORE_STRATI_ID_NOME);
                sb.append(livelli[i].getName());
            }
            return sb.toString();
        }
    }

}
