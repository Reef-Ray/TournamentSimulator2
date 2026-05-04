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
 
    @FXML
    private ScrollPane logScroll;
 
    @FXML
    private Label logLabel;
 
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
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
 
    public void setLogMessages(String messages) {
        if (messages == null || messages.isBlank()) messages = "No log entries yet.";
        final String m = messages;
        Platform.runLater(() -> logLabel.setText(m));
    }
 
    private void startPolling() {
        String encoded = java.net.URLEncoder.encode(tournamentName, java.nio.charset.StandardCharsets.UTF_8);
        String serverBase = viewModel.getBaseUrl();
 
        // Existing observer messages poll
        scheduler.scheduleAtFixedRate(() -> {
            if (tournamentName == null || tournamentName.isBlank()) return;
            try {
                RestTemplate rest = new RestTemplate();
                String url = serverBase + "/tournament/observe?name=" + encoded;
                String[] msgs = rest.getForObject(url, String[].class);
                final String joined = (msgs != null && msgs.length > 0)
                        ? String.join("\n", msgs) : "No observer messages.";
                Platform.runLater(() -> messagesLabel.setText(joined));
            } catch (Exception e) {
                Platform.runLater(() -> messagesLabel.setText("Failed to fetch observer messages: " + e.getMessage()));
            }
        }, 0, 2, TimeUnit.SECONDS);
 
        scheduler.scheduleAtFixedRate(() -> {
            if (tournamentName == null || tournamentName.isBlank()) return;
            try {
                RestTemplate rest = new RestTemplate();
                String url = serverBase + "/tournament/log?name=" + encoded;
                String[] entries = rest.getForObject(url, String[].class);
                final String joined = (entries != null && entries.length > 0)
                        ? String.join("\n", entries) : "No log entries yet.";
                Platform.runLater(() -> logLabel.setText(joined));
            } catch (Exception e) {
                Platform.runLater(() -> logLabel.setText("No log entries yet."));
            }
        }, 0, 2, TimeUnit.SECONDS);
    }
 
    @FXML
    public void goBack(ActionEvent event) {
        scheduler.shutdownNow();
        viewModel.showTournamentList();
    }
}