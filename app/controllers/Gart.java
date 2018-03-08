package controllers;

import com.xinrun.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;
import views.html.*;

import java.util.ArrayList;

public class Gart extends Controller {

    public static Result index() {
        return ok(index.render("Input your code here","output"));
    }

    public static Result genPic() {

        DynamicForm f = Form.form().bindFromRequest();
        String input = f.get("code");
        String output = Parser.DoParser(input);

        return ok(index.render(input,output));
    }

    public static Result getGenCode() {
        return ok(gencode.render("Input your code here","output"));
    }

    public static Result genCode() {

        DynamicForm f = Form.form().bindFromRequest();
        String input = f.get("code");
        String output = GenParser.DoParser(input);

        return ok(gencode.render(input,output));
    }

    public static Result getGenPoly() {
        return ok(genpoly.render(""));
    }

    public static Result setQuestion() {
        return ok(setquestion.render());
    }

    public static Result topologySelect() {

        DynamicForm f = Form.form().bindFromRequest();
        String input = f.get("code");
        //String trans_number = f.get("trans_number");
        //String disturb_number = f.get("disturb_number");
        String output = GenParser.DoParser(input);
        MetaGeo mg = MetaGeo.MetaGeoDict.get("Q");

        //Check topology count to decide need rootpoly select or not.
        mg.genPALTopology();
        if(mg.AllPALindexes.size()==1){
            mg.setPALSituation(0);

            String root = mg.toStringDefine() + "\n" + mg.toStringPALTopology(mg.AllPALindexes.get(0));
            String root_svg = Parser.DoParser(root + "\n"+mg.id+".draw\n");

            ArrayList<String> initials = new ArrayList<String>();
            ArrayList<String> initials_title = new ArrayList<String>();
            ArrayList<String> initials_svg = new ArrayList<String>();
            for(int i=0;i<mg.getGlobalStateSize();i++){
                String initial = mg.toStringDefine() + "\n" + mg.toStringGlobalState(i);
                initials.add(initial);
                String title = mg.toShortStringGlobalState(i);
                initials_title.add(title);
                String initial_svg = Parser.DoParser(initial + "\n"+mg.id+".draw\n");
                initials_svg.add(initial_svg);
            }

            return ok(rootpolyselect.render(initials_title,initials_svg,mg.allPossibleStateIndexes.size()));
        }
        else{
            ArrayList<String> topos_title = new ArrayList<String>();
            ArrayList<String> topos_svg = new ArrayList<String>();
            for(int i=0;i<mg.AllPALindexes.size();i++){
                mg.setPALSituation(i);
                String title = mg.toShortStringPALTopology(mg.AllPALindexes.get(i));
                topos_title.add(title);
                String topo = mg.toStringDefine() + "\n" + mg.toStringPALTopology(mg.AllPALindexes.get(i));
                String topo_svg = Parser.DoParser(topo + "\n"+mg.id+".draw\n");
                topos_svg.add(topo_svg);
            }
            return ok(topologyselect.render("请选择图形A装饰分布",topos_title, topos_svg));
        }

    }

    public static Result rootPolySelect() {
        DynamicForm f = Form.form().bindFromRequest();
        String topo = f.get("topo");

        MetaGeo mg = MetaGeo.MetaGeoDict.get("Q");
        mg.setPALSituation(Integer.parseInt(topo));

        ArrayList<String> initials = new ArrayList<String>();
        ArrayList<String> initials_title = new ArrayList<String>();
        ArrayList<String> initials_svg = new ArrayList<String>();
        for(int i=0;i<mg.getGlobalStateSize();i++){
            String initial = mg.toStringDefine() + "\n" + mg.toStringGlobalState(i);
            initials.add(initial);
            String title = mg.toShortStringGlobalState(i);
            initials_title.add(title);
            String initial_svg = Parser.DoParser(initial + "\n"+mg.id+".draw\n");
            initials_svg.add(initial_svg);
        }

        return ok(rootpolyselect.render(initials_title,initials_svg,mg.allPossibleStateIndexes.size()));
    }

