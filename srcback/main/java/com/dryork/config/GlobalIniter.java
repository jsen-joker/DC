package com.dryork.config;


import com.alibaba.fastjson.JSONObject;
import com.dryork.config.inject.ConfigAdapter;
import top.jsen.utils.help.SelfInfo;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018/9/26
 */
public class GlobalIniter {


    public static void globalInit() {
        JSONObject dconf = ConfigAdapter.getPropertiesToJSONObject("dconf.properties");
        if (dconf != null) {
            if (dconf.containsKey("self.server")) {
                SelfInfo.INSTANCE.setServerAddress(dconf.getString("self.server"));
            }
        }
        // JSONObject.DEFFAULT_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    }


}
