# Java APIs for graph merger library

## Public API

Public assembly API:
[org.qubership.itool.modules.processor.MergerApi](../../src/main/java/org/qubership/itool/modules/processor/MergerApi.java)

Its implementation is class [org.qubership.itool.modules.processor.GraphMerger](../../src/main/java/org/qubership/itool/modules/processor/GraphMerger.java)

Please use the following flow in order to merge applications into the single graph.
1. Prepare dumps with related metadata as a list of DumpAndMetainfo objects
2. Prepare target metadata for the resulting graph
3. Create new instance of GraphMerger
4. Invoke mergeDumps providing the prepared data
5. Close GraphMerger 

### Sample code to assemble graph from several Applications

```
// 1. Prepare dumps with related metadata as a list of DumpAndMetainfo objects
List<DumpAndMetainfo> dumpAndMetaList = new ArrayList<>();

for (...) {
    JsonObject srcMeta = new JsonObject()
        .put(P_IS_APPLICATION, true)
        .put(P_APP_NAME, "Application-Name")        // Set source Application name
        .put(P_APP_VERSION, "release-1.2.3"); // Set source Application version
    JsonObject srcDump = fetchGraphDumpFromArtifactory(...); // Dump of the graph for "Application-Name" of version "release-1.2.3"
    dumpAndMetaList.add(new DumpAndMetainfo(srcDump, srcMeta));
}

// 2. Prepare target metadata for the resulting graph
JsonObject targetMeta = new JsonObject()
    .put(P_IS_NAMESPACE, true)
    .put(P_NAMESPACE_NAME, "Customer prod");   // Set target namespace name

JsonObject namespaceDump;

// 3. Create new instance of GraphMerger
try (GraphMerger merger = new GraphMerger()) {
// 4. Invoke mergeDumps providing the prepared data
    namespaceDump = merger.mergeDumps(dumpAndMetaList, targetMeta); // Merge result
}
// 5. Close GraphMerger
```
