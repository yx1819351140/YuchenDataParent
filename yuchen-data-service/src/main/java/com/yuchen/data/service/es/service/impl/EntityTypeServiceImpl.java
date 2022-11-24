package com.yuchen.data.service.es.service.impl;

import com.yuchen.data.service.es.dao.EntityTypeDao;
import com.yuchen.data.service.es.entity.NestedEntity.entity.LabelEntity;
import com.yuchen.data.service.es.service.EntityTypeService;
import com.yuchen.data.service.es.vo.EntityTypeVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EntityTypeServiceImpl implements EntityTypeService {

    @Resource
    private EntityTypeDao entityTypeDao;

    @Override
    public String getEntityType(EntityTypeVo vo) {
        String qid = vo.getQID();
        Double score = 0.0;
        try{
            List<LabelEntity> labels = entityTypeDao.findById(qid).get().getLabels();
            String upTypeString = "";
            for (LabelEntity labelEntity: labels) {
                String upType = labelEntity.getUp_type();
                upTypeString = upTypeString + upType + " ";
            }
            if (upTypeString.contains("国家")) {
                return "Country_Instance";
            } else if (upTypeString.contains("地点")) {
                return "City";
            } else if (upTypeString.contains("人物")) {
                return "People";
            } else if (upTypeString.contains("机构")) {
                return "Org";
            } else if (upTypeString.contains("武器")) {
                return "Weapon";
            } else if (upTypeString.contains("空中目标")) {
                return "AirTarget";
            } else if (upTypeString.contains("海上目标")) {
                return "SeaTarget";
            } else if (upTypeString.contains("基地目标")) {
                return "Base";
            } else {
                return "Other";
            }
        } catch (Exception ignored) {
        }
        return "Other";
    }
}
