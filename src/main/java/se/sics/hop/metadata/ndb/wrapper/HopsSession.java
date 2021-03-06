/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.sics.hop.metadata.ndb.wrapper;

import com.mysql.clusterj.ClusterJException;
import com.mysql.clusterj.LockMode;
import com.mysql.clusterj.Query;
import com.mysql.clusterj.Session;
import com.mysql.clusterj.Transaction;
import com.mysql.clusterj.query.QueryBuilder;
import se.sics.hop.exception.StorageException;

public class HopsSession {
  private final Session session;

  public HopsSession(Session session) {
    this.session = session;
  }

  public HopsQueryBuilder getQueryBuilder() throws StorageException {
    try {
      QueryBuilder queryBuilder = session.getQueryBuilder();
      return new HopsQueryBuilder(queryBuilder);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public <T> HopsQuery<T> createQuery(HopsQueryDomainType<T> queryDefinition)
      throws StorageException {
    try {
      Query<T> query = session.createQuery(queryDefinition.getQueryDomainType());
      return new HopsQuery<T>(query);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public <T> T find(Class<T> aClass, Object o) throws StorageException {
    try {
      return session.find(aClass, o);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public <T> T newInstance(Class<T> aClass) throws StorageException {
    try {
      return session.newInstance(aClass);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public <T> T newInstance(Class<T> aClass, Object o) throws StorageException {
    try {
      return session.newInstance(aClass, o);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public <T> T makePersistent(T t) throws StorageException {
    try {
      return session.makePersistent(t);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public <T> T load(T t) throws StorageException {
    try {
      return session.load(t);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public Boolean found(Object o) throws StorageException {
    try {
      return session.found(o);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public void persist(Object o) throws StorageException {
    try {
      session.persist(o);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public Iterable<?> makePersistentAll(Iterable<?> iterable)
      throws StorageException {
    try {
      return session.makePersistentAll(iterable);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public <T> void deletePersistent(Class<T> aClass, Object o)
      throws StorageException {
    try {
      session.deletePersistent(aClass, o);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public void deletePersistent(Object o) throws StorageException {
    try {
      session.deletePersistent(o);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public void remove(Object o) throws StorageException {
    try {
      session.remove(o);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public <T> int deletePersistentAll(Class<T> aClass) throws StorageException {
    try {
      return session.deletePersistentAll(aClass);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public void deletePersistentAll(Iterable<?> iterable)
      throws StorageException {
    try {
      session.deletePersistentAll(iterable);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public void updatePersistent(Object o) throws StorageException {
    try {
      session.updatePersistent(o);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public void updatePersistentAll(Iterable<?> iterable)
      throws StorageException {
    try {
      session.updatePersistentAll(iterable);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public <T> T savePersistent(T t) throws StorageException {
    try {
      return session.savePersistent(t);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public Iterable<?> savePersistentAll(Iterable<?> iterable)
      throws StorageException {
    try {
      return session.savePersistentAll(iterable);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public HopsTransaction currentTransaction() throws StorageException {
    try {
      Transaction transaction = session.currentTransaction();
      return new HopsTransaction(transaction);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public void close() throws StorageException {
    try {
      session.close();
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public boolean isClosed() throws StorageException {
    try {
      return session.isClosed();
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public void flush() throws StorageException {
    try {
      session.flush();
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public void setPartitionKey(Class<?> aClass, Object o)
      throws StorageException {
    try {
      session.setPartitionKey(aClass, o);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public void setLockMode(LockMode lockMode) throws StorageException {
    try {
      session.setLockMode(lockMode);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public void markModified(Object o, String s) throws StorageException {
    try {
      session.markModified(o, s);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }

  public String unloadSchema(Class<?> aClass) throws StorageException {
    try {
      return session.unloadSchema(aClass);
    } catch (ClusterJException e) {
      throw HopsExceptionHelper.wrap(e);
    }
  }
}
