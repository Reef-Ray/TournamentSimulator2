package robots;

public class CopyBot extends Robot {

    public CopyBot(String name) {
        super(name);
    }

    @Override
    public String getAction(String enemyName) {
        for (int i = history.size() - 1; i >= 0; i--) {
            History h = history.get(i);
            if (h.player1.equals(enemyName)) {
                return h.player1Move;  // Enemy was player1, return what they did
            }
            if (h.player2.equals(enemyName)) {
                return h.player2Move;  // Enemy was player2, return what they did
            }
        }
        return "Cooperate";
    }
}