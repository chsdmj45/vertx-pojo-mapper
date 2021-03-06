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
package de.braintags.io.vertx.pojomapper.mysql.dataaccess;

import de.braintags.io.vertx.pojomapper.dataaccess.query.QueryOperator;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryOperatorTranslator;

/**
 * Translates operator definitions into propriate expressions for the datastore
 * 
 * @author Michael Remme
 * 
 */

public class SqlQueryOperatorTranslator implements IQueryOperatorTranslator {

  /**
   * Translate the given {@link QueryOperator} into an expression fitting for sql
   * 
   * @param op
   *          the {@link QueryOperator} to be translated
   * @return a suitable String expression
   */
  @Override
  public String translate(QueryOperator op) {
    switch (op) {
    case EQUALS:
      return "=";
    case NOT_EQUALS:
      return "!=";
    case LARGER:
      return ">";
    case LARGER_EQUAL:
      return ">=";
    case SMALLER:
      return "<";
    case SMALLER_EQUAL:
      return "<=";
    case IN:
      return "IN";
    case NOT_IN:
      return "NOT IN";
    case CONTAINS:
    case STARTS:
    case ENDS:
      return "LIKE";
    case NEAR:
      return "<=";

    default:
      throw new UnsupportedOperationException("No translator for " + op);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryOperatorTranslator#translateValue(de.braintags.io.
   * vertx.pojomapper.dataaccess.query.QueryOperator, java.lang.Object)
   */
  @Override
  public Object translateValue(QueryOperator operator, Object value) {
    switch (operator) {
    case CONTAINS:
      return "%" + value + "%";

    case STARTS:
      return value + "%";

    case ENDS:
      return "%" + value;

    default:
      return value;
    }
  }
}
