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
package it.aspix.archiver.componenti.alberi;

import it.aspix.sbd.obj.SpecieSpecification;

import javax.swing.tree.DefaultMutableTreeNode;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class NodoAlberoSpecieSpecification extends DefaultMutableTreeNode{

    private static final long serialVersionUID = 1L;
    
    public boolean inEvidenza;

    public NodoAlberoSpecieSpecification(){
        super();
    }
    
    public NodoAlberoSpecieSpecification(String s){
        super(s);
    }
    
    public String toString(){
        Object o = this.getUserObject();
        if (o instanceof SpecieSpecification) {
            SpecieSpecification specie = (SpecieSpecification) o;
            return specie.getNome();
        }
		return o.toString();
    }
}
