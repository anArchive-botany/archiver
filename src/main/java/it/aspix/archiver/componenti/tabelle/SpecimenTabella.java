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
package it.aspix.archiver.componenti.tabelle;

import it.aspix.archiver.componenti.CoppiaED;
import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.Place;
import it.aspix.sbd.obj.SpecieRef;
import it.aspix.sbd.obj.Specimen;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SpecimenTabella extends OggettoPerTabella{
    //per impostare i modelli di ordinamento
    private static int colonnaDaOrdinare;
    private static int verso;

    public int numeroRiga;
    private Specimen cartellino;
    private Place scorciatoiaPlace;
    private DirectoryInfo scorciatoiaDI;
    private static final SpecimenTabella perRecuperoDati = new SpecimenTabella();

    private SpecimenTabella(){
    }

    public SpecimenTabella(Specimen cartellino, int nr) {
        setModificato(false);
        numeroRiga = nr;
        if(cartellino!=null){
            this.cartellino = cartellino;
            scorciatoiaPlace = cartellino.getPlace();
            scorciatoiaDI = cartellino.getDirectoryInfo();
        }
    }

    public Specimen getSpecimen(){
        return cartellino;
    }

    /************************************************************************
     * @param n la colonna che interessa
     * @return il nome della colonna
     ***********************************************************************/
    public String getColumnHeader(int n){
        return (String) perRecuperoDati.eseguiOperazione(HEADER, n, null);
    }

    /************************************************************************
     * @param n la colonna che interessa
     * @return l'ampiezza della colonna
     ***********************************************************************/
    public static int getColumnPreferredWidth(int n){
        return ((Integer) perRecuperoDati.eseguiOperazione(WIDTH, n, null)).intValue();
    }

    public String toString(){
        DirectoryInfo di = new DirectoryInfo();
        return di.getContainerName()+"#"+di.getContainerSeqNo()+", "+
                di.getSubContainerName()+"#"+di.getSubContainerSeqNo()+" ->"+
                cartellino.getSpecieRef().getName();
    }

    public Object getColumn(int n){
        return eseguiOperazione(READ, n, null);
    }

    public void setColumn(int n, Object o){
        eseguiOperazione(WRITE, n, o);
    }

    private static final byte HEADER = 0;
    private static final byte WIDTH = 1;
    private static final byte READ = 2;
    private static final byte WRITE = 3;
    private Object eseguiOperazione(byte op, int colonna, Object o ){
        // Stato.debugLog.fine("op "+op+" su colonna "+colonna);
        switch(colonna){
        case 0:
            if(op==HEADER){     return "num";}
            else if(op==WRITE){ /* non modificabile */; return null; }
            else if(op==READ) { return new FlagModificato(isModificato(),numeroRiga);  }
            else {              return Integer.valueOf(40);}
        case 1:
            if(op==HEADER){     return "erbario";}
            else if(op==WRITE){ scorciatoiaDI.setContainerName(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaDI.getContainerName();  }
            else {              return Integer.valueOf(80);}
        case 2:
            if(op==HEADER){     return "#erbario";}
            else if(op==WRITE){ scorciatoiaDI.setContainerSeqNo(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaDI.getContainerSeqNo();  }
            else if(op==READ) { return Integer.valueOf(40);}
        case 3:
            if(op==HEADER){     return "id erbario";}
            else if(op==WRITE){ scorciatoiaDI.setContainerExternalId(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaDI.getContainerExternalId();  }
            else {              return Integer.valueOf(40);}
        case 4:
            if(op==HEADER){     return "collezione";}
            else if(op==WRITE){ scorciatoiaDI.setSubContainerName(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaDI.getSubContainerName();  }
            else {              return Integer.valueOf(80);}
        case 5:
            if(op==HEADER){     return "#collezione";}
            else if(op==WRITE){ scorciatoiaDI.setSubContainerSeqNo(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaDI.getSubContainerSeqNo();  }
            else {              return Integer.valueOf(40);}
        case 6:
            if(op==HEADER){     return "id collezione";}
            else if(op==WRITE){ scorciatoiaDI.setSubContainerExternalId(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaDI.getSubContainerExternalId();  }
            else {              return Integer.valueOf(40);}
        case 7:
            if(op==HEADER){     return "specie";}
            else if(op==WRITE){ cartellino.setSpecieRef( (SpecieRef)o ) ; return null; }
            else if(op==READ) { return cartellino.getSpecieRef().clone();  }
            else {              return Integer.valueOf(300);}
        case 8:
            if(op==HEADER){     return "bloccato";} // il dato è boolena ma si usa comunque un toString
            else if(op==WRITE){ cartellino.setLocked(o.toString()); return null; }
            else if(op==READ) { return Boolean.valueOf(cartellino.getLocked()!=null && cartellino.getLocked().equals("true")); }
            else {              return Integer.valueOf(40);}
        case 9:
            if(op==HEADER){     return "n.seg.";} // il dato è boolena ma si usa comunque un toString
            else if(op==WRITE){ cartellino.setNewSign(o.toString()); return null; }
            else if(op==READ) { return Boolean.valueOf(cartellino.getNewSign()!=null && cartellino.getNewSign().equals("true")); }
            else {              return Integer.valueOf(40);}
        case 10:
            if(op==HEADER){     return "prestabile";} // il dato è boolena ma si usa comunque un toString
            else if(op==WRITE){ cartellino.setAdmitLoan(o.toString()); return null; }
            else if(op==READ) { return Boolean.valueOf(cartellino.getAdmitLoan()!=null && cartellino.getAdmitLoan().equals("true")); }
            else {              return Integer.valueOf(40);}
        case 11:
            if(op==HEADER){     return "progetto";}
            else if(op==WRITE){ cartellino.setProject(o.toString()); return null; }
            else if(op==READ) { return cartellino.getProject(); }
            else {              return Integer.valueOf(100);}
        case 12:
            if(op==HEADER){     return "nome originale";}
            else if(op==WRITE){ cartellino.setOriginalName(o.toString()); return null; }
            else if(op==READ) { return cartellino.getOriginalName(); }
            else {              return Integer.valueOf(200);}
        case 13:
            if(op==HEADER){     return "typus";}  // l'oggeto selezionato è del tipo CoppiaED
            else if(op==WRITE){ cartellino.setTypus( ((CoppiaED)o).getEsterno() ); return null; }
            else if(op==READ) { return new PTTypus( cartellino.getTypus() ); }
            else {              return Integer.valueOf(100);}
        case 14:
            if(op==HEADER){     return "tipo legit";} // l'oggeto selezionato è del tipo CoppiaED
            else if(op==WRITE){ cartellino.setLegitType( ((CoppiaED)o).getEsterno() ); return null; }
            else if(op==READ) { return new PTLegDet( cartellino.getLegitType() ); }
            else {              return Integer.valueOf(150);}
        case 15:
            if(op==HEADER){     return "legit";}
            else if(op==WRITE){ cartellino.setLegitName(o.toString()); return null; }
            else if(op==READ) { return cartellino.getLegitName();}
            else {              return Integer.valueOf(150);}
        case 16:
            if(op==HEADER){     return "data legit";} // TODO: qui serve l'editor delle date
            else if(op==WRITE){ cartellino.setLegitDate(o.toString()); return null; }
            else if(op==READ) { return cartellino.getLegitDate();}
            else {              return Integer.valueOf(100);}
        case 17:
            if(op==HEADER){     return "det/rev";}
            else if(op==WRITE){ cartellino.setSpecieAssignedBy( ((CoppiaED)o).getEsterno() ); return null; }
            else if(op==READ) { return new PTDetRev( cartellino.getSpecieAssignedBy() ); }
            else {              return Integer.valueOf(100);}
        case 18:
            if(op==HEADER){     return "determinavit";}
            else if(op==WRITE){ cartellino.setDeterminavitName(o.toString()); return null; }
            else if(op==READ) { return cartellino.getDeterminavitName();}
            else {              return Integer.valueOf(150);}
        case 19:
            if(op==HEADER){     return "data deter.";} // TODO: qui serve l'editor delle date
            else if(op==WRITE){ cartellino.setDeterminavitDate(o.toString()); return null; }
            else if(op==READ) { return cartellino.getDeterminavitDate();}
            else {              return Integer.valueOf(100);}
        case 20:
            if(op==HEADER){     return "stato cons.";}
            else if(op==WRITE){ cartellino.setConservationStatus(o.toString()); return null; }
            else if(op==READ) { return cartellino.getConservationStatus(); }
            else {              return Integer.valueOf(100);}
        case 21:
            if(op==HEADER){     return "#fogli";}
            else if(op==WRITE){ cartellino.setNumberOfSheets(o.toString()); return null; }
            else if(op==READ) { return cartellino.getNumberOfSheets(); }
            else {              return Integer.valueOf(40);}
        case 22:
            if(op==HEADER){     return "caratteristiche";}
            else if(op==WRITE){ cartellino.setCharacteristics(o.toString()); return null; }
            else if(op==READ) { return cartellino.getCharacteristics(); }
            else {              return Integer.valueOf(150);}
        case 23:
            if(op==HEADER){     return "associazione";}
            else if(op==WRITE){ cartellino.setInAssociationWith(o.toString()); return null; }
            else if(op==READ) { return cartellino.getInAssociationWith(); }
            else {              return Integer.valueOf(150);}
        case 24:
            if(op==HEADER){     return "note originali";}
            else if(op==WRITE){ cartellino.setOriginalNote(o.toString()); return null; }
            else if(op==READ) { return cartellino.getOriginalNote(); }
            else {              return Integer.valueOf(150);}
        case 25:
            if(op==HEADER){     return "note";}
            else if(op==WRITE){ cartellino.setNote(o.toString()); return null; }
            else if(op==READ) { return cartellino.getNote(); }
            else {              return Integer.valueOf(150);}
        case 26:
            if(op==HEADER){     return "provenienza";}
            else if(op==WRITE){ cartellino.setOriginMode( ((CoppiaED)o).getEsterno() ); return null; }
            else if(op==READ) { return new PTProvenienza( cartellino.getOriginMode() ); }
            else {              return Integer.valueOf(100);}
        case 27:
            if(op==HEADER){     return "prov. org";}
            else if(op==WRITE){ cartellino.setOrigin(o.toString()); return null; }
            else if(op==READ) { return cartellino.getOrigin(); }
            else {              return Integer.valueOf(150);}
        case 28:
            if(op==HEADER){     return "possesso";}
            else if(op==WRITE){ cartellino.setPossessionMode( ((CoppiaED)o).getEsterno() ); return null; }
            else if(op==READ) { return new PTPossesso( cartellino.getPossession() ); }
            else {              return Integer.valueOf(100);}
        case 29:
            if(op==HEADER){     return "possesso org";}
            else if(op==WRITE){ cartellino.setPossession(o.toString()); return null; }
            else if(op==READ) { return cartellino.getPossession(); }
            else {              return Integer.valueOf(150);}

        case 30:
            if(op==HEADER){     return "località";}
            else if(op==WRITE){ scorciatoiaPlace.setName(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaPlace.getName(); }
            else {              return Integer.valueOf(150);}
        case 31:
            if(op==HEADER){     return "comune";}
            else if(op==WRITE){ scorciatoiaPlace.setTown(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaPlace.getTown(); }
            else {              return Integer.valueOf(100);}
        case 32:
            if(op==HEADER){     return "provincia";}
            else if(op==WRITE){ scorciatoiaPlace.setProvince(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaPlace.getProvince(); }
            else {              return Integer.valueOf(100);}
        case 33:
            if(op==HEADER){     return "regione";}
            else if(op==WRITE){ scorciatoiaPlace.setRegion(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaPlace.getRegion(); }
            else {              return Integer.valueOf(100);}
        case 34:
            if(op==HEADER){     return "stato";}
            else if(op==WRITE){ scorciatoiaPlace.setCountry(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaPlace.getCountry(); }
            else {              return Integer.valueOf(100);}
        case 35:
            if(op==HEADER){     return "tipo a.p.";}
            else if(op==WRITE){ scorciatoiaPlace.setProtectedAreaType( ((CoppiaED)o).getEsterno() ); return null; }
            else if(op==READ) { return new PTTipoAreaProtetta( scorciatoiaPlace.getProtectedAreaType() ); }
            else {              return Integer.valueOf(180);}
        case 36:
            if(op==HEADER){     return "area protetta";}
            else if(op==WRITE){ scorciatoiaPlace.setProtectedAreaName(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaPlace.getProtectedAreaName(); }
            else {              return Integer.valueOf(150);}
        case 37:
            if(op==HEADER){     return "RCFR";}
            else if(op==WRITE){ scorciatoiaPlace.setMainGrid(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaPlace.getMainGrid(); }
            else {              return Integer.valueOf(80);}
        case 38:
            if(op==HEADER){     return "UTM";}
            else if(op==WRITE){ scorciatoiaPlace.setSecondaryGrid(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaPlace.getSecondaryGrid(); }
            else {              return Integer.valueOf(80);}
        case 39:
            if(op==HEADER){     return "latitudine";}
            else if(op==WRITE){ scorciatoiaPlace.setLatitude(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaPlace.getLatitude(); }
            else {              return Integer.valueOf(80);}
        case 40:
            if(op==HEADER){     return "longitudine";}
            else if(op==WRITE){ scorciatoiaPlace.setLongitude(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaPlace.getLongitude(); }
            else {              return Integer.valueOf(80);}
        case 41:
            if(op==HEADER){     return "sorgente";}
            else if(op==WRITE){ scorciatoiaPlace.setPointSource(((CoppiaED)o).getEsterno() ); return null; }
            else if(op==READ) { return new PTTipoAreaProtetta( scorciatoiaPlace.getPointSource()); }
            else {              return Integer.valueOf(80);}
        case 42:
            if(op==HEADER){     return "precisione";}
            else if(op==WRITE){ scorciatoiaPlace.setPointPrecision(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaPlace.getPointPrecision(); }
            else {              return Integer.valueOf(80);}
        case 43:
            if(op==HEADER){     return "altitudine";}
            else if(op==WRITE){ scorciatoiaPlace.setElevation(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaPlace.getElevation(); }
            else {              return Integer.valueOf(80);}
        case 44:
            if(op==HEADER){     return "esposizione";}
            else if(op==WRITE){ scorciatoiaPlace.setExposition(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaPlace.getExposition(); }
            else {              return Integer.valueOf(80);}
        case 45:
            if(op==HEADER){     return "inclinazione";}
            else if(op==WRITE){ scorciatoiaPlace.setInclination(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaPlace.getInclination(); }
            else {              return Integer.valueOf(80);}
        case 46:
            if(op==HEADER){     return "substrato";}
            else if(op==WRITE){ scorciatoiaPlace.setSubstratum(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaPlace.getSubstratum(); }
            else {              return Integer.valueOf(120);}
        case 47:
            if(op==HEADER){     return "habitat";}
            else if(op==WRITE){ scorciatoiaPlace.setHabitat(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaPlace.getHabitat(); }
            else {              return Integer.valueOf(120);}
        case 48:
            if(op==HEADER){     return "collocazione";}
            else if(op==WRITE){ cartellino.setCollocation(o.toString()); return null; }
            else if(op==READ) { return cartellino.getCollocation(); }
            else {              return Integer.valueOf(100);}
        case 49:
            if(op==HEADER){     return "id campagna";}
            else if(op==WRITE){ cartellino.setCountrysideReference(o.toString()); return null; }
            else if(op==READ) { return cartellino.getCountrysideReference(); }
            else {              return Integer.valueOf(80);}
        default:
            return null;
        }
    }
    public static final int NUMERO_COLONNE = 50;

    /***************************************************************************
     * Converte nella rappresentazione standard del cartellino
     **************************************************************************/
    public Specimen toCartellino(){
        return cartellino;
    }

    public int compareTo(OggettoPerTabella o){
        int risultato;
    	String dato1 = getColumn(colonnaDaOrdinare)!=null ? getColumn(colonnaDaOrdinare).toString() : null;
    	String dato2 = o.getColumn(colonnaDaOrdinare)!=null ? o.getColumn(colonnaDaOrdinare).toString() : null;

    	if(dato1==null){
    		risultato = -1;
    	}else if (dato2 == null){
    		risultato = 1;
    	}else{
	    	if(colonnaDaOrdinare==0 || colonnaDaOrdinare==2 || colonnaDaOrdinare==5){
	        	// ordinamento numerico
	    		int n1=Integer.parseInt(dato1);
	            int n2=Integer.parseInt(dato2);
	            risultato =  verso*(n2-n1);
	    	}else{
	        	// ordinamento stringa
	            risultato =  verso*dato1.compareTo(dato2);
	    	}
    	}
    	return risultato;
    }

    @Override
    public void setOrdinamento(int colonna, int v) {
        colonnaDaOrdinare = colonna;
        verso = v;
    }

}