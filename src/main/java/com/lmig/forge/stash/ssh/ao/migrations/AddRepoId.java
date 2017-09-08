package com.lmig.forge.stash.ssh.ao.migrations;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.ssh.SshAccessKey;
import com.atlassian.bitbucket.ssh.SshAccessKeyService;
import com.atlassian.bitbucket.util.Page;
import com.atlassian.bitbucket.util.PageRequestImpl;
import com.lmig.forge.stash.ssh.ao.EnterpriseKeyRepositoryImpl;
import com.lmig.forge.stash.ssh.ao.SshKeyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Eddie Webb on 9/6/17.
 */
public class AddRepoId implements ActiveObjectsUpgradeTask {

    private static final Logger log = LoggerFactory.getLogger(AddRepoId.class);
    private final SshAccessKeyService sshAccessKeyService;

    public AddRepoId(final SshAccessKeyService sshAccessKeyService) {
        this.sshAccessKeyService = sshAccessKeyService;
    }

    @Override
    public ModelVersion getModelVersion() {
        return ModelVersion.valueOf("1"); // this is first ever upgrade, ao defaults to 0
    }

    @Override
    public void upgrade(ModelVersion modelVersion, ActiveObjects activeObjects) {
        log.info("Starting migration of SSHKeyEntities to version: " + modelVersion);
        activeObjects.migrate(SshKeyEntity.class);
        for (SshKeyEntity key: activeObjects.find(SshKeyEntity.class)){
            Page<SshAccessKey> keys = sshAccessKeyService.findByKeyForRepositories(key.getKeyId(), new PageRequestImpl(0, 1));
            if( keys.getSize() > 0 ){
                log.info("Key {} is access key",key.getKeyId());
                SshAccessKey repoKey = keys.getValues().iterator().next();
                if(repoKey.getResource() instanceof Repository){
                    key.setRepoId(((Repository)repoKey.getResource()).getId());
                }else if(repoKey.getResource() instanceof Project){
                    key.setProjectId(((Project)repoKey.getResource()).getId());
                }
                key.save();
                log.info("Key updated.");
            }
        }
    }
}
