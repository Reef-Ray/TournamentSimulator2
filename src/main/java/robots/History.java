package robots;

public class History {
    public String player1;
    public String player2;
    public String player1Move;
    public String player2Move;
    public int[] outcome; // [score1, score2]

    public History(String player1, String player2,
                   String player1Move, String player2Move,
                   int[] outcome) {
        this.player1 = player1;
        this.player2 = player2;
        this.player1Move = player1Move;
        this.player2Move = player2Move;
        this.outcome = outcome;
    }
}