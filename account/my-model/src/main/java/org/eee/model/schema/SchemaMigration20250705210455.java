package org.eee.model.schema;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import java.util.List;

public class SchemaMigration20250705210455 extends SchemaMigrationBase {
    @Override
    public void forward() {
        forwardBuilder.addColumn("model",
                ColumnMetadata.builder().name("api_key").type("VARCHAR(500)").nullable(true).unique(false).build());
        forwardBuilder.addColumn("model",
                ColumnMetadata.builder().name("base_url").type("VARCHAR(100)").nullable(true).unique(false).build());
    }
    @Override
    public void backward() {
        backwardBuilder.dropColumn("model", "api_key");
        backwardBuilder.dropColumn("model", "base_url");
    }
}