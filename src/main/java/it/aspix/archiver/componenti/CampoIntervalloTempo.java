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

/****************************************************************************
 * Gestione del "tempo" (orario), l'intervallo va scritto nella forma hh:mm 
 * @author edoardo for aspix (aspix.it)
 ***************************************************************************/
public class CampoIntervalloTempo extends JTextField{

    private static final long serialVersionUID = 1L;

    /** rappresenta il nome della cella, utile per comunicare situazioni di errore */
    private String nome;
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public CampoIntervalloTempo(String nome){
        super();
        this.nome=nome;
        this.setToolTipText("formato hh:mm oppure hh:mm:ss");
    }
    
    /*************************************************************************
     * overload del metodo di JTextfield
     ************************************************************************/
    public void setText (String data){
        if(data==null || !( data.length()==8 && data.endsWith(":00") ))
            super.setText(data);
        else{
            super.setText(data.substring(0,5));
        }
    }
    
    /*************************************************************************
     * overload del metodo di JTextfield
     ************************************************************************/
    public String getText(){
        String valore=super.getText();
        if(valore==null || valore.length()==0){
            // in caso di valore non esistente si restituisce e basta
            return valore;
        }
        if(valore.length()!=5 && valore.length()!=8){
            throw new NumberFormatException("Intervallo di tempo "+valore+" non corretto nel campo "+nome+".");
        }
        char v[] = valore.toCharArray();
        if(Character.isDigit(v[0]) && Character.isDigit(v[1]) && v[2]==':' && Character.isDigit(v[3]) && Character.isDigit(v[4])){
        	if(v.length==5){
        		return valore+":00";
        	}else{
        		if(v[5]==':' && Character.isDigit(v[6]) && Character.isDigit(v[7])){
        			return valore;
        		}
        	}
        }
            
        
        throw new NumberFormatException("Intervallo di tempo "+valore+" non corretto nel campo "+nome+".");
    }

}