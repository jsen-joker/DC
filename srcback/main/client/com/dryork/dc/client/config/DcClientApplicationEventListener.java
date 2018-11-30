package com.dryork.dc.client.config;

import com.dryork.dc.client.service.logical.DcClientCoreService;
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
public class DcClientApplicationEventListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DcClientApplicationEventListener.class);

    @Autowired
    private DcClientCoreService dcClientCoreService;
    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("spring application ready");
        dcClientCoreService.clientRegisterToZK();
    }
}
