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






//#line 3 "pic.yacc"
package com.xinrun;
import java.lang.Math;
import java.io.*;
import java.util.StringTokenizer;
import java.util.*;
//#line 23 "Parser.java"




public class Parser
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
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
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
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short CIRCLE=257;
public final static short POINT=258;
public final static short LINE=259;
public final static short POLY3=260;
public final static short POLY4=261;
public final static short POLY5=262;
public final static short IDENTIFIER=263;
public final static short MYENUM=264;
public final static short MYFLOAT=265;
public final static short NL=266;
public final static short OPDEF=267;
public final static short OPMIRROR=268;
public final static short OPROTATE=269;
public final static short OPNEG=270;
public final static short OPSET=271;
public final static short BLANKS=272;
public final static short UMINUS=273;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    6,    6,    6,    6,    7,    7,    7,    4,
    4,    2,    2,    2,    2,    2,    2,    2,    5,    5,
    8,    8,    8,    8,    3,    1,    1,    1,    1,    1,
    1,    1,    1,
};
final static short yylen[] = {                            2,
    0,    2,    1,    2,    2,    2,    3,    6,    6,    1,
    3,    1,    3,    3,    3,    3,    2,    3,    1,    3,
    8,   10,   12,   14,    5,    1,    3,    3,    3,    3,
    3,    2,    3,
};
final static short yydefred[] = {                         1,
    0,    0,    0,    0,    0,    0,   26,    3,    0,    0,
    0,    2,    0,    0,    0,    0,    0,    0,    0,   32,
    0,    6,    0,    0,    0,    0,    0,    4,    5,    0,
    0,    0,    0,    0,   33,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   12,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   17,    0,    0,    0,    0,    0,    8,    0,
    9,    0,    0,    0,    0,    0,    0,   18,    0,    0,
    0,    0,   15,   16,    0,    0,    0,   21,    0,    0,
    0,   25,    0,    0,    0,   22,    0,    0,    0,    0,
   23,    0,    0,   24,
};
final static short yydgoto[] = {                          1,
   20,   63,   47,   56,   57,   12,   13,   14,
};
final static short yysindex[] = {                         0,
  -40, -262, -261, -260, -259,    2,    0,    0,   -9,   -9,
   -4,    0, -255, -248, -218, -212, -206, -193, -180,    0,
    9,    0,   -9,   -9,   -9,   -9,   -9,    0,    0,   45,
   47,   55,   57,   59,    0,   34,   34,   10,   10,   10,
   73,   73,   73,   73,   -5,   -9,   88,   95,  107,  112,
    0,   -5,   -5,   65,   99,   70,   81,   15,   -9,   73,
   73,   73,    0,   93,   -3,   -3,   -3,   -3,    0,   -3,
    0,   -9,   -9,   30,  117,  118,  119,    0,   -3,   -3,
   44,   44,    0,    0,   99,   65,   37,    0,   73,   73,
   73,    0,   76,  120,  121,    0,   73,   73,   96,  122,
    0,   73,  102,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  -99,    0,  -16,  -11,  -35,  -28,  -21,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   85,   86,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  104,  109,    0,    0,  114,  116,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
   43,   53,    3,    0,    0,    0,    0,    0,
};
final static int YYTABLESIZE=262;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         10,
   15,   16,   17,   18,    9,   29,   29,   29,   29,   29,
   28,   29,   30,   30,   30,   30,   30,   29,   30,   31,
   31,   31,   31,   31,   28,   31,   28,   28,   28,   27,
   10,   27,   27,   27,   53,    9,   80,   25,   24,   52,
   23,   79,   26,   11,   48,   49,   50,   19,   30,   35,
   25,   24,   21,   23,   31,   26,   25,   24,   73,   23,
   32,   26,   75,   76,   77,   36,   37,   38,   39,   40,
   88,   25,   24,   33,   23,   25,   26,   92,   25,   24,
   26,   23,   34,   26,   41,   67,   42,   54,   58,   27,
   68,   93,   94,   95,   43,   21,   44,   55,   45,   99,
  100,   74,   27,   27,  103,   64,   25,   24,   27,   23,
   69,   26,   46,   70,   86,   87,   96,   81,   82,   83,
   84,   71,   85,   27,   72,   19,   10,   27,   19,   10,
   27,   59,   64,   78,   67,   66,  101,   65,   60,   68,
   67,   66,  104,   65,   14,   68,   14,   14,   14,   13,
   61,   13,   13,   13,   11,   62,   20,   11,   27,   20,
   89,   90,   91,   97,   98,  102,    7,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    2,    0,    0,    3,
    4,    5,    6,    0,    7,    8,    0,    0,    0,    0,
   29,    0,    0,    0,    0,    0,    0,   30,    0,    0,
    0,    0,    0,    0,   31,    0,    0,    0,    0,   28,
    0,    0,    0,    0,   27,    7,    0,    0,   51,    7,
   51,   22,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
  263,  263,  263,  263,   45,   41,   42,   43,   44,   45,
  266,   47,   41,   42,   43,   44,   45,  266,   47,   41,
   42,   43,   44,   45,   41,   47,   43,   44,   45,   41,
   40,   43,   44,   45,   40,   45,   40,   42,   43,   45,
   45,   45,   47,    1,   42,   43,   44,   46,  267,   41,
   42,   43,   10,   45,  267,   47,   42,   43,   44,   45,
  267,   47,   60,   61,   62,   23,   24,   25,   26,   27,
   41,   42,   43,  267,   45,   42,   47,   41,   42,   43,
   47,   45,  263,   47,   40,   42,   40,   45,   46,   94,
   47,   89,   90,   91,   40,   53,   40,   45,   40,   97,
   98,   59,   94,   94,  102,   53,   42,   43,   94,   45,
   41,   47,   40,   44,   72,   73,   41,   65,   66,   67,
   68,   41,   70,   94,   44,   41,   41,   94,   44,   44,
   94,   44,   80,   41,   42,   43,   41,   45,   44,   47,
   42,   43,   41,   45,   41,   47,   43,   44,   45,   41,
   44,   43,   44,   45,   41,   44,   41,   44,   94,   44,
   44,   44,   44,   44,   44,   44,  266,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  257,   -1,   -1,  260,
  261,  262,  263,   -1,  265,  266,   -1,   -1,   -1,   -1,
  266,   -1,   -1,   -1,   -1,   -1,   -1,  266,   -1,   -1,
   -1,   -1,   -1,   -1,  266,   -1,   -1,   -1,   -1,  266,
   -1,   -1,   -1,   -1,  266,  265,   -1,   -1,  264,  265,
  264,  266,
};
}
final static short YYFINAL=1;
final static short YYMAXTOKEN=273;
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
null,null,null,null,null,null,"CIRCLE","POINT","LINE","POLY3","POLY4","POLY5",
"IDENTIFIER","MYENUM","MYFLOAT","NL","OPDEF","OPMIRROR","OPROTATE","OPNEG",
"OPSET","BLANKS","UMINUS",
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
"assignment : CIRCLE IDENTIFIER OPDEF '(' position ',' exp ')'",
"assignment : POLY3 IDENTIFIER OPDEF '(' position ',' position ',' position ')'",
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

