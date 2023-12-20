package com.ec.framework.utils;

/**
 * ID生成器，用于生成每次计算的商品id
 * @ClassName IDGennerator
 * @Description  非线程安全，不可在多线程共享，用于生成具体每一个商品的唯一id
 * @Author: LiHao
 * @Since 2023/12/16 21:48
 */
public class IDGenerator {

    public static IDGenerator getInstance() {
        return new IDGenerator();
    }

    private long currentId = 1;

    /**
     * 生成一个递增后的id
     * @return
     */
    public long nextId() {
        return currentId++;
    }
}
