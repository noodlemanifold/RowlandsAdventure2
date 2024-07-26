package tage.JmeBullet;

import com.jme3.math.Matrix4f;

public class JmeBulletUtils {
	
    public static float[] double_to_float_array(double[] array)
    {
        float[] float_array = new float[array.length];
        for(int i=0; i<array.length; i++)
        {
            float_array[i] = (float) array[i];
        }
        return float_array;
    }
    public static void row_to_column_major(double[] array)
    {

    }
    public static double[] float_to_double_array(float[] array)
    {
        double[] double_array = new double[array.length];
        for(int i=0; i<array.length; i++)
        {
            double_array[i] = (double) array[i];
        }
        return double_array;
    }

    public static void setFromOpenGL(Matrix4f mat, float[] m){
        mat.m00 = m[0]; mat.m01 = m[4]; mat.m02 = m[8];
		mat.m10 = m[1]; mat.m11 = m[5]; mat.m12 = m[9];
		mat.m20 = m[2]; mat.m21 = m[6]; mat.m22 = m[10];
        mat.m03 = m[12];
		mat.m13 = m[13];
		mat.m23 = m[14];
		mat.m33 = 1f;
        
    }

    public static float[] getOpenGLMatrix(Matrix4f mat, float[] m){
        m[0] = mat.m00;
		m[1] = mat.m10;
		m[2] = mat.m20;
		m[3] = 0f;
		m[4] = mat.m01;
		m[5] = mat.m11;
		m[6] = mat.m21;
		m[7] = 0f;
		m[8] = mat.m02;
		m[9] = mat.m12;
		m[10] = mat.m22;
		m[11] = 0f;
        m[12] = mat.m03;
		m[13] = mat.m13;
		m[14] = mat.m23;
		m[15] = 1f;

        return m;
    }

}
