package com.dryork.config.controller;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author jsen
 * @date 17/09/2016
 * handle error request path
 */
@RestController
// @CrossOrigin
public class AppErrorController extends AbstractErrorController {

    private final ErrorProperties errorProperties;

    private final static String ERROR_PATH = "/error";

    private static final Logger logger = Logger.getLogger(AppErrorController.class.getName());

    public AppErrorController(ServerProperties serverProperties) {
        super(new JDefaultErrorAttributes());
        this.errorProperties = serverProperties.getError();
    }

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> body = this.getErrorAttributes(request, this.isIncludeStackTrace(request));
        // response.setStatus((int) body.get("hcode"));
        response.setStatus(200);
        return new ResponseEntity<>(body, HttpStatus.valueOf(response.getStatus()));
    }
    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    /**
     * Determine if the stacktrace attribute should be included.
     * @param request the source request
     * @return if the stacktrace attribute should be included
     */
    protected boolean isIncludeStackTrace(HttpServletRequest request) {
        ErrorProperties.IncludeStacktrace include = getErrorProperties().getIncludeStacktrace();
        if (include == ErrorProperties.IncludeStacktrace.ALWAYS) {
            return true;
        }
        if (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM) {
            return getTraceParameter(request);
        }
        return false;
    }

    /**
     * Provide access to the error properties.
     * @return the error properties
     */
    protected ErrorProperties getErrorProperties() {
        return this.errorProperties;
    }
}
