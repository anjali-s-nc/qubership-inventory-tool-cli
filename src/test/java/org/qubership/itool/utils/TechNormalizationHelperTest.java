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

package org.qubership.itool.utils;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TechNormalizationHelperTest {

    @Test
    void normalizeTechs() {
        assertEquals(List.of("Java 69", "GoLang 4.20", "RandomUnknownLanguage v1"),
                TechNormalizationHelper.normalizeTechs(List.of("java 69", "go 4.20", "RandomUnknownLanguage v1")));
    }

    @Test
    void normalizeTech() {
        assertEquals("Java", TechNormalizationHelper.normalizeTech("JaVa  69").orElse(null));
        assertEquals("Elasticsearch", TechNormalizationHelper
                .normalizeTech("opendistro elasticsearch 4.20").orElse(null));
        assertEquals("Unknown", TechNormalizationHelper.normalizeTech("GoShLang").orElse("Unknown"));
    }

    @Test
    void getTechsNames() {
        assertEquals(List.of("Java", "GoLang", "RandomUnknownTech v2"),
                TechNormalizationHelper.getTechsNames(List.of("java 69", "go 4.20", "RandomUnknownTech v2")));
    }

    @Test
    void normalizeTechWithVersion() {
        assertEquals("Java 69", TechNormalizationHelper.normalizeTechWithVersion("JavA   69"));
        assertEquals("Angular 4.20", TechNormalizationHelper.normalizeTechWithVersion("angular  4.20"));
        assertEquals("GoShLang 4.20", TechNormalizationHelper.normalizeTechWithVersion("GoShLang 4.20"));
        assertEquals("GoLang", TechNormalizationHelper.normalizeTechWithVersion("Go"));
    }

    @Test
    void normalizeTechAsJson() {
        assertEquals(new JsonObject()
                        .put("name", "Java")
                        .put("version", "69"),
                TechNormalizationHelper.normalizeTechAsJson("JavA   69 x32"));
        assertEquals(new JsonObject()
                        .put("name", "UnknownAlienTechnology")
                        .put("version", "v4"),
                TechNormalizationHelper.normalizeTechAsJson("UnknownAlienTechnology v4"));
        assertEquals(new JsonObject()
                        .put("name", "Unknown")
                        .put("version", "Alien"),
                TechNormalizationHelper.normalizeTechAsJson("Unknown Alien Technology v4"));
        assertEquals(new JsonObject().put("name", "GoLang"),
                TechNormalizationHelper.normalizeTechAsJson("go"));

    }
}
