package com.yuchen.data.service.es.controller;

import com.yuchen.data.service.es.entity.ClusterNews;
import com.yuchen.data.service.es.entity.HotEventsSetting;
import com.yuchen.data.service.es.entity.HotNewsSetting;
import com.yuchen.data.service.es.service.*;
import com.yuchen.data.service.es.vo.*;
import com.yuchen.data.service.es.vo.ETLVo.CombineVo;
import com.yuchen.data.service.es.vo.ETLVo.DeleteIndexVo;
import com.yuchen.data.service.utils.result.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/bigdata")
@Api(tags = "hot event bigdata api service")
public class EsController {

    @Resource
    private HotEventsService hotEventsService;

    @Resource
    private HotEntityService hotEntityService;

    @Resource
    private EntityTypeService entityTypeService;

    @Resource
    private HotNewsService hotNewsService;

    @Resource
    private HotNewsEntityService hotNewsEntityService;

    @Resource
    private HotNewsSettingService hotNewsSettingService;

    @Resource
    private HotEventsSettingService hotEventsSettingService;

    @Resource
    private HotNewsScoreService hotNewsScoreService;

    @Resource
    private CustomLabelsCombineService customLabelsCombineService;

    @Resource
    private DeleteIndexService deleteIndexService;

    @Resource
    private ClusterNewsService clusterNewsService;

    @RequestMapping(value = "/get_news", method = RequestMethod.POST)
    @ApiOperation(value = "统一检索查询新闻", notes = "新闻查询")
    public ResponseResult getNews(@RequestBody HotNewsVo vo) {
        return hotNewsService.getNews(vo);
    }

    @RequestMapping(value = "/get_is_hot", method = RequestMethod.POST)
    @ApiOperation(value = "查询新闻是否热点", notes = "新闻查询")
    public Integer getIsHot(@RequestBody HotNewsScoreVo vo) {
        return hotNewsScoreService.getIsHot(vo);
    }

    @RequestMapping(value = "/get_is_exists", method = RequestMethod.POST)
    @ApiOperation(value = "查询新闻是否存在", notes = "新闻查询")
    public Integer getIsExists(@RequestBody HotNewsScoreVo vo) {return hotNewsScoreService.getIsExists(vo);}

