# MyBasicSSM
学习极客学院配置基本SSM框架项目
##运行步骤：
###0、设置项目结构
动态Web工程，conf为resource文件夹，src为源文件夹，添加web模块,out为编译输出文件夹，lib为依赖文件夹，添加Artifact
###1、配置Tomcat
###2、配置Mysql
```properties
select * from test01 where id = #{id}
jdbc.user=root
jdbc.password=821520
jdbc.jdbcUrl=jdbc:mysql://localhost:3306/mydatabase?useSSL=false&serverTimezone=GMT
``` 
###3、运行访问localhost:8080