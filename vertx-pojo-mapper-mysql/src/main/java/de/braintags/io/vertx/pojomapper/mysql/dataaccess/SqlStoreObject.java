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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IKeyGenerator;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.IStoreObject;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo;
import de.braintags.io.vertx.pojomapper.mapping.impl.AbstractStoreObject;
import de.braintags.io.vertx.pojomapper.mysql.typehandler.SqlFunction;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * An implementation of {@link IStoreObject} for use with sql databases
 * 
 * @author Michael Remme
 * 
 */

public class SqlStoreObject extends AbstractStoreObject<Object> {
  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(SqlStoreObject.class);

  /**
   * Creates a new instance of SqlStoreObject with a {@link Map} as internal format
   * 
   * @param mapper
   * @param entity
   * @param container
   */
  public SqlStoreObject(IMapper mapper, Object entity) {
    super(mapper, entity, new HashMap<>());
  }

  /**
   * Creates a new instance of SqlStoreObject with the given container as internal format.
   * 
   * @param container
   * @param mapper
   */
  public SqlStoreObject(JsonObject container, IMapper mapper) {
    super(container, mapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IStoreObject#get(de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public Object get(IField field) {
    String colName = field.getColumnInfo().getName();
    return container instanceof JsonObject ? ((JsonObject) container).getValue(colName)
        : ((Map) container).get(colName);
  }

  /**
   * If the internal format is a Map, it is converted as JsonObject and returned, otherwise the internal already
   * existing JsoObject is returned
   * 
   * @return
   */
  public JsonObject getContainerAsJson() {
    return container instanceof JsonObject ? (JsonObject) container : convert();
  }

  private JsonObject convert() {
    JsonObject jo = new JsonObject();
    ((Map<String, Object>) container).entrySet().forEach(entry -> jo.put(entry.getKey(), entry.getValue()));
    return jo;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.IStoreObject#hasProperty(de.braintags.io.vertx.pojomapper.mapping.IField)
   */
  @Override
  public boolean hasProperty(IField field) {
    String colName = field.getColumnInfo().getName();
    return container instanceof JsonObject ? ((JsonObject) container).containsKey(colName)
        : ((Map) container).containsKey(colName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IStoreObject#put(de.braintags.io.vertx.pojomapper.mapping.IField,
   * java.lang.Object)
   */
  @Override
  public IStoreObject<Object> put(IField field, Object value) {
    IColumnInfo ci = field.getMapper().getTableInfo().getColumnInfo(field);
    if (ci == null) {
      throw new MappingException("Can't find columninfo for field " + field.getFullName());
    }
    if (field.isIdField() && value != null) {
      setNewInstance(false);
    }
    if (container instanceof JsonObject) {
      ((JsonObject) container).put(ci.getName(), value);
    } else {
      ((Map) container).put(ci.getName(), value);
    }
    return this;
  }

  /**
   * Generates the sql statement to insert a record into the database and a list of fitting parameters
   * 
   * @return the sql statement to be executed
   */
  public void generateSqlInsertStatement(Handler<AsyncResult<SqlSequence>> resultHandler) {
    try {
      ITableInfo tInfo = getMapper().getTableInfo();
      SqlSequence sequence = new SqlSequence(tInfo.getName());
      Set<String> fieldNames = getMapper().getFieldNames();
      for (String fieldName : fieldNames) {
        IField field = getMapper().getField(fieldName);
        if (field != getMapper().getIdField()) {
          sequence.addEntry(field.getColumnInfo().getName(), get(field));
        }
      }
      getNextId(sequence, resultHandler);
    } catch (Exception e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }

  private void getNextId(SqlSequence sequence, Handler<AsyncResult<SqlSequence>> resultHandler) {
    IKeyGenerator gen = this.getMapper().getKeyGenerator();
    if (gen == null) {
      throw new UnsupportedOperationException(String.format(
          "No keygenerator defined for mapper %s. Did you set the property IKeyGenerator.DEFAULT_KEY_GERNERATOR for your datastore? ",
          getMapper().getMapperClass().getName()));
    }
    gen.generateKey(getMapper(), keyResult -> {
      if (keyResult.failed()) {
        resultHandler.handle(Future.failedFuture(keyResult.cause()));
      } else {
        Object genKey = keyResult.result().getKey();
        IField idField = getMapper().getIdField();
        idField.getTypeHandler().intoStore(genKey, idField, thResult -> {
          if (thResult.failed()) {
            resultHandler.handle(Future.failedFuture(thResult.cause()));
          } else {
            Object idValue = thResult.result().getResult();
            sequence.addEntry(idField.getColumnInfo().getName(), idValue);
            put(idField, idValue);
            resultHandler.handle(Future.succeededFuture(sequence));
          }
        });
      }
    });
  }

  /**
   * Generates the sql statement to update a record into the database and a list of fitting parameters
   * 
   * @return the sql statement to be executed
   */
  public SqlSequence generateSqlUpdateStatement() {
    ITableInfo tInfo = getMapper().getTableInfo();
    IField idField = getMapper().getIdField();
    Object id = get(idField);

    SqlSequence sequence = new SqlSequence(tInfo.getName(), idField.getColumnInfo(), id);

    Set<String> fieldNames = getMapper().getFieldNames();
    for (String fieldName : fieldNames) {
      IField field = getMapper().getField(fieldName);
      if (field != idField) {
        sequence.addEntry(tInfo.getColumnInfo(field).getName(), get(field));
      }
    }
    return sequence;
  }

  class SqlSequence {
    boolean added = false;
    private StringBuilder headStatement;
    private StringBuilder setStatement;
    private StringBuilder whereStatement;
    private Object id;
    private JsonArray parameters = new JsonArray();

    /**
     * Constructor for an insert command
     * 
     * @param tableName
     */
    public SqlSequence(String tableName) {
      headStatement = new StringBuilder("Insert into ").append(tableName);
      setStatement = new StringBuilder(" set ");
    }

    /**
     * Constructor for an update command
     * 
     * @param tableName
     *          the name of the table
     * @param idColInfo
     *          the {@link IColumnInfo} for the id column
     * @param idValue
     *          the id value
     */
    public SqlSequence(String tableName, IColumnInfo idColInfo, Object idValue) {
      headStatement = new StringBuilder("UPDATE ").append(tableName);
      setStatement = new StringBuilder(" set ");
      whereStatement = new StringBuilder(" WHERE ").append(idColInfo.getName()).append(" = ?");
      this.id = idValue;

    }

    void addEntry(String colName, Object value) {
      if (value == null)
        return;
      if (added)
        setStatement.append(", ");
      if (value instanceof SqlFunction) {
        setStatement.append(colName).append(" = ").append(((SqlFunction) value).getFunctionName()).append(" ( ? )");
        parameters.add(((SqlFunction) value).getContent());
      } else {
        setStatement.append(colName).append(" = ?");
        parameters.add(value);
      }
      added = true;
    }

    /**
     * Get the statement
     * 
     * @return the sqlStatement
     */
    public final String getSqlStatement() {
      StringBuilder ret = new StringBuilder(headStatement);
      if (parameters.isEmpty())
        ret.append(" () VALUES ()");// insert into SimpleMapper () VALUES ()
      else
        ret.append(setStatement);
      if (whereStatement != null)
        ret.append(whereStatement);
      return ret.toString();
    }

    /**
     * @return the parameters
     */
    public final JsonArray getParameters() {
      if (id != null)
        return parameters.copy().add(id);
      return parameters;
    }

    @Override
    public String toString() {
      return getSqlStatement() + " | " + getParameters();
    }
  }
}
