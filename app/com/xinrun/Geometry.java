package com.xinrun;

/**
 * Created by zhaoxueqian on 16/4/2.
 */
import java.nio.charset.CoderResult;
import java.util.*;
import com.vividsolutions.jts.algorithm.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import com.vividsolutions.jts.math.*;

public class Geometry
{
    public final static int circle = 0;
    public final static int point = 1;
    public final static int line = 2;
    public final static int triangle = 3;
    public final static int quadrangle = 4;
    public final static int pentagon = 5;

    public static final String opSet = "set";
    public static final String opRotate = "rotate";
    public static final String opMirror = "mirror";
    public static final String opDecorateAngular = "decorateA"; //angular decorate.
    public static final String opDecoratePoint = "decorateP"; //point decorate.
    public static final String opDecorateLine = "decorateL"; //line decorate.
    public static final String opDraw = "draw";

    public static final String strSuccess = "success";
    public static final String strFailed = "failed";

    static public Dictionary<String,Geometry> GeometryDict = new Hashtable<String, Geometry>();

    public int type;
    public String id;

    public ArrayList<Coordinate> points = new ArrayList<Coordinate>();
    boolean isFill = false;

    public ArrayList<Coordinate> corePoints = new ArrayList<Coordinate>();
    boolean isCoreFill = false;


    public List<Long> decAs = new ArrayList<Long>();
    public List<Long> decPs = new ArrayList<Long>();
    public List<Long> decLs = new ArrayList<Long>();

    public Geometry(){}

    public Geometry(String id){
        this.id = id;
        GeometryDict.put(id,this);
    }

    public double svgWidth = 256;
    public double svgHeight =256;

    //in a 2000*2000 rect.
    public double poly_strokeWidth = 10;
    public double decP_radiusS = 30;
    public double decP_radiusL = decP_radiusS*2;
    public double decP_rectSideLen = decP_radiusS*2;
    public double decP_strokeWidth = 10;

    public double decA_dist = 100;
    public double decA_sideLength = 50;
    public double decA_point_radius = 20;

    public double decL_dist = 80;
    public double decL_strokeWidth = 30;
    public double decL_dashWidth = 60;
    public double decL_indent = 0.1;
    public double Distance_delta = 0.01;

    Coordinate rotate(Coordinate p,double angle){
        Vector2D v = new Vector2D(p);
        v = v.rotate(Math.toRadians(angle));
        return v.toCoordinate();
    }

    Coordinate mirror(Coordinate p,double angle){
        Vector2D v = new Vector2D(p);
        v = v.rotate(Math.toRadians(0-angle));
        Coordinate r = new Coordinate(v.getX(),0.0-v.getY());
        return r;
    }

    Coordinate[] calcDecA(int index){
        int size = points.size();
        int next = index+1;
        if(next>=size)next-=size;
        int prev = index-1;
        if(prev<0)prev+=size;
        Coordinate[] result = new Coordinate[7];

        Vector2D now = new Vector2D(points.get(index));
        Vector2D toNext = new Vector2D(points.get(index),points.get(next));
        Vector2D toPrev = new Vector2D(points.get(index),points.get(prev));

        //double ang = toPrev.angleTo(toNext);
        //double ang = toNext.angle() - toPrev.angle();
        //ang/=2.0;
        Vector2D bisector = toNext.normalize().add(toPrev.normalize()).normalize();

        //Check in or out.
        Coordinate[] ring = (Coordinate[])points.toArray(new Coordinate[points.size()+1]);
        ring[points.size()] = ring[0];
        int check = RayCrossingCounter.locatePointInRing(now.add(bisector.multiply(Distance_delta)).toCoordinate(),ring);
        if( Location.INTERIOR!=check ){
            bisector = bisector.rotate(Math.PI);
        }

        result[0] = now.add( bisector.multiply(decA_dist) ).toCoordinate();
        result[1] = now.add( bisector.multiply(decA_dist-decA_sideLength/2.0) ).toCoordinate();
        result[2] = now.add( bisector.multiply(decA_dist+decA_sideLength/2.0) ).toCoordinate();

        Vector2D biVertToNext = bisector.rotate(Math.PI/2.0);
        Vector2D biVertToPrev = bisector.rotate(0-Math.PI/2.0);
        result[3] = (new Vector2D(result[1])).add(biVertToPrev.multiply(decA_sideLength/2.0)).toCoordinate();
        result[4] = (new Vector2D(result[1])).add(biVertToNext.multiply(decA_sideLength/2.0)).toCoordinate();
        result[5] = (new Vector2D(result[2])).add(biVertToPrev.multiply(decA_sideLength/2.0)).toCoordinate();
        result[6] = (new Vector2D(result[2])).add(biVertToNext.multiply(decA_sideLength/2.0)).toCoordinate();

        return result;
    }

