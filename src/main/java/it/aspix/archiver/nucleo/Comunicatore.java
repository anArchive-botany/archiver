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
package it.aspix.archiver.nucleo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import it.aspix.archiver.nucleo.Cache.ContenutoCache;
import it.aspix.sbd.introspection.ReflectUtil;
import it.aspix.sbd.obj.Attribute;
import it.aspix.sbd.obj.AttributeInfo;
import it.aspix.sbd.obj.Blob;
import it.aspix.sbd.obj.Link;
import it.aspix.sbd.obj.NameList;
import it.aspix.sbd.obj.OggettoSBD;
import it.aspix.sbd.obj.Rights;
import it.aspix.sbd.obj.Sample;
import it.aspix.sbd.obj.SimpleBotanicalData;
import it.aspix.sbd.obj.SpecieRef;
import it.aspix.sbd.obj.SpecieSpecification;
import it.aspix.sbd.obj.Specimen;
import it.aspix.sbd.saxHandlers.SHSimpleBotanicalData;
import it.aspix.sbd.AnalizzatoreDOM;

/************************************************************************************************
 * il Comunicatore si occupa di trasformare tutte le richieste in stringhe XML
 * e viceversa tutte le risposte in dati fruibili dal resto del programma
 ***********************************************************************************************/
public class Comunicatore {

    public static enum SuggerimentiGeografici {LOCALITA, COMUNE};

    private static enum MetodoHttp { GET, POST, PUT, DELETE }; // TODO: e li devo definire io??
    private static final int TIMEOUT_CONNESSIONE = 10000;

    /** viene usato per inviare al server il nome dell'applicaizone in uso */
    private String softwareName=null;
    /** viene usato per inviare al server la versione dell'applicaizone in uso */
    private String softwareVersion=null;

    /** usato per la comunicazione con il server */
    private static HttpClient httpClient;
    /** una volta recuperato il messaggio dal server
     si usa lo stesso parser per analizzare la risposta */
    private SAXParser parserSAX;
    /** utilizzata per generare gli id degli oggetti */
    private SimpleDateFormat dateFormat;
    /** la cache accumula una serie di dati recuperati dal server */
    private Cache cache;

    /** mappa on oggetto nel relativo servizio che lo gestisce */
    private static HashMap<Class<?>, String> mappaServizi = new HashMap<>();
    static {
        mappaServizi.put(Specimen.class, "specimen");
        mappaServizi.put(Sample.class, "sample");
        mappaServizi.put(SpecieSpecification.class, "specieSpecification");
        mappaServizi.put(SpecieRef.class, "specieSpecification");
        mappaServizi.put(Blob.class, "blob");
        mappaServizi.put(Link.class, "link");
    };

    /********************************************************************************************
     * @param softwareName il nome del software che verrà inviato al server
     * @param softwareVersion la versione del software che verrà inviata al server
     *******************************************************************************************/
    public Comunicatore(String softwareName, String softwareVersion) {
        this.softwareName = softwareName;
        this.softwareVersion = softwareVersion;
        try {
            SAXParserFactory fpf = SAXParserFactory.newInstance();
            fpf.setValidating(false);
            parserSAX = fpf.newSAXParser();
            dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(TIMEOUT_CONNESSIONE))
                    .version(Version.HTTP_1_1)
                    .build();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        cache = new Cache();
    }

    /********************************************************************************************
     * FIXME: "ApprossimaNomeSpecie" non esiste come servizio sul server
     *******************************************************************************************/
    public SimpleBotanicalData approssimaNome(String nome) throws SAXException, IOException, InterruptedException, URISyntaxException {
        SimpleBotanicalData sbd = new SimpleBotanicalData();
        SimpleBotanicalData risposta;
        NameList nl = new NameList();

        // poi si controlla se l'entrata è già presente in cache concatenando
        // il tipo siccome frammento potrebbe essere null
        risposta = (SimpleBotanicalData) cache.retrieveElement( ContenutoCache.NOME, nome);
        if (risposta == null) {
            // si procede con la richiesta verso il server
            nl.setType("specieName");
            nl.addName(nome);
            // sbd.setIdentity(getIdentity());
            sbd.addNameList(nl);
            // FIXME: quello qui sotto  non esiste su server
            // lo usano gli importatori non direttamente archiver
            risposta = inviaRicevi("ApprossimaNomeSpecie", null, sbd);
            if(risposta.getNameList()!=null){
                cache.putElement(ContenutoCache.NOME, nome, risposta);
            }
        }else{
            Stato.debugLog.fine("prendo il nome dalla cache");
        }
        return risposta;
    }

