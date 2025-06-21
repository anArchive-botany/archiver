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

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import it.aspix.archiver.eventi.ProprietaCambiataEvent;
import it.aspix.archiver.eventi.ProprietaCambiataListener;
import it.aspix.archiver.nucleo.Proprieta;

/****************************************************************************
 * Questa classe si occupa di filtrare i messaggi di log che vanno
 * visualizzati.
 *
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class FiltroLog implements Filter, ProprietaCambiataListener{

    public static java.util.Hashtable<String,Level> debugLevel;
    static {
    	debugLevel=new java.util.Hashtable<String,Level>();
    }

    /************************************************************************
     * il costruttore (non vengono costruiti molti oggetti di questo tipo)
     * reinizializza i livelli e poi si registra come ascoltatore dei cambiamenti
     ***********************************************************************/
    public FiltroLog(){
    	initLevels();
    	Proprieta.addProprietaCambiataListener(this);
    }

    /************************************************************************
     * intercetta i cambiamenti delle proprietà
     ***********************************************************************/
	public void proprietaCambiata(ProprietaCambiataEvent l) {
		if(l.getNomeProprieta().equals("devel.levelALL")){
			initLevels();
		}
	}

	/************************************************************************
	 * Ricostruisce la tabella dei livelli di log
	 ***********************************************************************/
    private static void initLevels(){
    	String valore = null;
    	try{
    		valore = Proprieta.recupera("devel.levelALL");
    	}catch(Error e){
    		; // do nothing, questa classe viene istanziata prima che vengano caricate le proprietà
    	}
    	if(valore!=null && valore.length()>0){
    		String elencoClassi[] = valore.split(",");
    		for(String i: elencoClassi){
    			debugLevel.put(i.trim(), Level.ALL);
    		}
    	}
    }

    public boolean isLoggable(LogRecord record) {
    	if(record.getLevel().intValue()>=Level.SEVERE.intValue() || record.getThrown()!=null ){
    		return true;
    	}
        Object dl = debugLevel.get(record.getSourceClassName());
        if(dl==null)
            return false;
        Level l = (Level) dl;
        if(record.getLevel().intValue() >= l.intValue())
            return true;
        return false;
    }

}
