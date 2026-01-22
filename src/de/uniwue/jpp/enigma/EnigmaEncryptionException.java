package de.uniwue.jpp.enigma;

public class EnigmaEncryptionException extends Exception {

	private static final long serialVersionUID = 1L;

	public EnigmaEncryptionException() {
		super();
	}

	public EnigmaEncryptionException(String s) {
		super(s);
	}

	public EnigmaEncryptionException(String s, Throwable cause) {
		super(s, cause);
	}
}