# file-server
一个独立运行，支持文件跨域上传、下载的 web server。多应用环境下公共服务。

1. 密钥授权机制 md5( md5( secret+timestamp ) + timestamp ) 
2. 分应用、分文件类型、分日期存盘 ，多语言响应提示信息

#### 编译启动

```Shell
git clone https://github.com/qinyou/file-server.git
cd file-server
mvn clean package
cd target/file-server-release/file-server
start.bat 或 start.sh
```

#### 调用方式 
1. 公共上传   http://localhost:8089/common/upload  
2. 公共下载   http://localhost:8089/common/download  
3. 更多自行扩展...

上传成功响应:

```Json
{
	"data": {
		"name": "timg.jpg",
		"path": "upload/image/2019_05_08/575776561135878144.jpg",
		"size": "33 KB",
		"sizeL": 33809,
		"uri": "http://localhost:8089/upload/crm/image/2019_05_08/575776561135878144.jpg"
	},
	"state": "ok",
	"timestamp": 1557317429598
}
```

具体用例见 https://github.com/qinyou/file-server/blob/master/src/main/webapp/test.html  


