broadband 1.0.x 2014
=========

Total Mobile Solution Internet Service Web Project

###Specification

* 编写测试文档（测试数据填写规范），测试行程表（测试时间段，例如11，1，3，5点各测一轮），上报测试结果
* 所有表单提交的controller方法，如果是要页面跳转的都要redirect.
* Naming Conventions
 * Controller Function Naming Conventions (Plan)
 * planView (/plan/view/1)(get)
 * toPlanCreate (/plan/create)(get)
 * planCreate (/plan/create)(post)
 * toPlanEdit (/plan/edit/{id})(get)
 * planEdit (/plan/edit)(post)
 * planRemove (/plan/remove/{id})(get)

 
demand version 1.0.8 2014-05-06
 
 * invoice重构代码，so important!(steven)
 * [sales模块在线下单功能，在确认生成order PDF浮窗里添加一个optional_request输入框，提交sales额外请求。](steven)
 * customer order 界面加一个功能，添加new installation按钮，点击可以为该order增加金额(待定...)
 
demand version 1.0.7 2014-05-06
 
 * 写plan query，方便查询plan(kanny, 2014-05-07)
 * [比如说，那个忘记密码，发手机短信的，还是要加验证码，不然一个手机可以无限发](steven, 2014-05-06)

demand version 1.0.6 2014-04-24

* online ordering list如果该order没有credit，则显示一个添加credit的图标链接到添加credit界面
* 问keith看local list是否google analyst功能
* 数据库加一个字段，用来限制客户在没有修改随即密码的情况下频繁使用忘记密码功能(kanny)
* [制作Contact Us动态加载客户在customer的contact us界面新提交的request的功能，客户提交时需要输入验证码](steven)
* [create customer, company detail的地址框都加上google map auto complete](steven)
* customer首页下方添加follow us on(twitter, facebook, email, youtube)
* [重新完善cyberpark首页设计](kanny)
* [客户忘记密码可以点击forgotten password?来选择是通过email或sms来获取随机密码](steven)
* [sale模块下单后随机生成密码插入customer属性存入数据库并将该随即密码发送给客户](steven)

demand version 1.0.6 2014-04-21

* [company_detail创建多种类型的T&C字段，至少4个，然后每一个T&C在一个可以折叠的panel里便于修改](steven)
* [完成personal plan-term的页面](kanny)
* 重构后台创建customer页面(kanny)
* [重构sale签约界面](kanny)
* [完善各种界面的连接](kanny)
* [完善各种T&C排版和布局](kanny)
* [完善公司介绍页面](kanny)

demand version 1.0.5 2014-04-16

* [user界面权限区域每个模块前都加上全选框](STEVEN)

demand version 1.0.5 2014-04-04

* [customer下添加organization表单从customer.organization里取出数据，如果为business则显示该表单，personal则不显示] (steven)
* [sale模块加个列表如果操作的user角色为administrator则将角色为sale的user迭代进下拉菜单，如果为sale角色则屏蔽下拉菜单其只能查看自己的signed和unsigned的order和credit PDF] (steven)
* [修改前端，注册购买页面，用mobile and email代替登入](kanny)
* [给购买流添加导航](kanny)
* 更换dps支付页面
* 修改用户登入后所看到的界面
* [order information界面，添加属性，可以下载签约的PDF] (steven)

demand version 1.0.4 2014-04-04

* [customer order表添加一个客户签字的字段signature，已签字=signed、未签字=unsigned](kanny)

demand version 1.0.4 2014-04-03

* [customer order detail里voip加和pstn一样的修改号码的按钮和功能] (steven)
* [界面上在customer order detail在table处用style将字体设为12px](kanny)
* [添加organization表，字段：org_type、org_trading_name、org_register_no、org_incoporate_date、org_trading_months](kanny)
* [organization表再加holder相关字段：holder_name、holder_job_title、holder_phone、holder_email](kanny)
* [organization表添加一个customer_id字段，和customer表关联，如果customer是business类型则可以通过id到organization表里查出相应数据](kanny)

demand version 1.0.4 2014-04-02

