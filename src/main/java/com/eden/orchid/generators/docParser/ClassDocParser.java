package com.eden.orchid.generators.docParser;

import com.eden.orchid.Orchid;
import com.eden.orchid.resources.OrchidReference;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Type;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class ClassDocParser {
    public static JSONObject createClassDocJson(ClassDoc classDoc) {
        JSONObject classInfoJson = new JSONObject();
        classInfoJson.put("modifiers", classDoc.modifiers());
        classInfoJson.put("comment", ParserUtils.getCommentDescription(classDoc));
        classInfoJson.put("info", getClassInfo(classDoc));

        if (classDoc.fields().length > 0) {
            classInfoJson.put("fields", new JSONArray());
            for (FieldDoc fieldDoc : classDoc.fields()) {
                JSONObject fieldDocJson = FieldDocParser.getFieldObject(fieldDoc);
                classInfoJson.getJSONArray("fields").put(fieldDocJson);
            }
        }

        if (classDoc.constructors().length > 0) {
            classInfoJson.put("ctors", new JSONArray());
            for (ConstructorDoc constructorDoc : classDoc.constructors()) {
                JSONObject constructorDocJson = ConstructorDocParser.getConstructorObject(constructorDoc);
                classInfoJson.getJSONArray("ctors").put(constructorDocJson);
            }
        }

        if (classDoc.methods().length > 0) {
            classInfoJson.put("methods", new JSONArray());
            for (MethodDoc methodDoc : classDoc.methods()) {
                JSONObject methodDocJson = MethodDocParser.getMethodObject(methodDoc);
                classInfoJson.getJSONArray("methods").put(methodDocJson);
            }
        }

        return classInfoJson;
    }

    public static JSONObject getClassInfo(ClassDoc classDoc) {
        JSONObject classInfoJson = new JSONObject();

        classInfoJson.put("type", ParserUtils.getTypeObject(classDoc));
        classInfoJson.put("simpleName", classDoc.name());
        classInfoJson.put("name", classDoc.qualifiedName());
        classInfoJson.put("package", classDoc.containingPackage().name());

        OrchidReference classReference = new OrchidReference("classes", classDoc.qualifiedTypeName().replaceAll("\\.", File.separator) + File.separator + "index.html");
        classInfoJson.put("url", classReference.toString());

        if (classDoc.superclassType() != null) {
            classInfoJson.put("superclass", ParserUtils.getTypeObject(classDoc.superclassType()));
        }

        if (classDoc.interfaces().length > 0) {
            classInfoJson.put("interfaces", new JSONArray());
            for (Type interfaceClass : classDoc.interfaces()) {
                classInfoJson.getJSONArray("interfaces").put(ParserUtils.getTypeObject(interfaceClass));
            }
        }

        JSONObject subclasses = new JSONObject();

        JSONArray directSubclasses = getDirectSubclasses(classDoc);
        if (directSubclasses.length() > 0) {
            subclasses.put("direct", directSubclasses);
        }

        JSONArray indirectSubclasses = getIndirectSubclasses(classDoc);
        if (indirectSubclasses.length() > 0) {
            subclasses.put("indirect", indirectSubclasses);
        }

        if (subclasses.keySet().size() > 0) {
            classInfoJson.put("subclasses", subclasses);
        }

        String classtype = "";
        if (classDoc.isClass()) { classtype = "class"; }
        else if (classDoc.isInterface()) { classtype = "interface"; }
        else if (classDoc.isEnum()) { classtype = "enum"; }
        classInfoJson.put("classType", classtype);

        return classInfoJson;
    }

    public static JSONArray getDirectSubclasses(ClassDoc classDoc) {
        JSONArray directSubclasses = new JSONArray();

        if (Orchid.query("index.classes") != null) {
            JSONArray subclasses = addSubclassesInList(classDoc, (JSONArray) Orchid.query("index.classes.internal").getElement());

            if (subclasses.length() > 0) {
                for (int i = 0; i < subclasses.length(); i++) {
                    directSubclasses.put(subclasses.get(i));
                }
            }
        }

        if (Orchid.query("index.classes") != null) {
            JSONArray subclasses = addSubclassesInList(classDoc, (JSONArray) Orchid.query("index.classes.external").getElement());

            if (subclasses.length() > 0) {
                for (int i = 0; i < subclasses.length(); i++) {
                    directSubclasses.put(subclasses.get(i));
                }
            }
        }

        return directSubclasses;
    }

    public static JSONArray addSubclassesInList(ClassDoc classDoc, JSONArray additionalClasses) {
        JSONArray directSubclasses = new JSONArray();

        for (int i = 0; i < additionalClasses.length(); i++) {
            if(additionalClasses.getJSONObject(i).optJSONObject("info") == null) {
                continue;
            }

            JSONObject testClass = additionalClasses.getJSONObject(i).getJSONObject("info");

            // check if the given class has a superclass that is the same type as our target ClassDoc

            if (testClass.optJSONObject("superclass") != null) {
                if (classDoc.qualifiedName().equals(testClass.getJSONObject("superclass").getString("name"))) {
                    directSubclasses.put(testClass);
                    continue;
                }
            }

            // check if the given class has an interface that is the same type as our target ClassDoc
            if (testClass.optJSONArray("interfaces") != null) {
                JSONArray interfaces = testClass.getJSONArray("interfaces");

                for (int j = 0; j < interfaces.length(); j++) {
                    if (classDoc.qualifiedName().equals(interfaces.getJSONObject(j).getString("name"))) {
                        directSubclasses.put(testClass);
                        continue;
                    }
                }
            }
        }

        return directSubclasses;
    }

    public static JSONArray getIndirectSubclasses(ClassDoc classDoc) {
        JSONArray indirectSubclasses = new JSONArray();

        return indirectSubclasses;
    }
}
