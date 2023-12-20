package com.ec.framework.entry.discount;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 优惠配置类信息
 * @ClassName DiscountConfig
 * @Description
 * @Author: LiHao
 * @Since 2023/12/17 17:55
 */
@Data
public class DiscountConfig implements Serializable {

    /**
     *当前优惠的优先级（如10 - 单商品组，20 - 整单组）
     */
    private int calculateGroup;

    /**
     * 商品限制 (0 - 所有商品可参与，1 - 指定某些商品参与， 2- 指定某些商品不参与)
     */
    private int goodsType;

    /**
     * 下一个属性 itemIds 存放的内容是啥，（0 - 商品ID， 1 - SKUID， 2 - 类目Id）
     */
    private int itemIdType;

    /**
     * 同上
     */
    private List<Long> itemIds;

    /**
     * 活动拓展信息
     */
    private Map<String, Object> extra;

}
