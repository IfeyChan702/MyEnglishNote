package com.ruoyi.system.mapper;

import java.util.List;

import com.ruoyi.system.domain.GiftCard;
import com.ruoyi.system.domain.vo.GiftCardQueryVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


/**
 * 礼品卡Mapper接口
 *
 * @author ifey
 * @date 2025-12-02
 */
public interface GiftCardMapper
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
     * @param giftCard 礼品卡
     * @return 礼品卡集合
     */
    public List<GiftCard> selectGiftCardList(GiftCard giftCard);


    /**
     * 根据时间查询礼品卡列表
     *
     * @param queryVo 礼品卡
     * @return 礼品卡集合
     */
    public List<GiftCard> selectGiftCardListByTime(GiftCardQueryVo queryVo);

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
     * 删除礼品卡
     *
     * @param id 礼品卡主键
     * @return 结果
     */
    public int deleteGiftCardById(Long id);

    /**
     * 批量删除礼品卡
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGiftCardByIds(Long[] ids);

    @Select("SELECT * FROM gift_card WHERE code = #{code} LIMIT 1")
    GiftCard selectGiftCardByCode(String code);

//    @Update("UPDATE gift_card SET usage_type = #{usageType}, status = #{status} WHERE code = #{code}")
//    int updateGiftCardByCode(GiftCard giftCard);

    int updateGiftCardByCode(GiftCard giftCard);

    GiftCard selectOldestGiftCardByStatus(Long status);

    GiftCard selectOldestGiftCardByStatusAndTypeForUpdate(
            @Param("status") Long status,
            @Param("giftType") String giftType
    );

    int batchResetToUnused(List<Long> cardIds);

    Double selectGiftCardAmountSum(GiftCardQueryVo queryVo);
}
