/* Java 1.5/JSR14 Recognizer
 *
 * Run 'java Main [-showtree] directory-full-of-java-files'
 *
 * [The -showtree option pops up a Swing frame that shows
 *  the AST constructed from the parser.]
 *
 * Run 'java Main <directory full of java files>'
 *
 * Contributing authors:
 *		John Mitchell		johnm@non.net
 *		Terence Parr		parrt@magelang.com
 *		John Lilley			jlilley@empathy.com
 *		Scott Stanchfield	thetick@magelang.com
 *		Markus Mohnen       mohnen@informatik.rwth-aachen.de
 *      Peter Williams      pete.williams@sun.com
 *      Allan Jacobs        Allan.Jacobs@eng.sun.com
 *      Steve Messick       messick@redhills.com
 *      John Pybus			john@pybus.org
 *
 * Version 1.00 December 9, 1997 -- initial release
 * Version 1.01 December 10, 1997
 *		fixed bug in octal def (0..7 not 0..8)
 * Version 1.10 August 1998 (parrt)
 *		added tree construction
 *		fixed definition of WS,comments for mac,pc,unix newlines
 *		added unary plus
 * Version 1.11 (Nov 20, 1998)
 *		Added "shutup" option to turn off last ambig warning.
 *		Fixed inner class def to allow named class defs as statements
 *		synchronized requires compound not simple statement
 *		add [] after builtInType DOT class in primaryExpression
 *		"const" is reserved but not valid..removed from modifiers
 * Version 1.12 (Feb 2, 1999)
 *		Changed LITERAL_xxx to xxx in tree grammar.
 *		Updated java.g to use tokens {...} now for 2.6.0 (new feature).
 *
 * Version 1.13 (Apr 23, 1999)
 *		Didn't have (stat)? for else clause in tree parser.
 *		Didn't gen ASTs for interface extends.  Updated tree parser too.
 *		Updated to 2.6.0.
 * Version 1.14 (Jun 20, 1999)
 *		Allowed final/abstract on local classes.
 *		Removed local interfaces from methods
 *		Put instanceof precedence where it belongs...in relationalExpr
 *			It also had expr not type as arg; fixed it.
 *		Missing ! on SEMI in classBlock
 *		fixed: (expr) + "string" was parsed incorrectly (+ as unary plus).
 *		fixed: didn't like Object[].class in parser or tree parser
 * Version 1.15 (Jun 26, 1999)
 *		Screwed up rule with instanceof in it. :(  Fixed.
 *		Tree parser didn't like (expr).something; fixed.
 *		Allowed multiple inheritance in tree grammar. oops.
 * Version 1.16 (August 22, 1999)
 *		Extending an interface built a wacky tree: had extra EXTENDS.
 *		Tree grammar didn't allow multiple superinterfaces.
 *		Tree grammar didn't allow empty var initializer: {}
 * Version 1.17 (October 12, 1999)
 *		ESC lexer rule allowed 399 max not 377 max.
 *		java.tree.g didn't handle the expression of synchronized
 *		statements.
 * Version 1.18 (August 12, 2001)
 *      	Terence updated to Java 2 Version 1.3 by
 *		observing/combining work of Allan Jacobs and Steve
 *		Messick.  Handles 1.3 src.  Summary:
 *		o  primary didn't include boolean.class kind of thing
 *      	o  constructor calls parsed explicitly now:
 * 		   see explicitConstructorInvocation
 *		o  add strictfp modifier
 *      	o  missing objBlock after new expression in tree grammar
 *		o  merged local class definition alternatives, moved after declaration
 *		o  fixed problem with ClassName.super.field
 *      	o  reordered some alternatives to make things more efficient
 *		o  long and double constants were not differentiated from int/float
 *		o  whitespace rule was inefficient: matched only one char
 *		o  add an examples directory with some nasty 1.3 cases
 *		o  made Main.java use buffered IO and a Reader for Unicode support
 *		o  supports UNICODE?
 *		   Using Unicode charVocabulay makes code file big, but only
 *		   in the bitsets at the end. I need to make ANTLR generate
 *		   unicode bitsets more efficiently.
 * Version 1.19 (April 25, 2002)
 *		Terence added in nice fixes by John Pybus concerning floating
 *		constants and problems with super() calls.  John did a nice
 *		reorg of the primary/postfix expression stuff to read better
 *		and makes f.g.super() parse properly (it was METHOD_CALL not
 *		a SUPER_CTOR_CALL).  Also:
 *
 *		o  "finally" clause was a root...made it a child of "try"
 *		o  Added stuff for asserts too for Java 1.4, but *commented out*
 *		   as it is not backward compatible.
 *
 * Version 1.20 (October 27, 2002)
 *
 *      Terence ended up reorging John Pybus' stuff to
 *      remove some nondeterminisms and some syntactic predicates.
 *      Note that the grammar is stricter now; e.g., this(...) must
 *	be the first statement.
 *
 *      Trinary ?: operator wasn't working as array name:
 *          (isBig ? bigDigits : digits)[i];
 *
 *      Checked parser/tree parser on source for
 *          Resin-2.0.5, jive-2.1.1, jdk 1.3.1, Lucene, antlr 2.7.2a4,
 *	    and the 110k-line jGuru server source.
 *
 * Version 1.21.2 (March, 2003)
 *      Changes by Matt Quail to support generics (as per JDK1.5/JSR14)
 *      Notes:
 *      o We only allow the "extends" keyword and not the "implements"
 *        keyword, since thats what JSR14 seems to imply.
 *      o Thanks to Monty Zukowski for his help on the antlr-interest
 *        mail list.
 *      o Thanks to Alan Eliasen for testing the grammar over his
 *        Fink source base
 *
 *
 * This grammar is in the PUBLIC DOMAIN
 */

/* --- Begin Xref additions --- */

// include an ANTLR "header" so all generated code will be in 
// package org.apache.alexandria.javasrc.xref
header {
package org.apache.alexandria.javasrc.xref;
}

// Import the necessary classes
{
import java.util.Vector;
import java.io.*;
import org.apache.alexandria.javasrc.symtab.SymbolTable;
import org.apache.alexandria.javasrc.symtab.JavaVector;
import org.apache.alexandria.javasrc.symtab.DummyClass;
import org.apache.alexandria.javasrc.util.*;
}

