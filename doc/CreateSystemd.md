##  創建文件  /etc/systemd/system/amazonPay.service
[Unit]
Description=amazonPay

[Service]
User=root           
ExecStart=java -jar /opt/ifey/jar/ruoyi-admin.jar --spring.profiles.active=prod
SuccessExitStatus=143
Restart=always
RestartSec=10
ExecStop=/bin/kill -15 $MAINPID

[Install]
WantedBy=multi-user.target



保存并关闭文件。
Description：描述你的应用程序。
User：将其替换为你的Linux用户。
ExecStart：指定启动应用程序的命令。确保将路径替换为你的JAR文件路径。
ExecStop：指定停止应用程序的命令。


重载Systemd配置：
一旦创建了Unit文件，需要通知Systemd重新加载配置，以便它能够识别你的新服务单元。运行以下命令：
sudo systemctl daemon-reload

启动Spring应用程序：
sudo systemctl start amazonPay.service
//关闭开机启动
systemctl disable pay-in-out-wall.service

设置开机自启动（可选）：

如果你希望Spring应用程序在系统启动时自动启动，运行以下命令：

sudo systemctl enable amazonPay.service


停止、重启和查看状态：

停止应用程序：sudo systemctl stop pay-in-out-wall
重启应用程序：sudo systemctl restart pay-in-out-wall
查看应用程序状态：sudo systemctl status pay-in-out-wall





journalctl -u pay-in-out-wall -f
journalctl -b | grep "old=1700new=1850.00,orderId=3301,old=1850.00,orderId=3301" -B 5 -A 5

journalctl -u pay-in-out-wall.service --since "2024-04-04 05:50:00" --until "2024-04-04 05:53:00"
journalctl -u pay-in-out-wall.service --since "2024-06-02 08:00:00" | grep "PayIn change mission amount"

journalctl -u pay-in-out-wall.service -f | grep "PayIn change mission amount"  //实时查看某个日志

journalctl -u pay-in-out-wall.service --since "2024-08-11 08:00:00" | grep "根据订单的金额小数去找订单返回失败userId"
journalctl -u pay-in-out-wall.service --since "2024-09-30 00:00:00" | grep "notifyMerchantPayOut " -B 5 -A 5

--查询代付通知
journalctl -u pay-in-out-wall.service --since "2024-10-08 08:00:00" | grep "O598990219948230532" -B 10 -A 10

--查询代付通知
journalctl -u pay-in-out-wall.service --since "2024-10-10 08:00:00" | grep "465025432778" -B 10 -A 10

journalctl -u pay-in-out-wall.service --since "2025-04-01 08:39:00" | grep "271781" -B 10 -A 10
journalctl -u pay-in-out-wall.service --since "2025-03-05 05:39:00" | grep "13158" -B 10 -A 10
journalctl -u pay-in-out-wall.service --since "2025-04-12 14:39:00" | grep "/api/android/payOutLimitDo:" -B 10 -A 10
journalctl -u pay-in-out-wall.service --since "2025-03-27 05:39:00" | grep "/api/android/payResultInspect:解码{" -B 10 -A 10
journalctl -u pay-in-out-wall.service --since "2025-03-27 05:39:00" | grep "/api/android/payResult:" -B 10 -A 10

journalctl -u pay-in-out-wall.service --since "2025-04-15 05:39:00" | grep "41126.0 -B 10 -A 10
--查询同一个用户分配给原来的用户
journalctl -u pay-in-out-wall.service --since "2025-03-11 06:00:00" | grep "User has been created repeat gays payin mission to user.getCustomName" -B 10 -A10


journalctl -u pay-in-out-wall.service --since "2025-03-11 06:00:00" | grep "repeat gays payout mission or order has been" -B 10 -A10

journalctl -u pay-in-out-wall.service -p info -f    -p info：显示 info 级别及以上的日志。
grep -v TRACE：过滤掉包含 TRACE 的行。-v 选项表示反向匹配，即显示不包含 TRACE 的所有行。
journalctl -u pay-in-out-wall.service -p info -f | grep -v TRACE
如果你还想排除其他级别的日志（例如 DEBUG），可以使用更复杂的 grep 过滤
journalctl -u pay-in-out-wall.service -p info -f | grep -v -E "TRACE|DEBUG"
journalctl -u pay-in-out-wall.service -p info -f | grep -v -E "INFO|DEBUG"

