package com.kubrick.sbt.web.auth.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * 处理无权请求
 *
 * @author k
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException)
			throws IOException, ServletException {
		boolean isAjax = ControllerTools.isAjaxRequest(request);
		if (!response.isCommitted()) {
			if (isAjax) {
				String msg = accessDeniedException.getMessage();
				log.info("accessDeniedException.message:" + msg);
				String accessDenyMsg = "{\"code\":\"403\",\"msg\":\"没有权限\"}";
				ControllerTools.print(response, accessDenyMsg);
			}
			else {
				request.setAttribute(WebAttributes.ACCESS_DENIED_403,
						accessDeniedException);
				response.setStatus(HttpStatus.FORBIDDEN.value());
				RequestDispatcher dispatcher = request.getRequestDispatcher("/403");
				dispatcher.forward(request, response);
			}
		}

	}

	public static class ControllerTools {

		public static boolean isAjaxRequest(HttpServletRequest request) {
			return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
		}

		public static void print(HttpServletResponse response, String msg)
				throws IOException {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=utf-8");
			PrintWriter writer = response.getWriter();
			writer.write(msg);
			writer.flush();
			writer.close();
		}

	}

}
