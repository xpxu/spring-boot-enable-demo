package com.example.mark.annotation;

import com.example.mark.interceptor.MarkMethodInterceptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

/**
 * DATE 2018/9/23.
 * @author xupeng.
 */
public class MarkConfigration extends AbstractPointcutAdvisor implements IntroductionAdvisor, BeanFactoryAware {

    private Advice advice;

    private Pointcut pointcut;

    private BeanFactory beanFactory;

    @PostConstruct
    public void init() {
        Set<Class<? extends Annotation>> annotationTypes = new LinkedHashSet<Class<? extends Annotation>>(1);
        annotationTypes.add(Mark.class);
        this.pointcut = buildPointcut(annotationTypes);
        this.advice = buildAdvice();
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }

    protected Advice buildAdvice() {
        MarkMethodInterceptor interceptor = new MarkMethodInterceptor();
        return interceptor;
    }

    protected Pointcut buildPointcut(Set<Class<? extends Annotation>> annotationTypes) {
        ComposablePointcut result = null;
        for (Class<? extends Annotation> retryAnnotationType : annotationTypes) {
            Pointcut filter = new AnnotationMethodPointcut(retryAnnotationType);
            if (result == null) {
                result = new ComposablePointcut(filter);
            }
            else {
                result.union(filter);
            }
        }
        return result;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public ClassFilter getClassFilter() {
        return pointcut.getClassFilter();
    }

    @Override
    public Class<?>[] getInterfaces() {
        return new Class[] {};
    }

    @Override
    public void validateInterfaces() throws IllegalArgumentException {
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    /**
     * 反省目标class，通过注解，确定匹配的目标方法
     */
    private final class AnnotationMethodPointcut extends StaticMethodMatcherPointcut {

        private final MethodMatcher methodResolver;

        AnnotationMethodPointcut(Class<? extends Annotation> annotationType) {
            this.methodResolver = new AnnotationMethodMatcher(annotationType);
            setClassFilter(new MethodBasedAnnotationClassFilter(annotationType));
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            return this.methodResolver.matches(method, targetClass);
        }
    }

    /**
     * 扫描class，通过反省method上的注解，确定目标class
     */
    private final class MethodBasedAnnotationClassFilter extends AnnotationClassFilter {

        private final AnnotationMethodsResolver methodResolver;

        MethodBasedAnnotationClassFilter(Class<? extends Annotation> annotationType) {
            super(annotationType, true);
            this.methodResolver = new AnnotationMethodsResolver(annotationType);
        }

        @Override
        public boolean matches(Class<?> clazz) {
            return this.methodResolver.hasAnnotatedMethods(clazz);
        }

    }

    private static class AnnotationMethodsResolver {

        private Class<? extends Annotation> annotationType;

        public AnnotationMethodsResolver(Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }

        public boolean hasAnnotatedMethods(Class<?> clazz) {
            final AtomicBoolean found = new AtomicBoolean(false);
            ReflectionUtils.doWithMethods(clazz,
                    new MethodCallback() {
                        @Override
                        public void doWith(Method method) throws IllegalArgumentException,
                                IllegalAccessException {
                            if (found.get()) {
                                return;
                            }
                            Annotation annotation = AnnotationUtils.findAnnotation(method,
                                    annotationType);
                            if (annotation != null) { found.set(true); }
                        }
                    });
            return found.get();
        }
    }
}
