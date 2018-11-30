package com.dryork.dc.core.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DcReturnRecord implements Serializable {
    /** Serialization version */
    private static final long serialVersionUID = 1L;

    private Long mapKey;

    private Long dcId;
    private String app;
}
