package netpresenter.compiler;

import netpresenter.annotations.CallBackType;
import netpresenter.annotations.NetCallBack;
import netpresenter.annotations.NetService;
import netpresenter.iface.INetBinder;
import netpresenter.iface.INetBuilder;
import netpresenter.iface.INetUnit;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

/**
 * @Author junweiliu
 * @Description 信息封装类
 * @Version 1.0
 * @CreateDate 2021/3/16
 * @QQ 1007271386Ø
 */
class NetPresenterSet {

    private final ClassName Log = ClassName.get("android.util", "Log");
    private final ClassName ListClassName = ClassName.get("java.util", "List");
    private final ClassName ArraysClassName = ClassName.get("java.util", "Arrays");
    private final ClassName ArrayListClassName = ClassName.get("java.util", "ArrayList");
    private final ClassName MapClassName = ClassName.get("java.util", "Map");
    private final ClassName ObjectClassName = ClassName.get("java.lang", "Object");

    private VariableElement mNetPresenterElement;
    private TypeElement mNetPresenterTypeElement;
    private ClassName mNetPresenterTypeClassName;
    private List<Element> mNetPresenterMemberElements;
    private TypeElement mNetBuilderElement;
    private TypeElement mNetUnitElement;
    private TypeElement mNetListenerElement;
    private Map<TypeElement, Map<VariableElement, List<ExecutableElement>>> mNetCallBanckElements;
    private boolean mServiceForOne;
    private boolean mHaveOverload;
    private Builder mBuilder;

    public NetPresenterSet() {
    }

    public NetPresenterSet(
            VariableElement netPresenterElement,
            TypeElement netPresenterTypeElement,
            ClassName netPresenterClassName,
            List<Element> netPresenterMemberElements,
            TypeElement netBuilderElement, TypeElement netUnitElement,
            TypeElement netListenerElement,
            Map<TypeElement, Map<VariableElement, List<ExecutableElement>>> netCallBanckElements,
            boolean serviceForOne,
            boolean haveOverload,
            Builder builder) {
        mNetPresenterElement = netPresenterElement;
        mNetPresenterTypeElement = netPresenterTypeElement;
        mNetPresenterTypeClassName = netPresenterClassName;
        mNetPresenterMemberElements = netPresenterMemberElements;
        mNetBuilderElement = netBuilderElement;
        mNetUnitElement = netUnitElement;
        mNetListenerElement = netListenerElement;
        mNetCallBanckElements = netCallBanckElements;
        mServiceForOne = serviceForOne;
        mHaveOverload = haveOverload;
        mBuilder = builder;
    }

    public JavaFile brewJava() {
        TypeSpec netConfiguration = createType();
        return JavaFile.builder(mNetPresenterTypeClassName.packageName(), netConfiguration)
                .build();
    }

