package com.robam.ventilator.response;

import com.robam.common.bean.BaseResponse;
import com.robam.ventilator.bean.UserInfo;

import java.util.List;

public class GetDeviceUserRes extends BaseResponse {
    public List<UserInfo> users;
}
