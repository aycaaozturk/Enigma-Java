package de.uniwue.jpp.enigma;

public class EnigmaCreationException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public EnigmaCreationException () {
		super();
	}
	
	public EnigmaCreationException(String s) {
		super(s);
	}
	
	public EnigmaCreationException(String s, Throwable cause) {
		super(s, cause);
	}
}
