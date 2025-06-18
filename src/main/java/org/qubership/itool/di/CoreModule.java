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
import com.google.inject.Singleton;

import io.vertx.core.Vertx;

import org.qubership.itool.modules.report.GraphReport;
import org.qubership.itool.modules.report.GraphReportImpl;
import org.qubership.itool.modules.graph.GraphReportFactory;
import org.qubership.itool.modules.graph.DefaultGraphReportFactory;
import org.qubership.itool.modules.graph.Graph;
import org.qubership.itool.modules.graph.DefaultGraphFactory;
import org.qubership.itool.modules.graph.GraphFactory;
import org.qubership.itool.modules.graph.GraphImpl;

/**
 * Core module that provides basic bindings for the core functionality.
 * This module can be extended or overridden by users of the library.
 */
public class CoreModule extends AbstractModule {

    private final Vertx vertx;
    
    public CoreModule(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    protected void configure() {
        // Bind core factories
        bind(GraphFactory.class).to(DefaultGraphFactory.class).in(Singleton.class);
        bind(GraphReportFactory.class).to(DefaultGraphReportFactory.class).in(Singleton.class);
        bind(GraphReport.class).to(GraphReportImpl.class);
        bind(Graph.class).to(GraphImpl.class);
        bind(Vertx.class).toInstance(vertx);
    }
} 