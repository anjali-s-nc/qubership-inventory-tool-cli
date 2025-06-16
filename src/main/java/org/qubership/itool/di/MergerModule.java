/*
 * Copyright 2024-2025 NetCracker Technology Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.qubership.itool.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.vertx.core.Vertx;
import org.qubership.itool.modules.processor.DefaultGraphMergerFactory;
import org.qubership.itool.modules.processor.GraphMergerFactory;
import org.qubership.itool.modules.processor.MergerApi;
import org.qubership.itool.modules.graph.GraphFactory;
import org.qubership.itool.modules.graph.GraphReportFactory;

/**
 * Module for merger-related bindings.
 */
public class MergerModule extends AbstractModule {

    /**
     * Provides the GraphMergerFactory instance.
     */
    @Provides
    @Singleton
    public GraphMergerFactory provideGraphMergerFactory(Vertx vertx, GraphFactory graphFactory, GraphReportFactory graphReportFactory) {
        return new DefaultGraphMergerFactory(vertx, graphFactory, graphReportFactory);
    }

    /**
     * Provides the MergerApi implementation using GraphMergerFactory.
     */
    @Provides
    public MergerApi provideMergerApi(GraphMergerFactory mergerFactory) {
        return mergerFactory.createMerger();
    }
} 