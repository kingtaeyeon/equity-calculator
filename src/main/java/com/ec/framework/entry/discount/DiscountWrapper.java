package com.ec.framework.entry.discount;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 优惠类
 *
 * @ClassName DiscountWrapper
 * @Description
 * @Author: LiHao
 * @Since 2023/12/17 17:16
 */
@Data
public class DiscountWrapper implements Serializable, Comparable<DiscountWrapper> {

    /**
     * 优惠的具体类型，CalculatorRouter会根据这个type在Calculator的实现类中扫描
     */
    private String type;

    /**
     * 优惠的ID
     */
    private String id;

    /**
     * 优惠的名称
     */
    private String name;

    /**
     * 是否是必须使用的优惠
     */
    private boolean mustUse;

    /**
     * 优惠的配置信息
     */
    private DiscountConfig discountConfig;

    public static DiscountWrapper of(String type, String id, String name, boolean mustUse, DiscountConfig discountConfig) {
        DiscountWrapper wrapper = new DiscountWrapper();
        wrapper.setType(type);
        wrapper.setId(id);
        wrapper.setName(name);
        wrapper.setMustUse(mustUse);
        wrapper.setDiscountConfig(discountConfig);
        return wrapper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscountWrapper that = (DiscountWrapper) o;
        return type.equals(that.type) && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }

    @Override
    public int compareTo(DiscountWrapper o) {
        return (this.type + this.id).compareTo(o.getType() + o.getId());
    }
}
