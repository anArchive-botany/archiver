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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.ToolTipManager;

import it.aspix.archiver.nucleo.Proprieta;

/****************************************************************************
 * Il gestore del sistema di suggerimenti, le descrizioni che vengono 
 * visualizzate seguono il focus
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class ManagerSuggerimenti {
	private static Properties descrizioni; 				// recuperate da un file di proprietà 
	private static JWindow finestra = new JWindow();	// senza decorazioni che andrà a contenre le indicazioni
	private static JEditorPane te = new JEditorPane();	// unico elemento di finestra
	static {
		InputStream is = ManagerSuggerimenti.class.getResourceAsStream("../descrizioneCampi.txt");
		descrizioni = new Properties();
		try {
			loadProperties(descrizioni, is);
			is.close();
		} catch (Exception e) {
			// se non trovo il file poco ci posso fare
			e.printStackTrace();
		}
        te.setEditable(false);
        te.setOpaque(false);
        te.setContentType("text/html");
        finestra.add(te);
        te.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        finestra.setSize(350, 100);
        finestra.validate();
        finestra.setBackground(new Color(0.92f, 0.92f, 0.0f, 0.65f));
        finestra.setAlwaysOnTop(true);
        MouseListener ascoltatore = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				finestra.setVisible(false);
			}
		};
        te.addMouseListener(ascoltatore);
        finestra.addMouseListener(ascoltatore);
        if(Proprieta.isTrue("generale.mostraIndicazioni")){
        	// se si vogliono avere le indicazioni anche i tooltip copmpaiono più in fretta
        	ToolTipManager.sharedInstance().setInitialDelay(100);
        }
	}
	
	/************************************************************************
     * @param p in cui caricare le proprieta
     * @param is lo stream (deve essere codificato in UTF-8)
     * @throws IOException 
     ***********************************************************************/
    private static void loadProperties(Properties p, InputStream is) throws IOException{
        InputStreamReader isr;
        BufferedReader lettoreRighe;
        String riga;
        int posUguale;
        
        isr = new InputStreamReader(is,"UTF-8");
        lettoreRighe = new BufferedReader(isr);
        while( (riga=lettoreRighe.readLine()) != null){
            riga = riga.trim();
            if(riga.length()==0 || riga.startsWith("#")){
                continue;
            }
            posUguale = riga.indexOf('=');
            p.put(riga.substring(0, posUguale), riga.substring(posUguale+1));
        }
        lettoreRighe.close();
        isr.close();
    }
	
	/************************************************************************
	 * Cerca tutti i JComponent che hanno un "name", se la proprietà è
	 * presente cerca una descrizione associata e imposta i listener
	 * per visualizzare il messaggio
	 * XXX: dipendentemente dall'ordine con qui questo metodo viene chiamato 
	 * potrebbe succedere che ad un oggettovengano attaccati due ascoltatori,
	 * questo non dovrebbe causare problemi durante l'esecuzione al dilà 
	 * dello spreco 
	 * @param container punti di inizio della ricerca
	 ***********************************************************************/
	public static void check(Container... container){
		String descrizione;
    	Stack<Container> elementi = new Stack<Container>();
    	Container x;
    	Component componenti[];
    	JComponent elemento;
    	String nomeDaCercare;
    	
    	for(Container c: container){
    		elementi.add(c);
    	}    	
    	while(elementi.size()>0){
    		x = elementi.pop();
    		componenti = x.getComponents();
    		for(Component c: componenti){
    			if(c instanceof JComponent){
    				// almeno un nome la classe lo avrà
    				nomeDaCercare = c.getClass().getSimpleName();
    				if(c.getName()!= null){
    					// se dichiara un nome di suo prendiamo quello
    					nomeDaCercare = c.getName();
    				}
    				// Questo elemento ha una nome
    				descrizione = descrizioni.getProperty(nomeDaCercare);
    				if(descrizione!=null){
    					elemento = (JComponent) c;
    					c.setName(nomeDaCercare); // è possibile che il nome sia derivato dal nome della classe e quindi lo impostiamo di nuovo qui
    					if(elemento instanceof JTextField){
	    		        	elemento.addFocusListener(new FocusListener() {
	    						public void focusLost(FocusEvent e) {
	    							finestra.setVisible(false);
	    						}
	    						public void focusGained(FocusEvent e) {
	    							if(Proprieta.isTrue("generale.mostraIndicazioni")){
		    							JComponent target = (JComponent) e.getSource();
		    							te.setText(descrizioni.getProperty(target.getName()));
		    							finestra.setVisible(true);
		    							Point posizione = target.getLocationOnScreen();
		    							posizione.y = posizione.y + target.getSize().height;
		    							posizione.x = posizione.x + 10;
		    							finestra.setLocation(posizione);
	    							}
	    						}
	    					});
    					}
    					if(elemento instanceof JButton){
    						elemento.setToolTipText(descrizioni.getProperty(nomeDaCercare));
    					}
    				}
    			}
    			if(c instanceof Container){
    				// se è un container lo aggiungo per cercare ancora
    				elementi.add((Container) c);
    			}
    		}
    	}
	}

}
