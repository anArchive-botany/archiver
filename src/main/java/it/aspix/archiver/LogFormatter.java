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
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/****************************************************************************
 * @author edoardo for aspix (aspix.it)
 ***************************************************************************/
public class LogFormatter extends Formatter {

    private String daAbbreviare = "it.aspix.entwash";
    private String abbreviareIn = "";
    
    private int fixedLength = 60;

    public String formatMessage(LogRecord record){
        return super.formatMessage(record);
    }

    public String format(LogRecord record) {
        StringBuffer msg = new StringBuffer();
        String nome;
        if(record.getThrown()==null){
            nome = record.getSourceClassName().replaceAll(daAbbreviare, abbreviareIn) + "#" + record.getSourceMethodName();
            if(fixedLength!=-1){
                nome = (nome+"                                                        ").substring(0,fixedLength);
            }
            msg.append('[');
            msg.append(record.getLevel().intValue()/100);
            msg.append("][");
            msg.append(nome);
            msg.append("] ");
            msg.append(record.getMessage());
            msg.append('\n');
        }else{
            msg= new StringBuffer(" ["+record.getSourceClassName()+"#"+record.getSourceMethodName()+"] " + record.getMessage()+"\n");
            msg.append(eccezioneToString(record.getThrown()));
        }   
        return msg.toString();
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