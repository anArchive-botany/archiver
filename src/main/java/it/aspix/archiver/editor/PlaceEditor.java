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
import it.aspix.archiver.archiver.ManagerSuggerimenti;
import it.aspix.archiver.componenti.CampoCoordinata;
import it.aspix.archiver.componenti.CampoEsposizione;
import it.aspix.archiver.componenti.CampoInclinazione;
import it.aspix.archiver.componenti.CampoTestoUnicode;
import it.aspix.archiver.componenti.ComboBoxModelED;
import it.aspix.archiver.componenti.ComboBoxModelEDFactory;
import it.aspix.archiver.componenti.ComboSuggerimenti;
import it.aspix.archiver.componenti.CampoCoordinata.Asse;
import it.aspix.archiver.dialoghi.SelezioneComune;
import it.aspix.archiver.nucleo.Cache;
import it.aspix.archiver.nucleo.Comunicatore;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.Place;
import it.aspix.sbd.obj.SimpleBotanicalData;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class PlaceEditor extends JPanel{

    private static final long serialVersionUID = 1L;
    /** impronta suggerimento serve per lavorare con la cache, una volta recuperato un suggerimento
     *  la sua impronta (concatenazione dei campi) viene registrata in questa variabile.
     *  Al momento del get() (fase dalla cui si suppone, solo suppone, sebbene ragionevolmente, 
     *  il dato venga inviato al server) se l'impronta cambia si invalida l'entrata in cache  */
    private String improntaSuggerimento;
    /** è null se l'entrata rappresenta un comune e quindi non c'è nulla da fare */
    private String entrataInCache;
    /** il colore originale del testo delle caselle di testo prima dell'inserimento dei dati */
    private Color oldColorTextField;
    /** il colore originale del testo del pulsante prima dell'inserimento dei dati */
    private Color oldColorButton;

    ComboBoxModelED modelloTipoAreaProtetta = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.TIPO_AREA_PROTETTA);
    ComboBoxModelED modelloSorgente = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.SORGENTE);

    GridBagLayout layoutPanneloGeografici= new GridBagLayout();
    
    JSeparator separatorePolitici        = new JSeparator();
    JLabel eLocalita                     = new JLabel();
    CampoTestoUnicode localita           = new CampoTestoUnicode();
    JLabel eComune                       = new JLabel();
    CampoTestoUnicode comune             = new CampoTestoUnicode();
    JLabel eProvincia                    = new JLabel();
    CampoTestoUnicode provincia          = new CampoTestoUnicode();
    JLabel eRegione                      = new JLabel();
    CampoTestoUnicode regione            = new CampoTestoUnicode();
    JLabel eStato                        = new JLabel();
    CampoTestoUnicode stato              = new CampoTestoUnicode();
    JLabel eMacroLocalita                = new JLabel();
    CampoTestoUnicode macroLocalita      = new CampoTestoUnicode();
    
    JSeparator separatoreReticoli        = new JSeparator();
    JLabel eReticoli                     = new JLabel();
    JLabel eReticoloPrincipale           = new JLabel();
    CampoTestoUnicode reticoloPrincipale = new CampoTestoUnicode();
    JLabel eReticoloSecondario           = new JLabel();
    CampoTestoUnicode reticoloSecondario = new CampoTestoUnicode();
    JLabel eIdInfc                       = new JLabel();
    CampoTestoUnicode idInfc             = new CampoTestoUnicode();

    JSeparator separatoreAreaProtetta  = new JSeparator();
    JLabel eAreaProtetta               = new JLabel();
    JLabel eTipoAreaProtetta           = new JLabel();
    JComboBox tipoAreaProtetta         = new JComboBox();
    JLabel eNomeAreaProtetta           = new JLabel();
    ComboSuggerimenti nomeAreaProtetta; // creata nel costruttore
    
    JSeparator separatorePunto = new JSeparator();
    JLabel ePunto          = new JLabel();
    JLabel eSorgente       = new JLabel();
    JComboBox sorgente     = new JComboBox();
    JLabel eLatitudine     = new JLabel();
    CampoCoordinata latitudine  = new CampoCoordinata(Asse.LATITUDINE);
    JLabel eLongitudine    = new JLabel();
    CampoCoordinata longitudine = new CampoCoordinata(Asse.LONGITUDINE);
    JLabel ePrecisione     = new JLabel();
    String[] precisioniPossibili = {
            "25",
            "50",
            "100",
            "250",
            "500",
            "1000",
            "2500",
            "5000",
            "10000",
            "100000"
    };
    JComboBox precisione   = new JComboBox(precisioniPossibili);

    JSeparator separatoreAspetto   = new JSeparator();
    JLabel eAltitudine             = new JLabel();
    CampoTestoUnicode altitudine   = new CampoTestoUnicode();
    JLabel eEsposizione            = new JLabel();
    CampoEsposizione esposizione   = new CampoEsposizione();
    JLabel eInclinazione           = new JLabel();
    CampoInclinazione inclinazione = new CampoInclinazione();
    JLabel eSubstrato              = new JLabel();
    CampoTestoUnicode substrato    = new CampoTestoUnicode();
    JLabel eHabitat                = new JLabel();
    CampoTestoUnicode habitat      = new CampoTestoUnicode();

    JPanel conversioni = new JPanel(new GridBagLayout());
    JLabel eEpsg = new JLabel();
    // http://spatialreference.org/ref/epsg/
    public static String[] epsgPossibili = {
            "32632 - WGS 84 / UTM zone 32N",
            "32633 - WGS 84 / UTM zone 33N",
            "23032 - ED50 / UTM zone 32N",
            "23033 - ED50 / UTM zone 33N",
            "3003 - Monte Mario / Italy zone 1",
            "3004 - Monte Mario / Italy zone 2"
    };
    JComboBox epsg = new JComboBox(epsgPossibili);
    JLabel eX = new JLabel();
    JTextField x = new JTextField();
    JLabel eY = new JLabel();
    JTextField y = new JTextField();
    JButton convertiCoordinate = new JButton();
    
    public PlaceEditor(String proprietaContenitore){
        nomeAreaProtetta = new ComboSuggerimenti(Proprieta.recupera(proprietaContenitore),"protectedAreaName",3,true,false,null);
        localita.setName("place.name");
        comune.setName("place.town");
        latitudine.setName("place.latitude");
        longitudine.setName("place.longitude");
        reticoloPrincipale.setName("place.mainGrid");
        x.setName("place.TMP.x");
        y.setName("place.TMP.y");
        // ----------------------------- testi ------------------------------
        eLocalita.setText("* localit\u00e0:");
        eComune.setText("comune:");
        eProvincia.setText("provincia:");
        eRegione.setText("regione:");
        eStato.setText("stato:");
        eMacroLocalita.setText("macro localit\u00e0:");
        eAreaProtetta.setText("area protetta");
        eTipoAreaProtetta.setText("tipo:");
        eNomeAreaProtetta.setText("nome:");     
        eReticoli.setText("reticoli");
        eReticoloPrincipale.setText("RCFR:");
        eReticoloSecondario.setText("UTM:");
        eIdInfc.setText("ID INFC:");
        ePunto.setText("punto:");
        eSorgente.setText("sorgente:");
        eLatitudine.setText("latitudine:");
        eLongitudine.setText("longitudine:");
        ePrecisione.setText("precisione:");
        eAltitudine.setText("altitudine:");
        eEsposizione.setText("esp.:");
        eInclinazione.setText("incl.:");
        eSubstrato.setText("substrato:");
        eHabitat.setText("habitat:");
        // pannello per le conversioni
        eEpsg.setText("epsg:");
        eX.setText("x:");
        eY.setText("y:");
        convertiCoordinate.setText("converti");
        JLabel avvisoAreaProtetta = new JLabel("I valori \"EU-SIC\" e \"ZPS\" non vanno usati, al loro posto va usato l'attributo \"Codice N2000\" nella sezione attributi");
        avvisoAreaProtetta.setForeground(Color.RED);
        
        // -------------------- inserimento nei pannelli --------------------
        this.setLayout(layoutPanneloGeografici);
        
        this.add(eLocalita,             new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(localita,              new GridBagConstraints(2, 1, 5, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
             
        this.add(eComune,               new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(comune,                new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eProvincia,            new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(provincia,             new GridBagConstraints(4, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eRegione,              new GridBagConstraints(5, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(regione,               new GridBagConstraints(6, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        
        this.add(eMacroLocalita,        new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(macroLocalita,         new GridBagConstraints(2, 3, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));   
        this.add(eStato,                new GridBagConstraints(5, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(stato,                 new GridBagConstraints(6, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));

        this.add(separatoreAreaProtetta,new GridBagConstraints(0, 4, 7, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsSeparatore, 0, 0));
        this.add(eAreaProtetta,         new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,     CostantiGUI.insetsEtichetta, 0, 0));
        this.add(eTipoAreaProtetta,     new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(tipoAreaProtetta,      new GridBagConstraints(2, 5, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eNomeAreaProtetta,     new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));             
        this.add(nomeAreaProtetta,      new GridBagConstraints(4, 5, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));

        this.add(avvisoAreaProtetta,    new GridBagConstraints(0, 6, 7, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsSeparatore, 0, 0));
        
        this.add(separatoreReticoli,    new GridBagConstraints(0, 7, 7, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsSeparatore, 0, 0));
        this.add(eReticoli,             new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,     CostantiGUI.insetsEtichetta, 0, 0));
        this.add(eReticoloPrincipale,   new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(reticoloPrincipale,    new GridBagConstraints(2, 8, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eReticoloSecondario,   new GridBagConstraints(3, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));             
        this.add(reticoloSecondario,    new GridBagConstraints(4, 8, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eIdInfc,               new GridBagConstraints(5, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));             
        this.add(idInfc,                new GridBagConstraints(6, 8, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
       
        this.add(separatorePunto,       new GridBagConstraints(0, 9, 7, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsSeparatore, 0, 0));
        this.add(conversioni,           new GridBagConstraints(2,10, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsGruppo, 0, 0));
        this.add(ePunto,                new GridBagConstraints(0,11, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(eSorgente,             new GridBagConstraints(1,11, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(sorgente,              new GridBagConstraints(2,11, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eLatitudine,           new GridBagConstraints(1,12, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(latitudine,            new GridBagConstraints(2,12, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eLongitudine,          new GridBagConstraints(3,12, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(longitudine,           new GridBagConstraints(4,12, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(ePrecisione,           new GridBagConstraints(5,12, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(precisione,            new GridBagConstraints(6,12, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
               
        this.add(separatoreAspetto,     new GridBagConstraints(0,14, 7, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsSeparatore, 0, 0));
        this.add(eAltitudine,           new GridBagConstraints(1,15, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(altitudine,            new GridBagConstraints(2,15, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eEsposizione,          new GridBagConstraints(3,15, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(esposizione,           new GridBagConstraints(4,15, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eInclinazione,         new GridBagConstraints(5,15, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(inclinazione,          new GridBagConstraints(6,15, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eSubstrato,            new GridBagConstraints(1,16, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(substrato,             new GridBagConstraints(2,16, 5, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eHabitat,              new GridBagConstraints(1,17, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        this.add(habitat,               new GridBagConstraints(2,17, 5, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));    
        
        conversioni.add(eEpsg,              new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        conversioni.add(epsg,               new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0)); 
        conversioni.add(eX,                 new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        conversioni.add(x,                  new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        conversioni.add(eY,                 new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        conversioni.add(y,                  new GridBagConstraints(5, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        conversioni.add(convertiCoordinate, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        
        
        // ------------------------------- ascoltatori ----------------------
        localita.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { localitaComune_keyPressed(e); }
        });
        comune.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { localitaComune_keyPressed(e); }
        });
        convertiCoordinate.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				convertiCoordinate_actionPerformed(e);
			}
        });
        x.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { conversione_keyPressed(e); }
        });
        y.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { conversione_keyPressed(e); }
        });
        
        // ---------------------------------- impostazioni ------------------
        tipoAreaProtetta.setModel(modelloTipoAreaProtetta);
        sorgente.setModel(modelloSorgente);
                
        int altezza=comune.getPreferredSize().height;
        comune.setPreferredSize(new Dimension(100, comune.getPreferredSize().height));
        provincia.setPreferredSize(new Dimension(100, provincia.getPreferredSize().height));
        latitudine.setPreferredSize(new Dimension(140,altezza));
        longitudine.setPreferredSize(new Dimension(140,altezza));
        
        Font usuale=eLocalita.getFont(); 
        Font piccolo=new Font(usuale.getName(),usuale.getStyle(), (int)(usuale.getSize()*0.75) );
        eAreaProtetta.setFont(piccolo);
        eReticoli.setFont(piccolo);
        ePunto.setFont(piccolo);
        
        // a mali estremi!! il -40 seerve perché i combo hanno la larghezza dell'elemento più lungo: inutile qui
        int elementi=this.getComponentCount();
        Component comp;
        Dimension dimCB = new Dimension( tipoAreaProtetta.getMinimumSize().width-40, tipoAreaProtetta.getPreferredSize().height);
        Dimension dimTF = new Dimension( tipoAreaProtetta.getMinimumSize().width-40, localita.getPreferredSize().height);
        for(int i=0;i<elementi;i++){
            comp = this.getComponent(i);
            if(comp instanceof JComboBox ){
                ((JComboBox)comp).setPreferredSize(dimCB);
                ((JComboBox)comp).setMinimumSize(dimCB);
            }
            if(comp instanceof JTextField ){
                ((JTextField)comp).setPreferredSize(dimTF);
                ((JTextField)comp).setMinimumSize(dimTF);
            }
        }
        precisione.setEditable(true);
    	oldColorTextField = x.getForeground();
    	oldColorButton = convertiCoordinate.getForeground();
        ManagerSuggerimenti.check(this);
    }
    
    void localitaComune_keyPressed(KeyEvent e) {
        if(KeyEvent.getKeyText(e.getKeyCode()).equals("E") && e.getModifiers()==InputEvent.CTRL_MASK){
            try{
                improntaSuggerimento = ""; // se richiede un suggerimento l'impronta è il vuoto
                Stato.debugLog.warning("ricerca comuni");
                //fa cercare i dati del comune
                Comunicatore.SuggerimentiGeografici oggettoCercato;
          
                Place[] posti;
                if(e.getSource()==localita){
                    posti = Stato.comunicatore.suggerimentiGeografici(localita.getText(),Comunicatore.SuggerimentiGeografici.LOCALITA).getPlace();
                    oggettoCercato=Comunicatore.SuggerimentiGeografici.LOCALITA;
                    entrataInCache = localita.getText();
                }else{
                    posti = Stato.comunicatore.suggerimentiGeografici(comune.getText(),Comunicatore.SuggerimentiGeografici.COMUNE).getPlace();
                    oggettoCercato=Comunicatore.SuggerimentiGeografici.COMUNE;
                    entrataInCache = null;
                }
                if(posti == null || posti.length==0){
                    Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Nessun suggerimento recuperato.","it",MessageType.INFO));
                    e.consume();
                    return;
                }
                if(posti.length==1){
                    Stato.debugLog.fine("Recuperato un comune");
                    Place c = posti[0];
                    boolean confrontoPositivo=false;
                    if(oggettoCercato==Comunicatore.SuggerimentiGeografici.LOCALITA)
                        confrontoPositivo=localita.getText().equals(c.getName());
                    else
                        confrontoPositivo=comune.getText().equals(c.getTown());
                    if(confrontoPositivo){
                        // se quello trovato è esattamente quello che cercavo lo scrivo a schermo
                        impostaLocalitaDaSuggerimento(e,c);
                    }else{
                        // se il suggerimento E' stato trovato per approssimazione chiedo conferma
                        SelezioneComune sc=new SelezioneComune();
                        sc.setAlternative(posti);
                        sc.setLocationRelativeTo(localita);
                        sc.setVisible(true);
                        c=sc.getSelezionato();
                        if(c!=null){
                            impostaLocalitaDaSuggerimento(e,c);
                        }
                    }
                }
                // quello sotto non è un else perché gli elementi trovati potrebbero essere anche zero
                if(posti.length>1){
                    Stato.debugLog.warning("Recuperati piu' comuni");
                    SelezioneComune sc=new SelezioneComune();
                    sc.setAlternative(posti);
                    sc.setLocationRelativeTo(localita);
                    sc.setVisible(true);
                    Place c=sc.getSelezionato();
                    if(c!=null){
                        impostaLocalitaDaSuggerimento(e,c);
                    }
                }
                Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Suggerimento recuperato.","it",MessageType.INFO));
                e.consume();
            }catch(Exception ex){
                Dispatcher.consegna(this, ex);
            }
        }
    }
    
    void conversione_keyPressed(KeyEvent e){
    	x.setForeground(CostantiGUI.coloreAttenzione);
    	y.setForeground(CostantiGUI.coloreAttenzione);
    	convertiCoordinate.setForeground(CostantiGUI.coloreAttenzione);
    }
    
    private void convertiCoordinate_actionPerformed(ActionEvent e){
    	SimpleBotanicalData risposta;
    	String codiceEpsg;
    	String tmp;
    	
    	tmp = (String) epsg.getSelectedItem();
    	codiceEpsg = tmp.substring(0,tmp.indexOf(' '));
    	Stato.debugLog.warning("cerco converisone per epsg \""+codiceEpsg+"\"");
    	try {
			risposta = Stato.comunicatore.conversioneCoordinate(codiceEpsg, x.getText(), y.getText());
			if(risposta.getPlaceSize()==1){
				latitudine.setText(risposta.getPlace(0).getLatitude());
				longitudine.setText(risposta.getPlace(0).getLongitude());
				x.setForeground(oldColorTextField);
				y.setForeground(oldColorTextField);
				convertiCoordinate.setForeground(oldColorButton);
			}else{
				Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Coordinata non convertibile.","it",MessageType.INFO));
			}
		}catch(Exception ex){
            Dispatcher.consegna(this, ex);
        }
    }
    
    /** A solo uso locale per razionalizzare il codice (viene chiamata in 3 diversi punti di localitaComune_keyPressed() */
    private final void impostaLocalitaDaSuggerimento(KeyEvent e, Place c){
        if(e.getSource()==localita){  
            // modifico la località se la richiesta viene dalla casella località
            localita.setText(c.getName());
        }
        comune.setText(c.getTown());
        provincia.setText(c.getProvince());
        regione.setText(c.getRegion());
        stato.setText(c.getCountry());
        macroLocalita.setText(c.getMacroPlace());
        if(Proprieta.isTrue("generale.suggerimentiGeograficiEstesi") && e.getSource()==localita){
            reticoloPrincipale.setText(c.getMainGrid());
            reticoloSecondario.setText(c.getSecondaryGrid());
            modelloTipoAreaProtetta.setSelectedEnum(c.getProtectedAreaType());
            nomeAreaProtetta.setText(c.getProtectedAreaName());
            idInfc.setText(c.getIdInfc());
            latitudine.requestFocus();
        }else{
            reticoloPrincipale.requestFocus();
        }
        improntaSuggerimento = getImpronta();
    }
    
    private final String getImpronta(){
        StringBuffer sb = new StringBuffer();
        sb.append(localita.getText());
        sb.append(comune.getText());
        sb.append(provincia.getText());
        sb.append(regione.getText());
        sb.append(stato.getText());
        sb.append(macroLocalita.getText());
        // extended
        sb.append(reticoloPrincipale.getText());
        sb.append(reticoloSecondario.getText());
        sb.append(modelloTipoAreaProtetta.getSelectedEnum());
        sb.append(nomeAreaProtetta.getText());
        sb.append(idInfc.getText());
        Stato.debugLog.fine("impronta: "+sb.toString());
        return sb.toString();
    }
    
    public void setPlace(Place p){
        if(p == null){
        	throw new IllegalArgumentException("Place non può essere null");
        }
        entrataInCache = null;
        localita.setText(p.getName());
        comune.setText(p.getTown());
        provincia.setText(p.getProvince());
        regione.setText(p.getRegion());
        stato.setText(p.getCountry());
        macroLocalita.setText(p.getMacroPlace());
        
        modelloTipoAreaProtetta.setSelectedEnum(p.getProtectedAreaType());
        nomeAreaProtetta.setText(p.getProtectedAreaName());
        
        reticoloPrincipale.setText(p.getMainGrid());
        reticoloSecondario.setText(p.getSecondaryGrid());
        idInfc.setText(p.getIdInfc());
       
        modelloSorgente.setSelectedEnum(p.getPointSource());
        latitudine.setText(p.getLatitude());
        longitudine.setText(p.getLongitude());
        precisione.setSelectedItem(p.getPointPrecision());
        
        altitudine.setText(p.getElevation());
        esposizione.setText(p.getExposition());
        inclinazione.setText(p.getInclination());
        substrato.setText(p.getSubstratum());
        habitat.setText(p.getHabitat());
    }
    
    public Place getPlace(){
        Place p = new Place();
        // politici
        p.setName(localita.getText());
        p.setTown(comune.getText());
        p.setProvince(provincia.getText());
        p.setRegion(regione.getText());
        p.setCountry(stato.getText());
        p.setMacroPlace(macroLocalita.getText());
        // aree protette
        p.setProtectedAreaType(modelloTipoAreaProtetta.getSelectedEnum());
        p.setProtectedAreaName(nomeAreaProtetta.getText());
        // reticoli
        p.setMainGrid(reticoloPrincipale.getText());
        p.setSecondaryGrid(reticoloSecondario.getText());
        p.setIdInfc(idInfc.getText());
        // punto
        p.setPointSource(modelloSorgente.getSelectedEnum());
        p.setLatitude(latitudine.getText());
        p.setLongitude(longitudine.getText());
        if(precisione.getSelectedItem()!=null){
            p.setPointPrecision(precisione.getSelectedItem().toString());
        }else{
            p.setPointPrecision("");
        }
        // aspetto del terreno
        p.setElevation(altitudine.getText());
        p.setExposition(esposizione.getText());
        p.setInclination(inclinazione.getText());
        // aspetti fisici
        p.setSubstratum(substrato.getText());
        p.setHabitat(habitat.getText());
        
        // esegui i controlli sull'impronta per vedere se rimuovere una entrata dalla cache
        if(entrataInCache!=null){
            String i = getImpronta();
            if(!i.equals(improntaSuggerimento)){
                Stato.comunicatore.rimuoviDallaCache(Cache.ContenutoCache.LOCALITA,entrataInCache);
            }
        }
        return p;
    }
    
    public void clearPlace() {
        entrataInCache = null;
        localita.setText("");
        comune.setText("");
        provincia.setText("");
        regione.setText("");
        stato.setText("");
        reticoloPrincipale.setText("");
        reticoloSecondario.setText("");
        idInfc.setText("");

        tipoAreaProtetta.setSelectedIndex(0);
        nomeAreaProtetta.setText("");
        
        sorgente.setSelectedIndex(0);
        latitudine.setText("");
        longitudine.setText("");
        precisione.setSelectedIndex(2);
        
        altitudine.setText("");
        esposizione.setText("");
        inclinazione.setText("");
        substrato.setText("");
        habitat.setText("");
    }
}
