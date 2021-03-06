/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mapping.impl;

import java.lang.reflect.Field;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor;
import de.braintags.io.vertx.util.exception.PropertyAccessException;

/**
 * An accessor onto a public field of a java object
 * 
 * @author Michael Remme
 * 
 */

public class JavaFieldAccessor implements IPropertyAccessor {
  private String name;
  private Field field;

  /**
   * 
   * @param field
   *          the underlaying {@link IField}
   */
  public JavaFieldAccessor(Field field) {
    this.name = field.getName();
    this.field = field;
  }

  @Override
  public Object readData(Object record) {
    try {
      return field.get(record);
    } catch (Exception e) {
      throw new PropertyAccessException("Cannot read data from property " + name, e);
    }
  }

  @Override
  public void writeData(Object record, Object data) {
    try {
      field.set(record, data);
    } catch (Exception e) {
      throw new PropertyAccessException("Cannot write data from property " + name, e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor#getName()
   */
  @Override
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

}
