DELIMITER |
DROP EVENT IF EXISTS egg_log.event__init_payment_data |
CREATE EVENT IF NOT EXISTS egg_log.event__init_payment_data
ON SCHEDULE EVERY 1 DAY  
ON COMPLETION PRESERVE
DO

BEGIN
    -- 统计付费数据
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

    declare start_date date;
    declare i integer;
    set i = 1;
    WHILE i < 15 DO
    BEGIN
    set start_date = date_sub(curdate(),interval i day);

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

	insert into payment_detail(date,os,paid_money,paid_people,paid_num,ft_paid_money,ft_paid_people,fd_paid_money,fd_paid_people,fd_paid_num) values(start_date,"iOS",paid_money_ios,paid_people_ios,paid_num_ios,ft_paid_money_ios,ft_paid_people_ios,fd_paid_money_ios,fd_paid_people_ios,fd_paid_num_ios);
	insert into payment_detail(date,os,paid_money,paid_people,paid_num,ft_paid_money,ft_paid_people,fd_paid_money,fd_paid_people,fd_paid_num) values(start_date,"android",paid_money_android,paid_people_android,paid_num_android,ft_paid_money_android,ft_paid_people_android,fd_paid_money_android,fd_paid_people_android,fd_paid_num_android);
	insert into payment_detail(date,os,paid_money,paid_people,paid_num,ft_paid_money,ft_paid_people,fd_paid_money,fd_paid_people,fd_paid_num) values(start_date,"windows",paid_money_windows,paid_people_windows,paid_num_windows,ft_paid_money_windows,ft_paid_people_windows,fd_paid_money_windows,fd_paid_people_windows,fd_paid_num_windows);

    set i = i+1;
    END;
    END WHILE;
END |
DELIMITER ;
