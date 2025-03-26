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
package it.aspix.archiver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.SpecieSpecification;
import it.aspix.sbd.obj.SurveyedSpecie;

public class UtilitaVegetazione {
    
    /** utilizzato per creare i livelli fittizi nei singoli settori (o comunque negli elementi terminali della suddivisione) **/
    public static it.aspix.sbd.obj.Level livelloUnico;
    static{
        livelloUnico = new it.aspix.sbd.obj.Level();
        livelloUnico.setId("0");
        livelloUnico.setName("unico");
    }
	
	/************************************************************************
	 * @param l1
	 * @param l2
	 * @return true se i livelli hanno lo stesso id
	 ***********************************************************************/
	public static final boolean stessoLivello(Level l1, Level l2){
		return l1.getId().equals(l2.getId());
	}
	
    /************************************************************************
     * Il confronto viene fatto in base ai campi: NOMESPECIE, COMESINONIMODI, 
     * DETERMINAZIONE
     * @param s1 la prima specie da confrontare
     * @param s2 la seconda specie da confrontare
     * @return true se il nomi coincidono 
     ***********************************************************************/
    public static boolean stessoNomeSurveyedSpecie(SurveyedSpecie s1, SurveyedSpecie s2){
    	return Utilita.ugualeIgnoraVuote(s1.getSpecieRefName(), s2.getSpecieRefName()) &&
	        Utilita.ugualeIgnoraVuote(s1.getSpecieRefAliasOf(), s2.getSpecieRefAliasOf()) &&
	        Utilita.ugualeIgnoraVuote(s1.getDetermination(), s2.getDetermination());
    }
	
	/************************************************************************
	 * @param l l'insieme dei livelli in cui cercare
	 * @param bersaglio il livello interessato
	 * @param ss la specie da cercare
	 * @return la specie trovata o null
	 ***********************************************************************/
	public static final SurveyedSpecie cercaSurveyedSpecie(Level l[], Level bersaglio, SurveyedSpecie ss){
		int indiceLivello;
		int indiceSpecie;
		SurveyedSpecie risposta = null;
		
		for(indiceLivello=0 ; indiceLivello<l.length && risposta==null; indiceLivello++){
			if(stessoLivello(bersaglio, l[indiceLivello])){
				for(indiceSpecie=0 ; indiceSpecie<l[indiceLivello].getSurveyedSpecieCount() && risposta==null; indiceSpecie++){
					if(stessoNomeSurveyedSpecie(ss, l[indiceLivello].getSurveyedSpecie(indiceSpecie))){
						risposta = l[indiceLivello].getSurveyedSpecie(indiceSpecie);
					}
				}
			}
		}
		return risposta;
	}
	
    /************************************************************************
     * Calcola un nome di specie con eventuali livelli di incertezza
     * @param nome il nome della specie
     * @param determinazione il livello di incertezza
     * @return il nome della specie calcolato
     ***********************************************************************/
    public static final String calcolaNomeSpecie(String nome, String determinazione){
        if(determinazione.equals("sure")){
            return nome;
        }
		String nomeAggiustato="ERRORE";
		try{
		    Matcher m;
		    if(determinazione.equals("only-genus")){
		        m=patternSoloGenere.matcher(nome);
		        if(m.find()){
		            nomeAggiustato=m.group(1)+" sp.";
		        }else{
		            throw new Exception(" SPECIE SCONOSCIUTA");
		        }
		    }else if(determinazione.equals("dubt-specie")){
		        m=patternIndecisaSpecie.matcher(nome);
		        if(m.find()){
		            nomeAggiustato=m.group(1)+" cfr. "+m.group(2);
		        }else{
		            throw new Exception(" SPECIE DUBBIA");
		        }
		    }else if(determinazione.equals("only-specie")){
		        m=patternSoloSpecie.matcher(nome);
		        if(m.find()){
		            nomeAggiustato=m.group(1)+" "+SpecieSpecification.SEP_SUBSPECIE;
		        }else{
		            throw new Exception(" SOTTOSPECIE SCONOSCIUTA");
		        }
		    }else if(determinazione.equals("dubt-subspecie")){
		        m=patternIndecisaSottospecie.matcher(nome);
		        if(m.find()){
		            nomeAggiustato=m.group(1)+" cfr. "+m.group(2);
		        }else{
		            throw new Exception(" SOTTOSPECIE DUBBIA");
		        }
		    }else if(determinazione.equals("only-subspecie")){
		        m=patternSoloSottospecie.matcher(nome);
		        if(m.find()){
		            nomeAggiustato=m.group(1)+" "+SpecieSpecification.SEP_VARIETY;
		        }else{
		            throw new Exception(" VARIET\u00c0 SCONOSCIUTA");
		        }
		    }else if(determinazione.equals("dubt-variety")){
		        m=patternIndecisaVarieta.matcher(nome);
		        if(m.find()){
		            nomeAggiustato=m.group(1)+" cfr. "+m.group(2);
		        }else{
		            throw new Exception(" VARIET\u00c0 DUBBIA");
		        }                   
		    }else if(determinazione.equals("group")){
		        nomeAggiustato = nome + " -aggruppamento-";
		    }
		}catch(Exception e){
			e.printStackTrace();
		    nomeAggiustato=nome+e.getMessage();
		}
		return nomeAggiustato;
    }
    
    /************************************************************************
     * Calcola il nome di una specie rilevata con eventuali livelli di incertezza
     * @param specie il nome della specie
     * @return il nome della specie calcolato
     ***********************************************************************/
    public static final String[] calcolaNomeSpecie(SurveyedSpecie specie){
    	String risposta[] = new String[2];
    	StringBuffer sb = new StringBuffer();
    	if(specie.getAbundance()!=null){
    		sb.append("<"+specie.getAbundance()+"> ");
    	}
        sb.append(calcolaNomeSpecie(specie.getSpecieRefName(), specie.getDetermination()));
        if(specie.getSpecieRefAliasOf()!= null && specie.getSpecieRefAliasOf().length()>0){
            sb.append(" ["+specie.getSpecieRefAliasOf()+"]");
        }
        risposta[0] = sb.toString();
        sb = new StringBuffer();
        if(specie.getIncidence()!=null && !specie.getIncidence().equals("unknown")){
            sb.append(" ["+specie.getIncidence()+"]");
        }
        if(specie.getJuvenile()!=null && specie.getJuvenile().equals("true")){
            sb.append(" [PLANTULA]");
        }
        if(specie.getSampleId()!=null && specie.getSampleId().length()>0)
            sb.append(" [ id campione="+specie.getSampleId()+"]");
        risposta[1] = sb.toString();
        return risposta;
    }

    private static final Pattern patternSoloGenere          = Pattern.compile("(\\w+) .*");
    private static final Pattern patternIndecisaSpecie      = Pattern.compile("(\\w+) (\\w+) .*");
    private static final Pattern patternSoloSpecie          = Pattern.compile("(.*) "+SpecieSpecification.SEP_SUBSPECIE+" .*");
    private static final Pattern patternIndecisaSottospecie     = Pattern.compile("(.*) "+SpecieSpecification.SEP_SUBSPECIE+" (\\w+).*");
    private static final Pattern patternSoloSottospecie         = Pattern.compile("(.* "+SpecieSpecification.SEP_SUBSPECIE+" \\w+) "+SpecieSpecification.SEP_VARIETY+".*");
    private static final Pattern patternIndecisaVarieta         = Pattern.compile("(.*) "+SpecieSpecification.SEP_VARIETY+" (\\w+).*");
}
