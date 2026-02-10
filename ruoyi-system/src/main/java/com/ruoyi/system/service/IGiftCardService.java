package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.GiftCard;
import com.ruoyi.system.domain.vo.GiftCardQueryVo;
import com.ruoyi.system.mapper.GiftCardMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 礼品卡Service接口
 *
 * @author ifey
 * @date 2025-12-02
 */
public interface IGiftCardService
{
    /**
     * 查询礼品卡
     *
     * @param id 礼品卡主键
     * @return 礼品卡
     */
    public GiftCard selectGiftCardById(Long id);

    /**
     * 查询礼品卡列表
     *
     * @param queryVo 礼品卡
     * @return 礼品卡集合
     */
    public List<GiftCard> selectGiftCardList(GiftCard queryVo);

    /**
     * 查询礼品卡列表
     *
     * @param queryVo 礼品卡
     * @return 礼品卡集合
     */
    public List<GiftCard> selectGiftCardList(GiftCardQueryVo queryVo);

    /**
     * 新增礼品卡
     *
     * @param giftCard 礼品卡
     * @return 结果
     */
    public int insertGiftCard(GiftCard giftCard);

    /**
     * 修改礼品卡
     *
     * @param giftCard 礼品卡
     * @return 结果
     */
    public int updateGiftCard(GiftCard giftCard);

    /**
     * 批量删除礼品卡
     *
     * @param ids 需要删除的礼品卡主键集合
     * @return 结果
     */
    public int deleteGiftCardByIds(Long[] ids);

    /**
     * 删除礼品卡信息
     *
     * @param id 礼品卡主键
     * @return 结果
     */
    public int deleteGiftCardById(Long id);
    int updateGiftCardByCode(GiftCard giftCard);

    public GiftCard getOldestGiftCardByStatus(Long status,String giftType);

    Double selectGiftCardAmountSum(GiftCardQueryVo queryVo);
}