    /********************************************************************************************
     * FIXME: "ControllaElencoNomi" non esiste come servizio sul server
     *******************************************************************************************/
    public SimpleBotanicalData controllaListaNomiSpecie(String nomi[]) throws SAXException, IOException, InterruptedException, URISyntaxException {
    	SimpleBotanicalData sbd = new SimpleBotanicalData();
        SimpleBotanicalData risposta;
        NameList nl = new NameList();

        nl.setType("specieName");
        for(int i=0; i<nomi.length; i++){
        		nl.addName(nomi[i]);
        }
        // sbd.setIdentity(getIdentity());
        sbd.addNameList(nl);
        risposta = inviaRicevi(MetodoHttp.POST,"nomiSpecie/list", null, null, false, sbd);

        return risposta;
    }


    /********************************************************************************************
     * Ricerca un elenco di nomi
     * @param tipoLista una delle costanti di it.aspix.sbd.obj.NameList
     * @param contenutiIn il container che deve contenere i nomi
     * @param frammento usato per la ricerca
     * @param ancheInterno se true inserisce il suggerimento "internalSubstring"
     * @return un oggetto RispostaObject il cui campo oggetto contine un Vector
     * @throws IOException
     * @throws SAXException
     * @throws URISyntaxException
     * @throws InterruptedException
     *******************************************************************************************/
    public SimpleBotanicalData recuperaNomi(String tipoLista, String contenutiIn, String frammento, boolean ancheInterno) throws SAXException, IOException, InterruptedException, URISyntaxException {
        SimpleBotanicalData sbd = new SimpleBotanicalData();
        SimpleBotanicalData risposta;

        risposta = (SimpleBotanicalData) cache.retrieveElement( ContenutoCache.NOME, tipoLista+contenutiIn+frammento);
        if (risposta == null) {
            // si procede con la richiesta verso il server
        		StringBuilder sb = new StringBuilder("set="+tipoLista);
            if(frammento!=null) {
            		sb.append("&q=" + URLEncoder.encode(frammento, "UTF-8") );
            }
            if(contenutiIn!=null) {
            		sb.append("&container=" + URLEncoder.encode(contenutiIn, "UTF-8") );
            }
            if(ancheInterno) {
            		sb.append("&inner=true");
            }
            risposta = inviaRicevi("nomi", sb.toString(), sbd);
            if(risposta.getNameList()!=null) {
                cache.putElement(ContenutoCache.NOME, tipoLista+frammento, risposta);
            }
        }else{
            Stato.debugLog.fine("prendo i nomi dalla cache");
        }
        return risposta;
    }

    /********************************************************************************************
     * Recupera le informazioni su tutti gli attributi disponibili
     * @return un oggetto RispostaObject il cui campo oggetto contine un Vector
     * @throws IOException
     * @throws SAXException
     * @throws URISyntaxException
     * @throws InterruptedException
     *******************************************************************************************/
    public SimpleBotanicalData recuperaInformazioniAttributi() throws SAXException, IOException, InterruptedException, URISyntaxException {
        SimpleBotanicalData risposta;

        risposta = (SimpleBotanicalData) cache.retrieveElement( ContenutoCache.LISTA_ATTRIBUTI, "");
        if (risposta == null) {
            // FIXME sbd.setIdentity(getIdentity()); forse non serve più, ce ne sono altre in giro
            risposta = inviaRicevi("attributi", null, null);
            if(risposta.getAttributeInfo()!=null)
                cache.putElement(ContenutoCache.LISTA_ATTRIBUTI, "", risposta);
        }else{
            Stato.debugLog.fine("prendo i gli attributi dalla cache");
        }
        return risposta;
    }

