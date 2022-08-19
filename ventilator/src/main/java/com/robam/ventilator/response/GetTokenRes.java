package com.robam.ventilator.response;

import com.robam.common.bean.BaseResponse;

public class GetTokenRes extends BaseResponse {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;

    public String getAccess_token() {
        return access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }
}
