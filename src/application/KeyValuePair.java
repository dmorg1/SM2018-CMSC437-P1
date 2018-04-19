package application;

import javafx.util.StringConverter;

public class KeyValuePair {
	private final String key;
	private final int value;
	public KeyValuePair(){
		this.key = null;
		this.value = -1;
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public KeyValuePair(String key, int value) {
		this.key = key;
		this.value = value;
	}

	public String getKey()   {    return key;    }

	public int getValue() {    return value;  }
	
	public StringConverter<KeyValuePair> getConverter() {
		return new StringConverter<KeyValuePair>(){
			public String toString(KeyValuePair object) {
				return object.getKey();
			}
			public KeyValuePair fromString(String string) {
				return null;
			}
		};
	}
}