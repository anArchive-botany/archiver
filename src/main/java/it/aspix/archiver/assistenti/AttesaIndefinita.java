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

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

/****************************************************************************
 * Crea una finestra con un messaggio e senza possibilità di essere chiusa
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class AttesaIndefinita extends JFrame{
	
	private static final long serialVersionUID = 1L;

	public AttesaIndefinita(String messaggio){
		JLabel etichetta = new JLabel(messaggio);
		this.getContentPane().add(etichetta);
		etichetta.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		this.setUndecorated(true);
		this.pack();
		this.setBackground(Color.RED);
	}
}
