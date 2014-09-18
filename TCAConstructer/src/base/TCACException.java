package base;

public class TCACException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TCACException() {
	}

	public TCACException(String message) {
		super(message);
	}

	public TCACException(Throwable cause) {
		super(cause);
	}

	public TCACException(String message, Throwable cause) {
		super(message, cause);
	}

}
