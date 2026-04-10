package tests;

import clients.ClientApp;
import clients.TournamentClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import servers.TournamentServer;
import servers.TournamentController;
import tournaments.RoundRobinTournament;
import tournaments.Tournament;
import games.PrisonersDilemmaGame;
import templates.RemoteInfo;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = ClientTests.TestApp.class
)
public class ClientTests {

    @LocalServerPort
    private int port;

    @org.springframework.beans.factory.annotation.Autowired
    private Environment env;

    @org.springframework.beans.factory.annotation.Autowired
    private TournamentServer server;

    @Test
    void clientApp_StartsWithoutError() {
        assertDoesNotThrow(() -> ClientApp.main(new String[]{}));
    }

    @Test
    void tournamentClient_DiscoveryReturnsValidPort() {
        TournamentClient client = new TournamentClient(env);
        
        assertNotEquals(0, client.getPort());
        assertTrue(client.getPort() > 0);
    }

    @Test
    void tournamentClient_DiscoveryReturnsValidIP() {
        TournamentClient client = new TournamentClient(env);
        
        assertNotNull(client.getIP());
        assertFalse(client.getIP().isEmpty());
    }

    @Test
    void tournamentServer_ListsAvailableTournamentsViaEndpoint() {
        List<String> available = server.getAvailableTournaments();
        
        assertNotNull(available);
        assertTrue(available.contains("T1"));
    }

    @Test
    void tournamentServer_AcceptsRegistrationViaEndpoint() {
        String result = server.register("ClientBot", "T1", "remote", "localhost", "8080");
        
        assertEquals("Registered", result);
        assertTrue(server.getTournament("T1").getPlayers().stream()
            .anyMatch(r -> r.getName().equals("ClientBot")));
    }

    @Test
    void clientApp_ReceivesRemoteInfoAndReturnsAction() {
        ClientApp app = new ClientApp();
        RemoteInfo info = new RemoteInfo("Opponent", new ArrayList<>());
        
        String action = app.getAction(info);
        
        assertNotNull(action);
        assertTrue(action.equals("Cooperate") || action.equals("Defect"));
    }

    @SpringBootApplication(scanBasePackages = {
            "servers", "tournaments", "games", "robots", "clients"
    })
    static class TestApp {

        @Bean
        public TournamentServer tournamentServer() {
            Tournament t = new RoundRobinTournament(
                "T1",
                new ArrayList<>(),
                new PrisonersDilemmaGame(1),
                5
            );

            TournamentServer server = new TournamentServer();
            server.addTournament("T1", t);
            return server;
        }

        @Bean
        public TournamentController tournamentController(TournamentServer server) {
            return new TournamentController(server);
        }
    }
}
