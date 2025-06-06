# 官方账号（管理员）功能适配指南
## 1. 登录和权限检查
用户登录后，通过调用 '/user/info' 接口获取用户信息，其中包含 isOfficial 字段。当 isOfficial 为 true 时，表示该账号具有管理员权限，前端应显示
管理后台入口。
## 2. 可用API接口
- GET /admin/dashboard - 获取平台统计数据
- GET /admin/products/pending - 获取待审核商品列表
- POST /admin/products/{id}/audit - 审核商品
- GET /admin/shops/pending - 获取待审核店铺列表
- POST /admin/shops/{id}/audit - 审核店铺
- GET /admin/users - 获取所有用户列表
- POST /admin/users/{id}/set-official - 设置用户为官方账号
## 3. 前端适配建议
- 登录后通过 /user/info 获取 isOfficial 字段，若为 true，显示
管理后台入口。
- 管理后台需设计仪表盘页面，显示平台统计数据。
- 商品审核和店铺审核页面需包含审核操作按钮。
- 用户管理页面需包含设置/取消官方账号的功能。
- 所有管理员接口均需在请求头中包含有效的Authorization和userId。
