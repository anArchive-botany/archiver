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

import java.util.ArrayList;

import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.introspection.InformazioniTipiEnumerati;
import it.aspix.sbd.introspection.ValoreEnumeratoDescritto;

/****************************************************************************
 * FIXME: spiegare come funziona non sarebbe male!
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class ComboBoxModelEDFactory {
    
	public enum TipoCombo {
	    DIRITTI              ("DirectoryInfo.OwnerReadRights", "diritti"),
	    TIPO_AREA_PROTETTA   ("Place.ProtectedAreaType", "tipo area protetta"),
	    SORGENTE             ("Place.PointSource", "sorgente del punto"),
	    TYPUS                ("Specimen.Typus", "typus"),
	    ORIGINE              ("Specimen.OriginMode", "origine"),
	    POSSESSO             ("Specimen.PossessionMode", "possesso"),
	    DETERMINAVIT_REVIDIT ("Specimen.SpecieAssignedBy", "determinavit/revidit"),
	    LEGIT_DETERMINAVIT   ("Specimen.LegitType", "legit/legit et determinavit"),
	    STATO_SPECIE         (null, "stato specie"),
	    DETERMINAZIONE       ("SurveyedSpecie.Determination", "determinazione"),
	    INCIDENZA            ("SurveyedSpecie.incidence", "incidenza"),
	    TYPUS_VEGETAZIONE    ("Classification.Typus", "typus vegetazione"),
	    FORMA                ("Cell.ShapeName", "forma"),
	    SCALA_ABBONDANZA     ("Cell.AbundanceScale", "scala abbondanza"),
	    USO_NOME_SPECIE      ("SpecieSpecification.Use", "uso nome specie"),
	    CLASSIFICAZIONE      ("Classification.Type", "classificazione"),
	    STRATIFICAZIONE      ("Cell.modelOfTheLevels", "stratificazione");
		
	    private final String proprietaSBD;
		private final String descrizione;
		TipoCombo(String tipoSBD, String descrizione) {
			this.proprietaSBD = tipoSBD;
	        this.descrizione = descrizione;
	    }
	    
	    public String descrizione(){
	    	return this.descrizione;
	    }
	    public String proprietaSBD(){
	    	return this.proprietaSBD;
	    }
	}
    
    /************************************************************************
     * @param tipo uno dei valori in TipoCombo
     * @return un modello che rispecchia tipo
     ***********************************************************************/
    public static ComboBoxModelED createComboBoxModelED(TipoCombo tipo){
        ComboBoxModelED temp = new ComboBoxModelED();
        if(tipo==TipoCombo.STATO_SPECIE){
        	// serve un trattamento speciale perché non esiste in SimpleBotanicalData
            temp.addElement(new CoppiaCSTesto("canonico","Canonico"));
            temp.addElement(new CoppiaCSTesto("sinonimo","Sinonimo"));
            temp.addElement(new CoppiaCSTesto("proparte","Pro parte"));
        }else{
        	ArrayList<ValoreEnumeratoDescritto> alc;
        	Stato.debugLog.fine("Richiesto modello per "+tipo.proprietaSBD());
        	alc=InformazioniTipiEnumerati.getValoriDescritti(tipo.proprietaSBD(),"it");
    		for(ValoreEnumeratoDescritto c: alc){
    			temp.addElement(new CoppiaCSTesto(c));
    		}
        }
        return temp;
    }
    
}