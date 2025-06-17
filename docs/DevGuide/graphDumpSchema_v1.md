# Graph Dump JSON Data Model v1

## Overview

Graph Dump Data Model it's a container for following data structures:
* Report entities
* Graph Vertex and Edge entities

Container defined Model Version for own structure and for the underlying data.

```
{
  "modelVersion": 1,
  "report": { "modelVersion": 1, <REPORT DATA> },
  "graph": { "modelVersion": 1, <GRAPH DATA> }
}
```

JSON Schema:

![schema](https://www.planttext.com/plantuml/png/ZPAnYiCm38PtFuNnNF80dGetTBdvbjB1nCBrSP62f9OU9UzUApJK3GUzAVZJB_d_vUn48-kt4BgRypuCbE3d2rWAOTZwK50X0uUD5Hb6OgtOf6_4ixmdJb8h1t_pGNz7C9Ke5ynXK_j19EOLx6aO2G6rmNxonpfpcbraZkFftoOWPTW30aSb_g1phV9VNecUmUgvfDRLGjSH5evnQFcCfyVa2x32zRE8BejudUr3rtK6x6D16-Djbz2P-zMkwjjbq5TcVPRatJsFbRh9kHrWib_W3W00)

[Source](https://www.planttext.com/?text=ZPAnYiCm38PtFuNnNF80dGetTBdvbjB1nCBrSP62f9OU9UzUApJK3GUzAVZJB_d_vUn48-kt4BgRypuCbE3d2rWAOTZwK50X0uUD5Hb6OgtOf6_4ixmdJb8h1t_pGNz7C9Ke5ynXK_j19EOLx6aO2G6rmNxonpfpcbraZkFftoOWPTW30aSb_g1phV9VNecUmUgvfDRLGjSH5evnQFcCfyVa2x32zRE8BejudUr3rtK6x6D16-Djbz2P-zMkwjjbq5TcVPRatJsFbRh9kHrWib_W3W00)

Where:
* `/modelVersion` - Graph Dump Container Data Model Version
* `/report/modelVersion` - Report Entities Data Model Version
* `/graph/modelVersion` - Graph Data Model Version

## Report Entity

Report Entities contains information about some error/notification from the Graph Builder logic.
The Report Entity structure is simple: `type`, `message`, `component` (optional).

## Graph Entity

### Vertex

Because Graph contains a different types of Vertex (Component, Library, Application, File, ...) only one field is mandatory - `id`. Other field is optional. Most often useful fields is `type` and `name`

### Edge

Edge in the Graph has following structure:
* `source` - source Vertex Id
* `target` - target Vertex Id
* `edge` - edge properties. Mandatory only `id` property. Also common property is `type`

## Example

```
{
  "modelVersion": 1,
  "graph": {
    "modelVersion": 1,
    "edgeGeneratorCounter": 0,
    "root": {
      "id": "root",
      "type": "root",
      "name": "root"
    },
    "vertexList": [ { "id": "COMPONENT1", "type": "backend", "name": "Component name" } ],
    "edgeList": [ { "source": "root", "target": "COMPONENT1", "edge": { "id": "1", "type": "mandatory" } } ]
  },
  "report": {
    "modelVersion": 1,
    "records": [ { "type": "ERROR", "message": "Some error message", "component": "COMPONENT1" } ]
  }
}
```
