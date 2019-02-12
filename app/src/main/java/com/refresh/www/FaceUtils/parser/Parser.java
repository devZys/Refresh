/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.refresh.www.FaceUtils.parser;

import com.refresh.www.FaceUtils.exception.FaceError;

/**
 * JSON解析
 * @param <T>
 */
public interface Parser<T> {
    T parse(String json) throws FaceError;
}
