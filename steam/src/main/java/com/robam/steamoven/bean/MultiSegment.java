package com.robam.steamoven.bean;

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
    public String temperature;

    public int modelNum = 0;

    public MultiSegment() {

    }

    public MultiSegment(Parcel in) {
        no = in.readInt();
        model = in.readString();
        duration = in.readString();
        temperature = in.readString();
        modelNum = in.readInt();
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
        parcel.writeString(temperature);
        parcel.writeInt(modelNum);
    }
}
