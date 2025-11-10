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

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.qubership.itool.modules.diagram.DiagramBuilder;
import org.qubership.itool.modules.diagram.DiagramService;

import java.util.List;

public class DiagramGeneralDomainMethod implements TemplateMethodModelEx {

    private DiagramService diagramService;

    public DiagramGeneralDomainMethod(DiagramService diagramService) {
        this.diagramService = diagramService;
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        try {
            String defaultDomainId = null;
            String department = null;
            if (arguments.get(0).getClass() == SimpleScalar.class) {
                department = ((SimpleScalar) arguments.get(0)).getAsString();
                if (arguments.size() >= 2) {
                    defaultDomainId = ((SimpleScalar) arguments.get(1)).getAsString();
                }
            } else {
                department = (String) arguments.get(0);
                if (arguments.size() >= 2) {
                    defaultDomainId = (String) arguments.get(1);
                }
            }
            DiagramBuilder diagramBuilder = new DiagramBuilder()
                    .department(department)
                    .defaultDomainLevelEntity(defaultDomainId);

            String plantUml = this.diagramService.generate("general-domains", diagramBuilder.build());
            return plantUml == null ? "" : plantUml;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

}
