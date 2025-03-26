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

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.EventObject;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;

/**************************************************************************** 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class CampoCoordinata extends JTextField implements TableCellEditor{

    private static final long serialVersionUID = 1L;
    
    private abstract class Tentativo{
        public abstract Pattern getPattern();
        public abstract double converti(Matcher m);
    }
    
    private Tentativo tentativi[];
    
    public enum Asse { 
        LATITUDINE("Nord","Sud","(NORD|SUD|Nord|Sud|nord|sud|N|S|n|s)"), 
        LONGITUDINE("Est","Ovest","(EST|OVEST|Est|Ovest|est|ovest|E|O|e|o)");
        
        private final String positivo;
        private final String negativo;
        private final String direzioniPossibili;
        
        Asse(String positivo, String negativo, String direzioniPossibili) {
            this.positivo = positivo;
            this.negativo = negativo;
            this.direzioniPossibili = direzioniPossibili;
        }
        public String positivo()   { return positivo; }
        public String negativo() { return negativo; }
        public boolean isNegativo(String x) {
            return Character.toLowerCase(x.charAt(0)) == Character.toLowerCase(negativo.charAt(0)); 
        }
        public String direzioniPossibili() { return direzioniPossibili; }

    };
    
    private Asse asse;
    private DecimalFormatSymbols separatoreDecimale;
    private Color coloreNormale;
    
    /*************************************************************************
     * @param p una delle costanti LATITUDINE o LONGITUDINE
     ************************************************************************/
    public CampoCoordinata(Asse p){
        super();
        ascoltatori = new Vector<CellEditorListener>();
        separatoreDecimale=new DecimalFormatSymbols(Locale.US);
        asse = p;
        tentativi = new Tentativo[]{
            new Tentativo(){ // 18° 26' 16.67"
                @Override
                public Pattern getPattern() {
                    return Pattern.compile("^(\\d+)\u00b0 ?(\\d+)' ?(\\d+(?:\\.\\d+)?)\" "+asse.direzioniPossibili+"$");
                }
                @Override
                public double converti(Matcher m) {
                    double gradi = Double.parseDouble(m.group(1))+Double.parseDouble(m.group(2))/60+Double.parseDouble(m.group(3))/3600;
                    if(m.group(4)!=null && asse.isNegativo(m.group(4)) )
                        gradi=-gradi;
                    return gradi;
                }                
            },
            new Tentativo(){ // 18 12 45.56
                @Override
                public Pattern getPattern() {
                    return Pattern.compile("^(\\d+) (\\d+) (\\d+(?:\\.\\d+)?) "+asse.direzioniPossibili+"$");
                }
                @Override
                public double converti(Matcher m) {
                    double gradi = Double.parseDouble(m.group(1))+Double.parseDouble(m.group(2))/60+Double.parseDouble(m.group(3))/3600;
                    if(m.group(4)!=null && asse.isNegativo(m.group(4)) )
                        gradi=-gradi;
                    return gradi;
                }                
            },
            new Tentativo(){ // 18° 16.2345'
                @Override
                public Pattern getPattern() {
                    return Pattern.compile("^(\\d+)\u00b0 ?(\\d+(?:\\.\\d+)?)' "+asse.direzioniPossibili+"$");
                }
                @Override
                public double converti(Matcher m) {
                    double gradi = Double.parseDouble(m.group(1))+Double.parseDouble(m.group(2))/60;
                    if(m.group(3)!=null && asse.isNegativo(m.group(3)) )
                        gradi=-gradi;
                    return gradi;
                }                
            },
            new Tentativo(){ // 18 26.763
                @Override
                public Pattern getPattern() {
                    return Pattern.compile("^(\\d+) (\\d+(?:\\.\\d+))? "+asse.direzioniPossibili+"$");
                }
                @Override
                public double converti(Matcher m) {
                    double gradi = Double.parseDouble(m.group(1))+Double.parseDouble(m.group(2))/60;
                    if( m.group(3)!=null && asse.isNegativo(m.group(3)) )
                        gradi=-gradi;
                    return gradi;
                }                
            },
            new Tentativo(){ // 26.763
                @Override
                public Pattern getPattern() {
                    return Pattern.compile("^(\\d+(?:\\.\\d+))? "+asse.direzioniPossibili+"$");
                }
                @Override
                public double converti(Matcher m) {
                    double gradi = Double.parseDouble(m.group(1));
                    if( m.group(2)!=null && asse.isNegativo(m.group(2)) )
                    	gradi=-gradi;
                    return gradi;
                }                
            }
            
        };
        // le impostazioni necessarie per il controllo della validità del dato immesso
        coloreNormale = super.getBackground();
        this.addKeyListener(new KeyAdapter(){
			public void keyTyped(KeyEvent arg0) {
				premutoTasto();
			}
			public void keyReleased(KeyEvent arg0) {
				premutoTasto();
			}
		});
    }
    
    /************************************************************************
     * Serve a controllare se il dato immesso è utilizzabile
     ***********************************************************************/
    private void premutoTasto(){
    	String contenuto = super.getText();
    	if(contenuto.length()>0){
    		// se c'è scritto qualcosa
    		if(analizzaTesto(contenuto).length()>0){
    			this.setBackground(coloreNormale);
    		}else{
    			this.setBackground(CostantiGUI.coloreAttenzione);
    		}
    	}
    }
    
    /*************************************************************************
     * overload del metodo di JTextfield
     ************************************************************************/
    public void setText (String s){
        String modo = Proprieta.recupera("generale.gradi");
        String elaborata;
        if(s==null || s.length()<1){
            elaborata = s;
        }else{
            elaborata = formattaCoordinata(s, modo, asse.negativo, asse.positivo, separatoreDecimale);
        }
        Stato.debugLog.fine(s+"->"+elaborata);
        super.setText(elaborata);
        premutoTasto();
    }
    
    /************************************************************************
     * Presente per poter utilizzare questo componente come convertitore
     * @param s 
     ***********************************************************************/
    public void setTextNoFormat(String s){
        super.setText(s);
    }
    
    /************************************************************************
     * @param s testo da analizzare
     * @return il testo che rappresenta il numero compreso o "" in caso
     * non sia stato compreso nulla
     ***********************************************************************/
    private String analizzaTesto(String s){
        Matcher m;
        String risposta="";
        
        if(s!=null && s.length()>0){
        	s = s.trim().replace('“', '"').replace('”', '"');
            for(int i=0 ; i<tentativi.length ; i++){
                m = tentativi[i].getPattern().matcher(s);
                if(m.find()){
                    risposta = "" + tentativi[i].converti(m);
                    break;
                }
            }
        }
        return risposta;
    }
    
    /*************************************************************************
     * @return i gradi in formato decimale
     ************************************************************************/
    public String getText(){
        String s = super.getText();
        String risposta = analizzaTesto(s);

        if(risposta.length()==0 && s.length()>0){
        	throw new NumberFormatException("coordinata \""+s+"\" non comprensibile.");
        }
        return risposta;
    }
    
    /************************************************************************
     * @param s il dato da formattare
     * @param modo "gps" "gradiPrimi" o altro
     * @param negativo la stringa suffisso per i valori negativi
     * @param positivo la stringa suffisso per i valori positivi
     * @param separatoreDecimale 
     ***********************************************************************/
    public static final String formattaCoordinata(String s, String modo, String negativo, String positivo, DecimalFormatSymbols separatoreDecimale){
        if(modo.equals("gps") && s!=null){
            String emisfero=positivo;
            double convertito=Double.parseDouble(s);
            if(convertito<0){
                convertito=-convertito;
                emisfero=negativo;
            }
            long gradi=(long)(convertito);
            convertito=(convertito-gradi)*60;
            long primi=(long)(convertito);
            convertito=(convertito-primi)*60;
            DecimalFormat df = new DecimalFormat("##.####");
            df.setDecimalFormatSymbols(separatoreDecimale);
            return gradi+"\u00b0 "+primi+"' "+df.format(convertito)+"\" "+emisfero;
        }else if(modo.equals("gradiPrimi")){
            String emisfero=positivo;
            double convertito=Double.parseDouble(s);
            if(convertito<0){
                convertito=-convertito;
                emisfero=negativo;
            }
            int gradi=(int)convertito;
            convertito=(convertito-gradi)*60;
            if(convertito<0)
                convertito=-convertito; // non può essere minore di zero
            DecimalFormat df = new DecimalFormat("##.####");
            df.setDecimalFormatSymbols(separatoreDecimale);
            return gradi+"\u00b0 "+df.format(convertito)+"' "+emisfero; 
        }else if(modo.equals("decimali")){
            String emisfero=positivo;
            double convertito=Double.parseDouble(s);
            if(convertito<0){
                convertito=-convertito;
                emisfero=negativo;
            }
            return convertito+" "+emisfero; 
        }else{
            // modalità sconosciuta
            return s;
        }
    }
    
    // =========================================================================
    // interfaccia javax.swing.table.TableCellEditor
    // =========================================================================
    private Vector<CellEditorListener> ascoltatori;
    
    public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
        this.setText(table.getModel().getValueAt(row,column).toString());
        return this;
    }

    public void addCellEditorListener(CellEditorListener l){
        ascoltatori.add(l);
        Stato.debugLog.finer("aggiunto ascoltatore");
    }

    public void removeCellEditorListener(CellEditorListener l){
        CellEditorListener c;
        for(int j=0;j<ascoltatori.size();j++){
            c = ascoltatori.elementAt(j);
            if(c==l)
                ascoltatori.remove(j);
        }
        Stato.debugLog.finer("rimosso ascoltatore");
    }

    public void cancelCellEditing(){
        ;
    }

    public Object getCellEditorValue(){
        return getText();
    }

    public boolean isCellEditable(EventObject anEvent){
        this.requestFocus();
        return true;
    }

    public boolean shouldSelectCell(EventObject anEvent){
        return true;
    }

    public boolean stopCellEditing(){
        Stato.debugLog.finer("");
        CellEditorListener c;
        for(int j=0;j<ascoltatori.size();j++){
            c = ascoltatori.elementAt(j);
            c.editingStopped(new javax.swing.event.ChangeEvent(this));
        }
        return true;
    }

}