package com.dryork.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * simple jsen rest response
 * @version v1.2.5
 * @author jsen
 */
public class ResponseBase extends JSONObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseBase.class);
    private static final long serialVersionUID = 362498520763181265L;


    public ResponseBase() {
    }
    public static ResponseBase create() {
        return new ResponseBase().code(0);
    }
    public static ResponseBase create(Object o) {
        ResponseBase responseBase = create();
        responseBase.putAll((JSONObject) JSON.toJSON(o));
        return responseBase;
    }


    /**
     * easy when chain
     * @param key key
     * @param value value
     * @return self
     */
    public ResponseBase add(String key, Object value) {
        put(key, value);
        return this;
    }

    /**
     * easy to merge result
     * @param map content eg JSONObject
     * @return self
     */
    public ResponseBase addAll(Map<? extends String, ? extends Object> map) {
        putAll(map);
        return this;
    }

    /**
     * a SWResponseBase response object must contain code key to classier bad request
     * @param code succeed 0 otherwise 1
     * @return self
     */
    public ResponseBase code(int code) {
        return add(SimpleKey.code, code);
    }

    /**
     *
     * @param o a java bean object
     * @return self
     */
    public ResponseBase append(Object o) {
        putAll((JSONObject) JSON.toJSON(o));
        return this;
    }

    /**
     * in most situation data is a complex object or a list
     * @param data any primary type 、 JSONArray 、 JSONObject 、 Map 、 List   etc.
     * @return self
     */
    public ResponseBase data(Object data) {
        return add(SimpleKey.data, data);
    }

    /**
     * in most situation when an error thrown (code eq 1),some error msg or tips will store in here
     * @param msg content
     * @return self
     */
    public ResponseBase msg(String msg) {
        LOGGER.debug(msg);
        return add(SimpleKey.msg, msg);
    }

    /**
     * spring try to return a httpCode=200 response,but the actual httpCode is not 200,so we can send the real httpCode here
     * @param hcode content, eg 404
     * @return self
     */
    public ResponseBase hcode(int hcode) {
        return add(SimpleKey.hcode, hcode);
    }

    /**
     * when some error happened,msg or code can not easy to location the error,
     * so ecode can easier classier the logical and business error
     * @param ecode content
     * @return self
     */
    public ResponseBase ecode(int ecode) {
        return add(SimpleKey.ecode, ecode);
    }

    /**
     * it just like msg,but you can store error when a exception exactly be catch
     * @param error content
     * @return self
     */
    public ResponseBase error(String error) {
        return add(SimpleKey.error, error);
    }


    public interface SimpleKey {
        String msg = "msg";
        String data = "data";
        String hcode = "hcode";
        String ecode = "ecode";
        String error = "error";
        String code = "code";
    }

}
