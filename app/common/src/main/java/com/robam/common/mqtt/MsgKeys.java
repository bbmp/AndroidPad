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
     * 电磁灶报警上报
     */
    short StoveAlarm_Noti = 138;

    /**
     * 电磁灶事件上报
     */
    short StoveEvent_Noti = 139;

    /**
     * 灶具温度事件上报     by zhaiyuanyi 20151029
     */

    short StoveTemp_Noti = 140;

    /**
     * 油烟机事件上报
     */
    short FanEvent_Noti = 148;
    /**
     * 消毒柜报警上报      by zhaiyuanyi 20151120
     */
    short SteriAlarm_Noti = 146;
    /**
     * 消毒柜事件上报      by zhaiyuanyi 20151218
     */
    short SteriEvent_Noti = 152;

    // -------------------------------------------------------------------------------
    // 应答类
    // -------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------电磁灶
    /**
     * 获取电磁灶状态（请求）
     */
    short GetStoveStatus_Req = 128;

    /**
     * 获取电磁灶状态（应答）
     */
    short GetStoveStatus_Rep = 129;

    /**
     * 设置电磁灶状态（请求）
     */
    short SetStoveStatus_Req = 130;

    /**
     * 设置电磁灶状态（应答）
     */
    short SetStoveStatus_Rep = 131;
    /**
     * 设置电磁灶档位（请求）
     */
    short SetStoveLevel_Req = 132;

    /**
     * 设置电磁灶档位（应答）
     */
    short SetStoveLevel_Rep = 133;
    /**
     * 设置电磁灶定时关机（请求）
     */
    short SetStoveShutdown_Req = 134;

    /**
     * 设置电磁灶定时关机（应答）
     */
    short SetStoveShutdown_Rep = 135;

    /**
     * 设置电磁灶童锁（请求）
     */
    short SetStoveLock_Req = 136;

    /**
     * 设置电磁灶童锁（应答）
     */
    short SetStoveLock_Rep = 137;
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
// -------------------------------------------------------------------------------油烟机

    /**
     * 读取智能互动模式设定（请求）
     */
    short GetSmartConfig_Req = 128;

    /**
     * 读取智能互动模式设定（应答）
     */
    short GetSmartConfig_Rep = 129;

    /**
     * 读取油烟机状态（请求）
     */
    short GetFanStatus_Req = 130;

    /**
     * 读取油烟机状态（应答）
     */
    short GetFanStatus_Rep = 131;

    /**
     * 设置油烟机工作状态（请求）
     */
    short SetFanStatus_Req = 132;

    /**
     * 设置油烟机工作状态（应答）
     */
    short SetFanStatus_Rep = 133;

    /**
     * 设置油烟机档位（请求）
     */
    short SetFanLevel_Req = 134;

    /**
     * 设置油烟机档位（应答）
     */
    short SetFanLevel_Rep = 135;

    /**
     * 设置油烟机灯（请求）
     */
    short SetFanLight_Req = 136;

    /**
     * 设置油烟机灯（应答）
     */
    short SetFanLight_Rep = 137;

    /**
     * 设置油烟机整体状态（请求）
     */
    short SetFanAllParams_Req = 138;

    /**
     * 设置油烟机整体状态（应答）
     */
    short SetFanAllParams_Rep = 139;

    /**
     * 重置烟机清洗计时（请求）
     */
    short RestFanCleanTime_Req = 140;

    /**
     * 重置烟机清洗计时（应答）
     */
    short RestFanCleanTime_Rep = 141;

    /**
     * 重启油烟机网络板（请求）
     */
    short RestFanNetBoard_Req = 142;

    /**
     * 重启油烟机网络板（应答）
     */
    short RestFanNetBoard_Rep = 143;

    /**
     * 设置油烟机定时工作（请求）
     */
    short SetFanTimeWork_Req = 144;

    /**
     * 设置油烟机定时工作（应答）
     */
    short SetFanTimeWork_Rep = 145;

    /**
     * 设置智能互动模式（请求）
     */
    short SetSmartConfig_Req = 146;

    /**
     * 设置智能互动模式（应答）
     */
    short SetSmartConfig_Rep = 147;

    /**
     * 设置油烟机重置油杯定时工作（请求）
     */
    short SetFanCleanOirCupTime_Req = 166;

    //2019年新增指令
    short SetFanStatusCompose_Rep = 162;


    short SetFanStatusCompose_Req = 163;

    short FanStatusComposeCheck_Rep = 149;

    short FanStatusComposeCheck_Req = 150;

    //设置定时提醒
    short SetFanTimingRemind_Rep = 168;

    short SetFanTimingRemind_Req = 169;

    /**
     * 设置油烟机重置油杯定时工作（应答）
     */
    short SetFanCleanOirCupTime_Rep = 167;
    // ------------------------------------------------------------------------------消毒柜  by zhaiyuanyi 20151120
    /**
     * 设置消毒柜开关（请求）
     */
    short SetSteriPowerOnOff_Req = 128;
    /**
     * 设置消毒柜开关（应答）
     */
    short SetSteriPowerOnOff_Rep = 129;
    /**
     * 设置消毒柜预约时间（请求）
     */
    short SetSteriReserveTime_Req = 130;
    /**
     * 设置消毒柜预约时间（应答）
     */
    short SetSteriReserveTime_Rep = 131;
    /**
     * 设置消毒柜烘干（请求）
     */
    short SetSteriDrying_Req = 132;
    /**
     * 设置消毒柜烘干（应答）
     */
    short SetSteriDrying_Rep = 133;
    /**
     * 设置消毒柜保洁（请求）
     */
    short SetSteriClean_Req = 134;
    /**
     * 设置消毒柜保洁（应答）
     */
    short SetSteriClean_Rep = 135;
    /**
     * 设置消毒柜消毒（请求）
     */
    short SetSteriDisinfect_Req = 136;
    /**
     * 设置消毒柜消毒（应答）
     */
    short SetSteriDisinfect_Rep = 137;
    /**
     * 设置消毒柜童锁（829请求）
     */
    short SetSteriLock_Req = 138;//消毒柜829童锁
    /**
     * 设置消毒柜童锁（829应答）
     */
    short SetSteriLock_Rep = 139;

    /**
     * 设置消毒柜童锁（826请求）
     */
    short SetSteri826Lock_Req = 153;//消毒柜826童锁
    /**
     * 设置消毒柜童锁（826应答）
     */
    short SetSteri826Lock_Rep = 154;

    /**
     * 查询消毒柜温度／湿度／细菌数（请求）
     */
    short GetSteriParam_Req = 142;
    /**
     * 查询消毒柜温度／湿度／细菌数（应答）
     */
    short GetSteriParam_Rep = 143;
    /**
     * 消毒柜状态查询（请求）
     */
    short GetSteriStatus_Req = 144;
    /**
     * 消毒柜状态查询（应答）
     */
    short GetSteriStatus_Rep = 145;
    /**
     * 读取消毒柜峰谷定时设置(请求)
     */
    short GetSteriPVConfig_Req = 147;
    /**
     * 读取消毒柜峰谷定时设置(应答)
     */
    short GetSteriPVConfig_Rep = 148;
    /**
     * 设置消毒柜峰谷定时开启（请求）
     */
    short SetSteriPVConfig_Req = 149;
    /**
     * 设置消毒柜峰谷定时开启（应答）
     */
    short SetSteriPVConfig_Rep = 150;
}
