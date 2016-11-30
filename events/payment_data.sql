DELIMITER |
DROP EVENT IF EXISTS egg_log.event_payment_data |
CREATE EVENT IF NOT EXISTS egg_log.event_payment_data
ON SCHEDULE EVERY 1 DAY  STARTS '2016-09-24 03:00:00'
ON COMPLETION PRESERVE
DO

BEGIN
    -- 统计付费数据
    declare add_players_ios int(11);
    declare add_players_android int(11);
    declare add_players_windows int(11);
    
    declare paid_money_ios double(11,4) DEFAULT 0.0000;
    declare paid_people_ios int(11);
    declare paid_num_ios int(11);
	declare paid_money_android double(11,4) DEFAULT 0.0000;
    declare paid_people_android int(11);
    declare paid_num_android int(11);
    declare paid_money_windows double(11,4) DEFAULT 0.0000;
    declare paid_people_windows int(11);
    declare paid_num_windows int(11);
    
    declare ft_paid_money_ios double(11,4) DEFAULT 0.0000;
    declare ft_paid_people_ios int(11);
	declare ft_paid_money_android double(11,4) DEFAULT 0.0000;
    declare ft_paid_people_android int(11);
    declare ft_paid_money_windows double(11,4) DEFAULT 0.0000;
    declare ft_paid_people_windows int(11);
    
    declare fd_paid_money_ios double(11,4) DEFAULT 0.0000;
    declare fd_paid_people_ios int(11);
    declare fd_paid_num_ios int(11);
	declare fd_paid_money_android double(11,4) DEFAULT 0.0000;
    declare fd_paid_people_android int(11);
    declare fd_paid_num_android int(11);
    declare fd_paid_money_windows double(11,4) DEFAULT 0.0000;
    declare fd_paid_people_windows int(11);
    declare fd_paid_num_windows int(11);

	declare fw_paid_people_ios int(11);
    declare fw_paid_people_android int(11);
    declare fw_paid_people_windows int(11);
    
    declare fm_paid_people_ios int(11);
    declare fm_paid_people_android int(11);
    declare fm_paid_people_windows int(11);

    declare start_date date;
    set start_date = date_sub(curdate(),interval 1 day);

	-- 新增玩家
    select count(*) into add_players_ios from egg_log.create_role A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.create_time,'%Y-%m-%d')= start_date and B.os = "iOS";
    select count(*) into add_players_android from egg_log.create_role A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.create_time,'%Y-%m-%d')= start_date and B.os = "android";
    select count(*) into add_players_windows from egg_log.create_role A join device_info B on A.openudid = B.openudid where DATE_FORMAT(A.create_time,'%Y-%m-%d')= start_date and B.os = "windows";
    
    -- 活跃玩家付费
    select case when sum(A.count) is null then 0.0000 else sum(A.count) end into paid_money_ios from egg_log.log_charge A join egg_log.create_role B on A.account=B.account join egg_log.device_info C on B.openudid=C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d')=start_date and C.os = "iOS";
	select count(distinct A.account)people into paid_people_ios from egg_log.log_charge A join egg_log.create_role B on A.account=B.account join egg_log.device_info C on B.openudid=C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d')=start_date and C.os = "iOS";
    select count(*)count into paid_num_ios from egg_log.log_charge A join egg_log.create_role B on A.account=B.account join egg_log.device_info C on B.openudid=C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d')=start_date and C.os = "iOS";
    
    select case when sum(A.count) is null then 0.0000 else sum(A.count) end into paid_money_android from egg_log.log_charge A join egg_log.create_role B on A.account=B.account join egg_log.device_info C on B.openudid=C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d')=start_date and C.os = "android";
	select count(distinct A.account)people into paid_people_android from egg_log.log_charge A join egg_log.create_role B on A.account=B.account join egg_log.device_info C on B.openudid=C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d')=start_date and C.os = "android";
    select count(*)count into paid_num_android from egg_log.log_charge A join egg_log.create_role B on A.account=B.account join egg_log.device_info C on B.openudid=C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d')=start_date and C.os = "android";

	select case when sum(A.count) is null then 0.0000 else sum(A.count) end into paid_money_windows from egg_log.log_charge A join egg_log.create_role B on A.account=B.account join egg_log.device_info C on B.openudid=C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d')=start_date and C.os = "windows";
	select count(distinct A.account)people into paid_people_windows from egg_log.log_charge A join egg_log.create_role B on A.account=B.account join egg_log.device_info C on B.openudid=C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d')=start_date and C.os = "windows";
    select count(*)count into paid_num_windows from egg_log.log_charge A join egg_log.create_role B on A.account=B.account join egg_log.device_info C on B.openudid=C.openudid where A.is_product = 1 and DATE_FORMAT(A.timestamp,'%Y-%m-%d')=start_date and C.os = "windows";

    -- 首次付费
	select case when sum(A.count) is null then 0.0000 else sum(A.count) end into ft_paid_money_ios from (select* from egg_log.log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d')=start_date) A left join (select * from egg_log.log_charge where is_product = 1 and timestamp<start_date) B on A.account=B.account join egg_log.create_role C on A.account=C.account join egg_log.device_info D on C.openudid=D.openudid where B.account is null and D.os = "iOS";
	select count(distinct A.account)people into ft_paid_people_ios from (select* from egg_log.log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d')=start_date) A left join (select * from egg_log.log_charge where is_product = 1 and timestamp<start_date) B on A.account=B.account join egg_log.create_role C on A.account=C.account join egg_log.device_info D on C.openudid=D.openudid where B.account is null and D.os = "iOS";

	select case when sum(A.count) is null then 0.0000 else sum(A.count) end into ft_paid_money_android from (select* from egg_log.log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d')=start_date) A left join (select * from egg_log.log_charge where is_product = 1 and timestamp<start_date) B on A.account=B.account join egg_log.create_role C on A.account=C.account join egg_log.device_info D on C.openudid=D.openudid where B.account is null and D.os = "android";	
	select count(distinct A.account)people into ft_paid_people_android from (select* from egg_log.log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d')=start_date) A left join (select * from egg_log.log_charge where is_product = 1 and timestamp<start_date) B on A.account=B.account join egg_log.create_role C on A.account=C.account join egg_log.device_info D on C.openudid=D.openudid where B.account is null and D.os = "android";

	select case when sum(A.count) is null then 0.0000 else sum(A.count) end into ft_paid_money_windows from (select* from egg_log.log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d')=start_date) A left join (select * from egg_log.log_charge where is_product = 1 and timestamp<start_date) B on A.account=B.account join egg_log.create_role C on A.account=C.account join egg_log.device_info D on C.openudid=D.openudid where B.account is null and D.os = "windows";
	select count(distinct A.account)people into ft_paid_people_windows from (select* from egg_log.log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d')=start_date) A left join (select * from egg_log.log_charge where is_product = 1 and timestamp<start_date) B on A.account=B.account join egg_log.create_role C on A.account=C.account join egg_log.device_info D on C.openudid=D.openudid where B.account is null and D.os = "windows";

	-- 首日付费
    select case when sum(B.count) is null then 0.0000 else sum(B.count) end into fd_paid_money_ios from (select account,openudid from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')= start_date) A join (select * from  egg_log.log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d')= start_date) B on A.account=B.account join egg_log.device_info C on A.openudid=C.openudid where C.os = "iOS";
    select count(distinct A.account)people into fd_paid_people_ios from (select account,openudid from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')= start_date) A join (select * from  egg_log.log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d')= start_date) B on A.account=B.account join egg_log.device_info C on A.openudid=C.openudid where C.os = "iOS";
    select count(*)count into fd_paid_num_ios from (select account,openudid from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')= start_date) A join (select * from  egg_log.log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d')= start_date) B on A.account=B.account join egg_log.device_info C on A.openudid=C.openudid where C.os = "iOS";
    
    select case when sum(B.count) is null then 0.0000 else sum(B.count) end into fd_paid_money_android from (select account,openudid from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')= start_date) A join (select * from  egg_log.log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d')= start_date) B on A.account=B.account join egg_log.device_info C on A.openudid=C.openudid where C.os = "android";
    select count(distinct A.account)people into fd_paid_people_android from (select account,openudid from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')= start_date) A join (select * from  egg_log.log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d')= start_date) B on A.account=B.account join egg_log.device_info C on A.openudid=C.openudid where C.os = "android";
    select count(*)count into fd_paid_num_android from (select account,openudid from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')= start_date) A join (select * from  egg_log.log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d')= start_date) B on A.account=B.account join egg_log.device_info C on A.openudid=C.openudid where C.os = "android";
    
	select case when sum(B.count) is null then 0.0000 else sum(B.count) end into fd_paid_money_windows from (select account,openudid from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')= start_date) A join (select * from  egg_log.log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d')= start_date) B on A.account=B.account join egg_log.device_info C on A.openudid=C.openudid where C.os = "windows";
    select count(distinct A.account)people into fd_paid_people_windows from (select account,openudid from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')= start_date) A join (select * from  egg_log.log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d')= start_date) B on A.account=B.account join egg_log.device_info C on A.openudid=C.openudid where C.os = "windows";
    select count(*)count into fd_paid_num_windows from (select account,openudid from egg_log.create_role where DATE_FORMAT(create_time,'%Y-%m-%d')= start_date) A join (select * from  egg_log.log_charge where is_product = 1 and DATE_FORMAT(timestamp,'%Y-%m-%d')= start_date) B on A.account=B.account join egg_log.device_info C on A.openudid=C.openudid where C.os = "windows";

	insert into payment_detail(date,os,paid_money,paid_people,paid_num,ft_paid_money,ft_paid_people,fd_paid_money,fd_paid_people,fd_paid_num,add_players) values(start_date,"iOS",paid_money_ios,paid_people_ios,paid_num_ios,ft_paid_money_ios,ft_paid_people_ios,fd_paid_money_ios,fd_paid_people_ios,fd_paid_num_ios,add_players_ios);
	insert into payment_detail(date,os,paid_money,paid_people,paid_num,ft_paid_money,ft_paid_people,fd_paid_money,fd_paid_people,fd_paid_num,add_players) values(start_date,"android",paid_money_android,paid_people_android,paid_num_android,ft_paid_money_android,ft_paid_people_android,fd_paid_money_android,fd_paid_people_android,fd_paid_num_android,add_players_android);
	insert into payment_detail(date,os,paid_money,paid_people,paid_num,ft_paid_money,ft_paid_people,fd_paid_money,fd_paid_people,fd_paid_num,add_players) values(start_date,"windows",paid_money_windows,paid_people_windows,paid_num_windows,ft_paid_money_windows,ft_paid_people_windows,fd_paid_money_windows,fd_paid_people_windows,fd_paid_num_windows,add_players_windows);

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
    

END |
DELIMITER ;