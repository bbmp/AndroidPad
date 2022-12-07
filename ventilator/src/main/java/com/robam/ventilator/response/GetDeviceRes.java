package com.robam.ventilator.response;

import com.robam.common.bean.BaseResponse;
import com.robam.common.bean.Device;

import java.util.List;

public class GetDeviceRes extends BaseResponse {
    public List<Device> devices;
}
