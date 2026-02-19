package io.github.XanderGI.servlet;

import io.github.XanderGI.dto.ErrorResponse;
import io.github.XanderGI.exception.ModelAlreadyExistsException;
import io.github.XanderGI.exception.ModelNotFoundException;
import io.github.XanderGI.utils.JsonMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class BaseServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String method = req.getMethod();

            if (method.equals("PATCH")) {
                this.doPatch(req, resp);
                return;
            }

            super.service(req, resp);
        } catch (NumberFormatException e) {
            JsonMapper.sendJson(resp, new ErrorResponse("Invalid format number"), 400);
        } catch (IllegalArgumentException e) {
            JsonMapper.sendJson(resp, new ErrorResponse(e.getMessage()), 400);
        } catch (ModelNotFoundException e) {
            JsonMapper.sendJson(resp, new ErrorResponse(e.getMessage()), 404);
        } catch (ModelAlreadyExistsException e) {
            JsonMapper.sendJson(resp, new ErrorResponse(e.getMessage()), 409);
        } catch (Exception e) {
            e.printStackTrace();
            JsonMapper.sendJson(resp, new ErrorResponse("Server error"), 500);
        }

    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}