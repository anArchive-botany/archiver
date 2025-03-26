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

import it.aspix.archiver.CodaCircolare;
import it.aspix.archiver.CostantiGUI;
import it.aspix.archiver.Icone;
import it.aspix.archiver.UtilitaGui;
import it.aspix.archiver.eventi.ValoreException;
import it.aspix.archiver.nucleo.Stato;
import it.aspix.sbd.obj.Message;
import it.aspix.sbd.obj.MessageType;
import it.aspix.sbd.obj.Text;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
/****************************************************************************
 * Visualizza dei messaggi di stato.
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class StatusBar extends JPanel implements GestoreMessaggi {
    
    private static final long serialVersionUID = 1L;
    
    /** se due messaggi arrivano con un desta minore di TOLLERANZA TEMPO vengono
     visualizzati entrambi */
    private static final int TOLLERANZA_TEMPO = 3000;
    /** allo scadere di INTERVALO_PULIZIA la status bar viene pulita */
    private static final int INTERVALLO_PULIZIA = 15000;
    /** utilizzato internamente quando si passa un messaggio nullo */
    private static final String MESSAGGIO_PARAMETRO_NULL = "Passato argomento null come mesaggio, per favore segnala l'errore.";
    
    private JLabel etichetta = new JLabel();
    private Color colorePrioritaMedia;
    private Color colorePrioritaBassa;
    private CodaCircolare<MessaggioSalvato> codaMessaggi = new CodaCircolare<MessaggioSalvato>(10);
    private DateFormat formattatoreData = new SimpleDateFormat("---- yyyy MM dd - HH:mm:ss.S ---------------------------------------");
    private Timer timer;
    
    /************************************************************************
     * Crea gli elementi grafici e inizializza il timer
     ***********************************************************************/
    public StatusBar() {
        JButton dettagli = new JButton();
        try {
            Message avvio = new Message(MessageType.INFO);
            avvio.addText(new Text("it","Gestore dei messaggi avviato."));
            codaMessaggi.add(new MessaggioSalvato(avvio));
            etichetta.setOpaque(true);
            etichetta.setText("barra di stato");
            etichetta.setBorder(BorderFactory.createEmptyBorder(0,2,0,0));
            this.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(0,0,0,0),
                            BorderFactory.createLineBorder(Color.GRAY)
                    )
            );
            this.setLayout(new BorderLayout());
            this.add(etichetta, BorderLayout.CENTER);
            this.add(dettagli, BorderLayout.EAST);
            colorePrioritaBassa = etichetta.getForeground();
            colorePrioritaMedia = CostantiGUI.coloreAttenzione;
            dettagli.setIcon(Icone.ElencoMessaggi);
            
            dettagli.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					mostraAnello();
				}
            });
            dettagli.setBorderPainted(false);
            dettagli.setBorder(BorderFactory.createEmptyBorder());
            dettagli.setOpaque(false);
            // imposto il primo messaggio, serve una partenza per via
            // del timer che fa i conti tra il tmepo attuale e l'ultimo messaggio inserito
            Message messaggio = new Message(MessageType.INFO);
            messaggio.addText(new Text("it","Status bar avviata."));
            
            //timer per il messaggio "pronto"
            timer = new Timer(INTERVALLO_PULIZIA, new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(System.currentTimeMillis()-codaMessaggi.getFromHead(0).time > INTERVALLO_PULIZIA){
                        etichetta.setText("Pronto.");
                        timer.setDelay(INTERVALLO_PULIZIA);
                    }else{
                        timer.setDelay( (int)(INTERVALLO_PULIZIA - (System.currentTimeMillis()-codaMessaggi.getFromHead(0).time)) );
                    }
                }
            });
            timer.setRepeats(true);
            timer.setInitialDelay(INTERVALLO_PULIZIA);
            timer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /************************************************************************
     * @see it.aspix.archiver.componenti.GestoreMessaggi#addMessaggio(java.lang.Exception)
     ***********************************************************************/
    public void addMessaggio(Exception ex) {
        if(ex!=null){
            Message messaggio = new Message();
            MessaggioSalvato msg = new MessaggioSalvato(ex);
            messaggio.addText(new Text("it","Situazione ECCEZIONALE: \"" + ex.getMessage() + "\""));
            messaggio.setType(MessageType.ERROR);
            msg.messaggio = messaggio;
            gestisciMessaggio(msg);
        }else{
            ValoreException eccezione = new ValoreException(MESSAGGIO_PARAMETRO_NULL);
            addMessaggio(eccezione);
        }
    }
    
    /************************************************************************
     * @see it.aspix.archiver.componenti.GestoreMessaggi#addMessaggio(it.aspix.sbd.obj.Message[])
     ***********************************************************************/
    public void addMessaggio(Message[] messaggi) {
        if(messaggi!=null){
            for(int i=0 ; i<messaggi.length ; i++)
                addMessaggio(messaggi[i]);
        }else{
            ValoreException eccezione = new ValoreException(MESSAGGIO_PARAMETRO_NULL);
            addMessaggio(eccezione);
        }
    }

    /************************************************************************
     * @see it.aspix.archiver.componenti.GestoreMessaggi#addMessaggio(it.aspix.sbd.obj.Message)
     ***********************************************************************/
    public void addMessaggio(Message messaggio) {
        if(messaggio!=null){
            gestisciMessaggio(new MessaggioSalvato(messaggio));
        }else{
            ValoreException eccezione = new ValoreException(MESSAGGIO_PARAMETRO_NULL);
            addMessaggio(eccezione);
        }
    }
    
    /************************************************************************
     * Inserisce un messaggio in coda e visualizza le informazioni
     * nella linea di stato
     * @param msg il messaggio da gestire
     ***********************************************************************/
    private void gestisciMessaggio(MessaggioSalvato msg){
        if (msg != null){
            // prima di tutto inserisco il messaggio nell'anello
            codaMessaggi.add(msg);
            if(msg.messaggio.getType().equals(MessageType.ERROR)){
                // messaggio di errore, viene visualizzato in una finestra
                Stato.debugLog.fine("messaggio="+msg);
                UtilitaGui.mostraMessaggioAndandoACapo(msg.toString(), "Errore", JOptionPane.ERROR_MESSAGE);
                etichetta.setText("Pronto.");
            }else{
                // scorro l'anello all'indietro per vedere se ci sono messaggi che sono rimasti
                // visualizzati per troppo poco tempo
                StringBuilder buffer = new StringBuilder(codaMessaggi.getFromHead(0).toString());
                MessaggioSalvato precedente;
                
                for(int i=1 ; i<codaMessaggi.size() ; i++){
                    precedente = codaMessaggi.getFromHead(i);
                    if(precedente==null)
                        break;
                    if( precedente.messaggio.getText(0).getText().endsWith("..."))
                        break;
                    if( msg.time-precedente.time < TOLLERANZA_TEMPO ){
                        buffer.insert(0, ", ");
                        // buffer.insert(0, precedente.messaggio.getText(0).text);
                        buffer.insert(0, precedente.toString());
                    }
                }
                if(msg.messaggio.getType()==MessageType.WARNING){
                    etichetta.setForeground(colorePrioritaMedia);
                }else{
                    etichetta.setForeground(colorePrioritaBassa);
                }
                etichetta.setText(buffer.toString());
                Graphics g = etichetta.getGraphics();
                etichetta.update(g);
            }            
        }        
    }
    
    /************************************************************************
     * Mostra l'anello dei messaggi a schermo
     ***********************************************************************/
    private void mostraAnello(){
    	JDialog dialogo = new JDialog();
    	JPanel pannello = new JPanel(new BorderLayout());
    	JTextArea testo = new JTextArea();
    	JScrollPane scroll = new JScrollPane(testo);
    	StringBuffer messaggi = new StringBuffer();
    	MessaggioSalvato ms;
    	
    	dialogo.setTitle("Anello Messaggi");
    	dialogo.getContentPane().add(pannello);
    	pannello.add(scroll, BorderLayout.CENTER);
    	for(int i=0 ; i<codaMessaggi.size() ; i++){
    		ms = codaMessaggi.getFromBase(i);
    		Date data = new Date(ms.time);
    		messaggi.append(formattatoreData.format(data));
    		messaggi.append('\n');
    		if(ms.messaggio!=null){
    			messaggi.append(ms.messaggio);
    		}
    		if(ms.eccezione!=null){
    			StringWriter sw = new StringWriter();
    			PrintWriter pw = new PrintWriter(sw);
    			ms.eccezione.printStackTrace(pw);
    			messaggi.append('\n');
    			if(ms.eccezione.getCause()!=null){
    			    messaggi.append("Causato da");
    			    ms.eccezione.getCause().printStackTrace(pw);
    			    messaggi.append('\n');
    			}
                messaggi.append(sw.getBuffer());
    		}
    		messaggi.append('\n');
    	}
    	testo.setText(messaggi.toString());
    	dialogo.setModal(true);
    	dialogo.setSize(new Dimension(800,800));
    	dialogo.validate();
    	UtilitaGui.centraDialogoAlloSchermo(dialogo, UtilitaGui.CENTRO);
    	dialogo.setVisible(true);
    }
    
    /************************************************************************
     * Serve solamente per l'anello dei messaggio (per il debug)
     ***********************************************************************/
    private class MessaggioSalvato{
        public long time;
        public Message messaggio;
        public Exception eccezione;
        
        public MessaggioSalvato(Message m){
            time = System.currentTimeMillis();
            messaggio = m;
            eccezione = null;
        }
        
        public MessaggioSalvato(Exception e){
            time = System.currentTimeMillis();
            messaggio = null;
            eccezione = e;
        }
        public String toString(){
            return eccezione!=null 
            		? (eccezione.getMessage() != null ? eccezione.getMessage() : eccezione.toString()) 
            		: (messaggio.getCode()!=null ? messaggio.getCode()+": " : "")+ messaggio.getText(0).getText();
        }
    }
    
}