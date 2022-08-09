package com.robam.cabinet.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class CabFunBean implements Parcelable {
    public int funtionCode ;
    public String funtionName ;
    public String backgroundImg ;
    public String mode ;
    public String into ;

    private CabFunBean(Parcel in) {
        funtionCode = in.readInt();
        funtionName = in.readString();
        backgroundImg = in.readString();
//        mode = in.readArrayList(Integer.class.getClassLoader());
        mode = in.readString();
        into = in.readString();
    }

    public CabFunBean(int funtionCode, String funtionName, String backgroundImg, String mode, String into) {
        this.funtionCode = funtionCode;
        this.funtionName = funtionName;
        this.backgroundImg = backgroundImg;
        this.mode = mode;
        this.into = into;
    }

    public static final Creator<CabFunBean> CREATOR = new Creator<CabFunBean>() {
        @Override
        public CabFunBean createFromParcel(Parcel in) {
            return new CabFunBean(in);
        }

        @Override
        public CabFunBean[] newArray(int size) {
            return new CabFunBean[size];
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
