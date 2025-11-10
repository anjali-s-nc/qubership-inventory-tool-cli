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
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.qubership.itool.modules.graph.Graph.F_NAME;
import static org.qubership.itool.modules.graph.Graph.F_VERSION;

public class TechNormalizationHelper {

    private static final String NORMALIZATION_RESOURCE_PATH = "classpath:/normalizationConfig.json";
    private static final Map<String, Pattern> TECH_NAMES_MAP = buildTechNamesMap();

    private static Map<String, Pattern> buildTechNamesMap() {
        JsonObject techList = null;
        try {
            techList = JsonUtils.readJsonResource(TechNormalizationHelper.class,
                    NORMALIZATION_RESOURCE_PATH, JsonObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Pattern> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> t : techList) {
            result.put(
                t.getKey(),
                StringUtils.isNotEmpty((String) t.getValue())
                    ? Pattern.compile((String) t.getValue(), CASE_INSENSITIVE)
                    : Pattern.compile("^" + t.getKey() + "(?:\\s+(.+))?$", CASE_INSENSITIVE)
            );
        }

        return Collections.unmodifiableMap(result);
    }

    public static Map<String, Pattern> getTechNamesMap() {
        return TECH_NAMES_MAP;
    }

    public static List<String> normalizeTechs(List<String> techNames) {
        return techNames.stream()
            .map(TechNormalizationHelper::normalizeTechWithVersion)
            .collect(Collectors.toList());
    }

    public static Optional<String> normalizeTech(String tech) {
        if (tech == null) {
            return Optional.empty();
        }
        return TECH_NAMES_MAP.entrySet().stream()
                .filter(entry -> entry.getValue().matcher(tech).matches())
                .map(Map.Entry::getKey)
                .findFirst();
    }

    public static List<String> getTechsNames(List<String> techEntries) {
        return techEntries.stream()
                .map(tech -> normalizeTech(tech).orElse(tech))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static String normalizeTechWithVersion(String tech) {
        if (StringUtils.isEmpty(tech)) {
            return tech;
        }
        if (TECH_NAMES_MAP.containsKey(tech)) {
            return tech;
        }
        Optional<String> matchedTech = TECH_NAMES_MAP.entrySet().stream()
            .map(entry -> {
                Matcher matcher = entry.getValue().matcher(tech);
                if (matcher.matches()) {
                    String version = matcher.group(1);
                    return entry.getKey() + (version != null ? " " + version : "");
                }
                return null;
            })
            .filter(Objects::nonNull)
            .findFirst();

        return matchedTech.orElse(tech);
    }

    public static JsonObject normalizeTechAsJson(String tech) {
        if (StringUtils.isBlank(tech)) {
            return null;
        }
        if (TECH_NAMES_MAP.containsKey(tech)) {
            return new JsonObject().put(F_NAME, tech);
        }
        String normalizedTech = normalizeTechWithVersion(tech);
        String[] nameVersion = normalizedTech.split("\\s+", 3); //strip everything after second element
        JsonObject result = new JsonObject()
                .put(F_NAME, nameVersion[0]);
        if (nameVersion.length > 1) {
            result.put(F_VERSION, nameVersion[1]);
        }
        return result;
    }

}
