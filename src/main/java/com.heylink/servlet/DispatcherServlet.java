package com.heylink.servlet;

import com.heylink.annotation.Controller;
import com.heylink.annotation.Qualifier;
import com.heylink.annotation.RequestMapping;
import com.heylink.annotation.Service;
import com.heylink.controller.SpringmvcController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * introductions:
 * created by Heylink on 2018/4/23 19:46
 */
public class DispatcherServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    List<String> packageNames = new ArrayList<>();
    // 所有类的实例，key是注解的value,value是所有类的实例
    Map<String, Object> instanceMap = new HashMap<>();
    Map<String, Object> handlerMap = new HashMap<>();

    public DispatcherServlet() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        //包扫描,获取包中的文件
        scanPackage("com.heylink");
        //根据文件名查找controller与service的类，键为文件路径，值为类对象
        try {
            filterAndInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //对得到的controller键值对进行处理，键为方法路径，值为方法对象
        handleMap();
        //实现注入
        ioc();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        String url = req.getRequestURI();
        /*
        *获取当前项目根地址
        * 比如你现在的url是192.1.1.1:8080/simplespringmvc/test/insert
        * tomcat配置的当前项目访问地址是192.1.1.1:8080/simplespringmvc
        * request.getContextPath（）得到的就是192.1.1.1:8080/simplespringmvc
        * */
        String context = req.getContextPath();
        //同上，类似于拿到test/insert 相对路径
        String path = url.replace(context, "");
        //根据  /test/insert  就能拿到方法对象(注意有两个斜杠)
        Method method = (Method) handlerMap.get(path);
        //拿到 test对应的对象——也就是我们@Controller注解值为test的SpringmvcController类
        Object controllerObj = instanceMap.get(path.split("/")[1]);
        //因为是简单模拟，我们也知道就一个SpringmvcController，所以可以强转
        SpringmvcController controller = (SpringmvcController)controllerObj;

        try {
            method.invoke(controller, new Object[]{req, resp, null});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //实现依赖注入,其实就是给注解过的属性进行注入
    private void ioc() {
        if (instanceMap.size() <= 0) {
            return;
        }
        //遍历每一个键值对
        for (Map.Entry<String, Object> entry : instanceMap.entrySet()) {
            //对每一个实例都去遍历其属性  !!切记这里要用getDeclaredFields
            for (Field field : entry.getValue().getClass().getDeclaredFields()) {
                //设置，可访问私有属性
                field.setAccessible(true);
                //对属性进行判断，是否有注解Qualifier
                if (field.isAnnotationPresent(Qualifier.class)){
                    Qualifier qualifier = field.getAnnotation(Qualifier.class);
                    //拿到注解的类名
                    String iocClazz = qualifier.value();
                    //将entry.getValue()这个对象中的field属性对象设为新值
                    try {
                        field.set(entry.getValue(), instanceMap.get(iocClazz));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //???
        SpringmvcController springmvcController = (SpringmvcController) instanceMap.get("test");
        System.out.println(springmvcController);
    }

    //处理映射关系 controller/requestMapping将路径与对应的具体方法进行映射
    private void handleMap() {
        if (instanceMap.size() <= 0) {
            return;
        }
        //遍历每一个键值对
        for (Map.Entry<String, Object> entry : instanceMap.entrySet()) {
            //寻找controller注解的类
            if (entry.getValue().getClass().isAnnotationPresent(Controller.class)) {
                //拿到controller注解的value
                Controller controller = entry.getValue().getClass().getAnnotation(Controller.class);
                String firstPath = controller.value();
                //拿到该类下所有requestMapping注解的方法与RequestMapping中的值
                for (Method method : entry.getValue().getClass().getMethods()) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        String secondPath = requestMapping.value();
                        //将相对路径作为键，将方法对象作为值添加到map中
                        handlerMap.put("/" + firstPath + "/" + secondPath, method);
                    }
                }
            }
        }
    }

    //根据获得的文件进行过滤（选择Controller与Service注解的类）和实例化
    private void filterAndInstance() throws Exception {
        if (packageNames.size() <= 0) {
            return;
        }

        for (String pName : packageNames) {
            String cName = pName.replace(".class", "").trim();
            Class<?> clazz = Class.forName(cName);
            if (clazz.isAnnotationPresent(Controller.class)) {
                //如果是被Controller注解
                Object instance = clazz.newInstance();
                Controller controller = clazz.getAnnotation(Controller.class);
                String key = controller.value();
                instanceMap.put(key, instance);
            } else if (clazz.isAnnotationPresent(Service.class)) {
                //如果是被Service注解
                Object instance = clazz.newInstance();
                Service service = clazz.getAnnotation(Service.class);
                String key = service.value();
                instanceMap.put(key, instance);
            }
        }
    }

    //扫描包
    private void scanPackage(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/" + replaceTo(packageName));
        String filePath = url.getFile();
        File file = new File(filePath);
        String[] childrenFileList = file.list();
        for (String path : childrenFileList) {
            File childrenFile = new File(filePath + path);
            if (childrenFile.isDirectory()) {
                //递归查找所有子文件
                scanPackage(packageName + "." + childrenFile.getName());
            }else {
                //将子文件添加到数组表中
                packageNames.add(packageName + "." + childrenFile.getName());
            }
        }
    }

    // 将所有的.转义获取对应的路径
    private String replaceTo(String path) {
        //将.转为/
        return path.replaceAll("\\.", "/");
    }

}
