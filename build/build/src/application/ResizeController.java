package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.util.Pair;

public class ResizeController extends PopupController implements Initializable {
	
	@FXML private TextField widthTF;
	@FXML private TextField heightTF;
	
	@FXML private Button resizeButton;
	@FXML private Button cancelButton;
	
	
	private Pair<Double, Double> result;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		widthTF.textProperty().addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(!newValue.matches("\\d*")){
					widthTF.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});
		
		
		heightTF.textProperty().addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(!newValue.matches("\\d*")){
					heightTF.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});
		
		
		resizeButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				result = new Pair<Double, Double>(new Double(Double.parseDouble(widthTF.getText())), 
						new Double(Double.parseDouble(heightTF.getText())));
				closeStage();
			}
		});
		
		
		cancelButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				result = null;
				closeStage();
			}
		});
		
	}
	
	@Override
	public Pair<Double, Double> getResult(){
		return this.result;
	}
	
	private void closeStage(){
		if(stage != null){
			stage.close();
		}
	}

}
