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
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.archiver.ElencoInterattivo;
import it.aspix.archiver.archiver.PannelloDescrivibile;
import it.aspix.archiver.componenti.alberi.NodoAlberoSpecieSpecification;
import it.aspix.archiver.componenti.alberi.RenderAlberoSpecieSpecification;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.OggettoSBD;
import it.aspix.sbd.obj.SpecieSpecification;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class ElencoSpecieSpecification extends PannelloDescrivibile implements ElencoInterattivo{
    
    private static final long serialVersionUID = 1L;
    DefaultTreeModel modelloAlbero=new DefaultTreeModel(new NodoAlberoSpecieSpecification("vuoto"));
    JTree albero = new JTree();
    NodoAlberoSpecieSpecification radice;
    
    private static final String GENERE_FITTIZIO_PER_SINONIMI = "SINONIMI";
    
    public ElencoSpecieSpecification(){
        super();
        setTipoContenuto(it.aspix.sbd.obj.Specimen.class);
        JScrollPane scrollTabella = new JScrollPane(albero);
        this.setLayout(new BorderLayout());
        this.add(scrollTabella, BorderLayout.CENTER);
        albero.setModel(modelloAlbero);
        albero.setRootVisible(false);
        albero.setBackground(CostantiGUI.coloreSfondoElementiSelezionabili);
        albero.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                specie_selezionata(null);                
            }
        });
        modelloAlbero.setRoot(radice);
        albero.setToggleClickCount(1);
        this.setTipoContenuto(SpecieSpecification.class);
    }
    
    /************************************************************************
     * click sull'albero
     ***********************************************************************/
    public void specie_selezionata(TreeSelectionEvent e){
        if(albero.getSelectionPath()!=null){
            SpecieSpecification spe = (SpecieSpecification)( ((NodoAlberoSpecieSpecification)(albero.getSelectionPath().getLastPathComponent())).getUserObject() );
            if(!spe.getGenusName().equals(GENERE_FITTIZIO_PER_SINONIMI)){
	            Stato.debugLog.fine("specie selezionata:"+spe.toString());
	            try {
	                Dispatcher.consegna(this, spe, this);
	                // FIXME: nella versione 4 al momento della modifica l'albero poteva essere aggiornato
	            } catch (Exception ex) {
	                Dispatcher.consegna(this,ex);
	                ex.printStackTrace();
	            }
            }
        }
    }
    
    
    /****************************************************************************
     * inserisce gli elementi nella lista
     ***************************************************************************/
    public void setOggettoSBD(OggettoSBD[] elementi, OggettoSBD pattern) throws Exception {
        if(elementi==null || !(elementi instanceof SpecieSpecification[])){
            throw new IllegalArgumentException("Questo elemento può visualizzare soltanto array di SpecieSpecification");
        }
        ArrayList<NodoAlberoSpecieSpecification> livelloUno = new ArrayList<NodoAlberoSpecieSpecification>();
        ArrayList<NodoAlberoSpecieSpecification> nomiBuoniMaNonCercatiDirettamente = new ArrayList<NodoAlberoSpecieSpecification>();
        ArrayList<SpecieSpecification> sinonimi = new ArrayList<SpecieSpecification>();
        SpecieSpecification speSpe;
        radice = new NodoAlberoSpecieSpecification("SPECIE");
        String cercata = ""; // XXX:questo dovrebbe essere un parametro
        
        albero.setCellRenderer(new RenderAlberoSpecieSpecification()); // XXX: serve farlo tutte le volte?
        try {
            for(int i=0 ; i<elementi.length ; i++){
                speSpe = (SpecieSpecification) elementi[i];
                speSpe.nullToEmpty();
                Stato.debugLog.fine("inserisco nell'albero:"+speSpe.toString());
                // inserisce la specie nell'albero
                if(speSpe.getAliasOf().length()==0){
                    // E' un nome originale, creo il nodo
                    NodoAlberoSpecieSpecification mtn = new NodoAlberoSpecieSpecification();
                    mtn.setUserObject(speSpe);
                    mtn.inEvidenza = speSpe.getNome().startsWith(cercata);
                    if(mtn.inEvidenza){
                        radice.add(mtn);
                        livelloUno.add(mtn);
                    }else{
                        nomiBuoniMaNonCercatiDirettamente.add(mtn);
                    }
                    /*NodoAlberoSpecieSpecification mtn = new NodoAlberoSpecieSpecification();
                    mtn.setUserObject(speSpe);
                    // aggiunge il nome all'albero
                    radice.add(mtn);
                    mtn.inEvidenza = speSpe.getNome().startsWith(cercata);
                    // aggiunge il nome al vettore dei "primo livello"
                    livelloUno.add(mtn);
                    */
                }else{
                    // E' un sinonimo
                    sinonimi.add(speSpe);
                    Stato.debugLog.fine("sinonimo da inserire poi:"+speSpe.toString());
                }
            }
            // ***** inserisco i nomi buoni non cercati direttamente *****
            for(NodoAlberoSpecieSpecification daInserire: nomiBuoniMaNonCercatiDirettamente){
                radice.add(daInserire);
                livelloUno.add(daInserire);
            }
            
            // ***** deve inserire i sinonimi *****
            SpecieSpecification daInserire;
            // il nodo in cui (eventualmente) inserire la specie
            NodoAlberoSpecieSpecification daConfrontareNodo;
            // la specie contenuta in (DefaultMutableTreeNode)
            SpecieSpecification daConfrontareSpecie;
            // l'indice che scorre il primo livello dell'albero
            int j;
            // impostata a true se la specie è stata inserita nell'albero
            boolean inserimentoRiuscito=false;
            // pseudo genitore per specie senza genitore
            NodoAlberoSpecieSpecification pseudoGenitore=null;
            for(int i=0;i<sinonimi.size();i++){
                // recupera l'elemento da inserire nell'albero
                daInserire = sinonimi.get(i);
                inserimentoRiuscito=false;
                for(j=0;j<livelloUno.size();j++){
                    // recupera gli elementi per il confronto
                    daConfrontareNodo = livelloUno.get(j);
                    daConfrontareSpecie = (SpecieSpecification)(daConfrontareNodo.getUserObject());
                    // Stato.debugLog.fine(daConfrontakpecie.getNome()+"<->"+daInserire.getField(SpecieSpecification.SINONIMO_DI));
                    if( daInserire.getAliasOf().equals(daConfrontareSpecie.getNome()) ){
                        // esito positivo del confronto
                        Stato.debugLog.fine("trovato antenato:"+daConfrontareSpecie.toString());
                        NodoAlberoSpecieSpecification mtn=new NodoAlberoSpecieSpecification();
                        mtn.setUserObject(daInserire);
                        mtn.inEvidenza = daInserire.getNome().startsWith(cercata);
                        // inserisce il nodo
                        daConfrontareNodo.add(mtn);
                        inserimentoRiuscito=true;
                        break;
                    }
                }
                // controlla se la specie non e' stata inserita
                if(!inserimentoRiuscito){
                    if(pseudoGenitore==null){
                        //lo pseudo genitore non esiste => lo creo
                        pseudoGenitore = new NodoAlberoSpecieSpecification();
                        SpecieSpecification specieFinta=new SpecieSpecification();
                        specieFinta.setGenusName(GENERE_FITTIZIO_PER_SINONIMI);
                        pseudoGenitore.setUserObject(specieFinta);
                        radice.add(pseudoGenitore);
                    }
                    Stato.debugLog.fine("inserito figlio dello pseudo genitore");
                    NodoAlberoSpecieSpecification mtn=new NodoAlberoSpecieSpecification();
                    mtn.setUserObject(daInserire);
                    mtn.inEvidenza = daInserire.getNome().startsWith(cercata);
                    // inserisce il nodo
                    pseudoGenitore.add(mtn);
                }
            }
            modelloAlbero.setRoot(radice);
            UtilitaGui.expandAll(albero, new TreePath(radice), true, UtilitaGui.ESPANDI_TUTTI_I_LIVELLI); 
        } catch (Exception e) {
            Dispatcher.consegna(this, e);
        }
    }

    public OggettoSBD getSuccessivo() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public OggettoSBD getPrecedente() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isVisualizzabile(Object oggetto) {
        return oggetto instanceof SpecieSpecification[];
    }

    @Override
    public String toString() {
        return "lspec";
    }

}
