package tournaments;
import games.Game;
import robots.Robot;
import java.util.*;

public class RoundRobinTournament extends Tournament {

    public RoundRobinTournament(String name, List<Robot> robots, Game game, int maxPlayers) {
        super(name, robots, game, maxPlayers);
    }

    @Override
    public List<Robot[]> getBracket() {

        List<Robot[]> matches = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                matches.add(new Robot[]{players.get(i), players.get(j)});
            }
        }

        return matches;
    }

    @Override
    public boolean checkEnd() {
        return finished; // Round Robin ends after one full pass through the bracket
    }
    
    @Override
    public boolean isOpen() { //player count
        if (endCondition == 0) {
            return true; // Local tournaments are always open    
        } else if (players.size() >= endCondition) {
            return false;
        }
        return true;
    }
}