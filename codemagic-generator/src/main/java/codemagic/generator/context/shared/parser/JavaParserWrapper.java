package codemagic.generator.context.shared.parser;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import com.floreysoft.jmte.Engine;
import com.github.javaparser.ASTHelper;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.base.Verify;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import grain.util.ClassLoadUtil;

public class JavaParserWrapper {

	private final CompilationUnit cu;

	public JavaParserWrapper(
			final String templateName, 
			final Class<?> loader, 
			final Map<String, Object> model,
			final Engine engine) {
		
		final String template = ClassLoadUtil.loadFile(loader, templateName);
		
		final String transformedJava = engine.transform(template, model);
		
		this.cu = loadCU(transformedJava);
	}

	public JavaParserWrapper(final String fullJavaFileName, Class<?> loader) {
		try {
			
			final String content = Files.toString(new File(fullJavaFileName), Charset.defaultCharset());
			this.cu = loadCU(content);
			
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	public void addMethod(JavaParserWrapper methodParser, final String methodToBeAddedName) {
		
		final Optional<TypeDeclaration> type = findFirstType();
		Verify.verify(type.isPresent(), "Cannot add the method %s, because the target java file has not type delcared within", methodToBeAddedName);

		final Optional<MethodDeclaration> saveOrUpdateMethod = methodParser.findMethod(methodToBeAddedName);
		Verify.verify(saveOrUpdateMethod.isPresent(), "The method %s not found", methodToBeAddedName);

		// Remove older if exists
		final Optional<MethodDeclaration> _foundMethod = findMethod(methodToBeAddedName);
		if (_foundMethod.isPresent()) {
			type.get().getMembers().remove( _foundMethod.get() );
		}
		
		// Add the method
		ASTHelper.addMember(type.get(), saveOrUpdateMethod.get());
	}

	/**
	 * 
	 * @param other
	 *            The {@link JavaParserWrapper} whose import declaration
	 *            will be imported. The duplciation is removed.
	 */
	public void addImport(final JavaParserWrapper other) {
		
		final List<ImportDeclaration> myImports = cu.getImports();
		
		final List<ImportDeclaration> otherImports = other.getCompilationUnit().getImports();

		final Map<String, ImportDeclaration> mergedImports = Maps.newHashMap();

		// Merge and remove duplicated imports
		for (final ImportDeclaration decl : Iterables.concat(myImports, otherImports)) {
			mergedImports.put(decl.getName().toString(), decl);
		}

		// Apply merged imports
		cu.setImports( Lists.newArrayList(mergedImports.values()) );
	}

	public CompilationUnit getCompilationUnit() {
		return cu;
	}

	public String asString() {
		return cu.toString();
	}

	public Optional<TypeDeclaration> findFirstType() {
		return Optional.fromNullable(Iterables.getFirst(cu.getTypes(), null));
	}

	public Optional<MethodDeclaration> findMethod(String methodName) {

		methodName = sanitize(methodName).trim();
		Verify.verify( !methodName.isEmpty(), "The method name cannot be empty" );
		
		final List<TypeDeclaration> types = cu.getTypes();
		for (final TypeDeclaration type : types) {
			final List<BodyDeclaration> members = type.getMembers();
			for (final BodyDeclaration member : members) {
				if (member instanceof MethodDeclaration) {
					final MethodDeclaration method = (MethodDeclaration) member;
					if (methodName.equals(method.getName())) {
						return Optional.of( method );
					}
				}
			}
		}

		return Optional.absent();
	}

	  //~~~~~~~~~~~~//
	 // Utilities  //
	//~~~~~~~~~~~~//

	private CompilationUnit loadCU(final String transformedJava) {
		final StringReader sr = new StringReader(transformedJava);
		try {
			final CompilationUnit method = JavaParser.parse(sr, true);
			return method;
		} catch (final Throwable t) {
			throw Throwables.propagate(t);
		} finally {
			sr.close();
		}
	}
}