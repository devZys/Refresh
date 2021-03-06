/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.refresh.www.FaceUtils.parser;

import android.util.Log;

import com.refresh.www.FaceUtils.exception.FaceError;
import com.refresh.www.FaceUtils.model.RegResult;
import com.refresh.www.UiShowUtils.PopMessageUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class RegResultParser implements Parser<RegResult> {


    @Override
    public RegResult parse(String json) throws FaceError {
        PopMessageUtil.Log("oarse:" + json);
        try {
            JSONObject jsonObject = new JSONObject(json);

            if (jsonObject.has("error_code")) {
                FaceError error = new FaceError(jsonObject.optInt("error_code"), jsonObject.optString("error_msg"));
                if (error.getErrorCode() != 0) {
                    throw error;
                }
            }

            RegResult result = new RegResult();
            result.setLogId(jsonObject.optLong("log_id"));
            result.setJsonRes(json);

            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            FaceError error = new FaceError(FaceError.ErrorCode.JSON_PARSE_ERROR, "Json parse error:" + json, e);
            throw error;
        }
    }
}
