DELIMITER |
DROP EVENT IF EXISTS egg_log.event_retain_equipment |
CREATE EVENT IF NOT EXISTS egg_log.event_retain_equipment
ON SCHEDULE EVERY 1 DAY  STARTS '2016-09-24 03:00:00'
ON COMPLETION PRESERVE
DO

BEGIN
    -- 以2016-09-05 03:00:00为例 实际上计算2016-09-03的次日留存率,2016-08-28的7日留存率,2016-08-05的30日留存率

    -- 分终端统计新增设备
    declare day_before_add_equipment_ios int(11);
    declare day_before_add_equipment_android int(11);
    declare day_before_add_equipment_windows int(11);
    declare yesterday_retain_equipment_ios int(11);
    declare yesterday_retain_equipment_android int(11);
    declare yesterday_retain_equipment_windows int(11);
    
    declare retain_equipment_ios int(11);
    declare retain_equipment_android int(11);
    declare retain_equipment_windows int(11);

    declare i integer;
    declare add_equipment_date date;
    declare start_date date;
    declare row_name varchar(20);
    
    -- 以2016-09-05 03:00:00为例 计算2016-09-03 当天的新增设备数 (分终端类型)
    select count(B.openudid) into day_before_add_equipment_ios from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 2 day) and os in("iOS")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 2 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null;
    select count(B.openudid) into day_before_add_equipment_android from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 2 day) and os in("android")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 2 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null;
    select count(B.openudid) into day_before_add_equipment_windows from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 2 day) and os in("windows")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 2 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null;
 
    -- 求2016-09-03 的次日留存设备数 (分终端)  即2016-09-03 的新增设备在 09-04 的留存数
    select count(distinct D.openudid) into yesterday_retain_equipment_ios from (select distinct account from egg_log.login where date = date_sub(curdate(),interval 1 day)) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 2 day) and os in("iOS")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 2 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
    select count(distinct D.openudid) into yesterday_retain_equipment_android from (select distinct account from egg_log.login where date = date_sub(curdate(),interval 1 day)) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 2 day) and os in("android")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 2 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
    select count(distinct D.openudid) into yesterday_retain_equipment_windows from (select distinct account from egg_log.login where date = date_sub(curdate(),interval 1 day)) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 2 day) and os in("windows")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 2 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
    
    insert into egg_log.retain_equipment (date,add_equipment,first_day,os) values (date_sub(curdate(),interval 2 day),day_before_add_equipment_ios,yesterday_retain_equipment_ios,"iOS");
    insert into egg_log.retain_equipment (date,add_equipment,first_day,os) values (date_sub(curdate(),interval 2 day),day_before_add_equipment_android,yesterday_retain_equipment_android,"android");
    insert into egg_log.retain_equipment (date,add_equipment,first_day,os) values (date_sub(curdate(),interval 2 day),day_before_add_equipment_windows,yesterday_retain_equipment_windows,"windows");
    -- for循环 计算以当前 时间前一天 为基准的 每天的第三日留存/第四日留存/第五日留存......
    -- 例如计算 三天前新增的设备在昨天登录的数量/ 七天前新增的设备在昨天登录的数量 / 三十天前新增的设备在昨天登录的数量 
    set i = 3;
    WHILE i < 9 DO
    BEGIN
        set add_equipment_date = date_sub(curdate(),interval i day);
        set start_date = date_sub(curdate(),interval (i-1) day);
        set retain_equipment_ios = 0;
        set retain_equipment_android = 0;
        set retain_equipment_windows = 0;
        
        select count(distinct D.openudid) into retain_equipment_ios from (select distinct account from egg_log.login where date = date_sub(curdate(),interval 1 day)) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date and os in("iOS")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
        select count(distinct D.openudid) into retain_equipment_android from (select distinct account from egg_log.login where date = date_sub(curdate(),interval 1 day)) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date and os in("android")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
        select count(distinct D.openudid) into retain_equipment_windows from (select distinct account from egg_log.login where date = date_sub(curdate(),interval 1 day)) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date and os in("windows")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
        
        case i
            when 3 then 
				update egg_log.retain_equipment set second_day = retain_equipment_ios where date = add_equipment_date and os = "iOS"; 
				update egg_log.retain_equipment set second_day = retain_equipment_android where date = add_equipment_date and os = "android";
				update egg_log.retain_equipment set second_day = retain_equipment_windows where date = add_equipment_date and os = "windows";
            when 4 then 
				update egg_log.retain_equipment set third_day = retain_equipment_ios where date = add_equipment_date and os = "iOS"; 
				update egg_log.retain_equipment set third_day = retain_equipment_android where date = add_equipment_date and os = "android";
				update egg_log.retain_equipment set third_day = retain_equipment_windows where date = add_equipment_date and os = "windows";
            when 5 then 
				update egg_log.retain_equipment set forth_day = retain_equipment_ios where date = add_equipment_date and os = "iOS"; 
				update egg_log.retain_equipment set forth_day = retain_equipment_android where date = add_equipment_date and os = "android";
				update egg_log.retain_equipment set forth_day = retain_equipment_windows where date = add_equipment_date and os = "windows";
            when 6 then 
				update egg_log.retain_equipment set fifth_day = retain_equipment_ios where date = add_equipment_date and os = "iOS"; 
				update egg_log.retain_equipment set fifth_day = retain_equipment_android where date = add_equipment_date and os = "android";
				update egg_log.retain_equipment set fifth_day = retain_equipment_windows where date = add_equipment_date and os = "windows";
            when 7 then 
				update egg_log.retain_equipment set sixth_day = retain_equipment_ios where date = add_equipment_date and os = "iOS"; 
				update egg_log.retain_equipment set sixth_day = retain_equipment_android where date = add_equipment_date and os = "android";
				update egg_log.retain_equipment set sixth_day = retain_equipment_windows where date = add_equipment_date and os = "windows";
            when 8 then 
				update egg_log.retain_equipment set seven_day = retain_equipment_ios where date = add_equipment_date and os = "iOS"; 
				update egg_log.retain_equipment set seven_day = retain_equipment_android where date = add_equipment_date and os = "android";
				update egg_log.retain_equipment set seven_day = retain_equipment_windows where date = add_equipment_date and os = "windows";
        end case;
        
        set i = i+1;
    END;
    END WHILE;
    
    
    -- 求2016-08-22 的14天设备留存数 
    select count(distinct D.openudid) into retain_equipment_ios from (select distinct account from egg_log.login where date = date_sub(curdate(),interval 1 day)) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 15 day) and os in("iOS")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 15 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
    select count(distinct D.openudid) into retain_equipment_android from (select distinct account from egg_log.login where date = date_sub(curdate(),interval 1 day)) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 15 day) and os in("android")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 15 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
    select count(distinct D.openudid) into retain_equipment_windows from (select distinct account from egg_log.login where date = date_sub(curdate(),interval 1 day)) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 15 day) and os in("windows")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 15 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
    
    update egg_log.retain_equipment set fourteen_day = retain_equipment_ios where date = date_sub(curdate(),interval 15 day) and os = "iOS"; 
    update egg_log.retain_equipment set fourteen_day = retain_equipment_android where date = date_sub(curdate(),interval 15 day) and os = "android";
    update egg_log.retain_equipment set fourteen_day = retain_equipment_windows where date = date_sub(curdate(),interval 15 day) and os = "windows";
    
    -- 求2016-08-06 的30天设备留存数 
    select count(distinct D.openudid) into retain_equipment_ios from (select distinct account from egg_log.login where date = date_sub(curdate(),interval 1 day)) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 31 day) and os in("iOS")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 31 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
    select count(distinct D.openudid) into retain_equipment_android from (select distinct account from egg_log.login where date = date_sub(curdate(),interval 1 day)) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 31 day) and os in("android")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 31 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
    select count(distinct D.openudid) into retain_equipment_windows from (select distinct account from egg_log.login where date = date_sub(curdate(),interval 1 day)) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 31 day) and os in("windows")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(curdate(),interval 31 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
    
    update egg_log.retain_equipment set thirty_day = retain_equipment_ios where date = date_sub(curdate(),interval 31 day) and os = "iOS"; 
    update egg_log.retain_equipment set thirty_day = retain_equipment_android where date = date_sub(curdate(),interval 31 day) and os = "android";
    update egg_log.retain_equipment set thirty_day = retain_equipment_windows where date = date_sub(curdate(),interval 31 day) and os = "windows";
    
END |
DELIMITER ;