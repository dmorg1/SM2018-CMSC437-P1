package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;

public class AboutController extends PopupController implements Initializable {
	
	@FXML private VBox textVB;
	
	@FXML private Button closeButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		Text title = new Text("About");
		title.setFont(Font.font("Arial", 20));
		
		Text author = new Text("Author: David Morgan");
		
		
		Text version = new Text("Version: 1.0.2");
		
		
		Text date = new Text("Version Date: 2017 July 3");
		
		
		Text body = new Text("This is a basic paint application developed\n"
				+ "for the CMSC 437 GUI Programming class.\n"
				+ "See the Get Help menu or press F1 to get\n"
				+ "more information on the functionality of\n"
				+ "the application.");
		
		
		textVB.getChildren().addAll(title, new Separator(), author, version, date, body);
		
		closeButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				closeStage();
			}
		});
	}

	@Override
	public Pair<Double, Double> getResult() {
		return null;
	}
	
	private void closeStage(){
		if(stage != null){
			stage.close();
		}
	}

}
