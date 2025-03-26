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
package it.aspix.archiver.eventi;

/*****************************************************************************
 * Sollevato dal gestore delle preferenze ogni volta che viene modificata
 * una proprietà
 * @author Edoardo Panfili
 * @version 0.0
 ****************************************************************************/
public class ProprietaCambiataEvent {
  private String nomeProprieta;
  private String valoreProprieta;
  private String vecchioValore;

  /***************************************************************************
   * @param nome della proprietà che è cambiata
   * @param valore il valore assunto dalla proprietà
   * @param vecchioValore che la proprietà aveva prima della variazione
   **************************************************************************/
  public ProprietaCambiataEvent(String nome, String valore, String vecchioValore){
    this.nomeProprieta=nome;
    this.valoreProprieta=valore;
    this.vecchioValore=vecchioValore;
  }

  /***************************************************************************
   * @return il nome della propriet� che � cambiata
   **************************************************************************/
  public String getNomeProprieta(){
    return nomeProprieta;
  }

  /***************************************************************************
   * @return il valore della propriet� cambiata
   **************************************************************************/
  public String getValoreProprieta(){
    return valoreProprieta;
  }

  /***************************************************************************
   * @return il valore della propriet� prima del cambiamento
   **************************************************************************/
  public String getVecchioValoreProprieta(){
    return vecchioValore;
  }
}