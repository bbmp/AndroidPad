package com.robam.common.http;

import com.robam.common.bean.BaseResponse;

public interface RetrofitCallback<T extends BaseResponse> {

    void onSuccess(T t);

    void onFaild(String err);
}
