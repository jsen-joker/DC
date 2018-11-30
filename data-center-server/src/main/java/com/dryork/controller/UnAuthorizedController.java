package com.dryork.controller;

import com.dryork.utils.ResponseBase;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018/9/26
 */
@RestController
@CrossOrigin
@RequestMapping("/pub")
public class UnAuthorizedController {

    @RequestMapping("/loginFailed")
    public ResponseBase loginFailed() {
        return ResponseBase.create().code(1).msg("UnAuthorized");
    }
}
