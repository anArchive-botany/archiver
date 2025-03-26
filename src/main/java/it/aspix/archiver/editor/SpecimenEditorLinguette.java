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
import javax.swing.JTabbedPane;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.archiver.ManagerSuggerimenti;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SpecimenEditorLinguette extends SpecimenEditorDaEstendere{

    private static final long serialVersionUID = 1L;
    
    LayoutManager lPrincipale = new BorderLayout();
    JTabbedPane pannelloLinguette = new JTabbedPane();
    GridBagLayout lPaginaDue = new GridBagLayout();
    JPanel paginaDue = new JPanel(lPaginaDue);
    GridBagLayout lPaginaTre = new GridBagLayout();
    JPanel paginaTre = new JPanel(lPaginaTre);
    GridBagLayout lPaginaQuattro = new GridBagLayout();
    JPanel paginaQuattro = new JPanel(lPaginaQuattro);

        
    public SpecimenEditorLinguette(){
        super();
        init();
        JPanel spreco1 = new JPanel();
        JPanel spreco2 = new JPanel();
        JPanel spreco3 = new JPanel();
        JPanel pannelloAttributi = new JPanel(new BorderLayout());
        
        this.setLayout(lPrincipale);
        this.add(pannelloLinguette, BorderLayout.CENTER);
        
        pannelloLinguette.add(paginaDue,"generici");
        pannelloLinguette.add(paginaTre,"geografici");
        pannelloLinguette.add(paginaQuattro,"attributi");

        paginaDue.add(editorDirectoryInfo,   new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        paginaDue.add(pannelloDatiBase,      new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        paginaDue.add(spreco1,               new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       CostantiGUI.insetsSpreco, 0, 0));
        paginaTre.add(editorPlace,           new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        paginaTre.add(pannelloStato,         new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        paginaTre.add(pannelloCollocazione,  new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        paginaTre.add(spreco2,               new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       CostantiGUI.insetsSpreco, 0, 0));
        paginaQuattro.add(pannelloAttributi, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        paginaQuattro.add(spreco3,           new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       CostantiGUI.insetsSpreco, 0, 0));
        UtilitaGui.setOpaqueRicorsivo(this, false);
        
        pannelloAttributi.add(editorDirectoryInfo.getPannelloAtributi(), BorderLayout.CENTER);
        pannelloAttributi.setBorder(UtilitaGui.creaBordoConTesto("attributi estesi", 0, 0));
        pannelloAttributi.setPreferredSize(new Dimension(0,400));
        
        pannelloAttributi.setPreferredSize(new Dimension(0,400));
        ManagerSuggerimenti.check(this);
    }
    
}