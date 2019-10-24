package com.youlexuan.search.listener;

import com.alibaba.fastjson.JSON;
import com.youlexuan.pojo.TbItem;
import com.youlexuan.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

/**
 * 监听器
 */
public class SolrAddMessageListenner implements MessageListener {


    @Autowired
    private ItemSearchService searchService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage)message;
        try {
            //json字符串
            String jsonStr = textMessage.getText();
            List<TbItem> itemList = JSON.parseArray(jsonStr, TbItem.class);
            //加工spceMap
            for(TbItem item:itemList){
                //转换成map类型
               Map specMap =  JSON.parseObject(item.getSpec());
               item.setSpecMap(specMap);
            }
            //导包
            searchService.importItemList(itemList);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
