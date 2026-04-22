package tests;

import org.junit.jupiter.api.Test;
import servers.TournamentServer;
import tournaments.RoundRobinTournament;
import games.PrisonersDilemmaGame;
import robots.DefectBot;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TournamentServerTests {

    @Test
    void availableAndAllTournamentsSeparateOpenVsClosed() {
        TournamentServer server = new TournamentServer();

        // Open tournament: endCondition = 0 (local/open)
        RoundRobinTournament open = new RoundRobinTournament("Open", new ArrayList<>(), new PrisonersDilemmaGame(1), 0);
        // Closed tournament: endCondition = 1 and already has one player
        List<robots.Robot> players = new ArrayList<>();
        players.add(new DefectBot("P1"));
        RoundRobinTournament closed = new RoundRobinTournament("Closed", players, new PrisonersDilemmaGame(1), 1);

        server.addTournament(open.getName(), open);
        server.addTournament(closed.getName(), closed);

        List<String> all = server.getAllTournaments();
        List<String> avail = server.getAvailableTournaments();

        assertTrue(all.contains("Open"));
        assertTrue(all.contains("Closed"));

        assertTrue(avail.contains("Open"));
        assertFalse(avail.contains("Closed"));
    }
}
