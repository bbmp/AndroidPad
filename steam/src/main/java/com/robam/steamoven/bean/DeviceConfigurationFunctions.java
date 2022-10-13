package com.robam.steamoven.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceConfigurationFunctions implements Parcelable {
    public String functionCode;

    public String functionName;

    public String backgroundImg;

    public SubView subView;

    public long id;

    protected DeviceConfigurationFunctions(Parcel in) {
        functionCode = in.readString();
        functionName = in.readString();
        backgroundImg = in.readString();
        id = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(functionCode);
        dest.writeString(functionName);
        dest.writeString(backgroundImg);
        dest.writeLong(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DeviceConfigurationFunctions> CREATOR = new Creator<DeviceConfigurationFunctions>() {
        @Override
        public DeviceConfigurationFunctions createFromParcel(Parcel in) {
            return new DeviceConfigurationFunctions(in);
        }

        @Override
        public DeviceConfigurationFunctions[] newArray(int size) {
            return new DeviceConfigurationFunctions[size];
        }
    };
}
