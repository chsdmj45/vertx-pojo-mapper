/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.impl;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultTableInfo;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class DummyTableInfo extends DefaultTableInfo {

  /**
   * @param mapper
   */
  public DummyTableInfo(IMapper mapper) {
    super(mapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultTableInfo#generateColumnInfo(de.braintags.io.vertx.
   * pojomapper.mapping.IField, de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler)
   */
  @Override
  protected IColumnInfo generateColumnInfo(IField field, IColumnHandler columnHandler) {
    return new DummyColumnInfo(field, columnHandler);
  }

}
