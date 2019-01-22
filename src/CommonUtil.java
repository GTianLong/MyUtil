import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: MyUtils
 * @description: 通用Util
 * @author: GTL
 * @create: 2019-01-22 10:05
 **/
public class CommonUtil {

    /**
     * 按固定字符截取内容
     * new String[]{"<sender>(.*?)</sender>","<user>(.*?)</user>"}
     */
    public static String[] matcherStr(String msg,String[] args){
        String regex[]=args;
        List list=new ArrayList();
        for(int x=0;x<regex.length;x++){
            Pattern pattern= Pattern.compile(regex[x]);
            Matcher matcher=pattern.matcher(msg);
            while (matcher.find()){
                list.add(matcher.group(1));
            }
        }
        String[] result=new String[list.size()];
        return (String[]) list.toArray(result);
    }
}
