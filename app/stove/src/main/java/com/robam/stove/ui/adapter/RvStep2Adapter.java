package com.robam.stove.ui.adapter;

import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.DateUtil;
import com.robam.stove.R;
import com.robam.stove.bean.RecipeStep;

//工作界面
public class RvStep2Adapter extends BaseQuickAdapter<RecipeStep, BaseViewHolder> {
    private int curStep;
    //倒计时
    private TextView tvCountdown;

    public RvStep2Adapter() {
        super(R.layout.stove_item_recipe_step2);
        curStep = 0;
    }

    public void setCurStep(int curStep) {
        this.curStep = curStep;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, RecipeStep recipeStep) {
        if (getItemPosition(recipeStep) == curStep) {
            baseViewHolder.setVisible(R.id.stove_group, true);
            TextView tvCurStep = baseViewHolder.getView(R.id.tv_cur_step);
            baseViewHolder.setText(R.id.tv_cur_step, String.format(getContext().getString(R.string.stove_cur_step), recipeStep.getNo()+"", getData().size()+""));
            baseViewHolder.setTextColorRes(R.id.tv_cur_step, R.color.stove_step);
            tvCountdown = baseViewHolder.getView(R.id.tv_countdown);
            tvCountdown.setText(DateUtil.secForMatTime(recipeStep.needTime - recipeStep.elapsedTime));
            ProgressBar pbTime = baseViewHolder.getView(R.id.pb_time);
            pbTime.setProgress(recipeStep.elapsedTime * 100 / recipeStep.needTime);
            tvCurStep.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimensionPixelSize(com.robam.common.R.dimen.sp_32));
            baseViewHolder.setTextColorRes(R.id.tv_step_des, R.color.stove_white);
            baseViewHolder.setText(R.id.tv_step_des, String.format(getContext().getString(R.string.stove_recipe_step_des), recipeStep.getNo() + "", recipeStep.getDesc()));
        } else {
            baseViewHolder.setGone(R.id.stove_group, true);
            TextView tvCurStep = baseViewHolder.getView(R.id.tv_cur_step);
            baseViewHolder.setText(R.id.tv_cur_step, String.format(getContext().getString(R.string.stove_step2), recipeStep.getNo()+"", getData().size()+""));
            baseViewHolder.setTextColorRes(R.id.tv_cur_step, R.color.stove_white_50);
            tvCurStep.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimensionPixelSize(com.robam.common.R.dimen.sp_24));
            TextView tvStepDes = baseViewHolder.getView(R.id.tv_step_des);
            baseViewHolder.setTextColorRes(R.id.tv_step_des, R.color.stove_white_50);
            baseViewHolder.setText(R.id.tv_step_des, String.format(getContext().getString(R.string.stove_recipe_step_des), recipeStep.getNo() + "", recipeStep.getDesc()));
        }
    }

}
