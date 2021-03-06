/*
 * Copyright 2017-present Open Networking Foundation
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
package io.atomix.core.multimap;

import com.google.common.collect.Maps;
import io.atomix.core.multimap.impl.DefaultAtomicMultimapBuilder;
import io.atomix.core.multimap.impl.AtomicMultimapService;
import io.atomix.core.multimap.impl.DefaultAtomicMultimapService;
import io.atomix.primitive.PrimitiveManagementService;
import io.atomix.primitive.PrimitiveType;
import io.atomix.primitive.service.PrimitiveService;
import io.atomix.primitive.service.ServiceConfig;
import io.atomix.utils.serializer.Namespace;
import io.atomix.utils.serializer.Namespaces;
import io.atomix.utils.time.Versioned;

import java.util.ArrayList;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Consistent multimap primitive type.
 */
public class AtomicMultimapType<K, V> implements PrimitiveType<AtomicMultimapBuilder<K, V>, AtomicMultimapConfig, AtomicMultimap<K, V>> {
  private static final String NAME = "atomic-multimap";
  private static final AtomicMultimapType INSTANCE = new AtomicMultimapType();

  /**
   * Returns a new consistent multimap type.
   *
   * @param <K> the key type
   * @param <V> the value type
   * @return a new consistent multimap type
   */
  @SuppressWarnings("unchecked")
  public static <K, V> AtomicMultimapType<K, V> instance() {
    return INSTANCE;
  }

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public Namespace namespace() {
    return Namespace.builder()
        .register(PrimitiveType.super.namespace())
        .nextId(Namespaces.BEGIN_USER_CUSTOM_ID)
        .register(Versioned.class)
        .register(ArrayList.class)
        .register(Maps.immutableEntry("", "").getClass())
        .register(AtomicMultimapService.Batch.class)
        .build();
  }

  @Override
  public PrimitiveService newService(ServiceConfig config) {
    return new DefaultAtomicMultimapService();
  }

  @Override
  public AtomicMultimapConfig newConfig() {
    return new AtomicMultimapConfig();
  }

  @Override
  public AtomicMultimapBuilder<K, V> newBuilder(String name, AtomicMultimapConfig config, PrimitiveManagementService managementService) {
    return new DefaultAtomicMultimapBuilder<>(name, config, managementService);
  }

  @Override
  public String toString() {
    return toStringHelper(this)
        .add("name", name())
        .toString();
  }
}