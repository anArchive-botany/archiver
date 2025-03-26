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

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextField;

import it.aspix.archiver.nucleo.Proprieta;

/****************************************************************************
 * Gestione delle date secondo il formato ISO o europa, questi non collidono,
 * se si vuole inserire il formato americano i problemi sono diversi
 * @author edoardo for aspix (aspix.it)
 ***************************************************************************/
public class CampoData extends JTextField{

    private static final long serialVersionUID = 1L;

    private String nome;
    
    public CampoData(String nome){
        super();
        this.nome=nome;
        this.setToolTipText("esempio 18-09-2007 oppure 2007-09-18"); 
    }
    
    /*************************************************************************
     * overload del metodo di JTextfield
     ************************************************************************/
    public void setText (String data){
        if(data==null || data.length()<1)
            super.setText(data);
        else{
            String modo = Proprieta.recupera("generale.formatoData");
            int tentativo;
            Matcher m;
            Object parti[] = new Object[3]; // sempre nel formato anno,mese,giorno
            boolean impostata = false;
            
            // la data del server è sempre nel formato iso
            for(tentativo=0 ; tentativo<3 && impostata==false ; tentativo++){
                m=patternISO[tentativo].matcher(data);
                if(m.find()){
                    parti[0] = Integer.valueOf(m.group(1));
                    parti[1] = m.groupCount()>1 ? Integer.valueOf(m.group(2)) : null;
                    parti[2] = m.groupCount()>2 ? Integer.valueOf(m.group(3)) : null;
                    if(modo.equals("europa")){
                        super.setText(MessageFormat.format(formatoEuropa[tentativo],parti));
                        impostata = true;
                    }else{
                    	super.setText(MessageFormat.format(formatoISO[tentativo],parti));
                    	impostata = true;
                    }
                }
            }
            if( !impostata ){
            	// throw new NumberFormatException ("Data \""+data+"\" non comprensibile.");
            	// XXX: la data laimposto comunque così che possa essere corretta
            	super.setText(data);
            }
        }
    }
    
    /************************************************************************
     * Presente per poter utilizzare questo componente come convertitore
     * @param s 
     ***********************************************************************/
    public void setTextNoFormat(String s){
        super.setText(s);
    }
    
    /*************************************************************************
     * overload del metodo di JTextfield
     ************************************************************************/
    public String getText(){
        String data = super.getText();
        
        if(data==null || data.length()<1)
            return data;
        try{
            int tentativo;
            Matcher m;
            Object parti[]=new Object[3]; // sempre nel formato anno,mese,giorno
            
            // prima provo con il formato iso
            for(tentativo=0;tentativo<3;tentativo++){
            	m = patternISO[tentativo].matcher(data);
            	if(m.find()){
                    parti[0] = Integer.valueOf(m.group(1));
                    parti[1] = m.groupCount()>1 ? Integer.valueOf(m.group(2)) : null;
                    parti[2] = m.groupCount()>2 ? Integer.valueOf(m.group(3)) : null;
                    return MessageFormat.format(formatoISO[tentativo],parti);
                }
            }
            // poi provo con il formato europeo
            for(tentativo=0;tentativo<3;tentativo++){
            	m = patternEuropa[tentativo].matcher(data);
            	if(m.find()){
                    parti[0] = Integer.valueOf(m.group(m.groupCount()) );
                    parti[1] = m.groupCount()>1 ? Integer.valueOf(m.group(m.groupCount()-1)) : null;
                    parti[2] = m.groupCount()>2 ? Integer.valueOf(m.group(m.groupCount()-2)) : null;
                    return MessageFormat.format(formatoISO[tentativo],parti);
                }
            }
        }catch(Exception ex){
            throw new NumberFormatException("Data non corretta nel campo "+nome+": \""+data+"\"");
        }
        throw new NumberFormatException("Data non corretta nel campo "+nome+": \""+data+"\"");
    }
    
    private static final Pattern patternEuropa[]={
        Pattern.compile("^(\\d+)[ /-](\\d+)[ /-](\\d\\d\\d\\d)$"),  // g-m-a 
        Pattern.compile("^(\\d+)[ /-](\\d\\d\\d\\d)$"),         // m-a
        Pattern.compile("^(\\d\\d\\d\\d)$")                     // a
    };
    private static final Pattern patternISO[]={
        Pattern.compile("^(\\d\\d\\d\\d)[-](\\d\\d)[-](\\d\\d)$"),  // aaaa-mm-gg
        Pattern.compile("^(\\d\\d\\d\\d)[-](\\d\\d)$"),         // aaaa-mm
        Pattern.compile("^(\\d\\d\\d\\d)$")                     // aaaa
    };
    private static final String formatoEuropa[]={
        "{2,number,##}-{1,number,##}-{0,number,####}",
        "{1,number,##}-{0,number,####}",
        "{0,number,####}"
    };
    private static final String formatoISO[]={
        "{0,number,0000}-{1,number,00}-{2,number,00}",
        "{0,number,0000}-{1,number,00}",
        "{0,number,0000}"
    };

}
