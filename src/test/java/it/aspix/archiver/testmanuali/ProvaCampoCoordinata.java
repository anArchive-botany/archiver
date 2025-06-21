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
package it.aspix.archiver.testmanuali;

import javax.swing.JFrame;

import it.aspix.archiver.componenti.CampoCoordinata;
import it.aspix.archiver.nucleo.Proprieta;

/****************************************************************************
 * Test per il riconoscimento dei dati nella casella CampoCoordinata
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class ProvaCampoCoordinata {
    public static void main(String args[]){
        CampoCoordinata cc = new CampoCoordinata(CampoCoordinata.Asse.LATITUDINE);
        Proprieta.caricaProprieta();
        
        String batteria[] = {"18 12.78 Nord", "17° 15' 78\" Sud", "34.8876 N", "39° 23' 38.30” N", "16° 35' 56.75” S"};
        // cc.setTextNoFormat("15 36 18 N");
        for(int i=0 ; i<batteria.length ; i++){
            cc.setTextNoFormat(batteria[i]);
            System.out.println(batteria[i]+"->"+cc.getText());
        }
        // test in ambiente swing
        JFrame x = new JFrame();
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        x.getContentPane().add(cc);
        x.setVisible(true);
        cc.setText("12.887653");
    }
}
