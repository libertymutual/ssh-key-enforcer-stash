package com.edwardawebb.stash.ssh.ao;

import java.util.Date;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Table("ENTKEY")
public interface SshKeyEntity extends Entity{
    
    @Mutator("KEYID")
    Integer getKeyId();
    @NotNull
    @Mutator("TEXT")
    @StringLength(value=767)
    String getText();
    @NotNull
    @Mutator("LABEL")
    String getLabel();
    @NotNull
    @Mutator("USERID")
    Integer getUserId();
    @NotNull
    @Mutator("CREATED")
    Date getCreatedDate();
    

    @Accessor("KEYID")
    void setKeyId(int keyId);
    @Accessor("TEXT")
    void setText(String text);
    @Accessor("LABEL")
    void setLabel(String label);
    @Accessor("USERID")
    void setUserId(Integer id);
    @Accessor("CREATED")
    void setCreatedDate(Date created);
    
}
