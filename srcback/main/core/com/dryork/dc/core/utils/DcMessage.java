package com.dryork.dc.core.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * <p>
 *     保证全部转换为dc端字段
 * </p>
 *
 * @author jsen
 * @since 2018-11-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DcMessage {

    /**
     * 会自动获取，不用设置
     */
    private String app;
    /**
     * 客户端设置 执行类型
     */
    private ActionType actionType;
    /**
     * 客户端设置，多条执行记录，group型执行要保证顺序
     */
    private Set<DcRecord> dcRecordSet;


}
