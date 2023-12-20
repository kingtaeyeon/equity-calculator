package com.ec.framework.core;

import com.ec.framework.entry.calc.CalcResult;
import com.ec.framework.entry.calc.CalcStage;
import com.ec.framework.entry.calc.CalcState;
import com.ec.framework.entry.discount.DiscountContext;
import com.ec.framework.entry.discount.DiscountWrapper;
import com.ec.framework.entry.goods.GoodsItem;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import org.assertj.core.util.Sets;
import org.apache.commons.collections4.CollectionUtils;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @ClassName Permutation
 * @Description
 * @Author: LiHao
 * @Since 2023/12/5 19:19
 */
@SuppressWarnings("all")
public abstract class Permutation<T extends GoodsItem> {

    /**
     * 根据注解路由到目标Calculator
     */
    private CalculatorRouter calculatorRouter;

    /**
     * 上下文
     */
    private DiscountContext<T> context;

    /**
     * 必须使用的优惠，比如用户手动选择的优惠
     */
    private final Set<Byte> mustUseSet = Sets.newHashSet();

    /**
     * 缓存，key是calcKey生成的数字，value是保存点
     */
    private final Map<Integer, CalcState<T>> cache = Maps.newHashMap();

    /**
     * 1-SOUPPORTEDSIZE之间所有排列组合的记录
     */
    private final static Map<Integer, Collection<List<Byte>>> PERMUTATIONS = Maps.newHashMap();

    /**
     * 将calculatorRouter路由绑定到当前Permutation
     *
     * @param calculatorRouter 路由器
     * @return
     */
    public Permutation<T> build(CalculatorRouter calculatorRouter) {
        this.calculatorRouter = calculatorRouter;
        return this;
    }

    /**
     * 最大支持的共享组长度
     */
    public final static int SUPPORTEDSIZE = 7;

    static {
        /** 前置计算，1-SUPPORTEDSIZE之间所有的排列组合 **/
        for (byte i = 1; i <= SUPPORTEDSIZE; i++) {
            PERMUTATIONS.put((int) i, Collections2.permutations(IntStream.range(0, i)
                    .boxed().map(x -> (byte) x.intValue()).collect(Collectors.toList())));
        }
    }

    /**
     * 计算用户选择的优惠组合是否有可行解
     * @param context 上下文
     * @return
     */
    public boolean findSolution(DiscountContext<T> context) {
        int size = context.getDiscountWrappers().size();
        this.context = context;
        if (size == 0) {
            return false;
        }
        Collection<List<Byte>> list = PERMUTATIONS.get(size);
        for (List<Byte> a : list) {
            if (findSolution(context, a)) {
                return true;
            }
        }
        cache.clear();
        mustUseSet.clear();
        return false;
    }

    /**
     * 计算最优解
     * @param context 上下文
     */
    public void prem(DiscountContext<T> context) {
        int size = context.getDiscountWrappers().size();
        this.context = context;
        loadMustUseDiscount();
        if (size == 0) {
            return;
        }
        Collection<List<Byte>> list = PERMUTATIONS.get(size);
        for (List<Byte> a : list) {
            boolean isBetter = executeCalc(context, a);
            if (isBetter) {
                /** 若出现比当前结果更优的结果则替换 */
                updateRecord(this.context.getCalcResult());
            }
        }
        cache.clear();
        mustUseSet.clear();
    }

    /**
     * 若出现比当前结果更优的结果则替换
     * @param result 当前的计算结果
     */
    private void updateRecord(CalcResult result) {
        result.setFinalPrice(result.getCurFinalPrice());
        System.arraycopy(result.getCurStages(), 0, result.getStages(), 0, result.getStages().length);
    }

