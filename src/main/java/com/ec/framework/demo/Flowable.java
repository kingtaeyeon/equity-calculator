package com.ec.framework.demo;

import com.ec.framework.core.Permutation;
import com.ec.framework.demo.constant.Constant;
import com.ec.framework.entry.calc.CalcStage;
import com.ec.framework.entry.calc.CalcState;
import com.ec.framework.entry.discount.DiscountContext;
import com.ec.framework.entry.goods.GoodsItem;

import java.util.List;

/**
 * @ClassName Flowable
 * @Description
 * @Author: LiHao
 * @Since 2023/12/22 23:42
 */
public class Flowable extends Permutation<GoodsItem> {

    @Override
    protected void resetContext(DiscountContext<GoodsItem> context) {

    }

    @Override
    protected void makeSnapshot(DiscountContext<GoodsItem> context, CalcState<GoodsItem> calcState) {

    }

    @Override
    protected boolean sameOptimumCondition(CalcStage[] stages) {
        return false;
    }

    @Override
    protected boolean enableOptimize(List<Byte> a) {
        return false;
    }

    @Override
    protected void backToSnapshot(DiscountContext<GoodsItem> context, CalcState<GoodsItem> state) {

    }

    @Override
    protected void resetItems(GoodsItem item) {
        item.getExtra().put(Constant.UPDATE_ABLE_PRICE, item.getSalePrice());
    }
}
