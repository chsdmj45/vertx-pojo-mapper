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

/**
 * Handles Long and creates BIGINT from it. Properties for scale and precision are not used
 * 
 * @author Michael Remme
 * 
 */

public class LongColumnHandler extends NumericColumnHandler {

  /**
   * Constructor for a LongColumnHandler
   */
  public LongColumnHandler() {
    super("BIGINT", false, false, Long.class, long.class);
  }

}
