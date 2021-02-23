package com.sundear.admin.controller;

import com.sundear.admin.mq.MessageWapper;
import com.sundear.admin.mq.RabbitConfig;
import com.sundear.base.model.Result;
import com.sundear.core.pojo.AqDeviceDataInfo;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author luc
 * @date 2020/6/2416:20
 */
@Api(tags = "消息队列生产者")
@RestController
@RequestMapping("/provider")
@Scope("prototype")
@RequiredArgsConstructor
@Slf4j
public class RabbitMqProviderController extends BaseController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @ApiOperation(value = "直流交换机生产者",notes = "直流交换机生产者")
    @PostMapping("/direct")
    public Result direct(){
        try {
            MessageWapper wapper=getMessage();
            //将消息序列化转为byte数组
            byte[] msg=ProtostuffIOUtil.toByteArray(wapper,RuntimeSchema.createFrom(MessageWapper.class),LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            //发送消息
            rabbitTemplate.convertAndSend(RabbitConfig.exchange,RabbitConfig.key, msg);
            return Result.success();
        }catch (Exception e){
            log.error("DirectRabbitProvider:direct:"+e.getMessage());
            return Result.fail();
        }
    }
    @ApiOperation(value = "直流交换机生产者",notes = "直流交换机生产者")
    @PostMapping("/topicMan")
    public Result man(){
        try {
            MessageWapper wapper=getMessage();
            //将消息序列化转为byte数组
            byte[] msg=ProtostuffIOUtil.toByteArray(wapper,RuntimeSchema.createFrom(MessageWapper.class),LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            rabbitTemplate.convertAndSend(RabbitConfig.topic_exchange, RabbitConfig.man,msg);
            return Result.success();
        }catch (Exception e){
            log.error("DirectRabbitProvider:direct:"+e.getMessage());
            return Result.fail();
        }
    }
    @ApiOperation(value = "直流交换机生产者",notes = "直流交换机生产者")
    @PostMapping("/topicWoman")
    public Result woman(){
        try {
            MessageWapper wapper=getMessage();
            //将消息序列化转为byte数组
            byte[] msg=ProtostuffIOUtil.toByteArray(wapper,RuntimeSchema.createFrom(MessageWapper.class),LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            rabbitTemplate.convertAndSend(RabbitConfig.topic_exchange, RabbitConfig.woman,msg);
            return Result.success();
        }catch (Exception e){
            log.error("DirectRabbitProvider:direct:"+e.getMessage());
            return Result.fail();
        }
    }



    @ApiOperation(value = "直流交换机生产者",notes = "直流交换机生产者")
    @PostMapping("/other")
    public Result other(){
        try {
            MessageWapper wapper=getMessage();
            //将消息序列化转为byte数组
            byte[] msg=ProtostuffIOUtil.toByteArray(wapper,RuntimeSchema.createFrom(MessageWapper.class),LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            rabbitTemplate.convertAndSend("other", RabbitConfig.woman,msg);
            return Result.success();
        }catch (Exception e){
            log.error("DirectRabbitProvider:other:"+e.getMessage());
            return Result.fail();
        }
    }
    @ApiOperation(value = "直流交换机生产者",notes = "直流交换机生产者")
    @PostMapping("/other1")
    public Result other1(){
        try {
            MessageWapper wapper=getMessage();
            //将消息序列化转为byte数组
            byte[] msg=ProtostuffIOUtil.toByteArray(wapper,RuntimeSchema.createFrom(MessageWapper.class),LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            rabbitTemplate.convertAndSend(RabbitConfig.topic_exchange, "other",msg);
            return Result.success();
        }catch (Exception e){
            log.error("DirectRabbitProvider:other1:"+e.getMessage());
            return Result.fail();
        }
    }
    @ApiOperation(value = "直流交换机生产者",notes = "直流交换机生产者")
    @PostMapping("/other2")
    public Result other2(){
        try {
            MessageWapper wapper=getMessage();
            //将消息序列化转为byte数组
            byte[] msg=ProtostuffIOUtil.toByteArray(wapper,RuntimeSchema.createFrom(MessageWapper.class),LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            rabbitTemplate.convertAndSend("other", "other",msg);
            return Result.success();
        }catch (Exception e){
            log.error("DirectRabbitProvider:other2:"+e.getMessage());
            return Result.fail();
        }
    }

    protected MessageWapper getMessage(){
        MessageWapper wapper=new MessageWapper();
        List<AqDeviceDataInfo> list=new ArrayList<>();
        for (int i=0;i<10;i++){
            AqDeviceDataInfo info=new AqDeviceDataInfo();
            info.setDeviceId(UUID.randomUUID().toString());
            info.setDeviceDataId(UUID.randomUUID().toString());
            info.setDataItem("CO2");
            info.setDataTime(LocalDateTime.now());
            info.setDataValue("30");
            info.setStatus(1);
            list.add(info);
        }
        wapper.setData(list);
        return wapper;
    }
}