    Coordinate[] calcDecL(int index){
        int size = points.size();
        int next = index+1;
        if(next>=size)next-=size;
        Coordinate[] result = new Coordinate[2];

        Vector2D now = new Vector2D(points.get(index));
        Vector2D vnext = new Vector2D(points.get(next));
        Double len = now.distance(vnext);

        Vector2D toNext = new Vector2D(points.get(index),points.get(next)).normalize();

        Vector2D VertToNext = toNext.rotate(0 - Math.PI / 2.0).normalize();
        //Check in or out.
        Coordinate[] ring = (Coordinate[])points.toArray(new Coordinate[points.size()+1]);
        ring[points.size()] = ring[0];
        Vector2D midPoint = now.add(vnext).divide(2);

        if(Location.EXTERIOR!=RayCrossingCounter.locatePointInRing(midPoint.add(VertToNext.multiply(Distance_delta)).toCoordinate(),ring)){
            VertToNext = VertToNext.rotate(Math.PI);
        }

        result[0] = now.add( VertToNext.multiply(decL_dist) ).add(toNext.multiply(len * decL_indent)).toCoordinate();
        result[1] = vnext.add( VertToNext.multiply(decL_dist) ).add(toNext.multiply(0-len*decL_indent)).toCoordinate();

        return result;
    }


    public void rotate(Double angular){
        for(int i=0;i<points.size();i++){
            points.set(i,rotate(points.get(i), angular));
        }
        corerotate(angular); //We rotate core with parent.
    }
    public void mirror(Double angular){
        for(int i=0;i<points.size();i++){
            points.set(i,mirror(points.get(i), angular));
        }
        coremirror(angular); //We mirror core with parent.
    }
    public void changefill(){
        isFill = !isFill;
    }

    public void corerotate(Double angular){
        for(int i=0;i<corePoints.size();i++){
            corePoints.set(i,rotate(corePoints.get(i), angular));
        }
    }
    public void coremirror(Double angular){
        for(int i=0;i<corePoints.size();i++){
            corePoints.set(i,mirror(corePoints.get(i), angular));
        }
    }
    public void corechangefill(){
        isCoreFill = !isCoreFill;
    }

    public boolean setDecorateA(int index,int state){
        if(index>=decAs.size()) return false;
        else {
            decAs.set(index,new Long(state));
            return true;
        }
    }
    public boolean setDecorateP(int index,int state){
        if(index>=decPs.size()) return false;
        else {
            decPs.set(index,new Long(state));
            return true;
        }
    }
    public boolean setDecoratePSize(int index,int state){
        if(index>=decPs.size()) return false;
        else {
            if(decPs.get(index)<0)decPs.set(index,0L);
            int value = decPs.get(index).intValue();
            if(state==0)value&=(~0x02);
            else value|=0x02;
            decPs.set(index,new Long(value));
            return true;
        }
    }
    public boolean setDecoratePFill(int index,int state){
        if(index>=decPs.size()) return false;
        else {
            if(decPs.get(index)<0)decPs.set(index,0L);
            int value = decPs.get(index).intValue();
            if(state==0)value&=(~0x01);
            else value|=0x01;
            decPs.set(index,new Long(value));
            return true;
        }
    }
    public boolean notDecoratePSize(int index){
        if(index>=decPs.size()) return false;
        else {
            if(decPs.get(index)<0)decPs.set(index,0L);
            int value = decPs.get(index).intValue();
            value^=0x02;
            decPs.set(index,new Long(value));
            return true;
        }
    }
    public boolean notDecoratePFill(int index){
        if(index>=decPs.size()) return false;
        else {
            if(decPs.get(index)<0)decPs.set(index,0L);
            int value = decPs.get(index).intValue();
            value^=0x01;
            decPs.set(index,new Long(value));
            return true;
        }
    }
    public boolean setDecorateL(int index,int state){
        if(index>=decLs.size()) return false;
        else {
            decLs.set(index,new Long(state));
            return true;
        }
    }
    public boolean addDecorateA(int index,int stateDelta){
        decAs.set(index,new Long((decAs.get(index)+stateDelta)%decA_allStates));
        return true;
    }
    public boolean addDecorateP(int index,int stateDelta){
        if(stateDelta==1)notDecoratePFill(index);
        if(stateDelta==2)notDecoratePSize(index);
        if(stateDelta==3){
            notDecoratePFill(index);
            notDecoratePSize(index);
        }
        //decPs.set(index,new Long((decPs.get(index)+stateDelta)%decP_allStates));
        return true;
    }
    public boolean bitnotDecorateP(int index,int bitpos){
        decPs.set(index,new Long((decPs.get(index)^(1<<bitpos))));
        return true;
    }
    public boolean addDecorateL(int index,int stateDelta){
        decLs.set(index,new Long((decLs.get(index)+stateDelta)%decL_allStates));
        return true;
    }

