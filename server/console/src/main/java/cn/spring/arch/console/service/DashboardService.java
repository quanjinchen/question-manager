package cn.spring.arch.console.service;

import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.resp.DashboardSummaryDTO;

public interface DashboardService {

    RespInfo<DashboardSummaryDTO> summary();
}


