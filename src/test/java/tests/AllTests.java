package tests;

import games.PrisonersDilemmaGame;
import robots.*;
import tournaments.RoundRobinTournament;
import listeners.MoveListener;
import listeners.ScoreListener;
import tournaments.Tournament;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

public class AllTests {

    static class StaticRobot extends Robot {
        private final String move;

        public StaticRobot(String name, String move) {
            super(name);
            this.move = move;
        }

        @Override
        public String getAction(String enemyName) {
            return move;
        }
    }

    @Test
    void testCooperateVsCooperate() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        StaticRobot r1 = new StaticRobot("A", "Cooperate");
        StaticRobot r2 = new StaticRobot("B", "Cooperate");

        game.run(r1, r2);

        assertEquals(1, r1.getHistory().size());
        assertEquals(1, r2.getHistory().size());
        assertEquals(3, r1.getScore());
        assertEquals(3, r2.getScore());
    }

    @Test
    void testDefectVsDefect() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        StaticRobot r1 = new StaticRobot("A", "Defect");
        StaticRobot r2 = new StaticRobot("B", "Defect");

        game.run(r1, r2);

        assertEquals(1, r1.getScore());
        assertEquals(1, r2.getScore());
    }

        @Test
    void testDefectVsCooperate() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        StaticRobot r1 = new StaticRobot("A", "Defect");
        StaticRobot r2 = new StaticRobot("B", "Cooperate");

        game.run(r1, r2);

        assertEquals(5, r1.getScore());
        assertEquals(0, r2.getScore());
    }

    @Test
    void testInvalidMoves() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        StaticRobot r1 = new StaticRobot("A", "Invalid");
        StaticRobot r2 = new StaticRobot("B", "Invalid");

        game.run(r1, r2);

        assertEquals(0, r1.getScore());
        assertEquals(0, r2.getScore());
    }

    @Test
    void testCopyBot_DefaultsToCooperateWhenNoHistory() {
        CopyBot c = new CopyBot("CopyBot");
        assertEquals("Cooperate", c.getAction("NewOpponent"));
    }

    @Test
    void testCopyBotCopiesMostRecentEnemyMove() {
        CopyBot c = new CopyBot("CopyBot");
        c.addHistory(new History("Enemy", "CopyBot", "Defect", "Cooperate", new int[]{5, 0}));
        assertEquals("Defect", c.getAction("Enemy"));
    }

    @Test
    void testRobotScoring_AccumulatesCorrectly() {
        Robot r = new CopyBot("TestBot");
        assertEquals(0, r.getScore());
        r.addHistory(new History("TestBot", "A", "Cooperate", "Cooperate", new int[]{3, 3}));
        assertEquals(3, r.getScore());
        r.addHistory(new History("TestBot", "B", "Defect", "Cooperate", new int[]{5, 0}));
        assertEquals(8, r.getScore());
    }

    @Test
    void testRobotBasicProperties() {
        Robot r = new DefectBot("MyBot");
        assertEquals("MyBot", r.getName());
        assertNotNull(r.getHistory());
        assertTrue(r.getHistory().isEmpty());
    }

    @Test
    void tournamentBracketGenerationMatchesCombinatorics() {
        List<Robot> players = Arrays.asList(
                new DefectBot("A"),
                new CopyBot("B"),
                new RandomBot("C"),
                new DefectBot("D")
        );

        RoundRobinTournament t = new RoundRobinTournament("Test", players, new PrisonersDilemmaGame(1), 0);
        assertEquals(6, t.getBracket().size());
    }

    private PrintStream standardOut;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    void setUpStreamCapture() {
        standardOut = System.out;
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(standardOut);
    }

    @Test
    void testMoveListenerPrintsCorrectly() {
        MoveListener listener = new MoveListener();
        listener.updateMove("Alice", "Cooperate", "Bob", "Defect");
        String output = capturedOutput.toString();
        assertTrue(output.contains("Alice") && output.contains("Cooperate") && output.contains("Bob"));
    }

    @Test
    void testScoreListenerPrintsCorrectly() {
        ScoreListener listener = new ScoreListener();
        listener.updateScore("Alice", 5, "Bob", 3);
        String output = capturedOutput.toString();
        assertTrue(output.contains("Final Score") && output.contains("Alice = 5"));
    }

    @Test
    void testLoggingBotLogsActions() {
        Robot original = new CooperateBot("TestBot");
        LoggingBot logged = new LoggingBot(original);
        
        logged.getAction("Opponent1");
        logged.getAction("Opponent2");
        
        assertEquals(2, logged.getLog().size());
        assertEquals("TestBot", logged.getLog().get(0).botName);
        assertEquals("Cooperate", logged.getLog().get(0).action);
    }

    @Test
    void testLoggingBotPreservesScore() {
        Robot original = new DefectBot("DefectBot");
        original.addHistory(new History("DefectBot", "Enemy", "Defect", "Cooperate", new int[]{5, 0}));
        LoggingBot logged = new LoggingBot(original);
        
        assertEquals(original.getScore(), logged.getScore());
        assertEquals(5, logged.getScore());
    }

    @Test
    void testLoggingBotCanAddHistory() {
        Robot original = new RandomBot("RandomBot");
        LoggingBot logged = new LoggingBot(original);
        
        logged.addHistory(new History("RandomBot", "X", "Cooperate", "Defect", new int[]{0, 5}));
        assertEquals(1, logged.getHistory().size());
    }

    @Test
    void testGameAddRemoveAndNotifyMoveListeners() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        MoveListener listener1 = new MoveListener();
        MoveListener listener2 = new MoveListener();
        
        game.addMoveListener(listener1);
        game.addMoveListener(listener2);
        
        StaticRobot r1 = new StaticRobot("Alice", "Cooperate");
        StaticRobot r2 = new StaticRobot("Bob", "Defect");
        
        game.run(r1, r2);
        
        String output = capturedOutput.toString();
        assertTrue(output.contains("Alice") && output.contains("Bob"));
        
        capturedOutput.reset();
        game.removeMoveListener(listener1);
        PrisonersDilemmaGame game2 = new PrisonersDilemmaGame(1);
        game2.addMoveListener(listener2);
        game2.run(new StaticRobot("Charlie", "Cooperate"), new StaticRobot("David", "Cooperate"));
        
        output = capturedOutput.toString();
        assertTrue(output.contains("Charlie") && output.contains("David"));
    }

    @Test
    void testGameAddRemoveAndNotifyScoreListeners() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        ScoreListener listener1 = new ScoreListener();
        ScoreListener listener2 = new ScoreListener();
        
        game.addScoreListener(listener1);
        game.addScoreListener(listener2);
        
        StaticRobot r1 = new StaticRobot("Alice", "Cooperate");
        StaticRobot r2 = new StaticRobot("Bob", "Cooperate");
        
        game.run(r1, r2);
        
        String output = capturedOutput.toString();
        assertTrue(output.contains("Final Score") && output.contains("Alice") && output.contains("Bob"));
        
        capturedOutput.reset();
        game.removeScoreListener(listener1);
        PrisonersDilemmaGame game2 = new PrisonersDilemmaGame(1);
        game2.addScoreListener(listener2);
        game2.run(new StaticRobot("Charlie", "Defect"), new StaticRobot("David", "Defect"));
        
        output = capturedOutput.toString();
        assertTrue(output.contains("Final Score"));
    }

    @Test
    void testCopyBotCopiesEnemyMoveWhenEnemyIsPlayer2() {
        CopyBot c = new CopyBot("CopyBot");
        c.addHistory(new History("CopyBot", "Enemy", "Cooperate", "Defect", new int[]{0, 5}));
        assertEquals("Defect", c.getAction("Enemy"));
    }

    @Test
    void testRobotAddToHistory() {
        Robot r = new CooperateBot("Bot");
        r.addToHistory("Cooperate");
        assertEquals(1, r.getHistory().size());
        assertEquals("Bot", r.getHistory().get(0).player1);
    }

    @Test
    void testRobotScoringAsPlayer2() {
        Robot r = new CooperateBot("Bot");
        r.addHistory(new History("Opponent", "Bot", "Defect", "Cooperate", new int[]{5, 0}));
        assertEquals(0, r.getScore());
        r.addHistory(new History("Other", "Bot", "Cooperate", "Cooperate", new int[]{3, 3}));
        assertEquals(3, r.getScore());
    }

    @Test
    void testTournamentMainMethodSortsPlayers() {
        List<Robot> players = new ArrayList<>();
        players.add(new CooperateBot("Cooperator"));
        players.add(new DefectBot("Defector"));
        RoundRobinTournament t = new RoundRobinTournament("Test", players, new PrisonersDilemmaGame(1), 0);
        Robot[] ranked = t.main();
        assertEquals(2, ranked.length);
        assertEquals("Defector", ranked[0].getName());
        assertEquals("Cooperator", ranked[1].getName());
    }

    @Test
    void testRoundRobinTournamentIsOpenWhenEndConditionZero() {
        List<Robot> players = new ArrayList<>();
        players.add(new DefectBot("A"));
        players.add(new DefectBot("B"));
        RoundRobinTournament t = new RoundRobinTournament("Local", players, new PrisonersDilemmaGame(1), 0);
        assertTrue(t.isOpen());
    }

    @Test
    void testPrisonersDilemmaValidMove1InvalidMove2() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        StaticRobot r1 = new StaticRobot("A", "Cooperate");
        StaticRobot r2 = new StaticRobot("B", "Invalid");
        game.run(r1, r2);
        assertEquals(5, r1.getScore());
        assertEquals(0, r2.getScore());
    }

    @Test
    void testPrisonersDilemmaInvalidMove1ValidMove2() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        StaticRobot r1 = new StaticRobot("A", "Invalid");
        StaticRobot r2 = new StaticRobot("B", "Cooperate");
        game.run(r1, r2);
        assertEquals(0, r1.getScore());
        assertEquals(5, r2.getScore());
    }

    @Test
    void testTournamentAddPlayerWhenOpenAndClosed() {
        List<Robot> players = new ArrayList<>();
        players.add(new DefectBot("InitialPlayer"));
        RoundRobinTournament tournament = new RoundRobinTournament("Test", players, new PrisonersDilemmaGame(1), 2);
        
        Robot newPlayer = new CooperateBot("NewPlayer");
        tournament.addPlayer(newPlayer);
        assertEquals(2, tournament.getPlayers().size());
        assertTrue(tournament.getPlayers().contains(newPlayer));
        
        tournament.run();
        
        capturedOutput.reset();
        Robot latePlayer = new RandomBot("LatePlayer");
        tournament.addPlayer(latePlayer);
        String output = capturedOutput.toString();
        assertTrue(output.contains("Cannot add player") && output.contains("Registration closed"));
        assertEquals(2, tournament.getPlayers().size());
    }
}
