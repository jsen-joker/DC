package com.dryork.config.inject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * <p>
 *     first search conf
 *     search config
 *     search classpath
 *
 *     use default spring config system profile、conditional etc.
 *     keep same behaviour as sb
 * </p>
 *
 * @author jsen
 * @since 2018/7/13
 */
@Configuration
public class ConfigAdapter {

    static InputStream getDefaultConfigInputStream(String configFile) {
        File file = new File("config");
        if (!file.exists()) {
            if (!file.mkdir()) {
                return ConfigAdapter.class.getClassLoader().getResourceAsStream(configFile);
            }
        }
        file = new File("config" + File.separator + configFile);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return ConfigAdapter.class.getClassLoader().getResourceAsStream(configFile);
                }
            } catch (IOException e) {
                return ConfigAdapter.class.getClassLoader().getResourceAsStream(configFile);
            }
        }

        try {
            return new FileInputStream("config" + File.separator + configFile);
        } catch (FileNotFoundException e) {
            return ConfigAdapter.class.getClassLoader().getResourceAsStream(configFile);
        }
    }

    public static InputStream profileAdapter(String configFile, String profile) {
        String name = configFile.substring(0, configFile.lastIndexOf("."));
        String suffix = configFile.substring(configFile.lastIndexOf("."));
        String nConfigFile = name + "-" + profile + suffix;
        InputStream inputStream = getDefaultConfigInputStream(nConfigFile);
        if (inputStream != null) {
            return inputStream;
        }
        return getDefaultConfigInputStream(configFile);
    }


    public static JSONObject getPropertiesToJSONObject(String configFile) {


        try(InputStream inputStream = getDefaultConfigInputStream(configFile)) {
            JSONObject obj = new JSONObject();
            Properties prop = new Properties();
            prop.load(inputStream);
            Set<String> enumeration = prop.stringPropertyNames();
            for (String key : enumeration) {
                obj.put(key, prop.getProperty(key));
            }
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * json
     * @param configFile
     * @return
     */
    public static JSONObject getDefaultConfigJSONObject(String configFile) {
        InputStream inputStream = getDefaultConfigInputStream(configFile);

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
            return JSON.parseObject(data.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getDefaultConfigJSONArray(String configFile) {
        InputStream inputStream = getDefaultConfigInputStream(configFile);

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
            return JSON.parseArray(data.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static void dumpJSONObjectToProperties(JSONObject file, String configFile) {
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("config" + File.separator + configFile)))) {
            StringBuilder build = new StringBuilder();
            for (Map.Entry<String, Object> entry : file.entrySet()) {
                build.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            }
            writer.write(build.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
