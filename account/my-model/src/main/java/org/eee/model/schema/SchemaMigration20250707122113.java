package org.eee.model.schema;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import java.util.List;

public class SchemaMigration20250707122113 extends SchemaMigrationBase {
    @Override
    public void forward() {
        forwardBuilder.addColumn("blog",
                ColumnMetadata.builder().name("type").type("INT").nullable(false).unique(false).build());
    }
    @Override
    public void backward() {
        backwardBuilder.dropColumn("blog", "type");
    }
}