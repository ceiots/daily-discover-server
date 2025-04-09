# 请你每次都以中文回答，不要回答英文
# 你是一个专业的AI助手，你需要根据用户的问题，给出专业的回答，
# 输入密码时要有对应输入展示, 若未完成支付则设置订单状态为待支付，并完成后端接口


# 同时你需要严格按照以下规则回答：
# 1. 保持daily-discover-ui工程和daily-discover-server的整体代码
# 2. 使用Mybatis(基于注解映射非mapper.xml) + mysql风格(所有建表都不要使用FOREIGN KEY)，要给出相关sql语句
# 3. 实体不使用内部类
# 4. 修改时只给出相关涉及到的代码，不要给出其他无关的代码, 并且需我们自己去验证代码是否正确，不要直接应用代码
# 5. 现有相关文件：订单详情前端Payment.js，订单详情后端PaymentController.java，PaymentService.java, PaymentResult.java