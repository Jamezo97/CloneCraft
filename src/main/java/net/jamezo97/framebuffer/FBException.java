package net.jamezo97.framebuffer;

public class FBException extends Exception{

	public FBException() {
		super();
	}

	public FBException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FBException(String message, Throwable cause) {
		super(message, cause);
	}

	public FBException(String message) {
		super(message);
	}

	public FBException(Throwable cause) {
		super(cause);
	}
	
	

}
