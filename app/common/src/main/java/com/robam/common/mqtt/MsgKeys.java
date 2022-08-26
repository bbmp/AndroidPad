package com.robam.common.mqtt;

public interface MsgKeys {

    // -------------------------------------------------------------------------------
    // 通知类
    // -------------------------------------------------------------------------------

    /**
     * 设备上线通知
     */
    short DeviceConnected_Noti = 32;

    /**
     * WiFi配置完成通知
     */
    short DeviceActivated_Noti = 33;

    /**
     * 子设备列表变更通知
     */
    short SubDeviceChanged_Noti = 46;

    /**
     * 云端推送通知信息
     */
    short CloudPush_Noti = 73;

    // -------------------------------------------------------------------------------
    // 应答类
    // -------------------------------------------------------------------------------

    /**
     * 获取WiFi信号强度(请求)
     */
    short GetWifiSignal_Req = 34;

    /**
     * 获取WiFi信号强度(应答)
     */
    short GetWifiSignal_Rep = 35;

    /**
     * 设置路由器信息(请求)
     */
    short SetWifiParamsAndOwner_Req = 36;

    /**
     * 设置路由器信息(应答)
     */
    short SetWifiParamsAndOwner_Rep = 37;

    /**
     * 删除子设备(请求)
     */
    short RemoveChildDevice_Req = 38;

    /**
     * 删除子设备(应答)
     */
    short RemoveChildDevice_Rep = 39;

    /**
     * 获取设备列表(请求)
     */
    short GetDevices_Req = 40;

    /**
     * 获取设备列表(应答)
     */
    short GetDevices_Rep = 41;

    /**
     * 触发设备进入配对模式(请求)
     */
    short MakePair_Req = 44;

    /**
     * 触发设备进入配对模式(应答)
     */
    short MakePair_Rep = 45;

    /**
     * 取消配对模式(请求)
     */
    short ExitPair_Req = 47;

    /**
     * 取消配对模式(应答)
     */
    short ExitPair_Rep = 48;
    //----------------------------------------------------------
    /**
     * 一体机状态查询
     */
    short getSteameOvenStatus_Req = 150;

    /**
     * 一体机状态查询(应答)
     */
    short getSteameOvenStatus_Rep = 151;
    /**
     * 属性查询
     */
    short getDeviceAttribute_Req = 190;

    /**
     * 属性查询响应
     */
    short getDeviceAttribute_Rep = 191;

    /**
     * 属性设置
     */
    short setDeviceAttribute_Req = 192;

    /**
     * 属性设置响应
     */
    short setDeviceAttribute_Rep = 193;

    /**
     * 事件上报
     */
    short getDeviceEventReport = 194;

    /**
     * 历史事件上报
     */
    short getDeviceHistoryEventReport = 195;

    /**
     * 报警上报
     */
    short getDeviceAlarmEventReport = 197;

    /**
     * 历史报警上报
     */
    short getDeviceHistoryAlarmEventReport = 198;

    /**
     * 属性查询 烟机（针对集成灶设备需要分开查询）
     */
    short getDeviceAttribute_fan_Req = 190;

    /**
     * 属性查询 灶具（针对集成灶设备需要分开查询）
     */
    short getDeviceAttribute_stove_Req = 190;

}
