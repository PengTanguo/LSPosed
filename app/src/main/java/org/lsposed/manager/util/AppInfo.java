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

import android.graphics.drawable.Drawable;

public class AppInfo {
    public String packageName;
    public String label;
    public Drawable icon;
    public boolean isSystemApp;
    public boolean isEnabled;
    public long versionCode;
    public String versionName;
    public long installTime;
    public long updateTime;
    
    public AppInfo() {
        this.packageName = "";
        this.label = "";
        this.icon = null;
        this.isSystemApp = false;
        this.isEnabled = true;
        this.versionCode = 0;
        this.versionName = "";
        this.installTime = 0;
        this.updateTime = 0;
    }
    
    public AppInfo(String packageName, String label, Drawable icon, boolean isSystemApp, boolean isEnabled) {
        this.packageName = packageName;
        this.label = label;
        this.icon = icon;
        this.isSystemApp = isSystemApp;
        this.isEnabled = isEnabled;
        this.versionCode = 0;
        this.versionName = "";
        this.installTime = 0;
        this.updateTime = 0;
    }
}
