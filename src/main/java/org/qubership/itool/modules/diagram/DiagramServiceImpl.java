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

package org.qubership.itool.modules.diagram;

import org.qubership.itool.modules.diagram.providers.DiagramProvider;
import org.qubership.itool.modules.graph.Graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DiagramServiceImpl implements DiagramService {

    private Graph graph;

    Properties properties;

    private Map<String, DiagramProvider> diagramProviders = new HashMap<>();

    public DiagramServiceImpl(Graph graph, Properties properties) {
        this.graph = graph;
        this.properties = properties;
    }

    @Override
    public void register(DiagramProvider diagramProvider) {
        diagramProvider.setProperties(this.properties);
        this.diagramProviders.put(diagramProvider.getType(), diagramProvider);
    }

    @Override
    public String generate(String type, Diagram diagram) {
        DiagramProvider diagramProvider = this.diagramProviders.get(type);
        if (diagramProvider == null) {
            throw new IllegalStateException("Can't find DiagramProvider: " + type + ". Available: "
                    + this.diagramProviders.keySet());
        }

        return diagramProvider.generate(this, this.graph, diagram);
    }

}
