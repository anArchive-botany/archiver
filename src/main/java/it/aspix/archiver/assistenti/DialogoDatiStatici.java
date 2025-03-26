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
import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.Icone;
import it.aspix.archiver.Utilita;
import it.aspix.archiver.componenti.ComboSuggerimenti;
import it.aspix.archiver.componenti.FornitoreGestoreMessaggi;
import it.aspix.archiver.componenti.GestoreMessaggi;
import it.aspix.archiver.componenti.StatusBar;
import it.aspix.archiver.editor.PlaceEditor;
import it.aspix.sbd.introspection.InformazioniTipiEnumerati;
import it.aspix.sbd.introspection.ValoreEnumeratoDescritto;
import it.aspix.sbd.obj.Cell;
import it.aspix.sbd.obj.Classification;
import it.aspix.sbd.obj.Place;
import it.aspix.sbd.obj.PublicationRef;
import it.aspix.sbd.obj.Sample;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/****************************************************************************
 * Chiede un insieme di dati da inserire in ogni rilievo
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DialogoDatiStatici extends JDialog implements FornitoreGestoreMessaggi{

	private static final long serialVersionUID = 1L;
	
	JPanel principale = new JPanel(new BorderLayout());
	
	JPanel pannelloInferiore = new JPanel(new BorderLayout());
	JPanel pannelloPulsanti = new JPanel(new GridBagLayout());
	JButton indietro = new JButton("indietro");
	JButton avanti = new JButton("avanti");
	StatusBar barraDiStato = new StatusBar();
	
	JTabbedPane linguette = new JTabbedPane();
	JPanel pannelloAvvisi = new JPanel(new BorderLayout());
	JEditorPane avvisi = new JEditorPane();
	JScrollPane scrollAvvisi = new JScrollPane(avvisi);
	JLabel immagineTabella = new JLabel();
	
	JPanel pannelloProgetto = new JPanel(new GridBagLayout());
	JLabel eProgetto = new JLabel("progetto:");
	JTextField progetto = new JTextField();
	JLabel eSottoprogetto = new JLabel("sottoprogetto:");
	JPanel pannelloSottoprogetto = new JPanel(new BorderLayout());
	ComboSuggerimenti sottoprogetto;
	
	JPanel pannelloPubblicazione = new JPanel(new GridBagLayout());
	JTextArea ePubblicazione1 = new JTextArea("metti il codice lisy");
	JTextField lisy = new JTextField();
	JTextArea ePubblicazione2 = new JTextArea("in caso tu non abbia lisy");
	JTextField bibliografia = new JTextField();
	JLabel eTabella = new JLabel("tabella: ");
	JTextField tabella = new JTextField();
	
	JPanel pannelloGeo = new JPanel(new GridBagLayout());
	JLabel eSistemaDiRiferimento = new JLabel ("sistema di riferimento:"); 
	DefaultComboBoxModel<String> contenutoSistemadiDiRiferimento = new DefaultComboBoxModel<>(PlaceEditor.epsgPossibili);
	JComboBox<String> sistemadiDiRiferimento = new JComboBox<>(contenutoSistemadiDiRiferimento);
	
	JPanel pannelloCampi = new JPanel(new GridBagLayout());
	JLabel eRilevatore = new JLabel("rilevatore:");
	JTextField rilevatore = new JTextField();
	JLabel eRiferimentoFitosociologico = new JLabel("rif. fitosociologico:");
	JTextField riferimentoFitosociologico = new JTextField();
	JLabel eData = new JLabel("data:");
	JTextField data = new JTextField();
	JLabel eLocalita = new JLabel("località:");
	JTextField localita = new JTextField();
	JLabel eComune = new JLabel("comune:");
	JTextField comune = new JTextField();
	JLabel eProvincia = new JLabel("provincia:");
	JTextField provincia = new JTextField();
	JLabel eRegione = new JLabel("regione:");
	JTextField regione = new JTextField();
	
	JPanel pannelloAbbondanze = new JPanel(new GridBagLayout());
	JTextArea eScalaAbbondanza = new JTextArea();
	JRadioButton scalaAbbondanza[];
	ButtonGroup gruppoAbbondanze = new ButtonGroup();
	String scalaAbbondanzaSelezionata;
	
	JPanel pannelloDiritti = new JPanel(new GridBagLayout());
	JTextArea eDiritti = new JTextArea();
	JRadioButton diritto[];
	ButtonGroup gruppoDiritti = new ButtonGroup();
	String dirittoSelezionato;

	public DialogoDatiStatici(String titolo, String nomeFileAvviso, BarraAvanzamentoWizard ba){	
		this.setTitle(titolo);
		ArrayList<ValoreEnumeratoDescritto> alc;
    	alc = InformazioniTipiEnumerati.getValoriDescritti("Sample.AbundanceScale", "it");
    	scalaAbbondanza = new JRadioButton[alc.size()];
    	ArrayList<ValoreEnumeratoDescritto> dirittiPossibili;
    	dirittiPossibili = InformazioniTipiEnumerati.getValoriDescritti("DirectoryInfo.AccessRight","it");
    	diritto = new JRadioButton[dirittiPossibili.size()];
    	
    	if(nomeFileAvviso!=null){
			linguette.add("avvisi", pannelloAvvisi);
			pannelloAvvisi.add(scrollAvvisi, BorderLayout.CENTER);
			pannelloAvvisi.add(immagineTabella, BorderLayout.SOUTH);
    	}
		
		linguette.add("file & progetto", pannelloProgetto);
		pannelloProgetto.add(eProgetto,     			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
		pannelloProgetto.add(progetto,      			new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
		pannelloProgetto.add(eSottoprogetto,			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
		pannelloProgetto.add(pannelloSottoprogetto ,	new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
		pannelloProgetto.add(new JLabel(),  			new GridBagConstraints(0, 3, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsGruppo, 0, 0));

		linguette.add("pubblicazione", pannelloPubblicazione);
		pannelloPubblicazione.add(ePubblicazione1, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
		pannelloPubblicazione.add(lisy,            new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
		pannelloPubblicazione.add(ePubblicazione2, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
		pannelloPubblicazione.add(bibliografia,    new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
		pannelloPubblicazione.add(eTabella,        new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
		pannelloPubblicazione.add(tabella,         new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		pannelloPubblicazione.add(new JLabel(),    new GridBagConstraints(0, 5, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsGruppo, 0, 0));
		
		linguette.add("geografici", pannelloGeo);
		pannelloGeo.add(eSistemaDiRiferimento, 		new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsEtichetta, 0, 0));
		pannelloGeo.add(sistemadiDiRiferimento,     new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		pannelloGeo.add(new JLabel(),     			new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsGruppo, 0, 0));
	
		linguette.add("campi", pannelloCampi);
		pannelloCampi.add(eRilevatore, 					new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE, 	   CostantiGUI.insetsEtichetta, 0, 0));
		pannelloCampi.add(rilevatore, 					new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		pannelloCampi.add(eRiferimentoFitosociologico,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE, 	   CostantiGUI.insetsEtichetta, 0, 0));
		pannelloCampi.add(riferimentoFitosociologico,   new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		pannelloCampi.add(eData, 						new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE, 	   CostantiGUI.insetsEtichetta, 0, 0));
		pannelloCampi.add(data, 						new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		pannelloCampi.add(eLocalita, 					new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE, 	   CostantiGUI.insetsEtichetta, 0, 0));
		pannelloCampi.add(localita, 					new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		pannelloCampi.add(eComune, 						new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,  	   CostantiGUI.insetsEtichetta, 0, 0));
		pannelloCampi.add(comune, 						new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		pannelloCampi.add(eProvincia, 					new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
		pannelloCampi.add(provincia,					new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		pannelloCampi.add(eRegione, 					new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
		pannelloCampi.add(regione, 						new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
		pannelloCampi.add(new JLabel(),    				new GridBagConstraints(0, 7, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsGruppo, 0, 0));
		
		linguette.add("abbondanze", pannelloAbbondanze);
		pannelloAbbondanze.add(eScalaAbbondanza,     	new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsTestoDescrittivo, 0, 0));
		ActionListener ascoltatoreAbbondanze = new ActionListener() {
			public void actionPerformed(ActionEvent evento) {
				scalaAbbondanzaSelezionata = ((JRadioButton)(evento.getSource())).getName();
			}
		};
		for(int i=0; i<alc.size() ; i++){
			scalaAbbondanza[i] = new JRadioButton(alc.get(i).descrizione);
			scalaAbbondanza[i].setName(alc.get(i).enumerato);
			scalaAbbondanza[i].addActionListener(ascoltatoreAbbondanze);
			gruppoAbbondanze.add(scalaAbbondanza[i]);
			pannelloAbbondanze.add(scalaAbbondanza[i],     	new GridBagConstraints(0, i+1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsTestoDescrittivo, 0, 0));
		}
		pannelloAbbondanze.add(new JLabel(),    				new GridBagConstraints(0, alc.size()+1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsGruppo, 0, 0));
		
		linguette.add("diritti", pannelloDiritti);
		pannelloDiritti.add(eDiritti,     	new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsTestoDescrittivo, 0, 0));
		ActionListener ascoltatoreDiritti = new ActionListener() {
			public void actionPerformed(ActionEvent evento) {
				dirittoSelezionato= ((JRadioButton)(evento.getSource())).getName();
			}
		};
		for(int i=0; i<dirittiPossibili.size() ; i++){
			diritto[i] = new JRadioButton(dirittiPossibili.get(i).descrizione);
			diritto[i].setName(dirittiPossibili.get(i).enumerato);
			diritto[i].addActionListener(ascoltatoreDiritti);
			gruppoDiritti.add(diritto[i]);
			pannelloDiritti.add(diritto[i],     	new GridBagConstraints(0, i+1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsTestoDescrittivo, 0, 0));
		}
		pannelloDiritti.add(new JLabel(),    				new GridBagConstraints(0, alc.size()+1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsGruppo, 0, 0));
		
		pannelloInferiore.add(pannelloPulsanti, BorderLayout.CENTER);
		pannelloInferiore.add(barraDiStato, BorderLayout.SOUTH);
		ba.setDialogo(this);
		pannelloPulsanti.add(ba, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzione, 0, 0));
		
		principale.add(linguette, BorderLayout.CENTER);
		principale.add(pannelloInferiore, BorderLayout.SOUTH);
		this.getContentPane().add(principale);
		
		if(nomeFileAvviso!=null){
			pannelloAvvisi.setOpaque(false);
			avvisi.setContentType("text/html");
			avvisi.setOpaque(false);
			String manuale;
			manuale = Utilita.leggiStringa(nomeFileAvviso, "manuale non trovato");
			avvisi.setText(manuale);
			avvisi.setEditable(false);
			// TODO: questa viene inserita brutalmente, va sistemata in altro modo
			immagineTabella.setIcon(Icone.TabellaDiEsempio);
			immagineTabella.setHorizontalAlignment(SwingConstants.CENTER);
		}
		pannelloProgetto.setOpaque(false);
		pannelloPubblicazione.setOpaque(false);
		ePubblicazione1.setOpaque(false);
		ePubblicazione1.setEditable(false);
		ePubblicazione1.setText("Se tutti i rilievi della tabella provengono dalla stessa pubblicazione presente in LISY scrivi nella casella sottostante il codice LISY");
		ePubblicazione1.setWrapStyleWord(true);
		ePubblicazione1.setLineWrap(true);
		ePubblicazione2.setOpaque(false);
		ePubblicazione2.setEditable(false);
		ePubblicazione2.setText("Se tutti i rilievi della tabella provengono dalla stessa pubblicazione che però non è presente in LISY scrivi nella casella sottostante la citazione bibliografica");
		ePubblicazione2.setWrapStyleWord(true);
		ePubblicazione2.setLineWrap(true);
		ePubblicazione2.setOpaque(false);
		pannelloCampi.setOpaque(false);
		pannelloAbbondanze.setOpaque(false);
		eScalaAbbondanza.setText("Seleziona una scala di abbondanza (è possibile al dilà della scelta specificare la sociabilità):");
		eScalaAbbondanza.setEditable(false);
		eScalaAbbondanza.setWrapStyleWord(true);
		eScalaAbbondanza.setLineWrap(true);
		eScalaAbbondanza.setOpaque(false);
		scalaAbbondanza[1].setSelected(true);
		scalaAbbondanzaSelezionata = scalaAbbondanza[1].getName();
		eDiritti.setText("Seleziona un diritto di accesso per gli utenti non registrati, il diritto \"limitato\" implica la visibilità del rilievo da parte degli utenti non registrati escludendo però le coordinate e le specie presenti:");
		eDiritti.setWrapStyleWord(true);
		eDiritti.setLineWrap(true);
		eDiritti.setOpaque(false);
		diritto[1].setSelected(true);
		dirittoSelezionato = diritto[1].getName(); 
		
		pannelloSottoprogetto.setOpaque(false);
		
		avanti.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int x = linguette.getSelectedIndex();
				if(x<linguette.getTabCount()-1){
					linguette.setSelectedIndex(x+1);
				}
			}
		});
		indietro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int x = linguette.getSelectedIndex();
				if(x>0){
					linguette.setSelectedIndex(x-1);
				}
			}
		});
		progetto.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent arg0) {
				variazioneProgetto();
			}
			public void keyPressed(KeyEvent arg0) {
				variazioneProgetto();
			}
		});
		
		// ! default
		data.setText("01-01-3000");
		rilevatore.setText("Non inserito");
		localita.setText("non inserita");
	}
	
	String progettoInUso="";
	private void variazioneProgetto(){
		String x = progetto.getText();
		if(!x.equals(progettoInUso)){
			progettoInUso = x;
			if(progettoInUso.length()>5){
				try{
					sottoprogetto = new ComboSuggerimenti(progettoInUso, "subContainer", ComboSuggerimenti.SOLO_AVVIO, true, false, "", true);
					// sottoprogetto.setOpaque(false);
				}catch(Exception ex){
					; // qui la cosa che può non andare è il progetto che non esiste: in questo caso non importa
				}
				pannelloSottoprogetto.removeAll();
	            pannelloSottoprogetto.add(sottoprogetto, BorderLayout.CENTER);
	            pannelloSottoprogetto.updateUI();
			}
		}
	}
	
	public SampleWrapper getModelloRilievo(){
		SampleWrapper sw = new SampleWrapper();
		
		sw.setDirectoryInfo(CostruttoreOggetti.createDirectoryInfo());		
		sw.getDirectoryInfo().setContainerName(progetto.getText().trim());
		sw.getDirectoryInfo().setSubContainerName(sottoprogetto.getText().trim());
		sw.getDirectoryInfo().setOthersReadRights(dirittoSelezionato);
		
		sw.setPublicationRef(new PublicationRef());
		sw.getPublicationRef().setReference(lisy.getText().trim());
		sw.getPublicationRef().setCitation(bibliografia.getText().trim());
		sw.getPublicationRef().setTable(tabella.getText().trim());
		
		sw.setSurveyer(rilevatore.getText().trim());
		sw.setDate(data.getText().trim());
		
		if(data.getText().trim().length()>0){
			String parti[] = data.getText().trim().split("[\\-/]");
			String d = null;
			if(parti.length==3){
				d = parti[2]+"-"+parti[1]+"-"+parti[0];
			}else if(parti.length==2){
				d = parti[1]+"-"+parti[0];
			}else{
				d = data.getText().trim();
			}
			sw.setDate(d);
		}
		if(riferimentoFitosociologico.getText().trim().length()>0){
			Classification c = new Classification();
			c.setName(riferimentoFitosociologico.getText().trim());
			c.setType("actual");
			sw.addClassification(c);
		}
		
		sw.setPlace(new Place());
		sw.getPlace().setName(localita.getText().trim());
		sw.getPlace().setTown(comune.getText().trim());
		sw.getPlace().setProvince(provincia.getText().trim());
		sw.getPlace().setRegion(regione.getText().trim());
		
		sw.setCell(new Cell());
		sw.getCell().setAbundanceScale(scalaAbbondanzaSelezionata);
		sw.getCell().setType("relevée");
		
		String r = (String) sistemadiDiRiferimento.getSelectedItem();
		sw.epsg = r.substring(0, r.indexOf(' '));
		
		return sw;
	}
	
	public GestoreMessaggi getGestoreMessaggi() {
		return barraDiStato;
	}
	
	public String getEpsg(){
		String r = (String) sistemadiDiRiferimento.getSelectedItem();
		return r.substring(0, r.indexOf(' '));
	}
	
	public void setModelloRilievo(Sample s){
		progetto.setText(s.getDirectoryInfo().getContainerName());
		progettoInUso = s.getDirectoryInfo().getContainerName();
		sottoprogetto = new ComboSuggerimenti(progettoInUso, "subContainer", ComboSuggerimenti.SOLO_AVVIO, true, false, "", true);
		sottoprogetto.setText(s.getDirectoryInfo().getSubContainerName());
		pannelloSottoprogetto.removeAll();
        pannelloSottoprogetto.add(sottoprogetto, BorderLayout.CENTER);
        pannelloSottoprogetto.updateUI();
        
		for(int i=0; i<diritto.length; i++){
			if(diritto[i].getName().equals(s.getDirectoryInfo().getOthersReadRights())){
				diritto[i].setSelected(true);
			}
		}
		if(s.getPublicationRef()!=null){
			lisy.setText(s.getPublicationRef().getReference());
			bibliografia.setText(s.getPublicationRef().getCitation());
			tabella.setText(s.getPublicationRef().getTable());
		}
		rilevatore.setText(s.getSurveyer());
		if(s.getDate()!=null){
			String parti[] = s.getDate().split("[\\-/]");
			String d = null;
			if(parti.length==3){
				d = parti[2]+"-"+parti[1]+"-"+parti[0];
			}else if(parti.length==2){
				d = parti[1]+"-"+parti[0];
			}else{
				d = s.getDate();
			}
			data.setText(d);
		}
		if(s.getClassificationCount()>0){
			riferimentoFitosociologico.setText(s.getClassification(0).getName());
		}
		localita.setText(s.getPlace().getName());
		comune.setText(s.getPlace().getTown());
		provincia.setText(s.getPlace().getProvince());
		regione.setText(s.getPlace().getRegion());
		
		for(int i=0; i<scalaAbbondanza.length; i++){
			if(scalaAbbondanza[i].getName().equals(s.getCell().getAbundanceScale())){
				scalaAbbondanza[i].setSelected(true);
			}
		}
	}
	
	public void setEpsg(String epsg){
		for(int i=0; i<contenutoSistemadiDiRiferimento.getSize(); i++){
			if((contenutoSistemadiDiRiferimento.getElementAt(i)).startsWith(epsg)){
				sistemadiDiRiferimento.setSelectedIndex(i);
			}
		}
	}
}
