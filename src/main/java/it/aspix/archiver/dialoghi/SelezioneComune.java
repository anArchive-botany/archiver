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
package it.aspix.archiver.dialoghi;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Place;

/*****************************************************************************
 * chiede di selezionare il comune da una lista
 * @author edoarod Panfili, studio Aspix
 ****************************************************************************/
public class SelezioneComune extends JDialog {

    private static final long serialVersionUID = 1L;

    DefaultListModel<String> contenutoLista = new DefaultListModel<>();

    JPanel jPanel1 = new JPanel();
    JScrollPane scrollAlternative = new JScrollPane();
    JList<String> listaComune = new JList<>();
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel pannelloPulsanti = new JPanel();
    JButton ok = new JButton();
    JButton annulla = new JButton();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    Border border1;

    Place[] alternative;
    /**************************************************************************
     * aggiunge gli elementi alle alternative
     *************************************************************************/
    public void setAlternative(Place[] v){
        for(int i=0;i<v.length;i++){
            if(Proprieta.isTrue("suggerimentiGeograficiEstesi")){
                contenutoLista.addElement(v[i].toString()+
                        (v[i].getMacroPlace()!=null ? v[i].getMacroPlace() : "") +
                        " ["+
                        (v[i].getMainGrid()!=null ? v[i].getMainGrid() : "N/D") +
                        ", "+
                        (v[i].getProtectedAreaName()!=null ? v[i].getProtectedAreaName() : "N/D") +
                        "]");
            }else{
                contenutoLista.addElement(v[i].toString()+ (v[i].getMacroPlace()!=null ? v[i].getMacroPlace() : "") );
            }
        }
        alternative=v;
    }

    int selezionato=-1;
    /**************************************************************************
     * @return la localita selezionata o NULL
     *************************************************************************/
    public Place getSelezionato(){
        if(selezionato==-1)
            return null;
        return alternative[selezionato];
    }

    public SelezioneComune() {
        this.setTitle("Seleziona un comune");
        border1 = BorderFactory.createEmptyBorder(5,5,5,5);
        jPanel1.setLayout(borderLayout1);
        ok.setText("OK");
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok_actionPerformed();
            }
        });
        annulla.setText("Annulla");
        annulla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                annulla_actionPerformed();
            }
        });
        pannelloPulsanti.setLayout(gridBagLayout1);
        jPanel1.setBorder(border1);
        listaComune.setModel(contenutoLista);
        listaComune.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                listaComune_mouseClicked(e);
            }
        });
        this.setModal(true);
        this.getContentPane().add(jPanel1, BorderLayout.CENTER);
        jPanel1.add(scrollAlternative, BorderLayout.CENTER);
        jPanel1.add(pannelloPulsanti, BorderLayout.SOUTH);
        pannelloPulsanti.add(annulla, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0 ,GridBagConstraints.EAST, GridBagConstraints.NONE,   CostantiGUI.insetsAzioneInBarraUltimaDiGruppo, 0, 0));
        pannelloPulsanti.add(ok,      new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0 ,GridBagConstraints.CENTER, GridBagConstraints.NONE, CostantiGUI.insetsAzioneInBarra, 0, 0));
        scrollAlternative.getViewport().add(listaComune, null);
        this.setSize(400,200);
        this.validate();
    }

    void ok_actionPerformed() {
        selezionato=listaComune.getSelectedIndex();
        Stato.debugLog.fine("voce selezionata:"+selezionato);
        this.setVisible(false);
    }

    void annulla_actionPerformed() {
        selezionato=-1;
        this.setVisible(false);
    }

    void listaComune_mouseClicked(MouseEvent e) {
        Stato.debugLog.fine("numero click:"+e.getClickCount());
        if(e.getClickCount()==2)
            this.ok_actionPerformed();
    }
}