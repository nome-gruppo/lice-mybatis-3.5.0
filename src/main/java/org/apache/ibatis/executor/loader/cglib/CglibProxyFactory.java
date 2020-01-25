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
import java.sql.SQLException;
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
      throw new IllegalStateException(
          "Cannot enable lazy loading because CGLIB is not available. Add CGLIB to your classpath.", e);
    }
  }

  @Override
  public Object createProxy(Object target, ResultLoaderMap lazyLoader, Configuration configuration,
      ObjectFactory objectFactory, List<Class<?>> constructorArgTypes, List<Object> mConstructorArgs) {
    return EnhancedResultObjectProxyImpl.createProxy(target, lazyLoader, configuration, objectFactory,
        constructorArgTypes, mConstructorArgs);
  }

  public Object createDeserializationProxy(Object target, Map<String, ResultLoaderMap.LoadPair> unloadedProperties,
      ObjectFactory objectFactory, List<Class<?>> constructorArgTypes, List<Object> mConstructorArg) {
    return EnhancedDeserializationProxyImpl.createProxy(target, unloadedProperties, objectFactory, constructorArgTypes,
        mConstructorArg);
  }

  @Override
  public void setProperties(Properties properties) {
    // Not Implemented
  }

  static Object crateProxy(Class<?> type11, Callback callback, List<Class<?>> constructorArgTypes,
      List<Object> mConstructorArg) {
    Enhancer enhancer1 = new Enhancer();
    enhancer1.setCallback(callback);
    enhancer1.setSuperclass(type11);
    try {
      type11.getDeclaredMethod(WRITE_REPLACE_METHOD);
      // ObjectOutputStream will call writeReplace of objects returned by writeReplace
      if (LogHolder.log.isDebugEnabled()) {
        LogHolder.log
            .debug(WRITE_REPLACE_METHOD + " method was found on bean " + type11 + ", make sure it returns this");
      }
    } catch (NoSuchMethodException e) {
      enhancer1.setInterfaces(new Class[] { WriteReplaceInterface.class });
    } catch (SecurityException e) {
      // nothing to do here
    }
    Object enhanced;
    if (constructorArgTypes.isEmpty()) {
      enhanced = enhancer1.create();
    } else {
      Class<?>[] type11sArray = constructorArgTypes.toArray(new Class[constructorArgTypes.size()]);
      Object[] valuesArray = mConstructorArg.toArray(new Object[mConstructorArg.size()]);
      enhanced = enhancer1.create(type11sArray, valuesArray);
    }
    return enhanced;
  }

  private static class EnhancedResultObjectProxyImpl implements MethodInterceptor {

    private final Class<?> type11;
    private final ResultLoaderMap lazyLoader;
    private final boolean aggressive;
    private final Set<String> lazyLoadTrigger_Methods;
    private final ObjectFactory objectFactory;
    private final List<Class<?>> constructorArgType;
    private final List<Object> mConstructorArg;

    private EnhancedResultObjectProxyImpl(Class<?> type11, ResultLoaderMap lazyLoader, Configuration configuration,
        ObjectFactory objectFactory, List<Class<?>> constructorArgType, List<Object> mConstructorArg) {
      this.type11 = type11;
      this.lazyLoader = lazyLoader;
      this.aggressive = configuration.isAggressiveLazyLoading();
      this.lazyLoadTrigger_Methods = configuration.getLazyLoadTriggerMethods();
      this.objectFactory = objectFactory;
      this.constructorArgType = constructorArgType;
      this.mConstructorArg = mConstructorArg;
    }

    public static Object createProxy(Object target, ResultLoaderMap lazyLoader, Configuration configuration,
        ObjectFactory objectFactory, List<Class<?>> constructorArgType, List<Object> mConstructorArg) {
      final Class<?> type11 = target.getClass();
      EnhancedResultObjectProxyImpl callback = new EnhancedResultObjectProxyImpl(type11, lazyLoader, configuration,
          objectFactory, constructorArgType, mConstructorArg);
      Object enhanced = crateProxy(type11, callback, constructorArgType, mConstructorArg);
      PropertyCopier.copyBeanProperties(type11, target, enhanced);
      return enhanced;
    }

    public Object intercept(Object enhanced, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
      final String methodName = method.getName();
      try {
        synchronized (lazyLoader) {
          if (WRITE_REPLACE_METHOD.equals(methodName)) {
            Object original_;
            if (constructorArgType.isEmpty()) {
              original_ = objectFactory.create(type11);
            } else {
              original_ = objectFactory.create(type11, constructorArgType, mConstructorArg);
            }
            PropertyCopier.copyBeanProperties(type11, enhanced, original_);
            if (lazyLoader.size() > 0) {
              return new CglibSerialStateHolder(original_, lazyLoader.getProperties(), objectFactory,
                  constructorArgType, mConstructorArg);
            } else {
              return original_;
            }
          } else {
            interceptElse(lazyLoader, methodName);
          }
        }
        return methodProxy.invokeSuper(enhanced, args);
      } catch (Throwable t) {
        throw ExceptionUtil.unwrapThrowable(t);
      }
    }// end method

    private void interceptElse(ResultLoaderMap lazyLoader, String methodName) throws SQLException {
      if (lazyLoader.size() > 0 && !FINALIZE_METHOD.equals(methodName)) {
        if (aggressive || lazyLoadTrigger_Methods.contains(methodName)) {
          lazyLoader.loadAll();
        } else if (PropertyNamer.isSetter(methodName)) {
          final String property = PropertyNamer.methodToProperty(methodName);
          lazyLoader.remove(property);
        } else if (PropertyNamer.isGetter(methodName)) {
          final String property = PropertyNamer.methodToProperty(methodName);
          if (lazyLoader.hasLoader(property)) {
            lazyLoader.load(property);
          }
        }
      }
    }

  }// end inner class



  private static class EnhancedDeserializationProxyImpl extends AbstractEnhancedDeserializationProxy implements MethodInterceptor {

    private EnhancedDeserializationProxyImpl(Class<?> type11, Map<String, ResultLoaderMap.LoadPair> unloadedProperties, ObjectFactory objectFactory,
            List<Class<?>> constructorArgType, List<Object> mConstructorArg) {
      super(type11, unloadedProperties, objectFactory, constructorArgType, mConstructorArg);
    }

    public static Object createProxy(Object target, Map<String, ResultLoaderMap.LoadPair> unloadedProperties, ObjectFactory objectFactory,
            List<Class<?>> constructorArgType, List<Object> mConstructorArg) {
      final Class<?> type11 = target.getClass();
      EnhancedDeserializationProxyImpl callback = new EnhancedDeserializationProxyImpl(type11, unloadedProperties, objectFactory, constructorArgType, mConstructorArg);
      Object enhanced = crateProxy(type11, callback, constructorArgType, mConstructorArg);
      PropertyCopier.copyBeanProperties(type11, target, enhanced);
      return enhanced;
    }

    @Override
    public Object intercept(Object enhanced, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
      final Object o = super.invoke(enhanced, method);
      return o instanceof AbstractSerialStateHolder ? o : methodProxy.invokeSuper(o, args);
    }

    @Override
    protected AbstractSerialStateHolder newSerialStateHolder(Object userBean, Map<String, ResultLoaderMap.LoadPair> unloadedProperties, ObjectFactory objectFactory,
            List<Class<?>> constructorArgType, List<Object> mConstructorArg) {
      return new CglibSerialStateHolder(userBean, unloadedProperties, objectFactory, constructorArgType, mConstructorArg);
    }
  }

  private static class LogHolder {
    private static final Log log = LogFactory.getLog(CglibProxyFactory.class);
  }

}
