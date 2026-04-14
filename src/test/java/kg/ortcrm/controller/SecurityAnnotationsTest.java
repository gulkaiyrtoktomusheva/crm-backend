package kg.ortcrm.controller;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SecurityAnnotationsTest {

    @Test
    void authRegisterShouldRequireUserCreatePermission() throws Exception {
        Method method = AuthController.class.getMethod("register", kg.ortcrm.dto.auth.RegisterRequest.class);
        PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);

        assertNotNull(annotation);
        assertEquals("hasAuthority('USER_CREATE')", annotation.value());
    }

    @Test
    void studentCreateShouldRequireStudentCreatePermission() throws Exception {
        Method method = StudentController.class.getMethod("create", kg.ortcrm.dto.student.StudentRequest.class);
        PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);

        assertNotNull(annotation);
        assertEquals("hasAuthority('STUDENT_CREATE')", annotation.value());
    }

    @Test
    void groupDeleteShouldRequireGroupDeletePermission() throws Exception {
        Method method = GroupController.class.getMethod("delete", Long.class);
        PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);

        assertNotNull(annotation);
        assertEquals("hasAuthority('GROUP_DELETE')", annotation.value());
    }
}
