package application;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class OverlaySelection extends OverlayTool {
	
	private WritableImage selectedImage;
	private SelectionBoundsState currentState;
	private final SelectionBoundsState initState;
	private double x1, x2, y1, y2, upperLeftX, upperLeftY, width, height, dragOriginX, dragOriginY, originalX, originalY;
	private boolean active, draggable;
	
	public OverlaySelection(){
		x1 = 0;
		x2 = 0;
		y1 = 0;
		y2 = 0;
		
		upperLeftX = 0;
		upperLeftX = 0;
		width = 0;
		height = 0;
		
		dragOriginX = 0;
		dragOriginY = 0;
		
		originalX = 0;
		originalY = 0;
		
		active = false;
		draggable = false;
		
		initState = SelectionBoundsState.INITIAL;
		currentState = initState;
		
		selectedImage = null;
	}
	
	public OverlaySelection(WritableImage wi){
		upperLeftX = 0;
		upperLeftX = 0;
		width = 0;
		height = 0;
		
		dragOriginX = 0;
		dragOriginY = 0;
		
		originalX = 0;
		originalY = 0;
		
		active = false;
		draggable = false;
		
		initState = SelectionBoundsState.STATIC;
		currentState = initState;
		
		selectedImage = wi;
		
		x1 = 0;
		x2 = wi.getWidth();
		y1 = 0;
		y2 = wi.getHeight();
	}

	@Override
	public void setActive() {
		active = true;
		
	}

	@Override
	public void setInactive(Canvas overlay) {
		active = false;
		currentState = initState;
		overlay.getGraphicsContext2D().clearRect(0, 0, overlay.getWidth(), overlay.getHeight());
		overlay.getGraphicsContext2D().setLineDashes(0d);
	}

	@Override
	public void update(Canvas overlay, Color color, Font font) {
		
		GraphicsContext gc = overlay.getGraphicsContext2D();
		
		gc.clearRect(0, 0, overlay.getWidth(), overlay.getHeight());
		
		
		upperLeftX = (x1 < x2) ? x1 : x2;
		upperLeftY = (y1 < y2) ? y1 : y2;
		width = (x1 < x2) ? x2 - x1 : x1 - x2;
		height = (y1 < y2) ? y2 - y1 : y1 - y2;
		
		if(currentState == SelectionBoundsState.STATIC){
			overlay.getGraphicsContext2D().drawImage(selectedImage, upperLeftX, upperLeftY);
		}
		
		gc.setLineDashes(4d);
		gc.strokeRect(upperLeftX + 1, upperLeftY + 1, width - 2, height - 2);

	}
	
	public void setSelectedImage(Canvas canvas, Canvas overlay, Color color){
		
		if(width != 0 && height != 0){
			SnapshotParameters p = new SnapshotParameters();
			p.setViewport(new Rectangle2D(upperLeftX, upperLeftY, width, height));
			
			selectedImage = new WritableImage((int) width, (int) height);
			
			canvas.snapshot(p, selectedImage);
			
			CanvasUtils.clearArea(canvas, color, upperLeftX, upperLeftY, width, height);
		}
	}
	
	public void placeSelectedImage(Canvas canvas, Canvas overlay){
		
		double correctedX = upperLeftX + (overlay.getTranslateX() - canvas.getTranslateX());
		double correctedY = upperLeftY + (overlay.getTranslateY() - canvas.getTranslateY());
		
		canvas.getGraphicsContext2D().drawImage(selectedImage, correctedX, correctedY);
		
		selectedImage = null;
	}
	
	public void setClipboard(){
		
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		final ClipboardContent clipboardContent = new ClipboardContent();
		
		clipboardContent.put(DataFormat.IMAGE, selectedImage);
		clipboard.setContent(clipboardContent);
	}
	
	public double getX(){ return upperLeftX; }
	public double getY(){ return upperLeftY; }
	public double getWidth(){ return width; }
	public double getHeight(){ return height; }
	
	
	public boolean isActive(){ return active; }
	
	
	public void setDraggable(double eventX, double eventY){ 
		draggable = true;
		dragOriginX = eventX;
		dragOriginY = eventY;
		originalX = upperLeftX;
		originalY = upperLeftY;
	}
	
	public void setUndraggable(){ draggable = false; }
	public boolean isDraggable(){ return draggable; }
	
	
	public double getDragOriginX(){ return dragOriginX; }
	public double getDragOriginY(){ return dragOriginY; }
	
	public double getOriginalX(){ return originalX; }
	public double getOriginalY(){ return originalY; }
	
	
	public void setOrigin(double x, double y){
		x1 = x;
		y1 = y;
	}
	
	public void setSecondPoint(double x, double y){
		x2 = x;
		y2 = y;
	}
	
	public void moveTo(double x, double y){
		width = (x1 < x2) ? x2 - x1 : x1 - x2;
		height = (y1 < y2) ? y2 - y1 : y1 - y2;
		
		x1 = x;
		y1 = y;
		
		x2 = x1 + width;
		y2 = y1 + height;
	}
	
	public SelectionBoundsState getState(){
		return currentState;
	}
	
	public void setState(SelectionBoundsState s){
		currentState = s;
	}
	
	public boolean containsPoint(double x, double y){
		Rectangle rect = new Rectangle(upperLeftX, upperLeftY, width, height);
		return rect.contains(x, y);
	}
	
	
	public enum SelectionBoundsState {
		
		INITIAL(0),
		DYNAMIC(1),
		STATIC(2);
		
		private int num;
		
		SelectionBoundsState(int n){
			this.num = n;
		}
		
		public int valueOf(){
			return num;
		}
	}

}
