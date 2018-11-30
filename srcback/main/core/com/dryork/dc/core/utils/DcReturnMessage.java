package com.dryork.dc.core.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

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
public class DcReturnMessage {
    private String app;
    private ActionType actionType;
    private List<DcReturnRecord> dcReturnRecordList;
}
