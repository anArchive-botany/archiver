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

import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.Place;
import it.aspix.sbd.obj.Sample;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SampleTabella extends OggettoPerTabella{
    //per impostare i modelli di ordinamento
    public static int colonnaDaOrdinare;
    public static int verso;

    public int numeroRiga;
    private Sample rilievo;
    private Place scorciatoiaPlace;
    private DirectoryInfo scorciatoiaDI;
    private static final SampleTabella perRecuperoDati = new SampleTabella();

    private SampleTabella(){
    }

    public SampleTabella(Sample plot, int nr) {
        setModificato(false);
        numeroRiga = nr;
        this.rilievo = plot;
        if(plot!=null){
            scorciatoiaPlace = rilievo.getPlace();
            scorciatoiaDI = rilievo.getDirectoryInfo();
        }
    }

    public Sample getPlot(){
        return rilievo;
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
                rilievo.getCommunity();
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
    /************************************************************************
     * @param op l'operazione WRITE non è implementata
     * @param colonna su cui effettuare l'operazione
     * @param o il dato da inserire
     * @return dipende dall'operazione richiesta
     ***********************************************************************/
    private Object eseguiOperazione(byte op, int colonna, Object o ){
        // Stato.debugLog.fine("op "+op+" su colonna "+colonna);
        switch(colonna){
        case 0:
            if(op==HEADER){     return "num";}
            else if(op==WRITE){ return null; }
            else if(op==READ) { return Integer.valueOf(numeroRiga);  }
            else {              return Integer.valueOf(40);}
        case 1:
            if(op==HEADER){     return "progetto";}
            else if(op==WRITE){ return null; }
            else if(op==READ) { return scorciatoiaDI.getContainerName();  }
            else {              return Integer.valueOf(80);}
        case 2:
            if(op==HEADER){     return "#progetto";}
            else if(op==WRITE){ return null; }
            else if(op==READ) { return scorciatoiaDI.getContainerSeqNo();  }
            else{ 				return Integer.valueOf(40);}
        case 3:
            if(op==HEADER){     return "id prog.";}
            else if(op==WRITE){ return null; }
            else if(op==READ) { return scorciatoiaDI.getContainerExternalId();  }
            else if(op==READ) { return Integer.valueOf(40);}
        case 4:
            if(op==HEADER){     return "subProj";}
            else if(op==WRITE){ return null; }
            else if(op==READ) { return scorciatoiaDI.getSubContainerName();  }
            else {              return Integer.valueOf(80);}
        case 5:
            if(op==HEADER){     return "#subProj";}
            else if(op==WRITE){ return null; }
            else if(op==READ) { return scorciatoiaDI.getSubContainerSeqNo();  }
            else {              return Integer.valueOf(40);}
        case 6:
            if(op==HEADER){     return "id sottop.";}
            else if(op==WRITE){ return null; }
            else if(op==READ) { return scorciatoiaDI.getSubContainerExternalId();  }
            else if(op==READ) { return Integer.valueOf(60);}
        case 7:
            if(op==HEADER){     return "legit";}
            else if(op==WRITE){ return null; }
            else if(op==READ) { return rilievo.getSurveyer();}
            else {              return Integer.valueOf(100);}
        case 8:
            if(op==HEADER){     return "località";}
            else if(op==WRITE){ return null; }
            else if(op==READ) { return scorciatoiaPlace.getName(); }
            else {              return Integer.valueOf(150);}
        case 9:
            if(op==HEADER){     return "comune";}
            else if(op==WRITE){ return null; }
            else if(op==READ) { return scorciatoiaPlace.getTown(); }
            else {              return Integer.valueOf(100);}
        default:
            return null;
        }
    }
    public static final int NUMERO_COLONNE = 10;

    /***************************************************************************
     * Converte nella rappresentazione standard del Plot
     **************************************************************************/
    public Sample toPlot(){
        return rilievo;
    }

    public int compareTo(OggettoPerTabella o){
        String dato1 = getColumn(colonnaDaOrdinare)!=null ? getColumn(colonnaDaOrdinare).toString() : null;
        String dato2 = o.getColumn(colonnaDaOrdinare)!=null ? o.getColumn(colonnaDaOrdinare).toString() : null;
        int risultato;

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