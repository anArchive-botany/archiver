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

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.archiver.ManagerSuggerimenti;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SpecimenEditorScroll extends SpecimenEditorDaEstendere{

    private static final long serialVersionUID = 1L;
    
    LayoutManager lPrincipale = new BorderLayout();
    JScrollPane pannelloScroll = new JScrollPane();
    JPanel pannelloUnico = new JPanel(new GridBagLayout());
        
    public SpecimenEditorScroll(){
        super();
        init();
        
        this.setLayout(lPrincipale);
        this.add(pannelloScroll, BorderLayout.CENTER);
        
        pannelloUnico.add(editorDirectoryInfo,   new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloUnico.add(pannelloDatiBase,      new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloUnico.add(editorPlace,           new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloUnico.add(pannelloStato,         new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloUnico.add(pannelloCollocazione,  new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        
        pannelloScroll.getViewport().add(pannelloUnico);
        UtilitaGui.setOpaqueRicorsivo(this, false);
        ManagerSuggerimenti.check(this);
    }
    
    public Dimension getPreferredSize(){
        // dobbiamo bypassare il pannelloScroll e vedere le dimensioni richieste dal contenuto
        return new Dimension (pannelloUnico.getMinimumSize().width,0);
    }
    
}