package com.sundear.admin.mq;

import com.rabbitmq.client.Channel;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author luc
 * @date 2020/6/2810:48
 */
@Component
public class RabbitMqReceiver implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        RuntimeSchema<MessageWapper> schema = RuntimeSchema.createFrom(MessageWapper.class);
        MessageWapper wapper = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(message.getBody(), wapper, schema);

        double hc1=2.7d+8.7d*Math.pow(0.16,0.67);
        double hc=2.7d+(8.7d*Math.pow(0.16,0.67));

        double a=Math.pow(0.16,0.67);
        double b=8.7d*a;
        double c=2.7d+b;

        System.err.println(hc1+"___"+hc+"___"+c);

        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
