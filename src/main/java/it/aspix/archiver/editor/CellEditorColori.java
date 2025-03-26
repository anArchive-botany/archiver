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
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.UtilitaVegetazione;
import it.aspix.archiver.componenti.CampoEsposizione;
import it.aspix.archiver.componenti.CampoIntervalloTempo;
import it.aspix.archiver.componenti.ComboBoxModelED;
import it.aspix.archiver.componenti.ComboBoxModelEDFactory;
import it.aspix.archiver.componenti.insiemi.SurveyedSpecieSet;
import it.aspix.archiver.dialoghi.FiltraSpecieListaAAlbero;
import it.aspix.archiver.dialoghi.Spiegazione;
import it.aspix.archiver.eventi.SistemaException;
import it.aspix.archiver.eventi.SurveyedSpecieCambiata;
import it.aspix.archiver.eventi.SurveyedSpecieListener;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Cell;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/****************************************************************************
 * Questo editor può essere utilizzato ricorsivamente.
 * Visualizza o dei figli o un subplot
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class CellEditorColori extends CellEditor implements SurveyedSpecieListener, ChangeListener{
    
    private static final long serialVersionUID = 1L;
    
    ArrayList<ChangeListener> ascoltatoriCambiamenti = new ArrayList<ChangeListener>();
    // alcuni dati non vengono visualizzati in campi specifici dell'interfaccia ma vanno comunque memorizzati;
    private String note; // TODO: questo campo deve essere editabile
    private String nomeTipo;
    private String padreRiga;
    private String padreColonna;
    private String numeroRighe;
    private String numeroColonne;
    
    // questo componente può visualizzare diversi tipi di celle
	public static enum IstanzaEditor { UNDEFINED, RELEVEE, PLOT, SUBPLOT_INTERMEDIO, SUBPLOT_TERMINALE };
	private IstanzaEditor istanza = IstanzaEditor.UNDEFINED;

    /** il colore che questo elemento dovrebbe assumere normalmente **/
    private Color coloreOriginale = Color.YELLOW;
    // la sequenza dei colori che questo elemento può assumere a seconda del livello di annidamento
    private static final Color[] sequenzaColoriSfondo={
        new Color(0x99, 0x99, 0x99), // new Color(0xd7, 0x73, 0x35), // new Color(71,171,79), 
        new Color(0xaa, 0xaa, 0xaa), // new Color(0xb3, 0xa4, 0x85), // new Color(142,215,148), 
        new Color(0xbb, 0xbb, 0xbb), // new Color(0x83, 0x7d, 0x6b), // new Color(204,248,207), 
        new Color(0xcc, 0xcc, 0xcc), // new Color(0x89, 0x6d, 0x61), // new Color(204,248,245)
    };
    {
    	sequenzaColoriSfondo[0] = (Color) UIManager.get("Panel.background");
    }
    private static final Color colorePannelloInfo = new Color(255,255,255,100);
        
    // interfaccia grafica
    private GridBagLayout layoutDatiCella = new GridBagLayout();
    private JPanel pannelloDatiCella = new JPanel(layoutDatiCella);
    private JLabel eForma = new JLabel();
    private ComboBoxModelED modelloForma= ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.FORMA);
    private JComboBox forma = new JComboBox(modelloForma);    
    private JLabel eDimensione1 = new JLabel();
    private JTextField dimensione1 = new JTextField();
    private JLabel eDimensione2 = new JLabel();
    private JTextField dimensione2 = new JTextField();
    private JLabel eArea = new JLabel();
    private JTextField area = new JTextField();
    private JLabel eScalaAbbondanza = new JLabel();
    ComboBoxModelED modelloScalaAbbondanza= ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.SCALA_ABBONDANZA);
    private JComboBox scalaAbbondanza = new JComboBox(modelloScalaAbbondanza);
    private JLabel eOrientamento = new JLabel();
    private CampoEsposizione orientamento = new CampoEsposizione();
    private JLabel eCopertura = new JLabel();
    private JTextField copertura = new JTextField();
    private JLabel descrizione = new JLabel();
    private JLabel eTempo = new JLabel();
    private CampoIntervalloTempo tempo;
    private JLabel eStratificazione = new JLabel();
    ComboBoxModelED modelloStratificazione= ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.STRATIFICAZIONE);
    private JComboBox stratificazione = new JComboBox(modelloStratificazione);
    
    private JTabbedPane alternatoreEditorLivelli = new JTabbedPane();
    private LevelEditor editorLivelli;
    private LevelEditor editorLivelliAlternativo;	// questo serve solamente per lavorarci su, i dati al momento del get non vengono letti da qui
    private PannelloDelta pannelloDelta = new PannelloDelta();
    
    private JPanel pannelloFigli = new JPanel();
    private CellEditor[] figli;
    private JPanel sprecoCelle = new JPanel();
    
	Font fontOriginale;
	Font fontPiccolo;

    /************************************************************************
     * Questo costruttore non genera il layout perché questo varia a seconda 
     * della cella che deve essere rappresentata
     ***********************************************************************/
    public CellEditorColori(){
        tempo = new CampoIntervalloTempo("orario cella"); // qui non è dato sapere un nome più preciso 
        // le stringhe
        eForma.setText("forma:");
        eDimensione1.setText("dim:");
        eDimensione2.setText("x");
        eArea.setText("area:");
        eScalaAbbondanza.setText("scala abbondanza:");
        eOrientamento.setText("orientamento:");
        eCopertura.setText("copertura:");
        descrizione.setText("descrizione");
        eTempo.setText("tempo:");
        eStratificazione.setText("stratificazione:");
        // gestione degli elementi nei layout
        this.setLayout(new GridBagLayout());
	    this.add(pannelloDatiCella, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsGruppoIsolato, 0, 0));
        // i pannelli degli strati e dei figli vengono inseriti all'occorrenza
	    tempo.setPreferredSize(new Dimension(50,tempo.getPreferredSize().height));
        this.setBorder(
        		BorderFactory.createCompoundBorder(
	        		CostantiGUI.bordoGruppoIsolato,
	        		BorderFactory.createLineBorder(Color.BLACK, 1)
        		)
        );
        this.setBackground(coloreOriginale);
        scalaAbbondanza.setPreferredSize(new Dimension(150,scalaAbbondanza.getPreferredSize().height));
        
        fontOriginale = descrizione.getFont();
        fontPiccolo = fontOriginale.deriveFont(fontOriginale.getSize()*0.75f);
        descrizione.setFont(fontPiccolo);
        // descrizione fittizia
        descrizione.setText("¿descr?");
        pannelloDatiCella.setBackground(colorePannelloInfo);
        pannelloDatiCella.setOpaque(true);
        sprecoCelle.setOpaque(false);
        pannelloDelta.setOpaque(true); // mettendo false i contenuti si perdono (non è evidente il raggruppamento)
        pannelloDelta.setBackground(Color.WHITE); // e se non imposti il colore l'opaco vien sovrascritto
        if(Stato.isMacOSX){
        	forma.setOpaque(false);
        	scalaAbbondanza.setOpaque(false);
        }
        pannelloDelta.dettagli.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dettagliDelta();
			}
        });
        pannelloDelta.sync.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				sincronizzaAlbero();
			}
        });
        stratificazione.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cambiatoModelloStratificazione();
			}
		});
    }
    
    /************************************************************************
     * Imposta il livello in edit nel suo editor principale (se ne esiste uno) 
     * e propaga il messaggio agli eventuali figli
     ***********************************************************************/
	@Override
	public void setLivelloInEdit(Level livello){
		if(editorLivelli!=null){
			editorLivelli.setLivelloInEdit(livello);
		}
		if(pannelloFigli != null){
	        for(int i=0;i<figli.length;i++){
	            figli[i].setLivelloInEdit(livello);
	        }
        }
	}
	
    /************************************************************************
     * l'interfaccia SurveyedSpecieListener
     ***********************************************************************/
    public void specieSelezionata(SurveyedSpecieCambiata s) {
        if(editorLivelli!=null){
            editorLivelli.setSpecieInEdit(s.getSpecieRilevata());
        }
        if(figli != null){
            for(int i=0;i<figli.length;i++){
                figli[i].specieSelezionata(s);
            }
        }
    }
    
    public void specieRimossa(SurveyedSpecieCambiata s){
        if(editorLivelli!=null){
            try {
                editorLivelli.rimuoviSpecie(null, s.getSpecieRilevata());
            } catch (SistemaException e) {
                Dispatcher.consegna(this, e);
            }
        }
        if(figli != null){
            for(int i=0;i<figli.length;i++){
                figli[i].specieRimossa(s);
            }
        }
    }
    
    // TODO:
    public void specieModificata(SurveyedSpecieCambiata s){
       // non fa nulla per adesso
    }
    
    // TODO:
    public void specieAggiunta(SurveyedSpecieCambiata s){
        // XXX: qui volontariamente non faccio nulla
    }
    
    /************************************************************************
     * @return la cella rappresentata da questo elemento
     * @throws ValoreException se i dati contenuti non sono validi
     ***********************************************************************/
    public Cell getCell() throws ValoreException{
        Cell cella = new Cell();
        cella.setNote(note);
        cella.setType(nomeTipo);
        cella.setWorkTime(tempo.getText());
        cella.setRow(padreRiga);
        cella.setColumn(padreColonna);
        cella.setNumberOfRows(numeroRighe);
        cella.setNumberOfColumns(numeroColonne);
        cella.setAbundanceScale(modelloScalaAbbondanza.getSelectedEnum());
        cella.setModelOfTheLevels(modelloStratificazione.getSelectedEnum());
        cella.setTotalCovering(copertura.getText());
        cella.setShapeName(modelloForma.getSelectedEnum());
        cella.setShapeDimension1(dimensione1.getText());
        cella.setShapeDimension2(dimensione2.getText());
        cella.setShapeArea(area.getText());
        cella.setShapeOrientation(orientamento.getText());
        // i livelli vanno recuperati per tutti tranne che per le celle intermedie
        if(istanza!=IstanzaEditor.SUBPLOT_INTERMEDIO){
        	Level x[] = editorLivelli.getLevels();
        	for(int i=0 ; i<x.length ; i++)
        		cella.addLevel(x[i]);
        }
        // le celle vanno recuperate solo per i plot e le celle intermedie
        if(istanza==IstanzaEditor.PLOT || istanza==IstanzaEditor.SUBPLOT_INTERMEDIO){
            int righe = Integer.parseInt(cella.getNumberOfRows());
            int colonne = Integer.parseInt(cella.getNumberOfColumns());
            for(int i=0;i<righe;i++){
                for(int j=0;j<colonne;j++){
                	cella.setFiglio(i, j, figli[i*colonne+j].getCell());
                }
            }
        }
        return cella;
    }

    /************************************************************************
     * Riorganizza il contenuto di questo componente in base alla cella da editare
     * @param cella la cella da editare
     * @throws ValoreException 
     ***********************************************************************/
	public void setCell(Cell cella) throws ValoreException {
        Stato.debugLog.fine("Visualizzo la cella "+cella);
        if(cella == null){
        	throw new IllegalArgumentException("La cella non può essere null");
        }
		// all'inizio rimuovo i componenti che ho inserito e non essenziali
		// non è il massimo dell'efficenza ma serve essere molto ordinati in questo componente
		switch(istanza){
		case PLOT:
			alternatoreEditorLivelli.removeAll();
			pannelloDatiCella.removeAll();
	        pannelloFigli.removeAll();
	        this.remove(pannelloFigli);
			this.remove(pannelloDelta);
			this.remove(alternatoreEditorLivelli);
			this.remove(sprecoCelle);
	        break;
		case RELEVEE:
			this.remove(editorLivelli);
			pannelloDatiCella.removeAll();
	        break;
		case SUBPLOT_INTERMEDIO:
			eTempo.setFont(fontOriginale);
			pannelloDatiCella.removeAll();
	        pannelloFigli.removeAll();
	        this.remove(pannelloFigli);
	        break;
		case SUBPLOT_TERMINALE:
			eTempo.setFont(fontOriginale);
			this.remove(editorLivelli);
			pannelloDatiCella.removeAll();
	        pannelloFigli.removeAll();
	        this.remove(pannelloFigli);
	        break;			
		case UNDEFINED:
			Stato.debugLog.fine("SetCell su un CellEditorColori vuoto");
			break;
		}
		
		// adesso bisogna capire quale tipo di cella va visualizzato
		Stato.debugLog.fine("nome del tipo: "+cella.getType());
		if(cella.getType()!=null && cella.getType().equals("plot")){
			istanza = IstanzaEditor.PLOT;
		}else if(cella.getType()!=null && cella.getType().equals("relevée")){
			istanza = IstanzaEditor.RELEVEE;
		}else{
			// è un subplot, non si tiene conto del nome del tipo (perché è variabile), dipende dalla presenza o meno di figli
			if(cella.getFigli()==null || cella.getFigli().length==0){
				istanza = IstanzaEditor.SUBPLOT_TERMINALE;
			}else{
				istanza = IstanzaEditor.SUBPLOT_INTERMEDIO;
			}
		}
    	Stato.debugLog.fine("l'istanza per la cella "+cella.toString()+" è "+istanza.toString());
    	
    	// se occorre creo il pannello dei figli
        if(istanza==IstanzaEditor.PLOT || istanza==IstanzaEditor.SUBPLOT_INTERMEDIO){
            // questa cella contiene altre celle
        	int righe,colonne;      // che vanno a comporre questo oggetto
            CellEditor tmp;
            righe = Integer.parseInt(cella.getNumberOfRows());
            colonne = Integer.parseInt(cella.getNumberOfColumns());
            pannelloFigli = new JPanel(new GridLayout(righe,colonne));
            pannelloFigli.setBackground(Color.BLUE);
            pannelloFigli.setOpaque(false);
            this.figli = new CellEditor[righe*colonne];
            for(int i=0;i<righe;i++){
                for(int j=0;j<colonne;j++){
                	tmp = new CellEditorColori();
                	tmp.setCell(cella.getFiglio(i,j));
                	this.figli[i*colonne+j]=tmp;
                	tmp.addChangeListener(this);
                	Stato.debugLog.fine("Aggiungo una cella con "+tmp.getComponentCount()+" elementi");
                    pannelloFigli.add(tmp);
                }
            }
            Stato.debugLog.fine("Numero elementi nel pannello figli di "+cella+": "+pannelloFigli.getComponentCount());
        }else{
        	this.figli = null;
        }
		
		// visualizzazione dei campi 
        if(istanza==IstanzaEditor.PLOT || istanza==IstanzaEditor.RELEVEE){
            pannelloDatiCella.add(descrizione,      new GridBagConstraints(0, 0, 9, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDatiCella.add(eForma,           new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDatiCella.add(forma,            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
            pannelloDatiCella.add(eDimensione1,     new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDatiCella.add(dimensione1,      new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
            pannelloDatiCella.add(eDimensione2,     new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDatiCella.add(dimensione2,      new GridBagConstraints(5, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
            pannelloDatiCella.add(eArea,            new GridBagConstraints(7, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDatiCella.add(area,             new GridBagConstraints(8, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
            pannelloDatiCella.add(eCopertura,       new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDatiCella.add(copertura,        new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
            pannelloDatiCella.add(eOrientamento,    new GridBagConstraints(4, 2, 2, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDatiCella.add(orientamento,     new GridBagConstraints(6, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
            pannelloDatiCella.add(eTempo,           new GridBagConstraints(7, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDatiCella.add(tempo,            new GridBagConstraints(8, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
            
            pannelloDatiCella.add(eScalaAbbondanza, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDatiCella.add(scalaAbbondanza,  new GridBagConstraints(1, 3, 8, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
            pannelloDatiCella.add(eStratificazione, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDatiCella.add(stratificazione,  new GridBagConstraints(1, 4, 8, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        }
        if(istanza==IstanzaEditor.SUBPLOT_INTERMEDIO || istanza==IstanzaEditor.SUBPLOT_TERMINALE){
            eTempo.setFont(fontPiccolo);
            pannelloDatiCella.add(descrizione,      new GridBagConstraints(0, 0, 9, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsTestoDescrittivo, 0, 0));
            pannelloDatiCella.add(eTempo,           new GridBagConstraints(7, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDatiCella.add(tempo,            new GridBagConstraints(8, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        }
		switch(istanza){
		case PLOT:
	        editorLivelli = new LevelEditorAlbero();
		    editorLivelli.addSurveyedSpecieListener(this);
		    editorLivelliAlternativo = new LevelEditorLista();
		    editorLivelliAlternativo.addSurveyedSpecieListener(this);
			alternatoreEditorLivelli.add(editorLivelli,"albero");
			alternatoreEditorLivelli.add(editorLivelliAlternativo,"lista");
			this.add(pannelloDelta,              new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsGruppoIsolato, 0, 0));
			this.add(pannelloFigli,              new GridBagConstraints(1, 1, 1, 2, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,        CostantiGUI.insetsGruppoIsolato, 0, 0));
			this.add(alternatoreEditorLivelli,   new GridBagConstraints(0, 2, 1, 2, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,        CostantiGUI.insetsGruppoIsolato, 0, 0));
            this.add(sprecoCelle,                new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,    CostantiGUI.insetsGruppoIsolato, 0, 0));
	        break;
		case RELEVEE:
	        editorLivelli = new LevelEditorAlbero();
		    this.add(editorLivelli,   new GridBagConstraints(0, 1, 1, 2, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,  CostantiGUI.insetsGruppoIsolato, 0, 0));
	        break;
		case SUBPLOT_INTERMEDIO:
            this.add(pannelloFigli,     new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,  CostantiGUI.insetsGruppoIsolato, 0, 0));
	        break;
		case SUBPLOT_TERMINALE:
		    editorLivelli = new LevelEditorPresenzaAssenza();
		    editorLivelli.addSurveyedSpecieListener(this);
		    this.add(editorLivelli,   new GridBagConstraints(0, 1, 1, 2, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,  CostantiGUI.insetsGruppoIsolato, 0, 0));
	        break;
        case UNDEFINED:
            throw new ValoreException("Il tipo UNDEFINED non può essere gestito da questo editor.");
		}
		
        // re-imposto i dati statici
        note = cella.getNote();
        nomeTipo = cella.getType();
        tempo.setText(cella.getWorkTime());
        padreRiga = cella.getRow();
        padreColonna = cella.getColumn();
        numeroRighe = cella.getNumberOfRows();
        numeroColonne = cella.getNumberOfColumns();
        modelloScalaAbbondanza.setSelectedEnum(cella.getAbundanceScale());
        modelloStratificazione.setSelectedEnum(cella.getModelOfTheLevels());
        copertura.setText(cella.getTotalCovering());
        modelloForma.setSelectedEnum(cella.getShapeName());
        dimensione1.setText(cella.getShapeDimension1());
        dimensione2.setText(cella.getShapeDimension2());
        area.setText(cella.getShapeArea());
        orientamento.setText(cella.getShapeOrientation());
        // impostazioni comuni
    	coloreOriginale = sequenzaColoriSfondo[cella.getNestingLevel()];
    	this.setBackground(coloreOriginale);
    	descrizione.setText(cella.getType()+" "+cella.getQualifiedName());
    	tempo.setNome(cella.getType()+" "+cella.getQualifiedName());
    	
    	if(istanza!=IstanzaEditor.SUBPLOT_INTERMEDIO){
    		// questa cella ha (dovrebbe avere) dei livelli
    		Stato.debugLog.fine("Aggiungo i livelli");
    		Level x[] = cella.getLevel();
        	try {
				editorLivelli.setLevels(x);
			} catch (ValoreException e) {
			    Dispatcher.consegna(this, e);
			}
    	}
    	if(istanza==IstanzaEditor.PLOT){
    		SurveyedSpecieSet sss = new SurveyedSpecieSet();
    		SurveyedSpecieSet tmp;
    		for(int i=0 ; i<figli.length ; i++){
    			tmp = figli[i].getSpecieSet();
    			sss.add(tmp);
    		}
    		// aggiungo l'elenco delle specie all'editor alternativo (creando un livello)
    		Level livelliFittizi[] = new Level[1];
    		livelliFittizi[0] = UtilitaVegetazione.livelloUnico.clone();
    		for(SurveyedSpecie ss: sss.getSurveyedSpecies()){
    		    livelliFittizi[0].addSurveyedSpecie(ss);
    		}
    		try {
                editorLivelliAlternativo.setLevels(livelliFittizi);
            } catch (ValoreException e) {
                // ma questa cosa non può succedere per costruzione
                Dispatcher.consegna(this, e);
            }
            aggiornaPannelloSincronia();
    	}
    	
        // la cella top-level non deve avere lo sfondo colorato
        this.setOpaque(cella.getPadre()!=null);
        this.updateUI();
        Stato.debugLog.fine("Mia dimensione "+this.getSize()+" elementi:"+this.getComponentCount());
	}
	
	/************************************************************************
	 * Calcola l'insieme delle specie presenti in questo componente, 
	 * prende i dati sempre dall'editor dei livelli principale tranne che nel
	 * caso di una cella intermedia (in questo caso fa l'unione delle
	 * specie presenti nei figli)
	 ***********************************************************************/
	@Override
    public SurveyedSpecieSet getSpecieSet() {
    	SurveyedSpecieSet risultato;
        SurveyedSpecieSet insieme;
        
        // TODO: nel caso del plot conviene recuperare da due canali distinti e poi controllare se combaciano?
        if(istanza==IstanzaEditor.SUBPLOT_INTERMEDIO){
        	risultato = new SurveyedSpecieSet();
        	for(int i=0;i<figli.length;i++){
	        	insieme = figli[i].getSpecieSet();
	            risultato.add(insieme);
	        } 
        }else{
        	risultato = editorLivelli.getSpecieSet();
        }
        return risultato;
    }

    @Override
    public void rimuoviSpecie(SurveyedSpecie s, Level l) {
    	if(istanza!=IstanzaEditor.SUBPLOT_INTERMEDIO){
    		try {
				editorLivelli.rimuoviSpecie(l, s);
			} catch (SistemaException e) {
			    Dispatcher.consegna(this, e);
			}
			if(istanza==IstanzaEditor.PLOT){
				try {
					editorLivelli.rimuoviSpecie(l, s);
				} catch (SistemaException e) {
				    Dispatcher.consegna(this, e);
				}
			}
    	}
        for(int i=0;i<figli.length;i++){
            if(figli[i]!=null)
                figli[i].rimuoviSpecie(s,l);
        }   
    }
	
    /************************************************************************
     * aggiorna lo stato del pannello 
     ***********************************************************************/
	public void aggiornaPannelloSincronia(){
		SurveyedSpecieSet sssAlbero = editorLivelli.getSpecieSet();
		SurveyedSpecieSet sssLista = editorLivelliAlternativo.getSpecieSet();
		SurveyedSpecieSet sssFigli = new SurveyedSpecieSet();
		for(int i=0 ; i<figli.length ; i++){
			sssFigli.add(figli[i].getSpecieSet());
		}
		Stato.debugLog.fine("Test sincronizzazione");
		Stato.debugLog.fine("Insieme albero: "+sssAlbero);
		Stato.debugLog.fine("Insieme lista: "+sssLista);
		Stato.debugLog.fine("Insieme figli: "+sssFigli);
		pannelloDelta.setStato(PannelloDelta.ALBERO_LISTA, sssAlbero.contiene(sssLista) && sssLista.contiene(sssAlbero) );
		pannelloDelta.setStato(PannelloDelta.GRIGLIA_ALBERO, sssAlbero.contiene(sssFigli) && sssFigli.contiene(sssAlbero) );
		pannelloDelta.setStato(PannelloDelta.LISTA_GRIGLIA, sssLista.contiene(sssFigli) && sssFigli.contiene(sssLista) );
	}

	/************************************************************************
	 * visualizza una finestra con le differenze dettagliate tra la lista
	 * e l'albero
	 ***********************************************************************/
	public void dettagliDelta(){
		StringBuffer sb = new StringBuffer();
		Spiegazione finestraSpiegazioni;
		SurveyedSpecie differenza[];
		SurveyedSpecieSet sssAlbero = editorLivelli.getSpecieSet();
		SurveyedSpecieSet sssLista = editorLivelliAlternativo.getSpecieSet();
		
		differenza = sssLista.sottrai(sssAlbero).getSurveyedSpecies();
		if(differenza.length>0){
			sb.append("<body style=\"margin:10px\">\n");
			sb.append("<h2 style=\"margin-bottom:0px\">Presenti in lista ma non in albero</h2>\n");
			sb.append("<p style=\"margin-top:0px\">\n");
			for(int i=0 ; i<differenza.length ; i++){
				sb.append(differenza[i]);
				sb.append("<br>");
			}
			sb.append("</p>\n");
		}
		differenza = sssAlbero.sottrai(sssLista).getSurveyedSpecies();
		if(differenza.length>0){
			sb.append("<h2 style=\"margin-bottom:0px\">Presenti in albero ma non in lista</h2>\n");
			sb.append("<h3 style=\"margin:0px;color:red\">(verranno rimossi in sincronizzazione)</h2>\n");
			sb.append("<p style=\"margin-top:0px\">\n");
			for(int i=0 ; i<differenza.length ; i++){
				sb.append(differenza[i]);
				sb.append("<br>");
			}
			sb.append("</p>\n");
		}
		sb.append("</body>");
		finestraSpiegazioni = new Spiegazione("Differenze",sb.toString());
		finestraSpiegazioni.setVisible(true);
	}
	
	/************************************************************************
	 * Copia l'elenco delle specie dalla lista all'albero
	 ***********************************************************************/
	public void sincronizzaAlbero(){
		try {
			Level[] livelliAlbero = editorLivelli.getLevels();
			Level[] livelliLista = editorLivelliAlternativo.getLevels();
			FiltraSpecieListaAAlbero finestraFiltro = new FiltraSpecieListaAAlbero(livelliLista, livelliAlbero, false);
			UtilitaGui.centraDialogoAlloSchermo(finestraFiltro, UtilitaGui.CENTRO);
			finestraFiltro.setVisible(true);
			livelliAlbero = finestraFiltro.getLevels();
			editorLivelli.setLevels(livelliAlbero);
			aggiornaPannelloSincronia();
			this.updateUI();
		} catch (ValoreException e) {
		    Dispatcher.consegna(this, e);
		}		
	}
	
	/************************************************************************
	 * Se non ci sono specie nell'editor dei livelli imposta
	 * una nuova stratificazione a seconda della voce scelta nel modello
	 ***********************************************************************/
	private void cambiatoModelloStratificazione(){
		String ms = modelloStratificazione.getSelectedEnum();
		Level elenco[];
		int numeroSpecie = 0;
		try {
			elenco = editorLivelli.getLevels();
			for(int i=0; i<elenco.length ; i++){
				numeroSpecie += elenco[i].getSurveyedSpecieCount();
			}
			if(numeroSpecie==0){
				Cell tmp = new Cell();
				if(ms.equals("999")){
					CostruttoreOggetti.addLevels(tmp, Proprieta.recupera("vegetazione.schemaStrati"));
				}else{
					CostruttoreOggetti.addLevelsByPattern(tmp, ms);
				}
				editorLivelli.setLevels(tmp.getLevel());
			}
		} catch (ValoreException e) {
			// do nothing: è un metodo di utilità se qualcosa va male non si fa niente e basta
		}
	}
	
	/************************************************************************
	 * Implementazione dell'interfaccia StateListener
	 ***********************************************************************/
	public void stateChanged(ChangeEvent e) {
		if(istanza==IstanzaEditor.PLOT)
			aggiornaPannelloSincronia();
        for (ChangeListener ascoltatore : ascoltatoriCambiamenti) {
			ascoltatore.stateChanged(null);
		}
	}
	
	@Override
	public void addChangeListener(ChangeListener cl) {
		ascoltatoriCambiamenti.add(cl);
	}
	
	/************************************************************************
	 * Il punto di controllo tra le diverse viste delle specie rilevate 
	 * @author Edoardo Panfili, studio Aspix
	 ***********************************************************************/
	private static class PannelloDelta extends JPanel{
		private static final long serialVersionUID = 1L;
		private JLabel albero_lista = new JLabel("¿undef?");
		private JLabel lista_griglia = new JLabel("¿undef?");
		private JLabel griglia_albero = new JLabel("¿undef?");
		private JLabel icona = new JLabel(Icone.PlotDelta);
		public JButton sync = new JButton(Icone.PlotSincronizza);
		public JButton dettagli = new JButton(Icone.InfoPiccolo);
		
		public PannelloDelta(){
			this.setLayout(new GridBagLayout());
			sync.setToolTipText("Sincronizza albero e lista");
			this.add(sync,            new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE, CostantiGUI.insetsAzione, 0, 0));
			this.add(albero_lista,    new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE, CostantiGUI.insetsEtichetta, 0, 0));
			this.add(icona,        	  new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, CostantiGUI.insetsEtichetta, 0, 0));
			this.add(lista_griglia,   new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.NONE, CostantiGUI.insetsEtichetta, 0, 0));
			this.add(dettagli,        new GridBagConstraints(4, 0, 1, 2, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE, CostantiGUI.insetsAzione, 0, 0));
			this.add(griglia_albero,  new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, CostantiGUI.insetsEtichetta, 0, 0));
		}
		
		public static final byte ALBERO_LISTA   = 0;
		public static final byte LISTA_GRIGLIA  = 1;
		public static final byte GRIGLIA_ALBERO = 2;
		public void setStato(byte chi, boolean ok){
			JLabel target = (chi==ALBERO_LISTA ? albero_lista : (chi==LISTA_GRIGLIA ? lista_griglia : griglia_albero));
			target.setText(ok ? "ok" : "diversi");
		}
	}

    @Override
    public String getLevelsSchema() {
        return editorLivelli.getLevelsSchema();
    }
}