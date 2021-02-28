package com.tngtech.archunit.exampletest;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.junit.Test;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class SpringControllerTest {
    private final JavaClasses classes = new ClassFileImporter().importPackages("com.myapp.somepackage", "com.myapp.other");

    @Test
    public void controllersMustBeAnnotatedWithRestController() {
        classes().that().resideInAPackage("..controller..")
                .should().beAnnotatedWith(RestController.class)
                .check(classes);
    }

    @Test
    public void classesOutsideControllersPackageMustNotBeAnnotatedWithRestController() {
        noClasses().that().resideOutsideOfPackage("..controller..")
                .should().beAnnotatedWith(RestController.class)
                .check(classes);
    }

    @Test
    public void publicMethodsInControllersMustHaveRouteAnnotation() {
        methods().that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .and().arePublic()
                .should().beAnnotatedWith(GetMapping.class)
                .orShould().beAnnotatedWith(PostMapping.class)
                .orShould().beAnnotatedWith(DeleteMapping.class)
                .orShould().beAnnotatedWith(PutMapping.class)
                .check(classes);
    }

    @Test
    public void foo() {
        GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION.check(classes);
    }
}
