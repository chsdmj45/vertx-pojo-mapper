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
package de.braintags.io.vertx.pojomapper.mysql.typehandler;

import java.lang.reflect.Constructor;
import java.sql.Time;
import java.util.Date;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Implementation dealing with {@link Time}
 * 
 * @author Michael Remme
 * 
 */

public class SqlDateTypeHandler extends AbstractTypeHandler {
  private static final DateTimeFormatter formater = ISODateTimeFormat.dateHourMinuteSecondMillis();

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public SqlDateTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, Date.class);
  }

  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    if (source == null) {
      success(source, resultHandler);
      return;
    }
    try {
      @SuppressWarnings("rawtypes")
      Constructor constr = getConstructor(field, cls, long.class);
      long millis = formater.parseMillis((String) source);
      Date date = (Date) constr.newInstance(millis);
      success(date, resultHandler);
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }

  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    Object value = source == null ? source : formater.print(((Date) source).getTime());
    success(value, resultHandler);
  }

}
