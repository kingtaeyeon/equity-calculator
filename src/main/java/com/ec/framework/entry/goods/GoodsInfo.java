package com.ec.framework.entry.goods;

import com.google.common.collect.Maps;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName GoodsInfo
 * @Description
 * @Author: LiHao
 * @Since 2023/12/15 23:34
 */
@Data
public class GoodsInfo implements Serializable, Cloneable {

    private Long goodsId;

    private Long skuId;

    /**
     * 商品分类id，一个商品可能出现在多个分类中
     **/
    private List<Long> categoryIds;

    /**
     * 商品购买数量
     **/
    private int num;

    /**
     * 商品价格，单位：分
     */
    private long salePrice;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品拓展属性
     */
    private Map<String, Object> goodsExtra = Maps.newHashMap();

    public static GoodsInfo of(Long goodsId, Long skuId, List<Long> categoryIds, int num, long salePrice, String name, Map<String, Object> goodsExtra) {
        GoodsInfo goodsInfo = new GoodsInfo();
        goodsInfo.setGoodsId(goodsId);
        goodsInfo.setCategoryIds(categoryIds);
        goodsInfo.setNum(num);
        goodsInfo.setSkuId(skuId);
        goodsInfo.setSalePrice(salePrice);
        goodsInfo.setName(name);
        goodsInfo.setGoodsExtra(goodsExtra);
        return goodsInfo;
    }

    @Override
    protected GoodsInfo clone() throws CloneNotSupportedException {
        return (GoodsInfo) super.clone();
    }
}
