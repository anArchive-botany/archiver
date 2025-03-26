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
import it.aspix.sbd.obj.Sample;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public abstract class SampleEditor extends PannelloDescrivibile implements TopLevelEditor{
	
    private static final long serialVersionUID = 1L;
    
    public SampleEditor(){
        setTipoContenuto(it.aspix.sbd.obj.Sample.class);
    }

    /************************************************************************
	 * @param plot da visualizzare
	 * @throws SistemaException
	 * @throws ValoreException
	 ***********************************************************************/
    public abstract void setSample(Sample plot) throws SistemaException, ValoreException;
    
    /************************************************************************
     * @return il plot visualizzato
     * @throws ValoreException
     ***********************************************************************/
    public abstract Sample getSample() throws ValoreException;

    /************************************************************************
     * @return una stringa che rappresenta gli strati visualizzati
     ***********************************************************************/
    public abstract String getLevelsSchema();
    
    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
    public void setOggettoSBD(OggettoSBD oggetto) throws SistemaException, ValoreException {
        if(oggetto instanceof Sample){
            setSample((Sample)oggetto);
        }else{
            throw new IllegalArgumentException("Questo oggetto può visualizzare soltanto Sample, hai passato un "+oggetto.getClass().getCanonicalName());
        }
    }

    /** it.aspix.entwash.archiver.TopLevelEditor **/
    public OggettoSBD getOggettoSBD() throws ValoreException {
        return this.getSample();
    }

    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
    public boolean isVisualizzabile(Object oggetto) {
        return oggetto instanceof Sample;
    }
}