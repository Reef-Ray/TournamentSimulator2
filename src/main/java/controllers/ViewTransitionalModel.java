package controllers;

import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewTransitionalModel implements ViewTransitionModelInterface {

	private static ViewTransitionalModel instance;
	private Stage primaryStage;
	private String baseUrl;
	private final StringProperty serverIp = new SimpleStringProperty("");
	private final StringProperty serverPort = new SimpleStringProperty("");

	private ViewTransitionalModel() {}

	public static synchronized ViewTransitionalModel getInstance() {
		if (instance == null) instance = new ViewTransitionalModel();
		return instance;
	}

	public void setPrimaryStage(Stage stage) {
		this.primaryStage = stage;
	}

	public void setBaseUrl(String url) {
		this.baseUrl = url;
	}

	public String getBaseUrl() {
		return baseUrl == null || baseUrl.isBlank() ? "http://127.0.0.1:8080" : baseUrl;
	}

	public StringProperty serverIpProperty() {
		return serverIp;
	}

	public String getServerIp() {
		return serverIp.get();
	}

	public void setServerIp(String ip) {
		serverIp.set(ip);
	}

	public StringProperty serverPortProperty() {
		return serverPort;
	}

	public String getServerPort() {
		return serverPort.get();
	}

	public void setServerPort(String port) {
		serverPort.set(port);
	}

	@Override
	public void showTournamentList() {
		if (primaryStage == null) return;
		try {
			FXMLLoader loader = new FXMLLoader(ViewTransitionalModel.class.getResource("/views/TournamentsListView.fxml"));
			Parent root = loader.load();
			primaryStage.setScene(new Scene(root));
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void showSpectateView(String tournamentName) {
		if (primaryStage == null) return;
		try {
			FXMLLoader loader = new FXMLLoader(ViewTransitionalModel.class.getResource("/views/SpectateTournamentView.fxml"));
			Parent root = loader.load();
			Object ctrl = loader.getController();
			if (ctrl instanceof SpectateTournamentViewController) {
				((SpectateTournamentViewController) ctrl).setTournamentName(tournamentName);
			}
			primaryStage.setScene(new Scene(root));
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void showRegistrationView(String tournamentName) {
		if (primaryStage == null) return;
		try {
			FXMLLoader loader = new FXMLLoader(ViewTransitionalModel.class.getResource("/views/registration.fxml"));
			Parent root = loader.load();
			Object ctrl = loader.getController();
			if (ctrl instanceof RegistrationViewController) {
				((RegistrationViewController) ctrl).setTournamentName(tournamentName);
			}
			primaryStage.setScene(new Scene(root));
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}