class JavaXref extends Parser;
options {
	k = 2;                           // two token lookahead
	exportVocab=Java;                // Call its vocabulary "Java"
	codeGenMakeSwitchThreshold = 2;  // Some optimizations
	codeGenBitsetTestThreshold = 3;
	defaultErrorHandler = false;     // Don't generate parser error handlers
	buildAST = true;
}

tokens {
	BLOCK; MODIFIERS; OBJBLOCK; SLIST; CTOR_DEF; METHOD_DEF; VARIABLE_DEF;
	INSTANCE_INIT; STATIC_INIT; TYPE; CLASS_DEF; INTERFACE_DEF;
	PACKAGE_DEF; ARRAY_DECLARATOR; EXTENDS_CLAUSE; IMPLEMENTS_CLAUSE;
	PARAMETERS; PARAMETER_DEF; LABELED_STAT; TYPECAST; INDEX_OP;
	POST_INC; POST_DEC; METHOD_CALL; EXPR; ARRAY_INIT;
	IMPORT; UNARY_MINUS; UNARY_PLUS; CASE_GROUP; ELIST; FOR_INIT; FOR_CONDITION;
	FOR_ITERATOR; EMPTY_STAT; FINAL="final"; ABSTRACT="abstract";
	STRICTFP="strictfp"; SUPER_CTOR_CALL; CTOR_CALL;
}

// Define some methods and variables to use in the generated parser.
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
            System.err.println("   "+f.getAbsolutePath());
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
            lexer.setTokenObjectClass("org.apache.alexandria.javasrc.xref.JavaToken");

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
}

/* --- End Xref additions --- */

// Compilation Unit: In Java, this is a single file.  This is the start
//   rule for this parser
compilationUnit
	:	// A compilation unit starts with an optional package definition
		(	packageDefinition
		|	/* nothing */ {useDefaultPackage();} // XR
		)

		// Next we have a series of zero or more import statements
		( importDefinition )*

		// Wrapping things up with any number of class or interface
		//    definitions
		( typeDefinition )*

		EOF!
		{endFile();} // XR: if a package were defined, pop its scope
	;


