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
import it.aspix.sbd.obj.OggettoSBD;
import it.aspix.sbd.obj.Specimen;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public abstract class SpecimenEditor extends PannelloDescrivibile implements TopLevelEditor{
    
    private static final long serialVersionUID = 1L;
    
    public abstract void setSpecimen(Specimen cartellino);
    public abstract Specimen getSpecimen();
    
    public SpecimenEditor(){
        setTipoContenuto(it.aspix.sbd.obj.Specimen.class);
    }
    
    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
    public void setOggettoSBD(OggettoSBD oggetto) {
        if(oggetto instanceof Specimen){
            setSpecimen((Specimen)oggetto);
        }else{
            throw new IllegalArgumentException("Questo oggetto può visualizzare soltanto Specimen, hai passato un "+oggetto.getClass().getCanonicalName());
        }
    }

    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
    public OggettoSBD getOggettoSBD() {
        return this.getSpecimen();
    }

    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
    public boolean isVisualizzabile(Object oggetto) {
        return oggetto instanceof Specimen;
    }
}