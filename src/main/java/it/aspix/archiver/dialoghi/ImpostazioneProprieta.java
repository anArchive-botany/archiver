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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.nucleo.Proprieta;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class ImpostazioneProprieta extends JDialog{
    
    private static final long serialVersionUID = 1L;
    
    DefaultMutableTreeNode  radice = new DefaultMutableTreeNode("RADICE");
    DefaultTreeModel        modelloAlbero = new DefaultTreeModel(radice);
    JTree                   albero = new JTree(modelloAlbero);
    JTextArea				areaMessaggi = new JTextArea(); // eventuali messaggi di errore;
    JTextArea               areaDescrizione = new JTextArea(); // compare la descrizione della proprieta da impostare
    JPanel                  areaValore = new JPanel(new GridBagLayout()); // gli input per impostare la proprieta
    String                  proprietaInEdit = null; // impostata al momento della selezione per permettere agli ascoltatori di impostarene il valore
    JPanel                  spreco = new JPanel(); // qui solo per comodità viene usato in diversi punti
    
    public ImpostazioneProprieta(){
        JPanel principale = new JPanel(new BorderLayout(4,4));
        JPanel pannelloEdit = new JPanel(new BorderLayout());
        JScrollPane scrollAlbero = new JScrollPane(albero);
        JScrollPane scrollAreaMessaggi = new JScrollPane(areaMessaggi);
        JButton ok = new JButton("ok");
        aggiornaMessaggio();
        
        // ---------- layout ----------
        this.getContentPane().add(principale);
        principale.add(scrollAreaMessaggi, BorderLayout.NORTH);
        principale.add(scrollAlbero, BorderLayout.WEST);
        principale.add(pannelloEdit, BorderLayout.CENTER);
        principale.add(ok, BorderLayout.SOUTH);
        pannelloEdit.add(areaDescrizione, BorderLayout.NORTH);
        pannelloEdit.add(areaValore, BorderLayout.CENTER);
        
        // ---------- costruisco l'albero delle proprieta ----------
        DefaultMutableTreeNode nodoGruppo;
        for(int i=0 ; i<Proprieta.gruppi.size() ; i++){
            nodoGruppo = new DefaultMutableTreeNode(Proprieta.gruppi.get(i)); 
            radice.add(nodoGruppo);
            for(int j=0 ; j<Proprieta.gruppi.get(i).descrizioniProprieta.size() ; j++){
                if(!(Proprieta.gruppi.get(i).descrizioniProprieta.get(j).nascondi)){
                    nodoGruppo.add(new DefaultMutableTreeNode(Proprieta.gruppi.get(i).descrizioniProprieta.get(j)));
                }
            }
        }
        
        // ---------- ascoltatori ----------
        albero.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                cambiataSelezione(e);
            }
        });
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ImpostazioneProprieta.this.dispose();
            }
        });
        
        // ---------- proprietà degli oggetti ----------
        UtilitaGui.expandAll(albero, new TreePath(radice), true, 1);
        this.setTitle("Proprieta");
        principale.setBorder(CostantiGUI.bordoSpaziatoreTopLevelEditor);
        scrollAlbero.setPreferredSize(new Dimension(220,350));
        spreco.setOpaque(false);
        areaValore.setOpaque(false);
        areaDescrizione.setEditable(false);
        areaDescrizione.setPreferredSize(new Dimension(400,100));
        areaDescrizione.setWrapStyleWord(true);
        areaDescrizione.setLineWrap(true);
        areaDescrizione.setBorder(CostantiGUI.bordoGruppoIsolato);
        areaDescrizione.setOpaque(false);
        
        areaMessaggi.setEditable(false);
        areaMessaggi.setPreferredSize(new Dimension(400,50));
        areaMessaggi.setWrapStyleWord(true);
        areaMessaggi.setLineWrap(true);
        areaMessaggi.setBorder(CostantiGUI.bordoGruppoIsolato);
        areaMessaggi.setOpaque(false);
        areaMessaggi.setForeground(CostantiGUI.coloreAttenzione);
        this.pack();
        this.setModal(true);
    }
    
    /************************************************************************
     * Imposta l'interfaccia per la gestione di una nuova proprieta
     * @param e l'evento da gestire
     ***********************************************************************/
    void cambiataSelezione(TreeSelectionEvent e){
        TreePath cammino = e.getNewLeadSelectionPath();
        Object[] passi = cammino.getPath();
        
        areaValore.removeAll();
        areaValore.updateUI();
        if(passi.length==3){
            // ha selezionato una proprieta, con 2 avrebbe selezionato un gruppo
            Proprieta.GruppoDescrizioneProprieta g = (Proprieta.GruppoDescrizioneProprieta) ( (DefaultMutableTreeNode)passi[1]).getUserObject();
            Proprieta.DescrizioneProprieta dg = (Proprieta.DescrizioneProprieta) ( (DefaultMutableTreeNode)passi[2]).getUserObject();
            String valoreAttuale;
            
            proprietaInEdit = g+"."+dg;
            valoreAttuale = Proprieta.recupera(proprietaInEdit);
            areaDescrizione.setText(dg.descrizione);
            if(dg.tipo.equals("stringa")){
                final JTextField campo = new JTextField();
                areaValore.add(campo, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
                campo.setText(valoreAttuale);
                campo.addKeyListener(new KeyListener() {
                    public void keyTyped(KeyEvent e) { aggiornaProprieta(campo); }
                    public void keyReleased(KeyEvent e) { aggiornaProprieta(campo); }
                    public void keyPressed(KeyEvent e) { aggiornaProprieta(campo); }
                });
            }else if(dg.tipo.equals("enumerato")){
                final ButtonGroup gruppoSpunte = new ButtonGroup();
                int i;
                for(i=0 ; i<dg.valoreEnumerato.size() ; i++){
                    JRadioButton spunta = new JRadioButton(dg.valoreEnumerato.get(i).getDescrizione());
                    areaValore.add(spunta, new GridBagConstraints(0, i, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
                    spunta.setName(dg.valoreEnumerato.get(i).getEsterno());
                    gruppoSpunte.add(spunta);
                    spunta.setSelected( dg.valoreEnumerato.get(i).getEsterno().equals(valoreAttuale) ) ;
                    spunta.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            aggiornaProprieta(e);
                        }
                    });
                }
                areaValore.add(spreco, new GridBagConstraints(0, i, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,   GridBagConstraints.BOTH, CostantiGUI.insetsDatoTesto, 0, 0));
            }else if(dg.tipo.equals("boolean")){
                final ButtonGroup gruppoSpunte = new ButtonGroup();
                int i;
                for(i=0 ; i<dg.valoreEnumerato.size() ; i++){
                    JRadioButton spunta = new JRadioButton(dg.valoreEnumerato.get(i).getDescrizione());
                    areaValore.add(spunta, new GridBagConstraints(0, i, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
                    spunta.setName(dg.valoreEnumerato.get(i).getEsterno());
                    gruppoSpunte.add(spunta);
                    spunta.setSelected( dg.valoreEnumerato.get(i).getEsterno().equals(valoreAttuale) ) ;
                    spunta.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            aggiornaProprieta(e);
                        }
                    });
                }
                areaValore.add(spreco, new GridBagConstraints(0, i, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,   GridBagConstraints.BOTH, CostantiGUI.insetsDatoTesto, 0, 0));
            }
        }
    }
    
    void aggiornaProprieta(JTextField e){
        Proprieta.aggiorna(proprietaInEdit, e.getText());
        aggiornaMessaggio();
    }
    
    void aggiornaProprieta(ActionEvent e){
        Proprieta.aggiorna(proprietaInEdit, ((JRadioButton)e.getSource()).getName());
        aggiornaMessaggio();
    }
    
    private void aggiornaMessaggio(){
    	String testo = Proprieta.check();
    	areaMessaggi.setText(testo);
    }
    
}
