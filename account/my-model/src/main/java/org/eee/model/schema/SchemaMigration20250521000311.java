package org.eee.model.schema;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import java.util.List;

public class SchemaMigration20250521000311 extends SchemaMigrationBase {
    @Override
    public void forward() {
        forwardBuilder.alterColumn("user", "user_name",
                ColumnMetadata.builder().name("username").type("VARCHAR(30)").nullable(true).unique(true).build());
    }
    @Override
    public void backward() {
        backwardBuilder.alterColumn("user", "username",
                ColumnMetadata.builder().name("user_name").type("VARCHAR(30)").nullable(true).unique(true).build());
    }
}