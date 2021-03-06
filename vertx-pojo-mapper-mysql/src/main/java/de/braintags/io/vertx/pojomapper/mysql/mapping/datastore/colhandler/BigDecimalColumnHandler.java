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
package de.braintags.io.vertx.pojomapper.mysql.mapping.datastore.colhandler;

import java.math.BigDecimal;

/**
 * 
 * @author Michael Remme
 * 
 */

public class BigDecimalColumnHandler extends DoubleColumnHandler {

  /**
   * handles {@link BigDecimal}
   */
  public BigDecimalColumnHandler() {
    super(BigDecimal.class);
  }

  /**
   * @param clz
   */
  public BigDecimalColumnHandler(Class<? extends BigDecimal> clz) {
    super(clz);
  }

}
