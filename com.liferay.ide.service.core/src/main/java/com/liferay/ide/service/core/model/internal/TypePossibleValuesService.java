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

package com.liferay.ide.service.core.model.internal;

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.service.core.model.ServiceBuilder6xx;

import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Version;

/**
 * @author Cindy Li
 */
public class TypePossibleValuesService extends PossibleValuesService {

	@Override
	protected void compute(Set<String> values) {
		for (String type : _DEFAULT_TYPES) {
			values.add(type);
		}

		Version version = SapphireUtil.getContent(context(ServiceBuilder6xx.class).getVersion());

		if (version.compareTo(new Version("6.2")) >= 0) {
			values.add("Blob");
		}
	}

	private static final String[] _DEFAULT_TYPES = {"String", "long", "boolean", "int", "double", "Date", "Collection"};

}