//#line 93 "pic.yacc"
  /* create polygons */


  /* a reference to the lexer object */
  private Yylex lexer;
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
  public Parser(Reader r) {	
    lexer = new Yylex(r, this);
    output = "";
    //yydebug = true;
  }

  /* lib use. */
  public static String DoParser(String input){
    Parser yyparser = new Parser(new StringReader(input));
    yyparser.yyparse();
    return yyparser.output;
  }

  /* that's how you use the parser */
  public static void main(String args[]) throws IOException {
    Parser yyparser = new Parser(new FileReader(args[0]));
    yyparser.yyparse();
    System.out.println(yyparser.output);
  }
//#line 339 "Parser.java"
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
//#line 48 "pic.yacc"
{ System.out.println(" " + val_peek(1).dval + " "); }
break;
case 7:
//#line 51 "pic.yacc"
{ String ret = Geometry.Operator(val_peek(2).sval,val_peek(0).sval,null); if(ret!=null){ output+=ret; output+="\n";}  }
break;
case 8:
//#line 52 "pic.yacc"
{ String ret = Geometry.iOperator(val_peek(5).sval,val_peek(3).sval,val_peek(1).obj); if(ret!=null){ output+=ret; output+="\n";} }
break;
case 9:
//#line 53 "pic.yacc"
{ String ret = Geometry.dOperator(val_peek(5).sval,val_peek(3).sval,val_peek(1).obj); if(ret!=null){ output+=ret; output+="\n";} }
break;
case 10:
//#line 56 "pic.yacc"
{ List<Long> p=new ArrayList<Long>(); p.add(new Long(val_peek(0).ival)); yyval.obj=p; }
break;
case 11:
//#line 57 "pic.yacc"
{ ((List<Long>)val_peek(2).obj).add(new Long(val_peek(0).ival)); yyval.obj=val_peek(2).obj; }
break;
case 12:
//#line 60 "pic.yacc"
{ yyval.ival = val_peek(0).ival; }
break;
case 13:
//#line 61 "pic.yacc"
{ yyval.ival = val_peek(2).ival + val_peek(0).ival; }
break;
case 14:
//#line 62 "pic.yacc"
{ yyval.ival = val_peek(2).ival - val_peek(0).ival; }
break;
case 15:
//#line 63 "pic.yacc"
{ yyval.ival = val_peek(2).ival * val_peek(0).ival; }
break;
case 16:
//#line 64 "pic.yacc"
{ yyval.ival = val_peek(2).ival / val_peek(0).ival; }
break;
case 17:
//#line 65 "pic.yacc"
{ yyval.ival = -val_peek(0).ival; }
break;
case 18:
//#line 66 "pic.yacc"
{ yyval.ival = val_peek(1).ival; }
break;
case 19:
//#line 69 "pic.yacc"
{ List<Double> p=new ArrayList<Double>(); p.add(new Double(val_peek(0).dval)); yyval.obj=p; }
break;
case 20:
//#line 70 "pic.yacc"
{ ((List<Double>)val_peek(2).obj).add(new Double(val_peek(0).dval)); yyval.obj=val_peek(2).obj; }
break;
case 22:
//#line 74 "pic.yacc"
{ Geometry g = new Geometry(val_peek(8).sval); g.initPoly3(val_peek(5).obj,val_peek(3).obj,val_peek(1).obj); }
break;
case 23:
//#line 75 "pic.yacc"
{ Geometry g = new Geometry(val_peek(10).sval); g.initPoly4(val_peek(7).obj,val_peek(5).obj,val_peek(3).obj,val_peek(1).obj); }
break;
case 24:
//#line 76 "pic.yacc"
{ Geometry g = new Geometry(val_peek(12).sval); g.initPoly5(val_peek(9).obj,val_peek(7).obj,val_peek(5).obj,val_peek(3).obj,val_peek(1).obj); }
break;
case 25:
//#line 79 "pic.yacc"
{ yyval.obj = new Position(val_peek(3).dval,val_peek(1).dval); }
break;
case 26:
//#line 82 "pic.yacc"
{ yyval.dval = val_peek(0).dval; }
break;
case 27:
//#line 83 "pic.yacc"
{ yyval.dval = val_peek(2).dval + val_peek(0).dval; }
break;
case 28:
//#line 84 "pic.yacc"
{ yyval.dval = val_peek(2).dval - val_peek(0).dval; }
break;
case 29:
//#line 85 "pic.yacc"
{ yyval.dval = val_peek(2).dval * val_peek(0).dval; }
break;
case 30:
//#line 86 "pic.yacc"
{ yyval.dval = val_peek(2).dval / val_peek(0).dval; }
break;
case 31:
//#line 87 "pic.yacc"
{ yyval.dval = Math.pow(val_peek(2).dval, val_peek(0).dval); }
break;
case 32:
//#line 88 "pic.yacc"
{ yyval.dval = -val_peek(0).dval; }
break;
case 33:
//#line 89 "pic.yacc"
{ yyval.dval = val_peek(1).dval; }
break;
//#line 596 "Parser.java"
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
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
