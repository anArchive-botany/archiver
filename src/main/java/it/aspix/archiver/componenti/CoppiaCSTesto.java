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

import it.aspix.sbd.introspection.ValoreEnumeratoDescritto;


/*****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ****************************************************************************/
public class CoppiaCSTesto implements CoppiaED{

    private String esterno;
    private String descrizione;

    public CoppiaCSTesto(ValoreEnumeratoDescritto v){
        this.esterno = v.enumerato;
        this.descrizione = v.descrizione;
    }

    public CoppiaCSTesto(String enumerato, String descrizione){
        this.esterno = enumerato;
        this.descrizione = descrizione;
    }

    /***************************************************************************
     * la rappresentazione di default è quella del client
     **************************************************************************/
    public String toString(){
        return descrizione;
    }

    /************************************************************************
     * @see it.aspix.nimrodel.elementi.CoppiaED#getClient()
     ***********************************************************************/
    public String getEsterno() {
        return esterno;
    }

    /************************************************************************
     * @see it.aspix.nimrodel.elementi.CoppiaED#getServer()
     ***********************************************************************/
    public String getDescrizione() {
        return descrizione;
    }
}