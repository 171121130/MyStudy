package com.lmc.myspring.servlet;

import com.lmc.myspring.annotation.MyController;
import com.lmc.myspring.annotation.MyRequestMapping;
import com.lmc.myspring.context.MyApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Li Meichao
 * @Date 2020/3/2 0002
 * @Description
 */
public class MyDispatchServlet extends HttpServlet {

    private List<String> classNameList = new ArrayList<String>();
    private HashMap<String, Object> iocMap = new HashMap<String, Object>();
    MyApplicationContext applicationContext;
    private List<MyHandlerMapping> handlerMappings = new ArrayList<MyHandlerMapping>();
    private Map<MyHandlerMapping, HandlerAdapter> handlerAdapters = new HashMap<MyHandlerMapping, HandlerAdapter>();
    private List<MyViewResolver> viewResolvers = new ArrayList<MyViewResolver>();

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

        //初始化IoC容器
        applicationContext = new MyApplicationContext(servletConfig.getInitParameter("contextConfigLocation"));

        //初始化SpringMVC九大组件
        initStrategies(applicationContext);


        System.out.println("MySpring Framework is initialized");

        doTestPrintData();

    }

    private void initStrategies(MyApplicationContext applicationContext) {

        initMultiResolver(applicationContext);
        initLocaleResolver(applicationContext);
        initThemeResolver(applicationContext);

        //初始化HandlerMapping
        initHandlerMappings(applicationContext);

        //用来动态匹配、转换method参数
        initHandlerAdapters(applicationContext);

        initViewResolver(applicationContext);

        System.out.println("");

    }

    private void initViewResolver(MyApplicationContext applicationContext) {
        String templateRoot = applicationContext.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File fileFolder = new File(templateRootPath);
        for (String filePath : fileFolder.list()) {
            this.viewResolvers.add(new MyViewResolver(templateRootPath));
        }
    }

    private void initHandlerAdapters(MyApplicationContext applicationContext) {
        for (MyHandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new HandlerAdapter());
        }
    }

    private void initThemeResolver(MyApplicationContext applicationContext) {
    }

    private void initLocaleResolver(MyApplicationContext applicationContext) {
    }

    private void initMultiResolver(MyApplicationContext applicationContext) {
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        MyHandlerMapping handler = getHandler(req);

        if (handler == null) {
            try {
                resp.getWriter().write("404!!!");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        HandlerAdapter handlerAdapter = this.getHandlerAdapter(handler);
        MyModelView mv = handlerAdapter.handle(req, resp, handler);

        processModelView(req, resp, mv);

    }

    private void processModelView(HttpServletRequest req, HttpServletResponse resp, MyModelView mv) throws Exception {
        if (mv == null) {
            return;
        }
        if (this.viewResolvers.isEmpty()) {
            return;
        }
        for (MyViewResolver viewResolver : viewResolvers) {
            MyView view = viewResolver.resolveViewName(mv.getViewName(), null);
            if (view != null) {
                view.render(mv.getModel(), req, resp);
                return;
            }

        }
    }

    private HandlerAdapter getHandlerAdapter(MyHandlerMapping handler) {
        HandlerAdapter handlerAdapter = this.handlerAdapters.get(handler);
        if (handlerAdapter.supports(handler)) {
            return handlerAdapter;
        }
        return null;
    }

    private MyHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }
        String contextPath = req.getContextPath();
        String requestURL = req.getRequestURI();
        String url = requestURL.replaceAll(contextPath, "").replaceAll("/+", "/");

        for (MyHandlerMapping handler : this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }

    private void initHandlerMappings(MyApplicationContext applicationContext) {

        //拿到所有beanNames，ioc中获取实例，得到类信息，判断注解，遍历方法注解信息，url + method + instance生成handlerMap
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();

        for (String beanName : beanDefinitionNames) {
            Object instance = null;
            try {
                instance = applicationContext.getBean(beanName);
                Class<?> clazz = instance.getClass();
                if (!clazz.isAnnotationPresent(MyController.class)) {
                    continue;
                }
                String baseUrl = "";
                if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                    baseUrl = clazz.getAnnotation(MyRequestMapping.class).value().trim();
                }

                for (Method method : clazz.getMethods()) {

                    if (!method.isAnnotationPresent(MyRequestMapping.class)) {
                        continue;
                    }
                    MyRequestMapping myRequestMapping = method.getAnnotation(MyRequestMapping.class);
                    String regex = ("/" + baseUrl + myRequestMapping.value().trim().replaceAll("\\*", ".*")).replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new MyHandlerMapping(pattern, instance, method));
                    System.out.println("Mapping:" + regex + ", " + method);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }


    /**
     * 6、打印数据
     */
    private void doTestPrintData() {

        System.out.println("[INFO-6]----data------------------------");


        System.out.println("[classNameList]-->");
        for (String str : classNameList) {
            System.out.println(str);
        }

        System.out.println("[iocMap]-->");
        for (Map.Entry<String, Object> entry : iocMap.entrySet()) {
            System.out.println(entry);
        }

        System.out.println("[handlerMapping]-->");
        for (MyHandlerMapping myHandlerMapping : handlerMappings) {
            System.out.println(myHandlerMapping);
        }

        System.out.println("[INFO-6]----done-----------------------");

        System.out.println("====启动成功====");
        System.out.println("测试地址：http://localhost:8080/test/query?username=lmc");
        System.out.println("测试地址：http://localhost:8080/test/listClassName");
    }
}
