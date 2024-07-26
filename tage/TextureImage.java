package tage;

import org.joml.Vector2f;
import org.joml.Vector4f;

/**
* TextureImage holds all raw data associated with a particular texture image.
* <br>
* Specifically, it includes the following:
* <ul>
* <li> a String containing the associated texture file pathname
* <li> an integer reference to the associated OpenGL texture object
* </ul>
* <p>
* Each GameObject typically is associated with one TextureImage.
* More than one GameObject can use the same TextureImage.
* All texture images must be read in during init().
* However, assignments of texture images to game objects can be made or changed later.
* @author Scott Gordon
*/
public class TextureImage
{
	private String textureFile;
	private int texture;
	private boolean cache;

	private TextureData textureData;

	public TextureImage()
	{	
	}

	public static class TextureData{
		public int width;
		public int height;
		public byte[] rgba;
	}

	/** Loads a texture image file and uses it to build a new TextureImage object. */
	public TextureImage(String texFile){	
		textureFile = "assets/textures/" + texFile;
		cache = false;
		Engine.getEngine().getRenderSystem().addTexture(this);		
	}

	/** Loads a texture image file and uses it to build a new TextureImage object, with the option of keeping a copy of the texture as a byte array on the CPU for later lookups */
	public TextureImage(String texFile, boolean savePixels){	
		textureFile = "assets/textures/" + texFile;
		cache = savePixels;
		Engine.getEngine().getRenderSystem().addTexture(this);		
	}

	/** Gets pixel nearest to the specified uv coordinates if savePixels is true */
	public Vector4f getNearestPixel(float u, float v){
		Vector4f col = new Vector4f(0f,0f,0f,0f);
		if (!cache){
			return col;
		}
		u = Utils.clamp(0, 1, u);
		v = Utils.clamp(0, 1, v);
		int x = Math.round(u*(float)(textureData.width-1));
		int y = Math.round(v*(float)(textureData.height-1));
		int index = x*4 + y*4*textureData.width;
		col.x = ((int)(textureData.rgba[index+0] & 0xff)/255f);
		col.y = ((int)(textureData.rgba[index+1] & 0xff)/255f);
		col.z = ((int)(textureData.rgba[index+2] & 0xff)/255f);
		col.w = ((int)(textureData.rgba[index+3] & 0xff)/255f);
		return col;
	}

	/** Gets pixel nearest to the specified uv coordinates if savePixels is true */
	public Vector4f getNearestPixel(Vector2f uv){
		return getNearestPixel(uv.x, uv.y);
	}

	protected void setTexture(int t) { 
		texture = t; 
	}

	protected void setTextureData(TextureData data){
		textureData = data;
	}

	protected boolean getWantsCache(){
		return cache;
	}

	/** for engine use */
	public void setTextureFile(String t) { textureFile = t; }
	/** for engine use */
	public String getTextureFile() { return textureFile; }
	/** for engine use */
	public int getTexture() { return texture; }
}