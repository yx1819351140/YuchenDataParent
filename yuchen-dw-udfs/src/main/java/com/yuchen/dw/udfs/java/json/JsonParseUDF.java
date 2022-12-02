package com.yuchen.dw.udfs.java.json;

import com.alibaba.fastjson.JSONObject;
import com.yuchen.common.utils.JsonExtractTool;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/22 10:36
 * @Package: com.yuchen.dw.udfs.json
 * @ClassName: JsonParseUDF
 * @Description:
 **/
public class JsonParseUDF extends GenericUDF {

    private transient StringObjectInspector stringOI0;
    private transient StringObjectInspector stringOI1;
    private static final Logger logger = LoggerFactory.getLogger(JsonParseUDF.class);
    private static final JsonExtractTool jsonExtract = JsonExtractTool.getInstance();

    @Override
    public ObjectInspector initialize(ObjectInspector[] objectInspectors) throws UDFArgumentException {
        if (objectInspectors.length != 2) {
            throw new UDFArgumentException("JsonParseUDF() takes only tow argument");
        }
        // 校验参数类型是String
        if (objectInspectors[0].getCategory() != ObjectInspector.Category.PRIMITIVE && ((PrimitiveObjectInspector) objectInspectors[0]).getPrimitiveCategory() != PrimitiveObjectInspector.PrimitiveCategory.STRING) {
            throw new UDFArgumentException("JsonParseUDF() takes a String as a parameter");
        }
        //校验jsonpath表达式参数类型是string
        if (objectInspectors[1].getCategory() != ObjectInspector.Category.PRIMITIVE && ((PrimitiveObjectInspector) objectInspectors[0]).getPrimitiveCategory() != PrimitiveObjectInspector.PrimitiveCategory.STRING) {
            throw new UDFArgumentException("JsonParseUDF() takes a String as a parameter");
        }

        logger.debug("测试UDF初始化,代码执行,参数: {}", JSONObject.toJSONString(objectInspectors));
        this.stringOI0 = (StringObjectInspector) objectInspectors[0];
        this.stringOI1 = (StringObjectInspector) objectInspectors[1];
        return PrimitiveObjectInspectorFactory.javaStringObjectInspector;
    }

    @Override
    public Object evaluate(DeferredObject[] deferredObjects) throws HiveException {
        // select json(obj, "$.职业[0][1].test[*].age") from a where xxx = 1;
        String jsonStr = stringOI0.getPrimitiveJavaObject(deferredObjects[0].get());
        String jsonPaths = stringOI1.getPrimitiveJavaObject(deferredObjects[1].get());
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        Object extract = null;
        try {
            extract = jsonExtract.extract(jsonStr, jsonPaths);
        } catch (Exception e) {
            logger.debug("json 解析异常, 有可能是");
        }
        return extract;
    }

    @Override
    public String getDisplayString(String[] strings) {
        StringBuilder sb = new StringBuilder();
        sb.append("input json ");
        sb.append(strings[0]);
        return sb.toString();
    }

}
