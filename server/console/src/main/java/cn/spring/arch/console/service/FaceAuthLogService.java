package cn.spring.arch.console.service;

import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.ListFaceAuthLogReqParam;
import cn.spring.arch.console.pojo.resp.FaceAuthLogDTO;

public interface FaceAuthLogService {

    RespInfo<PageData<FaceAuthLogDTO>> listFaceAuthLog(ListFaceAuthLogReqParam reqParam);
}


