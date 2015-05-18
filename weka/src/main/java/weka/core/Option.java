/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    Option.java
 *    Copyright (C) 1999-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.core;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Class to store information about an option.
 * <p>
 *
 * Typical usage:
 * <p>
 *
 * <code>Option myOption = new Option("Uses extended mode.", "E", 0, "-E")); </code>
 * <p>
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @version $Revision$
 */
public class Option implements RevisionHandler {

  /** What does this option do? */
  private String m_Description;

  /** The synopsis. */
  private String m_Synopsis;

  /** What's the option's name? */
  private String m_Name;

  /** How many arguments does it take? */
  private int m_NumArguments;

  /**
   * Creates new option with the given parameters.
   *
   * @param description the option's description
   * @param name the option's name
   * @param numArguments the number of arguments
   */
  public Option(String description, String name, int numArguments,
    String synopsis) {

    m_Description = description;
    m_Name = name;
    m_NumArguments = numArguments;
    m_Synopsis = synopsis;
  }

  /**
   * Get a list of options for a class. Options identified by this method are
   * bean properties (with get/set methods) annotated using the OptionMetadata
   * annotation. All options from the class up to, but not including, the
   * supplied oldest superclass are returned.
   * 
   * 
   * @param childClazz the class to get options for
   * @param oldestAncestorClazz the oldest superclass (inclusive) at which to
   *          stop getting options from
   * @return a list of options
   */
  public static Vector<Option> listOptionsForClassHierarchy(
    Class<?> childClazz, Class<?> oldestAncestorClazz) {
    Vector<Option> results = listOptionsForClass(childClazz);

    Class<?> parent = childClazz;
    do {
      parent = parent.getSuperclass();
      if (parent == null) {
        break;
      }
      results.addAll(listOptionsForClass(parent));
    } while (!parent.equals(oldestAncestorClazz));

    return results;
  }

  /**
   * Adds all methods from the supplied class to the supplied list of methods.
   * 
   * @param clazz the class to get methods from
   * @param methList the list to add them to
   */
  protected static void addMethodsToList(Class<?> clazz, List<Method> methList) {
    Method[] methods = clazz.getDeclaredMethods();
    for (Method m : methods) {
      methList.add(m);
    }
  }

  /**
   * Gets a list of options for the supplied class. Only examines immediate
   * methods in the class (does not consider superclasses). Options identified
   * by this method are bean properties (with get/set methods) annotated using
   * the OptionMetadata annotation.
   * 
   * @param clazz the class to examine for options
   * @return a list of options
   */
  public static Vector<Option> listOptionsForClass(Class<?> clazz) {
    Vector<Option> results = new Vector<Option>();
    List<Method> allMethods = new ArrayList<Method>();
    addMethodsToList(clazz, allMethods);

    Class<?>[] interfaces = clazz.getInterfaces();
    for (Class c : interfaces) {
      addMethodsToList(c, allMethods);
    }

    Option[] unsorted = new Option[allMethods.size()];
    int[] opOrder = new int[allMethods.size()];
    for (int i = 0; i < opOrder.length; i++) {
      opOrder[i] = Integer.MAX_VALUE;
    }
    int index = 0;
    for (Method m : allMethods) {
      OptionMetadata o = m.getAnnotation(OptionMetadata.class);
      if (o != null) {
        if (o.commandLineParamName().length() > 0) {
          opOrder[index] = o.displayOrder();
          String description = o.description();
          if (!description.startsWith("\t")) {
            description = "\t" + description;
          }
          description = description.replace("\n", "\n\t");
          String name = o.commandLineParamName();
          if (name.startsWith("-")) {
            name = name.substring(1, name.length());
          }
          String synopsis = o.commandLineParamSynopsis();
          if (!synopsis.startsWith("-")) {
            synopsis = "-" + synopsis;
          }
          int numParams = o.commandLineParamIsFlag() ? 0 : 1;
          Option option = new Option(description, name, numParams, synopsis);
          unsorted[index] = option;
          index++;
        }
      }
    }

    int[] sortedOpts = Utils.sort(opOrder);
    for (int i = 0; i < opOrder.length; i++) {
      if (opOrder[i] < Integer.MAX_VALUE) {
        results.add(unsorted[sortedOpts[i]]);
      }
    }

    return results;
  }

  /**
   * Get the settings of the supplied object. Settings identified by this method
   * are bean properties (with get/set methods) annotated using the
   * OptionMetadata annotation. All options from the class up to, but not
   * including, the supplied oldest superclass are returned.
   * 
   * @param target the target object to get settings for
   * @param oldestAncestorClazz the oldest superclass at which to stop getting
   *          options from
   * @return
   */
  public static String[] getOptionsForHierarchy(Object target,
    Class<?> oldestAncestorClazz) {

    ArrayList<String> options = new ArrayList<String>();
    for (String s : getOptions(target, target.getClass())) {
      options.add(s);
    }

    Class<?> parent = target.getClass();
    do {
      parent = parent.getSuperclass();
      if (parent == null) {
        break;
      }
      for (String s : getOptions(target, parent)) {
        options.add(s);
      }
    } while (!parent.equals(oldestAncestorClazz));

    return options.toArray(new String[options.size()]);
  }

