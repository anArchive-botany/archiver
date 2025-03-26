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
package it.aspix.archiver.componenti.tabelle;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import it.aspix.archiver.CostantiGUI;

/****************************************************************************
 * Utilizzata per fare il rendering del contenuto della tabella:
 * gestisce il render di sole icone (come tali), tutto il resto viene
 * rappresentato come testo (con il metodo toString)
 * l'oggetto che fa il rendering è comunque e sempre una etichetta
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class RenderFlagModificato extends JLabel implements TableCellRenderer{

    private static final long serialVersionUID = 1L;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        FlagModificato fm = (FlagModificato)value;
        
        JLabel etichetta = new JLabel();
        etichetta.setOpaque(true);
        etichetta.setText(""+fm.riga);
        if(fm.modificato){
            etichetta.setBackground(CostantiGUI.coloreAttenzione);
        }else{
            etichetta.setBackground(CostantiGUI.coloreSfondoElementiSelezionabili);
        }
        return etichetta;
    }
}