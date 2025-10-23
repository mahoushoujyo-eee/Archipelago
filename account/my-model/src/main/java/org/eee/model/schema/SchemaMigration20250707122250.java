package org.eee.model.schema;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import java.util.List;

public class SchemaMigration20250707122250 extends SchemaMigrationBase {
    @Override
    public void forward() {
        forwardBuilder.addColumn("blog",
                ColumnMetadata.builder().name("size").type("BIGINT").nullable(false).unique(false).build());
    }
    @Override
    public void backward() {
        backwardBuilder.dropColumn("blog", "size");
    }
}