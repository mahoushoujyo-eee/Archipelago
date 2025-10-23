package org.eee.model.schema;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import java.util.List;

public class SchemaMigration20250715195042 extends SchemaMigrationBase {
    @Override
    public void forward() {
        forwardBuilder.dropColumn("like", "user_id");
    }
    @Override
    public void backward() {
        backwardBuilder.addColumn("like",
                ColumnMetadata.builder().name("user_id").type("BIGINT").nullable(true).unique(false).build());
    }
}