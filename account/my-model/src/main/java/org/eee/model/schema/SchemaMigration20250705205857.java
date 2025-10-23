package org.eee.model.schema;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import java.util.List;

public class SchemaMigration20250705205857 extends SchemaMigrationBase {
    @Override
    public void forward() {
        forwardBuilder.createTable("model", builder -> {
            builder.column().name("id").type("BIGINT").nullable(false).unique(false).autoIncrement(1);
            builder.column().name("name").type("VARCHAR(250)").nullable(true).unique(false);
            builder.column().name("description").type("VARCHAR(250)").nullable(true).unique(false);
            builder.column().name("level").type("INT").nullable(false).unique(false);
            builder.primaryKey().columnName("id");
            builder.engine("InnoDB");
        });
    }
    @Override
    public void backward() {
        backwardBuilder.dropTable("model");
    }
}