package com.xinrun;

import java.util.ArrayList;
import javax.vecmath.*;

/**
 * Created by zhaoxueqian on 16/4/11.
 */
public class Utils {
    /**
     * 组合问题： 输出从数组a的n个元素中选出m个元素的组合
     * @param a 给定数组
     * @param b 组合结果  (b中存放的是元素在a中的编号)
     * @param n
     * @param m
     * @param M 常量=m 记录a中元素个数
     *
     * 思路：1.从n个元素中选出序号最大的数，然后在剩下的(n-1)个元素中选(m-1)个
     *    当m=1时，倒序输出数组b；
     *      2.从n个元素中选出编号次小的数，重复第1步。
     */
    public static void comb( int[] a, int[] b, int n, int m, final int M ){
        for( int i=n; i>=m; i--){
            b[m-1] = i-1;
            if( m>1 ){
                comb(a,b,i-1,m-1,M);
            }else{
                for( int j=M-1; j>=0; j-- ){
                    System.out.print(a[b[j]]+" ");
                    if( j==0 )
                        System.out.println();
                }
            }
        }
    }


    public static ArrayList<Long> myDeepClone(final ArrayList<Long> src){
        ArrayList<Long> result = new  ArrayList<Long>();
        for(int i=0;i<src.size();i++){
            result.add(new Long(src.get(i)));
        }
        return result;
    }

    public static ArrayList<ArrayList<Long>> collectMfromN(final ArrayList<Long> src, int m){
        if(src.size()<m)return null;//error, could not happen with correct call.
        if(m==0){
            ArrayList<ArrayList<Long>> result = new ArrayList<ArrayList<Long>>();
            ArrayList<Long> e = new ArrayList<Long>();
            result.add(e);
            return result;
        }
        if(src.size()==m){
            ArrayList<ArrayList<Long>> result = new ArrayList<ArrayList<Long>>();
            result.add(myDeepClone(src));
            return result;
        }

        Long element = src.get(0);
        //first don't chosen this one.
        ArrayList<Long> part = myDeepClone(src);
        part.remove(0);
        ArrayList<ArrayList<Long>> result1 = collectMfromN(part, m);

        // then chosen this one.
        ArrayList<ArrayList<Long>> result2 = collectMfromN(part,m-1);
        for(ArrayList<Long> r:result2){
            r.add(0,element);
        }

        ArrayList<ArrayList<Long>> result = new ArrayList<ArrayList<Long>>();
        result.addAll(result1);
        result.addAll(result2);

        return result;
    }

    public static ArrayList<ArrayList<Long>> CrossMultiply(final ArrayList<ArrayList<Long>> src, final ArrayList<ArrayList<Long>> multiplier){
        ArrayList<ArrayList<Long>> result = new ArrayList<ArrayList<Long>>();
        if(src.size()==0){ result.addAll(multiplier); return result; }
        if(multiplier.size()==0){ result.addAll(src); return result; }

        for(ArrayList<Long> arrayList:src){
            for(ArrayList<Long> multi:multiplier){
                ArrayList<Long> m = new ArrayList<Long>();
                m.addAll(arrayList);
                m.addAll(multi);
                if(m.size()>0)result.add(m);
            }
        }
        return result;
    }

    public static ArrayList<Long> AddWithRangeLimit(final ArrayList<Long> src,final Long added, final ArrayList<Long> rangeLimit){
        ArrayList<Long> result = new ArrayList<Long>();
        for(int i=0;i<src.size();i++){
            result.add(new Long(src.get(i)));
        }

        long add = added;
        while(add>0){
            add--;
            for(int i=(result.size()-1);i>=0;i--){
                if(rangeLimit.get(i)==0)continue;

                long v = result.get(i)+1;
                if(v>=rangeLimit.get(i)) {
                    v %= rangeLimit.get(i);
                    result.set(i,v);
                }
                else{
                    result.set(i,v);
                    break;
                }
            }
        }
        return result;
    }

    public static ArrayList<ArrayList<Long>> GenOperators(int opsCount, final ArrayList<Long> rangeLimit){
        if(opsCount>rangeLimit.size())return null;

        ArrayList<Long> allpos = new ArrayList<Long>();
        for(long i=0;i<rangeLimit.size();i++)allpos.add(i);

        ArrayList<ArrayList<Long>> allWheres = collectMfromN(allpos,opsCount);

        ArrayList<ArrayList<Long>> result = new ArrayList<ArrayList<Long>>();
        ArrayList<Long> root = new  ArrayList<Long>();

        for(ArrayList<Long> pos:allWheres){
            root.clear();
            for(int i=0;i<rangeLimit.size();i++)root.add(new Long(0));
            result.addAll(GenOps(root,pos,rangeLimit));
        }

        return result;
    }

    public static ArrayList<ArrayList<Long>> GenOperatorsMustRotate(int opsCount, final ArrayList<Long> rangeLimit){
        if(opsCount>rangeLimit.size())return null;

        ArrayList<Long> allpos = new ArrayList<Long>();
        for(long i=1;i<rangeLimit.size();i++)allpos.add(i); //the abs index 0 is poly rotate. at first dont add it.

        ArrayList<ArrayList<Long>> allWheres = collectMfromN(allpos,opsCount-1);
        for(ArrayList<Long> where:allWheres)where.add(0,new Long(0));  // add abs index 0 back.

        ArrayList<ArrayList<Long>> result = new ArrayList<ArrayList<Long>>();
        ArrayList<Long> root = new  ArrayList<Long>();

        for(ArrayList<Long> pos:allWheres){
            root.clear();
            for(int i=0;i<rangeLimit.size();i++)root.add(new Long(0));
            result.addAll(GenOps(root,pos,rangeLimit));
        }

        return result;
    }


    static ArrayList<ArrayList<Long>> GenOps( final ArrayList<Long> current, final ArrayList<Long> poses, final ArrayList<Long> rangeLimit){
        if(poses.size()==0){
            ArrayList<Long> e = myDeepClone(current);
            ArrayList<ArrayList<Long>> ret = new ArrayList<ArrayList<Long>>();
            ret.add(e);
            return ret;
        }
        long pos = poses.remove(0);
        ArrayList<ArrayList<Long>> ret = new ArrayList<ArrayList<Long>>();

        for(int i=1;i<rangeLimit.get((int)pos);i++){
            ArrayList<Long> e = myDeepClone(current);
            ArrayList<Long> p = myDeepClone(poses);
            e.set((int)pos,new Long(i));
            ret.addAll(GenOps(e,p,rangeLimit));
        }

        return ret;
    }

}