    /********************************************************************************************
     * @param attributo di cui cercare il descrittore
     * @return il descrittore dell'attributo
     * @throws SAXException
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     *******************************************************************************************/
    public AttributeInfo getAttributeInfo(Attribute attributo) throws SAXException, IOException, InterruptedException, URISyntaxException{
        SimpleBotanicalData info;
        if(cache.retrieveElement(ContenutoCache.LISTA_ATTRIBUTI, "") == null){
        	info = recuperaInformazioniAttributi();
        }else{
        	info = (SimpleBotanicalData) cache.retrieveElement(ContenutoCache.LISTA_ATTRIBUTI, "");
        }
        AttributeInfo[] elenco = info.getAttributeInfo();
        for(int i=0; i<elenco.length;i++){
        	if(attributo.getName().equals(elenco[i].getName())){
        		return elenco[i];
        	}
        }
        return null;
    }

    /********************************************************************************************
     * @param nome della località o del comune
     * @param tipo una delle costanti LOCALITA o COMUNE che
     *        descrive il contenuto di "nome"
     * @return l'ogggetto SimpleBotanicalData ricevuto dal server
     * @throws SAXException
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     *******************************************************************************************/
    public SimpleBotanicalData suggerimentiGeografici(String nome, SuggerimentiGeografici tipo) throws SAXException, IOException, InterruptedException, URISyntaxException {
        SimpleBotanicalData risposta;
        String parametri;

        if (tipo == SuggerimentiGeografici.LOCALITA) {
        	// TODO: qui e in altri punti quale è l'encoding da usare nelle URL?
        	parametri = "localita="+URLEncoder.encode(nome, "UTF-8");
        }else {
        	parametri = "comune="+URLEncoder.encode(nome, "UTF-8");
        }
        //sbd.setIdentity(getIdentity());
        SimpleBotanicalData recuperati= (SimpleBotanicalData) cache.retrieveElement((tipo == SuggerimentiGeografici.LOCALITA ? ContenutoCache.LOCALITA : ContenutoCache.COMUNE), nome);
        if (recuperati != null) {
            Stato.debugLog.fine("prendo i suggerimenti dalla cache");
            return recuperati;
        }
        risposta = inviaRicevi("datiGeografici", parametri, null);
        if(risposta.getPlace()!=null){
            cache.putElement((tipo == SuggerimentiGeografici.LOCALITA ? ContenutoCache.LOCALITA : ContenutoCache.COMUNE), nome, risposta);
        }

        return risposta;
    }

    /************************************************************************
     * @param epsg del punto da convertire
     * @param x
     * @param y
     * @return l'ogggetto SimpleBotanicalData ricevuto dal server
     * @throws SAXException
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     ***********************************************************************/
    public SimpleBotanicalData conversioneCoordinate(String epsg, String x, String y) throws SAXException, IOException, InterruptedException, URISyntaxException {
        SimpleBotanicalData risposta;
        String parametri="x="+x+"&y="+y+"&rpsg="+epsg;

        Stato.debugLog.fine("richiesta conversione");
        // TODO: anche qui si può usare la cache
        risposta = inviaRicevi("coordinateWGS84", parametri, null);
        return risposta;
    }


    /************************************************************************
     * Richiede i suggerimenti dal server e costruisce un vettore di stringhe
     * @return l'ogggetto SimpleBotanicalData ricevuto dal server
     * @throws IOException
     * @throws SAXException
     * @throws URISyntaxException
     * @throws InterruptedException
     ***********************************************************************/
    public SimpleBotanicalData suggerimentiSpecie(String prefisso) throws SAXException, IOException, InterruptedException, URISyntaxException {
        SimpleBotanicalData risposta;
        NameList nl = new NameList();

        // poi si controlla se l'entrata è già presente in cache
        risposta = (SimpleBotanicalData) cache.retrieveElement(ContenutoCache.SPECIE, prefisso.substring(0, 3));
        if (risposta == null) {
            // si procede con la richiesta verso il server
            nl.setType("specieName");
            nl.addName(prefisso);
            //sbd.setIdentity(getIdentity());
            risposta = inviaRicevi("nomiSpecie", "q="+prefisso, null);
            if(risposta.getNameList()!=null)
                cache.putElement(ContenutoCache.SPECIE, prefisso, risposta);
        }else{
            Stato.debugLog.fine("prendo i suggerimenti dalla cache");
        }

        return risposta;
    }