    public static Result transPolySelect() {
        DynamicForm f = Form.form().bindFromRequest();
        String rootstate = f.get("rootstate");

        MetaGeo mg = MetaGeo.MetaGeoDict.get("Q");
        mg.setGlobalStateValue(Integer.parseInt(rootstate));
        //mg.setTransNumberMustRotate();
        mg.setTransNumber();

        String root = mg.toStringDefine() + "\n" + mg.toStringGlobalState(mg.currentGlobalStateValue);
        String roottitle = mg.toShortStringGlobalState(mg.currentGlobalStateValue);
        String root_svg = Parser.DoParser(root + "\n" + mg.id + ".draw\n");

        ArrayList<String> trans = new ArrayList<String>();
        ArrayList<String> trans_title = new ArrayList<String>();
        ArrayList<String> trans_svg = new ArrayList<String>();
        ArrayList<Long>   trans_val = new ArrayList<Long>();
        ArrayList<String> htrans = new ArrayList<String>();
        ArrayList<String> htrans_title = new ArrayList<String>();
        ArrayList<String> htrans_svg = new ArrayList<String>();
        ArrayList<Long>   htrans_val = new ArrayList<Long>();

        long i=0;
        for(ArrayList<Long> transops:mg.AllTransOps){
            String tran = mg.toStringTransOPS(transops);
            if(mg.isHardLevel(transops)){
                htrans_title.add(mg.toShortStringTransOPS(transops));
                htrans.add(tran);
                htrans_svg.add(Parser.DoParser(root + "\n" + tran + "\n" + mg.id + ".draw\n"));
                htrans_val.add(new Long(i));
            }
            else {
                trans_title.add(mg.toShortStringTransOPS(transops));
                trans.add(tran);
                trans_svg.add(Parser.DoParser(root + "\n" + tran + "\n" + mg.id + ".draw\n"));
                trans_val.add(new Long(i));
            }
            i++;
        }

        return ok(transpolyselect.render(roottitle, root_svg,trans_title, trans_svg, trans_val,htrans_title, htrans_svg, htrans_val));
    }


    public static Result AnswerTopologySelect() {
        DynamicForm f = Form.form().bindFromRequest();
        String transindex = f.get("transindex");

        //Check topology count to decide need rootpoly select or not.
        MetaGeo mg = MetaGeo.MetaGeoDict.get("Q");
        mg.currentTransIndex = Long.parseLong(transindex);

        ArrayList<Long> transAr = mg.AllTransOps.get((int) mg.currentTransIndex);
        String root = mg.toStringDefine() + "\n" + mg.toStringGlobalState(mg.currentGlobalStateValue);
        String roottitle = mg.toShortStringGlobalState(mg.currentGlobalStateValue);
        String root_svg = Parser.DoParser(root + "\n" + mg.id + ".draw\n");
        String trans = root + "\n" + mg.toStringTransOPS(transAr);
        String transtitle = mg.toRelativeShortStringFromTransOPS(transAr);
        String trans_svg = Parser.DoParser(trans + "\n" + mg.id + ".draw\n");

        MetaGeo mg2 = MetaGeo.MetaGeoDict.get("A");
        mg2.genPALTopology();
        if(mg2.AllPALindexes.size()==1){
            mg2.setPALSituation(0);


            ArrayList<String> initials = new ArrayList<String>();
            ArrayList<String> initials_title = new ArrayList<String>();
            ArrayList<String> initials_svg = new ArrayList<String>();
            ArrayList<Long> initials_state = new ArrayList<Long>();
            for(int i=0;i<mg2.getGlobalStateSize();i++){
                String initial = mg2.toStringDefine() + "\n" + mg2.toStringGlobalState(i);
                initials.add(initial);
                String title = mg2.toShortStringGlobalState(i);
                initials_state.add(new Long(i));
                initials_title.add(title);
                String initial_svg = Parser.DoParser(initial + "\n"+mg2.id+".draw\n");
                initials_svg.add(initial_svg);
            }

            return ok(answerrootselect.render(roottitle, root_svg, transtitle, trans_svg, initials_title, initials_svg, initials_state, mg2.allPossibleStateIndexes.size()));
        }
        else{
            ArrayList<String> topos_title = new ArrayList<String>();
            ArrayList<String> topos_svg = new ArrayList<String>();
            for(int i=0;i<mg2.AllPALindexes.size();i++){
                mg2.setPALSituation(i);
                String title = mg2.toShortStringPALTopology(mg2.AllPALindexes.get(i));
                topos_title.add(title);
                String topo = mg2.toStringDefine() + "\n" + mg2.toStringPALTopology(mg2.AllPALindexes.get(i));
                String topo_svg = Parser.DoParser(topo + "\n"+mg2.id+".draw\n");
                topos_svg.add(topo_svg);
            }
            return ok(answertoposelect.render(roottitle, root_svg, transtitle, trans_svg,"请选择图形C装饰分布",topos_title, topos_svg));
        }

    }

