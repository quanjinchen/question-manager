package cn.spring.arch.console.service;

import cn.spring.arch.common.page.PageData;
import cn.spring.arch.common.pojo.RespInfo;
import cn.spring.arch.console.pojo.req.CreateUserReqParam;
import cn.spring.arch.console.pojo.req.DeleteUserReqParam;
import cn.spring.arch.console.pojo.req.GetUserByIdReqParam;
import cn.spring.arch.console.pojo.req.ListUserReqParam;
import cn.spring.arch.console.pojo.req.ResetUserPasswordReqParam;
import cn.spring.arch.console.pojo.req.UpdateUserReqParam;
import cn.spring.arch.console.pojo.resp.UserDTO;

public interface UserService {

    RespInfo<Void> createUser(CreateUserReqParam createUserReqParam);

    RespInfo<UserDTO> getUserById(GetUserByIdReqParam getUserByIdReqParam);

    RespInfo<PageData<UserDTO>> listUser(ListUserReqParam listUserReqParam);

    RespInfo<Void> updateUser(UpdateUserReqParam updateUserReqParam);

    RespInfo<Void> deleteUser(DeleteUserReqParam deleteUserReqParam);

    RespInfo<Void> resetUserPassword(ResetUserPasswordReqParam resetUserPasswordReqParam);
}

