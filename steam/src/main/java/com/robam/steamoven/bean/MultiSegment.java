package com.robam.steamoven.bean;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 多段
 */
public class MultiSegment implements Parcelable {
    //段数
    public int no;
    //模式
    public String model;
    //时长
    public String duration;
    //温度
   // public String temperature;

    public int funCode = 0;

    //蒸汽量
    public String steam;

    public String defTemp;//上温度

    public String downTemp;//下温度

    private int cookState = 1;//1 默认 未开始;3(<<1) 开始烹饪; 4(<<2) 暂停烹饪;8(<<3) 烹饪结束
    public static final int COOK_STATE_START = 1 << 1;
    public static final int COOK_STATE_PAUSE = 1 << 2;
    public static final int COOK_STATE_FINISH = 1 << 3;




    public MultiSegment() {

    }


    public MultiSegment(Parcel in) {
        no = in.readInt();
        model = in.readString();
        duration = in.readString();
        funCode = in.readInt();
        steam = in.readString();
        defTemp = in.readString();
        downTemp = in.readString();
        cookState = in.readInt();
    }

    public static final Creator<MultiSegment> CREATOR = new Creator<MultiSegment>() {
        @Override
        public MultiSegment createFromParcel(Parcel in) {
            return new MultiSegment(in);
        }

        @Override
        public MultiSegment[] newArray(int size) {
            return new MultiSegment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isCooking(){
        return (cookState & COOK_STATE_START) != 0;
    }

    public boolean isPause(){
        return (cookState & COOK_STATE_PAUSE) != 0;
    }

    /**
     *是否工作过（开始、暂停、完成）
     * @return
     */
    public boolean isStart(){
        return isCooking() || isPause() || isFinish();
    }


    public boolean isFinish(){
        return (cookState & COOK_STATE_FINISH) != 0;
    }

    public void setCookState(int cookState){
        if(cookState != COOK_STATE_START && cookState != COOK_STATE_PAUSE && cookState != COOK_STATE_FINISH){
            throw new IllegalStateException(" 参数不正确,传递的参数需要是 [COOK_STATE_START、COOK_STATE_PAUSE、COOK_STATE_FINISH] 中任意一个");
        }
        this.cookState = cookState;
    }

    public void reSetCookState(){
        this.cookState = 1;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(no);
        parcel.writeString(model);
        parcel.writeString(duration);
        parcel.writeInt(funCode);
        parcel.writeString(steam);
        parcel.writeString(defTemp);
        parcel.writeString(downTemp);
        parcel.writeInt(cookState);
    }
}
