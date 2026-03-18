package robots;

import java.util.ArrayList;
import java.util.List;

public abstract class Robot {

    protected String name;
    protected List<History> history;

    public Robot(String name) {
        this.name = name;
        this.history = new ArrayList<>();
    }

    public abstract String getAction(String enemyName);

    public String getName() {
        return name;
    }

    public void addHistory(History h) {
        history.add(h);
    }

    public List<History> getHistory() {
        return history;
    }

    public void addToHistory(String entry) {
        History h = new History(this.name, "", entry, "", new int[]{0,0});
        history.add(h);
    }

    public int getScore() {
        int total = 0;
        for (History h : history) {
            if (h.player1.equals(name)) {
                total += h.outcome[0];
            } else if (h.player2.equals(name)) {
                total += h.outcome[1];
            }
        }
        return total;
    }
}