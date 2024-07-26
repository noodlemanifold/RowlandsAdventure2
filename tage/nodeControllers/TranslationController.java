package tage.nodeControllers;
import tage.*;
import org.joml.*;
import java.lang.Math;

/**
* A TranslationController is a node controller that, when enabled, causes any object
* it is attached to to oscillate up and down on the axis specified.
* @author Scott Gordon, Roxanne Campbell
*/

public class TranslationController extends NodeController
{
	private Vector3f translationAxis = new Vector3f(0.0f, 1.0f, 0.0f);
	private Vector3f translationOrigin = new Vector3f(0.0f, 0.0f, 0.0f);
	private float translationPeriod = 1.0f;
	private float translationAmplitude = 1.0f;
	//private Engine engine;

	/** Creates a translation controller with vertical axis, and period and amplitude=1.0. */
	public TranslationController() { super(); }

	/** Creates a translation controller with rotation axis and parameters as specified. */
	public TranslationController(Engine e, Vector3f axis, Vector3f origin, float period, float amplitude)
	{	super();
		translationAxis = new Vector3f(axis);
		translationOrigin = origin;
		translationPeriod = period;
		translationAmplitude = amplitude;
		//engine = e;
	}

	/** sets the translation period when the controller is enabled */
	public void setPeriod(float s) { translationPeriod = s; }

	/** sets the translation amplitude when the controller is enabled */
	public void setAmplitude(float s) { translationAmplitude = s; }

	/** This is called automatically by the RenderSystem (via SceneGraph) once per frame
	*   during display().  It is for engine use and should not be called by the application.
	*/
	public void apply(GameObject go)
	{	float elapsedTime = super.getElapsedTimeTotal()/1000f;
		float disp = (float)Math.sin(elapsedTime * translationPeriod * 3.14159f * 2f) * translationAmplitude;
		Vector3f newPos = new Vector3f(translationAxis.x,translationAxis.y,translationAxis.z);
		newPos.mul(disp);
		newPos.add(translationOrigin);
		go.setLocalLocation(newPos);
	}
}