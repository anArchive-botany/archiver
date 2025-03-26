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
package it.aspix.archiver.eventi;

import it.aspix.sbd.obj.SurveyedSpecie;

/****************************************************************************
 * Incapsula tutti i dati sui cambiamenti riguardanti una specie
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SurveyedSpecieCambiata {
    private SurveyedSpecie specieRilevata;
    private SurveyedSpecie vecchiaSpecieRilevata; // serve alla modifica e potrebbe servire anche alla selezione
    
    public SurveyedSpecieCambiata() {
        super();
    }
    public SurveyedSpecieCambiata(SurveyedSpecie specieRilevata) {
        super();
        this.specieRilevata = specieRilevata;
    }
    
    public SurveyedSpecie getSpecieRilevata() {
        return specieRilevata;
    }
    public void setSpecieRilevata(SurveyedSpecie specieRilevata) {
        this.specieRilevata = specieRilevata;
    }
    public SurveyedSpecie getVecchiaSpecieRilevata() {
        return vecchiaSpecieRilevata;
    }
    public void setVecchiaSpecieRilevata(SurveyedSpecie vecchiaSpecieRilevata) {
        this.vecchiaSpecieRilevata = vecchiaSpecieRilevata;
    }
    
}
