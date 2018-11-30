package com.dryork.dc.core.exception;

/**
 * <p>
 *     dc dubbo远程执行出错异常
 * </p>
 *
 * @author jsen
 * @since 2018-11-27
 */
public class DcRequestExecuteException extends AbstractDcException {

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public DcRequestExecuteException(String message) {
        super(message);
    }
}
