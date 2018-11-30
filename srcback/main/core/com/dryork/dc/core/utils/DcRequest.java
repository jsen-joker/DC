package com.dryork.dc.core.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class DcRequest {
    private ActionType actionType;
    /**
     * <<表名字>, <字段名字, 字段值>>
     */
    private Map<String, Map<String, Object>> params;
}
