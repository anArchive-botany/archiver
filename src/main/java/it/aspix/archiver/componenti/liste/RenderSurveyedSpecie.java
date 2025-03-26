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
import it.aspix.archiver.UtilitaVegetazione;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class RenderSurveyedSpecie implements ListCellRenderer{
	
    private static final long serialVersionUID = 1L;
    
    public RenderSurveyedSpecie(){
    }

    public Component getListCellRendererComponent( JList lista, Object value, int indice, boolean selected, boolean hasFocus)  {
        JPanel pannello = new JPanel();
        JLabel riga1=new JLabel();
        JLabel riga2=new JLabel();
        
        GridBagLayout lay=new GridBagLayout();
        pannello.setLayout(lay);
        pannello.add(riga1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,     GridBagConstraints.NONE, CostantiGUI.insetsTestoDescrittivo, 0, 0));
        pannello.add(riga2, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,     GridBagConstraints.HORIZONTAL, CostantiGUI.insetsTestoDescrittivo, 0, 0));

        pannello.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        
        Font f=riga2.getFont();
        riga2.setFont(f.deriveFont( f.getSize2D()*0.8f ));
        riga1.setText("X");
        riga2.setText("X");
        
        if(selected)
            pannello.setBackground(CostantiGUI.coloreSfondoElementiSelezionabiliSelezionati);
        else
            pannello.setBackground(CostantiGUI.coloreSfondoElementiSelezionabili);
        SurveyedSpecie ss = (SurveyedSpecie)value;
        String parti[] = UtilitaVegetazione.calcolaNomeSpecie(ss);
        riga1.setText(parti[0]);
        riga2.setText(parti[1]);
        if(ss.getJuvenile()!=null && ss.getJuvenile().equals("true")){
            riga1.setForeground(CostantiGUI.colorePlantula);
        }
        if(ss.getDetermination()!=null && !ss.getDetermination().equals("sure")){
            if(ss.getDetermination().equals("group"))
                riga1.setForeground(CostantiGUI.coloreGruppo);
            else
                riga1.setForeground(CostantiGUI.coloreIncerta);
        }
        return pannello;
    }

}
