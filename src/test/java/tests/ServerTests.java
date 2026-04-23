package tests;

import org.junit.jupiter.api.Test;
import robots.*;
import servers.*;
import games.PrisonersDilemmaGame;
import tournaments.RoundRobinTournament;
import tournaments.Tournament;

import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServerTests {

    @Test
    void testMainMethod() {
        ServerApplicationApp.main(new String[]{"--server.port=0"});
    }

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

        List<String> allTournaments = server.getAllTournaments();
        assertTrue(allTournaments.contains("Open"));
        assertTrue(allTournaments.contains("Open2"));
        assertTrue(allTournaments.contains("Closed"));

    }
    
    @Test
    void testGetTournaments() {
        TournamentServer service = mock(TournamentServer.class);
        when(service.getAllTournaments()).thenReturn(List.of("T1", "T2"));

        TournamentController controller = new TournamentController(service);

        List<String> result = controller.getTournaments();

        assertEquals(2, result.size());
        assertEquals("T1", result.get(0));
    }

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
    void testRegisterDirectCall() {
        TournamentServer service = mock(TournamentServer.class);
        when(service.register(any(), any(), any(), any(), any()))
                .thenReturn("Registered");

        TournamentController controller = new TournamentController(service);

        String result = controller.register(
                "Jeff", "T1", "remote", "127.0.0.1", "8080"
        );

        assertEquals("Registered", result);
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

     @Test
    void testRunTournament_runsWhenValid() {
        TournamentServer server = new TournamentServer();

        Tournament t = mock(Tournament.class);
        when(t.checkEnd()).thenReturn(false);

        server.addTournament("T1", t);

        server.runTournament("T1");

        verify(t, times(1)).run();
    }

    @Test
    void testGetPlayerCountMaxAndObserverMessages() {
        TournamentServer server = new TournamentServer();

        List<Robot> players = new ArrayList<>();
        DefectBot a = new DefectBot("Alice");
        DefectBot b = new DefectBot("Bob");
        players.add(a);
        players.add(b);

        Tournament t = new RoundRobinTournament("T1", players,
                new PrisonersDilemmaGame(1), 3);

        a.addHistory(new History("Alice", "Bob", "Cooperate", "Defect", new int[]{2,1}));
        b.addHistory(new History("Bob", "Alice", "Defect", "Cooperate", new int[]{1,2}));

        server.addTournament("T1", t);

        assertThat(server.getPlayerCount("T1")).isEqualTo(2);
        assertThat(server.getMaxPlayers("T1")).isEqualTo(3);

        List<String> messages = server.getObserverMessages("T1");
        assertThat(messages).isNotEmpty();
        assertThat(messages).anyMatch(s -> s.contains("Alice chose Cooperate") && s.contains("Bob chose Defect"));
        assertThat(messages).anyMatch(s -> s.contains("Alice=2") && s.contains("Bob=1"));

        assertThat(server.getPlayerCount("NoSuch")).isEqualTo(0);
        assertThat(server.getMaxPlayers("NoSuch")).isEqualTo(0);
        assertThat(server.getObserverMessages("NoSuch")).isEmpty();
    }

    @Test
    void testGetAvailableTournaments() {
        TournamentServer service = mock(TournamentServer.class);
        when(service.getAvailableTournaments()).thenReturn(List.of("Open1", "Open2"));

        TournamentController controller = new TournamentController(service);

        List<String> result = controller.getAvailableTournaments();

        assertEquals(2, result.size());
        assertTrue(result.contains("Open1"));
        assertTrue(result.contains("Open2"));
    }

    @Test
    void testInfo() {
        TournamentServer service = mock(TournamentServer.class);
        when(service.getPlayerCount("T1")).thenReturn(3);
        when(service.getMaxPlayers("T1")).thenReturn(5);

        TournamentController controller = new TournamentController(service);

        java.util.Map<String, Integer> result = controller.info("T1");

        assertEquals(3, result.get("current"));
        assertEquals(5, result.get("max"));
    }
}
