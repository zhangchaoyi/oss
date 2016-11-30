# oss
#权限用户----用户名----密码
#系统管理员----systemroot----KooGameRoot
#平台管理员----admin----admin
#超级用户----supervip----KooGameVip
#其他权限可以在系统管理员中创建 

#支持多个数据源切换,前提是保持多个mysql数据源之间表结构相同,另外涉及平台用户管理的数据需要完全一致
#配置config.txt数据源时 多个数据源使用相同的用户名和密码  需要配置 $jdbcUrl jdbcList jdbcDefault 
#配置 jdbcUrl遵循 $dbName + "Url"    jdbcList为项目中使用的所有的数据源名称     jdbcDefault为默认数据源名称
#更换数据源注意需要同时配置 jdbcList 和 jdbcDefault
#在用户反馈页强制使用 马来服务器
