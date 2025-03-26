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
package it.aspix.archiver.editor;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Attribute;
import it.aspix.sbd.obj.AttributeInfo;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class AttributeEditor extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	JLabel etichettaNome = new JLabel();
	JComponent contenuto = null;
	Attribute attributo = null;
	JButton rimuovi = new JButton("-");
	
	/************************************************************************ 
	 * @param a l'attributo che questo editor dovrà contenere
	 ***********************************************************************/
	public AttributeEditor(Attribute a){
		super();
		this.setLayout(new BorderLayout());
		this.add(etichettaNome, BorderLayout.WEST);
		this.add(rimuovi, BorderLayout.EAST);
		this.setAttribute(a);
		this.setText(a.getValue());
		this.setBorder(CostantiGUI.bordoSpaziatoreTopLevelEditor);
	}
	
	/************************************************************************
	 * @param att da visualizzare
	 ***********************************************************************/
	public void setAttribute(Attribute att){
		this.attributo = att;
		if(contenuto!=null){
			this.remove(contenuto);
		}
		try{
			// bisogna recuperare il tipo dell'attributo
			AttributeInfo ai = Stato.comunicatore.getAttributeInfo(att);
			if("text".equals(ai.getType())){
				contenuto = new JTextField();
			}else if("number".equals(ai.getType())){
				contenuto = new JTextField();
			}else if("boolean".equals(ai.getType())){
				JCheckBox x = new JCheckBox();
				x.setText("si");
				contenuto = x;
			} else if("enum".equals(ai.getType())){
				DefaultComboBoxModel<String> dm = new DefaultComboBoxModel<>(ai.getValidValues().split(","));
				contenuto = new JComboBox<String>(dm);
			}
			this.attributo = att;
			this.setText(att.getValue());
			etichettaNome.setText(att.getName()+": ");
			this.add(contenuto, BorderLayout.CENTER);
		}catch(Exception ex){
			Dispatcher.consegna(this, ex);
		}
	}
	
	/************************************************************************
	 * Il testo da visualizzare, l'azione che questo metodo intraprende
	 * dipende dal tipo di attributo che viene visualizzato
	 * @param text
	 ***********************************************************************/
	public void setText(String text){
		if(contenuto instanceof JComboBox){
			JComboBox<String> elemento = (JComboBox<String>) contenuto;
			ComboBoxModel m = elemento.getModel();
			int i;
			for(i=0; i<m.getSize() ; i++){
				if(m.getElementAt(i).equals(text)){
					elemento.setSelectedIndex(i);
					break;
				}
			}
			if(i==m.getSize()){
				// non è stato trovato l'elemento
				throw new IllegalArgumentException(text+" non valido per l'attributo "+attributo.getName());
			}
		}else if(contenuto instanceof JCheckBox){
			JCheckBox elemento = (JCheckBox) contenuto;
			if("true".equals(text)){
				elemento.setSelected(true);
			}else if("false".equals(text)){
				elemento.setSelected(false);
			}else{
				throw new IllegalArgumentException(text+" non valido per l'attributo "+attributo.getName());
			}
		}else{
			((JTextField) contenuto).setText(text);
		}
	}
	
	/************************************************************************
	 * @return l'attributo contenuto in questo editor
	 ***********************************************************************/
	public Attribute getAttribute(){
		Attribute risposta = new Attribute();
		risposta.setName(attributo.getName());
		if(contenuto instanceof JComboBox){
			risposta.setValue( (String) ((JComboBox)contenuto).getSelectedItem());
		}else if(contenuto instanceof JCheckBox){
			JCheckBox elemento = (JCheckBox) contenuto;
			risposta.setValue( elemento.isSelected() ? "true" : "false" );
		}else{
			risposta.setValue( ((JTextField) contenuto).getText() );
		}
		return risposta;
	}
	
	/************************************************************************
	 * Aggancia un actionlistener al pulsante rimuovi
	 * @param al
	 ***********************************************************************/
	public void addRemoveListener(ActionListener al){
		rimuovi.addActionListener(al);
	}

}
