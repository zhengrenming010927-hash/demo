# Java 用户接口练习

这是我学习 Java 和 Spring Boot 的练习项目。

## 已完成的功能

- 定义 User 用户类
- 使用构造方法给用户属性赋值
- 使用 getter 和 setter 读取、修改属性
- 通过 HTTP 接口返回 JSON 用户数据

## 如何运行

需要安装 JDK 17。

在 Windows 项目目录的终端中执行：

```powershell
.\mvnw.cmd spring-boot:run
```

启动成功后，在浏览器访问：

```text
http://localhost:8080/api/users/100
```

返回示例：

```json
{
  "id": 100,
  "username": "tanaka",
  "name": "田中"
}
```

目前编号来自网址，用户名和姓名是固定值，尚未连接数据库。