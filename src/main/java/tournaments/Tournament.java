package tournaments;

import games.Game;
import robots.Robot;
import java.util.*;

public abstract class Tournament {

    protected List<Robot> players;
    protected Game game;
    protected boolean finished = false;
    protected int maxPlayers; // 0 for local tournaments since players will already be added

    public Tournament(List<Robot> players, Game game, int maxPlayers) {
        this.players = players;
        this.game = game;
        this.maxPlayers = maxPlayers;
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
        }

        finished = true;
    }

    public void addPlayer(Robot bot) {
        if (isOpen()) {
            players.add(bot);
        } else {
            System.out.println("Cannot add player: tournament is full");
        }
    }

    public List<Robot> getPlayers() {
        return players;
    }

    public abstract List<Robot[]> getBracket();

    public abstract boolean checkEnd();

    public abstract boolean isOpen();
}