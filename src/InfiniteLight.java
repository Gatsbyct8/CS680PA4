import com.sun.org.apache.bcel.internal.generic.IADD;

import java.awt.*;
import java.util.Random;

public class InfiniteLight extends Light{
	public float alpha;
	private float a0,a1,a2;
private Random rnd=new Random();
	
	public InfiniteLight(ColorType _c, Point3D _direction,float alpha, float a0, float a1, float a2)
	{
		color = new ColorType(_c);
		direction = new Point3D(_direction);
		position = new Point3D(0, 0, 0); // Not used in this class
		this.alpha = alpha;
		this.a0 = a0;
		this.a1 = a1;
		this.a2 = a2;
	}
	
	// apply this light source to the vertex / normal, given material
	// return resulting color value
	// v: viewing vector
	// n: face normal
	public ColorType applyLight(Material mat, Point3D v, Point3D n){
		ColorType res = new ColorType();
		// ****************Implement Code here*******************//
//		ColorType I_amb = new ColorType();
//		I_amb.r = mat.ka.r * color.r;
//		I_amb.g = mat.ka.g * color.g;
//		I_amb.b = mat.ka.b * color.b;

		ColorType I_dif = new ColorType();
		float NL = n.dotProduct(direction);
		if (NL > 0.0) {
			if (Lab_PA4.specular == true) {
				I_dif.r = mat.kd.r * color.r * n.dotProduct(direction);
				I_dif.g = mat.kd.g * color.g * n.dotProduct(direction);
				I_dif.b = mat.kd.b * color.b * n.dotProduct(direction);
			}


			ColorType I_spec = new ColorType();
			Point3D r = direction.reflection(n);
			float VR = v.dotProduct(r);
			if (Lab_PA4.diffuse == true && VR > 0.0) {
				float temp = (float) Math.pow(VR, mat.ns);
				I_spec.r = mat.ks.r * color.r * temp;
				I_spec.g = mat.ks.g * color.g * temp;
				I_spec.b = mat.ks.b * color.b * temp;
			}



			res.r = I_dif.r + I_spec.r;
			res.g = I_dif.g + I_spec.g;
			res.b = I_dif.b + I_spec.b;

			if (Lab_PA4.attenuation) {
				float d =-( (float) direction.x + direction.y + direction.z);
				float radialFactor = 1 / (a0 + a1 * d + a2 * (float)Math.pow(d, 2));
				res.r *= radialFactor;
				res.g *= radialFactor;
				res.b *= radialFactor;
			}
		}

		res.clamp();
		return res;

	}
}
