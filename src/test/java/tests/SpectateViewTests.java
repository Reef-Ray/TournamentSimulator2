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
}