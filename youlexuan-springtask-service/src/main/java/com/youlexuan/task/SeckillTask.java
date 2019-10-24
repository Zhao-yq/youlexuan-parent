package com.youlexuan.task;

import com.youlexuan.CONSTANT;
import com.youlexuan.mapper.TbSeckillGoodsMapper;
import com.youlexuan.pojo.TbSeckillGoods;
import com.youlexuan.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SeckillTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @Scheduled  计划任务  执行的规则
     * 一秒钟执行一次
     */
    @Scheduled(cron = "* * * * * ?")
    public void refreshSeckillGoodsList(){
        TbSeckillGoodsExample exam = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = exam.createCriteria();
        criteria.andStatusEqualTo("1");//审核通过
        criteria.andStockCountGreaterThan(0);//库存大于0
        criteria.andStartTimeLessThanOrEqualTo(new Date());//开始时间小于等于当前时间
        criteria.andEndTimeGreaterThanOrEqualTo(new Date());//大于等于当前时间
        List<Long> idList =new ArrayList<>(redisTemplate.boundHashOps(CONSTANT.SECKILL_GOODS_LIST_KEY).keys());
        criteria.andIdNotIn(idList);//不能在这个列表里
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(exam);
        System.out.println("start:redis中数据个数:"+idList.size());
        for (TbSeckillGoods seckillGoods:seckillGoodsList){
            redisTemplate.boundHashOps(CONSTANT.SECKILL_GOODS_LIST_KEY).put(seckillGoods.getId(),seckillGoods);
        }
        System.out.println("end：redis中数据个数:"+redisTemplate.boundHashOps(CONSTANT.SECKILL_GOODS_LIST_KEY).keys().size());
    }


    /**
     * 过期商品列表移除
     * 结束时间在当前时间之前的
     */
    @Scheduled(cron = "* * * * * ?")
    public void removeSeckillGoodsList(){
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps(CONSTANT.SECKILL_GOODS_LIST_KEY).values();
        for (TbSeckillGoods seckillGoods:seckillGoodsList){
            if (seckillGoods.getEndTime().before(new Date())){
                //从缓存中移除
                redisTemplate.boundHashOps(CONSTANT.SECKILL_GOODS_LIST_KEY).delete(seckillGoods.getId());
                //持久化到数据库
                seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
            }
        }
    }
}
