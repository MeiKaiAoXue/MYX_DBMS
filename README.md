<h1>MYX_DBMS</h1>
数据库管理系统，使用java完成，引用部分Jsqparse依赖中的解析功能，使用maven进行包管理，采用前后分离式结构。<br>
<strong>实现功能：</strong>（以下功能只支持单表完成）
<ol>
<li>SQL解析</li>
<li>数据库的创建、删除（通过gui完成</li>
<li>表的创建删除</li>
<li>表中字段结构的增删改</li>
<li>表中数据元组的增删改查</li>
</ol>
<strong>额外功能：</strong>
<ol>
  <li>用户授权（都有查询权限，区别在于创建修改表的权限授予）</li>
  <li>视图的创建及查询</li>
  <li>sql语句的批量执行</li>
</ol>

sql语句具体的执行格式要求参考MAVEN中的后端test部分,需注意<strong>单引号及中英文输入法</strong>的使用，测试中要求使用英文输入法，中文输入法输入的符合会导致sql解析无法完成<br>
![image](https://github.com/MeiKaiAoXue/MYX_DBMS/assets/111553908/2b50ae15-2811-4f95-b4e7-bc180d3e84c4)

<br>前端gui在显示当行 * 查询时列名会有格式错误
