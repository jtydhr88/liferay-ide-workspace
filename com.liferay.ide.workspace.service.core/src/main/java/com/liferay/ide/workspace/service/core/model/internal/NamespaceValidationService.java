/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.workspace.service.core.model.internal;

import com.liferay.ide.workspace.service.core.model.ServiceBuilder;

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Cindy Li
 */
public class NamespaceValidationService extends ValidationService {

	@Override
	public Status compute() {
		ServiceBuilder namespaceClass = context().find(ServiceBuilder.class);

		Value<String> namespace = namespaceClass.getNamespace();

		String content = namespace.content();

		if (!_isValidNamespace(content)) {
			return Status.createErrorStatus(Msgs.namespaceElementValidKeyword);
		}

		return Status.createOkStatus();
	}

	private boolean _isChar(char c) {
		int x = c;

		if (((x >= 97) && (x <= 122)) || ((x >= 65) && (x <= 90))) {
			return true;
		}

		return false;
	}

	private boolean _isValidNamespace(String namespace) {
		if (namespace == null) {
			return false;
		}

		for (char c : namespace.toCharArray()) {
			if ((c != '_') && !_isChar(c)) {
				return false;
			}
		}

		return true;
	}

	private static class Msgs extends NLS {

		public static String namespaceElementValidKeyword;

		static {
			initializeMessages(NamespaceValidationService.class.getName(), Msgs.class);
		}

	}

}