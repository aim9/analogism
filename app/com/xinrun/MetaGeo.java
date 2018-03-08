package com.xinrun;

import com.vividsolutions.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;



/**
 * Created by zhaoxueqian on 16/4/11.
 */
public class MetaGeo {
    public final static int quadrangle = 4;
    public final static int pentagon = 5;
    public int type;
    public String id;
    boolean hasCore = false;
    boolean hardLevel = true;

    public static final int trans_easy = 0;
    public static final int trans_hard = 1;
    public static final int trans_all = 2;
    public int random_trans_type = trans_all;

    public int random_disturb_count = 10;

    public static final String strSuccess = "success";
    public static final String strFailed = "failed";

    static public Dictionary<String,MetaGeo> MetaGeoDict = new Hashtable<String, MetaGeo>();

    public MetaGeo(){}

    public MetaGeo(String id){
        this.id = id;
        MetaGeoDict.put(id,this);
    }

    public List<Coordinate> points = new ArrayList<Coordinate>();
    public List<Coordinate> corePoints = new ArrayList<Coordinate>();
    public final static int baseDims = 8;
    public final static int decorDimsPerPoints = 4;

    public long getAbsDimension(){
        return baseDims+points.size()*decorDimsPerPoints; //One point decorator include 2 dims.
    }

    //PAL index from 0 to DecoratorMaxCount, 0,1,2 mean point0's P,A,L decorator,...
    public ArrayList<Long> allPossibleStateIndexes = new ArrayList<Long>();
    public ArrayList<Long> allPossibleStateRangeLimit = new ArrayList<Long>();
    public ArrayList<Long> polyPossibleStateIndexes = new ArrayList<Long>();
    public ArrayList<Long> polyPossibleStateRangeLimit = new ArrayList<Long>();
    public ArrayList<Long> decoratorPossibleStateIndexes = new ArrayList<Long>();
    public ArrayList<Long> decoratorPossibleStateRangeLimit = new ArrayList<Long>();

    //Realtive <=> Absolute index map.
    public Hashtable<Long,Long> pR2A = new Hashtable<>();
    public Hashtable<Long,Long> pA2R = new Hashtable<>();
    public Hashtable<Long,Long> aR2A = new Hashtable<>();
    public Hashtable<Long,Long> aA2R = new Hashtable<>();
    public Hashtable<Long,Long> lR2A = new Hashtable<>();
    public Hashtable<Long,Long> lA2R = new Hashtable<>();

    public void initWithPALindexes(List<Long> PALnindexes){
        state_rotate = 0L;  //index:0 -- state:0,1,2,3.
        state_hmirror = 0L; //index:1 -- state:0,1.
        state_vmirror = 0L; //index:2 -- state:0,1.
        state_fill = 0L;    //index:3 -- state:0,1.

        state_core_rotate = 0L;  //index:0 -- state:0,1,2,3.
        state_core_hmirror = 0L; //index:1 -- state:0,1.
        state_core_vmirror = 0L; //index:2 -- state:0,1.
        state_core_fill = 0L;    //index:3 -- state:0,1.

        state_decP_fill.clear();
        state_decP_size.clear();
        state_decA.clear();
        state_decL.clear();
        for(int i=0;i<type;i++){
            state_decP_fill.add(-1L);state_decP_size.add(-1L);state_decA.add(-1L);state_decL.add(-1L);
        }

        pNumber = aNumber =lNumber = 0;
        for(Long palIndex:PALnindexes){
            int number = palIndex.intValue()/3;
            switch(palIndex.intValue()%3){
                case 0:
                    state_decP_fill.set(number,0L);
                    state_decP_size.set(number,0L);
                    pR2A.put(new Long(pNumber), new Long(number));
                    pA2R.put(new Long(number),new Long(pNumber));
                    pNumber++;
                    break;
                case 1:
                    state_decA.set(number,0L);
                    aR2A.put(new Long(aNumber), new Long(number));
                    aA2R.put(new Long(number), new Long(aNumber));
                    aNumber++;
                    break;
                case 2:
                    state_decL.set(number,0L);
                    lR2A.put(new Long(lNumber), new Long(number));
                    lA2R.put(new Long(number), new Long(lNumber));
                    lNumber++;
                    break;
            }
        }

        allPossibleStateIndexes.clear();
        allPossibleStateRangeLimit.clear();
        polyPossibleStateIndexes.clear();
        polyPossibleStateRangeLimit.clear();
        decoratorPossibleStateIndexes.clear();
        decoratorPossibleStateRangeLimit.clear();

        allPossibleStateIndexes.add(0L);allPossibleStateIndexes.add(1L);allPossibleStateIndexes.add(2L);allPossibleStateIndexes.add(3L);
        allPossibleStateRangeLimit.add(4L);allPossibleStateRangeLimit.add(2L);allPossibleStateRangeLimit.add(2L);
        if(!hasCore){ allPossibleStateRangeLimit.add(2L); } else { allPossibleStateRangeLimit.add(1L); }
        allPossibleStateIndexes.add(4L);allPossibleStateIndexes.add(5L);allPossibleStateIndexes.add(6L);allPossibleStateIndexes.add(7L);
        allPossibleStateRangeLimit.add(4L);allPossibleStateRangeLimit.add(2L);allPossibleStateRangeLimit.add(2L);allPossibleStateRangeLimit.add(2L);

        polyPossibleStateIndexes.add(0L);polyPossibleStateIndexes.add(1L);polyPossibleStateIndexes.add(2L);polyPossibleStateIndexes.add(3L);
        polyPossibleStateRangeLimit.add(4L);polyPossibleStateRangeLimit.add(2L);polyPossibleStateRangeLimit.add(2L);
        if(!hasCore){ polyPossibleStateRangeLimit.add(2L); } else { polyPossibleStateRangeLimit.add(1L); }
        polyPossibleStateIndexes.add(4L);polyPossibleStateIndexes.add(5L);polyPossibleStateIndexes.add(6L);polyPossibleStateIndexes.add(7L);
        polyPossibleStateRangeLimit.add(4L);polyPossibleStateRangeLimit.add(2L);polyPossibleStateRangeLimit.add(2L);polyPossibleStateRangeLimit.add(2L);

        for(int i=0;i<points.size();i++){
            if(state_decP_fill.get(i)>=0){
                allPossibleStateIndexes.add(new Long(baseDims+decorDimsPerPoints*i));
                allPossibleStateRangeLimit.add(2L);
                decoratorPossibleStateIndexes.add(new Long(baseDims+decorDimsPerPoints*i));
                decoratorPossibleStateRangeLimit.add(2L);            }
            if(state_decP_size.get(i)>=0){
                allPossibleStateIndexes.add(new Long(baseDims+decorDimsPerPoints*i)+1);
                allPossibleStateRangeLimit.add(2L);
                decoratorPossibleStateIndexes.add(new Long(baseDims+decorDimsPerPoints*i)+1);
                decoratorPossibleStateRangeLimit.add(2L);
            }
            if(state_decA.get(i)>=0){
                allPossibleStateIndexes.add(new Long(baseDims+decorDimsPerPoints*i)+2);
                allPossibleStateRangeLimit.add(3L);
                decoratorPossibleStateIndexes.add(new Long(baseDims+decorDimsPerPoints*i)+2);
                decoratorPossibleStateRangeLimit.add(3L);
            }
            if(state_decL.get(i)>=0){
                allPossibleStateIndexes.add(new Long(baseDims+decorDimsPerPoints*i)+3);
                allPossibleStateRangeLimit.add(2L);
                decoratorPossibleStateIndexes.add(new Long(baseDims+decorDimsPerPoints*i)+3);
                decoratorPossibleStateRangeLimit.add(2L);
            }
        }
    }

