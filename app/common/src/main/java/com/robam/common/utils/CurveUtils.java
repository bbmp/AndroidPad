package com.robam.common.utils;

import com.robam.common.bean.LineChartDataBean;
import com.robam.common.bean.SetPotCurveStageParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurveUtils {

    public static List<SetPotCurveStageParams> curveStageParamsToList(String strData) {
        List<SetPotCurveStageParams> list = new ArrayList<>();
        strData = strData.substring(1, strData.indexOf("}"));
        String str[] = strData.split(",");
        for (int i = 0; i < str.length; i++) {
            int time = Integer.parseInt(str[i].split(":")[0].trim().replace("\"", ""));
            String strNe[] = str[i].split(":")[1].trim().replace("\"", "").split("-");
            int temp = (int) Float.parseFloat(strNe[0].trim().replace("\"", ""));
            int control = Integer.parseInt(strNe[1].trim().replace("\"", ""));
            list.add(new SetPotCurveStageParams(time, temp, control));
        }
        Collections.sort(list, (bean1, bean2) -> {
            if (bean1.time > bean2.time) {
                return 1;
            } else if (bean1.time == bean2.time) {
                return 0;
            } else {
                return -1;
            }
        });
        return list;
    }

    public static List<LineChartDataBean> curveDataToLine(String strData) {
        List<LineChartDataBean> list = new ArrayList<>();
        strData = strData.substring(1, strData.indexOf("}"));
        String str[] = strData.split(",");
        for (int i = 0; i < str.length; i++) {
            float xValue = Integer.parseInt(str[i].split(":")[0].trim().replace("\"", ""));
            String strNe[] = str[i].split(":")[1].trim().replace("\"", "").split("-");
            float yValue = (int) Float.parseFloat(strNe[0].trim().replace("\"", ""));
            int gear = Integer.parseInt(strNe[1].trim().replace("\"", ""));
            int power = 0;
            if (strNe.length > 2) {
                power = Integer.parseInt(strNe[2].trim().replace("\"", ""));
            }
            list.add(new LineChartDataBean(xValue, yValue, power, gear));
        }
        Collections.sort(list, (bean1, bean2) -> {
            if (bean1.xValue > bean2.xValue) {
                return 1;
            } else if (bean1.xValue == bean2.xValue) {
                return 0;
            } else {
                return -1;
            }
        });
        return list;
    }

    public static List<SetPotCurveStageParams> curveStageParamsListSetGear(List<SetPotCurveStageParams> paramsList, List<LineChartDataBean> lineList) {
        for (int i = 0; i < paramsList.size(); i++) {
            for (int j = 0; j < lineList.size(); j++) {
//                if (paramsList.get(i).time == lineList.get(j).xValue) {
//                    paramsList.get(i).gear = lineList.get(j).gear;
//                }
                if (paramsList.get(i).time <= lineList.get(j).xValue && paramsList.get(i).gear == 0) {
                    paramsList.get(i).gear = lineList.get(j).gear;
                }
            }
        }
        return paramsList;
    }
    /**
     * 数据转曲线
     *
     * @param strData
     * @return
     */
    public static List<LineChartDataBean> curveDataToLineFormat(String strData) {
        List<LineChartDataBean> list = new ArrayList<>();
        strData = strData.substring(1, strData.indexOf("}"));
        String str[] = strData.split(",");
        for (int i = 0; i < str.length; i++) {
            float xValue = Integer.parseInt(str[i].split(":")[0].trim().replace("\"", ""));
            String strNe[] = str[i].split(":")[1].trim().replace("\"", "").split("-");
            float yValue = (int) Float.parseFloat(strNe[0].trim().replace("\"", ""));
            int gear = Integer.parseInt(strNe[1].trim().replace("\"", ""));
            int power = 0;
            if (strNe.length > 2) {
                power = Integer.parseInt(strNe[2].trim().replace("\"", ""));
            }
            list.add(new LineChartDataBean(xValue, yValue, power, gear));
        }
        Collections.sort(list, (bean1, bean2) -> {
            if (bean1.xValue > bean2.xValue) {
                return 1;
            } else if (bean1.xValue == bean2.xValue) {
                return 0;
            } else {
                return -1;
            }
        });
//        return list;
        int diffY = 0, value = 2;
        List<LineChartDataBean> brokenLineList = countPointUnitedNew(list, diffY, value);
        while (brokenLineList.size() > 11) {
            brokenLineList = countPointUnitedNew(brokenLineList, ++diffY, ++value);
        }
        return brokenLineList;
    }

    public static String listToJsonStr(List<LineChartDataBean> list) {
        try {
            int diffY = 0, value = 2;
            List<LineChartDataBean> brokenLineList = countPointUnitedNew(list, diffY, value);
            while (brokenLineList.size() > 11) {
                brokenLineList = countPointUnitedNew(brokenLineList, ++diffY, ++value);
            }

            if (brokenLineList == null || brokenLineList.isEmpty()) {
                return "";
            }
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < brokenLineList.size(); i++) {
                int key = (int) brokenLineList.get(i).xValue;
                jsonObject.put(key + "", brokenLineList.get(i).yValue + "-" + brokenLineList.get(i).type);

            }
            JSONObject cmdPointsJsonObject = new JSONObject();
            cmdPointsJsonObject.put("cmdPoints", jsonObject);
            return cmdPointsJsonObject.toString();
        } catch (Exception e) {

        }
        return null;
    }

    public static String listToJosnStr(String strData) {
        try {
            List<LineChartDataBean> dataBeanList = curveDataToLineFormat(strData);
            if (dataBeanList == null || dataBeanList.isEmpty()) {
                return "";
            }
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < dataBeanList.size(); i++) {
                int key = (int) dataBeanList.get(i).xValue;
                jsonObject.put(key + "", dataBeanList.get(i).yValue + "-" + dataBeanList.get(i).type);

            }
            JSONObject cmdPointsJsonObject = new JSONObject();
            cmdPointsJsonObject.put("cmdPoints", jsonObject.toString());
            return cmdPointsJsonObject.toString();
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 曲线取点新算法
     */

    private static List<LineChartDataBean> countPointUnitedNew(List<LineChartDataBean> dataBeanList, float diffY, int value) {//diffY 0  value 2
        if (dataBeanList == null || dataBeanList.size() < 2) {
            return dataBeanList;
        }
        List<LineChartDataBean> brokenLineList = new ArrayList<>();
        float kslope = 0;
        LineChartDataBean oldEntry = dataBeanList.get(0);
        oldEntry.type = 1;
        brokenLineList.add(oldEntry);
        LineChartDataBean markEntry = dataBeanList.get(0);
        for (int i = 1; i < dataBeanList.size(); i++) {
            LineChartDataBean newEntry = dataBeanList.get(i);
            float newKslope = (newEntry.yValue - oldEntry.yValue) / (newEntry.xValue - oldEntry.xValue);
            if (i == dataBeanList.size()) {
                brokenLineList.add(newEntry);
                break;
            }
            //前后两个点的x,y的差值
            float diff_y = newEntry.yValue - oldEntry.yValue;
            Integer diff_x = (int) newEntry.xValue - (int) oldEntry.xValue;
            //转折点最小x间隔
            int target_x = value * 15;
            //最新点和上一个标记点间隔 以及最小x间隔
            int mark_x = (int) newEntry.xValue - (int) markEntry.xValue;
            int interval_x = value * 5;
            //1. k>0, k=0, k<0
            if (newKslope > 0) {
                if (kslope > 0 && diff_x < target_x && mark_x < interval_x) {
                    oldEntry = newEntry;
                    continue;
                }
                if (Math.abs(diff_y) >= diffY && mark_x >= interval_x) {
                    newEntry.type = 1;
                    kslope = newKslope;
                    markEntry = newEntry;
                    brokenLineList.add(newEntry);
                }

            } else if (newKslope < 0) {
                if (kslope < 0 && diff_x < target_x && mark_x < interval_x) {
                    oldEntry = newEntry;
                    continue;
                }
                if (Math.abs(diff_y) >= diffY && mark_x >= interval_x) {
                    newEntry.type = 2;
                    kslope = newKslope;
                    markEntry = newEntry;
                    brokenLineList.add(newEntry);
                }
            } else if (newKslope == 0) {
                if (kslope == 0 && diff_x < target_x && mark_x < interval_x) {
                    oldEntry = newEntry;
                    continue;
                }
                if (Math.abs(diff_y) >= diffY && mark_x >= interval_x) {
                    newEntry.type = 3;
                    kslope = newKslope;
                    markEntry = newEntry;
                    brokenLineList.add(newEntry);
                }
            }
            oldEntry = newEntry;
        }
        //多次过滤数据无变化，通过移除中间变化小的点
        int cumulativeCount = 0;
        if (brokenLineList.size() > 11 && brokenLineList.size() == dataBeanList.size()) {
            if (cumulativeCount < 2) {
                cumulativeCount += 1;
            } else {
                LineChartDataBean lastEntry = null;
                int count = 0;
                for (int idx = 0; idx < brokenLineList.size(); idx++) {
                    LineChartDataBean newEntry = brokenLineList.get(idx);
                    if (idx == 0) {
                        lastEntry = newEntry;
                        continue;
                    }
                    float diff_value = newEntry.yValue - lastEntry.yValue;
                    if (Math.abs(diff_value) <= 1) {
                        count += 1;
                    } else {
                        count = 0;
                    }
                    if (count == 3) {
                        brokenLineList.remove(lastEntry);
                        count -= 1;
                    }
                    lastEntry = newEntry;
                }
                cumulativeCount = 0;
            }
        } else {
            cumulativeCount = 0;
        }
        return brokenLineList;
    }
}
