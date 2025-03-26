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
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.componenti.ComboBoxModelED;
import it.aspix.archiver.componenti.ComboBoxModelEDFactory;
import it.aspix.archiver.eventi.SistemaException;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Cell;
import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.Place;
import it.aspix.sbd.obj.Sample;
import it.aspix.sbd.obj.SpecieRef;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class SampleEditorSintetico extends SampleEditor {
    
    private static final long serialVersionUID = 1L;
    
    VincoliSpeciePresenti vsp; // creata in pannelloRilievoSemplificato()
    private Level strati[];
    private String scalaAbbondanza;
    
    GridBagLayout   lPannelloGenerale = new GridBagLayout();
    JPanel          pannelloGenerale = new JPanel(lPannelloGenerale);
    JLabel          eComunita = new JLabel();
    JTextField      comunita = new JTextField();
    JLabel          eProgetto = new JLabel();
    JTextField      progetto = new JTextField();
    JLabel          eProgrProgetto = new JLabel();
    JTextField      progrProgetto = new JTextField();
    JLabel          eIdProgetto = new JLabel();
    JTextField      idProgetto = new JTextField();
    
    JLabel          eSottoprogetto = new JLabel();
    JTextField      sottoprogetto = new JTextField();
    JLabel          eProgrSottoprogetto = new JLabel();
    JTextField      progrSottoprogetto = new JTextField();
    JLabel          eIdSottoprogetto = new JLabel();
    JTextField      idSottoprogetto = new JTextField();
    
    JLabel          eRilevatore = new JLabel();
    JTextField      rilevatore = new JTextField();  
    JLabel          eLocalita = new JLabel();
    JTextField      localita = new JTextField();    
    JLabel          eComune = new JLabel();
    JTextField      comune = new JTextField();
    JLabel          eRegione = new JLabel();
    JTextField      regione = new JTextField();
    
    JButton         specieConstraints = new JButton();
    
    public SampleEditorSintetico(){
        super();
        vsp = new VincoliSpeciePresenti();
        // ---------- le stringhe ---------
        eComunita.setText("comunita:");
        eProgetto.setText("progetto:");
        eProgrProgetto.setText("progr:");
        eIdProgetto.setText("id prog.:");
        eSottoprogetto.setText("sottopr.:");
        eProgrSottoprogetto.setText("progr:");
        eIdSottoprogetto.setText("id sott.:");
        eRilevatore.setText("rilevatore:");
        eLocalita.setText("localit\u00e0:");
        eComune.setText("comune:");
        eRegione.setText("reg.:");
        specieConstraints.setText("vincoli specie (assenti)");
        // ---------- inserimento nei pannelli ----------
        this.setLayout(new BorderLayout());
        this.add(pannelloGenerale,BorderLayout.NORTH);
        
        pannelloGenerale.add(eComunita,             new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloGenerale.add(comunita,              new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloGenerale.add(eProgetto,             new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloGenerale.add(progetto,              new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 50, 0));
        pannelloGenerale.add(eProgrProgetto,        new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloGenerale.add(progrProgetto,         new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 50, 0));
        pannelloGenerale.add(eIdProgetto,           new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloGenerale.add(idProgetto,            new GridBagConstraints(1, 2, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        
        pannelloGenerale.add(eSottoprogetto,        new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloGenerale.add(sottoprogetto,         new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloGenerale.add(eProgrSottoprogetto,   new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloGenerale.add(progrSottoprogetto,    new GridBagConstraints(3, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloGenerale.add(eIdSottoprogetto,      new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloGenerale.add(idSottoprogetto,       new GridBagConstraints(1, 4, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        
        pannelloGenerale.add(eRilevatore,           new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloGenerale.add(rilevatore,            new GridBagConstraints(1, 5, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloGenerale.add(eLocalita,             new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloGenerale.add(localita,              new GridBagConstraints(1, 6, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloGenerale.add(eComune,               new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloGenerale.add(comune,                new GridBagConstraints(1, 7, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloGenerale.add(eRegione,              new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloGenerale.add(regione,               new GridBagConstraints(3, 7, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        
        pannelloGenerale.add(specieConstraints,     new GridBagConstraints(1, 8, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        // ---------- ascoltatori ----------
        specieConstraints.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { specieConstraints_actionPerformed(e); }
        });
        // ---------- impostazioni ----------
        this.setOpaque(false);
        pannelloGenerale.setOpaque(false);
        this.setBorder(BorderFactory.createEmptyBorder(0,0,10,5));
        if(Stato.isMacOSX){
            specieConstraints.setOpaque(false);
        }
        
        progetto.setText(Proprieta.recupera("vegetazione.database"));
    }
    
    /************************************************************************
     * L'immissione dei vincoli per le specie presenti
     * @param e
     ***********************************************************************/
    void specieConstraints_actionPerformed(ActionEvent e) {
        vsp.setVisible(true);
        strati=vsp.getLevel();
        scalaAbbondanza=vsp.getScalaAbbondanza();
        if(strati.length==0)
            specieConstraints.setText("vincoli specie (assenti)");
        else
            specieConstraints.setText("vincoli specie (presenti)");
    }

    /************************************************************************
     * @throws ValoreException 
     * @throws SistemaException 
     * @see it.aspix.archiver.editor.SampleEditor#setPlot(it.aspix.sbd.obj.Sample)
     ***********************************************************************/
    @Override
    public void setSample(Sample plot) {
        ; // completamente e volutamente ignorata
    }

    @Override
    public Sample getSample() {
        Sample plot = new Sample();
        DirectoryInfo di = new DirectoryInfo();
        Cell cella = new Cell();
        Place pla = new Place();
        
        plot.setCommunity(comunita.getText());
        di.setContainerName(progetto.getText());
        di.setContainerSeqNo(progrProgetto.getText());
        di.setContainerExternalId(idProgetto.getText());
        di.setSubContainerName(sottoprogetto.getText());
        di.setSubContainerSeqNo(progrSottoprogetto.getText());
        di.setSubContainerExternalId(idSottoprogetto.getText());
        plot.setDirectoryInfo(di);
        plot.setSurveyer(rilevatore.getText());  
        pla.setName(localita.getText());
        pla.setTown(comune.getText());
        pla.setRegion(regione.getText());
        plot.setPlace(pla);
        cella.setAbundanceScale(scalaAbbondanza);
        if(strati!=null)
            for(int i=0;i<strati.length;i++)
                cella.addLevel(strati[i]);
        plot.setCell(cella);
        return plot;
    }
    
    @Override
    public String getLevelsSchema() {
        // questa classe non visualizza i livelli
        return null;
    }
    
    /************************************************************************
     * La finestra che permette di inserire i dati sulle specie presenti nel
     * rilievo da cercare
     ***********************************************************************/
    private class VincoliSpeciePresenti extends JDialog{

        private static final long serialVersionUID = 1L;
        
        private static final String QUALSIASI_LIVELLO = "qualsiasi";
        
        BorderLayout lPannelloPrincipale = new BorderLayout();
        JPanel pannelloPrincipale = new JPanel(lPannelloPrincipale);
        
        GridBagLayout lPannelloPulsanti = new GridBagLayout();
        JPanel pannelloPulsanti = new JPanel(lPannelloPulsanti);
        JButton pulisci = new JButton();
        JButton ok = new JButton();
        
        BorderLayout lPannelloScala = new BorderLayout();
        JPanel pannelloScala = new JPanel(lPannelloScala);
        JLabel eScalaAbbondanza         = new JLabel();
        ComboBoxModelED modelloScalaAbbondanza  = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.SCALA_ABBONDANZA);
        JComboBox scalaAbbondanzaVincoli   = new JComboBox(modelloScalaAbbondanza);

        GridLayout lPannelloNomi = new GridLayout(0,1);
        JPanel pannelloNomi = new JPanel(lPannelloNomi);
        
        Border[] bordo                  = new Border[4];    
        GridBagLayout[] layoutGruppo    = new GridBagLayout[4];
        JPanel[]        pannelloGruppo  = new JPanel[4];
        JLabel          eNome[][]       = new JLabel[4][3];
        SpecieRefEditor nome[][]        = new SpecieRefEditor[4][3];
        JLabel          eStrato[][]     = new JLabel[4][3];
        JComboBox       strato[][]      = new JComboBox[4][3];
        JLabel          ePresenza[][]   = new JLabel[4][3];
        JTextField      presenza[][]    = new JTextField[4][3];
        
        public VincoliSpeciePresenti(){
            System.out.println( modelloScalaAbbondanza.getSize() );
            eScalaAbbondanza.setText("Scala abbondanza:");
            scalaAbbondanzaVincoli.setSelectedIndex(1);
            
            int i,j;
            for(i=0;i<4;i++){
                bordo[i] = BorderFactory.createCompoundBorder( 
                    BorderFactory.createEmptyBorder(20,0,0,0),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(CostantiGUI.coloreBordi),(i>0?"e anche ":"")+"una specie tra le seguenti"),
                            BorderFactory.createEmptyBorder(10,0,0,0)
                    )
                );
                layoutGruppo[i]=new GridBagLayout();
                pannelloGruppo[i]=new JPanel(layoutGruppo[i]);
                pannelloGruppo[i].setBorder(bordo[i]);
                for(j=0;j<3;j++){
                    eNome[i][j]=new JLabel("specie:");
                    nome[i][j]=new SpecieRefEditor();
                    eStrato[i][j]=new JLabel("strato:");
                    strato[i][j]=new JComboBox();
                    strato[i][j].setModel(new DefaultComboBoxModel(new String[]{QUALSIASI_LIVELLO,"1","2","3","4","5","6","7","8"}));
                    ePresenza[i][j]=new JLabel("pres. min:");
                    presenza[i][j]=new JTextField();                
                }
            }
            // ---------- le stringhe ---------
            ok.setText("ok");
            pulisci.setText("pulisci tutto");
            this.setTitle("Specie che caratterizzano il rilievo");
            // ---------- inserimento nei pannelli ----------
            
            this.getContentPane().add(pannelloPrincipale);
            pannelloPrincipale.add(pannelloScala,BorderLayout.NORTH);
            pannelloPrincipale.add(pannelloNomi,BorderLayout.CENTER);
            pannelloPrincipale.add(pannelloPulsanti,BorderLayout.SOUTH);
                                        
            pannelloScala.add(eScalaAbbondanza, BorderLayout.WEST);
            pannelloScala.add(scalaAbbondanzaVincoli, BorderLayout.CENTER);
            
            for(i=0;i<4;i++){
                for(j=0;j<3;j++){
                    pannelloGruppo[i].add(eNome[i][j],    new GridBagConstraints(0+j*4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,    CostantiGUI.insetsEtichetta,        0, 0));
                    pannelloGruppo[i].add(nome[i][j],     new GridBagConstraints(1+j*4, 0, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,    CostantiGUI.insetsDatoTesto,      120, 0));
                    pannelloGruppo[i].add(eStrato[i][j],  new GridBagConstraints(0+j*4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,    CostantiGUI.insetsEtichetta,        0, 0));
                    pannelloGruppo[i].add(strato[i][j],   new GridBagConstraints(1+j*4, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,    CostantiGUI.insetsDatoTesto,        0, 0));
                    pannelloGruppo[i].add(ePresenza[i][j],new GridBagConstraints(2+j*4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,    CostantiGUI.insetsEtichetta,        0, 0));
                    pannelloGruppo[i].add(presenza[i][j], new GridBagConstraints(3+j*4, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,    CostantiGUI.insetsDatoTesto,      0, 0));
                }
                pannelloNomi.add(pannelloGruppo[i]);
            }       
            pannelloPulsanti.add(pulisci,new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,     CostantiGUI.insetsAzione, 0, 0));
            pannelloPulsanti.add(ok,     new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,     CostantiGUI.insetsAzione, 100, 0));
            
            ok.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) { ok_actionPerformed(); }
            });
            pulisci.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) { pulisci_actionPerformed(); }
            });
            
            pannelloPrincipale.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
            // ---------- impostazioni ----------
            this.setModal(true);
            this.pack();
            UtilitaGui.centraDialogoAlloSchermo(this, UtilitaGui.CENTRO);
        }
            
        /************************************************************************
         * premuto il tasto OK
         ***********************************************************************/
        protected void ok_actionPerformed() {
            this.setVisible(false);
        }
        
        /************************************************************************
         * rimozione di tutti i dati presenti
         ***********************************************************************/
        protected void pulisci_actionPerformed() {
            for(int riga=0 ; riga<4 ; riga++)
                for(int colonna=0; colonna<3 ; colonna++){
                    nome[riga][colonna].setSpecieRef(new SpecieRef());
                    strato[riga][colonna].setSelectedIndex(0);
                    presenza[riga][colonna].setText("");
                }
        }
        
        /************************************************************************
         * @return la scala di abbondanza selezionata
         ***********************************************************************/
        public String getScalaAbbondanza(){
            return modelloScalaAbbondanza.getSelectedEnum();
        }
        
        /************************************************************************
         * I nomi degli strati sono fatti convenzionalmente così:
         * sMN.L dove:
         *      s: il carattere 's'
         *      M: il gruppo a cui appartie (i gruppi vanno in AND)
         *         numero da 0 a 9
         *      N: l'elemento all'interno del gruppo (gli elementi vanno in OR)
         *         numero da 0 a 9
         *      -: il carattere '-'
         *      L: il numero del livello di cui si sta parlando
         *         eventualemente 'x' per 'Qualsiasi strato'
         * @return un array di sttrati contenenti una singola specie
         ***********************************************************************/
        public Level[] getLevel(){
            Level level[];          // conterrà tutti gli strati del rilievo
            int stratiPresenti=0;   // numero degli strati presenti
            int i,j;                // per scandire gli elementi dell'interfaccia
            int contatoreStrati;    // viene incrementato durante l'inserimento
            SurveyedSpecie  ss;     // specie rilevata
            String buffer;
            
            for(i=0;i<4;i++){
                for(j=0;j<3;j++){
                    if(nome[i][j].getSpecieRef().getName()!=null && nome[i][j].getSpecieRef().getName().length()>0)
                        stratiPresenti++;
                }
            }
            Stato.debugLog.fine("Condizioni contate:"+stratiPresenti);
            level = new Level[stratiPresenti];
            contatoreStrati=0;
            for(i=0;i<4;i++)
                for(j=0;j<3;j++){
                    if(nome[i][j].getSpecieRef().getName()!=null && nome[i][j].getSpecieRef().getName().length()>0){
                        Stato.debugLog.fine("Elaboro la condizione:"+contatoreStrati);
                        level[contatoreStrati] = new Level();
                        buffer=(String)(strato[i][j].getModel().getElementAt(strato[i][j].getSelectedIndex()));
                        if(buffer.equals(QUALSIASI_LIVELLO))
                            level[contatoreStrati].setName("s"+i+j+"-x");
                        else
                            level[contatoreStrati].setName("s"+i+j+"-"+buffer);
                        ss = new SurveyedSpecie();
                        ss.setSpecieRefName(nome[i][j].getSpecieRef().getName());
                        buffer=nome[i][j].getSpecieRef().getAliasOf();
                        if(buffer!=null && buffer.length()>0)
                            ss.setSpecieRefAliasOf(nome[i][j].getSpecieRef().getAliasOf());
                        buffer=presenza[i][j].getText();
                        if(buffer!=null && buffer.length()>0)
                            ss.setAbundance(buffer);
                        level[contatoreStrati].addSurveyedSpecie(ss);
                        Stato.debugLog.fine(level[contatoreStrati].toXMLString(false));
                        contatoreStrati++;
                    }   
                }
            Stato.debugLog.fine("Condizioni inserite:"+contatoreStrati);
            return level;   
        }
    }// FINE inner-class VincoliSpeciePresenti

    @Override
    public String toString() {
        return "rilievi";
    }
    
    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
	public String getSuggerimenti() {
		return null;
	}
    
}