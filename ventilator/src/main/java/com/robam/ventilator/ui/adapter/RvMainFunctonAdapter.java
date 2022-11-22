package com.robam.ventilator.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.ventilator.R;
import com.robam.ventilator.bean.VenFunBean;

import net.center.blurview.ShapeBlurView;

public class RvMainFunctonAdapter extends BaseQuickAdapter<VenFunBean, BaseViewHolder> {
    private Context mContext;
    private int pickPosition = -1;
    private Animation imgAnimation;

    public boolean setPickPosition(int pickPosition) {
        if (this.pickPosition == pickPosition)
            return false;

        this.pickPosition = pickPosition;
        notifyDataSetChanged();
        return true;
    }

    public int getPickPosition() {
        return pickPosition;
    }

    public RvMainFunctonAdapter(Context context) {
        super(R.layout.ventilator_item_layout_function);
        this.mContext = context;
        imgAnimation = AnimationUtils.loadAnimation(mContext, R.anim.ventilator_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        imgAnimation.setInterpolator(lin);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, VenFunBean venFun) {
        if (null != venFun) {
//            baseViewHolder.setText(R.id.tv_fun, venFun.getTitle());
            if (getItemPosition(venFun) == pickPosition) {
                baseViewHolder.getView(R.id.ventilator_main_item).setScaleX(1.6f);
                baseViewHolder.getView(R.id.ventilator_main_item).setScaleY(1.6f);
                ShapeBlurView blurView = baseViewHolder.getView(R.id.tv_fun_name);
//                blurView.refreshView(ShapeBlurView.build(mContext).setDownSampleFactor(4*1.6f));
                if (1 == pickPosition) {
                    imgAnimation.setDuration(4000);
                    baseViewHolder.getView(R.id.iv_fun).startAnimation(imgAnimation);
                } else if (2 == pickPosition) {
                    imgAnimation.setDuration(4000);
                    baseViewHolder.getView(R.id.iv_fun).startAnimation(imgAnimation);
                } else if (3 == pickPosition) {
                    imgAnimation.setDuration(2000);
                    baseViewHolder.getView(R.id.iv_fun).startAnimation(imgAnimation);
                }
            } else {
                baseViewHolder.getView(R.id.ventilator_main_item).setScaleX(1.0f);
                baseViewHolder.getView(R.id.ventilator_main_item).setScaleY(1.0f);
                baseViewHolder.getView(R.id.iv_fun).clearAnimation();
                imgAnimation.cancel();
            }
            baseViewHolder.setImageResource(R.id.iv_fun, venFun.iconRes);
        }
    }

    private Bitmap blur(Bitmap bitmap, float radius) {
        Bitmap output = Bitmap.createBitmap(bitmap); // 创建输出图片
        RenderScript rs = RenderScript.create(mContext); // 构建一个RenderScript对象
        ScriptIntrinsicBlur gaussianBlue = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs)); // 创建高斯模糊脚本
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap); // 创建用于输入的脚本类型
        Allocation allOut = Allocation.createFromBitmap(rs, output); // 创建用于输出的脚本类型
        gaussianBlue.setRadius(radius); // 设置模糊半径，范围0f<radius<=25f
        gaussianBlue.setInput(allIn); // 设置输入脚本类型
        gaussianBlue.forEach(allOut); // 执行高斯模糊算法，并将结果填入输出脚本类型中
        allOut.copyTo(output); // 将输出内存编码为Bitmap，图片大小必须注意
        rs.destroy(); // 关闭RenderScript对象，API>=23则使用rs.releaseAllContexts()
        return output;
    }
}
