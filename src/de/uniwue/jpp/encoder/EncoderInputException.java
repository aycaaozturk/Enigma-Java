package de.uniwue.jpp.encoder;

public class EncoderInputException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public EncoderInputException () {
		super();
	}
	
	public EncoderInputException(String s) {
		super(s);
	}
	
	public EncoderInputException(String s, Throwable cause) {
		super(s, cause);
	}
}