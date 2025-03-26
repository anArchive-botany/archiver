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
import it.aspix.archiver.componenti.InterfacciaTestoUnicode;
import it.aspix.archiver.componenti.VisualizzatoreInformazioniSpecie;
import it.aspix.archiver.dialoghi.ConfermaNome;
import it.aspix.archiver.dialoghi.SelettoreCarattereUnicode;
import it.aspix.archiver.eventi.ProprietaCambiataEvent;
import it.aspix.archiver.eventi.ProprietaCambiataListener;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.NameList;
import it.aspix.sbd.obj.SimpleBotanicalData;
import it.aspix.sbd.obj.SpecieRef;
import it.aspix.sbd.obj.SpecieSpecification;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;


/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SpecieRefEditor extends JComboBox implements InterfacciaTestoUnicode, TableCellEditor, ProprietaCambiataListener{

    private static final long serialVersionUID = 1L;

    private Border bordoOriginale;
    private Border alternativeMigliori = BorderFactory.createLineBorder(CostantiGUI.coloreBordi,2);

    char carattereVeloce;

    // ---------------- proprieta 'informazini' --------------------------------
    // la casella che visualizza le informazioni sulla specie
    private VisualizzatoreInformazioniSpecie visualizzatoreInformazioni=null;

    /***************************************************************************
     * @param info il componente in cui andranno inserire le iformazioni sulla specie
     **************************************************************************/
    public void setVisualizzatoreInformazioni(VisualizzatoreInformazioniSpecie info){
        visualizzatoreInformazioni=info;
    }
    /**************************************************************************
     * @return il componente il cui inserisce le informazioni sulla specie
     *************************************************************************/
    public VisualizzatoreInformazioniSpecie getVisualizzatoreInformazioni(){
        return visualizzatoreInformazioni;
    }

    // ---------------- proprieta 'riferimentoSpecie' --------------------------
    // il riferimento completo alla specie

    String aliasOf;
    String id;
    /**************************************************************************
     * @return il riferimento completo alla spcie viaualizzata
     *************************************************************************/
    public SpecieRef getSpecieRef(){
        SpecieRef rs=new SpecieRef();
        rs.setName(campoTesto.getText());
        rs.setAliasOf(aliasOf);
        rs.setId(id);
        return rs;
    }
    /**************************************************************************
     * imposta il riferimento alla specie
     *************************************************************************/
    public void setSpecieRef(SpecieRef rs){
        if(rs == null){
        	throw new IllegalArgumentException("SpecieRef non può essere null");
        }
        campoTesto.setText(rs.getName());
        aliasOf = rs.getAliasOf();
        id = rs.getId();
        this.setBorder(bordoOriginale);
    }

    /**************************************************************************
     * il costruttore di default
     *************************************************************************/
    public SpecieRefEditor() {
        carattereVeloce = Proprieta.recupera("generale.carattereSpecieVeloci").charAt(0);
        Proprieta.addProprietaCambiataListener(this);
        this.setEditable(true);
        this.getEditor().getEditorComponent().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                tasto_rilasciato(e);
            }
            public void keyTyped(KeyEvent e) {
                tasto_premuto(e);
            }
        });
        this.getEditor().getEditorComponent().addFocusListener(new java.awt.event.FocusAdapter(){
            public void focusGained(FocusEvent e) {
                ctu_focusGained();
            }
            public void focusLost(FocusEvent e){
                fuoco_perso(e);
            }
        });
        this.getEditor().getEditorComponent().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(KeyEvent.getKeyText(e.getKeyCode()).equals("U") && e.getModifiers()==InputEvent.CTRL_MASK){
                    insersciCarattere();
                    e.consume();
                }
            }
        });
        this.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    itemSelezionato(e);
                }
            }
        });
        campoTesto =(JTextField)(this.getEditor().getEditorComponent());
        contenutoCombo=new DefaultComboBoxModel();
        this.setModel(contenutoCombo);
        tagliaMinima=super.getMinimumSize();
        ascoltatori = new Vector<CellEditorListener>();
        bordoOriginale=this.getBorder();
        if(System.getProperty("os.name").equals("Mac OS X")){
            this.setOpaque(false);
        }
    }

    // ------------------------------------------------------------------------
    // ------------------------         interne          ----------------------
    // ------------------------------------------------------------------------

    DefaultComboBoxModel contenutoCombo;
    JTextField campoTesto;
    Dimension tagliaMinima;
    private boolean testoModificato=false;  //tasto_tilasciato la imposta a true
    //fuoco_perso la imposta a false

    public void ctu_focusGained(){
        Stato.debugLog.warning("acquisito focus");
        Stato.ultimoUtilizzato=this;
    }

    /***************************************************************************
     * Usato per l'inserimento veloce
     **************************************************************************/
    private void tasto_premuto(KeyEvent e){
        char c = e.getKeyChar();
        if( c==carattereVeloce && Proprieta.isTrue("generale.specieVeloci") ){
            String presente = campoTesto.getText();
            int posizioneAttuale = ( campoTesto.getSelectionStart() != -1 ? campoTesto.getSelectionStart() : campoTesto.getCaretPosition() );
            int pos1 = presente.indexOf(" ");
            int pos2 = presente.indexOf("subsp.");
            if(posizioneAttuale<pos1){
                campoTesto.setCaretPosition(pos1+1);
            }else{
                if(pos2==-1){
                    SimpleBotanicalData r=null;
                    try {
                        r = Stato.comunicatore.suggerimentiSpecie(presente+" subsp. ");
                        ArrayList<String> v = filtraNomi(r, presente+" subsp. ");
                        if(v.size() == 0){
                            campoTesto.setCaretPosition(presente.length());
                        }else{
                            campoTesto.setText(v.get(0));
                            campoTesto.setCaretPosition(presente.length()+7+1);
                        }
                    } catch (Exception e1) {
                        Dispatcher.consegna(this, e1);
                    }
                }else{
                    campoTesto.setCaretPosition(pos2+6+1);
                }
            }
            e.consume();
        }
    }

    /***************************************************************************
     * Completa il nome della specie
     **************************************************************************/
    private void tasto_rilasciato(KeyEvent e) {
        Stato.debugLog.fine("jComboBox_keyReleased");
        //previene il possibile arrivo di messaggi di modifica
        campoTesto.setEditable(false);
        //recupera il codice del carattere
        char c = e.getKeyChar();
        testoModificato=true;
        if(Character.isLetterOrDigit(c) || Character.isWhitespace(c)){
            Stato.debugLog.fine("Tasto da prendere in considerazione");
            //e' un qualche tipo di carattere che interessa
            //prende la parte di testo che precede il cursore (maybe undesidered)
            String presente = (campoTesto.getText()).substring(0,campoTesto.getCaretPosition());
            if( presente.length()==1 && Proprieta.isTrue("generale.specieVeloci") ){
                char primo = campoTesto.getText().charAt(0);
                if(Character.isLowerCase(primo)){
                    campoTesto.setText(""+Character.toUpperCase(primo));
                    campoTesto.setCaretPosition(1);
                    campoTesto.grabFocus();
                }
            }
            if(presente.length()>=3){
                Stato.debugLog.fine("Cerco i suggerimenti");
                // vanno impostate le alternative e la voce del campo di testo in questo
                // ordine altrimenti l'impostazione della lista sovrascrive la
                // casella di edit
                // imposta le alternative della combo box
                contenutoCombo.removeAllElements();
                try{
                    SimpleBotanicalData r =  Stato.comunicatore.suggerimentiSpecie(presente);
                    ArrayList<String> risposta = filtraNomi(r,presente);
                    Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Pronto.","it",MessageType.INFO));   
                    Dispatcher.consegnaMessaggi(this, r);
                    int numeroAlternative=0;
                    for(int i=0;i<risposta.size();i++){
                        contenutoCombo.addElement(risposta.get(i));
                    }
                    // imposta la nuova stringa
                    campoTesto.setText(presente);
                    if(risposta.size()>0){
                        String primo = risposta.get(0);
                        campoTesto.setText(primo);
                        campoTesto.setCaretPosition(presente.length());
                        campoTesto.setSelectionStart(presente.length());
                        campoTesto.setSelectionEnd(primo.length());
                        for(int i=0;i<risposta.size();i++){
                            Stato.debugLog.fine("compare '"+primo+"'<->'"+risposta.get(i)+"'");
                            if(risposta.get(i).startsWith(primo))
                                numeroAlternative++;
                        }
                    }
                    if(numeroAlternative<=1){
                        this.setBorder(bordoOriginale);
                        Stato.debugLog.fine("bordo -originale-"+bordoOriginale);
                    }else{
                        this.setBorder(alternativeMigliori);
                        Stato.debugLog.fine("bordo -alternative migliori-");
                    }
                    Stato.debugLog.fine("Suggerimenti possibili="+numeroAlternative);
                }catch(Exception ex){
                    Stato.debugLog.throwing("X", "X", ex);
                    Dispatcher.consegna(this, ex);
                }           
            }else{
                Stato.debugLog.fine("Non cerco i suggerimenti (pochi caratteri)");
            }
        }
        aliasOf=null;
        campoTesto.setEditable(true);
    }

    /***************************************************************************
     * controlla solamente se era possibile fare una scelta più precisa
     **************************************************************************/
    private void itemSelezionato(ItemEvent e){
        String selezionato=(String) e.getItem();
        int ls=selezionato.length();
        String daConfrontare;
        Stato.debugLog.fine("selezionato:"+selezionato);
        campoTesto.setText(selezionato);
        boolean esisteMigliore=false;

        for(int i=0;i<contenutoCombo.getSize();i++){
            daConfrontare=(String)(contenutoCombo.getElementAt(i));
            if(daConfrontare.startsWith(selezionato) && daConfrontare.length()>ls)
                esisteMigliore=true;
        }
        if(esisteMigliore){
            this.setBorder(alternativeMigliori);
            Stato.debugLog.fine("bordo -alternative migliori-");
        }else{
            this.setBorder(bordoOriginale);
            Stato.debugLog.fine("bordo -originale-"+bordoOriginale);
        }
    }

    boolean aperto=false; // per evitare di aprire due volte la spessa finestra
    /************************************************************************
     * @param e l'evento relativo
     ***********************************************************************/
    private void fuoco_perso(FocusEvent e){
        String nome=campoTesto.getText();
        boolean sinonimo=true;
        SpecieSpecification informazioni = null;

        // controllo per la presenza di spazi in fondo alla stringa
        if(!nome.equals(nome.trim())){
            // in caso li elimino
            nome=nome.trim();
            campoTesto.setText(nome);
        }

        Stato.debugLog.fine("Fuoco perso : testoModificato="+testoModificato+" aperto="+aperto+" nome.length()="+nome.length());

        if(testoModificato  && !aperto && nome.length()>0){
            aperto=true;
            SpecieSpecification[] ispecie;
            Stato.debugLog.fine("chiedo al comunicatore di recuperare le specie");
            try {
                // prima cerco una sola specie per vedere se è un nome valido o un sinonimo
                ispecie = Stato.comunicatore.cerca(new SpecieRef(nome,null),null).getSpecieSpecification();
                Stato.debugLog.fine("specie recuperate:"+ispecie.length);
                
                // prima di tutto eliminiamo i casi estremi
                if(ispecie.length == 0){
                    // non identificabile
                    throw new ValoreException("La specie "+nome+" non è identificabile");
                }
                
                // adesso vediamo se il nome richiesto è un nome non sinonimo
                // cercando nell'elenco di quelli recuperati
                Stato.debugLog.fine("numero specie recuperate: "+ispecie.length);
                for( int i=0;i<ispecie.length && sinonimo==true;i++){
                    if(ispecie[i].getNome().equals(nome)){
                        if(ispecie[i].getAliasOf()==null){
                            sinonimo=false;
                            informazioni = ispecie[i];
                        }
                    }
                }
                Stato.debugLog.fine("Il nome cercato è un sinonimo: "+sinonimo);
                
                // se il nome è un sinonimo mostro le alternative
                if(sinonimo){
                    // va richiesta la conferma del nome
                    ConfermaNome confermaNome=new ConfermaNome("Immesso sinonimo", "Seleziona un nome differente", "o conferma il nome selezionato");
                    for( int i=0;i<ispecie.length && sinonimo==true;i++){
                        Icon icona;
                        String descrizione;
                        if(ispecie[i].getAliasOf()!=null && ispecie[i].getAliasOf().length()>=0){
                            descrizione = ispecie[i].getNome() + 
                            	(ispecie[i].getBasionym().equals("true") ? " [bas.]" : "")+
                            	" {" + ispecie[i].getAliasOf() + "}";
                            if(ispecie[i].getProParte()!=null && ispecie[i].getProParte().equals("true"))
                                icona = Icone.Proparte;
                            else
                                icona = Icone.Sinonimo;
                        }else{
                            descrizione = ispecie[i].getNome();
                            icona = Icone.Specie;
                        }
                        confermaNome.addAlternativa(descrizione, ispecie[i], icona, icona==Icone.Specie ? 0 : -1);
                        if(ispecie[i].getNome().equals(nome)){
                        	Stato.debugLog.fine("Seleziono l'elemento "+i);
                        	confermaNome.setSelezionata(ispecie[i]);
                        }
                    }
                    Stato.debugLog.fine("Mostro il dialogo per la scelta di un nome");
                    confermaNome.setLocationRelativeTo(this);
                    confermaNome.setVisible(true);
                    Stato.debugLog.fine("nome scelto="+ ((SpecieSpecification) confermaNome.getSelezionata()).getNome());
                    //caricamento del "nome selezionato"
                    informazioni = (SpecieSpecification) confermaNome.getSelezionata();
                    campoTesto.setText(informazioni.getNome());
                    aliasOf = informazioni.getAliasOf();
                }
                visualizzaInformazioni(informazioni);
                
            } catch (Exception ex) {
                Dispatcher.consegna(this, ex);
            }
            aperto=false;
        }
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

    /************************************************************************
     * Aggiorna le informazioni nel pannello apposito se questo esiste
     * @param sp le specifiche della specie in edit
     ***********************************************************************/
    private void visualizzaInformazioni(SpecieSpecification sp){
        if(visualizzatoreInformazioni!=null){
        	visualizzatoreInformazioni.setSpecieSpecification(sp);
        }
    }
    
    private ArrayList<String> filtraNomi(SimpleBotanicalData sbd, String inizio){
        ArrayList<String> v = new ArrayList<String>();
        NameList nl = null;
        try{
        	nl = sbd.getNameList(0);
        }catch(Exception ex){
        	; // non ha trovato nulla, c'è poco da fare
        }
        String s;
        if(nl!=null){
            for(int i=0;i<nl.size();i++){
                s = nl.getName(i);
                if( s.startsWith(inizio)){
                    v.add(s);
                }
            }
        }
        return v;
    }

    // questi due metodi servono a evitare il ridimenzionamento automatico dei componenti swing
    public Dimension getMinimumSize(){
        Stato.debugLog.fine("TAGLIA ritornata:"+tagliaMinima);
        return tagliaMinima;
    }
    public Dimension getPreferredSize(){
        Stato.debugLog.fine("TAGLIA ritornata:"+tagliaMinima);
        return tagliaMinima;
    }


    // =========================================================================
    // interfaccia javax.swing.table.TableCellEditor
    public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
        SpecieRef rs = (SpecieRef)table.getModel().getValueAt(row,column);
        this.setSpecieRef(rs);
        Stato.debugLog.finer("specie da editare:"+rs);
        return this;
    }

    // =========================================================================
    // interfaccia javax.swing.table.TableCellEditor

    private Vector<CellEditorListener> ascoltatori;

    // Adds a listener to the list that's notified when the editor stops, or cancels editing.
    public void addCellEditorListener(CellEditorListener l){
        ascoltatori.add(l);
        Stato.debugLog.finer("aggiunto ascoltatore, totale presenti:"+ascoltatori.size());
    }

    // Removes a listener from the list that's notified
    public void removeCellEditorListener(CellEditorListener l){
        CellEditorListener c;
        for(int j=0;j<ascoltatori.size();j++){
            c = ascoltatori.elementAt(j);
            if(c==l)
                ascoltatori.remove(j);
        }
        Stato.debugLog.finer("rimosso ascoltatore, ascoltatori rimanenti: "+ascoltatori.size());
    }

    // Tells the editor to cancel editing and not accept any partially edited value.
    public void cancelCellEditing(){
        Stato.debugLog.finer("");
    }

    // Returns the value contained in the editor.
    public Object getCellEditorValue(){
        Stato.debugLog.finer("");
        return getSpecieRef();
    }

    // Asks the editor if it can start editing using anEvent.
    public boolean isCellEditable(EventObject anEvent){
        if(anEvent!=null)
            Stato.debugLog.finer("evento non nullo");
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

    public void proprietaCambiata(ProprietaCambiataEvent l) {
        if(l.getNomeProprieta().equals("generale.carattereSpecieVeloci"))
            carattereVeloce = l.getValoreProprieta().charAt(0);
    }

}