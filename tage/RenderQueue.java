package tage;
import java.util.*;

import tage.shapes.AnimatedShape;
import tage.shapes.ImportedModel;

/**
* Builds a render queue by traversing the GameObjects as specified in the scenegraph tree.
* After building the queue as a Vector, it makes available an iterator for the queue.
* It is used by the engine before rendering each frame,
* and none of the functions should be called directly by the game application.
* <p>
* Eventually, the plan is to support transparency, such that transparent objects are
* moved to the end of the queue.  But this is not yet implemented.  As of now, all methods are protected.
* @author Scott Gordon
*/

public class RenderQueue
{
	private Vector<GameObject> queue;
	private Vector<GameObject> queueVolumes;
	private Vector<GameObject> queueNoDepth;
	private GameObject root;

	protected RenderQueue(GameObject r)
	{	queue = new Vector<GameObject>();
		queueVolumes = new Vector<GameObject>();
		queueNoDepth = new Vector<GameObject>();
		root = r;
	}

	// A standard queue includes all of the game objects.
	// It is built by starting at the root and traversing all of the
	// children and their descendents, adding them to the queue.

	protected Vector<GameObject> createStandardQueue()
	{	//GameObject current = root;
		queue.clear();
		queueNoDepth.clear();
		addToQueue(root.getChildrenIterator());
		return queue;
	}

	protected Vector<GameObject> createVolumeQueue()
	{	//GameObject current = root;
		queueVolumes.clear();
		addToVolumeQueue(root.getChildrenIterator());
		return queueVolumes;
	}

	protected void addToQueue(GameObject g) { queue.add(g); }
	protected void addToVolumeQueue(GameObject g) { queueVolumes.add(g); }
	protected void addToQueueNoDepth(GameObject g) { queueNoDepth.add(g); }

	// Recursive traversal of the game objects

	protected void addToQueue(Iterator<GameObject> goIterator)
	{	while (goIterator.hasNext())
		{	GameObject go = goIterator.next();
			if (go.getRenderStates().hasDepthTesting()){
				addToQueue(go);
			}else{
				addToQueueNoDepth(go);
			}
			if (go.hasChildren()) addToQueue(go.getChildrenIterator());
		}
		Iterator<GameObject> noDepthItr = queueNoDepth.iterator();
		while (noDepthItr.hasNext()){
			addToQueue((GameObject)noDepthItr.next());
		}
	}

	protected void addToVolumeQueue(Iterator<GameObject> goIterator)
	{	while (goIterator.hasNext())
		{	GameObject go = goIterator.next();
			if (go.getShape() instanceof ImportedModel || go.getShape() instanceof AnimatedShape){
				addToVolumeQueue(go);
			}
			if (go.hasChildren()) addToVolumeQueue(go.getChildrenIterator());
		}
	}

	protected Iterator<GameObject> getIterator() { return queue.iterator(); }
}