    public boolean raddDecorateA(int index,int stateDelta){
        for(int i=0,j=0;i<decAs.size();i++){
            if(decAs.get(i)>=0){
                if(index==j){
                    decAs.set(i,new Long((decAs.get(i)+stateDelta)%decA_allStates));
                    break;
                }
                j++;
            }
        }
        return true;
    }
    public boolean raddDecorateP(int index,int stateDelta){
        for(int i=0,j=0;i<decPs.size();i++){
            if(decPs.get(i)>=0){
                if(index==j){
                    if(stateDelta==1)notDecoratePFill(i);
                    if(stateDelta==2)notDecoratePSize(i);
                    if(stateDelta==3){
                        notDecoratePFill(i);
                        notDecoratePSize(i);
                    }
                    break;
                }
                j++;
            }
        }
        return true;
    }
    public boolean raddDecorateL(int index,int stateDelta){
        for(int i=0,j=0;i<decLs.size();i++){
            if(decLs.get(i)>=0){
                if(index==j){
                    decLs.set(i,new Long((decLs.get(i)+stateDelta)%decA_allStates));
                    break;
                }
                j++;
            }
        }
        return true;
    }

    //exchange two index state.
    public boolean exchgDecorateP(int index1,int index2){
        index1 = index1%decPs.size();
        index2 = index2%decPs.size();
        Long save = decPs.get(index1);
        decPs.set(index1,decPs.get(index2));
        decPs.set(index2,save);
        return true;
    }

    public boolean exchgDecorateA(int index1,int index2){
        index1 = index1%decAs.size();
        index2 = index2%decAs.size();
        Long save = decAs.get(index1);
        decAs.set(index1,decAs.get(index2));
        decAs.set(index2,save);
        return true;
    }

    public boolean exchgDecorateL(int index1,int index2){
        index1 = index1%decLs.size();
        index2 = index2%decLs.size();
        Long save = decLs.get(index1);
        decLs.set(index1,decLs.get(index2));
        decLs.set(index2,save);
        return true;
    }

    String drawPolys(){
        String pts = "<polygon points=\"";
        for(Coordinate p:points){
            pts+= Double.toString(p.x)+","+Double.toString(p.y)+" ";
        }
        if(isFill)pts+="\" fill=\"gray\"";
        else pts+="\" fill=\"none\"";
        pts+=" stroke=\"black\" stroke-width=\""+Double.toString(poly_strokeWidth)+"\"/>";

        //core polys.
        if(corePoints.size()>0) {
            pts += "<polygon points=\"";
            for (Coordinate p : corePoints) {
                pts += Double.toString(p.x) + "," + Double.toString(p.y) + " ";
            }
            if (isCoreFill) pts += "\" fill=\"gray\"";
            else pts += "\" fill=\"none\"";
            pts += " stroke=\"black\" stroke-width=\"" + Double.toString(poly_strokeWidth) + "\"/>";
        }

        return pts;
    }

    //State mean:
    // -1: no decorator.
    //  0: small circle without fill.
    //  1: small circle with fill.
    //  2: large circle without fill.
    //  3: large circle with fill.
    static final long decP_allStates = 4;
    String drawDecPs(){
        String result = "";
        for(int i=0;i<decPs.size();i++){
            long state = decPs.get(i);
            if(state>=0){
                Coordinate p = points.get(i);
                //if(state<=1) {
                    result += "<circle cx=\"" + Double.toString(p.x) + "\" cy=\"" + Double.toString(p.y)
                            + "\" r=\"" + ((state & 0x02) == 0 ? Double.toString(decP_radiusS) : Double.toString(decP_radiusL))
                            + "\" stroke=\"black\" stroke-width=\"" + Double.toString(decP_strokeWidth)
                            + "\" " + ((state & 0x01) == 0 ? ("fill=\"none\"") : ("fill=\"black\""))
                            + "/>";
                /*}
                else{
                    result += "<rect x=\"" + Double.toString(p.x-decP_rectSideLen/2) + "\" y=\"" + Double.toString(p.y-decP_rectSideLen/2)
                            + "\" width=\"" + Double.toString(decP_rectSideLen) + "\" height=\"" + Double.toString(decP_rectSideLen)
                            + "\" stroke=\"black\" stroke-width=\"" + Double.toString(decP_strokeWidth)
                            + "\" " + ((state & 0x01) == 0 ? ("fill=\"none\"") : ("fill=\"black\""))
                            + "/>";
                }*/
            }
        }
        return result;
    }


