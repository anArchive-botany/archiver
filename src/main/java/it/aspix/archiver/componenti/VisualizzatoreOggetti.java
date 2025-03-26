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

import it.aspix.archiver.archiver.ElencoInterattivo;
import it.aspix.sbd.obj.OggettoSBD;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public interface VisualizzatoreOggetti {
    /************************************************************************
     * @param oggetto da visualizzare
     * @param elenco da cui viene l'oggetto
     * @return true se si è in grado di visualizzarlo e si è tentato di farlo
     * (è peraltro possibile che qualcosa sia andato male e la visualizzazione
     * non è avvenuta) false se non si è in grado di visualizzare questo
     * particolare oggetto
     ***********************************************************************/
    public boolean visualizza(OggettoSBD oggetto, ElencoInterattivo elenco);
    /************************************************************************
     * @param oggetto da visualizzare
     * @param pattern che descrive l'array di oggetti
     * @return true se si è in grado di visualizzarlo e si è tentato di farlo
     * (è peraltro possibile che qualcosa sia andato male e la visualizzazione
     * non è avvenuta) false se non si è in grado di visualizzare questo
     * particolare oggetto
     ***********************************************************************/
    public boolean visualizza(OggettoSBD oggetto[], OggettoSBD pattern);
}
