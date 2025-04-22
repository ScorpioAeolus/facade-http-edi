package com.facade.edi.starter.support;

import com.facade.edi.starter.util.ILogInject;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.AdviceMode;

import javax.annotation.Resource;

/**
 * 远程调用接口抽象定义
 *               <ul>
 *                   <li>它是一个FactoryBean,注入使用时,调用getObject返回真实实例</li>
 *                   <li>实现InitializingBean接口,实例化完成后,使用ApiProxy创建接口实现</li>
 *              </ul>
 * @see org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
 *
 */
public class EdiServiceFactoryBean<T> implements FactoryBean<T>, InitializingBean, ILogInject {

    private final Class<T> targetClass;

    private final AdviceMode adviceMode;

    private final boolean preheat;

    @Resource
    private EdiApiProxyFactory ediApiProxyFactory;


    public EdiServiceFactoryBean(Class<T> targetClass,AdviceMode adviceMode,boolean preheat) {
        this.targetClass = targetClass;
        this.adviceMode = adviceMode;
        this.preheat = preheat;
    }

    @Override
    public T getObject() throws Exception {
        if (targetClass == null) {
            throw new NullPointerException("class is null");
        }
        if(AdviceMode.ASPECTJ == this.adviceMode) {
            return this.ediApiProxyFactory.newCglibInstance(targetClass,preheat);
        }
        return this.ediApiProxyFactory.newInstance(targetClass,preheat);
        //log.info(targetClass.getName() + "  edi api build success!!!");
    }

    @Override
    public Class<?> getObjectType() {
        return targetClass;
    }

    @Override
    public void afterPropertiesSet() {

    }

}
