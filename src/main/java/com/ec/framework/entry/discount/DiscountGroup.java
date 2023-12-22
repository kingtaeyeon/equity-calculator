package com.ec.framework.entry.discount;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;

import java.util.*;

/**
 * 共享互斥组
 *
 * @ClassName DisCountFroup
 * @Description
 * @Author: LiHao
 * @Since 2023/12/22 21:01
 */
@Data
public class DiscountGroup {

    private String relation;

    private List<Item> items;

    /**
     * 根据用户可用的优惠，对组内信息进行过滤
     *
     * @param inMap
     * @return
     */
    public List<Item> filterItems(Map<String, Map<String, DiscountWrapper>> inMap) {

        if (CollectionUtils.isEmpty(items) || MapUtils.isEmpty(inMap)) {
            return null;
        }
        /** 构建item副本 */
        List<Item> itemsCopy = Lists.newArrayList(items);
        Iterator<Item> it = itemsCopy.iterator();
        while(it.hasNext()){
            Item item = it.next();
            if(!inMap.containsKey(item.getType())){
                it.remove();
                continue;
            }
            if(StringUtils.isNotBlank(item.getId())){
                //理论上m不可能为空
                Map<String,DiscountWrapper> m = inMap.get(item.getType());
                if(MapUtils.isNotEmpty(m)){
                    if(!m.containsKey(item.getId())){
                        it.remove();
                    }
                }
            }
        }
        return itemsCopy;

    }
}
