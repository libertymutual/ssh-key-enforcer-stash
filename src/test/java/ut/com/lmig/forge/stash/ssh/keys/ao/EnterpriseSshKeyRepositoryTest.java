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

package ut.com.lmig.forge.stash.ssh.keys.ao;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import net.java.ao.DBParam;
import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.jdbc.Jdbc;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.bitbucket.ssh.SshKey;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.lmig.forge.stash.ssh.ao.EnterpriseKeyRepository;
import com.lmig.forge.stash.ssh.ao.EnterpriseKeyRepositoryImpl;
import com.lmig.forge.stash.ssh.ao.SshKeyEntity;
import com.lmig.forge.stash.ssh.ao.SshKeyEntity.KeyType;

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
@Data(EnterpriseSshKeyRepositoryTest.EnterpriseSshKeyRepositoryTestData.class)
@Jdbc(net.java.ao.test.jdbc.DynamicJdbcConfiguration.class)
public class EnterpriseSshKeyRepositoryTest {
    private static final String PUBLIC_KEY_ONE = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQC0O2PpfWd0RuoveFkSLP8DaL2ZekQZJM7gzQFi/cavziK8jnAY+xtNIAF1K7EN64JSM2DTMU7BZUFkJvqqbugzc29A/LOfZ6GzvMhSiR7YR2J/eOkVZbmPPyC1qWDCc5Ne71pEJhU5OdlFd4Hj5XgDzNyMANoYlO+xm1IDzHBxDSrvY++VGrnZG1rJ6aSdxyRCoE7MVtQkLuIMDSVPTVfdqDV4oKlH2bzd4LyA1Jm01+MBmWq2qVcKcF6UYKaUILVreZZZSm2/PBbgQ+H5yzjNeEbvdnAr7bcn+xRdhEM0ZGm/RRDRIvwkTlWJ2y9M3KvnJEKbv/c9ZAlOmbs5K1OhfGL/jCU8h1EslwQ9euFp0wjKUMj5u9ll8QqpNcXxsfUnaN9qc2rrm5FS5t5TFAkbIX5fOTJCPb+seE146ax/cNovzOoJUPvF+qBfvJLQGX2L/4JdPqDQ6FkLbvJy194/K5oWag8w4F9ftYIfd/SOgatPMiKuhOls2zYufm34UBbksc7qxDD12JUiI/q7JNted53tnPVBSDLM5RYtohDq/w4MfyFmA51UeETSLumlwg9kOuqaWBYjr2Esn09EtkQNIhQxxt3w47O0RFghZgJdnP3VORju3v2l0Qfo7A/EbeDGKXQhCl6yeMv82lmUtzOhVN6IAApOwMH7Hmh/z209jw==";
    private static final int ADMIN_ID = 1;
    private static final int USER_ID = 2;
    private static final int ADHOC_USER_ID = 3;
    private static final int STASH_KEY_ID = 100;
    private static final int DAYS_ALLOWED_FOR_USERS = 90;
    private static final int DAYS_ALLOWED_FOR_BAMBOO = 365;

    // gets injected thanks to ActiveObjectsJUnitRunner.class
    private EntityManager entityManager;

    private ActiveObjects ao;
    private EnterpriseKeyRepository keyRepo;

    @Before
    public void setup() {

        ao = new TestActiveObjects(entityManager);
        keyRepo = new EnterpriseKeyRepositoryImpl(ao);
    }

    @Test
    @NonTransactional
    public void aKeyCanBeSaved() {
        ApplicationUser user = mock(ApplicationUser.class);
        when(user.getId()).thenReturn(ADHOC_USER_ID);
        String comment = "No Comment123";

        keyRepo.createOrUpdateUserKey(user, PUBLIC_KEY_ONE, comment);

        SshKeyEntity[] createdRecords = ao.find(SshKeyEntity.class, Query.select().where("USERID = ?", ADHOC_USER_ID));
        assertThat(createdRecords.length, is(1));
        assertThat(createdRecords[0].getLabel(), is(comment));
        assertThat(createdRecords[0].getText(), is(PUBLIC_KEY_ONE));
    }

