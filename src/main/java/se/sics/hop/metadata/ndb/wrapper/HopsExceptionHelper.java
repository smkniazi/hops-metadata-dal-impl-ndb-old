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

import com.mysql.clusterj.ClusterJDatastoreException;
import com.mysql.clusterj.ClusterJException;
import se.sics.hop.exception.StorageException;
import se.sics.hop.exception.TransientStorageException;

public class HopsExceptionHelper {
  public static StorageException wrap(ClusterJException e) {
    if (isTransient(e)) {
      return new TransientStorageException(e);
    } else {
      return new StorageException(e);
    }
  }

  private static boolean isTransient(ClusterJException e) {
    if (e instanceof ClusterJDatastoreException) {
      // TODO identify transient exceptions
      // http://dev.mysql.com/doc/ndbapi/en/ndb-error-codes.html
      if (e.getMessage().contains("code 266")) {
        return true;
      }
    }
    return false;
  }
}
