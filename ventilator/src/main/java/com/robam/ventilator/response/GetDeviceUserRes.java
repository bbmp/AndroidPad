package com.robam.ventilator.response;

import com.robam.common.bean.BaseResponse;
import com.robam.common.bean.UserInfo;

import java.util.List;

public class GetDeviceUserRes extends BaseResponse {
    public List<UserInfo> users;
}
