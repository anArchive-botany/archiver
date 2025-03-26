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
import it.aspix.archiver.componenti.CampoTestoUnicode;
import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.PublicationRef;
import it.aspix.sbd.obj.SpecieSpecification;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*****************************************************************************
 * Permette di inserire i dati per la ricerca di un nome di specie
 * @param Edoardo Panfili, studio Aspix
 ****************************************************************************/
public class SpecieSpecificationEditorSintetico extends SpecieSpecificationEditor{

    private static final long serialVersionUID = 1L;

    GridBagLayout lThis = new GridBagLayout();
    
    JLabel              eContainer = new JLabel();
    CampoTestoUnicode   container = new CampoTestoUnicode();
    JLabel              eSubcontainer = new JLabel();
    CampoTestoUnicode   subcontainer = new CampoTestoUnicode();
    JLabel              eFamiglia = new JLabel();
    CampoTestoUnicode   famiglia = new CampoTestoUnicode();
    JLabel              eNome = new JLabel();
    CampoTestoUnicode   nome = new CampoTestoUnicode();
    JLabel              eLetteratura = new JLabel();
    CampoTestoUnicode   letteratura = new CampoTestoUnicode();
    JCheckBox			ricercaSpecieAnaloghe = new JCheckBox();
    
    public SpecieSpecificationEditorSintetico(){
        super();
        this.setLayout(lThis);
        eContainer.setText("check-list:");
        eSubcontainer.setText("gruppo:");
        eFamiglia.setText("famiglia:");
        eNome.setText("nome:");
        eLetteratura.setText("letteratura:");
        ricercaSpecieAnaloghe = new JCheckBox("Ricerca specie equivalenti");
        
        this.add(eContainer,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(container,    new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 100, 0));
        this.add(eSubcontainer,new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(subcontainer, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eFamiglia,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(famiglia,     new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eNome,        new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(nome,         new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eLetteratura, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(letteratura,  new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(ricercaSpecieAnaloghe,  new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        JPanel spreco = new JPanel(); 
        this.add(spreco,new GridBagConstraints(0, 5, 4, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsSpreco, 0, 0));
        spreco.setOpaque(false);
        ricercaSpecieAnaloghe.setSelected(true);
        ricercaSpecieAnaloghe.setOpaque(false);
    }

    @Override
    /************************************************************************
     * @see it.aspix.entwash.editor.SpecieSpecificationEditor#getSpecieSpecification()
     ***********************************************************************/
    public SpecieSpecification getSpecieSpecification() {
        SpecieSpecification specie = new SpecieSpecification();
        DirectoryInfo di = new DirectoryInfo();
        PublicationRef pr = new PublicationRef();
        String nom  = nome.getText();
        int posizioneSpazio1=nom.indexOf(" ");
        int posizioneSpazio2=nom.indexOf(" ",posizioneSpazio1+1);
        
        di.setContainerName(container.getText());
        di.setSubContainerName(subcontainer.getText());
        specie.setDirectoryInfo(di);
        specie.setFamily(famiglia.getText());
        if(posizioneSpazio1==-1){
            // nella casella nome c'e' una sola parola
            specie.setGenusName(nom);
        }else{
            // nella casella nome ci sono almeno due parole
            if(posizioneSpazio2==-1){
                // ci sono esattamente due parole
                specie.setGenusName(nom.substring(0,posizioneSpazio1));
                specie.setSpecieName(nom.substring(posizioneSpazio1+1));
            }else{
                // ci sono piu' di due parole, prendo solo le prime due
                specie.setGenusName(nom.substring(0,posizioneSpazio1));
                specie.setSpecieName(nom.substring(posizioneSpazio1+1,posizioneSpazio2));
            }
        }
        pr.setCitation(letteratura.getText());
        specie.setPublicationRef(pr);
        return specie;
    }

    @Override
    /************************************************************************
     * @see it.aspix.entwash.editor.SpecieSpecificationEditor#setSpecieSpecification(it.aspix.sbd.obj.SpecieSpecification)
     ***********************************************************************/
    public void setSpecieSpecification(SpecieSpecification specie) {
        container.setText(specie.getDirectoryInfo().getContainerName());
        subcontainer.setText(specie.getDirectoryInfo().getSubContainerName());
        famiglia.setText(specie.getFamily());
        nome.setText(specie.getGenusName()+" "+specie.getSpecieName());
        letteratura.setText(specie.getPublicationRef().getCitation());
    }

    @Override
    public String toString() {
        return "specie";
    }
    
    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
	public String getSuggerimenti() {
		return ricercaSpecieAnaloghe.isSelected() ? null : "noEquivalentObjects";
	}

}
