package com.ec.framework.demo.calc;

import com.ec.framework.core.impl.AbstractCalculator;
import com.ec.framework.demo.constant.Constant;
import com.ec.framework.entry.calc.CalcStage;
import com.ec.framework.entry.discount.DiscountContext;
import com.ec.framework.entry.discount.DiscountWrapper;
import com.ec.framework.entry.goods.GoodsItem;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DisCountCalc
 * @Description
 * @Author: LiHao
 * @Since 2023/12/22 19:19
 */
@Component("discount")
public class DiscountCalc extends AbstractCalculator<GoodsItem> {

    @Override
    public long calc(DiscountContext<GoodsItem> context, DiscountWrapper wrapper, Map<Long, GoodsItem> records, long precStagePrice, CalcStage curStage) {
        List<GoodsItem> goodsItems = Lists.newArrayList(context.getDiscountItemGroup().get(wrapper.getId()));
        Map<Long, List<GoodsItem>> skuIdGroup = (Map<Long, List<GoodsItem>>) context.getPreCompute().get("GroupBySkuIdPrecompute");
        long maxPrice = 0L;
        Collection<GoodsItem> group = CollectionUtils.EMPTY_COLLECTION;
        for (Collection<GoodsItem> c : skuIdGroup.values()) {
            long tmp = (long) c.iterator().next().getExtra().get(Constant.UPDATE_ABLE_PRICE);
            if (tmp > maxPrice) {
                maxPrice = tmp;
                group = c;
            }
        }
        long discount = (long) (maxPrice * group.size() * 0.2);
        /** 均摊 */
        for (GoodsItem item : group) {
            item.getExtra().put(Constant.UPDATE_ABLE_PRICE, (long)item.getExtra().get(Constant.UPDATE_ABLE_PRICE) - discount / goodsItems.size());
        }
        return precStagePrice - discount;
    }
}
