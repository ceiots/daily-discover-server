# 电商系统数据表概览

本文档提供了电商系统所有数据表的概览，按业务模块划分。

## 1. 用户与会员系统 (user_member_tables.sql)

| 表名 | 描述 |
| --- | --- |
| user | 用户主表，存储用户的基本信息 |
| user_account | 用户账户表，管理用户的余额、积分等资金信息 |
| user_account_log | 用户账户流水表，记录用户账户的所有资金变动 |
| member_level | 会员等级表，定义会员等级体系 |
| user_member | 用户会员信息表，存储用户的会员信息 |
| user_points_log | 用户积分记录表，记录用户积分的获取和消费 |
| user_auth | 用户授权表，管理第三方登录 |
| user_favorite | 用户收藏表，记录用户收藏的商品、店铺等 |
| user_browse_history | 用户浏览历史表，记录用户的浏览行为 |

## 2. 营销与促销系统 (marketing_promotion_tables.sql)

| 表名 | 描述 |
| --- | --- |
| marketing_campaign | 营销活动表，管理平台各类促销活动 |
| campaign_product | 活动商品关联表，记录参与活动的商品 |
| coupon_template | 优惠券模板表，定义优惠券的基本信息和规则 |
| coupon_scope | 优惠券适用范围表，定义优惠券的使用范围 |
| user_coupon | 用户优惠券表，记录用户领取和使用的优惠券 |
| promotion_full_reduction | 促销满减规则表，定义满减促销规则 |
| promotion_scope | 促销范围表，定义促销活动的适用范围 |
| seckill_activity | 秒杀活动表，管理秒杀活动 |
| seckill_product | 秒杀商品表，记录参与秒杀的商品 |

## 3. 内容与评价系统 (content_review_tables.sql)

| 表名 | 描述 |
| --- | --- |
| content_category | 内容分类表，对内容进行分类管理 |
| content | 内容主表，存储文章、视频等内容的基本信息 |
| content_detail | 内容详情表，存储内容的详细信息 |
| content_tag_relation | 内容标签关联表，管理内容与标签的关系 |
| tag | 标签表，统一管理内容、商品、用户标签 |
| product_review | 商品评价表，记录用户对商品的评价 |
| product_review_reply | 商品评价回复表，记录评价的回复内容 |
| review_like | 评价点赞表，记录用户对评价的点赞 |
| content_comment | 内容评论表，记录用户对内容的评论 |
| content_like | 内容点赞表，记录用户对内容的点赞 |
| comment_like | 评论点赞表，记录用户对评论的点赞 |

## 4. 店铺与财务系统 (shop_finance_tables.sql)

| 表名 | 描述 |
| --- | --- |
| shop | 店铺信息表，存储店铺的基本信息 |
| shop_qualification | 店铺资质表，管理店铺的各类资质证明 |
| shop_rating | 店铺评分表，记录用户对店铺的评分 |
| shop_category | 店铺分类表，对店铺进行分类管理 |
| shop_category_relation | 店铺分类关联表，管理店铺与分类的关系 |
| shop_account | 店铺账户表，管理店铺的资金账户 |
| shop_account_log | 店铺账户流水表，记录店铺账户的资金变动 |
| shop_settlement | 店铺结算单表，管理平台与店铺的结算 |
| payment_record | 支付记录表，记录系统中的支付行为 |
| invoice | 发票信息表，管理订单的发票信息 |
| user_invoice_info | 用户发票信息表，管理用户的发票信息模板 |

## 5. 报表与统计系统 (report_system_tables.sql)

| 表名 | 描述 |
| --- | --- |
| sales_statistics | 销售报表统计表，汇总销售数据 |
| product_sales_statistics | 商品销售统计表，统计商品销售情况 |
| user_behavior_statistics | 用户行为统计表，分析用户行为 |
| search_keyword_statistics | 搜索关键词统计表，统计搜索词热度 |
| marketing_statistics | 营销活动统计表，分析活动效果 |
| shop_statistics | 店铺统计表，分析店铺运营情况 |
| platform_monitor | 平台运营监控表，监控平台整体运营状况 |
| user_profile_tag | 用户画像标签表，存储用户画像信息 |
| product_recommendation_log | 商品推荐记录表，记录推荐行为与效果 |

## 6. 搜索与推荐系统 (search_recommend_tables.sql)

| 表名 | 描述 |
| --- | --- |
| search_config | 搜索配置表，定义搜索的权重和排序规则 |
| hot_search_keyword | 热搜词表，管理热门搜索词 |
| search_word_dict | 搜索词库表，管理搜索的词库 |
| user_search_history | 用户搜索历史表，记录用户的搜索行为 |
| search_recommend_config | 搜索推荐配置表，配置搜索结果的推荐策略 |
| content_recommend_config | 内容推荐配置表，配置内容推荐策略 |
| product_recommend_config | 商品推荐配置表，配置商品推荐策略 |
| search_synonym | 搜索同义词表，管理搜索的同义词 |
| search_correction | 搜索纠错表，管理搜索词的纠错 |
| user_interest_tag | 用户兴趣标签表，记录用户兴趣偏好 |

## 7. 订单与购物车系统 (order_cart_tables_optimized.sql)

| 表名 | 描述 |
| --- | --- |
| cart | 购物车表，存储用户的购物车商品 |
| order | 订单主表，存储订单的基本信息 |
| order_item | 订单商品表，记录订单中的商品明细 |
| order_payment | 订单支付表，记录订单的支付信息 |
| order_address | 订单地址表，记录订单的收货地址 |
| order_log | 订单日志表，记录订单状态变更 |

## 8. 商品系统 (product_tables_optimized.sql)

| 表名 | 描述 |
| --- | --- |
| product_category | 商品分类表，管理商品分类 |
| product | 商品信息表，存储商品的基本信息 |
| product_sku | 商品SKU表，存储商品的SKU信息 |
| product_attribute | 商品属性表，定义商品的属性 |
| product_attribute_value | 商品属性值表，存储商品的属性值 |
| product_image | 商品图片表，存储商品的图片信息 |
| product_spec | 商品规格表，定义商品的规格 |
| product_spec_value | 商品规格值表，存储商品的规格值 |

## 9. 供应商与库存系统 (supplier_inventory_tables.sql)

| 表名 | 描述 |
| --- | --- |
| supplier | 供应商表，管理供应商信息 |
| supplier_product | 供应商商品表，管理供应商提供的商品 |
| inventory | 库存表，管理商品库存 |
| inventory_log | 库存日志表，记录库存变动 |
| inventory_check | 库存盘点表，管理库存盘点工作 |
| purchase_order | 采购单表，管理采购订单 |
| purchase_item | 采购单商品表，记录采购的商品明细 |
| purchase_receive | 采购入库表，管理采购商品入库 |

## 10. 物流与配送系统 (logistics_refund_tables.sql)

| 表名 | 描述 |
| --- | --- |
| logistics_company | 物流公司表，管理物流公司信息 |
| logistics_order | 物流订单表，管理发货物流订单 |
| logistics_address | 物流地址表，管理发货和收货地址 |
| logistics_track | 物流跟踪表，记录物流轨迹 |
| refund_apply | 退款申请表，管理退款申请 |
| refund_process | 退款处理表，记录退款处理过程 |
| refund_logistics | 退款物流表，管理退货物流 |
| refund_payment | 退款支付表，记录退款支付信息 |
| refund_item | 退货商品表，记录退货的商品明细 |
| refund_log | 退款日志表，记录退款处理日志 