package com.ec.framework.entry.discount;

import lombok.*;

/**
 * 共享互斥关系中的元素
 *
 * @ClassName Item
 * @Description
 * @Author: LiHao
 * @Since 2023/12/22 21:02
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Item {

    /**
     * 优惠类型，和DiscountWrapper中的type保持一致
     */
    private String type;

    /**
     * 优惠的ID，和DiscountWrapper中的id保持一致
     */
    private String id;


}

