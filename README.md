# ShutdownDemo
##### 一个带界面的关机小程序

## 核心代码

##### String [] Str = new String[] {"shutdown","/c","-s -t 60"}; // 设定60s后关机
##### String [] Str = new String[] {"shutdown","/c","-a"}; // 取消关机计划
##### Runtime.getRuntime().exec(Str); // 等同于cmd 
