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
package de.braintags.io.vertx.pojomapper.datastoretest.typehandler;

import de.braintags.io.vertx.pojomapper.datastoretest.mapper.SimpleMapper;
import de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler.BaseRecord;
import de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler.EmbeddedMapper_Single;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class EmbeddedSingleTest extends EmbeddedSingleTest_Null {

  /**
   * 
   */
  public EmbeddedSingleTest() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.datastoretest.typehandler.AbstractTypeHandlerTest#createInstance()
   */
  @Override
  public BaseRecord createInstance() {
    EmbeddedMapper_Single mapper = new EmbeddedMapper_Single();
    mapper.simpleMapper = new SimpleMapper("testname", "secnd prop");
    return mapper;
  }
}