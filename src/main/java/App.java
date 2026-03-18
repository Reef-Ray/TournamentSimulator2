import robots.*;
import tournaments.*;
import games.*;
import clients.*;
import servers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
// import java.util.Scanner;

public class App {
    public static void main(String[] args) {

        Tournament tournament = new RoundRobinTournament(
                new ArrayList<>(),
                new PrisonersDilemmaGame(5),
                0 // maxPlayers=0 for unlimited
        );

        // Add "remote" bots directly
        tournament.addPlayer(new RemoteBot("Viole", "2.3.4.5", "1111"));
        tournament.addPlayer(new RemoteBot("Khun", "1.3.4", "2222"));
        tournament.addPlayer(new RemoteBot("Rak", "4.5.7", "3333"));
        tournament.addPlayer(new RemoteBot("Joe", "112.3.4.5", "4444"));

        // Run tournament
        tournament.run();

        // Print results
        System.out.println("\n=== Tournament Results ===");
        for (Robot r : tournament.main()) { // main() returns sorted Robot[]
            System.out.println(r.getName() + " - Score: " + r.getScore());
        }
    }
}
