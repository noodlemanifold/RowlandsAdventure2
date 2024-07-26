package tage.objectRenderers;

import static com.jogamp.opengl.GL.GL_ALWAYS;
import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_CCW;
import static com.jogamp.opengl.GL.GL_CLAMP_TO_EDGE;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_FRONT_AND_BACK;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_MIRRORED_REPEAT;
import static com.jogamp.opengl.GL.GL_REPEAT;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE1;
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
import tage.shapes.AnimatedShape;
import tage.TextureImage;

/**
* Includes a single method render() for rendering a GameObject with animated shape.
* It is basically the same as rendering a standard object, except that it
* also transfers the pose skin matrices needed for the shader to pose the model.
* <p>
* Used by the game engine, should not be used directly by the game application.
* @author Scott Gordon
*/

public class RenderObjectAnimation
{	//private GLCanvas myCanvas;
	private Engine engine;

	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	//private FloatBuffer valsIT = Buffers.newDirectFloatBuffer(9);
	//private Matrix4f pMat = new Matrix4f();  // perspective matrix
	//private Matrix4f vMat = new Matrix4f();  // view matrix
	private Matrix4f mMat = new Matrix4f();  // model matrix
	private Matrix4f invTrMat = new Matrix4f(); // inverse-transpose
	private int mLoc, vLoc, pLoc, nLoc, eLoc, oLoc, tLoc, t2Loc,lLoc, fLoc, sLoc, cLoc,shLoc, plLoc;
	private int globalAmbLoc,mambLoc,mdiffLoc,mspecLoc,mshiLoc;
	private int skinMatLoc, skinMatITLoc;
	private int hasSolidColor, hasTex, hasTex2, thisTexture, texture2, tiling, tilingOption;
	private int isEnvMapped, hasLighting, activeSkyBoxTexture;

	private TextureImage ramp;
	private TextureImage planetRamp;

	/** for engine use only. */
	public RenderObjectAnimation(Engine e, TextureImage ramp, TextureImage planetRamp)
	{	engine = e;
		this.ramp = ramp;
		this.planetRamp = planetRamp;
	}

	/** for engine use only. */
	public void render(GameObject go, int renderingProgram, Matrix4f pMat, Matrix4f vMat, float shadow)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		// ----------- prepare animation transform matrices
		tage.rml.Matrix4[] skinMats = ((AnimatedShape)go.getShape()).getPoseSkinMatrices();
		tage.rml.Matrix3[] skinMatsIT = ((AnimatedShape)go.getShape()).getPoseSkinMatricesIT();
		int boneCount = ((AnimatedShape)go.getShape()).getBoneCount();

		gl.glUseProgram(renderingProgram);

