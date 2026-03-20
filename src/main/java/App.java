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

        //make bots
        RemoteBot bot1 = new RemoteBot("Viole", "2.3.4.5", "1111") {
                @Override
                public  String getAction(String opponentName) {
                    // Simple strategy: always cooperate
                    return "COOPERATE";
                }
        };
        RemoteBot bot2 = new RemoteBot("Khun", "1.3.4", "2222") {
                @Override
                public  String getAction(String opponentName) {
                    // Simple strategy: always defect
                    return "Defect";
                }
        };
        RemoteBot bot3 = new RemoteBot("Rak", "4.5.7", "3333") {
                @Override
                public  String getAction(String opponentName) {
                    // Simple strategy: random
                    return Math.random() < 0.5 ? "COOPERATE" : "DEFECT";
                }
        };
        RemoteBot bot4 = new RemoteBot("Joe", "112.3.4.5", "4444") {
                @Override
                public  String getAction(String opponentName) {
                    return Math.random() < 0.5 ? "COOPERATE" : "DEFECT";
                }
        };

        // Add "remote" bots directly
        tournament.addPlayer(bot1);
        tournament.addPlayer(bot2);
        tournament.addPlayer(bot3);
        tournament.addPlayer(bot4);

        // Run tournament
        Robot[] result = tournament.main();

        // Print results
        System.out.println("\n=== Tournament Results ===");
        for (Robot r : result) {
            System.out.println(r.getName() + " - Score: " + r.getScore());
        }
    }
}
