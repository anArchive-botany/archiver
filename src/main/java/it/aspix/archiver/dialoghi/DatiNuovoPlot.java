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
import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.UtilitaGui;
import it.aspix.sbd.obj.Sample;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/****************************************************************************
 * Dialogo usato per la creazione di un nuovo Plot, raccoglie tutte 
 * le informazioni essenziali
 ***************************************************************************/
public class DatiNuovoPlot extends JDialog {

    private static final long serialVersionUID = 1L;
    private boolean accettato;

    private GridBagLayout layoutPrincipale = new GridBagLayout();
    private JPanel pannelloPrincipale = new JPanel(layoutPrincipale);
    
    // dimensioni del nuovo plot
    private GridBagLayout layoutPannelloDimensioni = new GridBagLayout();
    private JPanel pannelloDimensioni = new JPanel(layoutPannelloDimensioni);
    private JLabel eLunghezza = new JLabel();
    private JTextField lunghezza = new JTextField();
    private JLabel eLarghezza = new JLabel();
    private JTextField larghezza = new JTextField();
    private JLabel eSuperficie = new JLabel();
    private JTextField superficie = new JTextField();

    // dimensioni del nuovo settore (più piccola parte del plot)
    private GridBagLayout layoutPannelloDimensioniSettore = new GridBagLayout();
    private JPanel pannelloDimensioniSettore = new JPanel(layoutPannelloDimensioniSettore);
    private JLabel eLunghezzaSettore = new JLabel();
    private JTextField lunghezzaSettore = new JTextField();
    private JLabel eLarghezzaSettore = new JLabel();
    private JTextField larghezzaSettore = new JTextField();
    private JLabel eSuperficieSettore = new JLabel();
    private JTextField superficieSettore = new JTextField();
    
    private GridBagLayout layoutPannelloDivisioni           = new GridBagLayout();
    private JPanel pannelloDivisioni                        = new JPanel(layoutPannelloDivisioni);
    // pannello delle divisioni
    private static final int NUMERO_DIVISIONI=5;
    private JLabel eNomeDivisione[] = new JLabel[NUMERO_DIVISIONI];
    private JTextField nomeDivisione[] = new JTextField[NUMERO_DIVISIONI];
    private JLabel eNumeroRighe[] = new JLabel[NUMERO_DIVISIONI];
    private JTextField numeroRighe[] = new JTextField[NUMERO_DIVISIONI];
    private JLabel eNumeroColonne[] = new JLabel[NUMERO_DIVISIONI];
    private JTextField numeroColonne[] = new JTextField[NUMERO_DIVISIONI];
    
    private GridBagLayout layoutPulsanti    = new GridBagLayout();
    private JPanel pannelloPulsanti         = new JPanel(layoutPulsanti);
    private JButton ok                  = new JButton();
    private  JButton annulla                = new JButton();
    
