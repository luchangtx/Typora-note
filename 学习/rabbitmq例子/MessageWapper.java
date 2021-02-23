package com.sundear.admin.mq;

import com.sundear.core.pojo.AqDeviceDataInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author luc
 * @date 2020/6/2811:58
 */
@Data
public class MessageWapper implements Serializable {
    private List<AqDeviceDataInfo> data;
    public int size(){
        return data.size();
    }
}
