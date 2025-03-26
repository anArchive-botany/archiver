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

import it.aspix.archiver.nucleo.Dispatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

/****************************************************************************
 * Metodi di utilità per trattare con tabelle, principalmente
 * interfacce verso POI e ODF toolkit 
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class TablesUtils {
	
	/************************************************************************
	 * Carica un file utilizzando POI o ODF Toolkit
	 * @param f nome del file da caricare
	 * @return un modello da usare in una JTable
	 * @throws Exception
	 ***********************************************************************/
	public static DefaultTableModel loadTable(File f) throws Exception{
		String estensione = f.toString();
		DefaultTableModel contenutoTabella = null;
		
		estensione = estensione.substring(estensione.lastIndexOf('.')+1);
		if(estensione.equalsIgnoreCase("xls") || estensione.equalsIgnoreCase("xlsx")){
			contenutoTabella = loadTableXls(f, estensione, 0);
		}
		if(estensione.equalsIgnoreCase("ods")){
			contenutoTabella = loadTableOds(f, 0);
		}
		// controllo per eventuali caselle null
		for(int riga=0 ; riga<contenutoTabella.getRowCount() ; riga++){
			for(int colonna=0 ; colonna<contenutoTabella.getColumnCount(); colonna++){
				if(contenutoTabella.getValueAt(riga, colonna)==null){
					contenutoTabella.setValueAt("", riga, colonna);
				}
			}
		}
		return contenutoTabella;
	}
	
	/************************************************************************
	 * Legge un file utilizzando POI
	 * @param f nome del file
	 * @param estensione usata per discriminare tra vecchi e nuovi file di excel
	 * @param pagina il numero della pagina da leggere
	 * @return 
	 * @throws IOException
	 ***********************************************************************/
	private static DefaultTableModel loadTableXls(File f, String estensione, int pagina) throws IOException{
		DefaultTableModel contenutoTabella = new DefaultTableModel();
		InputStream s;
		Sheet foglio;
		Row riga;
		Cell cella;
		s = new FileInputStream(f);
		int max=0;
		double d;
		Workbook wb;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		// il workbook cambia in funzione dell'estensione
		if(estensione.equals("xls")){
			wb = new HSSFWorkbook(s);
		}else{
			wb = new XSSFWorkbook(s);
		}
		// mi interessa soltnato il primo foglio
		foglio = wb.getSheetAt(pagina);
		

		// per calcolare la larghezza del foglio guardo le prime 20 righe
		// non si può usare semplicemnete getLastRowNum() perché conta anche le celle vuote
		int maxRiga;
		for(int iRiga=0; iRiga<foglio.getLastRowNum() && iRiga<=20; iRiga++){
			riga = foglio.getRow(iRiga);
			maxRiga=0;
			for(int i=0; i<riga.getLastCellNum(); i++){
				if(riga.getCell(i)!=null && riga.getCell(i).toString().length()>0){
					maxRiga = i;
				}
			}
			if(maxRiga>max){
				max=maxRiga;
			}
		}
		contenutoTabella.setRowCount(foglio.getLastRowNum()+1);
		contenutoTabella.setColumnCount(max+1);
		
		// carico i dati
		for(int i=0; i<=foglio.getLastRowNum(); i++){
			riga = foglio.getRow(i);
			if(riga!=null){
				for(int j=0; j<=max; j++){
					cella = riga.getCell(j);
					if(cella!=null){
						cella.getCellType();
						switch(cella.getCellType()){
						case STRING:
							contenutoTabella.setValueAt(cella.getStringCellValue(), i, j);
							break;
						case NUMERIC:
							// in questo caso cadono anche i tipi data
						    if (DateUtil.isCellDateFormatted(cella)) {
						    	contenutoTabella.setValueAt(sdf.format(cella.getDateCellValue()), i, j);
						    } else {
								d = cella.getNumericCellValue();
								if(d != (int)d){
									contenutoTabella.setValueAt(cella.getNumericCellValue(), i, j);
								}else{
									contenutoTabella.setValueAt((int) cella.getNumericCellValue(), i, j);
								}
						    }
							break;
						case BLANK:
							contenutoTabella.setValueAt("", i, j);
							break;
						default:
							Dispatcher.consegna(null, new Exception("Non gestisco la cella di tipo "+cella.getCellType()));
						}
						
					}
				}
			}
		}wb.close();
		return contenutoTabella;
	}
	
	/************************************************************************
	 * Legge un file utilizzando ODF Toolkit
	 * @param f nome del file
	 * @param pagina il numero della pagina da leggere
	 * @return
	 * @throws Exception
	 ***********************************************************************/
	private static DefaultTableModel loadTableOds(File f, int pagina) throws Exception{
		DefaultTableModel contenutoTabella = new DefaultTableModel();
		OdfSpreadsheetDocument documento;
		int colonneDaControllare;
		int righeDaControllare;
		int righeUtili = 0;
		int colonneUtili = 0;
		OdfTableRow riga;
		int lun;
		
		documento = OdfSpreadsheetDocument.loadDocument(f);
		OdfTable foglio = documento.getTableList().get( pagina );

		// è possibile che il numero riornato sia molto più grande delle righe/colonne
		// realmente utili, per queste si cercano una specie di bounding box
		colonneDaControllare = foglio.getColumnCount()<3 ? foglio.getColumnCount() : 3;
		for(int i=0; i<foglio.getRowCount() ; i++){
			riga = foglio.getRowByIndex(i);
			lun = 0;
			for(int j=0 ; j<colonneDaControllare ; j++){
				lun += riga.getCellByIndex(j).getDisplayText().length();
			}
			if(lun!=0){
				righeUtili = i;
			}
			if(i-righeUtili>3){
				break;
			}
		}
		righeDaControllare = foglio.getRowCount()<2 ? foglio.getRowCount() : 2;
		for(int i=0; i<foglio.getColumnCount() ; i++){
			lun = 0;
			for(int j=0 ; j<righeDaControllare ; j++){
				lun += foglio.getRowByIndex(j).getCellByIndex(i).getDisplayText().length();
			}
			if(lun!=0){
				colonneUtili = i;
			}
			if(i-colonneUtili>3){
				break;
			}
		}
		contenutoTabella.setColumnCount(colonneUtili+1);
		contenutoTabella.setRowCount(righeUtili+1);
		
		// carico i dati
		for(int i=0; i<=righeUtili ; i++){
			riga = foglio.getRowByIndex(i);
			for(int j=0 ; j<=colonneUtili; j++){
				contenutoTabella.setValueAt(riga.getCellByIndex(j).getDisplayText(), i, j);
			}
		}
		
		return contenutoTabella;
	}
	
	/************************************************************************
	 * Metodo di utilità per inserire una colonna
	 * @param indice della colonna da inserire
	 * @param testo da inserire nella colonna
	 * @param nomeColonna "header"
	 ***********************************************************************/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void inserisciColonna(DefaultTableModel contenutoTabella, int indice, String testo, String nomeColonna){
		Vector<Vector> dataVector;
		Vector<String> columnIdentifiers = new Vector<String>();
		
		// recupero i dati attualmente visualizzati;
		dataVector = contenutoTabella.getDataVector();
		// recupero gli identificatori delle colonne
		for(int i=0 ; i<contenutoTabella.getColumnCount() ; i++){
			columnIdentifiers.add(contenutoTabella.getColumnName(i));
		}
		for(int i=0; i<dataVector.size(); i++){
			dataVector.get(i).insertElementAt(testo, indice);
		}
		columnIdentifiers.insertElementAt(nomeColonna,indice);

		contenutoTabella.setDataVector(dataVector, columnIdentifiers);
	}
	
	/************************************************************************
	 * Metodo di utilità per eliminare una colonna
	 * @param contenutoTabella modello dei dati 
	 * @param indice della colonna da rimuovere
	 ***********************************************************************/
	@SuppressWarnings({"rawtypes" })
	public static void eliminaColonna(DefaultTableModel contenutoTabella, int indice){
		Vector<Vector> dataVector;
		Vector<String> columnIdentifiers = new Vector<String>();
		
		// recupero i dati attualmente visualizzati;
		dataVector = contenutoTabella.getDataVector();
		// recupero gli identificatori delle colonne
		for(int i=0 ; i<contenutoTabella.getColumnCount() ; i++){
			columnIdentifiers.add(contenutoTabella.getColumnName(i));
		}
		
		for(int i=0; i<dataVector.size(); i++){
			dataVector.get(i).removeElementAt(indice);
		}
		columnIdentifiers.removeElementAt(indice);

		contenutoTabella.setDataVector(dataVector, columnIdentifiers);
	}
	
	/************************************************************************
	 * @param dati modello in cui inserire la riga
	 * @param indice indice della riga da inserire
	 * @param col0 valore della prima colonna
	 * @param col1 valore della seconda colonna
	 * @param colX valore di tutte le altre colonne
	 ***********************************************************************/
	public static void inserisciRiga(DefaultTableModel dati, int indice, String col0, String col1, String colX){
		int numeroDiColonne;
		numeroDiColonne = dati.getColumnCount();
		String nuovaRiga[] = new String[numeroDiColonne];
		
		nuovaRiga[0] = col0;
		nuovaRiga[1] = col1;
		for(int i=2; i<numeroDiColonne; i++){
			nuovaRiga[i] = colX;
		}
		
		dati.insertRow(0, nuovaRiga);
	}
	
	public static boolean isVuota(DefaultTableModel dati, int indice){
		Vector<?> x = dati.getDataVector();
		Vector<?> riga = (Vector<?>) x.get(indice);
		boolean vuota = true;
		
		for(int i=0; i<10 && i<riga.size() && vuota; i++){
			vuota &= (riga.get(i)==null || ( riga.get(i) instanceof String && ((String)(riga.get(i))).length()==0) );
		}
		return vuota;
	}
	
}
