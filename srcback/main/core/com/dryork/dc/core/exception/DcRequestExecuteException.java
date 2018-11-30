package com.dryork.dc.core.exception;

/**
 * <p>
 *     参数解析出错，严格验证，一旦不符合就抛出
 * </p>
 *
 * @author jsen
 * @since 2018-11-27
 */
public class DcRequestExecuteException extends DcException {

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
