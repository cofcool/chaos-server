package net.cofcool.chaos.server.sourceprocessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * 处理 <code>BaseComponent</code> 注解
 *
 * @author CofCool
 */
@SupportedAnnotationTypes({ ComponentAnnotationProcessor.BASE_COMPONENT_ANNOTATION })
public class ComponentAnnotationProcessor extends AbstractProcessor {

    public static final String BASE_COMPONENT_ANNOTATION = "net.cofcool.chaos.server.common.core.BaseComponent";
    public static final String SPRING_SERVICE_ANNOTATION = "org.springframework.stereotype.Service";

    public static final String SPRING_COMPONENT_ANNOTATION = "org.springframework.stereotype.Component";
    public static final String JAVA_RESOURCE_ANNOTATION = "javax.annotation.Resource";

    public ComponentAnnotationProcessor() {
    }

    private Types typeUtils;
    private Elements elementUtils;

    private Map<String, Element> baseComponents;
    private Map<String, Element> componentInterfaces;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();

        baseComponents = new HashMap<>();
        componentInterfaces = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            parseElement(roundEnv.getRootElements(), annotation);
        }

        baseComponents.forEach((name, ele) -> ele.getEnclosedElements().forEach(member -> {
            if (member.getKind() == ElementKind.FIELD &&
                (
                    hasTheAnnotation(member, elementUtils.getTypeElement(SPRING_COMPONENT_ANNOTATION)) ||
                        hasTheAnnotation(member, elementUtils.getTypeElement(JAVA_RESOURCE_ANNOTATION))
                )
            ) {

                Element memberRealElement = componentInterfaces.get(member.asType().toString());
                if (memberRealElement != null && baseComponents.get(memberRealElement.asType().toString()) == null) {
                    throw new IllegalStateException("the " + memberRealElement.asType().toString() + " must be baseComponent");
                }
            }
        }));

        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void parseElement(Set<? extends Element> elements, TypeElement type) {
        for (Element e : elements) {
            if (e.getKind().isClass() && (hasTheAnnotation(e, elementUtils.getTypeElement(SPRING_SERVICE_ANNOTATION)) || hasTheAnnotation(e, elementUtils.getTypeElement(SPRING_COMPONENT_ANNOTATION)))) {
                TypeElement typeElement = (TypeElement) e;
                typeElement.getInterfaces().forEach(typeMirror ->
                    componentInterfaces.put(typeMirror.toString(), typeElement)
                );

                if (hasTheAnnotation(typeElement, type)) {
                    baseComponents.put(typeElement.getQualifiedName().toString(), typeElement);
                } else {
                    for (TypeMirror typeMirror : typeElement.getInterfaces()) {
                        if (hasTheAnnotation(typeUtils.asElement(typeMirror), type)) {
                            baseComponents.put(typeElement.getQualifiedName().toString(), typeElement);

                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean hasTheAnnotation(Element e, TypeElement type) {
        for (AnnotationMirror mirror : e.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().asElement().equals(type)) {
                return true;
            }
        }

        return false;
    }

}
