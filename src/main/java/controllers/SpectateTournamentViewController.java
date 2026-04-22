package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.event.ActionEvent;

import org.springframework.web.client.RestTemplate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SpectateTournamentViewController {

    @FXML
    private Button backButton;

    @FXML
    private Label tournamentNameLabel;

    @FXML
    private ScrollPane messagesScroll;

    @FXML
    private Label messagesLabel;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private String tournamentName;
    private final ViewTransitionalModel viewModel = ViewTransitionalModel.getInstance();

    public void setTournamentName(String name) {
        if (name == null || name.isBlank()) name = "<Unnamed Tournament>";
        this.tournamentName = name;
        tournamentNameLabel.setText(name);
        startPolling();
    }

    public void setObserverMessages(String messages) {
        if (messages == null || messages.isBlank()) messages = "No messages yet.";
        final String m = messages;
        Platform.runLater(() -> messagesLabel.setText(m));
    }

    private void startPolling() {
        scheduler.scheduleAtFixedRate(() -> {
            if (tournamentName == null || tournamentName.isBlank()) return;
            try {
                String serverBase = "http://127.0.0.1:8080";
                RestTemplate rest = new RestTemplate();
                String url = serverBase + "/tournament/observe?name=" + java.net.URLEncoder.encode(tournamentName, java.nio.charset.StandardCharsets.UTF_8);
                String[] msgs = rest.getForObject(url, String[].class);
                final String joined;
                if (msgs != null && msgs.length > 0) joined = String.join("\n", msgs);
                else joined = "No observer messages.";
                Platform.runLater(() -> messagesLabel.setText(joined));
            } catch (Exception e) {
                Platform.runLater(() -> messagesLabel.setText("Failed to fetch observer messages: " + e.getMessage()));
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    @FXML
    public void goBack(ActionEvent event) {
        scheduler.shutdownNow();
        viewModel.showTournamentList();
    }
}
