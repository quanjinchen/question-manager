package cn.spring.arch.system.pojo.query;

import cn.spring.arch.system.enums.FaceAuthApiTypeEnum;
import lombok.Data;

@Data
public class ListFaceAuthLogQuery {

    private FaceAuthApiTypeEnum authApiType;

    private String ip;

    private Integer status;

    private String appName;

    private String authFullName;
}

