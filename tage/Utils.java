package tage;

import static com.jogamp.opengl.GL.GL_CLAMP_TO_EDGE;
import static com.jogamp.opengl.GL.GL_LINEAR_MIPMAP_LINEAR;
import static com.jogamp.opengl.GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;
import static com.jogamp.opengl.GL.GL_NO_ERROR;
import static com.jogamp.opengl.GL.GL_RGBA;
import static com.jogamp.opengl.GL.GL_RGBA8;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_CUBE_MAP;
import static com.jogamp.opengl.GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static com.jogamp.opengl.GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static com.jogamp.opengl.GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static com.jogamp.opengl.GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static com.jogamp.opengl.GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static com.jogamp.opengl.GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static com.jogamp.opengl.GL.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static com.jogamp.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_S;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_T;
import static com.jogamp.opengl.GL.GL_UNSIGNED_BYTE;
import static com.jogamp.opengl.GL2ES2.GL_COMPILE_STATUS;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_INFO_LOG_LENGTH;
import static com.jogamp.opengl.GL2ES2.GL_LINK_STATUS;
import static com.jogamp.opengl.GL2ES2.GL_TEXTURE_WRAP_R;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import static com.jogamp.opengl.GL3ES3.GL_COMPUTE_SHADER;
import static com.jogamp.opengl.GL3ES3.GL_GEOMETRY_SHADER;
import static com.jogamp.opengl.GL3ES3.GL_MAX_COMPUTE_WORK_GROUP_COUNT;
import static com.jogamp.opengl.GL3ES3.GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS;
import static com.jogamp.opengl.GL3ES3.GL_MAX_COMPUTE_WORK_GROUP_SIZE;
import static com.jogamp.opengl.GL3ES3.GL_TESS_CONTROL_SHADER;
import static com.jogamp.opengl.GL3ES3.GL_TESS_EVALUATION_SHADER;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.jme3.math.Quaternion;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import rowlandsAdventure2.Enums.CollisionGroups;
import rowlandsAdventure2.builders.CommonResources;
import rowlandsAdventure2.character.BouncePadSurface;
import rowlandsAdventure2.planets.Planet;
import tage.TextureImage.TextureData;
import tage.JmeBullet.JmeBulletPhysicsObject;

/**
 * Shader and graphics utilities used by the engine.
 * This class is taken from Computer Graphics Programming in OpenGL with Java.
 * <p>
 * Most of the functions are used by the engine and are protected (and thus not
 * visible in the javadoc).
 * <p>
 * The predefined materials are shown here and may be used by the game
 * application.
 * 
 * @author Scott Gordon
 * @author John Clevenger
 */

public class Utils {
	public Utils() {
	}

