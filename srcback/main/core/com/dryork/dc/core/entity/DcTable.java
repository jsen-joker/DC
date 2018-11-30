package com.dryork.dc.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Map;

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
public class DcTable extends AbstractDcBean {
    private String local;
    private String remote;

    private Map<String, DcColumn> dcColumnMap;


}
