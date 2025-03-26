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
package it.aspix.archiver.componenti.alberi;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.Icone;

/*****************************************************************************
 * solo per cambiare le icone
 * @author Edoardo Panfili, studio Aspix
 ****************************************************************************/
public class RenderAlberoSpecieSpecification extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;

    public RenderAlberoSpecieSpecification() {
        super();
        leafIcon = Icone.ASfoglia;
        closedIcon = Icone.ASchiuso;
        openIcon = Icone.ASaperto;
    }
    
    /************************************************************************
     * @see javax.swing.tree.TreeCellRenderer
     ***********************************************************************/
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)  {
        JLabel etichetta=new JLabel();
        
        if(selected)
            etichetta.setBackground(CostantiGUI.coloreSfondoElementiSelezionabiliSelezionati);
        else
            etichetta.setBackground(CostantiGUI.coloreSfondoElementiSelezionabili);

        NodoAlberoSpecieSpecification nas = (NodoAlberoSpecieSpecification)value;
        if(nas.isLeaf()){
            etichetta.setIcon(Icone.ASfoglia);
        }else{
            if(expanded){
                etichetta.setIcon(openIcon);
            }else{
                etichetta.setIcon(closedIcon);
            }
        }
        if(nas.inEvidenza){
            etichetta.setForeground(CostantiGUI.coloreTestoElementiSelezionabili);
        }else{
            etichetta.setForeground(CostantiGUI.coloreTestoElementiSelezionabiliSecondari);
        }
        etichetta.setText(nas.toString());
        return etichetta;
    }
}