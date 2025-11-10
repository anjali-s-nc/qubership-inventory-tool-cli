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

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.qubership.itool.modules.graph.Graph.F_NAME;
import static org.qubership.itool.modules.graph.Graph.F_VERSION;

class LanguageUtilsTest {

    @BeforeEach
    public void setUp() throws Exception {
    }

    @Test
    void convertListToLanguages() {
        assertEquals(new JsonArray(), LanguageUtils.convertListToLanguages(""));
        assertEquals(new JsonArray(), LanguageUtils.convertListToLanguages(" "));
        assertEquals(new JsonArray(), LanguageUtils.convertListToLanguages(null));
        JsonArray languages = new JsonArray()
                .add("angular")
                .add("java 22 x64");
        JsonArray expected = new JsonArray()
                .add(new JsonObject()
                        .put(F_NAME, "Angular"))
                .add(new JsonObject()
                        .put(F_NAME, "Java")
                        .put(F_VERSION, "22"));
        assertEquals(expected, LanguageUtils.convertListToLanguages(languages));

        List<String> languageList = new ArrayList<>();
        languageList.add("no");
        languageList.add("not required");
        languageList.add("go 1.1.1");
        expected = new JsonArray()
                .add(new JsonObject()
                        .put(F_NAME, "GoLang")
                        .put(F_VERSION, "1.1.1"));
        assertEquals(expected, LanguageUtils.convertListToLanguages(languageList));
    }
}
