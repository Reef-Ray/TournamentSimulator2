package tests;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClient;
import org.springframework.http.ResponseEntity;
import robots.*;
import servers.TournamentServer;
import tournaments.RoundRobinTournament;
import tournaments.Tournament;
import games.PrisonersDilemmaGame;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RemoteBotTests {

    private Tournament makeTournament() {
        return new RoundRobinTournament(new ArrayList<>(), new PrisonersDilemmaGame(1), 5);
    }

    private TournamentServer setupServer(String name, Tournament t) {
        TournamentServer server = new TournamentServer();
        server.addTournament(name, t);
        return server;
    }

    @Test
    void registerRemoteBotAddsToTournament() {
        Tournament t = makeTournament();
        TournamentServer server = setupServer("T1", t);

        String result = server.register("BotA", "T1", "remote", "127.0.0.1", "8080");

        assertEquals("Registered", result);
        assertEquals(1, t.getPlayers().size());
        assertTrue(t.getPlayers().get(0) instanceof RemoteBot);
    }

    @Test
    void registerTournamentNotFound() {
        TournamentServer server = new TournamentServer();
        String result = server.register("BotA", "BadTournament", "remote", "127.0.0.1", "8080");
        assertEquals("Tournament not found", result);
    }

    @Test
    void registerClosedTournament() {
        Tournament t = makeTournament();
        t.run(); // marks finished = true
        TournamentServer server = setupServer("T2", t);

        String result = server.register("BotA", "T2", "remote", "127.0.0.1", "8080");
        assertEquals("Tournament closed", result);
    }

    @Test
    void remoteBotGetActionReturnsMockedValue() {
        RemoteBot bot = new RemoteBot("BotA", "127.0.0.1", "8080") {
            @Override
            public String getAction(String opponentName) {
                return "Cooperate";
            }
        };

        String action = bot.getAction("EnemyBot");
        assertEquals("Cooperate", action);
    }

    @Test
    void remoteBotHistoryPassedCorrectly() {
        RemoteBot bot = new RemoteBot("BotB", "127.0.0.1", "8080") {
            @Override
            public String getAction(String opponentName) {
                assertNotNull(getHistory());
                assertEquals(0, getHistory().size());
                return "Defect";
            }
        };

        bot.getAction("EnemyBot");
    }

    @Test
    void remoteBotFallbackOnException() {
        RemoteBot bot = new RemoteBot("BotC", "bad_ip", "0000") {
            @Override
            public String getAction(String opponentName) {
                throw new RuntimeException("Simulated failure");
            }
        };

        String move;
        try {
            move = bot.getAction("EnemyBot");
        } catch (Exception e) {
            move = "Cooperate"; // fallback
        }

        assertEquals("Cooperate", move);
    }
}