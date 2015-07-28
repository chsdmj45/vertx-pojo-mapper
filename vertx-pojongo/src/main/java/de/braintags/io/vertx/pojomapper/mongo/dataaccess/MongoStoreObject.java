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

package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import de.braintags.io.vertx.pojomapper.json.dataaccess.JsonStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;

/**
 * 
 * @author Michael Remme
 */

public class MongoStoreObject extends JsonStoreObject {
  private Object entity = null;

  /**
   * Creates a new instance, where the internal container is filled from the contents of the given entity
   */
  public MongoStoreObject(IMapper mapper, Object entity) {
    super(mapper);
    this.entity = entity;
  }

  /**
   * Creates a new instance, where the internal container is filled from the contents of the given entity
   */
  public MongoStoreObject(JsonObject json, IMapper mapper) {
    super(json, mapper);
  }

  public Object getEntity() {
    if (entity == null) {
      throw new NullPointerException(
          "Internal Entity is not initialized; call method MongoStoreObject.initToEntity first ");
    }
    return entity;
  }

  /**
   * Initialize the internal entity
   * 
   * @param handler
   */
  public void initToEntity(Handler<AsyncResult<Void>> handler) {
    Object o = getMapper().getObjectFactory().createInstance(getMapper().getMapperClass());
    ErrorObject<Void> error = new ErrorObject<Void>();
    CounterObject co = new CounterObject(getMapper().getFieldNames().size());
    for (String fieldName : getMapper().getFieldNames()) {
      IField field = getMapper().getField(fieldName);
      field.getPropertyMapper().fromStoreObject(o, this, field, result -> {
        if (result.failed()) {
          error.setThrowable(result.cause());
          handler.handle(result);
        } else {
          if (co.reduce()) {
            entity = o;
            handler.handle(Future.succeededFuture());
          }
        }
      });
      if (error.isError()) {
        return;
      }
    }
  }

  /**
   * Initialize the internal entity into the StoreObject
   * 
   * @param handler
   */
  public void initFromEntity(Handler<AsyncResult<Void>> handler) {
    ErrorObject<Void> error = new ErrorObject<Void>();
    IMapper mapper = getMapper();
    CounterObject co = new CounterObject(mapper.getFieldNames().size());
    for (String fieldName : mapper.getFieldNames()) {
      IField field = mapper.getField(fieldName);
      field.getPropertyMapper().intoStoreObject(entity, this, field, result -> {
        if (result.failed()) {
          error.setThrowable(result.cause());
          handler.handle(result);
        } else {
          if (co.reduce())
            handler.handle(Future.succeededFuture());
        }
      });
      if (error.isError()) {
        return;
      }
    }
  }

}