    public static Result answerRootSelect() {
        DynamicForm f = Form.form().bindFromRequest();
        String topo = f.get("topo");

        MetaGeo mg = MetaGeo.MetaGeoDict.get("Q");

        ArrayList<Long> transAr = mg.AllTransOps.get((int) mg.currentTransIndex);
        String root = mg.toStringDefine() + "\n" + mg.toStringGlobalState(mg.currentGlobalStateValue);
        String roottitle = mg.toShortStringGlobalState(mg.currentGlobalStateValue);
        String root_svg = Parser.DoParser(root + "\n" + mg.id + ".draw\n");
        String trans = root + "\n" + mg.toStringTransOPS(transAr);
        String transtitle = mg.toRelativeShortStringFromTransOPS(transAr);
        String trans_svg = Parser.DoParser(trans + "\n" + mg.id + ".draw\n");

        MetaGeo mg2 = MetaGeo.MetaGeoDict.get("A");
        mg2.setPALSituation(Integer.parseInt(topo));

        ArrayList<String> initials = new ArrayList<String>();
        ArrayList<String> initials_title = new ArrayList<String>();
        ArrayList<String> initials_svg = new ArrayList<String>();
        ArrayList<Long> initials_state = new ArrayList<Long>();
        for(int i=0;i<mg2.getGlobalStateSize();i++){
            if(!mg.checkAnswerRootValidWithTransOP(mg.toArrayGlobalState(mg.currentGlobalStateValue),transAr,mg2,mg2.toArrayGlobalState(i)))continue;

            String initial = mg2.toStringDefine() + "\n" + mg2.toStringGlobalState(i);
            initials.add(initial);
            String title = mg2.toShortStringGlobalState(i);
            initials_state.add(new Long(i));
            initials_title.add(title);
            String initial_svg = Parser.DoParser(initial + "\n"+mg2.id+".draw\n");
            initials_svg.add(initial_svg);
        }

        return ok(answerrootselect.render(roottitle,root_svg,transtitle,trans_svg,initials_title,initials_svg,initials_state,mg2.allPossibleStateIndexes.size()));
    }

