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

package com.liferay.ide.ui.editor;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.properties.PortalPropertiesConfiguration;
import com.liferay.ide.ui.editor.LiferayPropertiesContentAssistProcessor.PropKey;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfigurationLayout;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.IPropertiesFilePartitions;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertiesFileSourceViewerConfiguration;
import org.eclipse.jface.internal.text.html.HTMLTextPresenter;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class LiferayPropertiesSourceViewerConfiguration extends PropertiesFileSourceViewerConfiguration {

	public LiferayPropertiesSourceViewerConfiguration(ITextEditor editor) {
		super(
			JavaPlugin.getDefault().getJavaTextTools().getColorManager(),
			JavaPlugin.getDefault().getCombinedPreferenceStore(), editor,
			IPropertiesFilePartitions.PROPERTIES_FILE_PARTITIONING);
	}

	@Override
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {

			@Override
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, new HTMLTextPresenter(true));
			}

		};
	}

	private String _getPropertiesEntry(IEditorInput input) {
		String retval = null;

		if ("system-ext.properties".equals(input.getName())) {
			retval = "system.properties";
		}
		else {
			retval = "portal.properties";
		}

		return retval;
	}

	private boolean _isHookProject(IProject project) {
		IWebProject webProject = LiferayCore.create(IWebProject.class, project);

		if ((webProject != null) && (webProject.getDescriptorFile(ILiferayConstants.LIFERAY_HOOK_XML_FILE) != null)) {
			return true;
		}

		return false;
	}

	private PropKey[] _parseKeys(InputStream inputStream) throws ConfigurationException, IOException {
		List<PropKey> parsed = new ArrayList<>();

		PortalPropertiesConfiguration config = new PortalPropertiesConfiguration();

		try {
			config.load(inputStream);
		}
		finally {
			inputStream.close();
		}

		Iterator<?> keys = config.getKeys();
		PropertiesConfigurationLayout layout = config.getLayout();

		while (keys.hasNext()) {
			Object o = keys.next();

			String key = o.toString();

			String comment = layout.getComment(key);

			parsed.add(new PropKey(key, comment == null ? null : comment.replaceAll("\n", "\n<br/>")));
		}

		PropKey[] parsedKeys = parsed.toArray(new PropKey[0]);

		Arrays.sort(
			parsedKeys,
			new Comparator<PropKey>() {

				@Override
				public int compare(PropKey o1, PropKey o2) {
					String o1key = o1.getKey();

					return o1key.compareTo(o2.getKey());
				}

			});

		return parsedKeys;
	}

}