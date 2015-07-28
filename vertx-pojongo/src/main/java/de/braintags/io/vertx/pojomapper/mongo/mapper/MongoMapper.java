/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mongo.mapper;

import java.lang.reflect.Field;

import de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor;
import de.braintags.io.vertx.pojomapper.mapping.impl.MappedField;
import de.braintags.io.vertx.pojomapper.mapping.impl.Mapper;
import de.braintags.io.vertx.pojomapper.mapping.impl.MapperFactory;

/**
 * 
 *
 * @author Michael Remme
 * 
 */

public class MongoMapper extends Mapper {

  /**
   * @param mapperClass
   * @param mapperFactory
   */
  public MongoMapper(Class<?> mapperClass, MapperFactory mapperFactory) {
    super(mapperClass, mapperFactory);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.impl.Mapper#createMappedField(java.lang.reflect.Field,
   * de.braintags.io.vertx.pojomapper.mapping.IPropertyAccessor)
   */
  @Override
  protected MappedField createMappedField(Field field, IPropertyAccessor accessor) {
    return new MongoMappedField(field, accessor, this);
  }

}