# LSPosed 应用管理功能

## 功能概述

在LSPosed项目中新增了一个完整的应用管理界面，提供以下功能：

### 主要功能

1. **获取所有应用** - 显示系统中所有已安装的应用
2. **启动应用** - 点击应用项或使用菜单启动应用
3. **显示/隐藏桌面图标** - 控制应用在启动器中的可见性
4. **卸载应用** - 通过系统卸载界面卸载应用
5. **应用信息** - 查看应用的详细信息
6. **搜索功能** - 按应用名称或包名搜索应用

### 界面特性

- **Material Design 3** 设计风格
- **响应式布局** 适配不同屏幕尺寸
- **搜索功能** 快速查找应用
- **下拉菜单** 提供更多操作选项
- **状态指示** 显示应用类型和状态

## 文件结构

### 新增文件

```
app/src/main/java/org/lsposed/manager/
├── ui/fragment/AppManagementFragment.java          # 主界面Fragment
├── adapters/AppManagementAdapter.java              # 应用列表适配器
└── util/AppVisibilityManager.java                  # 应用可见性管理器

app/src/main/res/
├── layout/
│   ├── fragment_app_management.xml                 # 主界面布局
│   └── item_app_management.xml                     # 应用项布局
├── menu/
│   ├── menu_app_management.xml                     # 主界面菜单
│   └── menu_app_actions.xml                        # 应用操作菜单
├── drawable/                                       # 新增图标资源
│   ├── ic_add.xml
│   ├── ic_launch.xml
│   ├── ic_visibility.xml
│   ├── ic_info.xml
│   ├── ic_delete.xml
│   ├── ic_more_vert.xml
│   ├── ic_search.xml
│   └── ic_refresh.xml
└── values/strings.xml                              # 新增字符串资源
```

### 修改文件

```
app/src/main/res/
├── menu/navigation_menu.xml                        # 添加应用管理导航项
├── navigation/main_nav.xml                         # 添加导航路由
└── values/strings.xml                              # 添加相关字符串
```

## 使用方法

### 1. 访问应用管理

- 启动LSPosed管理器
- 点击底部导航栏的"应用管理"图标
- 进入应用管理界面

### 2. 应用操作

#### 启动应用
- 点击应用项直接启动
- 或点击更多按钮选择"启动"

#### 显示/隐藏应用
- 点击更多按钮选择"显示/隐藏"
- 确认操作后应用图标将在启动器中显示或隐藏

#### 查看应用信息
- 点击更多按钮选择"信息"
- 查看应用名称、包名、类型和状态

#### 卸载应用
- 点击更多按钮选择"卸载"
- 确认后跳转到系统卸载界面

### 3. 搜索功能

- 点击搜索图标
- 输入应用名称或包名进行搜索
- 支持实时搜索过滤

### 4. 刷新列表

- 点击菜单中的"刷新"选项
- 重新加载所有应用列表

## 技术实现

### 应用可见性控制

使用`AppVisibilityManager`类实现应用在启动器中的显示/隐藏：

```java
// 检查应用可见性
AppVisibilityStatus status = AppVisibilityManager.getAppVisibilityStatus(context, packageName);

// 隐藏应用
boolean success = AppVisibilityManager.hideAppFromLauncher(context, packageName);

// 显示应用
boolean success = AppVisibilityManager.showAppInLauncher(context, packageName);
```

### 系统集成

- 利用LSPosed的系统级权限
- 通过ConfigManager调用系统服务
- 支持Android 10+的隐藏图标限制绕过

### 界面设计

- 使用RecyclerView实现高性能列表
- Material Design 3组件
- 响应式布局设计
- 支持深色主题

## 注意事项

1. **权限要求** - 需要LSPosed框架正常运行
2. **系统版本** - 支持Android 8.1+
3. **隐藏功能** - Android 10+需要特殊处理
4. **性能考虑** - 大量应用时建议使用搜索功能

## 扩展功能

### 可添加的功能

1. **应用分类** - 按类型、状态分类显示
2. **批量操作** - 支持多选和批量操作
3. **应用备份** - 备份应用数据和设置
4. **权限管理** - 管理应用权限
5. **启动器集成** - 与第三方启动器集成

### 自定义选项

- 修改界面主题和颜色
- 调整列表项布局
- 添加更多操作选项
- 自定义搜索逻辑

## 故障排除

### 常见问题

1. **应用列表为空** - 检查LSPosed框架状态
2. **隐藏功能无效** - 确认系统版本和权限
3. **启动失败** - 检查应用是否已安装
4. **搜索无结果** - 确认搜索关键词正确

### 调试方法

- 查看Logcat日志
- 检查ConfigManager状态
- 验证应用权限
- 测试系统服务连接

## 贡献指南

欢迎提交Issue和Pull Request来改进这个功能：

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 创建Pull Request

## 许可证

本功能遵循LSPosed项目的GPL-3.0许可证。
