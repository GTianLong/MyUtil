import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import com.thoughtworks.xstream.security.AnyTypePermission;

/**
 * @program: MyUtils 此工具类需要导入XStream jar包
 * @description:
 * @author: GTL
 * @create: 2019-01-21 16:17
 **/
public class XStreamUtil {

    /**
     * 报文实例
     * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
     * <root><body>......</body></root>
     * **/


    /**
     * 定义XML报文头报文
     * 在使用此方法时，注意查看此报文头是否符合要解析的xml报文
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

    /**
     * 将xml保文转换为java对象
     * @param  type 方法类型，作为判断使用
     * @param xmlStr 要处理的xml报文
     * @param clazz 要转换成的类型
     */
    public static <T> T xmlToModel(String type,String xmlStr,Class clazz){
        XStream xStream=getInstance();
        xStream.processAnnotations(clazz);
        //定义xml报文根节点为root
        //此处需注意：如果xml根节点的名次不是root，需要做对应修改
        xStream.alias("root",clazz);
        /***
         * xml在转换model的过程中，如果此模型中还包含了其他模型并且xml节点名称和类名称不一致，则需要将xml中相应的节点名称和模型进行声明
         * 实例如下：<root><body><user></user></body></root>
         * if("方法1".equals(type)){
         *     xStream.alias("user",SysUser.class);
         * }else if("方法2".equals(type)){
         *     xStream.alias("role",SysRole.class);
         * }
         * **/
        Object object=xStream.fromXML(xmlStr);
        T cast= (T) clazz.cast(object);
        return cast;
    }


    /**
     * 将model转为xml串
     * */
    public static String modelToXml(String type,Object object){
        StringBuffer buffer=new StringBuffer();
        buffer.append(XML_TAG);
        buffer.append(modelToXmlWithBody(type,object));
        return buffer.toString();
    }

    /**
     * 将model转为xml串
     * */
    public static String modelToXmlWithBody(String type,Object object){
        XStream xStream=getInstance();
        xStream.processAnnotations(object.getClass());
        //剔除所有的tab、制表符、换行符
        //此处需注意：在模型转xml时，默认会转为"<com.mygttp.SysUser>...</com.mygttp.SysUser>"此种类型，如果与xml报文中要求的不一致，注意做替换。
        String xml=xStream.toXML(object).replaceAll("\\s","").replace(object.getClass().getName(),"body");
        /*
        注意将模型内部引用的对象转换为xml中要求的节点名称
        if("模型1".equals(type)){
            xml=xml.replace(SysUser.class.getName(),"row");
        }else if("模型2".equals(type)){
            xml=xml.replace(SysUser.class.getName(),"row");
        }*/
        return xml;
    }
}
