package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TournamentsListViewController {

    @FXML
    private Button enterClosedTournament;

    @FXML
    private Button enterOpenTournament;

    @FXML
    private Button enterServer;

    @FXML
    private TextField serverPortField;

    @FXML
    private TextField serverIpField;

    @FXML
    private ListView<String> openTournamentsList;

    @FXML
    private ListView<String> closedTournamentsList;

    @FXML
    private TextField openTournamentNameField;

    @FXML
    private TextField closedTournamentNameField;

    private String baseUrl;
    private final ViewTransitionalModel viewModel = ViewTransitionalModel.getInstance();

    @FXML
    public void connectToServer(ActionEvent event) {
        String port = serverPortField.getText();
        String ip = serverIpField.getText();
        if (port == null || port.isBlank() || ip == null || ip.isBlank()) {
            return; // nothing to do
        }

        baseUrl = "http://" + ip + ":" + port;

        RestTemplate rest = new RestTemplate();

        try {
            String[] all = rest.getForObject(baseUrl + "/tournament/tournaments", String[].class);
            String[] available = rest.getForObject(baseUrl + "/tournament/available", String[].class);

            List<String> allList = all == null ? List.of() : Arrays.asList(all);
            List<String> availList = available == null ? List.of() : Arrays.asList(available);

            ObservableList<String> openItems = FXCollections.observableArrayList(availList);
            List<String> closed = new ArrayList<>();
            for (String t : allList) {
                if (!availList.contains(t)) closed.add(t);
            }
            ObservableList<String> closedItems = FXCollections.observableArrayList(closed);

            openTournamentsList.setItems(openItems);
            closedTournamentsList.setItems(closedItems);
        } catch (Exception e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
    }

    @FXML
    public void openRegistrationView(ActionEvent event) {
        // navigate to registration view
        try {
            String inputName = openTournamentNameField.getText();
            if (inputName == null) inputName = "";
            inputName = inputName.trim();

            String chosen = inputName;
            if (chosen.isBlank()) {
                chosen = openTournamentsList.getSelectionModel().getSelectedItem();
            }

            if (chosen == null || chosen.isBlank() || !openTournamentsList.getItems().contains(chosen)) {
                System.err.println("Registration view not opened: tournament name not found: '" + (chosen == null ? "" : chosen) + "'");
                return;
            }

            final String url = baseUrl == null || baseUrl.isBlank() ? "http://127.0.0.1:8080" : baseUrl;
            viewModel.setBaseUrl(url);
            viewModel.showRegistrationView(chosen);
        } catch (Exception e) {
            System.err.println("Failed to open registration view: " + e.getMessage());
        }
    }

    @FXML
    public void openSpectateView(ActionEvent event) {
        try {
        
            String inputName = closedTournamentNameField.getText();
            if (inputName == null) inputName = "";
            inputName = inputName.trim();

            String chosen = inputName;
            if (chosen.isBlank()) {
                chosen = closedTournamentsList.getSelectionModel().getSelectedItem();
            }

            if (chosen == null || chosen.isBlank() || !closedTournamentsList.getItems().contains(chosen)) {
                System.err.println("Spectate view not opened: tournament name not found: '" + (chosen == null ? "" : chosen) + "'");
                return;
            }

            viewModel.showSpectateView(chosen);
        } catch (Exception e) {
            System.err.println("Failed to open spectate view: " + e.getMessage());
        }
    }

}