    //Set P+A+L count.
    public int topologyMode;//0:total number;  1:P,A,L dividual total number; 2:P,A,L dividual certain position.

    public ArrayList<ArrayList<Long>> AllPALindexes;
    public int palTotalNumber;
    public long setPALTotalNumber(int count){
        if(count>(type*3))return -1;
        AllPALindexes = null;
        topologyMode = 0;

        palTotalNumber = count;

        return palTotalNumber;
    }

    public int pNumber;
    public int aNumber;
    public int lNumber;
    public ArrayList<ArrayList<Long>> AllPindexes;
    public ArrayList<ArrayList<Long>> AllAindexes;
    public ArrayList<ArrayList<Long>> AllLindexes;

    public long setPALNumber(int pcount,int acount,int lcount){
        if(pcount>type||acount>type||lcount>type)return -1;
        AllPALindexes = null;
        topologyMode = 1;

        pNumber = pcount;
        aNumber = acount;
        lNumber = lcount;
        palTotalNumber = pNumber+aNumber+lNumber;

        return palTotalNumber;
    }

    //layouts is points index, 4or5,   0,1,2,3 for poly4, 0,1,2,3,4 for poly5.
    public long setPLayout(ArrayList<Long> playouts){
        AllPALindexes = null;
        topologyMode = 2;
        pNumber = playouts.size();

        ArrayList<Long> allPossiblePs = new ArrayList<Long>();
        for(Long p:playouts){
            if(p>=0&&p<type)allPossiblePs.add((new Long(3*p)));
        }
        AllPindexes = new ArrayList<ArrayList<Long>>();
        if(allPossiblePs.size()>0)AllPindexes.add(allPossiblePs);
        return AllPindexes.size();
    }
    //layouts is angular index, 4or5,   0,1,2,3 for poly4, 0,1,2,3,4 for poly5.
    public long setALayout(ArrayList<Long> alayouts){
        AllPALindexes = null;
        topologyMode = 2;
        aNumber = alayouts.size();

        ArrayList<Long> allPossibleAs = new ArrayList<Long>();
        for(Long a:alayouts){
            if(a>=0&&a<type)allPossibleAs.add((new Long(3*a+1)));
        }
        AllAindexes = new ArrayList<ArrayList<Long>>();
        if(allPossibleAs.size()>0)AllAindexes.add(allPossibleAs);
        return AllAindexes.size();
    }
    //layouts is line index, 4or5,   0,1,2,3 for poly4, 0,1,2,3,4 for poly5.
    public long setLLayout(ArrayList<Long> llayouts){
        AllPALindexes = null;
        topologyMode = 2;
        lNumber = llayouts.size();

        ArrayList<Long> allPossibleLs = new ArrayList<Long>();
        for(Long l:llayouts){
            if(l>=0&&l<type)allPossibleLs.add((new Long(3*l+2)));
        }
        AllLindexes = new ArrayList<ArrayList<Long>>();
        if(allPossibleLs.size()>0)AllLindexes.add(allPossibleLs);
        return AllLindexes.size();
    }

    boolean checkPACollision(ArrayList<Long> pal){
        long state = 0;
        for(Long v:pal){
            if(v.intValue()%3!=2){//We don't check L decorator.
                if( (state&(0x01<<(v.intValue()/3))) != 0L )return true;
                state |= (0x01<<(v.intValue()/3));
            }
        }
        return false;
    }

