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
package it.aspix.archiver;

import javax.swing.ImageIcon;

/****************************************************************************
 * Icone di utilita' per tutto il progetto
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class Icone {
	
	public static ImageIcon LogoAnArchive = new ImageIcon(Icone.class.getResource("icone/anArchive.png"));
	public static ImageIcon LogoAnArchiveDoc = new ImageIcon(Icone.class.getResource("icone/anArchive_doc.png"));
	public static ImageIcon LogoTabImportDoc = new ImageIcon(Icone.class.getResource("icone/tabimport_doc.png"));
	public static ImageIcon LogoVegImportDoc = new ImageIcon(Icone.class.getResource("icone/vegimport_doc.png"));
	public static ImageIcon TabellaDiEsempio = new ImageIcon(Icone.class.getResource("icone/esempio_tabella.png"));
	
	// marchi
	public static ImageIcon Pdf = new ImageIcon(Icone.class.getResource("icone/pdf.png"));
	
	// alberi
	public static ImageIcon ASaperto = new ImageIcon(Icone.class.getResource("icone/aaperto.gif"));
	public static ImageIcon ASchiuso = new ImageIcon(Icone.class.getResource("icone/achiuso.gif"));
	public static ImageIcon ASfoglia = new ImageIcon(Icone.class.getResource("icone/foglia.gif"));

	//generiche
	public static ImageIcon Famiglia = new ImageIcon(Icone.class.getResource("icone/famiglia.gif"));
	public static ImageIcon Formabiologica = new ImageIcon(Icone.class.getResource("icone/fbio.gif"));
	public static ImageIcon InfoPiccolo = new ImageIcon(Icone.class.getResource("icone/info_piccolo.png"));
	public static ImageIcon Incrocio = new ImageIcon(Icone.class.getResource("icone/incrocio.gif"));
	public static ImageIcon Libro = new ImageIcon(Icone.class.getResource("icone/libro.gif"));
	public static ImageIcon Proparte = new ImageIcon(Icone.class.getResource("icone/proparte.gif"));
	public static ImageIcon Sinonimo = new ImageIcon(Icone.class.getResource("icone/sinonimo.gif"));
	public static ImageIcon Specie = new ImageIcon(Icone.class.getResource("icone/specie.gif"));
	public static ImageIcon TipoCorologico = new ImageIcon(Icone.class.getResource("icone/tcoro.gif"));
	public static ImageIcon ImmagineNonDisponibile = new ImageIcon(Icone.class.getResource("icone/immaginenondisponibile.png"));
	public static ImageIcon TrascinaQui = new ImageIcon(Icone.class.getResource("icone/trascina_qui.png"));
  
    //messaggi
    public static ImageIcon MessageError = new ImageIcon(Icone.class.getResource("icone/message_error.png"));
    public static ImageIcon MessageInfo = new ImageIcon(Icone.class.getResource("icone/message_info.png"));
    public static ImageIcon MessageWarning = new ImageIcon(Icone.class.getResource("icone/message_warning.png"));
    public static ImageIcon MessageDomanda = new ImageIcon(Icone.class.getResource("icone/message_domanda.png"));
    public static ImageIcon ElencoMessaggi = new ImageIcon(Icone.class.getResource("icone/elenco_messaggi.png"));
    
	//plot	
	public static ImageIcon PlotDelta = new ImageIcon(Icone.class.getResource("icone/plot_delta.png"));
	public static ImageIcon PlotSincronizza = new ImageIcon(Icone.class.getResource("icone/plot_sincronizza.png"));
	public static ImageIcon PlotSpecieCoprente = new ImageIcon(Icone.class.getResource("icone/plot_specie_coprente.png"));
	public static ImageIcon PlotSpecieCoprenteNo = new ImageIcon(Icone.class.getResource("icone/plot_specie_coprente_disattivo.png"));
	public static ImageIcon PlotSpecieRadicata = new ImageIcon(Icone.class.getResource("icone/plot_specie_radicata.png"));
	public static ImageIcon PlotSpecieRadicataNo = new ImageIcon(Icone.class.getResource("icone/plot_specie_radicata_disattivo.png"));

    //lucchetti
    public static ImageIcon LucchettoAperto = new ImageIcon(Icone.class.getResource("icone/lucchetto_aperto.png"));
    public static ImageIcon LucchettoChiuso = new ImageIcon(Icone.class.getResource("icone/lucchetto_chiuso.png"));
    
    //links
    public static ImageIcon LinkFrom = new ImageIcon(Icone.class.getResource("icone/link_from.png"));
    public static ImageIcon LinkTo = new ImageIcon(Icone.class.getResource("icone/link_to.png"));
    public static ImageIcon AddBlob = new ImageIcon(Icone.class.getResource("icone/add_blob.png"));
    
    //oggetti
    public static ImageIcon OggettoBlob = new ImageIcon(Icone.class.getResource("icone/oggettoBlob.png"));
    public static ImageIcon OggettoSpecimen = new ImageIcon(Icone.class.getResource("icone/oggettoSpecimen.png"));
    public static ImageIcon OggettoSample = new ImageIcon(Icone.class.getResource("icone/oggettoSample.png"));
    public static ImageIcon OggettoSpecieSpecification = new ImageIcon(Icone.class.getResource("icone/oggettoSpecieSpecification.png"));
    
	//splash
    public static ImageIcon splash = new ImageIcon(Icone.class.getResource("icone/splash.jpg"));
    public static ImageIcon splashVegImport = new ImageIcon(Icone.class.getResource("icone/vegimport.jpg"));
    public static ImageIcon splashTabImport = new ImageIcon(Icone.class.getResource("icone/tabimport.jpg"));
  
    // permessi
    public static ImageIcon readFull = new ImageIcon(Icone.class.getResource("icone/read_full.png"));
    public static ImageIcon readLimited = new ImageIcon(Icone.class.getResource("icone/read_limited.png"));
    public static ImageIcon readNone = new ImageIcon(Icone.class.getResource("icone/read_none.png"));
    public static ImageIcon writeFull = new ImageIcon(Icone.class.getResource("icone/write_full.png"));
    public static ImageIcon writeLimited = new ImageIcon(Icone.class.getResource("icone/write_limited.png"));
    public static ImageIcon writeNone = new ImageIcon(Icone.class.getResource("icone/write_none.png"));
    
}