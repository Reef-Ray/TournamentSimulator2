package tests;

import clients.TournamentClient;
import clients.ClientApp;
import clients.ClientChanges;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import servers.TournamentServer;
import servers.TournamentController;
import tournaments.RoundRobinTournament;
import tournaments.Tournament;
import games.PrisonersDilemmaGame;
import robots.RemoteBot;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = ClientTests.TestApp.class
)
public class ClientTests {

    @LocalServerPort
    private int port;

    @Autowired
    private Environment env;

    @Autowired
    private TournamentServer server;

    @Test
    void registerRemoteBotIntegrationTest() {
        TournamentClient client = new TournamentClient(env);

        String baseUrl = "http://localhost:" + port;

        assertDoesNotThrow(() ->
            client.register(baseUrl, "BotA", "T1", "remote")
        );

        Tournament t = server.getTournament("T1");

        assertEquals(1, t.getPlayers().size());
        assertTrue(t.getPlayers().get(0) instanceof RemoteBot);
    }

    @Test
    void mainRunsWithoutCrash() {
        assertDoesNotThrow(() -> 
            ClientApp.main(new String[]{})
        );
    }

    // 🔥 This replaces a separate TestApplication file
    @SpringBootApplication(scanBasePackages = {
            "servers", "tournaments", "games", "robots"
    })
    static class TestApp {

        @Bean
        public TournamentServer tournamentServer() {
            Tournament t = new RoundRobinTournament(
                new java.util.ArrayList<>(),
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

        // Only include this if needed
        @Bean
        public ClientChanges clientChanges() {
            return new ClientChanges();
        }
    }
}