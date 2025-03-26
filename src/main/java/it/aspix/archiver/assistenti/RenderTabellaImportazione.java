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
package it.aspix.archiver.assistenti;

import it.aspix.archiver.UtilitaVegetazione;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.logging.Level;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/****************************************************************************
 * Fa il rendering di un OggetoConLivello
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class RenderTabellaImportazione extends JLabel implements TableCellRenderer{

    private static final long serialVersionUID = 1L;
    
    public static final class Colori{
    	Color foreground;
    	Color background;
		public Colori(Color foreground, Color background) {
			super();
			this.foreground = foreground;
			this.background = background;
		}
    }
    
    
    public static HashMap<Level, Colori> colori;
    static {
    	colori = new HashMap<Level, Colori>();
    	colori.put(Level.SEVERE, new Colori(Color.RED, Color.YELLOW));
    	colori.put(Level.WARNING, new Colori(Color.RED, null));
    	colori.put(Level.INFO, new Colori(new Color(227,129,58), null));
    	colori.put(Level.CONFIG, new Colori(new Color(12,79,17), null));
    	colori.put(Level.FINE,   new Colori(new Color(12,79,17), null));
    	colori.put(Level.FINER,  new Colori(Color.BLACK, null));
    	colori.put(Level.FINEST, new Colori(Color.BLACK, null));
    	colori.put(Level.OFF,    new Colori(Color.BLACK, null));
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	JLabel etichetta = new JLabel();
    	Color foreground = Color.BLACK;
    	Color background;
        
    	etichetta.setOpaque(true);
    	if(isSelected){
    		background = Color.CYAN;
    	}else{
    		background = Color.WHITE;
    	}
    	if(value instanceof OggettoConLivello){
    		OggettoConLivello o = (OggettoConLivello) value;
	        Colori c;
    		
	        if( o.oggetto instanceof SurveyedSpecie ){
	        	SurveyedSpecie ss = (SurveyedSpecie) o.oggetto;
	        	etichetta.setText( UtilitaVegetazione.calcolaNomeSpecie(ss.getSpecieRefName(), ss.getDetermination() ) );
	        }else{
	        	etichetta.setText(o.getStringa());
	        }
	        c = colori.get(o.getLivello());
	        foreground = c.foreground;
	        if(c.background!=null){
	        	background = c.background;
	        }
    	}else{
    		if(value!=null){
    			etichetta.setText(value.toString());
    		}else{
    			etichetta.setText("");
    		}
    	}
    	etichetta.setBackground(background);
    	etichetta.setForeground(foreground);
        return etichetta;
    }
}