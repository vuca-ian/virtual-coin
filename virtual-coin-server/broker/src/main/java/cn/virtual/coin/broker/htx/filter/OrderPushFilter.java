package cn.virtual.coin.broker.htx.filter;//package com.bitiany.scs.filter;
//
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.bitiany.scs.common.utils.StringUtils;
//import com.bitiany.scs.websocket.WebSocketConnection;
//import com.bitiany.scs.websocket.chain.Filter;
//import com.bitiany.scs.websocket.chain.FilterChain;
//import com.bitiany.scs.websocket.common.WebSocketConstants;
//import com.bitiany.scs.websocket.model.OrderStatus;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.context.ApplicationEventPublisherAware;
//
///**
// * @author gdyang
// * @since 2021/8/2 4:45 下午
// */
//@Slf4j
//public class OrderPushFilter implements Filter<JSONObject>, ApplicationEventPublisherAware {
//    @Autowired
//    private EntrustOrderService entrustOrderService;
//
//    private ApplicationEventPublisher applicationEventPublisher;
//
//
//    @Override
//    public void doFilter(JSONObject data, WebSocketConnection connection, FilterChain chain) {
//        if(data.containsKey(WebSocketConstants.ACTION) && data.getString(WebSocketConstants.ACTION).equals(WebSocketConstants.PUSH)){
//            Order order = data.getObject("data", Order.class);
//            log.info("received order info {}",order);
//            EntrustOrder entrustOrder = entrustOrderService.selectOne(Wrappers.<EntrustOrder>lambdaQuery().eq(EntrustOrder::getOrderId, order.getClientOrderId()));
//            if(null == entrustOrder){
//                log.warn("未查到计划委托：{}",  order.getClientOrderId());
//                return;
//            }
//            entrustOrder.setOrderStatus(order.getOrderStatus());
//            entrustOrderService.update(entrustOrder);
//            order.setAccountId(entrustOrder.getAccountId());
//            OrderStatus status = OrderStatus.accept(order.getOrderStatus());
//            switch (status){
//                case canceled:
//                    break;
//                case partialFilled:
//                case filled:
//                    //事件驱动 创建委托  止损单
//                    createStopLossEntrustOrder(order);
//                    break;
//                default:
//                    log.warn("No support order status! {}", order.getClientOrderId());
//            }
//        }else{
//            chain.doFilter(data, connection);
//        }
//    }
//
//    private void createStopLossEntrustOrder(Order order){
//        if(StringUtils.isNotEmpty(order.getType()) && order.getType().contains("buy")) {
//            applicationEventPublisher.publishEvent(new CloseLossOrderEvent(order));
//        }
//    }
//
//    @Override
//    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
//        this.applicationEventPublisher = applicationEventPublisher;
//    }
//}
