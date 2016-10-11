/*
 * Calc.g - ANTLR grammar for calculator, 18/02/07
 *
 * (C) Copyright Dmitry Chuchva, 2007 
 */
 
header {
package j2eetraining.tests.calc;
}

{
import java.util.Vector;
}

class CalcParser extends Parser;
options {
   defaultErrorHandler = false;
}
line throws CalcException : { double res,x; } res=additive_expression EOL
			{
					System.out.println(res);
			}
	;
	
additive_expression returns [double ret]  throws CalcException { double x,y; }
	:	x=mult_expression { ret = x; } (PLUS y=mult_expression { ret = ret + y; } 
	                                   | MINUS y=mult_expression { ret = ret - y; } )*
	;
	
mult_expression returns [double ret]  throws CalcException { double x,y; }
	:	x=unary_expression { ret = x; } (MULT y=unary_expression { ret = ret * y; } 
	                                    | DIV y=unary_expression { ret = ret / y; } )*
	;
	
unary_expression returns [double ret] throws CalcException  { boolean negate = false; double x; Vector y; String backup; }
	:	(PLUS | MINUS { negate = true; } )? { backup = LT(1).getText(); } x=primary_expression y=braces
			{
				if (!Calc.isFunction(backup)) {
					if (y != null) {
						throw new CalcException("error: " + backup + ": not a function");
					}
					ret = x;
				}
				else {
					if (y == null) {
						throw new CalcException("error: " + backup + ": is a function");
					}
					ret = Calc.callOf(backup,y);
				}
				if (negate) {
					ret = -ret;
				}
			}
	;
	
braces returns [Vector args] throws CalcException  { args = null; Vector x; }
	:	LBRACE x=argument_expression_list RBRACE { args = x; }
	|	/* empty */
	;
	
argument_expression_list returns [Vector args] throws CalcException { args = new Vector(); double x; }
	:   x=additive_expression {	args.add(Double.valueOf(x)); } (COMMA x=additive_expression	{ args.add(Double.valueOf(x)); })*
	|	/* empty */
	;
	
primary_expression returns [double ret] throws CalcException { double x; }
	:	i:ID	{ ret = Calc.valueOf(i.getText()); }
	|	v:VALUE	{ ret = Double.parseDouble(v.getText()); }
	|	LBRACE x=additive_expression RBRACE { ret = x; }
	;	


class CalcLexer extends Lexer;
ID	:	('A'..'Z' | 'a'..'z' | '_') ('A'..'Z' | 'a'..'z' | '_' | '0'..'9')*
	;
	
WS	:	((' ' | '\t')+)
		{ $setType(Token.SKIP); }
	;
	
EOL	:	'\n';

VALUE	:	('0' | ('1'..'9' ('0'..'9')*)) ('.' ('0'..'9')+)?
		;
	
PLUS:	'+'
	;
	
MINUS:	'-';
LBRACE:	'(';
RBRACE:	')';
COMMA:	',';
MULT:	'*';
DIV:	'/';