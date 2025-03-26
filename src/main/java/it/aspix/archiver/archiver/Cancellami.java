package it.aspix.archiver.archiver;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import it.aspix.archiver.componenti.ComboBoxModelED;
import it.aspix.archiver.componenti.ComboBoxModelEDFactory;
import it.aspix.archiver.componenti.ComboBoxModelEDFactory.TipoCombo;
import it.aspix.sbd.introspection.InformazioniTipiEnumerati;
import it.aspix.sbd.introspection.ValoreEnumeratoDescritto;
import it.aspix.archiver.componenti.CoppiaED;

class Cancellami{
    public static void main(String args[]){
    	ArrayList<ValoreEnumeratoDescritto> alc = 
    			InformazioniTipiEnumerati.getValoriDescritti(TipoCombo.DETERMINAZIONE.proprietaSBD(),"it");
    	for(int i=0;i<alc.size();i++) {
    		System.out.println(alc.get(i));
    	}
    	System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^");
    	
       JFrame frame = new JFrame("My First GUI");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(300,200);
       
       ComboBoxModelED k = ComboBoxModelEDFactory.createComboBoxModelED(TipoCombo.DETERMINAZIONE);
       System.out.println(k.getSize());
       for(int i=0; i<k.getSize(); i++) {
    	   System.out.println(k.getElementAt(i).getClass().getSimpleName());
       }
       
       JButton button = new JButton("Press");
       // JComboBox<CoppiaCSTesto> cb = new JComboBox<>();
       JComboBox<CoppiaED> cb = new JComboBox<>(k);
       
       frame.getContentPane().setLayout( new BorderLayout() );
       frame.getContentPane().add(button , BorderLayout.NORTH);
       frame.getContentPane().add(cb , BorderLayout.SOUTH);
       frame.setVisible(true);
    }
}
