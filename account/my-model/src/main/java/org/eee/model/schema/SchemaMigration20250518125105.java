package org.eee.model.schema;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import java.util.List;

public class SchemaMigration20250518125105 extends SchemaMigrationBase {
    @Override
    public void forward() {
        setInitialized(false);
        forwardBuilder.createTable("Comment", builder -> {
            builder.column().name("blogId").type("BIGINT").nullable(true).unique(false);
            builder.column().name("content").type("VARCHAR(250)").nullable(true).unique(false);
            builder.column().name("state").type("INT").nullable(true).unique(false);
            builder.column().name("id").type("BIGINT").nullable(false).unique(false).autoIncrement(1);
            builder.column().name("creatorId").type("BIGINT").nullable(false).unique(false);
            builder.column().name("creationTime").type("DATETIME").nullable(true).unique(false).defaultValue("NOW()");
            builder.column().name("modifierId").type("BIGINT").nullable(false).unique(false);
            builder.column().name("modificationTime").type("DATETIME").nullable(true).unique(false)
                    .defaultValue("NOW()").onUpdate("NOW()");
            builder.primaryKey().columnName("id");
            builder.engine("InnoDB");
        });
        forwardBuilder.createTable("Like", builder -> {
            builder.column().name("blogId").type("BIGINT").nullable(true).unique(false);
            builder.column().name("id").type("BIGINT").nullable(false).unique(false).autoIncrement(1);
            builder.column().name("creatorId").type("BIGINT").nullable(false).unique(false);
            builder.column().name("creationTime").type("DATETIME").nullable(true).unique(false).defaultValue("NOW()");
            builder.column().name("modifierId").type("BIGINT").nullable(false).unique(false);
            builder.column().name("modificationTime").type("DATETIME").nullable(true).unique(false)
                    .defaultValue("NOW()").onUpdate("NOW()");
            builder.primaryKey().columnName("id");
            builder.engine("InnoDB");
        });
        forwardBuilder.createTable("role", builder -> {
            builder.column().name("role_name").type("VARCHAR(20)").nullable(true).unique(false);
            builder.column().name("user_id").type("BIGINT").nullable(true).unique(false);
            builder.column().name("role_code").type("INT").nullable(true).unique(false);
            builder.column().name("id").type("BIGINT").nullable(false).unique(false).autoIncrement(1);
            builder.column().name("creator_id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("creation_time").type("DATETIME").nullable(true).unique(false).defaultValue("NOW()");
            builder.column().name("modifier_id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("modification_time").type("DATETIME").nullable(true).unique(false)
                    .defaultValue("NOW()").onUpdate("NOW()");
            builder.primaryKey().columnName("id");
            builder.engine("InnoDB");
        });
        forwardBuilder.createTable("blog", builder -> {
            builder.column().name("name_in_oss").type("VARCHAR(100)").nullable(true).unique(false);
            builder.column().name("title").type("VARCHAR(50)").nullable(true).unique(false);
            builder.column().name("id").type("BIGINT").nullable(false).unique(false).autoIncrement(1);
            builder.column().name("creator_id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("creation_time").type("DATETIME").nullable(true).unique(false).defaultValue("NOW()");
            builder.column().name("modifier_id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("modification_time").type("DATETIME").nullable(true).unique(false)
                    .defaultValue("NOW()").onUpdate("NOW()");
            builder.primaryKey().columnName("id");
            builder.engine("InnoDB");
        });
        forwardBuilder.createTable("user", builder -> {
            builder.column().name("user_name").type("VARCHAR(30)").nullable(true).unique(true);
            builder.column().name("email").type("VARCHAR(30)").nullable(true).unique(true);
            builder.column().name("encrypted_password").type("VARCHAR(100)").nullable(true).unique(false);
            builder.column().name("state").type("INT").nullable(true).unique(false);
            builder.column().name("nickname").type("VARCHAR(30)").nullable(true).unique(false);
            builder.column().name("github_info").type("VARCHAR(100)").nullable(true).unique(false);
            builder.column().name("google_info").type("VARCHAR(100)").nullable(true).unique(false);
            builder.column().name("id").type("BIGINT").nullable(false).unique(false).autoIncrement(1);
            builder.column().name("creator_id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("creation_time").type("DATETIME").nullable(true).unique(false).defaultValue("NOW()");
            builder.column().name("modifier_id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("modification_time").type("DATETIME").nullable(true).unique(false)
                    .defaultValue("NOW()").onUpdate("NOW()");
            builder.primaryKey().columnName("id");
            builder.engine("InnoDB");
        });
    }
    @Override
    public void backward() {
        backwardBuilder.dropTable("Comment");
        backwardBuilder.dropTable("Like");
        backwardBuilder.dropTable("role");
        backwardBuilder.dropTable("blog");
        backwardBuilder.dropTable("user");
    }
}