    //State mean:
    // -1: no decorator.
    //  0: 1 point.
    //  1: 2 points.
    //  2: 4 points.
    static final long decA_allStates = 3;
    String drawDecAs(){
        String result = "";
        for(int i=0;i<decAs.size();i++){
            long state = decAs.get(i);
            if(state>=0){
                Coordinate[] cs = calcDecA(i);
                switch((int)state){
                    case 0:
                        result += "<circle cx=\""+Double.toString(cs[0].x)+"\" cy=\""+Double.toString(cs[0].y)
                                +"\" r=\"" + Double.toString(decA_point_radius) +"\" fill=\"black\"/>\n";
                        break;
                    case 1:
                        result += "<circle cx=\""+Double.toString(cs[1].x)+"\" cy=\""+Double.toString(cs[1].y)
                                +"\" r=\"" + Double.toString(decA_point_radius) +"\" fill=\"black\"/>\n";
                        result += "<circle cx=\""+Double.toString(cs[2].x)+"\" cy=\""+Double.toString(cs[2].y)
                                +"\" r=\"" + Double.toString(decA_point_radius) +"\" fill=\"black\"/>\n";
                        break;
                    case 2:
                        result += "<circle cx=\""+Double.toString(cs[3].x)+"\" cy=\""+Double.toString(cs[3].y)
                                +"\" r=\"" + Double.toString(decA_point_radius) +"\" fill=\"black\"/>\n";
                        result += "<circle cx=\""+Double.toString(cs[4].x)+"\" cy=\""+Double.toString(cs[4].y)
                                +"\" r=\"" + Double.toString(decA_point_radius) +"\" fill=\"black\"/>\n";
                        result += "<circle cx=\""+Double.toString(cs[5].x)+"\" cy=\""+Double.toString(cs[5].y)
                                +"\" r=\"" + Double.toString(decA_point_radius) +"\" fill=\"black\"/>\n";
                        result += "<circle cx=\""+Double.toString(cs[6].x)+"\" cy=\""+Double.toString(cs[6].y)
                                +"\" r=\"" + Double.toString(decA_point_radius) +"\" fill=\"black\"/>\n";
                        break;
                }
            }
        }
        return result;
    }

    //State mean:
    // -1: no decorator.
    //  0: normal line.
    //  1: dash line.
    static final long decL_allStates = 2;
    String drawDecLs(){
        String result = "";
        for(int i=0;i<decLs.size();i++){
            long state = decLs.get(i);
            if(state>=0){
                Coordinate[] l = calcDecL(i);
                result += "<line x1=\""+Double.toString(l[0].x)+"\" y1=\""+Double.toString(l[0].y)
                        +"\" x2=\""+Double.toString(l[1].x)+"\" y2=\""+Double.toString(l[1].y)
                        +"\" stroke=\"black\" stroke-width=\""+Double.toString(decL_strokeWidth)
                        +"\" "+((state&0x01)==0?"":("stroke-dasharray=\""+Double.toString(decL_dashWidth)+" "+ Double.toString(decL_dashWidth)+"\""))
                        +"/>\n";
            }
        }
        return result;
    }

    public String draw(){
        String svgbegin = "<svg width=\""+svgWidth+"\" height=\""+svgHeight+"\" viewBox=\"-1000,-1000,2000,2000\" style=\"border:1px solid #cd0000;\">";
        String svgend = "</svg>";
        String content;

        content = drawPolys()+drawDecPs()+drawDecAs()+drawDecLs();
        return svgbegin + content + svgend;
    }

    public String drawWithSize(int width,int height){
        String svgbegin = "<svg width=\""+width+"\" height=\""+height+"\" viewBox=\"-1000,-1000,2000,2000\" style=\"border:1px solid #cd0000;\">";
        String svgend = "</svg>";
        String content;

        content = drawPolys()+drawDecPs()+drawDecAs()+drawDecLs();
        return svgbegin + content + svgend;
    }


