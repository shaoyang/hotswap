package com.taobao.baoxian.utils.render;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.taobao.baoxian.utils.conf.Config;

public class VelocityRender {

	public static String render(String tempString, Map<Object, Object> model)
			throws ParseErrorException, MethodInvocationException,
			ResourceNotFoundException, IOException, URISyntaxException {

		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH,Config.RESOURCES_DIR);
		try {
			ve.init();
		} catch (Exception e) {
			e.printStackTrace();
		}

		VelocityContext context = new VelocityContext(model);

		if (null != model) {
			for (Object key : model.keySet()) {
				context.put((String) key, model.get(key));
			}
		}

		StringWriter writer = new StringWriter();
		ve.evaluate(context, writer, "", tempString);

		return writer.toString().trim();
	}
}
