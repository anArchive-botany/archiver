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
package it.aspix.archiver.componenti;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.dialoghi.SelettoreCarattereUnicode;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.NameList;
import it.aspix.sbd.obj.SimpleBotanicalData;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;


/*****************************************************************************
 * Questa combo box gestisce i suggerimenti per un elenco di nomi
 * @author Edoardo Panfili
 * @version 1.0
 ****************************************************************************/
public class ComboSuggerimenti extends JComboBox<String> implements InterfacciaTestoUnicode, TableCellEditor{

    private static final long serialVersionUID = 1L;
    
    /** il nome del contenitore a cui si riferisce questo combo, se i dati vengono
     * recuperati dal server questo dato serve per selezionare i suggerimenti */
    private String database;
    /** utilizzato per recuperare i dati dal server */
    private String tipoLista;
    /** il numero minimo di caratteri da immettere */
    private int limite;
    /** se true indica che il campione di testo non è necessariamente all'inizio della stringa */
    private boolean ancheInterno;
    
    
    /** un particolare valore per il parametro "limite" che indica di caricare i valori all'avvio */
    public static final int SOLO_AVVIO=-1;
    
    /**************************************************************************
     * @param database il nome del contenitore a cui si riferisce questo combo, se i dati vengono
     * 				   recuperati dal server questo dato serve per selezionare i suggerimenti
     * @param tipoLista il tipo di SimpleBotanicalData degli elementi della lista
     * @param limite il numero dei caratteri oltre il quale va richiesto
     *        il suggerimento
     * @param editabile true se il contenuto del combo è liberamente editabile dall'utente
     * @param ancheInterno true indica che il campione di testo non è necessariamente all'inizio della stringa
     * @param patern che gli elementi devono rispettare
     *************************************************************************/
    public ComboSuggerimenti(String database, String tipoLista, int limite, boolean editabile, boolean ancheInterno, String pattern) {
    	this(database, tipoLista, limite, editabile, ancheInterno, pattern, false); 
    }
    