  /**
   * Get the settings of the supplied object. Settings identified by this method
   * are bean properties (with get/set methods) annotated using the
   * OptionMetadata annotation. Options belonging to the targetClazz (either the
   * class of the target or one of its superclasses) are returned.
   * 
   * @param target the target to extract settings from
   * @param targetClazz the class to consider for obtaining settings - i.e.
   *          annotated methods from this class will have their values
   *          extracted. This class is expected to be either the class of the
   *          target or one of its superclasses
   * @return an array of settings
   */
  public static String[] getOptions(Object target, Class<?> targetClazz) {

      throw new RuntimeException("This feature is not available in weka-android.");
  }

  /**
   * Construct a String containing the class name of an OptionHandler and its
   * option settings
   *
   * @param handler the OptionHandler to construct an option string for
   * @return a String containing the name of the handler class and its options
   */
  protected static String
    getOptionStringForOptionHandler(OptionHandler handler) {
    String optHandlerClassName = handler.getClass().getCanonicalName();
    String optsVal = Utils.joinOptions(handler.getOptions());
    String totalOptVal = optHandlerClassName + " " + optsVal;

    return totalOptVal;
  }

  /**
   * Sets options on the target object. Settings identified by this method are
   * bean properties (with get/set methods) annotated using the OptionMetadata
   * annotation. All options from the class up to, but not including, the
   * supplied oldest superclass are processed in order.
   *
   * @param options the options to set
   * @param target the target on which to set options
   * @param oldestAncestorClazz the oldest superclass at which to stop setting
   *          options
   */
  public static void setOptionsForHierarchy(String[] options, Object target,
    Class<?> oldestAncestorClazz) {

    setOptions(options, target, target.getClass());

    Class<?> parent = target.getClass();
    do {
      parent = parent.getSuperclass();
      if (parent == null) {
        break;
      }

      setOptions(options, target, parent);
    } while (!parent.equals(oldestAncestorClazz));
  }

  /**
   * Sets options on the target object. Settings identified by this method are
   * bean properties (with get/set methods) annotated using the OptionMetadata
   * annotation. Options from just the supplied targetClazz (which is expected
   * to be either the class of the target or one of its superclasses) are set.
   *
   * @param options the options to set
   * @param target the target on which to set options
   * @param targetClazz the class containing options to be be set - i.e.
   *          annotated option methods in this class will have their values set.
   *          This class is expected to be either the class of the target or one
   *          of its superclasses
   */
  public static void setOptions(String[] options, Object target, Class<?> targetClazz) {
      throw new RuntimeException("This feature is not available in weka-android.");
  }

  /**
   * Construct an instance of an option handler from a String specifying its
   * class name and option values
   *
   * @param optionValue a String containing the class of the option handler
   *          followed by its options
   * @return an instantiated option handling object
   * @throws Exception if a problem occurs
   */
  protected static Object constructOptionHandlerValue(String optionValue)
    throws Exception {
    String[] optHandlerSpec = Utils.splitOptions(optionValue);
    if (optHandlerSpec.length == 0) {
      throw new Exception("Invalid option handler specification " + "string '"
        + optionValue);
    }
    String optionHandler = optHandlerSpec[0];
    optHandlerSpec[0] = "";
    Object handler = Utils.forName(null, optionHandler, optHandlerSpec);

    return handler;
  }

  /**
   * Set an option value on a target object
   *
   * @param setter the Method object for the setter method of the option to set
   * @param target the target object on which to set the option
   * @param valueToSet the value of the option to set
   * @throws InvocationTargetException if a problem occurs
   * @throws IllegalAccessException if a problem occurs
   */
  protected static void setOption(Method setter, Object target,
    Object valueToSet) throws InvocationTargetException, IllegalAccessException {
    Object[] setterArgs = { valueToSet };
    setter.invoke(target, setterArgs);
  }

  /**
   * Returns the option's description.
   *
   * @return the option's description
   */
  public String description() {

    return m_Description;
  }

  /**
   * Returns the option's name.
   *
   * @return the option's name
   */
  public String name() {

    return m_Name;
  }

  /**
   * Returns the option's number of arguments.
   *
   * @return the option's number of arguments
   */
  public int numArguments() {

    return m_NumArguments;
  }

  /**
   * Returns the option's synopsis.
   *
   * @return the option's synopsis
   */
  public String synopsis() {

    return m_Synopsis;
  }

  /**
   * Returns the revision string.
   *
   * @return the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }
}
