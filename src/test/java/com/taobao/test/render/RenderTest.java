package com.taobao.test.render;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.taobao.baoxian.utils.render.VelocityRender;

public class RenderTest {
	public static void main(String[] args){
			Map model = new HashMap();
			model.put("Shaoyang", "1");
			model.put("pttss","2");
			model.put("gmmzz", "4");
			try {
				System.out.println(""+VelocityRender.render("hellosite.vm", model));
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
}
