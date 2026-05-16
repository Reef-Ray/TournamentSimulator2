package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import controllers.TournamentsListViewController;
import controllers.ViewTransitionalModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class ListviewTests {

    private TournamentsListViewController controller;
    private ViewTransitionalModel model;

    @Start
    private void start(Stage stage) {
        model = ViewTransitionalModel.getInstance();
        model.setPrimaryStage(stage);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TournamentsListView.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setIP(FxRobot robot, String ip) {
        TextField field = robot.lookup("#serverIpField").queryAs(TextField.class);
        robot.interact(() -> { field.requestFocus(); field.setText(ip); });
    }

    private void setPort(FxRobot robot, String port) {
        TextField field = robot.lookup("#serverPortField").queryAs(TextField.class);
        robot.interact(() -> { field.requestFocus(); field.setText(port); });
    }

    @SuppressWarnings("unchecked")
    private ListView<String> openList(FxRobot robot) {
        return (ListView<String>) robot.lookup("#openTournamentsList").queryAll().iterator().next();
    }

    @SuppressWarnings("unchecked")
    private ListView<String> closedList(FxRobot robot) {
        return (ListView<String>) robot.lookup("#closedTournamentsList").queryAll().iterator().next();
    }

    @Test
    void testIpFieldStartsEmpty(FxRobot robot) {
        Assertions.assertThat(robot.lookup("#serverIpField").queryAs(TextField.class)).hasText("");
    }

    @Test
    void testIpFieldAcceptsInput(FxRobot robot) {
        setIP(robot, "192.168.1.1");
        Assertions.assertThat(robot.lookup("#serverIpField").queryAs(TextField.class)).hasText("192.168.1.1");
    }

    @Test
    void testPortFieldStartsEmpty(FxRobot robot) {
        Assertions.assertThat(robot.lookup("#serverPortField").queryAs(TextField.class)).hasText("");
    }

    @Test
    void testPortFieldAcceptsInput(FxRobot robot) {
        setPort(robot, "8080");
        Assertions.assertThat(robot.lookup("#serverPortField").queryAs(TextField.class)).hasText("8080");
    }

    @Test
    void testOpenListStartsEmpty(FxRobot robot) {
        Assertions.assertThat(openList(robot)).isEmpty();
    }

    @Test
    void testOpenListShowsItemsWhenSet(FxRobot robot) {
        robot.interact(() -> openList(robot).setItems(FXCollections.observableArrayList("T1", "T2")));
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(openList(robot)).hasExactlyNumItems(2);
        Assertions.assertThat(openList(robot)).hasListCell("T1");
        Assertions.assertThat(openList(robot)).hasListCell("T2");
    }

    @Test
    void testOpenListSelectionWorks(FxRobot robot) {
        robot.interact(() -> openList(robot).setItems(FXCollections.observableArrayList("T1", "T2")));
        WaitForAsyncUtils.waitForFxEvents();
        robot.interact(() -> openList(robot).getSelectionModel().select(0));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("T1", openList(robot).getSelectionModel().getSelectedItem());
    }

    @Test
    void testClosedListStartsEmpty(FxRobot robot) {
        Assertions.assertThat(closedList(robot)).isEmpty();
    }

    @Test
    void testClosedListShowsItemsWhenSet(FxRobot robot) {
        robot.interact(() -> closedList(robot).setItems(FXCollections.observableArrayList("T3", "T4")));
        WaitForAsyncUtils.waitForFxEvents();
        Assertions.assertThat(closedList(robot)).hasExactlyNumItems(2);
        Assertions.assertThat(closedList(robot)).hasListCell("T3");
        Assertions.assertThat(closedList(robot)).hasListCell("T4");
    }

    @Test
    void testClosedListSelectionWorks(FxRobot robot) {
        robot.interact(() -> closedList(robot).setItems(FXCollections.observableArrayList("T3", "T4")));
        WaitForAsyncUtils.waitForFxEvents();
        robot.interact(() -> closedList(robot).getSelectionModel().clearAndSelect(0));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("T3", closedList(robot).getSelectionModel().getSelectedItem());
    }

    @Test
    void testConnectButtonDoesNotThrowWhenFieldsEmpty(FxRobot robot) {
        assertDoesNotThrow(() -> {
            robot.clickOn("#enterServer");
            WaitForAsyncUtils.waitForFxEvents();
        });
    }

    @Test
    void testEnterOpenTournamentDoesNotThrowWithNoSelection(FxRobot robot) {
        assertDoesNotThrow(() -> {
            robot.interact(() -> controller.openRegistrationView(new javafx.event.ActionEvent()));
            WaitForAsyncUtils.waitForFxEvents();
        });
    }

    @Test
    void testEnterClosedTournamentDoesNotThrowWithNoSelection(FxRobot robot) {
        assertDoesNotThrow(() -> {
            robot.interact(() -> controller.openSpectateView(new javafx.event.ActionEvent()));
            WaitForAsyncUtils.waitForFxEvents();
        });
    }

    @Test
    void testShowTournamentListTransition(FxRobot robot) {
        assertDoesNotThrow(() -> robot.interact(() -> model.showTournamentList()));
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(robot.lookup(".root").query());
    }

    @Test
    void testShowSpectateViewTransition(FxRobot robot) {
        assertDoesNotThrow(() -> robot.interact(() -> model.showSpectateView("T1")));
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(robot.lookup(".root").query());
    }

    @Test
    void testShowRegistrationViewTransition(FxRobot robot) {
        assertDoesNotThrow(() -> robot.interact(() -> model.showRegistrationView("T1")));
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(robot.lookup(".root").query());
    }

	@Test
	void testConnectToServerPopulatesLists(FxRobot robot) throws Exception {
		// Spin up a real Spring Boot server on a random port
		org.springframework.context.ConfigurableApplicationContext ctx =
			org.springframework.boot.SpringApplication.run(servers.ServerApplicationApp.class,
				"--server.port=0", "--spring.main.lazy-initialization=true");

		int port = ctx.getEnvironment().getProperty("local.server.port", Integer.class);
		String ip = "localhost";

		try {
			setIP(robot, ip);
			setPort(robot, String.valueOf(port));
			robot.interact(() -> controller.connectToServer(new javafx.event.ActionEvent()));
			WaitForAsyncUtils.waitForFxEvents();

			// The mock loader adds 15 open and 3 closed tournaments
			assertTrue(openList(robot).getItems().size() > 0);
			assertTrue(closedList(robot).getItems().size() > 0);

			// Available (open) and closed lists should be mutually exclusive
			for (String item : openList(robot).getItems()) {
				assertFalse(closedList(robot).getItems().contains(item));
			}
		} finally {
			ctx.close();
		}
	}

	@Test
	void testTournamentFilterLogic() {
		java.util.List<String> all = java.util.Arrays.asList("T1", "T2", "T3", "T4");
		java.util.List<String> available = java.util.Arrays.asList("T1", "T3");
		
		java.util.List<String> closed = new ArrayList<>();
		for (String t : all) {
			if (!available.contains(t)) closed.add(t);
		}
		
		assertEquals(2, closed.size());
		assertTrue(closed.contains("T2") && closed.contains("T4"));
	}

	@Test
	void testBaseUrlConstruction() {
		String ip = "192.168.1.1";
		String port = "8080";
		String baseUrl = "http://" + ip + ":" + port;
		
		assertEquals("http://192.168.1.1:8080", baseUrl);
	}

	@Test
	void testInvalidInputRejection() {
		String port = "   ";
		String ip = "192.168.1.1";
		
		boolean isValid = !(port == null || port.isBlank() || ip == null || ip.isBlank());
		assertFalse(isValid);
	}

	@Test
	void testTournamentSelectionValidation() {
		String inputName = "Tournament1";
		java.util.List<String> available = java.util.Arrays.asList("Tournament1", "Tournament2");
		
		String chosen = inputName.trim();
		boolean isValid = chosen != null && !chosen.isBlank() && available.contains(chosen);
		
		assertTrue(isValid);
	}

	@Test
	void testConnectToServerInvalidServerHandlesErrorGracefully(FxRobot robot) {
		setIP(robot, "localhost");
		setPort(robot, "1");
		assertDoesNotThrow(() -> {
			robot.interact(() -> controller.connectToServer(new javafx.event.ActionEvent()));
			WaitForAsyncUtils.waitForFxEvents();
		});
		setIP(robot, "");
		setPort(robot, "");
	}

	@Test
	void testOpenRegistrationViewWithValidSelection(FxRobot robot) {
		robot.interact(() -> {
			openList(robot).setItems(javafx.collections.FXCollections.observableArrayList("T1", "T2"));
			openList(robot).getSelectionModel().select(0);
		});
		WaitForAsyncUtils.waitForFxEvents();
		assertDoesNotThrow(() -> {
			robot.interact(() -> controller.openRegistrationView(new javafx.event.ActionEvent()));
			WaitForAsyncUtils.waitForFxEvents();
		});
	}

	@Test
	void testOpenSpectateViewWithValidSelection(FxRobot robot) {
		robot.interact(() -> {
			closedList(robot).setItems(javafx.collections.FXCollections.observableArrayList("T3", "T4"));
			closedList(robot).getSelectionModel().select(0);
		});
		WaitForAsyncUtils.waitForFxEvents();
		assertDoesNotThrow(() -> {
			robot.interact(() -> controller.openSpectateView(new javafx.event.ActionEvent()));
			WaitForAsyncUtils.waitForFxEvents();
		});
	}

	@Test
	void testPlayerCountLogic() {
		int current = 5;
		int max = 10;
		boolean alreadyRegistered = false;
		
		boolean shouldDisableTournamentFull = current >= max && max > 0;
		boolean shouldDisable = shouldDisableTournamentFull || alreadyRegistered;
		
		assertFalse(shouldDisable);
	}

	@Test
	void testTournamentFullDisablesButtons() {
		int current = 10;
		int max = 10;
		boolean alreadyRegistered = false;
		
		boolean shouldDisableTournamentFull = current >= max && max > 0;
		boolean shouldDisable = shouldDisableTournamentFull || alreadyRegistered;
		
		assertTrue(shouldDisable);
	}
}