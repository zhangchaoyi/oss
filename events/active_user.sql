DELIMITER |
DROP EVENT IF EXISTS egg_log.event_active_user |
CREATE EVENT IF NOT EXISTS egg_log.event_active_user
ON SCHEDULE EVERY 1 DAY  STARTS '2016-09-06 03:00:00'
ON COMPLETION PRESERVE
DO

BEGIN 
	declare ios_dau int(11);
    declare android_dau int(11); 
    declare windows_dau int(11);
    
    declare ios_wau int(11);
    declare android_wau int(11); 
    declare windows_wau int(11);
    
    declare ios_mau int(11);   
    declare android_mau int(11);
	declare windows_mau int(11);
     
    -- 当天的日活跃玩家
    select count(*) into ios_dau from (select distinct account from login where login_time >= date_sub(curdate(),interval 1 day) and login_time < curdate()) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os="iOS";
    select count(*) into android_dau from (select distinct account from login where login_time >= date_sub(curdate(),interval 1 day) and login_time < curdate()) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os="android";
    select count(*) into windows_dau from (select distinct account from login where login_time >= date_sub(curdate(),interval 1 day) and login_time < curdate()) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os="windows";
    -- 当天的周活跃玩家
    select count(*) into ios_wau from (select distinct account from login where login_time >= date_sub(curdate(),interval 7 day) and login_time < curdate()) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os="iOS";
    select count(*) into android_wau from (select distinct account from login where login_time >= date_sub(curdate(),interval 7 day) and login_time < curdate()) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os="android";
    select count(*) into windows_wau from (select distinct account from login where login_time >= date_sub(curdate(),interval 7 day) and login_time < curdate()) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os="windows";
    -- 当天的月活跃玩家
	select count(*) into ios_mau from (select distinct account from login where login_time >= date_sub(curdate(),interval 1 month) and login_time < curdate()) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os="iOS";
    select count(*) into android_mau from (select distinct account from login where login_time >= date_sub(curdate(),interval 1 month) and login_time < curdate()) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os="android";
    select count(*) into windows_mau from (select distinct account from login where login_time >= date_sub(curdate(),interval 1 month) and login_time < curdate()) A join create_role B on A.account = B.account join device_info C on B.openudid = C.openudid where C.os="windows";

	-- 插入实体表
    insert into active_user (date, dau, wau, mau, os) values (date_sub(curdate(),interval 1 day), ios_dau, ios_wau, ios_mau, "iOS");
	insert into active_user (date, dau, wau, mau, os) values (date_sub(curdate(),interval 1 day), android_dau, android_wau, android_mau, "android");
    insert into active_user (date, dau, wau, mau, os) values (date_sub(curdate(),interval 1 day), windows_dau, windows_wau, windows_mau, "windows");
END |
DELIMITER ;
