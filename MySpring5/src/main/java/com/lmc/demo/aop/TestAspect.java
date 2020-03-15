package com.lmc.demo.aop;

import com.lmc.myspring.annotation.*;
import com.lmc.myspring.aop.MyJoinPoint;

import java.util.Arrays;

/**
 * @Author Li Meichao
 * @Date 2020/3/15 0015
 * @Description
 */
@MyAspect
public class TestAspect {

    //抽取公共的切入点表达式
    //1、本类引用
    //2、其他的切面引用
    @MyPointcut("execution(public int com.atguigu.aop.MathCalculator.*(..))")
    public void pointCut(){};

    //@Before在目标方法之前切入；切入点表达式（指定在哪个方法切入）
    @MyBefore("pointCut()")
    public void logStart(MyJoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        System.out.println(""+joinPoint.getSignature().getName()+"运行。。。@Before:参数列表是：{"+ Arrays.asList(args)+"}");
    }

    @MyAfter("com.atguigu.aop.LogAspects.pointCut()")
    public void logEnd(MyJoinPoint joinPoint){
        System.out.println(""+joinPoint.getSignature().getName()+"结束。。。@After");
    }

    //JoinPoint一定要出现在参数表的第一位
    @MyAfterReturning(value="pointCut()",returning="result")
    public void logReturn(MyJoinPoint joinPoint,Object result){
        System.out.println(""+joinPoint.getSignature().getName()+"正常返回。。。@AfterReturning:运行结果：{"+result+"}");
    }

    @MyAfterThrowing(value="pointCut()",throwing="exception")
    public void logException(MyJoinPoint joinPoint,Exception exception){
        System.out.println(""+joinPoint.getSignature().getName()+"异常。。。异常信息：{"+exception+"}");
    }

}
