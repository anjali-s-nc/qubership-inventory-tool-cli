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

package org.qubership.itool.modules.processor;

import io.vertx.core.Vertx;
import org.qubership.itool.modules.graph.GraphFactory;
import org.qubership.itool.modules.graph.GraphReportFactory;

import com.google.inject.Inject;


/**
 * Default implementation of GraphMergerFactory.
 */
public class DefaultGraphMergerFactory implements GraphMergerFactory {
    private final Vertx vertx;
    private final GraphFactory graphFactory;
    private final GraphReportFactory graphReportFactory;

    @Inject
    public DefaultGraphMergerFactory(Vertx vertx, GraphFactory graphFactory, GraphReportFactory graphReportFactory) {
        this.vertx = vertx;
        this.graphFactory = graphFactory;
        this.graphReportFactory = graphReportFactory;
    }

    @Override
    public GraphMerger createMerger() {
        return new GraphMerger(vertx, graphFactory, graphReportFactory);
    }
} 