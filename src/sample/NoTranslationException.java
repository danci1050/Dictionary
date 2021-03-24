package sample;

public class NoTranslationException extends Exception {
	private static final long serialVersionUID = 1L;

	public NoTranslationException() {
		super("The word could not be found in the dictionary");
	}

	public NoTranslationException(String message) {
		super(message);
	}

}
