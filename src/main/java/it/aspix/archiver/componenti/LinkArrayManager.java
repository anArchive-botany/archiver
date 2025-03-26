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
package it.aspix.archiver.componenti;

import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.Icone;
import it.aspix.archiver.DesktopApiWrapper;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.componenti.liste.ListaConProprietario;
import it.aspix.archiver.componenti.liste.RenderLink;
import it.aspix.archiver.dialoghi.DatiMinimiLink;
import it.aspix.archiver.editor.BlobEditor;
import it.aspix.archiver.editor.SampleEditor;
import it.aspix.archiver.editor.SpecieSpecificationEditor;
import it.aspix.archiver.editor.SpecimenEditor;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Blob;
import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.Link;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.OggettoSBD;
import it.aspix.sbd.obj.Sample;
import it.aspix.sbd.obj.SimpleBotanicalData;
import it.aspix.sbd.obj.SpecieSpecification;
import it.aspix.sbd.obj.Specimen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/****************************************************************************
 * Un oggetto in grado di gestire un elenco di link
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class LinkArrayManager extends JPanel{
    
    private static final long serialVersionUID = 1L;
    DefaultListModel contenutoLista;
    ListaConProprietario contenitoreLinks = new ListaConProprietario();
    JButton aggiungiGenerico = new JButton("+");
    JButton rimuovi = new JButton("-");
    JButton aggiungiBlob = new JButton(Icone.AddBlob);
    JButton incollaLink = new JButton(Icone.LinkTo);
    DirectoryInfo datiProprietario;
    
    public LinkArrayManager(){
        JScrollPane scroll = new JScrollPane();
        JPanel pannelloPulsanti = new JPanel(new GridLayout(0,1));
        
        incollaLink.setName("directoryInfo.pasteLink");
        
        contenutoLista = new DefaultListModel();
        contenitoreLinks.setModel(contenutoLista);
        this.setLayout(new BorderLayout());
        this.add(scroll, BorderLayout.CENTER);
        this.add(pannelloPulsanti, BorderLayout.EAST);
        pannelloPulsanti.add(aggiungiGenerico);
        pannelloPulsanti.add(rimuovi);
        pannelloPulsanti.add(aggiungiBlob);
        pannelloPulsanti.add(incollaLink);
        
        scroll.getViewport().add(contenitoreLinks);
        contenitoreLinks.setCellRenderer(new RenderLink());
        this.setMinimumSize(new Dimension(250,5));
        this.setPreferredSize(new Dimension(250,5));
        pannelloPulsanti.setOpaque(false);
        
        aggiungiGenerico.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                azione_aggiungiGenerico();
            }
        });
        rimuovi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                azione_rimuovi();
            }
        });
        aggiungiBlob.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                azione_aggiungiBlob();
            }
        });
        incollaLink.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                azione_incollaLink();
            }
        });
        contenitoreLinks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()==2){
                    azione_doppioClick(e);
                }
            }
        });
    }
    
    /************************************************************************
     * Imposta un proprietario, nel caso questo sia utilizzabile questo
     * componente recupererà i link esistenti e permetterà di inserire nuovi link.
     * @param di i dati del proprietario
     ***********************************************************************/
    public void setOwner(DirectoryInfo di){
        contenutoLista.removeAllElements();
        contenitoreLinks.proprietario = di;
        if(di.getContainerName()!=null && di.getContainerName().length()>0 && di.getContainerSeqNo()!=null && di.getContainerSeqNo().length()>0){
            this.setVisible(true);
            datiProprietario = di;
            Link l = new Link();
            l.setFromName(datiProprietario.getContainerName());
            l.setFromSeqNo(datiProprietario.getContainerSeqNo());
            try {
                SimpleBotanicalData risposta = Stato.comunicatore.cerca(l, null);
                Dispatcher.consegnaMessaggi(this, risposta);
                for(int i = 0 ; i<risposta.getLinkSize() ; i++){
                    contenutoLista.addElement(risposta.getLink(i));
                }
            } catch (Exception e) {
                Dispatcher.consegna(this, e);
            }
        }else{
            this.setVisible(false);
        }
        // vedo se serve il pulsante "aggiungi foto"
        @SuppressWarnings("rawtypes")
        final Class possibili[] = {SampleEditor.class, SpecimenEditor.class, SpecieSpecificationEditor.class, BlobEditor.class};
        @SuppressWarnings("rawtypes")
        Class antenato = UtilitaGui.getContenitoreTra(this,possibili);
        aggiungiBlob.setVisible(antenato!=BlobEditor.class);
    }
    
    /************************************************************************
     * Invia il link al server
     ***********************************************************************/
    void azione_aggiungiGenerico(){
        DatiMinimiLink dati = new DatiMinimiLink();
        dati.setVisible(true);
        if(dati.isOk()){
            Link l = new Link();
            l.setToName(dati.getContenitore());
            l.setToSeqNo(dati.getProgressivo());
            l.setUrl(dati.getUrl());
            addLink(l);
        }
    }
    
    /************************************************************************
     * Rimuove il link dal server
     ***********************************************************************/
    void azione_rimuovi(){
    	Link selezionato = (Link) contenitoreLinks.getSelectedValue();
    	// TODO: mettere richiesta conferma
        try {
            SimpleBotanicalData risposta = Stato.comunicatore.rimuovi(selezionato, null, false);
            if(!risposta.getMessage(0).getType().equals(MessageType.ERROR)){
                contenutoLista.removeElement(selezionato);
            }
            Dispatcher.consegnaMessaggi(this, risposta);
        } catch (Exception e) {
            Dispatcher.consegna(this, e);
        }
    }
    
    /************************************************************************
     * Gestisce l'immissione di un oggetto binario, e successivamente 
     * inserisce il link 
     ***********************************************************************/
    void azione_aggiungiBlob(){
        DirectoryInfo i = Dispatcher.insert(CostruttoreOggetti.createBlob(null));
        if(i!=null){
            // l'oggetto è stato inserito con successo
            Link l = new Link();
            l.setToName(i.getContainerName());
            l.setToSeqNo(i.getContainerSeqNo());
            addLink(l);
        }else{
            Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Non è stato possibile inserire l'oggetto binario", "it", MessageType.ERROR));
        }
    }
    
    /************************************************************************
     * Incolla un link
     ***********************************************************************/
    void azione_incollaLink(){
    	if(Stato.riferimentoPerLinkNome!=null && Stato.riferimentoPerLinkNome.length()>0 &&
    			Stato.riferimentoPerLinkProgressivo!=null && Stato.riferimentoPerLinkProgressivo.length()>0){
    		Link l = new Link();
            l.setToName(Stato.riferimentoPerLinkNome);
            l.setToSeqNo(Stato.riferimentoPerLinkProgressivo);
            addLink(l);
    	}else{
    		Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Prima di cincollare devi \"copiare\" un link.", "it", MessageType.WARNING));
    	}
    }
    
    /************************************************************************
     * Invia al server un link inserendo prima i dati del proprietario di
     * questo pannello nella parte from
     * @param l i link da inviare
     ***********************************************************************/
    public void addLink(Link l){
        l.setFromName(datiProprietario.getContainerName());
        l.setFromSeqNo(datiProprietario.getContainerSeqNo());
        if(l.getFromName().equals(l.getToName()) && l.getFromSeqNo().equals(l.getToSeqNo())){
        	Dispatcher.consegna(this, CostruttoreOggetti.createMessage("Un link verso se stesso non ha senso.", "it", MessageType.ERROR));
        }else{
	        try {
	            SimpleBotanicalData risposta = Stato.comunicatore.inserisci(l, null, false);
	            if(!risposta.getMessage(0).getType().equals(MessageType.ERROR)){
	                contenutoLista.addElement(l);
	            }
	            Dispatcher.consegnaMessaggi(this, risposta);
	        } catch (Exception e) {
	            Dispatcher.consegna(this, e);
	        }
        }
    }
    
    /************************************************************************
     * Chiede la visualizzazione dell'oggetto su cui è 
     * stato fatto il doppio click
     * @param e
     ***********************************************************************/
    void azione_doppioClick(MouseEvent e){
        Link l = (Link) contenitoreLinks.getSelectedValue();
        System.out.println("link: "+l);
        if(l.getUrl()!=null && l.getUrl().length()>0){
            // è un link: uso DestokAPI per aprire la pagina web
        	DesktopApiWrapper.openLink(this, l.getUrl());
        }else{
            // cerco l'altro capo del link, si fa affidamento che uno dei due capi
            // sia il proprietario di questa lista 
            DirectoryInfo target = new DirectoryInfo();
            if(l.getFromName().equals(datiProprietario.getContainerName()) && l.getFromSeqNo().equals(datiProprietario.getContainerSeqNo())){
                target.setContainerName(l.getToName());
                target.setContainerSeqNo(l.getToSeqNo());
            }else{
                target.setContainerName(l.getFromName());
                target.setContainerSeqNo(l.getFromSeqNo());
            }
            try {
                SimpleBotanicalData risposta = Stato.comunicatore.informazioniContenitore(target.getContainerName());
                String contenuto = risposta.getContainerInfo(0).getContains();
                OggettoSBD daVisualizzare;
                if(contenuto.equals("specie")){
                    daVisualizzare = new SpecieSpecification();
                    ((SpecieSpecification)daVisualizzare).setDirectoryInfo(target);
                    risposta = Stato.comunicatore.cerca(daVisualizzare, null);
                    daVisualizzare = risposta.getSpecieSpecification(0);
                }else if(contenuto.equals("specimen")){
                    daVisualizzare = new Specimen();
                    ((Specimen)daVisualizzare).setDirectoryInfo(target);
                    risposta = Stato.comunicatore.cerca(daVisualizzare, null);
                    daVisualizzare = risposta.getSpecimen(0);
                }else if(contenuto.equals("vegetation")){
                    daVisualizzare = new Sample();
                    ((Sample)daVisualizzare).setDirectoryInfo(target);
                    risposta = Stato.comunicatore.cerca(daVisualizzare, null);
                    daVisualizzare = risposta.getSample(0);
                }else if(contenuto.equals("blob")){
                    daVisualizzare = new Blob();
                    ((Blob)daVisualizzare).setDirectoryInfo(target);
                    risposta = Stato.comunicatore.cerca(daVisualizzare, null);
                    daVisualizzare = risposta.getBlob(0);
                }else{
                    // publication
                    throw new ValoreException("Non posso visualizzare "+contenuto);
                }
                
                Dispatcher.consegna(this, daVisualizzare, null);
            } catch (Exception e1) {
                Dispatcher.consegna(this, e1);
            }
        }
    }

}
