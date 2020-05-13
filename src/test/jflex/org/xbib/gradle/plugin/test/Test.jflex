package org.xbib.gradle.plugin.test;
import java.io.IOException;

%%

%class Test
%int
%unicode
%line
%column

%{
  int token;
  double yylval;

  int nextToken() {
    try {
      return token = yylex();
    } catch (IOException e) {
      return token = -1;
    }
  }

  int getToken() {
    return token;
  }

  double getSemantic() {
    return yylval;
  }
%}

ws     = [ \t\f]
digit  = [0-9]
number = {digit}+(\.{digit}+)?(E[+\-]?{digit}+)?

%%
  \r|\n|\r\n                     { return 0; }
  {ws}+                          { }
  {number}                       { yylval = Double.parseDouble(yytext()); return 1; }
  [+\-*/()=]                     { return (int)(yytext().charAt(0)); }
  "*+"                           { return 2; }
  .                              { throw new Error(yytext()); }
