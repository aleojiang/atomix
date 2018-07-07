/*
 * Copyright 2018-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.core.set.impl;

import io.atomix.core.cache.CacheConfig;
import io.atomix.core.set.AsyncDistributedTreeSet;
import io.atomix.core.set.DistributedTreeSet;

import java.time.Duration;

/**
 * Caching tree set.
 */
public class CachingAsyncDistributedTreeSet<E extends Comparable<E>>
    extends CachingAsyncDistributedNavigableSet<E>
    implements AsyncDistributedTreeSet<E> {
  public CachingAsyncDistributedTreeSet(AsyncDistributedTreeSet<E> set, CacheConfig cacheConfig) {
    super(set, cacheConfig);
  }

  @Override
  public DistributedTreeSet<E> sync(Duration timeout) {
    return new BlockingDistributedTreeSet<>(this, timeout.toMillis());
  }
}
