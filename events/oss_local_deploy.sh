#!/bin/bash
cd ~/workspace/oss
mvn clean package
date=`date +%Y%m%d%H%M%S`
if [ ! -f "target/oss.war" ] ; then
    echo << EOF
mvn 构建失败！
程序推出
EOF
    exit 0;
fi
sleep 5s
cd $CATALINA_HOME/www
mv oss backup/oss.$date
mkdir oss
cp ~/workspace/oss/target/oss.war oss
cd oss
unzip oss.war 
rm -rf oss.war
cd $CATALINA_HOME/bin
sudo sh shutdown.sh
sudo sh startup.sh
