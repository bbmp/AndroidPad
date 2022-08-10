package com.robam.pan.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class PanFunBean implements Parcelable {
    public int funtionCode ;
    public String funtionName ;
    public String backgroundImg ;
    public String mode ;
    public String into ;

    private PanFunBean(Parcel in) {
        funtionCode = in.readInt();
        funtionName = in.readString();
        backgroundImg = in.readString();
//        mode = in.readArrayList(Integer.class.getClassLoader());
        mode = in.readString();
        into = in.readString();
    }

    public PanFunBean(int funtionCode, String funtionName, String backgroundImg, String mode, String into) {
        this.funtionCode = funtionCode;
        this.funtionName = funtionName;
        this.backgroundImg = backgroundImg;
        this.mode = mode;
        this.into = into;
    }

    public static final Creator<PanFunBean> CREATOR = new Creator<PanFunBean>() {
        @Override
        public PanFunBean createFromParcel(Parcel in) {
            return new PanFunBean(in);
        }

        @Override
        public PanFunBean[] newArray(int size) {
            return new PanFunBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(funtionCode);
        parcel.writeString(funtionName);
        parcel.writeString(backgroundImg);
        parcel.writeString(mode);
        parcel.writeString(into);
    }
}
