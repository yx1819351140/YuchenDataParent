package com.yuchen.dw.udfs.java.json;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;

import java.util.HashMap;

/**
 * @Author: xiaozhennan
 * @Date: 2022/11/22 10:36
 * @Package: com.yuchen.dw.udfs.json
 * @ClassName: JsonParseUDF
 * @Description:
 **/
public class JsonParseUDF extends GenericUDF {

    private transient StringObjectInspector stringOI;

    @Override
    public ObjectInspector initialize(ObjectInspector[] objectInspectors) throws UDFArgumentException {
        if (objectInspectors.length != 2) {
            throw new UDFArgumentException("JsonParseUDF() takes only tow argument");
        }
        // 校验参数类型是String
        if (objectInspectors[0].getCategory() != ObjectInspector.Category.PRIMITIVE && ((PrimitiveObjectInspector) objectInspectors[0]).getPrimitiveCategory() != PrimitiveObjectInspector.PrimitiveCategory.STRING) {
            throw new UDFArgumentException("JsonParseUDF() takes a String as a parameter");
        }

        if (objectInspectors[1].getCategory() != ObjectInspector.Category.PRIMITIVE && ((PrimitiveObjectInspector) objectInspectors[0]).getPrimitiveCategory() != PrimitiveObjectInspector.PrimitiveCategory.STRING) {
            throw new UDFArgumentException("JsonParseUDF() takes a String as a parameter");
        }

        this.stringOI = (StringObjectInspector) objectInspectors[0];
        return PrimitiveObjectInspectorFactory.javaStringObjectInspector;
    }

    @Override
    public Object evaluate(DeferredObject[] deferredObjects) throws HiveException {
        // select json(obj, "obj.name.realname") from a where xxx = 1;
        String jsonStr = stringOI.getPrimitiveJavaObject(deferredObjects[0].get());
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        HashMap<String, String> testRes = new HashMap<>();
        testRes.put("t", System.currentTimeMillis() + "");
        // TODO 这里解析JSON, 解析格式  abc.baa[0].ddd
        return JSONObject.toJSONString(testRes);
    }

    @Override
    public String getDisplayString(String[] strings) {
        StringBuilder sb = new StringBuilder();
        sb.append("input json ");
        sb.append(strings[0]);
        return sb.toString();
    }

}