		mLoc = gl.glGetUniformLocation(renderingProgram, "m_matrix");
		vLoc = gl.glGetUniformLocation(renderingProgram, "v_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram, "norm_matrix");
		tLoc = gl.glGetUniformLocation(renderingProgram, "has_texture");
		t2Loc = gl.glGetUniformLocation(renderingProgram, "has_texture2");
		oLoc = gl.glGetUniformLocation(renderingProgram, "hasLighting");
		eLoc = gl.glGetUniformLocation(renderingProgram, "envMapped");
		sLoc = gl.glGetUniformLocation(renderingProgram, "solidColor");
		cLoc = gl.glGetUniformLocation(renderingProgram, "color");
		lLoc = gl.glGetUniformLocation(renderingProgram, "num_lights");
		fLoc = gl.glGetUniformLocation(renderingProgram, "fields_per_light");
		globalAmbLoc = gl.glGetUniformLocation(renderingProgram, "globalAmbient");
		mambLoc = gl.glGetUniformLocation(renderingProgram, "material.ambient");
		mdiffLoc = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
		mspecLoc = gl.glGetUniformLocation(renderingProgram, "material.specular");
		mshiLoc = gl.glGetUniformLocation(renderingProgram, "material.shininess");
		shLoc = gl.glGetUniformLocation(renderingProgram, "shadow");
		plLoc = gl.glGetUniformLocation(renderingProgram, "planet_light");

		mMat.identity();
		mMat.mul(go.getWorldTranslation());
		mMat.mul(go.getWorldRotation());
		mMat.mul(go.getRenderStates().getModelOrientationCorrection());
		mMat.mul(go.getWorldScale());

		if (go.getRenderStates().hasLighting())
			hasLighting = 1;
		else
			hasLighting = 0;

		hasTex = 1;
		hasSolidColor = 0;
		if (go.getTextureImage2() != null)
			hasTex2 = 1;
		else
			hasTex2 = 0;
		
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
		gl.glUniform1i(oLoc, hasLighting);
		gl.glUniform1i(sLoc, hasSolidColor);
		gl.glUniform3fv(cLoc, 1, ((go.getRenderStates()).getColor()).get(vals));
		gl.glUniform1i(lLoc, (engine.getLightManager()).getNumLights());
		gl.glUniform1i(fLoc, (engine.getLightManager()).getFieldsPerLight());
		gl.glUniform4fv(globalAmbLoc, 1, Light.getGlobalAmbient(), 0);
		gl.glUniform4fv(mambLoc, 1, go.getShape().getMatAmb(), 0);
		gl.glUniform4fv(mdiffLoc, 1, go.getShape().getMatDif(), 0);
		gl.glUniform4fv(mspecLoc, 1, go.getShape().getMatSpe(), 0);
		gl.glUniform1f(mshiLoc, go.getShape().getMatShi());
		gl.glUniform1f(shLoc, shadow);
		gl.glUniform1f(plLoc, ((go.getRenderStates()).getPlanetLight()));

		if ((go.getRenderStates()).isEnvironmentMapped()) isEnvMapped=1; else isEnvMapped=0;
		gl.glUniform1i(eLoc, isEnvMapped);
		
		for (int i=0; i<boneCount; i++)
		{	skinMatLoc = gl.glGetUniformLocation(renderingProgram, "skin_matrices["+i+"]");
			skinMatITLoc = gl.glGetUniformLocation(renderingProgram, "skin_matrices_IT["+i+"]");
			gl.glUniformMatrix4fv(skinMatLoc, 1, false, vals.put(0,skinMats[i].toFloatArray())/*directFloatBuffer(skinMats[i].toFloatArray())*/);
			gl.glUniformMatrix3fv(skinMatITLoc, 1, false, vals.put(0,skinMatsIT[i].toFloatArray())/*directFloatBuffer(skinMatsIT[i].toFloatArray())*/);
		}

		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getVertexBuffer());
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
	
		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getTexCoordBuffer());
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getNormalBuffer());
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getBoneIndicesBuffer());
		gl.glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(3);

		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getBoneWeightBuffer());
		gl.glVertexAttribPointer(4, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(4);

		if (hasTex==1)
			thisTexture = go.getTextureImage().getTexture();
		else
			thisTexture = engine.getRenderSystem().getDefaultTexture();
	
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, thisTexture);
		tiling = (go.getRenderStates()).getTiling();
		if (tiling != 0)
		{	if (tiling == 1) { tilingOption = GL_REPEAT; }
			else if (tiling == 2) { tilingOption = GL_MIRRORED_REPEAT; }
			else if (tiling == 3) { tilingOption = GL_CLAMP_TO_EDGE; }
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, tilingOption);
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, tilingOption);
		}

		activeSkyBoxTexture = (engine.getSceneGraph()).getActiveSkyBoxTexture();
		gl.glActiveTexture(GL_TEXTURE1);
		gl.glBindTexture(GL_TEXTURE_CUBE_MAP, activeSkyBoxTexture);

		if (hasTex2 == 1) {
			texture2 = go.getTextureImage2().getTexture();
			gl.glActiveTexture(GL_TEXTURE3);
			gl.glBindTexture(GL_TEXTURE_2D, texture2);
		}

		gl.glActiveTexture(GL_TEXTURE4);
		gl.glBindTexture(GL_TEXTURE_2D, ramp.getTexture());
		gl.glActiveTexture(GL_TEXTURE5);
		gl.glBindTexture(GL_TEXTURE_2D, planetRamp.getTexture());

		if ((go.getRenderStates()).isWireframe())
			gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		else
			gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		gl.glFrontFace(GL_CCW);
		if (go.getRenderStates().hasDepthTesting()){
			gl.glEnable(GL_DEPTH_TEST);
			gl.glDepthFunc(GL_LEQUAL);
		}else{
			gl.glDisable(GL_DEPTH_TEST);
			gl.glDepthFunc(GL_ALWAYS);
		}

		gl.glDrawArrays(GL_TRIANGLES_ADJACENCY, 0, go.getShape().getNumVertices());
	}
	
	private FloatBuffer directFloatBuffer(float[] values)
	{	return (FloatBuffer) directFloatBuffer(values.length).put(values).rewind();
	}
	private FloatBuffer directFloatBuffer(int capacity)
	{	return directByteBuffer(capacity * Float.BYTES).asFloatBuffer();
	}
	private ByteBuffer directByteBuffer(int capacity)
	{	return (ByteBuffer) ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
	}
}