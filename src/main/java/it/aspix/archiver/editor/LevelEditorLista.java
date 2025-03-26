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
import it.aspix.archiver.componenti.liste.ModelloListaSurveyedSpecie;
import it.aspix.archiver.componenti.liste.RenderSurveyedSpecie;
import it.aspix.archiver.dialoghi.DatiSurveyedSpecie;
import it.aspix.archiver.eventi.SistemaException;
import it.aspix.archiver.eventi.SurveyedSpecieCambiata;
import it.aspix.archiver.eventi.SurveyedSpecieListener;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class LevelEditorLista extends LevelEditor{

    private static final long serialVersionUID = 1L;
    
    ArrayList<SurveyedSpecieListener> ascoltatoriSpecieInEdit = new ArrayList<SurveyedSpecieListener>();
    Level livelloOriginale;
    
    GridBagLayout lThis = new GridBagLayout();
    JScrollPane scrollLivelli = new JScrollPane();
    ModelloListaSurveyedSpecie modelloLista = new ModelloListaSurveyedSpecie();
    JList listaSpecie = new JList(modelloLista);
    SurveyedSpecieEditor editorSpecierilevata;
    JButton aggiungi = new JButton();
    
    JPopupMenu popupSpecie = new JPopupMenu();
    JMenuItem menuModificaSpecie = new JMenuItem("modifica specie");
    JMenuItem menuEliminaSpecie = new JMenuItem("elimina specie");
    JMenuItem menuOrdinaAlberoS = new JMenuItem("ordina specie");

    public LevelEditorLista(){
        editorSpecierilevata = new SurveyedSpecieEditor(SurveyedSpecieEditor.Layout.VERTICALE_COMPRESSO);
        aggiungi.setText("aggiungi");
        // -------------------- layout --------------------
        this.setLayout(lThis);
        this.add(scrollLivelli,        new GridBagConstraints(0,0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,  GridBagConstraints.BOTH,       CostantiGUI.insetsGruppo,  0, 0));
        this.add(editorSpecierilevata, new GridBagConstraints(0,1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,  GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo,  0, 0));
        this.add(aggiungi,             new GridBagConstraints(0,2, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,    GridBagConstraints.NONE,       CostantiGUI.insetsGruppo,  0, 0));
        scrollLivelli.getViewport().add(listaSpecie);
        // ------------------- menu --------------------
        // popupSpecie.add(menuModificaSpecie); TODO: da riflettere sulla semantica
        popupSpecie.add(menuEliminaSpecie);
        popupSpecie.addSeparator();
        popupSpecie.add(menuOrdinaAlberoS);
        // ------------------- ascoltatori --------------------
        aggiungi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { aggiungiSpecie_actionPerformed(); }
        });
        menuModificaSpecie.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { modificaSpecie_actionPerformed(); }
        });
        menuEliminaSpecie.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { rimuoviSpecie_actionPerformed(); }
        });
        menuOrdinaAlberoS.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { ordina(); }
        });
        listaSpecie.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                if(e.isPopupTrigger()){
                    popupSpecie.show((Component)e.getSource(),e.getX(),e.getY());
                }
            }
            public void mouseReleased(MouseEvent e){
                if(e.isPopupTrigger()){
                    popupSpecie.show((Component)e.getSource(),e.getX(),e.getY());
                }
            }
        });
        listaSpecie.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				cambiataSelezione_actionPerformed();
			}
        });
        listaSpecie.setCellRenderer(new RenderSurveyedSpecie());
    }
    
	/************************************************************************
	 * Questo componente modifica direttamente i livelli passati come argomento
	 * Se è presente un solo livello, tutto bene altrimenti l'edit non può funzionare
	 * @param l l'array di elementi da editare
	 * @throws ValoreException 
	 ***********************************************************************/
	public void setLevels(Level[] l) throws ValoreException {
		if(l.length!=1)
			throw new ValoreException("Questo componente può editare un solo livello.");
		modelloLista.removeAllElements();
		for(int i=0 ; i<l[0].getSurveyedSpecieCount() ; i++)
			modelloLista.addSurveyedSpecie(l[0].getSurveyedSpecie(i));
		livelloOriginale = l[0].clone();
		livelloOriginale.removeAllSurveyedSpecie();
	}
    
    /****************************************************************************
     * Modifica una specie dell'albero
     * TODO: questa funzione non è attiva (non viene chiamata)
     ***************************************************************************/
    protected void modificaSpecie_actionPerformed() {
        SurveyedSpecie ss = (SurveyedSpecie) listaSpecie.getSelectedValue();
        Stato.debugLog.fine("Chiesta la modifica di '"+ss+"'");
        DatiSurveyedSpecie esr = new DatiSurveyedSpecie(listaSpecie ,ss);
        esr.setVisible(true);
        SurveyedSpecie ssm=esr.getSurveyedSpecie();
        if(ssm!=null){
            if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, "Vuoi che la specie sia modificata anche nei sottoelementi?", 
                    "Propagazione modifica", JOptionPane.YES_NO_OPTION)){
                SurveyedSpecieCambiata evento;
            	copiaDatiSurveyedSpecie(ss,ssm);
                evento = new SurveyedSpecieCambiata(ssm);
                evento.setVecchiaSpecieRilevata(ss);
                for (SurveyedSpecieListener ascoltatore : ascoltatoriSpecieInEdit) {
    				ascoltatore.specieModificata( evento );
    			}
            }
        }
        listaSpecie.updateUI();
    }
    
    /****************************************************************************
     * Rimuove una specie dall'albero
     ***************************************************************************/
    protected void rimuoviSpecie_actionPerformed() {
        int indice = listaSpecie.getSelectedIndex();
        Stato.debugLog.fine("Chiesta la rimozione dell'elemento "+indice);
        if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, "Vuoi che la specie sia rimossa anche dai sottoelementi?", 
                "Propagazione rimozione", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Icone.MessageDomanda)){
            SurveyedSpecieCambiata evento;
            evento = new SurveyedSpecieCambiata(modelloLista.getElementAt(indice));
            for (SurveyedSpecieListener ascoltatore : ascoltatoriSpecieInEdit) {
                ascoltatore.specieRimossa( evento );
    		}
        }
        modelloLista.removeElement(indice);
        listaSpecie.setSelectedIndex(0);
        cambiataSelezione_actionPerformed();
        listaSpecie.updateUI();
    }
    
    /****************************************************************************
     * Aggiunge una specie all'albero delle specie
     ***************************************************************************/
    protected void aggiungiSpecie_actionPerformed() {
        SurveyedSpecie ss;
        ss = editorSpecierilevata.getSurveyedSpecie();
        if( !(ss.getDetermination().equals("sure")) && ss.getSampleId().length()<1 ){
            Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Hai inserito una specie incerta senza riferimento al campione.","it",MessageType.ERROR));
        }
        modelloLista.addSurveyedSpecie(ss);
        for (SurveyedSpecieListener ascoltatore : ascoltatoriSpecieInEdit) {
            // FIXME: la semantica di "aggiungere" è ben strana
			ascoltatore.specieAggiunta( null );
		}
        listaSpecie.updateUI();
        listaSpecie.setSelectedIndex(modelloLista.getSize()-1);
        listaSpecie.ensureIndexIsVisible(listaSpecie.getSelectedIndex());
    }
    
    /************************************************************************
     * Ordina l'albero dele specie
     ***********************************************************************/
    public void ordina(){
        modelloLista.sort();
        listaSpecie.updateUI();
    }
    
    /************************************************************************
     * @return tutti i livelli presenti in questo editor
     ***********************************************************************/
    public Level[] getLevels(){
    	Level risposta[] = new Level[1];
    	
    	risposta[0] = livelloOriginale!=null ? livelloOriginale.clone() : UtilitaVegetazione.livelloUnico.clone();
    	for(int i=0 ; i<modelloLista.getSize() ; i++){
    		risposta[0].addSurveyedSpecie((SurveyedSpecie) modelloLista.getElementAt(i));
    	}
    	return risposta;
    }
    
    public void cambiataSelezione_actionPerformed(){
    	SurveyedSpecie ss = (SurveyedSpecie) listaSpecie.getSelectedValue();
		for(SurveyedSpecieListener ssl : ascoltatoriSpecieInEdit){
			ssl.specieSelezionata(new SurveyedSpecieCambiata(ss));
		}
    }

	@Override
	public void setLivelloInEdit(Level l) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setSpecieInEdit(SurveyedSpecie ss) {
		// TODO Auto-generated method stub
	}
	
	public void addSurveyedSpecieListener(SurveyedSpecieListener ssl){
		ascoltatoriSpecieInEdit.add(ssl);	
	}
	
	@Override
	public void rimuoviSpecie(Level l, SurveyedSpecie s) throws SistemaException{
		modelloLista.removeSurveyedSpecie(s);
	}

	@Override
	public SurveyedSpecieSet getSpecieSet() {
		int i;
		SurveyedSpecieSet sss = new SurveyedSpecieSet();
		
    	for(i=0 ; i<modelloLista.getSize() ; i++){
    		sss.add((SurveyedSpecie) modelloLista.getElementAt(i));
    	}
		return sss;
	}
	
    @Override
    public String getLevelsSchema() {
        return livelloOriginale==null 
            ? null 
            : (Proprieta.PREFISSO_STRATI+livelloOriginale.getId()+Proprieta.SEPARATORE_STRATI_ID_NOME+livelloOriginale.getName());
    }
}