    public long genPALTopology() {
        switch(topologyMode){
            case 0:
                ArrayList<Long> allPossiblePALs = new ArrayList<Long>();
                for(int i=0;i<type*3;i++){
                    allPossiblePALs.add((new Long(i)));
                }
                ArrayList<ArrayList<Long>> temp = Utils.collectMfromN(allPossiblePALs,palTotalNumber);
                AllPALindexes = new ArrayList<ArrayList<Long>>();
                for(ArrayList<Long> pal:temp){
                    if(!checkPACollision(pal))AllPALindexes.add(pal);
                }
                break;
            case 1:
                ArrayList<Long> allPossiblePs = new ArrayList<Long>();
                ArrayList<Long> allPossibleAs = new ArrayList<Long>();
                ArrayList<Long> allPossibleLs = new ArrayList<Long>();
                for(int i=0;i<type;i++){
                    allPossiblePs.add((new Long(3*i)));
                    allPossibleAs.add((new Long(3*i+1)));
                    allPossibleLs.add((new Long(3*i+2)));
                }
                AllPindexes = Utils.collectMfromN(allPossiblePs,pNumber);
                AllAindexes = Utils.collectMfromN(allPossibleAs,aNumber);
                AllLindexes = Utils.collectMfromN(allPossibleLs, lNumber);
                temp = Utils.CrossMultiply(Utils.CrossMultiply(AllPindexes, AllAindexes), AllLindexes);
                AllPALindexes = new ArrayList<ArrayList<Long>>();
                for(ArrayList<Long> pal:temp){
                    if(!checkPACollision(pal))AllPALindexes.add(pal);
                }
                break;
            case 2:
                if (AllPindexes == null){ AllPindexes = new ArrayList<ArrayList<Long>>(); pNumber = 0; }
                if (AllAindexes == null){ AllAindexes = new ArrayList<ArrayList<Long>>(); aNumber = 0; }
                if (AllLindexes == null){ AllLindexes = new ArrayList<ArrayList<Long>>(); lNumber = 0; }
                palTotalNumber = pNumber+aNumber+lNumber;
                AllPALindexes = Utils.CrossMultiply(Utils.CrossMultiply(AllPindexes, AllAindexes), AllLindexes);
                break;
        }
        return AllPALindexes.size();
    }

    //Go certain situation, is set pal layout individually, this index must be 0.
    public int currentPALSituation= -1;
    public long setPALSituation(int index) {
        if(index>=AllPALindexes.size())return -1;
        currentPALSituation = index;
        initWithPALindexes(AllPALindexes.get(index));
        long possible = 1;
        for(long limit:allPossibleStateRangeLimit){
            possible *= limit;
        }
        return possible;
    }

    public int getPALAbsIndex(int index){
        long absindex = allPossibleStateIndexes.get(index);
        if(absindex<baseDims)return -1;
        return (int)absindex/decorDimsPerPoints;
    }

    public long disturbNumber;
    public long transNumber;
    public ArrayList<ArrayList<Long>> AllDisturbOps;
    public ArrayList<ArrayList<Long>> AllTransOps;

    boolean checkRot(ArrayList<Long> transops){
        for(int i=0;i<transops.size();i++){
            int ops = transops.get(i).intValue();
            if(i>=allPossibleStateIndexes.size())continue;
            int absIndex = allPossibleStateIndexes.get(i).intValue();
            if(ops>0){//valid ops.
                if(absIndex==0)return true;
            }
        }
        return false;
    }
    boolean checkMirror(ArrayList<Long> transops){
        for(int i=0;i<transops.size();i++){
            int ops = transops.get(i).intValue();
            if(i>=allPossibleStateIndexes.size())continue;
            int absIndex = allPossibleStateIndexes.get(i).intValue();
            if(ops>0){//valid ops.
                if( absIndex==1 || absIndex==2 )return true;
            }
        }
        return false;
    }

    boolean checkCoreRot(ArrayList<Long> transops){
        for(int i=0;i<transops.size();i++){
            int ops = transops.get(i).intValue();
            if(i>=allPossibleStateIndexes.size())continue;
            int absIndex = allPossibleStateIndexes.get(i).intValue();
            if(ops>0){//valid ops.
                if(absIndex==4)return true;
            }
        }
        return false;
    }
    boolean checkCoreMirror(ArrayList<Long> transops){
        for(int i=0;i<transops.size();i++){
            int ops = transops.get(i).intValue();
            if(i>=allPossibleStateIndexes.size())continue;
            int absIndex = allPossibleStateIndexes.get(i).intValue();
            if(ops>0){//valid ops.
                if( absIndex==5 || absIndex==6 )return true;
            }
        }
        return false;
    }

    boolean checkRotMirrorCollision(ArrayList<Long> transops){
        return checkRot(transops)&&checkMirror(transops);
    }

    public boolean checkAnswerRootValid(ArrayList<Long> root,ArrayList<Long> transops,ArrayList<Long> answerroot){
        for(int i=0;i<transops.size();i++){
            int ops = transops.get(i).intValue();
            int initState = root.get(i).intValue();
            int answerState = answerroot.get(i).intValue();
            if(i>=allPossibleStateIndexes.size())continue;
            int absIndex = allPossibleStateIndexes.get(i).intValue();
            if(ops>0){//valid ops.
                if(absIndex>=baseDims){
                    int palindex = (absIndex-baseDims)/decorDimsPerPoints;
                    switch((absIndex-baseDims)%decorDimsPerPoints){
                        case 2:
                            if(ops==1){
                                if(initState==0||initState==1){
                                    if(answerState==2)return false;
                                }
                                if(initState==2){
                                    if(answerState!=2)return false;
                                }
                            }
                            if(ops==2){
                                if(initState==0){
                                    if(answerState!=0)return false;
                                }
                                if(initState==1||initState==2){
                                    if(answerState==0)return false;
                                }

                            }
                            break;
                    }
                }
            }
        }
        return true;
    }

    Long getAngularStateWithRelativeIndex(int rindex, ArrayList<Long> vstate){
        int absIndex= aR2A.get(new Long(rindex)).intValue();
        for(int i=0;i<allPossibleStateIndexes.size();i++){
            if(allPossibleStateIndexes.get(i).intValue()==(baseDims+absIndex*decorDimsPerPoints+2)){
                return vstate.get(i);
            }
        }
        return null;
    }

