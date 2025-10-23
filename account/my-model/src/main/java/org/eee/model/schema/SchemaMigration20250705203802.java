package org.eee.model.schema;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import java.util.List;

public class SchemaMigration20250705203802 extends SchemaMigrationBase {
    @Override
    public void forward() {
        forwardBuilder.createTable("conversation", builder -> {
            builder.column().name("conversation_id").type("VARCHAR(255)").nullable(true).unique(false);
            builder.column().name("user_id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("title").type("VARCHAR(255)").nullable(true).unique(false);
            builder.column().name("id").type("BIGINT").nullable(false).unique(false).autoIncrement(1);
            builder.column().name("creator_id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("creation_time").type("DATETIME").nullable(true).unique(false).defaultValue("NOW()");
            builder.column().name("modifier_id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("modification_time").type("DATETIME").nullable(true).unique(false)
                    .defaultValue("NOW()").onUpdate("NOW()");
            builder.primaryKey().columnName("id");
            builder.engine("InnoDB");
        });
        forwardBuilder.addColumn("like",
                ColumnMetadata.builder().name("user_id").type("BIGINT").nullable(true).unique(false).build());
    }
    @Override
    public void backward() {
        backwardBuilder.dropColumn("like", "user_id");
        backwardBuilder.dropTable("conversation");
    }
}