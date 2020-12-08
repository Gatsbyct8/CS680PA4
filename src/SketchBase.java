//****************************************************************************
// SketchBase.  
//****************************************************************************
// Comments : 
//   Subroutines to manage and draw points, lines an triangles
//
// History :
//   Aug 2014 Created by Jianming Zhang (jimmie33@gmail.com) based on code by
//   Stan Sclaroff (from CS480 '06 poly.c)

import java.awt.image.BufferedImage;
import java.util.*;

public class SketchBase 
{
	public SketchBase()
	{
		// deliberately left blank
	}
	
	/**********************************************************************
	 * Draws a point.
	 * This is achieved by changing the color of the buffer at the location
	 * corresponding to the point. 
	 * 
	 * @param buff
	 *          Buffer object.
	 * @param p
	 *          Point to be drawn.
	 */
	public static void drawPoint(BufferedImage buff, Point2D p)
	{
		if(p.x>=0 && p.x<buff.getWidth() && p.y>=0 && p.y < buff.getHeight())
			buff.setRGB(p.x, buff.getHeight()-p.y-1, p.c.getRGB_int());	
	}
	
	/**********************************************************************
	 * Draws a line segment using Bresenham's algorithm, linearly 
	 * interpolating RGB color along line segment.
	 * This method only uses integer arithmetic.
	 * 
	 * @param buff
	 *          Buffer object.
	 * @param p1
	 *          First given endpoint of the line.
	 * @param p2
	 *          Second given endpoint of the line.
	 */
	public static void drawLine(BufferedImage buff, Point2D p1, Point2D p2)
	{
	    int x0=p1.x, y0=p1.y;
	    int xEnd=p2.x, yEnd=p2.y;
	    int dx = Math.abs(xEnd - x0),  dy = Math.abs(yEnd - y0);

	    if(dx==0 && dy==0)
	    {
	    	drawPoint(buff,p1);
	    	return;
	    }
	    
	    // if slope is greater than 1, then swap the role of x and y
	    boolean x_y_role_swapped = (dy > dx); 
	    if(x_y_role_swapped)
	    {
	    	x0=p1.y; 
	    	y0=p1.x;
	    	xEnd=p2.y; 
	    	yEnd=p2.x;
	    	dx = Math.abs(xEnd - x0);
	    	dy = Math.abs(yEnd - y0);
	    }
	    
	    // initialize the decision parameter and increments
	    int p = 2 * dy - dx;
	    int twoDy = 2 * dy,  twoDyMinusDx = 2 * (dy - dx);
	    int x=x0, y=y0;
	    
	    // set step increment to be positive or negative
	    int step_x = x0<xEnd ? 1 : -1;
	    int step_y = y0<yEnd ? 1 : -1;
	    
	    // deal with setup for color interpolation
	    // first get r,g,b integer values at the end points
	    int r0=p1.c.getR_int(), rEnd=p2.c.getR_int();
	    int g0=p1.c.getG_int(), gEnd=p2.c.getG_int();
	    int b0=p1.c.getB_int(), bEnd=p2.c.getB_int();
	    
	    // compute the change in r,g,b 
	    int dr=Math.abs(rEnd-r0), dg=Math.abs(gEnd-g0), db=Math.abs(bEnd-b0);
	    
	    // set step increment to be positive or negative 
	    int step_r = r0<rEnd ? 1 : -1;
	    int step_g = g0<gEnd ? 1 : -1;
	    int step_b = b0<bEnd ? 1 : -1;
	    
	    // compute whole step in each color that is taken each time through loop
	    int whole_step_r = step_r*(dr/dx);
	    int whole_step_g = step_g*(dg/dx);
	    int whole_step_b = step_b*(db/dx);
	    
	    // compute remainder, which will be corrected depending on decision parameter
	    dr=dr%dx;
	    dg=dg%dx; 
	    db=db%dx;
	    
	    // initialize decision parameters for red, green, and blue
	    int p_r = 2 * dr - dx;
	    int twoDr = 2 * dr,  twoDrMinusDx = 2 * (dr - dx);
	    int r=r0;
	    
	    int p_g = 2 * dg - dx;
	    int twoDg = 2 * dg,  twoDgMinusDx = 2 * (dg - dx);
	    int g=g0;
	    
	    int p_b = 2 * db - dx;
	    int twoDb = 2 * db,  twoDbMinusDx = 2 * (db - dx);
	    int b=b0;
	    
	    // draw start pixel
	    if(x_y_role_swapped)
	    {
	    	if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth())
	    		buff.setRGB(y, buff.getHeight()-x-1, (r<<16) | (g<<8) | b);
	    }
	    else
	    {
	    	if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth())
	    		buff.setRGB(x, buff.getHeight()-y-1, (r<<16) | (g<<8) | b);
	    }
	    
	    while (x != xEnd) 
	    {
	    	// increment x and y
	    	x+=step_x;
	    	if (p < 0)
	    		p += twoDy;
	    	else 
	    	{
	    		y+=step_y;
	    		p += twoDyMinusDx;
	    	}
		        
	    	// increment r by whole amount slope_r, and correct for accumulated error if needed
	    	r+=whole_step_r;
	    	if (p_r < 0)
	    		p_r += twoDr;
	    	else 
	    	{
	    		r+=step_r;
	    		p_r += twoDrMinusDx;
	    	}
		    
	    	// increment g by whole amount slope_b, and correct for accumulated error if needed  
	    	g+=whole_step_g;
	    	if (p_g < 0)
	    		p_g += twoDg;
	    	else 
	    	{
	    		g+=step_g;
	    		p_g += twoDgMinusDx;
	    	}
		    
	    	// increment b by whole amount slope_b, and correct for accumulated error if needed
	    	b+=whole_step_b;
	    	if (p_b < 0)
	    		p_b += twoDb;
	    	else 
	    	{
	    		b+=step_b;
	    		p_b += twoDbMinusDx;
	    	}
		    
	    	if(x_y_role_swapped)
	    	{
	    		if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth())
	    			buff.setRGB(y, buff.getHeight()-x-1, (r<<16) | (g<<8) | b);
	    	}
	    	else
	    	{
	    		if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth())
	    			buff.setRGB(x, buff.getHeight()-y-1, (r<<16) | (g<<8) | b);
	    	}
	    }
	}

	/**********************************************************************
	 * Draws a filled triangle. 
	 * The triangle may be filled using flat fill or smooth fill. 
	 * This routine fills columns of pixels within the left-hand part, 
	 * and then the right-hand part of the triangle.
	 *   
	 *	                         *
	 *	                        /|\
	 *	                       / | \
	 *	                      /  |  \
	 *	                     *---|---*
	 *	            left-hand       right-hand
	 *	              part             part
	 *
	 * @param buff
	 *          Buffer object.
	 * @param p1
	 *          First given vertex of the triangle.
	 * @param p2
	 *          Second given vertex of the triangle.
	 * @param p3
	 *          Third given vertex of the triangle.
	 * @param do_smooth
	 *          Flag indicating whether flat fill or smooth fill should be used.                   
	 */
	public static void drawTriangle(BufferedImage buff, Point2D p1, Point2D p2, Point2D p3, boolean do_smooth)
	{
	    // sort the triangle vertices by ascending x value
	    Point2D p[] = sortTriangleVerts(p1,p2,p3);
	    
	    int x; 
	    float y_a, y_b;
	    float dy_a, dy_b;
	    float dr_a=0, dg_a=0, db_a=0, dr_b=0, dg_b=0, db_b=0;
	    
	    Point2D side_a = new Point2D(p[0]), side_b = new Point2D(p[0]);
	    
	    if(!do_smooth)
	    {
	    	side_a.c = new ColorType(p1.c);
	    	side_b.c = new ColorType(p1.c);
	    }
	    
	    y_b = p[0].y;
	    dy_b = ((float)(p[2].y - p[0].y))/(p[2].x - p[0].x);
	    
	    if(do_smooth)
	    {
	    	// calculate slopes in r, g, b for segment b
	    	dr_b = ((float)(p[2].c.r - p[0].c.r))/(p[2].x - p[0].x);
	    	dg_b = ((float)(p[2].c.g - p[0].c.g))/(p[2].x - p[0].x);
	    	db_b = ((float)(p[2].c.b - p[0].c.b))/(p[2].x - p[0].x);
	    }
	    
	    // if there is a left-hand part to the triangle then fill it
	    if(p[0].x != p[1].x)
	    {
	    	y_a = p[0].y;
	    	dy_a = ((float)(p[1].y - p[0].y))/(p[1].x - p[0].x);
		    
	    	if(do_smooth)
	    	{
	    		// calculate slopes in r, g, b for segment a
	    		dr_a = ((float)(p[1].c.r - p[0].c.r))/(p[1].x - p[0].x);
	    		dg_a = ((float)(p[1].c.g - p[0].c.g))/(p[1].x - p[0].x);
	    		db_a = ((float)(p[1].c.b - p[0].c.b))/(p[1].x - p[0].x);
	    	}
		    
		    // loop over the columns for left-hand part of triangle
		    // filling from side a to side b of the span
		    for(x = p[0].x; x < p[1].x; ++x)
		    {
		    	drawLine(buff, side_a, side_b);

		    	++side_a.x;
		    	++side_b.x;
		    	y_a += dy_a;
		    	y_b += dy_b;
		    	side_a.y = (int)y_a;
		    	side_b.y = (int)y_b;
		    	if(do_smooth)
		    	{
		    		side_a.c.r +=dr_a;
		    		side_b.c.r +=dr_b;
		    		side_a.c.g +=dg_a;
		    		side_b.c.g +=dg_b;
		    		side_a.c.b +=db_a;
		    		side_b.c.b +=db_b;
		    	}
		    }
	    }
	    
	    // there is no right-hand part of triangle
	    if(p[1].x == p[2].x)
	    	return;
	    
	    // set up to fill the right-hand part of triangle 
	    // replace segment a
	    side_a = new Point2D(p[1]);
	    if(!do_smooth)
	    	side_a.c =new ColorType(p1.c);
	    
	    y_a = p[1].y;
	    dy_a = ((float)(p[2].y - p[1].y))/(p[2].x - p[1].x);
	    if(do_smooth)
	    {
	    	// calculate slopes in r, g, b for replacement for segment a
	    	dr_a = ((float)(p[2].c.r - p[1].c.r))/(p[2].x - p[1].x);
	    	dg_a = ((float)(p[2].c.g - p[1].c.g))/(p[2].x - p[1].x);
	    	db_a = ((float)(p[2].c.b - p[1].c.b))/(p[2].x - p[1].x);
	    }

	    // loop over the columns for right-hand part of triangle
	    // filling from side a to side b of the span
	    for(x = p[1].x; x <= p[2].x; ++x)
	    {
	    	drawLine(buff, side_a, side_b);
		    
	    	++side_a.x;
	    	++side_b.x;
	    	y_a += dy_a;
	    	y_b += dy_b;
	    	side_a.y = (int)y_a;
	    	side_b.y = (int)y_b;
	    	if(do_smooth)
	    	{
	    		side_a.c.r +=dr_a;
	    		side_b.c.r +=dr_b;
	    		side_a.c.g +=dg_a;
	    		side_b.c.g +=dg_b;
	    		side_a.c.b +=db_a;
	    		side_b.c.b +=db_b;
	    	}
	    }
	}

	/**********************************************************************
	 * Helper function to bubble sort triangle vertices by ascending x value.
	 * 
	 * @param p1
	 *          First given vertex of the triangle.
	 * @param p2
	 *          Second given vertex of the triangle.
	 * @param p3
	 *          Third given vertex of the triangle.
	 * @return 
	 *          Array of 3 points, sorted by ascending x value.
	 */
	private static Point2D[] sortTriangleVerts(Point2D p1, Point2D p2, Point2D p3)
	{
	    Point2D pts[] = {p1, p2, p3};
	    Point2D tmp;
	    int j=0;
	    boolean swapped = true;
	         
	    while (swapped) 
	    {
	    	swapped = false;
	    	j++;
	    	for (int i = 0; i < 3 - j; i++) 
	    	{                                       
	    		if (pts[i].x > pts[i + 1].x) 
	    		{                          
	    			tmp = pts[i];
	    			pts[i] = pts[i + 1];
	    			pts[i + 1] = tmp;
	    			swapped = true;
	    		}
	    	}                
	    }
	    return(pts);
	}

	private static Point3D[] sortTriangleVerts(Point3D p1, Point3D p2, Point3D p3)
	{
		Point3D pts[] = {p1, p2, p3};
		Point3D tmp;
		int j=0;
		boolean swapped = true;

		while (swapped)
		{
			swapped = false;
			j++;
			for (int i = 0; i < 3 - j; i++)
			{
				if (pts[i].x > pts[i + 1].x)
				{
					tmp = pts[i];
					pts[i] = pts[i + 1];
					pts[i + 1] = tmp;
					swapped = true;
				}
			}
		}
		return(pts);
	}

	public static void drawPoint3D(BufferedImage buff, Point3D p, ColorType c) {
		if(!(p.x>=0 && p.x<buff.getWidth() && p.y>=0 && p.y < buff.getHeight()))
			return;
		Point2D p_2D = new Point2D((int)p.x, (int)p.y, c);
		if(DepthBuffer.updateBufferPoint(p))
			drawPoint(buff,p_2D);
	}
	public static void drawPoint2DWithDepth(BufferedImage buff, int x, int y, int depth, ColorType c) {
		if(!(x>=0 && x<buff.getWidth() && y>=0 && y < buff.getHeight()))
			return;
		Point3D p_3D = new Point3D(x,y,depth);
		Point2D p_2D = new Point2D((int)x, (int)y, c);
		if(DepthBuffer.updateBufferPoint(p_3D))
			drawPoint(buff,p_2D);
	}

	public static void drawLine3D(BufferedImage buff, Point3D p1, Point3D p2, ColorType c1, ColorType c2)
	{
		int x0=(int)p1.x, y0=(int)p1.y, z0=(int)p1.z;
		int xEnd=(int)p2.x, yEnd=(int)p2.y, zEnd=(int)p2.z;
		int dx = Math.abs(xEnd - x0),  dy = Math.abs(yEnd - y0);
		int dz = Math.abs(zEnd - z0);

		if(dx==0 && dy==0)
		{
			drawPoint3D(buff,p1,c1);
			return;
		}

		// if slope is greater than 1, then swap the role of x and y
		boolean x_y_role_swapped = (dy > dx);
		if(x_y_role_swapped)
		{
			x0=(int)p1.y;
			y0=(int)p1.x;
			xEnd=(int)p2.y;
			yEnd=(int)p2.x;
			dx = Math.abs(xEnd - x0);
			dy = Math.abs(yEnd - y0);
		}

		// initialize the decision parameter and increments
		int p = 2 * dy - dx;
		int twoDy = 2 * dy,  twoDyMinusDx = 2 * (dy - dx);
		int x=x0, y=y0;

		// set step increment to be positive or negative
		int step_x = x0<xEnd ? 1 : -1;
		int step_y = y0<yEnd ? 1 : -1;

		// deal with setup for color interpolation
		// first get r,g,b integer values at the end points
		int r0=c1.getR_int(), rEnd=c2.getR_int();
		int g0=c1.getG_int(), gEnd=c2.getG_int();
		int b0=c1.getB_int(), bEnd=c2.getB_int();

		// compute the change in r,g,b and depth
		int dr=Math.abs(rEnd-r0), dg=Math.abs(gEnd-g0), db=Math.abs(bEnd-b0);

		// set step increment to be positive or negative
		int step_r = r0<rEnd ? 1 : -1;
		int step_g = g0<gEnd ? 1 : -1;
		int step_b = b0<bEnd ? 1 : -1;
		int step_z = z0<zEnd ? 1 : -1;

		// compute whole step in each color that is taken each time through loop
		int whole_step_r = step_r*(dr/dx);
		int whole_step_g = step_g*(dg/dx);
		int whole_step_b = step_b*(db/dx);
		int whole_step_z = step_z*(dz/dx);

		// compute remainder, which will be corrected depending on decision parameter
		dr=dr%dx;
		dg=dg%dx;
		db=db%dx;
		dz=dz%dx;

		// initialize decision parameters for red, green, and blue
		int p_r = 2 * dr - dx;
		int twoDr = 2 * dr,  twoDrMinusDx = 2 * (dr - dx);
		int r=r0;

		int p_g = 2 * dg - dx;
		int twoDg = 2 * dg,  twoDgMinusDx = 2 * (dg - dx);
		int g=g0;

		int p_b = 2 * db - dx;
		int twoDb = 2 * db,  twoDbMinusDx = 2 * (db - dx);
		int b=b0;

		int z=z0;

		// draw start pixel
		if(x_y_role_swapped)
		{
			if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth()) {
				drawPoint2DWithDepth(buff,y,x,z, new ColorType(r/255f,g/255f,b/255f));
				//drawPoint2DWithDepth(buff,y,x,z, new ColorType(r,g,b));
			}

		}
		else
		{
			if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth())
				drawPoint2DWithDepth(buff,y,x,z, new ColorType(r/255f,g/255f,b/255f));
				//drawPoint2DWithDepth(buff,y,x,z, new ColorType(r,g,b));

		}

		while (x != xEnd)
		{
			// increment x and y, and z
			x+=step_x;
			if (p < 0)
				p += twoDy;
			else
			{
				y+=step_y;
				p += twoDyMinusDx;
			}
			z+=step_z;

			// increment r by whole amount slope_r, and correct for accumulated error if needed
			r+=whole_step_r;
			if (p_r < 0)
				p_r += twoDr;
			else
			{
				r+=step_r;
				p_r += twoDrMinusDx;
			}

			// increment g by whole amount slope_b, and correct for accumulated error if needed
			g+=whole_step_g;
			if (p_g < 0)
				p_g += twoDg;
			else
			{
				g+=step_g;
				p_g += twoDgMinusDx;
			}

			// increment b by whole amount slope_b, and correct for accumulated error if needed
			b+=whole_step_b;
			if (p_b < 0)
				p_b += twoDb;
			else
			{
				b+=step_b;
				p_b += twoDbMinusDx;
			}

			if(x_y_role_swapped)
			{
				if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth())
					drawPoint2DWithDepth(buff,y,x,z, new ColorType(r/255f,g/255f,b/255f));
					//drawPoint2DWithDepth(buff,y,x,z, new ColorType(r,g,b));
			}
			else
			{
				if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth())
					drawPoint2DWithDepth(buff,y,x,z, new ColorType(r/255f,g/255f,b/255f));
					//drawPoint2DWithDepth(buff,y,x,z, new ColorType(r,g,b));
			}
		}
	}

	public static void drawTriangle3DWithDepthBuffer(BufferedImage buff, Point3D p1_3D, Point3D p2_3D, Point3D p3_3D, ColorType c1, ColorType c2, ColorType c3, boolean do_smooth) {
		Point2D p1 = new Point2D((int)p1_3D.x, (int)p1_3D.y, c1);
		Point2D p2 = new Point2D((int)p2_3D.x, (int)p2_3D.y, c2);
		Point2D p3 = new Point2D((int)p3_3D.x, (int)p3_3D.y, c3);
		// sort the triangle vertices by ascending x value
		Point3D p[] = sortTriangleVerts(p1_3D,p2_3D,p3_3D);
		Point2D p_2D[] = sortTriangleVerts(p1,p2,p3);

		int x;
		float y_a, y_b;
		float z_a, z_b;
		float dy_a, dy_b;
		float dz_a, dz_b;
		float dr_a=0, dg_a=0, db_a=0, dr_b=0, dg_b=0, db_b=0;

//	    Point2D side_a = new Point2D(p[0]), side_b = new Point2D(p[0]);
		Point3D side_a = new Point3D(p[0]), side_b = new Point3D(p[0]);
		Point2D side_a_2D = new Point2D(p_2D[0]), side_b_2D = new Point2D(p_2D[0]);
//	    side_a_2D.c = new ColorType(p1.c);
//	    side_b_2D.c = new ColorType(p1.c);


		y_b = p_2D[0].y;
		dy_b = ((float)(p_2D[2].y - p_2D[0].y))/(p_2D[2].x - p_2D[0].x);
		z_b = p[0].z;
		dz_b = ((float)(p[2].z - p[0].z))/(p_2D[2].x - p_2D[0].x);

		dr_b = ((float)(p_2D[2].c.r - p_2D[0].c.r))/(p[2].x - p[0].x);
		dg_b = ((float)(p_2D[2].c.g - p_2D[0].c.g))/(p[2].x - p[0].x);
		db_b = ((float)(p_2D[2].c.b - p_2D[0].c.b))/(p[2].x - p[0].x);


		// if there is a left-hand part to the triangle then fill it
		if(p_2D[0].x != p_2D[1].x)
		{
			y_a = p_2D[0].y;
			z_a = p[0].z;
			dy_a = ((float)(p_2D[1].y - p_2D[0].y))/(p_2D[1].x - p_2D[0].x);
			dz_a = ((float)(p[1].z - p[0].z))/(p[1].x - p[0].x);


			dr_a = ((float)(p_2D[1].c.r - p_2D[0].c.r))/(p[1].x - p[0].x);
			dg_a = ((float)(p_2D[1].c.g - p_2D[0].c.g))/(p[1].x - p[0].x);
			db_a = ((float)(p_2D[1].c.b - p_2D[0].c.b))/(p[1].x - p[0].x);

			// loop over the columns for left-hand part of triangle
			// filling from side a to side b of the span
			for(x = p_2D[0].x; x < p_2D[1].x; ++x)
			{
				drawLine3D(buff, side_a, side_b, side_a_2D.c, side_b_2D.c);

				++side_a.x;
				++side_a_2D.x;
				++side_b.x;
				++side_b_2D.x;
				y_a += dy_a;
				y_b += dy_b;
				z_a += dz_a;
				z_b += dz_b;
				side_a.y = (int)y_a;
				side_b.y = (int)y_b;
				side_a.z = (int)z_a;
				side_b.z = (int)z_b;

				side_a_2D.y = (int)y_a;
				side_b_2D.y = (int)y_b;
				side_a.z = (int)z_a;
				side_b.z = (int)z_b;

				side_a_2D.c.r +=dr_a;
				side_b_2D.c.r +=dr_b;
				side_a_2D.c.g +=dg_a;
				side_b_2D.c.g +=dg_b;
				side_a_2D.c.b +=db_a;
				side_b_2D.c.b +=db_b;

			}
		}

		// there is no right-hand part of triangle
		if(p[1].x == p[2].x)
			return;

		// set up to fill the right-hand part of triangle
		// replace segment a
		side_a = new Point3D(p[1]);
		side_a_2D.c =new ColorType(p_2D[1].c);

		y_a = p_2D[1].y;
		dy_a = ((float)(p_2D[2].y - p_2D[1].y))/(p_2D[2].x - p_2D[1].x);
		z_a = p[1].z;
		dz_a = ((float)(p[2].z - p[1].z))/(p_2D[2].x - p_2D[1].x);
		// calculate slopes in r, g, b for replacement for segment a
		dr_a = ((float)(p_2D[2].c.r - p_2D[1].c.r))/(p_2D[2].x - p_2D[1].x);
		dg_a = ((float)(p_2D[2].c.g - p_2D[1].c.g))/(p_2D[2].x - p_2D[1].x);
		db_a = ((float)(p_2D[2].c.b - p_2D[1].c.b))/(p_2D[2].x - p_2D[1].x);

		// loop over the columns for right-hand part of triangle
		// filling from side a to side b of the span
		for(x = (int)p[1].x; x <= p[2].x; ++x)
		{
			drawLine3D(buff, side_a, side_b, side_a_2D.c, side_b_2D.c);

			++side_a.x;
			++side_b.x;
			y_a += dy_a;
			y_b += dy_b;
			z_a += dz_a;
			z_b += dz_b;
			side_a.y = (int)y_a;
			side_b.y = (int)y_b;
			side_a.z = (int)z_a;
			side_b.z = (int)z_b;

			side_a_2D.c.r +=dr_a;
			side_b_2D.c.r +=dr_b;
			side_a_2D.c.g +=dg_a;
			side_b_2D.c.g +=dg_b;
			side_a_2D.c.b +=db_a;
			side_b_2D.c.b +=db_b;
		}
	}
	public static void drawTriangleWithPhong(BufferedImage buff, Point3D p1_3D, Point3D p2_3D, Point3D p3_3D, Point3D n1, Point3D n2, Point3D n3, Material mat, Point3D view_vector, boolean do_smooth, AmbientLight ambientLight, InfiniteLight infiniteLight, PointLight pointLight) {
		// sort the triangle vertices by ascending x value
		ColorType c1 = lightUtil.applyLight(ambientLight, infiniteLight, pointLight, mat, view_vector, n1, view_vector);
		ColorType c2 = lightUtil.applyLight(ambientLight, infiniteLight, pointLight, mat, view_vector, n2, view_vector);
		ColorType c3 = lightUtil.applyLight(ambientLight, infiniteLight, pointLight, mat, view_vector, n3, view_vector);
		Point2D p1 = new Point2D((int)p1_3D.x, (int)p1_3D.y, c1);
		Point2D p2 = new Point2D((int)p2_3D.x, (int)p2_3D.y, c2);
		Point2D p3 = new Point2D((int)p3_3D.x, (int)p3_3D.y, c3);
		// sort the triangle vertices by ascending x value
		Point3D p[] = {p1_3D,p2_3D,p3_3D};
		Point2D p_2D[] = {p1,p2,p3};
		Point3D ns[] = {n1, n2, n3};
		sortTriangleVertsWithN(p, ns, p_2D);
		//Point3D []res = sortTriangleVerts(p1_3D,p2_3D,p3_3D);
		int x;
		float y_a, y_b;
		float z_a, z_b;
		float nx_a, ny_a, nz_a, nx_b, ny_b, nz_b;
		float dnx_a, dnx_b;
		float dny_a, dny_b;
		float dnz_a, dnz_b;
		float dx_a, dx_b;
		float dy_a, dy_b;
		float dz_a, dz_b;

//	    Point2D side_a = new Point2D(p[0]), side_b = new Point2D(p[0]);
		Point3D side_a = new Point3D(p[0]), side_b = new Point3D(p[0]);
		Point2D side_a_2D = new Point2D(p_2D[0]), side_b_2D = new Point2D(p_2D[0]);
//	    side_a_2D.c = new ColorType(p1.c);
//	    side_b_2D.c = new ColorType(p1.c);


		y_b = p_2D[0].y;
		dy_b = ((float)(p_2D[2].y - p_2D[0].y))/(p_2D[2].x - p_2D[0].x);
		z_b = p[0].z;
		dz_b = ((float)(p[2].z - p[0].z))/(p_2D[2].x - p_2D[0].x);


		nx_a = ns[0].x;
		ny_a = ns[0].y;
		nz_a = ns[0].z;
		nx_b = ns[0].x;
		ny_b = ns[0].y;
		nz_b = ns[0].z;
		dnx_b = ((float)(ns[2].x - ns[0].x))/(p[2].x - p[0].x);
		dny_b = ((float)(ns[2].y - ns[0].y))/(p[2].x - p[0].x);
		dnz_b = ((float)(ns[2].z - ns[0].z))/(p[2].x - p[0].x);


		// if there is a left-hand part to the triangle then fill it
		if(p_2D[0].x != p_2D[1].x)
		{
			y_a = p_2D[0].y;
			z_a = p[0].z;
			dy_a = ((float)(p_2D[1].y - p_2D[0].y))/(p_2D[1].x - p_2D[0].x);
			dz_a = ((float)(p[1].z - p[0].z))/(p[1].x - p[0].x);


			dnx_a = ((float)(ns[1].x - ns[0].x))/(p[1].x - p[0].x);
			dny_a = ((float)(ns[1].y - ns[0].y))/(p[1].x - p[0].x);
			dnz_a = ((float)(ns[1].z - ns[0].z))/(p[1].x - p[0].x);

			// loop over the columns for left-hand part of triangle
			// filling from side a to side b of the span
			for(x = p_2D[0].x; x < p_2D[1].x; ++x)
			{
				//
				Point3D thisNa = new Point3D(nx_a,ny_a,nz_a);
				Point3D thisNb = new Point3D(nx_b,ny_b,nz_b);
				thisNa.normalize();
				thisNb.normalize();
				drawLine3DPhong(buff, side_a, side_b, thisNa, thisNb, view_vector, mat, ambientLight, infiniteLight, pointLight);

				++side_a.x;
				++side_a_2D.x;
				++side_b.x;
				++side_b_2D.x;
				y_a += dy_a;
				y_b += dy_b;
				z_a += dz_a;
				z_b += dz_b;
				side_a.y = (int)y_a;
				side_b.y = (int)y_b;
				side_a.z = (int)z_a;
				side_b.z = (int)z_b;

				side_a_2D.y = (int)y_a;
				side_b_2D.y = (int)y_b;
				side_a.z = (int)z_a;
				side_b.z = (int)z_b;

				nx_a += dnx_a;
				ny_a += dny_a;
				nz_a += dnz_a;

				nx_b += dnx_b;
				ny_b += dny_b;
				nz_b += dnz_b;

			}
		}

		// there is no right-hand part of triangle
		if(p[1].x == p[2].x)
			return;

		// set up to fill the right-hand part of triangle
		// replace segment a
		side_a = new Point3D(p[1]);
		side_a_2D.c =new ColorType(p_2D[1].c);

		y_a = p_2D[1].y;
		dy_a = ((float)(p_2D[2].y - p_2D[1].y))/(p_2D[2].x - p_2D[1].x);
		z_a = p[1].z;
		dz_a = ((float)(p[2].z - p[1].z))/(p_2D[2].x - p_2D[1].x);

		nx_a = ns[1].x;
		ny_a = ns[1].y;
		nz_a = ns[1].z;
		dnx_a = ((float)(ns[2].x - ns[1].x))/(p[2].x - p[1].x);
		dny_a = ((float)(ns[2].y - ns[1].y))/(p[2].x - p[1].x);
		dnz_a = ((float)(ns[2].z - ns[1].z))/(p[2].x - p[1].x);


		// loop over the columns for right-hand part of triangle
		// filling from side a to side b of the span
		for(x = (int)p[1].x; x <= p[2].x; ++x)
		{
			Point3D thisNa = new Point3D(nx_a,ny_a,nz_a);
			Point3D thisNb = new Point3D(nx_b,ny_b,nz_b);
			thisNa.normalize();
			thisNb.normalize();
			drawLine3DPhong(buff, side_a, side_b, thisNa, thisNb, view_vector, mat, ambientLight, infiniteLight, pointLight);

			++side_a.x;
			++side_b.x;
			y_a += dy_a;
			y_b += dy_b;
			z_a += dz_a;
			z_b += dz_b;
			side_a.y = (int)y_a;
			side_b.y = (int)y_b;
			side_a.z = (int)z_a;
			side_b.z = (int)z_b;

			nx_a += dnx_a;
			ny_a += dny_a;
			nz_a += dnz_a;

			nx_b += dnx_b;
			ny_b += dny_b;
			nz_b += dnz_b;
		}
	}

	public static void drawLine3DPhong(BufferedImage buff, Point3D p1, Point3D p2, Point3D n1, Point3D n2, Point3D view_vector, Material mat, AmbientLight ambientLight, InfiniteLight infiniteLight, PointLight pointLight) {
		int x0=(int)p1.x, y0=(int)p1.y, z0=(int)p1.z;
		int xEnd=(int)p2.x, yEnd=(int)p2.y, zEnd=(int)p2.z;

		float nx0=n1.x, ny0=n1.y, nz0=n1.z;
		float nxEnd=n2.x, nyEnd=n2.y, nzEnd=n2.z;

		int dx = Math.abs(xEnd - x0),  dy = Math.abs(yEnd - y0);
		int dz = zEnd - z0;

		float dnx = Math.abs(nxEnd - nx0),  dny = Math.abs(nyEnd - ny0);
		float dnz = Math.abs(nzEnd - nz0);

		ColorType c1 = lightUtil.applyLight(ambientLight, infiniteLight, pointLight, mat, view_vector, n1, p1);


//	    System.out.println(n2.x+" "+n2.y+" "+n2.z);

		if(dx==0 && dy==0)
		{
			drawPoint3D(buff,p1,c1);
			return;
		}

		// if slope is greater than 1, then swap the role of x and y
		boolean x_y_role_swapped = (dy > dx);
		if(x_y_role_swapped)
		{
			x0=(int)p1.y;
			y0=(int)p1.x;
			xEnd=(int)p2.y;
			yEnd=(int)p2.x;
			dx = Math.abs(xEnd - x0);
			dy = Math.abs(yEnd - y0);
		}

		// initialize the decision parameter and increments
		int p = 2 * dy - dx;
		int twoDy = 2 * dy,  twoDyMinusDx = 2 * (dy - dx);
		int x=x0, y=y0;

		// set step increment to be positive or negative
		int step_x = x0<xEnd ? 1 : -1;
		int step_y = y0<yEnd ? 1 : -1;


		// set step increment to be positive or negative
		int step_nx = nx0<nxEnd ? 1 : -1;
		int step_ny = ny0<nyEnd ? 1 : -1;
		int step_nz = nz0<nzEnd ? 1 : -1;
		int step_z = z0<zEnd ? 1 : -1;

		// compute whole step in each color that is taken each time through loop
		float whole_step_nx = step_nx*(dnx/dx);
		float whole_step_ny = step_ny*(dny/dx);
		float whole_step_nz = step_nz*(dnz/dx);
		int whole_step_z = step_z*(dz/dx);

		// compute remainder, which will be corrected depending on decision parameter
		dnx=dnx%dx;
		dny=dny%dx;
		dnz=dnz%dx;
		dz=dz%dx;

		// initialize decision parameters for red, green, and blue
		float p_nx = 2 * dnx - dx;
		float twoDnx = 2 * dnx,  twoDnxMinusDx = 2 * (dnx - dx);
		float nx=nx0;

		float p_ny = 2 * dny - dx;
		float twoDny = 2 * dny,  twoDnyMinusDx = 2 * (dny - dx);
		float ny=ny0;

		float p_nz = 2 * dnz - dx;
		float twoDnz = 2 * dnz,  twoDnzMinusDx = 2 * (dnz - dx);
		float nz=nz0;

		int z=z0;

		// draw start pixel
		if(x_y_role_swapped)
		{
			if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth()) {
				Point3D thisPoint = new Point3D(y,x,z);
				Point3D thisN = new Point3D(nx,ny,nz);
				thisN.normalize();
				ColorType c_new = lightUtil.applyLight(ambientLight, infiniteLight, pointLight, mat, view_vector, thisN, thisPoint);
				drawPoint2DWithDepth(buff,y,x,z, c_new);
			}

		}
		else
		{
			if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth()) {
				Point3D thisPoint = new Point3D(x,y,z);
				Point3D thisN = new Point3D(nx,ny,nz);
				thisN.normalize();
				ColorType c_new = lightUtil.applyLight(ambientLight, infiniteLight, pointLight, mat, view_vector, thisN, thisPoint);
				drawPoint2DWithDepth(buff,x,y,z, c_new);
			}

		}

		while (x != xEnd)
		{
			// increment x and y, and z
			x+=step_x;
			if (p < 0)
				p += twoDy;
			else
			{
				y+=step_y;
				p += twoDyMinusDx;
			}
			z+=step_z;

			// increment r by whole amount slope_r, and correct for accumulated error if needed
			nx+=whole_step_nx;
//			if (p_nx < 0)
//				p_nx += twoDnx;
//			else
//			{
//				nx+=step_nx;
//				p_nx += twoDnxMinusDx;
//			}

			// increment g by whole amount slope_b, and correct for accumulated error if needed
			ny+=whole_step_ny;
//			if (p_ny < 0)
//				p_ny += twoDny;
//			else
//			{
//				ny+=step_ny;
//				p_ny += twoDnyMinusDx;
//			}

			// increment b by whole amount slope_b, and correct for accumulated error if needed
			nz+=whole_step_nz;
//			if (p_nz < 0)
//				p_nz += twoDnz;
//			else
//			{
//				nz+=step_nz;
//				p_nz += twoDnzMinusDx;
//			}

			if(x_y_role_swapped)
			{
				if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth()) {
					Point3D thisPoint = new Point3D(y,x,z);
					Point3D thisN = new Point3D(nx,ny,nz);
					thisN.normalize();
					ColorType c_new = lightUtil.applyLight(ambientLight, infiniteLight, pointLight, mat, view_vector, thisN, thisPoint);
					drawPoint2DWithDepth(buff,y,x,z, c_new);
				}
			}
			else
			{
				if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth()){
					Point3D thisPoint = new Point3D(x,y,z);
					Point3D thisN = new Point3D(nx,ny,nz);
					thisN.normalize();
					ColorType c_new = lightUtil.applyLight(ambientLight, infiniteLight, pointLight, mat, view_vector, thisN, thisPoint);
					drawPoint2DWithDepth(buff,x,y,z, c_new);
				}
			}
		}
	}

	private static void sortTriangleVertsWithN(Point3D[] ps, Point3D[] ns, Point2D[] p_2D) {

		int j=0;
		boolean swapped = true;
		Point3D tmp;
		Point2D temp;

		while (swapped)
		{
			swapped = false;
			j++;
			for (int i = 0; i < 3 - j; i++)
			{
				if (ps[i].x > ps[i + 1].x)
				{
					tmp = ps[i];
					ps[i] = ps[i + 1];
					ps[i + 1] = tmp;

					tmp = ns[i];
					ns[i] = ns[i + 1];
					ns[i + 1] = tmp;

					temp = p_2D[i];
					p_2D[i] = p_2D[i + 1];
					p_2D[i + 1] = temp;

					swapped = true;
				}
			}
		}

	}

}