    public boolean checkAnswerRootValidWithTransOP(ArrayList<Long> root,ArrayList<Long> transops,MetaGeo answerMG, ArrayList<Long> answerroot){
        for(int i=0;i<transops.size();i++){
            int ops = transops.get(i).intValue();
            int initState = root.get(i).intValue();
            if(i>=allPossibleStateIndexes.size())continue;
            int absIndex = allPossibleStateIndexes.get(i).intValue();
            if(ops>0){//valid ops.
                if(absIndex>=baseDims){
                    int palindex = (absIndex-baseDims)/decorDimsPerPoints;
                    switch((absIndex-baseDims)%decorDimsPerPoints){
                        case 2://We only check angular decorator.
                            Long aState = answerMG.getAngularStateWithRelativeIndex(aA2R.get(new Long(palindex)).intValue(),answerroot);
                            if(aState==null)return false;
                            int answerState = aState.intValue();
                            if(ops==1){
                                if(initState==0||initState==1){
                                    if(answerState==2)return false;
                                }
                                if(initState==2){
                                    if(answerState!=2)return false;
                                }
                            }
                            if(ops==2){
                                if(initState==0){
                                    if(answerState!=0)return false;
                                }
                                if(initState==1||initState==2){
                                    if(answerState==0)return false;
                                }

                            }
                            break;
                    }
                }
            }
        }
        return true;
    }

    public ArrayList<Long> currentDisturbIndexes;
    public long currentTransIndex;
    public long setDisturbsNumber(){
        if(disturbNumber>allPossibleStateIndexes.size())disturbNumber = allPossibleStateIndexes.size();
        /*
        AllDisturbOps = Utils.GenOperators((int) disturbNumber, allPossibleStateRangeLimit);
        */
        /* we now gen all disturb ops <= disturb number. */
        if(AllDisturbOps!=null)AllDisturbOps.clear();
        else AllDisturbOps = new ArrayList<ArrayList<Long>>();
        for(int c=(int)disturbNumber;c>0;c--) {
            AllDisturbOps.addAll( Utils.GenOperators(c, allPossibleStateRangeLimit) );
        }
        return AllDisturbOps.size();
    }

    public long setTransNumberMustRotate(){
        if(transNumber>allPossibleStateIndexes.size())transNumber = allPossibleStateIndexes.size();
        AllTransOps = Utils.GenOperatorsMustRotate((int) transNumber, allPossibleStateRangeLimit);
        if(!hardLevel){
            ArrayList<ArrayList<Long>> temp = new ArrayList<ArrayList<Long>>();
            for(ArrayList<Long> ops:AllTransOps){
                if((!checkRotMirrorCollision(ops)) && (!checkCoreRot(ops)) && (!checkCoreMirror(ops)) ){
                    temp.add(ops);
                }
            }
            AllTransOps = temp;
        }
        return AllTransOps.size();
    }

    public long setTransNumber(){
        if(transNumber>allPossibleStateIndexes.size())transNumber = allPossibleStateIndexes.size();
        AllTransOps = Utils.GenOperators((int) transNumber, allPossibleStateRangeLimit);
        if(!hardLevel){
            ArrayList<ArrayList<Long>> temp = new ArrayList<ArrayList<Long>>();
            for(ArrayList<Long> ops:AllTransOps){
                if((!checkRotMirrorCollision(ops)) && (!checkCoreRot(ops)) && (!checkCoreMirror(ops)) ){
                    temp.add(ops);
                }
            }
            AllTransOps = temp;
        }
        return AllTransOps.size();
    }

    //print all possible trans ops.
    //Pls Call setPALCount first.
    public String printAllTrans(){
        //Print poly define at first.
        String result = "All translators:\n";

        //Print trans rules.
        for(ArrayList<Long> transops:AllTransOps){
            result += "------------------------------------------------------------\n";
            result += toStringTransOPS(transops);
        }

        return result;
    }

    public String printAllDisturbs(){
        //Print poly define at first.
        String result = "All disturb translators:\n";

        //Print trans rules.
        for(ArrayList<Long> transops:AllDisturbOps){
            result += "------------------------------------------------------------\n";
            result += toStringTransOPS(transops);
        }

        return result;
    }

    public String toStringTransOPS(ArrayList<Long> transops){
        String result = "";
        for(int i=0;i<transops.size();i++){
            int ops = transops.get(i).intValue();
            if(i>=allPossibleStateIndexes.size())continue;
            int absIndex = allPossibleStateIndexes.get(i).intValue();
            if(ops>0){//valid ops.
                result+=id+".";
                if(absIndex<baseDims){
                    switch(absIndex){
                        case 0:
                            if(ops==1)result += "rotate(90)\n";
                            else if(ops==2)result += "rotate(180)\n";
                            else if(ops==3)result += "rotate(270)\n";
                            break;
                        case 1:
                            result += "mirror(90)\n";
                            break;
                        case 2:
                            result += "mirror(0)\n";
                            break;
                        case 3:
                            result += "changefill(1)\n";
                            break;
                        case 4:
                            if(ops==1)result += "corerotate(90)\n";
                            else if(ops==2)result += "corerotate(180)\n";
                            else if(ops==3)result += "corerotate(270)\n";
                            break;
                        case 5:
                            result += "coremirror(90)\n";
                            break;
                        case 6:
                            result += "coremirror(0)\n";
                            break;
                        case 7:
                            result += "corechangefill(1)\n";
                            break;                    }
                }
                else{
                    int palindex = (absIndex-baseDims)/decorDimsPerPoints;
                    switch((absIndex-baseDims)%decorDimsPerPoints){
                        case 0:
                            result += "adddp("+palindex+",1)\n";
                            break;
                        case 1:
                            result += "adddp("+palindex+",2)\n";
                            break;
                        case 2:
                            result += "addda("+palindex+","+ops+")\n";
                            break;
                        case 3:
                            result += "adddl("+palindex+",1)\n";
                            break;
                    }
                }
            }
        }
        return result;
    }

