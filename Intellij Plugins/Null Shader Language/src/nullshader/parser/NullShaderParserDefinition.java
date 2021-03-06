package nullshader.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import nullshader.NullShaderLanguage;
import nullshader.parser.NullShaderLexerAdapter;
import nullshader.parser.NullShaderParser;
import nullshader.parser.psi.NullShaderFile;
import nullshader.parser.psi.NullShaderTypes;
import org.jetbrains.annotations.NotNull;

public class NullShaderParserDefinition implements ParserDefinition {

	public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
	public static final TokenSet COMMENT = TokenSet.create(NullShaderTypes.COMMENT);


	public static final IFileElementType FILE = new IFileElementType(Language.findInstance(NullShaderLanguage.class));

	/**
	 * Returns the lexer for lexing files in the specified project. This lexer does not need to support incremental relexing - it is always
	 * called for the entire file.
	 *
	 * @param project the project to which the lexer is connected.
	 * @return the lexer instance.
	 */
	@NotNull
	@Override
	public Lexer createLexer(Project project) {
		return new NullShaderLexerAdapter();
	}

	/**
	 * Returns the parser for parsing files in the specified project.
	 *
	 * @param project the project to which the parser is connected.
	 * @return the parser instance.
	 */
	@Override
	public PsiParser createParser(Project project) {
		return new NullShaderParser();
	}

	/**
	 * Returns the element type of the node describing a file in the specified language.
	 *
	 * @return the file node element type.
	 */
	@Override
	public IFileElementType getFileNodeType() {
		return FILE;
	}

	/**
	 * Returns the set of token types which are treated as whitespace by the PSI builder.
	 * Tokens of those types are automatically skipped by PsiBuilder. Whitespace elements
	 * on the bounds of nodes built by PsiBuilder are automatically excluded from the text
	 * range of the nodes.
	 * <p><strong>It is strongly advised you return TokenSet that only contains {@link TokenType#WHITE_SPACE},
	 * which is suitable for all the languages unless you really need to use special whitespace token</strong>
	 *
	 * @return the set of whitespace token types.
	 */
	@NotNull
	@Override
	public TokenSet getWhitespaceTokens() {
		return WHITE_SPACES;
	}

	/**
	 * Returns the set of token types which are treated as comments by the PSI builder.
	 * Tokens of those types are automatically skipped by PsiBuilder. Also, To Do patterns
	 * are searched in the text of tokens of those types.
	 *
	 * @return the set of comment token types.
	 */
	@NotNull
	@Override
	public TokenSet getCommentTokens() {
		return COMMENT;
	}

	/**
	 * Returns the set of element types which are treated as string literals. "Search in strings"
	 * option in refactorings is applied to the contents of such tokens.
	 *
	 * @return the set of string literal element types.
	 */
	@NotNull
	@Override
	public TokenSet getStringLiteralElements() {
		return TokenSet.EMPTY;
	}

	/**
	 * Creates a PSI element for the specified AST node. The AST tree is a simple, semantic-free
	 * tree of AST nodes which is built during the PsiBuilder parsing pass. The PSI tree is built
	 * over the AST tree and includes elements of different types for different language constructs.
	 * <p>
	 * !!!WARNING!!! PSI element types should be unambiguously determined by AST node element types.
	 * You can not produce different PSI elements from AST nodes of the same types (e.g. based on AST node content).
	 * Typically, your code should be as simple as that:
	 * <code>
	 * if (node.getElementType == MY_ELEMENT_TYPE) {
	 * return new MyPsiElement(node);
	 * }
	 * </code>
	 *
	 * @param node the node for which the PSI element should be returned.
	 * @return the PSI element matching the element type of the AST node.
	 */
	@NotNull
	@Override
	public PsiElement createElement(ASTNode node) {
		return NullShaderTypes.Factory.createElement(node);
	}

	/**
	 * Creates a PSI element for the specified virtual file.
	 *
	 * @param viewProvider virtual file.
	 * @return the PSI file element.
	 */
	@Override
	public PsiFile createFile(FileViewProvider viewProvider) {
		return new NullShaderFile(viewProvider);
	}

	/**
	 * Checks if the specified two token types need to be separated by a space according to the language grammar.
	 * For example, in Java two keywords are always separated by a space; a keyword and an opening parenthesis may
	 * be separated or not separated. This is used for automatic whitespace insertion during AST modification operations.
	 *
	 * @param left  the first token to check.
	 * @param right the second token to check.
	 * @return the spacing requirements.
	 * @since 6.0
	 */
	@Override
	public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
		return SpaceRequirements.MAY;
	}
}
