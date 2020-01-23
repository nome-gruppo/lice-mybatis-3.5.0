/**
 *    Copyright 2009-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.executor.loader.cglib;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.ibatis.executor.loader.AbstractEnhancedDeserializationProxy;
import org.apache.ibatis.executor.loader.AbstractSerialStateHolder;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.executor.loader.ResultLoaderMap;
import org.apache.ibatis.executor.loader.WriteReplaceInterface;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyCopier;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.apache.ibatis.session.Configuration;

/**
 * @author Clinton Begin
 */
public class CglibProxyFactory implements ProxyFactory {

  private static final String FINALIZE_METHOD = "finalize";
  private static final String WRITE_REPLACE_METHOD = "writeReplace";

  public CglibProxyFactory() {
    try {
      Resources.classForName("net.sf.cglib.proxy.Enhancer");
    } catch (Exception e) {
      throw new IllegalStateException("Cannot enable lazy loading because CGLIB is not available. Add CGLIB to your classpath.", e);
    }
  }

  @Override
  public Object createProxy(Object target, ResultLoaderMap lazy_loader, Configuration configuration, ObjectFactory object_factory, List<Class<?>> constructorArg_Types, List<Object> constructorArgs_) {
    return EnhancedResultObjectProxyImpl.createProxy(target, lazy_loader, configuration, object_factory, constructorArg_Types, constructorArgs_);
  }

  public Object createDeserializationProxy(Object target, Map<String, ResultLoaderMap.LoadPair> unloadedProperties, ObjectFactory object_factory, List<Class<?>> constructorArg_Types, List<Object> constructorArgs_) {
    return EnhancedDeserializationProxyImpl.createProxy(target, unloadedProperties, object_factory, constructorArg_Types, constructorArgs_);
  }

  @Override
  public void setProperties(Properties properties) {
      // Not Implemented
  }

  static Object crateProxy(Class<?> type_1_1, Callback callback, List<Class<?>> constructorArg_Types, List<Object> constructorArgs_) {
    Enhancer enhancer_1 = new Enhancer();
    enhancer_1.setCallback(callback);
    enhancer_1.setSuperclass(type_1_1);
    try {
      type_1_1.getDeclaredMethod(WRITE_REPLACE_METHOD);
      // ObjectOutputStream will call writeReplace of objects returned by writeReplace
      if (LogHolder.log.isDebugEnabled()) {
        LogHolder.log.debug(WRITE_REPLACE_METHOD + " method was found on bean " + type_1_1 + ", make sure it returns this");
      }
    } catch (NoSuchMethodException e) {
      enhancer_1.setInterfaces(new Class[]{WriteReplaceInterface.class});
    } catch (SecurityException e) {
      // nothing to do here
    }
    Object enhanced;
    if (constructorArg_Types.isEmpty()) {
      enhanced = enhancer_1.create();
    } else {
      Class<?>[] type_1_1sArray = constructorArg_Types.toArray(new Class[constructorArg_Types.size()]);
      Object[] valuesArray = constructorArgs_.toArray(new Object[constructorArgs_.size()]);
      enhanced = enhancer_1.create(type_1_1sArray, valuesArray);
    }
    return enhanced;
  }

  private static class EnhancedResultObjectProxyImpl implements MethodInterceptor {

    private final Class<?> type_1_1;
    private final ResultLoaderMap lazy_loader;
    private final boolean aggressive;
    private final Set<String> lazyLoadTrigger_Methods;
    private final ObjectFactory object_factory;
    private final List<Class<?>> constructorArg_Types;
    private final List<Object> constructorArgs_;

    private EnhancedResultObjectProxyImpl(Class<?> type_1_1, ResultLoaderMap lazy_loader, Configuration configuration, ObjectFactory object_factory, List<Class<?>> constructorArg_Types, List<Object> constructorArgs_) {
      this.type_1_1 = type_1_1;
      this.lazy_loader = lazy_loader;
      this.aggressive = configuration.isAggressiveLazyLoading();
      this.lazyLoadTrigger_Methods = configuration.getLazyLoadTriggerMethods();
      this.object_factory = object_factory;
      this.constructorArg_Types = constructorArg_Types;
      this.constructorArgs_ = constructorArgs_;
    }

