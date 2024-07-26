package tage.objectRenderers;

import static com.jogamp.opengl.GL.GL_ALWAYS;
import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_CCW;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LEQUAL;
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

import tage.Camera;
import tage.Engine;
import tage.GameObject;
import tage.shapes.AnimatedShape;

/**
* Includes a single method render() for rendering a GameObject with animated shape.
* It is basically the same as rendering a standard object, except that it
* also transfers the pose skin matrices needed for the shader to pose the model.
* <p>
* Used by the game engine, should not be used directly by the game application.
* @author Scott Gordon
*/

public class RenderObjectAnimationVolume
{	//private GLCanvas myCanvas;
	private Engine engine;

	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	private FloatBuffer valsVec = Buffers.newDirectFloatBuffer(4);
	//private FloatBuffer valsIT = Buffers.newDirectFloatBuffer(9);
	//private Matrix4f pMat = new Matrix4f();  // perspective matrix
	//private Matrix4f vMat = new Matrix4f();  // view matrix
	private Matrix4f mMat = new Matrix4f();  // model matrix
	private Matrix4f invTrMat = new Matrix4f(); // inverse-transpose
	private int mLoc, vLoc, pLoc, nLoc, cLoc, lLoc, dLoc;
	private int skinMatLoc, skinMatITLoc;

	/** for engine use only. */
	public RenderObjectAnimationVolume(Engine e)
	{	engine = e;
	}

	/** for engine use only. */
	public void render(GameObject go, int renderingProgram, Matrix4f pMat, Matrix4f vMat, Camera camera)
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
		cLoc = gl.glGetUniformLocation(renderingProgram, "cam_position");
		lLoc = gl.glGetUniformLocation(renderingProgram, "light_pos");
		dLoc = gl.glGetUniformLocation(renderingProgram, "shadow_distance");

		mMat.identity();
		mMat.mul(go.getWorldTranslation());
		mMat.mul(go.getWorldRotation());
		mMat.mul(go.getRenderStates().getModelOrientationCorrection());
		mMat.mul(go.getWorldScale());

		invTrMat.identity();
		invTrMat.mul(vMat);
		invTrMat.mul(mMat);
		invTrMat.invert(invTrMat);
		invTrMat.transpose(invTrMat);

		gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
		gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		
		for (int i=0; i<boneCount; i++)
		{	skinMatLoc = gl.glGetUniformLocation(renderingProgram, "skin_matrices["+i+"]");
			skinMatITLoc = gl.glGetUniformLocation(renderingProgram, "skin_matrices_IT["+i+"]");
			gl.glUniformMatrix4fv(skinMatLoc, 1, false, vals.put(0,skinMats[i].toFloatArray()));
			gl.glUniformMatrix3fv(skinMatITLoc, 1, false, vals.put(0,skinMatsIT[i].toFloatArray()));
		}

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

		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getBoneIndicesBuffer());
		gl.glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(3);

		gl.glBindBuffer(GL_ARRAY_BUFFER, go.getShape().getBoneWeightBuffer());
		gl.glVertexAttribPointer(4, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(4);


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