# 集中式模板管理使用说明

## 概述

本项目采用集中式模板管理架构，所有子项目的启动脚本和Maven Wrapper文件都通过符号链接指向统一的模板目录，实现配置的集中管理和维护。

## 目录结构

```
templates/
├── maven-wrapper/          # Maven Wrapper模板文件
│   ├── .mvn/
│   │   └── wrapper/
│   ├── mvnw                # Unix/Linux脚本
│   └── mvnw.cmd            # Windows脚本
├── startup-scripts/        # 启动脚本模板
│   ├── start-template.bat  # Windows启动脚本模板
│   └── start-template.sh   # Unix/Linux启动脚本模板
├── setup-symlinks.bat      # Windows符号链接部署脚本
├── setup-symlinks.sh       # Unix/Linux符号链接部署脚本
└── README.md              # 本说明文档
```

## 使用方法

### 1. 部署符号链接到子项目

在需要部署模板的子项目根目录中执行以下命令：

**Windows系统：**
```cmd
cd d:\daily-discover\daily-discover-server\daily-discover-common\templates
setup-symlinks.bat <目标项目路径>
```

**Unix/Linux系统：**
```bash
cd /d/daily-discover/daily-discover-server/daily-discover-common/templates
./setup-symlinks.sh <目标项目路径>
```

例如，为`daily-discover-user`项目部署符号链接：
```bash
./setup-symlinks.sh /d/daily-discover/daily-discover-server/daily-discover-user
```

### 2. 脚本功能说明

#### 符号链接部署脚本 (`setup-symlinks.sh` / `setup-symlinks.bat`)

- 自动创建以下文件的符号链接：
  - `mvnw` -> 指向模板目录的mvnw文件
  - `mvnw.cmd` -> 指向模板目录的mvnw.cmd文件
  - `start.bat` -> 指向模板目录的start-template.bat文件
  - `start.sh` -> 指向模板目录的start-template.sh文件
  - `.mvn/wrapper/maven-wrapper.properties` -> 指向模板目录的对应文件

- 自动备份现有文件（添加`.backup`后缀）
- 验证目标目录是否存在
- 提供详细的执行日志

#### 启动脚本模板 (`start-template.bat` / `start-template.sh`)

**功能特性：**
- 自动检测Java环境
- 支持独立模式运行（添加`standalone`参数）
- 自动编译项目
- 使用Spring Boot Maven插件启动服务
- 提供中文友好的输出信息

**使用方法：**
```bash
# 普通模式启动
./start.sh

# 独立模式启动
./start.sh standalone
```

### 3. 模板文件定制

如果需要修改模板文件，请直接在`templates`目录下修改对应的模板文件，所有使用符号链接的子项目将自动获得更新。

**可定制的模板文件：**
- `maven-wrapper/mvnw` - Maven Wrapper Unix脚本
- `maven-wrapper/mvnw.cmd` - Maven Wrapper Windows脚本
- `startup-scripts/start-template.bat` - Windows启动脚本
- `startup-scripts/start-template.sh` - Unix启动脚本
- `maven-wrapper/.mvn/wrapper/maven-wrapper.properties` - Maven配置

### 4. 验证符号链接状态

在子项目根目录执行以下命令验证符号链接：

**Unix/Linux系统：**
```bash
ls -la | grep -E "(mvnw|start\.)"
```

**Windows系统：**
```cmd
dir | findstr "mvnw start"
```

正常输出应显示符号链接指向模板目录。

## 优势

1. **统一管理** - 所有项目的配置集中维护
2. **一致性** - 确保所有项目使用相同的配置版本
3. **易于更新** - 修改模板文件即可更新所有项目
4. **减少冗余** - 避免重复的配置文件
5. **版本控制友好** - 模板文件统一版本管理

## 注意事项

- 部署符号链接前请确保目标项目目录存在
- 脚本会自动备份现有文件，但建议手动备份重要文件
- 符号链接在不同操作系统间可能有兼容性问题
- 确保模板目录的路径正确配置在部署脚本中