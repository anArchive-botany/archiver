/****************************************************************************
 * Copyright 2020 studio Aspix 
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

import java.awt.Component;
import java.awt.Desktop;
import java.net.URI;

import it.aspix.archiver.nucleo.Dispatcher;
import it.aspix.sbd.obj.MessageType;

/****************************************************************************
 * Metodi per utilizzare semplicemente un browser per aprire una pagina on-line
 * 
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class DesktopApiWrapper {
	
	/************************************************************************
	 * FIXME: sarebbe il caso di fare il controllo qui se DesktopApi
	 * è disponibile o no
	 * @param sorgente componente che chiede l'apertura del browser
	 * @param link da aprire nel browser
	 ***********************************************************************/
	public static void openLink(Component sorgente, String link){
        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(link));
            } catch (Exception e1) {
                Dispatcher.consegna(sorgente, e1);
            }
        }else{
            Dispatcher.consegna(sorgente, CostruttoreOggetti.createMessage("Non è possibile aprire il browser", "it", MessageType.WARNING));
        }
	}
	
	/************************************************************************
	 * @return ritorna true se desktopApi è disponibile
	 ***********************************************************************/
	public static boolean isDesktopApiBrowseAvailable(){
		try{
			return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
		}catch(Error e){
			// qualsiasi cosa vada male vuol dire che non posso usare desktop API
			return false;
		}
	}
	
}
