broadband 1.0 2014
=========

Total Mobile Solution Internet Service Web Project


demand version 1.0.3 2014-03-24

* 后天添加plan删除功能，改变状态功能，group,type, sort, status
* 继续下次出账单的时间，改为在月份单位上增加，而不是在天单位上增加 (steven)
* 后台customer inovice部分，给还没有付款的invoice添加支付功能，支付类型为罗列出来的5种(steven)
* 后台customer order detail部分，添加，一种discount detial类型，可以输入detail_name, detail_price, detail_unit, detail_expired,提交后刷新页面(steven)
* 需要支持5种，支付方式，dps-creditcard,dps-accounttoaccount,paypal,dd,topup-voucher,cash
* [google map address in company-detail table](kanny)
* [pppoe_loginname, pppoe_password, in customer order](kanny)

demand version 1.0.2 2014-03-17

* 前后台购买plan时，在checkout前，要勾选company的term condition
* 前后台增加voucher支付方式
* [添加tm_voucher表，记录所有voucher](kanny)
* [plan表添加，pstn_count字段，表明此plan默认配有几根pstn电话线，pstn_rental_amount,单条pstn的租用费用](kanny)
* [customerOrder表，添加, pstn_count,默认是plan的pstn数量，可另外添加数量，pstn_rental_amount](kanny)
* [罗列出所有provision的log](STEVEN-6)
* 在CRM下面新增添加customer功能，三个页面：customer创建页，plan创建页，invoice查看页。(STEVEN-1.1)

demand version 1.0.1 2014-03-16

* [用filter控制购买plan的几个页面](kanny)
* [给customer_order 添加一个 order due 字段，用此字段判断这个order的服务周期](STEVEN-5，字段已被Kanny添加)
* [加入最新版本的datepicker for boostrap，让所有时间字段可选](kanny)
* [添加user权限，每一个模块是一个大的权限区，每一个某块下的功能页面是一个小的功能区。此功能区域用filter控制进入各个功能页面](STEVEN-2)
* [利用chart.js做出用户注册图标，标注天，周，月，用户注册情况](STEVEN-4)
* 在customer登入后，可以进行支付，topup
* 用户可以对自己购买的plan，进行change, cancel, stop等操作
* 后台开始编写，创建customer, 创建customer的plan, 管理员可帮助customer进行支付操作(STEVEN-1)
* [完善各类email模版，SMS模版](STEVEN-3)
* 用户，注册成功，make payment成功，top up成功，change, cancel, stop他的order，需要发送email和sms
* 管理员帮助客户支付，启动某一个用户的order, change, cancel, stop，需要发送email
* 创建post_pay类型的order, 加入pstn电话的相关表
* [调用google map api，开始支持地址自动匹配](steven)
