package controllers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewTransitionalModel implements ViewTransitionModelInterface {

	private static ViewTransitionalModel instance;
	private Stage primaryStage;

	private ViewTransitionalModel() {}

	public static synchronized ViewTransitionalModel getInstance() {
		if (instance == null) instance = new ViewTransitionalModel();
		return instance;
	}

	public void setPrimaryStage(Stage stage) {
		this.primaryStage = stage;
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