package com.dryork.dc.core.utils.help;

import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-30
 */
public class ParameterHelp {

    /**
     * 生成sql a=a,b=b
     * a=a and b=b
     *  and
     * @param params
     * @param split
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String genUpdateSql(Map params, String split) {
        StringBuilder build = new StringBuilder();
        params.forEach((key, value) -> {
            build.append("`").append(key).append("`='").append(value).append("'").append(split);
        });
        if (build.length() > 0) {
            return build.substring(0, build.length() - split.length());
        }
        return null;
    }

}
