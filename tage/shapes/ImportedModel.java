package tage.shapes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.joml.*;
import tage.*;

/**
 * Supports importing OBJ model files into the game.
 * <p>
 * There are tight restrictions on which OBJ files are supported.
 * They must have the following characteristics:
 * <ul>
 * <li>vertices must be triangulated (this importer doesn't support quads)
 * <li>texture coordinates must be present (i.e., must be UV-unwrapped)
 * <li>normal vectors must be present
 * <li>must be a SINGLE object, not a composite of multiple objects
 * <li>only v, vt, vn, and f tags are read - all other tags are ignored
 * <li>all f tags must be of the form f xxx/xxx/xxx xxx/xxx/xxx xxx/xxx/xxx
 * <li>associated material file is ignored (use the material accessor functions
 * instead)
 * </ul>
 * <p>
 * If you have a model that fails one of the above restrictions, you may need to
 * load it into
 * a tool such as Blender, and export it as an OBJ file that meets all of the
 * above.
 * <p>
 * This tool is described in Chapter 6 of Computer Graphics Programming in
 * OpenGL with Java.
 * 
 * @author Scott Gordon
 */
public class ImportedModel extends ObjShape {
	private Vector3f[] verticesV;
	private Vector2f[] texCoordsV;
	private Vector3f[] normalsV;
	private Vector3f[] tangentsV;
	private int numPrimitiveVerts;
	private String fileName;
	private HashMap<Edge, EdgeNeighbors> edgeMap;

	/**
	 * Use this constructor to read in an OBJ file with the specified file name,
	 * from the models folder.
	 */
	public ImportedModel(String filename) {
		super();
		importFrom(filename, "assets/models/");
	}

	/**
	 * Use this constructor to read in an OBJ file with the specified file name,
	 * from the specified folder.
	 */
	public ImportedModel(String filename, String location) {
		super();
		importFrom(filename, location);
	}

