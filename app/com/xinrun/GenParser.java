//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 3 "gen.yacc"
package com.xinrun;
import java.lang.Math;
import java.io.*;
import java.util.StringTokenizer;
import java.util.*;
//#line 23 "GenParser.java"




public class GenParser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class GenParserVal is defined in GenParserVal.java


String   yytext;//user variable to return contextual strings
GenParserVal yyval; //used to return semantic vals from action routines
GenParserVal yylval;//the 'lval' (result) I got from yylex()
GenParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new GenParserVal[YYSTACKSIZE];
  yyval=new GenParserVal();
  yylval=new GenParserVal();
  valptr=-1;
}
void val_push(GenParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
GenParserVal val_pop()
{
  if (valptr<0)
    return new GenParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
GenParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new GenParserVal();
  return valstk[ptr];
}
final GenParserVal dup_yyval(GenParserVal val)
{
  GenParserVal dup = new GenParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short POLY3=257;
public final static short POLY4=258;
public final static short POLY5=259;
public final static short IDENTIFIER=260;
public final static short MYENUM=261;
public final static short MYFLOAT=262;
public final static short NL=263;
public final static short OPDEF=264;
public final static short BLANKS=265;
public final static short UMINUS=266;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    6,    6,    6,    6,    7,    7,    7,    4,
    4,    2,    2,    2,    2,    2,    2,    2,    5,    5,
    8,    8,    3,    1,    1,    1,    1,    1,    1,    1,
    1,
};
final static short yylen[] = {                            2,
    0,    2,    1,    2,    2,    2,    3,    6,    6,    1,
    3,    1,    3,    3,    3,    3,    2,    3,    1,    3,
   12,   14,    5,    1,    3,    3,    3,    3,    3,    2,
    3,
};
final static short yydefred[] = {                         1,
    0,    0,    0,    0,   24,    3,    0,    0,    0,    2,
    0,    0,    0,    0,    0,   30,    0,    6,    0,    0,
    0,    0,    0,    4,    5,    0,    0,    0,   31,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   12,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   17,    0,    0,    0,    0,    0,    8,    0,    9,    0,
    0,    0,    0,   18,    0,    0,    0,    0,   15,   16,
    0,    0,    0,    0,    0,   23,    0,    0,    0,    0,
    0,    0,   21,    0,    0,   22,
};
final static short yydgoto[] = {                          1,
   16,   51,   39,   46,   47,   10,   11,   12,
};
final static short yysindex[] = {                         0,
  -40, -259, -258,  -43,    0,    0,   -9,   -9,   -4,    0,
 -252, -245, -260, -219, -211,    0,    5,    0,   -9,   -9,
   -9,   -9,   -9,    0,    0,   18,   20,   39,    0,   14,
   14,  -23,  -23,  -23,   44,   44,   -5,   -9,   41,   54,
    0,   -5,   -5,   31,   87,   51,   61,   25,   44,   44,
    0,   46,   -3,   -3,   -3,   -3,    0,   -3,    0,   -9,
   -9,   63,   65,    0,   -3,   -3,   35,   35,    0,    0,
   87,   31,   12,   44,   44,    0,   76,   80,   44,   44,
   34,   84,    0,   44,   60,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0, -118,    0,  -16,
  -11,  -35,  -28,  -21,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   97,   98,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   73,   92,    0,    0,
   99,  103,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
   43,   57,   47,    0,    0,    0,    0,    0,
};
final static int YYTABLESIZE=259;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                          8,
   13,   14,   15,   26,    7,   27,   27,   27,   27,   27,
   24,   27,   28,   28,   28,   28,   28,   25,   28,   29,
   29,   29,   29,   29,   26,   29,   26,   26,   26,   25,
    8,   25,   25,   25,   43,    7,   66,   21,   20,   42,
   19,   65,   22,    9,   27,   29,   21,   20,   28,   19,
   17,   22,   76,   21,   20,   21,   19,   35,   22,   36,
   22,   30,   31,   32,   33,   34,   21,   20,   61,   19,
   23,   22,   21,   20,   83,   19,   55,   22,   37,   44,
   48,   56,   40,   38,   49,   17,   64,   55,   54,   23,
   53,   57,   56,   45,   58,   62,   63,   50,   23,   52,
   86,   59,   72,   73,   60,   23,   74,   23,   75,   67,
   68,   69,   70,   14,   71,   14,   14,   14,   23,   79,
   77,   78,   52,   80,   23,   81,   82,   84,   55,   54,
   85,   53,   13,   56,   13,   13,   13,   19,   10,   11,
   19,   10,   11,   20,    7,    0,   20,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    2,    3,    4,
    0,    5,    6,    0,    0,    0,    0,   27,    0,    0,
    0,    0,    0,    0,   28,    0,    0,    0,    0,    0,
    0,   29,    0,    0,    0,    0,   26,    0,    0,    0,
    0,   25,    5,    0,    0,   41,    5,   41,   18,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
  260,  260,   46,  264,   45,   41,   42,   43,   44,   45,
  263,   47,   41,   42,   43,   44,   45,  263,   47,   41,
   42,   43,   44,   45,   41,   47,   43,   44,   45,   41,
   40,   43,   44,   45,   40,   45,   40,   42,   43,   45,
   45,   45,   47,    1,  264,   41,   42,   43,  260,   45,
    8,   47,   41,   42,   43,   42,   45,   40,   47,   40,
   47,   19,   20,   21,   22,   23,   42,   43,   44,   45,
   94,   47,   42,   43,   41,   45,   42,   47,   40,   37,
   38,   47,   36,   40,   44,   43,   41,   42,   43,   94,
   45,   41,   47,   37,   44,   49,   50,   44,   94,   43,
   41,   41,   60,   61,   44,   94,   44,   94,   44,   53,
   54,   55,   56,   41,   58,   43,   44,   45,   94,   44,
   74,   75,   66,   44,   94,   79,   80,   44,   42,   43,
   84,   45,   41,   47,   43,   44,   45,   41,   41,   41,
   44,   44,   44,   41,  263,   -1,   44,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  258,  259,  260,
   -1,  262,  263,   -1,   -1,   -1,   -1,  263,   -1,   -1,
   -1,   -1,   -1,   -1,  263,   -1,   -1,   -1,   -1,   -1,
   -1,  263,   -1,   -1,   -1,   -1,  263,   -1,   -1,   -1,
   -1,  263,  262,   -1,   -1,  261,  262,  261,  263,
};
}
final static short YYFINAL=1;
final static short YYMAXTOKEN=266;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,"'^'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,"POLY3","POLY4","POLY5","IDENTIFIER","MYENUM",
"MYFLOAT","NL","OPDEF","BLANKS","UMINUS",
};
final static String yyrule[] = {
"$accept : input",
"input :",
"input : input line",
"line : NL",
"line : operator NL",
"line : assignment NL",
"line : exp NL",
"operator : IDENTIFIER '.' IDENTIFIER",
"operator : IDENTIFIER '.' IDENTIFIER '(' iparameters ')'",
"operator : IDENTIFIER '.' IDENTIFIER '(' dparameters ')'",
"iparameters : iexp",
"iparameters : iparameters ',' iexp",
"iexp : MYENUM",
"iexp : iexp '+' iexp",
"iexp : iexp '-' iexp",
"iexp : iexp '*' iexp",
"iexp : iexp '/' iexp",
"iexp : '-' iexp",
"iexp : '(' iexp ')'",
"dparameters : exp",
"dparameters : dparameters ',' exp",
"assignment : POLY4 IDENTIFIER OPDEF '(' position ',' position ',' position ',' position ')'",
"assignment : POLY5 IDENTIFIER OPDEF '(' position ',' position ',' position ',' position ',' position ')'",
"position : '(' exp ',' exp ')'",
"exp : MYFLOAT",
"exp : exp '+' exp",
"exp : exp '-' exp",
"exp : exp '*' exp",
"exp : exp '/' exp",
"exp : exp '^' exp",
"exp : '-' exp",
"exp : '(' exp ')'",
};

