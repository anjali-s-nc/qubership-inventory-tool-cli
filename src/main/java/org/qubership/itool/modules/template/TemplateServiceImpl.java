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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.vertx.core.json.JsonObject;
import org.qubership.itool.modules.diagram.DiagramService;
import org.qubership.itool.utils.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import static freemarker.template.Configuration.SQUARE_BRACKET_INTERPOLATION_SYNTAX;
import static freemarker.template.Configuration.SQUARE_BRACKET_TAG_SYNTAX;

public class TemplateServiceImpl implements TemplateService {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateServiceImpl.class);

    private DiagramService diagramService;
    private Configuration configuration;
    private JsonObject appConfig;

    public TemplateServiceImpl(DiagramService diagramService, JsonObject config) {
        this.diagramService = diagramService;
        this.appConfig = config;
    }

    public Configuration getConfiguration() {
        if (this.configuration == null) {
            try {
                this.configuration = configure();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this.configuration;
    }

    @Override
    public Configuration configure() throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setDirectoryForTemplateLoading(new File("."));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setTagSyntax(SQUARE_BRACKET_TAG_SYNTAX);
        cfg.setInterpolationSyntax(SQUARE_BRACKET_INTERPOLATION_SYNTAX);
        cfg.setObjectWrapper(new JsonObjectWrapper(cfg.getIncompatibleImprovements()));

        cfg.setSharedVariable("diagramMicroservice", new DiagramMicroserviceMethod(this.diagramService));
        cfg.setSharedVariable("diagramGeneralDomain", new DiagramGeneralDomainMethod(this.diagramService));
        cfg.setSharedVariable("diagramDomain", new DiagramDomainMethod(this.diagramService));
        cfg.setSharedVariable("diagramQueue", new DiagramQueueMethod(this.diagramService));

        cfg.addAutoImport("md",
                ConfigUtils.getConfigFilePath(appConfig, BASE_TEMPLATES_PATH, "macro", "markdown.ftlh").toString());

        return cfg;
    }

    @Override
    public Template getTemplate(String templateName) {
        Template template = null;
        try {
            template = getConfiguration().getTemplate(
                    ConfigUtils.getConfigFilePath(appConfig, BASE_TEMPLATES_PATH, templateName).toString()
                            .replaceAll("\\\\", "/"));
        } catch (IOException e) {
            getLogger().error("Freemarker error", e);
        }
        return template;
    }

    @Override
    public String processTemplate(ConfluencePage confluencePage) throws TemplateException, IOException {
        StringWriter writer = new StringWriter();
        getTemplate(confluencePage.getTemplate()).process(confluencePage.getDataModel(), writer);
        return writer.toString();
    }

    @Override
    public DiagramService getDiagramService() {
        return diagramService;
    }

    protected Logger getLogger() {
        return LOG;
    }
}
