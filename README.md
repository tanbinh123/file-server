# file-server
一个独立运行，支持文件上传下载的 web server, 多应用环境 下 公共的文件上传下载服务。
  
## 编译启动
```
git clone https://github.com/qinyou/file-server.git
cd file-server
mvn clean package
cd target/file-server-release/file-server
start.bat 或 start.sh
```
## 调用方式 
1. 上传   http://localhost:8089/file/upload  
2. 下载   http://localhost:8089/file/download  

```javascript
var form_data = new FormData();
// 文件input
var file_data = $("#img_input").prop("files")[0];
// 时间戳
form_data.append("timestamp", "1557284842861");
// 授权密文, 加密方式 MD5( MD5(authPwd+timestamp) + timestamp )
form_data.append("authToken", "eed079688545ca9877e5d0d366104729");
// 文件
form_data.append("file", file_data);

$.ajax({
    type: "POST",
    url: "http://localhost:8089/file/upload",
    dataType: "json",
    crossDomain: true, 
    processData: false, 
    contentType: false, 
    data: form_data,
    success:function(data){
        $('#result').html(JSON.stringify(data));
        console.log(JSON.stringify(data,null, '\t'));
        if(data.state == 'ok'){
            var path = data.data.path.replace(/\//gm,'$'); // 左斜杠 替换为 美元符合
            
            // 文件下载链接
            console.log('文件下载链接：http://localhost:8089/file/download?path='+path+'&name='+data.data.name);
        }

    },
    fail:function(x,h,r){
        alert(x + "  " + h + " " + r);
    }
});

```
具体调用见 https://github.com/qinyou/file-server/blob/master/src/main/webapp/test.html  

上传文件响应
```json
{
	"data": {
		"name": "timg.jpg",
		"path": "upload/image/2019_05_08/575776561135878144.jpg",
		"size": "33 KB",
		"sizeL": 33809,
		"uri": "http://localhost:8089/upload/image/2019_05_08/575776561135878144.jpg"
	},
	"state": "ok",
	"timestamp": 1557317429598
}
```

Enjoy!


