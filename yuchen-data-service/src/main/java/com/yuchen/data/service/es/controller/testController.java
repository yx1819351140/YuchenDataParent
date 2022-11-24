package com.yuchen.data.service.es.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuchen.data.service.es.service.*;
import com.yuchen.data.service.es.vo.*;
import com.yuchen.data.service.utils.result.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Api(tags = "hot event bigdata api service")
public class testController {

    @RequestMapping(value = "/partA_test", method = RequestMethod.POST)
    @ApiOperation(value = "增量打标partA_test", notes = "增量打标partA_test")
    public ResponseResult partA_test() {
        JSONArray jsonArray = new JSONArray();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title_id", "00039a0d150b325e247fc2110d836ccd");
        jsonObject.put("weight_score", 0.25);

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("title_id", "000515ab11c0fe9f252d7cfa70ed5e11");
        jsonObject1.put("weight_score", 0.5);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("title_id", "0007ede8b0b19acfa8787340e1938782");
        jsonObject2.put("weight_score", 0.75);

        jsonArray.add(jsonObject);
        jsonArray.add(jsonObject1);
        jsonArray.add(jsonObject2);



        return new ResponseResult(200, "success",jsonArray);
    }

}
