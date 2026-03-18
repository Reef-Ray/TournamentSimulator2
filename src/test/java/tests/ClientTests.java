package tests;

import clients.TournamentClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import servers.TournamentServer;
import servers.TournamentController;
import tournaments.RoundRobinTournament;
import tournaments.Tournament;
import games.PrisonersDilemmaGame;
import robots.RemoteBot;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
		webEnvironment = WebEnvironment.RANDOM_PORT,
		classes = TournamentClient.class
)
@AutoConfigureRestTestClient
public class ClientTests {

    @Autowired
    private int port;

    @Autowired
    private Environment env;

    @Test
    void registerRemoteBotIntegrationTest() {
        // Set up server and tournament
        Tournament t = new RoundRobinTournament(new java.util.ArrayList<>(), new PrisonersDilemmaGame(1), 5);
        TournamentServer server = new TournamentServer();
        server.addTournament("T1", t);
        TournamentController controller = new TournamentController(server);

        // Client uses Spring Environment to get port
        TournamentClient client = new TournamentClient(env);

        // Call register
        assertDoesNotThrow(() -> client.register("http://localhost:" + port, "BotA", "T1", "remote"));

        // Verify the bot was added
        assertEquals(1, t.getPlayers().size());
        assertTrue(t.getPlayers().get(0) instanceof RemoteBot);
    }
}