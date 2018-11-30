package com.dryork.dc.server.config;

import com.dryork.dc.server.service.logical.DcServerCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-29
 */
@Component
public class DcServerApplicationEventListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DcServerApplicationEventListener.class);

    @Autowired
    private DcServerCoreService dcServerCoreService;
    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("spring application ready");
        dcServerCoreService.serverListen();
    }
}