    public String toRelativeStringTransOPS(ArrayList<Long> transops){
        String result = "";
        for(int i=0;i<transops.size();i++){
            int ops = transops.get(i).intValue();
            if(i>=allPossibleStateIndexes.size())continue;
            int absIndex = allPossibleStateIndexes.get(i).intValue();
            if(ops>0){//valid ops.
                result+=id+".";
                if(absIndex<baseDims){
                    switch(absIndex){
                        case 0:
                            if(ops==1)result += "rotate(90)\n";
                            else if(ops==2)result += "rotate(180)\n";
                            else if(ops==3)result += "rotate(270)\n";
                            break;
                        case 1:
                            result += "mirror(90)\n";
                            break;
                        case 2:
                            result += "mirror(0)\n";
                            break;
                        case 3:
                            result += "changefill(1)\n";
                            break;
                        case 4:
                            if(ops==1)result += "corerotate(90)\n";
                            else if(ops==2)result += "corerotate(180)\n";
                            else if(ops==3)result += "corerotate(270)\n";
                            break;
                        case 5:
                            result += "coremirror(90)\n";
                            break;
                        case 6:
                            result += "coremirror(0)\n";
                            break;
                        case 7:
                            result += "corechangefill(1)\n";
                            break;                    }
                }
                else{
                    int palindex = (absIndex-baseDims)/decorDimsPerPoints;
                    int relpalindex = 0;
                    switch((absIndex-baseDims)%decorDimsPerPoints){
                        case 0:
                            relpalindex = pA2R.get(new Long(palindex)).intValue();
                            result += "radddp("+relpalindex+",1)\n";
                            break;
                        case 1:
                            relpalindex = pA2R.get(new Long(palindex)).intValue();
                            result += "radddp("+relpalindex+",2)\n";
                            break;
                        case 2:
                            relpalindex = aA2R.get(new Long(palindex)).intValue();
                            result += "raddda("+relpalindex+","+ops+")\n";
                            break;
                        case 3:
                            relpalindex = lA2R.get(new Long(palindex)).intValue();
                            result += "radddl("+relpalindex+",1)\n";
                            break;
                    }
                }
            }
        }
        return result;
    }

    public String toRelativeShortStringFromTransOPS(ArrayList<Long> transops){
        String result = "";
        for(int i=0;i<transops.size();i++){
            int ops = transops.get(i).intValue();
            if(i>=allPossibleStateIndexes.size())continue;
            int absIndex = allPossibleStateIndexes.get(i).intValue();
            if(ops>0){//valid ops.
                if(absIndex<baseDims){
                    switch(absIndex){
                        case 0:
                            if(ops==1)result += "R1";
                            else if(ops==2)result += "R2";
                            else if(ops==3)result += "R3";
                            break;
                        case 1:
                            result += "M1";
                            break;
                        case 2:
                            result += "M2";
                            break;
                        case 3:
                            result += "F1";
                            break;
                        case 4:
                            if(ops==1)result += "r1";
                            else if(ops==2)result += "r2";
                            else if(ops==3)result += "r3";
                            break;
                        case 5:
                            result += "m1";
                            break;
                        case 6:
                            result += "m2";
                            break;
                        case 7:
                            result += "f1";
                            break;                    }
                }
                else{
                    int palindex = (absIndex-baseDims)/decorDimsPerPoints;
                    int relpalindex = 0;
                    switch((absIndex-baseDims)%decorDimsPerPoints){
                        case 0:
                            relpalindex = pA2R.get(new Long(palindex)).intValue();
                            result += "P"+relpalindex+"1";
                            break;
                        case 1:
                            relpalindex = pA2R.get(new Long(palindex)).intValue();
                            result += "P"+relpalindex+"2";
                            break;
                        case 2:
                            relpalindex = aA2R.get(new Long(palindex)).intValue();
                            result += "A"+relpalindex+""+ops;
                            break;
                        case 3:
                            relpalindex = lA2R.get(new Long(palindex)).intValue();
                            result += "L"+relpalindex+"1";
                            break;
                    }
                }
            }
        }
        return result;
    }

    /*
    public ArrayList<Long> toTransOPSFromRelativeString(String relOpsStr){
        int i=0;
        char[] relOPS = relOpsStr.toCharArray();
        ArrayList<Long> ops = new ArrayList<>();
        while(i<relOpsStr.length()){
            switch(relOPS[i]){
                case 'R':
                    i++;


                    break;
                case 'M':
                    break;
                case 'F':
                    break;
                case 'r':
                    break;
                case 'm':
                    break;
                case 'f':
                    break;
                case 'P':
                    break;
                case 'A':
                    break;
                case 'L':
                    break;
            }

        }
    }*/

    public boolean isHardLevel(ArrayList<Long> transops){
        return checkRotMirrorCollision(transops)||checkCoreMirror(transops)||checkCoreRot(transops);
    }

    public String toShortStringTransOPS(ArrayList<Long> transops){
        String result0 = "";
        String result1 = "";
        String result2 = "";
        int opsCnt = 0;
        for(int i=0;i<transops.size();i++){
            int ops = transops.get(i).intValue();
            if(i>=allPossibleStateIndexes.size())continue;
            int absIndex = allPossibleStateIndexes.get(i).intValue();
            if(ops>0){//valid ops.
                if(absIndex<baseDims){
                    switch(absIndex){
                        case 0:
                            if(ops==1)result0 += "R1";
                            else if(ops==2)result0 += "R2";
                            else if(ops==3)result0 += "R3";
                            opsCnt++;
                            break;
                        case 1:
                            result0 += "M1";
                            opsCnt++;
                            break;
                        case 2:
                            result0 += "M2";
                            opsCnt++;
                            break;
                        case 3:
                            result0 += "F1";
                            opsCnt++;
                            break;
                        case 4:
                            if(ops==1)result1 += "r1";
                            else if(ops==2)result1 += "r2";
                            else if(ops==3)result1 += "r3";
                            opsCnt++;
                            break;
                        case 5:
                            result1 += "m1";
                            opsCnt++;
                            break;
                        case 6:
                            result1 += "m2";
                            opsCnt++;
                            break;
                        case 7:
                            result1 += "f1";
                            opsCnt++;
                            break;                    }
                }
                else{
                    int palindex = (absIndex-baseDims)/decorDimsPerPoints;
                    switch((absIndex-baseDims)%decorDimsPerPoints){
                        case 0:
                            result2 += "P"+palindex+"1";
                            opsCnt++;
                            break;
                        case 1:
                            result2 += "P"+palindex+"2";
                            opsCnt++;
                            break;
                        case 2:
                            result2 += "A"+palindex+""+ops;
                            opsCnt++;
                            break;
                        case 3:
                            result2 += "L"+palindex+"1";
                            opsCnt++;
                            break;
                    }
                }
            }
        }
        String strCnt = "("+Integer.toString(opsCnt)+")";
        if( isHardLevel(transops) ){
            return result0+result1+result2+strCnt+"[H]";
        }
        return result0+result1+result2+strCnt;
    }

