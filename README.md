# SSH Key Enforcer for BitBucket Server

## What it do..
Layers additional controls over -Stash's- Bitbucket's SSH key features that enforce the stronger controls required in an enterprise environment.

## Features
- Block unamed keys being added directly to Projects or Repositories
All keys must be created for specific users, and inherit their access.
- Blocks upload of existing keys and generates new RSA 2048 bit keys for the user.
User can download public and private key pair, and regenerate as needed.
Special users designated by a Group may add keys directly, this supports the current Bamboo<>Stash integration which generates user keys when repositories are created in bamboo. This Group should only be grnated to admins or system accounts that provision pipelines.
- Enforces Key expiration policy
To mitigate risk, all user keys are expired after 90 days, and users are notified to re-generate

## License
   Copyright 2015 Liberty Mutual Insurance

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.


## Contributing
SSH Key Enforcer for Stash is built using Atlassian SDK, here's some info on that...



Here are the SDK commands you'll use immediately:

* atlas-run   -- installs this plugin into the product and starts it on localhost
* atlas-debug -- same as atlas-run, but allows a debugger to attach at port 5005
* atlas-cli   -- after atlas-run or atlas-debug, opens a Maven command line window:
                 - 'pi' reinstalls the plugin into the running product instance
* atlas-help  -- prints description for all commands in the SDK

Full documentation is always available at:

https://developer.atlassian.com/display/DOCS/Introduction+to+the+Atlassian+Plugin+SDK
