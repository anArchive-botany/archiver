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

import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.archiver.ElencoInterattivo;
import it.aspix.archiver.archiver.PannelloDescrivibile;
import it.aspix.archiver.componenti.tabelle.ModelloTabella;
import it.aspix.archiver.componenti.tabelle.SpecimenTabella;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.OggettoSBD;
import it.aspix.sbd.obj.SimpleBotanicalData;
import it.aspix.sbd.obj.Specimen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

public class ElencoSpecimen extends PannelloDescrivibile implements ElencoInterattivo{
    
    private static final long serialVersionUID = 1L;
    ModelloTabella<SpecimenTabella> modelloTabella = new ModelloTabella<SpecimenTabella>(new SpecimenTabella(null, -1));
    JTable tabella = new JTable(modelloTabella);
    JButton aggiornaServer = new JButton("invia modifiche al server");
    private int rigaSelezionata = -1;         // usata per la gestione della tabella
    
    public ElencoSpecimen(){
        super();
        setTipoContenuto(it.aspix.sbd.obj.Specimen.class);
        JScrollPane scrollTabella = new JScrollPane(tabella);
        this.setLayout(new BorderLayout());
        this.add(scrollTabella, BorderLayout.CENTER);
        tabella.setAutoscrolls(true);
        tabella.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabella.addMouseListener(new MouseAdapter(){
             public void mouseClicked(MouseEvent e){
                 if(e.getClickCount()==2 && !Proprieta.isTrue("herbaria.tabellaRicercaEditabile")){
                     tabella_doppioClick();          
                 }
             }
        });
        final JPopupMenu popupAzioni = new JPopupMenu();
        final JMenuItem menuIgnoraModifiche = new JMenuItem("ignora le modifiche di questo elemento");
        popupAzioni.add(menuIgnoraModifiche);
        tabella.getTableHeader().addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                if(e.isPopupTrigger()){
                    popupAzioni.show((Component)e.getSource(),e.getX(),e.getY());
                    rigaSelezionata = e.getPoint().y / tabella.getRowHeight();
                }
            }
            public void mouseReleased(MouseEvent e){
                if(e.isPopupTrigger()){
                    popupAzioni.show((Component)e.getSource(),e.getX(),e.getY());
                    rigaSelezionata = e.getPoint().y / tabella.getRowHeight();
                }
            }
            public void mouseClicked(MouseEvent e) {
                TableColumnModel columnModel = tabella.getColumnModel();
                int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                int column = tabella.convertColumnIndexToModel(viewColumn);
                Stato.debugLog.fine(e.getClickCount()+" clicks su colonna "+column);
                if(e.getClickCount() == 1 && column != -1) {
                    Stato.debugLog.fine("Richiesta ordinamento su colonna "+column);
                    modelloTabella.ordina(column);
                    Stato.debugLog.fine("Ordinamento effettuato su colonna "+column);
                    tabella.revalidate();
                    tabella.updateUI();
                }
            }
        });       
        menuIgnoraModifiche.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { ignoraModifiche_actionPerformed(e); }
        });
        if(Proprieta.isTrue("herbaria.tabellaRicercaEditabile")){
            modelloTabella.setEditable(true);
            this.add(aggiornaServer, BorderLayout.SOUTH);
        }else{
            modelloTabella.setEditable(false);
        }
        this.setTipoContenuto(Specimen.class);
        aggiornaServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { aggiornaServer_actionPerformed(); }
        });
    }
    
    /****************************************************************************
     * gestisce il doppio click, recupera i dati completi di un cartellino
     ***************************************************************************/
    void tabella_doppioClick() {
        OggettoSBD x = internalGetSelezionato();
        if(x!=null){
            try {
                Dispatcher.consegna(this, x, this);
            } catch (Exception ex) {
                Dispatcher.consegna(this,ex);
                ex.printStackTrace();
            }
        }
    }
    
    /************************************************************************
     * Contrassegna una riga come "non modificata"
     * @param e
     ***********************************************************************/
    public void ignoraModifiche_actionPerformed(ActionEvent e) {    
        modelloTabella.getOggettoAt(rigaSelezionata).setModificato(false);
    }
    
    /****************************************************************************
     * inserisce gli elementi nella lista
     ***************************************************************************/
    public void setOggettoSBD(OggettoSBD[] elementi, OggettoSBD pattern) throws Exception {
        if(elementi==null || !(elementi instanceof Specimen[])){
            throw new IllegalArgumentException("Questo elemento può visualizzare soltanto array di Specimen");
        }
        modelloTabella.rimuoviTutto();
        Specimen c;
        if(elementi.length>0){
            Stato.debugLog.fine("cartellini recuperati:"+elementi.length);
            for(int i=0;i<elementi.length;i++){
                c = (Specimen) elementi[i];
                Stato.debugLog.fine("aggiunto cartellino "+i+", ID="+c.getId()+" alla tabella:"+c.getSpecieRef().getName());
                modelloTabella.aggiungi(new SpecimenTabella(c,i+1));
            }  
        }
        tabella.revalidate();
        tabella.updateUI();
    }

    public OggettoSBD getSuccessivo() throws Exception {
        int selezionato = tabella.getSelectedRow() + 1 ;
        
        if(selezionato<tabella.getRowCount()){
            tabella.getSelectionModel().setSelectionInterval(selezionato,selezionato);
        }
        return internalGetSelezionato();
    }

    public OggettoSBD getPrecedente() throws Exception {
        int selezionato = tabella.getSelectedRow() - 1 ;
        
        if(selezionato>=0){
            tabella.getSelectionModel().setSelectionInterval(selezionato,selezionato);
        }
        return internalGetSelezionato();
    }
    
    private OggettoSBD internalGetSelezionato(){
        SpecimenTabella cartellinoTabella = modelloTabella.getOggettoAt(tabella.getSelectedRow());
        Specimen cartellino = cartellinoTabella.toCartellino();
        Stato.debugLog.fine("Id del cartellino selezionato:"+cartellino.getId());
        SimpleBotanicalData r;
        OggettoSBD risposta = null;
        try {
            r = Stato.comunicatore.cerca(cartellino, null);
            if(r.getSpecimenSize()==1){
                // la ricerca diretta deve recuperare un solo elemento
                risposta = r.getSpecimen(0); 
                Dispatcher.consegna(this, r.getMessage(0));
            }else{
                if(r.getMessageSize()>0){
                    Dispatcher.consegnaMessaggi(this, r);
                }else{
                    Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Il cartellino non può essere recuperato", "it", MessageType.ERROR));
                }
            }
        } catch (Exception ex) {
            Dispatcher.consegna(this,ex);
            ex.printStackTrace();
        }
        return risposta;
    }

    public boolean isVisualizzabile(Object oggetto) {
        return oggetto instanceof Specimen[];
    }

    @Override
    public String toString() {
        return "lcart";
    }
    
    /****************************************************************************
     * aggiornare sul server tutti i cartellini modificati nella tabella
     ***************************************************************************/   
    void aggiornaServer_actionPerformed() {
        int i;
        int numeroCambiati=0;
        int risultato;
        DefaultListModel ml = new DefaultListModel();
        JList lista = new JList(ml);
        JPanel infop = new JPanel(new BorderLayout());
        JScrollPane  scroll = new JScrollPane(lista);
        JLabel info = new JLabel();
        
        for(i=0;i<modelloTabella.getRowCount();i++){
            if(modelloTabella.getOggettoAt(i).isModificato()){
                numeroCambiati++;
                ml.addElement(modelloTabella.getOggettoAt(i).toString());
            }
        }
        info.setText("L'operazione potrebbe richiedere molto tempo.");
        infop.add(scroll,BorderLayout.CENTER);
        infop.add(info,BorderLayout.SOUTH);
        risultato=JOptionPane.showConfirmDialog(this,infop, "Modifica "+numeroCambiati+" cartellini in blocco", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE); 
        if(risultato == JOptionPane.OK_OPTION){
            SimpleBotanicalData risp;
            ml.removeAllElements();
            for(i=0;i<modelloTabella.getRowCount();i++){
                if(modelloTabella.getOggettoAt(i).isModificato()){
                    ml.addElement(modelloTabella.getOggettoAt(i).toString());
                    try {
                        // XXX: prende in considerazione solamente il primo messaggio
                        risp = Stato.comunicatore.modifica(modelloTabella.getOggettoAt(i).getSpecimen(),null,false);
                        ml.addElement( 
                                (risp.getMessage(0).getType()==MessageType.ERROR ? "ERR" : "OK") 
                                + risp.getMessage(0).getText(0).getText()
                        );
                    } catch (Exception ex) {
                        ml.addElement("ECC" + modelloTabella.getOggettoAt(i).numeroRiga+" "+ex.getMessage());
                    }
                    modelloTabella.getOggettoAt(i).setModificato(false);
                }
            }
            info.setText("I dati visualizzati nel pannello non sono sincroni con il server, è consigliabile effettuare una nuova ricerca.");
            JOptionPane.showMessageDialog(this,infop, "Risultati modifica cartellini",JOptionPane.INFORMATION_MESSAGE); 
        }
    }

}
