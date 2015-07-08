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

package de.braintags.io.vertx.pojomapper.dataaccess.query;

import java.util.Iterator;

/**
 * The result of an executed {@link IQuery}. Acts as an {@link Iterator}, so that implementations can decide to perform
 * a lazy load of found results
 * 
 * @author Michael Remme
 * 
 */

public interface IQueryResult<T> extends Iterator<T> {

}
