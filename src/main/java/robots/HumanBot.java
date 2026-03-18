package robots;

import java.util.Scanner;

public class HumanBot extends Robot {

    private Scanner scanner;

    public HumanBot(String name) {
        super(name);
    }

    @Override
    public String getAction(String enemyName) {
        System.out.print(", enter your move: ");
        return scanner.nextLine();
    }
}