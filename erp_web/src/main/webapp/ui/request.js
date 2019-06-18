var url=location.search;
var Request = new Object();
if(url.indexOf("?")!=-1)
{
    var str = url.substr(1)  //获取?号后的参数
    strs = str.split("&");//切割得到键值对数组
    for(var i=0;i<strs.length;i++)
    {
        Request[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
    }
}