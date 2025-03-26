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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.UtilitaVegetazione;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class RenderLevelSurveyedSpecie implements TreeCellRenderer{
    
    public RenderLevelSurveyedSpecie(){
    }
    
    /************************************************************************
     * @see javax.swing.tree.TreeCellRenderer
     ***********************************************************************/
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)  {
        JPanel pannello = new JPanel();
        JLabel riga1=new JLabel();
        JLabel riga2=new JLabel();
        
        GridBagLayout lay=new GridBagLayout();
        pannello.setLayout(lay);
        pannello.add(riga1, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.SOUTH,     GridBagConstraints.NONE, CostantiGUI.insetsTestoDescrittivo, 0, 0));
        pannello.add(riga2, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.SOUTH,     GridBagConstraints.NONE, CostantiGUI.insetsTestoDescrittivo, 0, 0));

        pannello.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        
        Font f=riga2.getFont();
        riga2.setFont(f.deriveFont( f.getSize2D()*0.8f ));
        riga1.setText("X");
        riga2.setText("X");
        
        if(selected)
            pannello.setBackground(CostantiGUI.coloreSfondoElementiSelezionabiliSelezionati);
        else
            pannello.setBackground(CostantiGUI.coloreSfondoElementiSelezionabili);
        NodoAlberoLevelSurveyedSpecie nas = (NodoAlberoLevelSurveyedSpecie)value;
        if(nas.isStrato()){
            riga1.setText(nas.strato.getId()+": "+nas.strato.getName());
            StringBuffer sb=new StringBuffer();
            if(nas.strato.getHeight()!=null && nas.strato.getHeight().length()>0){
            	sb.append(nas.strato.getHeight()+"m ");
            }
            if(nas.strato.getCoverage()!=null && nas.strato.getCoverage().length()>0){
                sb.append(", cop="+nas.strato.getCoverage()+"%");
            }
            if(nas.strato.getNote()!=null && nas.strato.getNote().length()>0){
                sb.append(" ("+nas.strato.getNote()+")");
            }
            if(nas.getChildCount()>0){
                sb.append("  ("+nas.getChildCount()+" specie)");
            }
            riga2.setText(sb.toString());
        }else{
        	String parti[] = UtilitaVegetazione.calcolaNomeSpecie(nas.specie);
            riga1.setText(parti[0]);
            riga2.setText(parti[1]);
            if(nas.specie.getJuvenile()!=null && nas.specie.getJuvenile().equals("true")){
                riga1.setForeground(CostantiGUI.colorePlantula);
            }
            if(nas.specie.getDetermination()!=null && !nas.specie.getDetermination().equals("sure")){
                // sb.append(" [DET INCERTA]");
                if(nas.specie.getDetermination().equals("group"))
                    riga1.setForeground(CostantiGUI.coloreGruppo);
                else
                    riga1.setForeground(CostantiGUI.coloreIncerta);
            }
        }
        return pannello;
    }

}