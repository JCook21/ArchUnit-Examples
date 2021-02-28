package com.tngtech.archunit.exampletest;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.domain.properties.HasModifiers;
import com.tngtech.archunit.core.domain.properties.HasType;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

/**
 * This ArchCondition checks that a class has a serialVersionUID field that is valid for use with Serializable classes.
 */
public class HaveASerialVersionUIDField extends ArchCondition<JavaClass> {

    public HaveASerialVersionUIDField() {
        super("have a valid serialVersionUID field");
    }

    @Override
    public void check(JavaClass item, ConditionEvents events) {
        var errorMessage = item.getName() + " does not contain a valid serialVersionUID field";
        try {
            var field = item.getField("serialVersionUID");
            var hasValidSerialVersionUID = HasModifiers.Predicates.modifier(JavaModifier.STATIC).apply(field)
                    && HasModifiers.Predicates.modifier(JavaModifier.FINAL).apply(field)
                    && HasType.Predicates.rawType("long").apply(field);
            events.add(new SimpleConditionEvent(item, hasValidSerialVersionUID, errorMessage));
        } catch (IllegalArgumentException e) {
            events.add(SimpleConditionEvent.violated(item, errorMessage));
        }
    }
}
