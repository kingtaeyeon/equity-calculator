### 背景介绍
在电商领域中，运营策略往往能直接影响到平台的收益，最常见的运营策略之一就是赠送优惠券了。在用户选完商品后，电商系统需要对用户所拥有且能使用的优惠券来计算出当前结算的最优惠金额，而有些类型的优惠券又是可叠加计算的，也就是说优惠券的计算顺序不一样，最终的计算出的实付金额也就不一样。

<img src="./MATERIAL/rela.png" width="60%">

假如某用户有7种不同类型的优惠券（下文简称权益），那么就需要计算7! * 7也就是35280次。
这个计算量是非常庞大的。因此，本系统实现了很多新颖的设计来求出用户优惠券叠加计算的最优解并且保证了接口的响应时间。
