package ut.com.lmig.forge.stash.ssh.keys.rest;


import com.atlassian.bitbucket.user.ApplicationUser;
import com.lmig.forge.stash.ssh.ao.SshKeyEntity;
import com.lmig.forge.stash.ssh.rest.KeyDetailsResourceModel;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KeyDetailsResourceModelTest {
    private static final java.util.Date STATIC_DATE = new Date();
    private static final java.lang.Integer USER_ID = 1;
    private static final java.lang.String USERNAME = "Jean";
    private SshKeyEntity sshKey;
    private ApplicationUser user;

    @Before
    public void setup(){
        sshKey = mock(SshKeyEntity.class);
        when(sshKey.getCreatedDate()).thenReturn(STATIC_DATE);
        user = mock(ApplicationUser.class);
        when(user.getId()).thenReturn(USER_ID);
        when(user.getSlug()).thenReturn(USERNAME);
    }

    @Test
    public void keyDetailsAreAccurateToSshKeyEntity() {
        KeyDetailsResourceModel createdKeyDetails = KeyDetailsResourceModel.from(sshKey,user);
        assertThat(createdKeyDetails.getCreated(),is(STATIC_DATE));
    }


    @Test
    public void keyDetailsContainUserEntity() {
        KeyDetailsResourceModel createdKeyDetails = KeyDetailsResourceModel.from(sshKey,user);
        assertThat(createdKeyDetails.getUser().getId(),is(USER_ID));
        assertThat(createdKeyDetails.getUser().getUsername(),is(USERNAME));
    }



}




