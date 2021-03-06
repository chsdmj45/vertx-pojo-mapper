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
package de.braintags.io.vertx.pojomapper.testdatastore;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry;
import de.braintags.io.vertx.pojomapper.dataaccess.write.WriteAction;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.impl.keygen.DebugGenerator;
import de.braintags.io.vertx.pojomapper.mapping.impl.keygen.DefaultKeyGenerator;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.KeyGeneratorMapper;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.KeyGeneratorMapperDebugGenerator;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author mremme
 * 
 */
public class TestKeyGenerator extends DatastoreBaseTest {

  @Test
  public void testKeyGenerator(TestContext context) {
    clearTable(context, "KeyGeneratorMapper");

    IMapper mapper = getDataStore(context).getMapperFactory().getMapper(KeyGeneratorMapper.class);
    context.assertTrue(mapper.getKeyGenerator() instanceof DefaultKeyGenerator,
        "not an instance of DefaultKeyGenerator: " + String.valueOf(mapper.getKeyGenerator()));

    KeyGeneratorMapper sm = new KeyGeneratorMapper();
    sm.name = "testName";
    ResultContainer resultContainer = saveRecord(context, sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
    IWriteEntry we1 = resultContainer.writeResult.iterator().next();
    context.assertEquals(we1.getAction(), WriteAction.INSERT);
    context.assertNotNull(sm.id);
    context.assertTrue(sm.id.hashCode() != 0); // "ID wasn't set by insert statement",
    try {
      Integer.parseInt(sm.id);
    } catch (NumberFormatException e) {
      context.fail("Not a numeric ID: " + sm.id);
    }

    sm.name = "testNameModified";
    resultContainer = saveRecord(context, sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
    IWriteEntry we = resultContainer.writeResult.iterator().next();
    context.assertEquals(we.getAction(), WriteAction.UPDATE);
    context.assertEquals(String.valueOf(we1.getId()), String.valueOf(we.getId()),
        "id must not change after update: " + we1.getId() + " | " + we.getId());
  }

  @Test
  public void testKeyExists(TestContext context) {
    clearTable(context, "KeyGeneratorMapperDebugGenerator");
    DebugGenerator gen = (DebugGenerator) getDataStore(context).getKeyGenerator(DebugGenerator.NAME);

    KeyGeneratorMapperDebugGenerator km = new KeyGeneratorMapperDebugGenerator();
    km.name = "testName";
    ResultContainer resultContainer = saveRecord(context, km);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
    checkWriteAction(context, resultContainer, WriteAction.INSERT);
    int id = Integer.parseInt(km.id);
    context.assertEquals(id, 1, "expected first id as 1");

    gen.resetCounter();
    km = new KeyGeneratorMapperDebugGenerator();
    km.name = "testId Exists";
    resultContainer = saveRecord(context, km);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
    checkWriteAction(context, resultContainer, WriteAction.INSERT);
    id = Integer.parseInt(km.id);
    context.assertEquals(id, 2, "expected first id as 2 cause of existing record");

  }

  private void checkWriteAction(TestContext context, ResultContainer resultContainer, WriteAction we) {
    IWriteEntry we1 = resultContainer.writeResult.iterator().next();
    context.assertEquals(we1.getAction(), we);
  }

}
