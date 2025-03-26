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
package it.aspix.archiver.assistenti;

import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.UtilitaVegetazione;
import it.aspix.archiver.assistenti.RenderTabellaImportazione.Colori;
import it.aspix.sbd.obj.Message;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.SurveyedSpecie;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class DialogoSpecie extends JDialog{
	
	/*************************************************************************
	 * Inserisce una colonna fittizia con u numeri di linea
	 ************************************************************************/
	class RowNumberWrapper implements TableModel{

		TableModel tmBase;
		
		public RowNumberWrapper(TableModel tm){
			tmBase = tm;
		}
		public void addTableModelListener(TableModelListener l) {
			tmBase.addTableModelListener(l);
		}
		public Class<?> getColumnClass(int columnIndex) {
			return tmBase.getColumnClass(columnIndex-1);
		}
		public int getColumnCount() {
			return tmBase.getColumnCount()+1;
		}
		public String getColumnName(int columnIndex) {
			if(columnIndex>0){
				return tmBase.getColumnName(columnIndex-1);
			}else{
				return "nriga";
			}
		}
		public int getRowCount() {
			return tmBase.getRowCount();
		}
		public Object getValueAt(int rowIndex, int columnIndex) {
			if(columnIndex>0){
				return tmBase.getValueAt(rowIndex, columnIndex-1);
			}else{
				return ""+rowIndex;
			}
		}
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if(columnIndex>0){
				return tmBase.isCellEditable(rowIndex, columnIndex-1);
			}else{
				return false;
			}
		}
		public void removeTableModelListener(TableModelListener l) {
			tmBase.removeTableModelListener(l);
		}
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if(columnIndex>0){
				tmBase.setValueAt(aValue, rowIndex, columnIndex-1);
			}else{
				// do nothing (non voglio cambiare i numeri di riga!)
			}
		}
		
	}
	
	private static final long serialVersionUID = 1L;
	DefaultTableModel dati;
	JTable tabella;
	int rigaDaImpostare;
	JTextArea avviso;
	File filePerDump;
	
	public DialogoSpecie(DefaultTableModel dati, BarraAvanzamentoWizard ba, File filePerDump){
		JPanel pannelloGestione = new JPanel(new BorderLayout());
		JButton esporta = new JButton("esporta");
		this.filePerDump = filePerDump;
		this.setTitle("Revisione dei nomi delle specie");
		this.dati = dati;
		
		RowNumberWrapper rowNumberWrapper = new RowNumberWrapper(dati);

		tabella = new JTable(rowNumberWrapper){
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int rowIndex, int colIndex) {
				  return false; //Disallow the editing of any cell
			  }
		};
		JScrollPane scroll = new JScrollPane(tabella);
		JPanel principale = new JPanel(new BorderLayout());
		avviso = new JTextArea();
		
		ba.setDialogo(this);
		principale.add(pannelloGestione, BorderLayout.NORTH);
		principale.add(scroll, BorderLayout.CENTER);
		principale.add(ba, BorderLayout.SOUTH);
		pannelloGestione.add(avviso, BorderLayout.CENTER);
		pannelloGestione.add(esporta, BorderLayout.EAST);
		this.getContentPane().add(principale);
		
		tabella.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent arg0) {
				gestisciClick(arg0);
			}
			private void gestisciClick(MouseEvent e){
				if(e.getClickCount()==1){
					int riga = tabella.rowAtPoint(e.getPoint());
					OggettoConLivello ol = (OggettoConLivello) DialogoSpecie.this.dati.getValueAt(riga,2);
					StringBuilder sb = new StringBuilder();
					avviso.setOpaque(false);
					if(ol.getMessaggi().size()>0){
						MessageType mt = null;
						for(Message m: ol.getMessaggi()){
							sb.append(m.getText(0).getText()+"\n");
							mt= MessageType.max(mt, m.getType());
						}
						switch(mt){
						case ERROR:
							avviso.setOpaque(true);
							avviso.setBackground(Color.RED);
							break;
						case INFO:
							avviso.setOpaque(true);
							avviso.setBackground(Color.GREEN);
							break;
						case WARNING:
							avviso.setOpaque(true);
							avviso.setBackground(Color.YELLOW);
							break;
						}
					}
					avviso.setText(sb.toString());
				}
				if(e.getClickCount()==2){
					rigaDaImpostare = tabella.rowAtPoint(e.getPoint());
					DialogoNomeSpecieRilevata ds = new DialogoNomeSpecieRilevata(tabella, null);
					UtilitaGui.centraDialogoAlloSchermo(ds, UtilitaGui.CENTRO);
					ds.setVisible(true);
					if(ds.getSurveyedSpecie()!=null){
						OggettoConLivello nuovoOggetto = new OggettoConLivello(ds.getSurveyedSpecie(), Level.OFF);
						nuovoOggetto.setEditatoManualmente(true);
						DialogoSpecie.this.dati.setValueAt(nuovoOggetto, rigaDaImpostare, 2);
						aggiornaAvviso();
					}
				}
			}
		});
		esporta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eseguiEsportazione();
			}
		});
		
		tabella.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		avviso.setOpaque(false);
		avviso.setEditable(false);
		avviso.setWrapStyleWord(true);
		avviso.setLineWrap(true);
		avviso.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tabella.getColumnModel().getColumn(2).setMinWidth(200);
		tabella.getColumnModel().getColumn(3).setMinWidth(200);
		tabella.setDefaultRenderer(Object.class, new RenderTabellaImportazione());
		aggiornaAvviso();
	}
	
	private void aggiornaAvviso(){
		int numeroErrori = 0;
		OggettoConLivello o;
		Object x;
		
		for(int riga=0 ; riga<dati.getRowCount() ; riga++){
			x = dati.getValueAt(riga, 2);
			if(x instanceof OggettoConLivello){
				o = (OggettoConLivello) dati.getValueAt(riga, 2);
				if(o.livello==Level.SEVERE){
					numeroErrori++;
				}
			}
		}
		if(numeroErrori==0){
			avviso.setText("Click su una specie per cambiarnel il nome.");
		}else{
			if(numeroErrori==1){
				avviso.setText("Click su una specie per cambiarnel il nome, c'è un errore da correggere.");
			}else{
				avviso.setText("Click su una specie per cambiarnel il nome, ci sono "+numeroErrori+" errori da correggere.");
			}
		}
	}
	
	private void eseguiEsportazione(){
		Colori c;
		String id;
		String nome;
		OggettoConLivello sostituto;
		
		try {
			FileOutputStream fos = new FileOutputStream(filePerDump);
			OutputStreamWriter output = new OutputStreamWriter(fos, "UTF-8");
			output.write(
					"<!DOCTYPE html>\n"+
					"<html lang=\"en\" dir=\"ltr\" >\n"+
					"<head>\n"+
					"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n"+
					"<title>sostituzioni delle specie</title>\n"+
					"<style type=\"text/css\">" +
					"td { border:1px solid gray ; padding: 0.25em }\n");
			for(Level o: RenderTabellaImportazione.colori.keySet()){
				c = RenderTabellaImportazione.colori.get(o);
				output.write("tr."+o+" td { color:"+colorToCss(c.foreground)+"; ");
				if(c.background!=null){
					output.write("background-color: "+colorToCss(c.background));
				}
				output.write("}\n");
			}
			output.write("</style>\n"+
					"</head>\n"+
					"<body>\n"+
					"<table cellpadding=\"0\">\n"+
					"<tr><th>id</th><th>nome</th><th>sostituto</th></tr>\n");
			for(int iRiga=0; iRiga<dati.getRowCount(); iRiga++){
				id = (String) dati.getValueAt(iRiga, 0);
				nome = (String) dati.getValueAt(iRiga, 1);
				if(dati.getValueAt(iRiga, 2) instanceof OggettoConLivello){
					sostituto= (OggettoConLivello) dati.getValueAt(iRiga, 2);
					output.write("<tr class=\""+sostituto.livello+"\">" +
							"<td>"+id+"</td>"+
							"<td>"+nome+"</td>"+
							"<td>"+elaboraNome(sostituto)+"</td>"+
							"</tr>\n");
				}else{
					output.write("<tr>" +
							"<td>"+id+"</td>"+
							"<td>"+nome+"</td>"+
							"<td>"+dati.getValueAt(iRiga, 2)+"</td>"+
							"</tr>\n");
				}
			}
			
			output.write("</table>\n"+
					"</body>\n"+
					"</html>");
			output.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static final String colorToCss(Color c){
		return "rgb("+c.getRed()+","+c.getGreen()+","+c.getBlue()+")";
	}
	
	private String elaboraNome(OggettoConLivello o){
        if( o.oggetto instanceof SurveyedSpecie ){
        	SurveyedSpecie ss = (SurveyedSpecie) o.oggetto;
        	return UtilitaVegetazione.calcolaNomeSpecie(ss.getSpecieRefName(), ss.getDetermination() );
        }else{
        	return o.getStringa();
        }
	}
}
