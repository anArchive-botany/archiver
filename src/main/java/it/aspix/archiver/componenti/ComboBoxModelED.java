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

import javax.swing.DefaultComboBoxModel;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class ComboBoxModelED extends DefaultComboBoxModel<CoppiaED>{
    
    private static final long serialVersionUID = 1L;

    public String getSelectedEnum(){
        CoppiaED sel = (CoppiaED) this.getSelectedItem();
        return sel.getEsterno();
    }
    
    /************************************************************************
     * Seleziona per default la prima voce
     * @param s la voce da selezionare (la parte server)
     ***********************************************************************/
    public void setSelectedEnum(String s){
        CoppiaED temp;
        int i;
        for(i=0 ; i<this.getSize() ; i++){
            temp = this.getElementAt(i);
            if(temp.getEsterno()==null && s==null){
                this.setSelectedItem(temp);
                break;
            }
            if(temp.getEsterno()!=null && temp.getEsterno().equals(s)){
                this.setSelectedItem(temp);
                break;
            }
        }
        if(i==this.getSize())
        	this.setSelectedItem(this.getElementAt(0));
    }
    
    /* la selezione viene comunque fatta in base alla stringa server */
    /* l'archiettura di questo oggetto è da rivedere: è possibile eliminare set/getSelectedServer() */
    public void setSelectedItem(Object o){
        if(o instanceof CoppiaED){
            super.setSelectedItem(o);
        }else{
        	throw new ClassCastException("il tipo "+o.getClass().getCanonicalName()+" non è utilizzabile in questo contesto");
        }
    }
}