//#line 84 "gen.yacc"

  /* a reference to the lexer object */
  private GenYylex lexer;
  public String output;

  /* interface to the lexer */
  private int yylex () {
    int yyl_return = -1;
    try {
      yyl_return = lexer.yylex();
    }
    catch (IOException e) {
      System.err.println("IO error :"+e);
    }
    return yyl_return;
  }

  /* error reporting */
  public void yyerror (String error) {
    System.err.println ("Error: " + error);
  }

  /* lexer is created in the constructor */
  public GenParser(Reader r) {
    lexer = new GenYylex(r, this);
    output = "";
    //yydebug = true;
  }

  /* lib use. */
  public static String DoParser(String input){
      GenParser yyparser = new GenParser(new StringReader(input));
      yyparser.yyparse();
      return yyparser.output;
  }

  /* that's how you use the parser */
  public static void main(String args[]) throws IOException {
      GenParser yyparser = new GenParser(new FileReader(args[0]));
      yyparser.yyparse();
      System.out.println(yyparser.output);
  }
//#line 319 "GenParser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 6:
//#line 41 "gen.yacc"
{ System.out.println(" " + val_peek(1).dval + " "); }
break;
case 7:
//#line 44 "gen.yacc"
{ String ret = MetaGeo.Operator(val_peek(2).sval,val_peek(0).sval,null); if(ret!=null){ output+=ret; output+="\n";}  }
break;
case 8:
//#line 45 "gen.yacc"
{ String ret = MetaGeo.iOperator(val_peek(5).sval,val_peek(3).sval,val_peek(1).obj); if(ret!=null){ output+=ret; output+="\n";} }
break;
case 9:
//#line 46 "gen.yacc"
{ String ret = MetaGeo.dOperator(val_peek(5).sval,val_peek(3).sval,val_peek(1).obj); if(ret!=null){ output+=ret; output+="\n";} }
break;
case 10:
//#line 49 "gen.yacc"
{ List<Long> p=new ArrayList<Long>(); p.add(new Long(val_peek(0).ival)); yyval.obj=p; }
break;
case 11:
//#line 50 "gen.yacc"
{ ((List<Long>)val_peek(2).obj).add(new Long(val_peek(0).ival)); yyval.obj=val_peek(2).obj; }
break;
case 12:
//#line 53 "gen.yacc"
{ yyval.ival = val_peek(0).ival; }
break;
case 13:
//#line 54 "gen.yacc"
{ yyval.ival = val_peek(2).ival + val_peek(0).ival; }
break;
case 14:
//#line 55 "gen.yacc"
{ yyval.ival = val_peek(2).ival - val_peek(0).ival; }
break;
case 15:
//#line 56 "gen.yacc"
{ yyval.ival = val_peek(2).ival * val_peek(0).ival; }
break;
case 16:
//#line 57 "gen.yacc"
{ yyval.ival = val_peek(2).ival / val_peek(0).ival; }
break;
case 17:
//#line 58 "gen.yacc"
{ yyval.ival = -val_peek(0).ival; }
break;
case 18:
//#line 59 "gen.yacc"
{ yyval.ival = val_peek(1).ival; }
break;
case 19:
//#line 62 "gen.yacc"
{ List<Double> p=new ArrayList<Double>(); p.add(new Double(val_peek(0).dval)); yyval.obj=p; }
break;
case 20:
//#line 63 "gen.yacc"
{ ((List<Double>)val_peek(2).obj).add(new Double(val_peek(0).dval)); yyval.obj=val_peek(2).obj; }
break;
case 21:
//#line 66 "gen.yacc"
{ MetaGeo g = new MetaGeo(val_peek(10).sval); g.initPoly4(val_peek(7).obj,val_peek(5).obj,val_peek(3).obj,val_peek(1).obj); }
break;
case 22:
//#line 67 "gen.yacc"
{ MetaGeo g = new MetaGeo(val_peek(12).sval); g.initPoly5(val_peek(9).obj,val_peek(7).obj,val_peek(5).obj,val_peek(3).obj,val_peek(1).obj); }
break;
case 23:
//#line 70 "gen.yacc"
{ yyval.obj = new Position(val_peek(3).dval,val_peek(1).dval); }
break;
case 24:
//#line 73 "gen.yacc"
{ yyval.dval = val_peek(0).dval; }
break;
case 25:
//#line 74 "gen.yacc"
{ yyval.dval = val_peek(2).dval + val_peek(0).dval; }
break;
case 26:
//#line 75 "gen.yacc"
{ yyval.dval = val_peek(2).dval - val_peek(0).dval; }
break;
case 27:
//#line 76 "gen.yacc"
{ yyval.dval = val_peek(2).dval * val_peek(0).dval; }
break;
case 28:
//#line 77 "gen.yacc"
{ yyval.dval = val_peek(2).dval / val_peek(0).dval; }
break;
case 29:
//#line 78 "gen.yacc"
{ yyval.dval = Math.pow(val_peek(2).dval, val_peek(0).dval); }
break;
case 30:
//#line 79 "gen.yacc"
{ yyval.dval = -val_peek(0).dval; }
break;
case 31:
//#line 80 "gen.yacc"
{ yyval.dval = val_peek(1).dval; }
break;
//#line 572 "GenParser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public GenParser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public GenParser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
