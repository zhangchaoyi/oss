DELIMITER |
DROP EVENT IF EXISTS egg_log.event_retain_equipment_initial_next_day |
CREATE EVENT IF NOT EXISTS egg_log.event_retain_equipment_initial_next_day
ON SCHEDULE EVERY 1 DAY  
ON COMPLETION PRESERVE
DO

BEGIN
	-- 初始化 以往的retain_equipment数据

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
    declare j integer;
	declare add_equipment_date date;
    declare v_add_equipment_date date;
    declare start_date date;

    declare row_name varchar(20);
    
    set i = 20;
    WHILE i > 1 DO
    BEGIN
		set add_equipment_date = date_sub(curdate(),interval i day);
        set start_date = date_sub(curdate(),interval (i-1) day);
        
        set day_before_add_equipment_ios = 0;
        set day_before_add_equipment_android = 0;
        set day_before_add_equipment_windows = 0;
        set yesterday_retain_equipment_ios = 0;
        set yesterday_retain_equipment_android =0;
        set yesterday_retain_equipment_windows=0;
        
		select count(B.openudid) into day_before_add_equipment_ios from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date and os in("iOS")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date  group by openudid) B on A.openudid = B.openudid where B.openudid is not null;
		select count(B.openudid) into day_before_add_equipment_android from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date and os in("android")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date  group by openudid) B on A.openudid = B.openudid where B.openudid is not null;
		select count(B.openudid) into day_before_add_equipment_windows from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date and os in("windows")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date  group by openudid) B on A.openudid = B.openudid where B.openudid is not null;
	 
		select count(distinct D.openudid) into yesterday_retain_equipment_ios from (select distinct account from egg_log.login where date = start_date) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date and os in("iOS")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
		select count(distinct D.openudid) into yesterday_retain_equipment_android from (select distinct account from egg_log.login where date = start_date) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date and os in("android")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
		select count(distinct D.openudid) into yesterday_retain_equipment_windows from (select distinct account from egg_log.login where date = start_date) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date and os in("windows")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=add_equipment_date  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;

		
        set j = 2;
		WHILE j < 8 DO
		BEGIN
			set v_add_equipment_date = date_sub(start_date,interval j day);
			set retain_equipment_ios = 0;
			set retain_equipment_android = 0;
			set retain_equipment_windows = 0;
            
            select count(distinct D.openudid) into retain_equipment_ios from (select distinct account from egg_log.login where date = start_date) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=v_add_equipment_date and os in("iOS")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=v_add_equipment_date  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
			select count(distinct D.openudid) into retain_equipment_android from (select distinct account from egg_log.login where date = start_date) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=v_add_equipment_date and os in("android")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=v_add_equipment_date  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
			select count(distinct D.openudid) into retain_equipment_windows from (select distinct account from egg_log.login where date = start_date) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=v_add_equipment_date and os in("windows")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=v_add_equipment_date  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
			
			case j
				when 2 then 
					update egg_log.retain_equipment set second_day = retain_equipment_ios where date = v_add_equipment_date and os = "iOS"; 
					update egg_log.retain_equipment set second_day = retain_equipment_android where date = v_add_equipment_date and os = "android";
					update egg_log.retain_equipment set second_day = retain_equipment_windows where date = v_add_equipment_date and os = "windows";
				when 3 then 
					update egg_log.retain_equipment set third_day = retain_equipment_ios where date = v_add_equipment_date and os = "iOS"; 
					update egg_log.retain_equipment set third_day = retain_equipment_android where date = v_add_equipment_date and os = "android";
					update egg_log.retain_equipment set third_day = retain_equipment_windows where date = v_add_equipment_date and os = "windows";
				when 4 then 
					update egg_log.retain_equipment set forth_day = retain_equipment_ios where date = v_add_equipment_date and os = "iOS"; 
					update egg_log.retain_equipment set forth_day = retain_equipment_android where date = v_add_equipment_date and os = "android";
					update egg_log.retain_equipment set forth_day = retain_equipment_windows where date = v_add_equipment_date and os = "windows";
				when 5 then 
					update egg_log.retain_equipment set fifth_day = retain_equipment_ios where date = v_add_equipment_date and os = "iOS"; 
					update egg_log.retain_equipment set fifth_day = retain_equipment_android where date = v_add_equipment_date and os = "android";
					update egg_log.retain_equipment set fifth_day = retain_equipment_windows where date = v_add_equipment_date and os = "windows";
				when 6 then 
					update egg_log.retain_equipment set sixth_day = retain_equipment_ios where date = v_add_equipment_date and os = "iOS"; 
					update egg_log.retain_equipment set sixth_day = retain_equipment_android where date = v_add_equipment_date and os = "android";
					update egg_log.retain_equipment set sixth_day = retain_equipment_windows where date = v_add_equipment_date and os = "windows";
				when 7 then 
					update egg_log.retain_equipment set seven_day = retain_equipment_ios where date = v_add_equipment_date and os = "iOS"; 
					update egg_log.retain_equipment set seven_day = retain_equipment_android where date = v_add_equipment_date and os = "android";
					update egg_log.retain_equipment set seven_day = retain_equipment_windows where date = v_add_equipment_date and os = "windows";
			end case;
            
            set j = j+1;
		END;
		END WHILE;
        
		select count(distinct D.openudid) into retain_equipment_ios from (select distinct account from egg_log.login where date = start_date) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(start_date,interval 14 day) and os in("iOS")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(start_date,interval 14 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
		select count(distinct D.openudid) into retain_equipment_android from (select distinct account from egg_log.login where date = start_date) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(start_date,interval 14 day) and os in("android")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(start_date,interval 14 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
		select count(distinct D.openudid) into retain_equipment_windows from (select distinct account from egg_log.login where date = start_date) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(start_date,interval 14 day) and os in("windows")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(start_date,interval 14 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
		
		update egg_log.retain_equipment set fourteen_day = retain_equipment_ios where date = date_sub(start_date,interval 14 day) and os = "iOS"; 
		update egg_log.retain_equipment set fourteen_day = retain_equipment_android where date = date_sub(start_date,interval 14 day) and os = "android";
		update egg_log.retain_equipment set fourteen_day = retain_equipment_windows where date = date_sub(start_date,interval 14 day) and os = "windows";
		
		-- 求2016-08-06 的30天设备留存数 
		select count(distinct D.openudid) into retain_equipment_ios from (select distinct account from egg_log.login where date = start_date) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(start_date,interval 30 day) and os in("iOS")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(start_date,interval 30 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
		select count(distinct D.openudid) into retain_equipment_android from (select distinct account from egg_log.login where date = start_date) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(start_date,interval 30 day) and os in("android")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(start_date,interval 30 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
		select count(distinct D.openudid) into retain_equipment_windows from (select distinct account from egg_log.login where date = start_date) A join egg_log.create_role B on A.account =B.account join egg_log.device_info C on B.openudid = C.openudid join (select B.openudid from (select openudid from egg_log.device_info where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(start_date,interval 30 day) and os in("windows")) A left join (select openudid,min(create_time) create_time from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')=date_sub(start_date,interval 30 day)  group by openudid) B on A.openudid = B.openudid where B.openudid is not null) D on C.openudid = D.openudid;
		
		update egg_log.retain_equipment set thirty_day = retain_equipment_ios where date = date_sub(start_date,interval 30 day) and os = "iOS"; 
		update egg_log.retain_equipment set thirty_day = retain_equipment_android where date = date_sub(start_date,interval 30 day) and os = "android";
		update egg_log.retain_equipment set thirty_day = retain_equipment_windows where date = date_sub(start_date,interval 30 day) and os = "windows";
        
        set i = i-1;
    END;
    END WHILE;
    
END |
DELIMITER ;