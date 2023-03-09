package com.robam.steamoven.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.robam.steamoven.constant.Constant;

public class TempAxisValueFormatter extends DefaultAxisValueFormatter {
    /**
     * Constructor that specifies to how many digits the value should be
     * formatted.
     *
     * @param digits
     */
    public TempAxisValueFormatter(int digits) {
        super(digits);
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        String formattedValue = super.getFormattedValue(value, axis);
        return formattedValue + Constant.UNIT_TEMP;
    }
}
