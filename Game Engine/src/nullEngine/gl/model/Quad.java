package nullEngine.gl.model;

import nullEngine.loading.Loader;

public class Quad {
	
	private static Model model;
	private static Model back;
	
	private static final float[] vertices = new float[] {
			-1, 1, 0f,
			-1, -1, 0f,
			1, -1, 0f,
			1, 1, 0f,
	};

	private static final int[] indices = new int[] {
			0, 1, 3,
			3, 1, 2
	};

	private static final int[] indicesBack = new int[] {
			3, 1, 0,
			2, 1, 3
	};

	private static final float[] texCoords = new float[] {
			0, 0,
			0, 1,
			1, 1,
			1, 0
	};

	private static final float[] normals = new float[] {
			0, 0, -1,
			0, 0, -1,
			0, 0, -1,
			0, 0, -1
	};
	
	public static void setup(Loader loader) {
		model = loader.loadModel(vertices, texCoords, normals, indices);
		back = loader.loadModel(vertices, texCoords, normals, indicesBack);
	}
	
	public static Model get() {
		return model;
	}

	public static Model back() {
		return back;
	}
}