    public void initPoly3(Object o1,Object o2,Object o3){
        this.type = triangle;
        Position p1 = (Position)o1;
        Position p2 = (Position)o2;
        Position p3 = (Position)o3;
        points.add(new Coordinate(p1.x,p1.y));
        decAs.add(-1L);decLs.add(-1L);decPs.add(-1L);
        points.add(new Coordinate(p2.x,p2.y));
        decAs.add(-1L);decLs.add(-1L);decPs.add(-1L);
        points.add(new Coordinate(p3.x,p3.y));
        decAs.add(-1L);decLs.add(-1L);decPs.add(-1L);
        //System.out.println(" POLY3:" + p1.x + "," + p1.y + " ");
    }

    public void initPoly4(Object o1,Object o2,Object o3,Object o4){
        this.type = quadrangle;
        Position p1 = (Position)o1;
        Position p2 = (Position)o2;
        Position p3 = (Position)o3;
        Position p4 = (Position)o4;
        points.add(new Coordinate(p1.x,p1.y));
        decAs.add(-1L);decLs.add(-1L);decPs.add(-1L);
        points.add(new Coordinate(p2.x,p2.y));
        decAs.add(-1L);decLs.add(-1L);decPs.add(-1L);
        points.add(new Coordinate(p3.x,p3.y));
        decAs.add(-1L);decLs.add(-1L);decPs.add(-1L);
        points.add(new Coordinate(p4.x,p4.y));
        decAs.add(-1L);decLs.add(-1L);decPs.add(-1L);
        //System.out.println(" POLY4:" + p1.x + "," + p1.y + " ");
    }

    public void initPoly5(Object o1,Object o2,Object o3,Object o4,Object o5){
        this.type = pentagon;
        Position p1 = (Position)o1;
        Position p2 = (Position)o2;
        Position p3 = (Position)o3;
        Position p4 = (Position)o4;
        Position p5 = (Position)o5;
        points.add(new Coordinate(p1.x,p1.y));
        decAs.add(-1L);decLs.add(-1L);decPs.add(-1L);
        points.add(new Coordinate(p2.x,p2.y));
        decAs.add(-1L);decLs.add(-1L);decPs.add(-1L);
        points.add(new Coordinate(p3.x,p3.y));
        decAs.add(-1L);decLs.add(-1L);decPs.add(-1L);
        points.add(new Coordinate(p4.x,p4.y));
        decAs.add(-1L);decLs.add(-1L);decPs.add(-1L);
        points.add(new Coordinate(p5.x,p5.y));
        decAs.add(-1L);decLs.add(-1L);decPs.add(-1L);
        //System.out.println(" POLY5:" + p1.x + "," + p1.y + " ");
    }

    //Operator to process none params call.
    public static String Operator(String objName,String opName,Object params){
        Geometry g = GeometryDict.get(objName);
        if(g==null)return "Obj:" + objName +" not found!";

        if(opName.equals("draw")){
            return g.draw();
        }
        else if(opName.equals("changefill")){
            g.changefill();
            return null;
        }
        else if(opName.equals("corechangefill")){
            g.corechangefill();
            return null;
        }
        return strFailed;
    }

