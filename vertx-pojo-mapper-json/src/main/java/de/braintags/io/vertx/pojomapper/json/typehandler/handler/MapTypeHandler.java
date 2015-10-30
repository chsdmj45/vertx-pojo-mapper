/*
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.json.typehandler.handler;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.AbstractTypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import de.braintags.io.vertx.util.CounterObject;
import de.braintags.io.vertx.util.ErrorObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * Deals all fields, which contain {@link Map} content, which are NOT annotated as {@link Referenced} or
 * {@link Embedded}
 * 
 * @author Michael Remme
 * 
 */

public class MapTypeHandler extends AbstractTypeHandler {

  /**
   * Constructor with parent {@link ITypeHandlerFactory}
   * 
   * @param typeHandlerFactory
   *          the parent {@link ITypeHandlerFactory}
   */
  public MapTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory, Map.class);
  }

  @Override
  public final short matches(IField field) {
    if (matchAnnotation(field) == MATCH_NONE)
      return MATCH_NONE;
    return super.matches(field);
  }

  /**
   * Checks, wether an annotation like {@link Referenced} or {@link Embedded} is set to the field and returns the
   * propriate match definition. If the method returns MATCH_NONE, then the class won't be checkd, otherwise it will be
   * checked
   * 
   * @param field
   *          the field to be checked
   * @return MATCH_NONE or MATCH_MINOR
   */
  protected short matchAnnotation(IField field) {
    if (field.hasAnnotation(Referenced.class) || field.hasAnnotation(Embedded.class))
      return MATCH_NONE;
    return MATCH_MINOR;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    JsonArray jsonArray = (JsonArray) source;
    if (jsonArray == null || jsonArray.isEmpty())
      resultHandler.handle(Future.succeededFuture());

    ErrorObject<ITypeHandlerResult> errorObject = new ErrorObject<ITypeHandlerResult>(resultHandler);
    CounterObject co = new CounterObject(jsonArray.size());
    final MapEntry[] resultArray = new MapEntry[jsonArray.size()];
    int counter = 0;
    for (Object jo : jsonArray) {
      CurrentCounter cc = new CurrentCounter(counter++, jo);
      handleOneEntryFromStore(field, cc, resultArray, result -> {
        if (result.failed()) {
          errorObject.setThrowable(result.cause());
          return;
        } else {
          checkSuccessFromStore(field, co, resultArray, resultHandler);
        }
      });
      if (errorObject.isError())
        return;
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void checkSuccessFromStore(IField field, CounterObject co, MapEntry[] resultArray,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    if (co.reduce()) {
      Map map = field.getMapper().getObjectFactory().createMap(field);
      for (int i = 0; i < resultArray.length; i++) {
        map.put(resultArray[i].key, resultArray[i].value);
      }
      success(map, resultHandler);
    }
  }

  private void handleOneEntryFromStore(IField field, CurrentCounter cc, MapEntry[] resultArray,
      Handler<AsyncResult<Void>> resultHandler) {
    Object keyIn = ((JsonArray) cc.value).getValue(0);
    ITypeHandler th = field.getMapper().getMapperFactory().getDataStore().getTypeHandlerFactory()
        .getTypeHandler(field.getMapKeyClass());
    th.fromStore(keyIn, field, field.getMapKeyClass(), keyResult -> {
      if (keyResult.failed()) {
        resultHandler.handle(Future.failedFuture(keyResult.cause()));
        return;
      } else {
        Object valueIn = ((JsonArray) cc.value).getValue(1);
        convertValueFromStore(valueIn, field, valueResult -> {
          if (valueResult.failed()) {
            resultHandler.handle(Future.failedFuture(valueResult.cause()));
            return;
          } else {
            Object javaValue = valueResult.result();
            if (javaValue != null) {
              resultArray[cc.i] = new MapEntry(keyResult.result().getResult(), valueResult.result());
            }
            resultHandler.handle(Future.succeededFuture());
          }
        });
      }
    });

  }

  /**
   * Converts the value for one entry like it was coming from the datastore into the needed format for the object to be
   * filled
   * 
   * @param valueIn
   *          the value from the datastore
   * @param field
   *          the field of the {@link Map}
   * @param resultHandler
   *          the {@link Handler} to be informed
   */
  protected void convertValueFromStore(Object valueIn, IField field, Handler<AsyncResult<Object>> resultHandler) {
    if (field.getSubTypeHandler() == null) {
      resultHandler.handle(Future.succeededFuture(valueIn));
      return;
    }
    field.getSubTypeHandler().fromStore(valueIn, field, field.getSubClass(), valueResult -> {
      if (valueResult.failed()) {
        resultHandler.handle(Future.failedFuture(valueResult.cause()));
      } else {
        Object javaValue = valueResult.result().getResult();
        resultHandler.handle(Future.succeededFuture(javaValue));
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler#intoStore(java.lang.Object,
   * de.braintags.io.vertx.pojomapper.mapping.IField, io.vertx.core.Handler)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void intoStore(Object source, IField field, Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    Map<?, ?> map = (Map<?, ?>) source;
    int size = map == null ? 0 : map.size();
    if (size == 0)
      resultHandler.handle(Future.succeededFuture());
    ErrorObject<ITypeHandlerResult> errorObject = new ErrorObject<ITypeHandlerResult>(resultHandler);
    CounterObject co = new CounterObject(size);
    JsonArray[] resultArray = new JsonArray[size];
    Iterator<?> it = map.entrySet().iterator();
    int counter = 0;
    while (it.hasNext() && !errorObject.isError()) {
      // trying to write the array in the order like it is
      Entry entry = (Entry) it.next();
      CurrentCounter cc = new CurrentCounter(counter++, entry);

      valueIntoStore(field, cc, resultArray, result -> {
        if (result.failed()) {
          errorObject.setThrowable(result.cause());
          return;
        } else {
          checkSuccessIntoStore(co, resultArray, resultHandler);
        }
      });
    }
  }

  private void checkSuccessIntoStore(CounterObject co, JsonArray[] resultArray,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    if (co.reduce()) {
      JsonArray arr = new JsonArray();
      for (int k = 0; k < resultArray.length; k++) {
        arr.add(resultArray[k]);
      }
      success(arr, resultHandler);
    }
  }

  /**
   * Transforms the key - value pair into the given Json Array
   * 
   * @param field
   *          the field, which describes the {@link Map}
   * @param cc
   *          the instance of {@link CurrentCounter}
   * @param resultArray
   *          the {@link JsonArray} to be filled
   * @param resultHandler
   *          the {@link Handler} to be informed
   */
  @SuppressWarnings("rawtypes")
  protected void valueIntoStore(IField field, CurrentCounter cc, JsonArray[] resultArray,
      Handler<AsyncResult<Void>> resultHandler) {
    ITypeHandler keyTh = getKeyTypeHandler(((Entry) cc.value).getKey(), field);

    keyTh.intoStore(((Entry) cc.value).getKey(), field, keyResult -> {
      if (keyResult.failed()) {
        resultHandler.handle(Future.failedFuture(keyResult.cause()));
        return;
      } else {
        ITypeHandler valueTh = getValueTypeHandler(((Entry) cc.value).getValue(), field);
        valueTh.intoStore(((Entry) cc.value).getValue(), field, valueResult -> {
          if (valueResult.failed()) {
            resultHandler.handle(Future.failedFuture(valueResult.cause()));
            return;
          } else {
            resultArray[cc.i] = new JsonArray().add(keyResult.result().getResult())
                .add(valueResult.result().getResult());
            resultHandler.handle(Future.succeededFuture());
          }
        });
      }
    });
  }

  /**
   * Get the {@link ITypeHandler} which shall be used for the entry value
   * 
   * @param value
   *          the value to be written
   * @param field
   *          the field to be handled
   * @return the {@link ITypeHandler} to be used
   */
  @SuppressWarnings("rawtypes")
  protected ITypeHandler getValueTypeHandler(Object value, IField field) {
    Class valueClass = field.getSubClass();
    if (valueClass == null || valueClass == Object.class)
      valueClass = value.getClass();
    return getSubTypeHandler(valueClass);
  }

  /**
   * Get the {@link ITypeHandler} which shall be used for the entry key
   * 
   * @param value
   *          the value to be written
   * @param field
   *          the field to be handled
   * @return the {@link ITypeHandler} to be used
   */
  @SuppressWarnings("rawtypes")
  public ITypeHandler getKeyTypeHandler(Object value, IField field) {
    Class keyClass = field.getMapKeyClass();
    if (keyClass == null || keyClass == Object.class)
      keyClass = value.getClass();
    return getSubTypeHandler(keyClass);
  }

  class CurrentCounter {
    int i;
    Object value;

    CurrentCounter(int i, Object value) {
      this.i = i;
      this.value = value;
    }
  }

  class MapEntry {
    Object key;
    Object value;

    MapEntry(Object key, Object value) {
      this.key = key;
      this.value = value;
    }
  }

}
