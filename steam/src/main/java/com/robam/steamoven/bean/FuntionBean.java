package com.robam.steamoven.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 首页功能bean
 */
public class FuntionBean implements Parcelable {
    public int funtionCode ;
    public String funtionName ;
    public String backgroundImg ;
    public String mode ;
    public String into ;

    protected FuntionBean(Parcel in) {
        funtionCode = in.readInt();
        funtionName = in.readString();
        backgroundImg = in.readString();
//        mode = in.readArrayList(Integer.class.getClassLoader());
        mode = in.readString();
        into = in.readString();
    }

    public static final Creator<FuntionBean> CREATOR = new Creator<FuntionBean>() {
        @Override
        public FuntionBean createFromParcel(Parcel in) {
            return new FuntionBean(in);
        }

        @Override
        public FuntionBean[] newArray(int size) {
            return new FuntionBean[size];
        }
    };

    public int getFuntionCode() {
        return funtionCode;
    }

    public void setFuntionCode(int funtionCode) {
        this.funtionCode = funtionCode;
    }

    public String getFuntionName() {
        return funtionName;
    }

    public void setFuntionName(String funtionName) {
        this.funtionName = funtionName;
    }

    public String getBackgroundImg() {
        return backgroundImg;
    }

    public void setBackgroundImg(String backgroundImg) {
        this.backgroundImg = backgroundImg;
    }

    public String getInto() {
        return into;
    }

    public void setInto(String into) {
        this.into = into;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(funtionCode);
        parcel.writeString(funtionName);
        parcel.writeString(backgroundImg);
//        parcel.writeList(mode);
        parcel.writeString(mode);
        parcel.writeString(into);
    }
}