
public class DepthBuffer {
	public static int [][] bufferMap;
	private static int width, height;
	
	public static void initBufferMap(int w, int h) {
		width = w;
		height = h;
		bufferMap = new int[width][height];
		for(int i=0; i<width; i++) {
			for(int j=0; j<height; j++) {
				bufferMap[i][j] = 9999;
			}
		}
	}
	public static void initBufferMap() {
		for(int i=0; i<width; i++) {
			for(int j=0; j<height; j++) {
				bufferMap[i][j] = 9999;
			}
		}
	}
	public static void updateBuffer(Point3D view_point, Point3D tri1, Point3D tri2, Point3D tri3) {
		//divide the triangle into 2 parts, and we need to sort the 3D points first
		Point3D [] tri = {tri1, tri2, tri3};
		sortTriPoints(tri);
		
		//start to update the bufferMap as the depth of each point
		
	}
	public static boolean updateBufferPoint(Point3D p) {
//		if((int)p.x<0||(int)p.x>=width||(int)p.y<0||(int)p.y>=height)
//			return false;
		if((int)p.z<bufferMap[(int)p.x][(int)p.y]) {
			bufferMap[(int)p.x][(int)p.y] = (int)p.z;
			return true;
		}
		else return false;
	}
	
	private static void sortTriPoints(Point3D[] tri) {
		boolean OK = true;
		do {
			OK = true;
			for(int i=0; i<tri.length-1; i++) {
				if(tri[i].y<tri[i+1].y) {
					Point3D temp = tri[i];
					tri[i] = tri[i+1];
					tri[i+1] = temp;
					OK = false;
				}
			}
		}
		while(OK==false);
	}
}
