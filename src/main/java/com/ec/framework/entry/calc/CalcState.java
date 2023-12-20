package com.ec.framework.entry.calc;

import com.ec.framework.core.Calculator;
import com.ec.framework.entry.goods.GoodsItem;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 保存点，用于缓存预计算到第3个节点时将所有状态写入CalcState
 *
 * @ClassName CalcState
 * @Description
 * @Author: LiHao
 * @Since 2023/12/19 18:31
 */
@Data
public class CalcState<T extends GoodsItem> implements Serializable {

    /**
     * 截止到保存点计算的最终优惠价格
     */
    private long curPrice;

    /**
     * 截止到保存点计算的calcStage
     */
    private CalcStage[] calcStages;

    /**
     * 截止到保存点的已计算商品列表
     */
    private Map<Long, T> records;

    /**
     * 截止到保存点的拓展信息
     */
    private Map<String, Object> extra;

}
