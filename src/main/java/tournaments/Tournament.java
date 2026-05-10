package tournaments;

import games.Game;
import robots.Robot;
import java.util.*;

public abstract class Tournament {

    protected String name;
    protected List<Robot> players;
    protected Game game;
    protected boolean finished = false;
    protected int endCondition; // 0 for local tournaments since players will already be added

    public Tournament(String name, List<Robot> players, Game game, int endCondition) {
        this.name = name;
        this.players = players;
        this.game = game;
        this.endCondition = endCondition;
    }

    public Robot[] main() {
        run();
        List<Robot> sorted = new ArrayList<>(players);
        sorted.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        return sorted.toArray(new Robot[0]);
    }

    public void run() {
        if (finished) return;

        List<Robot[]> matches = getBracket();

        for (Robot[] pair : matches) {
            game.run(pair[0], pair[1]);
            
            // Add delay between matches so they don't complete simultaneously
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.err.println("Match delay interrupted: " + e.getMessage());
            }
        }

        finished = true;
    }

    public void addPlayer(Robot bot) {
        if (isOpen()) {
            players.add(bot);
        } else {
            System.out.println("Cannot add player: Registration closed");
        }
    }

    public List<Robot> getPlayers() {
        return players;
    }

    public int getMaxPlayers() {
        return endCondition;
    }

    public String getName() {
        return name;
    }

    public abstract List<Robot[]> getBracket();

    public abstract boolean checkEnd();

    public abstract boolean isOpen();
}