package com.ec.framework.entry.goods;

import com.ec.framework.utils.IDGenerator;
import com.google.common.collect.Maps;
import lombok.Data;
import org.assertj.core.util.Lists;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/**
 * @ClassName GoodsItem
 * @Description
 * @Author: LiHao
 * @Since 2023/12/15 23:51
 */
@Data
public class GoodsItem extends GoodsInfo implements Serializable, Comparable<GoodsItem>, Cloneable {


    /**
     * 引擎计算时使用的id，每个单独商品一个
     */
    private Long calculateId;

    /**
     * 商品拓展属性
     */
    private Map<String, Object> extra = Maps.newHashMap();

    /**
     * 根据基本信息构造具体的计算商品，复制属性，以及单商品唯一id
     *
     * @param calculateId
     * @param goodsInfo
     */
    public GoodsItem(Long calculateId, GoodsInfo goodsInfo) {
        this.calculateId = calculateId;
        this.setGoodsId(goodsInfo.getGoodsId());
        this.setName(goodsInfo.getName());
        this.setSalePrice(goodsInfo.getSalePrice());
        this.setSkuId(goodsInfo.getSkuId());
        this.setGoodsExtra(goodsInfo.getGoodsExtra());

    }


    /**
     * 根据商品信息数量，生成计算时候需要的具体商品（单个）
     *
     * @param goodsInfo   商品信息
     * @param idGenerator id生成器
     * @param consumer    在创建商品时对extra的内容进行初始化
     */
    public static <T extends GoodsItem> List<T> generateItems(GoodsInfo goodsInfo, IDGenerator idGenerator, Consumer<T> consumer) {
        if (Objects.isNull(goodsInfo)) {
            return Lists.newArrayList();
        }
        List<T> goodsItems = Lists.newArrayList();
        for (int i = 0; i < goodsInfo.getNum(); i++) {
            T item = (T) new GoodsItem(idGenerator.nextId(), goodsInfo);
            consumer.accept(item);
            goodsItems.add(item);
        }
        return goodsItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GoodsItem goodsItem = (GoodsItem) o;
        return Objects.equals(calculateId, goodsItem.calculateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calculateId);
    }

    @Override
    public int compareTo(GoodsItem o) {
        return this.getCalculateId().compareTo(o.getCalculateId());
    }

    @Override
    protected GoodsItem clone() throws CloneNotSupportedException {
        return (GoodsItem) super.clone();
    }
}