    private TypeSpec createType() {
        TypeSpec.Builder netPresenterType = TypeSpec.classBuilder(mNetPresenterTypeClassName.simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(mNetPresenterTypeElement.asType())
                .addSuperinterface(INetBinder.class)
                .addField(mServiceForOne ? ClassName.get(mNetPresenterElement.getEnclosingElement().asType()) : ObjectClassName, "mTarget", Modifier.PRIVATE)
                .addField(INetBuilder.class, "mNetBuilder", Modifier.PRIVATE)
                .addField(ParameterizedTypeName.get(MapClassName, TypeName.get(String.class), TypeName.get(INetUnit.class)), "mNetUnits")
                .addField(String[].class, "mNotCancelTags");

        MethodSpec netConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
//                .addParameter(mServiceForOne ? ClassName.get(((TypeElement) mNetPresenterElement.getEnclosingElement()).asType()) : ObjectClassName, "target")
                .addParameter(Object.class, "target")
                .addParameter(String[].class, "notCancelTags")
                .addStatement("mTarget = $Ntarget", mServiceForOne ? "(" + mNetPresenterElement.getEnclosingElement().getSimpleName() + ")" : "")
                .addStatement("mNotCancelTags = notCancelTags")
                .addStatement("mNetBuilder = new $T()", mNetBuilderElement.asType())
                .addStatement("mNetUnits = new $T<$T,$T>()", LinkedHashMap.class, String.class, INetUnit.class)
                .build();
        netPresenterType.addMethod(netConstructor);

        MethodSpec netBinder = MethodSpec.methodBuilder("unbind")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$T notCacncels= $T.asList(mNotCancelTags)", ListClassName, ArraysClassName)
                .beginControlFlow("for (String key : mNetUnits.keySet())")
                .addStatement("if (notCacncels.contains(key)) continue")
                .addStatement("mNetUnits.get(key).cancelRequest()")
                .endControlFlow()
                .build();
        netPresenterType.addMethod(netBinder);

        for (Element element : mNetPresenterMemberElements) {
            if (element.getKind() == ElementKind.METHOD) {
                ExecutableElement executableElement = (ExecutableElement) element;
                if (executableElement.getReturnType().getKind() == TypeKind.DECLARED
                        && (!executableElement.getSimpleName().toString().equals("getClass")
                        && !executableElement.getSimpleName().toString().equals("toString"))) {
                    List<ParameterSpec> parms = new ArrayList<>();
                    StringBuffer parmsStr = new StringBuffer();
                    for (Element typeParameterElement : executableElement.getParameters()) {
                        ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.get(typeParameterElement.asType()),
                                typeParameterElement.getSimpleName().toString())
                                .build();
                        parms.add(parameterSpec);
                        parmsStr.append(typeParameterElement.getSimpleName().toString()).append(",");
                    }
                    String returnType = NetPresenterUtil.extractMessage(executableElement.getReturnType().toString());
                    TypeSpec typeSpec = TypeSpec.anonymousClassBuilder("")
                            .addSuperinterface(TypeVariableName.get(mNetListenerElement.getQualifiedName().toString()
                                    + "<" + returnType + ">"))
                            .addMethod(MethodSpec.methodBuilder("onStart")
                                    .addModifiers(Modifier.PUBLIC)
                                    .addCode(getCallBackCode(CallBackType.START))
                                    .returns(void.class)
                                    .build())
                            .addMethod(MethodSpec.methodBuilder("onFinished")
                                    .addModifiers(Modifier.PUBLIC)
                                    .addCode(getCallBackCode(CallBackType.FINISH))
                                    .returns(void.class)
                                    .build())
                            .addMethod(MethodSpec.methodBuilder("onSuc")
                                    .addParameter(TypeVariableName.get(returnType), "bean")
                                    .addModifiers(Modifier.PUBLIC)
                                    .addCode(getCallBackCode(CallBackType.SUC))
                                    .returns(void.class)
                                    .build())
                            .addMethod(MethodSpec.methodBuilder("onFail")
                                    .addParameter(TypeVariableName.get("String..."), "msgs")
                                    .addCode(getCallBackCode(CallBackType.FAIL))
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(void.class)
                                    .build())
                            .build();

                    MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                            .addAnnotation(Override.class)
                            .returns(TypeName.get(executableElement.getReturnType()))
                            .addModifiers(Modifier.PUBLIC)
                            .addParameters(parms);
                    if (!mHaveOverload) {
                        methodSpec.addStatement("$T tag = $S", String.class, executableElement.getSimpleName().toString());
                    } else {
                        methodSpec.addStatement("$T tag =new $T($S)", StringBuilder.class, StringBuilder.class, executableElement.getSimpleName().toString());
                        methodSpec.beginControlFlow("if (mNetUnits.containsKey(tag.toString())) ")
                                .addStatement("tag.append($S)", "_" + executableElement.getParameters().size())
                                .endControlFlow();
                    }
                    methodSpec.addStatement("$T netUnit = new $T().setObservable(mNetBuilder.create($T.class).$N($N)).request($L)",
                            INetUnit.class, mNetUnitElement.asType(),
                            mNetPresenterTypeElement.asType(),
                            executableElement.getSimpleName(), parmsStr.deleteCharAt(parmsStr.length() - 1),
                            typeSpec)
                            .addStatement("mNetUnits.put(tag.toString(),netUnit)")
                            .addStatement("return null")
                            .build();
                    netPresenterType.addMethod(methodSpec.build());
                }
            }
        }
        return netPresenterType.build();
    }

