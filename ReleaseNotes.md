# v1.0 (5/28/2019)
#Release Notes
Updated to ignore CBOR data messages.

# v0.2 (10/20/2017)
# Release Notes

## Notable Changes
The Barcelona Release (v 0.2) of the Support Rules Engine micro service includes the following:
* Application of Google Style Guidelines to the code base
* Increase in unit/intergration tests from 27 tests to 115 tests
* POM changes for appropriate repository information for distribution/repos management, checkstyle plugins, etc.
* Removed all references to unfinished DeviceManager work as part of Dell Fuse
* Added Dockerfile for creation of micro service targeted for ARM64 
* Added interfaces for all Controller classes

## Bug Fixes
* Removed OS specific file path for logging file 
* Provide option to include stack trace in log outputs

## Pull Request/Commit Details
 - [#11](https://github.com/edgexfoundry/support-rulesengine/pull/11) - Remove staging plugin contributed by Jeremy Phelps ([JPWKU](https://github.com/JPWKU))
 - [#10](https://github.com/edgexfoundry/support-rulesengine/pull/10) - Fixes Maven artifact dependency path contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#9](https://github.com/edgexfoundry/support-rulesengine/pull/9) - harden work in progress. Added google style checks, added repos to p… contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#8](https://github.com/edgexfoundry/support-rulesengine/pull/8) - Removed device manager url refs in properties files contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#7](https://github.com/edgexfoundry/support-rulesengine/pull/7) - Added support for aarch64 arch contributed by ([feclare](https://github.com/feclare))
 - [#6](https://github.com/edgexfoundry/support-rulesengine/pull/6) - Adds Docker build capability contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#5](https://github.com/edgexfoundry/support-rulesengine/issues/5) - is rulesengine working currently?
 - [#4](https://github.com/edgexfoundry/support-rulesengine/pull/4) - Fixes Log File Path contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#3](https://github.com/edgexfoundry/support-rulesengine/issues/3) - Log File Path not Platform agnostic
 - [#2](https://github.com/edgexfoundry/support-rulesengine/pull/2) - Add distributionManagement for artifact storage contributed by Andrew Grimberg ([tykeal](https://github.com/tykeal))
 - [#1](https://github.com/edgexfoundry/support-rulesengine/pull/1) - Contributed Project Fuse source code contributed by Tyler Cox ([trcox](https://github.com/trcox))

