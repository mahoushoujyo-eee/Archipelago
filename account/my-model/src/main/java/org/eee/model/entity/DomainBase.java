package org.eee.model.entity;

import java.util.Date;

import lombok.Data;
import stark.coderaider.fluentschema.commons.annotations.AutoIncrement;
import stark.coderaider.fluentschema.commons.annotations.Column;
import stark.coderaider.fluentschema.commons.annotations.NotMapped;
import stark.coderaider.fluentschema.commons.annotations.PrimaryKey;


@Data
@NotMapped
public abstract class DomainBase
{
    /**
     * ID of the record.
     */
    @PrimaryKey
    @AutoIncrement
    public long id;

    /**
     * ID of the creator of the record.
     */
    public long creatorId;

    /**
     * Creation time of the record.
     */
    @Column(defaultValue = "NOW()")
    public Date creationTime;

    /**
     * ID of the user who modifies the record.
     */
    public long modifierId;

    /**
     * Last modification time of the record.
     */
    @Column(defaultValue = "NOW()", onUpdate = "NOW()")
    public Date modificationTime;
}
