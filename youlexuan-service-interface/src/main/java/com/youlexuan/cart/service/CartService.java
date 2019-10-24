package com.youlexuan.cart.service;

import com.youlexuan.pojogroup.Cart;

import java.util.List;

public interface CartService {

    /**
     * 添加商品到购物车 添加成功后会把购物车返回给浏览器
     * @param cartList
     * @param itemid
     * @param num
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemid, Integer num);


    List<Cart> findCartListFromRedis(String name);

    void saveCartListToRedis(String name, List<Cart> cartList);

    List<Cart> mergeCartList(List<Cart> cartList,List<Cart> cartList2);
}
