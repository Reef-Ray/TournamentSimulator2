package tests;
 
import static org.junit.jupiter.api.Assertions.*;
 
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
 
import controllers.SpectateTournamentViewController;
import controllers.ViewTransitionalModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
 
@ExtendWith(ApplicationExtension.class)
public class SpectateViewTests {
 
    private SpectateTournamentViewController controller;
    private ViewTransitionalModel model;
 
    @Start
    private void start(Stage stage) {
        model = ViewTransitionalModel.getInstance();
        model.setPrimaryStage(stage);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SpectateTournamentView.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
    @Test
    void testBackButtonExists(FxRobot robot) {
        Button backButton = robot.lookup("#backButton").queryAs(Button.class);
        assertNotNull(backButton);
        assertNotNull(backButton.getOnAction());
    }
 
    @Test
    void testBackButtonNavigatesToTournamentList(FxRobot robot) {
        robot.clickOn("#backButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(robot.lookup(".root").query());
    }
  
    @Test
    void testSetTournamentNameUpdatesLabel(FxRobot robot) {
        robot.interact(() -> controller.setTournamentName("Finals"));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Finals", robot.lookup("#tournamentNameLabel").queryAs(Label.class).getText());
    }
 
    @Test
    void testSetTournamentNameNullShowsPlaceholder(FxRobot robot) {
        robot.interact(() -> controller.setTournamentName(null));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("<Unnamed Tournament>", robot.lookup("#tournamentNameLabel").queryAs(Label.class).getText());
    }
 
    @Test
    void testSetTournamentNameBlankShowsPlaceholder(FxRobot robot) {
        robot.interact(() -> controller.setTournamentName("   "));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("<Unnamed Tournament>", robot.lookup("#tournamentNameLabel").queryAs(Label.class).getText());
    }
  
    @Test
    void testSetObserverMessagesUpdatesLabel(FxRobot robot) {
        robot.interact(() -> controller.setObserverMessages("Alice chose Cooperate | Bob chose Defect"));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Alice chose Cooperate | Bob chose Defect", robot.lookup("#messagesLabel").queryAs(Label.class).getText());
    }

    @Test
    void testSetLogMessagesUpdatesLabel(FxRobot robot) {
        robot.interact(() -> controller.setLogMessages("[12:00] Tournament started\n[12:01] Match 1 finished"));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("[12:00] Tournament started\n[12:01] Match 1 finished", robot.lookup("#logLabel").queryAs(Label.class).getText());
    }
 
    @Test
    void testSetObserverMessagesNullShowsPlaceholder(FxRobot robot) {
        robot.interact(() -> controller.setObserverMessages(null));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("No messages yet.", robot.lookup("#messagesLabel").queryAs(Label.class).getText());
    }
 
    @Test
    void testSetObserverMessagesBlankShowsPlaceholder(FxRobot robot) {
        robot.interact(() -> controller.setObserverMessages("   "));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("No messages yet.", robot.lookup("#messagesLabel").queryAs(Label.class).getText());
    }

    @Test
    void testSetLogMessagesNullShowsPlaceholder(FxRobot robot) {
        robot.interact(() -> controller.setLogMessages(null));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("No log entries yet.", robot.lookup("#logLabel").queryAs(Label.class).getText());
    }

    @Test
    void testSetLogMessagesBlankShowsPlaceholder(FxRobot robot) {
        robot.interact(() -> controller.setLogMessages("   "));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("No log entries yet.", robot.lookup("#logLabel").queryAs(Label.class).getText());
    }
  
    @Test
    void testShowSpectateViewSetsName(FxRobot robot) {
        robot.interact(() -> model.showSpectateView("Semifinals"));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Semifinals", robot.lookup("#tournamentNameLabel").queryAs(Label.class).getText());
    }
 
    @Test
    void testShowSpectateViewWithNullDoesNotThrow(FxRobot robot) {
        assertDoesNotThrow(() -> robot.interact(() -> model.showSpectateView(null)));
        WaitForAsyncUtils.waitForFxEvents();
    }
 
    @Test
    void testShowTournamentListFromSpectateView(FxRobot robot) {
        assertDoesNotThrow(() -> robot.interact(() -> model.showTournamentList()));
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(robot.lookup(".root").query());
    }

    @Test
    void testURLEncoding() {
        String tournamentName = "Tournament Name With Spaces";
        String encoded = java.net.URLEncoder.encode(tournamentName, java.nio.charset.StandardCharsets.UTF_8);
        
        assertNotNull(encoded);
        assertFalse(encoded.contains(" "));
        assertTrue(encoded.contains("+") || encoded.contains("%20"));
    }

    @Test
    void testMessageHandling_Empty() {
        String messages = null;
        String display = (messages == null || messages.isBlank()) ? "No messages yet." : messages;
        assertEquals("No messages yet.", display);
    }

    @Test
    void testMessageHandling_Valid() {
        String messages = "Message 1\nMessage 2";
        String display = (messages == null || messages.isBlank()) ? "No messages yet." : messages;
        assertEquals(messages, display);
    }

    @Test
    void testLogHandling_Empty() {
        String logs = null;
        String display = (logs == null || logs.isBlank()) ? "No log entries yet." : logs;
        assertEquals("No log entries yet.", display);
    }

    @Test
    void testMessageArrayJoining() {
        String[] msgs = {"Msg1", "Msg2", "Msg3"};
        String joined = String.join("\n", msgs);
        assertEquals("Msg1\nMsg2\nMsg3", joined);
    }

    @Test
    void testSchedulerConfiguration() {
        java.util.concurrent.ScheduledExecutorService scheduler = 
            java.util.concurrent.Executors.newScheduledThreadPool(2);
        assertNotNull(scheduler);
        assertFalse(scheduler.isShutdown());
        scheduler.shutdown();
    }

    @Test
    void testPollingValidation() {
        String tournamentName = "ValidTournament";
        boolean shouldPoll = tournamentName != null && !tournamentName.isBlank();
        assertTrue(shouldPoll);
    }

    @Test
    void testPollingValidation_Null() {
        String tournamentName = null;
        boolean shouldPoll = tournamentName != null && !tournamentName.isBlank();
        assertFalse(shouldPoll);
    }

    @Test
    void testViewTransitionalModelSetGetBaseUrl() {
        model.setBaseUrl("http://10.0.0.1:9090");
        assertEquals("http://10.0.0.1:9090", model.getBaseUrl());
    }

    @Test
    void testViewTransitionalModelGetBaseUrlDefaultWhenNull() {
        model.setBaseUrl(null);
        assertEquals("http://127.0.0.1:8080", model.getBaseUrl());
    }

    @Test
    void testViewTransitionalModelGetBaseUrlDefaultWhenBlank() {
        model.setBaseUrl("  ");
        assertEquals("http://127.0.0.1:8080", model.getBaseUrl());
    }

    @Test
    void testViewTransitionalModelSetGetServerIp() {
        model.setServerIp("192.168.0.1");
        assertEquals("192.168.0.1", model.getServerIp());
    }

    @Test
    void testViewTransitionalModelSetGetServerPort() {
        model.setServerPort("9999");
        assertEquals("9999", model.getServerPort());
    }

    @Test
    void testViewTransitionalModelServerIpPropertyNotNull() {
        assertNotNull(model.serverIpProperty());
    }

    @Test
    void testViewTransitionalModelServerPortPropertyNotNull() {
        assertNotNull(model.serverPortProperty());
    }

    @Test
    void testStartPollingFiresAndHandlesFailure(FxRobot robot) throws Exception {
        model.setBaseUrl("http://127.0.0.1:1");
        try {
            robot.interact(() -> controller.setTournamentName("PollTest"));
            Thread.sleep(800);
            WaitForAsyncUtils.waitForFxEvents();
            String msg = robot.lookup("#messagesLabel").queryAs(Label.class).getText();
            assertTrue(msg.startsWith("Failed to fetch observer messages:"));
        } finally {
            model.setBaseUrl(null);
        }
    }

    @Test
    void testStartPollingWithLiveServer(FxRobot robot) throws Exception {
        org.springframework.context.ConfigurableApplicationContext ctx =
            org.springframework.boot.SpringApplication.run(servers.ServerApplicationApp.class,
                "--server.port=0", "--spring.main.lazy-initialization=true");
        int port = ctx.getEnvironment().getProperty("local.server.port", Integer.class);
        try {
            model.setBaseUrl("http://localhost:" + port);
            robot.interact(() -> controller.setTournamentName("AlmostFull"));
            Thread.sleep(800);
            WaitForAsyncUtils.waitForFxEvents();
            String msg = robot.lookup("#messagesLabel").queryAs(Label.class).getText();
            assertNotNull(msg);
            assertFalse(msg.startsWith("Failed to fetch observer messages:"));
        } finally {
            ctx.close();
            model.setBaseUrl(null);
        }
    }
}