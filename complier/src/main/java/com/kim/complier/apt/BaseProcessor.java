package com.kim.complier.apt;


import com.kim.complier.apt.util.LogUtil;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/2/4
 * Describe
 */
public class BaseProcessor extends AbstractProcessor
{
    protected Filer mFiler;
    protected Messager mMessager;
    protected static String PACKAGE_NAME = "com.kim.ipc";
    protected LogUtil mLogUtil;
    protected Elements mElements;
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        mLogUtil = new LogUtil(mMessager);
        mElements = processingEnv.getElementUtils();
        Map<String, String> options = processingEnv.getOptions();
        if (options != null && options.size() > 0){
            for (Map.Entry<String, String> entry : options.entrySet())
            {
                if (entry.getKey().contains("kim.applicationId")){
                    PACKAGE_NAME = entry.getValue();
                }
            }
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> types = new LinkedHashSet<>();
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }
}
