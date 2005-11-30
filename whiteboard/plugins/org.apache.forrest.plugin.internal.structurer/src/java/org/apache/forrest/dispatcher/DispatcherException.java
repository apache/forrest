package org.apache.forrest.dispatcher;

/**
 * Dispatcher Exception
 */
public class DispatcherException extends Exception {
    public static final String ERROR_404 =  "code: 404 - Source not found";
    public static final String ERROR_500 =  "code: 500 - Internal server error";
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new DispatcherException.
     */
    public DispatcherException() {
        super();
    }

    /**
     * Creates a new DispatcherException.
     * @param message the exception message
     */
    public DispatcherException(String message) {
        super(message);
    }

    /**
     * Creates a new DispatcherException.
     * @param message the exception message
     * @param cause the cause of the exception
     */
    public DispatcherException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new DispatcherException.
     * @param cause the cause of the exception
     */
    public DispatcherException(Throwable cause) {
        super(cause);
    }

}
