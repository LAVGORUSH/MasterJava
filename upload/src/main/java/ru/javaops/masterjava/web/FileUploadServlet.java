package ru.javaops.masterjava.web;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.javaops.masterjava.config.ThymeleafConfig.getTemplateEngine;

public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        WebContext context = new WebContext(req,res,req.getServletContext(),req.getLocale());
        getTemplateEngine().process("fileUploadForm", context, res.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Collection<Part> parts = req.getParts();
        List<User> users = null;
        try {
            users = getUserFromXml(parts);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        WebContext context = new WebContext(req, res, req.getServletContext(), req.getLocale());
        context.setVariable("users", users);
        getTemplateEngine().process("users", context, res.getWriter());
    }

    private List<User> getUserFromXml(Collection<Part> parts) throws XMLStreamException, JAXBException, IOException {
        List<User> users = new ArrayList<>();
        for (Part part : parts) {
            if (part != null) {
                InputStream is = part.getInputStream();
                StaxStreamProcessor processor = new StaxStreamProcessor(is);

                JaxbParser parser = new JaxbParser(User.class);
                while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                    User user = parser.unmarshal(processor.getReader(), User.class);
                    users.add(user);
                }
            }
        }
        return users;
    }
}