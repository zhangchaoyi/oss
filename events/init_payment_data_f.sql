DELIMITER |
DROP EVENT IF EXISTS egg_log.event__init_payment_f |
CREATE EVENT IF NOT EXISTS egg_log.event__init_payment_f
ON SCHEDULE EVERY 1 DAY  
ON COMPLETION PRESERVE
DO

BEGIN
	declare add_players_ios int(11);
    declare add_players_android int(11);
    declare add_players_windows int(11);
    
    declare fw_paid_people_ios int(11);
    declare fw_paid_people_android int(11);
    declare fw_paid_people_windows int(11);

	declare fm_paid_people_ios int(11);
    declare fm_paid_people_android int(11);
    declare fm_paid_people_windows int(11);
    
    declare start_date date;
    declare i integer;
    set i = 1;
    WHILE i < 15 DO
    BEGIN
    set start_date = date_sub(curdate(),interval i day);

	-- 新增玩家
    select count(*) into add_players_ios from egg_log.create_role A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.create_time,'%Y-%m-%d')= start_date and B.os = "iOS";
    select count(*) into add_players_android from egg_log.create_role A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.create_time,'%Y-%m-%d')= start_date and B.os = "android";
    select count(*) into add_players_windows from egg_log.create_role A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.create_time,'%Y-%m-%d')= start_date and B.os = "windows";

	update payment_detail set add_players = add_players_ios where date = start_date and os = 'iOS';
	update payment_detail set add_players = add_players_android where date = start_date and os = 'android';
	update payment_detail set add_players = add_players_windows where date = start_date and os = 'windows';
    
	-- 首周付费人数
    select count(*) into fw_paid_people_ios from (select account,openudid from create_role where DATE_FORMAT(create_time,'%Y-%m-%d')= date_sub(start_date,interval 6 day)) A join (select distinct account from log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d') between date_sub(start_date,interval 6 day) and start_date) B on A.account = B.account join device_info C on A.openudid = C.openudid where C.os = "iOS";
	select count(*) into fw_paid_people_android from (select account,openudid from create_role where DATE_FORMAT(create_time,'%Y-%m-%d')= date_sub(start_date,interval 6 day)) A join (select distinct account from log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d') between date_sub(start_date,interval 6 day) and start_date) B on A.account = B.account join device_info C on A.openudid = C.openudid where C.os = "android";
	select count(*) into fw_paid_people_windows from (select account,openudid from create_role where DATE_FORMAT(create_time,'%Y-%m-%d')= date_sub(start_date,interval 6 day)) A join (select distinct account from log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d') between date_sub(start_date,interval 6 day) and start_date) B on A.account = B.account join device_info C on A.openudid = C.openudid where C.os = "windows";

	update payment_detail set fw_paid_people = fw_paid_people_ios where date = date_sub(start_date,interval 6 day) and os = 'iOS';
	update payment_detail set fw_paid_people = fw_paid_people_android where date = date_sub(start_date,interval 6 day) and os = 'android';
	update payment_detail set fw_paid_people = fw_paid_people_windows where date = date_sub(start_date,interval 6 day) and os = 'windows';
    
    -- 首月付费人数
	select count(*) into fm_paid_people_ios from (select account,openudid from create_role where DATE_FORMAT(create_time,'%Y-%m-%d')= date_sub(start_date,interval 29 day)) A join (select distinct account from log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d') between date_sub(start_date,interval 29 day) and start_date) B on A.account = B.account join device_info C on A.openudid = C.openudid where C.os = "iOS";
	select count(*) into fm_paid_people_android from (select account,openudid from create_role where DATE_FORMAT(create_time,'%Y-%m-%d')= date_sub(start_date,interval 29 day)) A join (select distinct account from log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d') between date_sub(start_date,interval 29 day) and start_date) B on A.account = B.account join device_info C on A.openudid = C.openudid where C.os = "android";
	select count(*) into fm_paid_people_windows from (select account,openudid from create_role where DATE_FORMAT(create_time,'%Y-%m-%d')= date_sub(start_date,interval 29 day)) A join (select distinct account from log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d') between date_sub(start_date,interval 29 day) and start_date) B on A.account = B.account join device_info C on A.openudid = C.openudid where C.os = "windows";

	update payment_detail set fw_paid_people = fw_paid_people_ios where date = date_sub(start_date,interval 29 day) and os = 'iOS';
	update payment_detail set fw_paid_people = fw_paid_people_android where date = date_sub(start_date,interval 29 day) and os = 'android';
	update payment_detail set fw_paid_people = fw_paid_people_windows where date = date_sub(start_date,interval 29 day) and os = 'windows';

    set i = i+1;
    END;
    END WHILE;
END |
DELIMITER ;
