import java.util.Random;

public class PointLight extends Light{
	public float alpha;
	private float a0,a1,a2;
	private Random rnd=new Random();

	public PointLight(ColorType _c, Point3D _direction, Point3D _position, float alpha, float a0, float a1, float a2)
	{
		color = new ColorType(_c);
		direction = new Point3D(_direction);
		position = new Point3D(_position); // Not used in this class
		this.alpha = alpha;
		this.a0 = a0;
		this.a1 = a1;
		this.a2 = a2;
	}
	
	// apply this light source to the vertex / normal, given material
	// return resulting color value
	// v: viewing vector
	// n: face normal
	public ColorType applyLight(Material mat, Point3D v, Point3D n, Point3D point) {
		ColorType res = new ColorType();
		Point3D L = position.minus(point); // vobj
		L.normalize();
		// ****************Implement Code here*******************//

		ColorType I_dif = new ColorType();
		float NL = L.dotProduct(n);
		if (NL > 0.0) {
			if (Lab_PA4.specular == true) {
				I_dif.r = mat.kd.r * color.r * NL;
				I_dif.g = mat.kd.g * color.g * NL;
				I_dif.b = mat.kd.b * color.b * NL;
			}


			ColorType I_spec = new ColorType();
			Point3D r = L.reflection(n);
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
				float d = (float) Math.sqrt(Math.pow(position.x - point.x, 2) + Math.pow(position.y - point.y, 2) + Math.pow(position.z - point.z, 2));
				float radialFactor = 1 / (a0 + a1 * d + a2 * (float)Math.pow(d, 2));
				res.r *= radialFactor;
				res.g *= radialFactor;
				res.b *= radialFactor;
			}

			if (Lab_PA4.attenuation) {
				float dot = L.dotProduct(direction);
				if (dot < Math.cos(1.57079633)) {
					res.r *= Math.pow(dot, alpha);
					res.g *= Math.pow(dot, alpha);
					res.b *= Math.pow(dot, alpha);
				}
			}
		}

		res.clamp();
		return res;

	}
}
