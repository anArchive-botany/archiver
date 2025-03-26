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

import it.aspix.sbd.obj.SpecieSpecification;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;


/****************************************************************************
 * Visualizza le informazioni riguardanti una specie
 * @author Edoardo Panfili, studio Aspix
 ****************************************************************************/
public class VisualizzatoreInformazioniSpecie extends JPanel{

    private static final long serialVersionUID = 1L;
    
    DefaultListModel modelloLista=new DefaultListModel();
    JScrollPane pannelloPrincipale = new JScrollPane();
    BorderLayout borderLayout1 = new BorderLayout();
    JList lista = new JList();
    
    /************************************************************************
     * Costruttore di default
     ***********************************************************************/
    public VisualizzatoreInformazioniSpecie() {
        this.setLayout(borderLayout1);
        lista.setModel(modelloLista);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.add(pannelloPrincipale, BorderLayout.CENTER);
        pannelloPrincipale.getViewport().add(lista, null);
        lista.setEnabled(false);
    }
    
    /************************************************************************
     * @param spe la specie di cui visualizzare le informazioni
     ***********************************************************************/
    public void setSpecieSpecification(SpecieSpecification spe){
    	modelloLista.removeAllElements();
    	if(spe!=null){
    		String temp;
    		if(spe.getUse()!=null){
    			if(spe.getUse().equals("misapplied")){
    				modelloLista.addElement("MISAPPLIED: è preferibile non usare questo nome.");
    				lista.setSelectedIndex(0);
    			}
    			if(spe.getUse().equals("unusable")){
    				modelloLista.addElement("UNUSABLE: questo nome non può essere utilizzato.");
    				lista.setSelectedIndex(0);
    			}
    		}
    		if(spe.getAliasOf()!=null){
    			modelloLista.addElement("[sin di] "+spe.getAliasOf());
    		}
            if(spe.getFamily()!=null || spe.getLifeForm()!=null || spe.getCorologicalType()!=null){
                temp="[fam]" + spe.getFamily() +
                	" [fbio]" + spe.getLifeForm()+
                	" [ecoro]" + spe.getCorologicalType();
                modelloLista.addElement(temp);
            }
            if(spe.getHibridAt()!= null && !spe.getHibridAt().equals("none")){
            	modelloLista.addElement(spe.getHibridationDataName1()+" -X- "+spe.getHibridationDataName2());
            }
            String listaSinonimi[] = spe.getSinonimo();
            if(listaSinonimi.length > 0){
            	ArrayList<String> sinonimi = new ArrayList<String>();
            	for(int i=0 ; i<listaSinonimi.length ; i++)
            		sinonimi.add(listaSinonimi[i]);
                java.util.Collections.sort(sinonimi);
                StringBuffer s=new StringBuffer();
                for(int i=0;i<sinonimi.size();i++){
                    if(i!=0)
                        s.append("; ");
                    s.append(sinonimi.get(i));
                }
                modelloLista.addElement("[sinonimi]"+s.toString());
            }
    	}
    }

    public void setFont(Font f){
        super.setFont(f);
        if(lista!=null)
            lista.setFont(f);
    }
}