    /**************************************************************************
     * @param database il nome del contenitore a cui si riferisce questo combo, se i dati vengono
     * 				   recuperati dal server questo dato serve per selezionare i suggerimenti
     * @param tipoLista il tipo di SimpleBotanicalData degli elementi della lista
     * @param limite il numero dei caratteri oltre il quale va richiesto
     *        il suggerimento
     * @param editabile true se il contenuto del combo è liberamente editabile dall'utente
     * @param ancheInterno true indica che il campione di testo non è necessariamente all'inizio della stringa
     * @param patternIniziale che gli elementi devono rispettare
     * @param ignoraErrori true se eventuali errori in fase di costruzione vanno ignorati
     *************************************************************************/
    public ComboSuggerimenti(String database, String tipoLista, int limite, boolean editabile, boolean ancheInterno, String patternIniziale, boolean ignoraErrori) {
        this.database = database;
        this.tipoLista = tipoLista;
        this.limite = limite;
        this.ancheInterno = ancheInterno;
        if(limite!=SOLO_AVVIO)
            this.getEditor().getEditorComponent().addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    tasto_rilasciato(e);
                }
            });
        this.getEditor().getEditorComponent().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(KeyEvent.getKeyText(e.getKeyCode()).equals("U") && e.getModifiersEx()==InputEvent.CTRL_DOWN_MASK){
                    insersciCarattere();
                    e.consume();
                }
            }
        });
        campoTesto =(JTextField)(this.getEditor().getEditorComponent());
        contenutoCombo = new DefaultComboBoxModel<String>();
        this.setModel(contenutoCombo);
        if(limite==SOLO_AVVIO && tipoLista!=null && database.length()>0){
            Stato.debugLog.fine("Carico il combo all'avvio");
            contenutoCombo.removeAllElements();
            try{
                SimpleBotanicalData rispostaCompleta = Stato.comunicatore.recuperaNomi(tipoLista,database,patternIniziale,ancheInterno);
                if(rispostaCompleta.getMessageSize()>0 && rispostaCompleta.getMessage(0).getType().equals(MessageType.ERROR)){
                    	if(!ignoraErrori){
                    		Dispatcher.consegnaMessaggi(this, rispostaCompleta);
                    	}
                }else{
                    NameList risposta = rispostaCompleta.getNameList(0);
                    Stato.debugLog.fine("Elementi caricati:"+risposta.size());
                    for(int i=0;i<risposta.size();i++){
                        contenutoCombo.addElement(risposta.getName(i));
                    }
                }
            }catch(Exception ex){
                Dispatcher.consegna(this, ex);
            }
        }
        tagliaMinima=super.getMinimumSize();
        if(Stato.isMacOSX){
            // XXX: brutto ma necessario per il sistema 10
            tagliaMinima=new Dimension(super.getMinimumSize().width, new JTextField().getMinimumSize().height);
        }
        this.setEditable(editabile);
    }
    
    /************************************************************************
     * @return il testo contenuto nel combo
     ***********************************************************************/
    public String getText() {
        Stato.debugLog.fine("selected index: "+getSelectedIndex());
        // se è editabile prendo sempre il testo nella casella
        if(getSelectedIndex()!=-1 && !this.isEditable)
            return (String)(contenutoCombo.getElementAt(getSelectedIndex()));
        return campoTesto.getText();
    }
    
    /************************************************************************
     * @param testo il testo contenuto nel combo
     ***********************************************************************/
    public void setText(String testo) {
        if(testo==null || testo.length()==0){
            if(isEditable())
                campoTesto.setText("");
            else
                try{
                    setSelectedIndex(0);
                }catch(Exception ex){
                    ; // do nothing
                }
            return;
        }
        if(isEditable()){
            this.setSelectedIndex(-1);
            campoTesto.setText(testo);
        }else{
            boolean impostato=false;
            for(int i=0;i<contenutoCombo.getSize();i++){
                if( ((String)(contenutoCombo.getElementAt(i))).equals(testo) ){
                    this.setSelectedIndex(i);
                    impostato=true;
                    break;
                }
            }
            if(!impostato){
                contenutoCombo.insertElementAt(testo, 0);
                this.setSelectedIndex(0);
                Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Non trovata corrispondenza per "+tipoLista+" ='"+testo+"'","it",MessageType.WARNING));
            }
        }
        
    }
    
    /****************************************************************************
     * @param possibilita le alternative da inserire nel combo
     ***************************************************************************/
    public void setSuggerimenti(String[] possibilita) {
        contenutoCombo.removeAllElements();
        if(possibilita!=null)
            for(int i=0; i<possibilita.length; i++)
                contenutoCombo.addElement(possibilita[i]);
    }
    
    // ------------------------------------------------------------------------
    // ------------------------         interne          ----------------------
    // ------------------------------------------------------------------------

    DefaultComboBoxModel<String> contenutoCombo;
    JTextField campoTesto;
    Dimension tagliaMinima;

    public void ctu_focusGained(){
        Stato.debugLog.finest("acquisito focus");
        Stato.ultimoUtilizzato=this;
    }

    /***************************************************************************
     * Completa il nome della spcecie
     **************************************************************************/
    private void tasto_rilasciato(KeyEvent e) {
        Stato.debugLog.fine("jComboBox_keyReleased");
        //previene il possibile arrivo di messaggi di modifica
        campoTesto.setEditable(false);
        //recupera il codice del carattere
        char c = e.getKeyChar();
        if(Character.isLetterOrDigit(c) || Character.isWhitespace(c)){ 
            Stato.debugLog.fine("Tasto da prendere in considerazione");
            //e' un qualche tipo di carattere che interessa
            //prende la parte di testo che precede il cursore (maybe undesidered)
            String presente = (campoTesto.getText()).substring(0,campoTesto.getCaretPosition());
            if(presente.length()>=limite){
                Stato.debugLog.fine("Cerco i suggerimenti");
                // vanno impostate le alternative e la voce del campo di testo in questo
                // ordine altrimenti l'impostazione della lista sovrascrive la
                // casella di edit
                // imposta le alternative della combo box
                try{
                    SimpleBotanicalData rispostaIntera = Stato.comunicatore.recuperaNomi(tipoLista, database, presente, ancheInterno);
                    if(rispostaIntera.getNameListSize()!=0){
                        NameList risposta = rispostaIntera.getNameList(0);
                        contenutoCombo.removeAllElements();
                        Stato.debugLog.fine("elementi="+risposta.size());
                        for(int i=0;i<risposta.size();i++){
                            Stato.debugLog.fine(i+"= '"+risposta.getName(i)+"'");
                            contenutoCombo.addElement(risposta.getName(i));
                        }
                        // imposta la nuova stringa
                        campoTesto.setText(presente);
                        if(risposta.size()>0){
                            String primo = risposta.getName(0);
                            campoTesto.setText(primo);
                            Stato.debugLog.fine("primo="+primo);
                            int inizioSelezione;
                            if(ancheInterno){
                            	inizioSelezione = primo.indexOf(presente) + presente.length();
                            }else{
	                            inizioSelezione = presente.length();
                            }
                            campoTesto.setCaretPosition(inizioSelezione);
                            campoTesto.setSelectionStart(inizioSelezione);
                            campoTesto.setSelectionEnd(primo.length());
                        }
                    }
                }catch(Exception ex){
                    Dispatcher.consegna(this, ex);
                }
            }else{
                Stato.debugLog.fine("Non cerco i suggerimenti (pochi caratteri)");
            }
        }
        campoTesto.setEditable(true);
    }

    /***************************************************************************
     * visualizza il dialogo per la scelta di un carattere
     * e lo inserisce nel testo
     **************************************************************************/
    public void insersciCarattere(){
        JTextField sorgente=campoTesto;
        Color sfondo=this.getBackground();
        this.setBackground(CostantiGUI.coloreAttenzione);
        //SelettoreSimboli ss=new SelettoreSimboli();
        SelettoreCarattereUnicode ss = new SelettoreCarattereUnicode();
        ss.setLocationRelativeTo(sorgente);
        ss.setVisible(true);
        if(ss.lettera!=SelettoreCarattereUnicode.NIENTE){
            int posizioneCursore=sorgente.getCaretPosition();
            StringBuffer testo=new StringBuffer(sorgente.getText());
            testo.insert(posizioneCursore,ss.lettera);
            sorgente.setText(testo.toString());
            sorgente.setCaretPosition(posizioneCursore+1);
        }
        this.setBackground(sfondo);
        this.grabFocus();
    }

    
    // questi dei metodi servono a evitare il ridimenzionamento automatico dei componenti swing
    public Dimension getMinimumSize(){
        Stato.debugLog.finest("TAGLIA ritornata:"+tagliaMinima);
        return tagliaMinima;
    }
    public Dimension getPreferredSize(){
        Stato.debugLog.finest("TAGLIA ritornata:"+tagliaMinima);
        return tagliaMinima;
    }

    // =========================================================================
    // interfaccia javax.swing.table.TableCellEditor
    public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
        this.setText((String)table.getModel().getValueAt(row,column));
        return this;
    }

    // =========================================================================
    // interfaccia javax.swing.table.TableCellEditor

    private Vector<CellEditorListener> ascoltatori;

    // Adds a listener to the list that's notified when the editor stops, or cancels editing.
    public void addCellEditorListener(CellEditorListener l){
        ascoltatori.add(l);
        Stato.debugLog.finer("aggiunto ascoltatore");
    }

    // Removes a listener from the list that's notified
    public void removeCellEditorListener(CellEditorListener l){
        CellEditorListener c;
        for(int j=0;j<ascoltatori.size();j++){
            c = ascoltatori.elementAt(j);
            if(c==l)
                ascoltatori.remove(j);
        }
        Stato.debugLog.finer("rimosso ascoltatore");
    }

    // Tells the editor to cancel editing and not accept any partially edited value.
    public void cancelCellEditing(){
        Stato.debugLog.finer("");
    }

    // Returns the value contained in the editor.
    public Object getCellEditorValue(){
        Stato.debugLog.finer("");
        return getText();
    }

    // Asks the editor if it can start editing using anEvent.
    public boolean isCellEditable(EventObject anEvent){
        if(anEvent!=null)
            Stato.debugLog.finer("evento nullo");
        else
            Stato.debugLog.finer("");
        this.campoTesto.requestFocus();
        return true;
    }

    // Returns true if the editing cell should be selected, false otherwise.
    public boolean shouldSelectCell(EventObject anEvent){
        Stato.debugLog.finer("");
        return true;
    }

    // Tells the editor to stop editing and accept any partially edited value as the value of the editor.
    public boolean stopCellEditing(){
        Stato.debugLog.finer("");
        CellEditorListener c;
        for(int j=0;j<ascoltatori.size();j++){
            c = ascoltatori.elementAt(j);
            c.editingStopped(new javax.swing.event.ChangeEvent(this));
        }
        return true;
    }

}