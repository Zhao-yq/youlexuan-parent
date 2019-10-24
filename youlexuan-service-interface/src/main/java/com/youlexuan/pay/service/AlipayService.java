package com.youlexuan.pay.service;

import java.util.Map;

/**
 * 支付宝支付接口
 */
public interface AlipayService {

    /**
     * 生成支付宝支付二维码
     * @param out_trade_no 订单号
     * @param total_amount 金额（分）
     * @return
     */
    Map createNative(String out_trade_no, String total_amount);

    /**
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    Map queryPayStatus(String out_trade_no);

    Map closePay(String out_trade_no);
}
