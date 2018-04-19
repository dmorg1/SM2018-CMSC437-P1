package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	
	private Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;
			
			primaryStage.setTitle("CMSC 437 Project");
			
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("Main.fxml"));
	        Controller mainController = new Controller();
	        mainController.setMainApp(this);
	        loader.setController(mainController);
	        Parent root = loader.load();
	        
			Scene scene = new Scene(root);
            primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public Stage getPrimaryStage(){
		return primaryStage;
	}
}
