package com.yuchen.data.service.es.service.impl;

import com.yuchen.data.service.es.dao.HotEntityDao;
import com.yuchen.data.service.es.dao.PartATempDao;
import com.yuchen.data.service.es.dao.TempADao;
import com.yuchen.data.service.es.dao.TempBDao;
import com.yuchen.data.service.es.service.DeleteIndexService;
import com.yuchen.data.service.es.vo.ETLVo.DeleteIndexVo;
import com.yuchen.data.service.utils.result.ResponseResult;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DeleteIndexServiceImpl implements DeleteIndexService {

    @Resource
    private HotEntityDao hotEntityDao;
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Resource
    private PartATempDao partATempDao;

    @Resource
    private TempADao  tempADao;

    @Resource
    private TempBDao tempBDao;

    @Override
    public ResponseResult deleteAllData(DeleteIndexVo vo) {
        String indexName = vo.getIndexName();
        if(indexName.equals("part_a_temp")){
            partATempDao.deleteAll();
            return new ResponseResult(200,"删除part_a_temp成功", null);
        }else if(indexName.equals("test_a")){
            //delete part_b_temp
            tempADao.deleteAll();
            return new ResponseResult(200,"删除test_a成功", null);
        }else if(indexName.equals("test_b")){
            tempBDao.deleteAll();
            return new ResponseResult(200,"删除test_b成功", null);
        }

        return new ResponseResult(500,"删除失败", null);
    }
}
