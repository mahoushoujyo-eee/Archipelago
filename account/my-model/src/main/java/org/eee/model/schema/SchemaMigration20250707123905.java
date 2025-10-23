package org.eee.model.schema;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import java.util.List;

public class SchemaMigration20250707123905 extends SchemaMigrationBase {
    @Override
    public void forward() {
        forwardBuilder.createTable("resource", builder -> {
            builder.column().name("name_in_oss").type("VARCHAR(100)").nullable(true).unique(false);
            builder.column().name("title").type("VARCHAR(50)").nullable(true).unique(false);
            builder.column().name("type").type("INT").nullable(false).unique(false);
            builder.column().name("size").type("BIGINT").nullable(false).unique(false);
            builder.column().name("id").type("BIGINT").nullable(false).unique(false).autoIncrement(1);
            builder.column().name("creator_id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("creation_time").type("DATETIME").nullable(true).unique(false).defaultValue("NOW()");
            builder.column().name("modifier_id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("modification_time").type("DATETIME").nullable(true).unique(false)
                    .defaultValue("NOW()").onUpdate("NOW()");
            builder.primaryKey().columnName("id");
            builder.engine("InnoDB");
        });
    }
    @Override
    public void backward() {
        backwardBuilder.dropTable("resource");
    }
}