package com.dryork.dc.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * <p>
 *     dc 核心处理统一日志
 * </p>
 *
 * @author jsen
 * @since 2018-11-29
 */
public class DcLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger("DcLog");

    public static LOG startLog(DcMessage dcMessage) {
        return new LOG(dcMessage);
    }
    public static LOG startEmpty() {
        return new LOGEmpty();
    }

    public static class LOG {
        private String id;

        private LOG(DcMessage dcMessage) {
            id = UUID.randomUUID().toString();
            LOGGER.info("start----------------------------------" + id);
            debug("app: {}, actionType: {}", dcMessage.getApp(), dcMessage.getActionType());
        }

        private LOG() {
        }

        public void startRecord(DcRecord dcRecord) {
            debug("dcId: {}, table: {}, keys: {}, replace: {}, ignore: {}, sum: {}",
                    dcRecord.getDcId(), dcRecord.getTable(), dcRecord.getKeys(), dcRecord.getReplace(),
                    dcRecord.getIgnore(), dcRecord.getSum());
        }

        public void info(String format, Object... arguments) {
            LOGGER.info(id + "--" + format, arguments);
        }

        public void debug(String format, Object... arguments) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(id + "--" + format, arguments);
            }
        }

        public void finished() {
            LOGGER.info("end----------------------------------" + id);
        }
    }

    public static class LOGEmpty extends LOG {

        private LOGEmpty() {
            super();
        }

        @Override
        public void startRecord(DcRecord dcRecord) {
        }

        @Override
        public void info(String format, Object... arguments) {
            LOGGER.info(format, arguments);
        }

        @Override
        public void debug(String format, Object... arguments) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(format, arguments);
            }
        }

        @Override
        public void finished() {
        }
    }

}
