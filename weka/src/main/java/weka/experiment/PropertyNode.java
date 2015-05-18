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
 *    PropertyNode.java
 *    Copyright (C) 1999-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.experiment;

import java.io.IOException;
import java.io.Serializable;

import weka.core.RevisionHandler;
import weka.core.RevisionUtils;

/**
 * Stores information on a property of an object: the class of the object with
 * the property; the property descriptor, and the current value.
 * 
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @version $Revision$
 */
public class PropertyNode implements Serializable, RevisionHandler {

  /** for serialization */
  private static final long serialVersionUID = -8718165742572631384L;

  /** The current property value */
  public Object value;

  /** The class of the object with this property */
  public Class<?> parentClass;

  /**
   * Creates a mostly empty property.
   * 
   * @param pValue a property value.
   */
  public PropertyNode(Object pValue) {

      throw new RuntimeException("This feature is not available in weka-android.");
  }


  /**
   * Returns a string description of this property.
   * 
   * @return a value of type 'String'
   */
  @Override
  public String toString() {

      return "Available properties";
  }

  /*
   * Handle serialization ourselves since PropertyDescriptor isn't serializable
   */
  private void writeObject(java.io.ObjectOutputStream out) throws IOException {

    try {
      out.writeObject(value);
    } catch (Exception ex) {
      throw new IOException("Can't serialize object: " + ex.getMessage());
    }
    out.writeObject(parentClass);
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException,
    ClassNotFoundException {
      throw new RuntimeException("This feature is not available in weka-android.");
  }

  /**
   * Returns the revision string.
   * 
   * @return the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }
} // PropertyNode
