shell目录下包含有数据的备份脚本：
备份脚本都是在各安装的服务器上定时执行，备份脚本包括redis数据和mongodb数据备份





目录recommendData下是定时任务，从kafka中消费数据，根据我们定义的规则去计算得分，存储在mongodb中，顺便同步到redis中。执行的方法是com.wonder.data.DataDeal

目录recommendSystem下是根据用户名ID，得到推荐栏目的接口。接口名是com.wonder.servlet.QueryItemByUseridServlet(GetAllItemIDServlet是根据用户名查询该用户下所有的栏目得分详情)，根据
用户ID从redis中得到最大的得分栏目，返回栏目；若redis中查询不到，则从mongodb中同步到redis，再返回栏目。



和新闻线上环境地址，用户名和密码如使用不了可找沃勇强（henews  henews!Q2w）：
#和新闻
200.200.18.117   ZJHZ-CMREAD-NEWSTAG01-VBUS-SQ
200.200.18.130   ZJHZ-CMREAD-NEWSTAG02-VBUS-SQ
200.200.18.123   ZJHZ-CMREAD-NEWSTAG03-VBUS-SQ
200.200.18.126   ZJHZ-CMREAD-NEWSTAG04-VBUS-SQ
200.200.18.128   ZJHZ-CMREAD-NEWSTAG05-VBUS-SQ
200.200.18.129   ZJHZ-CMREAD-NEWSTAG06-VBUS-SQ