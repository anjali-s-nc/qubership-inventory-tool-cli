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

package org.qubership.itool.modules.gremlin2;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.qubership.itool.modules.gremlin2.P.neq;
import static org.qubership.itool.modules.gremlin2.graph.__.in;
import static org.qubership.itool.modules.gremlin2.graph.__.out;
import static org.qubership.itool.modules.gremlin2.graph.__.repeat;

public class TestForkJoin extends AbstractGremlinTest {

    static final Comparator<Map<Object, String>> C1 = Comparator.comparing(m -> m.get("C1"));
    static final Comparator<Map<Object, String>> C2 = C1.thenComparing(m -> m.get("D1"));
    static final Comparator<Map<Object, String>> C3 = C2.thenComparing(m -> m.get("M1"));

    protected void createGraph() {
        createLoopedGraph();
    }

    @Test
    public void testSimpleForkJoin() {
        System.out.println("== testSimpleForkJoin ==");
        List<Map<Object, String>> result1 = V().hasType("domain").as("D")
            .out().as("C").select("D", "C")
            .<String>values("C1:/C/id", "D1:/D/id")
            .toList();
        result1.sort(C2);
        System.out.println("result1 = " + result1);

        List<Map<Object, String>> result2 = V().hasType("domain").as("D")
            .fork(
                out().as("C").select("D", "C")
            )
            .<String>values("C1:/C/id", "D1:/D/id")
            .toList();
        result2.sort(C2);

        assertEquals(result1, result2);
        System.out.println("======================");
    }

    @Test
    public void testLoopedForkJoin() {
        System.out.println("== testLoopedForkJoin ==");
        List<Map<Object, String>> result11 = V().hasType("domain").as("D").out().as("C")
            .repeat(out("module", "dependence"))
            .until(in("module").where(neq("C"))).as("M")
            .<JsonObject>select("D", "C", "M")
            .<String>values("C1:/C/id", "D1:/D/id", "M1:/M/id")
            .toList();
        result11.sort(C3);
        System.out.println("result11 = " + result11);

        List<Map<Object, String>> result12 = V().hasType("domain").as("D").out().as("C")
            .fork(
                repeat(out("module", "dependence"))
                .until(in("module").where(neq("C"))).as("M")
                .<JsonObject>select("D", "C", "M")
                .<String>values("C1:/C/id", "D1:/D/id", "M1:/M/id")
            )
            .toList();
        result12.sort(C3);

        assertEquals(result11, result12);

        List<Map<Object, String>> result21 = V().hasType("domain").as("D").out().as("C")
            .repeat(out("module", "dependence")).emit().as("M")
            .<JsonObject>select("D", "C", "M")
            .<String>values("C1:/C/id", "D1:/D/id", "M1:/M/id")
            .toList();
        result21.sort(C3);
        System.out.println("result21 = " + result21);

        List<Map<Object, String>> result22 = V().hasType("domain").as("D").out().as("C")
            .fork(
                repeat(out("module", "dependence")).emit().as("M")
                .<JsonObject>select("D", "C", "M")
                .<String>values("C1:/C/id", "D1:/D/id", "M1:/M/id")
            )
            .toList();
        result22.sort(C3);

        assertEquals(result21, result22);

        System.out.println("======================");

    }

}
