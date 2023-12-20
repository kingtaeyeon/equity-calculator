package com.ec.framework.core.impl;

import com.ec.framework.core.Calculator;
import com.ec.framework.entry.calc.CalcResult;
import com.ec.framework.entry.calc.CalcStage;
import com.ec.framework.entry.discount.DiscountContext;
import com.ec.framework.entry.discount.DiscountWrapper;
import com.ec.framework.entry.goods.GoodsItem;

import java.util.Map;

/**
 * 抽象类计算器，每种类型优惠做一个实现类，负责创建stage，维护CalcStage[]数组等内部工作，这对使用者是透明的
 *
 * @ClassName AbstractCalculator
 * @Description
 * @Author: LiHao
 * @Since 2023/12/18 23:08
 */
public abstract class AbstractCalculator<T extends GoodsItem> implements Calculator<T> {


    @Override
    public long calcWarp(DiscountContext<T> context, DiscountWrapper wrapper, Map<Long, T> records, byte index, int i) {
        CalcStage stage = new CalcStage();
        CalcResult result = context.getCalcResult();
        long curPrice = result.getCurPrice();
        stage.setBeforeCalcPrice(curPrice);


        return 0;
    }

    /**
     * 返回该优惠下的最终要支付的金额，若不符合则返回 prevStagePrice
     * @param context 上下文
     * @param wrapper 优惠信息
     * @param records 记录享受过优惠的商品，key是calculateId，这里只提供容器，添加和判断规则则由使用者自行决定
     * @param precStagePrice 上一步计算出的订单价格
     * @param curStage 当前stage
     * @return
     */
    public abstract long calc(DiscountContext<T> context, DiscountWrapper wrapper, Map<Long, T> records, long precStagePrice, CalcStage curStage);
}
