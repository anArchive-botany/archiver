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

import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.sbd.obj.Attribute;
import it.aspix.sbd.obj.AttributeInfo;
import it.aspix.sbd.obj.Blob;
import it.aspix.sbd.obj.Cell;
import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.Level;
import it.aspix.sbd.obj.Message;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.Place;
import it.aspix.sbd.obj.PublicationRef;
import it.aspix.sbd.obj.Sample;
import it.aspix.sbd.obj.SpecieRef;
import it.aspix.sbd.obj.SpecieSpecification;
import it.aspix.sbd.obj.Specimen;
import it.aspix.sbd.obj.SurveyedSpecie;
import it.aspix.sbd.obj.Text;

/****************************************************************************
 * Costruisce oggetti di diverso tipo basandosi sulle preferenze
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class CostruttoreOggetti {
	
	/************************************************************************
	 * @param ai il prototipo su cui costruire l'attributo
	 * @return un Attribute con valori di default ragionevoli
	 ***********************************************************************/
	public static final Attribute createAttribute(AttributeInfo ai){
		Attribute a = new Attribute();
		a.setName(ai.getName());
		if("text".equals(ai.getType())){
			a.setValue("");
		}else if("boolean".equals(ai.getType())){
			a.setValue("false");
		} else if("enum".equals(ai.getType())){
			a.setValue(ai.getValidValues().split(",")[0]);
		}
		return a;
	}
	
	/************************************************************************
	 * @return un DirectoryInfo costruito in base a Proprieta
	 ***********************************************************************/
	public static final DirectoryInfo createDirectoryInfo(){
		DirectoryInfo di = new DirectoryInfo();
		// CONTAINER_NOME non può essere impostato da qui, ma si può fare da un oggetto top-level
		di.setOwnerReadRights(Proprieta.recupera("umask.ownerRead"));
		di.setOwnerWriteRights(Proprieta.recupera("umask.ownerWrite"));
		di.setContainerReadRights(Proprieta.recupera("umask.groupRead"));
		di.setContainerWriteRights(Proprieta.recupera("umask.groupWrite"));
		di.setOthersReadRights(Proprieta.recupera("umask.otherRead"));
		di.setOthersWriteRights(Proprieta.recupera("umask.otherWrite"));
		return di;
	}
	
    /************************************************************************
     * @param pattern per recuperare (se specificato nelle proprietà) alcune informazioni
     * @return una SpecieSpecification costruita in base a Proprieta
     ***********************************************************************/
    public static final Blob createBlob(Blob pattern){
        Blob vuoto = new Blob();
        vuoto.setDirectoryInfo(createDirectoryInfo());
        vuoto.getDirectoryInfo().setContainerName(Proprieta.recupera("blob.database"));
        return vuoto;
    }
	
	/************************************************************************
	 * @param pattern per recuperare (se specificato nelle proprietà) la
	 * località o altre informazioni
	 * @return uno Specimen costruito in base a Proprieta
	 ***********************************************************************/
	public static final Specimen createSpecimen(Specimen pattern){
	    Specimen daRestituire = null;
		Specimen vuoto = new Specimen();
		vuoto.setPlace(new Place());
		vuoto.setDirectoryInfo(createDirectoryInfo());
		vuoto.getDirectoryInfo().setContainerName(Proprieta.recupera("herbaria.database"));
		vuoto.setSpecieRef(new SpecieRef());
		vuoto.setLocked(Proprieta.recupera("herbaria.bloccaNomeSpecie"));
		vuoto.setConservationStatus(Proprieta.recupera("herbaria.statoConservazione"));
		if(pattern!=null){
    	    if(Proprieta.recupera("herbaria.pulireAlNuovo").equals("tutto")){
                daRestituire = vuoto;
            }else if(Proprieta.recupera("herbaria.pulireAlNuovo").equals("tranneGeografici")){
                daRestituire = vuoto;
                daRestituire.setPlace(pattern.getPlace());
            }else{
                DirectoryInfo di = pattern.getDirectoryInfo();
                daRestituire = pattern.clone();
                di.setContainerSeqNo("");
                di.setContainerExternalId("");
                di.setSubContainerSeqNo("");
                di.setSubContainerExternalId("");
                daRestituire.setDirectoryInfo(di);
            }
		}else{
		    daRestituire = vuoto;
		}
	    return daRestituire;
	}
	
    /************************************************************************
     * @param pattern per recuperare (se specificato nelle proprietà) alcune informazioni
     * @return una SpecieSpecification costruita in base a Proprieta
     ***********************************************************************/
    public static final SpecieSpecification createSpecieSpecification(SpecieSpecification pattern){
        SpecieSpecification vuoto = new SpecieSpecification();
        vuoto.setDirectoryInfo(createDirectoryInfo());
        vuoto.getDirectoryInfo().setContainerName(Proprieta.recupera("check-list.database"));
        vuoto.setPublicationRef(new PublicationRef());
        if(pattern!=null){
        	if(Proprieta.isTrue("check-list.nonPulireNulla")){
        		vuoto = pattern.clone();
        		vuoto.setId(null);
        		vuoto.getDirectoryInfo().setContainerSeqNo("");
        		vuoto.getDirectoryInfo().setContainerExternalId("");
        		vuoto.getDirectoryInfo().setSubContainerSeqNo("");
        		vuoto.getDirectoryInfo().setSubContainerExternalId("");
        	}else{
	        	if(Proprieta.isTrue("check-list.nonPulireFamiglia")){
	        		vuoto.setFamily(pattern.getFamily());
	        	}
	        	if(Proprieta.isTrue("check-list.nonPulireBibliografia")){
	        		vuoto.setPublicationRef(pattern.getPublicationRef().clone());
	        	}
        	}
        }
        return vuoto;
    }
	
	/************************************************************************
	 * @param pattern per recuperare (se specificato nelle proprietà) la
     * località o altre informazioni
     * @return un Sample costruito in base a Proprieta
	 ***********************************************************************/
	public static final Sample createSample(Sample pattern, double larghezza, double lunghezza, String nomi[], int righe[], int colonne[]){
        Cell cellaPrincipale;
        
		Sample plot = new Sample();
		plot.setPlace(new Place());
		plot.setDirectoryInfo(createDirectoryInfo());
		plot.setPublicationRef(new PublicationRef());
		plot.getDirectoryInfo().setContainerName(Proprieta.recupera("vegetazione.database"));
		cellaPrincipale = new Cell();
		cellaPrincipale.setShapeName("rectangle");
		cellaPrincipale.setAbundanceScale(Proprieta.recupera("vegetazione.defaultScalaAbbondanza"));
		cellaPrincipale.setModelOfTheLevels(Proprieta.recupera("vegetazione.modelloLivelli"));
		if(nomi!=null){
			plot.setPattern("plot");
			cellaPrincipale.setType("plot");
		}else{
			plot.setPattern("relevée");
			cellaPrincipale.setType("relevée");
		}
		// creo gli strati
		if(Proprieta.recupera("vegetazione.schemaStrati").length()>0){
			addLevels(cellaPrincipale, Proprieta.recupera("vegetazione.schemaStrati"));
		}else{
			addLevelsByPattern(cellaPrincipale, Proprieta.recupera("vegetazione.modelloLivelli"));
		}
		plot.setCell(cellaPrincipale);
		if(nomi!=null){
			aggiungiLivelloRicorsivo(cellaPrincipale, larghezza, lunghezza, 0, righe, colonne, nomi);
		}
		if(pattern!=null){ 
    		// copio le parti utili dal pattern
    		if(Proprieta.isTrue("vegetazione.nonPulireNulla")){
                // devo recuperare tutti i dati tranne le griglie
                plot = pattern.clone();
                rimuoviSpecieRicorsivo(plot.getCell());
                plot.getDirectoryInfo().setContainerSeqNo("");
                plot.getDirectoryInfo().setContainerExternalId("");
                plot.getDirectoryInfo().setSubContainerSeqNo("");
                plot.getDirectoryInfo().setSubContainerExternalId("");
                plot.setId(null);
            }else{
                if(Proprieta.isTrue("vegetazione.nonPulireProgettoDiritti")){
                    plot.setDirectoryInfo(pattern.getDirectoryInfo());
                    plot.getDirectoryInfo().setContainerSeqNo("");
                    plot.getDirectoryInfo().setContainerExternalId("");
                    plot.getDirectoryInfo().setSubContainerSeqNo("");
                    plot.getDirectoryInfo().setSubContainerExternalId("");
                }
                if(Proprieta.isTrue("vegetazione.nonPulireRilevatori")){
                    plot.setSurveyer(pattern.getSurveyer());
                    plot.setDate(pattern.getDate());
                }
                if(Proprieta.isTrue("vegetazione.nonPulireGeografici")){
                    plot.setPlace(pattern.getPlace());
                }
            }
		}
		return plot;
	}
	
	/************************************************************************
	 * Quello che viene costruito è sempre un rilievo, non un plot 
	 * @param modelloStrati uno dei valori di sbd (es: "1" o "10")
	 * @param nomeScalaAbbondanza uno dei valori di sbd (es: "1" o "10")
     * @return un Sample costruito in base a Proprieta
	 ***********************************************************************/
	public static final Sample createSimpleSample(String modelloStrati, String nomeScalaAbbondanza){
        Cell cellaPrincipale;
        
		Sample plot = new Sample();
		plot.setPlace(new Place());
		plot.setDirectoryInfo(createDirectoryInfo());
		plot.setPublicationRef(new PublicationRef());
		cellaPrincipale = new Cell();
		plot.setPattern("relevée");
		cellaPrincipale.setType("relevée");
		cellaPrincipale.setAbundanceScale(nomeScalaAbbondanza);
		cellaPrincipale.setModelOfTheLevels(modelloStrati);
		addLevelsByPattern(cellaPrincipale, modelloStrati);
		plot.setCell(cellaPrincipale);
		return plot;
	}
	
	
    /********************************************************************************************
     * Serve per la generazione iniziale dell'albero delle celle, è ricorsiva!
     * @param radice la cella a cui appendere quelle generate
     * @param larghezza della cella radice
     * @param lunghezza della cella radice
     * @param livello il livello a cui appartiene la radice
     * @param righe il vettore che contiene il numero di righe di ogni livello
     * @param colonne il vettore che contiene il numero di colonne di ogni livello 
     * @param nome il vettore che contiene il nome di ogni livello
     *******************************************************************************************/
    private static void aggiungiLivelloRicorsivo(Cell radice, double larghezza, double lunghezza, int livello, int righe[], int colonne[], String nome[]){
        if(livello==righe.length)
            return;
        
        int ri,co;
        Cell nuova;
        radice.setNumberOfRows(""+righe[livello]);
        radice.setNumberOfColumns(""+colonne[livello]);
        radice.setShapeName(Cell.FORMA_RECTANGLE);
        radice.setShapeDimension1(""+larghezza);
        radice.setShapeDimension2(""+lunghezza);
        for(ri=0;ri<righe[livello];ri++){
            for(co=0;co<colonne[livello];co++){
                nuova = new Cell();
                nuova.setType(nome[livello+1]);
                nuova.setRow(""+ri);
                nuova.setColumn(""+co);
                radice.setFiglio(ri,co,nuova);
                nuova.setPadre(radice);
                nuova.setModelOfTheLevels("0"); // livello unico
                if(livello==righe.length-2){
                	nuova.addLevel(UtilitaVegetazione.livelloUnico.clone());
                }else{
                	aggiungiLivelloRicorsivo(nuova,larghezza/colonne[livello],lunghezza/righe[livello],livello+1,colonne,righe,nome);
                }
            }
        }
    }
    
    /************************************************************************
     * rimuove tutte le specie presenti anche nei figli di questa cella
     * @param cella da cui partire per la rimozione
     ***********************************************************************/
    private static void rimuoviSpecieRicorsivo(Cell cella){
    	if(cella.getLevelCount()>0){
    		Level l;
    		for(int i=0; i<cella.getLevelCount() ; i++){
    			l = cella.getLevel(i);
    			l.removeAllSurveyedSpecie();
    		}
    	}
    	Cell figli[] = cella.getFigli();
    	if(figli!=null){
    		for(int i=0; i<figli.length; i++){
    			rimuoviSpecieRicorsivo(figli[i]);
    		}
    	}
    }
    
	/************************************************************************
	 * @param pattern per recuperare (se specificato nelle proprietà) la
     * località o altre informazioni
     * @return un Sample costruito in base a Proprieta
	 ***********************************************************************/
	public static final Sample createSampleLivelloUnico(Sample base){
        Cell cellaPrincipale;
        Sample plot;
        if(base==null){
        	plot = new Sample();
        }else{
        	plot = base;
        }
		plot.setPlace(new Place());
		plot.setDirectoryInfo(createDirectoryInfo());
		plot.setPublicationRef(new PublicationRef());
		cellaPrincipale = new Cell();
		plot.setPattern("relevée");
		cellaPrincipale.setType("relevée");
		cellaPrincipale.addLevel(CostruttoreOggetti.createLevel("0", "unico"));
		plot.setCell(cellaPrincipale);
		return plot;
	}

	
	/************************************************************************
	 * @return un Level costruito in base ai parametri
	 ************************************************************************/
    public static final Level createLevel(String id, String nome, String copertura, String altezza, String note ){
        Level l = new Level();
        l.setId(id);
        l.setName(nome);
        l.setCoverage(copertura);
        l.setHeight(altezza);
        l.setNote(note);    
        return l;
    }

    /************************************************************************
     * @param id
     * @param nome
     * @return un Level con l'id e il nome spcificati
     ***********************************************************************/
    public static final Level createLevel(String id, String nome){
        Level l = new Level();
        l.setId(id);
        l.setName(nome);
        return l;
    }
    
    /************************************************************************
     * @return una SurveyedSpecie in base ai parametri forniti
     ***********************************************************************/
    public static final SurveyedSpecie createSurveyedSpecie(SpecieRef rs, boolean bloccato, String valore, String determinazione, String id, String giovane,String incidenza){
        SurveyedSpecie s = new SurveyedSpecie();
        s.setSpecieRefName(rs.getName());
        s.setSpecieRefAliasOf(rs.getAliasOf());
        s.setLocked(""+bloccato);
        s.setAbundance(valore);
        s.setDetermination(determinazione);
        s.setSampleId(id);
        s.setJuvenile(giovane);
        s.setIncidence(incidenza);
        return s;
    }
    
    /************************************************************************
     * @return una SurveyedSpecie in base ai parametri forniti
     ***********************************************************************/
    public static final SurveyedSpecie createSurveyedSpecie(String nome, boolean bloccato, String determinazione){
        SurveyedSpecie s = new SurveyedSpecie();
        s.setSpecieRefName(nome);
        s.setLocked(""+bloccato);
        s.setDetermination(determinazione);
        return s;
    }
    
    /************************************************************************
     * Metodo di utilità per la costruzione di un messaggio
     * @param testo il corpo del messaggio
     * @param lingua la lingua del corpo
     * @param tipo una delle costanti di Message.TYPE_
     * @return il messaggio creato
     ***********************************************************************/
    public static Message createMessage(String testo, String lingua, MessageType tipo){
        Message msg = new Message();
        msg.setType(tipo);
        msg.addText(new Text(lingua,testo));
        return msg;
    }
    
    /************************************************************************
     * Classe di utilità per recuperare le informazioni sulle 
     * suddivisioni dalla preferenze
     * @author Edoardo Panfili, studio Aspix
     ***********************************************************************/
    public static class Suddivisione{
    	public String nomi[];
    	public int divisioniX[];
    	public int divisioniY[];
    }
    
    /************************************************************************
     * @return la rappresentazione delle suddivisioni descritte
     * nelle preferenze
     ***********************************************************************/
    public static Suddivisione suddivisioniDaPreferenze(){
    	String parti[] = Proprieta.recupera("vegetazione.modelloSuddivisioni").split(";");
        String nomi[] = new String[parti.length];
        int dims[] = new int[parti.length];
    	String subparti[];
    	for(int i=0 ; i<parti.length ; i++){
    		subparti = parti[i].trim().split(" +");
    		nomi[i] = subparti[0];
    		dims[i] = Integer.parseInt(subparti[1]);
    	}
    	// costruisco la risposta
    	Suddivisione risposta = new Suddivisione();
    	risposta.nomi = nomi;
    	risposta.divisioniX = dims;
    	risposta.divisioniY = dims;
    	return risposta;
    }
    
    /************************************************************************
     * Crea gli strati secondo lo schema passato
     * @param cella a cui aggiungere gli strato
     * @param pattern %id~nome strato
     ***********************************************************************/
    public static void addLevels(Cell cella, String pattern){
    	int posizioneTilde;
    	String id;
    	String nome;
        String[] parti;
        
        parti = pattern.substring(1).split("%");
        for(int i=0;i<parti.length;i++){
            posizioneTilde = parti[i].indexOf("~");
            id=parti[i].substring(0,posizioneTilde);
            nome=parti[i].substring(posizioneTilde+1);
            cella.addLevel(CostruttoreOggetti.createLevel(id, nome));
        }
    }
    
    /************************************************************************
     * Rimuove gli strati vuoti da una cella (non funziona ricorsivamente
     * sulle subcelle)
     * @param cella di cui controllare gli strati
     ***********************************************************************/
    public static void rimuoviLivelliVuoti(Cell c){
    	// for a ritroso perché cancello gli elementi
    	for(int i=c.getLevelCount()-1; i>=0; i--){
    		if(c.getLevel(i).getSurveyedSpecieCount()==0){
    			c.removeLevel(c.getLevel(i));
    		}
    	}
    }
    
    /************************************************************************
     * Crea gli strati secondo lo schema passato
     * @param cella a cui aggiungere gli strato
     * @param nomePattern uno dei nomi di pattern di sbd
     ***********************************************************************/    
    public static void addLevelsByPattern(Cell cella, String nomePattern){
    	if(nomePattern.indexOf('0')!=-1 || "all".equals(nomePattern)){
    		cella.addLevel(CostruttoreOggetti.createLevel("0", "unico"));
    	}
    	if("1".equals(nomePattern) || "10".equals(nomePattern)){
    		addLevels(cella,
    				"%1~alberi" +
    				"%2~arbusti" +
    				"%3~erbe" +
    				"%4~tallofite" +
    				"%5~liane" +
    				"%6~idrofite" +
    				"%7~acqua" +
    				"%8~lettiera" +
    				"%9~rocce" +
    				"%10~pietre");
    	}
    	if("333".equals(nomePattern) || "3330".equals(nomePattern) || "all".equals(nomePattern)){
    		addLevels(cella,
    				"%1.1~alti alberi%1.2~medi alberi%1.3~bassi alberi" +
    				"%2.1~alti arbusti%2.2~medi arbusti%2.3~bassi arbusti" +
    				"%3.1~alte erbe%3.2~medie erbe%3.3~basse erbe" +
    				"%4.1~funghi%4.2~briofite%4.3~licheni" +
    				"%5~liane" +
    				"%6.1~idrofite galleggianti%6.2~idrofite sommerse non radicanti%6.3~idrofite radicanti" +
    				"%7~acqua" +
    				"%8~lettiera" +
    				"%9~rocce" +
    				"%10~pietre");
    	}
    	if("all".equals(nomePattern)){
    		addLevels(cella,
    				"%1~alberi" +
    				"%2~arbusti" +
    				"%3~erbe" +
    				"%4~tallofite" +
    				"%6~idrofite");
    	}
    	if("222".equals(nomePattern) || "2220".equals(nomePattern)){
    		addLevels(cella,
    				"%1.1~alti alberi%1.3~bassi alberi" +
    				"%2.1~alti arbusti%2.3~bassi arbusti" +
    				"%3.1~alte erbe%3.3~basse erbe" +
    				"%4~tallofite" +
    				"%5~liane" +
    				"%6~idrofite" +
    				"%7~acqua" +
    				"%8~lettiera" +
    				"%9~rocce" +
    				"%10~pietre");
    	}
    	if("322".equals(nomePattern) || "3220".equals(nomePattern)){
    		addLevels(cella,"%1.1~alti alberi%1.2~medi alberi%1.3~bassi alberi" +
    				"%2.1~alti arbusti%2.3~bassi arbusti" +
    				"%3.1~alte erbe%3.3~basse erbe" +
    				"%4~tallofite" +
    				"%5~liane" +
    				"%6~idrofite" +
    				"%7~acqua" +
    				"%8~lettiera" +
    				"%9~rocce" +
    				"%10~pietre");
    	}
    }
}
