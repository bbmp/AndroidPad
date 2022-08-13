package com.robam.stove.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class StoveFunBean implements Parcelable {
    public int funtionCode ;
    public String funtionName ;
    public String backgroundImg ;
    public String mode ;
    public String into ;

    private StoveFunBean(Parcel in) {
        funtionCode = in.readInt();
        funtionName = in.readString();
        backgroundImg = in.readString();
//        mode = in.readArrayList(Integer.class.getClassLoader());
        mode = in.readString();
        into = in.readString();
    }

    public StoveFunBean(int funtionCode, String funtionName, String backgroundImg, String mode, String into) {
        this.funtionCode = funtionCode;
        this.funtionName = funtionName;
        this.backgroundImg = backgroundImg;
        this.mode = mode;
        this.into = into;
    }

    public static final Parcelable.Creator<StoveFunBean> CREATOR = new Parcelable.Creator<StoveFunBean>() {
        @Override
        public StoveFunBean createFromParcel(Parcel in) {
            return new StoveFunBean(in);
        }

        @Override
        public StoveFunBean[] newArray(int size) {
            return new StoveFunBean[size];
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
