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
import it.aspix.archiver.archiver.ManagerSuggerimenti;
import it.aspix.archiver.archiver.TopLevelEditor;
import it.aspix.archiver.componenti.CampoTestoUnicode;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.Place;
import it.aspix.sbd.obj.SpecieRef;
import it.aspix.sbd.obj.Specimen;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class SpecimenEditorSintetico extends SpecimenEditor implements TopLevelEditor{

    private static final long serialVersionUID = 1L;

    GridBagLayout lThis = new GridBagLayout();
    
    JLabel              eErbario = new JLabel();
    CampoTestoUnicode   erbario = new CampoTestoUnicode();
    JLabel              eProgrErbario = new JLabel();
    JTextField          progrErbario = new JTextField();
    
    JLabel              eCollezione = new JLabel();
    CampoTestoUnicode   collezione = new CampoTestoUnicode();
    JLabel              eProgrCollezione = new JLabel();
    CampoTestoUnicode   progrCollezione = new CampoTestoUnicode();
    
    JLabel              eSpecie = new JLabel();
    CampoTestoUnicode   specie = new CampoTestoUnicode();
    JLabel              eLegit = new JLabel();
    CampoTestoUnicode   legit = new CampoTestoUnicode();
    JLabel              eLocalita = new JLabel();
    CampoTestoUnicode   localita = new CampoTestoUnicode();
    
    public SpecimenEditorSintetico(){
        super();
        this.setLayout(lThis);
        eErbario.setText("erbario:");
        eProgrErbario.setText("#");
        eCollezione.setText("collezione:");
        eProgrCollezione.setText("#");
        eSpecie.setText("specie:");
        eLegit.setText("legit:");
        eLocalita.setText("localit\u00e0:");
        
        this.add(eErbario,      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(erbario,       new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 100, 0));
        this.add(eProgrErbario, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(progrErbario,  new GridBagConstraints(3, 0, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 60, 0));
        
        this.add(eCollezione,      new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(collezione,       new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eProgrCollezione, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(progrCollezione,  new GridBagConstraints(3, 1, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        
        this.add(eSpecie,     new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(specie,      new GridBagConstraints(1, 2, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eLegit,      new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(legit,       new GridBagConstraints(1, 3, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eLocalita,   new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(localita,    new GridBagConstraints(1, 4, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        
        JPanel spreco = new JPanel(); 
        this.add(spreco,new GridBagConstraints(0, 5, 4, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsSpreco, 0, 0));
        spreco.setOpaque(false);
    
        erbario.setText(Proprieta.recupera("herbaria.database"));
        ManagerSuggerimenti.check(this);
    }
    
    @Override
    public Specimen getSpecimen() {
        Specimen cartellino = new Specimen();
        Place loc = new Place();
        DirectoryInfo di = new DirectoryInfo();
        cartellino.setDirectoryInfo(di);
        cartellino.setPlace(loc);
        
        di.setContainerName(erbario.getText());
        di.setContainerSeqNo(progrErbario.getText());
        di.setSubContainerName(collezione.getText());
        di.setSubContainerSeqNo(progrCollezione.getText());
        cartellino.setSpecieRef(new SpecieRef(specie.getText(), null));
        cartellino.setLegitName(legit.getText());
        loc.setName(localita.getText());
        return cartellino;
    }

    @Override
    public void setSpecimen(Specimen cartellino) {
    	DirectoryInfo di = cartellino.getDirectoryInfo();
    	erbario.setText(di.getContainerName());
    	progrErbario.setText(di.getContainerSeqNo());
    	collezione.setText(di.getSubContainerName());
    	progrErbario.setText(di.getSubContainerSeqNo());
        specie.setText(cartellino.getSpecieRef().getName());
    	legit.setText(cartellino.getLegitName());
    	localita.setText(cartellino.getPlace().getName());
    }

    @Override
    public String toString() {
        return "cartellini";
    }

    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
	public String getSuggerimenti() {
		return null;
	}
}
