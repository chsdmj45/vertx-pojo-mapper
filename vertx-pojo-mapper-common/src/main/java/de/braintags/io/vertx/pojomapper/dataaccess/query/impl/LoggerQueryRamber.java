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
package de.braintags.io.vertx.pojomapper.dataaccess.query.impl;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IFieldParameter;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ILogicContainer;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * An implementation of IQueryRambler which is logging the elements
 * 
 * @author Michael Remme
 * 
 */

public class LoggerQueryRamber implements IQueryRambler {
  private static Logger logger = LoggerFactory.getLogger(LoggerQueryRamber.class);
  private String levelPrefix = "";
  private int level;

  /**
   * 
   */
  public LoggerQueryRamber() {
  }

  public void raiseLevel() {
    ++level;
    setHirarchyString();
  }

  public void reduceLevel() {
    --level;
    setHirarchyString();
  }

  public void setHirarchyString() {
    StringBuilder prefixBuffer = new StringBuilder();
    for (int i = 0; i < level; i++) {
      prefixBuffer.append(" ");
    }
    levelPrefix = prefixBuffer.toString();
  }

  @Override
  public void start(IQuery<?> query) {
    log("start query in: " + query.getMapper().getTableInfo().getName());
  }

  @Override
  public void stop(IQuery<?> query) {
    log("stop query ");
  }

  @Override
  public void start(ILogicContainer<?> container) {
    raiseLevel();
    log(container.getLogic().toString());
  }

  @Override
  public void stop(ILogicContainer<?> container) {
    reduceLevel();
  }

  @Override
  public void start(IFieldParameter<?> fieldParameter, Handler<AsyncResult<Void>> resultHandler) {
    raiseLevel();
    log(fieldParameter.getField().getName() + " " + fieldParameter.getOperator().toString() + " "
        + fieldParameter.getValue());
    resultHandler.handle(Future.succeededFuture());
  }

  @Override
  public void stop(IFieldParameter<?> fieldParameter) {
    reduceLevel();
  }

  private final void log(String message) {
    logger.info(levelPrefix + message);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler#start(de.braintags.io.vertx.pojomapper.dataaccess.
   * query.ISortDefinition)
   */
  @Override
  public void start(ISortDefinition<?> sortDefinition) {
    raiseLevel();
    log(sortDefinition.toString());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryRambler#stop(de.braintags.io.vertx.pojomapper.dataaccess.
   * query.ISortDefinition)
   */
  @Override
  public void stop(ISortDefinition<?> sortDefinition) {
    reduceLevel();
  }

}
