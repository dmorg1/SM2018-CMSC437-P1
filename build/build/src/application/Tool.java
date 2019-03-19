package application;

public enum Tool{
	BRUSH (0),
	TEXT (1),
	SELECT(2),
	COLOR_PICKER(3);
	
	private int num;
	Tool(int n){
		this.num = n;
	}
	
	public int valueOf() { return num; }
	
	public static Tool intToTool(int value){
		switch(value){
		case 0:
			return Tool.BRUSH;
		case 1:
			return Tool.TEXT;
		case 2:
			return Tool.SELECT;
		case 3:
			return Tool.COLOR_PICKER;
		}
		return Tool.BRUSH;
	}
}