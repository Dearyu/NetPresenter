package netpresenter.compiler;


import com.google.auto.service.AutoService;
import netpresenter.NetPresenterException;
import netpresenter.annotations.NetBuilder;
import netpresenter.annotations.NetCallBack;
import netpresenter.annotations.NetListener;
import netpresenter.annotations.NetService;
import netpresenter.annotations.NetUnit;
import netpresenter.iface.INetBuilder;
import netpresenter.iface.INetListener;
import netpresenter.iface.INetUnit;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class NetPresenterProcessor extends AbstractProcessor {

    private Types mTypeUtils;
    private static Messager mMessager;
    private Filer mFiler;
    private Elements mElementUtils;
    private TypeElement mNetBuilder;
    private TypeElement mNetUnit;
    private TypeElement mNetListener;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mTypeUtils = processingEnvironment.getTypeUtils();
        mMessager = processingEnvironment.getMessager();
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(NetService.class.getCanonicalName());
        annotations.add(NetBuilder.class.getCanonicalName());
        annotations.add(NetUnit.class.getCanonicalName());
        annotations.add(NetListener.class.getCanonicalName());
        annotations.add(NetCallBack.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment env) {
        Map<TypeElement, netpresenter.compiler.NetPresenterSet> setMap = parseNet(env);
        for (Map.Entry<TypeElement, netpresenter.compiler.NetPresenterSet> entry : setMap.entrySet()) {
            netpresenter.compiler.NetPresenterSet binding = entry.getValue();
            JavaFile javaFile = binding.brewJava();
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private Map<TypeElement, netpresenter.compiler.NetPresenterSet> parseNet(RoundEnvironment env) {
        initAndVerifyNet(env);
        Map<TypeElement, netpresenter.compiler.NetPresenterSet> buildMap = new LinkedHashMap<>();
        for (Element annotatedElement : env.getElementsAnnotatedWith(NetService.class)) {
            if (annotatedElement.asType().getKind() != TypeKind.DECLARED
                    || mTypeUtils.asElement(annotatedElement.asType()).getKind() != ElementKind.INTERFACE) {
                throw new NetPresenterException("Object of @NetService must be an interface");
            }
            VariableElement netVar = (VariableElement) annotatedElement;
            TypeElement netType = (TypeElement) mTypeUtils.asElement(annotatedElement.asType());
            TypeElement netEnc = (TypeElement) annotatedElement.getEnclosingElement();
            List<ExecutableElement> netCallBack = parseCallBack(netEnc);
            List<Element> netMember = (List<Element>) mElementUtils.getAllMembers(netType);
            netpresenter.compiler.NetPresenterSet netSet = buildMap.get(netType);
            if (null == netSet) {
                netSet = new netpresenter.compiler.NetPresenterSet().Builder()
                        .addNetPresenter(netVar)
                        .addNetPresenterType(netType)
                        .addNetMember(netMember)
                        .addNetBuilder(mNetBuilder)
                        .addNetUnit(mNetUnit)
                        .addNetListener(mNetListener)
                        .addNetCallBack(netEnc, netVar, netCallBack)
                        .build();
            } else {
                netSet = netSet.Builder()
                        .addNetCallBack(netEnc, netVar, netCallBack)
                        .build();
            }
            buildMap.put(netType, netSet);
        }
        return buildMap;
    }

    private List<ExecutableElement> parseCallBack(TypeElement enc) {
        List<ExecutableElement> callBack = new ArrayList<>();
        for (Element element : mElementUtils.getAllMembers(enc)) {
            if (element.getKind() == ElementKind.METHOD) {
                ExecutableElement callBackElement = (ExecutableElement) element;
                if (null != callBackElement.getAnnotation(NetCallBack.class)) {
                    if (callBackElement.getModifiers().contains(Modifier.PUBLIC)) {
                        callBack.add(callBackElement);
                    } else {
                        throw new NetPresenterException("method of @NetCallBack must be public");
                    }
                }
            }
        }
        return callBack;
    }

    private void initAndVerifyNet(RoundEnvironment env) {
        if (env.getElementsAnnotatedWith(NetService.class).size() > 0) {
            if (env.getElementsAnnotatedWith(NetBuilder.class).size() != 1) {
                throw new NetPresenterException("@NetBuilder can only be one");
            } else {
                mNetBuilder = (TypeElement) env.getElementsAnnotatedWith(NetBuilder.class).toArray()[0];
                if (!implementsInterface(mNetBuilder, mElementUtils.getTypeElement(TypeName.get(INetBuilder.class).toString()).asType())) {
                    throw new NetPresenterException("@NetBuilder have to implement the INetBuilder");
                }
            }
            if (env.getElementsAnnotatedWith(NetUnit.class).size() != 1) {
                throw new NetPresenterException("@NetUnit can only be one");
            } else {
                mNetUnit = (TypeElement) env.getElementsAnnotatedWith(NetUnit.class).toArray()[0];
                if (!implementsInterface(mNetUnit, mElementUtils.getTypeElement(TypeName.get(INetUnit.class).toString()).asType())) {
                    throw new NetPresenterException("@NetUnit have to implement the INetUnit");
                }
            }
            if (env.getElementsAnnotatedWith(NetListener.class).size() != 1) {
                throw new NetPresenterException("@NetListener can only be one");
            } else {
                mNetListener = (TypeElement) env.getElementsAnnotatedWith(NetListener.class).toArray()[0];
                if (!implementsInterface(mNetListener, mElementUtils.getTypeElement(TypeName.get(INetListener.class).toString()).asType())) {
                    throw new NetPresenterException("@NetListener have to implement the INetListener");
                }
            }
        }
    }

    public boolean implementsInterface(TypeElement myTypeElement, TypeMirror desiredInterface) {
        for (TypeMirror t : myTypeElement.getInterfaces()) {
            if (mTypeUtils.isSubtype(t, desiredInterface)
                    || t.toString().equals(desiredInterface.toString()))
                return true;
        }
        return false;
    }

    public static void note(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    public static void error(String msg) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, msg);
    }

}