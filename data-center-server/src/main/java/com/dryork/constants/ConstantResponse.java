package com.dryork.constants;


import com.dryork.utils.ResponseBase;

/**
 * <p>
 * </p>
 *
 * @author ${User}
 * @since 2018/4/20
 */
public interface ConstantResponse {
    ResponseBase FAIL = ResponseBase.create().code(1);
    ResponseBase SUCCESS = ResponseBase.create().code(0);
}
