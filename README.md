# mesh
&nbsp;&nbsp;&nbsp;A cross compiled Scala 3 library that provides accessible functionality to generate, manipulate, and export triangular 3D mesh objects.  Currently supports .PLY, and .OBJ/.MTL file formats.

<h3>sbt</h3>

```scala
libraryDependencies += "ai.dragonfly" %%% "mesh" % "<LATEST_VERSION>"
```

<h3>Features:</h3>

- Fast, lightweight mesh generation for:
    - Plane
    - Cube
    - Cylinder
    - Drum
    - Threaded Bolt
    - Threaded Screw
- Mesh file export to formats:
    - PLY
    - OBJ + MTL
- Compiles to Scala JVM, Native, and JavaScript environments.

<h3>Examples:</h3>

```scala
// include these imports for all examples
import slash.vector.*
import slash.vector.Vec.*
import ai.dragonfly.mesh.shape.*
```
<h4>Plane</h4>

```scala
// Plane
Plane(
  Vec[3](0, 0, 1),  // first corner
  Vec[3](4, 0, 2),  // second corner
  Vec[3](0, 4, 3),  // third corner
  9,                // vertical segment count
  12                // horizontal segment count
)
```
<h4>Cube</h4>

```scala
Cube.minimal( 1.0 /* side length */ )  // minimal

Cube()  // default parameters

Cube(
  l = 1.0,         // side length
  n = 64,          // number of segments
  name = "Cube"    // mesh name
)
```
<h4>Cylinder</h4>

```scala
Cylinder() // default parameters

Cylinder(
  angularSegments = 36,
  sideSegments = 1,
  baseSegments = 1,
  capSegments = 1,
  radius = 1.0,
  height = 1.0
)
```
<h4>Drum</h4>

```scala
Drum() // default parameters

Drum(
  angularSegments = 12,
  sideSegments = 1,
  baseSegments = 1,
  capSegments = 1,
  baseRadius = 2.0,
  capRadius = 1.0,
  height = 1.0,
  name = "Drum"
)
```
<h4>Threaded Bolt</h4>

```scala
Bolt() // default parameters

Bolt(
  length = 10.0,
  threadsPerUnit = 3.0,
  threadThickness = 0.1,
  shankLength = 3.5,
  angularSegments = 12,
  threadRadius = 1.0,
  coreRadius = 0.85,
  name = "Bolt"
)
```
<h4>Threaded Screw</h4>

```scala
Screw() // default parameters

Screw(
  length = 7.0,
  threadsPerUnit = 2.0,
  threadThickness = 0.05,
  shankLength = 1.5,
  pointLength = 0.5,
  angularSegments = 12,
  threadRadius = 0.375,
  coreRadius = 0.25,
  name = "Screw"
)
```

<h3>Exporting to File Formats:</h3>
&nbsp;&nbsp;&nbsp;The examples below demonstrate how to convert a mesh to a string that represents a PLY, MTL, or OBJ file, or how to write the mesh directly to an OutputStream.

<h3>PLY:</h3>

```scala
import ai.dragonfly.mesh.shape.*
import ai.dragonfly.mesh.io.PLY

val ply:String = PLY.fromMesh(  // generate a PLY file as a String
  Cube(),                       // a mesh
  vertexColorMapper = PLY.randomVertexColorMapper // a way to color the vertices.
)

val os:java.io.OutputStream = ...

PLY.writeMesh(     // write directly to an output Stream
  mesh = Cube(),   // a mesh
  out = os,        // an output stream
  vertexColorMapper = PLY.randomVertexColorMapper // a way to color the vertices.
)
```


<h3>MTL:</h3>

```scala
import ai.dragonfly.mesh.io.MTL

val material = MTL(
  name,
  diffuse,
  ambient = gray,
  specular = Specular(),
  dissolve = 1f
)

val mtl:String = material.mtl   // generate an MTL file as a String


val os:java.io.OutputStream = ...

MTL.writeMTL(material, os)      // write directly to an output Stream
```

<h3>OBJ:</h3>

```scala
import ai.dragonfly.mesh.shape.*
import ai.dragonfly.mesh.io.OBJ

val cube:Mesh = Cube()

val obj:String = OBJ.fromMesh(  // generate an OBJ file as a String
  mesh = cube,
  name = cube.name,
  comment = "cube",
  materialLibraryFile = "default.MTL",
  material = material,
  smooth = true
)

val os:java.io.OutputStream = ...

OBJ.writeMesh(   // write directly to an output Stream
  mesh = cube,
  out = os,
  name = cube.name,
  comment = "cube",
  materialLibraryFileName = "default.MTL",
  material = material
)
```