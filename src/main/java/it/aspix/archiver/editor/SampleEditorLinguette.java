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
 * Un editor per Sample che usa due linguette
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SampleEditorLinguette extends SampleEditorDaEstendere{

    private static final long serialVersionUID = 1L;
  
    // pannello principale e sue pagine
    JTabbedPane pannelloLinguette = new JTabbedPane();
    GridBagLayout layoutPaginaGenerali = new GridBagLayout();
    JPanel pannelloPaginaGenerali = new JPanel(layoutPaginaGenerali);
    GridBagLayout layoutPaginaGeografici = new GridBagLayout();
    JPanel pannelloPaginaGeografici = new JPanel(layoutPaginaGeografici);
    GridBagLayout layoutPaginaCelle = new GridBagLayout();
    JPanel pannelloPaginaCelle = new JPanel(layoutPaginaCelle);
    GridBagLayout layoutPaginaAttributi = new GridBagLayout();
    JPanel pannelloPaginaAttributi = new JPanel(layoutPaginaAttributi);
        
    public SampleEditorLinguette(){
        super();
        init();
        
        JPanel sprecoGenerali = new JPanel();
        JPanel sprecoGeografici = new JPanel();
        JPanel pannelloAttributi = new JPanel(new BorderLayout());
        JPanel sprecoAttributi = new JPanel();
        
        this.setLayout(new BorderLayout());
        this.add(pannelloLinguette, BorderLayout.CENTER);
        
        pannelloLinguette.add("generali", pannelloPaginaGenerali);
        pannelloLinguette.add("geografici", pannelloPaginaGeografici);
        pannelloLinguette.add("specie", pannelloPaginaCelle);
        pannelloLinguette.add("attributi", pannelloPaginaAttributi);
        
        pannelloPaginaGenerali.add(editorDirectoryInfo,     new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloPaginaGenerali.add(pannelloRilevatore,      new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloPaginaGenerali.add(pannelloOrari,           new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloPaginaGenerali.add(pannelloClassificazione, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloPaginaGenerali.add(editorPublicationRef,    new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloPaginaGenerali.add(pannelloNote,            new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloPaginaGenerali.add(sprecoGenerali,          new GridBagConstraints(0, 6, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       CostantiGUI.insetsSpreco, 0, 0));

        pannelloPaginaGeografici.add(editorPlace,      new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloPaginaGeografici.add(sprecoGeografici, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       CostantiGUI.insetsSpreco, 0, 0));
        
        pannelloPaginaCelle.add(editorCella, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsSpreco, 0, 0));
        
        pannelloPaginaAttributi.add(pannelloAttributi, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        pannelloPaginaAttributi.add(sprecoAttributi,   new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       CostantiGUI.insetsSpreco, 0, 0));
        
        pannelloAttributi.add(editorDirectoryInfo.getPannelloAtributi(), BorderLayout.CENTER);
        pannelloAttributi.setBorder(UtilitaGui.creaBordoConTesto("attributi estesi", 0, 0));
        pannelloAttributi.setPreferredSize(new Dimension(0,400));
        
        UtilitaGui.setOpaqueRicorsivo(this, false);
    }
    
}