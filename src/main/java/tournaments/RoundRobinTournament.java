package tournaments;
import games.Game;
import robots.Robot;
import java.util.*;

public class RoundRobinTournament extends Tournament {

    public RoundRobinTournament(List<Robot> robots, Game game, int maxPlayers) {
        super(robots, game, maxPlayers);
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
}