package com.robam.steamoven.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;

import com.robam.common.utils.TimeUtils;
import com.robam.steamoven.constant.Constant;

public class TextSpanUtil {
    /**
     * 获取温度/时间样式
     * @param value 当 unit 是 Constant.UNIT_TEMP 时 ，value 时温度值；
     *              当 unit 是 Constant.UNIT_TIME_MIN，value 是时间值，单位秒
     * @param unit  Constant.UNIT_TIME_MIN 或 Constant.UNIT_TEMP
     * @return
     */
    public  static SpannableString getSpan(int value, String unit){
        if(unit.equals(Constant.UNIT_TIME_MIN)){
            int min = value/60;
            int sec = value % 60;
            if(sec != 0){
                min += 1;
            }
            String time = min+"min";
            //String time = TimeUtils.secToHourMinUp(value);
            SpannableString spannableString = new SpannableString(time);
            int pos = time.indexOf(Constant.UNIT_TIME_H);
            if (pos >= 0)
                spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            pos = time.indexOf(Constant.UNIT_TIME_MIN);
            if (pos >= 0)
                spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }else{
            SpannableString spannableString = new SpannableString(value+unit);
            SuperscriptSpan superscriptSpan = new SuperscriptSpan();
            spannableString.setSpan(new RelativeSizeSpan(0.8f), (value+"").length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(superscriptSpan,  (value+"").length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            return spannableString;
        }

    }
}
