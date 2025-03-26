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

import it.aspix.archiver.componenti.insiemi.SurveyedSpecieSet;
import it.aspix.archiver.eventi.SistemaException;
import it.aspix.archiver.eventi.SurveyedSpecieListener;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.SurveyedSpecie;

import javax.swing.JPanel;

/****************************************************************************
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public abstract class LevelEditor extends JPanel{

    private static final long serialVersionUID = 1L;

    /************************************************************************
     * I livelli vengono aggiunti (add) e non impostati (set), 
     * esternamente i livelli non hanno annidamento
     * @throws ValoreException 
     ***********************************************************************/
    public abstract void setLevels(Level l[]) throws ValoreException;
    
    /************************************************************************
     * @return tutti i liveli presenti in questo editor
     * @throws ValoreException se i dati contenuti non sono validi
     ***********************************************************************/
    public abstract Level[] getLevels() throws ValoreException;
    
    /************************************************************************
     * @param l il livello da editare (viene considerato solo l'ID per il confronto)
     ***********************************************************************/
    public abstract void setLivelloInEdit(Level l);

    /************************************************************************
     * @param ss la specie da editare
     ***********************************************************************/
    public abstract void setSpecieInEdit(SurveyedSpecie ss);
    
    /************************************************************************
     * @return l'insieme delle specie rappresentate nei livelli in edit
     ***********************************************************************/
    public abstract SurveyedSpecieSet getSpecieSet();
    
    /************************************************************************
     * @param l il livello a cui la specie appartiene, null se rimuovere
     *          da tutti i livelli
     * @param s la specie da rimuovere
     ***********************************************************************/
    public abstract void rimuoviSpecie(Level l, SurveyedSpecie s) throws SistemaException;
    
    /************************************************************************
     * @param ssl un ascoltatore per la selezione delle specie
     ***********************************************************************/
    public abstract void addSurveyedSpecieListener(SurveyedSpecieListener ssl);
    
    /************************************************************************
     * @return una stringa che rappresenta gli strati visualizzati
     ***********************************************************************/
    public abstract String getLevelsSchema();
    
    /************************************************************************
     * @param dest destinazione
     * @param orig origine
     ***********************************************************************/
    protected void copiaDatiSurveyedSpecie(SurveyedSpecie dest, SurveyedSpecie orig){
    	dest.setSpecieRefName(orig.getSpecieRefName());
    	dest.setSpecieRefAliasOf(orig.getSpecieRefAliasOf());
    	dest.setLocked(orig.getLocked());
    	dest.setAbundance(orig.getAbundance());
    	dest.setJuvenile(orig.getJuvenile());
    	dest.setSampleId(orig.getSampleId());
    	dest.setDetermination(orig.getDetermination());
    	dest.setIncidence(orig.getIncidence());
    	dest.setNote(orig.getNote());
    }
    
}
