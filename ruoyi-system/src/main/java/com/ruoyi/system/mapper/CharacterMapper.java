package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.Character;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 故事主角Mapper接口
 * 
 * @author ruoyi
 * @date 2025-02-11
 */
public interface CharacterMapper
{
    /**
     * 查询故事主角
     * 
     * @param id 主角ID
     * @return 故事主角
     */
    public Character selectCharacterById(Long id);

    /**
     * 查询故事主角列表
     * 
     * @param character 故事主角
     * @return 故事主角集合
     */
    public List<Character> selectCharacterList(Character character);

    /**
     * 根据用户ID查询主角列表
     * 
     * @param userId 用户ID
     * @return 故事主角集合
     */
    public List<Character> selectCharacterListByUserId(Long userId);

    /**
     * 新增故事主角
     * 
     * @param character 故事主角
     * @return 结果
     */
    public int insertCharacter(Character character);

    /**
     * 修改故事主角
     * 
     * @param character 故事主角
     * @return 结果
     */
    public int updateCharacter(Character character);

    /**
     * 删除故事主角
     * 
     * @param id 主角ID
     * @return 结果
     */
    public int deleteCharacterById(Long id);

    /**
     * 批量删除故事主角
     * 
     * @param ids 需要删除的数据ID集合
     * @return 结果
     */
    public int deleteCharacterByIds(Long[] ids);

    /**
     * 增加主角故事计数
     * 
     * @param characterId 主角ID
     * @return 结果
     */
    public int incrementStoryCount(Long characterId);

    /**
     * 减少主角故事计数
     * 
     * @param characterId 主角ID
     * @return 结果
     */
    public int decrementStoryCount(Long characterId);

    /**
     * 搜索主角
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return 主角列表
     */
    public List<Character> searchCharacters(
        @Param("userId") Long userId,
        @Param("keyword") String keyword
    );
}
