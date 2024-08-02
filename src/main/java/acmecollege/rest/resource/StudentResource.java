package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;

@Path("/api/v1/students")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getStudents() {
        LOG.debug("retrieving all students ...");
        List<Student> students = service.getAllStudents();
        return Response.ok(students).build();
    }

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path("/{id}")
    public Response getStudentById(@PathParam("id") int id) {
        LOG.debug("try to retrieve specific student " + id);
        Student student = null;
        Response response;

        if (sc.isCallerInRole(ADMIN_ROLE)) {
            student = service.getStudentById(id);
            response = Response.status(student == null ? Status.NOT_FOUND : Status.OK).entity(student).build();
        } else if (sc.isCallerInRole(USER_ROLE)) {
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
            student = sUser.getStudent();
            if (student != null && student.getId() == id) {
                response = Response.status(Status.OK).entity(student).build();
            } else {
                throw new ForbiddenException("User trying to access resource it does not own (wrong userid)");
            }
        } else {
            response = Response.status(Status.BAD_REQUEST).build();
        }
        return response;
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addPerson(Student newStudent) {
        Student newStudentWithIdTimestamps = service.persistStudent(newStudent);
        service.buildUserForNewStudent(newStudentWithIdTimestamps);
        return Response.status(Status.CREATED).entity(newStudentWithIdTimestamps).build();
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{studentId}/courses/{courseId}/professor")
    public Response updateProfessorForStudentCourse(@PathParam("studentId") int studentId, @PathParam("courseId") int courseId, Professor newProfessor) {
        Professor professor = service.setProfessorForStudentCourse(studentId, courseId, newProfessor);
        return Response.ok(professor).build();
    }

    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{id}")
    public Response deleteStudent(@PathParam("id") int id) {
        LOG.debug("delete student with id " + id);
        service.deleteStudentById(id);
        return Response.noContent().build();
    }
}
