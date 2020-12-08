import java.awt.*;
import java.awt.image.BufferedImage;

public class Superellipsoid
{
	private Point3D center;
	private float rx, ry, rz;
	private int m,n;
	public Mesh3D mesh;
	
	public Superellipsoid(float _x, float _y, float _z, float _rx, float _ry, float _rz, int _m, int _n)
	{
		center = new Point3D(_x,_y,_z);
		rx = _rx;
		ry = _ry;
		rz = _rz;
		
		m = _m;
		n = _n;
		initMesh();
	}
	
	public void set_center(float _x, float _y, float _z)
	{
		center.x=_x;
		center.y=_y;
		center.z=_z;
		fillMesh();  // update the triangle mesh
	}
	
	public void set_radius(float _rx, float _ry, float _rz)
	{
		rx = _rx;
		ry = _ry;
		rz = _rz;
		fillMesh(); // update the triangle mesh
	}
	
	public void set_m(int _m)
	{
		m = _m;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public void set_n(int _n)
	{
		n = _n;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public int get_n()
	{
		return n;
	}
	
	public int get_m()
	{
		return m;
	}

	private void initMesh()
	{
		mesh = new Mesh3D(m,n);
		fillMesh();  // set the mesh vertices and normals
	}
		
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	private void fillMesh()
	{
		int i,j;		
		float theta, phi;
		float d_theta=(float)(2.0*Math.PI)/ ((float)(m-1));
		float d_phi=(float)Math.PI / ((float)n-1);
		float c_theta,s_theta;
		float c_phi, s_phi;
		
		for(i=0,theta=-(float)Math.PI;i<m;++i,theta += d_theta)
	    {
			c_theta=(float)Math.cos(theta);
			s_theta=(float)Math.sin(theta);
			
			for(j=0,phi=(float)(-0.5*Math.PI);j<n;++j,phi += d_phi)
			{
				// vertex location
				c_phi = (float)Math.cos(phi);
				s_phi = (float)Math.sin(phi);
				double absU = Math.abs(c_phi);
				double absV = Math.abs(c_theta);
				double absSV = Math.abs(s_theta);
				double absSU = Math.abs(s_phi);
				mesh.v[i][j].x=center.x+rx*Math.signum(c_phi)*((float) Math.pow(absU, 0.1))*Math.signum(c_theta)*((float) Math.pow(absV, 3));
				mesh.v[i][j].y=center.y+ry*Math.signum(c_phi)*((float) Math.pow(absU, 0.1))*Math.signum(s_theta)*((float) Math.pow(absSV, 3));
				mesh.v[i][j].z=center.z+rz*Math.signum(s_phi) * ((float) Math.pow(absSU, 0.1));
				
				// used same normal as sphere
				mesh.n[i][j].x = -c_phi*c_theta;
				mesh.n[i][j].y = -c_phi*s_theta;
				mesh.n[i][j].z= -s_phi;
				
				mesh.n[i][0] =  new Point3D(0,0,1);
				mesh.n[i][n-1] =  new Point3D(0,0,-1);
				mesh.v[i][0] = new Point3D(center.x,center.y,center.z - rx);
				mesh.v[i][n-1] = new Point3D(center.x,center.y,center.z + rx);
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
