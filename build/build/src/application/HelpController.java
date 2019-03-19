package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

public class HelpController extends PopupController implements Initializable {
	
	@FXML private ImageView topToolbarIV;
	@FXML private ImageView bottomToolbarIV;
	@FXML private ImageView brushToolIV;
	@FXML private ImageView textToolIV;
	@FXML private ImageView selectToolIV;
	
	@FXML private Button closeButton;
	
	
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		Image topToolbarIMG = new Image(getClass().getResourceAsStream("/resources/topToolbar.PNG"));
		Image bottomToolbarIMG = new Image(getClass().getResourceAsStream("/resources/bottomToolbar.PNG"));
		
		Image brushToolIMG = new Image(getClass().getResourceAsStream("/resources/brushTool.PNG"));
		Image textToolIMG = new Image(getClass().getResourceAsStream("/resources/textTool.PNG"));
		Image selectToolIMG = new Image(getClass().getResourceAsStream("/resources/selectTool.PNG"));
		
		
		topToolbarIV.setImage(topToolbarIMG);
		bottomToolbarIV.setImage(bottomToolbarIMG);
		brushToolIV.setImage(brushToolIMG);
		textToolIV.setImage(textToolIMG);
		selectToolIV.setImage(selectToolIMG);
		
		
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
