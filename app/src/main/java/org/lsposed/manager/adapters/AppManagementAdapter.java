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

package org.lsposed.manager.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import org.lsposed.manager.R;
import org.lsposed.manager.util.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppManagementAdapter extends RecyclerView.Adapter<AppManagementAdapter.ViewHolder> {
    private List<AppInfo> apps = new ArrayList<>();
    private OnAppActionListener listener;

    public interface OnAppActionListener {
        void onLaunchApp(AppInfo app);
        void onToggleVisibility(AppInfo app);
        void onUninstallApp(AppInfo app);
        void onAppInfo(AppInfo app);
    }

    public AppManagementAdapter(OnAppActionListener listener) {
        this.listener = listener;
    }

    public void updateApps(List<AppInfo> apps) {
        this.apps = apps;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app_management, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo app = apps.get(position);
        holder.bind(app, listener);
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView packageName;
        private TextView status;
        private ImageView moreButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.app_icon);
            name = itemView.findViewById(R.id.app_name);
            packageName = itemView.findViewById(R.id.app_package);
            status = itemView.findViewById(R.id.app_status);
            moreButton = itemView.findViewById(R.id.more_button);
        }

        public void bind(AppInfo app, OnAppActionListener listener) {
            Context context = itemView.getContext();
            
            // 设置应用图标
            if (app.icon != null) {
                icon.setImageDrawable(app.icon);
            } else {
                icon.setImageResource(android.R.drawable.sym_def_app_icon);
            }
            
            // 设置应用名称
            name.setText(app.label);
            
            // 设置包名
            packageName.setText(app.packageName);
            
            // 设置状态
            StringBuilder statusText = new StringBuilder();
            if (app.isSystemApp) {
                statusText.append("系统应用");
            } else {
                statusText.append("用户应用");
            }
            if (!app.isEnabled) {
                statusText.append(" (已禁用)");
            }
            status.setText(statusText.toString());
            
            // 设置点击事件
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLaunchApp(app);
                }
            });
            
            // 设置更多按钮
            moreButton.setOnClickListener(v -> showPopupMenu(v, app, listener));
        }

        private void showPopupMenu(View anchor, AppInfo app, OnAppActionListener listener) {
            PopupMenu popup = new PopupMenu(anchor.getContext(), anchor);
            popup.getMenuInflater().inflate(R.menu.menu_app_actions, popup.getMenu());
            
            popup.setOnMenuItemClickListener(item -> {
                if (listener == null) return false;
                
                int itemId = item.getItemId();
                if (itemId == R.id.action_launch) {
                    listener.onLaunchApp(app);
                } else if (itemId == R.id.action_toggle_visibility) {
                    listener.onToggleVisibility(app);
                } else if (itemId == R.id.action_uninstall) {
                    listener.onUninstallApp(app);
                } else if (itemId == R.id.action_info) {
                    listener.onAppInfo(app);
                }
                return true;
            });
            
            popup.show();
        }
    }
}