    public static Result disturbSelect() {
        DynamicForm f = Form.form().bindFromRequest();
        String answerrootstate = f.get("answerrootstate");

        MetaGeo mg = MetaGeo.MetaGeoDict.get("Q");
        String root = mg.toStringDefine() + "\n" + mg.toStringGlobalState(mg.currentGlobalStateValue);
        String roottitle = mg.toShortStringGlobalState(mg.currentGlobalStateValue);
        String root_svg = Parser.DoParser(root + "\nQ.draw\n");
        ArrayList<Long> transAr = mg.AllTransOps.get((int) mg.currentTransIndex);
        String trans = root + "\n" + mg.toStringTransOPS(transAr);
        String transtitle = mg.toRelativeShortStringFromTransOPS(transAr);
        String trans_svg = Parser.DoParser(trans + "\nQ.draw\n");

        MetaGeo mg2 = MetaGeo.MetaGeoDict.get("A");
        mg2.currentGlobalStateValue = Long.parseLong(answerrootstate);
        mg2.setDisturbsNumber();

        String saveId = mg.id;
        mg.id = mg2.id;
        String answerroot = mg2.toStringDefine() + "\n" + mg2.toStringGlobalState(mg2.currentGlobalStateValue);
        String answerroottitle = mg2.toShortStringGlobalState(mg2.currentGlobalStateValue);
        String answerroot_svg = Parser.DoParser(answerroot + "\nA.draw\n");
        String answertrans = answerroot + "\n" + mg.toRelativeStringTransOPS(mg.AllTransOps.get((int) mg.currentTransIndex));
        String answertrans_svg = Parser.DoParser(answertrans + "\nA.draw\n");

        ArrayList<String> disturbs = new ArrayList<String>();
        ArrayList<String> disturbs_title = new ArrayList<String>();
        ArrayList<String> disturbs_svg = new ArrayList<String>();
        for(ArrayList<Long> disturbops:mg2.AllDisturbOps){
            disturbs_title.add(mg2.toShortStringTransOPS(disturbops));
            disturbs.add(mg2.toStringTransOPS(disturbops));
        }

        for(String disturb:disturbs){
            disturbs_svg.add(Parser.DoParser(answertrans+"\n"+disturb+"\nA.draw\n"));
        }
        mg.id = saveId;

        return ok(disturbselect.render(roottitle, root_svg,transtitle, trans_svg,answerroottitle, answerroot_svg,answertrans_svg,disturbs_title,disturbs_svg));
    }

    public static Result saveQuestion() {
        MetaGeo mg = MetaGeo.MetaGeoDict.get("Q");
        MetaGeo mg2 = MetaGeo.MetaGeoDict.get("A");

        mg2.currentDisturbIndexes = new ArrayList<Long>();
        DynamicForm f = Form.form().bindFromRequest();
        for(String v:f.data().values()){
            if(v.isEmpty())continue;
            Long index = Long.parseLong(v);
            mg2.currentDisturbIndexes.add(index);
        }

        String root = mg.toStringDefine() + "\n" + mg.toStringGlobalState(mg.currentGlobalStateValue);
        String roottitle = mg.toShortStringGlobalState(mg.currentGlobalStateValue);
        String root_svg = Parser.DoParser(root + "\nQ.draw\n");
        ArrayList<Long> transAr = mg.AllTransOps.get((int) mg.currentTransIndex);
        String trans = root + "\n" + mg.toStringTransOPS(transAr);
        String transtitle = mg.toRelativeShortStringFromTransOPS(transAr);
        String trans_svg = Parser.DoParser(trans + "\nQ.draw\n");


        String saveId = mg.id;
        mg.id = mg2.id;

        String answerroot = mg2.toStringDefine() + "\n" + mg2.toStringGlobalState(mg2.currentGlobalStateValue);
        String answerroottitle = mg2.toShortStringGlobalState(mg2.currentGlobalStateValue);
        String answerroot_svg = Parser.DoParser(answerroot + "\nA.draw\n");
        String answertrans = answerroot + "\n" + mg.toRelativeStringTransOPS(mg.AllTransOps.get((int) mg.currentTransIndex));
        String answertrans_svg = Parser.DoParser(answertrans + "\nA.draw\n");

        ArrayList<String> disturbs = new ArrayList<String>();
        ArrayList<String> disturbs_svg = new ArrayList<String>();
        ArrayList<String> disturbs_title = new ArrayList<String>();

        for(Long index:mg2.currentDisturbIndexes){
            mg2.shockPAL();
            disturbs_title.add(mg2.toRelativeShortStringFromTransOPS(mg2.AllDisturbOps.get(index.intValue()))+mg2.shortStrShockPAL);
            String disturb = answertrans + mg2.toRelativeStringTransOPS(mg2.AllDisturbOps.get(index.intValue()))+mg2.strShockPAL;
            String disturb_svg = Parser.DoParser(disturb + "\nA.draw\n");
            disturbs.add(disturb);
            disturbs_svg.add(disturb_svg);
        }

        mg.id = saveId;

        String result = "//题面初始图形代码\n"+ root+ "Q.draw\n" ;
        result += "//题面变换后图形代码\n" + trans + "Q.draw\n";
        result += "//答案初始图形代码\n"+ answerroot+ "A.draw\n" ;
        result += "//正确答案图形代码\n" + answertrans + "A.draw\n";

        int count = 1;
        for(String disturb:disturbs){
            result += "//错误答案["+count+"]图形代码\n" + disturb + "A.draw\n";
            count++;
        }

        /*
        String[] disturbs = disturbindexes.split(" ");
        for(String disturb:disturbs){
            int index = Integer.parseInt(disturb);
            result += root +"\n"+ trans + "\n" + mg.toStringTransOPS(mg.AllDisturbOps.get(index))+"\nQ.draw\n";
        }*/


        return ok(savequestion.render(roottitle, root_svg,transtitle, trans_svg,answerroottitle, answerroot_svg,answertrans_svg,disturbs_title, disturbs_svg,result));
    }

