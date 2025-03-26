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

import it.aspix.archiver.componenti.CampoEsposizione;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/****************************************************************************
 * Mostra un dialogo per il caricamento di un file e poi genera un Hash
 * in base al suo contenuto
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DialogoDizionario extends JDialog{

	private static final long serialVersionUID = 1L;
		
	DropFile dropFile;
	JCheckBox ignoraPrima;
	String dizionario = null;
	
	public DialogoDizionario() {
		JLabel avviso = new JLabel("Il file deve essere un testo separato da tab");
		AccettabilitaFile filtro = new AccettabilitaFile() {
			public boolean isAccettabile(File f) {
				return true;
			}
		};
		JPanel azioni = new JPanel(new GridLayout(2,2));
		JButton siglaProvincia = new JButton("sigla → provincia");
		JButton siglaRegione = new JButton("sigla → regione");
		JButton orientamento = new JButton("orientamento sigla → gradi");
		dropFile = new DropFile(filtro);
		ignoraPrima = new JCheckBox("ignora la prima riga");
		JPanel principale = new JPanel(new BorderLayout());
		principale.add(avviso, BorderLayout.NORTH);
		principale.add(dropFile, BorderLayout.CENTER);
		principale.add(azioni, BorderLayout.SOUTH);
		azioni.add(ignoraPrima);
		azioni.add(new JLabel());
		azioni.add(new JLabel());
		azioni.add(siglaProvincia);
		azioni.add(siglaRegione);
		azioni.add(orientamento);
		this.getContentPane().add(principale);
		this.pack();
		this.setModal(true);
		ignoraPrima.setSelected(true);
		
		dropFile.addPropertyChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if("text".equals(evt.getPropertyName())){
					dizionario = dropFile.nomeFile.toString();
					DialogoDizionario.this.setVisible(false);
				}
			}
		});
		siglaProvincia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dizionario = "PROVINCIA";
				DialogoDizionario.this.setVisible(false);
			}
		});
		siglaRegione.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dizionario = "REGIONE";
				DialogoDizionario.this.setVisible(false);
			}
		});
		orientamento.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dizionario = "ORIENTAMENTO";
				DialogoDizionario.this.setVisible(false);
			}
		});
	}
	
	public String getDizionario(){
		return dizionario;
	}
	
	public String getIgnoraPrima(){
		return Boolean.toString(ignoraPrima.isSelected());
	}
	
	public static HashMap<String, String> buildDizionario(String dizionario, String ignoraPrima, String fileDati) throws IOException{
		if(dizionario==null){
			return null;
		}
		if(dizionario.equals("PROVINCIA") || dizionario.equals("REGIONE")){
			int xx = dizionario.equals("PROVINCIA") ? 1 : 2;
			HashMap<String, String> risposta = new HashMap<String, String>();
			for(int i=0; i<baseSigle.length; i+=3){
				risposta.put(baseSigle[i], baseSigle[i+xx]);
			}
			return risposta;
		}if(dizionario.equals("ORIENTAMENTO")){
			return conversioneEsposizione;
		}else{
			// cerco il file: prima dove mi è stato detto e poi nella cartella dei dati
			File fileDaUsare = new File(dizionario);
			if(!fileDaUsare.exists()){
				// se il file non esiste lo cerco nella cartella in cui sta il file dei dati
				File fd = new File(fileDati);
				fileDaUsare = new File(fd.getParent()+File.separator+fileDaUsare.getName());
			}
			
			HashMap<String, String> risposta = new HashMap<String, String>();
			String riga;
			String parti[];
			FileInputStream fis = new FileInputStream(fileDaUsare);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader buffer = new BufferedReader(isr);
			
			if(ignoraPrima.equals(Boolean.TRUE)){
				buffer.readLine();
			}
			while(true){
				riga = buffer.readLine();
				if(riga==null){
					break;
				}
				parti = riga.split("\t");
				risposta.put(parti[0].trim(), parti[1].trim());
			}
			buffer.close();
			isr.close();
			fis.close();
			return risposta;
		}
	}
	
	private static final String baseSigle[] = {
	"AG","Agrigento","Sicilia",
	"AL","Alessandria ","Piemonte",
	"AN","Ancona ","Marche",
	"AO","Aosta","Valle d'Aosta",
	"AQ","Aquila","Abruzzo",
	"AR","Arezzo","Toscana",
	"AP","Ascoli-Piceno","Marche",
	"AT","Asti","Piemonte",
	"AV","Avellino","Campania",
	"BA","Bari","Puglia",
	"BT","Barletta-Andria-Trani","Puglia",
	"BL","Belluno","Veneto",
	"BN","Benevento","Campania",
	"BG","Bergamo","Lombardia",
	"BI","Biella","Piemonte",
	"BO","Bologna","Emilia Romagna",
	"BZ","Bolzano","Trentino Alto Adige",
	"BS","Brescia","Lombardia",
	"BR","Brindisi","Puglia",
	"CA","Cagliari","Sardegna",
	"CL","Caltanissetta","Sicilia",
	"CB","Campobasso","Molise",
	"CI","Carbonia Iglesias","Sardegna",
	"CE","Caserta","Campania",
	"CT","Catania","Sicilia",
	"CZ","Catanzaro","Calabria",
	"CH","Chieti","Abruzzo",
	"CO","Como","Lombardia",
	"CS","Cosenza","Calabria",
	"CR","Cremona","Lombardia",
	"KR","Crotone","Calabria",
	"CN","Cuneo","Piemonte",
	"EN","Enna","Sicilia",
	"FM","Fermo","Marche",
	"FE","Ferrara","Emilia Romagna",
	"FI","Firenze","Toscana",
	"FG","Foggia","Puglia",
	"FC","Forli-Cesena","Emilia Romagna",
	"FR","Frosinone","Lazio",
	"GE","Genova","Liguria",
	"GO","Gorizia","Friuli Venezia Giulia",
	"GR","Grosseto","Toscana",
	"IM","Imperia","Liguria",
	"IS","Isernia","Molise",
	"SP","La-Spezia","Liguria",
	"LT","Latina","Lazio",
	"LE","Lecce","Puglia",
	"LC","Lecco","Lombardia",
	"LI","Livorno","Toscana",
	"LO","Lodi","Lombardia",
	"LU","Lucca","Toscana",
	"MC","Macerata","Marche",
	"MN","Mantova","Lombardia",
	"MS","Massa-Carrara","Toscana",
	"MT","Matera","Basilicata",
	"VS","Medio Campidano","Sardegna",
	"ME","Messina","Sicilia",
	"MI","Milano","Lombardia",
	"MO","Modena","Emilia Romagna",
	"MB","Monza-Brianza","Lombardia",
	"NA","Napoli","Campania",
	"NO","Novara","Piemonte",
	"NU","Nuoro","Sardegna",
	"OG","Ogliastra","Sardegna",
	"OT","Olbia Tempio","Sardegna",
	"OR","Oristano","Sardegna",
	"PD","Padova","Veneto",
	"PA","Palermo","Sicilia",
	"PR","Parma","Emilia Romagna",
	"PV","Pavia","Lombardia",
	"PG","Perugia","Umbria",
	"PU","Pesaro-Urbino","Marche",
	"PE","Pescara","Abruzzo",
	"PC","Piacenza","Emilia Romagna",
	"PI","Pisa","Toscana",
	"PT","Pistoia","Toscana",
	"PN","Pordenone","Friuli Venezia Giulia",
	"PZ","Potenza","Basilicata",
	"PO","Prato","Toscana",
	"RG","Ragusa","Sicilia",
	"RA","Ravenna","Emilia Romagna",
	"RC","Reggio-Calabria","Calabria",
	"RE","Reggio-Emilia","Emilia Romagna",
	"RI","Rieti","Lazio",
	"RN","Rimini","Emilia Romagna",
	"Roma","Roma","Lazio",
	"RM","Roma","Lazio",
	"RO","Rovigo","Veneto",
	"SA","Salerno","Campania",
	"SS","Sassari","Sardegna",
	"SV","Savona","Liguria",
	"SI","Siena","Toscana",
	"SR","Siracusa","Sicilia",
	"SO","Sondrio","Lombardia",
	"TA","Taranto","Puglia",
	"TE","Teramo","Abruzzo",
	"TR","Terni","Umbria",
	"TO","Torino","Piemonte",
	"TP","Trapani","Sicilia",
	"TN","Trento","Trentino Alto Adige",
	"TV","Treviso","Veneto",
	"TS","Trieste","Friuli Venezia Giulia",
	"UD","Udine","Friuli Venezia Giulia",
	"VA","Varese","Lombardia",
	"VE","Venezia","Veneto",
	"VB","Verbania","Piemonte",
	"VC","Vercelli","Piemonte",
	"VR","Verona","Veneto",
	"VV","Vibo-Valentia","Calabria",
	"VI","Vicenza","Veneto",
	"VT","Viterbo","Lazio"
	};
	
	private static HashMap<String, String> conversioneEsposizione ;
	static {
		conversioneEsposizione = new HashMap<String, String>();
		for(int i=0; i<CampoEsposizione.traduzione.length ; i++){
			conversioneEsposizione.put(CampoEsposizione.traduzione[i].getEsterno(), CampoEsposizione.traduzione[i].getDescrizione());
			conversioneEsposizione.put(CampoEsposizione.traduzione[i].getEsterno().toLowerCase(), CampoEsposizione.traduzione[i].getDescrizione());
		}
		for(int i=0; i<CampoEsposizione.traduzioneEN.length ; i++){
			conversioneEsposizione.put(CampoEsposizione.traduzioneEN[i].getEsterno(), CampoEsposizione.traduzioneEN[i].getDescrizione());
			conversioneEsposizione.put(CampoEsposizione.traduzioneEN[i].getEsterno().toLowerCase(), CampoEsposizione.traduzioneEN[i].getDescrizione());
		}
	}
	
}
