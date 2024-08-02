/**
 * File:  TestACMECollegeSystem.java
 * Course materials (23W) CST 8277
 * Teddy Yap
 * (Original Author) Mike Norman
 *
 * @date 2020 10
 *
 * (Modified) @author Student Name
 */
package acmecollege;

import static acmecollege.utility.MyConstants.APPLICATION_API_VERSION;
import static acmecollege.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmecollege.entity.Student;

@SuppressWarnings("unused")

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestACMECollegeSystem {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    // Test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(
            new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        webTarget = client.target(uri);
    }

    @Test
    public void test01_all_students_with_adminrole() {
        Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_RESOURCE_NAME)
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        List<Student> students = response.readEntity(new GenericType<List<Student>>() {});
        assertThat(students, is(not(empty())));
        assertThat(students, hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    public void test02_get_student_by_id_with_adminrole() {
        Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_RESOURCE_NAME + "/1")
                .request()
                .get();
        assertThat(response.getStatus(), is(200));
        Student student = response.readEntity(Student.class);
        assertThat(student.getId(), is(1));
    }

    @Test
    public void test03_add_student_with_adminrole() {
        Student newStudent = new Student();
        newStudent.setFirstName("Test");
        newStudent.setLastName("Student");
        Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_RESOURCE_NAME)
                .request()
                .post(javax.ws.rs.client.Entity.json(newStudent));
        assertThat(response.getStatus(), is(200));
        Student createdStudent = response.readEntity(Student.class);
        assertThat(createdStudent.getId(), is(not(0)));
    }

    @Test
    public void test04_update_student_with_adminrole() {
        Student updateStudent = new Student();
        updateStudent.setFirstName("Updated");
        updateStudent.setLastName("Student");
        Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_RESOURCE_NAME + "/1")
                .request()
                .put(javax.ws.rs.client.Entity.json(updateStudent));
        assertThat(response.getStatus(), is(200));
        Student updatedStudent = response.readEntity(Student.class);
        assertThat(updatedStudent.getFirstName(), is("Updated"));
    }

    @Test
    public void test05_delete_student_with_adminrole() {
        Response response = webTarget
                .register(adminAuth)
                .path(STUDENT_RESOURCE_NAME + "/1")
                .request()
                .delete();
        assertThat(response.getStatus(), is(204));
    }


}