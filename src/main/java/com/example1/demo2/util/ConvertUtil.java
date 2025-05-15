package com.example1.demo2.util;

import com.example1.demo2.pojo.User;
import com.example1.demo2.pojo.dto.UserDto;

//实体类和Dto转换工具
public class ConvertUtil {
    /**
     * 将 User 实体转换为 UserDto（全属性映射）
     * @param user User 实体对象
     * @return 转换后的 UserDto 对象
     */
    public static UserDto convertUserToDto(User user) {
        UserDto dto = new UserDto();
        // 主键 ID
        dto.setUserId(user.getUserId());
        //jwt版本
        dto.setTokenVersion(user.getTokenVersion());
        // 邮箱
        dto.setEmail(user.getEmail());
        // 手机号
        dto.setMobile(user.getMobile());
        // 密码（注意：UserDto 中的 @JsonIgnore 会忽略密码字段，但转换时仍需赋值）
        dto.setPassword(user.getPassword());
        // 昵称
        dto.setNickname(user.getNickname());
        // 头像 URL
        dto.setAvatarUrl(user.getAvatarUrl());
        // 个人简介
        dto.setBio(user.getBio());
        // 用户状态
        dto.setStatus(user.getStatus());
        // 邮箱验证状态
        dto.setEmailVerified(user.getEmailVerified());
        // 手机验证状态
        dto.setMobileVerified(user.getMobileVerified());
        // 创建时间
        dto.setCreateTime(user.getCreateTime());
        // 更新时间
        dto.setUpdateTime(user.getUpdateTime());
        // 最后登录时间
        dto.setLastLoginTime(user.getLastLoginTime());
        return dto;
    }

    /**
     * 反向转换：UserDto 转 User
     */
    public static User convertDtoToUser(UserDto dto) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setTokenVersion(dto.getTokenVersion());
        user.setEmail(dto.getEmail());
        user.setMobile(dto.getMobile());
        user.setPassword(dto.getPassword()); // 注意：此处需先对密码加密再存入实体
        user.setNickname(dto.getNickname());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setBio(dto.getBio());
        user.setStatus(dto.getStatus());
        user.setEmailVerified(dto.getEmailVerified());
        user.setMobileVerified(dto.getMobileVerified());
        // 时间字段通常由系统自动生成，注册/更新时无需手动设置（可省略或按需处理）
        return user;
    }
}
