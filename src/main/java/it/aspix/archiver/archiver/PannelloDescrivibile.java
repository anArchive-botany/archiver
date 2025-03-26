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
package it.aspix.archiver.archiver;

import java.awt.Color;
import java.awt.Container;
import java.awt.SystemColor;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import it.aspix.archiver.CostantiGUI;

/****************************************************************************
 * Un pannello che notifica i propri cambiamenti, poi per recuperare la
 * descrizione va usata toString()
 * Questa classe registra soltanto gli ascoltatori e notifica le variazioni
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public abstract class PannelloDescrivibile extends JPanel{
    
    private static final long serialVersionUID = 1L;
    @SuppressWarnings("rawtypes")
    private Class tipoContenuto = java.lang.Object.class;
    private ArrayList<ChangeListener> ascoltatoriCambiamento = new ArrayList<ChangeListener>();
    /** Questo componente serve per essere incluso in un Raccoglitore che 
     * visualizza (inserendolo in un JPanel) un solo elemento alla volta così
     * gli elementi non visibili non hanno un parent, questa variabile serve 
     * a memorizzare il parent da fornire in risposta a getParent() 
     * qundo la gerarchia AWT non ne fornisce uno */
    private Container fixedParent = null;
    
    private static Color mediaColore(Color x){
        Color n = new Color(
                (SystemColor.window.getRed() + x.getRed())/2,
                (SystemColor.window.getGreen() + x.getGreen())/2,
                (SystemColor.window.getBlue() + x.getBlue())/2
        );
        return n;
    }
    
    @SuppressWarnings("rawtypes")
    private static HashMap<Class, Color> corrispondenze = new HashMap<Class, Color>();
    static{
        corrispondenze.put(java.lang.Object.class, CostantiGUI.coloreSfondoOggettoIndefinito);
        corrispondenze.put(it.aspix.sbd.obj.Sample.class, mediaColore(CostantiGUI.coloreSfondoSample));
        corrispondenze.put(it.aspix.sbd.obj.SpecieSpecification.class, mediaColore(CostantiGUI.coloreSfondoSpecieSpecification));
        corrispondenze.put(it.aspix.sbd.obj.Specimen.class, mediaColore(CostantiGUI.coloreSfondoSpecimen));
        corrispondenze.put(it.aspix.sbd.obj.Blob.class, mediaColore(CostantiGUI.coloreSfondoBlob));
    }
    
    public void addChangeListener(ChangeListener cl){
        ascoltatoriCambiamento.add(cl);
    }
    
    public void removeChangeListener(ChangeListener cl){
        ascoltatoriCambiamento.remove(cl);
    }    
    
    protected void fireChange(){
        ChangeEvent evento = new ChangeEvent(this);
        for(ChangeListener cl: ascoltatoriCambiamento){
            cl.stateChanged(evento);
        }
    }

    /************************************************************************
     * Memorizza il tipo contenuto e imposta il colore di sfondo di conseguenza
     * @param tipoContenuto dell'oggetto che questo pannello gestisce
     ***********************************************************************/
    public void setTipoContenuto(@SuppressWarnings("rawtypes") Class tipoContenuto) {
        this.tipoContenuto = tipoContenuto;
        this.setBackground(corrispondenze.get(tipoContenuto));
        // System.out.println("impostato il colore a "+corrispondenze.get(tipoContenuto));
    }

    /************************************************************************
     * @return il tipo dell'oggetto contenuto in questo pannello
     ***********************************************************************/
    @SuppressWarnings("rawtypes")
    public Class getTipoContenuto() {
        return tipoContenuto;
    }
    
    /************************************************************************
     * Un pannelloDescrivibile è stato disegnato per essere contenuto in un
     * Raccoglitore che usa toString() per descrivere il pannello nella linguetta
     ***********************************************************************/
    public abstract String toString();
    
    /************************************************************************
     * @param x il contenitore che contine questo pannello
     ***********************************************************************/
    public void setFixedParent(Container x){
    	fixedParent = x;
    }
    
    /************************************************************************
     * @return il normale valore ritornato da getParent() se non è null
     * altrimenti il parent fissato usando setFixedParent()
     ***********************************************************************/
    @Override
    public Container getParent(){
    	if(super.getParent()!=null){
    		return super.getParent();
    	}else{
    		return fixedParent;
    	}
    }
    
}
