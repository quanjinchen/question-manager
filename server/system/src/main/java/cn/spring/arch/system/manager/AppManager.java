package cn.spring.arch.system.manager;

import cn.spring.arch.system.entity.App;
import cn.spring.arch.system.pojo.query.ListAppQuery;

import java.util.List;

public interface AppManager {

    App getById(Long appId);

    App getByAppName(String appName);

    App getByAppCode(String appCode);

    App getByClientId(String clientId);

    List<App> listApp(ListAppQuery query);

    App save(App app);

    void deleteById(Long appId);
}

