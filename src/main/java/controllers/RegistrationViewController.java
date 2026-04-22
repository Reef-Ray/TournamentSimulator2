package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class RegistrationViewController {

    @FXML
    private Button backButton;

    @FXML
    private Label tournamentNameLabel;

    @FXML
    private Button humanBotButton;

    @FXML
    private Button remoteBotButton;

    @FXML
    private Label currentPlayerCount;

    @FXML
    private Label maxPlayerCount;

    private String tournamentName;
    private final ViewTransitionalModel viewModel = ViewTransitionalModel.getInstance();

    public void setTournamentName(String name) {
        if (name == null || name.isBlank()) name = "<Unnamed Tournament>";
        this.tournamentName = name;
        tournamentNameLabel.setText(name);
        refreshCounts();
    }

    private void refreshCounts() {
        //add
    }

    @FXML
    public void registerHuman(ActionEvent event) {
        System.out.println("Registering human player...");
    }

    @FXML
    public void registerRemote(ActionEvent event) {
        System.out.println("Registering remote player...");
    }

    @FXML
    public void goBack(ActionEvent event) {
        viewModel.showTournamentList();
    }
}
