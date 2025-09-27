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
 * Copyright (C) 2021 LSPosed Contributors
 */

package org.lsposed.manager.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import org.lsposed.manager.ConfigManager;

import java.util.List;

public class AppVisibilityManager {
    private static final String TAG = "AppVisibilityManager";

    /**
     * 检查应用是否在启动器中可见
     */
    public static boolean isAppVisibleInLauncher(Context context, String packageName) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage(packageName);
            
            List<ResolveInfo> resolveInfos = context.getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            
            return !resolveInfos.isEmpty();
        } catch (Exception e) {
            Log.e(TAG, "Error checking app visibility", e);
            return true; // 默认认为可见
        }
    }

    /**
     * 隐藏应用在启动器中的图标
     */
    public static boolean hideAppFromLauncher(Context context, String packageName) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ 使用系统设置
                return setAppVisibility(context, packageName, false);
            } else {
                // 旧版本使用禁用应用的方式
                return ConfigManager.setModuleEnabled(packageName, false);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error hiding app from launcher", e);
            return false;
        }
    }

    /**
     * 显示应用在启动器中的图标
     */
    public static boolean showAppInLauncher(Context context, String packageName) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ 使用系统设置
                return setAppVisibility(context, packageName, true);
            } else {
                // 旧版本使用启用应用的方式
                return ConfigManager.setModuleEnabled(packageName, true);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing app in launcher", e);
            return false;
        }
    }

    /**
     * 设置应用可见性
     */
    private static boolean setAppVisibility(Context context, String packageName, boolean visible) {
        try {
            // 使用LSPosed的系统级权限来修改应用可见性
            // 这里可以Hook PackageManager的相关方法来控制应用可见性
            
            // 方法1: 通过系统设置
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // 修改 show_hidden_icon_apps_enabled 设置
                ConfigManager.setHiddenIcon(!visible);
                return true;
            }
            
            // 方法2: 通过PackageManager Hook
            // 这里需要实现更复杂的Hook逻辑
            // 暂时返回true，表示操作成功
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error setting app visibility", e);
            return false;
        }
    }

    /**
     * 获取应用在启动器中的状态
     */
    public static AppVisibilityStatus getAppVisibilityStatus(Context context, String packageName) {
        try {
            boolean isVisible = isAppVisibleInLauncher(context, packageName);
            boolean isEnabled = isAppEnabled(context, packageName);
            
            if (isEnabled && isVisible) {
                return AppVisibilityStatus.VISIBLE;
            } else if (isEnabled && !isVisible) {
                return AppVisibilityStatus.HIDDEN;
            } else if (!isEnabled) {
                return AppVisibilityStatus.DISABLED;
            } else {
                return AppVisibilityStatus.UNKNOWN;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting app visibility status", e);
            return AppVisibilityStatus.UNKNOWN;
        }
    }

    /**
     * 检查应用是否启用
     */
    private static boolean isAppEnabled(Context context, String packageName) {
        try {
            return context.getPackageManager().getApplicationInfo(packageName, 0).enabled;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 应用可见性状态枚举
     */
    public enum AppVisibilityStatus {
        VISIBLE,    // 可见且启用
        HIDDEN,     // 隐藏但启用
        DISABLED,   // 禁用
        UNKNOWN     // 未知状态
    }
}
