package com.ec.framework.demo.precompute;

import com.ec.framework.entry.discount.DiscountWrapper;
import com.ec.framework.entry.goods.GoodsInfo;
import com.ec.framework.entry.goods.GoodsItem;
import com.ec.framework.precompute.PreCompute;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName PreGroupBy
 * @Description
 * @Author: LiHao
 * @Since 2023/12/22 18:57
 */
public class PreGroupBy implements PreCompute<GoodsItem> {

    @Override
    public Set<String> matchTypes() {
        return Sets.newHashSet("discount");
    }

    @Override
    public void preComputeItems(List<GoodsItem> items, DiscountWrapper wrapper, Map<String, Object> preCompute) {
        preCompute.put("GroupBySkuIdPrecompute", items.stream().collect(Collectors.groupingBy(GoodsInfo::getSkuId, Collectors.collectingAndThen(Collectors.toList(),
                e -> e.stream().sorted().collect(Collectors.toList())))));
    }
}
