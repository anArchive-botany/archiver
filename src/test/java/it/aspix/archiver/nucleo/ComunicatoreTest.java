package it.aspix.archiver.nucleo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import it.aspix.sbd.obj.SpecieRef;
import it.aspix.sbd.obj.SpecieSpecification;

/************************************************************************************************
 * Nel server utilizzato deve esistere un utente (test,test) con accesso a VEGASPIX
 *   ma non deve esitere un utente (test,textxx)
 ***********************************************************************************************/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class ComunicatoreTest {

	Comunicatore com;

    @BeforeAll
    void setUp() {
    	Proprieta.caricaProprieta();
        // Impostiamo i valori statici prima di ogni test
        Proprieta.aggiorna("connessione.URL", "http://localhost:8080/anArchive");
        Proprieta.aggiorna("connessione.nome", "test");
        com = new Comunicatore("test","0.1");
    }

    /*
    @Test
    @Order(1)
    void testLoginErrato() throws URISyntaxException, IOException, InterruptedException {
    	Proprieta.aggiorna("connessione.password", "testxx");
        boolean r = com.login();
        assertEquals(r, false, "ha permesso l'accesso ad un utente non verificato");
    }

    */

    @Test
    @Order(2)
    void testLoginGiusto() throws URISyntaxException, IOException, InterruptedException {
        Proprieta.aggiorna("connessione.password", "test");
        boolean r = com.login();
        assertEquals(r, true, "ha rifiutato l'accesso ad un utente accreditato");
    }


    @Test
    void testInformazioniSpecie() throws Exception {
    	 SpecieSpecification[] ispecie;
    	 String nome = "Quercus acutissima Carruth.";
    	 ispecie = com.cerca(new SpecieRef(nome,null),null).getSpecieSpecification();
    	 assertEquals(ispecie.length,1, "arrivata troppa roba");
    }

}