    /************************************************************************
     * @param nomeContenitore nei confronti del quale verificare i diritti
     * @return l'ogggetto SimpleBotanicalData ricevuto dal server
     * @throws SAXException
     * @throws IOException
     ***********************************************************************/
    public Rights verificaAccessoContenitore(String nomeContenitore) throws SAXException, IOException, InterruptedException, URISyntaxException {
        SimpleBotanicalData risposta;

        risposta = (SimpleBotanicalData) cache.retrieveElement(ContenutoCache.ACCESSO_CONTENITORE, nomeContenitore);
        if(risposta==null){
	        risposta = inviaRicevi(MetodoHttp.GET, "rights", null,
	                "user="+Proprieta.recupera("connessione.nome")+"&container="+nomeContenitore, false, null);
	        cache.putElement(ContenutoCache.ACCESSO_CONTENITORE, nomeContenitore, risposta);
        }
        return risposta.getRights(0);
    }

    // FIXME: mancano commenti di intestazione e usa un meccanismo atipico, sarebbe bene rifarla
    public boolean login() throws URISyntaxException, IOException, InterruptedException{
        String url = Proprieta.recupera("connessione.URL")+"/login";
        Stato.debugLog.fine(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Accept", "application/xml")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(BodyPublishers.ofString("nome="+Proprieta.recupera("connessione.nome")+"&password="+Proprieta.recupera("connessione.password")))
                .build();
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        return response.statusCode() == 200;
    }

    /************************************************************************
     * @param nomeContenitore di cui si vogliono avere le informazioni
     * @return l'ogggetto SimpleBotanicalData ricevuto dal server
     * @throws SAXException
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     ***********************************************************************/
    public SimpleBotanicalData informazioniContenitore(String nomeContenitore) throws SAXException, IOException, InterruptedException, URISyntaxException {
        SimpleBotanicalData risposta;

        risposta = (SimpleBotanicalData) cache.retrieveElement(ContenutoCache.INFORMAZIONI_CONTENITORE, nomeContenitore);
        if(risposta==null){
            risposta = inviaRicevi(MetodoHttp.GET, "container", nomeContenitore, null, false, null);
	        cache.putElement(ContenutoCache.INFORMAZIONI_CONTENITORE, nomeContenitore, risposta);
        }
        return risposta;
    }

    /************************************************************************
     * @param o l'oggetto da cercare
     * @param suggerimento da inviare al server per la ricerca
     * @return la risposta del server
     * @throws SAXException
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     * @throws InvalidValue
     ***********************************************************************/
    public SimpleBotanicalData cercaVecchio(OggettoSBD o, String suggerimento) throws SAXException, IOException, IllegalArgumentException, InterruptedException, URISyntaxException {
        SimpleBotanicalData richiesta = new SimpleBotanicalData();
    	    SimpleBotanicalData risposta;
    	    String chiaveCache = null;

        	// richiesta.setIdentity(getIdentity());
        	if(o instanceof Specimen){
        		richiesta.addSpecimen((Specimen) o);
        	}else if(o instanceof Sample){
        		richiesta.addSample((Sample) o);
        	}else if(o instanceof SpecieSpecification || o instanceof SpecieRef){
        		if(o instanceof SpecieSpecification){
        			SpecieSpecification x = (SpecieSpecification) o;
        			if(x.getPublicationRef()!=null){
        				chiaveCache = suggerimento+x.getFamily()+x.toString()+x.getPublicationRef().toString();
        			}else{
        				chiaveCache = suggerimento+x.getFamily()+o.toString();
        			}
        			richiesta.addSpecieSpecification((SpecieSpecification) o);
        		}else{
        			chiaveCache = suggerimento+o.toString();
        			richiesta.addSpecieRef((SpecieRef)o);
        		}
        		if(Proprieta.recupera("generale.cacheInfoSpecie").equals("true")){
        			SimpleBotanicalData sbd = (SimpleBotanicalData) cache.retrieveElement(ContenutoCache.SPECIE_INFO,chiaveCache);
        			if(sbd!=null){
        				Stato.debugLog.fine("prendo i dati dalla cache");
        				return sbd;
        			}
        		}
        	}else if(o instanceof Blob){
        		richiesta.addBlob((Blob) o);
        	}else if(o instanceof Link){
        		richiesta.addLink((Link) o);
        	}else{
        		throw new IllegalArgumentException("Non posso cercare "+o.getClass().getCanonicalName());
        	}
        	risposta = inviaRicevi(MetodoHttp.GET, mappaServizi.get(o.getClass()), null, suggerimento, false, richiesta);
        	if(o instanceof SpecieSpecification || o instanceof SpecieRef){
        		cache.putElement(ContenutoCache.SPECIE_INFO, chiaveCache, risposta);
        	}
        	return risposta;
    }

    // FIXME: ignora il suggerimento
    public SimpleBotanicalData cerca(OggettoSBD o, String suggerimento) throws Exception {
            SimpleBotanicalData risposta;
            // TODO: saltata implementazione cache, da rifare

            String servizio = mappaServizi.get(o.getClass())+"/cerca";
            String parametriURL = ReflectUtil.getHttpQueryString(o);
            risposta = inviaRicevi(MetodoHttp.GET, servizio, null, parametriURL, false, null);
            return risposta;
    }

    /************************************************************************
     * @param o l'oggetto da inserire
     * @param suggerimento da inviare al server per l'inserimento
     * @param simulazione true se non si vuole soltanto una simulazione
     * @return la risposta del server
     * @throws SAXException
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     * @throws InvalidValue
     ***********************************************************************/
    public SimpleBotanicalData inserisci(OggettoSBD o, String suggerimento, boolean simulazione) throws SAXException, IOException, IllegalArgumentException, InterruptedException, URISyntaxException {
        SimpleBotanicalData richiesta = new SimpleBotanicalData();
        SimpleBotanicalData risposta;

        // richiesta.setIdentity(getIdentity());
        if(o instanceof Specimen){
            richiesta.addSpecimen((Specimen)o);
        }else if(o instanceof Sample){
            richiesta.addSample((Sample)o);
        }else if(o instanceof SpecieSpecification){
            richiesta.addSpecieSpecification((SpecieSpecification)o);
            // XXX: è drastico
            cache.rimuoviTutti(ContenutoCache.SPECIE_INFO);
        }else if(o instanceof Blob){
            richiesta.addBlob((Blob)o);
        }else if(o instanceof Link){
            richiesta.addLink((Link)o);
        }else{
            throw new IllegalArgumentException("Non posso inserire "+o.getClass().getCanonicalName());
        }
        risposta = inviaRicevi(MetodoHttp.POST, mappaServizi.get(o.getClass()), null, suggerimento, simulazione, richiesta);
        return risposta;
    }

    /************************************************************************
     * @param o l'oggetto da modificare
     * @param suggerimento da inviare al server per la modifica
     * @param simulazione true se non si vuole soltanto una simulazione
     * @return la risposta del server
     * @throws SAXException
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     * @throws InvalidValue
     ***********************************************************************/
    public SimpleBotanicalData modifica(OggettoSBD o, String suggerimento, boolean simulazione) throws SAXException, IOException, IllegalArgumentException, InterruptedException, URISyntaxException {
        SimpleBotanicalData richiesta = new SimpleBotanicalData();
        SimpleBotanicalData risposta;
        String idOggetto;

        // richiesta.setIdentity(getIdentity());
        if(o instanceof Specimen){
            richiesta.addSpecimen((Specimen)o);
            idOggetto = ((Specimen)o).getId();
        }else if(o instanceof Sample){
            richiesta.addSample((Sample)o);
            idOggetto = ((Sample)o).getId();
        }else if(o instanceof SpecieSpecification){
            richiesta.addSpecieSpecification((SpecieSpecification)o);
            idOggetto = ((SpecieSpecification)o).getId();
            // XXX: è drastico
            cache.rimuoviTutti(ContenutoCache.SPECIE_INFO);
        }else if(o instanceof Blob){
            richiesta.addBlob((Blob)o);
            idOggetto = ((Blob)o).getId();
        }else{
            throw new IllegalArgumentException("Non posso inserire "+o.getClass().getCanonicalName());
        }

        // FIXME: i suggerimenti qui vengono inseriti male, o si sistema qui o in invia/ricevi
        risposta = inviaRicevi(MetodoHttp.PUT, mappaServizi.get(o.getClass()), idOggetto, suggerimento, simulazione, richiesta);
        return risposta;
    }

    /************************************************************************
     * @param o l'oggetto da rimuovere
     * @param suggerimento da inviare al server per la rimozione
     * @param simulazione true se non si vuole soltanto una simulazione
     * @return la risposta del server
     * @throws SAXException
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     * @throws InvalidValue
     ***********************************************************************/
    public SimpleBotanicalData rimuovi(OggettoSBD o, String suggerimento, boolean simulazione) throws SAXException, IOException, IllegalArgumentException, InterruptedException, URISyntaxException {
        SimpleBotanicalData richiesta = new SimpleBotanicalData();
        SimpleBotanicalData risposta;
        String idOggetto;

        // richiesta.setIdentity(getIdentity());
        if(o instanceof Specimen){
            richiesta.addSpecimen((Specimen)o);
            idOggetto = ((Specimen)o).getId();
        }else if(o instanceof Sample){
            richiesta.addSample((Sample)o);
            idOggetto = ((Sample)o).getId();
        }else if(o instanceof SpecieSpecification){
            richiesta.addSpecieSpecification((SpecieSpecification)o);
            idOggetto = ((SpecieSpecification)o).getId();
            // XXX. sempre cosa drastica
            cache.rimuoviTutti(ContenutoCache.SPECIE_INFO);
        }else if(o instanceof Blob){
            richiesta.addBlob((Blob)o);
            idOggetto = ((Blob)o).getId();
        }else if(o instanceof Link){
            richiesta.addLink((Link)o);
            idOggetto = null;
        }else{
            throw new IllegalArgumentException("Non posso rimuovere "+o.getClass().getCanonicalName());
        }
        risposta = inviaRicevi(MetodoHttp.DELETE, mappaServizi.get(o.getClass()), idOggetto, suggerimento, simulazione, richiesta);
        return risposta;
    }


    // ######################################################################
    // METODI DI UTILITÀ METODI DI UTILITÀ METODI DI UTILITÀ METODI DI UTILIT
    // ETODI DI UTILITÀ METODI DI UTILITÀ METODI DI UTILITÀ METODI DI UTILITÀ
    // ######################################################################

    /************************************************************************
     * Metodo ponte per l'accesso alla cache, a volte un editor può con
     * maggiore facilità sapere se una entrata è da rimuovere
     * @param type
     * @param name il nome della chiave nella cache
     ***********************************************************************/
    public void rimuoviDallaCache(ContenutoCache type, String name){
        cache.removeElement(type, name);
    }

    /************************************************************************
     * richiest a GET senza idOggetto ne simulazione ne entity
     * @param pathServizio
     * @param parametri
     * @param oggetto da inviare al server
     * @return il valore ritornato da inviaRicevi(HttpGet.METHOD_NAME, pathServizio, null, parametri, false, entity)
     * @throws SAXException
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     ***********************************************************************/
    private SimpleBotanicalData inviaRicevi(
    		String pathServizio, String parametri, SimpleBotanicalData entity
    	) throws SAXException, IOException, InterruptedException, URISyntaxException {
    		return inviaRicevi(MetodoHttp.GET , pathServizio, null, parametri, false, null);
    }

    /************************************************************************
     * Gestisce la comunicazione con il server, la URL richiesta sarà
     * pathServizio[/idOggetto][?parametri][[&]simulation=true]
     * @param metodoHttp GET|POST...
     * @param pathServizio percorso del servizio
     * @param idOggetto da appendere dopo il path
     * @param parametri da appendere alla URL
     * @param simulazione true in caso di richiesta di simulazione
     * @param oggetto da inviare al server
     * @return l'oggetto ricevuto dal server
     * @throws SAXException
     * @throws IOException
     * FIXME: parametri dovrebbe essere una lista e l'encoding si dovrebbe fare qui
     * @throws InterruptedException
     * @throws URISyntaxException
     ***********************************************************************/
    private SimpleBotanicalData inviaRicevi(
            MetodoHttp metodoHttp, String pathServizio, String idOggetto, String parametri,
    		boolean simulazione, SimpleBotanicalData entity
    	) throws SAXException, IOException, InterruptedException, URISyntaxException {
            String id = "m" + dateFormat.format(new Date()) + "_" + Proprieta.recupera("connessione.nome");
            String url = Proprieta.recupera("connessione.URL")
                    + "/ws/" + pathServizio + (idOggetto != null ? "/" + idOggetto : "")
                    + (parametri != null ? "?" + parametri : "")
                    + (simulazione ? (parametri != null ? "&" : "") + "simulation=true" : "");
            String credentials = new String(Base64.getEncoder()
                    .encode((Proprieta.recupera("connessione.nome") + ":" + Proprieta.recupera("connessione.password"))
                            .getBytes()));
        long inizio;

        // imposto l'id dell'oggetto (utile per debug)
        if(entity!=null) {
            entity.setId(id);
        }

        Stato.debugLog.fine("URL richiesta:" + url);
        inizio = System.currentTimeMillis();

        Builder rb = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Accept", "application/xml")
                .header("Content-Type", "application/xml")
                .header("User-Agent", softwareName+" "+softwareVersion +" ["+
                        "jvm V "+System.getProperty("java.version")+" ("+System.getProperty("java.vendor")+")"+
                        " @"+System.getProperty("os.name")+" "+System.getProperty("os.version")+" ]")
                .header("Authorization", "Basic "+credentials);
        switch(metodoHttp) {
        case GET:
            rb = rb.GET();
            break;
        case POST:
            rb = rb.POST(BodyPublishers.ofString( entity.toXMLString(false) ));
            break;
        case PUT:
            rb = rb.PUT(BodyPublishers.ofString( entity.toXMLString(false) ));
            break;
        case DELETE:
            rb = rb.DELETE();
            break;
        }

        HttpRequest request = rb.build();
        HttpResponse<String> risposta = httpClient.send(request, BodyHandlers.ofString());

        if(Proprieta.isTrue("devel.sbdLogEnabled")){
            try {
                String pathTmp = getTempFile(id+".co.xml");
                Stato.debugLog.info("Registro nel file "+pathTmp);
                // XXX: AnalizzatoreDOM.analizzaSuFile(oggetto, Proprieta.recupera("pathSchema"),pathTmp);
                AnalizzatoreDOM.analizzaSuFile(entity, pathTmp);
            } catch (Exception e) {
            	Stato.debugLog.throwing(this.getClass().getName(), "inviaRicevi", e);
            }
        }

        // legge la risposta dallo stream di input
        Stato.debugLog.finer("Content-Type=" + risposta.headers().firstValue("Content-Type").get());
        Stato.debugLog.finest("Tempo di comunicazione:" + (System.currentTimeMillis() - inizio));

        // analizzo la risposta
        String corpo = risposta.body();
        InputSource is = new InputSource(new StringReader( corpo ));
        SHSimpleBotanicalData handler = new SHSimpleBotanicalData();
        parserSAX.parse(is, handler);
        if(Proprieta.isTrue("devel.sbdLogEnabled")){
            try {
                String pathTmp = getTempFile(id+".ci.xml");
                FileWriter fw = new FileWriter(pathTmp);
                fw.write(corpo);
                fw.close();
                // TODO: forse vecchio adesso par cancellare il contenutyo del file
                // AnalizzatoreDOM.analizzaSuFile(handler.getSimpleBotanicalData(), pathTmp);
            } catch (Exception e) {
            	Stato.debugLog.throwing(this.getClass().getName(), "inviaRicevi", e);
            }
        }

        return handler.getSimpleBotanicalData();
    }

	public static String getTempFile(String nome) {
		String v = Proprieta.recupera("devel.tmp");
		File f;
		String nomeDefinititvo;

		if (v.equals("DEFAULT")) {
			v = System.getProperty("java.io.tmpdir").endsWith(File.separator) ? System.getProperty("java.io.tmpdir")
					: System.getProperty("java.io.tmpdir") + File.separator;
		} else {
			if (!v.endsWith(File.separator)) {
				v += File.separator;
			}
		}
		f = new File(v);
		if (!f.exists()) {
			// torniamo al default
			v = System.getProperty("java.io.tmpdir").endsWith(File.separator) ? System.getProperty("java.io.tmpdir")
					: System.getProperty("java.io.tmpdir") + File.separator;
		}
		nomeDefinititvo = v + nome;
		return nomeDefinititvo;
    }

}
