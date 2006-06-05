/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
// $ANTLR 2.7.1: "javaG.g" -> "JavaXref.java"$

package org.apache.forrest.forrestdoc.java.src.xref;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.forrest.forrestdoc.java.src.symtab.DummyClass;
import org.apache.forrest.forrestdoc.java.src.symtab.JavaVector;
import org.apache.forrest.forrestdoc.java.src.symtab.SymbolTable;
import org.apache.forrest.forrestdoc.java.src.util.SkipCRInputStream;

import antlr.ASTPair;
import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.SemanticException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.AST;
import antlr.collections.impl.ASTArray;
import antlr.collections.impl.BitSet;

public class JavaXref extends antlr.LLkParser
       implements JavaTokenTypes
 {

    /**
     * Counts the number of LT seen in the typeArguments production.
     * It is used in semantic predicates to ensure we have seen
     * enough closing '>' characters; which actually may have been
     * either GT, SR or BSR tokens.
     */
    private int ltCounter = 0;

    // these static variables are used to tell what kind of compound
    // statement is being parsed (see the compoundStatement rule
    static final int BODY          = 1;
    static final int CLASS_INIT    = 2;
    static final int INSTANCE_INIT = 3;
    static final int NEW_SCOPE     = 4;

    // We need a symbol table to track definitions
    private SymbolTable symbolTable;


    // This method decides what action to take based on the type of
    //   file we are looking at
    public static void doFile(File f, SymbolTable symbolTable,
			      boolean doRecurse, FileListener listener)
                              throws Exception {
        // If this is a directory, walk each file/dir in that directory
        if (f.isDirectory()) {
            String files[] = f.list();
            for(int i=0; i < files.length; i++) {
		File next;

		next = new File(f, files[i]);
		if (doRecurse || !next.isDirectory())
		    doFile(next, symbolTable, doRecurse, listener);
	    }
        }

        // otherwise, if this is a java file, parse it!
        else if ((f.getName().length()>5) &&
                f.getName().substring(f.getName().length()-5).equals(".java")) {
	    listener.notify(f.getAbsolutePath());
            symbolTable.setFile(f);
            //System.err.println("   "+f.getAbsolutePath());
            parseFile(new SkipCRInputStream(new FileInputStream(f)),
		      symbolTable);
        }
    }

    // Here's where we do the real work...
    public static void parseFile(InputStream s,
                                 SymbolTable symbolTable)
                                 throws Exception {
        try {
            // Create a scanner that reads from the input stream passed to us
            JavaLexer lexer = new JavaLexer(s);

	        lexer.setSymbolTable(symbolTable);

            // Tell the scanner to create tokens of class JavaToken
            lexer.setTokenObjectClass("org.apache.forrest.forrestdoc.java.src.xref.JavaToken");

            // Create a parser that reads from the scanner
            JavaXref parser = new JavaXref(lexer);

            // Tell the parser to use the symbol table passed to us
            parser.setSymbolTable(symbolTable);

            // start parsing at the compilationUnit rule
            parser.compilationUnit();
        }
        catch (Exception e) {
            System.err.println("parser exception: "+e);
            e.printStackTrace();   // so we can get stack trace     
        }
    }

    // Tell the parser which symbol table to use
    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
    
    //-------------------------------------------------------------------------
    // Symboltable adapter methods
    // The following methods are provided to give a single set of entry
    //   calls into the symbol table.  This makes it easy to add debugging
    //   code that will track all calls to symbolTable.popScope, for instance
    // These are direct pass-through calls to the symbolTable, but a
    //   few have special function.
    //-------------------------------------------------------------------------

    public void popScope()                 
    {
      // System.out.println("popScope");
      //RuntimeException ex = new RuntimeException();
      //ex.printStackTrace();
      symbolTable.popScope();
    }
    public void endFile()                  {symbolTable.popAllScopes();}
    public void defineBlock(JavaToken tok)
    {
      // System.out.println("defineBlock");
      symbolTable.defineBlock(tok);
    }
    public void definePackage(JavaToken t) {symbolTable.definePackage(t);}
    public void defineLabel(JavaToken t)   {symbolTable.defineLabel(t);}
    public void useDefaultPackage()        {symbolTable.useDefaultPackage();}
    public void reference(JavaToken t)     {symbolTable.reference(t);}
    public void setNearestClassScope()     {symbolTable.setNearestClassScope();}

    public void endMethodHead(JavaVector exceptions) {
        // System.out.println("endMethodHead");
        symbolTable.endMethodHead(exceptions);
    }

    public DummyClass dummyClass(JavaToken theClass) {
        // System.out.println("dummyClass");
        return symbolTable.getDummyClass(theClass);
    }


    public void defineClass(JavaToken theClass,
                            JavaToken superClass,
                            JavaVector interfaces) {
        // System.out.println("defineClass");
        symbolTable.defineClass(theClass, superClass, interfaces);
    }

    public void defineInterface(JavaToken theInterface,
                                JavaVector subInterfaces) {
        // System.out.println("defineInterface");
        symbolTable.defineInterface(theInterface, subInterfaces);
    }

    public void defineVar(JavaToken theVariable, JavaToken type) {
        // System.out.println("defineVar");
        symbolTable.defineVar(theVariable, type);
    }

    public void defineMethod(JavaToken theMethod, JavaToken type) {
        // System.out.println("defineMethod");
        symbolTable.defineMethod(theMethod, type);
    }

    public void addImport(JavaToken id, String className, String packageName) {
        // System.out.println("addImport");
        symbolTable.addImport(id, className, packageName);
    }

protected JavaXref(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public JavaXref(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected JavaXref(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public JavaXref(TokenStream lexer) {
  this(lexer,2);
}

public JavaXref(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final void compilationUnit() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST compilationUnit_AST = null;
		
		{
		switch ( LA(1)) {
		case LITERAL_package:
		{
			packageDefinition();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			break;
		}
		case EOF:
		case FINAL:
		case ABSTRACT:
		case STRICTFP:
		case SEMI:
		case LITERAL_import:
		case LITERAL_private:
		case LITERAL_public:
		case LITERAL_protected:
		case LITERAL_static:
		case LITERAL_transient:
		case LITERAL_native:
		case LITERAL_threadsafe:
		case LITERAL_synchronized:
		case LITERAL_volatile:
		case LITERAL_class:
		case LITERAL_interface:
		{
			if ( inputState.guessing==0 ) {
				useDefaultPackage();
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		_loop4:
		do {
			if ((LA(1)==LITERAL_import)) {
				importDefinition();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop4;
			}
			
		} while (true);
		}
		{
		_loop6:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				typeDefinition();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop6;
			}
			
		} while (true);
		}
		AST tmp1_AST = null;
		tmp1_AST = (AST)astFactory.create(LT(1));
		match(Token.EOF_TYPE);
		if ( inputState.guessing==0 ) {
			endFile();
		}
		compilationUnit_AST = (AST)currentAST.root;
		returnAST = compilationUnit_AST;
	}
	
	public final void packageDefinition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST packageDefinition_AST = null;
		Token  p = null;
		AST p_AST = null;
		JavaToken id;
		
		try {      // for error handling
			p = LT(1);
			if (inputState.guessing==0) {
				p_AST = (AST)astFactory.create(p);
				astFactory.makeASTRoot(currentAST, p_AST);
			}
			match(LITERAL_package);
			if ( inputState.guessing==0 ) {
				p_AST.setType(PACKAGE_DEF);
			}
			id=identifier();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			AST tmp2_AST = null;
			tmp2_AST = (AST)astFactory.create(LT(1));
			match(SEMI);
			if ( inputState.guessing==0 ) {
				definePackage(id);
			}
			packageDefinition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		returnAST = packageDefinition_AST;
	}
	
	public final void importDefinition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST importDefinition_AST = null;
		Token  i = null;
		AST i_AST = null;
		
		try {      // for error handling
			i = LT(1);
			if (inputState.guessing==0) {
				i_AST = (AST)astFactory.create(i);
				astFactory.makeASTRoot(currentAST, i_AST);
			}
			match(LITERAL_import);
			if ( inputState.guessing==0 ) {
				i_AST.setType(IMPORT);
			}
			identifierStar();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			AST tmp3_AST = null;
			tmp3_AST = (AST)astFactory.create(LT(1));
			match(SEMI);
			importDefinition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		returnAST = importDefinition_AST;
	}
	
	public final void typeDefinition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeDefinition_AST = null;
		AST m_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case FINAL:
			case ABSTRACT:
			case STRICTFP:
			case LITERAL_private:
			case LITERAL_public:
			case LITERAL_protected:
			case LITERAL_static:
			case LITERAL_transient:
			case LITERAL_native:
			case LITERAL_threadsafe:
			case LITERAL_synchronized:
			case LITERAL_volatile:
			case LITERAL_class:
			case LITERAL_interface:
			{
				modifiers();
				if (inputState.guessing==0) {
					m_AST = (AST)returnAST;
				}
				{
				switch ( LA(1)) {
				case LITERAL_class:
				{
					classDefinition(m_AST);
					if (inputState.guessing==0) {
						astFactory.addASTChild(currentAST, returnAST);
					}
					break;
				}
				case LITERAL_interface:
				{
					interfaceDefinition(m_AST);
					if (inputState.guessing==0) {
						astFactory.addASTChild(currentAST, returnAST);
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				typeDefinition_AST = (AST)currentAST.root;
				break;
			}
			case SEMI:
			{
				AST tmp4_AST = null;
				tmp4_AST = (AST)astFactory.create(LT(1));
				match(SEMI);
				typeDefinition_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_2);
			} else {
			  throw ex;
			}
		}
		returnAST = typeDefinition_AST;
	}
	
	public final JavaToken  identifier() throws RecognitionException, TokenStreamException {
		JavaToken t;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST identifier_AST = null;
		Token  id1 = null;
		AST id1_AST = null;
		Token  id2 = null;
		AST id2_AST = null;
		t=null;
		
		id1 = LT(1);
		if (inputState.guessing==0) {
			id1_AST = (AST)astFactory.create(id1);
			astFactory.addASTChild(currentAST, id1_AST);
		}
		match(IDENT);
		if ( inputState.guessing==0 ) {
			t=(JavaToken)id1;
		}
		{
		_loop35:
		do {
			if ((LA(1)==DOT)) {
				AST tmp5_AST = null;
				if (inputState.guessing==0) {
					tmp5_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp5_AST);
				}
				match(DOT);
				id2 = LT(1);
				if (inputState.guessing==0) {
					id2_AST = (AST)astFactory.create(id2);
					astFactory.addASTChild(currentAST, id2_AST);
				}
				match(IDENT);
				if ( inputState.guessing==0 ) {
					
					t.setText(t.getText() + "." + id2.getText());
								 t.setColumn(id2.getColumn());
					
				}
			}
			else {
				break _loop35;
			}
			
		} while (true);
		}
		identifier_AST = (AST)currentAST.root;
		returnAST = identifier_AST;
		return t;
	}
	
	public final void identifierStar() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST identifierStar_AST = null;
		Token  id = null;
		AST id_AST = null;
		Token  id2 = null;
		AST id2_AST = null;
		String className=""; String packageName="";
		
		id = LT(1);
		if (inputState.guessing==0) {
			id_AST = (AST)astFactory.create(id);
			astFactory.addASTChild(currentAST, id_AST);
		}
		match(IDENT);
		if ( inputState.guessing==0 ) {
			className=id.getText();
		}
		{
		_loop38:
		do {
			if ((LA(1)==DOT) && (LA(2)==IDENT)) {
				AST tmp6_AST = null;
				if (inputState.guessing==0) {
					tmp6_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp6_AST);
				}
				match(DOT);
				id2 = LT(1);
				if (inputState.guessing==0) {
					id2_AST = (AST)astFactory.create(id2);
					astFactory.addASTChild(currentAST, id2_AST);
				}
				match(IDENT);
				if ( inputState.guessing==0 ) {
					packageName += "."+className; className = id2.getText();
				}
			}
			else {
				break _loop38;
			}
			
		} while (true);
		}
		{
		switch ( LA(1)) {
		case DOT:
		{
			AST tmp7_AST = null;
			if (inputState.guessing==0) {
				tmp7_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp7_AST);
			}
			match(DOT);
			AST tmp8_AST = null;
			if (inputState.guessing==0) {
				tmp8_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp8_AST);
			}
			match(STAR);
			if ( inputState.guessing==0 ) {
				packageName += "."+className; className = null;
			}
			break;
		}
		case SEMI:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
			// put the overall name in the token's text
			if (packageName.equals(""))
			id.setText(className);
			else if (className == null)
			id.setText(packageName.substring(1));
			else
			id.setText(packageName.substring(1) + "." + className);
			
			// tell the symbol table about the import
			addImport((JavaToken)id, className, packageName);
			
		}
		identifierStar_AST = (AST)currentAST.root;
		returnAST = identifierStar_AST;
	}
	
	public final void modifiers() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST modifiers_AST = null;
		
		{
		_loop14:
		do {
			if ((_tokenSet_3.member(LA(1)))) {
				modifier();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop14;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			modifiers_AST = (AST)currentAST.root;
			modifiers_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(MODIFIERS,"MODIFIERS")).add(modifiers_AST));
			currentAST.root = modifiers_AST;
			currentAST.child = modifiers_AST!=null &&modifiers_AST.getFirstChild()!=null ?
				modifiers_AST.getFirstChild() : modifiers_AST;
			currentAST.advanceChildToEnd();
		}
		modifiers_AST = (AST)currentAST.root;
		returnAST = modifiers_AST;
	}
	
	public final void classDefinition(
		AST modifiers
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST classDefinition_AST = null;
		Token  id = null;
		AST id_AST = null;
		AST sc_AST = null;
		AST ic_AST = null;
		AST cb_AST = null;
		JavaToken superClass=null; JavaVector interfaces=null;
		
		AST tmp9_AST = null;
		if (inputState.guessing==0) {
			tmp9_AST = (AST)astFactory.create(LT(1));
		}
		match(LITERAL_class);
		id = LT(1);
		if (inputState.guessing==0) {
			id_AST = (AST)astFactory.create(id);
		}
		match(IDENT);
		{
		switch ( LA(1)) {
		case LT:
		{
			typeParameters();
			break;
		}
		case LITERAL_extends:
		case LCURLY:
		case LITERAL_implements:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		superClass=superClassClause();
		if (inputState.guessing==0) {
			sc_AST = (AST)returnAST;
		}
		interfaces=implementsClause();
		if (inputState.guessing==0) {
			ic_AST = (AST)returnAST;
		}
		if ( inputState.guessing==0 ) {
			defineClass((JavaToken)id, superClass, interfaces);
		}
		classBlock();
		if (inputState.guessing==0) {
			cb_AST = (AST)returnAST;
		}
		if ( inputState.guessing==0 ) {
			classDefinition_AST = (AST)currentAST.root;
			classDefinition_AST = (AST)astFactory.make( (new ASTArray(6)).add((AST)astFactory.create(CLASS_DEF,"CLASS_DEF")).add(modifiers).add(id_AST).add(sc_AST).add(ic_AST).add(cb_AST));
			currentAST.root = classDefinition_AST;
			currentAST.child = classDefinition_AST!=null &&classDefinition_AST.getFirstChild()!=null ?
				classDefinition_AST.getFirstChild() : classDefinition_AST;
			currentAST.advanceChildToEnd();
		}
		if ( inputState.guessing==0 ) {
			popScope();
		}
		returnAST = classDefinition_AST;
	}
	
	public final void interfaceDefinition(
		AST modifiers
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interfaceDefinition_AST = null;
		Token  id = null;
		AST id_AST = null;
		AST ie_AST = null;
		AST cb_AST = null;
		JavaVector superInterfaces = null;
		
		AST tmp10_AST = null;
		if (inputState.guessing==0) {
			tmp10_AST = (AST)astFactory.create(LT(1));
		}
		match(LITERAL_interface);
		id = LT(1);
		if (inputState.guessing==0) {
			id_AST = (AST)astFactory.create(id);
		}
		match(IDENT);
		{
		switch ( LA(1)) {
		case LT:
		{
			typeParameters();
			break;
		}
		case LITERAL_extends:
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		superInterfaces=interfaceExtends();
		if (inputState.guessing==0) {
			ie_AST = (AST)returnAST;
		}
		if ( inputState.guessing==0 ) {
			defineInterface((JavaToken)id, superInterfaces);
		}
		classBlock();
		if (inputState.guessing==0) {
			cb_AST = (AST)returnAST;
		}
		if ( inputState.guessing==0 ) {
			interfaceDefinition_AST = (AST)currentAST.root;
			interfaceDefinition_AST = (AST)astFactory.make( (new ASTArray(5)).add((AST)astFactory.create(INTERFACE_DEF,"INTERFACE_DEF")).add(modifiers).add(id_AST).add(ie_AST).add(cb_AST));
			currentAST.root = interfaceDefinition_AST;
			currentAST.child = interfaceDefinition_AST!=null &&interfaceDefinition_AST.getFirstChild()!=null ?
				interfaceDefinition_AST.getFirstChild() : interfaceDefinition_AST;
			currentAST.advanceChildToEnd();
		}
		if ( inputState.guessing==0 ) {
			popScope();
		}
		returnAST = interfaceDefinition_AST;
	}
	
/** A declaration is the creation of a reference or primitive-type variable
 *  Create a separate Type/Var tree for each var in the var list.
 */
	public final void declaration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaration_AST = null;
		AST m_AST = null;
		AST t_AST = null;
		AST v_AST = null;
		JavaToken type;
		
		modifiers();
		if (inputState.guessing==0) {
			m_AST = (AST)returnAST;
		}
		type=typeSpec(false);
		if (inputState.guessing==0) {
			t_AST = (AST)returnAST;
		}
		variableDefinitions(m_AST,t_AST,type);
		if (inputState.guessing==0) {
			v_AST = (AST)returnAST;
		}
		if ( inputState.guessing==0 ) {
			declaration_AST = (AST)currentAST.root;
			declaration_AST = v_AST;
			currentAST.root = declaration_AST;
			currentAST.child = declaration_AST!=null &&declaration_AST.getFirstChild()!=null ?
				declaration_AST.getFirstChild() : declaration_AST;
			currentAST.advanceChildToEnd();
		}
		returnAST = declaration_AST;
	}
	
	public final JavaToken  typeSpec(
		boolean addImagNode
	) throws RecognitionException, TokenStreamException {
		JavaToken t;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeSpec_AST = null;
		t=null;
		
		switch ( LA(1)) {
		case IDENT:
		{
			t=classTypeSpec(addImagNode);
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			typeSpec_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_void:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_short:
		case LITERAL_int:
		case LITERAL_float:
		case LITERAL_long:
		case LITERAL_double:
		{
			t=builtInTypeSpec(addImagNode);
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			typeSpec_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = typeSpec_AST;
		return t;
	}
	
	public final void variableDefinitions(
		AST mods, AST t, JavaToken type
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variableDefinitions_AST = null;
		
		variableDeclarator(getASTFactory().dupTree(mods),
						   getASTFactory().dupTree(t),
						   type);
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		_loop79:
		do {
			if ((LA(1)==COMMA)) {
				AST tmp11_AST = null;
				tmp11_AST = (AST)astFactory.create(LT(1));
				match(COMMA);
				variableDeclarator(getASTFactory().dupTree(mods),
							   getASTFactory().dupTree(t),
							   type);
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop79;
			}
			
		} while (true);
		}
		variableDefinitions_AST = (AST)currentAST.root;
		returnAST = variableDefinitions_AST;
	}
	
	public final void modifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST modifier_AST = null;
		
		switch ( LA(1)) {
		case LITERAL_private:
		{
			AST tmp12_AST = null;
			if (inputState.guessing==0) {
				tmp12_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp12_AST);
			}
			match(LITERAL_private);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_public:
		{
			AST tmp13_AST = null;
			if (inputState.guessing==0) {
				tmp13_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp13_AST);
			}
			match(LITERAL_public);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_protected:
		{
			AST tmp14_AST = null;
			if (inputState.guessing==0) {
				tmp14_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp14_AST);
			}
			match(LITERAL_protected);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_static:
		{
			AST tmp15_AST = null;
			if (inputState.guessing==0) {
				tmp15_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp15_AST);
			}
			match(LITERAL_static);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_transient:
		{
			AST tmp16_AST = null;
			if (inputState.guessing==0) {
				tmp16_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp16_AST);
			}
			match(LITERAL_transient);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case FINAL:
		{
			AST tmp17_AST = null;
			if (inputState.guessing==0) {
				tmp17_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp17_AST);
			}
			match(FINAL);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case ABSTRACT:
		{
			AST tmp18_AST = null;
			if (inputState.guessing==0) {
				tmp18_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp18_AST);
			}
			match(ABSTRACT);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_native:
		{
			AST tmp19_AST = null;
			if (inputState.guessing==0) {
				tmp19_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp19_AST);
			}
			match(LITERAL_native);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_threadsafe:
		{
			AST tmp20_AST = null;
			if (inputState.guessing==0) {
				tmp20_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp20_AST);
			}
			match(LITERAL_threadsafe);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_synchronized:
		{
			AST tmp21_AST = null;
			if (inputState.guessing==0) {
				tmp21_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp21_AST);
			}
			match(LITERAL_synchronized);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_volatile:
		{
			AST tmp22_AST = null;
			if (inputState.guessing==0) {
				tmp22_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp22_AST);
			}
			match(LITERAL_volatile);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case STRICTFP:
		{
			AST tmp23_AST = null;
			if (inputState.guessing==0) {
				tmp23_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp23_AST);
			}
			match(STRICTFP);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = modifier_AST;
	}
	
	public final JavaToken  classTypeSpec(
		boolean addImagNode
	) throws RecognitionException, TokenStreamException {
		JavaToken t;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST classTypeSpec_AST = null;
		t=null;
		
		t=classOrInterfaceType(addImagNode);
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		arraySpecOpt();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		if ( inputState.guessing==0 ) {
			classTypeSpec_AST = (AST)currentAST.root;
			
						if ( addImagNode ) {
							classTypeSpec_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(TYPE,"TYPE")).add(classTypeSpec_AST));
						}
					
			currentAST.root = classTypeSpec_AST;
			currentAST.child = classTypeSpec_AST!=null &&classTypeSpec_AST.getFirstChild()!=null ?
				classTypeSpec_AST.getFirstChild() : classTypeSpec_AST;
			currentAST.advanceChildToEnd();
		}
		classTypeSpec_AST = (AST)currentAST.root;
		returnAST = classTypeSpec_AST;
		return t;
	}
	
	public final JavaToken  builtInTypeSpec(
		boolean addImagNode
	) throws RecognitionException, TokenStreamException {
		JavaToken t;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST builtInTypeSpec_AST = null;
		t=null;
		
		t=builtInType();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		arraySpecOpt();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		if ( inputState.guessing==0 ) {
			builtInTypeSpec_AST = (AST)currentAST.root;
			
						if ( addImagNode ) {
							builtInTypeSpec_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(TYPE,"TYPE")).add(builtInTypeSpec_AST));
						}
					
			currentAST.root = builtInTypeSpec_AST;
			currentAST.child = builtInTypeSpec_AST!=null &&builtInTypeSpec_AST.getFirstChild()!=null ?
				builtInTypeSpec_AST.getFirstChild() : builtInTypeSpec_AST;
			currentAST.advanceChildToEnd();
		}
		builtInTypeSpec_AST = (AST)currentAST.root;
		returnAST = builtInTypeSpec_AST;
		return t;
	}
	
	public final void arraySpecOpt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arraySpecOpt_AST = null;
		Token  lb = null;
		AST lb_AST = null;
		
		{
		_loop18:
		do {
			if ((LA(1)==LBRACK) && (LA(2)==RBRACK)) {
				lb = LT(1);
				if (inputState.guessing==0) {
					lb_AST = (AST)astFactory.create(lb);
					astFactory.makeASTRoot(currentAST, lb_AST);
				}
				match(LBRACK);
				if ( inputState.guessing==0 ) {
					lb_AST.setType(ARRAY_DECLARATOR);
				}
				AST tmp24_AST = null;
				tmp24_AST = (AST)astFactory.create(LT(1));
				match(RBRACK);
			}
			else {
				break _loop18;
			}
			
		} while (true);
		}
		arraySpecOpt_AST = (AST)currentAST.root;
		returnAST = arraySpecOpt_AST;
	}
	
	public final JavaToken  classOrInterfaceType(
		boolean addImagNode
	) throws RecognitionException, TokenStreamException {
		JavaToken t;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST classOrInterfaceType_AST = null;
		Token  id1 = null;
		AST id1_AST = null;
		Token  id2 = null;
		AST id2_AST = null;
		t=null;
		
		id1 = LT(1);
		if (inputState.guessing==0) {
			id1_AST = (AST)astFactory.create(id1);
			astFactory.addASTChild(currentAST, id1_AST);
		}
		match(IDENT);
		if ( inputState.guessing==0 ) {
			t = (JavaToken)id1;
		}
		{
		switch ( LA(1)) {
		case LT:
		{
			typeArguments(addImagNode);
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			break;
		}
		case SEMI:
		case LBRACK:
		case RBRACK:
		case IDENT:
		case DOT:
		case COMMA:
		case GT:
		case SR:
		case BSR:
		case LITERAL_void:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_short:
		case LITERAL_int:
		case LITERAL_float:
		case LITERAL_long:
		case LITERAL_double:
		case LITERAL_extends:
		case BAND:
		case LCURLY:
		case RCURLY:
		case LITERAL_implements:
		case LPAREN:
		case RPAREN:
		case ASSIGN:
		case COLON:
		case PLUS_ASSIGN:
		case MINUS_ASSIGN:
		case STAR_ASSIGN:
		case DIV_ASSIGN:
		case MOD_ASSIGN:
		case SR_ASSIGN:
		case BSR_ASSIGN:
		case SL_ASSIGN:
		case BAND_ASSIGN:
		case BXOR_ASSIGN:
		case BOR_ASSIGN:
		case QUESTION:
		case LOR:
		case LAND:
		case BOR:
		case BXOR:
		case NOT_EQUAL:
		case EQUAL:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		_loop24:
		do {
			if ((LA(1)==DOT) && (LA(2)==IDENT)) {
				AST tmp25_AST = null;
				if (inputState.guessing==0) {
					tmp25_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp25_AST);
				}
				match(DOT);
				id2 = LT(1);
				if (inputState.guessing==0) {
					id2_AST = (AST)astFactory.create(id2);
					astFactory.addASTChild(currentAST, id2_AST);
				}
				match(IDENT);
				if ( inputState.guessing==0 ) {
					
					t.setText(t.getText() + "." + id2.getText());
								 t.setColumn(id2.getColumn());
					
				}
				{
				switch ( LA(1)) {
				case LT:
				{
					typeArguments(addImagNode);
					if (inputState.guessing==0) {
						astFactory.addASTChild(currentAST, returnAST);
					}
					break;
				}
				case SEMI:
				case LBRACK:
				case RBRACK:
				case IDENT:
				case DOT:
				case COMMA:
				case GT:
				case SR:
				case BSR:
				case LITERAL_void:
				case LITERAL_boolean:
				case LITERAL_byte:
				case LITERAL_char:
				case LITERAL_short:
				case LITERAL_int:
				case LITERAL_float:
				case LITERAL_long:
				case LITERAL_double:
				case LITERAL_extends:
				case BAND:
				case LCURLY:
				case RCURLY:
				case LITERAL_implements:
				case LPAREN:
				case RPAREN:
				case ASSIGN:
				case COLON:
				case PLUS_ASSIGN:
				case MINUS_ASSIGN:
				case STAR_ASSIGN:
				case DIV_ASSIGN:
				case MOD_ASSIGN:
				case SR_ASSIGN:
				case BSR_ASSIGN:
				case SL_ASSIGN:
				case BAND_ASSIGN:
				case BXOR_ASSIGN:
				case BOR_ASSIGN:
				case QUESTION:
				case LOR:
				case LAND:
				case BOR:
				case BXOR:
				case NOT_EQUAL:
				case EQUAL:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
			}
			else {
				break _loop24;
			}
			
		} while (true);
		}
		classOrInterfaceType_AST = (AST)currentAST.root;
		returnAST = classOrInterfaceType_AST;
		return t;
	}
	
	public final void typeArguments(
		boolean addImagNode
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeArguments_AST = null;
		int currentLtLevel = 0;
		
		if ( inputState.guessing==0 ) {
			currentLtLevel = ltCounter;
		}
		AST tmp26_AST = null;
		if (inputState.guessing==0) {
			tmp26_AST = (AST)astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp26_AST);
		}
		match(LT);
		if ( inputState.guessing==0 ) {
			ltCounter++;
		}
		classTypeSpec(addImagNode);
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		_loop27:
		do {
			if ((LA(1)==COMMA) && (LA(2)==IDENT)) {
				AST tmp27_AST = null;
				if (inputState.guessing==0) {
					tmp27_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp27_AST);
				}
				match(COMMA);
				classTypeSpec(addImagNode);
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop27;
			}
			
		} while (true);
		}
		{
		if (((LA(1) >= GT && LA(1) <= BSR)) && (_tokenSet_4.member(LA(2)))) {
			typeArgumentsEnd();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
		}
		else if ((_tokenSet_4.member(LA(1))) && (_tokenSet_5.member(LA(2)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		if (!((currentLtLevel != 0) || ltCounter == currentLtLevel))
		  throw new SemanticException("(currentLtLevel != 0) || ltCounter == currentLtLevel");
		typeArguments_AST = (AST)currentAST.root;
		returnAST = typeArguments_AST;
	}
	
	protected final void typeArgumentsEnd() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeArgumentsEnd_AST = null;
		
		switch ( LA(1)) {
		case GT:
		{
			AST tmp28_AST = null;
			if (inputState.guessing==0) {
				tmp28_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp28_AST);
			}
			match(GT);
			if ( inputState.guessing==0 ) {
				ltCounter-=1;
			}
			typeArgumentsEnd_AST = (AST)currentAST.root;
			break;
		}
		case SR:
		{
			AST tmp29_AST = null;
			if (inputState.guessing==0) {
				tmp29_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp29_AST);
			}
			match(SR);
			if ( inputState.guessing==0 ) {
				ltCounter-=2;
			}
			typeArgumentsEnd_AST = (AST)currentAST.root;
			break;
		}
		case BSR:
		{
			AST tmp30_AST = null;
			if (inputState.guessing==0) {
				tmp30_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp30_AST);
			}
			match(BSR);
			if ( inputState.guessing==0 ) {
				ltCounter-=3;
			}
			typeArgumentsEnd_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = typeArgumentsEnd_AST;
	}
	
	public final JavaToken  builtInType() throws RecognitionException, TokenStreamException {
		JavaToken t;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST builtInType_AST = null;
		Token  bVoid = null;
		AST bVoid_AST = null;
		Token  bBoolean = null;
		AST bBoolean_AST = null;
		Token  bByte = null;
		AST bByte_AST = null;
		Token  bChar = null;
		AST bChar_AST = null;
		Token  bShort = null;
		AST bShort_AST = null;
		Token  bInt = null;
		AST bInt_AST = null;
		Token  bFloat = null;
		AST bFloat_AST = null;
		Token  bLong = null;
		AST bLong_AST = null;
		Token  bDouble = null;
		AST bDouble_AST = null;
		t=null;
		
		switch ( LA(1)) {
		case LITERAL_void:
		{
			bVoid = LT(1);
			if (inputState.guessing==0) {
				bVoid_AST = (AST)astFactory.create(bVoid);
				astFactory.addASTChild(currentAST, bVoid_AST);
			}
			match(LITERAL_void);
			if ( inputState.guessing==0 ) {
				t = (JavaToken)bVoid;
			}
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_boolean:
		{
			bBoolean = LT(1);
			if (inputState.guessing==0) {
				bBoolean_AST = (AST)astFactory.create(bBoolean);
				astFactory.addASTChild(currentAST, bBoolean_AST);
			}
			match(LITERAL_boolean);
			if ( inputState.guessing==0 ) {
				t = (JavaToken)bBoolean;
			}
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_byte:
		{
			bByte = LT(1);
			if (inputState.guessing==0) {
				bByte_AST = (AST)astFactory.create(bByte);
				astFactory.addASTChild(currentAST, bByte_AST);
			}
			match(LITERAL_byte);
			if ( inputState.guessing==0 ) {
				t = (JavaToken)bByte;
			}
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_char:
		{
			bChar = LT(1);
			if (inputState.guessing==0) {
				bChar_AST = (AST)astFactory.create(bChar);
				astFactory.addASTChild(currentAST, bChar_AST);
			}
			match(LITERAL_char);
			if ( inputState.guessing==0 ) {
				t = (JavaToken)bChar;
			}
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_short:
		{
			bShort = LT(1);
			if (inputState.guessing==0) {
				bShort_AST = (AST)astFactory.create(bShort);
				astFactory.addASTChild(currentAST, bShort_AST);
			}
			match(LITERAL_short);
			if ( inputState.guessing==0 ) {
				t = (JavaToken)bShort;
			}
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_int:
		{
			bInt = LT(1);
			if (inputState.guessing==0) {
				bInt_AST = (AST)astFactory.create(bInt);
				astFactory.addASTChild(currentAST, bInt_AST);
			}
			match(LITERAL_int);
			if ( inputState.guessing==0 ) {
				t = (JavaToken)bInt;
			}
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_float:
		{
			bFloat = LT(1);
			if (inputState.guessing==0) {
				bFloat_AST = (AST)astFactory.create(bFloat);
				astFactory.addASTChild(currentAST, bFloat_AST);
			}
			match(LITERAL_float);
			if ( inputState.guessing==0 ) {
				t = (JavaToken)bFloat;
			}
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_long:
		{
			bLong = LT(1);
			if (inputState.guessing==0) {
				bLong_AST = (AST)astFactory.create(bLong);
				astFactory.addASTChild(currentAST, bLong_AST);
			}
			match(LITERAL_long);
			if ( inputState.guessing==0 ) {
				t = (JavaToken)bLong;
			}
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_double:
		{
			bDouble = LT(1);
			if (inputState.guessing==0) {
				bDouble_AST = (AST)astFactory.create(bDouble);
				astFactory.addASTChild(currentAST, bDouble_AST);
			}
			match(LITERAL_double);
			if ( inputState.guessing==0 ) {
				t = (JavaToken)bDouble;
			}
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = builtInType_AST;
		return t;
	}
	
	public final JavaToken  type() throws RecognitionException, TokenStreamException {
		JavaToken t;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST type_AST = null;
		t=null;
		
		switch ( LA(1)) {
		case IDENT:
		{
			t=classOrInterfaceType(false);
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			type_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_void:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_short:
		case LITERAL_int:
		case LITERAL_float:
		case LITERAL_long:
		case LITERAL_double:
		{
			t=builtInType();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			type_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = type_AST;
		return t;
	}
	
	public final void typeParameters() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeParameters_AST = null;
		int currentLtLevel = 0;
		
		if ( inputState.guessing==0 ) {
			currentLtLevel = ltCounter;
		}
		AST tmp31_AST = null;
		if (inputState.guessing==0) {
			tmp31_AST = (AST)astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp31_AST);
		}
		match(LT);
		if ( inputState.guessing==0 ) {
			ltCounter++;
		}
		typeParameter();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		_loop49:
		do {
			if ((LA(1)==COMMA)) {
				AST tmp32_AST = null;
				if (inputState.guessing==0) {
					tmp32_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp32_AST);
				}
				match(COMMA);
				typeParameter();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop49;
			}
			
		} while (true);
		}
		{
		switch ( LA(1)) {
		case GT:
		case SR:
		case BSR:
		{
			typeArgumentsEnd();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			break;
		}
		case IDENT:
		case LITERAL_void:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_short:
		case LITERAL_int:
		case LITERAL_float:
		case LITERAL_long:
		case LITERAL_double:
		case LITERAL_extends:
		case LCURLY:
		case LITERAL_implements:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if (!((currentLtLevel != 0) || ltCounter == currentLtLevel))
		  throw new SemanticException("(currentLtLevel != 0) || ltCounter == currentLtLevel");
		typeParameters_AST = (AST)currentAST.root;
		returnAST = typeParameters_AST;
	}
	
	public final JavaToken  superClassClause() throws RecognitionException, TokenStreamException {
		JavaToken t;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST superClassClause_AST = null;
		t=null;
		
		{
		switch ( LA(1)) {
		case LITERAL_extends:
		{
			AST tmp33_AST = null;
			if (inputState.guessing==0) {
				tmp33_AST = (AST)astFactory.create(LT(1));
			}
			match(LITERAL_extends);
			t=classOrInterfaceType(false);
			break;
		}
		case LCURLY:
		case LITERAL_implements:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		returnAST = superClassClause_AST;
		return t;
	}
	
	public final JavaVector  implementsClause() throws RecognitionException, TokenStreamException {
		JavaVector inters;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST implementsClause_AST = null;
		Token  i = null;
		AST i_AST = null;
		inters = new JavaVector(); JavaToken id;
		
		{
		switch ( LA(1)) {
		case LITERAL_implements:
		{
			i = LT(1);
			if (inputState.guessing==0) {
				i_AST = (AST)astFactory.create(i);
			}
			match(LITERAL_implements);
			id=classOrInterfaceType(false);
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			if ( inputState.guessing==0 ) {
				inters.addElement(dummyClass(id));
			}
			{
			_loop65:
			do {
				if ((LA(1)==COMMA)) {
					AST tmp34_AST = null;
					tmp34_AST = (AST)astFactory.create(LT(1));
					match(COMMA);
					id=classOrInterfaceType(false);
					if (inputState.guessing==0) {
						astFactory.addASTChild(currentAST, returnAST);
					}
					if ( inputState.guessing==0 ) {
						inters.addElement(dummyClass(id));
					}
				}
				else {
					break _loop65;
				}
				
			} while (true);
			}
			break;
		}
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			implementsClause_AST = (AST)currentAST.root;
			implementsClause_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(IMPLEMENTS_CLAUSE,"IMPLEMENTS_CLAUSE")).add(implementsClause_AST));
			currentAST.root = implementsClause_AST;
			currentAST.child = implementsClause_AST!=null &&implementsClause_AST.getFirstChild()!=null ?
				implementsClause_AST.getFirstChild() : implementsClause_AST;
			currentAST.advanceChildToEnd();
		}
		implementsClause_AST = (AST)currentAST.root;
		returnAST = implementsClause_AST;
		return inters;
	}
	
	public final void classBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST classBlock_AST = null;
		
		AST tmp35_AST = null;
		tmp35_AST = (AST)astFactory.create(LT(1));
		match(LCURLY);
		{
		_loop57:
		do {
			switch ( LA(1)) {
			case FINAL:
			case ABSTRACT:
			case STRICTFP:
			case IDENT:
			case LT:
			case LITERAL_void:
			case LITERAL_boolean:
			case LITERAL_byte:
			case LITERAL_char:
			case LITERAL_short:
			case LITERAL_int:
			case LITERAL_float:
			case LITERAL_long:
			case LITERAL_double:
			case LITERAL_private:
			case LITERAL_public:
			case LITERAL_protected:
			case LITERAL_static:
			case LITERAL_transient:
			case LITERAL_native:
			case LITERAL_threadsafe:
			case LITERAL_synchronized:
			case LITERAL_volatile:
			case LITERAL_class:
			case LITERAL_interface:
			case LCURLY:
			{
				field();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
				break;
			}
			case SEMI:
			{
				AST tmp36_AST = null;
				tmp36_AST = (AST)astFactory.create(LT(1));
				match(SEMI);
				break;
			}
			default:
			{
				break _loop57;
			}
			}
		} while (true);
		}
		AST tmp37_AST = null;
		tmp37_AST = (AST)astFactory.create(LT(1));
		match(RCURLY);
		if ( inputState.guessing==0 ) {
			classBlock_AST = (AST)currentAST.root;
			classBlock_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(OBJBLOCK,"OBJBLOCK")).add(classBlock_AST));
			currentAST.root = classBlock_AST;
			currentAST.child = classBlock_AST!=null &&classBlock_AST.getFirstChild()!=null ?
				classBlock_AST.getFirstChild() : classBlock_AST;
			currentAST.advanceChildToEnd();
		}
		classBlock_AST = (AST)currentAST.root;
		returnAST = classBlock_AST;
	}
	
	public final JavaVector  interfaceExtends() throws RecognitionException, TokenStreamException {
		JavaVector supers;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interfaceExtends_AST = null;
		Token  e = null;
		AST e_AST = null;
		JavaToken id; supers = new JavaVector();
		
		{
		switch ( LA(1)) {
		case LITERAL_extends:
		{
			e = LT(1);
			if (inputState.guessing==0) {
				e_AST = (AST)astFactory.create(e);
			}
			match(LITERAL_extends);
			id=classOrInterfaceType(false);
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			if ( inputState.guessing==0 ) {
				supers.addElement(dummyClass(id));
			}
			{
			_loop61:
			do {
				if ((LA(1)==COMMA)) {
					AST tmp38_AST = null;
					tmp38_AST = (AST)astFactory.create(LT(1));
					match(COMMA);
					id=classOrInterfaceType(false);
					if (inputState.guessing==0) {
						astFactory.addASTChild(currentAST, returnAST);
					}
					if ( inputState.guessing==0 ) {
						supers.addElement(dummyClass(id));
					}
				}
				else {
					break _loop61;
				}
				
			} while (true);
			}
			break;
		}
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			interfaceExtends_AST = (AST)currentAST.root;
			interfaceExtends_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(EXTENDS_CLAUSE,"EXTENDS_CLAUSE")).add(interfaceExtends_AST));
			currentAST.root = interfaceExtends_AST;
			currentAST.child = interfaceExtends_AST!=null &&interfaceExtends_AST.getFirstChild()!=null ?
				interfaceExtends_AST.getFirstChild() : interfaceExtends_AST;
			currentAST.advanceChildToEnd();
		}
		interfaceExtends_AST = (AST)currentAST.root;
		returnAST = interfaceExtends_AST;
		return supers;
	}
	
	public final void typeParameter() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeParameter_AST = null;
		
		AST tmp39_AST = null;
		if (inputState.guessing==0) {
			tmp39_AST = (AST)astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp39_AST);
		}
		match(IDENT);
		{
		if ((LA(1)==LITERAL_extends) && (LA(2)==IDENT)) {
			AST tmp40_AST = null;
			if (inputState.guessing==0) {
				tmp40_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp40_AST);
			}
			match(LITERAL_extends);
			classOrInterfaceType(false);
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			{
			_loop54:
			do {
				if ((LA(1)==BAND)) {
					AST tmp41_AST = null;
					if (inputState.guessing==0) {
						tmp41_AST = (AST)astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp41_AST);
					}
					match(BAND);
					classOrInterfaceType(false);
					if (inputState.guessing==0) {
						astFactory.addASTChild(currentAST, returnAST);
					}
				}
				else {
					break _loop54;
				}
				
			} while (true);
			}
		}
		else if ((_tokenSet_6.member(LA(1))) && (_tokenSet_7.member(LA(2)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		typeParameter_AST = (AST)currentAST.root;
		returnAST = typeParameter_AST;
	}
	
	public final void field() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST field_AST = null;
		AST mods_AST = null;
		AST h_AST = null;
		AST s_AST = null;
		AST cd_AST = null;
		AST id_AST = null;
		AST t_AST = null;
		Token  method = null;
		AST method_AST = null;
		AST param_AST = null;
		AST rt_AST = null;
		AST tc_AST = null;
		AST s2_AST = null;
		AST v_AST = null;
		AST s3_AST = null;
		AST s4_AST = null;
		JavaToken type; JavaVector exceptions=null;
		
		if ((_tokenSet_8.member(LA(1))) && (_tokenSet_9.member(LA(2)))) {
			modifiers();
			if (inputState.guessing==0) {
				mods_AST = (AST)returnAST;
			}
			{
			switch ( LA(1)) {
			case LITERAL_class:
			{
				classDefinition(mods_AST);
				if (inputState.guessing==0) {
					cd_AST = (AST)returnAST;
				}
				if ( inputState.guessing==0 ) {
					field_AST = (AST)currentAST.root;
					field_AST = cd_AST;
					currentAST.root = field_AST;
					currentAST.child = field_AST!=null &&field_AST.getFirstChild()!=null ?
						field_AST.getFirstChild() : field_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case LITERAL_interface:
			{
				interfaceDefinition(mods_AST);
				if (inputState.guessing==0) {
					id_AST = (AST)returnAST;
				}
				if ( inputState.guessing==0 ) {
					field_AST = (AST)currentAST.root;
					field_AST = id_AST;
					currentAST.root = field_AST;
					currentAST.child = field_AST!=null &&field_AST.getFirstChild()!=null ?
						field_AST.getFirstChild() : field_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
				if ((LA(1)==IDENT) && (LA(2)==LPAREN)) {
					ctorHead();
					if (inputState.guessing==0) {
						h_AST = (AST)returnAST;
					}
					constructorBody();
					if (inputState.guessing==0) {
						s_AST = (AST)returnAST;
					}
					if ( inputState.guessing==0 ) {
						field_AST = (AST)currentAST.root;
						field_AST = (AST)astFactory.make( (new ASTArray(4)).add((AST)astFactory.create(CTOR_DEF,"CTOR_DEF")).add(mods_AST).add(h_AST).add(s_AST));
						currentAST.root = field_AST;
						currentAST.child = field_AST!=null &&field_AST.getFirstChild()!=null ?
							field_AST.getFirstChild() : field_AST;
						currentAST.advanceChildToEnd();
					}
				}
				else if ((_tokenSet_10.member(LA(1))) && (_tokenSet_11.member(LA(2)))) {
					{
					switch ( LA(1)) {
					case LT:
					{
						typeParameters();
						break;
					}
					case IDENT:
					case LITERAL_void:
					case LITERAL_boolean:
					case LITERAL_byte:
					case LITERAL_char:
					case LITERAL_short:
					case LITERAL_int:
					case LITERAL_float:
					case LITERAL_long:
					case LITERAL_double:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					type=typeSpec(false);
					if (inputState.guessing==0) {
						t_AST = (AST)returnAST;
					}
					{
					if ((LA(1)==IDENT) && (LA(2)==LPAREN)) {
						method = LT(1);
						if (inputState.guessing==0) {
							method_AST = (AST)astFactory.create(method);
						}
						match(IDENT);
						if ( inputState.guessing==0 ) {
							defineMethod((JavaToken)method, type);
						}
						AST tmp42_AST = null;
						tmp42_AST = (AST)astFactory.create(LT(1));
						match(LPAREN);
						parameterDeclarationList();
						if (inputState.guessing==0) {
							param_AST = (AST)returnAST;
						}
						AST tmp43_AST = null;
						tmp43_AST = (AST)astFactory.create(LT(1));
						match(RPAREN);
						declaratorBrackets(t_AST);
						if (inputState.guessing==0) {
							rt_AST = (AST)returnAST;
						}
						{
						switch ( LA(1)) {
						case LITERAL_throws:
						{
							exceptions=throwsClause();
							if (inputState.guessing==0) {
								tc_AST = (AST)returnAST;
							}
							break;
						}
						case SEMI:
						case LCURLY:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						if ( inputState.guessing==0 ) {
							endMethodHead(exceptions);
						}
						{
						switch ( LA(1)) {
						case LCURLY:
						{
							compoundStatement(BODY);
							if (inputState.guessing==0) {
								s2_AST = (AST)returnAST;
							}
							break;
						}
						case SEMI:
						{
							AST tmp44_AST = null;
							if (inputState.guessing==0) {
								tmp44_AST = (AST)astFactory.create(LT(1));
							}
							match(SEMI);
							if ( inputState.guessing==0 ) {
								popScope();
							}
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						if ( inputState.guessing==0 ) {
							field_AST = (AST)currentAST.root;
							field_AST = (AST)astFactory.make( (new ASTArray(7)).add((AST)astFactory.create(METHOD_DEF,"METHOD_DEF")).add(mods_AST).add((AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(TYPE,"TYPE")).add(rt_AST))).add(method_AST).add(param_AST).add(tc_AST).add(s2_AST));
							currentAST.root = field_AST;
							currentAST.child = field_AST!=null &&field_AST.getFirstChild()!=null ?
								field_AST.getFirstChild() : field_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else if ((LA(1)==IDENT) && (_tokenSet_12.member(LA(2)))) {
						variableDefinitions(mods_AST,t_AST,type);
						if (inputState.guessing==0) {
							v_AST = (AST)returnAST;
						}
						AST tmp45_AST = null;
						if (inputState.guessing==0) {
							tmp45_AST = (AST)astFactory.create(LT(1));
						}
						match(SEMI);
						if ( inputState.guessing==0 ) {
							field_AST = (AST)currentAST.root;
							field_AST = v_AST;
							currentAST.root = field_AST;
							currentAST.child = field_AST!=null &&field_AST.getFirstChild()!=null ?
								field_AST.getFirstChild() : field_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					
					}
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		else if ((LA(1)==LITERAL_static) && (LA(2)==LCURLY)) {
			AST tmp46_AST = null;
			if (inputState.guessing==0) {
				tmp46_AST = (AST)astFactory.create(LT(1));
			}
			match(LITERAL_static);
			compoundStatement(CLASS_INIT);
			if (inputState.guessing==0) {
				s3_AST = (AST)returnAST;
			}
			if ( inputState.guessing==0 ) {
				field_AST = (AST)currentAST.root;
				field_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(STATIC_INIT,"STATIC_INIT")).add(s3_AST));
				currentAST.root = field_AST;
				currentAST.child = field_AST!=null &&field_AST.getFirstChild()!=null ?
					field_AST.getFirstChild() : field_AST;
				currentAST.advanceChildToEnd();
			}
		}
		else if ((LA(1)==LCURLY)) {
			compoundStatement(INSTANCE_INIT);
			if (inputState.guessing==0) {
				s4_AST = (AST)returnAST;
			}
			if ( inputState.guessing==0 ) {
				field_AST = (AST)currentAST.root;
				field_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(INSTANCE_INIT,"INSTANCE_INIT")).add(s4_AST));
				currentAST.root = field_AST;
				currentAST.child = field_AST!=null &&field_AST.getFirstChild()!=null ?
					field_AST.getFirstChild() : field_AST;
				currentAST.advanceChildToEnd();
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = field_AST;
	}
	
	public final void ctorHead() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ctorHead_AST = null;
		Token  method = null;
		AST method_AST = null;
		JavaToken id; JavaVector exceptions = null;
		
		method = LT(1);
		if (inputState.guessing==0) {
			method_AST = (AST)astFactory.create(method);
			astFactory.addASTChild(currentAST, method_AST);
		}
		match(IDENT);
		if ( inputState.guessing==0 ) {
			defineMethod((JavaToken)method, null);
		}
		AST tmp47_AST = null;
		tmp47_AST = (AST)astFactory.create(LT(1));
		match(LPAREN);
		parameterDeclarationList();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		AST tmp48_AST = null;
		tmp48_AST = (AST)astFactory.create(LT(1));
		match(RPAREN);
		{
		switch ( LA(1)) {
		case LITERAL_throws:
		{
			exceptions=throwsClause();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			break;
		}
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			endMethodHead(exceptions);
		}
		ctorHead_AST = (AST)currentAST.root;
		returnAST = ctorHead_AST;
	}
	
	public final void constructorBody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constructorBody_AST = null;
		Token  lc = null;
		AST lc_AST = null;
		
		lc = LT(1);
		if (inputState.guessing==0) {
			lc_AST = (AST)astFactory.create(lc);
			astFactory.makeASTRoot(currentAST, lc_AST);
		}
		match(LCURLY);
		if ( inputState.guessing==0 ) {
			lc_AST.setType(SLIST);
		}
		{
		if ((LA(1)==LITERAL_this||LA(1)==LITERAL_super) && (LA(2)==LPAREN)) {
			explicitConstructorInvocation();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
		}
		else if ((_tokenSet_13.member(LA(1))) && (_tokenSet_14.member(LA(2)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		{
		_loop75:
		do {
			if ((_tokenSet_15.member(LA(1)))) {
				statement();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop75;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			popScope();
		}
		AST tmp49_AST = null;
		tmp49_AST = (AST)astFactory.create(LT(1));
		match(RCURLY);
		constructorBody_AST = (AST)currentAST.root;
		returnAST = constructorBody_AST;
	}
	
	public final void parameterDeclarationList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parameterDeclarationList_AST = null;
		
		{
		switch ( LA(1)) {
		case FINAL:
		case IDENT:
		case LITERAL_void:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_short:
		case LITERAL_int:
		case LITERAL_float:
		case LITERAL_long:
		case LITERAL_double:
		{
			parameterDeclaration();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			{
			_loop100:
			do {
				if ((LA(1)==COMMA)) {
					AST tmp50_AST = null;
					tmp50_AST = (AST)astFactory.create(LT(1));
					match(COMMA);
					parameterDeclaration();
					if (inputState.guessing==0) {
						astFactory.addASTChild(currentAST, returnAST);
					}
				}
				else {
					break _loop100;
				}
				
			} while (true);
			}
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			parameterDeclarationList_AST = (AST)currentAST.root;
			parameterDeclarationList_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(PARAMETERS,"PARAMETERS")).add(parameterDeclarationList_AST));
			currentAST.root = parameterDeclarationList_AST;
			currentAST.child = parameterDeclarationList_AST!=null &&parameterDeclarationList_AST.getFirstChild()!=null ?
				parameterDeclarationList_AST.getFirstChild() : parameterDeclarationList_AST;
			currentAST.advanceChildToEnd();
		}
		parameterDeclarationList_AST = (AST)currentAST.root;
		returnAST = parameterDeclarationList_AST;
	}
	
	public final void declaratorBrackets(
		AST typ
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaratorBrackets_AST = null;
		Token  lb = null;
		AST lb_AST = null;
		
		if ( inputState.guessing==0 ) {
			declaratorBrackets_AST = (AST)currentAST.root;
			declaratorBrackets_AST=typ;
			currentAST.root = declaratorBrackets_AST;
			currentAST.child = declaratorBrackets_AST!=null &&declaratorBrackets_AST.getFirstChild()!=null ?
				declaratorBrackets_AST.getFirstChild() : declaratorBrackets_AST;
			currentAST.advanceChildToEnd();
		}
		{
		_loop83:
		do {
			if ((LA(1)==LBRACK)) {
				lb = LT(1);
				if (inputState.guessing==0) {
					lb_AST = (AST)astFactory.create(lb);
					astFactory.makeASTRoot(currentAST, lb_AST);
				}
				match(LBRACK);
				if ( inputState.guessing==0 ) {
					lb_AST.setType(ARRAY_DECLARATOR);
				}
				AST tmp51_AST = null;
				tmp51_AST = (AST)astFactory.create(LT(1));
				match(RBRACK);
			}
			else {
				break _loop83;
			}
			
		} while (true);
		}
		declaratorBrackets_AST = (AST)currentAST.root;
		returnAST = declaratorBrackets_AST;
	}
	
	public final JavaVector  throwsClause() throws RecognitionException, TokenStreamException {
		JavaVector exceptions;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST throwsClause_AST = null;
		JavaToken id; exceptions = new JavaVector();
		
		AST tmp52_AST = null;
		if (inputState.guessing==0) {
			tmp52_AST = (AST)astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp52_AST);
		}
		match(LITERAL_throws);
		id=identifier();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		if ( inputState.guessing==0 ) {
			exceptions.addElement(dummyClass(id));
		}
		{
		_loop96:
		do {
			if ((LA(1)==COMMA)) {
				AST tmp53_AST = null;
				tmp53_AST = (AST)astFactory.create(LT(1));
				match(COMMA);
				id=identifier();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
				if ( inputState.guessing==0 ) {
					exceptions.addElement(dummyClass(id));
				}
			}
			else {
				break _loop96;
			}
			
		} while (true);
		}
		throwsClause_AST = (AST)currentAST.root;
		returnAST = throwsClause_AST;
		return exceptions;
	}
	
	public final void compoundStatement(
		int scopeType
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST compoundStatement_AST = null;
		Token  lc = null;
		AST lc_AST = null;
		
		lc = LT(1);
		if (inputState.guessing==0) {
			lc_AST = (AST)astFactory.create(lc);
			astFactory.makeASTRoot(currentAST, lc_AST);
		}
		match(LCURLY);
		if ( inputState.guessing==0 ) {
			lc_AST.setType(SLIST);
		}
		if ( inputState.guessing==0 ) {
			// based on the scopeType we are processing
			switch(scopeType) {
			// if it's a new block, tell the symbol table
			case NEW_SCOPE:
			defineBlock((JavaToken)lc);
			break;
			
			// if it's a class initializer or instance initializer,
			//   treat it like a method with a special name
			case CLASS_INIT:
			lc.setText("~class-init~");
			defineMethod(null, (JavaToken)lc);
			endMethodHead(null);
			break;
			case INSTANCE_INIT:
			lc.setText("~instance-init~");
			defineMethod(null, (JavaToken)lc);
			endMethodHead(null);
			break;
			
			// otherwise, it's a body, so do nothing special
			}
			
		}
		{
		_loop106:
		do {
			if ((_tokenSet_15.member(LA(1)))) {
				statement();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop106;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			popScope();
		}
		AST tmp54_AST = null;
		tmp54_AST = (AST)astFactory.create(LT(1));
		match(RCURLY);
		compoundStatement_AST = (AST)currentAST.root;
		returnAST = compoundStatement_AST;
	}
	
/** Catch obvious constructor calls, but not the expr.super(...) calls */
	public final void explicitConstructorInvocation() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST explicitConstructorInvocation_AST = null;
		Token  lp1 = null;
		AST lp1_AST = null;
		Token  lp2 = null;
		AST lp2_AST = null;
		
		switch ( LA(1)) {
		case LITERAL_this:
		{
			AST tmp55_AST = null;
			tmp55_AST = (AST)astFactory.create(LT(1));
			match(LITERAL_this);
			lp1 = LT(1);
			if (inputState.guessing==0) {
				lp1_AST = (AST)astFactory.create(lp1);
				astFactory.makeASTRoot(currentAST, lp1_AST);
			}
			match(LPAREN);
			argList();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			AST tmp56_AST = null;
			tmp56_AST = (AST)astFactory.create(LT(1));
			match(RPAREN);
			AST tmp57_AST = null;
			tmp57_AST = (AST)astFactory.create(LT(1));
			match(SEMI);
			if ( inputState.guessing==0 ) {
				lp1_AST.setType(CTOR_CALL);
			}
			explicitConstructorInvocation_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_super:
		{
			AST tmp58_AST = null;
			tmp58_AST = (AST)astFactory.create(LT(1));
			match(LITERAL_super);
			lp2 = LT(1);
			if (inputState.guessing==0) {
				lp2_AST = (AST)astFactory.create(lp2);
				astFactory.makeASTRoot(currentAST, lp2_AST);
			}
			match(LPAREN);
			argList();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			AST tmp59_AST = null;
			tmp59_AST = (AST)astFactory.create(LT(1));
			match(RPAREN);
			AST tmp60_AST = null;
			tmp60_AST = (AST)astFactory.create(LT(1));
			match(SEMI);
			if ( inputState.guessing==0 ) {
				lp2_AST.setType(SUPER_CTOR_CALL);
			}
			explicitConstructorInvocation_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = explicitConstructorInvocation_AST;
	}
	
	public final void statement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statement_AST = null;
		AST m_AST = null;
		Token  id = null;
		AST id_AST = null;
		Token  c = null;
		AST c_AST = null;
		Token  bid = null;
		AST bid_AST = null;
		Token  cid = null;
		AST cid_AST = null;
		Token  s = null;
		AST s_AST = null;
		int count = -1;
		
		switch ( LA(1)) {
		case LCURLY:
		{
			compoundStatement(NEW_SCOPE);
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			statement_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_if:
		{
			AST tmp61_AST = null;
			if (inputState.guessing==0) {
				tmp61_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp61_AST);
			}
			match(LITERAL_if);
			AST tmp62_AST = null;
			tmp62_AST = (AST)astFactory.create(LT(1));
			match(LPAREN);
			expression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			AST tmp63_AST = null;
			tmp63_AST = (AST)astFactory.create(LT(1));
			match(RPAREN);
			statement();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			{
			if ((LA(1)==LITERAL_else) && (_tokenSet_15.member(LA(2)))) {
				AST tmp64_AST = null;
				tmp64_AST = (AST)astFactory.create(LT(1));
				match(LITERAL_else);
				statement();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else if ((_tokenSet_16.member(LA(1))) && (_tokenSet_17.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			statement_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_for:
		{
			AST tmp65_AST = null;
			if (inputState.guessing==0) {
				tmp65_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp65_AST);
			}
			match(LITERAL_for);
			AST tmp66_AST = null;
			tmp66_AST = (AST)astFactory.create(LT(1));
			match(LPAREN);
			forInit();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			AST tmp67_AST = null;
			tmp67_AST = (AST)astFactory.create(LT(1));
			match(SEMI);
			forCond();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			AST tmp68_AST = null;
			tmp68_AST = (AST)astFactory.create(LT(1));
			match(SEMI);
			forIter();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			AST tmp69_AST = null;
			tmp69_AST = (AST)astFactory.create(LT(1));
			match(RPAREN);
			statement();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			statement_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_while:
		{
			AST tmp70_AST = null;
			if (inputState.guessing==0) {
				tmp70_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp70_AST);
			}
			match(LITERAL_while);
			AST tmp71_AST = null;
			tmp71_AST = (AST)astFactory.create(LT(1));
			match(LPAREN);
			expression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			AST tmp72_AST = null;
			tmp72_AST = (AST)astFactory.create(LT(1));
			match(RPAREN);
			statement();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			statement_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_do:
		{
			AST tmp73_AST = null;
			if (inputState.guessing==0) {
				tmp73_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp73_AST);
			}
			match(LITERAL_do);
			statement();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			AST tmp74_AST = null;
			tmp74_AST = (AST)astFactory.create(LT(1));
			match(LITERAL_while);
			AST tmp75_AST = null;
			tmp75_AST = (AST)astFactory.create(LT(1));
			match(LPAREN);
			expression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			AST tmp76_AST = null;
			tmp76_AST = (AST)astFactory.create(LT(1));
			match(RPAREN);
			AST tmp77_AST = null;
			tmp77_AST = (AST)astFactory.create(LT(1));
			match(SEMI);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_break:
		{
			AST tmp78_AST = null;
			if (inputState.guessing==0) {
				tmp78_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp78_AST);
			}
			match(LITERAL_break);
			{
			switch ( LA(1)) {
			case IDENT:
			{
				bid = LT(1);
				if (inputState.guessing==0) {
					bid_AST = (AST)astFactory.create(bid);
					astFactory.addASTChild(currentAST, bid_AST);
				}
				match(IDENT);
				if ( inputState.guessing==0 ) {
					reference((JavaToken)bid);
				}
				break;
			}
			case SEMI:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			AST tmp79_AST = null;
			tmp79_AST = (AST)astFactory.create(LT(1));
			match(SEMI);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_continue:
		{
			AST tmp80_AST = null;
			if (inputState.guessing==0) {
				tmp80_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp80_AST);
			}
			match(LITERAL_continue);
			{
			switch ( LA(1)) {
			case IDENT:
			{
				cid = LT(1);
				if (inputState.guessing==0) {
					cid_AST = (AST)astFactory.create(cid);
					astFactory.addASTChild(currentAST, cid_AST);
				}
				match(IDENT);
				if ( inputState.guessing==0 ) {
					reference((JavaToken)cid);
				}
				break;
			}
			case SEMI:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			AST tmp81_AST = null;
			tmp81_AST = (AST)astFactory.create(LT(1));
			match(SEMI);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_return:
		{
			AST tmp82_AST = null;
			if (inputState.guessing==0) {
				tmp82_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp82_AST);
			}
			match(LITERAL_return);
			{
			switch ( LA(1)) {
			case IDENT:
			case LITERAL_void:
			case LITERAL_boolean:
			case LITERAL_byte:
			case LITERAL_char:
			case LITERAL_short:
			case LITERAL_int:
			case LITERAL_float:
			case LITERAL_long:
			case LITERAL_double:
			case LPAREN:
			case LITERAL_this:
			case LITERAL_super:
			case PLUS:
			case MINUS:
			case INC:
			case DEC:
			case BNOT:
			case LNOT:
			case LITERAL_true:
			case LITERAL_false:
			case LITERAL_null:
			case LITERAL_new:
			case NUM_INT:
			case CHAR_LITERAL:
			case STRING_LITERAL:
			case NUM_FLOAT:
			case NUM_LONG:
			case NUM_DOUBLE:
			{
				expression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
				break;
			}
			case SEMI:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			AST tmp83_AST = null;
			tmp83_AST = (AST)astFactory.create(LT(1));
			match(SEMI);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_switch:
		{
			AST tmp84_AST = null;
			if (inputState.guessing==0) {
				tmp84_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp84_AST);
			}
			match(LITERAL_switch);
			AST tmp85_AST = null;
			tmp85_AST = (AST)astFactory.create(LT(1));
			match(LPAREN);
			expression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			AST tmp86_AST = null;
			tmp86_AST = (AST)astFactory.create(LT(1));
			match(RPAREN);
			AST tmp87_AST = null;
			tmp87_AST = (AST)astFactory.create(LT(1));
			match(LCURLY);
			{
			_loop115:
			do {
				if ((LA(1)==LITERAL_case||LA(1)==LITERAL_default)) {
					casesGroup();
					if (inputState.guessing==0) {
						astFactory.addASTChild(currentAST, returnAST);
					}
				}
				else {
					break _loop115;
				}
				
			} while (true);
			}
			AST tmp88_AST = null;
			tmp88_AST = (AST)astFactory.create(LT(1));
			match(RCURLY);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_try:
		{
			tryBlock();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			statement_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_throw:
		{
			AST tmp89_AST = null;
			if (inputState.guessing==0) {
				tmp89_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp89_AST);
			}
			match(LITERAL_throw);
			expression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			AST tmp90_AST = null;
			tmp90_AST = (AST)astFactory.create(LT(1));
			match(SEMI);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_assert:
		{
			AST tmp91_AST = null;
			if (inputState.guessing==0) {
				tmp91_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp91_AST);
			}
			match(LITERAL_assert);
			expression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			{
			switch ( LA(1)) {
			case COLON:
			{
				AST tmp92_AST = null;
				tmp92_AST = (AST)astFactory.create(LT(1));
				match(COLON);
				expression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
				break;
			}
			case SEMI:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			AST tmp93_AST = null;
			tmp93_AST = (AST)astFactory.create(LT(1));
			match(SEMI);
			statement_AST = (AST)currentAST.root;
			break;
		}
		case SEMI:
		{
			s = LT(1);
			if (inputState.guessing==0) {
				s_AST = (AST)astFactory.create(s);
				astFactory.addASTChild(currentAST, s_AST);
			}
			match(SEMI);
			if ( inputState.guessing==0 ) {
				s_AST.setType(EMPTY_STAT);
			}
			statement_AST = (AST)currentAST.root;
			break;
		}
		default:
			boolean synPredMatched109 = false;
			if (((_tokenSet_18.member(LA(1))) && (_tokenSet_19.member(LA(2))))) {
				int _m109 = mark();
				synPredMatched109 = true;
				inputState.guessing++;
				try {
					{
					declaration();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched109 = false;
				}
				rewind(_m109);
				inputState.guessing--;
			}
			if ( synPredMatched109 ) {
				declaration();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
				AST tmp94_AST = null;
				tmp94_AST = (AST)astFactory.create(LT(1));
				match(SEMI);
				statement_AST = (AST)currentAST.root;
			}
			else if ((_tokenSet_20.member(LA(1))) && (_tokenSet_21.member(LA(2)))) {
				expression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
				AST tmp95_AST = null;
				tmp95_AST = (AST)astFactory.create(LT(1));
				match(SEMI);
				statement_AST = (AST)currentAST.root;
			}
			else if ((_tokenSet_22.member(LA(1))) && (_tokenSet_23.member(LA(2)))) {
				modifiers();
				if (inputState.guessing==0) {
					m_AST = (AST)returnAST;
				}
				classDefinition(m_AST);
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
				statement_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==IDENT) && (LA(2)==COLON)) {
				id = LT(1);
				if (inputState.guessing==0) {
					id_AST = (AST)astFactory.create(id);
					astFactory.addASTChild(currentAST, id_AST);
				}
				match(IDENT);
				c = LT(1);
				if (inputState.guessing==0) {
					c_AST = (AST)astFactory.create(c);
					astFactory.makeASTRoot(currentAST, c_AST);
				}
				match(COLON);
				if ( inputState.guessing==0 ) {
					c_AST.setType(LABELED_STAT);
				}
				statement();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
				if ( inputState.guessing==0 ) {
					defineLabel((JavaToken)id);
				}
				statement_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==LITERAL_synchronized) && (LA(2)==LPAREN)) {
				AST tmp96_AST = null;
				if (inputState.guessing==0) {
					tmp96_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp96_AST);
				}
				match(LITERAL_synchronized);
				AST tmp97_AST = null;
				tmp97_AST = (AST)astFactory.create(LT(1));
				match(LPAREN);
				expression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
				AST tmp98_AST = null;
				tmp98_AST = (AST)astFactory.create(LT(1));
				match(RPAREN);
				compoundStatement(NEW_SCOPE);
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
				statement_AST = (AST)currentAST.root;
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = statement_AST;
	}
	
	public final int  argList() throws RecognitionException, TokenStreamException {
		int count;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST argList_AST = null;
		count=0;
		
		{
		switch ( LA(1)) {
		case IDENT:
		case LITERAL_void:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_short:
		case LITERAL_int:
		case LITERAL_float:
		case LITERAL_long:
		case LITERAL_double:
		case LPAREN:
		case LITERAL_this:
		case LITERAL_super:
		case PLUS:
		case MINUS:
		case INC:
		case DEC:
		case BNOT:
		case LNOT:
		case LITERAL_true:
		case LITERAL_false:
		case LITERAL_null:
		case LITERAL_new:
		case NUM_INT:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		{
			count=expressionList();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			break;
		}
		case RPAREN:
		{
			if ( inputState.guessing==0 ) {
				argList_AST = (AST)currentAST.root;
				argList_AST = (AST)astFactory.create(ELIST,"ELIST");
				currentAST.root = argList_AST;
				currentAST.child = argList_AST!=null &&argList_AST.getFirstChild()!=null ?
					argList_AST.getFirstChild() : argList_AST;
				currentAST.advanceChildToEnd();
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		argList_AST = (AST)currentAST.root;
		returnAST = argList_AST;
		return count;
	}
	
/** Declaration of a variable.  This can be a class/instance variable,
 *   or a local variable in a method
 * It can also include possible initialization.
 */
	public final void variableDeclarator(
		AST mods, AST t, JavaToken type
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variableDeclarator_AST = null;
		Token  id = null;
		AST id_AST = null;
		AST d_AST = null;
		AST v_AST = null;
		
		id = LT(1);
		if (inputState.guessing==0) {
			id_AST = (AST)astFactory.create(id);
		}
		match(IDENT);
		declaratorBrackets(t);
		if (inputState.guessing==0) {
			d_AST = (AST)returnAST;
		}
		varInitializer();
		if (inputState.guessing==0) {
			v_AST = (AST)returnAST;
		}
		if ( inputState.guessing==0 ) {
			defineVar((JavaToken)id, type);
		}
		if ( inputState.guessing==0 ) {
			variableDeclarator_AST = (AST)currentAST.root;
			variableDeclarator_AST = (AST)astFactory.make( (new ASTArray(5)).add((AST)astFactory.create(VARIABLE_DEF,"VARIABLE_DEF")).add(mods).add((AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(TYPE,"TYPE")).add(d_AST))).add(id_AST).add(v_AST));
			currentAST.root = variableDeclarator_AST;
			currentAST.child = variableDeclarator_AST!=null &&variableDeclarator_AST.getFirstChild()!=null ?
				variableDeclarator_AST.getFirstChild() : variableDeclarator_AST;
			currentAST.advanceChildToEnd();
		}
		returnAST = variableDeclarator_AST;
	}
	
	public final void varInitializer() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST varInitializer_AST = null;
		
		{
		switch ( LA(1)) {
		case ASSIGN:
		{
			AST tmp99_AST = null;
			if (inputState.guessing==0) {
				tmp99_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp99_AST);
			}
			match(ASSIGN);
			initializer();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			break;
		}
		case SEMI:
		case COMMA:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		varInitializer_AST = (AST)currentAST.root;
		returnAST = varInitializer_AST;
	}
	
	public final void initializer() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST initializer_AST = null;
		
		switch ( LA(1)) {
		case IDENT:
		case LITERAL_void:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_short:
		case LITERAL_int:
		case LITERAL_float:
		case LITERAL_long:
		case LITERAL_double:
		case LPAREN:
		case LITERAL_this:
		case LITERAL_super:
		case PLUS:
		case MINUS:
		case INC:
		case DEC:
		case BNOT:
		case LNOT:
		case LITERAL_true:
		case LITERAL_false:
		case LITERAL_null:
		case LITERAL_new:
		case NUM_INT:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		{
			expression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			initializer_AST = (AST)currentAST.root;
			break;
		}
		case LCURLY:
		{
			arrayInitializer();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			initializer_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = initializer_AST;
	}
	
	public final void arrayInitializer() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayInitializer_AST = null;
		Token  lc = null;
		AST lc_AST = null;
		
		lc = LT(1);
		if (inputState.guessing==0) {
			lc_AST = (AST)astFactory.create(lc);
			astFactory.makeASTRoot(currentAST, lc_AST);
		}
		match(LCURLY);
		if ( inputState.guessing==0 ) {
			lc_AST.setType(ARRAY_INIT);
		}
		{
		switch ( LA(1)) {
		case IDENT:
		case LITERAL_void:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_short:
		case LITERAL_int:
		case LITERAL_float:
		case LITERAL_long:
		case LITERAL_double:
		case LCURLY:
		case LPAREN:
		case LITERAL_this:
		case LITERAL_super:
		case PLUS:
		case MINUS:
		case INC:
		case DEC:
		case BNOT:
		case LNOT:
		case LITERAL_true:
		case LITERAL_false:
		case LITERAL_null:
		case LITERAL_new:
		case NUM_INT:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		{
			initializer();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			{
			_loop89:
			do {
				if ((LA(1)==COMMA) && (_tokenSet_24.member(LA(2)))) {
					AST tmp100_AST = null;
					tmp100_AST = (AST)astFactory.create(LT(1));
					match(COMMA);
					initializer();
					if (inputState.guessing==0) {
						astFactory.addASTChild(currentAST, returnAST);
					}
				}
				else {
					break _loop89;
				}
				
			} while (true);
			}
			{
			switch ( LA(1)) {
			case COMMA:
			{
				AST tmp101_AST = null;
				tmp101_AST = (AST)astFactory.create(LT(1));
				match(COMMA);
				break;
			}
			case RCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case RCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		AST tmp102_AST = null;
		tmp102_AST = (AST)astFactory.create(LT(1));
		match(RCURLY);
		arrayInitializer_AST = (AST)currentAST.root;
		returnAST = arrayInitializer_AST;
	}
	
	public final void expression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_AST = null;
		
		assignmentExpression();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		if ( inputState.guessing==0 ) {
			expression_AST = (AST)currentAST.root;
			expression_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(EXPR,"EXPR")).add(expression_AST));
			currentAST.root = expression_AST;
			currentAST.child = expression_AST!=null &&expression_AST.getFirstChild()!=null ?
				expression_AST.getFirstChild() : expression_AST;
			currentAST.advanceChildToEnd();
		}
		expression_AST = (AST)currentAST.root;
		returnAST = expression_AST;
	}
	
	public final void parameterDeclaration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parameterDeclaration_AST = null;
		AST pm_AST = null;
		AST t_AST = null;
		Token  id = null;
		AST id_AST = null;
		AST pd_AST = null;
		JavaToken type;
		
		parameterModifier();
		if (inputState.guessing==0) {
			pm_AST = (AST)returnAST;
		}
		type=typeSpec(false);
		if (inputState.guessing==0) {
			t_AST = (AST)returnAST;
		}
		id = LT(1);
		if (inputState.guessing==0) {
			id_AST = (AST)astFactory.create(id);
		}
		match(IDENT);
		declaratorBrackets(t_AST);
		if (inputState.guessing==0) {
			pd_AST = (AST)returnAST;
		}
		if ( inputState.guessing==0 ) {
			parameterDeclaration_AST = (AST)currentAST.root;
			parameterDeclaration_AST = (AST)astFactory.make( (new ASTArray(4)).add((AST)astFactory.create(PARAMETER_DEF,"PARAMETER_DEF")).add(pm_AST).add((AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(TYPE,"TYPE")).add(pd_AST))).add(id_AST));
			currentAST.root = parameterDeclaration_AST;
			currentAST.child = parameterDeclaration_AST!=null &&parameterDeclaration_AST.getFirstChild()!=null ?
				parameterDeclaration_AST.getFirstChild() : parameterDeclaration_AST;
			currentAST.advanceChildToEnd();
		}
		if ( inputState.guessing==0 ) {
			defineVar((JavaToken)id, type);
		}
		returnAST = parameterDeclaration_AST;
	}
	
	public final void parameterModifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parameterModifier_AST = null;
		Token  f = null;
		AST f_AST = null;
		
		{
		switch ( LA(1)) {
		case FINAL:
		{
			f = LT(1);
			if (inputState.guessing==0) {
				f_AST = (AST)astFactory.create(f);
				astFactory.addASTChild(currentAST, f_AST);
			}
			match(FINAL);
			break;
		}
		case IDENT:
		case LITERAL_void:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_short:
		case LITERAL_int:
		case LITERAL_float:
		case LITERAL_long:
		case LITERAL_double:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			parameterModifier_AST = (AST)currentAST.root;
			parameterModifier_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(MODIFIERS,"MODIFIERS")).add(f_AST));
			currentAST.root = parameterModifier_AST;
			currentAST.child = parameterModifier_AST!=null &&parameterModifier_AST.getFirstChild()!=null ?
				parameterModifier_AST.getFirstChild() : parameterModifier_AST;
			currentAST.advanceChildToEnd();
		}
		parameterModifier_AST = (AST)currentAST.root;
		returnAST = parameterModifier_AST;
	}
	
	public final void forInit() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forInit_AST = null;
		int count = -1;
		
		{
		boolean synPredMatched128 = false;
		if (((_tokenSet_18.member(LA(1))) && (_tokenSet_19.member(LA(2))))) {
			int _m128 = mark();
			synPredMatched128 = true;
			inputState.guessing++;
			try {
				{
				declaration();
				}
			}
			catch (RecognitionException pe) {
				synPredMatched128 = false;
			}
			rewind(_m128);
			inputState.guessing--;
		}
		if ( synPredMatched128 ) {
			declaration();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
		}
		else if ((_tokenSet_20.member(LA(1))) && (_tokenSet_25.member(LA(2)))) {
			count=expressionList();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
		}
		else if ((LA(1)==SEMI)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		if ( inputState.guessing==0 ) {
			forInit_AST = (AST)currentAST.root;
			forInit_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(FOR_INIT,"FOR_INIT")).add(forInit_AST));
			currentAST.root = forInit_AST;
			currentAST.child = forInit_AST!=null &&forInit_AST.getFirstChild()!=null ?
				forInit_AST.getFirstChild() : forInit_AST;
			currentAST.advanceChildToEnd();
		}
		forInit_AST = (AST)currentAST.root;
		returnAST = forInit_AST;
	}
	
	public final void forCond() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forCond_AST = null;
		
		{
		switch ( LA(1)) {
		case IDENT:
		case LITERAL_void:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_short:
		case LITERAL_int:
		case LITERAL_float:
		case LITERAL_long:
		case LITERAL_double:
		case LPAREN:
		case LITERAL_this:
		case LITERAL_super:
		case PLUS:
		case MINUS:
		case INC:
		case DEC:
		case BNOT:
		case LNOT:
		case LITERAL_true:
		case LITERAL_false:
		case LITERAL_null:
		case LITERAL_new:
		case NUM_INT:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		{
			expression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			break;
		}
		case SEMI:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			forCond_AST = (AST)currentAST.root;
			forCond_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(FOR_CONDITION,"FOR_CONDITION")).add(forCond_AST));
			currentAST.root = forCond_AST;
			currentAST.child = forCond_AST!=null &&forCond_AST.getFirstChild()!=null ?
				forCond_AST.getFirstChild() : forCond_AST;
			currentAST.advanceChildToEnd();
		}
		forCond_AST = (AST)currentAST.root;
		returnAST = forCond_AST;
	}
	
	public final void forIter() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forIter_AST = null;
		
		{
		switch ( LA(1)) {
		case IDENT:
		case LITERAL_void:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_short:
		case LITERAL_int:
		case LITERAL_float:
		case LITERAL_long:
		case LITERAL_double:
		case LPAREN:
		case LITERAL_this:
		case LITERAL_super:
		case PLUS:
		case MINUS:
		case INC:
		case DEC:
		case BNOT:
		case LNOT:
		case LITERAL_true:
		case LITERAL_false:
		case LITERAL_null:
		case LITERAL_new:
		case NUM_INT:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		{
			expressionList();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			forIter_AST = (AST)currentAST.root;
			forIter_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(FOR_ITERATOR,"FOR_ITERATOR")).add(forIter_AST));
			currentAST.root = forIter_AST;
			currentAST.child = forIter_AST!=null &&forIter_AST.getFirstChild()!=null ?
				forIter_AST.getFirstChild() : forIter_AST;
			currentAST.advanceChildToEnd();
		}
		forIter_AST = (AST)currentAST.root;
		returnAST = forIter_AST;
	}
	
	public final void casesGroup() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST casesGroup_AST = null;
		
		{
		int _cnt119=0;
		_loop119:
		do {
			if ((LA(1)==LITERAL_case||LA(1)==LITERAL_default) && (_tokenSet_26.member(LA(2)))) {
				aCase();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				if ( _cnt119>=1 ) { break _loop119; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt119++;
		} while (true);
		}
		caseSList();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		if ( inputState.guessing==0 ) {
			casesGroup_AST = (AST)currentAST.root;
			casesGroup_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(CASE_GROUP,"CASE_GROUP")).add(casesGroup_AST));
			currentAST.root = casesGroup_AST;
			currentAST.child = casesGroup_AST!=null &&casesGroup_AST.getFirstChild()!=null ?
				casesGroup_AST.getFirstChild() : casesGroup_AST;
			currentAST.advanceChildToEnd();
		}
		casesGroup_AST = (AST)currentAST.root;
		returnAST = casesGroup_AST;
	}
	
	public final void tryBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tryBlock_AST = null;
		
		AST tmp103_AST = null;
		if (inputState.guessing==0) {
			tmp103_AST = (AST)astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp103_AST);
		}
		match(LITERAL_try);
		compoundStatement(NEW_SCOPE);
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		_loop135:
		do {
			if ((LA(1)==LITERAL_catch)) {
				handler();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop135;
			}
			
		} while (true);
		}
		{
		switch ( LA(1)) {
		case LITERAL_finally:
		{
			finallyClause();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			break;
		}
		case FINAL:
		case ABSTRACT:
		case STRICTFP:
		case SEMI:
		case IDENT:
		case LITERAL_void:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_short:
		case LITERAL_int:
		case LITERAL_float:
		case LITERAL_long:
		case LITERAL_double:
		case LITERAL_private:
		case LITERAL_public:
		case LITERAL_protected:
		case LITERAL_static:
		case LITERAL_transient:
		case LITERAL_native:
		case LITERAL_threadsafe:
		case LITERAL_synchronized:
		case LITERAL_volatile:
		case LITERAL_class:
		case LCURLY:
		case RCURLY:
		case LPAREN:
		case LITERAL_this:
		case LITERAL_super:
		case LITERAL_if:
		case LITERAL_else:
		case LITERAL_for:
		case LITERAL_while:
		case LITERAL_do:
		case LITERAL_break:
		case LITERAL_continue:
		case LITERAL_return:
		case LITERAL_switch:
		case LITERAL_throw:
		case LITERAL_assert:
		case LITERAL_case:
		case LITERAL_default:
		case LITERAL_try:
		case PLUS:
		case MINUS:
		case INC:
		case DEC:
		case BNOT:
		case LNOT:
		case LITERAL_true:
		case LITERAL_false:
		case LITERAL_null:
		case LITERAL_new:
		case NUM_INT:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		tryBlock_AST = (AST)currentAST.root;
		returnAST = tryBlock_AST;
	}
	
	public final void aCase() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST aCase_AST = null;
		
		{
		switch ( LA(1)) {
		case LITERAL_case:
		{
			AST tmp104_AST = null;
			if (inputState.guessing==0) {
				tmp104_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp104_AST);
			}
			match(LITERAL_case);
			expression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			break;
		}
		case LITERAL_default:
		{
			AST tmp105_AST = null;
			if (inputState.guessing==0) {
				tmp105_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp105_AST);
			}
			match(LITERAL_default);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		AST tmp106_AST = null;
		tmp106_AST = (AST)astFactory.create(LT(1));
		match(COLON);
		aCase_AST = (AST)currentAST.root;
		returnAST = aCase_AST;
	}
	
	public final void caseSList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST caseSList_AST = null;
		
		{
		_loop124:
		do {
			if ((_tokenSet_15.member(LA(1)))) {
				statement();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop124;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			caseSList_AST = (AST)currentAST.root;
			caseSList_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(SLIST,"SLIST")).add(caseSList_AST));
			currentAST.root = caseSList_AST;
			currentAST.child = caseSList_AST!=null &&caseSList_AST.getFirstChild()!=null ?
				caseSList_AST.getFirstChild() : caseSList_AST;
			currentAST.advanceChildToEnd();
		}
		caseSList_AST = (AST)currentAST.root;
		returnAST = caseSList_AST;
	}
	
	public final int  expressionList() throws RecognitionException, TokenStreamException {
		int count;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expressionList_AST = null;
		count=1;
		
		expression();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		_loop142:
		do {
			if ((LA(1)==COMMA)) {
				AST tmp107_AST = null;
				tmp107_AST = (AST)astFactory.create(LT(1));
				match(COMMA);
				expression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
				if ( inputState.guessing==0 ) {
					count++;
				}
			}
			else {
				break _loop142;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			expressionList_AST = (AST)currentAST.root;
			expressionList_AST = (AST)astFactory.make( (new ASTArray(2)).add((AST)astFactory.create(ELIST,"ELIST")).add(expressionList_AST));
			currentAST.root = expressionList_AST;
			currentAST.child = expressionList_AST!=null &&expressionList_AST.getFirstChild()!=null ?
				expressionList_AST.getFirstChild() : expressionList_AST;
			currentAST.advanceChildToEnd();
		}
		expressionList_AST = (AST)currentAST.root;
		returnAST = expressionList_AST;
		return count;
	}
	
	public final void handler() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST handler_AST = null;
		
		AST tmp108_AST = null;
		if (inputState.guessing==0) {
			tmp108_AST = (AST)astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp108_AST);
		}
		match(LITERAL_catch);
		AST tmp109_AST = null;
		tmp109_AST = (AST)astFactory.create(LT(1));
		match(LPAREN);
		parameterDeclaration();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		AST tmp110_AST = null;
		tmp110_AST = (AST)astFactory.create(LT(1));
		match(RPAREN);
		compoundStatement(NEW_SCOPE);
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		handler_AST = (AST)currentAST.root;
		returnAST = handler_AST;
	}
	
	public final void finallyClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST finallyClause_AST = null;
		
		AST tmp111_AST = null;
		if (inputState.guessing==0) {
			tmp111_AST = (AST)astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp111_AST);
		}
		match(LITERAL_finally);
		compoundStatement(NEW_SCOPE);
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		finallyClause_AST = (AST)currentAST.root;
		returnAST = finallyClause_AST;
	}
	
	public final void assignmentExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignmentExpression_AST = null;
		
		conditionalExpression();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		switch ( LA(1)) {
		case ASSIGN:
		case PLUS_ASSIGN:
		case MINUS_ASSIGN:
		case STAR_ASSIGN:
		case DIV_ASSIGN:
		case MOD_ASSIGN:
		case SR_ASSIGN:
		case BSR_ASSIGN:
		case SL_ASSIGN:
		case BAND_ASSIGN:
		case BXOR_ASSIGN:
		case BOR_ASSIGN:
		{
			{
			switch ( LA(1)) {
			case ASSIGN:
			{
				AST tmp112_AST = null;
				if (inputState.guessing==0) {
					tmp112_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp112_AST);
				}
				match(ASSIGN);
				break;
			}
			case PLUS_ASSIGN:
			{
				AST tmp113_AST = null;
				if (inputState.guessing==0) {
					tmp113_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp113_AST);
				}
				match(PLUS_ASSIGN);
				break;
			}
			case MINUS_ASSIGN:
			{
				AST tmp114_AST = null;
				if (inputState.guessing==0) {
					tmp114_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp114_AST);
				}
				match(MINUS_ASSIGN);
				break;
			}
			case STAR_ASSIGN:
			{
				AST tmp115_AST = null;
				if (inputState.guessing==0) {
					tmp115_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp115_AST);
				}
				match(STAR_ASSIGN);
				break;
			}
			case DIV_ASSIGN:
			{
				AST tmp116_AST = null;
				if (inputState.guessing==0) {
					tmp116_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp116_AST);
				}
				match(DIV_ASSIGN);
				break;
			}
			case MOD_ASSIGN:
			{
				AST tmp117_AST = null;
				if (inputState.guessing==0) {
					tmp117_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp117_AST);
				}
				match(MOD_ASSIGN);
				break;
			}
			case SR_ASSIGN:
			{
				AST tmp118_AST = null;
				if (inputState.guessing==0) {
					tmp118_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp118_AST);
				}
				match(SR_ASSIGN);
				break;
			}
			case BSR_ASSIGN:
			{
				AST tmp119_AST = null;
				if (inputState.guessing==0) {
					tmp119_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp119_AST);
				}
				match(BSR_ASSIGN);
				break;
			}
			case SL_ASSIGN:
			{
				AST tmp120_AST = null;
				if (inputState.guessing==0) {
					tmp120_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp120_AST);
				}
				match(SL_ASSIGN);
				break;
			}
			case BAND_ASSIGN:
			{
				AST tmp121_AST = null;
				if (inputState.guessing==0) {
					tmp121_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp121_AST);
				}
				match(BAND_ASSIGN);
				break;
			}
			case BXOR_ASSIGN:
			{
				AST tmp122_AST = null;
				if (inputState.guessing==0) {
					tmp122_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp122_AST);
				}
				match(BXOR_ASSIGN);
				break;
			}
			case BOR_ASSIGN:
			{
				AST tmp123_AST = null;
				if (inputState.guessing==0) {
					tmp123_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp123_AST);
				}
				match(BOR_ASSIGN);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			assignmentExpression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			break;
		}
		case SEMI:
		case RBRACK:
		case COMMA:
		case RCURLY:
		case RPAREN:
		case COLON:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		assignmentExpression_AST = (AST)currentAST.root;
		returnAST = assignmentExpression_AST;
	}
	
	public final void conditionalExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conditionalExpression_AST = null;
		
		logicalOrExpression();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		switch ( LA(1)) {
		case QUESTION:
		{
			AST tmp124_AST = null;
			if (inputState.guessing==0) {
				tmp124_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp124_AST);
			}
			match(QUESTION);
			assignmentExpression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			AST tmp125_AST = null;
			tmp125_AST = (AST)astFactory.create(LT(1));
			match(COLON);
			conditionalExpression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			break;
		}
		case SEMI:
		case RBRACK:
		case COMMA:
		case RCURLY:
		case RPAREN:
		case ASSIGN:
		case COLON:
		case PLUS_ASSIGN:
		case MINUS_ASSIGN:
		case STAR_ASSIGN:
		case DIV_ASSIGN:
		case MOD_ASSIGN:
		case SR_ASSIGN:
		case BSR_ASSIGN:
		case SL_ASSIGN:
		case BAND_ASSIGN:
		case BXOR_ASSIGN:
		case BOR_ASSIGN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		conditionalExpression_AST = (AST)currentAST.root;
		returnAST = conditionalExpression_AST;
	}
	
	public final void logicalOrExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalOrExpression_AST = null;
		
		logicalAndExpression();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		_loop150:
		do {
			if ((LA(1)==LOR)) {
				AST tmp126_AST = null;
				if (inputState.guessing==0) {
					tmp126_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp126_AST);
				}
				match(LOR);
				logicalAndExpression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop150;
			}
			
		} while (true);
		}
		logicalOrExpression_AST = (AST)currentAST.root;
		returnAST = logicalOrExpression_AST;
	}
	
	public final void logicalAndExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalAndExpression_AST = null;
		
		inclusiveOrExpression();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		_loop153:
		do {
			if ((LA(1)==LAND)) {
				AST tmp127_AST = null;
				if (inputState.guessing==0) {
					tmp127_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp127_AST);
				}
				match(LAND);
				inclusiveOrExpression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop153;
			}
			
		} while (true);
		}
		logicalAndExpression_AST = (AST)currentAST.root;
		returnAST = logicalAndExpression_AST;
	}
	
	public final void inclusiveOrExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST inclusiveOrExpression_AST = null;
		
		exclusiveOrExpression();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		_loop156:
		do {
			if ((LA(1)==BOR)) {
				AST tmp128_AST = null;
				if (inputState.guessing==0) {
					tmp128_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp128_AST);
				}
				match(BOR);
				exclusiveOrExpression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop156;
			}
			
		} while (true);
		}
		inclusiveOrExpression_AST = (AST)currentAST.root;
		returnAST = inclusiveOrExpression_AST;
	}
	
	public final void exclusiveOrExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST exclusiveOrExpression_AST = null;
		
		andExpression();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		_loop159:
		do {
			if ((LA(1)==BXOR)) {
				AST tmp129_AST = null;
				if (inputState.guessing==0) {
					tmp129_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp129_AST);
				}
				match(BXOR);
				andExpression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop159;
			}
			
		} while (true);
		}
		exclusiveOrExpression_AST = (AST)currentAST.root;
		returnAST = exclusiveOrExpression_AST;
	}
	
	public final void andExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST andExpression_AST = null;
		
		equalityExpression();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		_loop162:
		do {
			if ((LA(1)==BAND)) {
				AST tmp130_AST = null;
				if (inputState.guessing==0) {
					tmp130_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp130_AST);
				}
				match(BAND);
				equalityExpression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop162;
			}
			
		} while (true);
		}
		andExpression_AST = (AST)currentAST.root;
		returnAST = andExpression_AST;
	}
	
	public final void equalityExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equalityExpression_AST = null;
		
		relationalExpression();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		_loop166:
		do {
			if ((LA(1)==NOT_EQUAL||LA(1)==EQUAL)) {
				{
				switch ( LA(1)) {
				case NOT_EQUAL:
				{
					AST tmp131_AST = null;
					if (inputState.guessing==0) {
						tmp131_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp131_AST);
					}
					match(NOT_EQUAL);
					break;
				}
				case EQUAL:
				{
					AST tmp132_AST = null;
					if (inputState.guessing==0) {
						tmp132_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp132_AST);
					}
					match(EQUAL);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				relationalExpression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop166;
			}
			
		} while (true);
		}
		equalityExpression_AST = (AST)currentAST.root;
		returnAST = equalityExpression_AST;
	}
	
	public final void relationalExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST relationalExpression_AST = null;
		
		shiftExpression();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		switch ( LA(1)) {
		case SEMI:
		case RBRACK:
		case LT:
		case COMMA:
		case GT:
		case BAND:
		case RCURLY:
		case RPAREN:
		case ASSIGN:
		case COLON:
		case PLUS_ASSIGN:
		case MINUS_ASSIGN:
		case STAR_ASSIGN:
		case DIV_ASSIGN:
		case MOD_ASSIGN:
		case SR_ASSIGN:
		case BSR_ASSIGN:
		case SL_ASSIGN:
		case BAND_ASSIGN:
		case BXOR_ASSIGN:
		case BOR_ASSIGN:
		case QUESTION:
		case LOR:
		case LAND:
		case BOR:
		case BXOR:
		case NOT_EQUAL:
		case EQUAL:
		case LE:
		case GE:
		{
			{
			_loop171:
			do {
				if ((_tokenSet_27.member(LA(1)))) {
					{
					switch ( LA(1)) {
					case LT:
					{
						AST tmp133_AST = null;
						if (inputState.guessing==0) {
							tmp133_AST = (AST)astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp133_AST);
						}
						match(LT);
						break;
					}
					case GT:
					{
						AST tmp134_AST = null;
						if (inputState.guessing==0) {
							tmp134_AST = (AST)astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp134_AST);
						}
						match(GT);
						break;
					}
					case LE:
					{
						AST tmp135_AST = null;
						if (inputState.guessing==0) {
							tmp135_AST = (AST)astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp135_AST);
						}
						match(LE);
						break;
					}
					case GE:
					{
						AST tmp136_AST = null;
						if (inputState.guessing==0) {
							tmp136_AST = (AST)astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp136_AST);
						}
						match(GE);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					shiftExpression();
					if (inputState.guessing==0) {
						astFactory.addASTChild(currentAST, returnAST);
					}
				}
				else {
					break _loop171;
				}
				
			} while (true);
			}
			break;
		}
		case LITERAL_instanceof:
		{
			AST tmp137_AST = null;
			if (inputState.guessing==0) {
				tmp137_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp137_AST);
			}
			match(LITERAL_instanceof);
			typeSpec(true);
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		relationalExpression_AST = (AST)currentAST.root;
		returnAST = relationalExpression_AST;
	}
	
	public final void shiftExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST shiftExpression_AST = null;
		
		additiveExpression();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		_loop175:
		do {
			if ((_tokenSet_28.member(LA(1)))) {
				{
				switch ( LA(1)) {
				case SL:
				{
					AST tmp138_AST = null;
					if (inputState.guessing==0) {
						tmp138_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp138_AST);
					}
					match(SL);
					break;
				}
				case SR:
				{
					AST tmp139_AST = null;
					if (inputState.guessing==0) {
						tmp139_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp139_AST);
					}
					match(SR);
					break;
				}
				case BSR:
				{
					AST tmp140_AST = null;
					if (inputState.guessing==0) {
						tmp140_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp140_AST);
					}
					match(BSR);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				additiveExpression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop175;
			}
			
		} while (true);
		}
		shiftExpression_AST = (AST)currentAST.root;
		returnAST = shiftExpression_AST;
	}
	
	public final void additiveExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST additiveExpression_AST = null;
		
		multiplicativeExpression();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		_loop179:
		do {
			if ((LA(1)==PLUS||LA(1)==MINUS)) {
				{
				switch ( LA(1)) {
				case PLUS:
				{
					AST tmp141_AST = null;
					if (inputState.guessing==0) {
						tmp141_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp141_AST);
					}
					match(PLUS);
					break;
				}
				case MINUS:
				{
					AST tmp142_AST = null;
					if (inputState.guessing==0) {
						tmp142_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp142_AST);
					}
					match(MINUS);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				multiplicativeExpression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop179;
			}
			
		} while (true);
		}
		additiveExpression_AST = (AST)currentAST.root;
		returnAST = additiveExpression_AST;
	}
	
	public final void multiplicativeExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST multiplicativeExpression_AST = null;
		
		unaryExpression();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		_loop183:
		do {
			if ((_tokenSet_29.member(LA(1)))) {
				{
				switch ( LA(1)) {
				case STAR:
				{
					AST tmp143_AST = null;
					if (inputState.guessing==0) {
						tmp143_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp143_AST);
					}
					match(STAR);
					break;
				}
				case DIV:
				{
					AST tmp144_AST = null;
					if (inputState.guessing==0) {
						tmp144_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp144_AST);
					}
					match(DIV);
					break;
				}
				case MOD:
				{
					AST tmp145_AST = null;
					if (inputState.guessing==0) {
						tmp145_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp145_AST);
					}
					match(MOD);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				unaryExpression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				break _loop183;
			}
			
		} while (true);
		}
		multiplicativeExpression_AST = (AST)currentAST.root;
		returnAST = multiplicativeExpression_AST;
	}
	
	public final void unaryExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unaryExpression_AST = null;
		
		switch ( LA(1)) {
		case INC:
		{
			AST tmp146_AST = null;
			if (inputState.guessing==0) {
				tmp146_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp146_AST);
			}
			match(INC);
			unaryExpression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			unaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case DEC:
		{
			AST tmp147_AST = null;
			if (inputState.guessing==0) {
				tmp147_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp147_AST);
			}
			match(DEC);
			unaryExpression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			unaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case MINUS:
		{
			AST tmp148_AST = null;
			if (inputState.guessing==0) {
				tmp148_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp148_AST);
			}
			match(MINUS);
			if ( inputState.guessing==0 ) {
				tmp148_AST.setType(UNARY_MINUS);
			}
			unaryExpression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			unaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case PLUS:
		{
			AST tmp149_AST = null;
			if (inputState.guessing==0) {
				tmp149_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp149_AST);
			}
			match(PLUS);
			if ( inputState.guessing==0 ) {
				tmp149_AST.setType(UNARY_PLUS);
			}
			unaryExpression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			unaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case IDENT:
		case LITERAL_void:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_short:
		case LITERAL_int:
		case LITERAL_float:
		case LITERAL_long:
		case LITERAL_double:
		case LPAREN:
		case LITERAL_this:
		case LITERAL_super:
		case BNOT:
		case LNOT:
		case LITERAL_true:
		case LITERAL_false:
		case LITERAL_null:
		case LITERAL_new:
		case NUM_INT:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		{
			unaryExpressionNotPlusMinus();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			unaryExpression_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = unaryExpression_AST;
	}
	
	public final void unaryExpressionNotPlusMinus() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unaryExpressionNotPlusMinus_AST = null;
		Token  lpb = null;
		AST lpb_AST = null;
		Token  lp = null;
		AST lp_AST = null;
		JavaToken t;
		
		switch ( LA(1)) {
		case BNOT:
		{
			AST tmp150_AST = null;
			if (inputState.guessing==0) {
				tmp150_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp150_AST);
			}
			match(BNOT);
			unaryExpression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			unaryExpressionNotPlusMinus_AST = (AST)currentAST.root;
			break;
		}
		case LNOT:
		{
			AST tmp151_AST = null;
			if (inputState.guessing==0) {
				tmp151_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp151_AST);
			}
			match(LNOT);
			unaryExpression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			unaryExpressionNotPlusMinus_AST = (AST)currentAST.root;
			break;
		}
		case IDENT:
		case LITERAL_void:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_short:
		case LITERAL_int:
		case LITERAL_float:
		case LITERAL_long:
		case LITERAL_double:
		case LPAREN:
		case LITERAL_this:
		case LITERAL_super:
		case LITERAL_true:
		case LITERAL_false:
		case LITERAL_null:
		case LITERAL_new:
		case NUM_INT:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		{
			{
			if ((LA(1)==LPAREN) && ((LA(2) >= LITERAL_void && LA(2) <= LITERAL_double))) {
				lpb = LT(1);
				if (inputState.guessing==0) {
					lpb_AST = (AST)astFactory.create(lpb);
					astFactory.makeASTRoot(currentAST, lpb_AST);
				}
				match(LPAREN);
				if ( inputState.guessing==0 ) {
					lpb_AST.setType(TYPECAST);
				}
				builtInTypeSpec(true);
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
				AST tmp152_AST = null;
				tmp152_AST = (AST)astFactory.create(LT(1));
				match(RPAREN);
				unaryExpression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else {
				boolean synPredMatched188 = false;
				if (((LA(1)==LPAREN) && (LA(2)==IDENT))) {
					int _m188 = mark();
					synPredMatched188 = true;
					inputState.guessing++;
					try {
						{
						match(LPAREN);
						classTypeSpec(true);
						match(RPAREN);
						unaryExpressionNotPlusMinus();
						}
					}
					catch (RecognitionException pe) {
						synPredMatched188 = false;
					}
					rewind(_m188);
					inputState.guessing--;
				}
				if ( synPredMatched188 ) {
					lp = LT(1);
					if (inputState.guessing==0) {
						lp_AST = (AST)astFactory.create(lp);
						astFactory.makeASTRoot(currentAST, lp_AST);
					}
					match(LPAREN);
					if ( inputState.guessing==0 ) {
						lp_AST.setType(TYPECAST);
					}
					t=classTypeSpec(true);
					if (inputState.guessing==0) {
						astFactory.addASTChild(currentAST, returnAST);
					}
					AST tmp153_AST = null;
					tmp153_AST = (AST)astFactory.create(LT(1));
					match(RPAREN);
					unaryExpressionNotPlusMinus();
					if (inputState.guessing==0) {
						astFactory.addASTChild(currentAST, returnAST);
					}
					if ( inputState.guessing==0 ) {
						reference(t);
					}
				}
				else if ((_tokenSet_30.member(LA(1))) && (_tokenSet_31.member(LA(2)))) {
					postfixExpression();
					if (inputState.guessing==0) {
						astFactory.addASTChild(currentAST, returnAST);
					}
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				unaryExpressionNotPlusMinus_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			returnAST = unaryExpressionNotPlusMinus_AST;
		}
		
	public final void postfixExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST postfixExpression_AST = null;
		Token  id = null;
		AST id_AST = null;
		Token  lp = null;
		AST lp_AST = null;
		Token  lp3 = null;
		AST lp3_AST = null;
		Token  lps = null;
		AST lps_AST = null;
		Token  lb = null;
		AST lb_AST = null;
		Token  in = null;
		AST in_AST = null;
		Token  de = null;
		AST de_AST = null;
		JavaToken t; JavaToken temp; int count=-1;
		
		t=primaryExpression();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		_loop194:
		do {
			if ((LA(1)==DOT) && (LA(2)==IDENT)) {
				AST tmp154_AST = null;
				if (inputState.guessing==0) {
					tmp154_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp154_AST);
				}
				match(DOT);
				id = LT(1);
				if (inputState.guessing==0) {
					id_AST = (AST)astFactory.create(id);
					astFactory.addASTChild(currentAST, id_AST);
				}
				match(IDENT);
				if ( inputState.guessing==0 ) {
					if (t!=null)
							             { 
								          temp = new JavaToken(t);
								          reference(temp);
								          t.setText(t.getText()+"."+id.getText());
								          t.setColumn(id.getColumn());
								         } 
					
				}
				{
				switch ( LA(1)) {
				case LPAREN:
				{
					lp = LT(1);
					if (inputState.guessing==0) {
						lp_AST = (AST)astFactory.create(lp);
						astFactory.makeASTRoot(currentAST, lp_AST);
					}
					match(LPAREN);
					if ( inputState.guessing==0 ) {
						lp_AST.setType(METHOD_CALL);
					}
					count=argList();
					if (inputState.guessing==0) {
						astFactory.addASTChild(currentAST, returnAST);
					}
					AST tmp155_AST = null;
					tmp155_AST = (AST)astFactory.create(LT(1));
					match(RPAREN);
					if ( inputState.guessing==0 ) {
						
						if (t!=null)
								    {
						t.setParamCount(count);
								            }
						
					}
					break;
				}
				case SEMI:
				case LBRACK:
				case RBRACK:
				case DOT:
				case LT:
				case COMMA:
				case GT:
				case SR:
				case BSR:
				case STAR:
				case BAND:
				case RCURLY:
				case RPAREN:
				case ASSIGN:
				case COLON:
				case PLUS_ASSIGN:
				case MINUS_ASSIGN:
				case STAR_ASSIGN:
				case DIV_ASSIGN:
				case MOD_ASSIGN:
				case SR_ASSIGN:
				case BSR_ASSIGN:
				case SL_ASSIGN:
				case BAND_ASSIGN:
				case BXOR_ASSIGN:
				case BOR_ASSIGN:
				case QUESTION:
				case LOR:
				case LAND:
				case BOR:
				case BXOR:
				case NOT_EQUAL:
				case EQUAL:
				case LE:
				case GE:
				case LITERAL_instanceof:
				case SL:
				case PLUS:
				case MINUS:
				case DIV:
				case MOD:
				case INC:
				case DEC:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
			}
			else if ((LA(1)==DOT) && (LA(2)==LITERAL_this)) {
				AST tmp156_AST = null;
				if (inputState.guessing==0) {
					tmp156_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp156_AST);
				}
				match(DOT);
				AST tmp157_AST = null;
				if (inputState.guessing==0) {
					tmp157_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp157_AST);
				}
				match(LITERAL_this);
			}
			else if ((LA(1)==DOT) && (LA(2)==LITERAL_super)) {
				AST tmp158_AST = null;
				if (inputState.guessing==0) {
					tmp158_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp158_AST);
				}
				match(DOT);
				AST tmp159_AST = null;
				if (inputState.guessing==0) {
					tmp159_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp159_AST);
				}
				match(LITERAL_super);
				{
				switch ( LA(1)) {
				case LPAREN:
				{
					lp3 = LT(1);
					if (inputState.guessing==0) {
						lp3_AST = (AST)astFactory.create(lp3);
						astFactory.makeASTRoot(currentAST, lp3_AST);
					}
					match(LPAREN);
					argList();
					if (inputState.guessing==0) {
						astFactory.addASTChild(currentAST, returnAST);
					}
					AST tmp160_AST = null;
					tmp160_AST = (AST)astFactory.create(LT(1));
					match(RPAREN);
					if ( inputState.guessing==0 ) {
						lp3_AST.setType(SUPER_CTOR_CALL);
					}
					break;
				}
				case DOT:
				{
					AST tmp161_AST = null;
					if (inputState.guessing==0) {
						tmp161_AST = (AST)astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp161_AST);
					}
					match(DOT);
					AST tmp162_AST = null;
					if (inputState.guessing==0) {
						tmp162_AST = (AST)astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp162_AST);
					}
					match(IDENT);
					{
					switch ( LA(1)) {
					case LPAREN:
					{
						lps = LT(1);
						if (inputState.guessing==0) {
							lps_AST = (AST)astFactory.create(lps);
							astFactory.makeASTRoot(currentAST, lps_AST);
						}
						match(LPAREN);
						if ( inputState.guessing==0 ) {
							lps_AST.setType(METHOD_CALL);
						}
						count=argList();
						if (inputState.guessing==0) {
							astFactory.addASTChild(currentAST, returnAST);
						}
						AST tmp163_AST = null;
						tmp163_AST = (AST)astFactory.create(LT(1));
						match(RPAREN);
						if ( inputState.guessing==0 ) {
							
							if (t!=null)
								    	    {
							t.setParamCount(count);
									                }
							
						}
						break;
					}
					case SEMI:
					case LBRACK:
					case RBRACK:
					case DOT:
					case LT:
					case COMMA:
					case GT:
					case SR:
					case BSR:
					case STAR:
					case BAND:
					case RCURLY:
					case RPAREN:
					case ASSIGN:
					case COLON:
					case PLUS_ASSIGN:
					case MINUS_ASSIGN:
					case STAR_ASSIGN:
					case DIV_ASSIGN:
					case MOD_ASSIGN:
					case SR_ASSIGN:
					case BSR_ASSIGN:
					case SL_ASSIGN:
					case BAND_ASSIGN:
					case BXOR_ASSIGN:
					case BOR_ASSIGN:
					case QUESTION:
					case LOR:
					case LAND:
					case BOR:
					case BXOR:
					case NOT_EQUAL:
					case EQUAL:
					case LE:
					case GE:
					case LITERAL_instanceof:
					case SL:
					case PLUS:
					case MINUS:
					case DIV:
					case MOD:
					case INC:
					case DEC:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
			}
			else if ((LA(1)==DOT) && (LA(2)==LITERAL_new)) {
				AST tmp164_AST = null;
				if (inputState.guessing==0) {
					tmp164_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp164_AST);
				}
				match(DOT);
				newExpression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
			}
			else if ((LA(1)==LBRACK)) {
				lb = LT(1);
				if (inputState.guessing==0) {
					lb_AST = (AST)astFactory.create(lb);
					astFactory.makeASTRoot(currentAST, lb_AST);
				}
				match(LBRACK);
				if ( inputState.guessing==0 ) {
					lb_AST.setType(INDEX_OP);
				}
				expression();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
				AST tmp165_AST = null;
				tmp165_AST = (AST)astFactory.create(LT(1));
				match(RBRACK);
			}
			else {
				break _loop194;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			
			if (t != null)
				      {	
			reference(t);
			}
				
		}
		{
		switch ( LA(1)) {
		case INC:
		{
			in = LT(1);
			if (inputState.guessing==0) {
				in_AST = (AST)astFactory.create(in);
				astFactory.makeASTRoot(currentAST, in_AST);
			}
			match(INC);
			if ( inputState.guessing==0 ) {
				in_AST.setType(POST_INC);
			}
			break;
		}
		case DEC:
		{
			de = LT(1);
			if (inputState.guessing==0) {
				de_AST = (AST)astFactory.create(de);
				astFactory.makeASTRoot(currentAST, de_AST);
			}
			match(DEC);
			if ( inputState.guessing==0 ) {
				de_AST.setType(POST_DEC);
			}
			break;
		}
		case SEMI:
		case RBRACK:
		case LT:
		case COMMA:
		case GT:
		case SR:
		case BSR:
		case STAR:
		case BAND:
		case RCURLY:
		case RPAREN:
		case ASSIGN:
		case COLON:
		case PLUS_ASSIGN:
		case MINUS_ASSIGN:
		case STAR_ASSIGN:
		case DIV_ASSIGN:
		case MOD_ASSIGN:
		case SR_ASSIGN:
		case BSR_ASSIGN:
		case SL_ASSIGN:
		case BAND_ASSIGN:
		case BXOR_ASSIGN:
		case BOR_ASSIGN:
		case QUESTION:
		case LOR:
		case LAND:
		case BOR:
		case BXOR:
		case NOT_EQUAL:
		case EQUAL:
		case LE:
		case GE:
		case LITERAL_instanceof:
		case SL:
		case PLUS:
		case MINUS:
		case DIV:
		case MOD:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		postfixExpression_AST = (AST)currentAST.root;
		returnAST = postfixExpression_AST;
	}
	
	public final JavaToken  primaryExpression() throws RecognitionException, TokenStreamException {
		JavaToken t;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primaryExpression_AST = null;
		Token  th = null;
		AST th_AST = null;
		Token  s = null;
		AST s_AST = null;
		Token  lbt = null;
		AST lbt_AST = null;
		t=null;
		
		switch ( LA(1)) {
		case IDENT:
		{
			t=identPrimary();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			{
			if ((LA(1)==DOT) && (LA(2)==LITERAL_class)) {
				AST tmp166_AST = null;
				if (inputState.guessing==0) {
					tmp166_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp166_AST);
				}
				match(DOT);
				AST tmp167_AST = null;
				if (inputState.guessing==0) {
					tmp167_AST = (AST)astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp167_AST);
				}
				match(LITERAL_class);
			}
			else if ((_tokenSet_32.member(LA(1))) && (_tokenSet_33.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			primaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case NUM_INT:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		{
			constant();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			primaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_true:
		{
			AST tmp168_AST = null;
			if (inputState.guessing==0) {
				tmp168_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp168_AST);
			}
			match(LITERAL_true);
			primaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_false:
		{
			AST tmp169_AST = null;
			if (inputState.guessing==0) {
				tmp169_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp169_AST);
			}
			match(LITERAL_false);
			primaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_null:
		{
			AST tmp170_AST = null;
			if (inputState.guessing==0) {
				tmp170_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp170_AST);
			}
			match(LITERAL_null);
			primaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_new:
		{
			t=newExpression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			primaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_this:
		{
			th = LT(1);
			if (inputState.guessing==0) {
				th_AST = (AST)astFactory.create(th);
				astFactory.addASTChild(currentAST, th_AST);
			}
			match(LITERAL_this);
			if ( inputState.guessing==0 ) {
				t = (JavaToken)th; setNearestClassScope();
			}
			primaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_super:
		{
			s = LT(1);
			if (inputState.guessing==0) {
				s_AST = (AST)astFactory.create(s);
				astFactory.addASTChild(currentAST, s_AST);
			}
			match(LITERAL_super);
			if ( inputState.guessing==0 ) {
				t = (JavaToken)s;
			}
			primaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case LPAREN:
		{
			AST tmp171_AST = null;
			tmp171_AST = (AST)astFactory.create(LT(1));
			match(LPAREN);
			assignmentExpression();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			AST tmp172_AST = null;
			tmp172_AST = (AST)astFactory.create(LT(1));
			match(RPAREN);
			primaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_void:
		case LITERAL_boolean:
		case LITERAL_byte:
		case LITERAL_char:
		case LITERAL_short:
		case LITERAL_int:
		case LITERAL_float:
		case LITERAL_long:
		case LITERAL_double:
		{
			builtInType();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			{
			_loop199:
			do {
				if ((LA(1)==LBRACK)) {
					lbt = LT(1);
					if (inputState.guessing==0) {
						lbt_AST = (AST)astFactory.create(lbt);
						astFactory.makeASTRoot(currentAST, lbt_AST);
					}
					match(LBRACK);
					if ( inputState.guessing==0 ) {
						lbt_AST.setType(ARRAY_DECLARATOR);
					}
					AST tmp173_AST = null;
					tmp173_AST = (AST)astFactory.create(LT(1));
					match(RBRACK);
				}
				else {
					break _loop199;
				}
				
			} while (true);
			}
			AST tmp174_AST = null;
			if (inputState.guessing==0) {
				tmp174_AST = (AST)astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp174_AST);
			}
			match(DOT);
			AST tmp175_AST = null;
			if (inputState.guessing==0) {
				tmp175_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp175_AST);
			}
			match(LITERAL_class);
			primaryExpression_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = primaryExpression_AST;
		return t;
	}
	
/** object instantiation.
 *  Trees are built as illustrated by the following input/tree pairs:
 *
 *  new T()
 *
 *  new
 *   |
 *   T --  ELIST
 *           |
 *          arg1 -- arg2 -- .. -- argn
 *
 *  new int[]
 *
 *  new
 *   |
 *  int -- ARRAY_DECLARATOR
 *
 *  new int[] {1,2}
 *
 *  new
 *   |
 *  int -- ARRAY_DECLARATOR -- ARRAY_INIT
 *                                  |
 *                                EXPR -- EXPR
 *                                  |      |
 *                                  1      2
 *
 *  new int[3]
 *  new
 *   |
 *  int -- ARRAY_DECLARATOR
 *                |
 *              EXPR
 *                |
 *                3
 *
 *  new int[1][2]
 *
 *  new
 *   |
 *  int -- ARRAY_DECLARATOR
 *               |
 *         ARRAY_DECLARATOR -- EXPR
 *               |              |
 *             EXPR             1
 *               |
 *               2
 *
 */
	public final JavaToken  newExpression() throws RecognitionException, TokenStreamException {
		JavaToken t;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST newExpression_AST = null;
		t=null; int count=-1;
		
		AST tmp176_AST = null;
		if (inputState.guessing==0) {
			tmp176_AST = (AST)astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp176_AST);
		}
		match(LITERAL_new);
		t=type();
		if (inputState.guessing==0) {
			astFactory.addASTChild(currentAST, returnAST);
		}
		{
		switch ( LA(1)) {
		case LPAREN:
		{
			AST tmp177_AST = null;
			tmp177_AST = (AST)astFactory.create(LT(1));
			match(LPAREN);
			count=argList();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			if ( inputState.guessing==0 ) {
				
				// t.setText(t.getText()+".~constructor~");
				t.setText(t.getText()+"."+t.getText());
				t.setParamCount(count);
				
			}
			AST tmp178_AST = null;
			tmp178_AST = (AST)astFactory.create(LT(1));
			match(RPAREN);
			{
			switch ( LA(1)) {
			case LCURLY:
			{
				classBlock();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
				break;
			}
			case SEMI:
			case LBRACK:
			case RBRACK:
			case DOT:
			case LT:
			case COMMA:
			case GT:
			case SR:
			case BSR:
			case STAR:
			case BAND:
			case RCURLY:
			case RPAREN:
			case ASSIGN:
			case COLON:
			case PLUS_ASSIGN:
			case MINUS_ASSIGN:
			case STAR_ASSIGN:
			case DIV_ASSIGN:
			case MOD_ASSIGN:
			case SR_ASSIGN:
			case BSR_ASSIGN:
			case SL_ASSIGN:
			case BAND_ASSIGN:
			case BXOR_ASSIGN:
			case BOR_ASSIGN:
			case QUESTION:
			case LOR:
			case LAND:
			case BOR:
			case BXOR:
			case NOT_EQUAL:
			case EQUAL:
			case LE:
			case GE:
			case LITERAL_instanceof:
			case SL:
			case PLUS:
			case MINUS:
			case DIV:
			case MOD:
			case INC:
			case DEC:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case LBRACK:
		{
			newArrayDeclarator();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			{
			switch ( LA(1)) {
			case LCURLY:
			{
				arrayInitializer();
				if (inputState.guessing==0) {
					astFactory.addASTChild(currentAST, returnAST);
				}
				break;
			}
			case SEMI:
			case LBRACK:
			case RBRACK:
			case DOT:
			case LT:
			case COMMA:
			case GT:
			case SR:
			case BSR:
			case STAR:
			case BAND:
			case RCURLY:
			case RPAREN:
			case ASSIGN:
			case COLON:
			case PLUS_ASSIGN:
			case MINUS_ASSIGN:
			case STAR_ASSIGN:
			case DIV_ASSIGN:
			case MOD_ASSIGN:
			case SR_ASSIGN:
			case BSR_ASSIGN:
			case SL_ASSIGN:
			case BAND_ASSIGN:
			case BXOR_ASSIGN:
			case BOR_ASSIGN:
			case QUESTION:
			case LOR:
			case LAND:
			case BOR:
			case BXOR:
			case NOT_EQUAL:
			case EQUAL:
			case LE:
			case GE:
			case LITERAL_instanceof:
			case SL:
			case PLUS:
			case MINUS:
			case DIV:
			case MOD:
			case INC:
			case DEC:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		newExpression_AST = (AST)currentAST.root;
		returnAST = newExpression_AST;
		return t;
	}
	
/** Match a, a.b.c refs, a.b.c(...) refs, a.b.c[], a.b.c[].class,
 *  and a.b.c.class refs.  Also this(...) and super(...).  Match
 *  this or super.
 */
	public final JavaToken  identPrimary() throws RecognitionException, TokenStreamException {
		JavaToken t;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST identPrimary_AST = null;
		Token  id = null;
		AST id_AST = null;
		Token  id2 = null;
		AST id2_AST = null;
		Token  lp = null;
		AST lp_AST = null;
		Token  lbc = null;
		AST lbc_AST = null;
		JavaToken temp;t=null;int count=-1;
		
		id = LT(1);
		if (inputState.guessing==0) {
			id_AST = (AST)astFactory.create(id);
			astFactory.addASTChild(currentAST, id_AST);
		}
		match(IDENT);
		if ( inputState.guessing==0 ) {
			t = (JavaToken)id;
		}
		{
		_loop202:
		do {
			if ((LA(1)==DOT) && (LA(2)==IDENT)) {
				AST tmp179_AST = null;
				if (inputState.guessing==0) {
					tmp179_AST = (AST)astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp179_AST);
				}
				match(DOT);
				id2 = LT(1);
				if (inputState.guessing==0) {
					id2_AST = (AST)astFactory.create(id2);
					astFactory.addASTChild(currentAST, id2_AST);
				}
				match(IDENT);
				if ( inputState.guessing==0 ) {
					if (t!=null)
							             { 
								          temp = new JavaToken(t);
								          reference(temp);
								          t.setText(t.getText()+"."+id2.getText());
								          t.setColumn(id2.getColumn());
								         } 
					
				}
			}
			else {
				break _loop202;
			}
			
		} while (true);
		}
		{
		if ((LA(1)==LPAREN)) {
			{
			lp = LT(1);
			if (inputState.guessing==0) {
				lp_AST = (AST)astFactory.create(lp);
				astFactory.makeASTRoot(currentAST, lp_AST);
			}
			match(LPAREN);
			if ( inputState.guessing==0 ) {
				lp_AST.setType(METHOD_CALL);
			}
			count=argList();
			if (inputState.guessing==0) {
				astFactory.addASTChild(currentAST, returnAST);
			}
			AST tmp180_AST = null;
			tmp180_AST = (AST)astFactory.create(LT(1));
			match(RPAREN);
			if ( inputState.guessing==0 ) {
				
				if (t!=null)
						    {
				t.setParamCount(count);
						            }
				
			}
			}
		}
		else if ((LA(1)==LBRACK) && (LA(2)==RBRACK)) {
			{
			int _cnt206=0;
			_loop206:
			do {
				if ((LA(1)==LBRACK) && (LA(2)==RBRACK)) {
					lbc = LT(1);
					if (inputState.guessing==0) {
						lbc_AST = (AST)astFactory.create(lbc);
						astFactory.makeASTRoot(currentAST, lbc_AST);
					}
					match(LBRACK);
					if ( inputState.guessing==0 ) {
						lbc_AST.setType(ARRAY_DECLARATOR);
					}
					AST tmp181_AST = null;
					tmp181_AST = (AST)astFactory.create(LT(1));
					match(RBRACK);
				}
				else {
					if ( _cnt206>=1 ) { break _loop206; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt206++;
			} while (true);
			}
		}
		else if ((_tokenSet_32.member(LA(1))) && (_tokenSet_33.member(LA(2)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		identPrimary_AST = (AST)currentAST.root;
		returnAST = identPrimary_AST;
		return t;
	}
	
	public final void constant() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constant_AST = null;
		
		switch ( LA(1)) {
		case NUM_INT:
		{
			AST tmp182_AST = null;
			if (inputState.guessing==0) {
				tmp182_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp182_AST);
			}
			match(NUM_INT);
			constant_AST = (AST)currentAST.root;
			break;
		}
		case CHAR_LITERAL:
		{
			AST tmp183_AST = null;
			if (inputState.guessing==0) {
				tmp183_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp183_AST);
			}
			match(CHAR_LITERAL);
			constant_AST = (AST)currentAST.root;
			break;
		}
		case STRING_LITERAL:
		{
			AST tmp184_AST = null;
			if (inputState.guessing==0) {
				tmp184_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp184_AST);
			}
			match(STRING_LITERAL);
			constant_AST = (AST)currentAST.root;
			break;
		}
		case NUM_FLOAT:
		{
			AST tmp185_AST = null;
			if (inputState.guessing==0) {
				tmp185_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp185_AST);
			}
			match(NUM_FLOAT);
			constant_AST = (AST)currentAST.root;
			break;
		}
		case NUM_LONG:
		{
			AST tmp186_AST = null;
			if (inputState.guessing==0) {
				tmp186_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp186_AST);
			}
			match(NUM_LONG);
			constant_AST = (AST)currentAST.root;
			break;
		}
		case NUM_DOUBLE:
		{
			AST tmp187_AST = null;
			if (inputState.guessing==0) {
				tmp187_AST = (AST)astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp187_AST);
			}
			match(NUM_DOUBLE);
			constant_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = constant_AST;
	}
	
	public final void newArrayDeclarator() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST newArrayDeclarator_AST = null;
		Token  lb = null;
		AST lb_AST = null;
		
		{
		int _cnt216=0;
		_loop216:
		do {
			if ((LA(1)==LBRACK) && (_tokenSet_34.member(LA(2)))) {
				lb = LT(1);
				if (inputState.guessing==0) {
					lb_AST = (AST)astFactory.create(lb);
					astFactory.makeASTRoot(currentAST, lb_AST);
				}
				match(LBRACK);
				if ( inputState.guessing==0 ) {
					lb_AST.setType(ARRAY_DECLARATOR);
				}
				{
				switch ( LA(1)) {
				case IDENT:
				case LITERAL_void:
				case LITERAL_boolean:
				case LITERAL_byte:
				case LITERAL_char:
				case LITERAL_short:
				case LITERAL_int:
				case LITERAL_float:
				case LITERAL_long:
				case LITERAL_double:
				case LPAREN:
				case LITERAL_this:
				case LITERAL_super:
				case PLUS:
				case MINUS:
				case INC:
				case DEC:
				case BNOT:
				case LNOT:
				case LITERAL_true:
				case LITERAL_false:
				case LITERAL_null:
				case LITERAL_new:
				case NUM_INT:
				case CHAR_LITERAL:
				case STRING_LITERAL:
				case NUM_FLOAT:
				case NUM_LONG:
				case NUM_DOUBLE:
				{
					expression();
					if (inputState.guessing==0) {
						astFactory.addASTChild(currentAST, returnAST);
					}
					break;
				}
				case RBRACK:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				AST tmp188_AST = null;
				tmp188_AST = (AST)astFactory.create(LT(1));
				match(RBRACK);
			}
			else {
				if ( _cnt216>=1 ) { break _loop216; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt216++;
		} while (true);
		}
		newArrayDeclarator_AST = (AST)currentAST.root;
		returnAST = newArrayDeclarator_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"BLOCK",
		"MODIFIERS",
		"OBJBLOCK",
		"SLIST",
		"CTOR_DEF",
		"METHOD_DEF",
		"VARIABLE_DEF",
		"INSTANCE_INIT",
		"STATIC_INIT",
		"TYPE",
		"CLASS_DEF",
		"INTERFACE_DEF",
		"PACKAGE_DEF",
		"ARRAY_DECLARATOR",
		"EXTENDS_CLAUSE",
		"IMPLEMENTS_CLAUSE",
		"PARAMETERS",
		"PARAMETER_DEF",
		"LABELED_STAT",
		"TYPECAST",
		"INDEX_OP",
		"POST_INC",
		"POST_DEC",
		"METHOD_CALL",
		"EXPR",
		"ARRAY_INIT",
		"IMPORT",
		"UNARY_MINUS",
		"UNARY_PLUS",
		"CASE_GROUP",
		"ELIST",
		"FOR_INIT",
		"FOR_CONDITION",
		"FOR_ITERATOR",
		"EMPTY_STAT",
		"\"final\"",
		"\"abstract\"",
		"\"strictfp\"",
		"SUPER_CTOR_CALL",
		"CTOR_CALL",
		"\"package\"",
		"SEMI",
		"\"import\"",
		"LBRACK",
		"RBRACK",
		"IDENT",
		"DOT",
		"LT",
		"COMMA",
		"GT",
		"SR",
		"BSR",
		"\"void\"",
		"\"boolean\"",
		"\"byte\"",
		"\"char\"",
		"\"short\"",
		"\"int\"",
		"\"float\"",
		"\"long\"",
		"\"double\"",
		"STAR",
		"\"private\"",
		"\"public\"",
		"\"protected\"",
		"\"static\"",
		"\"transient\"",
		"\"native\"",
		"\"threadsafe\"",
		"\"synchronized\"",
		"\"volatile\"",
		"\"class\"",
		"\"extends\"",
		"\"interface\"",
		"BAND",
		"LCURLY",
		"RCURLY",
		"\"implements\"",
		"LPAREN",
		"RPAREN",
		"\"this\"",
		"\"super\"",
		"ASSIGN",
		"\"throws\"",
		"COLON",
		"\"if\"",
		"\"else\"",
		"\"for\"",
		"\"while\"",
		"\"do\"",
		"\"break\"",
		"\"continue\"",
		"\"return\"",
		"\"switch\"",
		"\"throw\"",
		"\"assert\"",
		"\"case\"",
		"\"default\"",
		"\"try\"",
		"\"finally\"",
		"\"catch\"",
		"PLUS_ASSIGN",
		"MINUS_ASSIGN",
		"STAR_ASSIGN",
		"DIV_ASSIGN",
		"MOD_ASSIGN",
		"SR_ASSIGN",
		"BSR_ASSIGN",
		"SL_ASSIGN",
		"BAND_ASSIGN",
		"BXOR_ASSIGN",
		"BOR_ASSIGN",
		"QUESTION",
		"LOR",
		"LAND",
		"BOR",
		"BXOR",
		"NOT_EQUAL",
		"EQUAL",
		"LE",
		"GE",
		"\"instanceof\"",
		"SL",
		"PLUS",
		"MINUS",
		"DIV",
		"MOD",
		"INC",
		"DEC",
		"BNOT",
		"LNOT",
		"\"true\"",
		"\"false\"",
		"\"null\"",
		"\"new\"",
		"NUM_INT",
		"CHAR_LITERAL",
		"STRING_LITERAL",
		"NUM_FLOAT",
		"NUM_LONG",
		"NUM_DOUBLE",
		"WS",
		"SL_COMMENT",
		"ML_COMMENT",
		"ESC",
		"HEX_DIGIT",
		"VOCAB",
		"EXPONENT",
		"FLOAT_SUFFIX"
	};
	
	private static final long _tokenSet_0_data_[] = { 39032662786048L, 12284L, 0L, 0L };
	public static final BitSet _tokenSet_0 = new BitSet(_tokenSet_0_data_);
	private static final long _tokenSet_1_data_[] = { 109401406963714L, 12284L, 0L, 0L };
	public static final BitSet _tokenSet_1 = new BitSet(_tokenSet_1_data_);
	private static final long _tokenSet_2_data_[] = { 39032662786050L, 12284L, 0L, 0L };
	public static final BitSet _tokenSet_2 = new BitSet(_tokenSet_2_data_);
	private static final long _tokenSet_3_data_[] = { 3848290697216L, 2044L, 0L, 0L };
	public static final BitSet _tokenSet_3 = new BitSet(_tokenSet_3_data_);
	private static final long _tokenSet_4_data_[] = { -2357352929951744L, 576458553302175745L, 0L, 0L };
	public static final BitSet _tokenSet_4 = new BitSet(_tokenSet_4_data_);
	private static final long _tokenSet_5_data_[] = { -101704825569280L, -1649275830273L, 131071L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_5 = new BitSet(_tokenSet_5_data_);
	private static final long _tokenSet_6_data_[] = { -3940649673949184L, 167937L, 0L, 0L };
	public static final BitSet _tokenSet_6 = new BitSet(_tokenSet_6_data_);
	private static final long _tokenSet_7_data_[] = { -67937174212837376L, 245757L, 0L, 0L };
	public static final BitSet _tokenSet_7 = new BitSet(_tokenSet_7_data_);
	private static final long _tokenSet_8_data_[] = { -69238995980124160L, 12285L, 0L, 0L };
	public static final BitSet _tokenSet_8 = new BitSet(_tokenSet_8_data_);
	private static final long _tokenSet_9_data_[] = { -67972358584926208L, 274429L, 0L, 0L };
	public static final BitSet _tokenSet_9 = new BitSet(_tokenSet_9_data_);
	private static final long _tokenSet_10_data_[] = { -69242844270821376L, 1L, 0L, 0L };
	public static final BitSet _tokenSet_10 = new BitSet(_tokenSet_10_data_);
	private static final long _tokenSet_11_data_[] = { 4081387162304512L, 0L, 0L };
	public static final BitSet _tokenSet_11 = new BitSet(_tokenSet_11_data_);
	private static final long _tokenSet_12_data_[] = { 4679521487814656L, 4194304L, 0L, 0L };
	public static final BitSet _tokenSet_12 = new BitSet(_tokenSet_12_data_);
	private static final long _tokenSet_13_data_[] = { -71455611421720576L, -9223371693354545155L, 131065L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_13 = new BitSet(_tokenSet_13_data_);
	private static final long _tokenSet_14_data_[] = { -4886779429650432L, -1855502028801L, 131071L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_14 = new BitSet(_tokenSet_14_data_);
	private static final long _tokenSet_15_data_[] = { -71455611421720576L, -9223371693354610691L, 131065L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_15 = new BitSet(_tokenSet_15_data_);
	private static final long _tokenSet_16_data_[] = { -71455611421720576L, -9223371487129006083L, 131065L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_16 = new BitSet(_tokenSet_16_data_);
	private static final long _tokenSet_17_data_[] = { -4886779429650432L, -9048065L, 131071L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_17 = new BitSet(_tokenSet_17_data_);
	private static final long _tokenSet_18_data_[] = { -71490795793809408L, 2045L, 0L, 0L };
	public static final BitSet _tokenSet_18 = new BitSet(_tokenSet_18_data_);
	private static final long _tokenSet_19_data_[] = { -67972358584926208L, 2045L, 0L, 0L };
	public static final BitSet _tokenSet_19 = new BitSet(_tokenSet_19_data_);
	private static final long _tokenSet_20_data_[] = { -71494644084506624L, -9223372036851367935L, 131065L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_20 = new BitSet(_tokenSet_20_data_);
	private static final long _tokenSet_21_data_[] = { -4890627720347648L, -2199015636989L, 131071L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_21 = new BitSet(_tokenSet_21_data_);
	private static final long _tokenSet_22_data_[] = { 3848290697216L, 4092L, 0L, 0L };
	public static final BitSet _tokenSet_22 = new BitSet(_tokenSet_22_data_);
	private static final long _tokenSet_23_data_[] = { 566798244118528L, 4092L, 0L, 0L };
	public static final BitSet _tokenSet_23 = new BitSet(_tokenSet_23_data_);
	private static final long _tokenSet_24_data_[] = { -71494644084506624L, -9223372036851335167L, 131065L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_24 = new BitSet(_tokenSet_24_data_);
	private static final long _tokenSet_25_data_[] = { -387028092977152L, -2199015636989L, 131071L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_25 = new BitSet(_tokenSet_25_data_);
	private static final long _tokenSet_26_data_[] = { -71494644084506624L, -9223372036834590719L, 131065L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_26 = new BitSet(_tokenSet_26_data_);
	private static final long _tokenSet_27_data_[] = { 11258999068426240L, 1729382256910270464L, 0L, 0L };
	public static final BitSet _tokenSet_27 = new BitSet(_tokenSet_27_data_);
	private static final long _tokenSet_28_data_[] = { 54043195528445952L, 4611686018427387904L, 0L, 0L };
	public static final BitSet _tokenSet_28 = new BitSet(_tokenSet_28_data_);
	private static final long _tokenSet_29_data_[] = { 0L, 2L, 6L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_29 = new BitSet(_tokenSet_29_data_);
	private static final long _tokenSet_30_data_[] = { -71494644084506624L, 3407873L, 130944L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_30 = new BitSet(_tokenSet_30_data_);
	private static final long _tokenSet_31_data_[] = { -105553116266496L, -2198998269949L, 131071L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_31 = new BitSet(_tokenSet_31_data_);
	private static final long _tokenSet_32_data_[] = { 71389090968240128L, -2199001677822L, 31L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_32 = new BitSet(_tokenSet_32_data_);
	private static final long _tokenSet_33_data_[] = { -101704825569280L, -1649275965441L, 131071L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_33 = new BitSet(_tokenSet_33_data_);
	private static final long _tokenSet_34_data_[] = { -71213169107795968L, -9223372036851367935L, 131065L, 0L, 0L, 0L };
	public static final BitSet _tokenSet_34 = new BitSet(_tokenSet_34_data_);
	
	}
