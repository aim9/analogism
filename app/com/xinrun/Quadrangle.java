package com.xinrun;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;

/**
 * Created by zhaoxueqian on 16/4/2.
 */
public class Quadrangle implements Comparable {
    Point3d p1,p2,p3,p4;

    public ArrayList<Point3d> getPoints(){
        ArrayList<Point3d> result = new ArrayList<>();
        result.add(p1);
        result.add(p2);
        result.add(p3);
        result.add(p4);
        return result;
    }

    public int compareTo(Object o){
        Quadrangle q = (Quadrangle)o;
        Double z = q.p1.z + q.p2.z + q.p3.z + q.p4.z;
        Double selfZ = p1.z + p2.z + p3.z + p4.z;
        return selfZ.compareTo(z);
    }
}
