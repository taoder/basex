package org.basex.data;

import java.io.IOException;

/**
 * This is an interface for query results.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-10, ISC License
 * @author Christian Gruen
 */
public interface Result {
  /**
   * Number of values, stored in the result instance.
   * @return number of values
   */
  int size();

  /**
   * Compares values for equality.
   * @param v value to be compared
   * @return true if values are equal
   */
  boolean same(Result v);

  /**
   * Serializes the complete result.
   * @param ser serializer
   * @throws IOException I/O exception
   */
  void serialize(Serializer ser) throws IOException;

  /**
   * Serializes the specified result.
   * @param ser serializer
   * @param n results offset to serialize
   * @throws IOException I/O exception
   */
  void serialize(Serializer ser, int n) throws IOException;
}