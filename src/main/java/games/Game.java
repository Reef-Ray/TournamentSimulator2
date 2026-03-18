package games;

import listeners.MoveListener;
import listeners.ScoreListener;
import robots.History;
import robots.Robot;
import java.util.*;

public abstract class Game {

    protected List<String> validMoves = new ArrayList<>();
    protected List<MoveListener> moveListeners = new ArrayList<>();
    protected List<ScoreListener> scoreListeners = new ArrayList<>();

    protected int roundsPlayed;
    protected int maxRounds;

    public Game(int maxRounds) {
        this.maxRounds = maxRounds;
    }

    public void addMoveListener(MoveListener l) {
        moveListeners.add(l);
    }

    public void removeMoveListener(MoveListener l) {
        moveListeners.remove(l);
    }

    public void addScoreListener(ScoreListener l) {
        scoreListeners.add(l);
    }

    public void removeScoreListener(ScoreListener l) {
        scoreListeners.remove(l);
    }

    public void run(Robot r1, Robot r2) {

        roundsPlayed = 0;
        int score1 = 0;
        int score2 = 0;

        while (!checkEnd()) {

            String move1 = r1.getAction(r2.getName());
            String move2 = r2.getAction(r1.getName());

            notifyMoveListeners(r1.getName(), move1, r2.getName(), move2);

            int[] result = decideOutcome(move1, move2);

            History h1 = new History(r1.getName(), r2.getName(), move1, move2, result);
            History h2 = new History(r2.getName(), r1.getName(), move2, move1, new int[]{result[1], result[0]});
            r1.addHistory(h1);
            r2.addHistory(h2);

            score1 += result[0];
            score2 += result[1];

            roundsPlayed++;
        }

        notifyScoreListeners(r1.getName(), score1, r2.getName(), score2);
    }

    public boolean checkEnd() {
        return roundsPlayed >= maxRounds;
    }

    protected abstract int[] decideOutcome(String m1, String m2);

    private void notifyMoveListeners(String r1, String m1, String r2, String m2) {
        for (MoveListener l : moveListeners) {
            l.updateMove(r1, m1, r2, m2);
        }
    }

    private void notifyScoreListeners(String r1, int s1, String r2, int s2) {
        for (ScoreListener l : scoreListeners) {
            l.updateScore(r1, s1, r2, s2);
        }
    }
}