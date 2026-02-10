package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.EmbeddingRecord;

import java.util.List;

/**
 * 向量检索记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
public interface EmbeddingMapper
{
    /**
     * 查询向量检索记录
     * 
     * @param id 记录ID
     * @return 向量检索记录
     */
    public EmbeddingRecord selectEmbeddingRecordById(Long id);

    /**
     * 查询向量检索记录列表
     * 
     * @param embeddingRecord 向量检索记录
     * @return 向量检索记录集合
     */
    public List<EmbeddingRecord> selectEmbeddingRecordList(EmbeddingRecord embeddingRecord);

    /**
     * 新增向量检索记录
     * 
     * @param embeddingRecord 向量检索记录
     * @return 结果
     */
    public int insertEmbeddingRecord(EmbeddingRecord embeddingRecord);

    /**
     * 删除向量检索记录
     * 
     * @param id 记录ID
     * @return 结果
     */
    public int deleteEmbeddingRecordById(Long id);

    /**
     * 批量删除向量检索记录
     * 
     * @param ids 需要删除的数据ID集合
     * @return 结果
     */
    public int deleteEmbeddingRecordByIds(Long[] ids);
}
