package com.sayweee.spock.mockfree.transformation

import com.sayweee.spock.mockfree.transformer.MockfreeTransformer
import groovy.transform.CompilationUnitAware
import javassist.ClassPool
import javassist.CtField
import javassist.Modifier
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.slf4j.Logger

import static org.slf4j.LoggerFactory.getLogger

/**
 * @Author: wangdengwu
 * @Date: 2022/8/28
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
class MockFreeASTTransformation extends AbstractASTTransformation implements CompilationUnitAware {
    private static final Logger log = getLogger(MockFreeASTTransformation)
    private static Set<ClassNode> detectedClasses = [] as Set<ClassNode>
    private static CompilationUnit compilationUnit

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        def annotation = nodes[0] as AnnotationNode
        detectedClasses.addAll(getClassNames(annotation) as List)
        annotation.members.clear()
    }

    @SuppressWarnings("all")
    private List<ClassNode> getClassNames(AnnotationNode annotation) {
        if (GroovySystem.getVersion().startsWith('2.')) {
            return getClassList(annotation, 'value') ?: []
        }
        getMemberClassList(annotation, 'value') ?: []
    }

    @Override
    void setCompilationUnit(CompilationUnit unit) {
        if (!compilationUnit) {
            compilationUnit = unit
            unit.addNewPhaseOperation({ SourceUnit sourceUnit ->
                if (!compilationUnit.phaseComplete) {
                    writeMockFreeTargetClasses()
                    compilationUnit.completePhase()
                }
            } as CompilationUnit.ISourceUnitOperation, CompilePhase.OUTPUT.phaseNumber)
        }
    }

    private static writeMockFreeTargetClasses() {
        String classPath = MockfreeTransformer.PACKAGE_PATH + "." + MockfreeTransformer.CLASS_NAME
        ClassPool pool = ClassPool.getDefault()
        def clazz = pool.makeInterface(classPath)
        CtField field = new CtField(pool.get("java.lang.String"), MockfreeTransformer.TARGET_CLASSES, clazz)
        field.setModifiers(Modifier.PUBLIC + Modifier.STATIC + Modifier.FINAL)
        if (detectedClasses.size() > 0) {
            clazz.addField(field, CtField.Initializer.constant(detectedClasses*.name.join(',')))
        } else {
            clazz.addField(field, CtField.Initializer.constant(""))
        }
        clazz.writeFile(compilationUnit.configuration.targetDirectory.toString())
    }
}
