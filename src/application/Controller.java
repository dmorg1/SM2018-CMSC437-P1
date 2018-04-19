package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.OverlaySelection.SelectionBoundsState;
import application.Popup.PopupType;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

public class Controller extends AbstractController implements Initializable {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;
    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    
    
    // ----------------------------------------------------------------------------- Primary Stage FXML Variables
    // ---------------------------------------------------------- Canvas
    @FXML private Canvas canvas;
    @FXML private Canvas overlayCS;
    
    // ---------------------------------------------------------- Pane
    @FXML private AnchorPane root;
    @FXML private ScrollPane scrollPane;
    @FXML private Pane csPane;
    
    // ---------------------------------------------------------- Label
    @FXML private Label fileNameLabel;
    @FXML private Label zoomLvlLabel;
    @FXML private Label dimLabel;
    
    // ---------------------------------------------------------- Slider
    @FXML private Slider zoomSlider;
    
    // ---------------------------------------------------------- ColorPicker
    @FXML private ColorPicker colorPicker;
    
    // ---------------------------------------------------------- ComboBox
    @FXML private ComboBox<KeyValuePair> lineWidthCB;
    @FXML private ComboBox<KeyValuePair> toolCB;
    @FXML private ComboBox<KeyValuePair> fontCB;
    @FXML private ComboBox<KeyValuePair> fontSizeCB;
    
    // ---------------------------------------------------------- ToggleButton
    @FXML private ToggleButton boldTB;
    @FXML private ToggleButton italicTB;
    
    // ---------------------------------------------------------- MenuItem
    @FXML private MenuItem newMI; // File Menu
    @FXML private MenuItem openMI;
    @FXML private MenuItem saveMI;
    @FXML private MenuItem saveAsMI;
    @FXML private MenuItem exitMI;
    
    @FXML private MenuItem cutMI; // Edit Menu
    @FXML private MenuItem copyMI;
    @FXML private MenuItem pasteMI;
    @FXML private MenuItem deleteMI;
    @FXML private MenuItem clearScreenMI;
    @FXML private MenuItem selectAllMI;
    @FXML private MenuItem deselectAllMI;
    
    @FXML private MenuItem resizeMI; // View Menu
    @FXML private MenuItem zoomInMI;
    @FXML private MenuItem zoomOutMI;
    @FXML private MenuItem fitInWindowMI;
    
    @FXML private MenuItem getHelpMI; // Help
    @FXML private MenuItem aboutMI;
    
    
    private Stage primaryStage;
    
    private GraphicsContext gc;
    private GraphicsContext overlayGC;
    
    private OverlayTextBox textBox;
    
    double lastX;
    double lastY;
    
    private int lineWidth;
    
    private Color currentColor;
    
    private Tool currentTool;
    private Tool previousTool;
    
    private Font currentFont;
    
    private FontStyle currentFontStyle;
    
    private Timer overlayTextBoxTimer;
    private Timer overlaySelectionTimer;
    private Timer overlayPasteTimer;
    
    private File filePath;
    
    private OverlaySelection selectionTool;
    private OverlaySelection pasteSelection;
    
    

	@Override
    public void initialize(URL location, ResourceBundle resources) {
    	
    	primaryStage = this.main.getPrimaryStage();
    	
    	lineWidth = 2;
    	
    	currentTool = Tool.BRUSH;
    	previousTool = currentTool;
    	
    	currentColor = Color.BLACK;
    	
    	currentFontStyle = new FontStyle(false, false);
    	currentFont = Font.font("System", currentFontStyle.getFontWeight(), currentFontStyle.getFontPosture(), 12);
    	
    	textBox = new OverlayTextBox();
    	
    	filePath = null;
    	
    	selectionTool = new OverlaySelection();
    	pasteSelection = new OverlaySelection();
        
        configureCanvas();
        configureLabels();
        configureComboBoxes();
        configureSlider();
        configureColorPicker();
        configureToggleButtons();
        configureKeyboardHandler();
        configureMenuItems();
        
        overlayTextBoxTimer = new Timer(textBox, 500);
        overlaySelectionTimer = new Timer(selectionTool, 10);
        overlayPasteTimer = new Timer(pasteSelection, 10);
    }