    @RequestMapping(value = "/get_news_entity", method = RequestMethod.POST)
    @ApiOperation(value = "实体id查询实体名称", notes = "实体查询")
    public ResponseResult getNewsEntity(@RequestBody HotNewsEntityVo vo) {
        return hotNewsEntityService.getNewsEntity(vo);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ApiOperation(value = "规则配置查询新闻", notes = "新闻查询")
    public ResponseResult search(@RequestBody HotNewsSettingVo vo) {
        return hotNewsSettingService.search(vo);
    }

    @RequestMapping(value = "/search_text", method = RequestMethod.POST)
    @ApiOperation(value = "规则配置查询新闻", notes = "新闻查询")
    public List<HotNewsSetting> searchText(@RequestBody HotNewsSettingVo vo) {
        return hotNewsSettingService.searchText(vo);
    }

    @RequestMapping(value = "/search_cluster_news", method = RequestMethod.POST)
    @ApiOperation(value = "聚类新闻查询", notes = "新闻查询")
    public List<ClusterNews> searchClusterNews(@RequestBody ClusterNewsVo vo) {
        return clusterNewsService.searchClusterNews(vo);
    }

    @RequestMapping(value = "/search_events", method = RequestMethod.POST)
    @ApiOperation(value = "规则配置查询事件", notes = "事件查询")
    public List<HotEventsSetting> searchText(@RequestBody HotEventsSettingVo vo) {
        return hotEventsSettingService.searchEvents(vo);
    }

    @RequestMapping(value = "/post/events", method = RequestMethod.POST)
    @ApiOperation(value = "测试事件", notes = "事件查询")
    public ResponseResult getEvents(@RequestBody HotEventsVo  vo) {
        return hotEventsService.getEvents(vo);
    }

    @RequestMapping(value = "/etl/entity/name", method = RequestMethod.POST)
    @ApiOperation(value = "实体ID列表查询英文名", notes = "实体查询")
    public ResponseResult getEntitiesByIds(@RequestBody HotEntityVo vo) {
        return hotEntityService.getEntityName(vo);
    }

    @RequestMapping(value = "/etl/entity/type", method = RequestMethod.POST)
    @ApiOperation(value = "实体ID查询实体类型", notes = "实体类型查询")
    public String getEntityTypeById(@RequestBody EntityTypeVo vo) {
        return entityTypeService.getEntityType(vo);
    }

    @RequestMapping(value = "/etl/entity/labels", method = RequestMethod.POST)
    @ApiOperation(value = "实体ID列表查询标签", notes = "实体ID列表查询原子标签")
    public ResponseResult getAtomLabelsByIds(@RequestBody HotEntityVo vo) {
        return hotEntityService.getAtomLabels(vo);
    }

    @RequestMapping(value = "/partB/combineLabels", method = RequestMethod.POST)
    @ApiOperation(value = "增量打标B：合并返回最终的custom_labels", notes = "增量打标：合并最终的custom_labels")
    public ResponseResult combineLabels(@RequestBody CombineVo vo) {
        return customLabelsCombineService.combineCustomLabels(vo);
    }

    @RequestMapping(value = "/partA/deleteIndex", method = RequestMethod.POST)
    @ApiOperation(value = "增量打标A：清空索引表数据，不删索引", notes = "增量打标：清空索引表数据，不删索引")
    public ResponseResult deleteIndex(@RequestBody DeleteIndexVo vo) {
        return deleteIndexService.deleteAllData(vo);
    }

    @RequestMapping(value = "/partA/get_title_score", method = RequestMethod.POST)
    @ApiOperation(value = "增量打标A：获得新闻标题和权重", notes = "增量打标A：获得新闻标题和权重")
    public ResponseResult searchForScore(@RequestBody HotNewsSettingVo vo) {
        return hotNewsSettingService.searchForScore(vo);
    }

    @RequestMapping(value = "/partB/get_semantic_labels", method = RequestMethod.POST)
    @ApiOperation(value = "增量打标B：获得语义标签", notes = "增量打标B：获得语义标签")
    public ResponseResult searchForPartB(@RequestBody HotNewsSettingVo vo) {
        return hotNewsSettingService.searchForPartB(vo);
    }

    @RequestMapping(value = "/partA/get_partA_data", method = RequestMethod.POST)
    @ApiOperation(value = "增量打标A：获得一小时增量数据", notes = "增量打标A：获得一小时增量数据")
    public ResponseResult searchForPartA(@RequestBody HotNewsSettingVo vo) {
        return hotNewsSettingService.searchForPartA(vo);
    }

    @RequestMapping(value = "/history/get_old_mongo_data", method = RequestMethod.POST)
    @ApiOperation(value = "历史数据入库：获得旧版mongo数据", notes = "历史数据入库：获得旧版mongo数据")
    public ResponseResult searchForHistory(@RequestBody HotNewsSettingVo vo) {
        return hotNewsSettingService.searchForHistory(vo);
    }

    @RequestMapping(value = "/history/get_old_important_mongo_data", method = RequestMethod.POST)
    @ApiOperation(value = "历史数据入库：获得旧版mongo重要数据", notes = "历史数据入库：获得旧版mongo重要数据")
    public ResponseResult searchForImportantHistory(@RequestBody HotNewsSettingVo vo) {
        return hotNewsSettingService.searchForImportantHistory(vo);
    }

    @RequestMapping(value = "/hot_news/get_cluster_fileds", method = RequestMethod.POST)
    @ApiOperation(value = "用于获取聚类字段", notes = "用于获取聚类字段")
    public ResponseResult searchForCluster(@RequestBody HotNewsSettingVo vo) {
        return hotNewsSettingService.searchForCluster(vo);
    }

    @RequestMapping(value = "/hot_news/get_task_ids", method = RequestMethod.POST)
    @ApiOperation(value = "用于获取任务task_ids列表", notes = "用于获取任务ID列表")
    public ResponseResult getTaskIds(@RequestBody HotNewsVo vo) {
        return hotNewsService.getTaskIds(vo);
    }
}
