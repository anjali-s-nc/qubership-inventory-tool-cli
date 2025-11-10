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

package org.qubership.itool.cli.ci.extension;

import io.vertx.core.json.JsonObject;
import org.qubership.itool.modules.report.GraphReportImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Extended implementation of GraphReport that adds custom functionality.
 */
public class ExtensionGraphReport extends GraphReportImpl {
    private static final Logger LOG = LoggerFactory.getLogger(ExtensionGraphReport.class);

    public ExtensionGraphReport() {
        super();
    }

    @Override
    public void addRecord(JsonObject record) {
        super.addRecord(record);
        record.put("extended", true);
        getLogger().info("[Extended] Adding record: {}", record);
    }

    // Add custom methods here
    protected Logger getLogger() {
        return LOG;
    }
}
