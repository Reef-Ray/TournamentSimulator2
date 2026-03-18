package listeners;

public class ScoreListener {

    public void updateScore(String r1, int s1, String r2, int s2) {
        System.out.println("Final Score:");
        System.out.println(r1 + " = " + s1);
        System.out.println(r2 + " = " + s2);
        System.out.println("----------------------");
    }
}