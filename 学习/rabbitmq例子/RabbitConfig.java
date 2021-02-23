package com.sundear.admin.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    //---------------------------------直流交换机------------------------------------------
    public static final String exchange="directExchange";
    public static final String queue="directQueue";
    public static final String key="directKey";

    @Bean
    public Queue directQueue(){
        /**
         * durable：是否持久化，默认false，持久化队列会被存储到磁盘上，消息代理重启时依然存在；暂存队列当前连接有效
         * exclusive：默认false，只能被当前创建的连接使用，连接断开队列即被删除，此优先级高于durable
         * autoDelete：默认false，没有生产者和消费者使用此队列，将会被删除
         * 一般就只设置队列持久化，其余默认false
         */
        return new Queue(queue,true);
    }
    @Bean
    DirectExchange directExchange(){
        //durable+autoDelete
        return new DirectExchange(exchange,true,false);
    }
    //将交换机和队列进行绑定，并设置匹配键：key=directKey
    @Bean
    Binding bindDirect(){
        return BindingBuilder.bind(directQueue()).to(directExchange()).with(key);
    }


    //-----------------------------主题交换机-------------------------------------
    public static final String topic_exchange="topicExchange";
    public static final String man="topic.man";
    public static final String woman="topic.woman";

    @Bean
    public Queue man(){
        return new Queue(man,true);
    }
    @Bean
    public Queue woman(){
        return new Queue(woman,true);
    }
    @Bean
    TopicExchange topicExchange(){
        return new TopicExchange(topic_exchange,true,false);
    }
    @Bean
    Binding bindTopic(){
        return BindingBuilder.bind(man()).to(topicExchange()).with(man);
    }
    @Bean
    Binding bindTopicRegex(){
        return BindingBuilder.bind(woman()).to(topicExchange()).with("topic.#");
    }


    @Autowired
    private CachingConnectionFactory connectionFactory;
    //---------------------------------消息确认回调函数------------------------------------------
    @Bean
    public RabbitTemplate createRabbitTemplate(){
        RabbitTemplate rabbitTemplate=new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback(){

            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("ConfirmCallback:     "+"相关数据："+correlationData);
                System.out.println("ConfirmCallback:     "+"确认情况："+ack);
                System.out.println("ConfirmCallback:     "+"原因："+cause);
            }
        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("ReturnCallback:     "+"消息："+message);
                System.out.println("ReturnCallback:     "+"回应码："+replyCode);
                System.out.println("ReturnCallback:     "+"回应信息："+replyText);
                System.out.println("ReturnCallback:     "+"交换机："+exchange);
                System.out.println("ReturnCallback:     "+"路由键："+routingKey);
            }
        });
        return rabbitTemplate;
    }

    @Autowired
    private RabbitMqReceiver rabbitMqReceiver;

    /**
     * queue listener  观察 监听模式
     * 当有消息到达时会通知监听在对应的队列上的监听对象
     * @return
     */
    @Autowired
    private RabbitProperties properties;
    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(){
        SimpleMessageListenerContainer listenerContainer=new SimpleMessageListenerContainer(connectionFactory);
        //设置当前监听消费者数为1个
        listenerContainer.setConcurrentConsumers(1);
        //设置当前最多监听消费者数为1
        listenerContainer.setMaxConcurrentConsumers(1);
        //设置手动消费  properties.getListener().getSimple().getAcknowledgeMode()
        listenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        listenerContainer.setQueueNames(queue,man,woman);
        listenerContainer.setMessageListener(rabbitMqReceiver);
        return listenerContainer;
    }
}
