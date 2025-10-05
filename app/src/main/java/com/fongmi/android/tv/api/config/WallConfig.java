package com.fongmi.android.tv.api.config;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.fongmi.android.tv.App;
import com.fongmi.android.tv.R;
import com.fongmi.android.tv.Setting;
import com.fongmi.android.tv.bean.Config;
import com.fongmi.android.tv.event.RefreshEvent;
import com.fongmi.android.tv.impl.Callback;
import com.fongmi.android.tv.utils.FileUtil;
import com.fongmi.android.tv.utils.ImgUtil;
import com.fongmi.android.tv.utils.Notify;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Asset;
import com.github.catvod.utils.Path;

import java.io.File;
import java.io.IOException;

public class WallConfig {

    private Drawable drawable;
    private Config config;
    private boolean sync;

    private static class Loader {
        static volatile WallConfig INSTANCE = new WallConfig();
    }

    public static WallConfig get() {
        return Loader.INSTANCE;
    }

    public static String getUrl() {
        return get().getConfig().getUrl();
    }

    public static String getDesc() {
        return get().getConfig().getDesc();
    }

    public static Drawable drawable(Drawable drawable) {
        if (get().drawable != null) return drawable;
        get().setDrawable(drawable);
        return drawable;
    }

    public static void load(Config config, Callback callback) {
        get().clear().config(config).load(callback);
    }

    public WallConfig init() {
        return config(Config.wall());
    }

    public WallConfig config(Config config) {
        this.config = config;
        if (config.getUrl() == null) return this;
        this.sync = config.getUrl().equals(VodConfig.get().getWall());
        return this;
    }

    public WallConfig clear() {
        this.config = null;
        return this;
    }

    public Config getConfig() {
        return config == null ? Config.wall() : config;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public void load(Callback callback) {
        App.execute(() -> loadConfig(callback));
    }

    /* 写死壁纸：每次把 res/raw/app_bg.jpg 拷贝到壁纸目录 */
    private void loadConfig(Callback callback) {
        try {
            File wallFile = write(FileUtil.getWall(0));   // 0 表示壁纸
            refresh(0);                                   // 立即生效
            App.post(callback::success);
        } catch (Throwable e) {
            App.post(() -> callback.error(Notify.getError(R.string.error_config_parse, e)));
            e.printStackTrace();
        }
    }

    /* 不再联网/解析，仅把内置图片输出到目标文件 */
    private File write(File file) throws IOException {
        Path.copy(App.get().getResources().openRawResource(R.raw.app_bg), file);
        return file;
    }

    public boolean needSync(String url) {
        return sync || TextUtils.isEmpty(config.getUrl()) || url.equals(config.getUrl());
    }

    public static void refresh(int index) {
        Setting.putWall(index);
        RefreshEvent.wall();
    }
}