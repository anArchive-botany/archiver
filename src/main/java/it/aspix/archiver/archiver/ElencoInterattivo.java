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
package it.aspix.archiver.archiver;

import it.aspix.sbd.obj.OggettoSBD;

/****************************************************************************
 * Questa interfaccia serve per interagire con dei contenitori generici
 * in grado di visualizzare elenchi di oggetti
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public interface ElencoInterattivo {
    /************************************************************************
     * @param oggetto da visualizzare nell'interfaccia
     * @param pattern descrive gli oggetti che compongono l'elenco
     * @throws IllegalArgumentException se l'oggetto non è visualizzabile
     * @throws Exception in altri casi, dipende dal lavoro dell'editor
     ***********************************************************************/
    public void setOggettoSBD(OggettoSBD elementi[], OggettoSBD pattern) throws Exception;
    /************************************************************************
     * @return l'oggetto successivo rispetto a quello selezionato (e sposta
     * la selezione)
     ***********************************************************************/
    public OggettoSBD getSuccessivo() throws Exception;
    /************************************************************************
     * @return l'oggetto precedente rispetto a quello selezionato (e sposta
     * la selezione)
     ***********************************************************************/
    public OggettoSBD getPrecedente() throws Exception;
    /************************************************************************
     * @param oggetto che si vorrebbe visualizzare
     * @return true se l'oggetto è visualizzabile
     ***********************************************************************/
    public boolean isVisualizzabile(Object oggetto);
}
