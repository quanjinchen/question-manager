package cn.spring.arch.console.service;

import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.CreateAppReqParam;
import cn.spring.arch.console.pojo.req.DeleteAppReqParam;
import cn.spring.arch.console.pojo.req.GetAppByIdReqParam;
import cn.spring.arch.console.pojo.req.ListAppReqParam;
import cn.spring.arch.console.pojo.req.UpdateAppReqParam;
import cn.spring.arch.console.pojo.resp.AppDTO;

public interface AppService {

    RespInfo<PageData<AppDTO>> listApp(ListAppReqParam reqParam);

    RespInfo<AppDTO> getAppById(GetAppByIdReqParam reqParam);

    RespInfo<AppDTO> createApp(CreateAppReqParam reqParam);

    RespInfo<AppDTO> updateApp(UpdateAppReqParam reqParam);

    RespInfo<Void> deleteApp(DeleteAppReqParam reqParam);
}

