import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import com.thoughtworks.xstream.security.AnyTypePermission;

/**
 * @program: MyUtils
 * @description:
 * @author: GTL
 * @create: 2019-01-21 16:17
 **/
public class XStreamUtil {

    /**
     * 定义XML报文头
     * */
    private static String XML_TAG="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

    private static XStream getInstance(){
        XStream xStream=new XStream(new DomDriver("UTF-8"){
            protected MapperWrapper mapperWrapper(MapperWrapper wrapper){
                return new MapperWrapper(wrapper) {
                    @Override
                    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
                        if(definedIn==Object.class){
                            return false;
                        }
                        return super.shouldSerializeMember(definedIn, fieldName);
                    }
                };
            }
        });
        //使用本地类加载器
        xStream.setClassLoader(XStreamUtil.class.getClassLoader());
        //允许所有的类进行转换
        xStream.addPermission(AnyTypePermission.ANY);
        return xStream;
    }

    //将xml保文转换为java对象
    public static <T> T xmlToModel(String type,String xmlStr,Class clazz){
        XStream xStream=getInstance();
        xStream.processAnnotations(clazz);
        xStream.alias("root",clazz);
        return null;
    }
}
