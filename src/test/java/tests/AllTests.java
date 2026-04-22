package tests;

import games.PrisonersDilemmaGame;
import robots.*;
import tournaments.RoundRobinTournament;

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
    void testCopyBot_CopiesMostRecentEnemyMove() {
        CopyBot c = new CopyBot("CopyBot");
        c.addHistory(new History("Enemy", "CopyBot", "Defect", "Cooperate", new int[]{5, 0}));
        assertEquals("Defect", c.getAction("Enemy"));
    }

    @Test
    void testRobot_Scoring_AccumulatesCorrectly() {
        Robot r = new CopyBot("TestBot");
        assertEquals(0, r.getScore());
        r.addHistory(new History("TestBot", "A", "Cooperate", "Cooperate", new int[]{3, 3}));
        assertEquals(3, r.getScore());
        r.addHistory(new History("TestBot", "B", "Defect", "Cooperate", new int[]{5, 0}));
        assertEquals(8, r.getScore());
    }

    @Test
    void testRobot_BasicProperties() {
        Robot r = new DefectBot("MyBot");
        assertEquals("MyBot", r.getName());
        assertNotNull(r.getHistory());
        assertTrue(r.getHistory().isEmpty());
    }

    @Test
    void tournament_BracketGeneration_MatchesCombinatorics() {
        List<Robot> players = Arrays.asList(
                new DefectBot("A"),
                new CopyBot("B"),
                new RandomBot("C"),
                new DefectBot("D")
        );

        RoundRobinTournament t = new RoundRobinTournament("Test", players, new PrisonersDilemmaGame(1), 0);
        assertEquals(6, t.getBracket().size());
    }

    @Test
    void tournament_RunsToCompletion() {
        List<Robot> players = Arrays.asList(
                new CooperateBot("A"),
                new DefectBot("B"),
                new DefectBot("C")
        );

        RoundRobinTournament t = new RoundRobinTournament("Test", players, new PrisonersDilemmaGame(1), 0);
        assertFalse(t.checkEnd());
        Robot[] results = t.main();
        assertTrue(t.checkEnd());
        assertEquals(3, results.length);
        assertEquals("B", results[0].getName());
    }

}

