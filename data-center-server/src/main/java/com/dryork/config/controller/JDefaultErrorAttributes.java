package com.dryork.config.controller;

import com.dryork.utils.ResponseBase;
import com.google.common.collect.Maps;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.ServletException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018/4/8
 */
public class JDefaultErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest requestAttributes, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = Maps.newLinkedHashMap();
        errorAttributes.put(ResponseBase.SimpleKey.code, 1);
        Throwable error = this.addErrorDetails(errorAttributes, requestAttributes, includeStackTrace);
        this.addStatus(errorAttributes, requestAttributes);
        if (error != null) {
            /*
            if (error instanceof JWTExpException) {
                errorAttributes.put(SWResponseBase.SimpleKey.ecode, 3001);
            }*/
            if (error instanceof AuthenticationException) {
                errorAttributes.put("hcode", 401);
                if (!StringUtils.isEmpty(error.getMessage())) {
                    errorAttributes.put("msg", error.getMessage());
                } else {
                    errorAttributes.put("msg", "认证失败");
                }
            } else if (error instanceof UnauthorizedException) {
                errorAttributes.put("hcode", 401);
                if (!StringUtils.isEmpty(error.getMessage())) {
                    errorAttributes.put("msg", error.getMessage());
                } else {
                    errorAttributes.put("msg", "缺少权限");
                }
            } else {
                this.addStatus(errorAttributes, requestAttributes);
                if (!StringUtils.isEmpty(error.getMessage())) {
                    errorAttributes.put("msg", error.getMessage());
                } else {
                    errorAttributes.put("msg", "缺少权限");
                }
            }
        }
        // errorAttributes.put("timestamp", new Date());
        // this.addStatus(errorAttributes, requestAttributes);
        this.addPath(errorAttributes, requestAttributes);
        return errorAttributes;
    }

    private void addStatus(Map<String, Object> errorAttributes, RequestAttributes requestAttributes) {
        Integer status = this.getAttribute(requestAttributes, "javax.servlet.error.status_code");
        errorAttributes.put(ResponseBase.SimpleKey.hcode, status);
        switch (status) {
            case 404:
                errorAttributes.put(ResponseBase.SimpleKey.msg, "no api found");
                break;
            default:
                errorAttributes.put(ResponseBase.SimpleKey.msg, "error " + status);
        }
    }

    private Throwable addErrorDetails(Map<String, Object> errorAttributes, WebRequest requestAttributes, boolean includeStackTrace) {
        Throwable error = this.getError(requestAttributes);

        if (error != null) {
            while(true) {
                if (!(error instanceof ServletException) || error.getCause() == null) {
                    errorAttributes.put(ResponseBase.SimpleKey.error, error.getClass().getName());
                    this.addErrorMessage(errorAttributes, error);
                    if (includeStackTrace) {
                        this.addStackTrace(errorAttributes, error);
                    }
                    break;
                }

                error = error.getCause();
            }
        }

        Object message = this.getAttribute(requestAttributes, "javax.servlet.error.message");
        if (errorAttributes.get(ResponseBase.SimpleKey.msg) == null && !(error instanceof BindingResult)) {
            if (!StringUtils.isEmpty(message)) {
                errorAttributes.put(ResponseBase.SimpleKey.msg, message);
            }
        }
        return error;
    }

    /**
     * 返回当前api路径信息
     * @param errorAttributes
     * @param requestAttributes
     */
    private void addPath(Map<String, Object> errorAttributes,
                         RequestAttributes requestAttributes) {
        String path = getAttribute(requestAttributes, "javax.servlet.error.request_uri");
        errorAttributes.put("api", path);
    }

    private void addErrorMessage(Map<String, Object> errorAttributes, Throwable error) {
        BindingResult result = this.extractBindingResult(error);
        if (result == null) {
            errorAttributes.put(ResponseBase.SimpleKey.msg, error.getMessage());
        } else {
            if (result.getErrorCount() > 0) {
                errorAttributes.put("errors", result.getAllErrors());
                errorAttributes.put(ResponseBase.SimpleKey.msg, "Validation failed for object='" + result.getObjectName() + "'. Error count: " + result.getErrorCount());
            } else {
                errorAttributes.put(ResponseBase.SimpleKey.msg, "No errors");
            }

        }
    }

    private void addStackTrace(Map<String, Object> errorAttributes, Throwable error) {
        StringWriter stackTrace = new StringWriter();
        error.printStackTrace(new PrintWriter(stackTrace));
        stackTrace.flush();
        errorAttributes.put("trace", stackTrace.toString());
    }

    private BindingResult extractBindingResult(Throwable error) {
        if (error instanceof BindingResult) {
            return (BindingResult) error;
        }
        if (error instanceof MethodArgumentNotValidException) {
            return ((MethodArgumentNotValidException) error).getBindingResult();
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, 0);
    }
}