// Package statement: "package" followed by an identifier.
packageDefinition
	options {defaultErrorHandler = true;} // let ANTLR handle errors
    {JavaToken id;} // XR: define an id for the package name
	:	p:"package"^ {#p.setType(PACKAGE_DEF);} id=identifier SEMI!
        {definePackage(id);}  // tell the symbol table about the package
	;


// Import statement: import followed by a package or class name
importDefinition
	options {defaultErrorHandler = true;}
	:	i:"import"^ {#i.setType(IMPORT);} identifierStar SEMI!
	;

// A type definition in a file is either a class or interface definition.
typeDefinition
	options {defaultErrorHandler = true;}
	:	m:modifiers!
		( classDefinition[#m]
		| interfaceDefinition[#m]
		)
	|	SEMI!
	;

/** A declaration is the creation of a reference or primitive-type variable
 *  Create a separate Type/Var tree for each var in the var list.
 */
declaration!
    {JavaToken type;}
	:	m:modifiers type=t:typeSpec[false] v:variableDefinitions[#m,#t,type]
		{#declaration = #v;}
	;

// A list of zero or more modifiers.  We could have used (modifier)* in
//   place of a call to modifiers, but I thought it was a good idea to keep
//   this rule separate so they can easily be collected in a Vector if
//   someone so desires
modifiers
	:	( modifier )*
		{#modifiers = #([MODIFIERS, "MODIFIERS"], #modifiers);}
	;

// A type specification is a type name with possible brackets afterwards
//   (which would make it an array type).
typeSpec[boolean addImagNode] returns [JavaToken t]
    {t=null;}
	: t=classTypeSpec[addImagNode]
	| t=builtInTypeSpec[addImagNode]
	;

arraySpecOpt:
        (options{greedy=true;}: // match as many as possible
            lb:LBRACK^ {#lb.setType(ARRAY_DECLARATOR);} RBRACK!
        )*
    ;

// A class type specification is a class type with either:
// - possible brackets afterwards
//   (which would make it an array type).
// - generic type arguments after
classTypeSpec[boolean addImagNode] returns [JavaToken t]
    {t=null;}
	:   t=classOrInterfaceType[addImagNode]
        arraySpecOpt
		{
			if ( addImagNode ) {
				#classTypeSpec = #(#[TYPE,"TYPE"], #classTypeSpec);
			}
		}
	;
classOrInterfaceType[boolean addImagNode] returns [JavaToken t]
    {t=null;}
	:   id1:IDENT {t = (JavaToken)id1;} (typeArguments[addImagNode])?
        (options{greedy=true;}: // match as many as possible
            DOT
          id2:IDENT
            {
             t.setText(t.getText() + "." + id2.getText());
			 t.setColumn(id2.getColumn());
            }
            (typeArguments[addImagNode])?
        )*
    ;
typeArguments[boolean addImagNode]
{int currentLtLevel = 0;}
    :
        {currentLtLevel = ltCounter;}
        LT {ltCounter++;}
        classTypeSpec[addImagNode]
        (options{greedy=true;}: // match as many as possible
            COMMA classTypeSpec [addImagNode]
        )*
        
        (   // turn warning off since Antlr generates the right code,
            // plus we have our semantic predicate below
            options{generateAmbigWarnings=false;}:
            typeArgumentsEnd
        )?
        
        // make sure we have gobbled up enough '>' characters
        // if we are at the "top level" of nested typeArgument productions
        {(currentLtLevel != 0) || ltCounter == currentLtLevel}?
    ;

// this gobbles up *some* amount of '>' characters, and counts how many
// it gobbled.
protected typeArgumentsEnd:
        GT {ltCounter-=1;}
    |   SR {ltCounter-=2;}
    |   BSR {ltCounter-=3;}
    ;

// A builtin type specification is a builtin type with possible brackets
// afterwards (which would make it an array type).
builtInTypeSpec[boolean addImagNode] returns [JavaToken t]
    {t=null;}
	:	t=builtInType arraySpecOpt
		{
			if ( addImagNode ) {
				#builtInTypeSpec = #(#[TYPE,"TYPE"], #builtInTypeSpec);
			}
		}
	;

// A type name. which is either a (possibly qualified and parameterized)
// class name or a primitive (builtin) type
type returns [JavaToken t]
    {t=null;}
	:	t=classOrInterfaceType[false]
	|	t=builtInType
	;

// The primitive types.
builtInType returns [JavaToken t]
    {t=null;}
    :   bVoid:"void"       {t = (JavaToken)bVoid;}
    |   bBoolean:"boolean" {t = (JavaToken)bBoolean;}
    |   bByte:"byte"       {t = (JavaToken)bByte;}
    |   bChar:"char"       {t = (JavaToken)bChar;}
    |   bShort:"short"     {t = (JavaToken)bShort;}
    |   bInt:"int"         {t = (JavaToken)bInt;}
    |   bFloat:"float"     {t = (JavaToken)bFloat;}
    |   bLong:"long"       {t = (JavaToken)bLong;}
    |   bDouble:"double"   {t = (JavaToken)bDouble;}
	;

// A (possibly-qualified) java identifier.  We start with the first IDENT
//   and expand its name by adding dots and following IDENTS
identifier returns [JavaToken t]
    {t=null;}
	:	id1:IDENT {t=(JavaToken)id1;}
        ( DOT^ 
          id2:IDENT
            {
             t.setText(t.getText() + "." + id2.getText());
			 t.setColumn(id2.getColumn());
            }
        )*
	;

// XR
// This is the special identifer rule used by package statements.  We will
//   keep track of the name in two parts: packageName and className
// There are three cases to consider:
//   1) a single IDENT:  This will be assigned to className; packageName
//      will be an empty string.  This means that we are importing a class
//      from the default package
//   2) more than one IDENT: for each succesive (DOT IDENT) pair we find, we
//      append the _last_ className to the package name.  The idea is that
//      the _last_ qualifier in a name must be a class name (or "*" -- handled
//      in #3).  Each time we see a new qualifier, we know all the previous
//      qualifiers are actually part of the packageName.  So we'll end up
//      with a package and class name
//   3) one or more IDENTs with DOT STAR at the end: We follow the above two
//      rules until we hit the DOT STAR at the end. Once we do, we know this is
//      an "import-on-demand" for a certain package name.  The entire name
//      is put into packageName, and className is set to null.
// We then take the class and package name and tell the symbol table about
//   the import statement
identifierStar
    {String className=""; String packageName="";}
	:	id:IDENT {className=id.getText();}
		( DOT^ id2:IDENT
             {packageName += "."+className; className = id2.getText();} )*
		( DOT^ STAR {packageName += "."+className; className = null;} )?

        {
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
	;

// modifiers for Java classes, interfaces, class/instance vars and methods
modifier
	:	"private"
	|	"public"
	|	"protected"
	|	"static"
	|	"transient"
	|	"final"
	|	"abstract"
	|	"native"
	|	"threadsafe"
	|	"synchronized"
//	|	"const"			// reserved word, but not valid
	|	"volatile"
	|	"strictfp"
	;

// Definition of a Java class
classDefinition![AST modifiers]
    {JavaToken superClass=null; JavaVector interfaces=null;}
	:	"class" id:IDENT
        // it _might_ have type paramaters
        (typeParameters)?
		// it _might_ have a superclass...
		superClass=sc:superClassClause
		// it might implement some interfaces...
		interfaces=ic:implementsClause

        // tell the symbol table about it
        // Note that defineClass pushes the class' scope,
        //   so we'll have to pop...
        {defineClass((JavaToken)id, superClass, interfaces);}

		// now parse the body of the class
		cb:classBlock
		{#classDefinition = #(#[CLASS_DEF,"CLASS_DEF"],
							   modifiers,id,sc,ic,cb);}

        // tell the symbol table that we are exiting a scope
        {popScope();}
	;

superClassClause! returns [JavaToken t]
    {t=null;}
	:	( "extends" t=classOrInterfaceType[false] )?
		//{#superClassClause = #(#[EXTENDS_CLAUSE,"EXTENDS_CLAUSE"],id);}
	;

// Definition of a Java Interface
interfaceDefinition![AST modifiers]
    {JavaVector superInterfaces = null;}
	:	"interface" id:IDENT // aha! an interface!
        // it _might_ have type paramaters
        (typeParameters)?
		// it might extend some other interfaces
		superInterfaces=ie:interfaceExtends

        // tell the symbol table about it!
        // Note that defineInterface pushes the interface scope, so
        //   we'll have to pop it...
        {defineInterface((JavaToken)id, superInterfaces);}

		// now parse the body of the interface (looks like a class...)
		cb:classBlock
		{#interfaceDefinition = #(#[INTERFACE_DEF,"INTERFACE_DEF"],
									modifiers,id,ie,cb);}

        // tell the symboltable that we are done in that scope
        {popScope();}
	;

typeParameters
{int currentLtLevel = 0;}
    :
        {currentLtLevel = ltCounter;}
        LT {ltCounter++;}
        typeParameter (COMMA typeParameter)*
        (typeArgumentsEnd)?
        // make sure we have gobbled up enough '>' characters
        // if we are at the "top level" of nested typeArgument productions
        {(currentLtLevel != 0) || ltCounter == currentLtLevel}?
    ;

typeParameter:
        IDENT
        (   // I'm pretty sure Antlr generates the right thing here:
            options{generateAmbigWarnings=false;}:
            "extends" classOrInterfaceType[false]
            (BAND classOrInterfaceType[false])*
        )?
    ;
// This is the body of a class.  You can have fields and extra semicolons,
// That's about it (until you see what a field is...)
classBlock
	:	LCURLY!
			( field | SEMI! )*
		RCURLY!
		{#classBlock = #([OBJBLOCK, "OBJBLOCK"], #classBlock);}
	;

// An interface can extend several other interfaces...
interfaceExtends returns [JavaVector supers]
    {JavaToken id; supers = new JavaVector();}
	:	(
		e:"extends"! id=classOrInterfaceType[false] {supers.addElement(dummyClass(id));}
		( COMMA! id=classOrInterfaceType[false]     {supers.addElement(dummyClass(id));} )*
		)?
		{#interfaceExtends = #(#[EXTENDS_CLAUSE,"EXTENDS_CLAUSE"],
							#interfaceExtends);}
	;

// A class can implement several interfaces...
implementsClause returns [JavaVector inters]
    {inters = new JavaVector(); JavaToken id;}
	:	(
			i:"implements"! id=classOrInterfaceType[false] {inters.addElement(dummyClass(id));}
              ( COMMA! id=classOrInterfaceType[false]      {inters.addElement(dummyClass(id));} )*
		)?
		{#implementsClause = #(#[IMPLEMENTS_CLAUSE,"IMPLEMENTS_CLAUSE"],
								 #implementsClause);}
	;

// Now the various things that can be defined inside a class or interface...
// Note that not all of these are really valid in an interface (constructors,
//   for example), and if this grammar were used for a compiler there would
//   need to be some semantic checks to make sure we're doing the right thing...
field!
    {JavaToken type; JavaVector exceptions=null;} 
	:	// method, constructor, or variable declaration
		mods:modifiers
		(	h:ctorHead s:constructorBody // constructor
			{#field = #(#[CTOR_DEF,"CTOR_DEF"], mods, h, s);}

		|	cd:classDefinition[#mods]       // inner class
			{#field = #cd;}

		|	id:interfaceDefinition[#mods]   // inner interface
			{#field = #id;}

		|	// A generic method has the typeParameters before the return type.
            // This is not allowed for variable definitions, but this production
            // allows it, a semantic check could be used if you wanted.
            (typeParameters)? type=t:typeSpec[false]  // method or variable declaration(s)
			(	method:IDENT  // the name of the method

                // tell the symbol table about it.  Note that this signals that 
                // we are in a method header so we handle parameters appropriately
                {defineMethod((JavaToken)method, type);}

				// parse the formal parameter declarations.
				LPAREN! param:parameterDeclarationList RPAREN!

				rt:declaratorBrackets[#t]

				// get the list of exceptions that this method is declared to throw
				(exceptions=tc:throwsClause)?

                // tell the symbol table we are done with the method header. Note that
                // this will tell the symbol table to handle variables normally
                {endMethodHead(exceptions);}

				( s2:compoundStatement[BODY] | SEMI {popScope();})
				{#field = #(#[METHOD_DEF,"METHOD_DEF"],
						     mods,
							 #(#[TYPE,"TYPE"],rt),
							 method,
							 param,
							 tc,
							 s2);}
			|	v:variableDefinitions[#mods,#t,type] SEMI
//				{#field = #(#[VARIABLE_DEF,"VARIABLE_DEF"], v);}
				{#field = #v;}
			)
		)

    // "static { ... }" class initializer
	|	"static" s3:compoundStatement[CLASS_INIT]
		{#field = #(#[STATIC_INIT,"STATIC_INIT"], s3);}

    // "{ ... }" instance initializer
	|	s4:compoundStatement[INSTANCE_INIT]
		{#field = #(#[INSTANCE_INIT,"INSTANCE_INIT"], s4);}
	;

constructorBody
    :   lc:LCURLY^ {#lc.setType(SLIST);}
            ( options { greedy=true; } : explicitConstructorInvocation)?
            (statement)*
            
            // tell the symbol table we're leaving a scope
            {popScope();}
        RCURLY!
    ;

/** Catch obvious constructor calls, but not the expr.super(...) calls */
explicitConstructorInvocation
    :   "this"! lp1:LPAREN^ argList RPAREN! SEMI!
		{#lp1.setType(CTOR_CALL);}
    |   "super"! lp2:LPAREN^ argList RPAREN! SEMI!
		{#lp2.setType(SUPER_CTOR_CALL);}
    ;

variableDefinitions[AST mods, AST t, JavaToken type]
	:	variableDeclarator[getASTFactory().dupTree(mods),
						   getASTFactory().dupTree(t),
						   type]
		(	COMMA!
			variableDeclarator[getASTFactory().dupTree(mods),
							   getASTFactory().dupTree(t),
							   type]
		)*
	;

/** Declaration of a variable.  This can be a class/instance variable,
 *   or a local variable in a method
 * It can also include possible initialization.
 */
variableDeclarator![AST mods, AST t, JavaToken type]
	:	id:IDENT d:declaratorBrackets[t] v:varInitializer
        {defineVar((JavaToken)id, type);}
		{#variableDeclarator = #(#[VARIABLE_DEF,"VARIABLE_DEF"], mods, #(#[TYPE,"TYPE"],d), id, v);}
	;

declaratorBrackets[AST typ]
	:	{#declaratorBrackets=typ;}
		(lb:LBRACK^ {#lb.setType(ARRAY_DECLARATOR);} RBRACK!)*
	;

varInitializer
	:	( ASSIGN^ initializer )?
	;

// This is an initializer used to set up an array.
arrayInitializer
	:	lc:LCURLY^ {#lc.setType(ARRAY_INIT);}
			(	initializer
				(
					// CONFLICT: does a COMMA after an initializer start a new
					//           initializer or start the option ',' at end?
					//           ANTLR generates proper code by matching
					//			 the comma as soon as possible.
					options {
						warnWhenFollowAmbig = false;
					}
				:
					COMMA! initializer
				)*
				(COMMA!)?
			)?
		RCURLY!
	;


// The two "things" that can initialize an array element are an expression
//   and another (nested) array initializer.
initializer
	:	expression
	|	arrayInitializer
	;

// This is the header of a method.  It includes the name and parameters
//   for the method.
//   This also watches for a list of exception classes in a "throws" clause.
ctorHead
    {JavaToken id; JavaVector exceptions = null;}
	:	method:IDENT  // the name of the method

        {defineMethod((JavaToken)method, null);}

		// parse the formal parameter declarations.
		LPAREN! parameterDeclarationList RPAREN!

		// get the list of exceptions that this method is declared to throw
		(exceptions=throwsClause)?

        { endMethodHead(exceptions); }   
	;

// This is a list of exception classes that the method is declared to throw
throwsClause returns [JavaVector exceptions]
    {JavaToken id; exceptions = new JavaVector();}
	:	"throws"^ id=identifier {exceptions.addElement(dummyClass(id));}
         ( COMMA! id=identifier {exceptions.addElement(dummyClass(id));} )*
	;


// A list of formal parameters
parameterDeclarationList
	:	( parameterDeclaration ( COMMA! parameterDeclaration )* )?
		{#parameterDeclarationList = #(#[PARAMETERS,"PARAMETERS"],
									#parameterDeclarationList);}
	;

// A formal parameter.
parameterDeclaration!
    {JavaToken type;}
	:	pm:parameterModifier type=t:typeSpec[false] id:IDENT
		pd:declaratorBrackets[#t]
		{#parameterDeclaration = #(#[PARAMETER_DEF,"PARAMETER_DEF"],
									pm, #([TYPE,"TYPE"],pd), id);}
        {defineVar((JavaToken)id, type);}
	;

parameterModifier
	:	(f:"final")?
		{#parameterModifier = #(#[MODIFIERS,"MODIFIERS"], f);}
	;

// Compound statement.  This is used in many contexts:
//   Inside a class definition prefixed with "static":
//      it is a class initializer, and is passed here with scopeType CLASS_INIT
//   Inside a class definition without "static":
//      it is an instance initializer, passed here with scopeType INSTANCE_INIT
//   As the body of a method, passed here with scopeType BODY
//   As a completely indepdent braced block of code inside a method
//      it starts a new scope for variable definitions, and is passed here
//      with NEW_SCOPE as its scopeType

compoundStatement[int scopeType]
	:	lc:LCURLY^ {#lc.setType(SLIST);}
            {   // based on the scopeType we are processing
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
 
			// include the (possibly-empty) list of statements
			(statement)*
            
            // tell the symbol table we're leaving a scope
            {popScope();}
		RCURLY!
	;


statement
    {int count = -1;} // used for parameter counts in method calls
    
	// A list of statements in curly braces -- start a new scope!
	:	compoundStatement[NEW_SCOPE]

	// declarations are ambiguous with "ID DOT" relative to expression
	// statements.  Must backtrack to be sure.  Could use a semantic
	// predicate to test symbol table to see what the type was coming
	// up, but that's pretty hard without a symbol table ;)
	|	(declaration)=> declaration SEMI!

	// An expression statement.  This could be a method call,
	// assignment statement, or any other expression evaluated for
	// side-effects.
	|	expression SEMI!

	// class definition
	|	m:modifiers! classDefinition[#m]

	// Attach a label to the front of a statement
	|	id:IDENT c:COLON^ {#c.setType(LABELED_STAT);} statement {defineLabel((JavaToken)id);}

	// If-else statement
	|	"if"^ LPAREN! expression RPAREN! statement
		(
			// CONFLICT: the old "dangling-else" problem...
			//           ANTLR generates proper code matching
			//			 as soon as possible.  Hush warning.
			options {
				warnWhenFollowAmbig = false;
			}
		:
			"else"! statement
		)?

	// For statement
	|	"for"^
			LPAREN!
				forInit SEMI!   // initializer
				forCond	SEMI!   // condition test
				forIter         // updater
			RPAREN!
			statement                     // statement to loop over

	// While statement
	|	"while"^ LPAREN! expression RPAREN! statement

	// do-while statement
	|	"do"^ statement "while"! LPAREN! expression RPAREN! SEMI!

	// get out of a loop (or switch)
	|	"break"^ (bid:IDENT {reference((JavaToken)bid);})? SEMI!

	// do next iteration of a loop
	|	"continue"^ (cid:IDENT {reference((JavaToken)cid);})? SEMI!

	// Return an expression
	|	"return"^ (expression)? SEMI!

	// switch/case statement
	|	"switch"^ LPAREN! expression RPAREN! LCURLY!
			( casesGroup )*
		RCURLY!

	// exception try-catch block
	|	tryBlock

	// throw an exception
	|	"throw"^ expression SEMI!

	// synchronize a statement
	|	"synchronized"^ LPAREN! expression RPAREN! compoundStatement[NEW_SCOPE]

	// asserts (uncomment if you want 1.4 compatibility)
	|	"assert"^ expression ( COLON! expression )? SEMI!

	// empty statement
	|	s:SEMI {#s.setType(EMPTY_STAT);}
	;

casesGroup
	:	(	// CONFLICT: to which case group do the statements bind?
			//           ANTLR generates proper code: it groups the
			//           many "case"/"default" labels together then
			//           follows them with the statements
			options {
				greedy = true;
			}
			:
			aCase
		)+
		caseSList
		{#casesGroup = #([CASE_GROUP, "CASE_GROUP"], #casesGroup);}
	;

aCase
	:	("case"^ expression | "default") COLON!
	;

caseSList
	:	(statement)*
		{#caseSList = #(#[SLIST,"SLIST"],#caseSList);}
	;

// The initializer for a for loop
forInit
    {int count = -1;}
		// if it looks like a declaration, it is
	:	(	(declaration)=> declaration
		// otherwise it could be an expression list...
		|	count=expressionList
		)?
		{#forInit = #(#[FOR_INIT,"FOR_INIT"],#forInit);}
	;

forCond
	:	(expression)?
		{#forCond = #(#[FOR_CONDITION,"FOR_CONDITION"],#forCond);}
	;

forIter
	:	(expressionList)?
		{#forIter = #(#[FOR_ITERATOR,"FOR_ITERATOR"],#forIter);}
	;

// an exception handler try/catch block
tryBlock
	:	"try"^ compoundStatement[NEW_SCOPE]
		(handler)*
		( finallyClause )?
	;

finallyClause
	:	"finally"^ compoundStatement[NEW_SCOPE]
	;

// an exception handler
handler
	:	"catch"^ LPAREN! parameterDeclaration RPAREN! compoundStatement[NEW_SCOPE]
	;


// expressions
// Note that most of these expressions follow the pattern
//   thisLevelExpression :
//       nextHigherPrecedenceExpression
//           (OPERATOR nextHigherPrecedenceExpression)*
// which is a standard recursive definition for a parsing an expression.
// The operators in java have the following precedences:
//    lowest  (13)  = *= /= %= += -= <<= >>= >>>= &= ^= |=
//            (12)  ?:
//            (11)  ||
//            (10)  &&
//            ( 9)  |
//            ( 8)  ^
//            ( 7)  &
//            ( 6)  == !=
//            ( 5)  < <= > >=
//            ( 4)  << >>
//            ( 3)  +(binary) -(binary)
//            ( 2)  * / %
//            ( 1)  ++ -- +(unary) -(unary)  ~  !  (type)
//                  []   () (method call)  . (dot -- identifier qualification)
//                  new   ()  (explicit parenthesis)
//
// the last two are not usually on a precedence chart; I put them in
// to point out that new has a higher precedence than '.', so you
// can validy use
//     new Frame().show()
//
// Note that the above precedence levels map to the rules below...
// Once you have a precedence chart, writing the appropriate rules as below
//   is usually very straightfoward



// the mother of all expressions
expression
	:	assignmentExpression
		{#expression = #(#[EXPR,"EXPR"],#expression);}
	;


// This is a list of expressions.
expressionList returns [int count]
    {count=1;}
	:	expression (COMMA! expression {count++;})*
		{#expressionList = #(#[ELIST,"ELIST"], expressionList);}
	;


// assignment expression (level 13)
assignmentExpression
	:	conditionalExpression
		(	(	ASSIGN^
            |   PLUS_ASSIGN^
            |   MINUS_ASSIGN^
            |   STAR_ASSIGN^
            |   DIV_ASSIGN^
            |   MOD_ASSIGN^
            |   SR_ASSIGN^
            |   BSR_ASSIGN^
            |   SL_ASSIGN^
            |   BAND_ASSIGN^
            |   BXOR_ASSIGN^
            |   BOR_ASSIGN^
            )
			assignmentExpression
		)?
	;


// conditional test (level 12)
conditionalExpression
	:	logicalOrExpression
		( QUESTION^ assignmentExpression COLON! conditionalExpression )?
	;


// logical or (||)  (level 11)
logicalOrExpression
	:	logicalAndExpression (LOR^ logicalAndExpression)*
	;


// logical and (&&)  (level 10)
logicalAndExpression
	:	inclusiveOrExpression (LAND^ inclusiveOrExpression)*
	;


// bitwise or non-short-circuiting or (|)  (level 9)
inclusiveOrExpression
	:	exclusiveOrExpression (BOR^ exclusiveOrExpression)*
	;


// exclusive or (^)  (level 8)
exclusiveOrExpression
	:	andExpression (BXOR^ andExpression)*
	;


// bitwise or non-short-circuiting and (&)  (level 7)
andExpression
	:	equalityExpression (BAND^ equalityExpression)*
	;


// equality/inequality (==/!=) (level 6)
equalityExpression
	:	relationalExpression ((NOT_EQUAL^ | EQUAL^) relationalExpression)*
	;


// boolean relational expressions (level 5)
relationalExpression
	:	shiftExpression
		(	(	(	LT^
				|	GT^
				|	LE^
				|	GE^
				)
				shiftExpression
			)*
		|	"instanceof"^ typeSpec[true]
		)
	;


// bit shift expressions (level 4)
shiftExpression
	:	additiveExpression ((SL^ | SR^ | BSR^) additiveExpression)*
	;


// binary addition/subtraction (level 3)
additiveExpression
	:	multiplicativeExpression ((PLUS^ | MINUS^) multiplicativeExpression)*
	;


// multiplication/division/modulo (level 2)
multiplicativeExpression
	:	unaryExpression ((STAR^ | DIV^ | MOD^ ) unaryExpression)*
	;

unaryExpression
	:	INC^ unaryExpression
	|	DEC^ unaryExpression
	|	MINUS^ {#MINUS.setType(UNARY_MINUS);} unaryExpression
	|	PLUS^  {#PLUS.setType(UNARY_PLUS);} unaryExpression
	|	unaryExpressionNotPlusMinus
	;

unaryExpressionNotPlusMinus
    { JavaToken t; }
	:	BNOT^ unaryExpression
	|	LNOT^ unaryExpression

	|	(	// subrule allows option to shut off warnings
			options {
				// "(int" ambig with postfixExpr due to lack of sequence
				// info in linear approximate LL(k).  It's ok.  Shut up.
				generateAmbigWarnings=false;
			}
		:	// If typecast is built in type, must be numeric operand
			// Also, no reason to backtrack if type keyword like int, float...
			lpb:LPAREN^ {#lpb.setType(TYPECAST);} builtInTypeSpec[true] RPAREN!
			unaryExpression

			// Have to backtrack to see if operator follows.  If no operator
			// follows, it's a typecast.  No semantic checking needed to parse.
			// if it _looks_ like a cast, it _is_ a cast; else it's a "(expr)"
		|	(LPAREN classTypeSpec[true] RPAREN unaryExpressionNotPlusMinus)=>
			lp:LPAREN^ {#lp.setType(TYPECAST);} t=classTypeSpec[true] RPAREN!
			unaryExpressionNotPlusMinus
            { reference(t); }

		|	postfixExpression
		)
	;

// qualified names, array expressions, method invocation, post inc/dec
postfixExpression
    {JavaToken t; JavaToken temp; int count=-1; }
	:   t=primaryExpression // start with a primary

		(
            DOT^ id:IDENT {if (t!=null)
		             { 
			          temp = new JavaToken(t);
			          reference(temp);
			          t.setText(t.getText()+"."+id.getText());
			          t.setColumn(id.getColumn());
			         } 
                            }
			(	lp:LPAREN^ {#lp.setType(METHOD_CALL);}
				count=argList
				RPAREN!
                {
                    if (t!=null)
        		    {
                    t.setParamCount(count);
		            }
                } 
			)?
		|	DOT^ "this"

		|	DOT^ "super"
            (   // (new Outer()).super()  (create enclosing instance)
                lp3:LPAREN^ argList RPAREN!
                {#lp3.setType(SUPER_CTOR_CALL);}
			|   DOT^ IDENT
                (	lps:LPAREN^ {#lps.setType(METHOD_CALL);}
                    count=argList
                    RPAREN!
                    {
                        if (t!=null)
        	    	    {
                        t.setParamCount(count);
		                }
                    } 
                )?
            )
		|	DOT^ newExpression
		|	lb:LBRACK^ {#lb.setType(INDEX_OP);} expression RBRACK!
		)*

        // if we have a reference, tell the symbol table
        { 
          if (t != null)
	      {	
            reference(t);
          }
	    }
	    
		(   // possibly add on a post-increment or post-decrement.
            // allows INC/DEC on too much, but semantics can check
			in:INC^ {#in.setType(POST_INC);}
	 	|	de:DEC^ {#de.setType(POST_DEC);}
		)?
 	;

// the basic element of an expression
primaryExpression returns [JavaToken t]
    {t=null;}
	:	t=identPrimary ( options {greedy=true;} : DOT^ "class" )?
    |   constant
	|	"true"
	|	"false"
	|	"null"
    |   t=newExpression
	|	th:"this" {t = (JavaToken)th; setNearestClassScope();}
	|	s:"super" {t = (JavaToken)s;}
	|	LPAREN! assignmentExpression RPAREN!
		// look for int.class and int[].class
	|	builtInType
		( lbt:LBRACK^ {#lbt.setType(ARRAY_DECLARATOR);} RBRACK! )*
		DOT^ "class"
	;

/** Match a, a.b.c refs, a.b.c(...) refs, a.b.c[], a.b.c[].class,
 *  and a.b.c.class refs.  Also this(...) and super(...).  Match
 *  this or super.
 */
identPrimary returns [JavaToken t]
    {JavaToken temp;t=null;int count=-1;}
	:	id:IDENT {t = (JavaToken)id;}
		(
            options {
				// .ident could match here or in postfixExpression.
				// We do want to match here.  Turn off warning.
				greedy=true;
			}
		:	DOT^ id2:IDENT {if (t!=null)
		             { 
			          temp = new JavaToken(t);
			          reference(temp);
			          t.setText(t.getText()+"."+id2.getText());
			          t.setColumn(id2.getColumn());
			         } 
                            }
		)*
		(
            options {
				// ARRAY_DECLARATOR here conflicts with INDEX_OP in
				// postfixExpression on LBRACK RBRACK.
				// We want to match [] here, so greedy.  This overcomes
                // limitation of linear approximate lookahead.
				greedy=true;
		    }
		:   ( lp:LPAREN^ {#lp.setType(METHOD_CALL);} 
		      count=argList 
		      RPAREN! 
                {
                    if (t!=null)
        		    {
                    t.setParamCount(count);
		            }
                } 
		    )
		|	( options {greedy=true;} :
              lbc:LBRACK^ {#lbc.setType(ARRAY_DECLARATOR);} RBRACK!
            )+
		)?
    ;

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
newExpression returns [JavaToken t]
    {t=null; int count=-1;}
	:	"new"^ t=type
		(	LPAREN! count=argList
                {
                    // t.setText(t.getText()+".~constructor~");
                    t.setText(t.getText()+"."+t.getText());
                    t.setParamCount(count);
                }
             RPAREN! (classBlock)?

			//java 1.1
			// Note: This will allow bad constructs like
			//    new int[4][][3] {exp,exp}.
			//    There needs to be a semantic check here...
			// to make sure:
			//   a) [ expr ] and [ ] are not mixed
			//   b) [ expr ] and an init are not used together

		|	newArrayDeclarator (arrayInitializer)?
		)
	;

argList returns [int count]
    {count=0;}
	:	(	count=expressionList
		|	/*nothing*/
			{#argList = #[ELIST,"ELIST"];}
		)
	;

newArrayDeclarator
	:	(
			// CONFLICT:
			// newExpression is a primaryExpression which can be
			// followed by an array index reference.  This is ok,
			// as the generated code will stay in this loop as
			// long as it sees an LBRACK (proper behavior)
			options {
				warnWhenFollowAmbig = false;
			}
		:
			lb:LBRACK^ {#lb.setType(ARRAY_DECLARATOR);}
				(expression)?
			RBRACK!
		)+
	;

constant
	:	NUM_INT
	|	CHAR_LITERAL
	|	STRING_LITERAL
	|	NUM_FLOAT
	|	NUM_LONG
	|	NUM_DOUBLE
	;


//----------------------------------------------------------------------------
// The Java scanner
//----------------------------------------------------------------------------
{
  import org.apache.alexandria.javasrc.symtab.SymbolTable;
}
class JavaLexer extends Lexer;

options {
	exportVocab=Java;      // call the vocabulary "Java"
	testLiterals=false;    // don't automatically test for literals
	k=4;                   // four characters of lookahead
	charVocabulary='\u0003'..'\uFFFF';
	// without inlining some bitset tests, couldn't do unicode;
	// I need to make ANTLR generate smaller bitsets; see
	// bottom of JavaLexer.java
	codeGenBitsetTestThreshold=20;
}

/* --- XR: Start Xref additions --- */
{
  protected SymbolTable symbolTable;
  protected int ccStart;
  protected int clStart;

  // Tell the parser which symbol table to use
  public void setSymbolTable(SymbolTable symbolTable)
  {
    this.symbolTable = symbolTable;
  }

  public void defineComment(int line,int column, String text)
  {
    symbolTable.defineComment(line,column, text);
  } 

  public void defineLiteral(int line,int column, String text)
  {
    symbolTable.defineLiteral(line,column, text);
  }
  
  // Test the token text against the literals table
  // Override this method to perform a different literals test
  public int testLiteralsTable(int ttype) {
               hashString.setBuffer(text.getBuffer(), text.length());
                Integer literalsIndex = (Integer)literals.get(hashString);
                if (literalsIndex != null) {
                        symbolTable.defineKeyword(getLine(),getColumn()-text.length(), new String(text.getBuffer(),0,text.length()));
                        ttype = literalsIndex.intValue();
                }
                return ttype;
  }   
}
/* --- XR: End Xref additions --- */

// OPERATORS
QUESTION		:	'?'		;
LPAREN			:	'('		;
RPAREN			:	')'		;
LBRACK			:	'['		;
RBRACK			:	']'		;
LCURLY			:	'{'		;
RCURLY			:	'}'		;
COLON			:	':'		;
COMMA			:	','		;
//DOT			:	'.'		;
ASSIGN			:	'='		;
EQUAL			:	"=="	;
LNOT			:	'!'		;
BNOT			:	'~'		;
NOT_EQUAL		:	"!="	;
DIV				:	'/'		;
DIV_ASSIGN		:	"/="	;
PLUS			:	'+'		;
PLUS_ASSIGN		:	"+="	;
INC				:	"++"	;
MINUS			:	'-'		;
MINUS_ASSIGN	:	"-="	;
DEC				:	"--"	;
STAR			:	'*'		;
STAR_ASSIGN		:	"*="	;
MOD				:	'%'		;
MOD_ASSIGN		:	"%="	;
SR				:	">>"	;
SR_ASSIGN		:	">>="	;
BSR				:	">>>"	;
BSR_ASSIGN		:	">>>="	;
GE				:	">="	;
GT				:	">"		;
SL				:	"<<"	;
SL_ASSIGN		:	"<<="	;
LE				:	"<="	;
LT				:	'<'		;
BXOR			:	'^'		;
BXOR_ASSIGN		:	"^="	;
BOR				:	'|'		;
BOR_ASSIGN		:	"|="	;
LOR				:	"||"	;
BAND			:	'&'		;
BAND_ASSIGN		:	"&="	;
LAND			:	"&&"	;
SEMI			:	';'		;


// Whitespace -- ignored
WS	:	(	' '
		|	'\t'
		|	'\f'
			// handle newlines
		|	(	options {generateAmbigWarnings=false;}
			:	"\r\n"  // Evil DOS
			|	'\r'    // Macintosh
			|	'\n'    // Unix (the right way)
			)
			{ newline(); }
		)+
		{ _ttype = Token.SKIP; }
	;

// Single-line comments
SL_COMMENT
	:	{ clStart=getLine(); ccStart=getColumn(); } "//"
		(~('\n'|'\r'))* 
		{defineComment(clStart,ccStart, getText());}
		('\n'|'\r'('\n')?)
		{$setType(Token.SKIP); newline();}
	;

// multiple-line comments
ML_COMMENT
	:	{ clStart=getLine(); ccStart=getColumn(); } "/*"
		(	/*	'\r' '\n' can be matched in one alternative or by matching
				'\r' in one iteration and '\n' in another.  I am trying to
				handle any flavor of newline that comes in, but the language
				that allows both "\r\n" and "\r" and "\n" to all be valid
				newline is ambiguous.  Consequently, the resulting grammar
				must be ambiguous.  I'm shutting this warning off.
			 */
			options {
				generateAmbigWarnings=false;
			}
		:
			{ LA(2)!='/' }? '*'
		|	'\r' '\n'		{newline();}
		|	'\r'			{newline();}
		|	'\n'			{newline();}
		|	~('*'|'\n'|'\r')
		)*
		"*/"
		{defineComment(clStart,ccStart,getText());}
		{$setType(Token.SKIP);}
	;


// character literals
CHAR_LITERAL
	:	'\'' ( ESC | ~'\'' ) '\''
	;

// string literals
STRING_LITERAL
	:	{ clStart=getLine(); ccStart=getColumn(); } '"' 
	    (ESC|~('"'|'\\'))* 
	    '"'
	    {defineLiteral(clStart,ccStart,getText());}
	;


// escape sequence -- note that this is protected; it can only be called
//   from another lexer rule -- it will not ever directly return a token to
//   the parser
// There are various ambiguities hushed in this rule.  The optional
// '0'...'9' digit matches should be matched here rather than letting
// them go back to STRING_LITERAL to be matched.  ANTLR does the
// right thing by matching immediately; hence, it's ok to shut off
// the FOLLOW ambig warnings.
protected
ESC
	:	'\\'
		(	'n'
		|	'r'
		|	't'
		|	'b'
		|	'f'
		|	'"'
		|	'\''
		|	'\\'
		|	('u')+ HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
		|	'0'..'3'
			(
				options {
					warnWhenFollowAmbig = false;
				}
			:	'0'..'7'
				(
					options {
						warnWhenFollowAmbig = false;
					}
				:	'0'..'7'
				)?
			)?
		|	'4'..'7'
			(
				options {
					warnWhenFollowAmbig = false;
				}
			:	'0'..'7'
			)?
		)
	;


// hexadecimal digit (again, note it's protected!)
protected
HEX_DIGIT
	:	('0'..'9'|'A'..'F'|'a'..'f')
	;


// a dummy rule to force vocabulary to be all characters (except special
//   ones that ANTLR uses internally (0 to 2)
protected
VOCAB
	:	'\3'..'\377'
	;


// an identifier.  Note that testLiterals is set to true!  This means
// that after we match the rule, we look in the literals table to see
// if it's a literal or really an identifer
IDENT
	options {testLiterals=true;}
	:	('a'..'z'|'A'..'Z'|'_'|'$') ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$')*
	;


// a numeric literal
NUM_INT
	{boolean isDecimal=false; Token t=null;}
    :   '.' {_ttype = DOT;}
            (	('0'..'9')+ (EXPONENT)? (f1:FLOAT_SUFFIX {t=f1;})?
                {
				if (t != null && t.getText().toUpperCase().indexOf('F')>=0) {
                	_ttype = NUM_FLOAT;
				}
				else {
                	_ttype = NUM_DOUBLE; // assume double
				}
				}
            )?

	|	(	'0' {isDecimal = true;} // special case for just '0'
			(	('x'|'X')
				(											// hex
					// the 'e'|'E' and float suffix stuff look
					// like hex digits, hence the (...)+ doesn't
					// know when to stop: ambig.  ANTLR resolves
					// it correctly by matching immediately.  It
					// is therefor ok to hush warning.
					options {
						warnWhenFollowAmbig=false;
					}
				:	HEX_DIGIT
				)+
			|	('0'..'7')+									// octal
			)?
		|	('1'..'9') ('0'..'9')*  {isDecimal=true;}		// non-zero decimal
		)
		(	('l'|'L') { _ttype = NUM_LONG; }

		// only check to see if it's a float if looks like decimal so far
		|	{isDecimal}?
            (   '.' ('0'..'9')* (EXPONENT)? (f2:FLOAT_SUFFIX {t=f2;})?
            |   EXPONENT (f3:FLOAT_SUFFIX {t=f3;})?
            |   f4:FLOAT_SUFFIX {t=f4;}
            )
            {
			if (t != null && t.getText().toUpperCase() .indexOf('F') >= 0) {
                _ttype = NUM_FLOAT;
			}
            else {
	           	_ttype = NUM_DOUBLE; // assume double
			}
			}
        )?
	;


// a couple protected methods to assist in matching floating point numbers
protected
EXPONENT
	:	('e'|'E') ('+'|'-')? ('0'..'9')+
	;


protected
FLOAT_SUFFIX
	:	'f'|'F'|'d'|'D'
	;

