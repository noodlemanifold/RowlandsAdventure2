package tage.objectRenderers;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL3ES3.GL_TRIANGLES_ADJACENCY;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

import tage.Camera;
import tage.Engine;
import tage.GameObject;
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
public class RenderObjectVolume {
	private Engine engine;

	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	private FloatBuffer valsVec = Buffers.newDirectFloatBuffer(4);
	//private Matrix4f pMat = new Matrix4f(); // perspective matrix
	//private Matrix4f vMat = new Matrix4f(); // view matrix
	private Matrix4f mMat = new Matrix4f(); // model matrix
	private Matrix4f invTrMat = new Matrix4f(); // inverse-transpose
	private int mLoc, vLoc, pLoc, nLoc, cLoc, lLoc, dLoc;

	/** for engine use only. */
	public RenderObjectVolume(Engine e) {
		engine = e;
	}

	/** for engine use only. */
	public void render(GameObject go, int renderingProgram, Matrix4f pMat, Matrix4f vMat, Camera camera) {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		gl.glUseProgram(renderingProgram);

		mLoc = gl.glGetUniformLocation(renderingProgram, "m_matrix");
		vLoc = gl.glGetUniformLocation(renderingProgram, "v_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram, "norm_matrix");
		cLoc = gl.glGetUniformLocation(renderingProgram, "cam_position");
		lLoc = gl.glGetUniformLocation(renderingProgram, "light_pos");
		dLoc = gl.glGetUniformLocation(renderingProgram, "shadow_distance");

		mMat.identity();
		mMat.mul(go.getWorldTranslation());
		mMat.mul(go.getWorldRotation());
		mMat.mul(go.getRenderStates().getModelOrientationCorrection());
		mMat.mul(go.getWorldScale());

		//gl.glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, (engine.getLightManager()).getLightSSBO());

		mMat.invert(invTrMat);
		invTrMat.transpose(invTrMat);

		gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

		Vector4f light = go.getRenderStates().getShadowDirection();
		Vector3f cp = camera.getLocation();
		Vector4f camPos = new Vector4f(cp.x,cp.y,cp.z,1f);
		gl.glUniform4fv(lLoc, 1, light.get(valsVec));
		gl.glUniform4fv(cLoc, 1, camPos.get(valsVec));
		gl.glUniform1f(dLoc, go.getRenderStates().getShadowDistance());


		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getVertexBuffer());
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getTexCoordBuffer());
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getNormalBuffer());
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);

		gl.glDrawArrays(GL_TRIANGLES_ADJACENCY, 0, go.getShape().getNumVertices());
	}
}