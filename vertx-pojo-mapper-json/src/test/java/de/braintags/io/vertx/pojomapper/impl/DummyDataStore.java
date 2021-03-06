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
package de.braintags.io.vertx.pojomapper.impl;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.IDataStoreMetaData;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryCountResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryLogicTranslator;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryOperatorTranslator;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.Query;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite;
import de.braintags.io.vertx.pojomapper.json.mapping.JsonPropertyMapperFactory;
import de.braintags.io.vertx.pojomapper.json.typehandler.JsonTypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.mapping.IDataStoreSynchronizer;
import de.braintags.io.vertx.pojomapper.mapping.IKeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.IMapperFactory;
import de.braintags.io.vertx.pojomapper.mapping.ITriggerContextFactory;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableGenerator;
import de.braintags.io.vertx.pojomapper.mapping.impl.MapperFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class DummyDataStore implements IDataStore {
  IMapperFactory mf = new MapperFactory(this, new JsonTypeHandlerFactory(), new JsonPropertyMapperFactory(), null);
  ITableGenerator tg = new DummyTableGenerator();
  String database;
  private JsonObject properties;
  private ITriggerContextFactory triggerContextFactory;

  public DummyDataStore() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#createQuery(java.lang.Class)
   */
  @Override
  public <T> IQuery<T> createQuery(Class<T> mapper) {
    return new DummyQuery<T>(mapper, this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#createWrite(java.lang.Class)
   */
  @Override
  public <T> IWrite<T> createWrite(Class<T> mapper) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#createDelete(java.lang.Class)
   */
  @Override
  public <T> IDelete<T> createDelete(Class<T> mapper) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getMapperFactory()
   */
  @Override
  public IMapperFactory getMapperFactory() {
    return mf;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void setUnsupported(Handler handler) {
    handler.handle(Future.failedFuture(new UnsupportedOperationException()));
  }

  class DummyQuery<T> extends Query<T> {

    /**
     * @param mapperClass
     * @param datastore
     */
    public DummyQuery(Class<T> mapperClass, IDataStore datastore) {
      super(mapperClass, datastore);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#execute(io.vertx.core.Handler)
     */
    @Override
    public void internalExecute(Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
      setUnsupported(resultHandler);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#executeCount(io.vertx.core.Handler)
     */
    @Override
    public void internalExecuteCount(Handler<AsyncResult<IQueryCountResult>> resultHandler) {
      setUnsupported(resultHandler);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryContainer#parent()
     */
    @Override
    public Object parent() {
      return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#executeExplain(io.vertx.core.Handler)
     */
    @Override
    public void executeExplain(Handler<AsyncResult<IQueryResult<T>>> resultHandler) {
      setUnsupported(resultHandler);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery#addNativeCommand(java.lang.Object)
     */
    @Override
    public void setNativeCommand(Object command) {
      throw new UnsupportedOperationException();
    }

  }

  @Override
  public IDataStoreSynchronizer getDataStoreSynchronizer() {
    return null;
  }

  @Override
  public ITableGenerator getTableGenerator() {
    return tg;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getDatabase()
   */
  @Override
  public String getDatabase() {
    return database;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getMetaData()
   */
  @Override
  public IDataStoreMetaData getMetaData() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getProperties()
   */
  @Override
  public JsonObject getProperties() {
    return properties;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getKeyGenerator(java.lang.String)
   */
  @Override
  public IKeyGenerator getKeyGenerator(String generatorName) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getDefaultKeyGenerator()
   */
  @Override
  public IKeyGenerator getDefaultKeyGenerator() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getVertx()
   */
  @Override
  public Vertx getVertx() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#shutdown(io.vertx.core.Handler)
   */
  @Override
  public void shutdown(Handler<AsyncResult<Void>> resultHandler) {
  }

  @Override
  public final ITriggerContextFactory getTriggerContextFactory() {
    return triggerContextFactory;
  }

  /**
   * @param triggerContextFactory
   *          the triggerContextFactory to set
   */
  @Override
  public final void setTriggerContextFactory(ITriggerContextFactory triggerContextFactory) {
    this.triggerContextFactory = triggerContextFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getClient()
   */
  @Override
  public Object getClient() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getQueryLogicTranslator()
   */
  @Override
  public IQueryLogicTranslator getQueryLogicTranslator() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.IDataStore#getQueryOperatorTranslator()
   */
  @Override
  public IQueryOperatorTranslator getQueryOperatorTranslator() {
    return null;
  }

}
