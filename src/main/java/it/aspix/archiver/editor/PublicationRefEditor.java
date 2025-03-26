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
package it.aspix.archiver.editor;

import it.aspix.archiver.CostantiGUI;
import it.aspix.sbd.obj.PublicationRef;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class PublicationRefEditor extends JPanel{

    private static final long serialVersionUID = 1L;
    
    LayoutManager lPrincipale = new GridBagLayout();
    
    JLabel eRiferimento = new JLabel();
    JTextField riferimento  = new JTextField();
    JLabel eCitazione = new JLabel();
    JTextField citazione  = new JTextField();
    JLabel eTavola = new JLabel();
    JTextField tavola  = new JTextField();
    JLabel eNumero = new JLabel();
    JTextField numero  = new JTextField();
    
    public PublicationRefEditor(){
    	citazione.setName("publicationRef.citation");
        eRiferimento.setText("riferimento:");
        eCitazione.setText("citazione:");
        eTavola.setText("tavola:");
        eNumero.setText("numero:");
        
        this.setLayout(lPrincipale);
        this.add(eRiferimento,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(riferimento,   new GridBagConstraints(1, 0, 1, 1, 0.3, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eCitazione,    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(citazione,     new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eTavola,       new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(tavola,        new GridBagConstraints(5, 0, 1, 1, 0.3, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eNumero,       new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(numero,        new GridBagConstraints(7, 0, 1, 1, 0.3, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
    }

    /************************************************************************
     * @param mostra se true il campo editor viene mostrato altrimenti no
     ***********************************************************************/
    public void showRiferimento(boolean mostra){
        eRiferimento.setVisible(mostra);
        riferimento.setVisible(mostra);
    }
    
    /************************************************************************
     * @param pr l'oggetto da visualizzare
     ***********************************************************************/
    public void setPublicationRef(PublicationRef pr){
        if(pr == null){
        	throw new IllegalArgumentException("PublicationRef non può essere null");
        }
        riferimento.setText(pr.getReference());
        citazione.setText(pr.getCitation());
        tavola.setText(pr.getTable());
        numero.setText(pr.getNumber());
    }
    
    /************************************************************************
     * @return l'oggetto rappresentato dfa questo editor
     ***********************************************************************/
    public PublicationRef getPublicationRef(){
        PublicationRef risposta = new PublicationRef();
        risposta.setReference(riferimento.getText());
        risposta.setCitation(citazione.getText());
        risposta.setTable(tavola.getText());
        risposta.setNumber(numero.getText());
        return risposta;
    }
}