    public DatiNuovoPlot() {
        try {
            int i;
            // ---------- creazione oggetti ---------
            for(i=0;i<NUMERO_DIVISIONI;i++){
                eNomeDivisione[i] = new JLabel("nome:");
                nomeDivisione[i] = new JTextField();
                eNumeroRighe[i] = new JLabel("suddiviso in  righe:");
                numeroRighe[i] = new JTextField();
                numeroRighe[i].addKeyListener( new KeyAdapter(){
                	public void keyReleased(KeyEvent arg0) {
						ricalcolaDimensioni();
					}
                	public void keyTyped(KeyEvent arg0) {
						ricalcolaDimensioni();
					}
                });
                eNumeroColonne[i] = new JLabel("colonne:");
                numeroColonne[i] = new JTextField();
                numeroColonne[i].addKeyListener( new KeyAdapter(){
                	public void keyReleased(KeyEvent arg0) {
						ricalcolaDimensioni();
					}
                	public void keyTyped(KeyEvent arg0) {
						ricalcolaDimensioni();
					}
                });
            }

            // ---------- le stringhe ----------
            this.setTitle("Dimensioni nuovo plot");
            ok.setText("ok");
            annulla.setText("annulla");
            eLunghezza.setText("altezza:");
            eLarghezza.setText("larghezza:");
            eSuperficie.setText("superficie:");
            eLunghezzaSettore.setText("altezza:");
            eLarghezzaSettore.setText("larghezza:");
            eSuperficieSettore.setText("superficie:");
            
            // ---------- inserimento nei pannelli ----------
            this.getContentPane().add(pannelloPrincipale);
            pannelloPrincipale.add(pannelloDimensioni,          new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsGruppo, 0, 0));
            pannelloPrincipale.add(pannelloDivisioni,           new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsGruppo, 0, 0));
            pannelloPrincipale.add(pannelloDimensioniSettore,   new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsGruppo, 0, 0));
            pannelloPrincipale.add(pannelloPulsanti,            new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsGruppo, 0, 0));
            
            pannelloDimensioni.add(eLunghezza,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDimensioni.add(lunghezza,   new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
            pannelloDimensioni.add(eLarghezza,  new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDimensioni.add(larghezza,   new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
            pannelloDimensioni.add(eSuperficie, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDimensioni.add(superficie,  new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
            
            pannelloDimensioniSettore.add(eLunghezzaSettore,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDimensioniSettore.add(lunghezzaSettore,     new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
            pannelloDimensioniSettore.add(eLarghezzaSettore,    new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDimensioniSettore.add(larghezzaSettore,     new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
            pannelloDimensioniSettore.add(eSuperficieSettore,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDimensioniSettore.add(superficieSettore,    new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
            
            for(i=0;i<NUMERO_DIVISIONI;i++){
                pannelloDivisioni.add(eNomeDivisione[i],    new GridBagConstraints(0, i, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,    CostantiGUI.insetsEtichetta, 0, 0));
                pannelloDivisioni.add(nomeDivisione[i],     new GridBagConstraints(1, i, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
                if(i<NUMERO_DIVISIONI-1){
                    pannelloDivisioni.add(eNumeroRighe[i],  new GridBagConstraints(2, i, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,    CostantiGUI.insetsEtichetta, 0, 0));
                    pannelloDivisioni.add(numeroRighe[i],       new GridBagConstraints(3, i, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
                    pannelloDivisioni.add(eNumeroColonne[i],    new GridBagConstraints(4, i, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,    CostantiGUI.insetsEtichetta, 0, 0));
                    pannelloDivisioni.add(numeroColonne[i],     new GridBagConstraints(5, i, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
                }
            }
            
            pannelloPulsanti.add(annulla,   new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,GridBagConstraints.EAST,    GridBagConstraints.NONE,    CostantiGUI.insetsAzioneInBarraUltimaDiGruppo, 0, 0));
            pannelloPulsanti.add(ok,        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,GridBagConstraints.CENTER,  GridBagConstraints.NONE,    CostantiGUI.insetsAzioneInBarra, 0, 0));
            
            // ---------- impostazioni ---------
            nomeDivisione[0].setMinimumSize(new Dimension(100,nomeDivisione[0].getPreferredSize().height));
            nomeDivisione[0].setPreferredSize(new Dimension(100,nomeDivisione[0].getPreferredSize().height));
            numeroRighe[0].setMinimumSize(new Dimension(50,nomeDivisione[0].getPreferredSize().height));
            numeroRighe[0].setPreferredSize(new Dimension(50,nomeDivisione[0].getPreferredSize().height));
            numeroColonne[0].setMinimumSize(new Dimension(50,nomeDivisione[0].getPreferredSize().height));
            numeroColonne[0].setPreferredSize(new Dimension(50,nomeDivisione[0].getPreferredSize().height));
            
            CostruttoreOggetti.Suddivisione suddivisione = CostruttoreOggetti.suddivisioniDaPreferenze();
            for(int k=0 ; k<suddivisione.nomi.length-1 ; k++){
            	nomeDivisione[k].setText(suddivisione.nomi[k]);
            	numeroRighe[k].setText(""+suddivisione.divisioniX[k]);
            	numeroColonne[k].setText(""+suddivisione.divisioniX[k]);
            }
            nomeDivisione[suddivisione.nomi.length-1].setText(suddivisione.nomi[suddivisione.nomi.length-1]); 
            // ---------- ascoltatori ----------
            ok.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(ActionEvent e) { ok_actionPerformed(); }
            });
            annulla.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(ActionEvent e) { annulla_actionPerformed(); }
            });
            larghezza.addKeyListener( new KeyAdapter(){
            	public void keyReleased(KeyEvent arg0) {
					ricalcolaDimensioni();
					calcolaSuperficie();
				}
            	public void keyTyped(KeyEvent arg0) {
					ricalcolaDimensioni();
					calcolaSuperficie();
				}
            });
            lunghezza.addKeyListener( new KeyAdapter(){
            	public void keyReleased(KeyEvent arg0) {
					ricalcolaDimensioni();
					calcolaSuperficie();
				}
            	public void keyTyped(KeyEvent arg0) {
					ricalcolaDimensioni();
					calcolaSuperficie();
				}
            });
            
            // ---------- impostazioni ----------
            pannelloDivisioni.setBorder(UtilitaGui.creaBordoConTesto("suddivisioni",0,2));
            pannelloDimensioni.setBorder(UtilitaGui.creaBordoConTesto("dimensioni plot",0,2));
            pannelloDimensioniSettore.setBorder(UtilitaGui.creaBordoConTesto("dimensioni suddivisioni minori",0,2));
            // alcuni campi non possono essere editati, questo dialogo suppono che il plot sia rettangolare
            superficie.setEditable(false);
            larghezzaSettore.setEditable(false);
            lunghezzaSettore.setEditable(false);
            superficieSettore.setEditable(false);
            this.setModal(true);
            this.pack();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /************************************************************************
     * Calcola le dimensioni delle unità inferiori
     ***********************************************************************/    
    void ricalcolaDimensioni(){
    	double divisioniX=1.0,divisioniY=1.0;
    	double dimX,dimY;
    	String tmp;
    	int val;
    	
    	try{
	        for(int i=0;i<NUMERO_DIVISIONI;i++){
	        	tmp = numeroRighe[i].getText();
	        	val = tmp.length()>0 ? Integer.parseInt(tmp) : 1;
	        	divisioniY *= val;
	        	tmp = numeroColonne[i].getText();
	        	val = tmp.length()>0 ? Integer.parseInt(tmp) : 1;
	        	divisioniX *= val;
	        }
	        dimX = Double.parseDouble(larghezza.getText()) / divisioniX;
	        dimY = Double.parseDouble(lunghezza.getText()) / divisioniY;
	        larghezzaSettore.setText( "" + dimX );
	        lunghezzaSettore.setText( "" + dimY );
	        superficieSettore.setText("" + (dimX*dimY) );
    	}catch(Exception ex){
    		; // è solo un metodo di utilità se le cose non vanno non si danno aiuti 
    	}
    }
    
    /************************************************************************
     * Calcola la superficie del plot
     ***********************************************************************/    
    void calcolaSuperficie(){
    	double dimX,dimY;
    	
    	try{
	    	dimX = Double.parseDouble(larghezza.getText());
	    	dimY = Double.parseDouble(lunghezza.getText());
	    	superficie.setText("" + (dimX*dimY) );
    	}catch(Exception ex){
    		; // è solo un metodo di utilità se le cose non vanno non si danno aiuti 
    	}
    }

    /************************************************************************
     * Se si preme annulla semplicemente la finestra viene chiusa
     ***********************************************************************/
    void annulla_actionPerformed() {
        accettato=false;
        this.setVisible(false);
    }

    /************************************************************************
     * Alla pressione di OK si controlla le proprietà cambiate 
     * e si reimpostano solo quelle
     ***********************************************************************/
    void ok_actionPerformed() {
        accettato=true;
        this.setVisible(false);
    }
    
    /************************************************************************
     * @return un nuovo Plot costruito a partire dalle specifiche presenti 
     * nell'interfaccia
     ***********************************************************************/
    public Sample getSample(Sample pattern){
    	int numeroDivisioni;
    	String nome[];
    	int righe[];
    	int colonne[];
    	double lar = 0.00;
    	double lun = 0.00;
    	
    	if(nomeDivisione[4].getText().length()>0)
    		numeroDivisioni = 5;
    	else if(nomeDivisione[3].getText().length()>0)
    		numeroDivisioni = 4;
    	else if(nomeDivisione[2].getText().length()>0)
    		numeroDivisioni = 3;
    	else if(nomeDivisione[1].getText().length()>0)
    		numeroDivisioni = 2;
    	else
    		numeroDivisioni = 1;
    	// creo gli array per le informazioni
    	nome = new String[numeroDivisioni];
        righe = new int[numeroDivisioni];
        colonne = new int[numeroDivisioni];        
        for(int i=0 ; i<numeroDivisioni ; i++){
            nome[i] = nomeDivisione[i].getText();
            try{
            	righe[i] = Integer.parseInt(numeroRighe[i].getText());
            }catch(Exception ex){
            	righe[i] = 0;
            }
            try{
            	colonne[i] = Integer.parseInt(numeroColonne[i].getText());
            }catch(Exception ex){
            	colonne[i] = 0;
            }
        }
        try{
        	lar = Double.parseDouble( larghezza.getText() );
        	lun = Double.parseDouble( lunghezza.getText() );
        }catch(Exception ex){
        	; // do nothing il valore zero in questo caso è appropriato
        }
        return CostruttoreOggetti.createSample(pattern, lar, lun, nome, righe, colonne);
    }
    
    /************************************************************************
     * @return true se l'operazione deve essere eseguita
     ***********************************************************************/
    public boolean isOk(){
        return accettato;
    }

}