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

import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.OggettoSBD;
import it.aspix.sbd.obj.Sample;
import it.aspix.sbd.obj.SpecieSpecification;
import it.aspix.sbd.obj.Specimen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


/*****************************************************************************
 * Di utilità generale per gli elementi gui
 * @author Edoardo Panfili, studio Aspix
 ****************************************************************************/
public class UtilitaGui {
    
    public static final String DEG_SIGN = "\u00b0";
    public static final String LASCIA_OPACO = "lascia opaco";
    public static final int CENTRO      = 0;
    public static final int LATO_ALTO   = 1;
    public static final int LATO_BASSO  = 2;
    /****************************************************************************
     * di supporto per il posizionamento dei dialoghi
     * @param finestra il dialogo da centrare
     * @param ancora cosa deve essere centrato (LATO_ALTO | CENTRO | LATO_BASSO)
     ***************************************************************************/
    public static void centraDialogoAlloSchermo(Window finestra, int ancora){
        centraDialogoAlloSchermo(finestra,ancora,new Point(0,0));
    }
    
    /****************************************************************************
     * Di supporto per il posizionamento dei dialoghi
     * @param finestra il dialogo da centrare
     * @param ancora cosa deve essere centrato
     * @param p lo scostamento da applicare al centro calcolato
     ***************************************************************************/
    public static void centraDialogoAlloSchermo(Window finestra, int ancora, Point p){
        int x,y;

        x=(finestra.getToolkit().getScreenSize().width-finestra.getSize().width)/2;
        switch(ancora){
            case LATO_ALTO:
                y=finestra.getToolkit().getScreenSize().height/2;
                break;
            case LATO_BASSO:
                y=finestra.getToolkit().getScreenSize().height/2-finestra.getSize().height;
                break;
            default:
                y=(finestra.getToolkit().getScreenSize().height-finestra.getSize().height)/2;
                break;
        }
        finestra.setLocation(x+p.x,y+p.y);
    }
    
