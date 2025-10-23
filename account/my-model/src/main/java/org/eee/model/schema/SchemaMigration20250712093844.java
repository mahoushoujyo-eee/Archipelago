package org.eee.model.schema;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import java.util.List;

public class SchemaMigration20250712093844 extends SchemaMigrationBase {
    @Override
    public void forward() {
        forwardBuilder.addColumn("knowledge",
                ColumnMetadata.builder().name("title").type("VARCHAR(100)").nullable(true).unique(false).build());
        forwardBuilder.alterColumn("knowledge", "collection_name", ColumnMetadata.builder().name("collection_name")
                .type("VARCHAR(500)").nullable(true).unique(false).build());
    }
    @Override
    public void backward() {
        backwardBuilder.alterColumn("knowledge", "collection_name", ColumnMetadata.builder().name("collection_name")
                .type("VARCHAR(100)").nullable(true).unique(false).build());
        backwardBuilder.dropColumn("knowledge", "title");
    }
}