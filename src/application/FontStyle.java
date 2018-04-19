package application;

import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class FontStyle {
	private boolean bold, italic;
	private FontWeight weight;
	private FontPosture posture;
	
	/**
	 * 
	 * @param bold
	 * @param italic
	 */
	public FontStyle(boolean b, boolean i){
		this.bold = b;
		this.italic = i;
		
		this.weight = this.bold ? FontWeight.BOLD : FontWeight.NORMAL;
		this.posture = this.italic ? FontPosture.ITALIC : FontPosture.REGULAR;
	}
	
	/**
	 * 
	 * @param weight
	 * @param posture
	 */
	public FontStyle(FontWeight w, FontPosture p){
		this.weight = w;
		this.posture = p;
		
		this.bold = (this.weight == FontWeight.BOLD) ? true : false;
		this.italic = (this.posture == FontPosture.ITALIC) ? true : false;
	}
	
	public boolean isBold(){ return bold; }
	public boolean isItalic(){ return italic; }
	public FontWeight getFontWeight(){ return weight; }
	public FontPosture getFontPosture(){ return posture; }
	
	/**
	 * 
	 * @param w
	 */
	public void setFontWeight(FontWeight w){ this.weight = w; }
	
	/**
	 * 
	 * @param p
	 */
	public void setFontPosture(FontPosture p){ this.posture = p; }
	
	public String toString(){
		String str = "";
		if(bold && !italic){ str = "Bold"; }
		else if(!bold && italic){ str = "Italic"; }
		else if(bold && italic){ str = "Bold Italic"; }
		else{ str = "Regular"; }
		
		return str;
	}
}