//****************************************************************************
//      Sphere class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//

import java.awt.image.BufferedImage;

public class Box3D
{
	private Point3D center;
	private float rx;
	private float ry;
	private float zMin;
	private float zMax;
	private int stacks,slices;
	public Mesh3D mesh;
	public Mesh3D endcapmesh;

	public Box3D(float _x, float _y, float _z, float _rx, float _ry, float zMin, float zMax, int _stacks, int _slices)
	{
		center = new Point3D(_x,_y,_z);
		rx = _rx;
		ry = _ry;
		this.zMin = zMin;
		this.zMax = zMax;
		stacks = 5;
		slices = _slices;
		initMesh();
	}
	
	public void set_center(float _x, float _y, float _z)
	{
		center.x=_x;
		center.y=_y;
		center.z=_z;
		fillMesh();  // update the triangle mesh
	}
	
	public void set_radius(float _rx, float _ry)
	{
		rx = _rx;
		ry = _ry;
		fillMesh(); // update the triangle mesh
	}
	
	public void set_stacks(int _stacks)
	{
		stacks = _stacks;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public void set_slices(int _slices)
	{
		slices = _slices;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public int get_n()
	{
		return slices;
	}
	
	public int get_m()
	{
		return stacks;
	}

	private void initMesh()
	{
		mesh = new Mesh3D(stacks,slices);
		//endcapmesh = new Mesh3D(stacks + 1, 2);
		fillMesh();  // set the mesh vertices and normals
	}
		
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	private void fillMesh()
	{
		// ****************Implement Code here*******************//
		stacks = 5;
		double pi = Math.PI;
		double u;
		double theta;
		//double dphi = 2 * pi/(stacks - 1);
		double dtheta = 2*pi/(stacks - 1);
		double du = Math.abs(zMax - zMin) / (slices - 1);
		int i;
		int j;

		for(i=0,theta=-(float)Math.PI;i<stacks;++i,theta += dtheta)
		{
			float cos_theta=(float)Math.cos(theta);
			float sin_theta=(float)Math.sin(theta);

			for(j=0,u=zMin;j<slices;++j,u += du)
			{
				// vertex location
				mesh.v[i][j].x = center.x + rx * (float)(cos_theta);
				mesh.v[i][j].y = center.y + ry * (float)(sin_theta);
				mesh.v[i][j].z = (float)u;

				// Compute unit normal at vertex
				mesh.n[i][j].x = -(float)(cos_theta);
				mesh.n[i][j].y = -(float)(sin_theta);
				mesh.n[i][j].z = (float)0.0;
			}
		}
		for (i = 0, theta = -pi; i < stacks; ++i, theta += dtheta){
			float cos_theta=(float)Math.cos(theta);
			float sin_theta=(float)Math.sin(theta);

			//rear endcap
			mesh.n[i][0] = new Point3D(0,0,1);
			mesh.n[i][1] = new Point3D(0,0,1);
			mesh.n[i][0].normalize();
			//mesh.v[i][0] = new Point3D(center.x,center.y,center.z - (float)rx);
			mesh.v[i][0] = new Point3D(center.x,center.y,zMin );

			//front endcap
			mesh.n[i][slices-1] = new Point3D(0,0,-1);
			mesh.n[i][slices-2] = new Point3D(0,0,-1);
			mesh.n[i][slices-1].normalize();
			//mesh.v[i][slices-1] = new Point3D(center.x,center.y,center.z + (float)rx);
			mesh.v[i][slices-1] = new Point3D(center.x,center.y,zMax);
		}
	}

	public void draw(Quaternion viewing_quaternion, Point3D viewing_center, Point2D[] tri, Point3D view_vector, Point3D triangle_normal, BufferedImage buff, boolean doSmooth, InfiniteLight infiniteLight, Material mat_sphere, PointLight pointLight, AmbientLight ambientLight){
		//DepthBuffer.initBufferMap(Lab_PA4.DEFAULT_WINDOW_WIDTH, Lab_PA4.DEFAULT_WINDOW_HEIGHT);
		Point3D v0,v1, v2, n0, n1, n2;
		int i, j, n, m;
		//mesh=this.mesh;
		n=this.get_n();
		m=this.get_m();



		// rotate the surface's 3D mesh using quaternion
		mesh.rotateMesh(viewing_quaternion, viewing_center);

		// draw triangles for the current surface, using vertex colors
		for(i=0; i < m-1; ++i)
		{
			for(j=0; j < n-1; ++j)
			{
				// ****************Implement Code here*******************//
				v0 = mesh.v[i][j];
				v1 = mesh.v[i+1][j];
				v2 = mesh.v[i+1][j+1];

				triangle_normal = Lab_PA4.computeTriangleNormal(v0,v1,v2);

				if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
				{
					// flat shading: use the normal to the triangle itself
					if (Lab_PA4.flat){
						n2 = n1 = n0 =  triangle_normal;
						Point3D center = new Point3D((v0.x+v1.x+v2.x)/3,
								(v0.y+v1.y+v2.y)/3,
								(v0.z+v1.z+v2.z/3));
						tri[2].c = tri[1].c = tri[0].c =lightUtil.applyLight(ambientLight, infiniteLight, pointLight, mat_sphere, view_vector, triangle_normal, center);
					}else if (Lab_PA4.gouraud){
						n0 = mesh.n[i][j];
						n1 = mesh.n[i+1][j];
						n2 = mesh.n[i+1][j+1];
						tri[0].c = lightUtil.applyLight(ambientLight, infiniteLight, pointLight,mat_sphere, view_vector, n0, v0);
						tri[1].c = lightUtil.applyLight(ambientLight, infiniteLight, pointLight,mat_sphere, view_vector, n1, v1);
						tri[2].c = lightUtil.applyLight(ambientLight, infiniteLight, pointLight,mat_sphere, view_vector, n2, v2);
					}else{
						n0 = mesh.n[i][j];
						n1 = mesh.n[i+1][j];
						n2 = mesh.n[i+1][j+1];
					}

					tri[0].x = (int)v0.x;
					tri[0].y = (int)v0.y;
					tri[1].x = (int)v1.x;
					tri[1].y = (int)v1.y;
					tri[2].x = (int)v2.x;
					tri[2].y = (int)v2.y;

					//SketchBase.drawTriangle(buff, tri[0], tri[1], tri[2], doSmooth);
					if (Lab_PA4.phong){
						SketchBase.drawTriangleWithPhong(buff, v0, v1, v2, n0, n1, n2, mat_sphere, view_vector, doSmooth, ambientLight, infiniteLight, pointLight);
					}else {
						SketchBase.drawTriangle3DWithDepthBuffer(buff, v0, v1, v2, tri[0].c, tri[1].c, tri[2].c, doSmooth);
					}
				}

				// ****************Implement Code here*******************//
				v0 = mesh.v[i][j];
				v1 = mesh.v[i+1][j+1];
				v2 = mesh.v[i][j+1];

				triangle_normal = Lab_PA4.computeTriangleNormal(v0,v1,v2);

				if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
				{
					// flat shading: use the normal to the triangle itself
					//tri[2].c = tri[1].c = tri[0].c = new ColorType(0.0f, 1.0f, 0.0f);
					//tri[2].c = tri[1].c = tri[0].c = infiniteLight.applyLight(mat_sphere, view_vector, triangle_normal);
					if (Lab_PA4.flat){
						n2 = n1 = n0 =  triangle_normal;
						Point3D center = new Point3D((v0.x+v1.x+v2.x)/3,
								(v0.y+v1.y+v2.y)/3,
								(v0.z+v1.z+v2.z/3));
						tri[2].c = tri[1].c = tri[0].c =lightUtil.applyLight(ambientLight, infiniteLight, pointLight, mat_sphere, view_vector, triangle_normal, center);
					}else if (Lab_PA4.gouraud){
						n0 = mesh.n[i][j];
						n1 = mesh.n[i+1][j+1];
						n2 = mesh.n[i][j+1];
						tri[0].c = lightUtil.applyLight(ambientLight, infiniteLight, pointLight,mat_sphere, view_vector, n0, v0);
						tri[1].c = lightUtil.applyLight(ambientLight, infiniteLight, pointLight,mat_sphere, view_vector, n1, v1);
						tri[2].c = lightUtil.applyLight(ambientLight, infiniteLight, pointLight,mat_sphere, view_vector, n2, v2);
					}else{
						n0 = mesh.n[i][j];
						n1 = mesh.n[i+1][j+1];
						n2 = mesh.n[i][j+1];
					}
//					tri[0].c = pointLight.applyLight(mat_sphere, view_vector, triangle_normal, center);
//					tri[1].c = pointLight.applyLight(mat_sphere, view_vector, triangle_normal, center);
//					tri[2].c = pointLight.applyLight(mat_sphere, view_vector, triangle_normal, center);
					//tri[2].c = tri[1].c = tri[0].c = ambientLight.applyLight(mat_sphere, view_vector, triangle_normal);

					tri[0].x = (int)v0.x;
					tri[0].y = (int)v0.y;
					tri[1].x = (int)v1.x;
					tri[1].y = (int)v1.y;
					tri[2].x = (int)v2.x;
					tri[2].y = (int)v2.y;

					//SketchBase.drawTriangle(buff, tri[0], tri[1], tri[2], doSmooth);
					if (Lab_PA4.phong){
						SketchBase.drawTriangleWithPhong(buff, v0, v1, v2, n0, n1, n2, mat_sphere, view_vector, doSmooth, ambientLight, infiniteLight, pointLight);
					}else {
						SketchBase.drawTriangle3DWithDepthBuffer(buff, v0, v1, v2, tri[0].c, tri[1].c, tri[2].c, doSmooth);
					}
				}
			}
		}
		//DepthBuffer.initBufferMap();
	}
}