    //Operator to process Long array params.
    public static String iOperator(String objName,String opName,Object params){
        Geometry g = GeometryDict.get(objName);
        if(g==null)return "Obj:" + objName +" not found!";
        if(!(params instanceof ArrayList))return "paramters format error";
        List<Long> args = (List<Long>)params;

        if(opName.equals("draw")){
            if(args.size()==2) {
                return g.drawWithSize(args.get(0).intValue(), args.get(1).intValue());
            }
        }
        else if(opName.equals("changefill")){
                g.changefill();
                return null;
        }
        else if(opName.equals("rotate")){
            if(args.size()>0) {
                g.rotate(new Double(args.get(0)));
                return null;
            }
        }
        else if(opName.equals("mirror")){
            if(args.size()>0) {
                g.mirror(new Double(args.get(0)));
                return null;
            }
        }
        else if(opName.equals("core")){
            if(args.size()>0 && (args.size()%2==0)) {
                for(int i=0;i<args.size()/2;i++){
                    g.corePoints.add(new Coordinate(args.get(i*2),args.get(i*2+1)));
                }
                return null;
            }
        }
        else if(opName.equals("corerotate")){
            if(args.size()>0) {
                g.corerotate(new Double(args.get(0)));
                return null;
            }
        }
        else if(opName.equals("coremirror")){
            if(args.size()>0) {
                g.coremirror(new Double(args.get(0)));
                return null;
            }
        }
        else if(opName.equals("corechangefill")){
            g.corechangefill();
            return null;
        }
        else if(opName.equals("setdp")){
            if(args.size()>1) {
                g.setDecorateP(args.get(0).intValue(), args.get(1).intValue());
                return null;
            }
        }
        else if(opName.equals("setdps")){
            if(args.size()>1) {
                g.setDecoratePSize(args.get(0).intValue(), args.get(1).intValue());
                return null;
            }
        }
        else if(opName.equals("setdpf")){
            if(args.size()>1) {
                g.setDecoratePFill(args.get(0).intValue(), args.get(1).intValue());
                return null;
            }
        }
        else if(opName.equals("setda")){
            if(args.size()>1) {
                g.setDecorateA(args.get(0).intValue(), args.get(1).intValue());
                return null;
            }
        }
        else if(opName.equals("setdl")){
            if(args.size()>1) {
                g.setDecorateL(args.get(0).intValue(), args.get(1).intValue());
                return null;
            }
        }
        else if(opName.equals("adddp")){
            if(args.size() > 1) {
                g.addDecorateP(args.get(0).intValue(), args.get(1).intValue());
                return null;
            }
        }
        else if(opName.equals("addda")){
            if(args.size() > 1) {
                g.addDecorateA(args.get(0).intValue(), args.get(1).intValue());
                return null;
            }
        }
        else if(opName.equals("adddl")){
            if(args.size() > 1) {
                g.addDecorateL(args.get(0).intValue(), args.get(1).intValue());
                return null;
            }
        }
        else if(opName.equals("bitnotdp")){
            if(args.size() > 1) {
                int index = args.get(0).intValue();
                g.bitnotDecorateP(index,args.get(1).intValue());
                return null;
            }
        }
        else if(opName.equals("radddp")){
            if(args.size() > 1) {
                g.raddDecorateP(args.get(0).intValue(), args.get(1).intValue());
                return null;
            }
        }
        else if(opName.equals("raddda")){
            if(args.size() > 1) {
                g.raddDecorateA(args.get(0).intValue(), args.get(1).intValue());
                return null;
            }
        }
        else if(opName.equals("radddl")){
            if(args.size() > 1) {
                g.raddDecorateL(args.get(0).intValue(), args.get(1).intValue());
                return null;
            }
        }
        else if(opName.equals("exchgp")){
            if(args.size() > 1) {
                g.exchgDecorateP(args.get(0).intValue(), args.get(1).intValue());
                return null;
            }
        }
        else if(opName.equals("exchga")){
            if(args.size() > 1) {
                g.exchgDecorateA(args.get(0).intValue(), args.get(1).intValue());
                return null;
            }
        }
        else if(opName.equals("exchgl")){
            if(args.size() > 1) {
                g.exchgDecorateL(args.get(0).intValue(), args.get(1).intValue());
                return null;
            }
        }

        return strFailed;
    }

    //Operator to process Double array params.
    public static String dOperator(String objName,String opName,Object params){
        Geometry g = GeometryDict.get(objName);
        if(g==null)return "Obj:" + objName +" not found!";
        if(!(params instanceof ArrayList))return "paramters format error";
        List<Double> args = (List<Double>)params;

        if(opName.equals("draw")){
            if(args.size()==2) {
                return g.drawWithSize(args.get(0).intValue(),args.get(1).intValue());
            }
        }
        else if(opName.equals("rotate")){
            if(args.size()>0) {
                g.rotate(args.get(0));
                return null;
            }
        }
        else if(opName.equals("mirror")){
            if(args.size()>0) {
                g.mirror(args.get(0));
                return null;
            }
        }
        else if(opName.equals("core")){
            if(args.size()>0 && (args.size()%2==0)) {
                for(int i=0;i<args.size()/2;i++){
                    g.corePoints.add(new Coordinate(args.get(i*2),args.get(i*2+1)));
                }
                return null;
            }
        }
        else if(opName.equals("corerotate")){
            if(args.size()>0) {
                g.corerotate(args.get(0));
                return null;
            }
        }
        else if(opName.equals("coremirror")){
            if(args.size()>0) {
                g.coremirror(args.get(0));
                return null;
            }
        }
        else if(opName.equals("corechangefill")){
            g.corechangefill();
            return null;
        }
        return strFailed;
    }

}
