#!/bin/bash

#bye bye whatever is there now!
echo "recreating home directories from scratch"
rm -R target/*


#creat cluster home dirs for mysql lib
mkdir -p target/stash-node-2/home/lib
mkdir -p target/stash-node-1/home/lib
mkdir -p target/stash-node-2/home/shared
mkdir -p target/stash-node-1/home/shared
cp src/test/resources/mysql-connector* target/stash-node-2/home/lib
cp src/test/resources/mysql-connector* target/stash-node-1/home/lib


echo "providing stash-config to clustered home for db settings"
# set stash-config.properties with defaults and force db pool below CF max limit of 40 connections
cat >>target/stash-node-1/home/shared/stash-config.properties <<EOF
logging.logger.com.atlassian.stash=INFO
logging.logger.com.atlassian.stash.internal.project=WARN
logging.logger.ROOT=WARN
feature.getting.started.page=false
plugin.branch-permissions.feature.splash=false
db.pool.partition.count=2
db.pool.partition.connection.maximum=9
EOF

cat target/stash-node-1/home/shared/stash-config.properties > target/stash-node-2/home/shared/stash-config.properties

echo "starting test group"
# run with cluster optipn
atlas-run --testGroup clusterTestGroup
