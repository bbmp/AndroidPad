package com.robam.ventilator.response;

import com.robam.common.bean.BaseResponse;
import com.robam.ventilator.bean.UserInfo;

public class GetUserInfoRes extends BaseResponse {
    private UserInfo user;

    public UserInfo getUser() {
        return user;
    }
}