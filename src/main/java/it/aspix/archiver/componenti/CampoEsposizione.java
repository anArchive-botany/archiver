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

import javax.swing.JTextField;

import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.nucleo.Proprieta;

/****************************************************************************
 * Gestione dell'esposizione secondo le preferenze impostate
 * @author edoardo for aspix (aspix.it)
 ***************************************************************************/
public class CampoEsposizione extends JTextField {

    private static final long serialVersionUID = 1L;

    // attenzione, questa dstruttura è duplicata in celebrand
    public static final CoppiaED traduzione[] = {
        new CoppiaCSTesto("N","0"),new CoppiaCSTesto("NNE","23"),new CoppiaCSTesto("NE","45"),new CoppiaCSTesto("ENE","68"),
        new CoppiaCSTesto("E","90"),new CoppiaCSTesto("ESE","113"),new CoppiaCSTesto("SE","135"),new CoppiaCSTesto("SSE","158"),
        new CoppiaCSTesto("S","180"),new CoppiaCSTesto("SSO","203"),new CoppiaCSTesto("SO","225"),new CoppiaCSTesto("OSO","248"),
        new CoppiaCSTesto("O","270"),new CoppiaCSTesto("ONO","293"),new CoppiaCSTesto("NO","315"),new CoppiaCSTesto("NNO","337")
    };
    
    public static final CoppiaED traduzioneEN[] = {
        new CoppiaCSTesto("N","0"),new CoppiaCSTesto("NNE","23"),new CoppiaCSTesto("NE","45"),new CoppiaCSTesto("ENE","68"),
        new CoppiaCSTesto("E","90"),new CoppiaCSTesto("ESE","113"),new CoppiaCSTesto("SE","135"),new CoppiaCSTesto("SSE","158"),
        new CoppiaCSTesto("S","180"),new CoppiaCSTesto("SSW","203"),new CoppiaCSTesto("SW","225"),new CoppiaCSTesto("WSW","248"),
        new CoppiaCSTesto("W","270"),new CoppiaCSTesto("WNW","293"),new CoppiaCSTesto("NW","315"),new CoppiaCSTesto("NNW","337")
    };

    public CampoEsposizione(){
        super();
    }

    /*************************************************************************
     * @param cardinale un punto espresso come NNE o N o ...
     * @return l'angolo in gradi o null se non trova una corrispondenza
     ************************************************************************/
    public static String daCardinaleANumero(String cardinale){
        for(int i=0;i<traduzione.length;i++){
            if(traduzione[i].getEsterno().equals(cardinale)){
                return traduzione[i].getDescrizione();
            }
        }
        for(int i=0;i<traduzioneEN.length;i++){
            if(traduzioneEN[i].getEsterno().equals(cardinale)){
                return traduzioneEN[i].getDescrizione();
            }
        }
        return null;
    }
    
    @Override
    public void setText (String s){
        String modo=Proprieta.recupera("generale.esposizione");
        if( s==null || s.length()==0 ){
            super.setText("");
            return;
        }
        if( modo.equals("cardinale") ){
            int i;
            for(i=0;i<traduzione.length;i++){
                if(traduzione[i].getDescrizione().equals(s)){
                    super.setText(traduzione[i].getEsterno());
                    break;
                }
            }
            if(i==traduzione.length){
                // non ha trovato corrispondenze, per quieto vivere scrivo lo stesso
                super.setText(s);
            }
        }else{
            // l'unica altra modalita' possibile e' quella numerica
            super.setText(s+UtilitaGui.DEG_SIGN);
        }
    }
    
    @Override
    public String getText(){
    	String testo = super.getText().toUpperCase();
    	
        for(int i=0;i<traduzione.length;i++){
            if(traduzione[i].getEsterno().equals(testo)){
                return traduzione[i].getDescrizione();
            }
        }
        if(testo.endsWith(UtilitaGui.DEG_SIGN)){
            return testo.substring(0,testo.length()-1);
        }
        if(testo.length()==0)
        	return testo;
        throw new NumberFormatException ("l'esposizione deve essere una sigla del tipo NNO o terminare con il simbolo "+UtilitaGui.DEG_SIGN);
    }

}
