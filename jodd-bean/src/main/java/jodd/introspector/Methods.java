// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ArraysUtil;
import jodd.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Methods collection.
 */
class Methods {

	final HashMap<String, MethodDescriptor[]> methodsMap;

	Method[] allMethods;		// cache

	Methods() {
		methodsMap = new HashMap<String, MethodDescriptor[]>();
	}

	void addMethod(String name, Method method, Class implClass) {
		MethodDescriptor[] mds = methodsMap.get(name);

		if (mds == null) {
			mds = new MethodDescriptor[1];
		} else {
			mds = ArraysUtil.resize(mds, mds.length + 1);
		}
		methodsMap.put(name, mds);

		mds[mds.length - 1] = new MethodDescriptor(method, implClass);

		// reset cache
		allMethods = null;
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns a method that matches given name and parameter types.
	 */
	Method getMethod(String name, Class[] paramTypes) {
		MethodDescriptor[] methodDescriptors = methodsMap.get(name);
		if (methodDescriptors == null) {
			return null;
		}
		for (int i = 0; i < methodDescriptors.length; i++) {
			Method m = methodDescriptors[i].getMethod();
			if (ReflectUtil.compareParameters(m.getParameterTypes(), paramTypes) == true) {
				return methodDescriptors[i].getMethod();
			}
		}
		return null;
	}

	/**
	 * Returns single method with given name, if one and only one such method exists.
	 */
	Method getMethod(String name) {
		MethodDescriptor[] methodDescriptors = methodsMap.get(name);
		if (methodDescriptors == null) {
			return null;
		}
		if (methodDescriptors.length != 1) {
			throw new IllegalArgumentException("Method name not unique: " + name);
		}
		return methodDescriptors[0].getMethod();
	}

	/**
	 * Returns all methods for given name. Not cached.
	 */
	Method[] getAllMethods(String name) {
		MethodDescriptor[] methodDescriptors = methodsMap.get(name);
		if (methodDescriptors == null) {
			return new Method[0];
		}

		List<Method> allMethodsList = new ArrayList<Method>();

		for (MethodDescriptor methodDescriptor : methodDescriptors) {
			allMethodsList.add(methodDescriptor.getMethod());
		}

		return allMethodsList.toArray(new Method[allMethodsList.size()]);
	}

	/**
	 * Returns all methods. Cached.
	 */
	Method[] getAllMethods() {
		if (allMethods == null) {
			List<Method> allMethodsList = new ArrayList<Method>();

			for (MethodDescriptor[] methodDescriptors : methodsMap.values()) {
				for (MethodDescriptor methodDescriptor : methodDescriptors) {
					allMethodsList.add(methodDescriptor.getMethod());
				}
			}

			allMethods = allMethodsList.toArray(new Method[allMethodsList.size()]);
		}
		return allMethods;
	}

	// todo!
	void removeAllMethodsForName(String name) {
		methodsMap.remove(name);
		// clear cache
		allMethods = null;
	}
}