package com.kim.complier.apt.util;


import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;


/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/2/3
 * Describe:
 */
public class ProcessorUtil {

    /**
     * 获得指定class的包名
     *
     * @param elementUtils
     * @param qualifiedSuperClassName
     * @return
     */
    public static String getPackageName(Elements elementUtils, String qualifiedSuperClassName) {
        TypeElement superClassname = elementUtils.getTypeElement(qualifiedSuperClassName);
        PackageElement pkg = elementUtils.getPackageOf(superClassname);
        if (pkg.isUnnamed()) {
            return null;
        }
        return pkg.getQualifiedName().toString();
    }

    /**
     * 获取指定元素的Class Name；如果是字段，方法元素，那么获取其所属类的Name
     *
     * @param element
     * @return
     */
    public static String getClassName(Element element) {
        if (element == null) return null;

        if (element.getKind().isClass() || element.getKind().isInterface()) {
            TypeElement typeElement = (TypeElement) element;
            return typeElement.getQualifiedName().toString();
        } else {
            return getClassName(element.getEnclosingElement());
        }
    }

    /**
     * 是否被 static  修饰符修饰
     *
     * @param element
     * @return
     */
    public static boolean isStaticModifier(Element element) {
        boolean result = false;
        if (element == null) return result;

        Set<Modifier> elementModifiers = element.getModifiers();
        for (Modifier modifier : elementModifiers) {



            if (Modifier.STATIC.equals(modifier)) {
                result = true;
                break;
            }
        }

        return result;
    }


    public static boolean isValidClass(TypeElement element, Messager messager, String annotation) {
        if (!isPublic(element)) {
            String message = String.format("Classes annotated with %s must be public.",
                    annotation);
            messager.printMessage(Diagnostic.Kind.ERROR, message, element);
            return false;
        }

        if (isAbstract(element)) {
            String message = String.format("Classes annotated with %s must not be abstract.",
                    annotation);
            messager.printMessage(Diagnostic.Kind.ERROR, message, element);
            return false;
        }
        return true;
    }

    public static boolean isFinalValidField(Element element, Messager messager, String annotation) {
        if (!isPublic(element)) {
            String message = String.format("Classes annotated with %s must be public.",
                    annotation);
            messager.printMessage(Diagnostic.Kind.ERROR, message, element);
            return false;
        }
        if (!isField(element)) {
            String message = String.format("must be file.",
                    annotation);
            messager.printMessage(Diagnostic.Kind.ERROR, message, element);
            return false;
        }
        if (!isFinal(element)) {
            String message = String.format("must be final.",
                    annotation);
            messager.printMessage(Diagnostic.Kind.ERROR, message, element);
            return false;
        }
        return true;
    }

    public static boolean isField(Element annotatedClass) {
        return annotatedClass.getKind() == ElementKind.FIELD;
    }

    public static boolean isFinal(Element annotatedClass) {
        return annotatedClass.getModifiers().contains(Modifier.FINAL);
    }

    public static boolean isPublic(Element annotatedClass) {
        return annotatedClass.getModifiers().contains(Modifier.PUBLIC);
    }

    public static boolean isAbstract(Element annotatedClass) {
        return annotatedClass.getModifiers().contains(Modifier.ABSTRACT);
    }

    /**
     * 获取类元件的包名
     * @param element
     * @return
     */
    public static String getPackageName(TypeElement element){
        String allName = element.getQualifiedName().toString();
        return allName.substring(0, allName.lastIndexOf("."));
    }

    /**
     * 通过镜像 获取class类名
     * @param element 当前元素
     * @param annotationName 注解名
     * @param className 注解定义的类名（无包名）
     * @return
     */
    public static String getClassName(Element element, String annotationName, String className){
        String superName = "";
        try
        {
            for (AnnotationMirror m: element.getAnnotationMirrors())
            {
                if (m.getAnnotationType().toString().equals(annotationName)){
                    for (Map.Entry e : m.getElementValues().entrySet())
                    {
                        ExecutableElement key = (ExecutableElement) e.getKey();
                        if (key.getSimpleName().toString().equals(className)){
                            AnnotationValue value = (AnnotationValue) e.getValue();
                            superName = value.getValue().toString();
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return superName;
    }

    public static String getClassName(Element element, String className){
        String superName = "";
        try
        {
            for (AnnotationMirror m: element.getAnnotationMirrors())
            {
                for (Map.Entry e : m.getElementValues().entrySet())
                {
                    ExecutableElement key = (ExecutableElement) e.getKey();
                    if (key.getSimpleName().toString().equals(className)){
                        AnnotationValue value = (AnnotationValue) e.getValue();
                        superName = value.getValue().toString();
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return superName;
    }

}


