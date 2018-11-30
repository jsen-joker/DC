package com.dryork.dc.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DcApp extends AbstractDcBean {
    private String name;
    private String type;
}
