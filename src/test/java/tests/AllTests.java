package tests;

import games.PrisonersDilemmaGame;
import robots.*;
import tournaments.RoundRobinTournament;
import tournaments.Tournament;
import listeners.MoveListener;
import listeners.ScoreListener;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
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

    static class MockMoveListener extends MoveListener {
        public int callCount = 0;
        public String lastR1;
        public String lastM1;
        public String lastR2;
        public String lastM2;

        @Override
        public void updateMove(String r1, String m1, String r2, String m2) {
            callCount++;
            lastR1 = r1;
            lastM1 = m1;
            lastR2 = r2;
            lastM2 = m2;
        }
    }

    static class MockScoreListener extends ScoreListener {
        public int callCount = 0;
        public String lastR1;
        public int lastS1;
        public String lastR2;
        public int lastS2;

        @Override
        public void updateScore(String r1, int s1, String r2, int s2) {
            callCount++;
            lastR1 = r1;
            lastS1 = s1;
            lastR2 = r2;
            lastS2 = s2;
        }
    }

    @Test
    void pdg_cooperateCooperate_yields3and3() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        StaticRobot r1 = new StaticRobot("A","Cooperate");
        StaticRobot r2 = new StaticRobot("B","Cooperate");

        game.run(r1, r2);

        assertEquals(1, r1.getHistory().size());
        assertEquals(1, r2.getHistory().size());
        assertEquals(3, r1.getScore());
        assertEquals(3, r2.getScore());
    }

    @Test
    void pdg_defectCooperate_yields5and0() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        StaticRobot r1 = new StaticRobot("A","Defect");
        StaticRobot r2 = new StaticRobot("B","Cooperate");

        game.run(r1, r2);

        assertEquals(5, r1.getScore());
        assertEquals(0, r2.getScore());
    }

    @Test
    void pdg_invalidMoves_handledCorrectly() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        StaticRobot r1 = new StaticRobot("A","X");
        StaticRobot r2 = new StaticRobot("B","X");

        game.run(r1, r2);

        assertEquals(0, r1.getScore());
        assertEquals(0, r2.getScore());
    }

    @Test
    void copyBot_defaultsToCooperate_whenNoHistory() {
        CopyBot c = new CopyBot("C");
        String action = c.getAction("X");
        assertEquals("Cooperate", action);
    }

    @Test
    void copyBot_copiesMostRecentEnemyMove() {
        CopyBot c = new CopyBot("C");
        c.addHistory(new History("X","C","Defect","Cooperate", new int[]{5,0}));
        assertEquals("Defect", c.getAction("X"));
    }

    @Test
    void defectBot_alwaysDefect() {
        DefectBot d = new DefectBot("D");
        assertEquals("Defect", d.getAction("any"));
    }

    @Test
    void randomBot_returnsAllowedMove() {
        RandomBot r = new RandomBot("R");
        String a = r.getAction("x");
        assertTrue(a.equals("Cooperate") || a.equals("Defect"));
    }

    @Test
    void getScore_sumsHistoryProperly() {
        CopyBot c = new CopyBot("C");
        c.addHistory(new History("C","A","Cooperate","Cooperate", new int[]{3,3}));
        c.addHistory(new History("C","B","Defect","Cooperate", new int[]{5,0}));
        assertEquals(8, c.getScore());
    }

    @Test
    void roundRobin_bracketSizeMatchesCombinationCount() {
        List<Robot> players = Arrays.asList(
                new DefectBot("A"),
                new CopyBot("B"),
                new RandomBot("C"),
                new DefectBot("D")
        );

        RoundRobinTournament t = new RoundRobinTournament(players, new PrisonersDilemmaGame(1), 0);
        List<Robot[]> bracket = t.getBracket();

        assertEquals(6, bracket.size());
    }

    @Test
    void tournament_run_marksFinished_and_returnsSortedResults() {
        List<Robot> players = Arrays.asList(
                new DefectBot("A"),
                new DefectBot("B")
        );

        RoundRobinTournament t = new RoundRobinTournament(players, new PrisonersDilemmaGame(1), 0);
        assertFalse(t.checkEnd());
        t.run();
        assertTrue(t.checkEnd());

        Robot[] results = t.main();
        assertEquals(2, results.length);
    }

    @Test
    void multipleHistoryEntries_accumulateScoreCorrectly_mixedPositions() {
        Robot r = new CopyBot("C");

        r.addHistory(new History("C","A","Cooperate","Cooperate", new int[]{3,3}));
        r.addHistory(new History("B","C","Defect","Cooperate", new int[]{5,0}));

        assertEquals(3, r.getScore()); // 3 + 0
    }

    @Test
    void addToHistory_multipleEntries() {
        Robot r = new CopyBot("C");

        r.addToHistory("Cooperate");
        r.addToHistory("Defect");

        assertEquals(2, r.getHistory().size());  
    }

    @Test
    void robot_getName_returnsCorrectName() {
        Robot r = new DefectBot("TestName");
        assertEquals("TestName", r.getName());
}

    @Test
    void addHistory_increasesHistorySize() {
        Robot r = new CopyBot("C");
        assertEquals(0, r.getHistory().size());

        r.addHistory(new History("C","X","Cooperate","Cooperate", new int[]{3,3}));

        assertEquals(1, r.getHistory().size());
    }

    @Test
    void addToHistory_createsHistoryEntry() {
        Robot r = new CopyBot("C");

        r.addToHistory("Cooperate");

        assertEquals(1, r.getHistory().size());
        History h = r.getHistory().get(0);

        assertEquals("C", h.player1);
        assertEquals("Cooperate", h.player1Move);
    }

    @Test
    void getHistory_returnsSameList_referenceBehavior() {
        Robot r = new CopyBot("C");

        List<History> h1 = r.getHistory();
        List<History> h2 = r.getHistory();

        assertSame(h1, h2);
    }

    @Test
    void getScore_countsWhenRobotIsPlayer2() {
        Robot r = new CopyBot("C");

        r.addHistory(new History("A","C","Cooperate","Defect", new int[]{0,5}));

        assertEquals(5, r.getScore());
    }

    @Test
    void getScore_emptyHistory_isZero() {
        Robot r = new CopyBot("C");
        assertEquals(0, r.getScore());
    }

    @Test
    void scoreListener_runsWithoutCrash() {
        listeners.ScoreListener s = new listeners.ScoreListener();

        assertDoesNotThrow(() -> {
            s.updateScore("A", 5, "B", 3);
        });
    }

    @Test
    void moveListener_runsWithoutCrash() {
        listeners.MoveListener m = new listeners.MoveListener();

        assertDoesNotThrow(() -> {
            m.updateMove("A", "Cooperate", "B", "Defect");
        });
    }

    @Test
    void tournament_isOpen_behavesCorrectly() {
        Tournament t = new RoundRobinTournament(new java.util.ArrayList<>(), new PrisonersDilemmaGame(1), 2);
        assertTrue(t.isOpen());

        t.addPlayer(new DefectBot("A"));
        assertTrue(t.isOpen());

        // Add another player (tournament now full)
        t.addPlayer(new CopyBot("B"));
        assertFalse(t.isOpen()); // Tournament full
    }

    @Test
    void remoteInfo_storesOpponentAndHistoryCorrectly() {
        java.util.List<robots.History> historyList = new java.util.ArrayList<>();
        historyList.add(new robots.History("A", "B", "Cooperate", "Defect", new int[]{3,0}));
        templates.RemoteInfo info = new templates.RemoteInfo("B", historyList);

        assertEquals("B", info.opponentName());

        assertEquals(1, info.history().size());
        assertEquals("A", info.history().get(0).player1);
        assertEquals("B", info.history().get(0).player2);
        assertEquals("Cooperate", info.history().get(0).player1Move);
        assertEquals("Defect", info.history().get(0).player2Move);
        assertArrayEquals(new int[]{3,0}, info.history().get(0).outcome);
    }

    // ===== COMPREHENSIVE PRISONER'S DILEMMA GAME TESTS =====
    
    @Test
    void pdg_defectDefect_yields1and1() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        StaticRobot r1 = new StaticRobot("A","Defect");
        StaticRobot r2 = new StaticRobot("B","Defect");

        game.run(r1, r2);

        assertEquals(1, r1.getScore());
        assertEquals(1, r2.getScore());
    }

    @Test
    void pdg_cooperateDefect_yields0and5() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        StaticRobot r1 = new StaticRobot("A","Cooperate");
        StaticRobot r2 = new StaticRobot("B","Defect");

        game.run(r1, r2);

        assertEquals(0, r1.getScore());
        assertEquals(5, r2.getScore());
    }

    @Test
    void pdg_multipleRounds_scoresAccumulate() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(3);
        StaticRobot r1 = new StaticRobot("A","Defect");
        StaticRobot r2 = new StaticRobot("B","Cooperate");

        game.run(r1, r2);

        assertEquals(15, r1.getScore());  // 5 + 5 + 5
        assertEquals(0, r2.getScore());   // 0 + 0 + 0
        assertEquals(3, r1.getHistory().size());
        assertEquals(3, r2.getHistory().size());
    }

    @Test
    void pdg_invalidMoveByPlayer1_yields0for1_5for2() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        StaticRobot r1 = new StaticRobot("A","Invalid");
        StaticRobot r2 = new StaticRobot("B","Cooperate");

        game.run(r1, r2);

        assertEquals(0, r1.getScore());
        assertEquals(5, r2.getScore());
    }

    @Test
    void pdg_invalidMoveByPlayer2_yields5for1_0for2() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        StaticRobot r1 = new StaticRobot("A","Cooperate");
        StaticRobot r2 = new StaticRobot("B","Invalid");

        game.run(r1, r2);

        assertEquals(5, r1.getScore());
        assertEquals(0, r2.getScore());
    }

    @Test
    void pdg_bothInvalidMoves_yields0and0() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        StaticRobot r1 = new StaticRobot("A","Invalid1");
        StaticRobot r2 = new StaticRobot("B","Invalid2");

        game.run(r1, r2);

        assertEquals(0, r1.getScore());
        assertEquals(0, r2.getScore());
    }

    // ===== MOVE LISTENER TESTS =====

    @Test
    void moveListener_isCalledOncePerRound() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(3);
        MockMoveListener listener = new MockMoveListener();
        game.addMoveListener(listener);

        StaticRobot r1 = new StaticRobot("A","Cooperate");
        StaticRobot r2 = new StaticRobot("B","Defect");

        game.run(r1, r2);

        assertEquals(3, listener.callCount);
    }

    @Test
    void moveListener_receivesCorrectMoveData() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        MockMoveListener listener = new MockMoveListener();
        game.addMoveListener(listener);

        StaticRobot r1 = new StaticRobot("A","Cooperate");
        StaticRobot r2 = new StaticRobot("B","Defect");

        game.run(r1, r2);

        assertEquals(1, listener.callCount);
        assertEquals("A", listener.lastR1);
        assertEquals("Cooperate", listener.lastM1);
        assertEquals("B", listener.lastR2);
        assertEquals("Defect", listener.lastM2);
    }

    @Test
    void moveListener_multipleListeners_allCalled() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        MockMoveListener listener1 = new MockMoveListener();
        MockMoveListener listener2 = new MockMoveListener();
        game.addMoveListener(listener1);
        game.addMoveListener(listener2);

        StaticRobot r1 = new StaticRobot("A","Cooperate");
        StaticRobot r2 = new StaticRobot("B","Cooperate");

        game.run(r1, r2);

        assertEquals(1, listener1.callCount);
        assertEquals(1, listener2.callCount);
    }

    @Test
    void moveListener_removedListener_notCalled() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        MockMoveListener listener = new MockMoveListener();
        game.addMoveListener(listener);
        game.removeMoveListener(listener);

        StaticRobot r1 = new StaticRobot("A","Cooperate");
        StaticRobot r2 = new StaticRobot("B","Defect");

        game.run(r1, r2);

        assertEquals(0, listener.callCount);
    }

    // ===== SCORE LISTENER TESTS =====

    @Test
    void scoreListener_isCalledOnceAtEnd() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(3);
        MockScoreListener listener = new MockScoreListener();
        game.addScoreListener(listener);

        StaticRobot r1 = new StaticRobot("A","Cooperate");
        StaticRobot r2 = new StaticRobot("B","Cooperate");

        game.run(r1, r2);

        assertEquals(1, listener.callCount);
    }

    @Test
    void scoreListener_receivesCorrectScoreData() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(3);
        MockScoreListener listener = new MockScoreListener();
        game.addScoreListener(listener);

        StaticRobot r1 = new StaticRobot("A","Defect");
        StaticRobot r2 = new StaticRobot("B","Cooperate");

        game.run(r1, r2);

        assertEquals(1, listener.callCount);
        assertEquals("A", listener.lastR1);
        assertEquals(15, listener.lastS1);  // 5 * 3 rounds
        assertEquals("B", listener.lastR2);
        assertEquals(0, listener.lastS2);   // 0 * 3 rounds
    }

    @Test
    void scoreListener_multipleListeners_allCalled() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        MockScoreListener listener1 = new MockScoreListener();
        MockScoreListener listener2 = new MockScoreListener();
        game.addScoreListener(listener1);
        game.addScoreListener(listener2);

        StaticRobot r1 = new StaticRobot("A","Cooperate");
        StaticRobot r2 = new StaticRobot("B","Cooperate");

        game.run(r1, r2);

        assertEquals(1, listener1.callCount);
        assertEquals(1, listener2.callCount);
    }

    @Test
    void scoreListener_removedListener_notCalled() {
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(1);
        MockScoreListener listener = new MockScoreListener();
        game.addScoreListener(listener);
        game.removeScoreListener(listener);

        StaticRobot r1 = new StaticRobot("A","Cooperate");
        StaticRobot r2 = new StaticRobot("B","Defect");

        game.run(r1, r2);

        assertEquals(0, listener.callCount);
    }

    // ===== COPYBOT PLAYER 1 TESTS =====

    @Test
    void copyBot_asPlayer1_copiesEnemyMove() {
        // This test specifically covers CopyBot when it's player 1 in a game
        // Real gameplay scenario: CopyBot vs DefectBot
        PrisonersDilemmaGame game = new PrisonersDilemmaGame(2);
        CopyBot copybot = new CopyBot("CopyBot");
        DefectBot defectbot = new DefectBot("DefectBot");

        game.run(copybot, defectbot);

        // After first round, CopyBot should have history with DefectBot's move (Defect)
        // On second round, CopyBot should return "Defect"
        assertEquals(2, copybot.getHistory().size());
        assertEquals("Defect", copybot.getAction("DefectBot"));
    }

    @Test
    void copyBot_withMixedHistory_copiesMostRecentEnemyMove() {
        CopyBot c = new CopyBot("C");
        // Simulate real gameplay where CopyBot is player1 (C is always player1 in its own history)
        c.addHistory(new History("C","Enemy1","Cooperate","Defect", new int[]{0,5}));
        c.addHistory(new History("C","Enemy2","Defect","Cooperate", new int[]{5,0}));
        c.addHistory(new History("C","Enemy1","Cooperate","Cooperate", new int[]{3,3}));

        // Most recent interaction with Enemy1 is Cooperate (player2Move in last entry)
        assertEquals("Cooperate", c.getAction("Enemy1"));
    }

    @Test
    void copyBot_noHistoryWithEnemy_defaulstToCooperate() {
        CopyBot c = new CopyBot("C");
        c.addHistory(new History("C","Enemy1","Cooperate","Defect", new int[]{0,5}));

        // No history with Enemy2, should default to Cooperate
        assertEquals("Cooperate", c.getAction("Enemy2"));
    }
}

