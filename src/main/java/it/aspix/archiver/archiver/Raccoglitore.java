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
package it.aspix.archiver.archiver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Path2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import it.aspix.archiver.CostantiGUI;

/****************************************************************************
 * Visualizza una serie di PannelloDescrivibile
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class Raccoglitore extends JPanel implements ChangeListener{

    private static final long serialVersionUID = 1L;
    private static final int OCCUPAZIONE_BORDI = 10;
    
    private Linguette linguette = new Linguette();
    private JPanel pannelloContenuto = new JPanel(new BorderLayout()); // di volta in volta il PannelloDescrivibile visualizzato
    private ArrayList <PannelloDescrivibile> elementi;         // oggetti visualizzati
    private int selezionata = 0;                        // rispetto all'array elementi
    private boolean minimale = false;
    
    public Raccoglitore(){
        elementi = new ArrayList<PannelloDescrivibile>();
        this.setLayout(new BorderLayout());
        this.add(linguette, BorderLayout.NORTH);
        this.add(pannelloContenuto, BorderLayout.CENTER);
        pannelloContenuto.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, CostantiGUI.coloreBordi));
    }
    
    /************************************************************************
     * @param pd da aggiungere a quelli visualizzati
     ***********************************************************************/
    public void addTab(PannelloDescrivibile pd){
        elementi.add(pd);
        pd.addChangeListener(this);
        pd.setFixedParent(this);
        linguette.aggiornaLinguette();
        this.updateUI();
    }
    
    /************************************************************************
     * La barra che rappresenta le linguette
     ***********************************************************************/
    public class Linguette extends JComponent {
        
        private static final long serialVersionUID = 1L;
        protected static final int ALTEZZA  = 25;
        
        public Linguette(){
            this.addMouseListener(new MouseListener() {
                public void mouseReleased(MouseEvent e) {}
                public void mousePressed(MouseEvent e) {
                    // System.out.println("pressed "+e.getPoint());
                    checkMouse(e.getPoint());
                }
                public void mouseExited(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mouseClicked(MouseEvent e) {
                    // System.out.println("click! "+e.getPoint());
                    checkMouse(e.getPoint());
                }
            });
        }
        
        private int partenza[] = new int[100]; // sovrabbondante ma per evitare di creare in continuazione array

        @Override
        public Dimension getMaximumSize(){
            return new Dimension(3000,ALTEZZA);
        }
        
        @Override
        public Dimension getMinimumSize(){
            return new Dimension(100,ALTEZZA);
        }

        @Override
        public Dimension getPreferredSize(){
            return new Dimension(100,ALTEZZA);
        }
        
        @Override
        public void update(Graphics g) {
            // System.out.println("update!");
            super.update(g);
        }
        
        private static final int SPAZIO_PRIMA = 10;
        private static final int SPAZIO_DOPO = 20;
        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            // System.out.println("paint! "+g2d.getClipBounds());
            int larghezza;
            Path2D.Double path;
            
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            partenza[0]=0;
            for(int indiceNome=0 ; indiceNome<elementi.size() ; indiceNome++){
                larghezza = (int) g2d.getFontMetrics().getStringBounds(elementi.get(indiceNome).toString(), g2d).getWidth();
                path = new Path2D.Double();
                path.moveTo(partenza[indiceNome], ALTEZZA);
                path.lineTo(partenza[indiceNome], 1);
                path.lineTo(partenza[indiceNome]+larghezza+SPAZIO_PRIMA, 1);
                path.curveTo(
                        partenza[indiceNome]+larghezza+SPAZIO_PRIMA+SPAZIO_DOPO/2, 1,
                        partenza[indiceNome]+larghezza+SPAZIO_PRIMA+SPAZIO_DOPO, ALTEZZA-5,
                        partenza[indiceNome]+larghezza+SPAZIO_PRIMA+SPAZIO_DOPO, ALTEZZA
                );
                // path.lineTo(partenza[indiceNome]+larghezza+SPAZIO_PRIMA+SPAZIO_DOPO, ALTEZZA);
                if(selezionata==indiceNome){
                    g2d.setPaint(elementi.get(indiceNome).getBackground());
                }else{
                    g2d.setPaint(whiter(elementi.get(indiceNome).getBackground()));
                }
                g2d.fill(path);
                g2d.setPaint(CostantiGUI.coloreBordi);
                g2d.draw(path);
                partenza[indiceNome+1] = partenza[indiceNome] + larghezza +SPAZIO_DOPO+SPAZIO_PRIMA;
                g2d.drawString(elementi.get(indiceNome).toString(), partenza[indiceNome]+SPAZIO_PRIMA, ALTEZZA-5);
            }
            g2d.drawLine(0, ALTEZZA-1, partenza[selezionata], ALTEZZA-1);
            g2d.drawLine(partenza[selezionata+1], ALTEZZA-1, 3000, ALTEZZA-1);
            
            super.paint(g);
        }
        
        public void checkMouse(Point p){
            for(int indiceNome=0 ; indiceNome<elementi.size() ; indiceNome++){
                if(partenza[indiceNome]<p.x && p.x<partenza[indiceNome+1]){
                    // System.out.println("  click su linguetta "+indiceNome);
                    selezionata = indiceNome;
                    aggiornaLinguette();
                }
            }
        }
        
        public void aggiornaLinguette(){
            pannelloContenuto.removeAll();
            pannelloContenuto.add(elementi.get(selezionata));
            Raccoglitore.this.updateUI();
            this.repaint();
        }
        
        private Color whiter(Color c){
            Color risposta = new Color((c.getRed()+510)/3, (c.getGreen()+510)/3, (c.getBlue()+510)/3 );
            // System.out.println(c+"  "+risposta);
            return risposta;
        }

    }

    public void stateChanged(ChangeEvent e) {
        this.updateUI();
    }

    /************************************************************************
     * @return l'indice della linguetta selezionata, zero based
     ***********************************************************************/
    public int getSelezionata() {
        return selezionata;
    }

    /************************************************************************
     * @param selezionata l'indice della linguetta da selezionare, zero based
     ***********************************************************************/
    public void setSelezionata(int selezionata) {
        this.selezionata = selezionata;
        linguette.aggiornaLinguette();
    }
    
    /************************************************************************
     * @return truse se preferredSize è uguale a minimumSize
     ***********************************************************************/
    public boolean isMinimale() {
		return minimale;
	}

    /************************************************************************
     * @param minimale truse se preferredSize deve essere uguale a minimumSize
     ***********************************************************************/
	public void setMinimale(boolean minimale) {
		this.minimale = minimale;
	}

	public Dimension getPreferredSize(){
        Dimension risposta;
        Dimension t;
        
        if(minimale){
        	risposta = getMinimumSize();
        }else{
        	risposta = new Dimension(0,0);
	        for( PannelloDescrivibile pd: elementi){
	            t = pd.getPreferredSize();
	            if(t.height>risposta.height){
	                risposta.height = t.height;
	            }
	            if(t.width>risposta.width){
	                risposta.width = t.width;
	            }
	        }
	        risposta.height+=Linguette.ALTEZZA;
	        risposta.width += OCCUPAZIONE_BORDI;
        }
        return risposta;
    }
    
    public Dimension getMinimumSize(){
        Dimension risposta = new Dimension(0,0);
        Dimension t;
        
        for( PannelloDescrivibile pd: elementi){
            t = pd.getMinimumSize();
            if(t.height>risposta.height){
                risposta.height = t.height;
            }
            if(t.width>risposta.width){
                risposta.width = t.width;
            }
        }
        risposta.height+=Linguette.ALTEZZA;
        risposta.width += OCCUPAZIONE_BORDI;
        return risposta;
    }

    
}
