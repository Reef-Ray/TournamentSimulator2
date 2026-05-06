package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


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

    @FXML
    private TextField playerNameField;

    @FXML
    private TextField portField;

    private String tournamentName;
    private String registeredPlayerName; // Track if this client registered a player
    private final ViewTransitionalModel viewModel = ViewTransitionalModel.getInstance();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int currentCount = 0;
    private int maxCount = 0;

    public void setTournamentName(String name) {
        if (name == null || name.isBlank()) name = "<Unnamed Tournament>";
        this.tournamentName = name;
        tournamentNameLabel.setText(name);
        refreshCounts();
        startPolling();
    }

    private void refreshCounts() {
        try {
            String serverBase = viewModel.getBaseUrl();
            RestTemplate rest = new RestTemplate();
            String url = serverBase + "/tournament/info?name=" + java.net.URLEncoder.encode(tournamentName, java.nio.charset.StandardCharsets.UTF_8);
            Map<String, Integer> info = rest.getForObject(url, Map.class);
            
            if (info != null) {
                currentCount = (int) info.getOrDefault("current", 0);
                maxCount = (int) info.getOrDefault("max", 0);
                
                final int current = currentCount;
                final int max = maxCount;
                Platform.runLater(() -> {
                    currentPlayerCount.setText(String.valueOf(current));
                    maxPlayerCount.setText(String.valueOf(max));
                    
                    // Disable buttons if tournament is full
                    boolean isFull = current >= max && max > 0;
                    humanBotButton.setDisable(isFull || registeredPlayerName != null);
                    remoteBotButton.setDisable(isFull || registeredPlayerName != null);
                });
            }
        } catch (Exception e) {
            System.err.println("Failed to refresh player counts: " + e.getMessage());
        }
    }

    private void startPolling() {
        scheduler.scheduleAtFixedRate(this::refreshCounts, 0, 1, TimeUnit.SECONDS);
    }

    @FXML
    public void registerHuman(ActionEvent event) {
        System.out.println("Registering human player... (not yet implemented)");
    }

    @FXML
    public void registerRemote(ActionEvent event) {
        String playerName = playerNameField.getText();
        String port = portField.getText();

        if (playerName == null || playerName.isBlank()) {
            System.err.println("Player name is required");
            return;
        }

        if (port == null || port.isBlank()) {
            System.err.println("Port is required");
            return;
        }

        try {
            String serverBase = viewModel.getBaseUrl();
            RestTemplate rest = new RestTemplate();
            
            String url = serverBase + "/tournament/register"
                    + "?name=" + java.net.URLEncoder.encode(playerName, java.nio.charset.StandardCharsets.UTF_8)
                    + "&tournament=" + java.net.URLEncoder.encode(tournamentName, java.nio.charset.StandardCharsets.UTF_8)
                    + "&type=remote"
                    + "&ip=localhost"
                    + "&port=" + port;

            String result = rest.postForObject(url, null, String.class);
            System.out.println("Registration result: " + result);

            registeredPlayerName = playerName;

            humanBotButton.setDisable(true);
            remoteBotButton.setDisable(true);

            refreshCounts();
            if (currentCount >= maxCount && maxCount > 0) {
                startTournament();
                Platform.runLater(() -> {
                    scheduler.shutdownNow();
                    viewModel.showSpectateView(tournamentName);
                });
            }

        } catch (Exception e) {
            System.err.println("Failed to register remote player: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startTournament() {
        try {
            String serverBase = viewModel.getBaseUrl();
            RestTemplate rest = new RestTemplate();
            String url = serverBase + "/tournament/run?name=" + java.net.URLEncoder.encode(tournamentName, java.nio.charset.StandardCharsets.UTF_8);
            String result = rest.postForObject(url, null, String.class);
            System.out.println("Tournament start result: " + result);
        } catch (Exception e) {
            System.err.println("Failed to start tournament: " + e.getMessage());
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        scheduler.shutdownNow();
        viewModel.showTournamentList();
    }
}
