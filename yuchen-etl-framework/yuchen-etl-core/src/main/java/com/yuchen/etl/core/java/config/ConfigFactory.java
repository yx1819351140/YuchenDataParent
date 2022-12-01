package com.yuchen.etl.core.java.config;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yuchen.common.constants.Constants;
import com.yuchen.common.pub.AbstractConfig;
import com.yuchen.common.utils.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/14 11:09
 * @description:
 */
public class ConfigFactory {

    public static <T extends Serializable> T loadFromJson(String json, Class<T> tClass) throws JsonProcessingException {
        //校验任务配置文件是否合法
        if (!JsonUtil.isJSONValid(json)) {
            throw new RuntimeException("Illegal configuration file content, please check the configuration file format.");
        }
        return content2Config(json, tClass);
    }

    public static <T extends Serializable> T load(String json, Class<T> tClass, boolean isEncode) {
        String loadJson = null;
        if (isEncode) {
            loadJson = CommonUtil.bese64Decode(json);
        }
        try {
            return loadFromJson(loadJson, tClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static <T extends Serializable> T load(File file, Class<T> tClass) throws IOException {
        String configFileContent = readConfigFile(file);
        T t = loadFromJson(configFileContent, tClass);
        return t;
    }

    public static String readConfigFile(File file) throws IOException {
        String content = null;
        String absolutePath = file.getAbsolutePath();
        if (absolutePath.endsWith(Constants.YML_FILE_SUFFIX) || absolutePath.endsWith(Constants.YAML_FILE_SUFFIX)) {
            String yamlContent = FileUtil.readFileContent(file);
            if (yamlContent != null) content = ConfConvertUtil.convertYaml2Json(yamlContent);
        } else if (absolutePath.endsWith(Constants.PROPERTIES_FILE_SUFFIX)) {
            String propContent = FileUtil.readFileContent(file);
            if (propContent != null) content = ConfConvertUtil.convertProp2Json(propContent);
        } else if (absolutePath.endsWith(Constants.JSON_FILE_SUFFIX)) {
            content = FileUtil.readFileContent(file);
        } else {
            //文件不可以是非法的文件类型
            throw new IOException(String.format("Unknown profile type. Only supported [%s | %s | %s]", Constants.JSON_FILE_SUFFIX, Constants.YAML_FILE_SUFFIX, Constants.PROPERTIES_FILE_SUFFIX));
        }
        if (content == null) {
            //文件内容不可以为空
            throw new IOException(String.format("The configuration file cannot be an empty file. Configuration file is %s", file.getName()));
        }
        return content;
    }


    public static <T extends Serializable> T content2Config(String content, Class<T> tClass) {
        return ObjectUtil.contentToObject(content, tClass);
    }

    public static <T extends Serializable> T load(String filePath, Class<T> tClass) throws IOException {
        if (StringUtils.isNotBlank(filePath)) {
            File file = new File(filePath);
            if (FileUtil.existsFile(file.getPath())) {
                return load(file, tClass);
            } else {
                throw new IOException("The job file does not exist, the configuration object cannot be created");
            }
        }
        return (T) new AbstractConfig() {
        };
    }


    public static String config2Content(SparkJobConfig config) throws JsonProcessingException {
        SparkJobConfig ContextConfig = new SparkJobConfig();
        return ObjectUtil.objectToContent(ContextConfig);
    }


    /**
     * 这里默认c2 覆盖 c1 存在优先级
     *
     * @param c1
     * @param c2
     * @return
     */
    public static AbstractConfig mergeJobConfig(AbstractConfig c1, AbstractConfig c2) {
        if (CheckTool.checkVersIsNull(c1, c2)) {
            CheckTool.throwNullPointException("JobConfig cannot be empty");
        }
        return mergeConfig(c1, c2);
    }

    public static AbstractConfig mergeConfig(AbstractConfig... configs) {
        CheckTool.checkIsNull(configs);
        if (configs.length < 2) {
            CheckTool.throwParameterErrorException("Need more than two Job Config");
        }

        JSONObject jsonObject = new JSONObject();
        for (AbstractConfig config : configs) {
            AbstractConfig.merge(jsonObject, ObjectUtil.beanToMap(config));
        }
        return ObjectUtil.mapToBean(jsonObject, AbstractConfig.class);
    }


    public static File writeJobFile(String dir, String fileName, String fileContent) throws IOException {
        File tmpDirFile = new File(dir);
        //如果文件目录存在就删除
        FileUtil.createDir(tmpDirFile, false);
        //拼接文件路径
        String filePath = dir + File.separator + fileName;
        File file = new File(filePath);
        //如果文件存在就删除
        if (FileUtil.existsFile(file)) {
            FileUtil.delete(file);
        }
        file.createNewFile();
        if (file.canWrite()) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(fileContent);
            bw.close();
        }
        return file;
    }
}
