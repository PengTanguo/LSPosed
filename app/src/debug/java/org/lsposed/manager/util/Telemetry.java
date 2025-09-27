/*
 * This file is part of LSPosed.
 *
 * LSPosed is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LSPosed is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LSPosed.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2022 LSPosed Contributors
 */

package org.lsposed.manager.util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.channel.AbstractChannelListener;
import com.microsoft.appcenter.channel.Channel;
import com.microsoft.appcenter.crashes.Crashes;

import org.lsposed.manager.App;
import org.lsposed.manager.BuildConfig;

import java.util.Map;

public class Telemetry {
    private static final String TAG = "Telemetry";
    private static boolean isNetworkAvailable = true;
    
    private static final Channel.Listener patchDeviceListener = new AbstractChannelListener() {
        @Override
        public void onPreparedLog(@NonNull com.microsoft.appcenter.ingestion.models.Log log, @NonNull String groupName, int flags) {
            var device = log.getDevice();
            device.setAppVersion(BuildConfig.VERSION_NAME);
            device.setAppBuild(String.valueOf(BuildConfig.VERSION_CODE));
        }
    };
    
    /**
     * 检查网络连接状态
     */
    private static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        } catch (Exception e) {
            Log.e(TAG, "检查网络状态失败", e);
        }
        return false;
    }

    private static void addPatchDeviceListener() {
        try {
            var channelField = AppCenter.class.getDeclaredField("mChannel");
            channelField.setAccessible(true);
            var channel = (Channel) channelField.get(AppCenter.getInstance());
            assert channel != null;
            channel.addListener(patchDeviceListener);
        } catch (ReflectiveOperationException e) {
            android.util.Log.e(App.TAG, "add listener", e);
        }
    }

    private static void patchDevice() {
        try {
            var handlerField = AppCenter.class.getDeclaredField("mHandler");
            handlerField.setAccessible(true);
            var handler = ((Handler) handlerField.get(AppCenter.getInstance()));
            assert handler != null;
            handler.post(Telemetry::addPatchDeviceListener);
        } catch (ReflectiveOperationException e) {
            android.util.Log.e(App.TAG, "patch device", e);
        }
    }

    public static void start(Application app) {
        try {
            // 检查网络连接状态
            isNetworkAvailable = isNetworkAvailable(app);
            
            if (!isNetworkAvailable) {
                Log.w(TAG, "网络不可用，跳过AppCenter初始化");
                return;
            }
            
            // 配置AppCenter
            AppCenter.configure(app, "eb3c4175-e879-4312-a72e-b0e64bca142c");
            
            // 设置网络超时和重试策略
            AppCenter.setNetworkRequestsAllowed(true);
            
            // 启动服务
            AppCenter.start(Analytics.class, Crashes.class);
            
            patchDevice();
            
            Log.i(TAG, "AppCenter初始化成功");
        } catch (Exception e) {
            Log.e(TAG, "AppCenter初始化失败", e);
            // 即使AppCenter初始化失败，也不影响应用正常运行
        }
    }

    public static void trackEvent(String name, Map<String, String> properties) {
        try {
            if (isNetworkAvailable && AppCenter.isConfigured()) {
                Analytics.trackEvent(name, properties);
            } else {
                Log.d(TAG, "网络不可用或AppCenter未配置，跳过事件追踪: " + name);
            }
        } catch (Exception e) {
            Log.e(TAG, "追踪事件失败: " + name, e);
        }
    }

    public static void trackError(Throwable throwable, Map<String, String> properties) {
        try {
            if (isNetworkAvailable && AppCenter.isConfigured()) {
                Crashes.trackError(throwable, properties, null);
            } else {
                Log.d(TAG, "网络不可用或AppCenter未配置，跳过错误追踪");
            }
        } catch (Exception e) {
            Log.e(TAG, "追踪错误失败", e);
        }
    }
    
    /**
     * 设置网络状态
     */
    public static void setNetworkAvailable(boolean available) {
        isNetworkAvailable = available;
        if (AppCenter.isConfigured()) {
            AppCenter.setNetworkRequestsAllowed(available);
        }
    }
}
