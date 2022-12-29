package com.yuchen.udfs.test;

import com.yuchen.common.utils.FileUtil;
import com.yuchen.common.utils.JsonExtractTool;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/2 9:03
 * @Package: com.yuchen.udfs.test
 * @ClassName: JsonFactoryTest
 * @Description:
 **/
public class JsonFactoryTest {
    private static final JsonExtractTool jsonExtract = JsonExtractTool.getInstance();

    @Test
    public void testJsonPath() {
//        JsonPath jsonPath = new JsonPath("$.职业[0][1].test[*].age");
        try {
            String json = FileUtil.readFileContent("src/test/resources/udf-example.json");
            Object extract = jsonExtract.extract(json, "$.labels.nb..value");
            System.out.println(extract);
//            BufferedWriter out = new BufferedWriter(new FileWriter("result.txt"));
//            out.write(extract.toString());
//            out.close();
            Assert.assertTrue(extract != null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testWikiJsonP() {
//        JsonPath jsonPath = new JsonPath("$.职业[0][1].test[*].age");
        try {
            String json = FileUtil.readFileContent("src/test/resources/wiki.json");
            Object extract = jsonExtract.extract(json, "$.claims.*.[1].mainsnak.datavalue..value..id");
            System.out.println(extract);
            Assert.assertTrue(extract != null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testWikiJsonP01() {
//        JsonPath jsonPath = new JsonPath("$.职业[0][1].test[*].age");
        try {
            String json = FileUtil.readFileContent("src/test/resources/test.json");
            Object extract = jsonExtract.extract(json, "$.within.[2]");
            System.out.println(extract);
            Assert.assertTrue(extract != null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
