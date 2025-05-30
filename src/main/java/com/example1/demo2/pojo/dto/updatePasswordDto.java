package com.example1.demo2.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

public class updatePasswordDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String oldpassword;

    @NotBlank(message = "密码不能为空")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "密码至少8位，需包含字母、数字和特殊字符"
    )
    private String newpassword;

    private String repassword;

    @Override
    public String toString() {
        return "updatePasswordDto{" +
                "oldpassword='" + oldpassword + '\'' +
                ", newpassword='" + newpassword + '\'' +
                ", repassword='" + repassword + '\'' +
                '}';
    }

    // Getters and Setters
    public String getOldpassword() {
        return oldpassword;
    }

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public String getRepassword() {
        return repassword;
    }

    public void setRepassword(String repassword) {
        this.repassword = repassword;
    }
}