package servers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import games.PrisonersDilemmaGame;
import robots.CooperateBot;
import robots.DefectBot;
import robots.RandomBot;
import tournaments.RoundRobinTournament;

import java.util.ArrayList;
import java.util.List;

@Component
public class MockTournamentsLoader implements CommandLineRunner {

    private final TournamentServer server;

    public MockTournamentsLoader(TournamentServer server) {
        this.server = server;
    }

    @Override
    public void run(String... args) throws Exception {
        for (int i = 1; i <= 15; i++) {
            String name = "OpenEmpty" + i;
            server.addTournament(name, new RoundRobinTournament(name, new ArrayList<>(), new PrisonersDilemmaGame(10), 4));
        }

        List<robots.Robot> players1 = new ArrayList<>();
        players1.add(new DefectBot("D1"));
        players1.add(new CooperateBot("C1"));
        players1.add(new DefectBot("D2"));
        players1.add(new CooperateBot("C2"));
        server.addTournament("ClosedFull1", new RoundRobinTournament("ClosedFull1", players1, new PrisonersDilemmaGame(10), 4));

        List<robots.Robot> players2 = new ArrayList<>();
        players2.add(new DefectBot("D3"));
        players2.add(new RandomBot("R1"));
        players2.add(new CooperateBot("C3"));
        players2.add(new RandomBot("R2"));
        server.addTournament("ClosedFull2", new RoundRobinTournament("ClosedFull2", players2, new PrisonersDilemmaGame(10), 4));
        server.runTournament("ClosedFull2");
        

        List<robots.Robot> players3 = new ArrayList<>();
        players3.add(new DefectBot("D4"));
        players3.add(new CooperateBot("C4"));
        players3.add(new DefectBot("D5"));
        players3.add(new CooperateBot("C5"));
        server.addTournament("ClosedFull3", new RoundRobinTournament("ClosedFull3", players3, new PrisonersDilemmaGame(10), 4));
    }
}
