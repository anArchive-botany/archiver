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
package it.aspix.archiver.nucleo;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/****************************************************************************
 * Questa classe non fa altro che misurare il numero di bytes letti, il
 * lavoro vero viene svolto dalla classe incapsulata.
 * @author Edoardo Panfili, studio Aspix
 * @Deprecated TODO con il passaggio ad Apache HttpClient probabilmente è da rimuovere
 ***************************************************************************/
public class InputStreamMisuratore extends FilterInputStream{

    private int counter;
    
    protected InputStreamMisuratore(InputStream in) {
        super(in);
        counter = 0;
    }
    
    public int read() throws IOException{
        int letto = super.read();
        if(letto>-1)
            counter++;
        return letto;
    }
    
    public int  read(byte[] b) throws IOException{
        int letti = super.read(b);
        if(letti>-1)
            counter+=letti;
        return letti;
    }

    public int read(byte[] b, int off, int len) throws IOException{
        int letti = super.read(b,off,len);
        if(letti>-1)
            counter+=letti;
        return letti;
    }
    
    public int getbytesLetti(){
        return counter;
    }

}
