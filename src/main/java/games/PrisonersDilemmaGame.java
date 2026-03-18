package games;
public class PrisonersDilemmaGame extends Game {

    public PrisonersDilemmaGame(int maxRounds) {
        super(maxRounds);
    }

    @Override
    protected int[] decideOutcome(String m1, String m2) {

        boolean valid1 = "Cooperate".equals(m1) || "Defect".equals(m1);
        boolean valid2 = "Cooperate".equals(m2) || "Defect".equals(m2);
        if (!valid1 && !valid2) {
            return new int[]{0, 0};
        }
        if (!valid1) {
            return new int[]{0, 5};
        }
        if (!valid2) {
            return new int[]{5, 0};
        }


        if (m1.equals("Cooperate") && m2.equals("Cooperate"))
            return new int[]{3, 3};

        if (m1.equals("Cooperate") && m2.equals("Defect"))
            return new int[]{0, 5};

        if (m1.equals("Defect") && m2.equals("Cooperate"))
            return new int[]{5, 0};

        return new int[]{1, 1};
    }
}