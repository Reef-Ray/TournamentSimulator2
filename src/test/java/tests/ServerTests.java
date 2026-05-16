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
import static org.mockito.Mockito.doThrow;
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
    void tournamentServerListsAvailableTournaments() {
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
    void tournamentServerRegistersRemoteBotSuccessfully() {
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
    void tournamentServerRegistersHumanBotSuccessfully() {
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
    void tournamentServerRegistersDefaultBotWhenTypeUnknown() {
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
    void tournamentServerRejectsRegistrationWhenTournamentClosed() {
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
    void tournamentServerRejectsRegistrationForNonExistentTournament() {
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
    void remoteBotHistoryIsAccessible() {
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
    void testRunTournamentrunsWhenValid() throws InterruptedException {
        TournamentServer server = new TournamentServer();

        Tournament t = mock(Tournament.class);
        when(t.checkEnd()).thenReturn(false);

        server.addTournament("T1", t);

        server.runTournament("T1");

        Thread.sleep(300);
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

    @Test
    void testLogControllerReturnsEmptyWhenTournamentNotFound() {
        TournamentServer server = new TournamentServer();
        LogController controller = new LogController(server);
        
        List<String> result = controller.getLog("NonExistent");
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testLogControllerReturnsLogsFromLoggingBots() {
        TournamentServer server = new TournamentServer();
        
        Robot original = new DefectBot("LoggedBot");
        LoggingBot logged = new LoggingBot(original);
        logged.getAction("Opponent1");
        logged.getAction("Opponent2");
        
        List<Robot> players = new ArrayList<>();
        players.add(logged);
        Tournament t = new RoundRobinTournament("TestTournament", players, 
                new PrisonersDilemmaGame(1), 1);
        server.addTournament("TestTournament", t);
        
        LogController controller = new LogController(server);
        List<String> result = controller.getLog("TestTournament");
        
        assertEquals(2, result.size());
        assertTrue(result.get(0).contains("LoggedBot"));
        assertTrue(result.get(0).contains("Opponent1"));
    }

    @Test
    void testLogControllerHandlesNoLoggingBots() {
        TournamentServer server = new TournamentServer();
        
        List<Robot> players = new ArrayList<>();
        players.add(new DefectBot("RegularBot"));
        Tournament t = new RoundRobinTournament("TestTournament", players, 
                new PrisonersDilemmaGame(1), 1);
        server.addTournament("TestTournament", t);
        
        LogController controller = new LogController(server);
        List<String> result = controller.getLog("TestTournament");
        
        assertTrue(result.contains("No log entries yet."));
    }

    @Test
    void testTournamentControllerObserve() {
        TournamentServer service = mock(TournamentServer.class);
        when(service.getObserverMessages("T1")).thenReturn(List.of("move line", "score line"));
        TournamentController controller = new TournamentController(service);

        List<String> result = controller.observe("T1");

        assertEquals(2, result.size());
        assertTrue(result.contains("move line"));
    }

    @Test
    void testTournamentControllerRunTournament() {
        TournamentServer service = mock(TournamentServer.class);
        TournamentController controller = new TournamentController(service);

        String result = controller.runTournament("T1");

        assertEquals("Tournament started", result);
        verify(service, times(1)).runTournament("T1");
    }

    @Test
    void testRemoteBotGetRemoteLogsInitiallyEmpty() {
        RemoteBot bot = new RemoteBot("Bot", "127.0.0.1", "8080");
        assertNotNull(bot.getRemoteLogs());
        assertTrue(bot.getRemoteLogs().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testLogControllerRemoteBotWithLogs() throws Exception {
        TournamentServer server = new TournamentServer();
        RemoteBot remoteBot = new RemoteBot("RemotePlayer", "127.0.0.1", "8080");

        java.lang.reflect.Field logsField = RemoteBot.class.getDeclaredField("remoteLogs");
        logsField.setAccessible(true);
        List<LogEntry> logs = (List<LogEntry>) logsField.get(remoteBot);
        logs.add(new LogEntry("RemotePlayer", "Opponent", "Cooperate"));

        List<Robot> players = new ArrayList<>();
        players.add(remoteBot);
        Tournament t = new RoundRobinTournament("T1", players, new PrisonersDilemmaGame(1), 1);
        server.addTournament("T1", t);

        LogController controller = new LogController(server);
        List<String> result = controller.getLog("T1");

        assertEquals(1, result.size());
        assertTrue(result.get(0).contains("RemotePlayer"));
    }

    @Test
    void testRunTournamentHandlesExceptionGracefully() throws InterruptedException {
        TournamentServer server = new TournamentServer();
        Tournament t = mock(Tournament.class);
        when(t.checkEnd()).thenReturn(false);
        doThrow(new RuntimeException("simulated failure")).when(t).run();

        server.addTournament("T1", t);
        server.runTournament("T1");

        Thread.sleep(300);
        verify(t, times(1)).run();
    }

    @Test
    void testLogControllerPreservesLogOrder() {
        TournamentServer server = new TournamentServer();
        
        Robot original = new CooperateBot("Bot");
        LoggingBot logged = new LoggingBot(original);
        
        logged.getAction("First");
        logged.getAction("Second");
        logged.getAction("Third");
        
        List<Robot> players = new ArrayList<>();
        players.add(logged);
        Tournament t = new RoundRobinTournament("TestTournament", players, 
                new PrisonersDilemmaGame(1), 1);
        server.addTournament("TestTournament", t);
        
        LogController controller = new LogController(server);
        List<String> result = controller.getLog("TestTournament");
        
        assertEquals(3, result.size());
        assertTrue(result.get(0).contains("First"));
        assertTrue(result.get(1).contains("Second"));
        assertTrue(result.get(2).contains("Third"));
    }
}