	// This is the importer described in the CSc-155 textbook
	private void importFrom(String filename, String location) {
		ModelImporter modelImporter = new ModelImporter();
		edgeMap = new HashMap<Edge, EdgeNeighbors>();
		this.fileName = filename;
		try {
			modelImporter.parseOBJ(location + filename);
			int numModelVerts = modelImporter.getNumVertices();
			int numFaces = numModelVerts / 3;
			numPrimitiveVerts = numModelVerts * 2;
			super.setNumVertices(numPrimitiveVerts);
			float[] verts = modelImporter.getVertices();
			float[] tcs = modelImporter.getTextureCoordinates();
			float[] norm = modelImporter.getNormals();

			verticesV = new Vector3f[numPrimitiveVerts];
			texCoordsV = new Vector2f[numPrimitiveVerts];
			normalsV = new Vector3f[numPrimitiveVerts];
			tangentsV = new Vector3f[numPrimitiveVerts];

			Vector3f nnv = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
			Vector2f nnt = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
			Vector3f nnn = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);

			Vector3f dv1 = new Vector3f();
			Vector3f dv2 = new Vector3f();
			Vector2f duv1 = new Vector2f();
			Vector2f duv2 = new Vector2f();

			// put in normal vertices while tracking edges
			// fill adjacency slots with null vectors.
			// these will be replaced if the edge has an adjacent face
			for (int i = 0; i < numFaces; i++) {
				int index = i * 3;
				for (int j = 0; j < 3; j++) {
					verticesV[index * 2] = new Vector3f();
					verticesV[index * 2].set(verts[index * 3], verts[index * 3 + 1], verts[index * 3 + 2]);
					verticesV[index * 2 + 1] = nnv;
					texCoordsV[index * 2] = new Vector2f();
					texCoordsV[index * 2].set(tcs[index * 2], tcs[index * 2 + 1]);
					texCoordsV[index * 2 + 1] = nnt;
					normalsV[index * 2] = new Vector3f();
					normalsV[index * 2].set(norm[index * 3], norm[index * 3 + 1], norm[index * 3 + 2]);
					normalsV[index * 2 + 1] = nnn;
					index += 1;
				}
				int i1 = (i * 3 + 0) * 2;
				int i2 = (i * 3 + 1) * 2;
				int i3 = (i * 3 + 2) * 2;
				addEdge(verticesV[i1], verticesV[i2], i3);
				addEdge(verticesV[i2], verticesV[i3], i1);
				addEdge(verticesV[i3], verticesV[i1], i2);

				verticesV[i2].sub(verticesV[i1], dv1);
				verticesV[i3].sub(verticesV[i1], dv2);

				texCoordsV[i2].sub(texCoordsV[i1], duv1);
				texCoordsV[i3].sub(texCoordsV[i1], duv2);

				float r = 1.0f / (duv1.x * duv2.y - duv1.y * duv2.x);
				// Vector3f tangent = (dv1 * duv2.y - dv2 * duv1.y) * r;
				// Vector3f bitangent = (dv2 * duv1.x - dv1 * duv2.x) * r;
				dv1.mul(duv2.y);
				dv2.mul(duv1.y);
				dv1.sub(dv2);
				dv1.mul(r);
				Vector3f tangent = new Vector3f(dv1);

				tangentsV[i1] = tangent;
				tangentsV[i1+1] = nnn;
				tangentsV[i2] = tangent;
				tangentsV[i2+1] = nnn;
				tangentsV[i3] = tangent;
				tangentsV[i3+1] = nnn;
			}
			// put in adjacency vertices
			for (int i = 0; i < numFaces; i++) {
				int i1 = (i * 3 + 0) * 2;
				int i2 = (i * 3 + 1) * 2;
				int i3 = (i * 3 + 2) * 2;
				int i1_2 = i1 + 1;
				int i2_3 = i2 + 1;
				int i3_1 = i3 + 1;
				int a1 = getAdjacent(verticesV[i1], verticesV[i2], i3);
				int a2 = getAdjacent(verticesV[i2], verticesV[i3], i1);
				int a3 = getAdjacent(verticesV[i3], verticesV[i1], i2);
				if (a1 >= 0) {
					verticesV[i1_2] = verticesV[a1];
					texCoordsV[i1_2] = texCoordsV[a1];
					normalsV[i1_2] = normalsV[a1];
					tangentsV[i1_2] = tangentsV[a1];
				}
				if (a2 >= 0) {
					verticesV[i2_3] = verticesV[a2];
					texCoordsV[i2_3] = texCoordsV[a2];
					normalsV[i2_3] = normalsV[a2];
					tangentsV[i2_3] = tangentsV[a2];
				}
				if (a3 >= 0) {
					verticesV[i3_1] = verticesV[a3];
					texCoordsV[i3_1] = texCoordsV[a3];
					normalsV[i3_1] = normalsV[a3];
					tangentsV[i3_1] = tangentsV[a3];
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		setNumVertices(this.getNumVertices());
		setVertices(this.getVerticesVector());
		setTexCoords(this.getTexCoordsVector());
		setNormals(this.getNormalsVector());
		setTangents(this.getTangentsVector());
		setWindingOrderCCW(true);

		edgeMap = null;
	}

	// these methods are for engine use only
	protected Vector3f[] getVerticesVector() {
		return verticesV;
	}

	protected Vector2f[] getTexCoordsVector() {
		return texCoordsV;
	}

	protected Vector3f[] getNormalsVector() {
		return normalsV;
	}

	public Vector3f[] getTangentsVector(){
		return tangentsV;
	}

	public String getFileName() {
		return fileName;
	}

	private void addEdge(Vector3f a, Vector3f b, int i) {
		Edge e = new Edge(a, b);
		EdgeNeighbors n = edgeMap.get(e);
		if (n == null) {
			n = new EdgeNeighbors();
			n.ia = i;
		} else {
			assert (n.ib == -1);
			n.ib = i;
		}
		edgeMap.put(e, n);
	}

	private int getAdjacent(Vector3f a, Vector3f b, int c) {
		Edge e = new Edge(a, b);
		EdgeNeighbors n = edgeMap.get(e);
		int ret = n.ia;
		if (n.ia == c) {
			ret = n.ib;
		}
		return ret;
	}

	private class ModelImporter { // values as read from OBJ file
		private ArrayList<Float> vertVals = new ArrayList<Float>();
		private ArrayList<Float> triangleVerts = new ArrayList<Float>();
		private ArrayList<Float> textureCoords = new ArrayList<Float>();

		// values stored for later use as vertex attributes
		private ArrayList<Float> stVals = new ArrayList<Float>();
		private ArrayList<Float> normals = new ArrayList<Float>();
		private ArrayList<Float> normVals = new ArrayList<Float>();

		protected void parseOBJ(String filename) throws IOException {
			InputStream input = new FileInputStream(new File(filename));
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("v ")) // vertex position ("v" case)
				{
					for (String s : (line.substring(2)).split(" ")) {
						vertVals.add(Float.valueOf(s));
					}
				} else if (line.startsWith("vt")) // texture coordinates ("vt" case)
				{
					for (String s : (line.substring(3)).split(" ")) {
						stVals.add(Float.valueOf(s));
					}
				} else if (line.startsWith("vn")) // vertex normals ("vn" case)
				{
					for (String s : (line.substring(3)).split(" ")) {
						normVals.add(Float.valueOf(s));
					}
				} else if (line.startsWith("f")) // triangle faces ("f" case)
				{
					for (String s : (line.substring(2)).split(" ")) {
						String v = s.split("/")[0];
						String vt = s.split("/")[1];
						String vn = s.split("/")[2];

						int vertRef = (Integer.valueOf(v) - 1) * 3;
						int tcRef = (Integer.valueOf(vt) - 1) * 2;
						int normRef = (Integer.valueOf(vn) - 1) * 3;

						triangleVerts.add(vertVals.get(vertRef));
						triangleVerts.add(vertVals.get((vertRef) + 1));
						triangleVerts.add(vertVals.get((vertRef) + 2));

						textureCoords.add(stVals.get(tcRef));
						textureCoords.add(stVals.get(tcRef + 1));

						normals.add(normVals.get(normRef));
						normals.add(normVals.get(normRef + 1));
						normals.add(normVals.get(normRef + 2));
					}
				}
			}
			input.close();
		}

		protected int getNumVertices() {
			return (triangleVerts.size() / 3);
		}

		protected float[] getVertices() {
			float[] p = new float[triangleVerts.size()];
			for (int i = 0; i < triangleVerts.size(); i++) {
				p[i] = triangleVerts.get(i);
			}
			return p;
		}

		protected float[] getTextureCoordinates() {
			float[] t = new float[(textureCoords.size())];
			for (int i = 0; i < textureCoords.size(); i++) {
				t[i] = textureCoords.get(i);
			}
			return t;
		}

		protected float[] getNormals() {
			float[] n = new float[(normals.size())];
			for (int i = 0; i < normals.size(); i++) {
				n[i] = normals.get(i);
			}
			return n;
		}
	}

	private class Edge {
		public Vector3f pointa;
		public Vector3f pointb;

		public Edge(Vector3f a, Vector3f b) {
			pointa = a;
			pointb = b;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null)
				return false;
			if (this.getClass() != o.getClass())
				return false;
			Edge edge = (Edge) o;
			// if both vertices are the same this wont work but im too tired to fix that rn
			boolean hasA = edge.pointa.equals(this.pointa) || edge.pointb.equals(this.pointa);
			boolean hasB = edge.pointa.equals(this.pointb) || edge.pointb.equals(this.pointb);
			return (hasA && hasB);
		}

		@Override
		public int hashCode() {
			return pointa.hashCode() * pointb.hashCode();
		}
	}

	private class EdgeNeighbors {
		public int ia = -1;
		public int ib = -1;
	}
}
