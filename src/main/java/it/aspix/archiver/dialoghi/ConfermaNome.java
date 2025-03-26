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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.nucleo.Stato;

/*****************************************************************************
 * chiede di selezionare il nome da una lista di nomi
 * @author Edoardo Panfili, studio Aspix
 ****************************************************************************/
public class ConfermaNome extends JDialog {

    private static final long serialVersionUID = 1L;
    
    private static class Voce{
    	public String descrizione;
    	public Object oggetto;
    	public Icon icona;
		public Voce(String descrizione, Object oggetto, Icon icona) {
			super();
			this.descrizione = descrizione;
			this.oggetto = oggetto;
			this.icona = icona;
		}
    }
    
    /** la descrizione che deve comparire nel pulsante */
    private ArrayList<Voce> voci;
    /** l'elemento selezionato */
    private Object selezionato;
    /** l'oggetto selezionato */
    private Voce voceSelezionata = null;
    /** da evidenziare quando la finestra rende il fuoco */
    private JButton pulsanteConFocus; 

    /************************************************************************
     * @param descr il testo che comparirà sui pulsanti
     * @param obj l'oggetto legato al testo
     * @param icn l'icona associata all'oggetto
     * @param posizione -1 se deve essere accodato 
     *        altrimenti posizione in base 0
     ***********************************************************************/
    public void addAlternativa(String descr, Object obj, Icon icn, int posizione){
    	this.voci.add(new Voce(descr,obj,icn));
        Stato.debugLog.fine("aggiunto: "+descr);
    }

    /************************************************************************
     * @param o l'oggetto da visualizzare come selezionato
     ***********************************************************************/
    public void setSelezionata(Object o){
    	selezionato = o;
    }
    
    /************************************************************************ 
     * @return l'oggetto selezionato
     ***********************************************************************/
    public Object getSelezionata(){
    	return voceSelezionata.oggetto;
    }
    // fine interfaccia pubblica

    JPanel pannelloPrincipale = new JPanel();
    GridBagLayout lPannelloPrincipale = new GridBagLayout();
    JPanel pannelloNomi = new JPanel();
    GridLayout lPannelloNomi = new GridLayout(0,1);
    JLabel motivo1 = new JLabel();
    JLabel motivo2 = new JLabel();
    JButton pulsante[];

    /************************************************************************
     * @param titolo della finestra di dialogo
     * @param msg1 prima riga del messaggio
     * @param msg2 seconda riga del messaggio
     ***********************************************************************/
    public ConfermaNome(String titolo, String msg1, String msg2) {
    	// visto che ci faccio i confronti ma fa comodo che non sia null
    	selezionato = "nessuno";
        voci = new ArrayList<Voce>();
        setTitle(titolo);
        motivo1.setText(msg1);
        motivo2.setText(msg2);
        
        Border bordoNomi = BorderFactory.createEmptyBorder(8,4,8,4);
        Border bordoPrincipale = BorderFactory.createEmptyBorder(5,5,5,5);
        pannelloPrincipale.setLayout(lPannelloPrincipale);
        pannelloNomi.setLayout(lPannelloNomi);
        pannelloNomi.setBorder(bordoNomi);
        lPannelloNomi.setVgap(2);
        pannelloPrincipale.setBorder(bordoPrincipale);
        this.getContentPane().add(pannelloPrincipale, BorderLayout.CENTER);
        pannelloPrincipale.add(motivo1,      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, CostantiGUI.insetsTestoDescrittivo, 0, 0));
        pannelloPrincipale.add(motivo2,      new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, CostantiGUI.insetsTestoDescrittivo, 0, 0));
        pannelloPrincipale.add(pannelloNomi, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsGruppo, 0, 0));
        this.setModal(true);
        this.addWindowFocusListener(new WindowFocusListener(){
			public void windowGainedFocus(WindowEvent e) {
			    if(pulsanteConFocus!=null){
			        pulsanteConFocus.requestFocusInWindow();
			    }
			}
			public void windowLostFocus(WindowEvent e) {
				;
			}
        });
    }

    /************************************************************************
     * I componenti vengono aggiunti alla finestra e viene impostato
     * l'elemento preselezionato
     ***********************************************************************/
    public void setVisible(boolean stato){
        if(stato){
        	Voce voce;
            pulsante=new JButton[voci.size()];
            for(int i=0;i<voci.size();i++){
            	voce = voci.get(i);
                Stato.debugLog.fine("aggiunto pulsante \""+voce.descrizione+"\"");
                pulsante[i]=new JButton( voce.descrizione );
                pulsante[i].setHorizontalAlignment(SwingConstants.LEFT);
                if( voce.icona!=null )
                    pulsante[i].setIcon( voce.icona );
                pannelloNomi.add(pulsante[i]);
                pulsante[i].addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        pulsante_actionPerformed((JButton) e.getSource());
                    }
                });
                pulsante[i].addKeyListener(new KeyListener() {
                    public void keyTyped(KeyEvent e) {
                        if(e.getKeyChar()=='\n'){
                            pulsante_actionPerformed((JButton) e.getSource());
                        }
                    }
                    public void keyReleased(KeyEvent e) { }
                    public void keyPressed(KeyEvent e) { }
                });
                if(voce.oggetto==selezionato){
                	pulsanteConFocus = pulsante[i];
                	// per evitare problemi se l'utente chiude la finestra senza fare click sui pulsanti
                	voceSelezionata = voci.get(i);
                }
            }
            this.pack();
        }
        super.setVisible(stato);
    }

    /************************************************************************
     * Seleziona l'oggetto in base alla descrizione presente nel pulsante
     * @param e
     ***********************************************************************/
    void pulsante_actionPerformed(JButton sorgente) {
    	for(int i=0;i<voci.size();i++){
    		if(sorgente.getText().equals(voci.get(i).descrizione)){
    			voceSelezionata = voci.get(i);
    		}
    	}
        this.setVisible(false);
    }

}