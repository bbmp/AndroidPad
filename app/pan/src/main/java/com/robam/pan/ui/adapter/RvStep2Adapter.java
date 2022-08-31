package com.robam.pan.ui.adapter;

import android.util.TypedValue;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.utils.DateUtil;
import com.robam.pan.R;
import com.robam.pan.bean.CurveStep;
import com.robam.pan.bean.RecipeStep;

public class RvStep2Adapter extends BaseQuickAdapter<CurveStep, BaseViewHolder> {
    private int curStep;
    //倒计时
    private TextView tvCountdown;

    public RvStep2Adapter() {
        super(R.layout.pan_item_recipe_step2);
        curStep = 0;
    }

    public void setCurStep(int curStep) {
        this.curStep = curStep;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, CurveStep curveStep) {
        if (getItemPosition(curveStep) == curStep) {
            baseViewHolder.setVisible(R.id.pan_group, true);
            TextView tvCurStep = baseViewHolder.getView(R.id.tv_cur_step);
            baseViewHolder.setText(R.id.tv_cur_step, String.format(getContext().getString(R.string.pan_cur_step), curveStep.no+"", getData().size()+""));
            baseViewHolder.setTextColorRes(R.id.tv_cur_step, R.color.pan_step);
            tvCountdown = baseViewHolder.getView(R.id.tv_countdown);
            tvCountdown.setText(DateUtil.secForMatTime(curveStep.needTime - curveStep.elapsedTime));
            ProgressBar pbTime = baseViewHolder.getView(R.id.pb_time);
            pbTime.setProgress(curveStep.elapsedTime * 100 / curveStep.needTime);
            tvCurStep.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimensionPixelSize(com.robam.common.R.dimen.sp_32));
            baseViewHolder.setTextColorRes(R.id.tv_step_des, R.color.pan_white);
            baseViewHolder.setText(R.id.tv_step_des, String.format(getContext().getString(R.string.pan_recipe_step_des), curveStep.no + "", curveStep.description));
        } else {
            baseViewHolder.setGone(R.id.pan_group, true);
            TextView tvCurStep = baseViewHolder.getView(R.id.tv_cur_step);
            baseViewHolder.setText(R.id.tv_cur_step, String.format(getContext().getString(R.string.pan_step2), curveStep.no +"", getData().size()+""));
            baseViewHolder.setTextColorRes(R.id.tv_cur_step, R.color.pan_white_50);
            tvCurStep.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimensionPixelSize(com.robam.common.R.dimen.sp_24));
            TextView tvStepDes = baseViewHolder.getView(R.id.tv_step_des);
            baseViewHolder.setTextColorRes(R.id.tv_step_des, R.color.pan_white_50);
            baseViewHolder.setText(R.id.tv_step_des, String.format(getContext().getString(R.string.pan_recipe_step_des), curveStep.no + "", curveStep.description));
        }
    }
}
