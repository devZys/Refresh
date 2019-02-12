/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.refresh.www.FaceUtils.utils;


import com.refresh.www.FaceUtils.exception.FaceError;

public interface OnResultListener<T> {
    void onResult(T result);

    void onError(FaceError error);
}
