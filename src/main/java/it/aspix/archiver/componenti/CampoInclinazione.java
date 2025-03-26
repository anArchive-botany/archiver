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
package it.aspix.archiver.componenti;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JTextField;

import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.nucleo.Proprieta;

/****************************************************************************
 * Gestione dell'esposizione secondo le preferenze impostate
 * @author edoardo for aspix (aspix.it)
 ***************************************************************************/
public class CampoInclinazione extends JTextField {

    private static final long serialVersionUID = 1L;
    
    private static DecimalFormatSymbols separatoreDecimale;
    private static DecimalFormat df = new DecimalFormat("##.####");
    {
    	separatoreDecimale = new DecimalFormatSymbols(Locale.US);
        df.setDecimalFormatSymbols(separatoreDecimale);
    }
    
    /*************************************************************************
     * @param angolo (di inclinazione) espresso in percentuale o con suffisso "°"
     * @return l'angolo in gradi o null se non finisce con %
     ************************************************************************/
    public static String daTestoANumero(String testo){
    	if(testo.endsWith("%")){
            testo=testo.substring(0,testo.length()-1);
            int angolo=(int)(Math.atan2(Double.parseDouble(testo),100)*180/Math.PI+0.5);
            return ""+angolo;   
        }else if(testo.endsWith(UtilitaGui.DEG_SIGN)){
            return testo.substring(0,testo.length()-1);
        }
        return null;
    }
    
    public CampoInclinazione(){
        super();
    }
        
    /*************************************************************************
     * overload del metodo di JTextfield
     ************************************************************************/
    public void setText (String s){
        String modo = Proprieta.recupera("generale.inclinazione");
        if(s==null || s.length()==0){
            super.setText("");
            return;
        }
        if( modo.equals("percentuale")  ){
            double percento = Math.tan(Double.parseDouble(s)*Math.PI/180)*100;
            super.setText(df.format(percento)+"%"); 
        }else{
            // l'unica altra modalita' possibile e' quella numerica
            super.setText(s+UtilitaGui.DEG_SIGN);
        }
    }
    
    /*************************************************************************
     * overload del metodo di JTextfield
     ************************************************************************/
    public String getText() throws NumberFormatException{
        // return getTextUsing(Proprieta.recupera("inclinazione"));
        String testo = super.getText();
        String convertito;
        if(testo==null || testo.length()<1)
            return testo;
        convertito = daTestoANumero(testo); 
        if(convertito!=null){
        	return convertito;
        }else{
            throw new NumberFormatException ("l'inclinazione deve terminare con il simbolo % o il simbolo "+UtilitaGui.DEG_SIGN);
        }
    }

}