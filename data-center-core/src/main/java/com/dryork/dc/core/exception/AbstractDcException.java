package com.dryork.dc.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 *     dc 异常基类
 * </p>
 *
 * @author jsen
 * @since 2018-11-27
 */
public abstract class AbstractDcException extends RuntimeException {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDcException.class);

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public AbstractDcException(String message) {
        super(message);
        LOGGER.error(message);
    }
}
