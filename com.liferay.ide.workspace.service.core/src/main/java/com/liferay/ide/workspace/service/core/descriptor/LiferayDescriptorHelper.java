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

package com.liferay.ide.workspace.service.core.descriptor;

import com.liferay.ide.workspace.core.LiferayCore;
import com.liferay.ide.workspace.core.util.FileUtil;
import com.liferay.ide.workspace.service.core.ServiceCore;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.content.ContentTypeManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

import org.osgi.framework.Version;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public abstract class LiferayDescriptorHelper {

	public static String getDescriptorVersion(IProject project) {
		return getDescriptorVersion(project, "7.0.0");
	}

	public static String getDescriptorVersion(IProject project, String defaultValue) {
		String retval = defaultValue;

		if ("0.0.0".equals(retval)) {
			retval = defaultValue;
		}

		return retval;
	}

	public LiferayDescriptorHelper() {
		addDescriptorOperations();
	}

	public LiferayDescriptorHelper(IProject project) {
		this.project = project;

		addDescriptorOperations();
	}

	public IContentType getContentType() {
		return contentType;
	}

	public abstract IFile getDescriptorFile();

	public IDescriptorOperation getDescriptorOperation(Class<? extends IDescriptorOperation> type) {
		for (IDescriptorOperation operation : descriptorOperations) {
			if (type.isAssignableFrom(operation.getClass())) {
				return operation;
			}
		}

		return null;
	}

	public String getDescriptorPath() {
		return descriptorPath;
	}

	public void setContentType(IContentType type) {
		contentType = type;
	}

	public void setContentType(String type) {
		ContentTypeManager contentTypeManager = ContentTypeManager.getInstance();

		contentType = contentTypeManager.getContentType(type);
	}

	public void setDescriptorPath(String path) {
		descriptorPath = path;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public abstract class DOMModelEditOperation extends DOMModelOperation {

		public DOMModelEditOperation(IFile descriptorFile) {
			super(descriptorFile);
		}

		public void createDefaultDescriptor(String templateString, String version) {
			String content = MessageFormat.format(templateString, version, version.replace('.', '_'));

			try (InputStream input = new ByteArrayInputStream(content.getBytes("UTF-8"))) {
				file.create(input, IResource.FORCE, null);
			}
			catch (Exception e) {
				LiferayCore.logError(e);
			}
		}

		@Override
		public IStatus execute() {
			IStatus retval = null;

			if (FileUtil.notExists(file)) {
				createDefaultFile();
			}

			IDOMModel domModel = null;

			try {
				IModelManager modelManager = StructuredModelManager.getModelManager();

				domModel = (IDOMModel)modelManager.getModelForEdit(file);

				domModel.aboutToChangeModel();

				IDOMDocument document = domModel.getDocument();

				retval = doExecute(document);

				domModel.changedModel();

				domModel.save();
			}
			catch (Exception e) {
				retval = LiferayCore.createErrorStatus(e);
			}
			finally {
				if (domModel != null) {
					domModel.releaseFromEdit();
				}
			}

			return retval;
		}

		protected void createDefaultFile() {
		}

	}

	protected static String getDescriptorVersionFromPortalVersion(String versionStr) {
		Version version = new Version(versionStr);

		int major = version.getMajor();
		int minor = version.getMinor();

		return String.valueOf(major) + "." + String.valueOf(minor) + ".0";
	}

	protected void addDescriptorOperation(IDescriptorOperation operation) {
		descriptorOperations.add(operation);
	}

	protected abstract void addDescriptorOperations();

	protected List<Element> getChildElements(Element parent) {
		List<Element> retval = new ArrayList<>();

		if (parent != null) {
			NodeList children = parent.getChildNodes();

			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);

				if (child instanceof Element) {
					retval.add((Element)child);
				}
			}
		}

		return retval;
	}

	protected String getDescriptorVersion() {
		return getDescriptorVersion(project);
	}

	protected IProject getProject() {
		return project;
	}

	protected IStatus removeAllElements(IDOMDocument document, String tagName) {
		if (document == null) {
			return ServiceCore.createErrorStatus(
				MessageFormat.format("Could not remove {0} elements: null document", tagName));
		}

		NodeList elements = document.getElementsByTagName(tagName);

		try {
			if ((elements != null) && (elements.getLength() > 0)) {
				for (int i = 0; i < elements.getLength(); i++) {
					Node element = elements.item(i);

					Node parentNode = element.getParentNode();

					parentNode.removeChild(element);
				}
			}
		}
		catch (Exception ex) {
			return ServiceCore.createErrorStatus(MessageFormat.format("Could not remove {0} elements", tagName), ex);
		}

		return Status.OK_STATUS;
	}

	protected IContentType contentType;
	protected ArrayList<IDescriptorOperation> descriptorOperations = new ArrayList<>();
	protected String descriptorPath;
	protected IProject project;

	protected abstract static class DOMModelOperation {

		public DOMModelOperation(IFile descriptorFile) {
			file = descriptorFile;
		}

		public abstract IStatus execute();

		protected abstract IStatus doExecute(IDOMDocument document);

		protected IFile file;

	}

	protected abstract class DOMModelReadOperation extends DOMModelOperation {

		public DOMModelReadOperation(IFile descriptorFile) {
			super(descriptorFile);
		}

		@Override
		public IStatus execute() {
			IStatus retval = null;

			if (FileUtil.notExists(file)) {
				return LiferayCore.createErrorStatus(file.getName() + " does not exist");
			}

			IDOMModel domModel = null;

			try {
				IModelManager modelManager = StructuredModelManager.getModelManager();

				domModel = (IDOMModel)modelManager.getModelForRead(file);

				IDOMDocument document = domModel.getDocument();

				retval = doExecute(document);
			}
			catch (Exception e) {
				retval = LiferayCore.createErrorStatus(e);
			}
			finally {
				if (domModel != null) {
					domModel.releaseFromRead();
				}
			}

			return retval;
		}

	}

}