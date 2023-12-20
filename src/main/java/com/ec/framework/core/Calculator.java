package com.ec.framework.core;

import com.ec.framework.entry.discount.DiscountContext;
import com.ec.framework.entry.discount.DiscountWrapper;
import com.ec.framework.entry.goods.GoodsItem;

import java.util.Map;

/**
 * @ClassName Calculator
 * @Description
 * @Author: LiHao
 * @Since 2023/12/17 13:33
 */
public interface Calculator<T extends GoodsItem> {

    /**
     *优惠计算引擎的内部计算方法
     * @param context 上下文
     * @param wrapper 当前优惠信息
     * @param records 参与优惠的商品记录，用于过滤
     * @param index 活动的index
     * @param i 当前计算的索引下标，它和index的区别在于比如有数组[9,4,6,5]，则i为数组下标i=1时对应的index是4
     * @return
     */
    long calcWarp(DiscountContext<T> context, DiscountWrapper wrapper, Map<Long, T> records, byte index, int i);
}
