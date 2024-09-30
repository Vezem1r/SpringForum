package com.back_end.forum.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {
    private String resetCode;
    private String newPassword;
}
