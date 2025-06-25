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

import it.aspix.sbd.obj.Blob;
import it.aspix.sbd.obj.DirectoryInfo;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class BlobTabella extends OggettoPerTabella{
    //per impostare i modelli di ordinamento
    public static int colonnaDaOrdinare;
    public static int verso;

    public int numeroRiga;
    private Blob blob;
    private DirectoryInfo scorciatoiaDI;
    private static final BlobTabella perRecuperoDati = new BlobTabella();

    private BlobTabella(){
    }

    public BlobTabella(Blob blob, int nr) {
        setModificato(false);
        numeroRiga = nr;
        this.blob = blob;
        if(blob!=null){
            scorciatoiaDI = blob.getDirectoryInfo();
        }
    }

    public Blob getBlob(){
        return blob;
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
                blob.getName();
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
    	// gli elese sono il caso "WIDTH", la larhghezza della colonna
        switch(colonna){
        case 0:
            if(op==HEADER){     return "num";}
            else if(op==WRITE){ /* non modificabile */; return null; }
            else if(op==READ) { return new FlagModificato(isModificato(),numeroRiga);  }
            else {              return Integer.valueOf(40);}
        case 1:
            if(op==HEADER){     return "contenitore";}
            else if(op==WRITE){ scorciatoiaDI.setContainerName(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaDI.getContainerName();  }
            else {              return Integer.valueOf(80);}
        case 2:
            if(op==HEADER){     return "#contenitore";}
            else if(op==WRITE){ scorciatoiaDI.setContainerSeqNo(o.toString()); return null; }
            else if(op==READ) { return scorciatoiaDI.getContainerSeqNo();  }
            else { 				return Integer.valueOf(40);}
        case 3:
            if(op==HEADER){     return "id contenitore";}
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
            if(op==HEADER){     return "nome";}
            else if(op==WRITE){ blob.setName(o.toString()) ; return null; }
            else if(op==READ) { return blob.getName();  }
            else {              return Integer.valueOf(250);}
        case 8:
            if(op==HEADER){     return "descrizione";}
            else if(op==WRITE){ blob.setDescription(o.toString()); return null; }
            else if(op==READ) { return blob.getDescription(); }
            else {              return Integer.valueOf(250);}
        case 9:
            if(op==HEADER){     return "autore";}
            else if(op==WRITE){ blob.setAuthor(o.toString()); return null; }
            else if(op==READ) { return blob.getAuthor(); }
            else {              return Integer.valueOf(150);}
        case 10:
            if(op==HEADER){     return "data";} // TODO: qui serve l'editor delle date
            else if(op==WRITE){ blob.setDate(o.toString()); return null; }
            else if(op==READ) { return blob.getDate();}
            else {              return Integer.valueOf(100);}
        case 11:
            if(op==HEADER){     return "tipo mime";}
            else if(op==WRITE){ blob.setMimeType( o.toString() ); return null; }
            else if(op==READ) { return blob.getMimeType(); }
            else {              return Integer.valueOf(100);}
        default:
            return null;
        }
    }
    public static final int NUMERO_COLONNE = 12;

    /***************************************************************************
     * Converte nella rappresentazione standard del cartellino
     **************************************************************************/
    public Blob toBlob(){
        return blob;
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