    @Test
    @NonTransactional
    public void theStashKeyIdOfExistingRecordCanBeUPdated() {
        assertThat(EnterpriseSshKeyRepositoryTestData.expiredUserKey.getID(), notNullValue());
        SshKey key = mock(SshKey.class);
        when(key.getId()).thenReturn(STASH_KEY_ID);
        keyRepo.updateRecordWithKeyId(EnterpriseSshKeyRepositoryTestData.expiredUserKey, key);

        SshKeyEntity[] createdRecords = ao.find(SshKeyEntity.class, Query.select().where("USERID = ? and TYPE = ?", ADMIN_ID,KeyType.USER));
        assertThat(createdRecords.length, is(1)); // if not found, issue with
                                                  // test data class
        assertThat(createdRecords[0].getKeyId(), is(STASH_KEY_ID)); // if not
                                                                    // found,
                                                                    // issue
                                                                    // with
                                                                    // update
                                                                    // call
    }

    @Test
    @NonTransactional
    public void listOfExpiredKeysRespectsUserKeyType() {
        DateTime now = new DateTime();
        List<SshKeyEntity> keys = keyRepo.listOfExpiredKeys(now.minusDays(DAYS_ALLOWED_FOR_USERS).toDate(), KeyType.USER);
        
        assertThat(keys.size(),is(1));
        assertThat("Expiry query returned keytype not requested",keys.get(0).getKeyType(),is(KeyType.USER));
        assertThat("Expired Key does not match expected",keys.get(0).getID(),is(EnterpriseSshKeyRepositoryTestData.expiredUserKey.getID()));
    }
    
    @Test
    @NonTransactional
    public void listOfExpiredKeysRespectsBambooKeyType() {
        DateTime now = new DateTime();
        List<SshKeyEntity> keys = keyRepo.listOfExpiredKeys(now.minusDays(DAYS_ALLOWED_FOR_BAMBOO).toDate(), KeyType.BAMBOO);
        
        assertThat(keys.size(),is(1));
        assertThat("Expiry query returned keytype not requested",keys.get(0).getKeyType(),is(KeyType.BAMBOO));
        assertThat("Expired Key does not match expected",keys.get(0).getID(),is(EnterpriseSshKeyRepositoryTestData.expiredBambooKey.getID()));
    }

    public static class EnterpriseSshKeyRepositoryTestData implements DatabaseUpdater {
        private static SshKeyEntity expiredUserKey;
        private static SshKeyEntity expiredBambooKey;
        private static SshKeyEntity validUserKey;
        private static SshKeyEntity validBambooKey;

        @Override
        public void update(EntityManager em) throws Exception {
            em.migrate(SshKeyEntity.class);

           
            DateTime now = new DateTime();
            // create a pre-expired user key in DB 
            expiredUserKey = em.create(SshKeyEntity.class, new DBParam("TYPE", KeyType.USER), new DBParam("USERID",
                    ADMIN_ID), new DBParam("TEXT", PUBLIC_KEY_ONE), new DBParam("LABEL", "COMPROMISED"), new DBParam(
                    "CREATED", now.minusDays(DAYS_ALLOWED_FOR_USERS + 1).toDate()));

            // create a pre-expired bamboo key
            expiredBambooKey = em.create(SshKeyEntity.class, new DBParam("USERID", ADMIN_ID), new DBParam("TEXT",
                    PUBLIC_KEY_ONE), new DBParam("LABEL", "BAMBOO"), new DBParam("TYPE", KeyType.BAMBOO), new DBParam(
                    "CREATED", now.minusDays(DAYS_ALLOWED_FOR_BAMBOO + 1).toDate()));
            
            // create a non-expired user key in DB 
            validUserKey = em.create(SshKeyEntity.class, new DBParam("TYPE", KeyType.USER), new DBParam("USERID",
                    USER_ID), new DBParam("TEXT", PUBLIC_KEY_ONE), new DBParam("LABEL", "COMPROMISED"), new DBParam(
                    "CREATED", now.minusDays(DAYS_ALLOWED_FOR_USERS - 1).toDate()));

            // create a babmoo key that is older then user limit, but valid to bamboo limit
            validBambooKey = em.create(SshKeyEntity.class, new DBParam("USERID", USER_ID), new DBParam("TEXT",
                    PUBLIC_KEY_ONE), new DBParam("LABEL", "BAMBOO"), new DBParam("TYPE", KeyType.BAMBOO), new DBParam(
                    "CREATED", now.minusDays(DAYS_ALLOWED_FOR_BAMBOO - 1).toDate()));

        }

    }
}
