package io.realworld.common.jooq;

import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;

public class DepluralizedAndPojoNamingGeneratorStrategy extends DefaultGeneratorStrategy {

    /**
     * @see <a href="https://github.com/jibx/core/blob/b388dc984bb6fc61222adf6a44d6d4796f31855e/build/src/org/jibx/util/NameUtilities.java#L35">jibx's NameUtilities.depluralize(String)</a>
     * @param name name to singularize
     * @return singularized name
     */
    private static String depluralize(String name) {
        if (name.endsWith("ies")) {
            return name.substring(0, name.length() - 3) + 'y';
        } else if (name.endsWith("sses")) {
            return name.substring(0, name.length() - 2);
        } else if (name.endsWith("s") && !name.endsWith("ss")) {
            return name.substring(0, name.length() - 1);
        } else {
            return name.endsWith("List") ? name.substring(0, name.length() - 4) : name;
        }
    }

    @Override
    public String getJavaClassName(final Definition definition, final Mode mode) {
        final String javaClassName = super.getJavaClassName(definition, mode);
        final String depluralized = depluralize(javaClassName);
        return switch (mode) {
            case POJO -> depluralized + "Pojo";
            default -> depluralized;
        };
    }
}
