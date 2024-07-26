package tage.objectRenderers;

import static com.jogamp.opengl.GL.GL_BLEND;
import static com.jogamp.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static com.jogamp.opengl.GL.GL_SRC_ALPHA;
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
import tage.Time;
import tage.shapes.AnimatedShape;
import tage.shapes.ImportedModel;
import tage.TextureImage;

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
public class RenderObjectStandard {
	private Engine engine;

	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	//private Matrix4f pMat = new Matrix4f(); // perspective matrix
	//private Matrix4f vMat = new Matrix4f(); // view matrix
	private Matrix4f mMat = new Matrix4f(); // model matrix
	private Matrix4f invTrMat = new Matrix4f(); // inverse-transpose
	private int mLoc, vLoc, pLoc, nLoc, tLoc, t2Loc, lLoc, eLoc, fLoc, sLoc, cLoc, hLoc, hsLoc, oLoc, tfLoc, tmLoc, shLoc, plLoc;
	private int globalAmbLoc, mambLoc, mdiffLoc, mspecLoc, mshiLoc;
	private int hasSolidColor, hasTex, hasTex2, thisTexture, texture2, tiling, tilingOption, tileFactor,
			heightMapped;
	private int isEnvMapped, hasLighting, activeSkyBoxTexture, heightMapTexture;

	private TextureImage ramp;
	private TextureImage planetRamp;

	/** for engine use only. */
	public RenderObjectStandard(Engine e, TextureImage ramp, TextureImage planetRamp) {
		engine = e;
		this.ramp = ramp;
		this.planetRamp = planetRamp;
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
		shLoc = gl.glGetUniformLocation(renderingProgram, "shadow");
		plLoc = gl.glGetUniformLocation(renderingProgram, "planet_light");

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

		if (go.getTextureImage() != null)
			hasTex = 1;
		else
			hasTex = 0;
		if (go.getTextureImage2() != null)
			hasTex2 = 1;
		else
			hasTex2 = 0;

		//set directional light to math shadow direction
		Vector4f shadowDir = go.getRenderStates().getShadowDirection();
		engine.getLightManager().getLight(0).setDirection(new Vector3f(shadowDir.x,shadowDir.y,shadowDir.z));
		engine.getLightManager().updateSSBO();

		gl.glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, (engine.getLightManager()).getLightSSBO());

		mMat.invert(invTrMat);
		invTrMat.transpose(invTrMat);

		gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
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
		gl.glUniform1f(plLoc, ((go.getRenderStates()).getPlanetLight()));

		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getVertexBuffer());
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getTexCoordBuffer());
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getNormalBuffer());
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		if (hasTex == 1)
			thisTexture = go.getTextureImage().getTexture();
		else
			thisTexture = engine.getRenderSystem().getDefaultTexture();

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, thisTexture);
		tiling = (go.getRenderStates()).getTiling();
		if (tiling != 0) {
			if (tiling == 1) {
				tilingOption = GL_REPEAT;
			} else if (tiling == 2) {
				tilingOption = GL_MIRRORED_REPEAT;
			} else if (tiling == 3) {
				tilingOption = GL_CLAMP_TO_EDGE;
			}
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, tilingOption);
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, tilingOption);
		}

		activeSkyBoxTexture = (engine.getSceneGraph()).getActiveSkyBoxTexture();
		gl.glActiveTexture(GL_TEXTURE1);
		gl.glBindTexture(GL_TEXTURE_CUBE_MAP, activeSkyBoxTexture);

		heightMapTexture = go.getHeightMap().getTexture();
		gl.glActiveTexture(GL_TEXTURE2);
		gl.glBindTexture(GL_TEXTURE_2D, heightMapTexture);

		if (hasTex2 == 1) {
			texture2 = go.getTextureImage2().getTexture();
			gl.glActiveTexture(GL_TEXTURE3);
			gl.glBindTexture(GL_TEXTURE_2D, texture2);
		}

		gl.glActiveTexture(GL_TEXTURE4);
		gl.glBindTexture(GL_TEXTURE_2D, ramp.getTexture());
		gl.glActiveTexture(GL_TEXTURE5);
		gl.glBindTexture(GL_TEXTURE_2D, planetRamp.getTexture());

		if (go.getShape().isWindingOrderCCW())
			gl.glFrontFace(GL_CCW);
		else
			gl.glFrontFace(GL_CW);

		if ((go.getRenderStates()).isWireframe())
			gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		else
			gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		if (go.getRenderStates().hasDepthTesting()){
			gl.glEnable(GL_DEPTH_TEST);
			gl.glDepthFunc(GL_LEQUAL);
		}else{
			gl.glDisable(GL_DEPTH_TEST);
			gl.glDepthFunc(GL_ALWAYS);
		}

		if (go.getRenderStates().isTransparent()){
			gl.glEnable(GL_BLEND);
			//gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE);
			//gl.glBlendFunc(GL_DST_COLOR, GL_ZERO);
			gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); 
		}

		if (go.getShape() instanceof ImportedModel || go.getShape() instanceof AnimatedShape){
			gl.glDrawArrays(GL_TRIANGLES_ADJACENCY, 0, go.getShape().getNumVertices());
		}else{
			gl.glDrawArrays(GL_TRIANGLES, 0, go.getShape().getNumVertices());
		}

		gl.glDisable(GL_BLEND);
	}
}