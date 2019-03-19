package application;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

public class Popup {
	
	public static Pair<Double, Double> showPopup(Stage primaryStage, PopupType ptype){
		
		FXMLLoader loader = new FXMLLoader(ptype.getFXMLDocumentLocation());
		PopupController controller = ptype.getAssociatedController();
		loader.setController(controller);
		Parent root;
		
		try {
			root = loader.load();
			Scene scene = new Scene(root);
			Stage popupStage = new Stage();
			
			controller.setStage(popupStage);
			
			popupStage.setTitle(ptype.getTitle());
			popupStage.initOwner(primaryStage);
			popupStage.initModality(Modality.APPLICATION_MODAL);
			popupStage.setScene(scene);
			popupStage.setResizable(false);
			popupStage.showAndWait();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return controller.getResult();
	}
	
	public enum PopupType{
		RESIZE("ResizeScene.fxml", new ResizeController(), "Resize"),
		ABOUT("AboutScene.fxml", new AboutController(), "About"),
		HELP("HelpScene.fxml", new HelpController(), "Help");
		
		private URL location;
		private PopupController controller;
		private String title;
		private PopupType(String docName, PopupController controller, String title){
			location = getClass().getResource(docName);
			this.controller = controller;
			this.title = title;
		}
		
		public URL getFXMLDocumentLocation(){
			return location;
		}
		
		public PopupController getAssociatedController(){
			return controller;
		}
		
		public String getTitle(){
			return title;
		}
	}

}