    /* Gen shock PAL random code */
    public String strShockPAL;
    public String shortStrShockPAL;
    public void shockPAL(){
        strShockPAL = "";
        shortStrShockPAL = "";
        for(int i=0;i<pNumber;i++){
            int absIndex = pR2A.get(new Long(i)).intValue();
            int randomIndex = (int)(Math.random()*type);
            shortStrShockPAL += "XP"+absIndex+randomIndex;
            strShockPAL += id+".exchgp("+absIndex+","+randomIndex+")\n";
        }
        for(int i=0;i<aNumber;i++){
            int absIndex = aR2A.get(new Long(i)).intValue();
            int randomIndex = (int)(Math.random()*type);
            shortStrShockPAL += "XA"+absIndex+randomIndex;
            strShockPAL += id+".exchga("+absIndex+","+randomIndex+")\n";
        }
        for(int i=0;i<lNumber;i++){
            int absIndex = lR2A.get(new Long(i)).intValue();
            int randomIndex = (int)(Math.random()*type);
            shortStrShockPAL += "XL"+absIndex+randomIndex;
            strShockPAL += id+".exchgl("+absIndex+","+randomIndex+")\n";
        }
    }

    public String toStringDefine(){
        String result = "";
        switch(type){
            case quadrangle:
                result +="quadrangle ";
                break;
            case pentagon:
                result +="pentagon ";
                break;
        }
        result+=id+" := (";
        for(int i=0;i<points.size();i++){
            Coordinate p = points.get(i);
            if(i!=0)result+=",(" + p.x + ", " + p.y + ")";
            else result +="(" + p.x + ", " + p.y + ")";
        }
        result += ")";
        //core define:
        if(hasCore && corePoints.size()>0) {
            result += "\n"+id+".core(";
            for(int i=0;i<corePoints.size();i++){
                Coordinate p = corePoints.get(i);
                result += p.x + "," + p.y;
                if(i!=(corePoints.size()-1))result+=",";
            }
            result += ")";
        }
        return result;
    }

    public String toStringInitialOps(){
        if(InitialOps.size()>0)return toStringInitOPS(InitialOps);
        return "";
    }

    public ArrayList<Long> InitialOps;
    String toStringInitOPS(ArrayList<Long> transops){
        String result = "";
        InitialOps = Utils.myDeepClone(transops);
        for(int i=0;i<transops.size();i++){
            int ops = transops.get(i).intValue();
            if(i>=allPossibleStateIndexes.size())continue;
            int absIndex = allPossibleStateIndexes.get(i).intValue();
                result+=id+".";
                if(absIndex<baseDims){
                    if(ops>0) {//valid ops.
                        switch (absIndex) {
                            case 0:
                                if (ops == 1) result += "rotate(90)\n";
                                else if (ops == 2) result += "rotate(180)\n";
                                else if (ops == 3) result += "rotate(270)\n";
                                break;
                            case 1:
                                result += "mirror(90)\n";
                                break;
                            case 2:
                                result += "mirror(0)\n";
                                break;
                            case 3:
                                result += "changefill(1)\n";
                                break;
                            case 4:
                                if (ops == 1) result += "corerotate(90)\n";
                                else if (ops == 2) result += "corerotate(180)\n";
                                else if (ops == 3) result += "corerotate(270)\n";
                                break;
                            case 5:
                                result += "coremirror(90)\n";
                                break;
                            case 6:
                                result += "coremirror(0)\n";
                                break;
                            case 7:
                                result += "corechangefill(1)\n";
                                break;
                        }
                    }
                }
                else{
                    int palindex = (absIndex-baseDims)/decorDimsPerPoints;
                    switch((absIndex-baseDims)%decorDimsPerPoints){
                        case 0:
                            result += "setdps("+palindex+","+ops+")\n";
                            break;
                        case 1:
                            result += "setdpf("+palindex+","+ops+")\n";
                            break;
                        case 2:
                            result += "setda("+palindex+","+ops+")\n";
                            break;
                        case 3:
                            result += "setdl("+palindex+","+ops+")\n";
                            break;
                    }
                }
            }
        return result;
    }

    public String toStringPALTopology(ArrayList<Long> PALtopology){
        String result = "";
        for(Long palindex:PALtopology) {
            switch (palindex.intValue() % 3) {
                case 0:
                    result += id+"."+"setdp(" + palindex/3 + ",0)\n";
                    break;
                case 1:
                    result += id+"."+"setda(" + palindex/3 + ",0)\n";
                    break;
                case 2:
                    result += id+"."+"setdl(" + palindex/3 + ",0)\n";
                    break;
            }
        }
        return result;
    }

    public String toShortStringPALTopology(ArrayList<Long> PALtopology){
        String resultP = "";
        String resultA = "";
        String resultL = "";
        for(Long palindex:PALtopology) {
            switch (palindex.intValue() % 3) {
                case 0:
                    resultP += "P" + palindex/3;
                    break;
                case 1:
                    resultA += "A" + palindex/3;
                    break;
                case 2:
                    resultL += "L" + palindex/3;
                    break;
            }
        }
        return resultP+resultA+resultL;
    }


    //Must called after setPALSituation().
    public long getGlobalStateSize(){
        long result = 1;
        for(Long range:allPossibleStateRangeLimit){
            result*=range;
        }
        return result;
    }

    //Must called after setPALSituation().
    public long currentGlobalStateValue = 0;
    public long setGlobalStateValue(long globalState){
        currentGlobalStateValue = globalState;
        return currentGlobalStateValue;
    }

