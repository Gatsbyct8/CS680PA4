public class lightUtil {
    public static ColorType applyLight(AmbientLight ambientLight, InfiniteLight infiniteLight, PointLight pointLight, Material mat, Point3D v, Point3D n,Point3D point){
        ColorType res = new ColorType();
        if (Lab_PA4.ambientLight){
            ColorType temp = ambientLight.applyLight(mat, v, n);
            res.r += temp.r;
            res.g += temp.g;
            res.b += temp.b;
        }
        if(Lab_PA4.infiniteLight){
            ColorType temp = infiniteLight.applyLight(mat, v, n);
            res.r += temp.r;
            res.g += temp.g;
            res.b += temp.b;
        }
        if(Lab_PA4.pointLight){
            ColorType temp = pointLight.applyLight(mat, v, n, point);
            res.r += temp.r;
            res.g += temp.g;
            res.b += temp.b;
        }
        res.clamp();
        return res;
    }

    public static void rotate(InfiniteLight infiniteLight, PointLight pointLight, Quaternion q, Point3D center)
    {
        Quaternion q_inv = q.conjugate();
        Point3D vec;

        Quaternion p;

        //for(int i=0;i<rows;++i)
            //for(int j=0;j<cols;++j)
            //{
                // apply pivot rotation to vertices, given center point
                p = new Quaternion((float)0.0,pointLight.position.minus(center));
                p=q.multiply(p);
                p=p.multiply(q_inv);
                vec = p.get_v();
                pointLight.position=vec.plus(center);

                // rotate the normals
//                p = new Quaternion((float)0.0,n[i][j]);
//                p=q.multiply(p);
//                p=p.multiply(q_inv);
//                n[i][j] = p.get_v();
            //}
        p = new Quaternion((float)0.0,infiniteLight.position.minus(center));
        p=q.multiply(p);
        p=p.multiply(q_inv);
        vec = p.get_v();
        infiniteLight.position=vec.plus(center);

    }

}
