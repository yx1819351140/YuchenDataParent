package com.yuchen.data.service.base.service;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

/**
 * 基础Service接口
 * @param <T>
 * @author lizhiwei
 */
public interface IBaseService<T> {

     /**
      * 列表查询
      * @param params
      * @param beanClass
      * @return
      * @throws Exception
      */
	Example<T> listExample(Map<String, Object> params, Class<T> beanClass) throws Exception ;

     /**
      * 排序列表
      * @param params
      * @param sortField
      * @return
      */
	Sort listSort(Map<String, Object> params, String sortField);

     /**
      * 分页排序查询
      * @param params
      * @param sort
      * @return
      */
	Pageable listPageable(Map<String, Object> params, Sort sort);

     /**
      * 查找
      * @param params
      * @param sortField
      * @return
      */
	Pageable listPageable(Map<String, Object> params, String sortField);

     /**
      * 查找
      * @param example
      * @return
      */
     List<T> findAll(Example<T> example);


     /**
      * 排序
      * @param sort
      * @return
      */
     List<T> findAll(Sort sort);

     /**
      * find
      * @param spec
      * @param sort
      * @return
      */
     List<T> findAll(Specification<T> spec, Sort sort);

     /**
      * 分页查询
      * @param pageable
      * @return
      */
     Page<T> findAll(Pageable pageable);

     /**
      * 分页查询
      * @param spec
      * @param pageable
      * @return
      */
     Page<T> findAll(Specification<T> spec, Pageable pageable);

     /**
      * 分页查询
      * @param example
      * @param pageable
      * @return
      */
     Page<T> findAll(Example<T> example, Pageable pageable);

     /**
      * 查找排序
      * @param example
      * @param sort
      * @return
      */
     List<T> findAll(Example<T> example, Sort sort);

     /**
      * 查找所有
      * @param params
      * @param beanClass
      * @param sortField
      * @return
      * @throws Exception
      */
     Page<T> findAll(Map<String, Object> params, Class<T> beanClass, String sortField) throws Exception;

     /**
      * 查找所有
      * @return
      */
     List<T> findAll();

     /**
      * 根据id查询
      * @param ids
      * @return
      */
     List<T> findAllById(List<Integer> ids);

     /**
      * 总数
      * @param spec
      * @return
      */
     long count(Specification<T> spec);

     /**
      * 总数量
      * @param example
      * @return
      */
     long count(Example<T> example);

     /**
      * 查找
      * @param pageNum
      * @param pageSize
      * @return
      */
     Page<T> find(int pageNum, int pageSize);

     /**
      * 根据ID查询
      * @param id
      * @return
      */
     T findById(Integer id);

     /**
      * 是否存在
      * @param id
      * @return
      */
     boolean existsById(Integer id);

     /**
      * 查找信息
      * @param spec
      * @return
      */
     T findOne(Specification<T> spec);

     /**
      * 查找信息
      * @param example
      * @return
      */
     T findOne(Example<T> example);

     /**
      * 删除所有
      * @param list
      */
     void deleteAll(List<T> list);

     /**
      * 根据ID删除
      * @param id
      */
     void deleteById(Integer id);

     /**
      * 删除信息
      * @param t
      */
     void delete(T t);

     /**
      * 新增和修改(根据ID来判断)
      * @param t
      * @return
     */
     T save(T t);

     /**
      * 批量删除
      * @param ids
      */
     void deleteByIds(Integer... ids);


}
