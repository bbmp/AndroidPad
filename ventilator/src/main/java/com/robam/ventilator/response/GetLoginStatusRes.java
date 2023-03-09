package com.robam.ventilator.response;

import com.robam.common.bean.BaseResponse;

public class GetLoginStatusRes extends BaseResponse {
    public Payload payload;

    public class Payload {
        //账号
        public String password;
        //密码
        public String account;
        //
        public String access_token;
    }
}