    public ArrayList<Long> toArrayGlobalState(long globalState){
        ArrayList<Long> result = new ArrayList<>();
        for(int i = allPossibleStateRangeLimit.size()-1;i>=0;i--)
        {
            long vrange = allPossibleStateRangeLimit.get(i);
            long v = globalState%vrange;
            globalState/=vrange;

            result.add(0,v);
        }
        return result;
    }

    public String toStringGlobalState(long globalState){
        String result = "";
        for(int i = allPossibleStateRangeLimit.size()-1;i>=0;i--)
        {
            long vrange = allPossibleStateRangeLimit.get(i);
            long v = globalState%vrange;
            globalState/=vrange;
            if(i<baseDims){
                if(v>0) {//valid ops.
                    switch (i) {
                        case 0:
                            if (v == 1) result += id+"."+"rotate(90)\n";
                            else if (v == 2) result += id+"."+"rotate(180)\n";
                            else if (v == 3) result += id+"."+"rotate(270)\n";
                            break;
                        case 1:
                            result += id+"."+"mirror(90)\n";
                            break;
                        case 2:
                            result += id+"."+"mirror(0)\n";
                            break;
                        case 3:
                            result += id+"."+"changefill(1)\n";
                            break;
                        case 4:
                            if (v == 1) result += id+"."+"corerotate(90)\n";
                            else if (v == 2) result += id+"."+"corerotate(180)\n";
                            else if (v == 3) result += id+"."+"corerotate(270)\n";
                            break;
                        case 5:
                            result += id+"."+"coremirror(90)\n";
                            break;
                        case 6:
                            result += id+"."+"coremirror(0)\n";
                            break;
                        case 7:
                            result += id+"."+"corechangefill(1)\n";
                            break;
                    }
                }
            }
            else {
                switch ((allPossibleStateIndexes.get(i).intValue() - baseDims) % decorDimsPerPoints) {
                    case 0:
                        result += id+"."+"setdps(" + (allPossibleStateIndexes.get(i).intValue() - baseDims) / decorDimsPerPoints + "," + v + ")\n";
                        break;
                    case 1:
                        result += id+"."+"setdpf(" + (allPossibleStateIndexes.get(i).intValue() - baseDims) / decorDimsPerPoints + "," + v + ")\n";
                        break;
                    case 2:
                        result += id+"."+"setda(" + (allPossibleStateIndexes.get(i).intValue() - baseDims) / decorDimsPerPoints + "," + v + ")\n";
                        break;
                    case 3:
                        result += id+"."+"setdl(" + (allPossibleStateIndexes.get(i).intValue() - baseDims) / decorDimsPerPoints + "," + v + ")\n";
                        break;
                }
            }
        }
        return result;
    }

    public String toShortStringGlobalState(long globalState){
        String result = "";
        String result2 = "";
        for(int i = allPossibleStateRangeLimit.size()-1;i>=0;i--)
        {
            long vrange = allPossibleStateRangeLimit.get(i);
            long v = globalState%vrange;
            globalState/=vrange;
            if(i<baseDims){
                if(v>0) {//valid ops.
                    switch (i) {
                        case 0:
                            if (v == 1) result += "R1";
                            else if (v == 2) result += "R2";
                            else if (v == 3) result += "R3";
                            break;
                        case 1:
                            result += "M1";
                            break;
                        case 2:
                            result += "M2";
                            break;
                        case 3:
                            result += "F1";
                            break;
                        case 4:
                            if (v == 1) result += "r1";
                            else if (v == 2) result += "r2";
                            else if (v == 3) result += "r3";
                            break;
                        case 5:
                            result += "m1";
                            break;
                        case 6:
                            result += "m2";
                            break;
                        case 7:
                            result += "f1";
                            break;
                    }
                }
            }
            else {
                switch ((allPossibleStateIndexes.get(i).intValue() - baseDims) % decorDimsPerPoints) {
                    case 0:
                        result2 += "PS" + (allPossibleStateIndexes.get(i).intValue() - baseDims) / decorDimsPerPoints + v;
                        break;
                    case 1:
                        result2 += "PF" + (allPossibleStateIndexes.get(i).intValue() - baseDims) / decorDimsPerPoints + v;
                        break;
                    case 2:
                        result2 += "A" + (allPossibleStateIndexes.get(i).intValue() - baseDims) / decorDimsPerPoints + v;
                        break;
                    case 3:
                        result2 += "L" + (allPossibleStateIndexes.get(i).intValue() - baseDims) / decorDimsPerPoints + v;
                        break;
                }
            }
        }
        return result+result2;
    }


    //Global index.
    public Long state_rotate = 0L;  //index:0 -- state:0,1,2,3.
    public Long state_hmirror = 0L; //index:1 -- state:0,1.
    public Long state_vmirror = 0L; //index:2 -- state:0,1.
    public Long state_fill = 0L;    //index:3 -- state:0,1.

    public Long state_core_rotate = 0L;  //index:0 -- state:0,1,2,3.
    public Long state_core_hmirror = 0L; //index:1 -- state:0,1.
    public Long state_core_vmirror = 0L; //index:2 -- state:0,1.
    public Long state_core_fill = 0L;    //index:3 -- state:0,1.

    public List<Long> state_decP_fill = new ArrayList<Long>(); //index:4+4*n -- state:0,1.
    public List<Long> state_decP_size = new ArrayList<Long>(); //index:4+4*n+1 -- state:0,1.
    public List<Long> state_decA = new ArrayList<Long>(); //index:4+4*n+2 -- state:0,1,2.
    public List<Long> state_decL = new ArrayList<Long>(); //index:4+4*n+3 -- state:0,1.


    public void initPoly4(Object o1,Object o2,Object o3,Object o4){
        this.type = quadrangle;
        Position p1 = (Position)o1;
        Position p2 = (Position)o2;
        Position p3 = (Position)o3;
        Position p4 = (Position)o4;
        points.add(new Coordinate(p1.x,p1.y));
        points.add(new Coordinate(p2.x,p2.y));
        points.add(new Coordinate(p3.x,p3.y));
        points.add(new Coordinate(p4.x,p4.y));
        for(int i=0;i<type;i++){
            state_decP_fill.add(-1L);state_decP_size.add(-1L);state_decA.add(-1L);state_decL.add(-1L);
        }
    }

