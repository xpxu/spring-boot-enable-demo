package com.example.mark.interceptor;

import com.example.mark.annotation.Mark;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;

/**
 * DATE 2018/9/23.
 * @author xupeng.
 */
public class MarkMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable{

        Mark annotation = invocation.getMethod().getAnnotation(Mark.class);
        Object ret = null;

        if (invocation instanceof ProxyMethodInvocation) {
            try {
                ret = ((ProxyMethodInvocation) invocation).invocableClone().proceed();
            }
            catch (Exception e) {
                throw e;
            }
            catch (Error e) {
                throw e;
            }
            catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        }
        else {
            throw new IllegalStateException(
                    "MethodInvocation of the wrong type detected - this should not happen with Spring AOP, " +
                            "so please raise an issue if you see this exception");
        }

        return annotation.message() + "," + (String)ret;
    }

}
