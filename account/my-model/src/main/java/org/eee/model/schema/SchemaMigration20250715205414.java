package org.eee.model.schema;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import java.util.List;

public class SchemaMigration20250715205414 extends SchemaMigrationBase {
    @Override
    public void forward() {
        forwardBuilder.alterColumn("comment", "type", ColumnMetadata.builder().name("type").type("INT").nullable(true)
                .unique(false).defaultValue("0").build());
    }
    @Override
    public void backward() {
        backwardBuilder.alterColumn("comment", "type",
                ColumnMetadata.builder().name("type").type("INT").nullable(true).unique(false).build());
    }
}