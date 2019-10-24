package com.youlexuan.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.youlexuan.pay.service.AlipayService;
import com.youlexuan.pojo.TbSeckillOrder;
import com.youlexuan.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private AlipayService alipayService;

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/createNative")
    private Map createNative() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder seckillOrder = seckillOrderService.searchSeckillOrderByUserId(userId);

        if (seckillOrder!=null){
            String out_trade_no = seckillOrder.getMoney()+ "";
            String total_amount = seckillOrder.getId()+"";
            return alipayService.createNative(out_trade_no, total_amount);
        }else {
            return new HashMap();
        }

    }

    /**
     * 查询支付状态
     * 1、多次查询  直至查询到状态为成功或者失败为止
     * 2、查询一定的次数也要退出死循环才行
     *
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Map queryPayStatus(String out_trade_no) {
        Map resultMap = null;
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        int x = 0;
        while (true) {
            //调用查询接口
            try {
                resultMap = alipayService.queryPayStatus(out_trade_no);
            } catch (Exception e1) {
                e1.printStackTrace();
                System.out.println("调用查询服务出错");
            }
            if (resultMap == null) {//出错
                resultMap.put("success",false);
                resultMap.put("message", "支付出错");
                break;
            }
            if (resultMap.get("tradestatus") != null && resultMap.get("tradestatus").equals("TRADE_SUCCESS")) {//如果成功
                resultMap.put("success", true);
                resultMap.put("message", "支付成功");
                //根据userId可以得到订单  等相关信息
                seckillOrderService.saveOrderFromRedisToDb(userId, (String) resultMap.get("trade_no"));
                break;
            }
            if (resultMap.get("tradestatus") != null && resultMap.get("tradestatus").equals("TRADE_CLOSED")) {//如果成功
                resultMap.put("success", false);
                resultMap.put("message", "未付款交易超时关闭，或支付完成后全额退款");
                break;
            }
            if (resultMap.get("tradestatus") != null && resultMap.get("tradestatus").equals("TRADE_FINISHED")) {//如果成功
                resultMap.put("success", true);
                resultMap.put(true, "交易结束，不可退款");
                seckillOrderService.saveOrderFromRedisToDb(userId, (String) resultMap.get("trade_no"));
                break;
            }
            //休息一会再调用
            try {
                Thread.sleep(3000);//间隔三秒
            } catch (InterruptedException e) {
//                e.printStackTrace();
                System.out.println("超时");
            }
            //为了不让循环无休止地运行，我们定义一个循环变量，如果这个变量超过了这个值则退出循环，设置时间为5分钟
            x++;
            if (x >= 3) {
                resultMap.put("success",false);
                resultMap.put("tradestatus","timeout");
                resultMap.put("message", "二维码超时");
                // 还原缓存 调用阿里关闭交易的接口
                //redis中还原
                seckillOrderService.resetOrderFromRedis(userId);
                //第三方接口
                alipayService.closePay(out_trade_no);
                break;
            }
        }
        return resultMap;
    }
}
