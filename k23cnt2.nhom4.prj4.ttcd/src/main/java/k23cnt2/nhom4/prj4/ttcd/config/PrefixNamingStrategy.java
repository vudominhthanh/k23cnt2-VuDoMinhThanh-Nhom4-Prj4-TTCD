package k23cnt2.nhom4.prj4.ttcd.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class PrefixNamingStrategy implements PhysicalNamingStrategy {

    private static final String PREFIX = "n4_";

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        return new Identifier(PREFIX + name.getText(), name.isQuoted());
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        return new Identifier(PREFIX + name.getText(), name.isQuoted());
    }

    @Override public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment context) { return name; }
    @Override public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) { return name; }
    @Override public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) { return name; }
}