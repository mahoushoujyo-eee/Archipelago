package org.eee.model.schema;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import java.util.List;

public class SchemaMigration20250712141341 extends SchemaMigrationBase {
    @Override
    public void forward() {
        forwardBuilder.dropColumn("blog", "size");
        forwardBuilder.addColumn("blog",
                ColumnMetadata.builder().name("tags").type("VARCHAR(500)").nullable(true).unique(false).build());
    }
    @Override
    public void backward() {
        backwardBuilder.dropColumn("blog", "tags");
        backwardBuilder.addColumn("blog",
                ColumnMetadata.builder().name("size").type("BIGINT").nullable(false).unique(false).build());
    }
}