    private String getCallBackCode(CallBackType type) {
        StringBuilder netCallBackCode = new StringBuilder();
        for (Map.Entry<TypeElement, Map<VariableElement, List<ExecutableElement>>> clsEntry : mNetCallBanckElements.entrySet()) {
            if (null == clsEntry.getValue() || clsEntry.getValue().isEmpty()) {
                continue;
            }
            for (Map.Entry<VariableElement, List<ExecutableElement>> entry : clsEntry.getValue().entrySet()) {
                if (null == entry.getValue() || entry.getValue().isEmpty()) {
                    continue;
                }
                NetService netService = entry.getKey().getAnnotation(NetService.class);
                ExecutableElement callMethod = null;
                for (ExecutableElement element : entry.getValue()) {
                    NetCallBack callBack = element.getAnnotation(NetCallBack.class);
                    if (null != callBack && callBack.value().equals(netService.value())
                            && type == callBack.type()) {
                        callMethod = element;
                        break;
                    }
                }
                if (null != callMethod) {
                    String resultStr = "";
                    switch (type) {
                        case START:
                        case FINISH:
                            break;
                        case SUC:
                            resultStr = ", bean";
                            break;
                        case FAIL:
                            resultStr = ", msgs";
                            break;
                    }
                    if (mServiceForOne) {
                        netCallBackCode.append("mTarget.").append(callMethod.getSimpleName()).append("(tag.toString()").append(resultStr).append(");");
                    } else {
                        if (NetPresenterUtil.isEmpty(netCallBackCode.toString())) {
                            netCallBackCode.append("if (mTarget instanceof ")
                                    .append(clsEntry.getKey().getQualifiedName())
                                    .append("){\n")
                                    .append("((").append(clsEntry.getKey().getQualifiedName()).append(")mTarget).").append(callMethod.getSimpleName())
                                    .append("(tag.toString()").append(resultStr).append(");")
                                    .append("\n}");
                        } else {
                            netCallBackCode.append("else if (mTarget instanceof ")
                                    .append(clsEntry.getKey().getQualifiedName())
                                    .append("){\n")
                                    .append("((").append(clsEntry.getKey().getQualifiedName()).append(")mTarget).").append(callMethod.getSimpleName())
                                    .append("(tag.toString()").append(resultStr).append(");").append("\n}");
                        }
                    }
                }
            }
        }
        return netCallBackCode.toString();
    }

    public Builder Builder() {
        if (null == mBuilder) {
            mBuilder = new Builder();
        }
        return mBuilder;
    }

    static class Builder {
        private VariableElement mNetPresenterElement;
        private TypeElement mNetPresenterTypeElement;
        private List<Element> mNetPresenterMemberElements;
        private TypeElement mNetBuilderElement;
        private TypeElement mNetUnitElement;
        private TypeElement mNetListenerElement;
        private Map<TypeElement, Map<VariableElement, List<ExecutableElement>>> mNetCallBanckElements;

        public Builder() {
        }

        public Builder addNetPresenter(VariableElement netPresenterElement) {
            mNetPresenterElement = netPresenterElement;
            return this;
        }

        public Builder addNetPresenterType(TypeElement netPresenterTypeElement) {
            mNetPresenterTypeElement = netPresenterTypeElement;
            return this;
        }

        public Builder addNetBuilder(TypeElement netBuilderElement) {
            mNetBuilderElement = netBuilderElement;
            return this;
        }

        public Builder addNetMember(List<Element> elements) {
            mNetPresenterMemberElements = elements;
            return this;
        }

        public Builder addNetUnit(TypeElement netUnitElement) {
            mNetUnitElement = netUnitElement;
            return this;
        }

        public Builder addNetListener(TypeElement netListenerElement) {
            mNetListenerElement = netListenerElement;
            return this;
        }

        public Builder addNetCallBack(TypeElement netType, VariableElement netValue, List<ExecutableElement> netCallBanckElements) {
            if (null == mNetCallBanckElements) {
                mNetCallBanckElements = new LinkedHashMap<>();
            }
            Map<VariableElement, List<ExecutableElement>> callMap = mNetCallBanckElements.get(netType);
            if (null == callMap) {
                callMap = new LinkedHashMap<>();
            }
            callMap.put(netValue, netCallBanckElements);
            mNetCallBanckElements.put(netType, callMap);
            return this;
        }

        public NetPresenterSet build() {
            if (null == mNetPresenterMemberElements) {
                mNetPresenterMemberElements = new ArrayList<>();
            }
            if (null == mNetCallBanckElements) {
                mNetCallBanckElements = new LinkedHashMap<>();
            }
            List<String> methodNames = new ArrayList<>();
            boolean haveOverload = false;
            for (Element element : mNetPresenterMemberElements) {
                if (element.getKind() == ElementKind.METHOD) {
                    ExecutableElement executableElement = (ExecutableElement) element;
                    if (executableElement.getReturnType().getKind() == TypeKind.DECLARED
                            && (!executableElement.getSimpleName().toString().equals("getClass")
                            && !executableElement.getSimpleName().toString().equals("toString"))) {
                        if (methodNames.contains(executableElement.getSimpleName().toString())) {
                            haveOverload = true;
                            break;
                        } else {
                            methodNames.add(executableElement.getSimpleName().toString());
                        }
                    }
                }
            }

            return new NetPresenterSet(
                    mNetPresenterElement,
                    mNetPresenterTypeElement,
                    NetPresenterUtil.getNetClassName(mNetPresenterTypeElement),
                    mNetPresenterMemberElements,
                    mNetBuilderElement,
                    mNetUnitElement,
                    mNetListenerElement,
                    mNetCallBanckElements,
                    mNetCallBanckElements.size() == 1,
                    haveOverload,
                    this);
        }
    }
}
