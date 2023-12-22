package com.ec.framework.demo.calc;

import com.ec.framework.core.impl.AbstractCalculator;
import com.ec.framework.demo.constant.Constant;
import com.ec.framework.entry.calc.CalcStage;
import com.ec.framework.entry.discount.DiscountContext;
import com.ec.framework.entry.discount.DiscountWrapper;
import com.ec.framework.entry.goods.GoodsItem;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @ClassName FullCalc
 * @Description
 * @Author: LiHao
 * @Since 2023/12/22 20:30
 */
@Component("full")
public class FullCalc extends AbstractCalculator<GoodsItem> {

    @Override
    public long calc(DiscountContext<GoodsItem> context, DiscountWrapper wrapper, Map<Long, GoodsItem> records, long precStagePrice, CalcStage curStage) {

        List<GoodsItem> goodsItems = Lists.newArrayList(context.getDiscountItemGroup().get(wrapper.getId()));
        if (precStagePrice >= 100 * 100) {
            precStagePrice = precStagePrice - 20 * 100;
        }
        for (GoodsItem item: goodsItems) {
            item.getExtra().put(Constant.UPDATE_ABLE_PRICE, (long)item.getExtra().get(Constant.UPDATE_ABLE_PRICE) - 20 * 100 / goodsItems.size());
        }
        return precStagePrice;
    }
}
