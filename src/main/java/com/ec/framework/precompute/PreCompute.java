package com.ec.framework.precompute;

import com.ec.framework.entry.discount.DiscountWrapper;
import com.ec.framework.entry.goods.GoodsItem;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 预计算接口
 *
 * @Description 预计算，把一些耗时的操作放在context上下文中进行
 * @Author: LiHao
 * @Since 2023/12/17 13:30
 */
public interface PreCompute<T extends GoodsItem> {

    /**
     * 判断符合条件的活动类型，符合才会执行preComputeItems
     *
     * @return
     */
    Set<String> matchTypes();

    /**
     * 对商品做一些复杂集合操作
     * @param items 当前参与优惠的商品
     * @param wrapper 当前优惠
     * @param preCompute 存储计算的结果
     */
    void preComputeItems(List<T> items, DiscountWrapper wrapper, Map<String, Object> preCompute);
}
