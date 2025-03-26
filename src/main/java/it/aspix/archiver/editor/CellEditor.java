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
import it.aspix.archiver.eventi.SurveyedSpecieListener;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.sbd.obj.Cell;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.SurveyedSpecie;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public abstract class CellEditor extends JPanel implements SurveyedSpecieListener{
	
    private static final long serialVersionUID = 1L;

    /************************************************************************
	 * @param cella da editare con questo componente
     * @throws ValoreException 
	 ***********************************************************************/
	public abstract void setCell(Cell cella) throws ValoreException;
	
	/************************************************************************
	 * @return la cella editata
	 * @throws ValoreException se i dati contenuti non sono validi
	 ***********************************************************************/
	public abstract Cell getCell() throws ValoreException;

    /************************************************************************
     * rimuove la specie da tutti i plot, la decisione deve venir "diffusa"
     * @param s la specie da rimuovere
     * @param l il livello a cui la specie appartiene
     ***********************************************************************/
    public abstract void rimuoviSpecie(SurveyedSpecie s, Level l);
    
    /************************************************************************
     * @return l'insieme dei lavori
     ***********************************************************************/
    public abstract SurveyedSpecieSet getSpecieSet();
    
	/************************************************************************
	 * @param livello quello in cui successivamente inserire la specie
	 ***********************************************************************/
	public abstract void setLivelloInEdit(Level livello);
	
    /************************************************************************
     * @param ssl un ascoltatore per inserimento/rimozione specie
     ***********************************************************************/
    public abstract void addChangeListener(ChangeListener cl);
    
    /************************************************************************
     * @return una stringa che rappresenta gli strati visualizzati
     ***********************************************************************/
    public abstract String getLevelsSchema();
    
}
