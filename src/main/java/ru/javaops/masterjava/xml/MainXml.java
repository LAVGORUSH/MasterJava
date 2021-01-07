package ru.javaops.masterjava.xml;

import org.xml.sax.SAXException;
import ru.javaops.masterjava.xml.schema.Group;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


/*Реализовать класс MainXml, которые принимает параметром имя проекта в тестовом xml и выводит отсортированный список его участников (использовать JAXB).
 */
public class MainXml {
    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

    static {
        JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
    }

    public static void main(String[] args) throws IOException, SAXException, JAXBException, ParserConfigurationException {

        String projectName = getInputForProjectName();
        Payload payload = getPayloadUsingJaxb();
        List<User> users = getUsersByJaxb(projectName, payload);

        users.forEach(System.out::println);
    }

    private static Payload getPayloadUsingJaxb() throws JAXBException, FileNotFoundException {
        Payload payload = JAXB_PARSER.unmarshal(new FileInputStream("D:/PROG/Java/MasterJava/src/test/resources/payload.xml"));
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

