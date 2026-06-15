package cn.spring.arch.auth.pojo.cache;

import cn.spring.arch.auth.pojo.resp.CertUserDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class CertTokenCache implements Serializable {

    private static final long serialVersionUID = 1L;

    private String certToken;

    private Long appId;

    private String appName;

    private String clientId;

    private Boolean authenticated;

    private CertUserDTO userInfo;
}
