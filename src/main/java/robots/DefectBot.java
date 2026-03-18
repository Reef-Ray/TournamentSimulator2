package robots;

public class DefectBot extends Robot {

    public DefectBot(String name) {
        super(name);
    }

    @Override
    public String getAction(String enemyName) {
        return "Defect";
    }
}