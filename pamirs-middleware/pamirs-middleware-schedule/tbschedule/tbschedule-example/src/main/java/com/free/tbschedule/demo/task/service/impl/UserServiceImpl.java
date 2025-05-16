package com.free.tbschedule.demo.task.service.impl;

import com.free.tbschedule.demo.task.dto.User;
import com.free.tbschedule.demo.task.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public List<User> getUsers(int mod, List<Integer> remainder, int topNum) {
        return UserDao.getAllUsers().stream()
            .filter(user -> remainder.contains(user.getId() % mod) && user.getStatus() == 0)
            .limit(topNum)
            .collect(Collectors.toList());
    }

    @Override
    public int update(User task) {
        List<User> result = UserDao.getAllUsers().stream()
            .filter(user -> user.getId() == task.getId())
            .collect(Collectors.toList());

        result.forEach(user -> user.setStatus(1));
        return result.size();
    }

    static class UserDao {

        private static ArrayList<User> users;

        /**
         * 模拟持久层，从数据库取数据
         */
        private static List<User> getAllUsers() {
            if (null == users) {
                users = new ArrayList<>(10);
                users.add(new User(1, "LiLi", 43, 0));
                users.add(new User(2, "WangYun", 54, 0));
                users.add(new User(3, "LiuSiSi", 89, 0));
                users.add(new User(4, "ZhangJia", 24, 0));
                users.add(new User(5, "ChenXiao", 25, 0));
                users.add(new User(6, "WangHai", 65, 0));
                users.add(new User(7, "FengNian", 45, 0));
                users.add(new User(8, "XiaoXiao", 85, 0));
                users.add(new User(9, "YingMo", 36, 0));
                users.add(new User(10, "KaEr", 98, 0));
            }
            return users;
        }
    }
}