	protected static int createShaderProgram(String vS, String tCS, String tES, String gS, String fS) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int vShader = prepareShader(GL_VERTEX_SHADER, vS);
		int tcShader = prepareShader(GL_TESS_CONTROL_SHADER, tCS);
		int teShader = prepareShader(GL_TESS_EVALUATION_SHADER, tES);
		int gShader = prepareShader(GL_GEOMETRY_SHADER, gS);
		int fShader = prepareShader(GL_FRAGMENT_SHADER, fS);
		int vtgfprogram = gl.glCreateProgram();
		gl.glAttachShader(vtgfprogram, vShader);
		gl.glAttachShader(vtgfprogram, tcShader);
		gl.glAttachShader(vtgfprogram, teShader);
		gl.glAttachShader(vtgfprogram, gShader);
		gl.glAttachShader(vtgfprogram, fShader);
		finalizeProgram(vtgfprogram);
		return vtgfprogram;
	}

	protected static int createShaderProgram(String vS, String tCS, String tES, String fS) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int vShader = prepareShader(GL_VERTEX_SHADER, vS);
		int tcShader = prepareShader(GL_TESS_CONTROL_SHADER, tCS);
		int teShader = prepareShader(GL_TESS_EVALUATION_SHADER, tES);
		int fShader = prepareShader(GL_FRAGMENT_SHADER, fS);
		int vtfprogram = gl.glCreateProgram();
		gl.glAttachShader(vtfprogram, vShader);
		gl.glAttachShader(vtfprogram, tcShader);
		gl.glAttachShader(vtfprogram, teShader);
		gl.glAttachShader(vtfprogram, fShader);
		finalizeProgram(vtfprogram);
		return vtfprogram;
	}

	protected static int createShaderProgram(String vS, String gS, String fS) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int vShader = prepareShader(GL_VERTEX_SHADER, vS);
		int gShader = prepareShader(GL_GEOMETRY_SHADER, gS);
		int fShader = prepareShader(GL_FRAGMENT_SHADER, fS);
		int vgfprogram = gl.glCreateProgram();
		gl.glAttachShader(vgfprogram, vShader);
		gl.glAttachShader(vgfprogram, gShader);
		gl.glAttachShader(vgfprogram, fShader);
		finalizeProgram(vgfprogram);
		return vgfprogram;
	}

	protected static int createShaderProgram(String vS, String fS) {
		// Thread var2 = Thread.currentThread();
		// System.out.println("Current Thread: " + var2.getName());
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int vShader = prepareShader(GL_VERTEX_SHADER, vS);
		int fShader = prepareShader(GL_FRAGMENT_SHADER, fS);
		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		finalizeProgram(vfprogram);
		return vfprogram;
	}

	protected static int createShaderProgram(String cS) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int cShader = prepareShader(GL_COMPUTE_SHADER, cS);
		int cprogram = gl.glCreateProgram();
		gl.glAttachShader(cprogram, cShader);
		finalizeProgram(cprogram);
		return cprogram;
	}

	protected static int finalizeProgram(int sprogram) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] linked = new int[1];
		gl.glLinkProgram(sprogram);
		checkOpenGLError();
		gl.glGetProgramiv(sprogram, GL_LINK_STATUS, linked, 0);
		if (linked[0] != 1) {
			System.out.println("linking failed");
			printProgramLog(sprogram);
		}
		return sprogram;
	}

	protected static int prepareShader(int shaderTYPE, String shader) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] shaderCompiled = new int[1];
		String shaderSource[] = readShaderSource(shader);
		int shaderRef = gl.glCreateShader(shaderTYPE);
		gl.glShaderSource(shaderRef, shaderSource.length, shaderSource, null, 0);
		gl.glCompileShader(shaderRef);
		checkOpenGLError();
		gl.glGetShaderiv(shaderRef, GL_COMPILE_STATUS, shaderCompiled, 0);
		if (shaderCompiled[0] != 1) {
			if (shaderTYPE == GL_VERTEX_SHADER)
				System.out.print("Vertex ");
			if (shaderTYPE == GL_TESS_CONTROL_SHADER)
				System.out.print("Tess Control ");
			if (shaderTYPE == GL_TESS_EVALUATION_SHADER)
				System.out.print("Tess Eval ");
			if (shaderTYPE == GL_GEOMETRY_SHADER)
				System.out.print("Geometry ");
			if (shaderTYPE == GL_FRAGMENT_SHADER)
				System.out.print("Fragment ");
			if (shaderTYPE == GL_COMPUTE_SHADER)
				System.out.print("Compute ");
			System.out.println("shader compilation error.");
			printShaderLog(shaderRef);
		}
		return shaderRef;
	}

	protected static String[] readShaderSource(String filename) {
		Vector<String> lines = new Vector<String>();
		Scanner sc;
		String[] program;
		try {
			sc = new Scanner(new File(filename));
			while (sc.hasNext()) {
				lines.addElement(sc.nextLine());
			}
			program = new String[lines.size()];
			for (int i = 0; i < lines.size(); i++) {
				program[i] = (String) lines.elementAt(i) + "\n";
			}
			sc.close();
		} catch (IOException e) {
			System.err.println("IOException reading file: " + e);
			return null;
		}
		return program;
	}

	protected static void printShaderLog(int shader) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;

		// determine the length of the shader compilation log
		gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0) {
			log = new byte[len[0]];
			gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);
			System.out.println("Shader Info Log: ");
			for (int i = 0; i < log.length; i++) {
				System.out.print((char) log[i]);
			}
		}
	}

	protected static void printProgramLog(int prog) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;

		// determine length of the program compilation log
		gl.glGetProgramiv(prog, GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0) {
			log = new byte[len[0]];
			gl.glGetProgramInfoLog(prog, len[0], chWrittn, 0, log, 0);
			System.out.println("Program Info Log: ");
			for (int i = 0; i < log.length; i++) {
				System.out.print((char) log[i]);
			}
		}
	}

	protected static boolean checkOpenGLError() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		boolean foundError = false;
		GLU glu = new GLU();
		int glErr = gl.glGetError();
		while (glErr != GL_NO_ERROR) {
			System.err.println("glError: " + glu.gluErrorString(glErr));
			foundError = true;
			glErr = gl.glGetError();
		}
		return foundError;
	}

	protected static void displayComputeShaderLimits() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] work_grp_cnt = new int[3];
		int[] work_grp_siz = new int[3];
		int[] work_grp_inv = new int[1];
		gl.glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_COUNT, 0, work_grp_cnt, 0);
		gl.glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_COUNT, 1, work_grp_cnt, 1);
		gl.glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_COUNT, 2, work_grp_cnt, 2);
		gl.glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_SIZE, 0, work_grp_siz, 0);
		gl.glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_SIZE, 1, work_grp_siz, 1);
		gl.glGetIntegeri_v(GL_MAX_COMPUTE_WORK_GROUP_SIZE, 2, work_grp_siz, 2);
		gl.glGetIntegerv(GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS, work_grp_inv, 0);
		System.out.println("maximum number of workgroups is: \n" +
				work_grp_cnt[0] + " " + work_grp_cnt[1] + " " + work_grp_cnt[2]);
		System.out.println("maximum size of workgroups is: \n" +
				work_grp_siz[0] + " " + work_grp_siz[1] + " " + work_grp_siz[2]);
		System.out.println("max local work group invocations is " + work_grp_inv[0]);
	}

	protected static int loadTexture(String textureFileName) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int finalTextureRef;
		Texture tex = null;
		try {
			tex = TextureIO.newTexture(new File(textureFileName), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finalTextureRef = tex.getTextureObject();

		// building a mipmap and use anisotropic filtering
		gl.glBindTexture(GL_TEXTURE_2D, finalTextureRef);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		gl.glGenerateMipmap(GL_TEXTURE_2D);
		if (gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic")) {
			float anisoset[] = new float[1];
			gl.glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, anisoset, 0);
			gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, anisoset[0]);
		}
		return finalTextureRef;
	}

	protected static int loadTextureAWT(String textureFileName, TextureData dataOut) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		BufferedImage textureImage = getBufferedImage(textureFileName);
		byte[] imgRGBA = getRGBAPixelData(textureImage, true);
		ByteBuffer rgbaBuffer = Buffers.newDirectByteBuffer(imgRGBA);
		dataOut.rgba = imgRGBA;
		dataOut.width = textureImage.getWidth();
		dataOut.height = textureImage.getHeight();

		int[] textureIDs = new int[1]; // array to hold generated texture IDs
		gl.glGenTextures(1, textureIDs, 0);
		int textureID = textureIDs[0]; // ID for the 0th texture object
		gl.glBindTexture(GL_TEXTURE_2D, textureID); // specifies the currently active 2D texture
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, // MIPMAP Level, number of color components
				textureImage.getWidth(), textureImage.getHeight(), 0, // image size, border (ignored)
				GL_RGBA, GL_UNSIGNED_BYTE, // pixel format and data type
				rgbaBuffer); // buffer holding texture data

		// build a mipmap and use anisotropic filtering if available
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		gl.glGenerateMipmap(GL_TEXTURE_2D);

		if (gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic")) {
			float anisoset[] = new float[1];
			gl.glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, anisoset, 0);
			gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, anisoset[0]);
		}
		return textureID;
	}

	protected static int loadCubeMap(String dirName) {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		String topFile = dirName + File.separator + "yp.jpg";
		String leftFile = dirName + File.separator + "xn.jpg";
		String backFile = dirName + File.separator + "zn.jpg";
		String rightFile = dirName + File.separator + "xp.jpg";
		String frontFile = dirName + File.separator + "zp.jpg";
		String bottomFile = dirName + File.separator + "yn.jpg";

		BufferedImage topImage = getBufferedImage(topFile);
		BufferedImage leftImage = getBufferedImage(leftFile);
		BufferedImage frontImage = getBufferedImage(frontFile);
		BufferedImage rightImage = getBufferedImage(rightFile);
		BufferedImage backImage = getBufferedImage(backFile);
		BufferedImage bottomImage = getBufferedImage(bottomFile);

		byte[] topRGBA = getRGBAPixelData(topImage, false);
		byte[] leftRGBA = getRGBAPixelData(leftImage, false);
		byte[] frontRGBA = getRGBAPixelData(frontImage, false);
		byte[] rightRGBA = getRGBAPixelData(rightImage, false);
		byte[] backRGBA = getRGBAPixelData(backImage, false);
		byte[] bottomRGBA = getRGBAPixelData(bottomImage, false);

		ByteBuffer topWrappedRGBA = ByteBuffer.wrap(topRGBA);
		ByteBuffer leftWrappedRGBA = ByteBuffer.wrap(leftRGBA);
		ByteBuffer frontWrappedRGBA = ByteBuffer.wrap(frontRGBA);
		ByteBuffer rightWrappedRGBA = ByteBuffer.wrap(rightRGBA);
		ByteBuffer backWrappedRGBA = ByteBuffer.wrap(backRGBA);
		ByteBuffer bottomWrappedRGBA = ByteBuffer.wrap(bottomRGBA);

		int[] textureIDs = new int[1];
		gl.glGenTextures(1, textureIDs, 0);
		int textureID = textureIDs[0];

		int width = topImage.getWidth();
		int height = topImage.getHeight();

		checkOpenGLError();

		gl.glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);
		gl.glTexStorage2D(GL_TEXTURE_CUBE_MAP, 1, GL_RGBA8, width, height);

		// attach the image texture to each face of the currently active OpenGL texture
		// ID
		gl.glTexSubImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, 0, 0, width, height,
				GL_RGBA, GL_UNSIGNED_BYTE, rightWrappedRGBA);
		gl.glTexSubImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, 0, 0, width, height,
				GL_RGBA, GL_UNSIGNED_BYTE, leftWrappedRGBA);
		gl.glTexSubImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, 0, 0, width, height,
				GL_RGBA, GL_UNSIGNED_BYTE, bottomWrappedRGBA);
		gl.glTexSubImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, 0, 0, width, height,
				GL_RGBA, GL_UNSIGNED_BYTE, topWrappedRGBA);
		gl.glTexSubImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, 0, 0, width, height,
				GL_RGBA, GL_UNSIGNED_BYTE, frontWrappedRGBA);
		gl.glTexSubImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, 0, 0, width, height,
				GL_RGBA, GL_UNSIGNED_BYTE, backWrappedRGBA);

		gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

		checkOpenGLError();
		return textureID;
	}

	protected static BufferedImage getBufferedImage(String fileName) {
		BufferedImage img;
		try {
			img = ImageIO.read(new File(fileName)); // assumes GIF, JPG, PNG, BMP
		} catch (IOException e) {
			System.err.println("Error reading '" + fileName + '"');
			throw new RuntimeException(e);
		}
		return img;
	}

	protected static byte[] getRGBAPixelData(BufferedImage img, boolean flip) {
		int height = img.getHeight(null);
		int width = img.getWidth(null);

		// create an (empty) BufferedImage with a suitable Raster and ColorModel
		WritableRaster raster = Raster.createInterleavedRaster(
				DataBuffer.TYPE_BYTE, width, height, 4, null);

		// convert to a color model that OpenGL understands
		ComponentColorModel colorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 }, // bits
				true, // hasAlpha
				false, // isAlphaPreMultiplied
				ComponentColorModel.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);

		BufferedImage newImage = new BufferedImage(colorModel, raster, false, null);
		Graphics2D g = newImage.createGraphics();

		if (flip) // flip image vertically
		{
			AffineTransform gt = new AffineTransform();
			gt.translate(0, height);
			gt.scale(1, -1d);
			g.transform(gt);
		}
		g.drawImage(img, null, null); // draw original image into new image
		g.dispose();

		// now retrieve the underlying byte array from the raster data buffer
		DataBufferByte dataBuf = (DataBufferByte) raster.getDataBuffer();
		return dataBuf.getData();
	}

	// GOLD material - ambient, diffuse, specular, and shininess
	public static float[] goldAmbient() {
		return (new float[] { 0.2473f, 0.1995f, 0.0745f, 1 });
	}

	public static float[] goldDiffuse() {
		return (new float[] { 0.7516f, 0.6065f, 0.2265f, 1 });
	}

	public static float[] goldSpecular() {
		return (new float[] { 0.6283f, 0.5559f, 0.3661f, 1 });
	}

	public static float goldShininess() {
		return 51.2f;
	}

	// SILVER material - ambient, diffuse, specular, and shininess
	public static float[] silverAmbient() {
		return (new float[] { 0.1923f, 0.1923f, 0.1923f, 1 });
	}

	public static float[] silverDiffuse() {
		return (new float[] { 0.5075f, 0.5075f, 0.5075f, 1 });
	}

	public static float[] silverSpecular() {
		return (new float[] { 0.5083f, 0.5083f, 0.5083f, 1 });
	}

	public static float silverShininess() {
		return 51.2f;
	}

	// BRONZE material - ambient, diffuse, specular, and shininess
	public static float[] bronzeAmbient() {
		return (new float[] { 0.2125f, 0.1275f, 0.0540f, 1 });
	}

	public static float[] bronzeDiffuse() {
		return (new float[] { 0.7140f, 0.4284f, 0.1814f, 1 });
	}

	public static float[] bronzeSpecular() {
		return (new float[] { 0.3936f, 0.2719f, 0.1667f, 1 });
	}

	public static float bronzeShininess() {
		return 25.6f;
	}

	/**
	 * Default material - returns ambient component
	 */
	public static float[] defAmbient() {
		return (new float[] { 0.3f, 0.3f, 0.3f, 1 });
	}

	/**
	 * Default material - returns diffuse component
	 */
	public static float[] defDiffuse() {
		return (new float[] { 0.7f, 0.7f, 0.7f, 1 });
	}

	/**
	 * Default material - returns specular component
	 */
	public static float[] defSpecular() {
		return (new float[] { 0.6f, 0.6f, 0.6f, 1 });
	}

	/**
	 * Default material - returns shininess component
	 */
	public static float defShininess() {
		return 50.0f;
	}

	public static float epsilon = 0.00000000001f;

	/** clamps value between min and max */
	public static float clamp(float min, float max, float value) {
		if (value > max)
			return max;
		if (value < min)
			return min;
		return value;
	}

	/** clamps value between min and max */
	public static int clamp(int min, int max, int value) {
		if (value > max)
			return max;
		if (value < min)
			return min;
		return value;
	}

	/** clamps value between min and max */
	public static double clamp(double min, double max, double value) {
		if (value > max)
			return max;
		if (value < min)
			return min;
		return value;
	}

	private static float sqrMagnitude = 0f;
	private static float sqrMin = 0f;
	private static float sqrMax = 0f;

	public static void clampMagnitude(float min, float max, Vector3f value) {
		sqrMagnitude = value.lengthSquared();
		sqrMin = min * min;
		sqrMax = max * max;
		if (sqrMagnitude > sqrMax) {
			value.normalize(max);
		} else if (sqrMagnitude < sqrMin) {
			value.normalize(min);
		}

	}

	public static void clampMagnitude(float min, float max, Vector2f value) {
		sqrMagnitude = value.lengthSquared();
		sqrMin = min * min;
		sqrMax = max * max;
		if (sqrMagnitude > sqrMax) {
			value.normalize(max);
		} else if (sqrMagnitude < sqrMin) {
			value.normalize(min);
		}

	}

	private static Vector3f verticalComponent = new Vector3f(0f, 0f, 0f);

	public static void projectOntoPlane(Vector3f vector, Vector3f planeNormal) {
		// assuming planeNormal is normalized
		// project vector onto plane normal to get the vertical component
		planeNormal.mul(vector.dot(planeNormal), verticalComponent);
		// subtract vertical component from the vector to get just the horizontal
		// components
		vector.sub(verticalComponent);
	}

	//https://math.stackexchange.com/questions/4108428/how-to-project-vector-onto-a-plane-but-not-along-plane-normal
	private static Vector3f diffCheck = new Vector3f(0f, 0f, 0f);
	private static Vector3f temp = new Vector3f(0f, 0f, 0f);
	public static void projectOntoPlane(Vector3f vector, Vector3f planeNormal, Vector3f direction) {
		planeNormal.cross(direction,diffCheck);
		float numerator = vector.dot(planeNormal);
		float denominator = direction.dot(planeNormal);
		if (denominator == 0f){
			//vector already inside plane
			return;
		}
		direction.mul((numerator/denominator),temp);
		vector.sub(temp);
	}

	public static float heightOffPlane(Vector3f vector, Vector3f planeNormal) {
		// assuming planeNormal is normalized
		// project vector onto plane normal to get the vertical component
		return vector.dot(planeNormal);
	}

	public static float lerp(float a, float b, float f) {
		return a * (1.0f - f) + (b * f);
	}

	private static Vector3f copyFwd_mlt = new Vector3f();
	private static Vector3f copyRight_mlt = new Vector3f();
	private static Vector3f up_mlt = new Vector3f();
	private static Vector3f right_mlt = new Vector3f();
	public static Matrix4f matLookTowards(Vector3f dir, Vector3f upDir){
		copyFwd_mlt.set(dir);
		if ((dir.equals(0,1,0)) || (dir.equals(0,-1,0)))
			right_mlt.set(1f,0f,0f);
		else{
			copyFwd_mlt.cross(upDir,right_mlt);
			right_mlt.normalize();
		}
		copyRight_mlt.set(right_mlt);
		copyRight_mlt.cross(dir,up_mlt);
		up_mlt.normalize();
		Matrix4f rot = new Matrix4f();
		rot.setColumn(0, new Vector4f(right_mlt.negate(), 0f));
		rot.setColumn(1, new Vector4f(up_mlt, 0f));
		rot.setColumn(2, new Vector4f(dir, 0f));
		return rot;
	}

	public static String formatVector(Vector3f v){
		String f = "%8.8f";
		String r = ("new Vector3f(" + String.format(f, v.x)+"f,"+ String.format(f, v.y)+"f,"+ String.format(f, v.z)+"f);");
		return r;
	}

	private static Vector3f bumperOffset = new Vector3f();
	public static void createBumper(Engine engine, Planet p1, Vector3f pos, Vector3f up, Vector3f forward, float strength, boolean overwrite){
		Vector3f bump1pos = pos;
        Vector3f bump1up = up;
        Vector3f bump1fo = forward;
		bump1up.normalize();
		Utils.projectOntoPlane(bump1fo, bump1up);
		bump1fo.normalize();
        GameObject b1 = new GameObject(GameObject.root(), CommonResources.effectCylinder, CommonResources.effectBumper);
        b1.setLocalLocation(bump1pos);
        Matrix4f bump1rot = Utils.matLookTowards(bump1fo, bump1up);
        b1.setLocalRotation(bump1rot);
        float[] b1lightDir = p1.getLightDir(new float[]{bump1pos.x,bump1pos.y,bump1pos.z});
        b1.getRenderStates().setShadowDirection(new Vector4f(b1lightDir[0],b1lightDir[1],b1lightDir[2],0f));
        float b1pl = p1.getLight(new float[]{bump1pos.x,bump1pos.y,bump1pos.z});
        b1.getRenderStates().setPlanetLight(b1pl);
        float b1sd = 2f;//p1.getShadowDistance();
        b1.getRenderStates().setShadowDistance(b1sd);
        Matrix4f physTransform = new Matrix4f();
        bump1up.mul(-0.35f, bumperOffset);
        bump1pos.add(bumperOffset);
        physTransform.translate(bump1pos);
        physTransform.mul(bump1rot);
        JmeBulletPhysicsObject b1po = engine.getSceneGraph().addPhysicsCylinder(0f, physTransform.get(new float[16]), 1f, 0.5f, CollisionGroups.Static.value(), CollisionGroups.All.value());
        b1.setPhysicsObject(b1po);
        b1po.setGameObject(b1);
        bump1up.mul(strength);
        b1po.setSurfaceProvider(new BouncePadSurface(bump1up, overwrite));
	}
}