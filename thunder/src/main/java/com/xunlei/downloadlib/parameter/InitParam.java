package com.xunlei.downloadlib.parameter;

import android.util.Base64;

import com.xunlei.downloadlib.android.XLUtil;

public class InitParam {

    public String mAppKey;
    public String mAppVersion;
    public int mPermissionLevel;
    public int mQueryConfOnInit;
    public String mStatSavePath;
    public String mStatCfgSavePath;

    public InitParam(String path) {
        this.mAppKey = "xzNjAwMQ^^yb==0^852^083dbcff^cee25055f125a2fde";
        this.mAppVersion = "21.01.07.800002";
        this.mPermissionLevel = 1;
        this.mQueryConfOnInit = 0;
        this.mStatSavePath = path;
        this.mStatCfgSavePath = path;
    }

    public String getSoKey() {
        try {
            String[] split = mAppKey.split("==");
            String replace = split[0].replace('^', '=');
            String str = new String(Base64.decode(replace.substring(2, replace.length() - 2), 0), "UTF-8");
            return XLUtil.generateAppKey("com.android.providers.downloads", Short.parseShort(str.split(";")[0]), (byte) 1);
        } catch (Exception e) {
            return "";
        }
    }
}
