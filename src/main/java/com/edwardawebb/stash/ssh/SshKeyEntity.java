package com.edwardawebb.stash.ssh;

import java.util.Date;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;

@Preload
public interface SshKeyEntity extends Entity{
    @NotNull
    @Mutator("TEXT")
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
    

    @Accessor("TEXT")
    void setText(String text);
    @Accessor("LABEL")
    void setLabel(String label);
    @Accessor("USERID")
    void setUserId(Integer id);
    @Accessor("CREATED")
    void setCreatedDate(Date created);
    
}
