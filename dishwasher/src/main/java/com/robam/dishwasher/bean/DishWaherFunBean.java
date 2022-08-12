package com.robam.dishwasher.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class DishWaherFunBean implements Parcelable {
    public int funtionCode ;
    public String funtionName ;
    public String backgroundImg ;
    public String mode ;
    public String into ;

    private DishWaherFunBean(Parcel in) {
        funtionCode = in.readInt();
        funtionName = in.readString();
        backgroundImg = in.readString();
//        mode = in.readArrayList(Integer.class.getClassLoader());
        mode = in.readString();
        into = in.readString();
    }

    public DishWaherFunBean(int funtionCode, String funtionName, String backgroundImg, String mode, String into) {
        this.funtionCode = funtionCode;
        this.funtionName = funtionName;
        this.backgroundImg = backgroundImg;
        this.mode = mode;
        this.into = into;
    }

    public static final Creator<DishWaherFunBean> CREATOR = new Creator<DishWaherFunBean>() {
        @Override
        public DishWaherFunBean createFromParcel(Parcel in) {
            return new DishWaherFunBean(in);
        }

        @Override
        public DishWaherFunBean[] newArray(int size) {
            return new DishWaherFunBean[size];
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