    /**
     * 根据数组顺序执行计算器
     *
     * @param context 上下文
     * @param a       排列组合
     */
    private boolean executeCalc(DiscountContext<T> context, List<Byte> a) {
        Integer key = calcKey(a);
        boolean canOptimize = enableOptimize(a) && cache.containsKey(key);
        initInner(canOptimize, key);
        for (int i = canOptimize ? 3 : 0; i < a.size(); i++) {
            DiscountWrapper wrapper = context.getDiscountWrappers().get(a.get(i));
            Calculator<T> calculator = (Calculator<T>) calculatorRouter.getService(wrapper.getType());
            /** 路由目标的计算器实现 */
            if (canOptimize && checkIfWakeUpJump(context.getDiscountWrappers().get(a.get(2)), wrapper)) {
                /** 还原保存点后，比较保存点的最后一个优惠节点和当前优惠的优先级，如不符合则跳出 */
                break;
            }
            if (Objects.nonNull(calculator)) {
                if (!calcInner(calculator, wrapper, a, i)) {
                    return false;
                }
                /** 优惠长度为 5、6、7时将开启优化，只缓存走到第3个节点的部分 */
                cacheSnapshot(a, i, key);
            }
        }

        long curPrice = context.getCalcResult().getCurPrice();
        context.getCalcResult().setCurFinalPrice(curPrice);
        CalcStage[] curStages = context.getCalcResult().getCurStages();
        return curPrice < context.getCalcResult().getFinalPrice() && (CollectionUtils.isEmpty(this.mustUseSet) || isMustUse(curStages, this.mustUseSet))
                /** 相同最优解处理逻辑 */
                || curPrice == context.getCalcResult().getFinalPrice() && (CollectionUtils.isEmpty(this.mustUseSet) || isMustUse(curStages, this.mustUseSet)) && sameOptimumCondition(curStages);

    }

    /**
     * 全排列计算后判断位置是否匹配
     *
     * @param curStages  当前statge数组
     * @param mustUseSet 必须使用的优惠索引
     * @return
     */
    private boolean isMustUse(CalcStage[] stages, Set<Byte> mustUseSet) {
        int c = 0;
        for (CalcStage stage : stages) {
            if (Objects.isNull(stage)) {
                continue;
            }
            if (mustUseSet.contains(stage.getIndex())) {
                c++;
            }
        }
        return c == mustUseSet.size();
    }

    private void loadMustUseDiscount() {
        for (int i = 0; i < context.getDiscountWrappers().size(); i++) {
            if (context.getDiscountWrappers().get(i).isMustUse()) {
                this.mustUseSet.add((byte) i);
            }
        }
    }

    /**
     * 根据byte数组生成唯一数字作为Map的key，避免字符串拼接等低效操作
     *
     * @param a
     * @return
     */
    private static Integer calcKey(List<Byte> a) {
        return a.size() >= 3 ? (a.get(0) << 6) + (a.get(1) << 3) + a.get(2) : 0;
    }

    /**
     * 找到可行解，这种情况下list中所有优惠必须被使用
     *
     * @param context 上下文
     * @param a       当前计算的排列
     * @return
     */
    private boolean findSolution(DiscountContext<T> context, List<Byte> a) {
        Integer key = calcKey(a);
        boolean canOptimeze = enableOptimize(a) && cache.containsKey(key);
        initInner(canOptimeze, key);
        for (int i = canOptimeze ? 3 : 0; i < a.size(); i++) {
            DiscountWrapper wrapper = context.getDiscountWrappers().get(a.get(i));
            Calculator<T> calculator = (Calculator<T>) calculatorRouter.getService(wrapper.getType());
            /** 路由目标的计算器实现 */
            if (canOptimeze && checkIfWakeUpJump(context.getDiscountWrappers().get(a.get(2)), wrapper)) {
                /** 还原保存点后，比较保存点的最后一个优惠节点和当前优惠的优先级，如果不符合则跳出 */
                break;
            }
            if (Objects.nonNull(calculator)) {
                /** 执行计算器 */
                if (!calcInner(calculator, wrapper, a, i)) {
                    return false;
                }
                CalcStage curStage = context.getCalcResult().getCurStages()[i];
                if (Objects.isNull(curStage)) {
                    /** 执行档期啊你排列组合，若存在未使用的优惠则证明当前排列不可行 */
                    return false;
                }
                /** 优惠长度为5、6、7时将开启优化，只缓存走到第3个节点的部分 */
                cacheSnapshot(a, i, key);
            }
        }

        return true;
    }

    private void cacheSnapshot(List<Byte> a, int i, Integer key) {
        if (enableOptimize(a) && i == 2 && !cache.containsKey(key)) {
            cache.put(key, makeSnapshot(context.getGoodsItems()));
        }
    }

