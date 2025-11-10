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

package org.qubership.itool.modules.template;

import freemarker.template.DefaultListAdapter;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.qubership.itool.modules.diagram.DiagramBuilder;
import org.qubership.itool.modules.diagram.DiagramService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiagramQueueMethod implements TemplateMethodModelEx {

    private DiagramService diagramService;

    public DiagramQueueMethod(DiagramService diagramService) {
        this.diagramService = diagramService;
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        try {
            List<Map<String, Object>> producers = new ArrayList<>();
            List<Map<String, Object>> consumers = new ArrayList<>();
            if (arguments.get(0).getClass() == DefaultListAdapter.class) {
                producers.addAll((List<Map<String, Object>>) ((DefaultListAdapter) arguments.get(0))
                        .getWrappedObject());
                consumers.addAll((List<Map<String, Object>>) ((DefaultListAdapter) arguments.get(1))
                        .getWrappedObject());
            } else {
                producers = (List<Map<String, Object>>) arguments.get(0);
                consumers = (List<Map<String, Object>>) arguments.get(1);
            }

            DiagramBuilder diagramBuilder = new DiagramBuilder()
                    .producers(producers).consumers(consumers);

            String plantUml = this.diagramService.generate("rabbitMQ", diagramBuilder.build());
            return plantUml == null ? "" : plantUml;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
}
