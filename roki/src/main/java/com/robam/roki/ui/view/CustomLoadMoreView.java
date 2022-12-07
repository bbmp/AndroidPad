package com.robam.roki.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.loadmore.BaseLoadMoreView;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.roki.R;

import org.jetbrains.annotations.NotNull;

public class CustomLoadMoreView extends BaseLoadMoreView {
    @NotNull
    @Override
    public View getLoadComplete(@NotNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.findView(R.id.load_more_load_complete_view);
    }

    @NotNull
    @Override
    public View getLoadEndView(@NotNull BaseViewHolder baseViewHolder) {
//        TextView tvThemeTellRoki = (TextView) baseViewHolder.findView(R.id.tv_theme_tell_roki);
//        tvThemeTellRoki.setText("已经到底了~\n还有想看的？点击告诉ROKI");
//        String tellRokiText = tvThemeTellRoki.getText().toString().trim();
//        int start = 15;
//        SpannableStringBuilder ssb = new SpannableStringBuilder();
//        ssb.append(tellRokiText);
//        ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.roki_sub_color)), start, start + 6, 0);
//        tvThemeTellRoki.setText(ssb);
//        tvThemeTellRoki.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UIService.getInstance().postPage(PageKey.TellRoki);
//            }
//        });
        return baseViewHolder.findView(R.id.load_more_load_end_view);
    }

    @NotNull
    @Override
    public View getLoadFailView(@NotNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.findView(R.id.load_more_load_fail_view);
    }

    @NotNull
    @Override
    public View getLoadingView(@NotNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.findView(R.id.load_more_loading_view);
    }

    @NotNull
    @Override
    public View getRootView(@NotNull ViewGroup viewGroup) {
        return LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.roki_layout_foot_tell_roki, viewGroup, false);
    }
}
