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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.UtilitaGui;

/****************************************************************************
 * Un editor per Sample in pannello unico con scroll
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SampleEditorScroll extends SampleEditorDaEstendere{

    private static final long serialVersionUID = 1L;
    
    LayoutManager lPrincipale = new BorderLayout();
    JScrollPane pannelloScroll = new JScrollPane();
    JPanel pannelloUnico = new JPanel(new GridBagLayout());
        
    public SampleEditorScroll(){
        super();
        init();
        
        this.setLayout(lPrincipale);
        this.add(pannelloScroll, BorderLayout.CENTER);
        
        pannelloUnico.add(editorDirectoryInfo,     new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloUnico.add(pannelloRilevatore,      new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloUnico.add(pannelloOrari,           new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloUnico.add(pannelloClassificazione, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloUnico.add(editorPublicationRef,    new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloUnico.add(pannelloNote,        	   new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloUnico.add(editorPlace,  		   new GridBagConstraints(0, 7, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloUnico.add(editorCella,             new GridBagConstraints(0, 8, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        
        pannelloScroll.getViewport().add(pannelloUnico);
        pannelloScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        UtilitaGui.setOpaqueRicorsivo(this, false);
    }
    
    public Dimension getPreferredSize(){
        // dobbiamo bypassare il pannelloScroll e vedere le dimensioni richieste dal contenuto
        return new Dimension (pannelloUnico.getMinimumSize().width,0);
    }
    
}