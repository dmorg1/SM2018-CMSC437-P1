package application;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class OverlayTool {
	public abstract void setActive();
	public abstract void setInactive(Canvas canvas);
	public abstract void update(Canvas canvas, Color color, Font font);
}
