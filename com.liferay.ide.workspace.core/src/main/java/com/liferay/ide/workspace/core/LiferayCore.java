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

package com.liferay.ide.workspace.core;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 */
public class LiferayCore extends Plugin {

	public static final String PLUGIN_ID = "com.liferay.ide.workspace.core";

	public static IStatus createErrorStatus(Exception e) {
		return createErrorStatus(PLUGIN_ID, e);
	}

	public static IStatus createErrorStatus(String msg) {
		return createErrorStatus(PLUGIN_ID, msg);
	}

	public static IStatus createErrorStatus(String pluginId, String msg) {
		return new Status(IStatus.ERROR, pluginId, msg);
	}

	public static IStatus createErrorStatus(String pluginId, String msg, Throwable e) {
		return new Status(IStatus.ERROR, pluginId, msg, e);
	}

	public static IStatus createErrorStatus(String pluginId, Throwable t) {
		return new Status(IStatus.ERROR, pluginId, t.getMessage(), t);
	}

	public static IStatus createInfoStatus(String msg) {
		return createInfoStatus(PLUGIN_ID, msg);
	}

	public static IStatus createInfoStatus(String pluginId, String msg) {
		return new Status(IStatus.INFO, pluginId, msg);
	}

	public static IStatus createWarningStatus(String message) {
		return new Status(IStatus.WARNING, PLUGIN_ID, message);
	}

	public static IStatus createWarningStatus(String message, String id) {
		return new Status(IStatus.WARNING, id, message);
	}

	public static IStatus createWarningStatus(String message, String id, Exception e) {
		return new Status(IStatus.WARNING, id, message, e);
	}

	public static LiferayCore getDefault() {
		return _plugin;
	}

	public static void logError(IStatus status) {
		ILog log = getDefault().getLog();

		log.log(status);
	}

	public static void logError(String msg) {
		logError(createErrorStatus(msg));
	}

	public static void logError(String msg, Throwable t) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(PLUGIN_ID, msg, t));
	}

	public static void logError(Throwable t) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, t.getMessage(), t));
	}

	public static void logInfo(String msg) {
		logError(createInfoStatus(msg));
	}

	public static void logWarning(Throwable t) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.WARNING, PLUGIN_ID, t.getMessage(), t));
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

	private static LiferayCore _plugin;

}