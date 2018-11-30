package com.dryork.dc.core.exception;

import com.dryork.dc.core.utils.DcLogger;

/**
 * <p>
 *     dc 执行dc数据库操作时出现数据库操作异常，报出该异常
 * </p>
 *
 * @author jsen
 * @since 2018-11-29
 */
public class DcSqlException extends AbstractDcException {
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public DcSqlException(String message, DcLogger.LOG log) {
        super(message);
        log.finished();
    }
}