* [界面上补全customer以及customer order新添的字段] (steven)
* [customer 加字段](kanny)
* [customerOrder 加字段](kanny)
* [添加customer credit表](kanny)

demand version 1.0.4 2014-03-31

* [customer update按钮旁加个delete customer按钮，提示操作员该操作将永久删除所有跟客户相关的信息，（customer、order、order detail、invoice、invoice detail、transaction）](STEVEN)
* [在detail处判断detail_type为pstn类型的行如果pstn_number不为空则在detail_name后显示(pstn_number)否则显示(Number is Empty)](STEVEN)
* [在customer order下的detail里detail_type为pstn则在后面加个Update PSTN Number来修改pstn_number字段](STEVEN)
* 在customer order标题加个Add Order跳转到create customer的第二个界面，选plan然后继续下单(STEVEN)
* [制作与term里图像一样布局的PDF，用以存放下单后生成的用户信息及订单细目](STEVEN)
* [填写好支付信息后点Next生成PDF，供客户签字，之后通过一个上传的界面上传至系统，跟相关order关联](STEVEN)
* 选择支付方式后跳转到填写支付信息界面
* online-ordering界面：界面1（填写基本信息），界面2（选plan），界面3（confirm界面，列出order的信息，显示选择Credit Card付款）
* [添加sale模块] (steven)
* [给User添加sale权限] (steven)
* order_status字段添加两个状态：stop，close
* VoIP信息
* [plan的NON-NAKED改用CLOTHING](STEVEN)

demand version 1.0.4 2014-03-28

* [修改provision的order层显示，压缩customer信息，order信息排版和customer里的一致，order detail显示字段和customer里一致但多一个操作列](steven)
* [user权限限制有bug, tm的chart没有勾选上，一样有权利进入观看](steven)
* [把后台登入换成ajax](kanny)
* [把plan，hareware功能换成ajax](steven)
* customer支付账单功能，user支付order功能
* 设定cyberpark的term condition
* 设计about cyberpark界面

demand version 1.0.4 2014-03-27

* [屏蔽下一次自动生成账单的函数，用createInvoicePDF测试，是否可以代替](steven)
* [添加删除discount按钮的提示，提示信息为remove](steven)
* [修改service given 的 edit bug](steven)
* [给customer order detial，的order due,添加一个，日历input，不要忘了插入provision记录](steven)
* [给customer order detial，的order status，添加一个，下拉选择框，这样管理员就可以某一张订单的状态，不要忘了插入provision记录](steven)
* [修正后台创建用户下单时一些detail没有显示price和unit值的问题，price为null则0d，unit为null则1](steven)

demand version 1.0.3 2014-03-24

* [后台添加hardware删除功能](steven)
* [后天添加plan删除功能，改变状态功能，group,type, sort, status](steven)
* [继续下次出账单的时间，改为在月份单位上增加，而不是在天单位上增加](steven)
* [后台customer inovice部分，给还没有付款的invoice添加支付功能，支付类型为罗列出来的5种](steven)
* [后台customer order detail部分，添加，一种discount detial类型，可以输入detail_name, detail_price, detail_unit, detail_expired,提交后刷新页面](steven)
* 需要支持5种，支付方式，dps-creditcard,dps-accounttoaccount,paypal,dd,topup-voucher,cash
* [google map address in company-detail table](kanny)
* [pppoe_loginname, pppoe_password, in customer order](kanny)

demand version 1.0.2 2014-03-17

* [前后台购买plan时，在checkout前，要勾选company的term condition](kanny)
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
* [在customer登入后，可以进行支付，topup](kanny)
* 用户可以对自己购买的plan，进行change, cancel, stop等操作(kanny)
* [后台开始编写，创建customer, 创建customer的plan, 管理员可帮助customer进行支付操作](STEVEN-1)
* [完善各类email模版，SMS模版](STEVEN-3)
* [用户，注册成功，make payment成功，top up成功，change, cancel, stop他的order，需要发送email和sms](kanny)
* [管理员帮助客户支付，启动某一个用户的order, change, cancel, stop，需要发送email](kanny)
* [创建post_pay类型的order, 加入pstn电话的相关表](kanny)
* [调用google map api，开始支持地址自动匹配](steven)
