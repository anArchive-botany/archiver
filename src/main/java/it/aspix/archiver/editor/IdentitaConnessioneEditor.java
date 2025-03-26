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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.componenti.CampoTestoUnicode;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;


/*****************************************************************************
 * Imposta le condizioni di lavoro in due passi:
 * al primo richiede all'utente nome e password,
 * al secondo l'erbario in cui inserire i dati.
 * @author Edoardo Panfili, studio Aspix
 ****************************************************************************/
public class IdentitaConnessioneEditor extends JPanel{
    
    private static final long serialVersionUID = 1L;
    
    Window padre;
    /************************************************************************
     * @return la finestra padre
     ***********************************************************************/
    public java.awt.Window getPadre() {
        return padre;
    }
    /************************************************************************
     * @param win la finestra padre
     ***********************************************************************/
    public void setPadre(java.awt.Window win) {
        padre=win;
    }

    /************************************************************************
     * Aggiorna i dati di connessione
     ***********************************************************************/
    public void aggiornaConnessione(){
        Proprieta.aggiorna("connessione.nome", new String(nome.getText()));
        Proprieta.aggiorna("connessione.password",new String(password.getPassword()));
        Proprieta.aggiorna("connessione.URL",new String(URL.getText()));
    }


    boolean premutoEsci=false;
    /***************************************************************************
     * @return true se l'utente  ha premuto annulla
     **************************************************************************/
    public boolean isEsci(){
        return premutoEsci;
    }

    BorderLayout layoutPrincipale = new BorderLayout();

    GridBagLayout lPannelloDati = new GridBagLayout(); 
    JPanel pannelloDati = new JPanel(lPannelloDati);
    JTextArea didascalia = new JTextArea();
    JLabel eNome = new JLabel();
    CampoTestoUnicode nome = new CampoTestoUnicode();
    JLabel ePassword = new JLabel();
    JPasswordField password = new JPasswordField();
    
    JLabel eURL = new JLabel();
    CampoTestoUnicode URL = new CampoTestoUnicode();
    
    JLabel eDatabase = new JLabel();
    CampoTestoUnicode database = new CampoTestoUnicode();
    
    GridBagLayout lPannelloPulsanti = new GridBagLayout();
    JPanel pannelloPulsanti = new JPanel(lPannelloPulsanti);
    JButton esci = new JButton();
    JButton entra = new JButton();
        
    boolean estesi=false;

    /***************************************************************************
     * @param chiediContenitore uno tra ERBARIO | NESSUNO
     **************************************************************************/
    public IdentitaConnessioneEditor() {
        // ---------- le stringhe ----------
        eDatabase.setText("database:");
        ePassword.setText("password:");
        eURL.setText("URL server:");
        eNome.setText("nome:");
        esci.setText("esci");
        entra.setText("entra");
        nome.setText(Proprieta.recupera("connessione.nome"));
        URL.setText(Proprieta.recupera("connessione.URL"));
        
        // ---------- inserimento nei pannelli ----------
        this.setLayout(layoutPrincipale);
        this.add(pannelloDati,BorderLayout.NORTH);
        pannelloDati.add(eNome,         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDati.add(nome,          new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 100, 0));
        pannelloDati.add(ePassword,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDati.add(password,      new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDati.add(eURL,          new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDati.add(URL,           new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        
        this.add(pannelloPulsanti,BorderLayout.SOUTH);
        pannelloPulsanti.add(esci,      new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE, CostantiGUI.insetsAzioneInBarra, 0, 0));
        pannelloPulsanti.add(entra,     new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, CostantiGUI.insetsAzioneInBarra, 0, 0));

        // ---------- ascoltatori ----------
        entra.addActionListener( e -> entra_actionPerformed() );
        esci.addActionListener( e ->  esci_actionPerformed() );
        nome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) { nome_keyReleased(e); }
        });
        password.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) { password_keyReleased(e); }
        });
        database.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) { database_keyReleased(e); }
        });
        this.addFocusListener(new FocusListener(){
            public void focusGained(FocusEvent e){
                password.grabFocus();
            }
            public void focusLost(FocusEvent e){}
        });
        // ---------- impostazioni ----------
        database.setEditable(true);
        didascalia.setWrapStyleWord(true);
        didascalia.setLineWrap(true);
        didascalia.setForeground(UIManager.getColor("Label.foreground"));
        didascalia.setBackground(UIManager.getColor("Label.background"));
        didascalia.setEditable(false);
        database.setPreferredSize(new Dimension(100,nome.getPreferredSize().height));
        if(Stato.isMacOSX){
            entra.setOpaque(false);
            esci.setOpaque(false);
        }
        sistemaPrimaColonna();
        pannelloDati.setOpaque(false);
        pannelloPulsanti.setOpaque(false);
    }

    /***************************************************************************
     * Imposta l'ampiezza della prima colonna, le etichette sono su
     * piu' pannelli e da sole avrebbero dimensioni diverse
     **************************************************************************/
    private void sistemaPrimaColonna(){
        int massimo=0;
        
        //il vettore seguente contiene tutte le etichette che vanno ridimensionate
        JLabel[] etichetta= { eNome, ePassword, eURL, eDatabase };
        for(int i=0;i<etichetta.length;i++){
            if(etichetta[i].getPreferredSize().width>massimo)
                massimo=etichetta[i].getPreferredSize().width;
        }

        Dimension d=new Dimension(massimo, etichetta[0].getPreferredSize().height);
        for(int i=0;i<etichetta.length;i++){
            etichetta[i].setMinimumSize(d);
            etichetta[i].setPreferredSize(d);
            etichetta[i].setMaximumSize(d);
            etichetta[i].setHorizontalAlignment(SwingConstants.RIGHT);
        }
    }

    void password_keyReleased(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_ENTER){
            entra_actionPerformed();
        }
    }
    
    void database_keyReleased(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_ENTER){
            entra_actionPerformed();
        }
    }

    void porta_keyReleased(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_ENTER){
            entra_actionPerformed();
        }
    }

    void nome_keyReleased(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_ENTER){
            password.grabFocus();
        }
    }

    void esci_actionPerformed() {
        fireEvent(new ActionEvent(this,0,"esci"));
    }

    void entra_actionPerformed() {
        fireEvent(new ActionEvent(this,0,"entra"));
    }  
    
    EventListenerList lista = new EventListenerList();
    public void addActionListener(ActionListener a){
        lista.add(ActionListener.class, a);
    }

    protected void fireEvent(ActionEvent e){
        ActionListener al[] = lista.getListeners(ActionListener.class);
        for(int i=0; i<al.length;i++){
            Stato.debugLog.fine("Chiamo "+al[i]);
            al[i].actionPerformed(e);           
        }
    }
    
    public void setColoreEtichette(Color colore){
        List.of(eNome, eURL, ePassword, eDatabase)
            .forEach( x -> x.setForeground(colore) );
    }
}