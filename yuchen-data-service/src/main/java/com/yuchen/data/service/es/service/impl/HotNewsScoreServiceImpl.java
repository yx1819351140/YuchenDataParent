package com.yuchen.data.service.es.service.impl;

import com.yuchen.data.service.es.dao.HotNewsScoreDao;
import com.yuchen.data.service.es.service.HotNewsScoreService;
import com.yuchen.data.service.es.vo.HotNewsScoreVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class HotNewsScoreServiceImpl implements HotNewsScoreService {
    @Resource
    private HotNewsScoreDao hotNewsScoreDao;

    @Override
    public Integer getIsHot(HotNewsScoreVo vo) {
        try {
            String id = vo.getId();
            Double score = 0.0;
            try {
                score = hotNewsScoreDao.findById(id).get().getScore();
            } catch (Exception e) {
                return 0;
            }
            return score > 0.76 ? 1 : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Integer getIsExists(HotNewsScoreVo vo) {
        String id = vo.getId();
        boolean isExists = false;
        try {
            isExists = hotNewsScoreDao.existsById(id);
        } catch (Exception e) {
        }
        return isExists ? 1 : 0;
    }
}
