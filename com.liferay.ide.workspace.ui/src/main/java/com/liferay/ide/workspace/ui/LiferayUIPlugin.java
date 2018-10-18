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

package com.liferay.ide.workspace.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 */
public class LiferayUIPlugin extends AbstractUIPlugin implements IStartup {

	public static final String IMG_LIFERAY_ICON_SMALL = "IMG_LIFERAY_ICON_SMALL";

	public static final String PLUGIN_ID = "com.liferay.ide.workspace.ui";

	public static IStatus createErrorStatus(String string) {
		return new Status(IStatus.ERROR, PLUGIN_ID, string);
	}

	public static LiferayUIPlugin getDefault() {
		return _plugin;
	}

	public static void logError(Exception e) {
		logError(e.getMessage(), e);
	}

	public static void logError(String msg, Exception e) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}

	public LiferayUIPlugin() {
	}

	@Override
	public void earlyStartup() {
	}

	public Image getImage(String key) {
		return getImageRegistry().get(key);
	}

	public ImageDescriptor getImageDescriptor(String key) {
		getImageRegistry();

		return imageDescriptors.get(key);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		_plugin = null;

		super.stop(context);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		registerImage(reg, IMG_LIFERAY_ICON_SMALL, "/icons/liferay_logo_16.png");
	}

	protected void registerImage(ImageRegistry registry, String key, String path) {
		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(getBundle().getEntry(path));

			imageDescriptors.put(key, id);

			registry.put(key, id);
		}
		catch (Exception e) {
		}
	}

	protected Map<String, ImageDescriptor> imageDescriptors = new HashMap<>();

	private static LiferayUIPlugin _plugin;

}