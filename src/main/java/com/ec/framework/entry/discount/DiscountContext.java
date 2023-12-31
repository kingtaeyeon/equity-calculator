package com.ec.framework.entry.discount;

import com.ec.framework.entry.calc.CalcResult;
import com.ec.framework.entry.goods.GoodsItem;
import com.ec.framework.precompute.PreCompute;
import com.ec.framework.precompute.PreComputeHolder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName DiscountContext
 * @Description
 * @Author: LiHao
 * @Since 2023/12/17 15:59
 */
@Data
public class DiscountContext<T extends GoodsItem> implements Serializable {

    private DiscountContext() {

    }

    /**
     * 原始价格
     */
    private long originalPrice;


    /**
     * 优惠的计算结果
     */
    private CalcResult calcResult;

    /**
     * 当前订单参与计算的商品
     */
    private List<T> goodsItems;

    /**
     * 当前订单可用优惠的配置
     */
    private List<DiscountWrapper> discountWrappers;

    /**
     * 优惠维度进行商品分组，这样每个计算器只读自己相关的商品
     */
    private Map<String, List<T>> discountItemGroup;

    /**
     * 记录享受过优惠的商品，key是calculateId
     */
    private Map<Long, T> records;

    /**
     * 存储预计算的结果
     */
    private Map<String, Object> preCompute;

    /**
     * 拓展字段
     */
    private Map<String, Object> extra;

    /**
     * 构建一个上下文
     *
     * @param originalPrice    订单原始价格
     * @param goodsItems       可用商品列表
     * @param discountWrappers 可用优惠列表
     * @param <T>
     * @return
     */
    public static <T extends GoodsItem> DiscountContext<T> create(long originalPrice, List<T> goodsItems, List<DiscountWrapper> discountWrappers) {

        DiscountContext<T> context = new DiscountContext<>();
        context.originalPrice = originalPrice;
        context.records = Maps.newHashMap();
        context.discountWrappers = discountWrappers;
        context.discountItemGroup = Maps.newHashMap();
        context.calcResult = CalcResult.create(discountWrappers.size());
        context.calcResult.setFinalPrice(originalPrice);
        context.goodsItems = goodsItems;
        context.discountWrappers = discountWrappers;
        context.preCompute = Maps.newHashMap();

        /**
         *预处理优惠
         */
        for (DiscountWrapper wrapper : discountWrappers) {
            initDiscount(context, wrapper);
        }

        return context;
    }

    /**
     * 对某个优惠进行初始化工作，包扩商品的筛选，预计算逻辑
     *
     * @param context 上下文
     * @param wrapper 当前优惠的信息
     * @param <T>
     */
    private static <T extends GoodsItem> void initDiscount(DiscountContext<T> context, DiscountWrapper wrapper) {
        DiscountConfig config = wrapper.getDiscountConfig();
        List<T> list = Lists.newArrayList(context.goodsItems);
        if (0 == config.getGoodsType()) {
            /* 所有商品都可参与 */
            list = list.stream().sorted().collect(Collectors.toList());

        } else if (1 == config.getGoodsType()) {
            /* 指定某些商品参与 */
            if (0 == config.getItemIdType()) {
                list = list.stream().filter(x -> config.getItemIds()
                        .contains(x.getGoodsId())).sorted().collect(Collectors.toList());

            } else if (1 == config.getItemIdType()) {
                list = list.stream().filter(x -> config.getItemIds()
                        .contains(x.getSkuId())).sorted().collect(Collectors.toList());
            } else {
                /* 取交集 */
                list = list.stream().filter(x -> CollectionUtils.intersection(
                        config.getItemIds(), x.getCategoryIds()).size() > 0).sorted().collect(Collectors.toList());
            }
        } else {
            /* 指定某些商品不参与 */
            if (0 == config.getItemIdType()) {
                list = list.stream().filter(x -> !config.getItemIds()
                        .contains(x.getGoodsId())).sorted().collect(Collectors.toList());
            } else if (1 == config.getItemIdType()) {
                list = list.stream().filter(x -> !config.getItemIds()
                        .contains(x.getSkuId())).sorted().collect(Collectors.toList());
            } else {
                list = list.stream().filter(x -> CollectionUtils.intersection(
                        config.getItemIds(), x.getCategoryIds()).size() == 0).sorted().collect(Collectors.toList());
            }
        }
        context.discountItemGroup.put(wrapper.getId(), list);
        runPreCompute(context, wrapper, list);

    }

    /**
     * 预计算
     *
     * @param context
     * @param wrapper
     * @param list
     * @param <T>
     */
    private static <T extends GoodsItem> void runPreCompute(DiscountContext<T> context, DiscountWrapper wrapper, List<T> list) {
        list = Lists.newArrayList(list);
        for (PreCompute<T> p : PreComputeHolder.COMPUTES) {
            if (p.matchTypes().contains(wrapper.getType())) {
                p.preComputeItems(list, wrapper, context.preCompute);
            }
        }

    }
}




