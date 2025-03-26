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
package it.aspix.archiver.componenti.liste;

import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.Icone;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.Util;
import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.Link;
import it.aspix.sbd.obj.SimpleBotanicalData;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/****************************************************************************
 * Visualizzare "l'altro capo" del link rispetto all'oggetto che posside la lista.
 * Siccome i link sono A<->B il render visualizza soltanto uno dei due capi, 
 * quello diverso dall'oggetto proprietario della lista.
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class RenderLink implements ListCellRenderer{

    public RenderLink(){
    }

    /** utilizzato per visualizzere il tipo di link */
    private static HashMap<String, ImageIcon> icona;
    {
        icona = new HashMap<String, ImageIcon>();
        icona.put("specie", Icone.OggettoSpecieSpecification);
        icona.put("specimen", Icone.OggettoSpecimen);
        icona.put("vegetation", Icone.OggettoSample);
        icona.put("publication", null);
        icona.put("blob", Icone.OggettoBlob);
    }
    
    public Component getListCellRendererComponent(JList lista, Object value, int indice, boolean selected, boolean hasFocus)  {
        JLabel etichetta = new JLabel();
        Link link = (Link)value;
        StringBuilder sb = new StringBuilder();
        String nomeContenitore = null;
        SimpleBotanicalData risposta;
        String n1,n2;
        
        etichetta.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        etichetta.setOpaque(true);
        
        if(selected)
            etichetta.setBackground(CostantiGUI.coloreSfondoElementiSelezionabiliSelezionati);
        else
            etichetta.setBackground(CostantiGUI.coloreSfondoElementiSelezionabili);
        n1 = append(sb,lista,link.getFromName(),link.getFromSeqNo());
        n2 = append(sb,lista,link.getToName(),link.getToSeqNo());
        nomeContenitore = Util.coalesce(n1, n2, "");
        if(link.getUrl()!=null && link.getUrl().length()>0){
            // non linka a un contenitore, è un link esterno
            sb = new StringBuilder(link.getUrl());
        }else{
            try {
                risposta = Stato.comunicatore.informazioniContenitore(nomeContenitore);
                etichetta.setIcon(icona.get(risposta.getContainerInfo(0).getContains()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        etichetta.setText(sb.toString());
        return etichetta;
    }
    
    /************************************************************************
     * Accoda la descrizione del link
     * @param buffer il Buffer in cui appendere i dati
     * @param lista
     * @param nome nome del contenitore
     * @param seqNo numero di sequenza
     * @return il nome del contenitore se è diverso da null e dal proprietario
     *         (considerando nome e numero di sequenza)
     ***********************************************************************/
    private static final String append(StringBuilder buffer, JList lista, String nome, String seqNo){
        DirectoryInfo di = ((ListaConProprietario)lista).proprietario; 
        if(nome!=null && ! (nome.equals(di.getContainerName()) && seqNo.equals(di.getContainerSeqNo())) ){
            buffer.append(nome);
            buffer.append('#');
            buffer.append(seqNo);
            return nome;
        }else{
            return null;
        }
    }

}