    public static Result randomget(){
        return ok(gart_random_begin.render());
    }

    public static Result randompost() {

        DynamicForm f = Form.form().bindFromRequest();
        String initcode = f.get("code");
        String output = GenParser.DoParser(initcode);

        /*  A topology */
        MetaGeo mgQ = MetaGeo.MetaGeoDict.get("Q");
        mgQ.genPALTopology();
        int qPALSituation = (int) (Math.random() * mgQ.AllAindexes.size());
        mgQ.setPALSituation(qPALSituation);

        /* A root state */
        int qInitialState = (int) (Math.random() * mgQ.getGlobalStateSize());
        mgQ.setGlobalStateValue(qInitialState);

        /* A => B trans */
        mgQ.setTransNumber();

        ArrayList<String> trans = new ArrayList<String>();
        ArrayList<Long> transIndex = new ArrayList<>();
        ArrayList<String> htrans = new ArrayList<String>();
        ArrayList<Long> htransIndex = new ArrayList<>();

        long tindex = 0;
        for (ArrayList<Long> transops : mgQ.AllTransOps) {
            String tran = mgQ.toStringTransOPS(transops);
            if (mgQ.isHardLevel(transops)) {
                htrans.add(tran);
                htransIndex.add(tindex);
            } else {
                trans.add(tran);
                transIndex.add(tindex);
            }
            tindex++;
        }

        switch(mgQ.random_trans_type){
            case MetaGeo.trans_all:
                int number = (int) (Math.random() * (transIndex.size()+ htransIndex.size()));
                if(number>=htransIndex.size()){
                    mgQ.currentTransIndex = transIndex.get(number-htransIndex.size());
                }
                else{
                    mgQ.currentTransIndex = htransIndex.get(number);
                }
                break;
            case MetaGeo.trans_easy:
                mgQ.currentTransIndex = transIndex.get((int) (Math.random() * transIndex.size()));
                break;
            case MetaGeo.trans_hard:
                mgQ.currentTransIndex = htransIndex.get((int) (Math.random() * htransIndex.size()));
                break;
        }

        ArrayList<Long> transAr = mgQ.AllTransOps.get((int) mgQ.currentTransIndex);

        /* C topology */
        MetaGeo mgA = MetaGeo.MetaGeoDict.get("A");
        mgA.genPALTopology();
        int aPALSituation = (int) (Math.random() * mgA.AllAindexes.size());
        mgA.setPALSituation(aPALSituation);

        /* C root state */
        while(true) {
            int aInitialState = (int) (Math.random() * mgA.getGlobalStateSize());
            mgA.setGlobalStateValue(aInitialState);
            if(mgQ.checkAnswerRootValidWithTransOP(mgQ.toArrayGlobalState(mgQ.currentGlobalStateValue),transAr,mgA,mgA.toArrayGlobalState(aInitialState)))break;
        }

        /* Disturb gen */
        mgA.setDisturbsNumber();
        ArrayList<Long> alldisIndex = new ArrayList<>();
        for(int i=0;i<mgA.AllDisturbOps.size();i++){
            alldisIndex.add(new Long(i));
        }
        ArrayList<Long> seldisIndex = new ArrayList<>();
        for(int i=0;i<mgA.random_disturb_count;i++){
            if(alldisIndex.size()==0)break;
            int imove = (int)(Math.random()*alldisIndex.size());
            seldisIndex.add(alldisIndex.remove(imove));
        }

        /*****************************/
        /* Gen all elements for view */
        /*****************************/
        String root = mgQ.toStringDefine() + "\n" + mgQ.toStringGlobalState(mgQ.currentGlobalStateValue);
        String roottitle = mgQ.toShortStringGlobalState(mgQ.currentGlobalStateValue);
        String root_svg = Parser.DoParser(root + "\nQ.draw\n");
        String trans_str = root + "\n" + mgQ.toStringTransOPS(transAr);
        String transtitle = mgQ.toRelativeShortStringFromTransOPS(transAr);
        String trans_svg = Parser.DoParser(trans_str + "\nQ.draw\n");


        String saveId = mgQ.id;
        mgQ.id = mgA.id;

        String answerroot = mgA.toStringDefine() + "\n" + mgA.toStringGlobalState(mgA.currentGlobalStateValue);
        String answerroottitle = mgA.toShortStringGlobalState(mgA.currentGlobalStateValue);
        String answerroot_svg = Parser.DoParser(answerroot + "\nA.draw\n");
        String answertrans_str = answerroot + "\n" + mgQ.toRelativeStringTransOPS(transAr);
        String answertrans_svg = Parser.DoParser(answertrans_str + "\nA.draw\n");

        ArrayList<String> disturbs_str = new ArrayList<String>();
        ArrayList<String> disturbs_svg = new ArrayList<String>();
        ArrayList<String> disturbs_title = new ArrayList<String>();

        for(Long index:seldisIndex){
            mgA.shockPAL();
            disturbs_title.add(mgA.toRelativeShortStringFromTransOPS(mgA.AllDisturbOps.get(index.intValue()))+mgA.shortStrShockPAL);
            String disturb_str = answertrans_str + mgA.toRelativeStringTransOPS(mgA.AllDisturbOps.get(index.intValue()))+mgA.strShockPAL;
            String disturb_svg = Parser.DoParser(disturb_str + "\nA.draw\n");
            disturbs_str.add(disturb_str);
            disturbs_svg.add(disturb_svg);
        }

        mgQ.id = saveId;

        String result = "//题面初始图形代码\n"+ root+ "Q.draw\n" ;
        result += "//题面变换后图形代码\n" + trans_str + "Q.draw\n";
        result += "//答案初始图形代码\n"+ answerroot+ "A.draw\n" ;
        result += "//正确答案图形代码\n" + answertrans_str + "A.draw\n";

        int count = 1;
        for(String disturb:disturbs_str){
            result += "//错误答案["+count+"]图形代码\n" + disturb + "A.draw\n";
            count++;
        }

        return ok(gart_random_next.render(roottitle, root_svg,transtitle, trans_svg,answerroottitle, answerroot_svg,answertrans_svg,disturbs_title, disturbs_svg,result,initcode));
    }



    public static Result edit() {

        DynamicForm f = Form.form().bindFromRequest();
        String input = f.get("code");
        if(input==null)input = "";
        String output = Parser.DoParser(input);

        return ok(gart_edit.render(input,output));
    }
}
