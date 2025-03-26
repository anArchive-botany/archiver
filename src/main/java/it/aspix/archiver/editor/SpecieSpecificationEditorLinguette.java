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

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.UtilitaGui;

/****************************************************************************
 * Un editor per SpecieSpecification che usa due linguette
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SpecieSpecificationEditorLinguette extends SpecieSpecificationEditorDaEstendere{

    private static final long serialVersionUID = 1L;
    
    BorderLayout lThis = new BorderLayout();
    JTabbedPane pannelloLiguette = new JTabbedPane();
    GridBagLayout lPagina1 = new GridBagLayout();
    JPanel pagina1 = new JPanel(lPagina1);
    GridBagLayout lPagina2 = new GridBagLayout();
    JPanel pagina2 = new JPanel(lPagina2);
        
    public SpecieSpecificationEditorLinguette(){
        super();
        init();
        JPanel pannelloAttributi = new JPanel(new BorderLayout());
        
        JPanel spreco1 = new JPanel();
        JPanel spreco2 = new JPanel();
        
        this.setLayout(lThis);
        this.add(pannelloLiguette, BorderLayout.CENTER);
        pannelloLiguette.add(pagina1,"generici");
        pannelloLiguette.add(pagina2,"indicatori");
        
        pagina1.add(editorDirectoryInfo, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pagina1.add(editorPublicationRef,new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pagina1.add(pannelloGeneraliCodici,    new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pagina1.add(pannelloStato,       new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pagina1.add(pannelloNome,        new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pagina1.add(pannelloNote,        new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pagina1.add(spreco1,             new GridBagConstraints(0, 6, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsSpreco, 0, 0));
        pagina2.add(pannelloIndicatori,  new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pagina2.add(pannelloAttributi, 	 new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pagina2.add(spreco2,             new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsSpreco, 0, 0));
        
        pannelloAttributi.add(editorDirectoryInfo.getPannelloAtributi(), BorderLayout.CENTER);
        pannelloAttributi.setBorder(UtilitaGui.creaBordoConTesto("attributi estesi", 0, 0));
        pannelloAttributi.setPreferredSize(new Dimension(0,400));
        UtilitaGui.setOpaqueRicorsivo(this, false);
    }
    
}