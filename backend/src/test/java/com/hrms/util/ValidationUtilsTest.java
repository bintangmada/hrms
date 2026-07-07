package com.hrms.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilsTest {

    @Test
    public void testValidateNotPlaceholder_Valid() {
        assertDoesNotThrow(() -> ValidationUtils.validateNotPlaceholder("ValidValue", "Field"));
    }

    @Test
    public void testValidateNotPlaceholder_NullOrEmpty() {
        Exception e1 = assertThrows(IllegalArgumentException.class, 
            () -> ValidationUtils.validateNotPlaceholder(null, "Field"));
        assertTrue(e1.getMessage().contains("cannot be empty"));

        Exception e2 = assertThrows(IllegalArgumentException.class, 
            () -> ValidationUtils.validateNotPlaceholder("   ", "Field"));
        assertTrue(e2.getMessage().contains("cannot be empty"));
    }

    @Test
    public void testValidateNotPlaceholder_SwaggerPlaceholder() {
        Exception e1 = assertThrows(IllegalArgumentException.class, 
            () -> ValidationUtils.validateNotPlaceholder("string", "Field"));
        assertTrue(e1.getMessage().contains("cannot contain the default Swagger placeholder"));

        Exception e2 = assertThrows(IllegalArgumentException.class, 
            () -> ValidationUtils.validateNotPlaceholder("STRING", "Field"));
        assertTrue(e2.getMessage().contains("cannot contain the default Swagger placeholder"));
    }

    @Test
    public void testValidateOptionalNotPlaceholder_ValidOrEmpty() {
        assertDoesNotThrow(() -> ValidationUtils.validateOptionalNotPlaceholder(null, "Field"));
        assertDoesNotThrow(() -> ValidationUtils.validateOptionalNotPlaceholder("", "Field"));
        assertDoesNotThrow(() -> ValidationUtils.validateOptionalNotPlaceholder("Valid Description", "Field"));
    }

    @Test
    public void testValidateOptionalNotPlaceholder_SwaggerPlaceholder() {
        Exception e1 = assertThrows(IllegalArgumentException.class, 
            () -> ValidationUtils.validateOptionalNotPlaceholder("string", "Field"));
        assertTrue(e1.getMessage().contains("cannot contain the default Swagger placeholder"));
    }
}
