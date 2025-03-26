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
package it.aspix.archiver.assistenti;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.Icone;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.sbd.obj.MessageType;

import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.UIManager;

/****************************************************************************
 * Una etichetta in cui è possibile trascinare un file
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DropFile extends JLabel implements DropTargetListener{
    private static final long serialVersionUID = 1L;
    
    File nomeFile;
    AccettabilitaFile tester;
    ArrayList<ActionListener> listaAscoltatori = new ArrayList<ActionListener>();
    
    public DropFile(AccettabilitaFile tester) {
    	this.tester = tester;
        // la creazione di questo oggetto serve affinché l'ambiente grafico lo accetti 
        // come target di un Drop (componente,azione,ascoltatore)
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this );
        // this.setOpaque(false);
        this.setIcon(Icone.TrascinaQui);
        this.setHorizontalAlignment(CENTER);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public void drop(DropTargetDropEvent e) {
        try {
            this.setBackground(UIManager.getColor("TextField.background"));
            Transferable tr = e.getTransferable();
            if(e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                @SuppressWarnings("unchecked")
                java.util.List<File> elenco = (java.util.List<File>) (tr.getTransferData(DataFlavor.javaFileListFlavor));
                nomeFile = elenco.get(0);
                if(!tester.isAccettabile(nomeFile)){
                	Dispatcher.consegna(this, CostruttoreOggetti.createMessage("File non utilizzabile", "it", MessageType.ERROR));
                }else{
                	this.setIcon(null);
                	this.setText(nomeFile.toString());
                    e.dropComplete(true);
                    for(ActionListener a: listaAscoltatori){
                    	a.actionPerformed(null);
                    }
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
    
    public void addActionListener(ActionListener al){
    	listaAscoltatori.add(al);
    }
}
