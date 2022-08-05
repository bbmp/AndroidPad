package com.robam.cabinet.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class FunctionBean implements Parcelable {
    public int funtionCode ;
    public String funtionName ;
    public String backgroundImg ;
    public String mode ;
    public String into ;

    private FunctionBean(Parcel in) {
        funtionCode = in.readInt();
        funtionName = in.readString();
        backgroundImg = in.readString();
//        mode = in.readArrayList(Integer.class.getClassLoader());
        mode = in.readString();
        into = in.readString();
    }

    public static final Creator<FunctionBean> CREATOR = new Creator<FunctionBean>() {
        @Override
        public FunctionBean createFromParcel(Parcel in) {
            return new FunctionBean(in);
        }

        @Override
        public FunctionBean[] newArray(int size) {
            return new FunctionBean[size];
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