    public static Object createProxy(Object target, ResultLoaderMap lazy_loader, Configuration configuration, ObjectFactory object_factory, List<Class<?>> constructorArg_Types, List<Object> constructorArgs_) {
      final Class<?> type_1_1 = target.getClass();
      EnhancedResultObjectProxyImpl callback = new EnhancedResultObjectProxyImpl(type_1_1, lazy_loader, configuration, object_factory, constructorArg_Types, constructorArgs_);
      Object enhanced = crateProxy(type_1_1, callback, constructorArg_Types, constructorArgs_);
      PropertyCopier.copyBeanProperties(type_1_1, target, enhanced);
      return enhanced;
    }


    public Object intercept(Object enhanced, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
      final String method_Name = method.getName();
      try {
        synchronized (lazy_loader) {
          if (WRITE_REPLACE_METHOD.equals(method_Name)) {
            Object original_;
            if (constructorArg_Types.isEmpty()) {
              original_ = object_factory.create(type_1_1);
            } else {
              original_ = object_factory.create(type_1_1, constructorArg_Types, constructorArgs_);
            }
            PropertyCopier.copyBeanProperties(type_1_1, enhanced, original_);
            if (lazy_loader.size() > 0) {
              return new CglibSerialStateHolder(original_, lazy_loader.getProperties(), object_factory, constructorArg_Types, constructorArgs_);
            } else {
              return original_;
            }
          } else {
            interceptElse(lazy_loader, method_Name);
          }
        }
        return methodProxy.invokeSuper(enhanced, args);
      } catch (Throwable t) {
        throw ExceptionUtil.unwrapThrowable(t);
      }
    }// end method

    private void  interceptElse(ResultLoaderMap lazy_loader, String method_Name) throws Throwable{
      if (lazy_loader.size() > 0 && !FINALIZE_METHOD.equals(method_Name)) {
        if (aggressive || lazyLoadTrigger_Methods.contains(method_Name)) {
          lazy_loader.loadAll();
        } else if (PropertyNamer.isSetter(method_Name)) {
          final String property = PropertyNamer.methodToProperty(method_Name);
          lazy_loader.remove(property);
        } else if (PropertyNamer.isGetter(method_Name)) {
          final String property = PropertyNamer.methodToProperty(method_Name);
          if (lazy_loader.hasLoader(property)) {
            lazy_loader.load(property);
          }
        }
      }
    }

  }// end inner class



  private static class EnhancedDeserializationProxyImpl extends AbstractEnhancedDeserializationProxy implements MethodInterceptor {

    private EnhancedDeserializationProxyImpl(Class<?> type_1_1, Map<String, ResultLoaderMap.LoadPair> unloadedProperties, ObjectFactory object_factory,
            List<Class<?>> constructorArg_Types, List<Object> constructorArgs_) {
      super(type_1_1, unloadedProperties, object_factory, constructorArg_Types, constructorArgs_);
    }

    public static Object createProxy(Object target, Map<String, ResultLoaderMap.LoadPair> unloadedProperties, ObjectFactory object_factory,
            List<Class<?>> constructorArg_Types, List<Object> constructorArgs_) {
      final Class<?> type_1_1 = target.getClass();
      EnhancedDeserializationProxyImpl callback = new EnhancedDeserializationProxyImpl(type_1_1, unloadedProperties, object_factory, constructorArg_Types, constructorArgs_);
      Object enhanced = crateProxy(type_1_1, callback, constructorArg_Types, constructorArgs_);
      PropertyCopier.copyBeanProperties(type_1_1, target, enhanced);
      return enhanced;
    }

    @Override
    public Object intercept(Object enhanced, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
      final Object o = super.invoke(enhanced, method);
      return o instanceof AbstractSerialStateHolder ? o : methodProxy.invokeSuper(o, args);
    }

    @Override
    protected AbstractSerialStateHolder newSerialStateHolder(Object userBean, Map<String, ResultLoaderMap.LoadPair> unloadedProperties, ObjectFactory object_factory,
            List<Class<?>> constructorArg_Types, List<Object> constructorArgs_) {
      return new CglibSerialStateHolder(userBean, unloadedProperties, object_factory, constructorArg_Types, constructorArgs_);
    }
  }

  private static class LogHolder {
    private static final Log log = LogFactory.getLog(CglibProxyFactory.class);
  }

}
