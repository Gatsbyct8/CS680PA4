//****************************************************************************
//       Example Main Program for CS480 PA1
//****************************************************************************
// Description: 
//   
//   This is a template program for the sketching tool.  
//
//     LEFTMOUSE: draw line segments 
//     RIGHTMOUSE: draw triangles 
//
//     The following keys control the program:
//
//		Q,q: quit 
//		C,c: clear polygon (set vertex count=0)
//		R,r: randomly change the color
//		S,s: toggle the smooth shading for triangle 
//			 (no smooth shading by default)
//		T,t: show testing examples
//		>:	 increase the step number for examples
//		<:   decrease the step number for examples
//
//****************************************************************************
// History :
//   Aug 2004 Created by Jianming Zhang based on the C
//   code by Stan Sclaroff
//   Nov 2014 modified to include test cases
//   Nov 5, 2019 Updated by Zezhou Sun
//


import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.*; 
import java.awt.image.*;
//import java.io.File;
//import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

//import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;//for new version of gl
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import com.jogamp.opengl.util.FPSAnimator;//for new version of gl


public class Lab_PA4 extends JFrame
	implements GLEventListener, KeyListener, MouseListener, MouseMotionListener
{
	
	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_WINDOW_WIDTH=512;
	public static final int DEFAULT_WINDOW_HEIGHT=512;
	private final float DEFAULT_LINE_WIDTH=1.0f;

	private GLCapabilities capabilities;
	private GLCanvas canvas;
	private FPSAnimator animator;

	private int numTestCase;
	private int testCase;
	private BufferedImage buff;
	@SuppressWarnings("unused")
	private ColorType color;
	private Random rng;
	
	 // specular exponent for materials
	private int ns=5; 
	
	private ArrayList<Point2D> lineSegs;
	private ArrayList<Point2D> triangles;
	private boolean doSmoothShading;
	private int Nsteps;

	/** The quaternion which controls the rotation of the world. */
    private Quaternion viewing_quaternion = new Quaternion();
    private Point3D viewing_center = new Point3D((float)(DEFAULT_WINDOW_WIDTH/2),(float)(DEFAULT_WINDOW_HEIGHT/2),(float)0.0);
    /** The last x and y coordinates of the mouse press. */
    private int last_x = 0, last_y = 0;
    /** Whether the world is being rotated. */
    private boolean rotate_world = false;
    
    /** Random colors **/
    private ColorType[] colorMap = new ColorType[100];
    private Random rand = new Random();
    public static boolean flat = false;
    public static boolean gouraud = false;
    public static boolean phong = true;
    public static boolean pointLight = true;
	public static boolean infiniteLight = false;
	public static boolean ambientLight = true;
	public static boolean attenuation = false;
	public static boolean specular = true;
	public static boolean diffuse = true;
	public static int dx = 0;
	public static int dy = 0;
    
	public Lab_PA4()
	{
	    capabilities = new GLCapabilities(null);
	    capabilities.setDoubleBuffered(true);  // Enable Double buffering

	    canvas  = new GLCanvas(capabilities);
	    canvas.addGLEventListener(this);
	    canvas.addMouseListener(this);
	    canvas.addMouseMotionListener(this);
	    canvas.addKeyListener(this);
	    canvas.setAutoSwapBufferMode(true); // true by default. Just to be explicit
	    canvas.setFocusable(true);
	    getContentPane().add(canvas);

	    animator = new FPSAnimator(canvas, 60); // drive the display loop @ 60 FPS

	    numTestCase = 2;
	    testCase = 0;
	    Nsteps = 12;

	    setTitle("CS480/680 Lab for PA4");
	    setSize( DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true);
	    setResizable(false);
	    
	    rng = new Random();
	    color = new ColorType(1.0f,0.0f,0.0f);
	    lineSegs = new ArrayList<Point2D>();
	    triangles = new ArrayList<Point2D>();
	    doSmoothShading = false;
	    
	    for (int i=0; i<100; i++) {
	    	this.colorMap[i] = new ColorType(i*0.005f+0.5f, i*-0.005f+1f, i*0.0025f+0.75f);
	    }
	}

	public void run()
	{
		animator.start();
	}

	public static void main( String[] args )
	{
		Lab_PA4 P = new Lab_PA4();
	    P.run();
	}

	//*********************************************** 
	//  GLEventListener Interfaces
	//*********************************************** 
	public void init( GLAutoDrawable drawable) 
	{
	    GL gl = drawable.getGL();
	    gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f);
	    gl.glLineWidth( DEFAULT_LINE_WIDTH );
	    Dimension sz = this.getContentPane().getSize();
	    buff = new BufferedImage(sz.width,sz.height,BufferedImage.TYPE_3BYTE_BGR);
	    clearPixelBuffer();
	}

	// Redisplaying graphics
	public void display(GLAutoDrawable drawable)
	{
	    GL2 gl = drawable.getGL().getGL2();
	    WritableRaster wr = buff.getRaster();
	    DataBufferByte dbb = (DataBufferByte) wr.getDataBuffer();
	    byte[] data = dbb.getData();

	    gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
	    gl.glDrawPixels (buff.getWidth(), buff.getHeight(),
                GL2.GL_BGR, GL2.GL_UNSIGNED_BYTE,
                ByteBuffer.wrap(data));
        drawTestCase();
	}

	// Window size change
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		// deliberately left blank
	}
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
	      boolean deviceChanged)
	{
		// deliberately left blank
	}
	
	void clearPixelBuffer()
	{
		lineSegs.clear();
    	triangles.clear();
		Graphics2D g = buff.createGraphics();
	    g.setColor(Color.BLACK);
	    g.fillRect(0, 0, buff.getWidth(), buff.getHeight());
	    g.dispose();
	}
	
	// drawTest
	void drawTestCase()
	{  
		/* clear the window and vertex state */
		clearPixelBuffer();
	  
		//System.out.printf("Test case = %d\n",testCase);

		switch (testCase){
		case 0:
			shadeTest(true); /* smooth shaded, sphere and torus */
			break;
		case 1:
			shadeTest(false); /* flat shaded, sphere and torus */
			break;
		}	
	}


	//*********************************************** 
	//          KeyListener Interfaces
	//*********************************************** 
	public void keyTyped(KeyEvent key)
	{
	//      Q,q: quit 
	//      C,c: clear polygon (set vertex count=0)
	//		R,r: randomly change the color
	//		S,s: toggle the smooth shading
	//		T,t: show testing examples (toggles between smooth shading and flat shading test cases)
	//		>:	 increase the step number for examples
	//		<:   decrease the step number for examples
	//     +,-:  increase or decrease spectral exponent

	    switch ( key.getKeyChar() ) 
	    {
	    case 'Q' :
	    case 'q' : 
	    	new Thread()
	    	{
	          	public void run() { animator.stop(); }
	        }.start();
	        System.exit(0);
	        break;
	    case 'R' :
	    case 'r' :
	    	color = new ColorType(rng.nextFloat(),rng.nextFloat(),
	    			rng.nextFloat());
	    	break;
	    case 'C' :
	    case 'c' :
	    	clearPixelBuffer();
	    	break;
	    case 'S' :
	    	specular = !specular;
	    	break;
	    case 's' :
	    	doSmoothShading = !doSmoothShading; // This is a placeholder (implemented in 'T')
	    	break;
			case '1':
			case 'a':
			case 'A':
				ambientLight = !ambientLight;
				break;
			case '2':
				infiniteLight = !infiniteLight;
				break;
			case '3':
				pointLight = !pointLight;
				break;
			case '4':
				attenuation = !attenuation;
				break;
			case 'f':
			case 'F':
				flat = true;
				gouraud = false;
				phong = false;
				break;
			case 'g':
			case 'G':
				flat = false;
				gouraud = true;
				phong = false;
				break;
			case 'p':
			case 'P':
				flat = false;
				gouraud = false;
				phong = true;
				break;
			case 'd':
			case 'D':
				diffuse = !diffuse;
	    case 'T' :
	    case 't' : 
	    	testCase = (testCase+1)%numTestCase;
	    	drawTestCase();
	        break; 
	    case '<':  
	        Nsteps = Nsteps < 4 ? Nsteps: Nsteps / 2;
	        System.out.printf( "Nsteps = %d \n", Nsteps);
	        drawTestCase();
	        break;
	    case '>':
	        Nsteps = Nsteps > 190 ? Nsteps: Nsteps * 2;
	        System.out.printf( "Nsteps = %d \n", Nsteps);
	        drawTestCase();
	        break;
	    case '+':
	    	ns++;
	        drawTestCase();
	    	break;
	    case '-':
	    	if(ns>0)
	    		ns--;
	        drawTestCase();
	    	break;
			case '[':
				dx--;
				break;
			case ']':
				dx++;
				break;
			case '(':
				dy--;
				break;
			case ')':
				dy++;
				break;
	    default :
	        break;
	    }
	}

	public void keyPressed(KeyEvent key)
	{
	    switch (key.getKeyCode()) 
	    {
	    case KeyEvent.VK_ESCAPE:
	    	new Thread()
	        {
	    		public void run()
	    		{
	    			animator.stop();
	    		}
	        }.start();
	        System.exit(0);
	        break;
	      default:
	        break;
	    }
	}

	public void keyReleased(KeyEvent key)
	{
		// deliberately left blank
	}

	//************************************************** 
	// MouseListener and MouseMotionListener Interfaces
	//************************************************** 
	public void mouseClicked(MouseEvent mouse)
	{
		// deliberately left blank
	}
	  public void mousePressed(MouseEvent mouse)
	  {
	    int button = mouse.getButton();
	    if ( button == MouseEvent.BUTTON1 )
	    {
	      last_x = mouse.getX();
	      last_y = mouse.getY();
	      rotate_world = true;
	    }
	  }

	  public void mouseReleased(MouseEvent mouse)
	  {
	    int button = mouse.getButton();
	    if ( button == MouseEvent.BUTTON1 )
	    {
	      rotate_world = false;
	    }
	  }

	public void mouseMoved( MouseEvent mouse)
	{
		// Deliberately left blank
	}

	/**
	   * Updates the rotation quaternion as the mouse is dragged.
	   * 
	   * @param mouse
	   *          The mouse drag event object.
	   */
	  public void mouseDragged(final MouseEvent mouse) {
	    if (this.rotate_world) {
	      // get the current position of the mouse
	      final int x = mouse.getX();
	      final int y = mouse.getY();

	      // get the change in position from the previous one
	      final int dx = x - this.last_x;
	      final int dy = y - this.last_y;

	      // create a unit vector in the direction of the vector (dy, dx, 0)
	      final float magnitude = (float)Math.sqrt(dx * dx + dy * dy);
	      if(magnitude > 0.0001)
	      {
	    	  // define axis perpendicular to (dx,-dy,0)
	    	  // use -y because origin is in upper lefthand corner of the window
	    	  final float[] axis = new float[] { -(float) (dy / magnitude),
	    			  (float) (dx / magnitude), 0 };

	    	  // calculate appropriate quaternion
	    	  final float viewing_delta = 3.1415927f / 180.0f;
	    	  final float s = (float) Math.sin(0.5f * viewing_delta);
	    	  final float c = (float) Math.cos(0.5f * viewing_delta);
	    	  final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s
	    			  * axis[2]);
	    	  this.viewing_quaternion = Q.multiply(this.viewing_quaternion);

	    	  // normalize to counteract acccumulating round-off error
	    	  this.viewing_quaternion.normalize();

	    	  // save x, y as last x, y
	    	  this.last_x = x;
	    	  this.last_y = y;
	          drawTestCase();
	      }
	    }

	  }
	  
	public void mouseEntered( MouseEvent mouse)
	{
		// Deliberately left blank
	}

	public void mouseExited( MouseEvent mouse)
	{
		// Deliberately left blank
	} 


	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}
	
	//************************************************** 
	// Test Cases
	// Nov 9, 2014 Stan Sclaroff -- removed line and triangle test cases
	//************************************************** 

	void shadeTest(boolean doSmooth){
		// the simple example scene includes one sphere and one torus
		float radius = (float)25.0;
        Sphere3D sphere = new Sphere3D((float)128.0, (float)128.0, (float)128.0, (float)1.5*radius, Nsteps, Nsteps);
        Material mat_sphere = new Material(new ColorType(0f, 0f, 0f), new ColorType(1f,0f,0f), new ColorType(1f,1f,1f),1);
        InfiniteLight infiniteLight = new InfiniteLight(new ColorType(1f,1f,1f), new Point3D(1f,1f,1f),45, 1, 0.000001f, 0.000001f);
        PointLight pointLight = new PointLight(new ColorType(1f,1f,1f), new Point3D(1f,1f,1f),new Point3D(128f,128f,900f),45, 1, 0.000001f, 0.000001f);
        AmbientLight ambientLight = new AmbientLight(new ColorType(1f,1f,1f), new Point3D(1f,1f,1f));
        Ellipsoid3D ellipsoid = new Ellipsoid3D((float)128.0, (float)128.0, (float)128.0, (float)2*radius, (float)2*radius, (float)2*radius, Nsteps, Nsteps);
        Cylinder3D cylinder3D = new Cylinder3D((float)128.0, (float)258.0, (float)128.0, (float)2*radius, (float)2*radius, 98f, 158f, Nsteps, Nsteps);
        Box3D box3D = new Box3D((float)258.0, (float)258.0, (float)128.0, (float)2*radius, (float)2*radius, 98f, 158f, Nsteps, Nsteps);
        Torus3D torus3D = new Torus3D((float)288.0, (float)128.0, (float)128.0, (float)2*radius, (float)radius,  Nsteps, Nsteps);
        Superellipsoid superellipsoid = new Superellipsoid((float)320,(float)320,(float)128,2*radius,2*radius,2*radius,Nsteps,Nsteps);
        // view vector is defined along z axis
        // this example assumes simple othorgraphic projection
        // view vector is used in 
        //   (a) calculating specular lighting contribution
        //   (b) backface culling / backface rejection
        Point3D view_vector = new Point3D((float)0.0,(float)0.0,(float)1.0);
        
        // normal to the plane of a triangle
        // to be used in backface culling / backface rejection
        Point3D triangle_normal = new Point3D();
        
        // a triangle mesh
        Mesh3D mesh;
            
		int i, j, n, m;
		
		// temporary variables for triangle 3D vertices and 3D normals
		Point3D v0,v1, v2, n0, n1, n2;
		
		// projected triangle, with vertex colors
		Point2D[] tri = {new Point2D(), new Point2D(), new Point2D()};


		mesh=sphere.mesh;
		n=sphere.get_n();
		m=sphere.get_m();

		DepthBuffer.initBufferMap(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
		ellipsoid.draw(viewing_quaternion, viewing_center, tri, view_vector, triangle_normal, buff, doSmooth, infiniteLight, mat_sphere, pointLight, ambientLight);
		cylinder3D.draw(viewing_quaternion, viewing_center, tri, view_vector, triangle_normal, buff, doSmooth, infiniteLight, mat_sphere, pointLight, ambientLight);
		box3D.draw(viewing_quaternion, viewing_center, tri, view_vector, triangle_normal, buff, doSmooth, infiniteLight, mat_sphere, pointLight, ambientLight);
		torus3D.draw(viewing_quaternion, viewing_center, tri, view_vector, triangle_normal, buff, doSmooth, infiniteLight, mat_sphere, pointLight, ambientLight);
		superellipsoid.draw(viewing_quaternion, viewing_center, tri, view_vector, triangle_normal, buff, doSmooth, infiniteLight, mat_sphere, pointLight, ambientLight);
		DepthBuffer.initBufferMap();
		
		
		
		// rotate the surface's 3D mesh using quaternion
//		mesh.rotateMesh(viewing_quaternion, viewing_center);
//
//		// draw triangles for the current surface, using vertex colors
//		for(i=0; i < m-1; ++i)
//	    {
//			for(j=0; j < n-1; ++j)
//			{
//				// ****************Implement Code here*******************//
//				v0 = mesh.v[i][j];
//				v1 = mesh.v[i+1][j];
//				v2 = mesh.v[i+1][j+1];
//
//				triangle_normal = computeTriangleNormal(v0,v1,v2);
//
//				if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
//				{
//					// flat shading: use the normal to the triangle itself
//					n2 = n1 = n0 =  triangle_normal;
//					//tri[2].c = tri[1].c = tri[0].c = new ColorType(1.0f, 0.0f, 0.0f);
//					tri[2].c = tri[1].c = tri[0].c = infiniteLight.applyLight(mat_sphere, view_vector, triangle_normal);
//
//					tri[0].x = (int)v0.x;
//					tri[0].y = (int)v0.y;
//					tri[1].x = (int)v1.x;
//					tri[1].y = (int)v1.y;
//					tri[2].x = (int)v2.x;
//					tri[2].y = (int)v2.y;
//
//					SketchBase.drawTriangle(buff, tri[0], tri[1], tri[2], doSmooth);
//				}
//
//				// ****************Implement Code here*******************//
//				v0 = mesh.v[i][j];
//				v1 = mesh.v[i+1][j+1];
//				v2 = mesh.v[i][j+1];
//
//				triangle_normal = computeTriangleNormal(v0,v1,v2);
//
//				if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
//				{
//					// flat shading: use the normal to the triangle itself
//					n2 = n1 = n0 =  triangle_normal;
//					//tri[2].c = tri[1].c = tri[0].c = new ColorType(0.0f, 1.0f, 0.0f);
//					tri[2].c = tri[1].c = tri[0].c = infiniteLight.applyLight(mat_sphere, view_vector, triangle_normal);
//
//					tri[0].x = (int)v0.x;
//					tri[0].y = (int)v0.y;
//					tri[1].x = (int)v1.x;
//					tri[1].y = (int)v1.y;
//					tri[2].x = (int)v2.x;
//					tri[2].y = (int)v2.y;
//
//					SketchBase.drawTriangle(buff, tri[0], tri[1], tri[2], doSmooth);
//				}
//			}
//	    }
	}

	// helper method that computes the unit normal to the plane of the triangle
	// degenerate triangles yield normal that is numerically zero
	public static Point3D computeTriangleNormal(Point3D v0, Point3D v1, Point3D v2)
	{
		Point3D e0 = v1.minus(v2);
		Point3D e1 = v0.minus(v2);
		Point3D norm = e0.crossProduct(e1);
		
		if(norm.magnitude()>0.000001)
			norm.normalize();
		else 	// detect degenerate triangle and set its normal to zero
			norm.set((float)0.0,(float)0.0,(float)0.0);

		return norm;
	}

}