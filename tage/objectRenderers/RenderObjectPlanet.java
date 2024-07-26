package tage.objectRenderers;

import static com.jogamp.opengl.GL.GL_ALWAYS;
import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_CCW;
import static com.jogamp.opengl.GL.GL_CLAMP_TO_EDGE;
import static com.jogamp.opengl.GL.GL_CW;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_FRONT_AND_BACK;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_MIRRORED_REPEAT;
import static com.jogamp.opengl.GL.GL_REPEAT;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE1;
import static com.jogamp.opengl.GL.GL_TEXTURE2;
import static com.jogamp.opengl.GL.GL_TEXTURE3;
import static com.jogamp.opengl.GL.GL_TEXTURE4;
import static com.jogamp.opengl.GL.GL_TEXTURE5;
import static com.jogamp.opengl.GL.GL_TEXTURE6;
import static com.jogamp.opengl.GL.GL_TEXTURE7;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_CUBE_MAP;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_S;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_T;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL2GL3.GL_FILL;
import static com.jogamp.opengl.GL2GL3.GL_LINE;
import static com.jogamp.opengl.GL3ES3.GL_SHADER_STORAGE_BUFFER;
import static com.jogamp.opengl.GL3ES3.GL_TRIANGLES_ADJACENCY;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

import tage.Engine;
import tage.GameObject;
import tage.Light;
import tage.TextureImage;
import tage.Time;

/**
 * Includes a single method render() for rendering a Game Object.
 * Considers the various render states that have been set.
 * Boolean flags are sent to the shaders at integers.
 * <p>
 * Follows closely the method described in Chapters 4, 5, 7, 9, and 10.
 * of Computer Graphics Programming in OpenGL with Java.
 * <p>
 * Used by the engine, should not be used directly by the game application.
 * 
 * @author Scott Gordon
 */
public class RenderObjectPlanet {
	private Engine engine;

	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	// private Matrix4f pMat = new Matrix4f(); // perspective matrix
	// private Matrix4f vMat = new Matrix4f(); // view matrix
	private Matrix4f mMat = new Matrix4f(); // model matrix
	private Matrix4f invTrMat = new Matrix4f(); // inverse-transpose
	private int mLoc, vLoc, pLoc, nLoc, tLoc, t2Loc, lLoc, eLoc, fLoc, sLoc, cLoc, hLoc, hsLoc, oLoc, tfLoc, tmLoc,
			s1Loc, s2Loc, s3Loc, s4Loc, shLoc;
	private int globalAmbLoc, mambLoc, mdiffLoc, mspecLoc, mshiLoc;
	private int hasSolidColor, hasTex, hasTex2, thisTexture, texture2, tiling, tilingOption, tileFactor,
			heightMapped;
	private int isEnvMapped, hasLighting, activeSkyBoxTexture, heightMapTexture;

	private TextureImage surface1, surface2, surface3, surface4, ramp;

	/** for engine use only. */
	public RenderObjectPlanet(Engine e, TextureImage r) {
		// load in standard planet textures here probably
		engine = e;
		surface1 = new TextureImage("grass.png");
		surface2 = new TextureImage("stone.png");
		surface3 = new TextureImage("dirt.png");
		surface4 = new TextureImage("ice.png");
		ramp = r;
	}

