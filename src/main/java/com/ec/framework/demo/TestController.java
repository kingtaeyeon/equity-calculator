package com.ec.framework.demo;

import com.ec.framework.core.CalculatorRouter;
import com.ec.framework.demo.constant.Constant;
import com.ec.framework.entry.calc.CalcStage;
import com.ec.framework.entry.discount.*;
import com.ec.framework.entry.goods.GoodsInfo;
import com.ec.framework.entry.goods.GoodsItem;
import com.ec.framework.enums.GroupRelationEnum;
import com.ec.framework.utils.DiscountGroupUtil;
import com.ec.framework.utils.IdGenerator;
import com.ec.framework.utils.LimitingUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName Controller
 * @Description
 * @Author: LiHao
 * @Since 2023/12/22 20:38
 */
@Controller
public class TestController {

    private final CalculatorRouter calculatorRouter;

    public TestController(CalculatorRouter calculatorRouter) {
        this.calculatorRouter = calculatorRouter;
    }

    @RequestMapping("test")
    @ResponseBody
    public Object test() {
        /** mock视频数据 */
        List<GoodsItem> items = mockItems();
        //mock组关系并转化为共享组
        List<Pair<Set<DiscountWrapper>,Set<DiscountWrapper>>> pairs = transform(mockGroups());
        //全局最优计算过程
        List<CalcStage> globalStages=Lists.newArrayList();
        int count = 0;
        //订单总金额
        long totalPrice = items.stream().mapToLong(GoodsInfo::getSalePrice).sum();
        long globalPrice = totalPrice;
        //构建计算流
        Flowable flowable = (Flowable) new Flowable().build(calculatorRouter);
        for(Pair<Set<DiscountWrapper>,Set<DiscountWrapper>> set:pairs) {
            //统计算力
            count += LimitingUtil.count(set.getLeft().size());
            if(count>100000){
                break;
            }
            List<DiscountWrapper> wrappers = Lists.newArrayList(set.getLeft());
            DiscountContext<GoodsItem> ctx = DiscountContext.create(totalPrice, Lists.newArrayList(items), wrappers);
            flowable.perm(ctx);
            if(ctx.getCalcResult().getFinalPrice() < globalPrice) {
                globalStages = Arrays.asList(ctx.getCalcResult().getStages());
                globalPrice = ctx.getCalcResult().getFinalPrice();
            }
        }
        return Pair.of(globalPrice,globalStages);
    }

    private List<GoodsItem> mockItems(){
        IdGenerator idGenerator = IdGenerator.getInstance();
        GoodsInfo goodsInfo = GoodsInfo.of(1001L,2001L,null,4,20 * 100,"产品1",null);
        GoodsInfo goodsInfo2 = GoodsInfo.of(1001L,2002L,null,2,10 * 100,"产品1",null);
        List<GoodsItem> items = GoodsItem.generateItems(goodsInfo,idGenerator,x->x.getExtra().put(Constant.UPDATE_ABLE_PRICE,x.getSalePrice()));
        items.addAll(GoodsItem.generateItems(goodsInfo2,idGenerator,x->x.getExtra().put(Constant.UPDATE_ABLE_PRICE,x.getSalePrice())));
        return items;
    }

    private List<List<DiscountGroup>> mockGroups(){
        List<List<DiscountGroup>> groups = Lists.newArrayList();
        DiscountGroup group = new DiscountGroup();
        group.setRelation(GroupRelationEnum.SHARE.getType());
        group.setItems(Lists.newArrayList(new Item("discount","1"),new Item("full","2"),new Item("gift","3")));
        groups.add(Lists.newArrayList(group));
        return groups;
    }

    private List<Pair<Set<DiscountWrapper>,Set<DiscountWrapper>>> transform(List<List<DiscountGroup>> groups){
        List<DiscountWrapper> wrapperList = Lists.newArrayList(
                DiscountWrapper.of("discount", "1", "折扣", false, new DiscountConfig()),
                DiscountWrapper.of("full", "2", "满减", false, new DiscountConfig())
        );
        Map<String, Map<String,DiscountWrapper>> inMap = wrapperList.stream().collect(Collectors.toMap(DiscountWrapper::getType, x-> ImmutableMap.of(x.getId(),x)));
        return DiscountGroupUtil.transform(groups,inMap);
    }
}
