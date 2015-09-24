/*
 * Copyright 2015, Liberty Mutual Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ut.com.edwardawebb.stash.ssh;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.java.ao.DBParam;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.jdbc.Jdbc;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.stash.ssh.api.SshKeyService;
import com.atlassian.stash.user.StashUser;
import com.atlassian.stash.user.UserService;
import com.lmig.forge.stash.ssh.ao.EnterpriseKeyRepository;
import com.lmig.forge.stash.ssh.ao.EnterpriseKeyRepositoryImpl;
import com.lmig.forge.stash.ssh.ao.SshKeyEntity;
import com.lmig.forge.stash.ssh.keys.EnterpriseSshKeyService;
import com.lmig.forge.stash.ssh.keys.EnterpriseSshKeyServiceImpl;
import com.lmig.forge.stash.ssh.notifications.NotificationService;

/**
 * Must run all methods that interact with service as @NonTransactional or
 * otherwise the multiple layers of transactions cause issues. Also
 * http://grepcode
 * .com/file/repo1.maven.org/maven2/net.java.dev.activeobjects/activeobjects
 * -test/0.23.0/net/java/ao/test/jdbc/DynamicJdbcConfiguration.java#
 * DynamicJdbcConfiguration.0jdbcSupplier has all the databtase types and
 * connection info needed in maven arguments.
 * 
 * @author Eddie Webb
 * 
 */
@RunWith(ActiveObjectsJUnitRunner.class)
@Data(EnterpriseSshKeyManagerImplTest.EnterpriseSshKeyRepositoryTestData.class)
@Jdbc(net.java.ao.test.jdbc.DynamicJdbcConfiguration.class)
public class EnterpriseSshKeyManagerImplTest {
    private static final String PUBLIC_KEY_ONE = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQC0O2PpfWd0RuoveFkSLP8DaL2ZekQZJM7gzQFi/cavziK8jnAY+xtNIAF1K7EN64JSM2DTMU7BZUFkJvqqbugzc29A/LOfZ6GzvMhSiR7YR2J/eOkVZbmPPyC1qWDCc5Ne71pEJhU5OdlFd4Hj5XgDzNyMANoYlO+xm1IDzHBxDSrvY++VGrnZG1rJ6aSdxyRCoE7MVtQkLuIMDSVPTVfdqDV4oKlH2bzd4LyA1Jm01+MBmWq2qVcKcF6UYKaUILVreZZZSm2/PBbgQ+H5yzjNeEbvdnAr7bcn+xRdhEM0ZGm/RRDRIvwkTlWJ2y9M3KvnJEKbv/c9ZAlOmbs5K1OhfGL/jCU8h1EslwQ9euFp0wjKUMj5u9ll8QqpNcXxsfUnaN9qc2rrm5FS5t5TFAkbIX5fOTJCPb+seE146ax/cNovzOoJUPvF+qBfvJLQGX2L/4JdPqDQ6FkLbvJy194/K5oWag8w4F9ftYIfd/SOgatPMiKuhOls2zYufm34UBbksc7qxDD12JUiI/q7JNted53tnPVBSDLM5RYtohDq/w4MfyFmA51UeETSLumlwg9kOuqaWBYjr2Esn09EtkQNIhQxxt3w47O0RFghZgJdnP3VORju3v2l0Qfo7A/EbeDGKXQhCl6yeMv82lmUtzOhVN6IAApOwMH7Hmh/z209jw==";
    private static final int EXPIRED_USER_ID = 1;
    private static final int VALID_USER_ID = 2;
    private static final int EXPIRED_STASH_KEY_ID = 100;
    private static final int VALID_STASH_KEY_ID = 200;

    // gets injected thanks to ActiveObjectsJUnitRunner.class
    private EntityManager entityManager;

    private ActiveObjects ao;
    private EnterpriseKeyRepository keyRepo;
    private EnterpriseSshKeyService ourKeyService;
    private NotificationService notificationService;
    private SshKeyService stashKeyService;
    private UserService userService;

