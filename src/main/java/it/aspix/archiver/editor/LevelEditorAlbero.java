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
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.componenti.alberi.ModelloAlberoLevelSurveyedSpecie;
import it.aspix.archiver.componenti.alberi.NodoAlberoLevelSurveyedSpecie;
import it.aspix.archiver.componenti.alberi.RenderLevelSurveyedSpecie;
import it.aspix.archiver.componenti.insiemi.SurveyedSpecieSet;
import it.aspix.archiver.dialoghi.DatiSurveyedSpecie;
import it.aspix.archiver.eventi.SistemaException;
import it.aspix.archiver.eventi.SurveyedSpecieCambiata;
import it.aspix.archiver.eventi.SurveyedSpecieListener;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.Link;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.Sample;
import it.aspix.sbd.obj.SpecieRef;
import it.aspix.sbd.obj.Specimen;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class LevelEditorAlbero extends LevelEditor{

    private static final long serialVersionUID = 1L;

    /************************************************************************
     * serve come editor di tutti i campi statici di Level
     ***********************************************************************/
    private class DialogoLevelAttribute extends JDialog{

        private static final long serialVersionUID = 1L;
        
        GridBagLayout lPrincipale = new GridBagLayout();
        JPanel principale = new JPanel(lPrincipale);
        
        JLabel eId = new JLabel();
        JTextField id = new JTextField();        
        JLabel eNome = new JLabel();
        JTextField nome = new JTextField();        
        JLabel eCopertura = new JLabel();
        JTextField copertura = new JTextField();
        JLabel eAltezza = new JLabel();
        JTextField altezza = new JTextField();
        JLabel eAltezzaMin = new JLabel();
        JTextField altezzaMin = new JTextField();
        JLabel eAltezzaMax = new JLabel();
        JTextField altezzaMax = new JTextField();
        JLabel eNote = new JLabel();
        JTextField note = new JTextField();
        GridLayout lPannelloPulsanti = new GridLayout(1,2);
        JPanel pannelloPulsanti = new JPanel(lPannelloPulsanti);
        JButton ok = new JButton();
        JButton annulla = new JButton();
        boolean accettato = false;
        DefaultComboBoxModel modelloScorciatoie = new DefaultComboBoxModel(); 
        JComboBox scorciatoie = new JComboBox(modelloScorciatoie);
        
        /********************************************************************
         * Il nome della funzione è dovuto al fatto che questo componente
         * visualizza solamente gli attributi statici e non le specie di un Level
         * @param l il livello i cui attributi statici vanno visualizzati
         *******************************************************************/
        public void setLevelAttribute(Level l){
            id.setText(l.getId());
            nome.setText(l.getName()); 
            copertura.setText(l.getCoverage());
            altezza.setText(l.getHeight());
            altezzaMin.setText(l.getHeightMin());
            altezzaMax.setText(l.getHeightMax());
            note.setText(l.getNote());
            if(l.getId()!=null){
                modelloScorciatoie.addElement(l.getId()+" sottolivello del livello selezionato");
            }
            scorciatoie.setSelectedIndex(modelloScorciatoie.getSize()-1);
        }

        /********************************************************************
         * @return un Level, senza specie
         *******************************************************************/
        public Level getLevel(){
            Level l = new Level();
            l.setId(id.getText());
            l.setName(nome.getText());
            l.setCoverage(copertura.getText());
            l.setHeight(altezza.getText());
            l.setHeightMin(altezzaMin.getText());
            l.setHeightMax(altezzaMax.getText());
            l.setNote(note.getText());
            return l;
        }
        
        /********************************************************************
         * @return true se la finestra è stata chiusa con OK
         *******************************************************************/
        public boolean isAccettato(){
            return accettato;
        }
        
        public DialogoLevelAttribute(boolean nuovoLivello){
            this.setTitle("dati strato");
            eId.setText("id:");
            eNome.setText("nome:");
            eCopertura.setText("copertura:");
            eAltezza.setText("altezza med:");
            eAltezzaMin.setText("altezza min:");
            eAltezzaMax.setText("altezza max:");
            eNote.setText("note:");
            ok.setText("ok");
            annulla.setText("annulla");
            this.getContentPane().add(principale);
            if(!nuovoLivello){
                principale.add(scorciatoie, new GridBagConstraints(0,0, 4, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 70, 0));
            }
            principale.add(eId,             new GridBagConstraints(0,1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta,  0, 0));
            principale.add(id,              new GridBagConstraints(1,1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 70, 0));
            principale.add(eNome,           new GridBagConstraints(2,1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta,  0, 0));
            principale.add(nome,            new GridBagConstraints(3,1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 70, 0));
            principale.add(eCopertura,      new GridBagConstraints(4,1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta,  0, 0));
            principale.add(copertura,       new GridBagConstraints(5,1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 70, 0));
            principale.add(eAltezzaMin,     new GridBagConstraints(0,2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta,  0, 0));
            principale.add(altezzaMin,      new GridBagConstraints(1,2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 70, 0));
            principale.add(eAltezza,        new GridBagConstraints(2,2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta,  0, 0));
            principale.add(altezza,         new GridBagConstraints(3,2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 70, 0));
            principale.add(eAltezzaMax,     new GridBagConstraints(4,2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta,  0, 0));
            principale.add(altezzaMax,      new GridBagConstraints(5,2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 70, 0));
            principale.add(eNote,           new GridBagConstraints(0,3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta,  0, 0));
            principale.add(note,            new GridBagConstraints(1,3, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 70, 0));
            principale.add(pannelloPulsanti,new GridBagConstraints(0,4, 4, 1, 1.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsAzioneInBarra, 70, 0));
            pannelloPulsanti.add(ok);
            pannelloPulsanti.add(annulla);
            annulla.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) { annullaStrato_actionPerformed(); }
            });
            ok.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) { okStrato_actionPerformed(); }
            });
            if(!nuovoLivello){
                scorciatoie.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) { scorciatoie_actionPerformed(); }
                });
            }
            this.pack();
            this.setModal(true);
        }

        public void annullaStrato_actionPerformed(){
            accettato = false;
            this.dispose();
        }
        public void okStrato_actionPerformed(){
            accettato = true;
            this.dispose();
        }
        public void scorciatoie_actionPerformed(){
            String selezionata = (String) scorciatoie.getSelectedItem();
            int spazio = selezionata.indexOf(' ');
            String prima = selezionata.substring(0,spazio);
            String dopo = selezionata.substring(spazio+1);
            if(prima.length()!=1){
                dopo="NOME";
            }
            id.setText(prima);
            nome.setText(dopo);
        }
    }
    
    ModelloAlberoLevelSurveyedSpecie msr = new ModelloAlberoLevelSurveyedSpecie();
    ArrayList<SurveyedSpecieListener> ascoltatoriSpecieInEdit = new ArrayList<SurveyedSpecieListener>();
    ArrayList<ChangeListener> ascoltatoriCambiamenti = new ArrayList<ChangeListener>();
    
    GridBagLayout lThis = new GridBagLayout();
    JScrollPane scrollLivelli = new JScrollPane();
    JTree alberoLivelli = new JTree();
    SurveyedSpecieEditor editorSpecierilevata;
    JButton aggiungi = new JButton();
    
    JPopupMenu popupSpecie = new JPopupMenu();
    JMenuItem menuModificaSpecie = new JMenuItem("modifica specie");
    JMenuItem menuEliminaSpecie = new JMenuItem("elimina specie");
    JMenuItem menuOrdinaAlberoS = new JMenuItem("ordina strati");
    JMenuItem menuCreaCartellino = new JMenuItem("crea cartellino...");
    JPopupMenu popupStrato = new JPopupMenu();
    JMenuItem menuAggiungiStrato = new JMenuItem("aggiungi strato...");
    JMenuItem menuModificaStrato = new JMenuItem("modifica strato...");
    JMenuItem menuEliminaStrato = new JMenuItem("elimina strato");
    JMenuItem menuOrdinaAlbero = new JMenuItem("ordina strati");
    JMenuItem menuRegistraStratificazione = new JMenuItem("registra stratificazione");
    JPopupMenu popupRoot = new JPopupMenu();
    JMenuItem menuAggiungiStratoRoot = new JMenuItem("aggiungi strato...");
    
    public LevelEditorAlbero(){
        editorSpecierilevata = new SurveyedSpecieEditor(SurveyedSpecieEditor.Layout.VERTICALE);
        aggiungi.setText("aggiungi");
        // -------------------- layout --------------------
        this.setLayout(lThis);
        this.add(scrollLivelli,        new GridBagConstraints(0,0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,  GridBagConstraints.BOTH,       CostantiGUI.insetsGruppo,  0, 0));
        this.add(editorSpecierilevata, new GridBagConstraints(0,1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,  GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo,  0, 0));
        this.add(aggiungi,             new GridBagConstraints(0,2, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,    GridBagConstraints.NONE,       CostantiGUI.insetsGruppo,  0, 0));
        scrollLivelli.getViewport().add(alberoLivelli);
        // ------------------- menu --------------------
        popupSpecie.add(menuModificaSpecie);
        popupSpecie.add(menuEliminaSpecie);
        popupSpecie.addSeparator();
        popupSpecie.add(menuOrdinaAlberoS);
        popupSpecie.add(menuCreaCartellino);
        popupStrato.add(menuAggiungiStrato);
        popupStrato.add(menuModificaStrato);
        popupStrato.add(menuEliminaStrato);
        popupStrato.addSeparator();
        popupStrato.add(menuOrdinaAlbero);
        popupRoot.add(menuAggiungiStratoRoot);
        // ------------------- ascoltatori --------------------
        editorSpecierilevata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                if(Proprieta.recupera("vegetazione.velocizzaTastiera").equals("true")){
                    aggiungiSpecie_actionPerformed();
                }
            }
        });
        aggiungi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { aggiungiSpecie_actionPerformed(); }
        });
        menuModificaSpecie.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { modificaSpecie_actionPerformed(); }
        });
        menuEliminaSpecie.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { rimuoviSpecie_actionPerformed(); }
        });
        menuCreaCartellino.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { creaCartellino_actionPerformed(); }
        });
        menuAggiungiStrato.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { aggiungiStrato_actionPerformed(); }
        });
        menuModificaStrato.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { modificaStrato_actionPerformed(); }
        });
        menuEliminaStrato.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { eliminaStrato_actionPerformed(); }
        });
        menuOrdinaAlbero.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { ordinaAlbero_actionPerformed(); }
        });
        menuOrdinaAlberoS.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { ordinaAlbero_actionPerformed(); }
        });
        menuRegistraStratificazione.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { registraStratificazione_actionPerformed(); }
        });
        menuAggiungiStratoRoot.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { aggiungiStrato_actionPerformed(); }
        });
        alberoLivelli.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                if(e.isPopupTrigger()){
                    if(alberoLivelli.getSelectionPath()==null){
                        popupRoot.show((Component)e.getSource(),e.getX(),e.getY());
                    }else{
                        NodoAlberoLevelSurveyedSpecie orig=(NodoAlberoLevelSurveyedSpecie)(alberoLivelli.getSelectionPath().getLastPathComponent());
                        if(orig.isStrato()){
                            popupStrato.show((Component)e.getSource(),e.getX(),e.getY());
                        }else{
                            popupSpecie.show((Component)e.getSource(),e.getX(),e.getY());
                        }
                    }
                }
            }
            public void mouseReleased(MouseEvent e){
                if(e.isPopupTrigger()){
                    if(alberoLivelli.getSelectionPath()==null){
                        popupRoot.show((Component)e.getSource(),e.getX(),e.getY());
                    }else{
                        NodoAlberoLevelSurveyedSpecie orig=(NodoAlberoLevelSurveyedSpecie)(alberoLivelli.getSelectionPath().getLastPathComponent());
                        if(orig.isStrato()){
                            popupStrato.show((Component)e.getSource(),e.getX(),e.getY());
                        }else{
                            popupSpecie.show((Component)e.getSource(),e.getX(),e.getY());
                        }
                    }
                }
            }
        });
        alberoLivelli.addTreeSelectionListener(new TreeSelectionListener(){
            public void valueChanged(TreeSelectionEvent e) {
            	NodoAlberoLevelSurveyedSpecie o = (NodoAlberoLevelSurveyedSpecie) e.getNewLeadSelectionPath().getLastPathComponent();
                if(o.specie != null) {
	                for (SurveyedSpecieListener ascoltatore : ascoltatoriSpecieInEdit) {
						ascoltatore.specieSelezionata(new SurveyedSpecieCambiata(o.specie) );
					}
                }
            }
        });
        // ------------------- impostazioni --------------------
        alberoLivelli.setModel(msr);
        alberoLivelli.setRootVisible(false);
        alberoLivelli.setCellRenderer(new RenderLevelSurveyedSpecie());
        alberoLivelli.setRowHeight(0);
        alberoLivelli.setToggleClickCount(1);
    }
    
    /************************************************************************
     * I livelli vengono aggiunti (add) e non impostati (set), 
     * esternamente i livelli non hanno annidamento
     ***********************************************************************/
    public void setLevels(Level livello[]){
    	int indiceLivello;
    	int posizionePunto;
    	int i;
    	
    	msr.removeAll();
        // aggiungo il livello stesso all'albero
    	for(indiceLivello=0 ; indiceLivello<livello.length ; indiceLivello++){
	        posizionePunto = livello[indiceLivello].getId().lastIndexOf('.');
	        NodoAlberoLevelSurveyedSpecie padre;
	        NodoAlberoLevelSurveyedSpecie figlio;
	        if(posizionePunto==-1){
	            padre = null;
	        }else{
	            padre = msr.cercaStratoPerId(livello[indiceLivello].getId().substring(0,posizionePunto));
	        }
	        figlio = new NodoAlberoLevelSurveyedSpecie(livello[indiceLivello].clone());
	        msr.addChild(padre, figlio);
	        // aggiungo le singole specie
	        NodoAlberoLevelSurveyedSpecie sp;
	        for(i=0 ; i<livello[indiceLivello].getSurveyedSpecieCount() ; i++){
	            sp = new NodoAlberoLevelSurveyedSpecie(livello[indiceLivello].getSurveyedSpecie(i));
	            msr.addChild(figlio, sp);
	        }
    	}
    	// se richiesto dalle preferenze gli strati vanno ordinati
    	if(Proprieta.isTrue("vegetazione.ordinamentoStratiAutomatico")){
    	    ordinaAlbero_actionPerformed();
    	}
        alberoLivelli.updateUI();
        UtilitaGui.expandAll(alberoLivelli, new TreePath(msr.getRoot()), true, UtilitaGui.ESPANDI_TUTTI_I_LIVELLI);
        Sample s;
        try {
            s = getSampleEditorStandard().getSample();
        } catch (ValoreException e) {
        	// problemi durante il recupero del rilievo visualizzato
        	Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Non riesco a recuperare i dati del rilievo visualizzato", "it", MessageType.ERROR));
        	s = null;
        } catch (NullPointerException e){
        	// lo strato non è inserito in un rilievo e quindi non posso prendere l'id
        	s = null;
        }
        menuCreaCartellino.setEnabled(s!=null && s.getId()!=null && s.getId().length()>0 );
    }
    
    /****************************************************************************
     * Modifica una specie dell'albero
     ***************************************************************************/
    protected void modificaSpecie_actionPerformed() {
        TreePath cammino= alberoLivelli.getSelectionPath();
        Stato.debugLog.fine("Chiesta la modifica di '"+cammino.getLastPathComponent()+"'");
        NodoAlberoLevelSurveyedSpecie nodo = (NodoAlberoLevelSurveyedSpecie)(cammino.getLastPathComponent());
        DatiSurveyedSpecie esr = new DatiSurveyedSpecie(alberoLivelli ,nodo.specie);
        esr.setVisible(true);
        SurveyedSpecie ssm=esr.getSurveyedSpecie();
        if(ssm!=null){
        	copiaDatiSurveyedSpecie(nodo.specie, ssm);
            for (ChangeListener ascoltatore : ascoltatoriCambiamenti) {
				ascoltatore.stateChanged(null);
			}
        }
        alberoLivelli.updateUI();
    }
    
    /****************************************************************************
     * Rimuove una specie dall'albero
     ***************************************************************************/
    protected void rimuoviSpecie_actionPerformed() {
        TreePath cammino = alberoLivelli.getSelectionPath();
        Stato.debugLog.fine("Chiesta rimozione di '"+cammino.getLastPathComponent()+"'");
        msr.removeNode((NodoAlberoLevelSurveyedSpecie)(cammino.getLastPathComponent()));
        for (ChangeListener ascoltatore : ascoltatoriCambiamenti) {
			ascoltatore.stateChanged(null);
		}
        alberoLivelli.updateUI();
    }
    
    /************************************************************************
     * Aggiunge uno strato all'albero dele specie
     ***********************************************************************/
    protected void aggiungiStrato_actionPerformed() {
        DialogoLevelAttribute dl=new DialogoLevelAttribute(false);
        TreePath camminoSelezione;
        Level nuovo;
        NodoAlberoLevelSurveyedSpecie nodoPadre;
        NodoAlberoLevelSurveyedSpecie orig = null;
        
        dl.setLocationRelativeTo(alberoLivelli);
        camminoSelezione = alberoLivelli.getSelectionPath();
        nuovo = new Level();
        if(camminoSelezione!=null){
            // sto creando un sottolivello
            orig = (NodoAlberoLevelSurveyedSpecie)(camminoSelezione.getLastPathComponent());
            if(orig.strato.getId().length()>1){
                JOptionPane.showMessageDialog(this,"Non \u00e8 concesso inserire sottolivelli di sottolivelli.","troppe sottodivisioni",JOptionPane.ERROR_MESSAGE);
                return;
            }
            nuovo.setId(orig.strato.getId()+".");
        }else{
            nuovo.setId("");
        }
        dl.setLevelAttribute(nuovo);
        dl.setVisible(true);
        if(dl.isAccettato()){
            if(msr.cercaStratoPerId(dl.getLevel().getId())!=null){
                JOptionPane.showMessageDialog(this,"Esiste gia' uno strato con lo stesso id.","Id errato",JOptionPane.ERROR_MESSAGE);
                return;             
            }
            if(dl.getLevel().getId().length()<1 || dl.getLevel().getId().length()==2 || dl.getLevel().getId().length()>3 || !Character.isDigit(dl.getLevel().getId().charAt(0))){
                // gli id di lunghezza minore di 1 non vanno bene
                // gli id di lunghezza 2 non vanno bene
                // gli id di lunghezza maggiore di 3 non vanno bene
                // il primo carattere deve essere un numero
                JOptionPane.showMessageDialog(this,"Id dello strato non valido.","Id errato",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(dl.getLevel().getId().length()>2 && dl.getLevel().getId().charAt(0)!=orig.strato.getId().charAt(0) && dl.getLevel().getId().charAt(1)!='.'){
                JOptionPane.showMessageDialog(this,"Id dello strato non valido.","Id errato",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(dl.getLevel().getId().length()==1){
                nodoPadre = null;
            }else{
                nodoPadre = orig;
            }
            // costruisco il nodo figlio
            NodoAlberoLevelSurveyedSpecie nas = new NodoAlberoLevelSurveyedSpecie(dl.getLevel());
            try {
                msr.addChild(nodoPadre,nas);
                alberoLivelli.expandPath(msr.buildPathfor(nas));
                alberoLivelli.setRowHeight(0);
                alberoLivelli.updateUI();
            } catch (Exception e1) {
                Dispatcher.consegna(this, e1);
            }
        }
        // se richiesto dalle preferenze gli strati vanno ordinati
        if(Proprieta.isTrue("vegetazione.ordinamentoStratiAutomatico")){
            ordinaAlbero_actionPerformed();
        }
        alberoLivelli.setRowHeight(0);
    }
    
    /************************************************************************
     * Aggiunge uno strato all'albero dele specie
     ***********************************************************************/
    protected void modificaStrato_actionPerformed() {
        DialogoLevelAttribute dl = new DialogoLevelAttribute(true);
        dl.setLocationRelativeTo(alberoLivelli);
        NodoAlberoLevelSurveyedSpecie orig=(NodoAlberoLevelSurveyedSpecie)(alberoLivelli.getSelectionPath().getLastPathComponent());
        dl.setLevelAttribute(orig.strato);
        dl.setVisible(true);
        if(dl.getLevel()!=null){
            // costruisco il nodo figlio
            Level nuovo=dl.getLevel();
            // verifica se è possibile cambiare l'id
            if(!orig.strato.getId().equals(nuovo.getId()) && msr.cercaStratoPerId(nuovo.getId())!=null){
                JOptionPane.showMessageDialog(this,"Esiste gia' uno strato con lo stesso id.","Id errato",JOptionPane.ERROR_MESSAGE);
                return;   
            }else{
            	orig.strato.setId(nuovo.getId());
            }
            // aggiorna il nodo
            orig.strato.setHeight(nuovo.getHeight());
            orig.strato.setHeightMin(nuovo.getHeightMin());
            orig.strato.setHeightMax(nuovo.getHeightMax());
            orig.strato.setCoverage(nuovo.getCoverage());
            orig.strato.setName(nuovo.getName());
            orig.strato.setNote(nuovo.getNote());
            alberoLivelli.setRowHeight(0);
            alberoLivelli.updateUI();
        }
        // se richiesto dalle preferenze gli strati vanno ordinati
        if(Proprieta.isTrue("vegetazione.ordinamentoStratiAutomatico")){
            ordinaAlbero_actionPerformed();
        }
        alberoLivelli.setRowHeight(0);
    }
    
    /************************************************************************
     * Aggiunge uno strato all'albero dele specie
     ***********************************************************************/
    protected void eliminaStrato_actionPerformed() {
        NodoAlberoLevelSurveyedSpecie orig = (NodoAlberoLevelSurveyedSpecie)(alberoLivelli.getSelectionPath().getLastPathComponent());
        if(orig.strato.getId().length()==1){
            JOptionPane.showMessageDialog(this,"Non \u00e8 possibile eliminare i livelli principali","eliminazione scorretta",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(orig.figli.size()>0){
            JOptionPane.showMessageDialog(this,"Non \u00e8 possibile eliminare livelli che contengono specie","eliminazione scorretta",JOptionPane.ERROR_MESSAGE);
            return;
        }
        msr.removeNode(orig);
        alberoLivelli.setRowHeight(0);
        alberoLivelli.updateUI();
    }
    
    /************************************************************************
     * Ordina l'albero dele specie
     ***********************************************************************/
    protected void ordinaAlbero_actionPerformed() {
        msr.sort();
        alberoLivelli.updateUI();
    }
    
    /************************************************************************
     * Registra la stratificazione nelle preferenze
     ***********************************************************************/
    protected void registraStratificazione_actionPerformed() {
        String schema = getLevelsSchema();
        Proprieta.aggiorna("vegetazione.schemaStrati", schema);
        Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Schema per i nuovi rilievi salvato.", "it", MessageType.INFO) );
    }
    
    /****************************************************************************
     * Aggiunge una specie all'albero delle specie
     ***************************************************************************/
    protected void aggiungiSpecie_actionPerformed() {
        TreePath cammino;
        NodoAlberoLevelSurveyedSpecie nodoLivello;
        NodoAlberoLevelSurveyedSpecie nodoSpecie;
        SurveyedSpecie ss;

        try {
            cammino =  alberoLivelli.getSelectionPath();
            if(cammino==null){
                throw new Exception("Non è stato selezionato nessuno strato.");
            }
            Stato.debugLog.fine("Nodi nel cammino di selezione: " + cammino.getPathCount());
            nodoLivello = (NodoAlberoLevelSurveyedSpecie) (cammino.getLastPathComponent());
            msr.buildPathfor(nodoLivello);
            Stato.debugLog.fine("Livello selezionato '" + nodoLivello + "'");
            if(!nodoLivello.isStrato())
                nodoLivello = (NodoAlberoLevelSurveyedSpecie) nodoLivello.getParent();
            Stato.debugLog.fine("Livello selezionato '" + nodoLivello + "'");
            ss = editorSpecierilevata.getSurveyedSpecie();
            // controllo che la specie se non sicura abbia un id
            if( !(ss.getDetermination().equals("sure")) && ss.getSampleId().length()<1 ){
                Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Hai inserito una specie incerta senza riferimento al campione.","it",MessageType.ERROR));
            }
            nodoSpecie = new NodoAlberoLevelSurveyedSpecie(ss);
            // controllo che la specie non sia già presente nello strato
            for(NodoAlberoLevelSurveyedSpecie na: nodoLivello.figli){
            	if(na.specie!=null){
            		if(na.specie.sameSurveyedSpecie(ss)){
            			Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Questa specie è già presente in questo strato.","it",MessageType.ERROR));
            			return;
            		}
            	}
            }
            msr.addChild(nodoLivello, nodoSpecie);
            alberoLivelli.expandPath(msr.buildPathfor(nodoSpecie));
            alberoLivelli.setSelectionPath(msr.buildPathfor(nodoSpecie));
            alberoLivelli.updateUI();
            alberoLivelli.scrollPathToVisible(msr.buildPathfor(nodoSpecie));
            for (ChangeListener ascoltatore : ascoltatoriCambiamenti) {
				ascoltatore.stateChanged(null);
			}
            if(Proprieta.recupera("vegetazione.velocizzaTastiera").equals("true")){
            	SurveyedSpecie sspe = new SurveyedSpecie();
            	sspe.setLocked(Proprieta.recupera("vegetazione.bloccaNomeSpecie"));
                editorSpecierilevata.setSurveyedSpecie(sspe);
                editorSpecierilevata.grabFocus();
            }
        } catch (Exception ex) {
            Dispatcher.consegna(this, ex);
        }
        // se richiesto dalle preferenze gli strati vanno ordinati
        if(Proprieta.isTrue("vegetazione.ordinamentoStratiAutomatico")){
            ordinaAlbero_actionPerformed();
        }
        alberoLivelli.setRowHeight(0);
    }
    
    /************************************************************************
     * Crea un cartellino a partire da un rilievo e una specie selezionata
     ***********************************************************************/
    protected void creaCartellino_actionPerformed() {
        TreePath cammino;
        NodoAlberoLevelSurveyedSpecie ultimo;
        Sample s;
        
        Stato.debugLog.fine("Richiesta creazione cartellino.");
        cammino = alberoLivelli.getSelectionPath();
        ultimo = (NodoAlberoLevelSurveyedSpecie) cammino.getLastPathComponent();
        try {
            s = getSampleEditorStandard().getSample();
            Specimen cartellino = new Specimen();
            cartellino.setPlace(s.getPlace());
            cartellino.setSpecieRef(new SpecieRef(ultimo.specie.getSpecieRefName(), ultimo.specie.getSpecieRefAliasOf()));
            cartellino.setDirectoryInfo(CostruttoreOggetti.createDirectoryInfo());
            cartellino.getDirectoryInfo().setContainerName(Proprieta.recupera("herbaria.database"));
            cartellino.setLegitName(s.getSurveyer());
            cartellino.setLegitDate(s.getDate());
            // TODO: mancano altri campi
            DirectoryInfo infoCartellinoInserito = Dispatcher.insert(cartellino);
            if(infoCartellinoInserito!=null){
                Link nuovoLink = new Link();
                nuovoLink.setToName(infoCartellinoInserito.getContainerName());
                nuovoLink.setToSeqNo(infoCartellinoInserito.getContainerSeqNo());
                getSampleEditorStandard().addLink(nuovoLink);
            }
        } catch (ValoreException e) {
            Dispatcher.consegna(this, e);
        }
    }
    
    /************************************************************************
     * @return il rilievo che contiene questa statificazione
     ***********************************************************************/
    private SampleEditorDaEstendere getSampleEditorStandard(){
        Container padre = this;
        while(padre.getParent()!=null && !(padre instanceof it.aspix.archiver.editor.SampleEditor)){
            padre = padre.getParent();
        }
        if(padre instanceof SampleEditorDaEstendere)
        	return (SampleEditorDaEstendere) padre;
        else
        	return null;
    }
    
    @Override
    public Level[] getLevels(){
    	return msr.getLevels();
    }

	@Override
	public void setLivelloInEdit(Level l) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setSpecieInEdit(SurveyedSpecie ss) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void addSurveyedSpecieListener(SurveyedSpecieListener ssl){
		ascoltatoriSpecieInEdit.add(ssl);	
	}
	
	@Override
	public void rimuoviSpecie(Level l, SurveyedSpecie s) throws SistemaException{
		msr.removeSpecie(l,s);
		alberoLivelli.updateUI();
	}
	
	@Override
	public SurveyedSpecieSet getSpecieSet() {
		int i,j;
		SurveyedSpecieSet sss = new SurveyedSpecieSet();
		Level livelli[] = msr.getLevels();
		
		for(i=0 ; i<livelli.length ; i++){
			for(j=0 ; j<livelli[i].getSurveyedSpecieCount() ; j++){
				sss.add(livelli[i].getSurveyedSpecie(j));
			}
		}
		return sss;
	}

    @Override
    public String getLevelsSchema() {
        return msr.getLevelsSchema();
    }
	
}