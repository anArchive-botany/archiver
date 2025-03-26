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

import it.aspix.archiver.archiver.PannelloDescrivibile;
import it.aspix.archiver.archiver.TopLevelEditor;
import it.aspix.archiver.eventi.SistemaException;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.sbd.obj.OggettoSBD;
import it.aspix.sbd.obj.SpecieSpecification;

/***************************************************************************
 * La classe da estendere per creare un editor di SpecieSpecification
 * @author Edoardo Panfili, studio Aspix
 **************************************************************************/
public abstract class SpecieSpecificationEditor extends PannelloDescrivibile implements TopLevelEditor{
    private static final long serialVersionUID = 1L;
    
    public abstract void setSpecieSpecification(SpecieSpecification specie);
    public abstract SpecieSpecification getSpecieSpecification();
    
    
    public SpecieSpecificationEditor(){
        setTipoContenuto(it.aspix.sbd.obj.SpecieSpecification.class);
    }
    
    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
    public void setOggettoSBD(OggettoSBD oggetto) throws SistemaException, ValoreException {
        if(oggetto instanceof SpecieSpecification){
            setSpecieSpecification((SpecieSpecification)oggetto);
        }else{
            throw new IllegalArgumentException("Questo oggetto può visualizzare soltanto SpecieSpecification, hai passato un "+oggetto.getClass().getCanonicalName());
        }
    }

    /** it.aspix.entwash.archiver.TopLevelEditor **/
    public OggettoSBD getOggettoSBD() throws ValoreException {
        return this.getSpecieSpecification();
    }

    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
    public boolean isVisualizzabile(Object oggetto) {
        return oggetto instanceof SpecieSpecification;
    }
}