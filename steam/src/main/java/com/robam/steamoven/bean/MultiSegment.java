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

    /**
     * 是否正在烹饪
     */
    public boolean isCooking = false;
    /**
     * 是否烹饪结束
     */
    public boolean isCooked = false;

    public MultiSegment() {

    }

    public MultiSegment(Parcel in) {
        no = in.readInt();
        model = in.readString();
        duration = in.readString();
       // temperature = in.readString();
        funCode = in.readInt();
        steam = in.readString();
        defTemp = in.readString();
        downTemp = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isCooking = in.readBoolean();
            isCooked = in.readBoolean();
        }else{
            isCooking = Boolean.parseBoolean(in.readInt()+"");
            isCooked = Boolean.parseBoolean(in.readInt()+"");
        }
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

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(no);
        parcel.writeString(model);
        parcel.writeString(duration);
        //parcel.writeString(temperature);
        parcel.writeInt(funCode);
        parcel.writeString(steam);
        parcel.writeString(defTemp);
        parcel.writeString(downTemp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(isCooking);
            parcel.writeBoolean(isCooked);
        }else{
            parcel.writeInt(isCooking ? 1 : 0);
            parcel.writeInt(isCooked? 1:0);
        }
    }
}
