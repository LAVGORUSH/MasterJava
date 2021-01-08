package ru.javaops.masterjava.xml;

import ru.javaops.masterjava.xml.schema.Group;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;
import ru.javaops.masterjava.xml.util.XsltProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;


/*Реализовать класс MainXml, которые принимает параметром имя проекта в тестовом xml и выводит отсортированный список его участников (использовать JAXB).
 */
public class MainXml {
    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);


    static {
        JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
    }

    public static void main(String[] args) throws Exception {
        FileInputStream fileInputStream = new FileInputStream("D:/PROG/Java/MasterJava/src/test/resources/payload.xml");
        String projectName = getInputForProjectName();
        /*Payload payload = getPayloadUsingJaxb(fileInputStream);
        List<User> usersByJaxb = getUsersByJaxb(projectName, payload);

        usersByJaxb.forEach(System.out::println);*/

        /*StaxStreamProcessor processor = new StaxStreamProcessor(fileInputStream);
        List<String> usersByStAX = getUsersByStAX(projectName, processor);
        usersByStAX.forEach(System.out::println);
        List<String[]> usersData = usersByStAX.stream().map(u -> u.split("/")).collect(Collectors.toList());

        processor.close();

        String render = html(head().withTitle("User table"),
                body().with(h1("User on project " + projectName)).with(br())
                        .with(table().attr("border", 1)
                                .with(thead(tr(th("Full name"), th("Email")))).with(tbody(each(usersData,
                                        u -> tr(td(u[0]), td(u[1]))))))).render();

        StringReader stringReader = new StringReader(render);
        FileWriter fileWriter1 = new FileWriter("D:/PROG/Java/MasterJava/src/main/resources/users1.html");
        fileWriter1.write(render);
        fileWriter1.close();*/

        FileWriter fileWriter2 = new FileWriter("D:/PROG/Java/MasterJava/src/main/resources/users2.html");
        XsltProcessor xsltProcessor = new XsltProcessor(new FileInputStream("D:/PROG/Java/MasterJava/src/main/resources/usersHtml.xsl"));
        xsltProcessor.setTransformParameter("ProjectName", projectName);
        xsltProcessor.transform(fileInputStream, fileWriter2);

        fileWriter2.close();
        fileInputStream.close();
//        System.out.println(render);
    }

    private static List<String> getUsersByStAX(String projectName, StaxStreamProcessor processor) throws XMLStreamException {
        List<String> users = new ArrayList<>();
        List<String> groupList = new ArrayList<>();
        List<String> result = new ArrayList<>();

        while (processor.startElement("User", "Users")) {
            String fullName = processor.getAttribute("fullName");
            String email = processor.getAttribute("email");
            String groups = processor.getAttribute("groups");
            users.add(fullName + "/" + email + " groups: " + groups);
        }


        while (processor.startElement("Project", "Projects")) {
            if (projectName.toLowerCase().equals(processor.getElementValue("name").toLowerCase())) {
                while (processor.startElement("Group", "Groups")) {
                    groupList.add(processor.getAttribute("id"));
                }
            }
        }

        for (String user : users) {
            String[] split = user.split(" groups: ");
            List<String> list = Arrays.asList(split[1].split(" "));
            List<String> copyGroups = new ArrayList<>(groupList);
            if (copyGroups.removeAll(list)) {
                result.add(split[0]);
            }
        }
        return result;
    }

    private static Payload getPayloadUsingJaxb(InputStream is) throws JAXBException, FileNotFoundException {
        Payload payload = JAXB_PARSER.unmarshal(is);
        return payload;
    }

    private static String getInputForProjectName() {
        System.out.println("Input name of project");
        Scanner scanner = new Scanner(System.in);
        String projectName = scanner.nextLine();
        return projectName;
    }

    private static List<User> getUsersByJaxb(String projectName, Payload payload) {
        List<User> result;
        List<Group> groupList = payload.getProjects().getProject().stream()
                .filter(p -> projectName.toLowerCase().equals(p.getName().toLowerCase()))
                .map(project -> project.getGroups().getGroup())
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        result = payload.getUsers().getUser().stream().filter(user -> {
            List<Object> groups = user.getGroups();
            for (Object group : groups) {
                if (groupList.contains(group)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        return result;
    }


}

