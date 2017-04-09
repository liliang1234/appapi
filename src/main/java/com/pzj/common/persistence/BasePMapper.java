package com.pzj.common.persistence;

import java.util.List;
import java.util.Map;

public interface BasePMapper<T extends BasePEntity> {

    /**
     * 删除对象
     * 
     * @param record
     * @return
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入
     * 
     * @param record
     * @return
     */
    int insert(T record);

    /**
     * 根据id查询对象
     * 
     * @param id
     * @return
     */
    T selectByPrimaryKey(Long id);

    /**
     * 按条件更新
     * 
     * @param record
     * @return
     */
    int updateByPrimaryKey(T record);

    /**
     * 返回所有数据
     * 
     * @param record
     * @return
     */
    List<T> selectAllObj(T record);

    /**
     * 通用总纪录数
     * 
     * @param record
     * @return
     */
    int countByParamMap(Map<String, Object> params);

    /**
     * 通用分页
     * 
     * @param params
     * @return
     */
    List<T> queryByParamMap(Map<String, Object> params);

    /**
     * 批量插入
     * 
     * @param entityList
     * @return
     */
    Long insertBatch(List<T> entityList);

    /**
     * 批量更新
     * 
     * @param entityList
     * @return
     */
    int updateBatchByPrimaryKey(List<T> entityList);

}
