broadband 1.0 2014
=========

Total Mobile Solution Internet Service Web Project


demand version 1.0.1 2014-03-16

* 用filter控制购买plan的几个页面
* 给customer_order 添加一个 order due 字段，用此字段判断这个order的服务周期
* 加入最新版本的datepicker for boostrap，让所有时间字段可选
* 添加user权限，每一个模块是一个大的权限区，每一个某块下的功能页面是一个小的功能区。此功能区域用filter控制进入各个功能页面
* 利用chart.js做出用户注册图标，标注天，周，月，用户注册情况
* 在customer登入后，可以进行支付，topup
* 用户可以对自己购买的plan，进行change, cancel, stop等操作
* 后台开始编写，创建customer, 创建customer的plan, 管理员可帮助customer进行支付操作
* 完善各类email模版，SMS模版
* 用户，注册成功，make payment成功，top up成功，change, cancel, stop他的order，需要发送email和sms
* 管理员帮助客户支付，启动某一个用户的order, change, cancel, stop，需要发送email
* 创建post_pay类型的order, 加入pstn电话的相关表
* 调用google map api，开始支持地址自动匹配
