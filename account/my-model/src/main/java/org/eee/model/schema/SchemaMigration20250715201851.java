package org.eee.model.schema;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import java.util.List;

public class SchemaMigration20250715201851 extends SchemaMigrationBase {
    @Override
    public void forward() {
        forwardBuilder.alterColumn("comment", "state",
                ColumnMetadata.builder().name("type").type("INT").nullable(true).unique(false).build());
    }
    @Override
    public void backward() {
        backwardBuilder.alterColumn("comment", "type",
                ColumnMetadata.builder().name("state").type("INT").nullable(true).unique(false).build());
    }
}