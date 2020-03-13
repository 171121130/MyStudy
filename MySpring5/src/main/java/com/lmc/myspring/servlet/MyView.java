package com.lmc.myspring.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Li Meichao
 * @Date 2020/3/13 0013
 * @Description
 */
public class MyView {
    private File file;

    public MyView(File file) {
        this.file = file;
    }

    public void render(Map<String,?> model, HttpServletRequest req, HttpServletResponse resp) throws Exception {

        //正则表达式，{[^}]+}，匹配{}中多个除}之外的字符
        Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        RandomAccessFile ra = new RandomAccessFile(this.file, "r");
        try {
/*            inputStreamReader = new InputStreamReader(new FileInputStream(this.file), "ISO-8859-1");
            bufferedReader = new BufferedReader(inputStreamReader);*/
            String line;
            StringBuffer sb = new StringBuffer();

            while (null != (line = ra.readLine())) {
                line = new String(line.getBytes("ISO-8859-1"), "utf-8");
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String paramName = matcher.group().replaceAll("￥\\{|\\}", "");
                    Object value = model.get(paramName);
                    if (value == null) {
                        return;
                    }
                    line = matcher.replaceFirst(value.toString());
                    matcher = pattern.matcher(line);
                }
                sb.append(line);
            }

            resp.setCharacterEncoding("utf-8");
            resp.getWriter().write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ra.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