    /**
     * 构建保存点
     *
     * @param goodsItems
     * @return
     */
    private CalcState<T> makeSnapshot(List<T> goodsItems) {
        CalcState<T> calcState = new CalcState<>();
        calcState.setCurPrice(context.getCalcResult().getCurPrice());
        calcState.setCalcStages(context.getCalcResult().getStages());
        calcState.setRecords(copyRecord(context.getRecords()));
        makeSnapshot(context, calcState);
        return calcState;
    }


    private boolean calcInner(Calculator<T> calculator, DiscountWrapper wrapper, List<Byte> a, int i) {
        long price = calculator.calcWarp(context, wrapper, context.getRecords(), a.get(i), i);
        if (price < 0) {
            return false;
        }
        if (i < a.size() - 1) {
            int order = wrapper.getDiscountConfig().getCalculateGroup();
            int nextOrder = context.getDiscountWrappers().get(a.get(i + 1)).getDiscountConfig().getCalculateGroup();
            if (order > nextOrder) {
                /** 优先级不符合则跳出 */
                return false;
            }
        }
        return true;
    }

    /**
     * 还原保存点后，比较保存点的最后一个优惠节点和当前优惠的优先级，如果不符合则跳出
     *
     * @param cacheWrapper 保存点的最后一个优惠节点
     * @param wrapper      当前优惠节点
     * @return
     */
    private boolean checkIfWakeUpJump(DiscountWrapper cacheWrapper, DiscountWrapper wrapper) {
        return cacheWrapper.getDiscountConfig().getCalculateGroup() > wrapper.getDiscountConfig().getCalculateGroup();

    }

    private void initInner(boolean canOptimeze, Integer key) {
        if (canOptimeze) {
            /** 若可优化，则还原之前的保存点 */
            backToSnapshot(key);
        } else {
            /** 若不可优化，则进行初始化操作 */
            initContext();
        }
    }

    /**
     * 初始化上下文，清理上一个排列留下的脏数据
     */
    private void initContext() {
        context.getCalcResult().setCurPrice(context.getOriginalPrice());
        CalcStage[] curStages = context.getCalcResult().getCurStages();
        /** 每次排列计算前初始化curStage为null */
        Arrays.fill(curStages, null);
        /** 每次排列计算前erset userdList中的单品价格，并清空 */
        context.getGoodsItems().forEach(this::resetItems);
        /** 清空黑名单 */
        context.getRecords().clear();
        /** 初始化context */
        resetContext(context);
    }

    /**
     * 返回保存点
     *
     * @param key 缓存的key
     */
    private void backToSnapshot(Integer key) {
        CalcState<T> state = (CalcState<T>) cache.get(key);
        context.getCalcResult().setCurPrice(state.getCurPrice());
        context.getCalcResult().setCurStages(state.getCalcStages());
        context.setRecords(copyRecord(state.getRecords()));
        backToSnapshot(context, state);

    }

    /**
     * context参数的重置逻辑，交给业务来实现*
     * @param context 上下文
     */
    protected abstract void resetContext(DiscountContext<T> context);

    /**
     * 业务将状态记录到保存点
     *
     * @param context   上下文
     * @param calcState 保存点对象
     */
    protected abstract void makeSnapshot(DiscountContext<T> context, CalcState<T> calcState);

    /**
     * 相同最优解的处理逻辑，交给业务来实现
     *
     * @param stages 最终的stage数组
     * @return
     */
    protected abstract boolean sameOptimumCondition(CalcStage[] stages);

    /**
     * 复制享受过优惠的商品记录，只用到calculateId，因此不对对象进行clone
     *
     * @param records 享受过优惠的商品记录
     * @return
     */
    private Map<Long, T> copyRecord(Map<Long, T> records) {
        return Maps.newHashMap(records);
    }

    /**
     * 开启缓存的条件，如 a.size > 4
     *
     * @param a 优惠排列
     * @return
     */
    protected abstract boolean enableOptimize(List<Byte> a);

    /**
     * 业务返回保存点状态
     *
     * @param context
     * @param state
     */
    protected abstract void backToSnapshot(DiscountContext<T> context, CalcState<T> state);

    /**
     * 商品参数的重置逻辑，交给业务来实现
     * @param item 单品
     */
    protected abstract void resetItems(T item);
}
