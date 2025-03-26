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

import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.OggettoSBD;
import it.aspix.sbd.obj.SimpleBotanicalData;
import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.archiver.ElencoInterattivo;
import it.aspix.archiver.archiver.PannelloDescrivibile;
import it.aspix.archiver.componenti.tabelle.BlobTabella;
import it.aspix.archiver.componenti.tabelle.ModelloTabella;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Blob;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

public class ElencoBlob extends PannelloDescrivibile implements ElencoInterattivo{
    
    private static final long serialVersionUID = 1L;
    ModelloTabella<BlobTabella> modelloTabella = new ModelloTabella<BlobTabella>(new BlobTabella(null,-1));
    JTable tabella = new JTable(modelloTabella);
    
    public ElencoBlob(){
        super();
        setTipoContenuto(it.aspix.sbd.obj.Specimen.class);
        JScrollPane scrollTabella = new JScrollPane(tabella);
        this.setLayout(new BorderLayout());
        this.add(scrollTabella, BorderLayout.CENTER);
        tabella.setAutoscrolls(true);
        tabella.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabella.addMouseListener(new MouseAdapter(){
             public void mouseClicked(MouseEvent e){
                 if(e.getClickCount()==2 && !Proprieta.isTrue("blob.tabellaRicercaEditabile")){
                     tabella_doppioClick();          
                 }
             }
        });
        tabella.getTableHeader().addMouseListener(new MouseAdapter(){
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
        this.setTipoContenuto(Blob.class);
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
    
    /****************************************************************************
     * inserisce gli elementi nella lista
     ***************************************************************************/
    public void setOggettoSBD(OggettoSBD[] elementi, OggettoSBD pattern) throws Exception {
        if(elementi==null || !(elementi instanceof Blob[])){
            throw new IllegalArgumentException("Questo elemento può visualizzare soltanto array di Specimen");
        }
        modelloTabella.rimuoviTutto();
        Blob b;
        if(elementi.length>0){
            Stato.debugLog.fine("cartellini recuperati:"+elementi.length);
            for(int i=0;i<elementi.length;i++){
                b = (Blob) elementi[i];
                Stato.debugLog.fine("aggiunto blob "+i+", ID="+b.getId()+" alla tabella:"+b.getName());
                modelloTabella.aggiungi(new BlobTabella(b,i+1));
            }  
        }
        tabella.revalidate();
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
        BlobTabella blobTabella = modelloTabella.getOggettoAt(tabella.getSelectedRow());
        Blob blob = blobTabella.toBlob();
        Stato.debugLog.fine("Id del blob selezionato:"+blob.getId());
        SimpleBotanicalData r;
        OggettoSBD risposta = null;
        try {
            r = Stato.comunicatore.cerca(blob, null);
            if(r.getBlobSize()==1){
                // la ricerca diretta deve recuperare un solo elemento
                risposta = r.getBlob(0); 
                Dispatcher.consegnaMessaggi(this, r);
            }else{
                if(r.getMessageSize()>0){
                    Dispatcher.consegnaMessaggi(this, r);
                }else{
                    Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Il blob non può essere recuperato", "it", MessageType.ERROR));
                }
            }
        } catch (Exception ex) {
            Dispatcher.consegna(this,ex);
            ex.printStackTrace();
        }
        return risposta;
    }

    public boolean isVisualizzabile(Object oggetto) {
        return oggetto instanceof Blob[];
    }

    @Override
    public String toString() {
        return "lblob";
    }

}
