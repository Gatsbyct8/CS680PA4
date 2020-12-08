//****************************************************************************
//      Sphere class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//

import java.awt.*;
import java.awt.image.BufferedImage;

public class Torus3D
{
	private Point3D center;
	private float r1;
	private float r2;
	private int stacks,slices;
	public Mesh3D mesh;
	public Mesh3D endcapmesh;

	public Torus3D(float _x, float _y, float _z, float _r1, float _r2, int _stacks, int _slices)
	{
		center = new Point3D(_x,_y,_z);
		r1 = _r1;
		r2 = _r2;
		stacks = _stacks;
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
	
	public void set_radius(float _rMin, float _r)
	{
		r1 = _rMin;
		r2 = _r;
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
		int i, j;
		float theta, phi;
		float d_theta = (float)(2*Math.PI)/(float)(stacks-1);
		float d_phi = (float)(2*Math.PI)/(float)(slices-1);
		float cos_theta, sin_theta;
		float cos_phi, sin_phi;
		Point3D du = new Point3D();
		Point3D dv = new Point3D();

		for(i=0, theta=(float)(-Math.PI); i<stacks; i++, theta+=d_theta) {
			cos_theta = (float)Math.cos(theta);
			sin_theta = (float)Math.sin(theta);

			for(j=0, phi=(float)(-Math.PI); j<slices; j++, phi+=d_phi) {
				cos_phi = (float)Math.cos(phi);
				sin_phi = (float)Math.sin(phi);

				mesh.v[i][j].x = center.x+(r1+r2*cos_phi)*cos_theta;
				mesh.v[i][j].y = center.y+(r1+r2*cos_phi)*sin_theta;
				mesh.v[i][j].z = center.z+r2*sin_phi;

				// normals
				du.x = (-r1+r2*cos_phi)*sin_theta;
				du.y = (r1+r2*cos_phi)*cos_theta;
				du.z = 0;

				dv.x = r2*sin_phi*cos_theta;
				dv.y = r2*sin_phi*sin_theta;
				dv.z = -r2*cos_phi;

				du.crossProduct(dv, mesh.n[i][j]);
				mesh.n[i][j].normalize();
			}
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
				v0 = mesh.v[i][j].plus(new Point3D(Lab_PA4.dx,Lab_PA4.dy,0));
				v1 = mesh.v[i+1][j].plus(new Point3D(Lab_PA4.dx,Lab_PA4.dy,0));
				v2 = mesh.v[i+1][j+1].plus(new Point3D(Lab_PA4.dx,Lab_PA4.dy,0));

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
				v0 = mesh.v[i][j].plus(new Point3D(Lab_PA4.dx,Lab_PA4.dy,0));
				v1 = mesh.v[i+1][j+1].plus(new Point3D(Lab_PA4.dx,Lab_PA4.dy,0));
				v2 = mesh.v[i][j+1].plus(new Point3D(Lab_PA4.dx,Lab_PA4.dy,0));

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