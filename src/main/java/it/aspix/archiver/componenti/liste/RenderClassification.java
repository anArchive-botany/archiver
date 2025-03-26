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
package it.aspix.archiver.componenti.liste;

import it.aspix.archiver.CostantiGUI;
import it.aspix.sbd.obj.Classification;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class RenderClassification implements ListCellRenderer{
	
    private static final long serialVersionUID = 1L;
    
    public RenderClassification(){
    }

    public Component getListCellRendererComponent( JList lista, Object value, int indice, boolean selected, boolean hasFocus)  {
        JLabel etichetta = new JLabel();
        Classification cl = (Classification)value;
        StringBuilder sb = new StringBuilder();
        
        etichetta.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        etichetta.setOpaque(true);
        
        if(selected)
            etichetta.setBackground(CostantiGUI.coloreSfondoElementiSelezionabiliSelezionati);
        else
            etichetta.setBackground(CostantiGUI.coloreSfondoElementiSelezionabili);
        sb.append(cl.getName());
        if(cl.getType().equals("actual")){
            sb.append(" [attuale]");
            etichetta.setForeground(CostantiGUI.coloreTestoElementiSelezionabili);
        }else{
            sb.append(" [sinonimo]");
            etichetta.setForeground(CostantiGUI.coloreTestoElementiSelezionabiliSecondari);
        }
        if(cl.getTypus()!=null && !cl.getTypus().equals("void")){
            sb.append(" <");
            sb.append(cl.getTypus());
            sb.append(">");
        }
        etichetta.setText(sb.toString());
        return etichetta;
    }

}
