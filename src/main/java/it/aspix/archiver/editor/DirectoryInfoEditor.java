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
import it.aspix.archiver.CostruttoreOggetti;
import it.aspix.archiver.Icone;
import it.aspix.archiver.Utilita;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.archiver.ManagerSuggerimenti;
import it.aspix.archiver.componenti.AreaTestoUnicode;
import it.aspix.archiver.componenti.ComboBoxModelED;
import it.aspix.archiver.componenti.ComboBoxModelEDFactory;
import it.aspix.archiver.componenti.ComboSuggerimenti;
import it.aspix.archiver.componenti.CoppiaED;
import it.aspix.archiver.componenti.LinkArrayManager;
import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.archiver.nucleo.Proprieta;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Attribute;
import it.aspix.sbd.obj.AttributeInfo;
import it.aspix.sbd.obj.DirectoryInfo;
import it.aspix.sbd.obj.Link;
import it.aspix.sbd.obj.Rights;
import it.aspix.sbd.obj.SimpleBotanicalData;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DirectoryInfoEditor extends JPanel{

    private static final long serialVersionUID = 1L;

    // alcuni dati non sono modificabili per cui vengono registrati
    // in  queste variabili e visualizzati a schermo in maniere differenti
    DirectoryInfo datiVisualizzati;

    GridBagLayout layoutPrincipale = new GridBagLayout();
    JPanel principale = new JPanel(layoutPrincipale);

    JPanel pannelloContainer = new JPanel(new GridBagLayout());
    JLabel eContainerNome = new JLabel();
    JTextField containerNome = new JTextField();
    JLabel eContainerProgressivo = new JLabel();
    JTextField containerProgressivo = new JTextField();
    JLabel eContainerId = new JLabel();
    JTextField containerId = new JTextField();

    JPanel pannelloSubContainer = new JPanel(new GridBagLayout());
    JLabel eSubContainerNome = new JLabel();
    ComboSuggerimenti subContainerNome;
    JLabel eSubContainerProgressivo = new JLabel();
    JTextField subContainerProgressivo = new JTextField();
    JLabel eSubContainerId = new JLabel();
    JTextField subContainerId = new JTextField();

    JPanel pannelloTesti = new JPanel(new GridBagLayout());
    JLabel inserito = new JLabel();
    JLabel modificato = new JLabel();
    JPanel visualizzazioneDiritti = new JPanel();
    JLabel iOwnerRead = new JLabel();
    JLabel iOwnerWrite = new JLabel();
    JLabel iGroupRead = new JLabel();
    JLabel iGroupWrite = new JLabel();
    JLabel iOtherRead = new JLabel();
    JLabel iOtherWrite = new JLabel();
    JLabel note = new JLabel();
    JButton modifica = new JButton();
    JButton copiaInformazioniLink = new JButton(Icone.LinkFrom);

    LinkArrayManager managerLinks = new LinkArrayManager();

    JPanel pannelloGestioneAttributi = new JPanel(new BorderLayout());
    JPanel pannelloAttributi = new JPanel(new GridLayout(0,1));
    JPanel pannelloAmmortizzatoreAttributi = new JPanel(new BorderLayout());
    JScrollPane scrollAttributi = new JScrollPane(pannelloAmmortizzatoreAttributi);
    JButton aggiungiAttributo = new JButton();
    static ActionListener ascoltatoreRimozioneAttributo = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JButton jb = (JButton) e.getSource();
			Component editor = jb.getParent();
			JPanel pannello = (JPanel) editor.getParent();
			pannello.remove(editor);
			pannello.updateUI();
		}
	};


    public DirectoryInfoEditor(Container contenitore){
        @SuppressWarnings("rawtypes")
        final Class possibili[] = {SampleEditor.class, SpecimenEditor.class, SpecieSpecificationEditor.class, BlobEditor.class};

        copiaInformazioniLink.setName("directoryInfo.copyLink");
    	eContainerNome.setText("nome:");
        eContainerProgressivo.setText("progr.:");
        containerProgressivo.setName("directoryInfo.containerSeqNo");
        eContainerId.setText("id:");
        containerId.setName("directoryInfo.containerExternalId");
        eSubContainerNome.setText("nome:");
        eSubContainerProgressivo.setText("progr.:");
        subContainerProgressivo.setName("directoryInfo.subContainerSeqNo");
        eSubContainerId.setText("id:");
        subContainerId.setName("directoryInfo.subContainerExternalId");
        modifica.setText("modifica diritti");
        // copiaInformazioniLink.setIcon(Icone.LinkFrom);
        aggiungiAttributo.setText("aggiungi attributo");

        @SuppressWarnings("rawtypes")
        Class antenato = UtilitaGui.getContenitoreTra(contenitore,possibili);

        if(antenato==SampleEditor.class){
            eContainerNome.setText("progetto:");
            eSubContainerNome.setText("sottoprogetto:");
            subContainerNome = new ComboSuggerimenti(Proprieta.recupera("vegetazione.database"),
                    "subContainer", ComboSuggerimenti.SOLO_AVVIO, true, false, "");
        }else if(antenato==SpecimenEditor.class){
            eContainerNome.setText("erbario:");
            eSubContainerNome.setText("collezione:");
            subContainerNome = new ComboSuggerimenti(Proprieta.recupera("herbaria.database"),
                    "subContainer", ComboSuggerimenti.SOLO_AVVIO, true, false, "");
        }else if(antenato==SpecieSpecificationEditor.class){
            eContainerNome.setText("check-list:");
            eSubContainerNome.setText("gruppo:");
            subContainerNome = new ComboSuggerimenti(Proprieta.recupera("check-list.database"),
                    "subContainer", ComboSuggerimenti.SOLO_AVVIO, true, false, "");
        }else if(antenato==BlobEditor.class){
            eContainerNome.setText("contenitore:");
            eSubContainerNome.setText("cartella:");
            subContainerNome = new ComboSuggerimenti(Proprieta.recupera("blob.database"),
                    "subContainer", ComboSuggerimenti.SOLO_AVVIO, true, false, "");
        }else{
            // indicazioni generiche, in realtà non ho riconosciuto nessun antenato
            eContainerNome.setText("CONTAINER:");
            eSubContainerNome.setText("SUBCONTAINER:");
            subContainerNome = new ComboSuggerimenti(null,null, 2000, true, false, "");
        }

        Dimension d;
        d = subContainerNome.getPreferredSize();
        d.width = 200;
        subContainerNome.setMaximumSize(d);

        // -------------------- inserimento nei pannelli --------------------
        visualizzazioneDiritti.setLayout(new BoxLayout(visualizzazioneDiritti, BoxLayout.X_AXIS));
        this.setLayout(new BorderLayout());
        this.add(principale, BorderLayout.CENTER);
        this.add(managerLinks, BorderLayout.EAST);
        principale.add(pannelloContainer,    	  new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        principale.add(pannelloSubContainer, 	  new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        principale.add(pannelloTesti,        	  new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));
        principale.add(pannelloGestioneAttributi, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsGruppo, 0, 0));

        pannelloTesti.add(visualizzazioneDiritti,  new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.NONE,       new Insets(0,0,0,20), 0, 0));
        pannelloTesti.add(inserito,                new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsTestoDescrittivo, 0, 0));
        pannelloTesti.add(modifica,                new GridBagConstraints(2, 0, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,  GridBagConstraints.VERTICAL,      CostantiGUI.insetsTestoDescrittivo,0, 0));
        pannelloTesti.add(copiaInformazioniLink,   new GridBagConstraints(3, 0, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,  GridBagConstraints.VERTICAL,      CostantiGUI.insetsTestoDescrittivo,0, 0));
        pannelloTesti.add(modificato,              new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsTestoDescrittivo, 0, 0));
        pannelloTesti.add(note,                    new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsTestoDescrittivo, 0, 0));

        pannelloContainer.add(eContainerNome,        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        pannelloContainer.add(containerNome,         new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloContainer.add(eContainerProgressivo, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        pannelloContainer.add(containerProgressivo,  new GridBagConstraints(3, 0, 1, 1, 0.6, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 80, 0));
        pannelloContainer.add(eContainerId,          new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        pannelloContainer.add(containerId,           new GridBagConstraints(5, 0, 1, 1, 0.6, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 80, 0));

        pannelloContainer.add(eSubContainerNome,        new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        pannelloContainer.add(subContainerNome,         new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
        pannelloContainer.add(eSubContainerProgressivo, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        pannelloContainer.add(subContainerProgressivo,  new GridBagConstraints(3, 1, 1, 1, 0.6, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 80, 0));
        pannelloContainer.add(eSubContainerId,          new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
        pannelloContainer.add(subContainerId,           new GridBagConstraints(5, 1, 1, 1, 0.6, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 80, 0));

        visualizzazioneDiritti.add(iOwnerRead);
        visualizzazioneDiritti.add(iOwnerWrite);
        visualizzazioneDiritti.add(Box.createRigidArea(new Dimension(5,0)));
        visualizzazioneDiritti.add(iGroupRead);
        visualizzazioneDiritti.add(iGroupWrite);
        visualizzazioneDiritti.add(Box.createRigidArea(new Dimension(5,0)));
        visualizzazioneDiritti.add(iOtherRead);
        visualizzazioneDiritti.add(iOtherWrite);

        JPanel ppAggiungi = new JPanel(new BorderLayout());
        pannelloGestioneAttributi.add(scrollAttributi, BorderLayout.CENTER);
        pannelloGestioneAttributi.add(ppAggiungi, BorderLayout.SOUTH);
        pannelloAmmortizzatoreAttributi.add(pannelloAttributi, BorderLayout.NORTH);
        ppAggiungi.add(aggiungiAttributo, BorderLayout.EAST);

        // ---------------------------------- ascolattori ------------------
        modifica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { modifica_actionPerformed(); }
        });
        copiaInformazioniLink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) { copiaInformazioniLink_actionPerformed(); }
        });
        aggiungiAttributo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { aggiungiAttributo_actionPerformed(); }
		});
        // ---------------------------------- impostazioni ------------------
        int altezza=eContainerNome.getPreferredSize().height;
        inserito.setPreferredSize(new Dimension(130,altezza));
        modificato.setPreferredSize(new Dimension(130,altezza));
        note.setPreferredSize(new Dimension(130,altezza));
        pannelloGestioneAttributi.setPreferredSize(new Dimension(0,100));
        pannelloAmmortizzatoreAttributi.putClientProperty(UtilitaGui.LASCIA_OPACO, "");

        Font usuale=note.getFont();
        Font piccolo=new Font(usuale.getName(),usuale.getStyle(), (int)(usuale.getSize()*0.75) );
        inserito.setFont(piccolo);
        modificato.setFont(piccolo);
        managerLinks.setBorder(UtilitaGui.creaBordoConTesto("Collegamenti",0,0));
        principale.setBorder(UtilitaGui.creaBordoConTesto("Contenitori e diritti",0,5));
        note.setVisible(false);
        note.setForeground(CostantiGUI.coloreAttenzione);
        ManagerSuggerimenti.check(this);
    }

    /************************************************************************
     * Modifica dei diritti di accesso
     ***********************************************************************/
    public void modifica_actionPerformed() {
        EditorParticolari ep = new EditorParticolari();
        ep.setDettagli(
                datiVisualizzati.getOwnerReadRights(),
                datiVisualizzati.getOwnerWriteRights(),
                datiVisualizzati.getContainerReadRights(),
                datiVisualizzati.getContainerWriteRights(),
                datiVisualizzati.getOthersReadRights(),
                datiVisualizzati.getOthersWriteRights(),
                note.getText()
        );
        ep.setLocation( modifica.getLocationOnScreen() );
        ep.setVisible(true);
        datiVisualizzati.setOwnerReadRights( ep.getOwnerRead() );
        datiVisualizzati.setOwnerWriteRights( ep.getOwnerWrite() );
        datiVisualizzati.setContainerReadRights( ep.getGroupRead() );
        datiVisualizzati.setContainerWriteRights( ep.getGroupWrite() );
        datiVisualizzati.setOthersReadRights( ep.getOtherRead() );
        datiVisualizzati.setOthersWriteRights( ep.getOtherWrite() );
        note.setText(ep.getNote());
        aggiornaDescrizioneDiritti();
    }

    /************************************************************************
     * Copia i dati nello stato
     ***********************************************************************/
    public void copiaInformazioniLink_actionPerformed() {
    	Stato.riferimentoPerLinkNome = containerNome.getText();
    	Stato.riferimentoPerLinkProgressivo = containerProgressivo.getText();
    }

    public void aggiungiAttributo_actionPerformed(){
    	SimpleBotanicalData risposta;
    	try {
			risposta = Stato.comunicatore.recuperaInformazioniAttributi();
	        AttributeInfo ai[] = risposta.getAttributeInfo();
	        SelettoreAttributi sa = new SelettoreAttributi(ai);
	        UtilitaGui.centraDialogoAlloSchermo(sa, UtilitaGui.CENTRO);
	        sa.setVisible(true);
	        if(sa.attributoSelezionato!=null){
        		AttributeEditor editor = new AttributeEditor(CostruttoreOggetti.createAttribute(sa.attributoSelezionato));
        		editor.addRemoveListener(ascoltatoreRimozioneAttributo);
        		pannelloAttributi.add(editor);
        		pannelloAttributi.updateUI();
	        }
		} catch (Exception e) {
			Dispatcher.consegna(this, e);
		}
    }

    /************************************************************************
     * Propaga la richiesta al manager dei link che contiene
     * @param l il link da aggiungere
     ***********************************************************************/
    public void addLink(Link l){
        managerLinks.addLink(l);
    }

    /************************************************************************
     * @param di l'oggetto da visualizzare
     ***********************************************************************/
    public void setDirectoryInfo(DirectoryInfo di){
        String tmp;
    	if( di==null ){
        	throw new IllegalArgumentException("DirectoryInfo non può essere null");
        }
        containerNome.setText(di.getContainerName());
        containerProgressivo.setText(di.getContainerSeqNo());
        containerId.setText(di.getContainerExternalId());

        subContainerNome.setText(di.getSubContainerName());
        subContainerProgressivo.setText(di.getSubContainerSeqNo());
        subContainerId.setText(di.getSubContainerExternalId());

        tmp = "inserito da "+Utilita.coalesce(di.getInsertionName(),"indefinito")+ " il "+Utilita.coalesce(di.getInsertionTimeStamp(),"indefinito");
        inserito.setText(tmp);
        tmp = "ultima modifica da "+Utilita.coalesce(di.getLastUpdateName(),"indefinito")+" il "+Utilita.coalesce(di.getLastUpdateTimeStamp(),"indefinito")+".";
        modificato.setText(tmp);
        datiVisualizzati = di.clone();
        if(di.getNote()!=null && di.getNote().length()>0){
            note.setVisible(true);
            note.setText(di.getNote());
        }else{
            note.setVisible(false);
        }

        aggiornaDescrizioneDiritti();
        managerLinks.setOwner(di);

        // parte degli attributi
        pannelloAttributi.removeAll();
        AttributeEditor ai;
        if(di.getAttribute()!=null){
        	for(Attribute a: di.getAttribute()){
        		ai = new AttributeEditor(a);
        		ai.addRemoveListener(ascoltatoreRimozioneAttributo);
        		pannelloAttributi.add(ai);
        	}
        }
    }

    /************************************************************************
     * @return il DirectoryInfo visualizzato da questo editor
     ***********************************************************************/
    public DirectoryInfo getDirectoryInfo(){
        DirectoryInfo di = datiVisualizzati.clone();
        di.setContainerName(containerNome.getText());
        di.setContainerSeqNo(containerProgressivo.getText());
        di.setContainerExternalId(containerId.getText());

        di.setSubContainerName(subContainerNome.getText());
        di.setSubContainerSeqNo(subContainerProgressivo.getText());
        di.setSubContainerExternalId(subContainerId.getText());

        di.setNote(note.getText());
        Attribute a;
        ArrayList<Attribute> ea = new ArrayList<Attribute>();
        for(Component c: pannelloAttributi.getComponents()){
        	a = ((AttributeEditor) c).getAttribute();
			ea.add( a );
		}
        di.setAttribute(ea);

        return di;
    }

    /************************************************************************
     * aggiorna la rappresentaizone dei diritti nelle schermata sintetica,
     * i dati vengono presentati in maniera compatta utilizzando delle icone
     ***********************************************************************/
    private void aggiornaDescrizioneDiritti(){
        Icon        ico;
        String      tip;
        boolean     isProprietario = Proprieta.recupera("connessione.nome").equals(datiVisualizzati.getInsertionName());
        Rights      risposta;
        String      dirittoLetturaContainer = Rights.NONE;
        String      dirittoScritturaContainer = Rights.NONE;

        try{
            if(datiVisualizzati.getContainerName()!=null && datiVisualizzati.getContainerName().length()>0){
                risposta = Stato.comunicatore.verificaAccessoContenitore(datiVisualizzati.getContainerName());
	            dirittoLetturaContainer = risposta.getReadRights();
	            dirittoScritturaContainer = risposta.getWriteRights();
           	}else{
        		/// XXX: nel dubbio siamo ottimisti
        		dirittoLetturaContainer = "full";
        		dirittoScritturaContainer = "full";
        	}
        }catch(Exception ex){
            ; // restiamo sul default NONE
        }

        // owner write
        if(datiVisualizzati.getOwnerReadRights().equals("full")){
            ico = Icone.readFull;
            tip = "il proprietario ("+(isProprietario ?"":"NON ")+"sei tu) può leggere tutti i dati";
        }else if(datiVisualizzati.getOwnerReadRights().equals("limited")){
            ico= Icone.readLimited;
            tip = "il proprietario ("+(isProprietario ?"":"NON ")+"sei tu) può leggere soltanto alcuni dati [impostazione anomala]";
        }else{
            ico = Icone.readNone;
            tip = "il proprietario ("+(isProprietario ?"":"NON ")+"sei tu) non puo leggere questo oggetto [impostazione anomala]";
        }
        iOwnerRead.setIcon(ico);
        iOwnerRead.setToolTipText(tip);

        // owner write
        if(datiVisualizzati.getOwnerWriteRights().equals("full")){
            ico = Icone.writeFull;
            tip = "il proprietario ("+(isProprietario ?"":"NON ")+"sei tu) può modificare tutti i dati";
        }else if(datiVisualizzati.getOwnerWriteRights().equals("limited")){
            ico = Icone.writeLimited;
            tip = "il proprietario ("+(isProprietario ?"":"NON ")+"sei tu) può modificare soltanto alcuni dati";
        }else{
            ico = Icone.writeNone;
            tip = "il proprietario ("+(isProprietario ?"":"NON ")+"sei tu) non può modificare questo oggetto";
        }
        iOwnerWrite.setIcon(ico);
        iOwnerWrite.setToolTipText(tip);

        // container read
        if(datiVisualizzati.getContainerReadRights().equals("full")){
            ico = Icone.readFull;
            tip = "un utente del gruppo ("+(dirittoLetturaContainer.equals(Rights.NONE) ? "NON ":"")+"ne fai parte) può leggere tutti i dati";
        }else if(datiVisualizzati.getContainerReadRights().equals("limited")){
            ico = Icone.readLimited;
            tip = "un utente del gruppo ("+(dirittoLetturaContainer.equals(Rights.NONE) ? "NON ":"")+"ne fai parte) può leggere soltanto alcuni dati";
        }else{
            ico = Icone.readNone;
            tip = "un utente del gruppo ("+(dirittoLetturaContainer.equals(Rights.NONE) ? "NON ":"")+"ne fai parte) non può leggere questo oggetto";
        }
        iGroupRead.setIcon(ico);
        iGroupRead.setToolTipText(tip);

        // container write
        if(datiVisualizzati.getContainerWriteRights().equals("full")){
            ico = Icone.writeFull;
            tip = "un utente del gruppo ("+(dirittoScritturaContainer.equals(Rights.NONE) ? "NON ":"")+"ne fai parte) può modificare tutti i dati";
        }else if(datiVisualizzati.getContainerWriteRights().equals("limited")){
            ico = Icone.writeLimited;
            tip = "un utente del gruppo ("+(dirittoScritturaContainer.equals(Rights.NONE) ? "NON ":"")+"ne fai parte) può modificare soltanto alcuni dati";
        }else{
            ico = Icone.writeNone;
            tip = "un utente del gruppo ("+(dirittoScritturaContainer.equals(Rights.NONE) ? "NON ":"")+"ne fai parte) non può modificare questo oggetto";
        }
        iGroupWrite.setIcon(ico);
        iGroupWrite.setToolTipText(tip);

        // other read
        if(datiVisualizzati.getOthersReadRights().equals("full")){
            ico = Icone.readFull;
            tip = "un utente qualsiasi può leggere tutti i dati";
        }else if(datiVisualizzati.getOthersReadRights().equals("limited")){
            ico = Icone.readLimited;
            tip = "un utente qualsiasi può leggere alcuni i dati";
        }else{
            ico = Icone.readNone;
            tip = "un utente qualsiasi non può leggere questo oggetto";
        }
        iOtherRead.setIcon(ico);
        iOtherRead.setToolTipText(tip);

        // other write
        if(datiVisualizzati.getOthersWriteRights().equals("full")){
            ico = Icone.writeFull;
            tip = "un utente qualsiasi può modificare tutti i dati [pericoloso]";
        }else if(datiVisualizzati.getOthersWriteRights().equals("limited")){
            ico = Icone.writeLimited;
            tip = "un utente qualsiasi può modificare alcuni i dati [pericoloso]";
        }else{
            ico = Icone.writeNone;
            tip = "un utente qualsiasi non può modificare questo oggetto";
        }
        iOtherWrite.setIcon(ico);
        iOtherWrite.setToolTipText(tip);
    }

    /************************************************************************
     * Rimuove il pannello dall'editor (chi chiama questo metodo
     * è poi responsabile di inserirlo nell'interfaccia)
     * @return il pannello per la gestione degli attributi
     ***********************************************************************/
    public JPanel getPannelloAtributi(){
    	principale.remove(pannelloGestioneAttributi);
    	return pannelloGestioneAttributi;
    }

    /************************************************************************
     * Serve per modificare i singoli diritti di accesso
     * @author Edoardo Panfili, studio Aspix
     ***********************************************************************/
    private class EditorParticolari extends JDialog{

        private static final long serialVersionUID = 1L;

        ComboBoxModelED modelloOwnerRead = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.DIRITTI);
        ComboBoxModelED modelloOwnerWrite = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.DIRITTI);
        ComboBoxModelED modelloGroupRead = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.DIRITTI);
        ComboBoxModelED modelloGroupWrite = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.DIRITTI);
        ComboBoxModelED modelloOtherRead = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.DIRITTI);
        ComboBoxModelED modelloOtherWrite = ComboBoxModelEDFactory.createComboBoxModelED(ComboBoxModelEDFactory.TipoCombo.DIRITTI);

        JPanel pannelloDettagli = new JPanel(new GridBagLayout());
        JLabel eLettura = new JLabel();
        JLabel eScrittura = new JLabel();
        JLabel eProprietario = new JLabel();
        JComboBox<CoppiaED> ownerRead = new JComboBox<>();
        JComboBox<CoppiaED> ownerWrite = new JComboBox<>();
        JLabel eGruppo = new JLabel();
        JComboBox<CoppiaED> groupRead = new JComboBox<>();
        JComboBox<CoppiaED> groupWrite = new JComboBox<>();
        JLabel eAltri = new JLabel();
        JComboBox<CoppiaED> otherRead = new JComboBox<>();
        JComboBox<CoppiaED> otherWrite = new JComboBox<>();

        JLabel eNote = new JLabel();
        AreaTestoUnicode noteParticolari = new AreaTestoUnicode();

        JButton ok = new JButton();

        public EditorParticolari(){
            this.setTitle("diritti e note di sistema");
            eLettura.setText("lettura");
            eScrittura.setText("scrittura");
            eProprietario.setText("proprietario");
            eGruppo.setText("gruppo");
            eAltri.setText("altri");
            eNote.setText("note di sistema:");
            ok.setText("ok");
            // ---------------------------------- layout ------------------
            pannelloDettagli.add(eLettura,      new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,       CostantiGUI.insetsEtichettaColonna, 0, 0));
            pannelloDettagli.add(eScrittura,    new GridBagConstraints( 2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,       CostantiGUI.insetsEtichettaColonna, 0, 0));
            pannelloDettagli.add(eProprietario, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDettagli.add(ownerRead,     new GridBagConstraints( 1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
            pannelloDettagli.add(ownerWrite,    new GridBagConstraints( 2, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
            pannelloDettagli.add(eGruppo,       new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDettagli.add(groupRead,     new GridBagConstraints( 1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
            pannelloDettagli.add(groupWrite,    new GridBagConstraints( 2, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
            pannelloDettagli.add(eAltri,        new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichetta, 0, 0));
            pannelloDettagli.add(otherRead,     new GridBagConstraints( 1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));
            pannelloDettagli.add(otherWrite,    new GridBagConstraints( 2, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, CostantiGUI.insetsDatoTesto, 0, 0));

            pannelloDettagli.add(eNote,          new GridBagConstraints( 0, 4, 3, 1, 1.0, 0.0, GridBagConstraints.WEST,   GridBagConstraints.NONE,       CostantiGUI.insetsEtichettaColonna, 0, 0));
            pannelloDettagli.add(noteParticolari,new GridBagConstraints( 0, 5, 3, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       CostantiGUI.insetsScrollTesto, 0, 0));
            pannelloDettagli.add(ok,             new GridBagConstraints( 0, 6, 3, 1, 1.0, 0.0, GridBagConstraints.EAST,   GridBagConstraints.HORIZONTAL, CostantiGUI.insetsAzione, 0, 0));

            this.getContentPane().add(pannelloDettagli);
            // ---------------------------------- ascoltatori ------------------
            ok.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) { ok_actionPerformed(); }
            });
            // ---------------------------------- impostazioni ------------------
            ownerRead.setModel(modelloOwnerRead);
            ownerWrite.setModel(modelloOwnerWrite);
            groupRead.setModel(modelloGroupRead);
            groupWrite.setModel(modelloGroupWrite);
            otherRead.setModel(modelloOtherRead);
            otherWrite.setModel(modelloOtherWrite);

            int altezza = noteParticolari.getPreferredSize().height;
            noteParticolari.setPreferredSize(new Dimension(130,(int) (altezza*3.5)));
            noteParticolari.setWrapStyleWord(true);
            noteParticolari.setLineWrap(true);
            pannelloDettagli.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            this.setModal(true);
            this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            this.pack();
        }

        public void ok_actionPerformed() {
            this.dispose();
        }

        public void setDettagli(String ownerRead, String ownerWrite, String groupRead, String groupWrite, String otherRead, String otherWrite, String sn){
            modelloOwnerRead.setSelectedEnum(ownerRead);
            modelloOwnerWrite.setSelectedEnum(ownerWrite);
            modelloGroupRead.setSelectedEnum(groupRead);
            modelloGroupWrite.setSelectedEnum(groupWrite);
            modelloOtherRead.setSelectedEnum(otherRead);
            modelloOtherWrite.setSelectedEnum(otherWrite);
            noteParticolari.setText(sn);
        }

        public String getOwnerRead(){ return modelloOwnerRead.getSelectedEnum(); }
        public String getOwnerWrite(){ return modelloOwnerWrite.getSelectedEnum(); }
        public String getGroupRead(){ return modelloGroupRead.getSelectedEnum(); }
        public String getGroupWrite(){ return modelloGroupWrite.getSelectedEnum(); }
        public String getOtherRead(){ return modelloOtherRead.getSelectedEnum(); }
        public String getOtherWrite(){ return modelloOtherWrite.getSelectedEnum(); }
        public String getNote(){ return noteParticolari.getText(); }
    }

    private class SelettoreAttributi extends JDialog{

		private static final long serialVersionUID = 1L;

		public AttributeInfo attributoSelezionato = null;

    	public SelettoreAttributi(AttributeInfo[] ai){
    		JPanel pannelloAttributiDaSelezionare = new JPanel(new GridLayout(0,1));
    		JPanel pannelloAmmortizzatoreScroll = new JPanel(new BorderLayout());
    		JScrollPane scroll = new JScrollPane(pannelloAmmortizzatoreScroll);
    		JButton pulsante;
    		SimpleBotanicalData risposta;
    		String oggettiContenutiNelContenitoreInEdit=null;
    		int attributiSelezionabili = 0;

    		pannelloAmmortizzatoreScroll.add(pannelloAttributiDaSelezionare, BorderLayout.NORTH);
    		try{
	    		risposta = Stato.comunicatore.informazioniContenitore(containerNome.getText());
	    		oggettiContenutiNelContenitoreInEdit = risposta.getContainerInfo(0).getContains();

	    		ActionListener ascoltatore = new ActionListener() {
					public void actionPerformed(ActionEvent arg) {
						SelettoreAttributi.this.attributoSelezionato = (AttributeInfo) ((JButton) arg.getSource()).getClientProperty("attributo");
						SelettoreAttributi.this.setVisible(false);
					}
				};
	    		for(int i=0; i<ai.length ; i++){
	    			if(!isAttributoPresente(ai[i]) && ai[i].getValidIn().indexOf(oggettiContenutiNelContenitoreInEdit)!=-1){
		    			pulsante = new JButton(ai[i].getName()+": "+ai[i].getDescription());
		    			pulsante.putClientProperty("attributo", ai[i]);
		    			pulsante.addActionListener(ascoltatore);
		    			pannelloAttributiDaSelezionare.add(pulsante);
		    			attributiSelezionabili++;
	    			}
	    		}
	    		if(attributiSelezionabili==0){
	    			pannelloAttributiDaSelezionare.add(new JLabel("Nessun attributo da aggiungere"));
	    			pannelloAttributiDaSelezionare.add(new JLabel("per l'oggetto selezionato"));
	    		}
    		}catch(Exception ex){
    			Dispatcher.consegna(this, ex);
    		}

    		this.setTitle("Selezione attributo");
    		this.getContentPane().add(scroll);
    		this.setSize(300, 300 );
    		this.validate();
    		this.setModal(true);
    	}

    	private boolean isAttributoPresente(AttributeInfo i){
    		for(Component c: pannelloAttributi.getComponents()){
    			if( ((AttributeEditor) c).getAttribute().getName().equals(i.getName()) ){
    				return true;
    			}
    		}
    		return false;
    	}
    }

}