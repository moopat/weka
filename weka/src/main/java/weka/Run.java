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
 *    Run.java
 *    Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package weka;

import java.util.ArrayList;
import java.util.List;

import weka.core.Utils;

/**
 * Helper class that executes Weka schemes from the command line. Performs
 * Suffix matching on the scheme name entered by the user - e.g.<br>
 * <br>
 * 
 * java weka.Run NaiveBayes <br>
 * <br>
 * 
 * will prompt the user to choose among
 * weka.classifiers.bayes.ComplementNaiveBayes,
 * weka.classifiers.bayes.NaiveBayes,
 * weka.classifiers.bayes.NaiveBayesMultinomial,
 * weka.classifiers.bayes.NaiveBayesMultinomialUpdateable,
 * weka.classifiers.bayes.NaiveBayesSimple,
 * weka.classifiers.bayes.NaiveBayesUpdateable
 * 
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 * @version $Revision$
 * 
 */
public class Run {

  /**
   * Main method for this class. -help or -h prints usage info.
   * 
   * @param args
   */
  public static void main(String[] args) {
      throw new RuntimeException("Running from command line is not available in weka-android.");
  }
}
