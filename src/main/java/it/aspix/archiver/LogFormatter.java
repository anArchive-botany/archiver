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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.StackWalker.StackFrame;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/****************************************************************************
 * @author edoardo for aspix (aspix.it)
 ***************************************************************************/
public class LogFormatter extends Formatter {

    private String daAbbreviare = "it.aspix.archiver";
    private String abbreviareIn = "…";

    private static final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private int fixedLength = 70;

    public String formatMessage(LogRecord record){
        return super.formatMessage(record);
    }

    public String format(LogRecord record) {
        String messaggio;
        String nome;
        int n = record.getLevel().intValue()/100;
        if(record.getThrown()==null){
        	// trova il primo pezzo utile
            StackFrame caller = walker.walk(frames -> frames
            		.filter(frame -> !frame.getClassName().startsWith("java.util.logging"))
	                .filter(frame -> !frame.getClassName().equals(LogFormatter.class.getName()))
	                .findFirst()
	                .orElse(null)
	        );
            // se viene formattato così Eclipse permette di cliccare direttamente per
            // andare alla linea di codice giusta
            String posizione = caller != null
                    ? String.format("(%s:%d)", caller.getFileName(), caller.getLineNumber())
                    : "(unknown location)";

            nome = record.getSourceClassName().replaceAll(daAbbreviare, abbreviareIn) +
            		"#" + record.getSourceMethodName()+
            		":" + posizione;
            if(fixedLength!=-1){
                nome = (nome+"                                                        ").substring(0,fixedLength);
            }
            messaggio = String.format("[%2d][%s] %s\n",
            		n, // il livello
            		nome, // la descrizione
            		record.getMessage()
            );
        }else{
        	messaggio = String.format("[%s#%s] %s\n%s",
        			record.getSourceClassName(),
        			record.getSourceMethodName(),
        			record.getMessage(),
        			eccezioneToString(record.getThrown())
        	);
        }
        return messaggio;
    }

    private static String eccezioneToString(Throwable ex){
        String msg;
        try{
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            pw.close();
            sw.close();
            msg=sw.toString();
        }catch(Exception e){
            msg="ERRORE DURANTE LA CONVERSIONE DELL'ECCEZIONE";
        }
        return msg;
    }
}