package robots;

public class CopyBot extends Robot {

    public CopyBot(String name) {
        super(name);
    }

    @Override
    public String getAction(String enemyName) {
        for (int i = history.size() - 1; i >= 0; i--) {
            History h = history.get(i);
            if (h.player1.equals(enemyName) || h.player2.equals(enemyName)) {
                if (h.player1.equals(enemyName)) return h.player1Move;
                return h.player2Move;
            }
        }
        return "Cooperate";
    }
}