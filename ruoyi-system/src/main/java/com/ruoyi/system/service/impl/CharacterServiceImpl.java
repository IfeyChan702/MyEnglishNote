package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.Character;
import com.ruoyi.system.mapper.CharacterMapper;
import com.ruoyi.system.service.ICharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 故事主角Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-02-11
 */
@Service
public class CharacterServiceImpl implements ICharacterService
{
    @Autowired
    private CharacterMapper characterMapper;

    /**
     * 查询故事主角
     * 
     * @param id 主角ID
     * @return 故事主角
     */
    @Override
    public Character selectCharacterById(Long id)
    {
        return characterMapper.selectCharacterById(id);
    }

    /**
     * 查询故事主角列表
     * 
     * @param character 故事主角
     * @return 故事主角
     */
    @Override
    public List<Character> selectCharacterList(Character character)
    {
        return characterMapper.selectCharacterList(character);
    }

    /**
     * 根据用户ID查询主角列表
     * 
     * @param userId 用户ID
     * @return 故事主角集合
     */
    @Override
    public List<Character> selectCharacterListByUserId(Long userId)
    {
        return characterMapper.selectCharacterListByUserId(userId);
    }

    /**
     * 新增故事主角
     * 
     * @param character 故事主角
     * @return 结果
     */
    @Override
    @Transactional
    public int insertCharacter(Character character)
    {
        character.setDelFlag("0");
        character.setStoryCount(0);
        character.setCreatedAt(new Date());
        character.setUpdatedAt(new Date());
        return characterMapper.insertCharacter(character);
    }

    /**
     * 修改故事主角
     * 
     * @param character 故事主角
     * @return 结果
     */
    @Override
    @Transactional
    public int updateCharacter(Character character)
    {
        character.setUpdatedAt(new Date());
        return characterMapper.updateCharacter(character);
    }

    /**
     * 删除故事主角对象
     * 
     * @param id 主角ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteCharacterById(Long id)
    {
        return characterMapper.deleteCharacterById(id);
    }

    /**
     * 批量删除故事主角
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteCharacterByIds(Long[] ids)
    {
        return characterMapper.deleteCharacterByIds(ids);
    }

    /**
     * 搜索主角
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return 主角列表
     */
    @Override
    public List<Character> searchCharacters(Long userId, String keyword)
    {
        return characterMapper.searchCharacters(userId, keyword);
    }
}
