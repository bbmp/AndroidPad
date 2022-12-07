package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;
import com.robam.steamoven.ui.dialog.SteamErrorDialog;

//除垢
public class DescalingActivity extends SteamBaseActivity {

    private ViewGroup promptParentView;
    private ViewGroup progressParentView;

    private TextView promptTvInd1,promptTvInd2,promptTvInd3;
    private TextView promptText;

    private TextView sureTv;
    private TextView progressPromptTv;
    private ProgressBar progressBar1,progressBar2,progressBar3;

    private int curTemp = 0;
    private boolean isProgress = false;


    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_descaling;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showLight();
        promptParentView = findViewById(R.id.descaling_prompt_parent);
        promptTvInd1 = findViewById(R.id.descaling_index_1);
        promptTvInd2 = findViewById(R.id.descaling_index_2);
        promptTvInd3 = findViewById(R.id.descaling_index_3);
        promptText = findViewById(R.id.descaling_text_prompt);
        sureTv = findViewById(R.id.btn_start);

        progressParentView = findViewById(R.id.descaling_progress_parent);
        progressPromptTv = findViewById(R.id.descaling_segment_prompt);
        progressBar1 = findViewById(R.id.descaling_progress_1);
        progressBar2 = findViewById(R.id.descaling_progress_2);
        progressBar3 = findViewById(R.id.descaling_progress_3);

        setOnClickListener(R.id.btn_start);
    }


    private void showLight(){
        showRightCenter();
        ((TextView)findViewById(R.id.tv_right_center)).setText(R.string.steam_light);
        ((ImageView)findViewById(R.id.iv_right_center)).setImageResource(R.drawable.steam_ic_light_max);
    }


    private void setProgressIndex(int curTemp,boolean isProgress){
        if(curTemp > 3){
            return;
        }
        if(isProgress){
            promptParentView.setVisibility(View.INVISIBLE);
            progressParentView.setVisibility(View.VISIBLE);
            for(int i = 0;i < curTemp ;i++){
                if(i == 0){
                    progressBar1.setProgress(100);
                }else if(i == 1){
                    progressBar2.setProgress(100);
                }else if(i == 2){
                    progressBar3.setProgress(100);
                }
            }
        }else{
            promptParentView.setVisibility(View.VISIBLE);
            progressParentView.setVisibility(View.INVISIBLE);
            if(curTemp ==1){
                promptTvInd1.setBackgroundResource(R.drawable.steam_indicator_selected_blue);
                promptTvInd2.setBackground(null);
                promptTvInd3.setBackground(null);
            }else if(curTemp ==2){
                promptTvInd1.setBackground(null);
                promptTvInd2.setBackgroundResource(R.drawable.steam_indicator_selected_blue);
                promptTvInd3.setBackground(null);
            }else if(curTemp == 3){
                promptTvInd1.setBackground(null);
                promptTvInd2.setBackground(null);
                promptTvInd3.setBackgroundResource(R.drawable.steam_indicator_selected_blue);
            }
        }

    }




    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        //super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            descallingFinish();
        }else if(id == R.id.btn_start){
            curTemp += 1;
            isProgress = true;
            setProgressIndex(curTemp,isProgress);
        }
    }



    public void descallingFinish(){
        SteamErrorDialog steamCommonDialog = new SteamErrorDialog(this);
        steamCommonDialog.setContentText(R.string.steam_descaling_complete);
        steamCommonDialog.setOKText(R.string.steam_common_step_complete);
        steamCommonDialog.setListeners(v -> {
            steamCommonDialog.dismiss();
            if(v.getId() == R.id.tv_ok){
                Intent intent = new Intent(DescalingActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },R.id.tv_ok);
        steamCommonDialog.show();
    }




}
