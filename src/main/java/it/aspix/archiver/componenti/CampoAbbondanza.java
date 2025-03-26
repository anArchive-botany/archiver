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

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.editor.SampleEditor;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.sbd.scale.sample.GestoreScale;
import it.aspix.sbd.scale.sample.ScalaSample;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

/****************************************************************************
 * Gestione delle date scale di abbondanza
 * @author Edoardo Panfili for aspix (aspix.it)
 ***************************************************************************/
public class CampoAbbondanza extends JTextField{

    private static final long serialVersionUID = 1L;

    private SampleEditor editor;
    Color oldColorTextField;
    
    public CampoAbbondanza(){
        super();
        this.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent arg0) { }
			
			public void keyReleased(KeyEvent arg0) {
				doTest();
			}
			
			public void keyPressed(KeyEvent arg0) {	}
		});
        oldColorTextField = this.getForeground();
    }
    
    private void doTest(){
        String nomeScala;
        ScalaSample scala;
        
    	if(editor==null){
        	Container x = this.getParent();
        	while(! (x instanceof SampleEditor)){
        		x = x.getParent();
        	}
        	editor = (SampleEditor) x;
    	}
    	try {
			nomeScala = editor.getSample().getCell().getAbundanceScale();
		} catch (ValoreException e) {
			nomeScala = null;
		}
		scala = GestoreScale.buildForName(nomeScala);
		String daTestare = this.getText();
		if(scala.isValid(daTestare)){
			this.setForeground(oldColorTextField);
		}else{
			this.setForeground(CostantiGUI.coloreAttenzione);
		}
    }

}