    public void initPoly5(Object o1,Object o2,Object o3,Object o4,Object o5){
        this.type = pentagon;
        Position p1 = (Position)o1;
        Position p2 = (Position)o2;
        Position p3 = (Position)o3;
        Position p4 = (Position)o4;
        Position p5 = (Position)o5;
        points.add(new Coordinate(p1.x,p1.y));
        points.add(new Coordinate(p2.x,p2.y));
        points.add(new Coordinate(p3.x,p3.y));
        points.add(new Coordinate(p4.x,p4.y));
        points.add(new Coordinate(p5.x,p5.y));
        for(int i=0;i<type;i++){
            state_decP_fill.add(-1L);state_decP_size.add(-1L);state_decA.add(-1L);state_decL.add(-1L);
        }
    }

    //Operator to process none params call.
    public static String Operator(String objName,String opName,Object params){
        MetaGeo g = MetaGeoDict.get(objName);
        if(g==null)return "Obj:" + objName +" not found!";
        else if(opName.equals("gen_topology_layout")){
            return "All layouts count:"+g.genPALTopology();
        }
        else if(opName.equals("print_trans")){
            return g.printAllTrans();
        }
        else if(opName.equals("print_disturbs")){
            return g.printAllDisturbs();
        }
        return strFailed;
    }

    //Operator to process Long array params.
    public static String iOperator(String objName,String opName,Object params){
        MetaGeo g = MetaGeoDict.get(objName);
        if(g==null)return "Obj:" + objName +" not found!";
        if(!(params instanceof ArrayList))return "paramters format error";
        ArrayList<Long> args = (ArrayList<Long>)params;

        if(opName.equals("core")) {
            if(args.size()>0 && (args.size()%2==0)) {
                for(int i=0;i<args.size()/2;i++){
                    g.corePoints.add(new Coordinate(args.get(i*2),args.get(i*2+1)));
                }
                g.hasCore = true;
                return null;
            }
        }
        else if(opName.equals("print_trans")){
            return g.printAllTrans();
        }
        else if(opName.equals("set_pal_number")){
            if(args.size() == 1) {
                long count = g.setPALTotalNumber(args.get(0).intValue());
                return "PAL totoal number is " + count +";";
            }
            else if(args.size() == 3) {
                long count = g.setPALNumber(args.get(0).intValue(), args.get(1).intValue(),args.get(2).intValue());
                return "PAL totoal number is " + count +";";
            }
        }
        else if(opName.equals("set_p_layout")){
            g.setPLayout(args);
            return "Point layouts count: 1";
        }
        else if(opName.equals("set_a_layout")){
            g.setALayout(args);
            return "Angular layouts count: 1";
        }
        else if(opName.equals("set_l_layout")){
            g.setLLayout(args);
            return "Line layouts count: 1";
        }
        else if(opName.equals("gen_topology_layout")){
            return "All layouts count:"+g.genPALTopology();
        }
        else if(opName.equals("set_topology_situation")){
            if(args.size() == 1) {
                long count = g.setPALSituation(args.get(0).intValue());
                long status_dims = g.allPossibleStateRangeLimit.size();
                String result = "The number of degrees-of-freedom is "+ status_dims + ";\n";
                result += "Current possible state number is " + count +";\n";
                result += "Degree value range is:\n";
                for(Long vrange:g.allPossibleStateRangeLimit){
                    result+="[0,"+(vrange-1)+"]";
                }

                return result;
            }
        }
        else if(opName.equals("set_initial_state")){
            if(args.size() == 1) {
                long index = g.setGlobalStateValue(args.get(0));
                return "Set initial global state: " + Long.toString(index) +";";
            }
        }
        else if(opName.equals("set_question_rules_number")){
            if(args.size() == 1) {
                g.transNumber = args.get(0).intValue();
                //long count = g.setTransNumberMustRotate(args.get(0).intValue());
                return "All question rules count is " + g.transNumber +";";
            }
        }
        else if(opName.equals("set_disturb_rules_number")){
            if(args.size() == 1) {
                g.disturbNumber = args.get(0).intValue();
                //long count = g.setDisturbsNumber(args.get(0).intValue());
                return "All disturb rules count is " + g.disturbNumber +";";
            }
        }
        else if(opName.equals("set_question_rules_index")){
            if( args.size() == 1 ) {
                g.currentTransIndex = args.get(0);
                return "Set question rules index to " + g.currentTransIndex +";";
            }
        }
        else if(opName.equals("set_disturb_rules_indexes")){
            if( args.size() >0 ) {
                g.currentDisturbIndexes = Utils.myDeepClone(args);
                return "Set disturb rules indexes;";
            }
        }
        else if(opName.equals("random_trans_type")){
            if( args.size() == 1 ) {
                g.random_trans_type = args.get(0).intValue();
                return "Set random generator trans type to " + g.random_trans_type +";";
            }
        }
        else if(opName.equals("random_disturb_count")){
            if( args.size() == 1 ) {
                g.random_disturb_count = args.get(0).intValue();
                return "Set random generator disturb count to " + g.random_disturb_count +";";
            }
        }
        return strFailed;
    }

    //Operator to process Double array params.
    public static String dOperator(String objName,String opName,Object params){
        MetaGeo g = MetaGeoDict.get(objName);
        if(g==null)return "Obj:" + objName +" not found!";
        if(!(params instanceof ArrayList))return "paramters format error";
        List<Double> args = (List<Double>)params;

        if(opName.equals("core")) {
            if(args.size()>0 && (args.size()%2==0)) {
                for(int i=0;i<args.size()/2;i++){
                    g.corePoints.add(new Coordinate(args.get(i*2),args.get(i*2+1)));
                }
                g.hasCore = true;
                return null;
            }
        }
        else if(opName.equals("print_trans")){
            return g.printAllTrans();
        }

        return strFailed;
    }

}
