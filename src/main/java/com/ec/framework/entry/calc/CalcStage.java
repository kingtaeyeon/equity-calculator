package com.ec.framework.entry.calc;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 单个具体优惠计算明细，可以当成日志使用，用于计算过程的追溯
 * @ClassName CalcStage
 * @Description
 * @Author: LiHao
 * @Since 2023/12/17 17:01
 */
@Data
public class CalcStage implements Serializable {

    /**
     * 优惠前价格
     */
    private long beforeCalcPrice;

    /**
     * 优惠后价格
     */
    private long afterCalcPrice;

    /**
     * 优惠类型
     */
    private String stageType;

    /**
     * 优惠索引
     */
    private byte index;

    /**
     * 拓展属性
     */
    private Map<String, Object> extra;
}
