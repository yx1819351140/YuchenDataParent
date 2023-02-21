//package com.yuchen.etl.runtime.java.rules;
//
//import com.alibaba.fastjson.JSONObject;
//import com.yuchen.etl.core.java.config.TaskConfig;
//import org.apache.flink.api.common.state.CheckpointListener;
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.runtime.state.FunctionInitializationContext;
//import org.apache.flink.runtime.state.FunctionSnapshotContext;
//import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
//import org.apache.flink.streaming.api.functions.ProcessFunction;
//import org.apache.flink.util.Collector;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Map;
//
///**
// * @Author: xiaozhennan
// * @Date: 2023/2/6 13:40
// * @Package: com.yuchen.etl.runtime.java.rules
// * @ClassName: RuleProcessFunc
// * @Description: 规则引擎算子
// **/
//public class RuleProcessFunc extends ProcessFunction<JSONObject, JSONObject> implements CheckpointedFunction, CheckpointListener {
//    private static final Logger logger = LoggerFactory.getLogger(RuleProcessFunc.class);
//
//    private TaskConfig taskConfig;
//    private RuleEngine ruleEngine;
//
//    public RuleProcessFunc(TaskConfig taskConfig) {
//        this.taskConfig = taskConfig;
//    }
//
//    @Override
//    public void open(Configuration parameters) throws Exception {
//        Map<String, Object> ruleEngineConfigs = taskConfig.getMap("ruleEngine");
//        this.ruleEngine = RuleEngineFactory.builder()
//                .config(ruleEngineConfigs)
//                .type(EngineType.DROOLS)
//                .init(true)
//                .build();
//        ruleEngine.start();
//    }
//
//    @Override
//    public void close() throws Exception {
//        super.close();
//    }
//
//    @Override
//    public void notifyCheckpointComplete(long checkpointId) throws Exception {
//        logger.info("通知checkpoint完成, checkpoint id: {}", checkpointId);
//    }
//
//    @Override
//    public void snapshotState(FunctionSnapshotContext context) throws Exception {
//        logger.info("checkpoint进行中...");
//    }
//
//    @Override
//    public void initializeState(FunctionInitializationContext context) throws Exception {
//        logger.info("初始化执行checkpoint");
//    }
//
//    @Override
//    public void processElement(JSONObject value, ProcessFunction<JSONObject, JSONObject>.Context ctx, Collector<JSONObject> out) throws Exception {
//        logger.info("开始执行规则引擎: {}", value.toJSONString());
//        ruleEngine.execute(value.getJSONObject("data"));
//        out.collect(value);
//        logger.info("规则引擎执行完成: {}", value.toJSONString());
//    }
//}
