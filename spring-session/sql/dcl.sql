#-------------------------------
# DCL
#-------------------------------
# mysql user
use mysql;
# database 생성
create database sample;
# user 생성
create user 'sample'@'%' identified by 'sample1234';
# user 권한 부여
grant all privileges on sample.* to 'sample'@'%';
# 권한 적용
flush privileges;