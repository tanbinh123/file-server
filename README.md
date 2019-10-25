# file-server
A stand-alone Java Web application that support cross-domain file upload and download, it can be used as file server in multi application scenarios.

1. Token encryption: md5(md5(SECRET+timestamp)+timestamp).
2. Save path distinguish application, file type and upload date, response support i18n.

#### Quick Start

```Shell
git clone https://github.com/qinyou/file-server.git
cd file-server
mvn clean package
cd target/file-server-release/file-server
start.bat 或 start.sh
```

#### Api Url
1. common upload     http://{server}:{port}/common/upload  
2. common download   http://{server}:{port}/common/download  
3. more self-expanding...

upload success response:

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

more example  
https://github.com/qinyou/file-server/blob/master/src/main/webapp/test.html  


