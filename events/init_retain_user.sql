DELIMITER |
DROP EVENT IF EXISTS egg_log.event_init_retain_user |
CREATE EVENT IF NOT EXISTS egg_log.event_init_retain_user
ON SCHEDULE EVERY 1 DAY 
ON COMPLETION PRESERVE
DO

BEGIN 
	-- 以2016-09-05 03:00:00为例 实际上计算2016-09-03的次日留存率,2016-08-28的7日留存率,2016-08-05的30日留存率
    -- 用于进行 retain_user 的历史恢复数据
    -- 定义分终端次日留存
	declare day_before_add_players_ios int(11);
    declare day_before_add_players_android int(11);
    declare day_before_add_players_windows int(11);
    -- 定义分终端次日留存
    declare yesterday_retain_players_ios int(11);
    declare yesterday_retain_players_android int(11);
    declare yesterday_retain_players_windows int(11);
    
    -- 定义分终端留存变量
    declare retain_players_ios int(11);
    declare retain_players_android int(11);
    declare retain_players_windows int(11);
    
    -- 定义循环的变量
    declare i integer;
    declare j integer;
	declare count_day date;
	declare current_day date;

    -- count_day为统计日,从过去到现在进行递增
    set j = 20;
    WHILE j > 0 DO
		set count_day = date_sub(curdate(),interval j day);
        -- 初始化
		set day_before_add_players_ios = 0;
		set day_before_add_players_android = 0;
		set day_before_add_players_windows = 0;
		set yesterday_retain_players_ios = 0;
		set yesterday_retain_players_android = 0;
		set yesterday_retain_players_windows = 0;
        
        select count(A.account) into day_before_add_players_ios from egg_log.create_role A join egg_log.device_info B on A.openudid = B.openudid where DATE_FORMAT(A.create_time,'%Y-%m-%d') = date_sub(count_day,interval 1 day) and B.os = "iOS";
		select count(A.account) into yesterday_retain_players_ios from (select distinct account from egg_log.login where date = count_day) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid where DATE_FORMAT(B.create_time,'%Y-%m-%d') = date_sub(count_day,interval 1 day) and C.os = "iOS" ;
		select count(A.account) into day_before_add_players_android from egg_log.create_role A join egg_log.device_info B on A.openudid = B.openudid where DATE_FORMAT(A.create_time,'%Y-%m-%d') = date_sub(count_day,interval 1 day) and B.os = "android";
		select count(A.account) into yesterday_retain_players_android from (select distinct account from egg_log.login where date = count_day) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid where DATE_FORMAT(B.create_time,'%Y-%m-%d') = date_sub(count_day,interval 1 day) and C.os = "android" ;
		select count(A.account) into day_before_add_players_windows from egg_log.create_role A join egg_log.device_info B on A.openudid = B.openudid where DATE_FORMAT(A.create_time,'%Y-%m-%d') = date_sub(count_day,interval 1 day) and B.os = "windows";
		select count(A.account) into yesterday_retain_players_windows from (select distinct account from egg_log.login where date = count_day) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid where DATE_FORMAT(B.create_time,'%Y-%m-%d') = date_sub(count_day,interval 1 day) and C.os = "windows" ;
		insert into egg_log.retain_user (date,add_user,first_day,os) values (date_sub(count_day,interval 1 day),day_before_add_players_ios,yesterday_retain_players_ios,"iOS");
		insert into egg_log.retain_user (date,add_user,first_day,os) values (date_sub(count_day,interval 1 day),day_before_add_players_android,yesterday_retain_players_android,"android");
		insert into egg_log.retain_user (date,add_user,first_day,os) values (date_sub(count_day,interval 1 day),day_before_add_players_windows,yesterday_retain_players_windows,"windows");
        
        set i = 2;
		WHILE i < 15 DO
			set current_day = date_sub(count_day,interval i day);
			set retain_players_ios = 0;
			set retain_players_android = 0;
			set retain_players_windows = 0;
			-- 在current_day新增的用户在 count_day 的登录数量
			select count(A.account) into retain_players_ios from (select distinct account from egg_log.login where date = count_day) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid where DATE_FORMAT(B.create_time,'%Y-%m-%d') = current_day and C.os = "iOS"; 
			select count(A.account) into retain_players_android from (select distinct account from egg_log.login where date = count_day) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid where DATE_FORMAT(B.create_time,'%Y-%m-%d') = current_day and C.os = "android"; 
			select count(A.account) into retain_players_windows from (select distinct account from egg_log.login where date = count_day) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid where DATE_FORMAT(B.create_time,'%Y-%m-%d') = current_day and C.os = "windows";

			case i
				when 2 then
					update egg_log.retain_user set second_day = retain_players_ios where date = current_day and os = "iOS";
					update egg_log.retain_user set second_day = retain_players_android where date = current_day and os = "android";
					update egg_log.retain_user set second_day = retain_players_windows where date = current_day and os = "windows";
				when 3 then
					update egg_log.retain_user set third_day = retain_players_ios where date = current_day and os = "iOS";
					update egg_log.retain_user set third_day = retain_players_android where date = current_day and os = "android";
					update egg_log.retain_user set third_day = retain_players_windows where date = current_day and os = "windows";
				when 4 then
					update egg_log.retain_user set forth_day = retain_players_ios where date = current_day and os = "iOS";
					update egg_log.retain_user set forth_day = retain_players_android where date = current_day and os = "android";
					update egg_log.retain_user set forth_day = retain_players_windows where date = current_day and os = "windows";
				when 5 then
					update egg_log.retain_user set fifth_day = retain_players_ios where date = current_day and os = "iOS";
					update egg_log.retain_user set fifth_day = retain_players_android where date = current_day and os = "android";
					update egg_log.retain_user set fifth_day = retain_players_windows where date = current_day and os = "windows";
				when 6 then
					update egg_log.retain_user set sixth_day = retain_players_ios where date = current_day and os = "iOS";
					update egg_log.retain_user set sixth_day = retain_players_android where date = current_day and os = "android";
					update egg_log.retain_user set sixth_day = retain_players_windows where date = current_day and os = "windows";
				when 7 then
					update egg_log.retain_user set seven_day = retain_players_ios where date = current_day and os = "iOS";
					update egg_log.retain_user set seven_day = retain_players_android where date = current_day and os = "android";
					update egg_log.retain_user set seven_day = retain_players_windows where date = current_day and os = "windows";
				when 8 then
					update egg_log.retain_user set eighth_day = retain_players_ios where date = current_day and os = "iOS";
					update egg_log.retain_user set eighth_day = retain_players_android where date = current_day and os = "android";
					update egg_log.retain_user set eighth_day = retain_players_windows where date = current_day and os = "windows";
				when 9 then
					update egg_log.retain_user set ninth_day = retain_players_ios where date = current_day and os = "iOS";
					update egg_log.retain_user set ninth_day = retain_players_android where date = current_day and os = "android";
					update egg_log.retain_user set ninth_day = retain_players_windows where date = current_day and os = "windows";
				when 10 then
					update egg_log.retain_user set tenth_day = retain_players_ios where date = current_day and os = "iOS";
					update egg_log.retain_user set tenth_day = retain_players_android where date = current_day and os = "android";
					update egg_log.retain_user set tenth_day = retain_players_windows where date = current_day and os = "windows";
				when 11 then
					update egg_log.retain_user set eleventh_day = retain_players_ios where date = current_day and os = "iOS";
					update egg_log.retain_user set eleventh_day = retain_players_android where date = current_day and os = "android";
					update egg_log.retain_user set eleventh_day = retain_players_windows where date = current_day and os = "windows";
				when 12 then
					update egg_log.retain_user set twelfth_day = retain_players_ios where date = current_day and os = "iOS";
					update egg_log.retain_user set twelfth_day = retain_players_android where date = current_day and os = "android";
					update egg_log.retain_user set twelfth_day = retain_players_windows where date = current_day and os = "windows";
				when 13 then
					update egg_log.retain_user set thirteenth_day = retain_players_ios where date = current_day and os = "iOS";
					update egg_log.retain_user set thirteenth_day = retain_players_android where date = current_day and os = "android";
					update egg_log.retain_user set thirteenth_day = retain_players_windows where date = current_day and os = "windows";
				when 14 then
					update egg_log.retain_user set fourteenth_day = retain_players_ios where date = current_day and os = "iOS";
					update egg_log.retain_user set fourteenth_day = retain_players_android where date = current_day and os = "android";
					update egg_log.retain_user set fourteenth_day = retain_players_windows where date = current_day and os = "windows";
			end case;
			set i = i+1;
		END WHILE;
        set j = j-1;
    END WHILE;
    
END |
DELIMITER ;