package com.cxf.hotel.constants;

public class MqConstans {
    public static final String DELAYED_EXCHANGE="delayed.exchange";
    public static final String DELAYED_QUEUE="delayed.queue";
    public static final String DELAYED_ROUTING_KEY="delayed.routingkey";


    /**
     * 交换机
     */
    public final static String HOTEL_EXCHANGE="hotel.topic";
    /**
     * 监听新增和修改的队列
     */
    public final static String HOTEL_INSERT_QUEUE="hotel.insert.queue";
    /**
     * 监听删除的队列
     */
    public final static String HOTEL_DELETE_QUEUE="hotel.delete.queue";
    /**
     * 新增或修改的RoutingKey
     */
    public final static String HOTEL_INSERT_KEY="hotel.insert";
    /**
     * 删除的RoutingKey
     */
    public final static String HOTEL_DELETE_KEY="hotel.delete";

    //普通交换机名称
    public static final String MY_EXCHANGE = "MY";
    //死信交换机名称
    public static final String MY_DEAD_LETTER_EXCHANGE = "MY_DEAD_LETTER";
    //普通队列名称
    public static final String QUEUE_MY = "Q_MY";
    //死信队列名称
    public static final String DEAD_LETTER_QUEUE_MY = "DEAD_LETTER_Q_MY";
    //普通RoutingKey名称
    public static final String MY_ROUTING_KEY = "ROUTING_MY";
    //死信RoutingKey名称
    public static final String DEAD_LETTER_ROUTING_KEY = "DEAD_LETTER_ROUTING_MY";
}