	// ---------------------------------------------------------------------------------------------------------------------- Canvas
	private void configureCanvas() {
		gc = canvas.getGraphicsContext2D();
    	gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.setFont(currentFont);
        
        overlayGC = overlayCS.getGraphicsContext2D();
        overlayGC.setFont(currentFont);
        
        lastX = -1;
        lastY = -1;

        overlayCS.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				
				if(!pasteSelection.isActive()){
					
					// Brush Tool
					if(currentTool == Tool.BRUSH){
						
						gc.fillOval(event.getX() - (lineWidth / 2), event.getY() - (lineWidth / 2), lineWidth, lineWidth);
						
						lastX = event.getX(); 
						lastY = event.getY();
					}
					
					// Text Tool
					else if(currentTool == Tool.TEXT){
						if(!textBox.isActive()){
							overlayTextBoxTimer.setActive();
							textBox.setLocation(event.getX(), event.getY());
							textBox.update(overlayCS, currentColor, currentFont);
						}
						else {
							String s = textBox.getText();
							double x = textBox.getX();
							double y = textBox.getY();
							overlayTextBoxTimer.setInactive();
							
							gc.fillText(s, x + 10, y + currentFont.getSize() * 1.1);
						}
					}
					
					// Select Tool
					else if(currentTool == Tool.SELECT){
						if(!selectionTool.isActive()){
							
							overlaySelectionTimer.setActive();
							
							selectionTool.setOrigin(event.getX(), event.getY());
							selectionTool.setSecondPoint(event.getX(), event.getY());
							selectionTool.update(overlayCS, currentColor, currentFont);
						}
						else if(!selectionTool.containsPoint(event.getX(), event.getY())) {
							selectionTool.placeSelectedImage(canvas, overlayCS);
							overlaySelectionTimer.setInactive();
						}
						else {
							selectionTool.setDraggable(event.getX(), event.getY());
						}
					}
					
					// Color Picker Tool
					else if(currentTool == Tool.COLOR_PICKER){
						WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
						canvas.snapshot(null, wi);
						Color pixelColor = wi.getPixelReader().getColor((int) event.getX(), (int) event.getY());
						colorPicker.setValue(pixelColor);
						
						gc.setFill(colorPicker.getValue());
						gc.setStroke(colorPicker.getValue());
						
						overlayGC.setFill(colorPicker.getValue());
						
						currentColor = colorPicker.getValue();
					}
				}
				
				// Paste Selection Tool
				else {

					if(!pasteSelection.containsPoint(event.getX(), event.getY())){
						pasteSelection.placeSelectedImage(canvas, overlayCS);
						overlayPasteTimer.setInactive();
					}
					else {
						pasteSelection.setDraggable(event.getX(), event.getY());
					}
				}
			}
        });
        overlayCS.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				
				if(!pasteSelection.isActive()){
					
					// Brush Tool
					if(currentTool == Tool.BRUSH){
						if(lastX != -1){
							gc.setLineWidth(lineWidth);
							gc.setLineCap(StrokeLineCap.ROUND);
							gc.strokeLine(lastX, lastY, event.getX(), event.getY());
						}
						
						lastX = event.getX(); 
						lastY = event.getY();
					}
					
					// Select Tool
					if(currentTool == Tool.SELECT){
						if(selectionTool.isActive() && (selectionTool.getState() == SelectionBoundsState.INITIAL || 
								selectionTool.getState() == SelectionBoundsState.DYNAMIC)){
							selectionTool.setState(SelectionBoundsState.DYNAMIC);
							selectionTool.setSecondPoint(event.getX(), event.getY());
						}
						else if(selectionTool.isDraggable()){
							CanvasUtils.handleDragEvent(overlayCS, selectionTool, event.getX(), event.getY());
						}
					}
				}
				
				// Paste Selection Tool
				else if(pasteSelection.isDraggable()){
					CanvasUtils.handleDragEvent(overlayCS, pasteSelection, event.getX(), event.getY());
				}
			}
		});
        overlayCS.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				
				if(!pasteSelection.isActive()){
					
					// Brush Tool
					if(currentTool == Tool.BRUSH){
						if(lastX != -1){
							gc.setLineWidth(lineWidth);
							gc.setLineCap(StrokeLineCap.ROUND);
							gc.strokeLine(lastX, lastY, event.getX(), event.getY());
						}
					}
					
					lastX = -1;
					lastY = -1;
					
					// Select Tool
					if(currentTool == Tool.SELECT){
						if(selectionTool.isActive() && selectionTool.getState() == SelectionBoundsState.DYNAMIC){
							selectionTool.setState(SelectionBoundsState.STATIC);
							selectionTool.setSelectedImage(canvas, overlayCS, currentColor);
						}
						else if(selectionTool.isDraggable()){
							selectionTool.setUndraggable();
						}
					}
				}
				
				// Paste Selection Tool
				else if(pasteSelection.isDraggable()){
					pasteSelection.setUndraggable();
				}
			}
        });
	}
	
	// ---------------------------------------------------------------------------------------------------------------------- Label
	private void configureLabels() {
		fileNameLabel.setText("untitled.png");
		zoomLvlLabel.setText("100%");
		dimLabel.setText("500 x 500 px");
	}
	
	// ---------------------------------------------------------------------------------------------------------------------- ComboBox
	private void configureComboBoxes(){
		
		// Line Width ComboBox
		lineWidthCB.setConverter(new KeyValuePair().getConverter());
		
		lineWidthCB.setItems(FXCollections.observableArrayList(
				new KeyValuePair("2px", 2),
				new KeyValuePair("3px", 3),
				new KeyValuePair("4px", 4),
				new KeyValuePair("5px", 5),
				new KeyValuePair("6px", 6),
				new KeyValuePair("7px", 7),
				new KeyValuePair("8px", 8),
				new KeyValuePair("9px", 9),
				new KeyValuePair("10px", 10),
				new KeyValuePair("12px", 12),
				new KeyValuePair("14px", 14),
				new KeyValuePair("16px", 16),
				new KeyValuePair("18px", 18),
				new KeyValuePair("20px", 20),
				new KeyValuePair("24px", 24),
				new KeyValuePair("28px", 28)));
		
		lineWidthCB.getSelectionModel().select(0);
		
		lineWidthCB.valueProperty().addListener((obs, oldVal, newVal) -> {
			lineWidth = newVal.getValue();
		});
		
		
		// Tool ComboBox
		toolCB.setConverter(new KeyValuePair().getConverter());
		
		toolCB.setItems(FXCollections.observableArrayList(
				new KeyValuePair("Brush",Tool.BRUSH.valueOf()),
				new KeyValuePair("Text", Tool.TEXT.valueOf()),
				new KeyValuePair("Select", Tool.SELECT.valueOf()),
				new KeyValuePair("Color Picker", Tool.COLOR_PICKER.valueOf())));
		
		toolCB.getSelectionModel().select(0);
		
		toolCB.valueProperty().addListener((obs, oldVal, newVal) -> {
			currentTool = Tool.intToTool(newVal.getValue());
			previousTool = Tool.intToTool(oldVal.getValue());
			
			if(textBox.isActive() && currentTool != Tool.TEXT){
				overlayTextBoxTimer.setInactive();
			}
			if(selectionTool.isActive() && currentTool != Tool.SELECT){
				overlaySelectionTimer.setInactive();
			}
		});
		
		// Font ComboBox
		ArrayList<KeyValuePair> fontAL = new ArrayList<>();
		ArrayList<String> fontFamilies = new ArrayList<>(Font.getFamilies());
		
		int systemFamilyIndex = 0;
		
		for(String str : fontFamilies){
			fontAL.add(new KeyValuePair(str, fontAL.size()));
			if(str == "System"){
				systemFamilyIndex = fontAL.size() - 1;
			}
		}
		
		
		ObservableList<KeyValuePair> fontOL = FXCollections.observableArrayList(fontAL);
		
		fontCB.setConverter(new KeyValuePair().getConverter());
		
		fontCB.setItems(fontOL);
		
		fontCB.getSelectionModel().select(systemFamilyIndex);
		
		fontCB.valueProperty().addListener((obs, oldVal, newVal) -> {
			updateFont(newVal.getKey(), currentFont.getSize());
		});
		
		
		// FontSize ComboBox
		fontSizeCB.setConverter(new KeyValuePair().getConverter());
		
		fontSizeCB.setItems(FXCollections.observableArrayList(
				new KeyValuePair("8pt", 8),
				new KeyValuePair("9pt", 9),
				new KeyValuePair("10pt", 10),
				new KeyValuePair("11pt", 11),
				new KeyValuePair("12pt", 12),
				new KeyValuePair("14pt", 14),
				new KeyValuePair("16pt", 16),
				new KeyValuePair("18pt", 18),
				new KeyValuePair("20pt", 20),
				new KeyValuePair("22pt", 22),
				new KeyValuePair("24pt", 24),
				new KeyValuePair("26pt", 26),
				new KeyValuePair("28pt", 28),
				new KeyValuePair("36pt", 36),
				new KeyValuePair("48pt", 48),
				new KeyValuePair("72pt", 72)));
		
		fontSizeCB.getSelectionModel().select(6);
		
		fontSizeCB.valueProperty().addListener((obs, oldVal, newVal) -> {
			updateFont(currentFont.getFamily(), newVal.getValue());
		});
	}
	
	// ---------------------------------------------------------------------------------------------------------------------- Slider
	private void configureSlider(){
		zoomSlider.valueProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				
				double value = getZoomPercent(newValue.intValue());
				
				if(value == 12.5){
					zoomLvlLabel.setText(value + "%");
				}
				else {
					zoomLvlLabel.setText((int) value + "%");
				}
				
				CanvasUtils.scalePerserveOrigin(canvas, overlayCS, csPane, value / 100);
			}
		});
	}
	
	// ---------------------------------------------------------------------------------------------------------------------- ColorPicker
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void configureColorPicker(){
		colorPicker.setValue(Color.BLACK);
		
		colorPicker.setOnAction(new EventHandler(){
			@Override
			public void handle(Event event) {
				gc.setFill(colorPicker.getValue());
				gc.setStroke(colorPicker.getValue());
				
				overlayGC.setFill(colorPicker.getValue());
				
				currentColor = colorPicker.getValue();
			}
		});
	}
	
	// ---------------------------------------------------------------------------------------------------------------------- ToggleButton
	private void configureToggleButtons(){
		ToggleGroup boldTG = new ToggleGroup();
		
		boldTB.setToggleGroup(boldTG);
		
		boldTG.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (newValue == null){
					currentFontStyle.setFontWeight(FontWeight.NORMAL);
					updateFont(currentFont.getFamily(), currentFont.getSize());
				}
				else{
					currentFontStyle.setFontWeight(FontWeight.BOLD);
					updateFont(currentFont.getFamily(), currentFont.getSize());
				}
			}
		});
		
		
		ToggleGroup italicTG = new ToggleGroup();
		
		italicTB.setToggleGroup(italicTG);
		
		italicTG.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (newValue == null){
					currentFontStyle.setFontPosture(FontPosture.REGULAR);
					updateFont(currentFont.getFamily(), currentFont.getSize());
				}
				else{
					currentFontStyle.setFontPosture(FontPosture.ITALIC);
					updateFont(currentFont.getFamily(), currentFont.getSize());
				}
			}
		});
	}
	
	// ---------------------------------------------------------------------------------------------------------------------- Keyboard
	private void configureKeyboardHandler(){
		root.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				
				int ascii = (int) event.getCharacter().charAt(0);
				
				if(currentTool == Tool.TEXT){
					
					// Backspace functionality for the OverlayTextBox
					if(event.getEventType() == KeyEvent.KEY_PRESSED && event.getCode() == KeyCode.BACK_SPACE){
						
						String str = textBox.getText();

						if(str.length() > 0){
							textBox.setText(str.substring(0, str.length() - 1));
							textBox.update(overlayCS, currentColor, currentFont);
						}
					}
					
					// Clears and closes the the OverlayTextBox
					else if(event.getEventType() == KeyEvent.KEY_PRESSED && event.getCode() == KeyCode.ESCAPE){
						overlayTextBoxTimer.setInactive();
					}
					
					// Basic typing functionality for the OverlayTextBox
					else if(ascii >= 32 && ascii <= 126) { // Legal English Characters
						textBox.setText(textBox.getText() + event.getCharacter());
						textBox.update(overlayCS, currentColor, currentFont);
					}
					
					event.consume();
				}
				
				// Secondary Accelerator for Zoom In MenuItem
				if(event.getEventType() == KeyEvent.KEY_PRESSED && event.getCode() == KeyCode.EQUALS && event.isShiftDown()){
					zoomSlider.setValue(zoomSlider.getValue() + 1);
				}
				
				// Secondary Accelerator for Zoom Out MenuItem
				if(event.getEventType() == KeyEvent.KEY_PRESSED && event.getCode() == KeyCode.MINUS){
					zoomSlider.setValue(zoomSlider.getValue() - 1);
				}
			}
		});
	}
	
	// ---------------------------------------------------------------------------------------------------------------------- MenuItem
	private void configureMenuItems(){
		newMI.setText("New\t\t\t");
		cutMI.setText("Cut\t\t\t");
		resizeMI.setText("Resize\t\t");
		getHelpMI.setText("Get Help\t\t");
		
		
		newMI.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
		newMI.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				filePath = null;
				saveMI.setDisable(true);
				fileNameLabel.setText("untitled.png");
				
				CanvasUtils.clearAll(canvas, currentColor);
				canvas.setHeight(500);
				canvas.setWidth(500);
				overlayCS.setHeight(500);
				overlayCS.setWidth(500);
			}
		});
		
		
		openMI.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
		openMI.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files", "*.png", "*.gif", "*.jpg");
				fileChooser.getExtensionFilters().add(extFilter);
				
				filePath = fileChooser.showOpenDialog(primaryStage);
				
				CanvasUtils.openFromImageFile(canvas, overlayCS, filePath);
				
				if(filePath != null){
					saveMI.setDisable(false);
					fileNameLabel.setText(filePath.getName());
					dimLabel.setText((int) canvas.getWidth() + " x " + (int) canvas.getHeight() + " px");
				}
			}
		});
		
		
		saveMI.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
		saveMI.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				CanvasUtils.saveToImageFile(canvas, filePath);
			}
		});
		
		
		saveAsMI.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				
				FileChooser fileChooser = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
				fileChooser.getExtensionFilters().add(extFilter);
				
				filePath = fileChooser.showSaveDialog(primaryStage);
				
				CanvasUtils.saveToImageFile(canvas, filePath);
				
				if(filePath != null){
					saveMI.setDisable(false);
					fileNameLabel.setText(filePath.getName());
				}
			}
		});
		
		
		exitMI.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				// TODO: After adding in saving feature, check to make sure the user's work is saved
				// 		 before they exit. Otherwise, prompt them to save or quit.
				System.exit(0);
			}
		});
		
		
		cutMI.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
		cutMI.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				
				if(selectionTool.getState() == SelectionBoundsState.STATIC && selectionTool.isActive()){
					selectionTool.setClipboard();
					overlaySelectionTimer.setInactive();
				}
			}
		});
		
		
		copyMI.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
		copyMI.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				
				if(selectionTool.getState() == SelectionBoundsState.STATIC && selectionTool.isActive()){
					selectionTool.setClipboard();
				}
			}
		});
		
		
		pasteMI.setAccelerator(KeyCombination.keyCombination("Ctrl+V"));
		pasteMI.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				
				if(selectionTool.isActive()){
					selectionTool.placeSelectedImage(canvas, overlayCS);
					overlaySelectionTimer.setInactive();
				}
				
				if(pasteSelection.isActive()){
					pasteSelection.placeSelectedImage(canvas, overlayCS);
					overlayPasteTimer.setInactive();
				}
				
				pasteSelection = CanvasUtils.paste(canvas);
				overlayPasteTimer = new Timer(pasteSelection, 10);
				overlayPasteTimer.setActive();
			}
		});
		
		
		deleteMI.setAccelerator(KeyCombination.keyCombination("Delete"));
		deleteMI.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				
				if(selectionTool.getState() == SelectionBoundsState.STATIC && selectionTool.isActive()){
					overlaySelectionTimer.setInactive();
				}
			}
		});
		
		
		clearScreenMI.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				CanvasUtils.clearAll(canvas, currentColor);
			}
		});
		
		
		selectAllMI.setAccelerator(KeyCombination.keyCombination("Ctrl+A"));
		selectAllMI.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				
				if(selectionTool.isActive()){
					selectionTool.placeSelectedImage(canvas, overlayCS);
					overlaySelectionTimer.setInactive();
				}
				
				if(pasteSelection.isActive()){
					pasteSelection.placeSelectedImage(canvas, overlayCS);
					overlayPasteTimer.setInactive();
				}
				
				previousTool = currentTool;
				currentTool = Tool.SELECT;
				toolCB.getSelectionModel().select(currentTool.valueOf());
				
				overlaySelectionTimer.setActive();
				
				selectionTool.setOrigin(0, 0);
				selectionTool.setSecondPoint(canvas.getWidth(), canvas.getHeight());
				selectionTool.setState(SelectionBoundsState.STATIC);
				
				selectionTool.update(overlayCS, currentColor, currentFont);
				
				selectionTool.setSelectedImage(canvas, overlayCS, currentColor);
			}
		});
		
		
		deselectAllMI.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				
				if(selectionTool.isActive()){
					selectionTool.placeSelectedImage(canvas, overlayCS);
					overlaySelectionTimer.setInactive();
				}
				
				if(pasteSelection.isActive()){
					pasteSelection.placeSelectedImage(canvas, overlayCS);
					overlayPasteTimer.setInactive();
				}
				
				currentTool = previousTool;
				toolCB.getSelectionModel().select(currentTool.valueOf());
				overlaySelectionTimer.setInactive();
			}
		});
		
		
		resizeMI.setAccelerator(KeyCombination.keyCombination("Ctrl+W"));
		resizeMI.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				Pair<Double, Double> dim = Popup.showPopup(primaryStage, PopupType.RESIZE);
				
				if(dim != null){
					
					double width = dim.getKey();
					double height = dim.getValue();
					
					// A ternary operator within a ternary operator... I wrote this code at 5 am...
					width = !(width > 0 && width < 5000) && width > 5000 ? 5000 : (width <= 0 ? 1 : width);
					height = !(height > 0 && height < 5000) && height > 5000 ? 5000 : (height <= 0 ? 1 : height);
					
					dimLabel.setText((int) width + " x " + (int) height + " px");
					
					WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
					
					canvas.snapshot(null, wi);
					
					zoomSlider.setValue(0);
					
					canvas.setWidth(width);
					canvas.setHeight(height);
					overlayCS.setWidth(width);
					overlayCS.setHeight(height);
					
					CanvasUtils.clearAll(canvas, currentColor);
					
					gc.drawImage(wi, 0, 0);
					
					csPane.setPrefSize(canvas.getWidth() * canvas.getScaleX(), canvas.getHeight() * canvas.getScaleY());
				}
			}
		});
		
		
		zoomInMI.setAccelerator(KeyCombination.keyCombination("Add"));
		zoomInMI.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				zoomSlider.setValue(zoomSlider.getValue() + 1);
			}
		});
		
		
		zoomOutMI.setAccelerator(KeyCombination.keyCombination("Subtract"));
		zoomOutMI.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				zoomSlider.setValue(zoomSlider.getValue() - 1);
			}
		});
		
		
		fitInWindowMI.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				
				zoomSlider.setValue(0);
				
				double availableWidth = scrollPane.getWidth() - 2;
				double availableHeight = scrollPane.getHeight() - 2;
				
				double widthDiff = availableWidth - (canvas.getWidth() * canvas.getScaleX());
				double heightDiff = availableHeight - (canvas.getHeight() * canvas.getScaleY());
				
				double scale = (widthDiff < heightDiff || widthDiff == heightDiff) ? 
						availableWidth / canvas.getWidth() : availableHeight / canvas.getHeight();
				
				CanvasUtils.scalePerserveOrigin(canvas, overlayCS, csPane, scale);
				
				scale *= 10000;
				int s = (int) scale;
				scale = (double) s / 100;
				
				zoomLvlLabel.setText(scale + "%");
			}
		});
		
		
		getHelpMI.setAccelerator(KeyCombination.keyCombination("F1"));
		getHelpMI.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				Popup.showPopup(primaryStage, PopupType.HELP);
			}
		});
		
		
		aboutMI.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				Popup.showPopup(primaryStage, PopupType.ABOUT);
			}
		});
	}
	
	// ---------------------------------------------------------------------------------------------------------------------- Timer
	public class Timer extends AnimationTimer{
		
		private OverlayTool overlayTool;
		private long lastUpdate;
		private int refreshRateNanos;
		
		public Timer(OverlayTool ot, int millis){
			this.overlayTool = ot;
			this.refreshRateNanos = millis * 1_000_000;
			lastUpdate = 0;
		}
		
		@Override
		public void handle(long now) {
			if(now - lastUpdate >= refreshRateNanos){
				
				if(overlayTool == null){
					System.err.println("overlayTool is null for some reason");
				}
				
				overlayTool.update(overlayCS, currentColor, currentFont);
				lastUpdate = now;
			}
		}
		
		public void setActive(){
			super.start();
			overlayTool.setActive();
		}
		
		public void setInactive(){
			super.stop();
			overlayTool.setInactive(overlayCS);
		}
	}
	
	// ---------------------------------------------------------------------------------------------------------------------- Local Methods
	/**
	 * 
	 * @param family
	 * @param size
	 */
	private void updateFont(String family, double size){
		currentFont = Font.font(family, currentFontStyle.getFontWeight(),
				currentFontStyle.getFontPosture(), size);
		
		gc.setFont(currentFont);
		overlayGC.setFont(currentFont);
		
		if(currentTool == Tool.TEXT && textBox.isActive()){
			textBox.update(overlayCS, currentColor, currentFont);
		}
	}
	
	private double getZoomPercent(double sliderValue){
		
		double num = Math.pow(2, Math.abs(sliderValue));
		
		if(sliderValue == 0){
			return 100;
		}
		else if (sliderValue > 0){
			return num * 100;
		}
		return (1 / num) * 100;
	}
}
