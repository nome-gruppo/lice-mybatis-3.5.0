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
package org.apache.ibatis.io;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * A {@link VFS} implementation that works with the VFS API provided by JBoss 6.
 *
 * @author Ben Gunter
 */
public class JBoss6VFS extends VFS {
  private static final Log log = LogFactory.getLog(JBoss6VFS.class);

  /** A class that mimics a tiny subset of the JBoss VirtualFile class. */
  static class VirtualFile {
    static Class<?> virtFile1;
    static Method getPathNameRelativeTo;
    static Method getChildrenRecursively;
    
    Object objVF;

    VirtualFile(Object virtualFile) {
      this.objVF = virtualFile;
    }

    String getPathName(VirtualFile parent) {
      try {
        return invoke(getPathNameRelativeTo, objVF, parent.objVF);
      } catch (IOException e) {
        // This exception is not thrown by the called method
        log.error("This should not be possible. VirtualFile.getPathNameRelativeTo() threw IOException.");
        return null;
      }
    }

    List<VirtualFile> getChildren() throws IOException {
      List<?> objects = invoke(getChildrenRecursively, objVF);
      List<VirtualFile> children = new ArrayList<>(objects.size());
      for (Object object : objects) {
        children.add(new VirtualFile(object));
      }
      return children;
    }
  }

  /** A class that mimics a tiny subset of the JBoss VFS class. */
  static class Vfs {
    static Class<?> tmp;
    static Method getChild;

    private Vfs() {
      // Prevent Instantiation
    }

    static VirtualFile getChildMethod(URL url) throws IOException {
      Object o = invoke(getChild, tmp, url);
      return o == null ? null : new VirtualFile(o);
    }
  }

  /** Flag that indicates if this VFS is valid for the current environment. */
  private static Boolean valid;

  /**
   * Find all the classes and methods that are required to access the JBoss 6 VFS.
   */
  protected static synchronized void initialize() {
    if (valid == null) {
      // Assume valid. It will get flipped later if something goes wrong.
      valid = Boolean.TRUE;

      // Look up and verify required classes
      Vfs.tmp = checkNotNull(getClass("org.jboss.vfs.VFS"));
      VirtualFile.virtFile1 = checkNotNull(getClass("org.jboss.vfs.VirtualFile"));

      // Look up and verify required methods
      Vfs.getChild = checkNotNull(getMethod(Vfs.tmp, "getChild", URL.class));
      VirtualFile.getChildrenRecursively = checkNotNull(getMethod(VirtualFile.virtFile1, "getChildrenRecursively"));
      VirtualFile.getPathNameRelativeTo = checkNotNull(getMethod(VirtualFile.virtFile1, "getPathNameRelativeTo", VirtualFile.virtFile1));

      // Verify that the API has not changed
      checkReturnType(Vfs.getChild, VirtualFile.virtFile1);
      checkReturnType(VirtualFile.getChildrenRecursively, List.class);
      checkReturnType(VirtualFile.getPathNameRelativeTo, String.class);
    }
  }

  /**
   * Verifies that the provided object reference is null. If it is null, then this VFS is marked
   * as invalid for the current environment.
   *
   * @param object The object reference to check for null.
   */
  protected static <T> T checkNotNull(T object) {
    if (object == null) {
      setInvalid();
    }
    return object;
  }

  /**
   * Verifies that the return type of a method is what it is expected to be. If it is not, then
   * this VFS is marked as invalid for the current environment.
   *
   * @param method The method whose return type is to be checked.
   * @param expected A type to which the method's return type must be assignable.
   * @see Class#isAssignableFrom(Class)
   */
  protected static void checkReturnType(Method method, Class<?> expected) {
    if (method != null && !expected.isAssignableFrom(method.getReturnType())) {
      log.error("Method " + method.getClass().getName() + "." + method.getName()
          + "(..) should return " + expected.getName() + " but returns "
          + method.getReturnType().getName() + " instead.");
      setInvalid();
    }
  }

  /** Mark this {@link VFS} as invalid for the current environment. */
  protected static void setInvalid() {
    if (JBoss6VFS.valid == Boolean.TRUE) {
      log.debug("JBoss 6 VFS API is not available in this environment.");
      JBoss6VFS.valid = Boolean.FALSE;
    }
  }

  static {
    initialize();
  }

  @Override
  public boolean isValid() {
    return valid;
  }

  @Override
  public List<String> list(URL url, String path) throws IOException {
    VirtualFile directory;
    directory = Vfs.getChildMethod(url);
    if (directory == null) {
      return Collections.emptyList();
    }
    if (!path.endsWith("/")) {
      path += "/";
    }
    List<VirtualFile> children = directory.getChildren();
    List<String> names = new ArrayList<>(children.size());
    for (VirtualFile vf : children) {
      names.add(path + vf.getPathName(directory));
    }
    return names;
  }
}