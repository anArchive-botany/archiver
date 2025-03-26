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
package it.aspix.archiver.archiver;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Blob;
import it.aspix.sbd.obj.OggettoSBD;
import it.aspix.sbd.obj.Sample;
import it.aspix.sbd.obj.SimpleBotanicalData;
import it.aspix.sbd.obj.SpecieSpecification;
import it.aspix.sbd.obj.Specimen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/************************************************************************************************
 * Incapsula un editor consentendo di inviare i dati al server per una ricerca
 * 
 * @author Edoardo Panfili, studio Aspix
 ***********************************************************************************************/
public class GestoreRicerca extends PannelloDescrivibile{
    
    private static final long serialVersionUID = 1L;
    
    GridBagLayout lPannelloAzioni = new GridBagLayout();
    JPanel pannelloAzioni = new JPanel(lPannelloAzioni);
    JButton cerca = new JButton("cerca");
    
    TopLevelEditor editorContenuto;

    public String toString(){
        return editorContenuto.toString();
    }
    
    public GestoreRicerca(TopLevelEditor tle){
        editorContenuto = tle;
        PannelloDescrivibile pd = (PannelloDescrivibile)tle;
        this.setTipoContenuto(pd.getTipoContenuto());
        this.setLayout(new BorderLayout());
        
        this.add(pannelloAzioni, BorderLayout.SOUTH);
        pannelloAzioni.add(cerca,      new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE, CostantiGUI.insetsAzioneInBarra, 0, 0));
        this.add((Component) editorContenuto, BorderLayout.CENTER);
        pannelloAzioni.setOpaque(false);
        ActionListener ascoltatoreCerca = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cercaDaPatern();
            }
        };
        cerca.addActionListener(ascoltatoreCerca);
        UtilitaGui.aggiungiActionListenerATutti(pd, ascoltatoreCerca);
    }
    
    public void cercaDaPatern(){
        try {
            OggettoSBD trovati[] = null;
            OggettoSBD pattern = editorContenuto.getOggettoSBD();
            // SimpleBotanicalData risposta = Stato.comunicatore.cerca(pattern, editorContenuto.getSuggerimenti());
            SimpleBotanicalData risposta = Stato.comunicatore.cerca(pattern, null);
            Dispatcher.consegnaMessaggi(this, risposta);
            // ora bisogna capire cosa abbiamo cercato!
            if(pattern instanceof Specimen){
                trovati = risposta.getSpecimen();
            }else if(pattern instanceof Sample){
                trovati = risposta.getSample();
            }else if(pattern instanceof SpecieSpecification){
                trovati = risposta.getSpecieSpecification();
            }else if(pattern instanceof Blob){
                trovati = risposta.getBlob();
            }else{
                Dispatcher.consegna(this, new Exception("Non sono in grado di recuperare gli oggetti di ricrca di tipo "+pattern.getClass().getCanonicalName()));
            }
            Dispatcher.consegna(this, trovati, pattern);
        } catch (Exception e) {
            Dispatcher.consegna(this, e);
        }
    }
}
