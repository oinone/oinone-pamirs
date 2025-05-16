package com.free.tbschedule.demo.task.service;

import com.free.tbschedule.demo.task.dto.User;
import java.util.List;

public interface UserService {

    /**
     * 获取待处理的数据
     *
     * @param mod 用多少取余
     * @param remainder 余数为多少
     * @param topNum 取前多少个数据
     */
    List<User> getUsers(int mod, List<Integer> remainder, int topNum);

    /**
     * 更新用户信息
     */
    int update(User task);
}
