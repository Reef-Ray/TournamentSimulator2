package tests;

import org.junit.jupiter.api.Test;
import robots.*;
import servers.TournamentServer;
import games.PrisonersDilemmaGame;
import tournaments.RoundRobinTournament;
import tournaments.Tournament;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTests {

    @Test
    void tournamentServer_ListsAvailableTournaments() {
        TournamentServer server = new TournamentServer();
        List<Robot> players = new ArrayList<>();
        Tournament t = new RoundRobinTournament("Open", players, 
                new PrisonersDilemmaGame(1), 2);
        Tournament t2 = new RoundRobinTournament("Open2", players, 
                new PrisonersDilemmaGame(1), 2);

        server.addTournament("Open", t);
        server.addTournament("Open2", t2);
        
        List<Robot> closedPlayers = new ArrayList<>();
        closedPlayers.add(new DefectBot("ClosedPlayer"));
        Tournament closed = new RoundRobinTournament("Closed", closedPlayers, 
                new PrisonersDilemmaGame(1), 1); // At capacity
        server.addTournament("Closed", closed);
        
        List<String> available = server.getAvailableTournaments();

        assertTrue(available.contains("Open"));
        assertTrue(available.contains("Open2"));
        assertFalse(available.contains("Closed"));
    }

    @Test
    void tournamentServer_HidesFullTournamentsFromAvailable() {
        TournamentServer server = new TournamentServer();
        List<Robot> players = new ArrayList<>();
        players.add(new DefectBot("A"));
        Tournament t = new RoundRobinTournament("Full", players, 
                new PrisonersDilemmaGame(1), 1);

        server.addTournament("Full", t);
        List<String> available = server.getAvailableTournaments();
        
        assertFalse(available.contains("Full"));
    }

    // ========== REGISTRATION TESTS ==========

    @Test
    void tournamentServer_RegistersRemoteBotSuccessfully() {
        TournamentServer server = new TournamentServer();
        List<Robot> players = new ArrayList<>();
        Tournament t = new RoundRobinTournament("Remote", players, 
                new PrisonersDilemmaGame(1), 2);
        server.addTournament("Remote", t);

        String result = server.register("RemotePlayer", "Remote", "remote", "127.0.0.1", "8081");
        
        assertEquals("Registered", result);
        assertTrue(t.getPlayers().get(0) instanceof RemoteBot);
    }

    @Test
    void tournamentServer_RegistersHumanBotSuccessfully() {
        TournamentServer server = new TournamentServer();
        List<Robot> players = new ArrayList<>();
        Tournament t = new RoundRobinTournament("Human", players, 
                new PrisonersDilemmaGame(1), 2);
        server.addTournament("Human", t);

        String result = server.register("HumanPlayer", "Human", "human", null, null);
        
        assertEquals("Registered", result);
        assertTrue(t.getPlayers().get(0) instanceof HumanBot);
    }

    @Test
    void tournamentServer_RegistersDefaultBotWhenTypeUnknown() {
        TournamentServer server = new TournamentServer();
        List<Robot> players = new ArrayList<>();
        Tournament t = new RoundRobinTournament("Default", players, 
                new PrisonersDilemmaGame(1), 2);
        server.addTournament("Default", t);

        String result = server.register("DefaultBot", "Default", "unknown", null, null);
        
        assertEquals("Registered", result);
        assertTrue(t.getPlayers().get(0) instanceof DefectBot);
    }

    @Test
    void tournamentServer_RejectsRegistrationWhenTournamentClosed() {
        TournamentServer server = new TournamentServer();
        List<Robot> players = new ArrayList<>();
        players.add(new DefectBot("A"));
        Tournament t = new RoundRobinTournament("Closed", players, 
                new PrisonersDilemmaGame(1), 1);
        server.addTournament("Closed", t);
        t.run();  // Close the tournament

        String result = server.register("LatePlayer", "Closed", "remote", "127.0.0.1", "8082");
        
        assertEquals("Tournament closed", result);
    }

    @Test
    void tournamentServer_RejectsRegistrationForNonExistentTournament() {
        TournamentServer server = new TournamentServer();
        
        String result = server.register("LostPlayer", "NonExistent", "remote", "127.0.0.1", "8082");
        
        assertEquals("Tournament not found", result);
    }

    @Test
    void remoteBot_HistoryIsAccessible() {
        RemoteBot bot = new RemoteBot("TestBot", "127.0.0.1", "8080");
        
        assertNotNull(bot.getHistory());
        assertTrue(bot.getHistory().isEmpty());
        
        bot.addHistory(new History("TestBot", "Opponent", "Cooperate", "Defect", new int[]{0, 5}));
        assertEquals(1, bot.getHistory().size());
    }

    @Test
    void remoteBot_ScoringWorksCorrectly() {
        RemoteBot bot = new RemoteBot("Bot", "127.0.0.1", "8080");
        
        bot.addHistory(new History("Bot", "Enemy1", "Cooperate", "Cooperate", new int[]{3, 3}));
        assertEquals(3, bot.getScore());
        
        bot.addHistory(new History("Bot", "Enemy2", "Defect", "Cooperate", new int[]{5, 0}));
        assertEquals(8, bot.getScore());
    }
}
