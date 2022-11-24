package com.yuchen.data.service.es.service.impl;

import com.yuchen.data.service.es.dao.HotNewsDao;
import com.yuchen.data.service.es.dao.PartATempDao;
import com.yuchen.data.service.es.service.CustomLabelsCombineService;
import com.yuchen.data.service.es.vo.ETLVo.CombineVo;
import com.yuchen.data.service.utils.result.ResponseResult;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;

@Service
public class CustomLabelsCombineServiceImpl implements CustomLabelsCombineService {

    @Resource
    private HotNewsDao hotNewsDao;
    @Resource
    private PartATempDao partATempDao;

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public ResponseResult combineCustomLabels(CombineVo vo) {
        String idName = vo.getId();
        List<Long> labels = vo.getSemanticLabels();
        List<Long> customLabels = null;
        try{
            customLabels = hotNewsDao.findById(idName).get().getCustomLabels();
            customLabels.addAll(labels);
            return new ResponseResult(200,"查询合并成功", removeDuplicate(customLabels));
        } catch (Exception e) {
            customLabels = labels;
            return new ResponseResult(201,"查询失败，返回原始结果", removeDuplicate(customLabels));
        }


    }
    // List去重，使用哈希set
    public static List removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

}
