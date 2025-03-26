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
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.archiver.PannelloDescrivibile;
import it.aspix.archiver.archiver.TopLevelEditor;
import it.aspix.archiver.componenti.CampoData;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.sbd.obj.Blob;
import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.OggettoSBD;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class BlobEditor extends PannelloDescrivibile implements TopLevelEditor{

    private static final long serialVersionUID = 1L;
    
    DirectoryInfoEditor editorDirectoryInfo;
    GridBagLayout layoutPrincipale = new GridBagLayout();
    
    GridBagLayout layoutOggetto = new GridBagLayout();
    JPanel pannelloOggetto = new JPanel(layoutOggetto);
    DropX oggetto = new DropX();
    JScrollPane scroll = new JScrollPane(oggetto);
    JLabel tipoMime = new JLabel();
    JLabel dimensione = new JLabel();
    
    GridBagLayout layoutDati = new GridBagLayout();
    JPanel pannelloDati = new JPanel(layoutDati);
    JLabel eNome = new JLabel();
    JTextField nome = new JTextField();
    JLabel eDescrizione = new JLabel();
    JTextField descrizione = new JTextField();
    JLabel eAutore = new JLabel();
    JTextField autore = new JTextField();
    JLabel eData = new JLabel();
    CampoData data = new CampoData("data");
    
    String mime;
    byte[] datiBinariBlob;
    String id; // viene inserito se si sta modificando un blob
    
    public BlobEditor(){
        eNome.setText("nome:");
        eDescrizione.setText("descrizione:");
        eAutore.setText("autore:");
        eData.setText("data:");
        editorDirectoryInfo = new DirectoryInfoEditor(this);
        tipoMime.setText("?/?");
        dimensione.setText("?");
        
        // -------------------- inserimento nei pannelli --------------------
        JPanel spreco1 = new JPanel();
        
        this.setLayout(layoutPrincipale);
        this.add(editorDirectoryInfo, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        this.add(pannelloDati,        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        this.add(pannelloOggetto,     new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       CostantiGUI.insetsGruppo, 0, 0));
        this.add(spreco1,             new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       CostantiGUI.insetsGruppo, 0, 0));
        
        pannelloDati.add(eNome,        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDati.add(nome,         new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDati.add(eDescrizione, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDati.add(descrizione,  new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDati.add(eAutore,      new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDati.add(autore,       new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloDati.add(eData,        new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,        CostantiGUI.insetsEtichetta, 0, 0));
        pannelloDati.add(data,         new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,  CostantiGUI.insetsDatoTesto, 0, 0));
        
        pannelloOggetto.add(scroll,     new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER,   GridBagConstraints.BOTH, CostantiGUI.insetsEtichetta, 0, 0));
        pannelloOggetto.add(tipoMime,   new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,   CostantiGUI.insetsDatoTesto, 0, 0));        
        pannelloOggetto.add(dimensione, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,   CostantiGUI.insetsDatoTesto, 0, 0));
        
        pannelloDati.setBorder(UtilitaGui.creaBordoConTesto("Informazioni",0,5));
        setTipoContenuto(it.aspix.sbd.obj.Blob.class);
        UtilitaGui.setOpaqueRicorsivo(this, false);
        scroll.getViewport().setOpaque(false);
        try{
            Blob blo = CostruttoreOggetti.createBlob(null);
            this.setBlob(blo);
        }catch(Exception ex){
            // FIXME: così soltanto non va bene ma al momento della creazione è possibile che l'oggetto non sia inserito in una gerarchia
            ex.printStackTrace();
        }
    }
    

    /************************************************************************
     * @return il blob rappresentato in questo oggetto
     ***********************************************************************/
    public Blob getBlob(){
        Blob blob = new Blob();
       
        blob.setId(id);
        blob.setDirectoryInfo(editorDirectoryInfo.getDirectoryInfo());
        blob.setAuthor(autore.getText());
        blob.setContent("full");
        blob.setDate(data.getText());
        blob.setDescription(descrizione.getText());
        blob.setMimeType(tipoMime.getText());
        blob.setName(nome.getText());
        blob.setBloccoDati(datiBinariBlob);
        blob.setSize(""+datiBinariBlob.length);
        return blob;
    }
    
    /************************************************************************
     * @param b l'ìoggetto da visualizzare
     ***********************************************************************/
    public void setBlob(Blob b){
        id = b.getId();
        editorDirectoryInfo.setDirectoryInfo(b.getDirectoryInfo());
        autore.setText(b.getAuthor());
        // FIXME: e se arriva una immagine thumbnail e il soggetto va a amodificarla?
        data.setText(b.getDate());
        descrizione.setText(b.getDescription());
        tipoMime.setText(b.getMimeType());
        mime=b.getMimeType();
        nome.setText(b.getName());
        datiBinariBlob = b.getBloccoDati();
        try{
            aggiornaAnteprima();
        }catch(Exception ex){
            Dispatcher.consegna(this, ex);
        }
    }
    
    /************************************************************************ 
     * visualizza l'immagine rappresentata dal vettore di bytes
     * @throws IOException
     ***********************************************************************/
    private void aggiornaAnteprima() throws IOException{
        if(datiBinariBlob!=null){
        	if(mime.equals("image/jpeg") || mime.equals("image/png")){
	            ByteArrayInputStream bais = new ByteArrayInputStream(datiBinariBlob); 
	            BufferedImage im = ImageIO.read(bais);
	            oggetto.setIcon( new ImageIcon( im ) );
        	}else{
        		oggetto.setIcon( Icone.Pdf );
        	}
        }else{
            oggetto.setIcon( Icone.TrascinaQui );
        }
    }

    /****************************************************************************
     * La casella che accetta un file usando il Drag&Drop
     * @author edoardo for aspix (aspix.it)
     ***************************************************************************/
    public class DropX extends JLabel implements DropTargetListener{

        private static final long serialVersionUID = 1L;
        
        public DropX() {
            // la creazione di questo oggetto serve affinché l'ambiente grafico lo accetti 
            // come target di un Drop (componente,azione,ascoltatore)
            new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this );
            // this.setOpaque(false);
        }

        public void drop(DropTargetDropEvent e) {
            String nomePiccolo;
            long dim;
            File nomeFile;
            try {
                this.setBackground(UIManager.getColor("TextField.background"));
                DataFlavor fileFlavor = DataFlavor.javaFileListFlavor;
                Transferable tr = e.getTransferable();
                if(e.isDataFlavorSupported(fileFlavor)) {
                    e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    @SuppressWarnings("unchecked")
                    java.util.List<File> elenco = (java.util.List<File>) (tr.getTransferData(fileFlavor));
                    nomeFile = elenco.get(0);
                    nomePiccolo = nomeFile.getPath().toLowerCase();
                    mime = null;
                    if(nomePiccolo.endsWith("jpg") || nomePiccolo.endsWith("jpeg")){
                        mime = "image/jpeg";
                    }else if(nomePiccolo.endsWith("png")){
                        mime = "image/png";
                    }else if(nomePiccolo.endsWith("pdf")){
                    	mime = "application/pdf";
                    }
                    // XXX: per usare tiff serve javax.media.jai.JAI
                    if(mime!=null){
                        // leggo i dati dal file
                        byte[] tmp = new byte[1024];
                        int letti;
                        FileInputStream fis = new FileInputStream(nomeFile);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        
                        while( (letti=fis.read(tmp)) != -1 ){
                            baos.write(tmp,0,letti);
                        }
                        datiBinariBlob = baos.toByteArray();
                        aggiornaAnteprima();
                        e.dropComplete(true);
                        tipoMime.setText(mime);
                        dim = nomeFile.length()/1024;
                        if(dim>1024){
                            dimensione.setText((nomeFile.length()/1024)+"MB");
                        }else{
                            dim/=1024;
                            dimensione.setText((nomeFile.length()/1024)+"KB");
                        }
                    }else{
                        e.dropComplete(false);
                    }
                }else {
                    e.rejectDrop();
                }
            }catch(Exception ioe) {
                ioe.printStackTrace();
            }
        }
        private boolean opaco = false;
        public void dragExit(DropTargetEvent e) { 
            this.processFocusEvent(new FocusEvent(this,FocusEvent.FOCUS_LOST));
            this.setBackground(UIManager.getColor("TextField.background"));
            this.setOpaque(opaco);
        }
        public void dragEnter(DropTargetDragEvent e) {
            this.processFocusEvent(new FocusEvent(this,FocusEvent.FOCUS_GAINED));
            opaco = this.isOpaque();
            this.setOpaque(true);
            this.setBackground(CostantiGUI.coloreDragGestito);
        }
        public void dragOver(DropTargetDragEvent e) {
            this.processFocusEvent(new FocusEvent(this,FocusEvent.FOCUS_GAINED));
            this.setBackground(CostantiGUI.coloreDragGestito); 
        }
        public void dropActionChanged(DropTargetDragEvent e) { }
    }
    
    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
    public void setOggettoSBD(OggettoSBD oggetto) {
        if(oggetto instanceof Blob){
            setBlob((Blob)oggetto);
        }else{
            throw new IllegalArgumentException("Questo oggetto può visualizzare soltanto Blob, hai passato un "+oggetto.getClass().getCanonicalName());
        }
    }

    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
    public OggettoSBD getOggettoSBD() {
        return this.getBlob();
    }

    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
    public boolean isVisualizzabile(Object oggetto) {
        return oggetto instanceof Blob;
    }

    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
	public String getSuggerimenti() {
		return null;
	}
	
    public String toString(){
        DirectoryInfo di = editorDirectoryInfo.getDirectoryInfo();
        String nome = di.getContainerName()+"#"+di.getContainerSeqNo(); 
        return nome.length()>1 ? nome : "blob";
    }

}
