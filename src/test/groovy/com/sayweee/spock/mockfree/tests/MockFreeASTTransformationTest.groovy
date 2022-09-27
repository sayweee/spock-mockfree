package com.sayweee.spock.mockfree.tests


import com.sayweee.spock.mockfree.transformation.MockFreeASTTransformation
import com.sayweee.spock.mockfree.transformer.MockfreeTransformer
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.SourceUnit
import spock.lang.Specification


/**
 * @author wangdengwu
 * Date 2022/9/13
 */
@SuppressWarnings("all")
class MockFreeASTTransformationTest extends Specification {
    AnnotationNode annotationNode = Spy()
    ClassExpression expression = Mock()
    SourceUnit sourceUnit = Mock()
    CompilationUnit compilationUnit = Spy()

    def "test visit"() {
        given:
        MockFreeASTTransformation transformation = new MockFreeASTTransformation()
        when:
        annotationNode.getMember(_ as String) >> new ListExpression([expression] as List<Expression>)
        expression.getType() >> ClassNode.THIS
        transformation.visit([annotationNode] as ASTNode[], sourceUnit)
        then:
        transformation.detectedClasses.contains(ClassNode.THIS)
    }

    def "test setCompilationUnit"() {
        given:
        MockFreeASTTransformation transformation = new MockFreeASTTransformation()
        def targetDir = "target/generated-test-classes"
        System.setProperty("groovy.target.directory", new File(targetDir).getAbsolutePath())
        compilationUnit.setConfiguration(new CompilerConfiguration())
        when:
        compilationUnit.addNewPhaseOperation(_ as CompilationUnit.ISourceUnitOperation, CompilePhase.OUTPUT.phaseNumber) >> { args ->
            CompilationUnit.ISourceUnitOperation op = args[0] as CompilationUnit.ISourceUnitOperation
            op.call(null)
        }
        transformation.setCompilationUnit(compilationUnit)
        then:
        new File(targetDir + "/com/sayweee/spock/mockfree/" + MockfreeTransformer.CLASS_NAME + ".class").exists()
    }
}
