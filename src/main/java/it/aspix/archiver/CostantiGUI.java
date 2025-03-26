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

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class CostantiGUI {
    // Insets(int top, int left, int bottom, int right) 
    public static Insets insetsDatoTesto = new Insets(1,0,0,0);
    public static Insets insetsScrollTesto = new Insets(1,3,0,3); // FIXME i 3 pixel in più rispetto a insetsDatoTesto vanno bene su Mac OS X ma non so sugli altri
    public static Insets insetsEtichetta = new Insets(0,2,0,0);
    /** le colonne fatte con cose tipo GridLayout non quelle di JTable */
    public static Insets insetsEtichettaColonna = new Insets(0,0,0,0);
    public static Insets insetsTestoDescrittivo = new Insets(0,0,0,0);
    /** un testo che deve staccarsi un minimo da chi lo precede e da chi lo segue */
    public static Insets insetsTestoDescrittivoIsolato = new Insets(7,0,7,0);
    
    public static Insets insetsGruppo = new Insets(0,5,0,5);
    /** un piccolo margine, usato per rendere evidente magari uno sfondo diverso */
    public static Insets insetsGruppoIsolato = new Insets(3,3,3,3);
    /** Un gruppo che deve staccarsi sia da quello che lo precede che da quello che lo segue */
    public static Insets insetsGruppoIsolatoVerticalmente = new Insets(30,0,30,0);
    
    public static Insets insetsAzione = new Insets(0,0,0,0);
    public static Insets insetsAzioneInBarraIsolata = new Insets(8,30,0,30);
    /** il tipo di pulsanti che vanno a formare una barra (insieme ad altri) */
    public static Insets insetsAzioneInBarra = new Insets(8,0,0,0);
    public static Insets insetsAzioneInBarraPrimaDiGruppo = new Insets(8,30,0,0);
    public static Insets insetsAzioneInBarraUltimaDiGruppo = new Insets(8,0,0,30);
    
    public static Insets insetsSpreco = new Insets(0,0,0,0);
    public static Insets insetsSeparatore = new Insets(5,0,5,0);
    
    public static Color coloreSfondoOggettoIndefinito = Color.GRAY;
    public static Color coloreSfondoSpecieSpecification = new Color(222,43,48);
    public static Color coloreSfondoSpecimen = new Color(161,205,234);
    public static Color coloreSfondoSample = new Color(151,197,155);
    public static Color coloreSfondoBlob = new Color(184,158,182);
    
    public static Color coloreBordi = Color.BLACK;
    public static Color coloreBordoGruppi = Color.BLACK;
    public static Color coloreSfondoElementiSelezionabili = Color.WHITE;
    public static Color coloreSfondoElementiSelezionabiliSelezionati = Color.YELLOW;
    public static Color coloreTestoElementiSelezionabili = Color.BLACK;
    public static Color coloreTestoElementiSelezionabiliSecondari = Color.GRAY;
    /** i colore di attenzione viene usato sia per il testo che per lo sfondo */
    public static Color coloreAttenzione = Color.RED;
    public static Color coloreDragGestito = Color.RED;
    public static Color coloreDragOk = Color.GREEN;
    
    public static Color colorePlantula = new Color(0,150,0);
    public static Color coloreIncerta = Color.RED;
    public static Color coloreGruppo = new Color(230,100,0);
    
    public static Color coloreWizardPassoAttuale = new Color(255,236,30);
    public static Color coloreWizardPassoAltro = new Color(253,255,126);
    public static Color coloreWizardPassoPulsanti = new Color(254,255,209);
    
    // createCompoundBorder(outside,inside)
    // createEmptyBorder(int top, int left, int bottom, int right)
    // createMatteBorder(int top, int left, int bottom, int right, Color color) 
    public static Border bordoSpaziatoreTopLevelEditor = BorderFactory.createEmptyBorder(3, 3, 3, 3);
    public static Border bordoGruppoIsolato = BorderFactory.createEmptyBorder(3, 3, 3, 3);
    public static Border bordoGruppoLinea = BorderFactory.createLineBorder(Color.GRAY);
    
    public static Font fontPiccolo;
    static {
    	Font x = UIManager.getFont("Label.font");
    	fontPiccolo = new Font(x.getName(),x.getStyle(), (int)(x.getSize()*0.75) );
    }

}