journalctl -u pay-in-out-wall.service --since "2025-07-22 13:00:00" | grep "loginSmsCodeVerify" -B 10 -A10
journalctl -u pay-in-out-wall.service --since "2025-07-22 14:20:00" | grep "insertSMSAssistantRecord" -B 10 -A10

journalctl -u pay-in-out-wall.service --since "2025-05-09 07:30:00" | grep "511940|Payment declined as per UPI risk policy to" -B 10 -A10
journalctl -u pay-in-out-wall.service --since "2025-05-09 07:30:00" | grep -E "511940.*Payment declined as per UPI risk policy to|Payment declined as per UPI risk policy to.*511940" -B 10 -A 10
journalctl -u pay-in-out-wall.service --since "2025-05-09 14:00:00" | grep  "446432" -B 10 -A 10

journalctl -u pay-in-out-wall.service --since "2025-04-09 08:45:56" --until "2025-04-09 08:45:56"
--查询某个订单Pending状态的日志
journalctl -u pay-in-out-wall.service --since "2025-05-09 00:00:00" | grep -E ".*payResultInspect.*Transfer Pending.*65390.*" -B 10 -A 10

journalctl -u pay-in-out-wall.service --since "2025-05-09 16:00:00" | grep -E ".*payResultInspect.*Transfer Pending" -B 10 -A 10

journalctl -u pay-in-out-wall.service --since "2025-05-09 16:00:00" | grep -E ".*payResultInspect.*493348" -B 10 -A 10


journalctl -u pay-in-out-wall.service -f --no-pager | grep -v 'userAutoLogin\|jsonGetParam'


journalctl -u pay-in-out-wall.service --since "2025-05-11 17:20:00" | grep -E "notifyMerchantPayOut.*TP675246861595206637" -B 10 -A 10

journalctl -u pay-in-out-wall.service --since "2025-07-14 08:00:00" | grep "getGatewayData No online user can receive " -B 10 -A 10
journalctl -u pay-in-out-wall.service --since "2025-06-20 7:00:00" | grep "不符合签名要求" -B 10 -A 10
--代收补单
journalctl -u pay-in-out-wall-schedule.service --since "2025-06-10 08:10:00" | grep -E "payInResult:.*602275" -B 10 -A 10


journalctl -u pay-in-out-wall.service --since "2025-07-11 13:38:00" --until "2025-07-11 13:38:20"


## 查找某个时间段的日志
journalctl -u pay-in-out-wall.service --since "2025-09-07 09:02:00" --until "2025-09-08 09:03:00" | grep  "justAddUserBalanceForLock:" -B 10 -A 10
journalctl -u pay-in-out-wall.service --since "2025-07-02 06:50:00" --until "2025-07-02 06:50:59"
journalctl -u pay-in-out-wall-schedule.service --since "2025-07-02 06:50:00" --until "2025-07-02 06:50:59"
journalctl -u pay-in-out-wall-dongge.service --since "2025-07-08 06:12:11" --until "2025-07-08 06:12:13"
journalctl -u pay-in-out-wall-schedule.service --since "2025-08-05 01:00:00" | grep  "insertSMSAssistantRecord: 林辉得短信验证登录" -B 10 -A 10
journalctl -u pay-in-out-wall.service --since "2025-11-10 01:00:00" | grep  "7845335614" -B 10 -A 10

journalctl -u pay-in-out-wall.service --since "2025-09-24 01:00:00" | grep  "getGatewayData" -B 10 -A 10

journalctl -u pay-in-out-wall.service --since "2025-10-20 12:00:00" | grep  "PayInJunPay getGatewayData" -B 30 -A 30

--since "2025-10-07 12:00:00" | grep  "https://transaction.gamecloud.vip/transaction/callback/v1/payin/null/PI202510200731521" -B 30 -A 30
