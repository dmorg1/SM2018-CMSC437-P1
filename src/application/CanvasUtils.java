package application;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class CanvasUtils {
	
	/**
	 * <p>
	 * Gets a snapshot of the canvas and saves it to a file path, specified by {@code file}.
	 * </p>
	 * @param canvas The canvas object
	 * @param file The file path to save the resulting image to
	 */
	public static void saveToImageFile(Canvas canvas, File file){
		if(file != null){
			WritableImage wImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
			canvas.snapshot(null, wImage);
			RenderedImage render = SwingFXUtils.fromFXImage(wImage, null);
			
			try {
				ImageIO.write(render, "png", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <p>
	 * Opens an image file from the file path determined by {@code file} and opens a draggable image
	 * which is applied to the main canvas when the user clicks out of the image area. Resizes the
	 * canvases as necessary.
	 * </p>
	 * @param canvas The canvas object
	 * @param overlay The overlay canvas object
	 * @param file The file path from which to load from
	 */
	public static void openFromImageFile(Canvas canvas, Canvas overlay, File file){
		if(file != null){
			try {
				WritableImage wImage = SwingFXUtils.toFXImage(ImageIO.read(file), null);
				
				canvas.setHeight(wImage.getHeight());
				canvas.setWidth(wImage.getWidth());
				overlay.setHeight(wImage.getHeight());
				overlay.setWidth(wImage.getWidth());
				
				canvas.getGraphicsContext2D().drawImage(wImage, 0, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <p>
	 * Gets the current content of the clipboard and stores it in an {@code Image} object. If the
	 * object is not {@code null}, convert to a {@code WriteableImage} and draw to the canvas.
	 * </p>
	 * @param canvas
	 */
	public static OverlaySelection paste(Canvas canvas){
		
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		
		Image image = clipboard.getImage();
		
		if(image != null){
			WritableImage wImage = SwingFXUtils.toFXImage(SwingFXUtils.fromFXImage(image, null), null);
			return new OverlaySelection(wImage);
		}
		
		return null;
	}
	
	/**
	 * <p> 
	 * Clears the entire area of the {@code canvas} and replaces the area with a white rectangle.
	 * </p>
	 * @param canvas The canvas object
	 * @param color The color used to restore the fill color to after completion
	 */
	public static void clearAll(Canvas canvas, Color color){
		canvas.getGraphicsContext2D().setFill(Color.WHITE);
		canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		canvas.getGraphicsContext2D().setFill(color);
	}
	
	/**
	 * <p>
	 * Clears an area of the canvas defined by {@code x}, {@code y}, {@code width}, and
	 * {@code height}, and replaces the area with a white rectangle.
	 * </p>
	 * @param canvas The canvas object
	 * @param color The color used to restore the fill color to after completion
	 * @param x The upper-left x coordinate of the area
	 * @param y The upper-left y coordinate of the area
	 * @param width The width of the area
	 * @param height The height of the area
	 */
	public static void clearArea(Canvas canvas, Color color, double x, double y, double width, double height){
		canvas.getGraphicsContext2D().setFill(Color.WHITE);
		canvas.getGraphicsContext2D().fillRect(x, y, width, height);
		canvas.getGraphicsContext2D().setFill(color);
	}
	
	
	public static void handleDragEvent(Canvas overlay, OverlaySelection selectTool, double eventX, double eventY){
		double xDiff = eventX - selectTool.getDragOriginX();
		double yDiff = eventY - selectTool.getDragOriginY();
		
		double newX = selectTool.getOriginalX() + xDiff;
		double newY = selectTool.getOriginalY() + yDiff;
		
		selectTool.moveTo(newX, newY);
	}
	
	
	public static void scalePerserveOrigin(Canvas canvas, Canvas overlay, Pane canvasPane, double scale){
		canvas.setScaleX(scale);
		canvas.setScaleY(scale);
		canvas.setTranslateX(((canvas.getWidth() * canvas.getScaleX()) - canvas.getWidth()) / 2);
		canvas.setTranslateY(((canvas.getHeight() * canvas.getScaleY()) - canvas.getHeight()) / 2);
		
		overlay.setScaleX(scale);
		overlay.setScaleY(scale);
		overlay.setTranslateX(((overlay.getWidth() * overlay.getScaleX()) - overlay.getWidth()) / 2);
		overlay.setTranslateY(((overlay.getHeight() * overlay.getScaleY()) - overlay.getHeight()) / 2);
		
		canvasPane.setPrefSize(canvas.getWidth() * canvas.getScaleX(), canvas.getHeight() * canvas.getScaleY());
	}
	
	/**
	 * <p>
	 * Starting from the location the user clicked, ({@code x}, {@code y}), each pixel with the same
	 * color value as {@code oldColor} is replaced by the color {@code newColor}. The operation is
	 * recursive, and is complete only when each pixel has no adjacent pixels that:
	 * </p>
	 * <ul>
	 * <li> Originally had the same color as the origin pixel</li>
	 * <li> Now has the same color as {@code color}</li>
	 * </ul>
	 * @param canvas The canvas object
	 * @param oldColor The color of the origin pixel
	 * @param newcolor	The color to change to
	 * @param x	The x coordinate of the pixel
	 * @param y The y coordinate of the pixel
	 */
	public static void fill(Canvas canvas, Color oldColor, Color newcolor, double x, double y){
		
	}
}
