package com.robam.common.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatRadioButton;


public class CancelRadioButton extends AppCompatRadioButton {
    public CancelRadioButton (Context context) {
        this(context, null);
    }

    public CancelRadioButton (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CancelRadioButton (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
        if (!isChecked()) {
            ((RadioGroup) getParent()).clearCheck();
        }
    }
}
