package tests;

import controllers.RegistrationViewController;
import controllers.ViewTransitionalModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class RegistrationViewTests {

    private RegistrationViewController controller;
    private ViewTransitionalModel model;

    @Start
    private void start(Stage stage) {
        model = ViewTransitionalModel.getInstance();
        model.setPrimaryStage(stage);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/registration.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSetTournamentNameSetsLabel(FxRobot robot) {
        robot.interact(() -> controller.setTournamentName("Finals"));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Finals", robot.lookup("#tournamentNameLabel").queryAs(Label.class).getText());
    }

    @Test
    void testSetTournamentNameNullUsesDefault(FxRobot robot) {
        robot.interact(() -> controller.setTournamentName(null));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("<Unnamed Tournament>", robot.lookup("#tournamentNameLabel").queryAs(Label.class).getText());
    }

    @Test
    void testSetTournamentNameBlankUsesDefault(FxRobot robot) {
        robot.interact(() -> controller.setTournamentName("   "));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("<Unnamed Tournament>", robot.lookup("#tournamentNameLabel").queryAs(Label.class).getText());
    }

    @Test
    void testRegisterHumanDoesNotThrow(FxRobot robot) {
        robot.interact(() -> controller.setTournamentName("Test"));
        WaitForAsyncUtils.waitForFxEvents();
        assertDoesNotThrow(() -> {
            robot.clickOn("#humanBotButton");
            WaitForAsyncUtils.waitForFxEvents();
        });
    }

    @Test
    void testRegisterRemoteBlankPlayerNameDoesNotThrow(FxRobot robot) {
        robot.interact(() -> controller.setTournamentName("Test"));
        WaitForAsyncUtils.waitForFxEvents();
        assertDoesNotThrow(() -> {
            robot.clickOn("#remoteBotButton");
            WaitForAsyncUtils.waitForFxEvents();
        });
    }

    @Test
    void testRegisterRemoteBlankIpDoesNotThrow(FxRobot robot) {
        robot.interact(() -> controller.setTournamentName("Test"));
        WaitForAsyncUtils.waitForFxEvents();
        robot.interact(() -> robot.lookup("#playerNameField").queryAs(TextField.class).setText("Player1"));
        assertDoesNotThrow(() -> {
            robot.clickOn("#remoteBotButton");
            WaitForAsyncUtils.waitForFxEvents();
        });
    }

    @Test
    void testRegisterRemoteBlankPortDoesNotThrow(FxRobot robot) {
        robot.interact(() -> controller.setTournamentName("Test"));
        WaitForAsyncUtils.waitForFxEvents();
        robot.interact(() -> {
            robot.lookup("#playerNameField").queryAs(TextField.class).setText("Player1");
            robot.lookup("#ipField").queryAs(TextField.class).setText("127.0.0.1");
        });
        assertDoesNotThrow(() -> {
            robot.clickOn("#remoteBotButton");
            WaitForAsyncUtils.waitForFxEvents();
        });
    }

    @Test
    void testGoBackDoesNotThrow(FxRobot robot) {
        robot.interact(() -> controller.setTournamentName("Test"));
        WaitForAsyncUtils.waitForFxEvents();
        assertDoesNotThrow(() -> {
            robot.clickOn("#backButton");
            WaitForAsyncUtils.waitForFxEvents();
        });
    }

    @Test
    void testRefreshCountsWithLiveServer(FxRobot robot) throws Exception {
        org.springframework.context.ConfigurableApplicationContext ctx =
            org.springframework.boot.SpringApplication.run(servers.ServerApplicationApp.class,
                "--server.port=0", "--spring.main.lazy-initialization=true");
        int port = ctx.getEnvironment().getProperty("local.server.port", Integer.class);
        try {
            model.setBaseUrl("http://localhost:" + port);
            robot.interact(() -> controller.setTournamentName("AlmostFull"));
            Thread.sleep(300);
            WaitForAsyncUtils.waitForFxEvents();
            assertEquals("3", robot.lookup("#currentPlayerCount").queryAs(Label.class).getText());
            assertEquals("4", robot.lookup("#maxPlayerCount").queryAs(Label.class).getText());
        } finally {
            ctx.close();
            model.setBaseUrl(null);
        }
    }

    @Test
    void testRegisterRemoteNonFullTournamentWithLiveServer(FxRobot robot) throws Exception {
        org.springframework.context.ConfigurableApplicationContext ctx =
            org.springframework.boot.SpringApplication.run(servers.ServerApplicationApp.class,
                "--server.port=0", "--spring.main.lazy-initialization=true");
        int port = ctx.getEnvironment().getProperty("local.server.port", Integer.class);
        try {
            model.setBaseUrl("http://localhost:" + port);
            robot.interact(() -> controller.setTournamentName("OpenEmpty1"));
            WaitForAsyncUtils.waitForFxEvents();
            robot.interact(() -> {
                robot.lookup("#playerNameField").queryAs(TextField.class).setText("TestPlayer");
                robot.lookup("#ipField").queryAs(TextField.class).setText("127.0.0.1");
                robot.lookup("#portField").queryAs(TextField.class).setText("9999");
            });
            assertDoesNotThrow(() -> {
                robot.clickOn("#remoteBotButton");
                WaitForAsyncUtils.waitForFxEvents();
            });
        } finally {
            ctx.close();
            model.setBaseUrl(null);
        }
    }

    @Test
    void testRegisterRemoteFillingTournamentWithLiveServer(FxRobot robot) throws Exception {
        org.springframework.context.ConfigurableApplicationContext ctx =
            org.springframework.boot.SpringApplication.run(servers.ServerApplicationApp.class,
                "--server.port=0", "--spring.main.lazy-initialization=true");
        int port = ctx.getEnvironment().getProperty("local.server.port", Integer.class);
        try {
            model.setBaseUrl("http://localhost:" + port);
            robot.interact(() -> controller.setTournamentName("AlmostFull"));
            Thread.sleep(300);
            WaitForAsyncUtils.waitForFxEvents();
            robot.interact(() -> {
                robot.lookup("#playerNameField").queryAs(TextField.class).setText("Player4");
                robot.lookup("#ipField").queryAs(TextField.class).setText("127.0.0.1");
                robot.lookup("#portField").queryAs(TextField.class).setText("9999");
            });
            assertDoesNotThrow(() -> {
                robot.interact(() -> controller.registerRemote(new javafx.event.ActionEvent()));
                Thread.sleep(500);
                WaitForAsyncUtils.waitForFxEvents();
            });
        } finally {
            ctx.close();
            model.setBaseUrl(null);
        }
    }
}
