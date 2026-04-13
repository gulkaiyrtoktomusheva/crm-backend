package kg.ortcrm.controller;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SecurityAnnotationsTest {

    @Test
    void reportExportShouldBeRestrictedToAdminAndManager() throws Exception {
        Method method = ReportController.class.getMethod("exportStudents", String.class, kg.ortcrm.entity.enums.StudentStatus.class, Long.class, Long.class, String.class);
        PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);

        assertNotNull(annotation);
        assertEquals("hasAnyRole('ADMIN','MANAGER')", annotation.value());
    }

    @Test
    void studentCreateShouldBeRestrictedToAdminAndManager() throws Exception {
        Method method = StudentController.class.getMethod("create", kg.ortcrm.dto.student.StudentRequest.class);
        PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);

        assertNotNull(annotation);
        assertEquals("hasAnyRole('ADMIN','MANAGER')", annotation.value());
    }

    @Test
    void groupDeleteShouldBeRestrictedToAdmin() throws Exception {
        Method method = GroupController.class.getMethod("delete", Long.class);
        PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);

        assertNotNull(annotation);
        assertEquals("hasRole('ADMIN')", annotation.value());
    }
}
