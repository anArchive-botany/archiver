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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

/****************************************************************************
 * Il box per inserire il file
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DialogoNomeFile extends JDialog {

	private static final long serialVersionUID = 1L;
	
	JPanel principale = new JPanel(new BorderLayout());
	DropFile nomeFile;
	
	public DialogoNomeFile(String titolo, AccettabilitaFile af){	
		nomeFile =  new DropFile(af);
		this.setTitle(titolo);
		
		principale.add(nomeFile, BorderLayout.CENTER);
		this.getContentPane().add(principale);
		nomeFile.addActionListener(new ActionListener() {		
			public void actionPerformed(ActionEvent e) {
				DialogoNomeFile.this.setVisible(false);
			}
		});
	}
	
	public String getNomeFile(){
		return nomeFile.getText();
	}
}
