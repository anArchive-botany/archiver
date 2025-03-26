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
import it.aspix.archiver.archiver.PannelloDescrivibile;
import it.aspix.archiver.archiver.TopLevelEditor;
import it.aspix.archiver.componenti.CampoTestoUnicode;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.sbd.obj.Blob;
import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.OggettoSBD;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class BlobEditorSintetico extends PannelloDescrivibile implements TopLevelEditor{

    private static final long serialVersionUID = 1L;
    
    GridBagLayout lThis = new GridBagLayout();
    
    JLabel              eContenitore = new JLabel();
    CampoTestoUnicode   contenitore = new CampoTestoUnicode();
    JLabel              eProgrContenitore = new JLabel();
    JTextField          progrContenitore = new JTextField();
    
    JLabel              eCollezione = new JLabel();
    CampoTestoUnicode   collezione = new CampoTestoUnicode();
    JLabel              eProgrCollezione = new JLabel();
    CampoTestoUnicode   progrCollezione = new CampoTestoUnicode();
    
    JLabel              eNome = new JLabel();
    CampoTestoUnicode   nome = new CampoTestoUnicode();
    JLabel              eDescrizione = new JLabel();
    CampoTestoUnicode   descrizione = new CampoTestoUnicode();
    JLabel              eAutore = new JLabel();
    CampoTestoUnicode   autore = new CampoTestoUnicode();
    
    public BlobEditorSintetico(){
        super();
        this.setLayout(lThis);
        this.setOpaque(false);
        eContenitore.setText("contenitore:");
        eProgrContenitore.setText("#");
        eCollezione.setText("cartella:");
        eProgrCollezione.setText("#");
        eNome.setText("nome:");
        eDescrizione.setText("descrizione:");
        eAutore.setText("autore:");
        
        // -------------------- inserimento nei pannelli --------------------
        this.add(eContenitore,      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(contenitore,       new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 100, 0));
        this.add(eProgrContenitore, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(progrContenitore,  new GridBagConstraints(3, 0, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 60, 0));
        
        this.add(eCollezione,      new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(collezione,       new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eProgrCollezione, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(progrCollezione,  new GridBagConstraints(3, 1, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        
        this.add(eNome,         new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(nome,          new GridBagConstraints(1, 2, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eDescrizione,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(descrizione,   new GridBagConstraints(1, 3, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        this.add(eAutore,       new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        this.add(autore,        new GridBagConstraints(1, 4, 3, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
    
        JPanel spreco = new JPanel(); 
        this.add(spreco,new GridBagConstraints(0, 5, 4, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, CostantiGUI.insetsSpreco, 0, 0));
        spreco.setOpaque(false);
        
        contenitore.setText(Proprieta.recupera("blob.database"));
        setTipoContenuto(it.aspix.sbd.obj.Blob.class);
    }
    

    /************************************************************************
     * @return il blob rappresentato in questo oggetto
     ***********************************************************************/
    public Blob getBlob(){
        Blob blob = new Blob();
        DirectoryInfo di = new DirectoryInfo();
        blob.setDirectoryInfo(di);
        
        di.setContainerName(contenitore.getText());
        di.setContainerSeqNo(progrContenitore.getText());
        di.setSubContainerName(collezione.getText());
        di.setSubContainerSeqNo(progrCollezione.getText());
        blob.setName(nome.getText());
        blob.setDescription(descrizione.getText());
        blob.setAuthor(autore.getText());
        return blob;
    }
    
    /************************************************************************
     * @param b l'ìoggetto da visualizzare
     ***********************************************************************/
    public void setBlob(Blob blob){
        DirectoryInfo di = blob.getDirectoryInfo();
        
        contenitore.setText(di.getContainerName());
        progrContenitore.setText(di.getContainerSeqNo());
        collezione.setText(di.getSubContainerName());
        progrCollezione.setText(di.getSubContainerSeqNo());
        nome.setText(blob.getName());
        descrizione.setText(blob.getDescription());
        autore.setText(blob.getAuthor());
    }
    
    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
    public void setOggettoSBD(OggettoSBD oggetto) {
        if(oggetto instanceof Blob){
            setBlob((Blob)oggetto);
        }else{
            throw new IllegalArgumentException("Questo oggetto può visualizzare soltanto Blob, hai passato un "+oggetto.getClass().getCanonicalName());
        }
    }

    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
    public OggettoSBD getOggettoSBD() {
        return this.getBlob();
    }

    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
    public boolean isVisualizzabile(Object oggetto) {
        return oggetto instanceof Blob;
    }
    
    /** @see it.aspix.archiver.archiver.TopLevelEditor **/
	public String getSuggerimenti() {
		return null;
	}
    
    public String toString(){
        return "blob";
    }
}
