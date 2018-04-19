package application;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class OverlayTextBox extends OverlayTool {
	private Text text, cursor;
	private double xPos, yPos;
	private boolean active, cToggle;
	
	public OverlayTextBox(){
		text = new Text("");
		cursor = new Text("|");
		
		active = false;
		cToggle = false;
		
		xPos = 0;
		yPos = 0;
	}
	
	/**
	 * Note: {@code setLocation()} does not update the visible location of the text box. Use
	 * {@code update()} to show the location change visually.
	 * @param x
	 * @param y
	 */
	public void setLocation(double x, double y){
		xPos = x;
		yPos = y;
	}
	
	public String getText(){ return text.getText(); }
	
	/**
	 * 
	 * @param text
	 */
	public void setText(String text){ 
		this.text.setText(text); 
	}
	
	public double getX(){
		return xPos;
	}
	
	public double getY(){
		return yPos;
	}
	
	public boolean isActive(){ 
		return active; 
	}
	
	public void setActive(){
		active = true;
	}
	
	/**
	 * 
	 * @param color
	 * @param graphicsContext
	 */
	public void setInactive(Canvas overlayCS){
		active = false;
		text.setText("");
		overlayCS.getGraphicsContext2D().clearRect(0, 0, overlayCS.getWidth(), overlayCS.getHeight());
		overlayCS.getGraphicsContext2D().setLineDashes(0d);
	}
	
	/**
	 * 
	 * @param cs
	 * @param gc
	 * @param color
	 * @param font
	 */
	public void update(Canvas cs, Color color, Font font){
		cs.getGraphicsContext2D().clearRect(0, 0, cs.getWidth(), cs.getHeight());
		
		// For future reference: Yes, these 3 lines DO matter. Why? Otherwise we can't properly calculate what the layoutBounds are.
		// Basically, the the graphics context is set to is all that matters (we don't use the Text object; only its string). So, without
		// properly updating the Text object itself, then the bounds we get back are the bounds you'd get from JavaFX font defaults.
		text.setFill(color);
		text.setFont(font);
		cursor.setFont(Font.font("System", font.getSize()));
		
		double width = text.getLayoutBounds().getWidth() + cursor.getLayoutBounds().getWidth() + 20;
		double height = font.getSize() * 1.5;
		
		cs.getGraphicsContext2D().setFill(Color.WHITE);
		cs.getGraphicsContext2D().fillRect(xPos, yPos, width, height);
		cs.getGraphicsContext2D().setFill(color);
		
		cs.getGraphicsContext2D().setLineDashes(4d);
		cs.getGraphicsContext2D().strokeRect(xPos, yPos, width, height);
		
		cs.getGraphicsContext2D().fillText(text.getText(), xPos + 10, yPos + font.getSize() * 1.1);
		
		if(cToggle){
			cs.getGraphicsContext2D().setFill(Color.BLACK);
			cs.getGraphicsContext2D().fillText(cursor.getText(), xPos + text.getLayoutBounds().getWidth() + 10,
					yPos + font.getSize() * 1.1);
			cs.getGraphicsContext2D().setFill(color);
		}
		
		cToggle = !cToggle;
	}
}