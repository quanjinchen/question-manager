package cn.spring.arch.system.manager;

import cn.spring.arch.system.entity.FaceAuthLog;
import cn.spring.arch.system.pojo.query.ListFaceAuthLogQuery;

import java.util.List;

public interface FaceAuthLogManager {

    void save(FaceAuthLog faceAuthLog);

    List<FaceAuthLog> listFaceAuthLogs(ListFaceAuthLogQuery query);
}

