package org.eee.model.schema;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import java.util.List;

public class SchemaMigration20250711110528 extends SchemaMigrationBase {
    @Override
    public void forward() {
        forwardBuilder.addColumn("blog",
                ColumnMetadata.builder().name("img_url").type("VARCHAR(300)").nullable(true).unique(false).build());
    }
    @Override
    public void backward() {
        backwardBuilder.dropColumn("blog", "img_url");
    }
}