    @Before
    public void setup() {
        ao = new TestActiveObjects(entityManager);
        keyRepo = new EnterpriseKeyRepositoryImpl(ao);
        stashKeyService = mock(SshKeyService.class);
        notificationService = mock(NotificationService.class);
        userService = mock(UserService.class);
        when(userService.getUserByName(EnterpriseSshKeyServiceImpl.ADMIN_ACCOUNT_NAME)).thenReturn(mock(StashUser.class));//defeat NPE check
        ourKeyService = new EnterpriseSshKeyServiceImpl(stashKeyService, keyRepo, null, notificationService, userService);
    }

    @Test
    public void whenExpireTaskIsCalledValidKeysAreIgnored() {
        SshKeyEntity validKey = ao.get(SshKeyEntity.class, EnterpriseSshKeyRepositoryTestData.validKey.getID());
        assertThat(validKey, notNullValue());
        assertThat(validKey.getKeyId(), is(VALID_STASH_KEY_ID));

        ourKeyService.replaceExpiredKeysAndNotifyUsers();

        // key survived?
        validKey = ao.get(SshKeyEntity.class, EnterpriseSshKeyRepositoryTestData.validKey.getID());
        assertThat(validKey, notNullValue());
        // stash's ssh service was not invoked
        verify(stashKeyService, times(0)).remove(VALID_STASH_KEY_ID);
    }

    @Test
    @NonTransactional
    public void whenExpireTaskIsCalledExpiredKeysAreDestroyed() {
        SshKeyEntity validKey = ao.get(SshKeyEntity.class, EnterpriseSshKeyRepositoryTestData.expiredKey.getID());
        assertThat(validKey, notNullValue());
        assertThat(validKey.getKeyId(), is(EXPIRED_STASH_KEY_ID));

        ourKeyService.replaceExpiredKeysAndNotifyUsers();

        // key was purged?
        validKey = ao.get(SshKeyEntity.class, EnterpriseSshKeyRepositoryTestData.expiredKey.getID());
        assertThat(validKey, nullValue());
        // stash ssh remve was called with ecxpired ssh key id

        // stash's ssh service was not invoked
        verify(stashKeyService).remove(EXPIRED_STASH_KEY_ID);
    }

    @Test
    public void whenKeyIsExpiredTheAppropriateUserIsNotified() {
        ourKeyService.replaceExpiredKeysAndNotifyUsers();

        // stash's ssh service was not invoked
        verify(notificationService).notifyUserOfExpiredKey(EXPIRED_USER_ID);
    }
    
 
    public static class EnterpriseSshKeyRepositoryTestData implements DatabaseUpdater {
        private static SshKeyEntity expiredKey;
        private static SshKeyEntity validKey;

        @Override
        public void update(EntityManager em) throws Exception {
            em.migrate(SshKeyEntity.class);

            // create a pre-expired key in DB for scheduler
            Date today = new Date();
            Calendar cal = new GregorianCalendar();
            cal.setTime(today);
            cal.add(Calendar.DAY_OF_YEAR, -91);
            expiredKey = em.create(SshKeyEntity.class, new DBParam("USERID", EXPIRED_USER_ID), new DBParam("KEYID",
                    EXPIRED_STASH_KEY_ID), new DBParam("TEXT", PUBLIC_KEY_ONE), new DBParam("LABEL", "COMPROMISED"),
                    new DBParam("CREATED", cal.getTime()));

            cal.add(Calendar.DAY_OF_YEAR, 2);
            validKey = em.create(SshKeyEntity.class, new DBParam("USERID", VALID_USER_ID), new DBParam("KEYID",
                    VALID_STASH_KEY_ID), new DBParam("TEXT", PUBLIC_KEY_ONE), new DBParam("LABEL", "VALID"),
                    new DBParam("CREATED", cal.getTime()));

        }

    }
}
