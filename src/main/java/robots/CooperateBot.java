package robots;

public class CooperateBot extends Robot {

    public CooperateBot(String name) {
        super(name);
    }

    @Override
    public String getAction(String enemyName) {
        return "Cooperate";
    }
}