package robots;

public class RandomBot extends Robot {

    public RandomBot(String name) {
        super(name);
    }

    @Override
    public String getAction(String enemyName) {
        if (Math.random() > 0.5) {
            return "Cooperate";
        } else {
            return "Defect";
        }
    }
}