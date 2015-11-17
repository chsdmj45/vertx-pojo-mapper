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

package de.braintags.io.vertx.pojomapper.testdatastore;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.SimpleMapper;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TestSimpleMapper extends DatastoreBaseTest {
  private static Logger logger = LoggerFactory.getLogger(TestSimpleMapper.class);

  @Test
  public void testSimpleMapper(TestContext context) {
    dropTable(context, "SimpleMapper");
    SimpleMapper sm = new SimpleMapper();
    sm.name = "testName";
    sm.setSecondProperty("my second property");
    ResultContainer resultContainer = saveRecord(context, sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
    IWriteEntry we = resultContainer.writeResult.iterator().next();
    context.assertEquals(we.getAction(), WriteAction.INSERT);
    context.assertNotNull(sm.id);
    context.assertTrue(sm.id.hashCode() != 0); // "ID wasn't set by insert statement",

    sm.name = "testNameModified";
    sm.setSecondProperty("my modified property");
    resultContainer = saveRecord(context, sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
    we = resultContainer.writeResult.iterator().next();
    context.assertEquals(we.getAction(), WriteAction.UPDATE);

    // SimpleQuery for all records
    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    resultContainer = find(context, query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    resultContainer.queryResult.iterator().next(result -> {
      if (result.failed()) {
        logger.error("", result.cause());
        context.fail(result.cause().toString());
      } else {
        context.assertTrue(sm.equals(result.result()));

        // search inside name field
        query.field("name").is("testNameModified");
        ResultContainer resultContainer2 = find(context, query, 1);
        if (resultContainer2.assertionError != null)
          throw resultContainer2.assertionError;

        resultContainer2.queryResult.iterator().next(res2 -> {
          if (res2.failed()) {
            logger.error("", result.cause());
            context.fail(result.cause().toString());
          } else {
            SimpleMapper rsm = (SimpleMapper) result.result();
            context.assertTrue(sm.equals(rsm));
            context.assertEquals("succeeded", rsm.afterSave);
            context.assertEquals("succeeded", rsm.beforeSave);
            context.assertEquals("succeeded", rsm.afterLoad);
          }

        });

      }
    });

  }

  @Test
  public void testSimpleOr(TestContext context) {
    dropTable(context, "SimpleMapper");
    createDemoRecords(context);

    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    query.field("name").is("Dublette");
    ResultContainer resultContainer = find(context, query, 2);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    query = getDataStore().createQuery(SimpleMapper.class);
    query.or("secondProperty").is("erste").field("secondProperty").is("zweite");
    resultContainer = find(context, query, 2);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  @Test
  public void testSimpleAnd(TestContext context) {
    dropTable(context, "SimpleMapper");
    createDemoRecords(context);

    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    query.and("name").is("Dublette").field("secondProperty").is("erste");
    ResultContainer resultContainer = find(context, query, 1);
    logger.info(resultContainer.queryResult.getOriginalQuery().toString());
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  @Test
  public void testSimpleAndCount(TestContext context) {
    dropTable(context, "SimpleMapper");
    createDemoRecords(context);

    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    query.and("name").is("Dublette").field("secondProperty").is("erste");
    ResultContainer resultContainer = findCount(context, query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  @Test
  public void testQueryMultipleFields(TestContext context) {
    dropTable(context, "SimpleMapper");
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    query.field("name").is("Dublette");
    ResultContainer resultContainer = find(context, query, 2);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    query.field("secondProperty").is("erste");
    resultContainer = find(context, query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /**
   * Search: Name = "AndOr" AND secondProperty="AndOr 1" OR secondProperty="AndOr 2"
   * 
   */
  @Test
  public void testAndOr(TestContext context) {
    dropTable(context, "SimpleMapper");
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    query.and("name").is("AndOr").field("secondProperty").is("AndOr 1").or("secondProperty").is("AndOr 2");

    ResultContainer resultContainer = find(context, query, -1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /**
   * Search:
   */
  @Test
  public void testIn(TestContext context) {
    dropTable(context, "SimpleMapper");
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    List<String> it = Arrays.asList("Dublette", "AndOr");
    query.field("name").in(it);

    ResultContainer resultContainer = find(context, query, 5);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /**
   * Search:
   */
  @Test
  public void testNotIn(TestContext context) {
    dropTable(context, "SimpleMapper");
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    List<String> it = Arrays.asList("erste", "zweite");
    query.field("secondProperty").notIn(it);

    ResultContainer resultContainer = find(context, query, 3);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /**
   * Search:
   */
  @Test
  public void testIsNot(TestContext context) {
    dropTable(context, "SimpleMapper");
    createDemoRecords(context);
    IQuery<SimpleMapper> query = getDataStore().createQuery(SimpleMapper.class);
    query.field("name").isNot("Dublette");

    ResultContainer resultContainer = find(context, query, 3);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

  /*
   * **************************************************** Helper Part
   */

  private void createDemoRecords(TestContext context) {
    SimpleMapper sm = new SimpleMapper();
    sm.name = "Dublette";
    sm.setSecondProperty("erste");
    sm.intValue = 10;
    ResultContainer resultContainer = saveRecord(context, sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    sm = new SimpleMapper();
    sm.name = "Dublette";
    sm.setSecondProperty("zweite");
    sm.intValue = 11;
    resultContainer = saveRecord(context, sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    for (int i = 0; i < 3; i++) {
      sm = new SimpleMapper();
      sm.name = "AndOr";
      sm.setSecondProperty("AndOr " + i);
      sm.intValue = i + 1;
      resultContainer = saveRecord(context, sm);
      if (resultContainer.assertionError != null)
        throw resultContainer.assertionError;
    }

  }

}