	/** for engine use only. */
	public void render(GameObject go, int renderingProgram, Matrix4f pMat, Matrix4f vMat, float shadow) {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		gl.glUseProgram(renderingProgram);

		mLoc = gl.glGetUniformLocation(renderingProgram, "m_matrix");
		vLoc = gl.glGetUniformLocation(renderingProgram, "v_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram, "norm_matrix");
		tLoc = gl.glGetUniformLocation(renderingProgram, "has_texture");
		t2Loc = gl.glGetUniformLocation(renderingProgram, "has_texture2");
		eLoc = gl.glGetUniformLocation(renderingProgram, "envMapped");
		oLoc = gl.glGetUniformLocation(renderingProgram, "hasLighting");
		sLoc = gl.glGetUniformLocation(renderingProgram, "solidColor");
		cLoc = gl.glGetUniformLocation(renderingProgram, "color");
		hLoc = gl.glGetUniformLocation(renderingProgram, "heightMapped");
		hsLoc = gl.glGetUniformLocation(renderingProgram, "heightScale");
		lLoc = gl.glGetUniformLocation(renderingProgram, "num_lights");
		fLoc = gl.glGetUniformLocation(renderingProgram, "fields_per_light");
		tfLoc = gl.glGetUniformLocation(renderingProgram, "tileCount");
		globalAmbLoc = gl.glGetUniformLocation(renderingProgram, "globalAmbient");
		mambLoc = gl.glGetUniformLocation(renderingProgram, "material.ambient");
		mdiffLoc = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
		mspecLoc = gl.glGetUniformLocation(renderingProgram, "material.specular");
		mshiLoc = gl.glGetUniformLocation(renderingProgram, "material.shininess");
		tmLoc = gl.glGetUniformLocation(renderingProgram, "time");
		s1Loc = gl.glGetUniformLocation(renderingProgram, "surf_color_1");
		s2Loc = gl.glGetUniformLocation(renderingProgram, "surf_color_2");
		s3Loc = gl.glGetUniformLocation(renderingProgram, "surf_color_3");
		s4Loc = gl.glGetUniformLocation(renderingProgram, "surf_color_4");
		shLoc = gl.glGetUniformLocation(renderingProgram, "shadow");

		mMat.identity();
		mMat.mul(go.getWorldTranslation());
		mMat.mul(go.getWorldRotation());
		mMat.mul(go.getRenderStates().getModelOrientationCorrection());
		mMat.mul(go.getWorldScale());

		if ((go.getRenderStates()).hasSolidColor()) {
			hasSolidColor = 1;
			hasTex = 0;
		} else {
			hasSolidColor = 0;
			hasTex = 1;
		}

		if ((go.getRenderStates()).isEnvironmentMapped())
			isEnvMapped = 1;
		else
			isEnvMapped = 0;

		if (go.isTerrain())
			heightMapped = 1;
		else
			heightMapped = 0;

		if (go.getRenderStates().hasLighting())
			hasLighting = 1;
		else
			hasLighting = 0;

		Vector4f shadowDir = go.getRenderStates().getShadowDirection();
		engine.getLightManager().getLight(0).setDirection(new Vector3f(shadowDir.x, shadowDir.y, shadowDir.z));
		engine.getLightManager().updateSSBO();
		gl.glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, (engine.getLightManager()).getLightSSBO());

		mMat.invert(invTrMat);
		invTrMat.transpose(invTrMat);

		gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		if (go.getTextureImage() != null)
			hasTex = 1;
		else
			hasTex = 0;
		if (go.getTextureImage2() != null)
			hasTex2 = 1;
		else
			hasTex2 = 0;
		gl.glUniform1i(tLoc, hasTex);
		gl.glUniform1i(t2Loc, hasTex2);
		gl.glUniform1i(eLoc, isEnvMapped);
		gl.glUniform1i(oLoc, hasLighting);
		gl.glUniform1i(sLoc, hasSolidColor);
		gl.glUniform3fv(cLoc, 1, ((go.getRenderStates()).getColor()).get(vals));
		gl.glUniform1i(hLoc, heightMapped);
		gl.glUniform1f(hsLoc, go.getTerrainScale());
		tileFactor = (go.getRenderStates()).getTileFactor();
		gl.glUniform1i(tfLoc, tileFactor);
		gl.glUniform1i(lLoc, (engine.getLightManager()).getNumLights());
		gl.glUniform1i(fLoc, (engine.getLightManager()).getFieldsPerLight());
		gl.glProgramUniform4fv(renderingProgram, globalAmbLoc, 1, Light.getGlobalAmbient(), 0);
		gl.glProgramUniform4fv(renderingProgram, mambLoc, 1, go.getShape().getMatAmb(), 0);
		gl.glProgramUniform4fv(renderingProgram, mdiffLoc, 1, go.getShape().getMatDif(), 0);
		gl.glProgramUniform4fv(renderingProgram, mspecLoc, 1, go.getShape().getMatSpe(), 0);
		gl.glProgramUniform1f(renderingProgram, mshiLoc, go.getShape().getMatShi());
		gl.glProgramUniform1f(renderingProgram, tmLoc, Time.elapsedTime);
		gl.glUniform1f(shLoc, shadow);

		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getVertexBuffer());
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getTexCoordBuffer());
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getNormalBuffer());
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getTangentBuffer());
		gl.glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(3);

		if (hasTex == 1)
			thisTexture = go.getTextureImage().getTexture();
		else
			thisTexture = engine.getRenderSystem().getDefaultTexture();

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, thisTexture);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		if (hasTex2 == 1) {
			texture2 = go.getTextureImage2().getTexture();
			gl.glActiveTexture(GL_TEXTURE1);
			gl.glBindTexture(GL_TEXTURE_2D, texture2);
			Vector4f surfCol1 = go.getTextureImage2().getNearestPixel(0.125f, 0.5f);
			Vector4f surfCol2 = go.getTextureImage2().getNearestPixel(0.375f, 0.5f);
			Vector4f surfCol3 = go.getTextureImage2().getNearestPixel(0.625f, 0.5f);
			Vector4f surfCol4 = go.getTextureImage2().getNearestPixel(0.875f, 0.5f);
			gl.glUniform3fv(s1Loc, 1, surfCol1.get(vals));
			gl.glUniform3fv(s2Loc, 1, surfCol2.get(vals));
			gl.glUniform3fv(s3Loc, 1, surfCol3.get(vals));
			gl.glUniform3fv(s4Loc, 1, surfCol4.get(vals));
		}

		heightMapTexture = go.getHeightMap().getTexture();
		gl.glActiveTexture(GL_TEXTURE2);
		gl.glBindTexture(GL_TEXTURE_2D, heightMapTexture);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		gl.glActiveTexture(GL_TEXTURE3);
		gl.glBindTexture(GL_TEXTURE_2D, surface1.getTexture());
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		gl.glActiveTexture(GL_TEXTURE4);
		gl.glBindTexture(GL_TEXTURE_2D, surface2.getTexture());
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		gl.glActiveTexture(GL_TEXTURE5);
		gl.glBindTexture(GL_TEXTURE_2D, surface3.getTexture());
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		gl.glActiveTexture(GL_TEXTURE6);
		gl.glBindTexture(GL_TEXTURE_2D, surface4.getTexture());
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		gl.glActiveTexture(GL_TEXTURE7);
		gl.glBindTexture(GL_TEXTURE_2D, ramp.getTexture());
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		if (go.getShape().isWindingOrderCCW())
			gl.glFrontFace(GL_CCW);
		else
			gl.glFrontFace(GL_CW);

		if ((go.getRenderStates()).isWireframe())
			gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		else
			gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		if (go.getRenderStates().hasDepthTesting()) {
			gl.glEnable(GL_DEPTH_TEST);
			gl.glDepthFunc(GL_LEQUAL);
		} else {
			gl.glDisable(GL_DEPTH_TEST);
			gl.glDepthFunc(GL_ALWAYS);
		}

		gl.glDrawArrays(GL_TRIANGLES_ADJACENCY, 0, go.getShape().getNumVertices());
	}
}