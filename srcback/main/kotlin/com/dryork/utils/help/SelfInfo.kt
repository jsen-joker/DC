package top.jsen.utils.help

import com.alibaba.fastjson.JSONObject
import com.dryork.config.inject.ConfigAdapter
import com.dryork.utils.TextUtils

import javax.servlet.ServletRequest

/**
 *
 * @author jsen
 * @since 12/10/2018
 */
object SelfInfo {

    private var serverAddress = ""
    private var updated = false

    fun getServerAddress(): String {
        return serverAddress
    }

    fun setServerAddress(request: ServletRequest) {
        if (TextUtils.isEmpty(serverAddress) || !updated) {
            updated = true
            if (request.serverPort == 80) {
                SelfInfo.serverAddress = String.format("%s://%s/", request.scheme, request.serverName)
            } else {
                SelfInfo.serverAddress = String.format("%s://%s:%s/", request.scheme, request.serverName, request.serverPort)
            }
            var dconf = ConfigAdapter.getPropertiesToJSONObject("dconf.properties")
            if (dconf == null) {
                dconf = JSONObject()
            }
            dconf["self.server"] = SelfInfo.serverAddress
            ConfigAdapter.dumpJSONObjectToProperties(dconf, "dconf.properties")
        }
    }

    fun setServerAddress(address: String) {
        if (TextUtils.isEmpty(serverAddress)) {
            SelfInfo.serverAddress = address
        }
    }
}
