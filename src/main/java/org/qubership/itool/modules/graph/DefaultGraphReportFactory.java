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

package org.qubership.itool.modules.graph;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.inject.Inject;
import org.qubership.itool.modules.report.GraphReport;

/**
 * Default implementation of GraphReportFactory that creates GraphReportImpl instances.
 */
@Singleton
public class DefaultGraphReportFactory implements GraphReportFactory {
    private final Provider<GraphReport> graphReportProvider;
    
    @Inject
    public DefaultGraphReportFactory(Provider<GraphReport> graphReportProvider) {
        this.graphReportProvider = graphReportProvider;
    }
    
    @Override
    public GraphReport createGraphReport() {
        return graphReportProvider.get();
    }
} 