    /************************************************************************
     * @param testo il titolo del bordo
     * @param fuori lo spazio da laciare fuori dal bordo
     * @param dentro lo spazio da lasciare all'interno del bordo
     * @return il bordo secondo le specifiche
     ***********************************************************************/
    public static Border creaBordoConTesto(String testo, int fuori, int dentro){
    	Border bordoTesto = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(CostantiGUI.coloreBordoGruppi), testo, TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, new Font("Dialog",Font.PLAIN,10));
        return BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(fuori,fuori,fuori,fuori),
            BorderFactory.createCompoundBorder(
                    bordoTesto,
                    BorderFactory.createEmptyBorder(dentro,dentro,dentro,dentro)
            )   
        );
        
    }

    public static final int ESPANDI_TUTTI_I_LIVELLI = -1;
    /************************************************************************
     * espande/collassa interamente un JTree
     * @param tree il JTree
     * @param parent il nodo da cui partire
     * @param expand true per espandere, false per collassare
     * @param livelliDaEspandere il numero di livelli da espandere o ESPANDI_TUTTI_I_LIVELLI
     ***********************************************************************/
    public static void expandAll(JTree tree, TreePath parent, boolean expand, int livelliDaEspandere) {
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        
        if (node.getChildCount() > 0) {
            Enumeration<?> e = node.children();
            while(e.hasMoreElements()) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                if(livelliDaEspandere>1 || livelliDaEspandere==ESPANDI_TUTTI_I_LIVELLI){
                    expandAll(tree, path, expand,
                            livelliDaEspandere == ESPANDI_TUTTI_I_LIVELLI ? ESPANDI_TUTTI_I_LIVELLI : livelliDaEspandere-1);
                }
            }
        }
        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }
    
    /************************************************************************
     * Visualizza on pannello di messaggio senza finestra madre. 
     * @param testo da visulizzare (vengono inseriti \n dove serve)
     * @param titolo della finestra
     * @param tipo una costrante di JOptionPane (es: JOptionPane.ERROR_MESSAGE)
     ***********************************************************************/
    public static void mostraMessaggioAndandoACapo(String testo, String titolo, int tipo){
        StringBuffer sb = new StringBuffer(testo);
        int posSpazio;
        
        for(int t=80 ; t<sb.length() ; t+=80){
            posSpazio = sb.indexOf(" ", t);
            if(posSpazio!=-1){
                sb.setCharAt(posSpazio, '\n');
            }
        }
        JOptionPane.showMessageDialog(null, sb.toString(), titolo, tipo);
    }    
    
    /************************************************************************
     * Aggancia l'ascoltatore a tutti i Jtextfield contenuti in c o in 
     * un suo figlio
     * @param c il componente da cui iniziare da scansione
     * @param al l'ascoltatore da agganciare
     ***********************************************************************/
    public static void aggiungiActionListenerATutti(Container c, ActionListener al){
        Stack<Container> daEsaminare = new Stack<Container>();
        Container inEsame;
        Component comp;
        int j;
        
        daEsaminare.push(c);
        while(!daEsaminare.isEmpty()){
            inEsame = daEsaminare.pop();
            for(j=0 ; j<inEsame.getComponentCount() ; j++){
                comp = inEsame.getComponent(j);
                if(comp instanceof Container){
                    // nb:anche una casella di testo è un container
                    daEsaminare.push((Container) comp);
                }
                if(comp instanceof JTextField){
                    ((JTextField) comp).addActionListener(al);
                }
            }
        }
    }
    
    /************************************************************************
     * Imposta la proprietà "opaque" a tutti i JPanel.
     * Se il sistema in cui sta girando è macOSX imposta la stessa proprietà
     * anche su JComboBox JCheckBox e JButton
     * @param c il componente da cui iniziare da scansione
     * @param isOpaque il valore della proprietà
     ***********************************************************************/
    public static void setOpaqueRicorsivo(Container c, boolean isOpaque){
        Stack<Container> daEsaminare = new Stack<Container>();
        Container inEsame;
        Component comp;
        int j;
        Color predefinito = (Color) UIManager.get("Panel.background");
        
        if(c instanceof JComponent){
    		if( ((JComponent)c).getClientProperty(LASCIA_OPACO) == null ){
    			((JPanel) c).setOpaque(isOpaque);
    		}
    	}
        daEsaminare.push(c);
        while(!daEsaminare.isEmpty()){
            inEsame = daEsaminare.pop(); 
            for(j=0 ; j<inEsame.getComponentCount() ; j++){
                comp = inEsame.getComponent(j);
                if(comp instanceof Container){
                    // nb:anche una casella di testo è un container
                    daEsaminare.push((Container) comp);
                }
                if(comp instanceof JTabbedPane){
                    ((JTabbedPane) comp).setUI(new NonDisegnaSfondoLinguette());
                }
                // se il colore è impostato a qualcosa di diverso dal default JPanel non va reso trasparente
                if(comp instanceof JPanel && comp.getBackground().equals(predefinito)){
                	if(comp instanceof JComponent){
                		if( ((JComponent)comp).getClientProperty(LASCIA_OPACO) == null ){
                			((JPanel) comp).setOpaque(isOpaque);
                		}
                	}else{
                		((JPanel) comp).setOpaque(isOpaque);
                	}
                }
                if(comp instanceof JScrollPane){
                    ((JScrollPane) comp).setOpaque(isOpaque);
                    ((JScrollPane) comp).getViewport().setOpaque(false);
                }
                if(Stato.isMacOSX){
                    if(comp instanceof JComboBox){
                        ((JComboBox<?>) comp).setOpaque(isOpaque);
                    }
                    if(comp instanceof JButton){
                        ((JButton) comp).setOpaque(isOpaque);
                    }
                    if(comp instanceof JRadioButton){
                        ((JRadioButton) comp).setOpaque(isOpaque);
                    }
                }
                if(comp instanceof JCheckBox){
                    ((JCheckBox) comp).setOpaque(isOpaque);
                }
            }
        }
    }
    
    /************************************************************************
     * @param o l'oggetto da sescrivere
     * @return il nome dell'oggeto (es: cartellino)
     ***********************************************************************/
    public static String getNome(OggettoSBD o){
        if(o instanceof Specimen){
            return "cartellino";
        }else if(o instanceof SpecieSpecification){
            return "specie";
        }else if(o instanceof Sample){
            return "rilievo/plot";
        }
        return "sconosciuto";
    }
    
    /************************************************************************
     * In ambienti diversi da OS X lo sfondo delle linguette 
     * non va disegnato, questa classe serve allo scopo. 
     ***********************************************************************/
    static class NonDisegnaSfondoLinguette extends javax.swing.plaf.basic.BasicTabbedPaneUI{
       protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected ){}
    }
    
    
    /************************************************************************
     * Possiziona un dialogo nel punto specificato
     * @param componente che ha rilevato il click
     * @param click punto in cui è avvenuto il click
     * @param dialogo il dialogo da posizionare
     ***********************************************************************/
    public static void posizionaDialogo(Component componente, Point click, JDialog dialogo){
    	Point outer; 
    	Point sottrai = new Point(0,0);
    	
    	Component padre = componente;
        while(padre!=null && !(padre instanceof JFrame || padre instanceof JDialog) ){
        	if(padre instanceof JScrollPane){
        		JScrollPane s = (JScrollPane) padre;
        		Point p = s.getViewport().getViewPosition();
        		sottrai.x += p.x;
        		sottrai.y += p.y;
        	}
        	padre = padre.getParent();
        }
    	
    	if(padre!=null){
    		outer = padre.getLocation();
    	}else{
    		outer = componente.getLocation();
    	}
    	Point daUsare = new Point(outer.x+click.x-sottrai.x, outer.y+click.y-sottrai.y);
    	dialogo.setLocation(daUsare);
    }
    
    /************************************************************************
     * Risale indietro la gerarchia dei contenitori e dei tipi fino a trovare 
     * una delle classi cercate. Si ferma quando trova un elemento presente
     * in tetto
     * @param c punto da cui iniziare la ricerca
     * @param tetto array di oggetti da cercare
     * @return uno dei valori presenti in tetto o null
     ***********************************************************************/
    @SuppressWarnings("rawtypes")
    public static final Class getContenitoreTra(Component c, Class[] tetto){
        Class combaciaCon = null;
        Class[] interfacce;
        Class superClasse;
        ArrayList<Class> lista;
        Class classe;
        
        do{ // il ciclo principale risale l'albero dei container
            lista = new ArrayList<Class>();
            lista.add(c.getClass());
            // scandisce i tipi che questo ggetto estende/rappresenta
            // l'idea è di mettere in una lista tutti i tipi o le ionterfacce
            // e poi vedere se uno di questi sta in tetto[]
            while(!lista.isEmpty() && combaciaCon==null){
                classe = lista.get(0);
                lista.remove(0);
                // la classe il cima alla lista combacia con una di quelle cercate?
                for(int i=0; i<tetto.length ; i++){
                    if(classe==tetto[i]){
                        combaciaCon = classe;
                        break;
                    }
                }
                if(combaciaCon==null){
                    // inserisco nella lista tutte le interfacce implementate
                    interfacce = classe.getInterfaces();
                    if(interfacce!=null){
                        for(int i=0 ; i<interfacce.length ; i++){
                            lista.add(interfacce[i]);
                        }
                    }
                    // inserisco nella lista la superclasse
                    superClasse = classe.getSuperclass();
                    if(superClasse!=null){
                        lista.add(superClasse);
                    }
                }
            }
            c = c.getParent();
        }while(c!=null && combaciaCon==null);
        return combaciaCon;
    }
    
    /************************************************************************
     * Imposta l'icona per l'applicazione, attualmente soltanto quella 
     * presente nel DOC di OS X
     * @param icona
     ***********************************************************************/
    public static void impostaIcona(ImageIcon icona){
    	// se siamo in  Mac OS X... vogliamo l'icona nel doc!
		try {
			// Questo è un giro decisamente strano, sarebbe possibile farlo utilizzando
			// com.apple.eawt.Application.getApplication().setDockIconImage(Icone.LogoAnArchive.getImage());
			// ma poi darebbe errori anche in compilazione in sistemi non Mac OS X
			Class<?> application = Class.forName("com.apple.eawt.Application");
			Class<?>[] tpApplication = new Class[0];
			Object pApplication[] = new Object[0];
			Method mApplication;
			Class<?>[] tpSetter = new Class[] { Image.class };
			Method settewrForDoc ;
			
			mApplication = application.getMethod("getApplication", tpApplication);
			Object o = mApplication.invoke(application, pApplication);
			settewrForDoc = o.getClass().getMethod("setDockIconImage", tpSetter);
			settewrForDoc.invoke(o, icona.getImage());
		} catch (Exception e) {
			// se qualcosa non va non ci si può fare molto, anche operché
			// questo probabilmente indica che non siamo in Mac OS X
		}
    }
}