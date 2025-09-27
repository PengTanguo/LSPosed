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

package org.lsposed.manager.ui.fragment;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.lsposed.manager.App;
import org.lsposed.manager.ConfigManager;
import org.lsposed.manager.R;
import org.lsposed.manager.adapters.AppManagementAdapter;
import org.lsposed.manager.databinding.FragmentAppManagementBinding;
import org.lsposed.manager.util.AppInfo;
import org.lsposed.manager.util.AppVisibilityManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import rikka.core.util.ResourceUtils;

public class AppManagementFragment extends Fragment implements AppManagementAdapter.OnAppActionListener {
    private FragmentAppManagementBinding binding;
    private AppManagementAdapter adapter;
    private List<AppInfo> appList = new ArrayList<>();
    private List<AppInfo> filteredAppList = new ArrayList<>();
    private String searchQuery = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAppManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupRecyclerView();
        setupFab();
        loadApps();
    }

    private void setupRecyclerView() {
        adapter = new AppManagementAdapter(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupFab() {
        binding.fab.setOnClickListener(v -> showAddAppDialog());
    }

    private void loadApps() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
        
        CompletableFuture.runAsync(() -> {
            try {
                // 获取所有已安装的应用
                List<PackageInfo> packages = ConfigManager.getInstalledPackagesFromAllUsers(
                    PackageManager.GET_META_DATA | PackageManager.GET_ACTIVITIES, false);
                
                appList.clear();
                for (PackageInfo pkg : packages) {
                    ApplicationInfo appInfo = pkg.applicationInfo;
                    if (appInfo != null && !appInfo.packageName.equals(getContext().getPackageName())) {
                        AppInfo app = new AppInfo();
                        app.packageName = appInfo.packageName;
                        app.label = appInfo.loadLabel(getContext().getPackageManager()).toString();
                        app.icon = appInfo.loadIcon(getContext().getPackageManager());
                        app.isSystemApp = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                        app.isEnabled = appInfo.enabled;
                        app.versionCode = pkg.versionCode;
                        app.versionName = pkg.versionName;
                        app.installTime = pkg.firstInstallTime;
                        app.updateTime = pkg.lastUpdateTime;
                        appList.add(app);
                    }
                }
                
                // 按应用名称排序
                Collections.sort(appList, (a, b) -> a.label.compareToIgnoreCase(b.label));
                
                requireActivity().runOnUiThread(() -> {
                    filterApps();
                    binding.progressBar.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                });
                
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "加载应用失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void filterApps() {
        filteredAppList.clear();
        if (TextUtils.isEmpty(searchQuery)) {
            filteredAppList.addAll(appList);
        } else {
            String query = searchQuery.toLowerCase();
            for (AppInfo app : appList) {
                if (app.label.toLowerCase().contains(query) || 
                    app.packageName.toLowerCase().contains(query)) {
                    filteredAppList.add(app);
                }
            }
        }
        adapter.updateApps(filteredAppList);
    }

    private void showAddAppDialog() {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("添加应用")
            .setMessage("选择要添加的应用")
            .setPositiveButton("从已安装应用选择", (dialog, which) -> {
                // 这里可以实现应用选择器
                Toast.makeText(requireContext(), "功能开发中...", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("取消", null)
            .show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_app_management, menu);
        
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("搜索应用...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                filterApps();
                return true;
            }
        });
        
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            loadApps();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLaunchApp(AppInfo app) {
        try {
            Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(app.packageName);
            if (intent != null) {
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "无法启动应用", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "启动应用失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onToggleVisibility(AppInfo app) {
        try {
            // 使用AppVisibilityManager检查应用状态
            AppVisibilityManager.AppVisibilityStatus status = 
                AppVisibilityManager.getAppVisibilityStatus(requireContext(), app.packageName);
            
            String statusText = getStatusText(status);
            boolean isVisible = (status == AppVisibilityManager.AppVisibilityStatus.VISIBLE);
            
            new MaterialAlertDialogBuilder(requireContext())
                .setTitle(isVisible ? "隐藏应用" : "显示应用")
                .setMessage("应用: " + app.label + "\n包名: " + app.packageName + 
                           "\n当前状态: " + statusText)
                .setPositiveButton(isVisible ? "隐藏" : "显示", (dialog, which) -> {
                    toggleAppVisibility(app, !isVisible);
                })
                .setNegativeButton("取消", null)
                .show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "操作失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private String getStatusText(AppVisibilityManager.AppVisibilityStatus status) {
        switch (status) {
            case VISIBLE:
                return "可见";
            case HIDDEN:
                return "隐藏";
            case DISABLED:
                return "已禁用";
            default:
                return "未知";
        }
    }
    
    private void toggleAppVisibility(AppInfo app, boolean show) {
        try {
            boolean success;
            if (show) {
                success = AppVisibilityManager.showAppInLauncher(requireContext(), app.packageName);
                if (success) {
                    Toast.makeText(requireContext(), "应用已显示", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "显示应用失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                success = AppVisibilityManager.hideAppFromLauncher(requireContext(), app.packageName);
                if (success) {
                    Toast.makeText(requireContext(), "应用已隐藏", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "隐藏应用失败", Toast.LENGTH_SHORT).show();
                }
            }
            
            // 刷新应用列表
            if (success) {
                loadApps();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "操作失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUninstallApp(AppInfo app) {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("卸载应用")
            .setMessage("确定要卸载应用 \"" + app.label + "\" 吗？")
            .setPositiveButton("卸载", (dialog, which) -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.setData(android.net.Uri.parse("package:" + app.packageName));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "卸载失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("取消", null)
            .show();
    }

    @Override
    public void onAppInfo(AppInfo app) {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("应用信息")
            .setMessage("应用名称: " + app.label + "\n" +
                       "包名: " + app.packageName + "\n" +
                       "系统应用: " + (app.isSystemApp ? "是" : "否") + "\n" +
                       "状态: " + (app.isEnabled ? "已启用" : "已禁用"))
            .setPositiveButton("确定", null)
            .show();
    }
}
