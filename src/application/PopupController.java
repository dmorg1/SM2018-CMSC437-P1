package application;

import javafx.stage.Stage;
import javafx.util.Pair;

public abstract class PopupController extends AbstractController {
	protected Stage stage;
	
	public abstract Pair<Double, Double> getResult();
	
	public void setStage(Stage stage){
		this.stage = stage;
	}
}
