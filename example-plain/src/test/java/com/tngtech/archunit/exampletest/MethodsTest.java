package com.tngtech.archunit.exampletest;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.example.layers.anticorruption.WrappedResult;
import com.tngtech.archunit.example.layers.security.Secured;
import org.junit.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;
import static com.tngtech.archunit.core.domain.properties.HasReturnType.Functions.GET_RAW_RETURN_TYPE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noCodeUnits;

public class MethodsTest {
    private final JavaClasses classes = new ClassFileImporter().importPackages("com.tngtech.archunit.example.layers");

    @Test
    public void all_public_methods_in_the_controller_layer_should_return_API_response_wrappers() {
        methods()
                .that().areDeclaredInClassesThat().resideInAPackage("..anticorruption..")
                .and().arePublic()
                .should().haveRawReturnType(WrappedResult.class)
                .because("we do not want to couple the client code directly to the return types of the encapsulated module")
                .check(classes);
    }

    @Test
    public void code_units_in_DAO_layer_should_not_be_Secured() {
        noCodeUnits()
                .that().areDeclaredInClassesThat().resideInAPackage("..persistence..")
                .should().beAnnotatedWith(Secured.class)
                .check(classes);
    }

    @Test
    public void foo() {
        methods().that().arePublic()
                .and().doNotHaveRawReturnType("void")
                .and().areDeclaredInClassesThat().areNotAnnotatedWith(ParametersAreNonnullByDefault.class)
                .and(GET_RAW_RETURN_TYPE.is(not(assignableTo(Collection.class))))
                .and(GET_RAW_RETURN_TYPE.is(not(assignableTo(Map.class))))
                .should().haveRawReturnType(Optional.class)
                .orShould().beAnnotatedWith(NotNull.class)
                .check(classes);
    }
}